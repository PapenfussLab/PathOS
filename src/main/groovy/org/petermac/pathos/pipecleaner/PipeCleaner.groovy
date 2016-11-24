/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.pipecleaner

import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.petermac.util.FileUtil
import org.petermac.util.Locator
import org.petermac.util.RunCommand
import org.petermac.util.Tsv
import org.petermac.util.Vcf
import org.petermac.util.VcfCompare


/**
 *	PipeCleaner.groovy
 *
 *	Validate a pipeline end to end with a config driven test
 *
 *	 Usage: PipeCleaner --config test001.pc
 *
 *	 01	 kdoig	 06-May-2013
 *   02  kdoig   24-Jun-2014     Complete redesign and rebuild
**/

@Log4j
class PipeCleaner
{
    static Locator loc = Locator.instance       // Singleton locator class

    //  Command line main
    //
    static void main( args )
    {
        //	Collect and parse command line args
        //
        def cli = new CliBuilder(   usage: "PipeCleaner [options]",
                                    header: '\nAvailable options (use -h for help):\n',
                                    footer: '\nRun a set of pipeline tests\n')

        //	Options to PipeCleaner
        //
        cli.with
        {
            h( longOpt: 'help',		'this help message' )
            c( longOpt: 'config', 	args: 1, required: true, 'PipeCleaner config file' )
            p( longOpt: 'phase', 	args: 1, 'PipeCleaner phases to perform eg reads,test,report,graph [test,report]' )
            d( longOpt: 'debug',    'Turn on debug logging')
        }

        //  Process options
        //
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
        def  cnfFile = new File( opt.config as String )
        if ( !  cnfFile.exists())
        {
            log.fatal( "PipeCleaner Config file doesn't exist " +  cnfFile.name )
            return
        }

        //  Perform test, default phases test then report
        //
        log.info( "Start PipeCleaner " + args )

        pipeCleaner( cnfFile, opt.phase ?: 'test,report' )

        log.info( "Done: processed ${cnfFile.name}" )
    }

    /**
     * Run a suite of tests on pipelines for a pc config file
     *
     * @param   cnfFile File with pc config
     * @param   phases  String of comma seperated phases
     */
    static pipeCleaner( File cnfFile, String phases )
    {
        //  Load config and validate
        //
        ConfigObject cnf = config( cnfFile, null )
        if ( ! cnf ) return false

        //  Loop through the tests, if no repeat block then just once
        //
        def loops = cnf.repeat ? cnf.repeat.loopArr?.size() : 1
        for ( loop in 0..loops-1 )
        {
            //  Reevaluate config file if it contains a repeat block for looping
            //
            if ( cnf.repeat )
                cnf = config( cnfFile, loop as String )

            //  Run pipeline
            //
            if ( phases.contains('reads'))
                if ( ! genReads( cnf )) return false

            //  Run pipeline
            //
            if ( phases.contains('test'))
                if ( ! runPipeline( cnf )) return false

            //  Report results
            //
            if ( phases.contains('report'))
                if ( ! report( cnf )) return false
        }

        //  Graph results Todo: implement this in R ?!
        //
        if ( phases.contains('graph')) log.warn( "PipeCleaner graphing not yet implemented" )
    }

    /**
     * Generate reads for pipeline testing
     *
     * @param   cnf     Test configuration
     * @return          true if reads generated
     */
    static boolean genReads( ConfigObject cnf )
    {
        String cmd = cnf.reads?.genCmd
        if ( cmd )
        {
            log.info( "Before Read Generation: \n${cmd}")
            String out = new RunCommand( cmd ).run()
            log.debug( "After  Read Generation: \n${out}")
        }

        return true
    }

