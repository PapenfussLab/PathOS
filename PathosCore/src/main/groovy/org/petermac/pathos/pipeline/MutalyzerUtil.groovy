/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.pathos.pipeline

import groovy.util.logging.Log4j
import org.petermac.annotate.MutVarDataSource
import org.petermac.util.Vcf

/**
 * High level Mutalyser utility methods for processing batches of variants
 *
 * Author:  Kenneth Doig
 * Date:    06-Nov-14
 */

@Log4j
class MutalyzerUtil
{
    /**
     * Run mutalyser batch extract to convert VCF variants
     *
     * @param   infile      VCF File
     * @param   ofile       Output VCF file
     * @param   cacheDB     Annotation cache DB to use
     * @param   nocache     Dont cache variants
     * @return              Number of variants output
     */
    static int convertVcf( File infile, File ofile, String cacheDB, boolean nocache )
    {
        //  Read in VCF variants as HGVSg
        //
        List<String> hgs = vcfVariants( infile )
        if ( ! hgs )
        {
            ofile.createNewFile()   // create empty file for things that need it
            return 0
        }

        //  Process all variants: either from cache or direct to Mutalyzer
        //
        List<Map> vars = []
        if ( nocache )
            vars = normaliseVariants( hgs, cacheDB )
        else
            vars = cacheVariants( hgs, cacheDB )

        //  Output VCF file of variants
        //
        vcfOutput( vars, infile, ofile)

        log.info( "convertVcf(${infile.name}): In ${hgs.size()} Out ${vars.size()}")

        return vars.size()
    }

    /**
     * Extract a List of HGVSg variants from a VCF file
     *
     * @param   vfile   VCF file
     * @return          List of HGVSg variants
     */
    public static List<String> vcfVariants( File vfile, String type = 'hgvsg' )
    {
        assert vfile.exists()

        //  Load in VCF file
        //
        def vcf = new Vcf( vfile )
        vcf.load()
        List<List> rows = vcf.getRows()

        //  Convert VCF variants to HGVSg format
        //
        List<String> hgs = []

        for ( List<String> row in rows )
        {
            if ( row.size() < 10 )
            {
                log.error( "Too few columns for VCF File, num cols=${row.size()}")
                continue
            }

            //  Convert a VCF row into a Map
            //  Map of converted variant [chr:, pos:, ref:, alt:, ensvar: "chr_pos_ref/alt", hgvsg: ]
            //
            Map var = HGVS.normaliseVcfVar( row[0], row[1], row[3], row[4] )
            if ( ! var.hgvsg )
            {
                log.error( "Couldn't normalise VCF variant ${row[0]+':'+row[1]} ${row[3]} > ${row[4]}")
                continue
            }

            //  Add to variant list
            //
            String varfmt = var.hgvsg

            if ( type == 'annovar' ) varfmt = var.annovar
            if ( type == 'vep'     ) varfmt = vepFormat( row, var.hgvsg )

            hgs << varfmt
        }

        log.info( "Loaded ${hgs.size()} variants from ${vfile}")

        return hgs
    }

    /**
     * Create a minimal VCF line for VEP processing
     *
     * @param row
     * @return
     */
    static String vepFormat( List<String> row, String hgvsg )
    {
        return [ row[0], row[1], '.', row[3], row[4], '.', 'PASS', "HGVSg=${hgvsg}", 'GT', '0/1'].join('\t') + '\n'
    }

    /**
     * Retreive a cached list of HGVSg variants by batch Mutalyzer
     *
     * @param   vars        Map of HGVSg variants keyed on HGVSg
     * @param   cacheDB     Annotation cache DB to use
     * @return              List of Maps of Mutalyzer variants
     */
    static List<Map> cacheVariants( List vars, String cacheDB )
    {
        def ds = new MutVarDataSource( cacheDB )

        return ds.addGetCache( vars, true )
    }

    /**
     * Process to normalise a list of HGVSg variants by batch Mutalyzer
     *
     * @param   vars    Map of HGVSg variants keyed on HGVSg
     * @param   rdb     RDB to use for preferred transcripts
     * @return          List of Maps of normalised variants
     */
    static List<Map> normaliseVariants( List vars, String rdb )
    {
        //  Convert hgvsg to transcripts
        //
        List<Map> mutl = Mutalyzer.batchPositionConverter( vars )

        //  Filter transcripts to preferred gene transcripts
        //
        List<Map> filterts = filterTranscripts( mutl, rdb )

        //  Run name checker on HGVSc variants
        //
        List<Map> varchk = nameChecker( filterts )

        //  Reposition 3' shifted variants
        //
        List<Map> varmods = threeShift( varchk )

        return varmods
    }

