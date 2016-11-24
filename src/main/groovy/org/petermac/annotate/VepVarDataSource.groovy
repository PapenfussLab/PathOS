/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.annotate

import groovy.json.JsonBuilder
import groovy.util.logging.Log4j
import org.petermac.pathos.pipeline.HGVS
import org.petermac.pathos.pipeline.Transcript
import org.petermac.util.FileUtil
import org.petermac.util.Locator
import org.petermac.util.RunCommand
import org.petermac.util.Tsv
import org.petermac.util.Vcf
import org.petermac.util.VepToTsv

/**
 * Created for PathOS.
 *
 * Description:
 *
 * This is the class for VEP annotation
 *
 * User: Ken Doig
 * Date: 23-Nov-2014
 */

@Log4j
class VepVarDataSource extends VarDataSource
{
    static Locator  loc  = Locator.instance
    static HGVS     hg
    static def      code = DS.VEP.code()

    VepVarDataSource( String rdb )
    {
        super( rdb )
        hg = new HGVS( rdb )
    }

    /**
     * Add a list of VCF format variants into cache after calling VEP
     *
     * @param   vcflines    List of VCF format lines
     * @param   useCache    True if cache lookup is done
     * @return              Number of variants added
     */
    static int addToCache( List vcflines, boolean useCache = true )
    {
        def jb   = new JsonBuilder()
        int nadd = 0

        Map  hgs       = vcfToHgvsg( vcflines )
        List novelVars = hgs.collect{it.key}

        //  Look up variants from cache first
        //
        if ( useCache )
        {
            novelVars = notInCache( DS.VEP.code(), novelVars )
            if ( vcflines.size())
                log.info( "Found ${vcflines.size()-novelVars.size()}/${vcflines.size()} in ${code} cache")
        }

        if ( novelVars )
        {
            //  VEP all uncached variants
            //
            List<Map> mutvars = runVep( novelVars, hgs )

            //  Process all muts individually
            //
            for ( mut in mutvars )
            {
                //  convert Map to JSON object
                //
                jb(mut)

                //  Add to cache using HGVSg as the key
                //
                Map params   =  [
                                data_source:        code,
                                hgvsg:              mut.hgvsg,
                                attr:               'mut_map',
                                value:              jb.toString(),
                                hgvsc:              (vepToHGVSc( mut ) ?: mut.HGVSc),
                                hgvsp:              mut.HGVSp,
                                version:            'v78',
                                gene:               mut.SYMBOL
                                ]

                List ids = saveValueMap( params )
                if ( ! ids ) log.warn( "Couldn't add ${params}")
                nadd += ids.size()
            }
        }

        return nadd
    }

    /**
     * Run VEP
     *
     * @return          List of HGVSg vars
     * @param lines     Lines to use
     * @return          List of annotated vars
     */
    static List<Map> runVep( List vars, Map veplines )
    {
        //  Dump variants into a temporary file
        //  Todo: replace with FileUtil.tmpFile( 'vep_' ) which is deleted on exit
        //
        File tmpFile = FileUtil.tmpFixedFile( '/tmp', 'vep_' )

        //  Output header
        //
        tmpFile << Vcf.header()

        //  Output variants
        //
        for ( var in vars )
            tmpFile << veplines[var]

        //  Run VEP
        //
        log.info( "Running VEP on ${vars.size()} vars" )
        def cmd   = "mp-vep.sh -i ${tmpFile.absolutePath} ${tmpFile.absolutePath}.vep"
        def sout  = new RunCommand( cmd ).run()
        if ( sout ) log.warn( "VEP command output: " + sout )

        //  Read in VEP output from specific file
        //
        tmpFile = new File( tmpFile.absolutePath + ".vep" )
        if ( ! tmpFile.canRead())
        {
            log.fatal( "No VEP results in ${tmpFile} exiting...")
            System.exit(1)
        }

        //	Unpack VEP file for database loading
        //  Todo: should call a VepToTsv method directly
        //
        def cols = loc.etcDir + "vepcols.txt"
        String[] cmdargs = "--columns ${cols} ${tmpFile.absolutePath} ${tmpFile.absolutePath}.tsv".tokenize(' ')
        new VepToTsv().main( cmdargs )

        //  Check TSV file created
        //
        tmpFile = new File( tmpFile.absolutePath + ".tsv" )
        if ( ! tmpFile.canRead())
        {
            log.fatal( "No VEP TSV results in ${tmpFile} exiting...")
            System.exit(1)
        }

        //  Read VEP output as TSV file and render as a List of Maps
        //
        Tsv tsv = new Tsv( tmpFile )
        tsv.load( true )
        List<Map> muts = tsv.getRowMaps()
        log.info( "Found ${muts.size()} VEP annotations")

        return transcriptFilter(muts)
    }

