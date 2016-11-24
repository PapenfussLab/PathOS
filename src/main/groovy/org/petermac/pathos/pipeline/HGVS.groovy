/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.pathos.pipeline

import groovy.util.logging.Log4j
import org.broadinstitute.sting.utils.analysis.AminoAcidTable
import groovy.sql.Sql
import org.petermac.pathos.pipeline.Transcript

//
//	DBMigrate.groovy
//
//	Migrate Path-OS Database from a previously created RDB in GORM structure
//
//	Usage:
//
//	01	kdoig	07-Mar-2014
//

/**
 *  HGVS.groovy
 *
 *  Set of utility routines for parsing, converting and generating HGVS compliant variants
 *
 *  Author: Ken Doig
 *
 *  Date: 30-Apr-13
 */

@Log4j
class HGVS
{
    def db
    Sql sql
    static def aat = new AminoAcidTable()

    static Map geneToTx = [:]

    /**
     * Constructor: Load in genes for lookup of default transcript
     *
     * @param dbname    database to look for reference transcripts
     */
    HGVS( String dbname )
    {
        geneToTx = Transcript.preferred( dbname )
    }

    /**
     * Get HGVS type of variant
     *
     * @param   var Variant to check
     * @return      Character of type of variant
     */
    static String hgvsType( String var )
    {
        def match = ( var =~ /.+:([a-z])\..+/ )
        if ( match.count != 1) return null

        return match[0][1]
    }

    /**
     * Check format of variant
     *
     * @param var       Variant to check
     * @return          true if HGVSg
     */
    static Boolean isHgvsG( String var )
    {
        return hgvsType(var) == 'g'
    }

    /**
     * Check format of variant
     *
     * @param var       Variant to check
     * @return          true if HGVSc
     */
    static Boolean isHgvsC( String var )
    {
        return hgvsType(var) == 'c'
    }

    /**
     * Check format of variant
     *
     * @param var       Variant to check
     * @return          true if HGVSp
     */
    static Boolean isHgvsP( String var )
    {
        return hgvsType(var) == 'p'
    }

    /**
     * Normalise a HGVS style variant so they are consistent Todo: this should throw an exception
     *
     * @param var       Variant to normalise
     * @return          Cleaned up version for database joins
     */
    static String normalise( String var )
    {
        var = var?.trim()?.replace( ' ','')   // remove any embedded spaces

        if ( isHgvsC(var))
            return normaliseHgvsC(var)

        if ( isHgvsG(var))
            return normaliseHgvsG(var)

        log.warn( "Normalisation not yet supported for variant type: ${var}")

        return var ?: ''
    }

    /**
     * Normalise a HGVSg variant so they are consistent
     *
     * @param var       HGVSg Variant to normalise
     * @return          Cleaned up version for database joins
     */
    static String normaliseHgvsG( String var )
    {
        //  Convert to chrX:g.xxx format
        //
        Map gvar = parseHgvsG( var )
        if ( gvar )
        {
            return gvar.chr + ':' + gvar.var
        }

        return var
    }

    /**
     * Compare two HGVSg variants
     *
     * @param           var1
     * @param           var2
     * @return          True if variants are equal
     */
    static boolean compareHgvsg( String var1, String var2 )
    {
        if ( var1 == var2 ) return true

        //  Convert to chromosome format
        //
        def v1 = parseHgvsG( var1 )
        def v2 = parseHgvsG( var2 )

        return (v1.chr == v2.chr) && (v1.var == v2.var)
    }

    /**
     * Parse a HGVSg variant
     *
     * @param var       HGVSg Variant to parse
     * @return          Map of parsed values (transcript, version, hgvstype, pos, endpos, ref, alt, muttype)
     */
    static Map parseHgvsG( String var )
    {
        var = var?.trim()?.replace( ' ','')     // remove embedded spaces

        //  Parse NC_000017.10:g.41276247A>G or chr17:g.41276247A>G
        //
        def match = ( var =~ /(NC_\d+\.\d+|chr\d+):g\.([\d+\-_*]+)(.*)/ )
        if ( match.count == 1)
        {
            log.debug( "Parse HGVSg, in: " + var + " out " + match[0])
            String trs  = match[0][1]
            String pos  = match[0][2]
            String rest = match[0][3]

            //  Split position into start and end
            //
            Map loc = parsePos(pos)

            return  [
                    transcript: trs,
                    chr:        toChromosome(trs),
                    chrnum:     toChromosomeNum(trs),
                    hgvstype:   'g',
                    pos:        loc['pos'],
                    var:        'g.' + pos + rest,
                    mut:        rest,
                    bases:      rest.replaceAll(mutType(rest),''),      // bases inserted eg AGCT from g.12_34insAGCT
                    endpos:     loc['endpos'],
                    muttype:    mutType(rest)
                    ]
        }

        return [:]
    }

