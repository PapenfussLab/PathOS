/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Kenneth Doig
 */

package org.petermac.pathos.loader

import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.codehaus.groovy.runtime.StackTraceUtils
import org.hibernate.Session
import org.petermac.annotate.*
import org.petermac.pathos.curate.SeqVariant
import org.petermac.pathos.curate.VarFilterService
import org.petermac.pathos.pipeline.HGVS
import org.petermac.pathos.pipeline.Mutalyzer
import org.petermac.pathos.pipeline.MutalyzerUtil
import org.petermac.pathos.pipeline.NormaliseVcf
import org.petermac.util.DbConnect
import org.petermac.util.DbLock
import org.petermac.util.FileUtil
import org.petermac.util.Locator
import org.petermac.util.MysqlCommand
import org.petermac.util.Tsv
import org.petermac.util.Vcf2Tsv
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * Load a list of VCF files into PathOS
 * Normalise and Annotate each VCF file a add new and unique variants into an annotation cache
 *
 * Author:  Kenneth Doig
 * Date:    4-Sep-16
 */

@Log4j
class VcfLoader
{
    //  Locator class for file locations
    //
    private Locator loc = Locator.instance

    //  DbLock for DB access in series
    //
    private static DbLock dblock  = null

    //  DB lock map
    //
    private static Map lockMap = null

    /**
     * main method for CLI execution
     *
     * @param args
     */
    static void main( args )
    {
        //	Collect and parse command line args
        //
        def cli = new CliBuilder(   usage: "VcfLoader [options] in.vcf ...",
                                    header: '\nAvailable options (use -h for help):\n',
                                    footer: '\nNormalise, annotate, cache and load a set of VCF files\n')

        //	Options to command
        //
        cli.with
        {
            h( longOpt: 'help',		    'This help message' )
            d( longOpt: 'debug',		'Turn on debugging' )
            f( longOpt: 'filter',		'Apply filter flags to variants' )
            n( longOpt: 'nocache',		'Dont use annotation cache' )
            r( longOpt: 'rdb',          args: 1, required: true, 'RDB to use' )
            q( longOpt: 'seqrun',       args: 1, required: true, 'Seqrun of VCF files' )
            s( longOpt: 'datasource',   args: 1, 'Comma separated list of datasources to use for annotation [vep]' )
            c( longOpt: 'columns',      args: 1, 'File of columns to output' )
            p( longOpt: 'panel',        args: 1, 'Panel name (must exist in PathOS)' )
            e( longOpt: 'errors',       args: 1, 'File name for error records' )
            mut(longOpt:'mutalyzer',    args: 1, 'Mutalyzer annotation server host [https://mutalyzer.nl]' )
        }

        def opt = cli.parse( args )
        if ( ! opt ) return

        List argin = opt.arguments()
        if ( opt.h || argin.size() < 1 )
        {
            cli.usage()
            return
        }

        //  Debug ?
        //
        if ( opt.debug ) Logger.getRootLogger().setLevel(Level.DEBUG)
        log.debug( "Debugging turned on!" )

        //  Validate data sources
        //
        List dss = [ 'vep'  ]
        if ( opt.datasource )
            dss = (opt.datasource as String).tokenize(',')

        //  Open files
        //
        List<File> vcfFiles = []
        for ( inf in argin )
        {
            def infile = new File( inf as String )
            if ( ! infile.exists())
            {
                log.fatal( "File ${infile.name} doesn't exist")
                continue
            }

            if ( infile.isFile())
                vcfFiles << infile
        }

        if ( ! vcfFiles)
        {
            log.fatal( "No data files to process")
            return
        }

        //  Optional list of columns to output
        //
        File colsf = new File( Locator.instance.etcDir + "vcfcols.txt" )
        if ( opt.columns )
        {
            colsf = new File( opt.columns as String)
        }

        if ( ! colsf.exists())
        {
            log.error( "File ${colsf} doesn't exist")
            return
        }

        //  Test Mutalyzer is available
        //
        String defaultMut = (opt.mutalyzer ?: 'https://mutalyzer.nl')
        if ( ! (new Mutalyzer(defaultMut)).ping())
        {
            log.fatal( "Can't connect to ${defaultMut} server")
            System.exit(1)
        }

        //  Open error file and zero it if required
        //
        File errFile = null
        if ( opt.errors )
        {
            errFile = new File( opt.errors as String )
            errFile.delete()
        }

        //  Perform data load
        //
        log.info( "Start VcfLoader " + (args.size() > 10 ? args[0..10] + "..." : args))

        //  Process VCFs
        //
        new VcfLoader().loadVcf( vcfFiles, opt.seqrun, opt.panel ?: 'NoPanel', opt.rdb, dss, colsf, errFile, opt.filter, opt.nocache, defaultMut )

        log.info( "Done: processed ${vcfFiles.size()} files into ${opt.rdb}" )
    }