    /**
     * Filter transcripts to a preferred list
     *
     * @param   mutl        List of Maps for each variant [variant:, error:, transcripts: ]
     * @param   dbname      Database for transcript references
     * @return              List of filtered mRNA refseq transcripts [variant:, error:, transcripts:, filterts:, refseq:, lrg: ]
     */
    static List<Map> filterTranscripts( List<Map> mutl, String dbname )
    {
        //  Transcript class for selecting the preferred transcript for each variant
        //
        def tscl = new Transcript( dbname )

        for ( mut in mutl )
        {
            log.debug(mut)

            if ( mut.error )
            {
                log.warn( "Errors found for variant ${mut}")
                continue
            }

            Map ts = tscl.selectTranscript( mut )
            if ( ! ts )
            {
                log.warn( "No transcripts found for variant ${mut}")
                mut.error = 'No transcripts found'
                continue
            }

            mut.filterts = ts.refseq
            mut.refseq   = ts.refseq
            mut.lrg      = ts.lrg
        }

        return mutl
    }

    /**
     * Validate HGVSc variants and perform 3' shifting and convert ins to dup
     *
     * @param   vars    List of Maps of hgvsg variants
     * @return          List of Maps for each variant
     *                  Map =   [
     *                          in:, error:, transcript:, gene:, variant:, hgvsn:,
     *                          hgvsc:, hgvsp:, hgvsc_full:, hgvsp_full:, genomic_ref:,
     *                          transcript2:, protein_ref:, affected_transcripts:,
     *                          affected_proteins:, restriction_sites_created:, restriction_sites_deleted
     *                          ]
     *
     */
    static List<Map> nameChecker( List<Map> vars )
    {
        if ( ! vars ) return []

        //  Extract HGVSc variants to name check
        //  Use LRG transcript if available otherwise Refseq ts otherwise flag with "NoVar"
        //
        List hgvscs = vars.collect { Map var -> var.refseq ?: "NoVar" }

        //  Perform validation: Use refseq hgvsc
        //
        List<Map> muts = Mutalyzer.batchNameChecker( hgvscs )

        //  Match up calling variant with Mutalyzer results
        //
        assert muts.size() == vars.size(), "Failed to get matching NameChecker results muts=${muts.size()} hgvsc=${vars.size()}"

        int i = 0
        for ( var in vars )
        {
            Map mut = muts[i++]

            if ( mut.in == "NoVar")
            {
                if ( var.hgvsg ) var.hgvsg = HGVS.normaliseHgvsG(var.hgvsg)
                continue
            }
            assert var.filterts == mut.in    // make sure we match with right variant

            var.hgvsc = mut.in
            if ( mut.hgvsc )
            {
                //  Use refseq version of LRG otherwise just normal refseq transcript
                //
                def transcript = mut.transcript
                if ( mut.refseq )
                    transcript = mut.refseq

                var.hgvsc = transcript + ':' + mut.hgvsc
            }

            if ( var.hgvsg ) var.hgvsg = HGVS.normaliseHgvsG(var.hgvsg)
            if ( var.hgvsc != mut.in ) var.status = 'RENAMED'
            if ( mut.hgvsp && mut.protein_ref ) var.hgvsp   = mut.protein_ref + ':' + mut.hgvsp
            if ( mut.gene ) var.gene = Mutalyzer.shortGene(mut.gene)
            if ( mut.error) var.error = mut.error
        }

        return vars
    }