    /**
     * Parse a HGVSg variant
     *
     * @param var       HGVSc Variant to parse
     * @return          Map of parsed values (transcript, version, hgvstype, pos, endpos, ref, alt, muttype)
     */
    static Map parseHgvsC( String var )
    {
        var = var?.trim()?.replace( ' ','')     // remove embedded spaces

        //  Parse NM_000017.10:c.412A>G
        //
        def match = ( var =~ /(NM_\d+\.\d+):c\.([\d+\-_*]+)(.*)/ )
        if ( match.count == 1)
        {
            log.debug( "Parse HGVSC, in: " + var + " out " + match[0])
            String trs  = match[0][1]
            String pos  = match[0][2]
            String rest = match[0][3]

            //  Split position into start and end
            //
            Map loc = parsePos(pos)

            return  [
                    transcript: trs,
                    hgvstype:   hgvsType(var),
                    pos:        loc['pos'],
                    var:        'c.' + pos + rest,
                    mut:        rest,
                    endpos:     loc['endpos'],
                    muttype:    mutType(rest)
            ]
        }

        return [:]
    }
    /**
     * Parse a genomic position chr:start-end
     *
     * @param   inpos   HGVSg Variant to parse
     * @return          Map of parsed values (chr, pos, endpos)
     */
    static Map parseChrPos( String inpos )
    {
        //  Parse 10:182354-182355
        //
        def match = ( inpos =~ /(\S+):(\d+)-(\d+)/ )

        if ( match.count != 1) return [:]   // unrecognised format

        def chr = match[0][1]
        int pos = match[0][2]    as int
        def endpos = match[0][3] as int

        return  [ chr: chr, pos: pos, endpos: endpos ]
    }

    /**
     * Work out genomic offset in human readable form
     *
     * @param   hgvsgOld    Old HGVSg variant
     * @param   hgvsgNew    New HGVSg variant
     * @return              Offset in bp (genomic direction)
     */
    static Integer baseOffset( String hgvsgOld, String hgvsgNew )
    {
        if (hgvsgOld == hgvsgNew ) return 0

        Map o = parseHgvsG( hgvsgOld )
        Map n = parseHgvsG( hgvsgNew )

        if ( o.chr != n.chr ) return null
        if ( o.pos == n.pos && o.endpos == n.endpos ) return 0
        if ( ! o.pos || ! n.pos ) return 0

        //  Calculate offset in bases of start of variants
        //
        return (n.pos as Integer) - (o.pos as Integer)
    }