    /**
     * Convert VEP lines into HGVSg keyed Map of lines
     *
     * @param lines     Lines to convert
     * @return          Map of lines vars
     */
    static Map vcfToHgvsg( List<String> lines )
    {
        Map hgs = [:]

        for ( line in lines )
        {
            def row  = line.tokenize()
            assert row.size() >= 5, "VCF line doesn't have enough columns [$line]"

            //  Convert a VCF row into a Map
            //  Map of converted variant [chr:, pos:, ref:, alt:, ensvar: "chr_pos_ref/alt", hgvsg: ]
            //
            Map var = HGVS.normaliseVcfVar( row[0], row[1], row[3], row[4] )

            def hgvsg = var.hgvsg
            assert hgvsg.startsWith('chr')
            hgs << [(hgvsg): line]
        }

        return hgs
    }

    /**
     * Filter out uninteresting transcripts and variants
     * Loop though all variants and only keep the "best" for each HGVSg variant
     *
     * @param   vars    List<Map> of VEP transcript annotation
     * @return          Filtered Map
     */
    private static List<Map> transcriptFilter( List<Map> vars )
    {
        Map<String,Map> bestVars = [:]      //  Best transcript for a variant

        for ( var in vars )
        {
            var.hgvsg = hg.ensToHgvsg( var.Uploaded_variation )   //  HGVSg variant
            if ( ! var.hgvsg )
            {
                //  unparseable variant
                //
                log.error( "Missing HGVSg for ${var}" )
                continue
            }

            var.hgvsc       = vepToHGVSc( var )                         //  HGVSc variant
            String prefts   = hg.geneToTranscript( var.SYMBOL )         //  preferred TS for gene
            String tss      = var.RefSeq_mRNA                           //  list of '|' separated refseq

            //  Ignore this vep annotation if not preferred for the VEP gene (SYMBOL column)
            //
            if ( ! prefts || ! tss.contains(prefts)) continue

            //  Select the "best" transcript if there are multiple for a variant
            //
            log.debug( "Best=${bestVars[var.hgvsg]?.hgvsc} Target=${var.hgvsc}")
            if ( Transcript.selectCoding( bestVars[var.hgvsg]?.hgvsc, var.hgvsc ))
                bestVars[var.hgvsg] = var
        }

        return bestVars.values() as List<Map>
    }

    /**
     *  Return a canonical HGVSc string from a VEP record,
     *  there my be multiple transcripts seperated by '|' - choose first only
     *
     *  @param vep  Map of VEP transcript attributes
     *  @return     NM_nnnn:c.nnnG>A
     */
    private static vepToHGVSc( Map vep )
    {
        if ( ! vep?.RefSeq_mRNA || ! vep?.HGVSc ) return null
        List   tss   = (vep.RefSeq_mRNA as String).tokenize( '|' )
        String hgvsc = vep.HGVSc

        return tss[0] + ':' + hgvsc
    }

//    /**
//     * Find the 'BEST' transcript if there are multiple transcripts annotated for a variant
//     * Todo: this needs to match logic in Transcript() for transcript selection
//     *
//     * @param best      Current best variant transcript
//     * @param newvar    Contender transcript
//     * @return          Best - currently the coding transcript
//     */
//    private static Map selectTranscript( Map best, Map newvar )
//    {
//        //  best is empty, use new as best
//        //
//        if ( ! best ) return newvar
//
//        //  new is not coding, keep best
//        //
//        if ( ! newvar.HGVSc ) return best
//
//        //  best is not coding but new is, switch to new
//        //
//        if ( ! best.HGVSc ) return newvar
//
//        //  both new and best are coding - warn of conflict Todo: this is a problem for variants tha both have HGVSc set
//        //
//        if ( best.HGVSc && newvar.HGVSc )
//            log.warn( "Overlapping coding transcripts for variant ${best.hgvsg} [${best.SYMBOL}:${best.RefSeq_mRNA}] [${newvar.SYMBOL}:${newvar.RefSeq_mRNA}]")
//
//        return best
//    }

    /**
     * Delete keys from cache
     *
     * @param vars          List of keys to delete
     * @return
     */
    static void removeFromCache( List vars )
    {
        removeFromCache( code, vars)
    }


    /**
     * Get a Map of values from cache
     *
     * @param  var          Variant to check
     * @return              Map of variant values in cache
     */
    static Map getValueMap( String var )
    {
        getValueMap( code as String, var )
    }
}
