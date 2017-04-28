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
            t(  longOpt: 'template',args: 1, required: false,'template .docx to use' )
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

        //  Open template file
        //
        File template = null
        if ( opt.template )
        {
            template = new File( opt.template as String )
            if ( ! template.exists())
            {
                log.fatal( "Template file ${opt.template} doesn't exist")
                return
            }
        }

        //  Perform report generation
        //
        def res = report( rdb, opt.seqrun as String, opt.sample as String, repfile, template )

        log.info( "Done: RunReport ${res ? "Success !" : "Failed !"}" )
    }

    /**
     * Sample report method
     *
     * @param rdb       RDB schema to use for report
     * @param seqrun    Seqrun of sample
     * @param sample    Sample to report
     * @param report    Report file name
     * @return          Success of reporting
     */
    static Boolean report( String rdb, String seqrun, String sample, File report, File template )
    {
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
                return Boolean.FALSE
            }

            //  Find SeqSample object to report
            //
            def ss = SeqSample.findBySeqrunAndSampleName( sr, sample )
            if ( ! ss )
            {
                log.fatal( "Couldn't find seqrun ${seqrun} sample ${sample}")
                return Boolean.FALSE
            }

            //  Find templates for sample
            //
            List<File> templates = [template]
            if ( ! template )
            {
                def rs = new ReportService()
                templates = rs.setTemplates( ss )
                if ( ! templates )
                {
                    log.error( "No templates found for sample ${ss}")
                    return Boolean.FALSE
                }
            }

            //  Run report
            //
            log.info( "Starting report for sample ${ss} into ${report.absolutePath}" )
            def output
            try
            {
                def rrs = new ReportRenderService()
                output = rrs.runReport( ss, false, db.sql(), templates, report )
            }
            catch( Exception e )
            {
                org.codehaus.groovy.runtime.StackTraceUtils.sanitize(e).printStackTrace()
                log.fatal( "Exiting: Couldn't report on ${sample} " + e.toString())
                return( Boolean.FALSE )
            }
            log.info( "Finished report in ${output}")
        }

        return Boolean.TRUE
    }
}