    /**
     * Normalise a HGVSc variant so they are consistent
     *
     * @param var       HGVSc Variant to normalise
     * @return          Cleaned up version for database joins
     */
    static String normaliseHgvsC( String var )
    {
        var = var.trim().replaceAll( ' ','')     // remove embedded spaces

        //  Parse SNP eg NM_000558.3:c.247_254C>A or NM_000059.1:c.*105A>C
        //
        def match = ( var =~ /NM_(\d+)\.(\d+):c\.([\d+\-_\*]+)([ATGC])>([ATGC])$/ )
        if ( match.count == 1)
        {
            def trs = match[0][1]
            int ver = match[0][2] as int
            def pos = match[0][3]
            def ref = match[0][4]
            def alt = match[0][5]

            return "NM_${trs}.${ver}:c.${pos}${ref}>${alt}"
        }

        //  Parse deletion NM_000033.3: c.977_983del[GGTATGT] or NM_000179.2:c.3647-53_3647-38del16
        //
        match = ( var =~ /NM_(\d+)\.(\d+):c\.([\d+\-_\*]+)del[ATGC0-9]*$/ )
        if ( match.count == 1)
        {
            def trs = match[0][1]
            int ver = match[0][2] as int
            def pos = match[0][3]
            pos = collapsePos(pos)

            return "NM_${trs}.${ver}:c.${pos}del"
        }


        //  Parse duplication eg NM_000214.2:c.1880dup[AAAA|NNN]
        //
        match = ( var =~ /NM_(\d+)\.(\d+):c\.([\d+\-_\*]+)dup[ATGC0-9]*$/ )
        if ( match.count == 1)
        {
            def trs = match[0][1]
            int ver = match[0][2] as int
            def pos = match[0][3]

            return "NM_${trs}.${ver}:c.${pos}dup"
        }

        //  Parse insertion eg NM_000551.3: c.165_166insAAAA
        //
        match = ( var =~ /NM_(\d+)\.(\d+):c\.([\d+\-_\*]+)ins([ATGC]+)$/ )
        if ( match.count == 1)
        {
            def trs = match[0][1]
            int ver = match[0][2] as int
            def pos = match[0][3]
            def ins = match[0][4]

            return "NM_${trs}.${ver}:c.${pos}ins${ins}"
        }

        //  Parse Complex var eg NM_000558.3:c.247_254del[CCCCC|NNN]insAAAAA
        //
        match = ( var =~ /NM_(\d+)\.(\d+):c\.([\d+\-_\*]+)del[ATGC0-9]*(ins[ATGC]+)$/ )
        if ( match.count == 1)
        {
            def trs = match[0][1]
            int ver = match[0][2] as int
            def pos = match[0][3] as String
            def ins = match[0][4]
            pos = collapsePos(pos)

            return "NM_${trs}.${ver}:c.${pos}del${ins}"
        }

        //  Log warning unless its a mitochondrial variant
        //
        if ( ! var.contains(':m.'))
            log.warn( "Couldn't parse variant: " + var )

        return null
    }

    /**
     * Collapse a position of the form '123_123' to '123'
     *
     * @param pos   Position string
     * @return      Collapsed position
     */
    static private String collapsePos( String pos )
    {
        //  Parse duplicated position (BIC violations)
        //
        Map pp = parsePos(pos)
        if ( pp['pos'] == pp['endpos'] ) return pp['pos']       // only need first position of redundant pair

        return pos
    }

    /**
     * Mutation type inferred from remaining HGVS string
     *
     * @param mtype     Mutation to parse
     * @return          Mutation type (del, ins, snp, dup, unknown)
     */
    private static String mutType( String mtype )
    {
        if ( mtype.startsWith('delins'))    return 'delins'
        if ( mtype.startsWith('del'))       return 'del'
        if ( mtype.startsWith('ins'))       return 'ins'
        if ( mtype.startsWith('dup'))       return 'dup'
        if ( mtype =~ /^[ATGC]>/ )          return 'snp'

        return 'unknown'
    }

    /**
     * Parse a HGVS position
     *
     * @param pos   String to parse
     * @return      Map of position locations (pos1, pos2)
     */
    private static Map parsePos( String pos )
    {
        //  Parse 123(_ or -)456
        //

        //def match = ( pos =~ /^(\d+)(-|_)(\d+)$/ )
        def match = ( pos =~ /^([0-9\+]+)(-|_)([0-9\+]+)$/ )
        def pos1 = pos
        def pos2 = pos
        if ( match.count == 1)
        {
            pos1 = match[0][1]
            pos2 = match[0][3]
        }

        return [ pos: pos1, endpos: pos2 ]
    }

    /**
     * Parse a refseq string as a chromosome
     *
     * @param   transcript of form NC_123456[.78] or chrXX
     * @return  chromosome string- one of 'chr1'-'chr22','chrX','chrY'
     */
    static String toChromosome( String transcript )
    {
        if ( transcript.startsWith('chr')) return transcript

        //  Parse  NC_123456[.78]
        //
        def match = ( transcript =~ /^NC_(\d{6})(\.\d+)?/ )

        if ( match.count != 1) return transcript

        String chrnum = match[0][1]

        //  numbered chromosome
        //
        if ( ! chrnum.isNumber()) return null

        def chr = (chrnum as int)
        if ( chr == 23 ) chr = 'X'     // X chromosome
        if ( chr == 24 ) chr = 'Y'     // Y chromosome

        return 'chr' + (chr as String)
    }

