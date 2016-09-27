/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.loader

import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.codehaus.groovy.runtime.StackTraceUtils
import org.petermac.annotate.Annotator
import org.petermac.annotate.VarDataSource
import org.petermac.pathos.pipeline.HGVS
import org.petermac.pathos.pipeline.LoadDetente
import org.petermac.pathos.pipeline.Mutalyzer
import org.petermac.pathos.pipeline.NormaliseVcf
import org.petermac.util.*
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * Load a TSV file of Patients into PathOS
 *
 * Author:  Kenneth Doig
 * Date:    21-Sep-16
 */

@Log4j
class PatientLoader
{
    //  Application File Locator
    //
    Locator loc = Locator.instance

    /**
     * main method for CLI execution
     *
     * @param args
     */
    static void main( args )
    {
        //	Collect and parse command line args
        //
        def cli = new CliBuilder(   usage: "PatientLoader [options]",
                                    header: '\nAvailable options (use -h for help):\n',
                                    footer: '\nLoad a TSV file of Patients, Samples and Assays into PathOS\n')

        //	Options to command
        //
        cli.with
        {
            h( longOpt: 'help',		    'This help message' )
            d( longOpt: 'debug',		'Turn on debugging' )
            p( longOpt: 'patients',     args: 1, required: false, 'TSV file of Patient/Sample/Assays' )
            l( longOpt: 'loaddir',      args: 1, required: false, 'Directory to search for LIMS files (eg /pmc-qmi/Molpathsql)' )
            r( longOpt: 'rdb',          args: 1, required: true,  'RDB to use' )
            e( longOpt: 'errors',       args: 1, 'File name for error records' )
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
        log.debug( "Debugging turned on!" )

        //  Open TSV file
        //
        File patf = null
        if ( opt.patients )
        {
            patf = new File(opt.patients as String )
            if ( ! patf.isFile())
            {
                log.fatal( "File ${patf} doesn't exist")
                System.exit(1)
            }
        }

        //  Search through LIMS output
        //
        if ( opt.loaddir )
        {
            //  Run Detente loader
            //
            patf = new File( "mp_detente.tsv" )
            new LoadDetente().dataload( new File(opt.loaddir as String), patf )
            int nlines = (patf.exists() ? patf.readLines().size() : 0)
            if ( nlines == 0 )
            {
                log.fatal( "No Patient data found in ${opt.loaddir}")
                System.exit(1)
            }
            log.info( "Found ${nlines} from directory ${opt.loaddir}")
        }

        if ( patf == null )
        {
            cli.usage()
            log.fatal( "No Patient data provided, use --patients or --loaddir")
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
        log.info( "Start PatientLoader " + args )

        //  Process Patients
        //
        int npat = new PatientLoader().load( patf, opt.rdb, errFile )

        log.info( "Done: processed ${patf} and ${npat} rows into ${opt.rdb}" )
    }

    /**
     * Load Patient TSV file
     *
     * @param patf
     * @param rdb
     * @param errFile
     * @return          Number of rows output
     */
    int load( File patf, String rdb, File errFile )
    {
        int npat = 0

        //  Load in patients
        //
        Tsv patfile = new Tsv(patf)
        patfile.load(true)
        List<Map> patients = patfile.rowMaps
        if (patients.size() < 1)
        {
            log.fatal("File ${patf} has no patients ${patients.size()}")
            System.exit(1)
        }

        for (patient in patients)
        {
            ++npat
            log.debug("${String.format("%4d", npat)}\t${patient.patient}\t${patient.sample}\t${patient.test_desc}\t${patient.test_set}")
        }

        //  Strip off TSV headers (if any)
        //
        File tmp = FileUtil.tmpFile("${patf}.")
        def lines = patf.readLines()
        for (line in lines)
        {
            if (line.startsWith('#')) continue
            tmp << line + "\n"
        }

        //  Load database with RDB tables mp_vcf
        //
        tableLoad( tmp, 'mp_detente', rdb )

        //  Populate GORM database from RDB
        //
        int vars = loadGorm( rdb )
        log.info( "Loaded ${vars} rows into GORM db ${rdb}" )

        return npat
    }

    /**
     * Load GORM db from rdb tables
     *
     * @param dbname
     * @return
     */
    static int loadGorm( String dbname )
    {
        def dbl = new DbLoader()
        def db  = new DbConnect( dbname )
        dbl.sql = db.sql()

        //  Load stand-alone Hibernate context - Database JDBC is embedded in <schema>_loaderContext.xml
        //
        ApplicationContext context = new ClassPathXmlApplicationContext( db.hibernateXml)

        //  Add the Patients
        //
        int nvar = dbl.addPatients()
        log.info( "Added ${nvar} Patients")

        //  Add the PatSamples
        //
        nvar += dbl.addSamples( true )
        log.info( "Added ${nvar} PatSamples")

        //  Add the PatAssays
        //
        nvar += dbl.addSampleTests( true )
        log.info( "Added ${nvar} PatAssays")

        return nvar
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