    /**
     * Modify variants that have been redesignated by NameChecker
     *
     * eg   NM_000314.4:c.254-32_254-31insT	 ->  NM_000314.4:c.254-30dup
     *
     * @param   vars    List of Maps of variants
     * @return          repositioned List of Maps
     */
    static List<Map> threeShift( List<Map> vars )
    {
        //  Find all redesignated HGVSc vars
        //
        List<Map> mods = vars.findAll { it.status == 'RENAMED' }

        log.info( "Found ${mods.size()} redesignated variants")
        if ( ! mods ) return vars

        //  Map them back to genomic positions
        //
        List<Map> hgs = Mutalyzer.batchPositionConverter( mods.hgvsc )

        //  One for one match of input/output
        //
        assert mods.size() == hgs.size(), "Failed to get matching PositionConverter results muts=${mods.size()} hgvsc=${hgs.size()}"

        //  Apply the new genomic positions back to variant list
        //
        int i = 0
        for ( vg in hgs )
        {
            Map vh = mods[i++]

            //  Make sure we have the right variant
            //
            assert vg.variant == vh.hgvsc, "Unmatched HGVSc variant in lists vg=${vg.variant} vh=${vh.hgvsc}"

            //  Save errors and original variant
            //
            if ( vg.error )
                vh.error = vg.error

            //  redesignate the variant
            //
            vh.hgvsg = HGVS.normaliseHgvsG( vg.hgvsg )
        }

        return vars
    }

    /**
     * Format a VCF object with additional columns for output
     * Todo: Get rid of the reload of the VCF file, just pass through a VCF rowMap from when it was read in orginally
     *
     * @param vars      List of variant Maps
     * @param vfile     Input VCF file
     * @param ofile     Output VCF file
     * @return          True if OK
     */
    static boolean vcfOutput( List<Map> vars, File vfile, File ofile )
    {
        //  delete output file
        //
        if ( ofile.exists()) ofile.delete()

        //  Load in original VCF file
        //
        assert vfile.exists()
        Vcf vcf = new Vcf( vfile )
        vcf.load()

        //  Match up VCF rows
        //
        Map mv = matchVars( vcf.rowMaps, vars )
        if ( ! mv ) return false

        //  Reset VCF in case any variants were invalid
        //
        vcf.setRowMaps( mv.vcf )

        //  Set list of Mutalyzer parameters to add to VCF file
        //
        vars = mv.mut

        //  Add new header metadata columns to VCF
        //
        Map hgvsgCol = [ name: 'HGVSg', cat: 'INFO', type: 'String', description: 'HGVSg format of variant']
        Map hgvscCol = [ name: 'HGVSc', cat: 'INFO', type: 'String', description: 'HGVSc format of variant']
        Map hgvspCol = [ name: 'HGVSp', cat: 'INFO', type: 'String', description: 'HGVSp format of variant']
        Map geneCol  = [ name: 'gene',  cat: 'INFO', type: 'String', description: 'gene of variant']
        Map lrgCol   = [ name: 'lrg',   cat: 'INFO', type: 'String', description: 'LRG transcript of variant']
        Map errCol   = [ name: 'muterr',cat: 'INFO', type: 'String', description: 'Mutalyzer error message for variant']
        Map stsCol   = [ name: 'status',cat: 'INFO', type: 'String', description: 'Mutalyzer status of variant']

        //  Add new data columns to VCF
        //
        vcf.addColumn( hgvsgCol, vars.hgvsg  as List )
        vcf.addColumn( hgvscCol, vars.hgvsc  as List )
        vcf.addColumn( hgvspCol, vars.hgvsp  as List )
        vcf.addColumn( geneCol,  vars.gene   as List )
        vcf.addColumn( lrgCol,   vars.lrg    as List )
        vcf.addColumn( errCol,   vars.error  as List )
        vcf.addColumn( stsCol,   vars.status as List )

        //  Shift position and ref/alt for 3' shifted variants
        //
        Vcf moveVcf = relocateVars( vcf )

        //  Coalesce duplicate 3' shifted variants
        //
        Vcf coalVcf = coalesceVars( moveVcf )

        //  Sort the variants in case any offsets have made them out of order
        //
        coalVcf.sort()

        //  Write VCF file to disk
        //
        coalVcf.write(ofile)
    }

