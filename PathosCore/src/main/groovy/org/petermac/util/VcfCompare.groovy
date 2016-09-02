/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */



package org.petermac.util

/**
 *  Utility to Compare two VCF files
 *
 *  Author:     Ken Doig
 *  Date:       10-Apr-15
 */

import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger

@Log4j
class VcfCompare
{
    /**
     * Main execution thread
     *
     * @param args  CLI arguments
     */
    static void main(args)
    {
        def cli = new CliBuilder(
                usage: 'VcfCompare [options] qry.vcf ref.vcf',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nCompare a VCF variant file to a reference VCF file\n')

        cli.with
        {
            h(longOpt: 'help',    'Usage Information',    required: false)
            d(longOpt: 'debug',   'Turn on debug logging')
        }
        def opt = cli.parse(args)

        if ( ! opt ) return
        List argin = opt.arguments()
        if ( opt.h || argin.size() != 2)
        {
            cli.usage()
            return
        }

        //  Debugging needed
        //
        if ( opt.debug ) Logger.getRootLogger().setLevel(Level.DEBUG)

        //  O p e n   t h e   f i l e s
        //
        //  VCF query file
        //
        File qryf  = new File(argin[0])
        if ( ! qryf.exists())
        {
            log.error( "Query VCF file ${argin[0]} doesn't exist")
            return
        }

        //  VCF reference file
        //
        File reff  = new File(argin[1])
        if ( ! reff.exists())
        {
            log.error( "Reference VCF file ${argin[1]} doesn't exist")
            return
        }

        //  Run the program
        //
        log.info("VcfCompare " + args )

        Map res = runVcfCompare( qryf, reff )

        if ( res )
            log.info("Done, compared ${qryf} to ${reff} \ntp: ${res.tp} fp: ${res.fp} fn: ${res.fn}")
        else
            log.fatal( "Couldn't compare files ${qryf} to ${reff}")
    }

    /**
     * Compare two VCF files to find differences
     *
     * @param   qryf    VCF query file
     * @param   reff    VCF reference file
     * @return          Map of comparison Vcfs [tp:, fp:, fn: ]
     */
    static Map runVcfCompare( File qryf, File reff )
    {
        if ( ! qryf.exists() || ! reff.exists())
        {
            log.error( "Missing VCF files qry=$qryf ref=$reff")
            return [:]
        }

        Map m = vcfCompare( qryf, reff )
        if ( ! m  ) return [:]

        //  Write out differences as VCF files
        //
        m.tp.write( "tp.vcf" )
        m.fp.write( "fp.vcf" )
        m.fn.write( "fn.vcf" )

        //  Return counts
        //
        return [tp: m.tp.nrows(), fp: m.fp.nrows(), fn: m.fn.nrows()]
    }

    /**
     * Compare two VCF files to find delta
     *
     * @param   qryf    VCF query file
     * @param   reff    VCF reference file
     * @return          Map of vcf differences
     */
    static Map vcfCompare( File qryf, File reff )
    {
        def qv = new Vcf(qryf)
        qv.load()
        def rv = new Vcf(reff)
        rv.load()

        Vcf fp = qv.minus(rv)
        Vcf fn = rv.minus(qv)
        Vcf tp = rv.intersect(qv)

        if ( fp && tp && fn )
            return [tp: tp, fp: fp, fn: fn]

        return [:]
    }
}
