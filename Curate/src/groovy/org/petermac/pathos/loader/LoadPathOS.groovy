/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

//
//	LoadPathOS.groovy
//
//	Load Path-OS Database from raw data files
//
//	Usage:
//
//	01	kdoig	06-May-2013
//  02  kdoig   15-Oct-2013     Moved to Loader module and integrated with GORM
//  10  kdoig   01-Nov-2015     Adapted for Autoloading after pipeline runs
//

package org.petermac.pathos.loader

import groovy.util.logging.Log4j
import org.apache.log4j.*
import org.codehaus.groovy.runtime.StackTraceUtils
import org.petermac.pathos.pipeline.*
import org.petermac.util.*
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import java.text.SimpleDateFormat

@Log4j
class LoadPathOS
{
    //  DbLock for DB access in series
    //
    static DbLock dbl  = null

    //  DB lock map
    //
    static Map lockMap = null

    //  ETL phases
    //
    private def mergePhases   = [ 'extract', 'transform', 'load', 'mergegorm' ]

    static void main( args )
    {
        Boolean crash = false

        //
        //	Collect and parse command line args
        //
        def cli = new CliBuilder(   usage: "LoadPathOS [options]",
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nLoad PathOS Database from raw data files\n')

        //	Options to LoadPathOS
        //
        cli.with
        {
            h( longOpt: 'help',		'this help message' )
            p( longOpt: 'phase',    args: 1, required: true, 'Comma separated list of phase(s) of ETL to perform (extract|transform|load|mergegorm|merge)' )
            c( longOpt: 'config', 	args: 1, required: true, 'ETL config file' )
            r( longOpt: 'rdb', 	    args: 1, 'RDB Schema to use eg pa_prod' )
            o( longOpt: 'orm', 	    args: 1, 'ORM Schema to use eg pa_prod' )
            d( longOpt: 'debug',    'Turn on debug logging')
        }

        def opt = cli.parse( args )
        if ( ! opt ) return
        if ( opt.h )
        {
            cli.usage()
            return
        }

        //  Debug ?
        //
        if ( opt.debug ) Logger.getRootLogger().setLevel(Level.DEBUG)

        //  Find build file
        //
        def etlFile = new File( opt.config as String )
        if ( ! etlFile.exists())
        {
            log.fatal( "ETL Config file doesn't exist " + etlFile.name )
            return
        }

        //  Set RDB to load from
        //
        def rdb = opt.rdb
        new DbConnect(rdb)

        //  Set ORM to load into
        //
        def orm = opt.orm
        new DbConnect(orm)

        //  Default start date to use 1 Jan 2000 eg ALL files
        //
        def cal = Calendar.instance
        cal.set( 2000, 0, 1)
        Date fromDate = cal.time

        //  Remove spaces
        //
        def phase = opt?.phase?.replace( ' ','')

        log.info( "Start Data Load from [${fromDate}]" + args )

        //  Create locking class
        //
        dbl = new DbLock( orm )

        //  Perform data load
        //
        try
        {
            new LoadPathOS().dataload( etlFile, phase, fromDate, rdb, orm )
        }
        catch ( Exception e )
        {
            StackTraceUtils.sanitize(e).printStackTrace()
            log.fatal( "Exiting: Couldn't load file ${etlFile.name} " + e.toString())
            crash = true
        }
        finally
        {
            //  Clear the lock despite what happened
            //
            if ( lockMap )
            {
                lockMap = dbl.clearLock( lockMap )
                log.warn( "Cleared DB Lock on ${orm} lock=${lockMap}")
            }
            if ( crash ) System.exit(1)
        }

        log.info( "Done: processed ${etlFile.name}" )
    }

    /**
     * Main data loader for PathOS
     *
     * @param etlFile   Groovy style configuration file for load
     * @param phase     Load phase to perform (extract|transform|load|rebuild|mergegorm)
     * @param from      Date after which to reload files [all]
     * @param rdb       RDB schema to load RDB tables
     * @param orm       ORM schema to load/merge ORM tables
     */
    void dataload( File etlFile, String phase, Date from, String rdb, String orm )
    {
        //  Prefab phase sets
        //
        def phases = []
        if ( phase == 'merge' ) phases = mergePhases

        //  Transfer command line phases
        //
        if ( ! phases )
        {
            //  Add phases in correct order to list
            //
            for ( ph in mergePhases )
            {
                if ( ph in phase.split(',') )
                    phases << ph
            }
        }

        //  Check we have valid phases to run
        //
        if ( ! phases )
        {
            log.error( "No valid phases found, exiting..." )
            return
        }
        log.info( "Running phases: " + phases )

        //  Load ETL configuration
        //
        def etl = new ConfigSlurper( rdb ).parse(etlFile.toURL())

        //  Set the date for file extract
        //
        etl.fromDate = from

        //  Validate ETL subdirectories
        //
        if ( ! checkSubDirs( etl ))
            return

        //  Extract data to Raw data directory
        //
        if ( phases.contains('extract') && ! etlExtract( etl ))
            return

        //  Transform data for loading
        //
        if ( phases.contains('transform') && ! etlTransform( etl ))
            return

        //  Process all tables in table list
        //
        if ( phases.contains('load'))
        {
            //  Only lock DB loader if performing both load and merge phases
            //
            if ( phases.contains('load') && phases.contains('mergegorm'))
            {
                //  Wait for lock on DB
                //
                while ( lockMap = dbl.hasLock())
                {
                    sleep( 60 * 1000 )                           // 1 minute wait
                    log.info( "Waiting for DB Lock on ${orm} lock=${lockMap}")
                }

                //  Acquire lock
                //
                lockMap = dbl.setLock()
                log.info( "Set DB Lock on ${orm} lock=${lockMap}")
            }

            def tables = etl.load.tables
            for ( tname in tables )
            {
                etlLoad( etl , tname, rdb )
            }
        }

        //  Merge the stage ORM database into production
        //
        if ( phases.contains('mergegorm'))
        {
            //  Merge staging database into GORM hibernate database
            //
            etlGorm( rdb, orm )

            //  Only lock DB loader is performing both load and merge phases
            //
            if ( lockMap )
            {
                lockMap = dbl.clearLock( lockMap )
                log.info( "Cleared DB Lock on ${orm} lock=${lockMap}")
            }
        }
    }

    /**
     * Extract data to raw data area
     *
     * @param etl   ETL configuration
     */
    static Boolean etlExtract( ConfigObject etl )
    {
        def cal = Calendar.instance
        String fromDir = etl.extract.indir
        String incl    = etl.extract.include
        String toDir   = etl.extract.outdir

        //  Clean out destination dir
        //
        def td = new File(toDir)
        if ( td.exists())
        {
            td.eachFile{ it.delete() }
        }

        log.info( "### Extracting from: ${fromDir} to: ${toDir}")

        //  Copy files from copyFiles list - leave in header comments
        //
        cal.set( 2000, 0, 1)    // copy all file from fromDir
        copyFiles( fromDir, incl, cal.time, toDir, true)

        //  Loop through etl.extract config looking for extract clauses
        //
        for (cfg in etl.extract )
        {
            //  Is this element a table processing clause ?
            //
            if ( cfg.value.getClass() == ConfigObject )        // detect a config sub clause
            {
                def table  = cfg.key
                Map etlmap = cfg.value

                //  Choose the appropriate configuration extractor
                //
                def extractor = etlmap.extractor
                switch (extractor)
                {
                    case 'mergeFiles':
                        log.info( "Extracting with mergeFiles from [${etl.fromDate}] into ${table}")

                        boolean header = etlmap.header == [:] ? true : etlmap.header    // Todo cleanup
                        fromDir        = etlmap.indir
                        String files   = etlmap.include
                        List<Map> samples = loadSamples( etlmap.infile as String, true )
                        if (( ! fromDir && ! samples ) || ! files )
                        {
                            log.error( "Missing values in extract configuration: indir, infile or include for table: " + table )
                            return false
                        }

                        if ( samples )
                        {
                            boolean append = false      //  New output file
                            for ( sample in samples )
                            {
                                boolean foundRows = mergeFiles( sample.outdir, files, etl.fromDate, toDir, table, header, append )

                                //  If we now have rows, only append from now on
                                //
                                if ( foundRows ) append = true
                            }
                            break
                        }
                        else
                        {
                            //  Merge files from fromDir
                            //
                            mergeFiles( fromDir, files, etl.fromDate, toDir, table, header, false )
                        }

                        break
                    case 'loadAlamut':
                        log.info( "Extracting with loadAlamut into ${table}")

                        //  Set input dir
                        //
                        fromDir = etlmap.indir

                        //  Run Alalmut loader
                        //
                        new LoadAlamut().dataload( new File(fromDir), new File( toDir, table + ".tsv"))
                        break
                    case 'loadDetente':
                        log.info( "Extracting with loadDetente into ${table}")

                        //  Set input dir
                        //
                        fromDir = etlmap.indir

                        //  Run Detente loader
                        //
                        new LoadDetente().dataload( new File(fromDir), new File( toDir, table + ".tsv"))
                        break
                    case 'seqrun':
                        log.info( "Extracting samples into ${table}")

                        //  Set outputfile
                        //
                        def outFile = new File( toDir, table + ".tsv" )

                        //  Read in sample list from pipeline parameters (Seqrun.tsv)
                        //
                        List<Map> samples = loadSamples( etlmap.infile as String, true )
                        if ( samples )
                        {
                            //  create mp_seqrun from infile from config
                            //
                            createSeqrun( samples, outFile, false )
                        }
                        else
                        {
                            //  Run Submit loader (Submit --database mp_seqrun.tsv --seqrun 1 --platform all)
                            //
                            Map params  = [ seqrun: '1', database: outFile, platform: 'all']
                            new Submit().submit( params )
                        }
                        break
                    default:
                        log.error( "Unknown extractor in etl block: ${extractor}")
                        return false
                }
            }
        }

        return true
    }

    /**
     * Load in sample list from a TSV file
     *
     * @param samfile   TSV file of samples
     * @param header    File has TSV header
     * @return          List of Maps of samples to load
     */
    private static List<Map> loadSamples( String samfile, boolean header )
    {
        if ( ! samfile || samfile == '[:]' ) return []

        File samf = new File( samfile )
        if ( ! samf.exists())
        {
            log.error( "Couldn't open file: ${samfile}")
            return []
        }

        //  Load in TSV file of samples
        //
        Tsv samt = new Tsv( samf )
        samt.load( header )
        def rows = samt.getRowMaps()
        log.info( "Found ${rows.size()} samples in ${samfile}")

        return rows
    }

    /**
     * Create a mp_seqrun.tsv file of seqrun / samples from the pipeline driver file
     * Todo: this will be replaced by the REST interface to Holly
     *
     * @param samples   List<Map> of samples from the pipeline driver file
     * @param outf      mp_seqrun.tsv file to populate
     */
    static void createMinimalSeqrun( List<Map> samples, File outf, boolean header )
    {
        //  Output header if required
        //
        if ( header )
        {
            def headers = ['seqrun','platform','sepe','library','experiment','scanner','readlen','sample','reference','analysis','username','useremail','laneno']
            outf << "##  Created by LoadPathOS.createMinimalSeqrun()\n##\n#"
            outf << headers.join("\t") + "\n"
        }

        for ( sam in samples )
        {
            Map fldmap = [:]

            //  Add seqrun data
            //
            fldmap << [seqrun:   sam.seqrun]
            fldmap << [platform: 'Seq']
            fldmap << [sepe:     'PE']
            fldmap << [library:  '']

            fldmap << [experiment: '' ]
            fldmap << [scanner:    '' ]
            fldmap << [readlen:    '' ]

            fldmap << [sample:    sam.sample]
            fldmap << [reference: sam.panel]
            fldmap << [analysis:  sam.pipeline]
            fldmap << [username:  'username']
            fldmap << [useremail: 'user@petermac.org']
            fldmap << [laneno:    0]

            //  Match for NextSeq runs
            //
            def match = ( sam.seqrun =~ /\d{6}_(NS[^_]+)_.*/ )       // 151009_NS500817_0026_AHGJYGBGXX

            //  Set NextSeq params
            //
            if ( match.count == 1)
            {
                log.info( "Parse Seqrun: ${sam.seqrun} " + match[0])
                fldmap.scanner  = match[0][1]
                fldmap.readlen  = 76
                fldmap.platform = 'NextSeq'
            }

            //  Output TSV row to dump file
            //
            outf << fldmap.values().join("\t") + "\n"
        }
    }

    /**
     * Create a mp_seqrun.tsv file of seqrun / samples from the pipeline driver file
     * Todo: this will be replaced by the REST interface to Holly
     *
     * @param samples   List<Map> of samples from the pipeline driver file
     * @param outf      mp_seqrun.tsv file to populate
     */
    static void createSeqrun( List<Map> seqrunSamples, File outf, Boolean header = true )
    {
        if ( seqrunSamples.size() == 0 )
        {
            log.fatal( "No Samples in Seqrun file")
            return
        }

        List   seqruns    = seqrunSamples.seqrun
        List   samplelist = seqrunSamples.sample
        String seqrun     = seqruns[0]

        log.info( "Creating ${outf} for ${seqrun} samples ${samplelist}")

        //  First look for LIMS XML file for this Seqrun
        //
        File limsf = GenSeqrun.findLimsXml( seqrun, null )

        //  If none found, output a minimal mp_seqrun file
        //
        if ( ! limsf )
        {
            createMinimalSeqrun( seqrunSamples, outf, header )
            return
        }

        //  Extract sample info from LIMs file
        //
        log.info( "Parsing ${limsf}")
        Map limsSamMap = SeqrunLims.parseLims( limsf.absolutePath )
        if ( ! limsSamMap )
        {
            //  invalid LIMS file
            //
            log.warn( "Couldn't parse LIMS XML ${limsf}")
            createMinimalSeqrun( seqrunSamples, outf, header )
            return
        }

        assert limsSamMap.seqrun == seqrun, "Mismatched seqrun lims=${limsSamMap.seqrun} Seqrun.tsv=${seqrun}"

        //  Load in Illumina [Rr]unParameters details
        //
        String rpFile = 'runParameters.xml'
        if ( seqrun =~ /.*_NS.*/ ) rpFile = 'RunParameters.xml'   // Illumina changed the name for NextSeq !!

        Map runp = SeqrunLims.parseRunParameters( limsf.parent + '/' + rpFile )
        if ( ! runp )
        {
            log.warn( "Couldn't parse RunParameters ${limsf.parent + '/' + rpFile}")
        }
        log.info( "${rpFile}=${runp}")

        //  Create mp_seqrun data
        //
        Map fldmap = [:]

        fldmap << [seqrun:   limsSamMap.seqrun]
        fldmap << [platform: limsSamMap.platform]
        fldmap << [sepe:     limsSamMap.sepe]
        fldmap << [library:  limsSamMap.library]

        fldmap << [experiment: runp?.experiment ]
        fldmap << [scanner:    runp?.scanner ]
        fldmap << [readlen:    runp?.readlen ]

        //  Output header
        //
        if ( header )
        {
            def headers = ['seqrun','platform','sepe','library','experiment','scanner','readlen','sample','reference','analysis','username','useremail','laneno']
            outf << "##  Created by LoadPathOS.createSeqrun() on ${new Date()}\n##\n#"
            outf << headers.join("\t") + "\n"
        }

        //  Process all sequenced samples
        //
        List<Map> limsSams    = limsSamMap.samples

        def nsamples = 0
        for ( srsam in seqrunSamples )
        {
            fldmap << [sample:    srsam.sample ]
            fldmap << [reference: srsam.panel ]
            fldmap << [analysis:  srsam.pipeline ]

            //  Find samples in the LIMS XML list
            //
            Map limsSam = limsSams.find { it.sample == srsam.sample }
            log.debug( "Seqrun=${seqrun} Seqrun.tsv ${srsam} Lims=${limsSam}")

            if ( limsSam )
            {
                fldmap << [username:  limsSam.username]
                fldmap << [useremail: limsSam.useremail]
                fldmap << [laneno:    limsSam.laneno]
            }
            else
            {
                log.warn( "Sample ${srsam.sample} missing from LIMS ${limsf}" )
                fldmap << [username:  'user']
                fldmap << [useremail: 'user@petermac.org']
                fldmap << [laneno:    0]
            }

            //  Output TSV row to dump file
            //
            outf << fldmap.values().join("\t") + "\n"
            ++nsamples
        }
    }

    /**
     * Transform data to staging area suitable for loading
     *
     * @param etl   ETL configuration
     */
    static Boolean etlTransform( ConfigObject etl )
    {
        String fromDir = etl.transform.indir
        String toDir   = etl.transform.outdir
        String incl    = etl.transform.include

        //  Clean out destination dir
        //
        def td = new File(toDir)
        if ( td.exists())
        {
            td.eachFile{ it.delete() }
        }

        //  Sanity check values
        //
        assert new File(fromDir).exists(), "Missing fromDir: ${fromDir}"
        assert new File(  toDir).exists(), "Missing   toDir: ${toDir}"
        assert incl.size(), "Missing include: ${incl}"

        log.info( "### Transforming from: ${fromDir} to: ${toDir}")

        //  Copy files from copyFiles list - strip out comments
        //
        copyFiles( fromDir, incl, etl.fromDate, toDir, false)

        //  Loop through etl.transform config looking for transform closure clauses
        //
        for (cfg in etl.transform )
        {
            //  Is this element a table processing clause ?
            //
            if ( cfg.value.getClass() == ConfigObject )        // detect a config sub clause
            {
                def txClosure = cfg.value.transform
                assert txClosure.getClass() =~ 'closure'

                //  Run transform closure on files
                //
                txClosure()
            }
        }

        return true
    }

    /**
     * Load a single table from a given ETL data directory
     *
     * @param etl      ETL configuration
     * @param table    Name of table to load
     * @param dbname   Name of RDB to populate
     */
    static Boolean etlLoad( ConfigObject etl, String table, String dbname )
    {
        log.info( "### Loading from: ${etl.load.createdir} to: ${dbname}")

        //  Find table create script
        //
        def tfile = new File( etl.load.createdir as String, table + '.sql' )
        if ( ! tfile.exists())
        {
            log.error( "Table create script doesn't exist: ${tfile.path}" )
            return false
        }

        //  SQL to drop and create table
        //
        def dml =   """
                    drop table if exists ${table};
                    source ${tfile.path};
                    """

        //  Find table raw data
        //
        def dfile = new File( etl.load.indir as String, table + '.tsv' )
        if ( dfile.exists())
        {
            //  SQL to load in data
            //
            dml +=  """
                    load data local infile '${dfile.path}' into table ${table};
                    show warnings;
                    """
        }
        else
        {
            log.error( "Table data doesn't exist: ${dfile.path}" )
            //
            //  Dont return here, drop through and delete the table anyway
        }

        //  Drop and load table
        //
        log.info( "Loading Table ${table} ...")
        new MysqlCommand( dbname ).run( dml )

        //  Look for optional index file
        //
        def ifile = new File( etl.load.createdir as String, table + '.idx.sql' )
        if ( ifile.exists())
        {
            dml =   """
                    source ${ifile.path};
                    show warnings;
                    """

            log.info( "Loading Index(es) for ${table} ...")
            new MysqlCommand( dbname ).run( dml )
        }

        return true
    }

    /**
     * Run a script against a database schema
     *
     * @param dir       Script directory
     * @param sqlFile   script basename (no .sql)
     * @param dbname    database schema
     * @return          warning output
     */
    static String runSql( String dir, String sqlFile, String dbname )
    {
        def pf = new File( dir, sqlFile )
        if ( ! pf.exists() )
        {
            log.error( "Missing script ${pf.name} ...")
            return null
        }

        log.info( "Running script ${pf.name} ...")

        def dml =   "source ${pf.path}; show warnings;"
        def out = new MysqlCommand( dbname ).run( dml )
        return out
    }

    /**
     * Load staging database into GORM hibernate database
     *
     * @param rdb           RDB schema to load from
     * @param orm           ORM schema to load into
     */
    static void etlGorm( String rdb, String orm )
    {

        log.info( "### Performing ORM merge from RDM ${rdb} to ORM ${orm}")

        //  Load stand-alone Hibernate context - Database JDBC is embedded in <schema>_loaderContext.xml
        //
        def db  = new DbConnect( orm )
        ApplicationContext context = new ClassPathXmlApplicationContext( db.hibernateXml)

        //  Run loader to move all data from RDB to GORM
        //
        def dbl = new DbLoader()

        //  Merge data with existing DB
        //
        dbl.mergeETL( rdb )

        log.info( "Finished ORM merge")
    }

    /**
     * Merge files into a single datafile ready for loading
     *
     * @param fromDir       Source tree to search
     * @param incl          Include file pattern
     * @param fromDate      Files after this date
     * @param toDir         Destination directory
     * @param mergeFile     Merge file to create
     * @param header        Extract header line from files
     * @param append        Dont delete output file
     * @return              True if records found
     */
    static boolean mergeFiles( String fromDir, String incl, Date fromDate, String toDir, String mergeFile, Boolean header, Boolean append )
    {
        def f = new File( toDir, mergeFile + ".tsv" )
        log.info( "Merging ${fromDir}${incl} after [${fromDate}] into ${f} append: ${append}")

        //  Set date into ANT format
        //
        def sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a")
        def from = sdf.format(fromDate)

        //  Perform merge using Ant
        //
        def ant = new AntBuilder()
        ant.sequential
        {
            if ( ! append )
                delete(file: f.absolutePath )

            if ( header && ! append )
            {
                echo("Ant loader: extracting header")
                concat( destfile: f.absolutePath, fixlastline:"yes")
                {
                    //  Remove ## comment lines - then copy first header line
                    //
                    filterchain()
                    {
                        striplinecomments() { comment(value: '##') }
                        headfilter( lines:1)
                    }
                    fileset(dir: fromDir) { include( name:incl ); date( datetime: from, when: 'after') }
                }
            }

            echo("Ant loader: copying file data")
            concat( destfile: f.absolutePath, append: "yes", fixlastline:"yes")
            {
                if ( header )
                {
                    filterchain()
                    {
                        striplinecomments() { comment(value: "#") }
                    }
                }
                fileset(dir: fromDir) { include( name:incl ); date( datetime: from, when: 'after') }
            }
        }

        if( ! f.exists()) log.warn( "No records found for ${f.name}" )

        return( f.exists())
    }

    /**
     * Copy a files set to a new dir
     *
     * @param fromDir   Source destination dir
     * @param incl      Include file pattern
     * @param toDir     Destination dir
     * @param comments  Preserve comments flag
     */
    static void copyFiles( String fromDir, String incl, Date fromDate, String toDir, Boolean comments )
    {
        //  Set date into ANT format
        //
        def sdf  = new SimpleDateFormat("MM/dd/yyyy hh:mm a")
        def from = sdf.format(fromDate)

        def ant = new AntBuilder()
        ant.sequential
        {
            copy( todir:toDir )
            {
                filterchain()
                {
                    if ( ! comments )       // strip out comments
                    {
                        striplinecomments() { comment(value: '#') }
                    }
                }
                fileset( dir:fromDir ) { include( name: incl ); date( datetime: from, when: 'after') }
            }
        }
    }

    /**
     * Check all the ETL subdirectories are present
     * Expecting the ETL config file passed to LoadPathOS has known blocks of
     * configuration for extract, transform, load and postload phases. These define
     * the directory for source and destination of files. ie
     * ETL config file:
     * extract      { outdir    = <dirpath> }
     * transform    { outdir    = <dirpath> }
     * load         { createdir = <dirpath> }
     *
     * @param etl   ETL configuration
     * @return      true if all OK
     */
    static Boolean checkSubDirs( ConfigObject etl )
    {
        //  Create and check extract output directory eg ./Raw
        //
        def eod = new File( etl.extract.outdir as String)
        eod.mkdirs()
        if ( ! eod.directory )
        {
            log.fatal( "Data directory missing ${etl.extract.outdir} directory: " )
            return false
        }

        //  Create and check extract output directory eg ./Staging
        //
        def tod = new File( etl.transform.outdir as String)
        tod.mkdirs()
        if ( ! tod.directory )
        {
            log.fatal( "Data directory missing ${etl.transform.outdir} directory: " )
            return false
        }

        //  Check loader table SQL directory eg ./Tables
        //
        if ( ! new File( etl.load.createdir as String).directory )
        {
            log.fatal( "Data directory missing ${etl.load.createdir} directory: " )
            return false
        }

        return true
    }
}