    /**
     * Validate Mutalyzer List against vars in VCF file
     *
     * @param vcfRows
     * @param vars
     * @return
     */
    static private Map matchVars( List<Map> vcfRows, List<Map> vars )
    {
        if ( ! vars ) return [:]

        List matchVars  = []
        List removeVars = []
        int  idx = 0

        for ( row in vcfRows )
        {
            Map var = HGVS.normaliseVcfVar( row.CHROM, row.POS, row.REF, row.ALT )
            if ( ! var.hgvsg )
            {
                log.error( "Couldn't normalise VCF variant ${row.CHROM+':'+row.POS} ${row.REF} > ${row.ALT}")
                removeVars << idx
                continue
            }

            def vcfHgvsg = var.hgvsg
            if ( idx >= vars.size())
            {
                log.fatal( "Missing vars to match VCF file $idx ${vars.size()}")
                return [:]
            }

            def varHgvsg = vars[idx]?.hgvsg

            boolean match = ( vcfHgvsg == varHgvsg ) || vars[idx]?.error
            if ( match )
            {
                matchVars << ( vcfHgvsg != null ? vars[idx] : [:] )
            }
            else
            {
                log.fatal( "Variant Mismatch ${match} vcf ${vcfHgvsg} mut ${varHgvsg} ${vars[idx]}")
                return [:]
            }

            ++idx
        }

        //  Should have matching number of rows
        //
        if ( matchVars.size() != vcfRows.size())
        {
            log.error( "Mismatched number of variants: VCF=${idx}/${vcfRows.size()} != Mutalyzer=${vars.size()}")

            //  Remove invalid variant rows from VCF file list
            //
            for ( varidx in removeVars )
                vcfRows.remove( varidx )
        }

        return [ vcf: vcfRows, mut: matchVars ]
    }

    /**
     * Relocate the position of any variants 3' shifted
     *
     * @param   vcf     Vcf to modify
     * @return          Filtered and relocated VCF
     */
    static private Vcf relocateVars( Vcf vcf )
    {
        List<Map> rows = vcf.getRowMaps()

        List<Map> newRows = []
        for ( row in rows )
        {
            try
            {
                newRows << moveVar( row )
            }
            catch( NumberFormatException e )
            {
                log.fatal( "Position conversion error: " + e.toString())
                return vcf
            }
        }

        vcf.setRowMaps( newRows )

        return vcf
    }

    /**
     * Move a variants VCF fields if it has been 3' shifted
     *
     * @param   row   Map of VCF fields (unpacked)
     * @return        Modified Map
     */
    static private Map moveVar( Map row )
    {
        row.status = ''     // reset status message to blank
        row.ID     ='.'     // remove any rs IDs as this is propagated to VEP Todo: deprecate

        //  Recalculate HGVSg for this vcf row
        //
        String hgvsg  = HGVS.normaliseVcfVar( row.CHROM, row.POS, row.REF, row.ALT ).hgvsg

        //  Ignore row if unchanged
        //
        if ( row.HGVSg == hgvsg ) return row

        //  Work out offset in bases between HGVSg variants
        //
        Integer offset = HGVS.baseOffset( hgvsg, row.HGVSg )
        def     stsmsg = "RENAMED_from:${hgvsg}_to:${row.HGVSg}"
        log.debug( "In moveVar(): row.HGVSg=${row.HGSVg} hgvsg=${hgvsg} offset=${offset} msg=${stsmsg}")

        //  Has the variant moved ?
        //
        if ( offset )
        {
            //  Adjust chromosome position in VCF
            //
            Integer pos = (row.POS as Integer) + offset
            row.POS = pos as String                  // genomic start position
            def ( ref , alt ) = refAltBases( row.REF, row.ALT, row.HGVSg)
            row.REF = ref
            row.ALT = alt

            //  Create a readable status message eg offset:[+-]nnnbp
            //
            stsmsg += "_offset:${offset>0?'+':''}${offset}bp"
            row.status = stsmsg
        }

        return row
    }