    /**
     * Parse a refseq string as a chromosome number
     *
     * @param   transcript of form NC_123456[.78] or chrXX
     * @return  chromosome number [1-24] where 23=X, 24=Y
     */
    static int toChromosomeNum( String transcript )
    {
        int chrnum = 0

        String chr = toChromosome( transcript )
        chr = chr.replaceAll('chr','')
        if ( chr.isNumber()) chrnum = chr as int
        if ( chr == 'X' ) chrnum = 23     // X chromosome
        if ( chr == 'Y' ) chrnum = 24     // Y chromosome
        assert chrnum > 0, "Invalid chromosome ${transcript}"

        return chrnum
    }

    /**
     * Convert a gene to it's preferred transcript using our modified HGNC sourced transcripts
     *
     * @param gene  HGNC gene name
     * @return      RefSeq preferred transcript for the gene
     */
    static String geneToTranscript( String gene, boolean version = false )
    {
        return geneToTx[gene]
    }

    /**
     * Convert a string with 3 letter AA codes to single letter
     * Uses GATK utility function
     *
     * @return  Revised string
     */
    static String toAA1(hgvsp)
    {
        if ( ! hgvsp )
            return ""

        String aa1 = hgvsp

        //  Find all 3 letter matches
        //
        def let3 = /([A-Z][a-z][a-z])/
        hgvsp.eachMatch(let3)
        {
            match ->
                def aa = aat.getAminoAcidByCode( match[0] )
                if ( aa )
                    aa1 = aa1.replaceFirst( match[0] as String, aa.letter )

                //  Check for STOP codons
                //
                if ( match[0] == 'Ter' )
                    aa1 = aa1.replaceFirst( 'Ter', '*' )
        }

        return aa1
    }

    /**
     * Return a transcript without a trailing version number
     *
     * @param ts    Transcript to strip version from
     * @return      Stripped transcript
     */
    static String transcriptNoVersion( String ts )
    {
        //  Parse NM_123456.78
        //
        def match = ( ts =~ /(NM_\d+)\.(\d+)$/ )
        if ( match.count == 1 )
        {
            return match[0][1]
        }

        return null
    }

    /**
     * Parse ensembl style variant Todo: support all types of HGVSg variants
     *
     * @param var   Ensembl variant eg 7_140453136_A/C (BRAF V600E)
     * @return      Mutalyser compliant HGVSg eg chr7:g.140453136A>C
     */
    static String ensToHgvsg( String var )
    {
        //  Parse <chr>_<pos>_<ref>/<alt>
        //
        def match = ( var =~ /(\d+|X|Y)_(\d+)_([ATGC]+|\-)\/([ATGC]+|\-)(\/[ATGC]+|\/\-)?$/ )
        if ( match.count != 1 )  return null

        def chr  = match[0][1] as String
        int pos  = match[0][2] as int
        def ref  = match[0][3] as String
        def alt  = match[0][4] as String
        def mul  = match[0][5] as String

        //  trailing multi-allele bases: Todo: support MA variants in VCF
        //
        if ( mul )
        {
            log.error( "Badly formed Ensembl variant: ${var}")
            return null
        }

        //  SNP id default
        //
        def hgvsg = "chr${chr}:g.${pos}${ref}>${alt}"

        //  deletion
        //
        if ( alt == '-' )
        {
            int end = pos+ref.length()-1
            if ( pos == end )
                hgvsg = "chr${chr}:g.${pos}del"
            else
                hgvsg = "chr${chr}:g.${pos}_${end}del"

            return normalise( hgvsg )
        }

        //  insertion
        //
        if ( ref == '-' )
        {
            hgvsg = "chr${chr}:g.${pos-1}_${pos}ins${alt}"

            return normalise( hgvsg )
        }

        //  delins
        //
        if ( ref.length() > 1 || alt.length() > 1 )
        {
            //  Strip off first base from ref & alt if they are the same
            //
            if ( ref[0] == alt[0] )
            {
                int spos = pos + 1
                int epos = pos + ref.length() - 1
                hgvsg = "chr${chr}:g.${spos}_${epos}delins${alt[1..-1]}"
            }
            else
            {
                int spos = pos
                int epos = pos + ref.length() - 1
                hgvsg = "chr${chr}:g.${spos}_${epos}delins${alt}"
            }
            log.info( "Complex indel: ${hgvsg}" )
        }

        return normalise( hgvsg )
    }

