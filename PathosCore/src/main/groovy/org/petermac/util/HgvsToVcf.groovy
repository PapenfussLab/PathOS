/*
 * Copyright (c) 2018. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.util

import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.petermac.pathos.pipeline.HGVS
import org.petermac.pathos.pipeline.Locus

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Utility command to convert a list of HGVSg variants to VCF format
 *
 * User: Ken Doig
 * Date: 22/11/2018
 * Time: 5:35 PM
 */

@Log4j
class HgvsToVcf
{
    /**
     * Main execution thread
     *
     * @param args  CLI arguments
     */
    static void main(args)
    {
        def cli = new CliBuilder(
                usage: 'HgvsToVcf [options] in.hgvs out.vcf',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nConvert HGVSg variants to a VCF variant file\n')

        cli.with
                {
                    h(longOpt: 'help', 'Usage Information', required: false)
                    s(longOpt: 'sample', 'Sample Name', args: 1, required: false)
                    d(longOpt: 'debug', 'Turn on debug logging')
                }
        def opt = cli.parse(args)

        if (!opt) return
        List argin = opt.arguments()
        if (opt.h || argin.size() != 2)
        {
            cli.usage()
            return
        }

        //  Debugging needed
        //
        if (opt.debug) Logger.getRootLogger().setLevel(Level.DEBUG)

        //  O p e n   t h e   f i l e s
        //

        //  HGVS input file
        //
        File hgvsf = new File(argin[0])
        if (!hgvsf.exists())
        {
            log.error("Input HGVS file ${argin[0]} doesn't exist")
            return
        }

        //  VCF output file
        //
        File vcff = new File(argin[1])
        if (vcff.exists()) vcff.delete()

        //  Run the program
        //
        log.info("HgvsToVcf " + args)

        def nlines = convertHgvsToVcf( hgvsf, vcff, opt.sample ?: '' )

        log.info("Done, processed ${nlines} lines")
    }

    /**
     * Process files from HGVSg to VCF
     *
     * @param hgvsf
     * @param vcff
     * @param sample
     * @return      number of lines processed
     */
    public static int convertHgvsToVcf(File hgvsf, File vcff, String sample )
    {
        int nlines  = 0

        vcff << Vcf.header( 'HgvsToVcf', null, sample )

        hgvsf.eachLine
        {
            hgvsg ->

            Map vcfm = hgvsToVcf( hgvsg )
            log.debug( "HGVS=${hgvsg} Map=${vcfm}" )

            if ( vcfm )
            {
                //#CHROM POS ID REF ALT QUAL FILTER INFO FORMAT sample
                //
                List vcfcols = [ vcfm.chr, vcfm.pos, ".", vcfm.ref, vcfm.alt, ".", ".", "HGVSg=${hgvsg}", "GT", "1/1" ]
                vcff << vcfcols.join("\t") + "\n"
            }
            ++nlines
        }

        return nlines
    }

    /**
     * Find reference bases for a del/ins sequence thats been relocated
     *
     * @param ref       Anchoring REF base
     * @param alt       Anchoring ALT base
     * @param hgvsg     HGVSg of moved variant
     * @return          Map of VCF chr, pos, ref and alt bases strings
     */
    public static Map hgvsToVcf( String hgvsg )
    {
        Map gmap = HGVS.parseHgvsG( hgvsg )
        if ( ! gmap )
        {
            log.error( "Invalid HGVSg [${hgvsg}]" )
            return null
        }

        //  Set VCF Map values for SNV
        //
        String chr = gmap.chr.replaceAll('chr','')      // VCF CHROM is without 'chr'

        if ( gmap.muttype == 'snp' )
        {
            assert gmap.bases.size() == 3   // "G>C"
            return [ chr: chr, pos: gmap.pos, ref: gmap.bases[0], alt: gmap.bases[2] ]
        }

        //  Only change ref/alt for deletions that have moved
        //
        if ( gmap.muttype == 'del' )
        {
            //  Lookup reference genome bases at the locus
            //
            Integer start    = gmap.pos    as Integer
            Integer end      = gmap.endpos as Integer
            String  refbases = new Locus( chr, start-1, end ).bases()  // add previous base for VCF ref base

            if ( ! refbases )
            {
                log.error( "Couldn't find reference bases for ${hgvsg}")
                return null
            }

            return [ chr: chr, pos: start-1, ref: refbases, alt: refbases[0] ]              // refbases are deleted, anchoring refbase is first base
        }

        //  Only change ref/alt for duplications that have moved
        //
        if ( gmap.muttype == 'dup' )
        {
            //  Lookup reference genome bases at the locus
            //
            Integer start    = gmap.pos    as Integer
            Integer end      = gmap.endpos as Integer
            String  refbases = new Locus( chr, start, end ).bases()       // find all duplicated bases

            if ( ! refbases )
            {
                log.error( "Couldn't find reference bases for ${hgvsg}")
                return null
            }

            return [ chr: chr, pos: start, ref: refbases[0], alt: refbases + refbases[0] ]  // refbases are inserted immediately prior to start of dup
        }

        //  Only change ref/alt for insertions that have moved
        //
        if ( gmap.muttype == 'ins' )
        {
            //  Lookup reference genome bases at the locus
            //
            Integer start    = gmap.pos    as Integer
            String  refbases = new Locus( chr, start, start ).bases()     // find ref base

            if ( ! refbases )
            {
                log.error( "Couldn't find reference bases for ${hgvsg}")
                return null
            }

            return [ chr: chr, pos: start-1, ref: refbases, alt: refbases + gmap.bases ]        // gmap.bases are inserted, anchoring refbase is first base
        }

        //  Change ref/alt bases for a delins
        //
        if ( gmap.muttype == 'delins' )
        {
            //  Lookup reference genome bases at the locus
            //
            Integer start    = gmap.pos    as Integer
            Integer end      = gmap.endpos as Integer
            String  refbases = new Locus( chr, start, end ).bases()

            if ( ! refbases )
            {
                log.error( "Couldn't find reference bases for ${hgvsg}")
                return null
            }

            return [ chr: chr, pos: start, ref: refbases, alt: gmap.bases ]
        }

        //  Change ref/alt bases for an inversion
        //
        if ( gmap.muttype == 'inv' )
        {
            //  Lookup reference genome bases at the locus
            //
            Integer start    = gmap.pos    as Integer
            Integer end      = gmap.endpos as Integer
            String  refbases = new Locus( chr, start, end ).bases()

            if ( ! refbases )
            {
                log.error( "Couldn't find reference bases for ${hgvsg}")
                return null
            }

            return [ chr: chr, pos: start, ref: refbases, alt: refbases.reverse() ]
        }

        //  Unknown variant type
        //
        log.error( "Unknown variant type for ${hgvsg}")
        return null
    }
}