    /**
     * Load VCF files
     *
     * @param   vcfs    List of VCF Files
     * @param   seqrun  Sequencing run name
     * @param   panel   Sequencing panel name
     * @param   rdb     Database name to populate
     * @param   dss     List of annotation datasources
     * @param   vcfcols Columns to match in VCF file
     * @param   errFile Error output file
     * @param   filter  Apply filtering
     * @param   nocache Dont use annotation cache
     * @return          Rows loaded
     */
    void loadVcf( List<File> vcfs, String seqrun, String panel, String rdb, List dss, File vcfcols, File errFile, boolean filter, boolean nocache, String mutHost )
    {
        //  Normalise all VCFs
        //
        List<Map> normVcfs = normaliseVcfs( vcfs, rdb, nocache, mutHost )

        //  Add VCFs to annotation cache
        //
        log.info( "Annotating variants with ${dss}")
        int nmut = new Annotator().annotateVcf( normVcfs.normFile, rdb, dss, errFile, mutHost )

        //  Create an aggregate TSV file of all variants
        //
        File tsv = new File( "vcfs.tsv" )            //  Create temporary TSV file for all variants
        tsv.delete()
        List<Map> sammap = tsvVcfs( normVcfs, tsv, seqrun, panel, vcfcols )

        //  Load Seqrun and Sample table into RDB
        //
        loadSeqrun( sammap, rdb )

        //  Load database with RDB tables mp_vcf
        //
//        tableLoad( tsv, 'mp_vcf', rdb )

        //  Populate GORM database from RDB
        //
        loadGorm( tsv, rdb )

        //  Apply filtering to variants if it was asked
        //
        if ( filter ) applyFilter( rdb )

    }

    /**
     * Normalise each VCF file
     *
     * @param vcfs      List of VCF Files
     * @param rdb       DN name of annotation cache
     * @param nocache   Use cache flag
     * @return          List of normalised VCF Files
     */
    static List<Map> normaliseVcfs( List<File> vcfs, String rdb, boolean nocache, String mutHost)
    {
        log.info( "Normalising variants with ${mutHost}")

        List<Map> normVcfs = []

        for ( vcf in vcfs )
        {
            File normVcf = FileUtil.tmpFixedFile()
            log.info( "Normalising VCF ${vcf.name} into ${normVcf}" )
            try
            {
                new MutalyzerUtil(mutHost).convertVcf( vcf, normVcf, rdb, nocache )
            }
            catch( Exception e )
            {
                StackTraceUtils.sanitize(e).printStackTrace()
                log.fatal( "Exiting: Couldn't normalise file ${vcf} " + e.toString())
                System.exit(1)
            }

            //  Get sample name from file name
            //
            String sample = vcf.name
            def match = ( sample =~ /([^\.]+)/ )
            if ( match.count ) sample = match[0][1]

            //  Save for next stage
            //
            normVcfs << [ normFile: normVcf, sample: sample ]
        }

        return normVcfs
    }

    /**
     * Create a TSV containing each VCF file variants
     *
     * @param vcfs      List of Maps of VCF Files and sample names
     * @param vcfcols   Column names of TSV file
     * @return          List of samples
     */
    static List<Map> tsvVcfs( List<Map> vcfs, File tsv, String seqrun, String panel, File vcfcols )
    {
        int     nl     = 0
        List    sammap = []

        log.info( "Creating variant TSV file ${tsv}")

        //  Process each VCF file into a TSV
        //
        boolean header = true
        for ( vcf in vcfs )
        {
            nl += Vcf2Tsv.vcf2Tsv( vcf.normFile, tsv, vcf.sample, seqrun, panel, vcfcols, header )

            sammap << [ seqrun: seqrun, sample: vcf.sample, panel: panel, pipeline: 'VcfLoader' ]
            header = false
        }

        return sammap
    }