    /**
     * Parse ensembl style variant into a map Todo: support all types of HGVSg variants
     *
     * @param var   Ensembl variant eg 7_140453136_A/C (BRAF V600E)
     * @return      map with chr, pos, ref, alt, end eg chr7:g.140453136A>C
     */
    static Map ensToMap( String var )
    {
        //  Parse <chr>_<pos>_<ref>/<alt>
        //
        def match = ( var =~ /(\d+|X|Y)_(\d+)_([ATGC]+|\-)\/([ATGC]+|\-)$/ )
        if ( match.count != 1 )  return null

        def chr  = match[0][1] as String
        int pos  = match[0][2] as int
        def ref  = match[0][3] as String
        def alt  = match[0][4] as String

        //  SNP
        //
        def hgvsg = "chr${chr}:g.${pos}${ref}>${alt}"

        //  deletion
        //
        if ( alt == '-' )
        {
            int end = pos+ref.length()-1
        } else {
            int end = pos
        }

        def varmap = [:]
        varmap['chr'] = chr
        varmap['pos'] = pos
        varmap['ref'] = ref
        varmap['alt'] = alt
        return varmap
    }

    /**
     * Unpack and convert a VCF variant to a Map
     *
     * @param chr   Chromosome
     * @param pos   Position
     * @param ref   Reference
     * @param alt   Alternative
     *
     * @return      Map of converted variant [chr:, pos:, endpos:, ref:, alt:, ensvar: "chr_pos_ref/alt", hgvsg: annovar: ]
     */
    static Map normaliseVcfVar( String chr, String pos, String origRef, String origAlt )
    {
        if ( chr == null ) return [:]

        Map varmap = [:]
        String ref = origRef
        String alt = origAlt

        //  Strip off 'chr' prefix if any
        //
        varmap.chr = chr.replaceFirst('chr','')

        //  Expect integer for position
        //
        try
        {
            varmap.pos    = pos as Integer
            varmap.endpos = varmap.pos
        }
        catch ( Exception e )
        {
            log.error( "Position formatting error: value=${pos} ${e}")
            return [:]
        }

        if ( ! ref || ! alt ) return [:]

        //  Todo: support this construct from multi-allele variant callers
        //
        def comma = alt?.indexOf(',')
        if ( comma != -1 )
        {
            log.warn( "Ignoring subsequent alleles in ${origAlt}")
            alt = alt.substring(0,comma)
        }

        //  Oversize ref and alt, usually multi-allelic
        //  Strip off common trailing bases
        //
        while (ref.size() > 1 && alt.size() > 1)
        {
            //  remove any similar suffix from both ref and alt
            //
            if ( ref[-1] != alt[-1] ) break

            ref = ref[0..-2]
            alt = alt[0..-2]
        }

        //  SNP
        //
        varmap.ref = ref
        varmap.alt = alt

        //  Adjust for deletion
        //
        if ( ref.size() > alt.size() && ref.startsWith(alt))
        {
            varmap.pos += alt.size()                        // adjust position
            varmap.ref = ref.substring(alt.size())          // strip off first char matching alt
            varmap.alt = '-'
            varmap.endpos = varmap.pos + varmap.ref.length() - 1
        }

        //  Adjust for insertion
        //
        if ( alt.size() > ref.size() && alt.startsWith(ref))
        {
            varmap.pos += ref.size()                        // adjust position
            varmap.alt = alt.substring(ref.size())          // strip off first char matching ref
            varmap.ref = '-'
            varmap.endpos = varmap.pos
        }

        //  construct Ensembl variant
        //
        varmap.ensvar = "${varmap.chr}_${varmap.pos}_${varmap.ref}/${varmap.alt}"

        //  convert to HGVSg
        //
        varmap.hgvsg = ensToHgvsg( varmap.ensvar )

        //  construct Annovar variant line
        //
        varmap.annovar = mapToAnnovar( varmap )

        return varmap
    }

    /**
     * Format a variant as an Annovar file variant
     *
     * @param   var     Map of variant attributes
     * @return          Annovar formatted line
     */
    static String mapToAnnovar( Map var )
    {
        //  Deletions
        //
        if ( var.alt == '-' )
        {
            return [ var.chr, var.pos, var.endpos, var.ref, var.alt, var.hgvsg ].join('\t') + '\n'
        }

        // SNPs and insertions
        //
        if ( var.ref == '-' ) --var.pos;    // insertions adjustment

        return [ var.chr, var.pos, var.pos, var.ref, var.alt, var.hgvsg ].join('\t') + '\n'
    }
}
