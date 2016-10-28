/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

/*
*	RunReport.groovy
*
*	Run a sample report indepedent of Grails using hibernate ORM
*   This is the most efficient way to test report templates
*
*	Usage:  RunReport --seqrun <seqrun> --sample <sample> report.ext (pdf,docx,html)
*
*	01	Ken Doig	06-Aug-2014
*/

package org.petermac.pathos.loader

import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.petermac.pathos.curate.*
import org.petermac.util.DbConnect
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

@Log4j
class RunReport
{
    static void main( args )
    {
        //
        //	Collect and parse command line args
        //
        def cli = new CliBuilder(   usage: "RunReport [options] report.ext",
                                    header: '\nAvailable options (use -h for help):\n',
                                    footer: '\nRun a stand-alone PathOS report\n')

        //	Options to LoadPathOS
        //
        cli.with
        {
            h(  longOpt: 'help',    'this help message' )
            r(  longOpt: 'rdb', 	args: 1, required: true, 'RDB Schema to use eg pa_prod' )
            s(  longOpt: 'sample', 	args: 1, required: true, 'Sample to report' )
            sr( longOpt: 'seqrun', 	args: 1, required: true, 'Seqrun to report' )
            d(  longOpt: 'debug',   'Turn on debug logging')
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

        //  Set RDB to load from
        //
        String    rdb = opt.rdb
        DbConnect db  = new DbConnect(rdb)
        if ( ! db.valid())
        {
            log.fatal( "Unknown RDB schema [${rdb}]")
            return
        }

        //  Run the program
        //
        log.info("RunReport " + args )

        //  Extract file name
        //
        List<String> extra = opt.arguments()
        if ( extra.size() != 1 )
        {
            log.fatal( "Missing report file")
            cli.usage()
            return
        }
        def repname  = extra[0]
        File repfile = new File(repname)
        if ( repfile.exists())
        {
            log.warn( "File already exists, overwriting ${repfile}")
        }

        //  Perform data load
        //
        boolean res = report( rdb, opt.seqrun, opt.sample, repfile )

        log.info( "Done: RunReport ${res ? "Success !" : "Failed !"}" )
    }

    /**
     * Main DB migrator
     *
     * @param rdb       RDB schema to use for report
     * @param seqrun    Seqrun of sample
     * @param sample    Sample to report
     * @param report    Report file name
     */
    static boolean report( String rdb, String seqrun, String sample, File report )
    {
        def cnt

        log.info( "Reporting on ${seqrun}:${sample} from RDM ${rdb}")

        //  Load stand-alone Hibernate context - Database JDBC is embedded in <schema>_loaderContext.xml
        //
        def db  = new DbConnect( rdb )
        ApplicationContext context = new ClassPathXmlApplicationContext( db.hibernateXml )

        SeqSample.withTransaction
        {
            status ->

            //  Find Seqrun object to report
            //
            def sr = Seqrun.findBySeqrun( seqrun )
            if ( ! sr )
            {
                log.fatal( "Couldn't find seqrun ${seqrun}")
                return false
            }

            //  Find SeqSample object to report
            //
            def ss = SeqSample.findBySeqrunAndSampleName( sr, sample )
            if ( ! ss )
            {
                log.fatal( "Couldn't find seqrun ${seqrun} sample ${sample}")
                return false
            }

            //  Find templates for sample
            //
            def rs = new ReportService()
            def templates = rs.setTemplates( ss )
            if ( ! templates )
            {
                log.error( "No templates found for sample ${ss}")
                return false
            }

            //  Run report
            //
            log.info( "Starting report for sample ${ss} into ${report.absolutePath}")
            def output = rs.runReport( ss, db.sql(), templates, report )
            log.info( "Finished report in ${output}")
        }

        return true
    }
}

