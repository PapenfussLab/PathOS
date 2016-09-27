/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.pathos.pipeline

/**
 *  Utility to Merge multipl VCF files
 *
 *  Author:     Ken Doig
 *  Date:       24-May-16
 */

import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.petermac.util.Vcf

@Log4j
class VcfMerge
{
    /**
     * Main execution thread
     *
     * @param args  CLI arguments
     */
    static void main(args)
    {
        def cli = new CliBuilder(
                usage: 'VcfMerge [options] vcf1.vcf vcf2.vcf ...',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nMerge VCF variant files into a merged VCF file\n')

        cli.with
        {
            h(longOpt: 'help',    'Usage Information',    required: false)
            o(longOpt: 'output',  'Output VCF file',      required: true,  args: 1)
            l(longOpt: 'labels',  'Comma separated variant labels identifiying source VCFs [file names]', required: false, args: 1)
            d(longOpt: 'debug',   'Turn on debug logging')
        }
        def opt = cli.parse(args)

        if ( ! opt ) return
        List argin = opt.arguments()
        if ( opt.h || argin.size() < 2)
        {
            cli.usage()
            return
        }

        //  Debugging needed
        //
        if ( opt.debug ) Logger.getRootLogger().setLevel(Level.DEBUG)
        log.debug( "Debugging turned on !" )

        //  O p e n   t h e   f i l e s
        //
        //  VCF output file
        //
        File outf  = new File(opt.output as String)
        if ( outf.exists())
        {
            log.warn( "Ouput VCF file exists ${outf} - Overwriting")
            outf.delete()
        }

        //  VCF Input files
        //
        List<File> vcfs = []
        for ( ivf in argin )
        {
            File vcf = new File( ivf as String )
            if ( ! vcf.exists())
            {
                log.fatal( "File ${vcf} doesn't exists: Exiting")
                System.exit(1)
            }
            vcfs << vcf
        }

        //  Set variants labels
        //
        List<String> labels = vcfs.collect { it.name }
        if ( opt.labels )
        {
            labels = (opt.labels as String).split(/,/)
        }

        //  Check for correct number of labels
        //
        if ( labels.unique().size() != vcfs.size())
        {
            log.fatal( "Number of labels ${labels.size()} doesn't match number of VCFs ${vcfs.size()}")
            System.exit(1)
        }

        //  Run the program
        //
        log.info("VcfMerge " + args )

        boolean res = runVcfMerge( outf, vcfs[0], labels[0], vcfs[1..-1], labels[1..-1] )

        if ( res )
            log.info("Done, merged ${vcfs} into ${outf} ")
        else
            log.fatal( "Couldn't merge files into ${outf}")
    }

    /**
     * Run hte merge VCF process
     *
     * @param outf      Output VCF File
     * @param prim      Primary VCF File
     * @param primLabel Label for Primary VCF variants
     * @param vcfs      List of VCF Files to merge into Primary
     * @param vcfLabels Labels for merged VCF variants
     * @return          True if success
     */
    static boolean runVcfMerge( File outf, File prim, String primLabel, List<File> vcfs, List<String> vcfLabels )
    {
        Vcf primVcf = new Vcf( prim )
        int lines = primVcf.load()
        log.info( "Loaded ${lines} lines from ${prim}")

        int i = 0
        for ( vcf in vcfs )
        {
            Vcf mrgVcf = new Vcf( vcf )
            lines = mrgVcf.load()
            log.info( "Loaded ${lines} lines from ${vcf}")

            //  Merge each VCF with primary VCF
            //
            primVcf = mergeVcf( primVcf, primLabel, mrgVcf, vcfLabels[i++] )
            if ( ! primVcf )
            {
                log.error( "Failed to merge ${vcf} into ${prim}")
                return false
            }
        }

        //  Write out merge result
        //
        if ( ! primVcf.write( outf ))
        {
            log.error( "Failed to write ${outf}")
            return false
        }

        return true
    }

    /**
     * Merge two VCF objects
     *
     * @param primary    Target VCF
     * @param primLabel  Primary label for variant caller
     * @param merge      Merge VCF
     * @param mergeLabel Merge label for variant caller
     * @return           True if success
     */
    static Vcf mergeVcf( Vcf primary, String primLabel, Vcf merge, String mergeLabel )
    {
        Vcf merged = new Vcf( primary )
        List<Map> pmaps = primary.rowMaps
        List<Map> mmaps = merge.rowMaps
        List    varCall = []

        //  Loop through variants and flag
        for ( pmap in pmaps )
        {
            String vc = primLabel
            Map m = mmaps.find { it.CHROM == pmap.CHROM && it.POS == pmap.POS && it.REF == pmap.REF && it.ALT == pmap.ALT }

            if ( m )
            {
                log.debug( "Found matching variant ${m.CHROM + ':' + m.POS}" )
                log.debug( "Primary variant ${pmap.CHROM + ':' + pmap.POS}" )
                vc = "Intersection"
                mmaps.remove( m )
                log.debug( "Merge variants remaining ${mmaps.size()}" )
            }

            varCall << vc
        }

        //  Add remaining merge Maps into primary
        //
        for( m in mmaps )
        {
            pmaps   << m
            varCall << mergeLabel
            //log.debug( "Added merge map ${m}")
        }

        //  Add new header column
        //
        Map varcallCol = [ name: 'Identified', cat: 'INFO', type: 'String', description: 'Variant caller of variant']

        log.debug( "Merge rows ${pmaps.size()} varcall rows=${varCall.size()}")
        merged.setRowMaps( pmaps )
        merged.addColumn( varcallCol, varCall )

        //  Sort list of variants
        //
        merged.sort()

        return merged
    }
}