    /**
     * Submit tests to pipeline
     *
     * @param   cnf     Test configuration
     * @return          number of samples submitted
     */
    static boolean runPipeline( ConfigObject cnf )
    {
        def    sut = cnf.systemUnderTest.pipeline
        if ( ! sut ) { log.error("Missing systemUnderTest.pipeline in config file"); return false }
        String cmd = sut.runCmd
        if ( ! cmd ) { log.error("Missing pipeline command in config file"); return false }

        log.info( "Before Pipeline: \n${cmd}")
        String out = new RunCommand( cmd ).run()
        log.debug( "After  Pipeline: \n${out}")

        //  Expected VCF
        //
        def    cmpVcf = cnf.report?.compareVcf
        String rd     = cnf.systemUnderTest.pipeline.resultDir
        if ( ! cmpVcf || ! rd ) return false

        def ef  = new File( cmpVcf.expectFile as String )
        def exp = new Tsv( ef )
        if ( ! ef.exists() || ! exp.load( true ))
        {
            log.error( "Couldn't load expected VCF ${ef.absolutePath}")
            return false
        }

        //  Save expected VCF into results directory
        //
        String sf = rd + "/expect.vcf"
        log.debug( "Save expect.vcf to ${sf}")
        if ( ! exp.write( sf ))
        {
            log.error( "Couldn't write ${sf}")
            return false
        }

        return true
    }

    /**
     * Report test results
     *
     * @param cnf   test configuration
     */
    static boolean report( ConfigObject cnf )
    {
        def    cmpVcf = cnf.report?.compareVcf
        String rd     = cnf.systemUnderTest.pipeline.resultDir
        if ( ! cmpVcf || ! rd ) return false

        //  Expected VCF
        //
        def ef  = new File( rd, 'expect.vcf' )
        def exp = new Tsv( ef )
        if ( ! ef.exists() || ! exp.load( true ))
        {
            log.error( "Couldn't load expected VCF ${ef.absolutePath}")
            return false
        }

        //  Actual VCF
        //
        def af  = new File( cmpVcf.actualFile as String )
        if ( ! af.exists())
        {
            log.error( "Couldn't load actual VCF ${af.absolutePath}")
            return false
        }
        def act = new Vcf(af)

        //  Generate and write out diagnostics VCF files
        //
        Map m = VcfCompare.runVcfCompare( af, ef )

        //  Summary report
        //
        String summary = """
Summary results for PipeCleaner test
====================================

Test type:           ${cnf.test.testType}
Test name:           ${cnf.test.testName}
Test description:    ${cnf.test.description}
Test reference:      ${cnf.reference.refFasta}
Test results dir:    ${rd}
Test variant counts: expected=${exp.nrows()}, actual=${act.nrows()}
Test response:       true positives=${m.tp}, false negatives=${m.fn}, false positives=${m.fp}
"""

        log.info( summary )
        File res = new File( rd, 'PipeCleanerResults.txt' )
        res.delete()
        res.createNewFile()
        res << summary

        //  Test data for downstream processing
        //
        res = new File( rd, "${cnf.test.testName}.tsv" )
        res.delete()
        res.createNewFile()
        res << """##    Generated by PipeCleaner
##
#sample\tloopVar\texpected\tactual\tTP\tFN\tFP
"""
        def data = [ cnf.test.testName, cnf.repeat.loopVar, exp.nrows(), act.nrows(), m.tp, m.fn, m.fp]
        res << data.join('\t') + '\n'

        return true
    }

    /**
     * Parse the configuration file
     *
     * @param   cnfFile     Groovy PipeCleaner configuration file
     * @param   environment Parameter to use when evaluating the config file (eg loop variable)
     * @return              Evaluated config file as a ConfigObject structure
     */
    static ConfigObject config( File cnfFile, String environment )
    {
        def cnf = new ConfigSlurper( environment ).parse(cnfFile.getText())

        if ( ! validateConfig( cnf )) return null

        log.info( "Loaded PipeCleaner config file: ${cnfFile.absolutePath} Environment: ${environment}")

        return cnf
    }

    /**
     * Validate a pipecleaner config file
     *
     * @param   cnf     Parsed config file
     * @return          true if OK
     */
    static boolean validateConfig( ConfigObject cnf )
    {
        if ( ! cnf                  ) { log.error( "Expecting a config"         ); return false }
        if ( ! cnf.test             ) { log.error( "Expecting a test block"     ); return false }
        if ( ! cnf.reads            ) { log.error( "Expecting a reads block"    ); return false }
        if ( ! cnf.systemUnderTest  ) { log.error( "Expecting a SUT block"      ); return false }
        if ( ! cnf.report           ) { log.error( "Expecting a report block"   ); return false }

        return true
    }
}