    /**
     * Create a file of Seqrun/Sample meta data
     *
     * @param   sammap  List of Maps of runs/samples/panels
     * @param   rdb     DB name to load into
     * @return
     */
    Boolean loadSeqrun( List<Map> sammap, String rdb )
    {
        //  Load database with RDB tables mp_vcf, mp_seqrun
        //
        def srf = new File( "mp_seqrun.tsv")
        srf.delete()

        //  Create mp_seqrun.tsv file
        //
        log.info( "Creating Seqrun/Sample TSV file ${srf}")

        LoadPathOS.createSeqrun( sammap, srf, false )

        def res = tableLoad( srf, 'mp_seqrun', rdb )
        log.info( "Loaded mp_seqrun from ${srf}, result=${res}" )

        return true
    }

    /**
     * Load GORM db from rdb tables
     *
     * @param dbname
     * @return
     */
    static int loadGorm( File tsv, String dbname )
    {
        log.info( "Loading variant file ${tsv} into GORM [${dbname}]")

        def dbl = new DbLoader()

        //  Load HGVS transcripts
        //
        def hg = new HGVS( dbname )

        //  Annotation object for DB
        //
        def db  = new DbConnect( dbname )
        dbl.sql = db.sql()

        //  Load stand-alone Hibernate context - Database JDBC is embedded in <schema>_loaderContext.xml
        //
        ApplicationContext context = new ClassPathXmlApplicationContext( db.hibernateXml)

        //  Add the Seqrun
        //
        int nrow = dbl.addSeqrun( true )
        log.info( "Added ${nrow} Seqrun")

        //  Add the Panels/Assays
        //
        nrow = dbl.addPanels( true )
        log.info( "Added ${nrow} Panels")

        //  Add the SeqSamples
        //
        nrow = dbl.addSeqSamples( true )
        log.info( "Added ${nrow} SeqSamples")

        //  Add the SeqVariants
        //
        nrow = LoadSeqVariant.addSeqVariant( tsv, dbl.sql, dbname )
        log.info( "Added ${nrow} SeqVariants")

        return nrow
    }

    /**
     * Apply filter flags to SeqVariant
     *
     * @param rdb
     */
    static void applyFilter( String rdb )
    {
        //  Set up lock
        //
        dblock = new DbLock( rdb, 120 )

        while ( lockMap = dblock.hasLock())
        {
            log.info( "Waiting for DB Lock on ${rdb} lock=${lockMap}")
            sleep( 60 * 1000 )                           // 1 minute wait
        }

        //  Acquire lock
        //
        lockMap = dblock.setLock()
        log.info( "Set DB Lock on ${rdb} lock=${lockMap}")

        //   Filter added SeqVariants
        //
        try {
            SeqVariant.withSession
                    {
                        Session session ->
                            def vfs = new VarFilterService()
                            int cnt = vfs.applyFilter(session, false)
                            log.info("Set Filter for ${cnt} Variants")
                    }
        }  catch (Exception e) {

            //  If we have an exception, dump the stack trace and error message.
            //  Don't exit, though.
            //
            StackTraceUtils.sanitize(e).printStackTrace()
            log.fatal( "Exception while running applyFilter: " + e.toString() )
        } finally {

            //  Clear the lock despite what happened.
            //
            if ( lockMap )
            {
                lockMap = dblock.clearLock( lockMap )
                log.info( "Cleared DB Lock on ${rdb} lock=${lockMap}")
            }
        }
    }

    /**
     * Load TSV file into DB
     *
     * @param tsv       File of TSV file
     * @param table     Name of RDB table to populate
     * @param dbname    Name of db to populate
     * @return          true if succeeded
     */
    Boolean tableLoad( File tsv, String table, String dbname )
    {
        log.info( "Loading from ${tsv} to: ${dbname}")

        //  Set etc dir
        //
        def sqlDir = "${loc.pathos_home}/ETL/Tables/"

        //  Find table create script
        //
        def tfile = new File( sqlDir, table + '.sql' )
        if ( ! tfile.exists())
        {
            log.fatal( "Table create script doesn't exist: ${tfile.path}" )
            System.exit(1)
        }

        //  SQL to drop and create table
        //
        def dml =   """
                    drop table if exists ${table};
                    source ${tfile.path};
                    """

        //  Find table raw data
        //
        if ( ! tsv.exists())
        {
            log.fatal( "Table data doesn't exist: ${tsv.path}" )
            System.exit(1)
        }

        //  SQL to load in data
        //
        dml +=  """
                    load data local infile '${tsv.path}' into table ${table};
                    show warnings;
                    """

        //  Drop and load table
        //
        log.info( "Loading Table ${table} ...")
        new MysqlCommand( dbname ).run( dml )

        //  Look for optional index file
        //
        def ifile = new File( sqlDir , table + '.idx.sql' )
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
}