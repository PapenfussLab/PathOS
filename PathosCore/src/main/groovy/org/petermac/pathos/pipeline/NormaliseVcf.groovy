/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.pathos.pipeline

import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.codehaus.groovy.runtime.StackTraceUtils
import org.petermac.util.RunCommand

/**
 * Normalise a VCF file to a uniform format and 3' shift variants
 * This utility looks at meta data to determine the downstream tool
 * that created the VCF file. If available, this is used to modify the VCF
 * file to match a standard format. The tool also uses Mutalyzer to 3' shift
 * any variants not conforming to HGVS nomenclature. This allows downstream
 * variant annotation to uniformly annotate variants.                               /
 *
 * Author:  Kenneth Doig
 * Date:    15-Oct-14
 */

@Log4j
class NormaliseVcf
{
    /**
     * main method for CLI execution
     *
     * @param args
     */
    static void main( args )
    {
        //	Collect and parse command line args
        //
        def cli = new CliBuilder(   usage: "NormaliseVcf [options] in.vcf out.vcf",
                                    header: '\nAvailable options (use -h for help):\n',
                                    footer: '\nNormalise the format and location of variants\n')

        //	Options to command
        //
        cli.with
        {
            h( longOpt: 'help',		      'this help message' )
            d( longOpt: 'debug',		  'turn on debugging' )
            f( longOpt: 'force',          'force an already normalised file to be renormalised' )
            r( longOpt: 'rdb',   args: 1, 'cache RDB to use' )
            n( longOpt: 'nocache',        'Dont cache variants' )
        }

        def opt = cli.parse( args )
        if ( ! opt ) return

        List argin = opt.arguments()
        if ( opt.h || argin.size() != 2)
        {
            cli.usage()
            return
        }

        //  Debug ?
        //
        if ( opt.debug ) Logger.getRootLogger().setLevel(Level.DEBUG)
        log.debug( "Debugging turned on!" )

        //  Open files
        //
        def ofile = new File( argin[1] as String )
        if ( ofile.exists()) ofile.delete()

        def infile = new File( argin[0] as String )
        if ( ! infile.exists())
        {
            log.fatal( "File ${infile.name} doesn't exist")
            System.exit(1)
        }

        //  Test Mutalyzer is available
        //
        if ( ! (new Mutalyzer()).ping())
        {
            log.fatal( "Can't connect to mutalyzer.nl server")
            System.exit(1)
        }

        //  Perform data load
        //
        def nmut = 0
        log.info( "Start NormaliseVcf " + args )

        try
        {
            nmut = normaliseVcf( infile, ofile, opt.rdb ?: null, opt.nocache )
        }
        catch( Exception e )
        {
            StackTraceUtils.sanitize(e).printStackTrace()
            log.fatal( "Exiting: Couldn't normalise file ${infile} " + e.toString())
            System.exit(1)
        }

        if ( ! nmut )
        {
            log.error( "Done: no mutations found in ${infile}" )
            if ( ofile.exists()) ofile.delete()      // cleanup empty file
            System.exit(1)
        }

        log.info( "Done: processed ${nmut} mutations into ${ofile}" )
    }

    /**
     * Normalise VCF variants
     *
     * @param   infile      VCF File
     * @param   ofile       Output VCF file
     * @param   cacheDB     Cache DB to use
     * @return              Number of variants output
     */
    static int normaliseVcf( File infile, File ofile, String cacheDB, boolean nocache )
    {
        return MutalyzerUtil.convertVcf( infile, ofile, cacheDB, nocache )
    }
}