    /**
     * Find reference bases for a del/ins sequence thats been relocated
     *
     * @param ref       Anchoring REF base
     * @param alt       Anchoring ALT base
     * @param hgvsg     HGVSg of moved variant
     * @return          VCF ref and alt bases strings
     */
    private static def refAltBases( String ref, String alt, String hgvsg )
    {
        Map gmap = HGVS.parseHgvsG( hgvsg )
        if ( !  gmap )
        {
            log.error( "Invalid HGVSg [${hgvsg}]" )
            return null
        }

        //  Only change ref/alt for deletions that have moved
        //
        if ( gmap.muttype == 'del' )
        {
            //  Lookup reference genome bases at the locus
            //
            Integer start    = gmap.pos    as Integer
            Integer end      = gmap.endpos as Integer
            String  refbases = new Locus( gmap.chrnum as String, start-1, end ).bases()  // add previous base for VCF ref base

            if ( ! refbases )
            {
                log.error( "Couldn't find reference bases for ${hgvsg}")
                return null
            }

            return [ refbases, refbases[0] ]            // refbases are deleted, anchoring refbase is first base
        }

        //  Only change ref/alt for duplications that have moved
        //
        if ( gmap.muttype == 'dup' )
        {
            //  Lookup reference genome bases at the locus
            //
            Integer start    = gmap.pos    as Integer
            Integer end      = gmap.endpos as Integer
            String  refbases = new Locus( gmap.chrnum as String, start, end ).bases()  // add previous base for VCF ref base

            if ( ! refbases )
            {
                log.error( "Couldn't find reference bases for ${hgvsg}")
                return null
            }

            return [ refbases[0], refbases + refbases[0] ]            // refbases are inserted immediately prior to start of dup
        }

        //  Only change ref/alt for insertions that have moved
        //
        if ( gmap.muttype == 'ins' )
        {
            //  Lookup reference genome bases at the locus
            //
            Integer start    = gmap.pos    as Integer
            String  refbases = new Locus( gmap.chrnum as String, start, start ).bases()  // find ref base

            if ( ! refbases )
            {
                log.error( "Couldn't find reference bases for ${hgvsg}")
                return null
            }

            return [ refbases, refbases + gmap.bases ]   // gmap.bases are inserted, anchoring refbase is first base
        }

        //  Change ref/alt bases for a delins
        //
        if ( gmap.muttype == 'delins' )
        {
            //  Lookup reference genome bases at the locus
            //
            Integer start    = gmap.pos    as Integer
            Integer end      = gmap.endpos as Integer
            String  refbases = new Locus( gmap.chrnum as String, start, end ).bases()

            if ( ! refbases )
            {
                log.error( "Couldn't find reference bases for ${hgvsg}")
                return null
            }

            return [ refbases, gmap.bases ]
        }

        //  No change
        //
        return [ ref, alt ]
    }

    /**
     * Coalesce duplicate variants that have converged by 3' shifting
     *
     * @param   vcf     Vcf to modify
     * @return          Filtered and deduplicated VCF
     */
    static private Vcf coalesceVars( Vcf vcf )
    {
        List<Map> rows = vcf.getRowMaps()

        List<Map> newRows = []
        Map lastvar = [:]
        for ( row in rows )
        {
            //  First time through loop
            //
            if ( ! lastvar )
            {
                lastvar = row
                continue
            }

            //  Check for duplicates
            //
            if ( lastvar.HGVSg && (lastvar.HGVSg == row.HGVSg))
            {
                log.info( "Merging duplicate var ${lastvar.HGVSg} ${lastvar.status}")
                if ((lastvar.status as String).startsWith('RENAMED_from'))
                    lastvar = mergeVar( lastvar, row )
                else
                    lastvar = mergeVar( row, lastvar )
            }
            else
            {
                newRows << lastvar
                lastvar = row
            }
        }

        //  Copy last variant
        //
        if ( lastvar ) newRows << lastvar

        vcf.setRowMaps( newRows )

        return vcf
    }

    /**
     * Merge the VAF read depths for two variants
     *
     * @param dup   Map of variant to merge into
     * @param merge Map of variant to merge from
     * @return      Map of merged variant
     */
    static private Map mergeVar( Map dup, Map merge )
    {
        if ( dup.CHROM != merge.CHROM
        ||   dup.POS   != merge.POS
        ||   dup.REF   != merge.REF
        ||   dup.ALT   != merge.ALT )
        {
            log.error( "Can't merge different variants dup=${dup} merge=${merge}" )
            return dup
        }

        try
        {
            //  Total read depth (take average)
            //
            Integer dp = ((dup.DP as Integer) + (merge.DP as Integer)) / 2
            dup.DP     = dp as String

            //  Allele read depth (sum depths)
            //
            dup.AD     = combineAD( dup.AD, merge.AD )

            dup.status = dup.status + "_MERGED"
        }
        catch( NumberFormatException e )
        {
            log.fatal( "Read depth conversion error: " + e.toString())
            return dup
        }

        return dup
    }

    /**
     * Combine allele depth AD fields. May have multiple comma separated values
     *
     * @param ad1   First AD field
     * @param ad2   2nd AD field
     * @return      Summed AD field
     */
    static private String combineAD( String ad1, String ad2 )
    {
        List ad1lst = ad1.split(',')
        List ad2lst = ad2.split(',')

        if ( ad1lst.size() != ad2lst.size()) return ad1
        String result = ""
        ad1lst.eachWithIndex
        {
            String entry, int i ->
                int ad = (entry as Integer) + (ad2lst[i] as Integer)

                result = result + (result ? ',' : '') + (ad as String)
        }

        return result
    }
}