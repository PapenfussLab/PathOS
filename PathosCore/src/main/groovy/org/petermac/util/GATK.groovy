/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.broadinstitute.gatk.engine.CommandLineGATK
import org.broadinstitute.gatk.utils.commandline.CommandLineProgram
import org.petermac.pathos.pipeline.Locus

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Launcher for GATK commands as a Groovy method
 *
 * Author:  Ken Doig
 * Date:    08-May2015
 */

@Log4j
class GATK
{
    private String ref   = null                 // Genome reference fasta file
    private String et    = null                 // GATK ET key :-(
    static         debug = false                // debug flag
    static Locator loc   = Locator.instance     // Singleton locator class

    GATK( boolean no_et = true )
    {
        ref = loc.genomePath
        if ( no_et )
        {
            et = loc.gatkET

            //  Check if ET key file exists
            //
            File etf = new File(et)
            if ( ! etf.exists()) et = null
        }
    }

    //  Command line main
    //
    static void main( String[] args )
    {
        int ret = -1

        if ( args.size() > 0 && ( args[0] == '-c' || args[0] == '--cmd' ))
        {
            //  Run a GATK command
            //
            List<String> gatkargs = args[1..-1]
            ret = new GATK().runCmd( gatkargs, 'INFO' )
        }

        //	Collect and parse command line args
        //
        def cli = new CliBuilder(   usage: "GATK [options]",
                                    header: '\nAvailable options (use -h for help):\n',
                                    footer: '\nRun a GATK command\n')

        //	Options to PipeCleaner
        //
        cli.with
        {
            h( longOpt: 'help',         'this help message' )
            d( longOpt: 'debug',        'Turn on debug logging')
            l( longOpt: 'locus',        args: 1, 'Extract reference bases')
           rc( longOpt: 'revcomp',  'Reverse complement bases (locus only)')
            v( longOpt: 'validate',     args: 1, 'Validate a VCF file')
            c( longOpt: 'cmd',          'Run a GATK command with the following args')
        }

        //  Process options
        //
        def opt = cli.parse( args )
        if ( ! opt ) return
        if ( opt.h )
        {
            cli.usage()

            //  Output GATK help essay
            //
            new GATK().runCmd( ['-h'], 'INFO' )

            return
        }

        //  Debug ?
        //
        if ( opt.debug )
        {
            debug = true
            Logger.getRootLogger().setLevel(Level.DEBUG)
        }

        log.info( "Start GATK " + args)

        //  Run locus bases command
        //
        if ( opt.locus )
        {
            String bases = new GATK().getBases( opt.locus, opt.revcomp ?: false )
            if ( bases )
            {
                println( bases )
                ret = 0
            }
        }

        //  Validate a VCF file
        //
        if ( opt.validate )
            ret = new GATK().validateVcf( opt.validate )

        if ( ret )
            log.error( "Done: processed GATK command exit=$ret" )
        else
            log.info( "Done: processed GATK command exit=$ret" )
    }

    /**
     * Get reference bases for a locus using Gatk call
     *
     * @param   locus   Locus to find chr:startpos-endpos
     * @param   revcomp reverse complement locus flag
     * @return          Reference bases at this locus or null if error
     */
    String getBases( String locus, boolean revcomp = false )
    {
        //  Create a tmp file to hold fasta file
        //
        File tmpf = File.createTempFile("tmp.gatk",".fasta")
        tmpf.deleteOnExit()

        List cmd =  [
                    '-T', 'FastaReferenceMaker',
                    '-L', locus,
                    '-raw',
                    '--out', tmpf.absolutePath
                    ]

        //  Run GATK command
        //
        int ret = runCmd( cmd )

        //  Read in bases
        //
        if ( ret == 0 )
        {
            def fa  = tmpf.readLines()
            String bases = fa[0]

            //  Reverse complement result
            //
            if ( revcomp )
                bases = Locus.revcom( bases )

            return bases
        }

        return null
    }

    /**
     * Validate a VCF file strictly
     *
     * @param   vcffile VCF file to validate
     * @return          0 = OK
     */
    int validateVcf( String vcffile )
    {
        List cmd =  [
                    '-T', 'ValidateVariants',
                    '-V', vcffile,
                    // '--warnOnErrors'
                    ]

        //  Run GATK command
        //
        return runCmd( cmd, 'INFO' )
    }

    /**
     * Run a GATK command
     *
     * @param args      Arguments to GATK
     * @param logLevel  Logging level eg INFO,WARN,ERROR,OFF default = WARN
     * @return
     */
    int runCmd( List args, String logLevel = 'WARN' )
    {
        File tmpLog = FileUtil.tmpFile( 'gatkLog' )

        List cmd =      [
                            '-R',   ref,
                            '-l',   (debug ? 'INFO' : logLevel),
                            '-log', tmpLog.absolutePath
                        ]

        //  Add ET options for GATK if we have a key installed
        //
        List etOpt =    [
                            '-et',  'NO_ET',
                            '-K',   et
                        ]

        if ( et ) cmd = (cmd << etOpt).flatten()

        //  Add any other options
        //
        cmd = (cmd << args).flatten()

        //  Run GATK command
        //
        try
        {
            CommandLineGATK instance = new CommandLineGATK();
            CommandLineGATK.start( instance, cmd as String[]);
        }
        catch ( Exception e )
        {
            //StackTraceUtils.sanitize(e).printStackTrace()
            log.error( "Exiting: Error running GATK " + e.toString())
            return -1
        }

        return CommandLineProgram.result
    }
}
