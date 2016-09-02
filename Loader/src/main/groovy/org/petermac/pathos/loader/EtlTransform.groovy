/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.pathos.loader

import groovy.util.logging.Log4j
import org.petermac.pathos.pipeline.HGVS
import org.petermac.util.RunCommand

/**
 * Perform all complex data transforms in ETL framework
 *
 * Called from SeqETL.groovy ETL config file under control of LoadPathOS
 *
 * User: Ken Doig
 * Date: 21-Aug-14  Added version to transcripts
 */

@Log4j
class EtlTransform
{
    static HGVS hg

    /**
     * Constructor takes RDB for HGVS from ETL config file
     *
     * @param rdb   RDB to find HGVS initilisation table
     */
    EtlTransform( String rdb )
    {
        hg = new HGVS( rdb )
    }

    /**
     * Filter a TSV file line by line
     * Uses the '#' comment line to convert fields into a named map for processing
     *
     * @param inf           File of input TSV file
     * @param outf          File of filtered output file
     * @param filterRow     Closure to be used for filtering each line. Closure input is a Map of fields
     * @return              Number of lines processed from inf
     */
    private static int fileFilter( File inf, File outf, Closure filterRow )
    {
        def header = []
        def nlines = 0

        //  Delete output file first
        //
        outf.delete()

        if ( ! inf.exists())
        {
            log.warn( "No file to transform [${inf.name}]")
            return 0
        }

        //  Process each tab separated line
        //
        inf.eachLine
        {
            line ->

                ++nlines
                if ( line =~ '^#' )
                {
                    line = line.replaceFirst('#','')
                    header = line.split("\t")
                }
                else
                {
                    //  add a sentinel space to preserve trailing null TSV fields
                    //  This is a bug in the way Groovy splits Strings
                    //
                    def fields = (line + ' ' ).split("\t")

                    //  Remove sentinel space in last field
                    //
                    fields[fields.size()-1] = fields[fields.size()-1].replaceAll( / $/ , '')

                    //  Check all fields are there
                    //
                    if ( header.size() != fields.size())
                    {
                        log.error("File ${inf} Line ${nlines} header size (${header.size()}) mismatches data fields (${fields.size()})")
                    }
                    else
                    {
                        //  Convert row to map
                        //
                        def fldmap = [ header, fields ].transpose().collectEntries { it }

                        //  Filter row based on table
                        //
                        fldmap = filterRow( fldmap )

                        //  Output row to output file if we have data
                        //
                        if ( fldmap ) outf << fldmap.values().join("\t") + "\n"
                    }
                }
        }

        return nlines
    }

    /**
     * TSV filtering method for mp_vcf table
     *
     * @param inFile    Input TSV File
     * @param outFile   Output TSV File
     * @param anvFile   Output Annovar File
     */
    static void mp_vcf( File inFile, File outFile, File anvFile )
    {
        def vcfFilterRow =
            {
                row ->

                    //  Remove multiple alleles
                    //
                    if ( row['AD'])
                    {
                        //log.info( "### ${row['AD']} ${stripAlleles( row['AD'] as String, 'R', ',' )}")
                        row['AD'] = stripAlleles( row['AD'] as String, 'R', ',' )
                    }

                    //  Normalise and convert VCF variant
                    //
                    Map var = HGVS.normaliseVcfVar( row['CHROM'] as String, row['POS'] as String, row['REF'] as String, row['ALT'] as String )

                    if ( ! var )
                    {
                        log.error( "Failed to convert VCF variant [${row['CHROM']+','+row['POS']+','+row['REF']+','+row['ALT']}]")
                        return null
                    }

                    //  add additional record properties - Ensembl style variant
                    //
                    row << [ 'ens_variant' : var.ensvar ]                  // add Ensembl variant

                    return row
            }

        log.info( "Transforming from ${inFile}")
        def nlines = fileFilter( inFile, outFile, vcfFilterRow )
        log.info( "Transformed ${nlines} lines from ${inFile} into ${outFile}")
    }

    /**
     * Find first value for multiple alleles
     *
     * @param   val     Possible token separated value
     * @param   type    VCF fields type 'R'=ref,alt1,alt2... and 'A'=alt1,alt2...
     * @return          Second element if 'R' type
     */
    static String stripAlleles( String val, String type, String sep )
    {
        if ( ! val ) return val

        List l = val.tokenize( sep )

        if ( l.size() < 2 ) return val
        if ( type == 'A' ) return l[0]      // multiple allele field
        if ( type == 'R' ) return l[1]      // ref then multiple allele field

        return l[0]
    }

    /**
     * TSV filtering method for mp_vep table
     *
     * @param inFile    Input TSV File
     * @param outFile   Output TSV File
     */
    static void mp_vep( File inFile, File outFile )
    {
        def missing  = [:]       // Map of genes missing transcript
//        List mutVars = []        // List of variants to validate

        def vepFilterRow =
            {
                row ->

                    //  prefix exon to prevent MS Excel date like atrocities
                    //
                    if ( row['EXON']) row['EXON'] = 'ex' + row['EXON']

                    //  Remove base from GMAF eg "G:0.0256"
                    //
                    String gmaf = row['GMAF']
                    if (  gmaf && (gmaf.contains(':')))
                    {
                        int i = gmaf.lastIndexOf(':')
                        double gd = gmaf.substring(i+1) as double
                        row['GMAF'] = gd * 100.0 as String
                    }
                    else
                        row['GMAF'] = '0'

                    //  List of Ensembl derived Refseq transcript
                    //  May be multiple refseq transcripts separated by '|' Todo: support multiple transcripts
                    //
                    List<String> refseqs = getRefseqList( row['RefSeq_mRNA'] as String )

                    String hgvsc = row[ 'HGVSc' ]
                    if ( ! hgvsc?.startsWith('c.')) return null

                    String gene = row[ 'SYMBOL' ]
                    def  genets = hg.geneToTranscript( gene )

                    //  No transcripts for this "gene" -  keep a running count for logging
                    //
                    if ( ! genets || ! refseqs)
                    {
                        log.debug( "Missing transcripts for ${gene} genets: ${genets} refseq: ${refseqs}")
                        missing[gene] = missing[gene] ? missing[gene]+1 : 1
                        return null
                    }

                    //  Find a transcript matching the preferred gene transcript
                    //
                    def ts = refseqs.find { it.startsWith(genets)}
                    if ( ! ts )
                    {
                        log.debug( "Transcript mismatch for ${gene} genets: ${genets} refseq: ${refseqs}")
                        missing[gene] = missing[gene] ? missing[gene]+1 : 1
                        return null
                    }

                    def var = ts + ':' + hgvsc
                    var = HGVS.normalise(var)

                    //  At present we are only interested in refseq transcripts - ignore the rest
                    //  Todo: can't ignore non refseq transcripts for research mode
                    //
                    if ( ! var?.startsWith('NM_')) return null

                    //  Add additional parameters to record
                    //
                    row << [ 'variant'    : var ]                                   // add normalised variant (HGVSc)
                    row << [ 'refseq'     : ts ]                                    // refseq transcript if available
                    row << [ 'cosmic'     : getCosmic(row['Existing_variation']) ]  // add first cosmic id
                    row << [ 'dbsnp'      : getDbsnp( row['Existing_variation']) ]  // add first dbsnp id

                    //  Convert ens to HGVSg variant
                    //
                    String ens_variant = row['Uploaded_variation']
                    row << [ 'hgvsg'      : HGVS.ensToHgvsg(ens_variant) ]          // HGVSg variant

                    return row
            }

        log.info( "Transforming from ${inFile}")

        def nlines = fileFilter( inFile, outFile, vepFilterRow )

        //  Warn of missing transcript genes and their counts
        //
        if ( missing )
            log.warn( "Genes missing a transcript : ${missing}")

        log.info( "Transformed ${nlines} lines from ${inFile} into ${outFile}")
    }

    /**
     * Return a list of refseq transcripts
     *
     * @param refseq
     * @return
     */
    private static List getRefseqList( String refseq )
    {
        if ( ! refseq ) return []
        return refseq.trim().split(/\|/)
    }

    /**
     * Return first COSMnnnn Cosmic ID found in vars
     *
     * @param vars
     * @return
     */
    private static String getCosmic( Object vars )
    {
        if ( ! vars ) return ''

        def m = ( vars as String =~ /COSM(\d+)/ )
        if ( m.count ) return m[0][1]
        return ''
    }

    /**
     * Return first rsnnnn dbSNP ID found in vars
     *
     * @param vars
     * @return
     */
    private static String getDbsnp( Object vars )
    {
        if ( ! vars ) return ''

        def m = ( vars as String =~ /rs(\d+)/ )
        if ( m.count ) return m[0][1]
        return ''
    }

    /**
     * Parse insilico prediction return of form 'class(n.nn)'
     * Todo: get rid of this and use the util.Classify class
     *
     * @param vars
     * @param value
     * @return
     */
    private static String parseInSilico( Object vars, boolean value )
    {
        if ( ! vars ) return ''

        def m = ( vars as String =~ /(\w+)\(([\d\.]+)\)/ )
        if ( m.count == 1 )
        {
            return value ? m[0][2] : m[0][1]
        }
        return ''
    }


    /**
     * TSV filtering method for mp_mutdesc table  Todo: deprecate this
     *
     * @param inFile    Input TSV File
     * @param outFile   Output TSV File
     */
    static void mp_mutdesc( File inFile, File outFile )
    {
        def mutdescFilterRow =
            {
                row ->
//                    def gene = row['gene'] as String
//                    def var = hg.geneToTranscript( gene, true ) + ':' + row[ 'hgvsc' ]
//                    row << [ 'variant' : HGVS.normalise(var) ]         // add normalised variant

                    return row
            }

        log.info( "Transforming from ${inFile}")
        def nlines = fileFilter( inFile, outFile, mutdescFilterRow )
        log.info( "Transformed ${nlines} lines from ${inFile} into ${outFile}")
    }

    /**
     * TSV filtering method for ref_kconfab table
     *
     * @param inFile    Input TSV File
     * @param outFile   Output TSV File
     */
    static void ref_kconfab( File inFile, File outFile )
    {
        def kconfabFilterRow =
            {
                row ->

                String var   = ''
                String hgvsc = ''
                String hgvsg = ''
                String gene  = ''

                //  Parse the variant field and extract gene and variant
                //
                def fld = row[ 'hgvs' ]
                if ( fld?.contains( 'c.') )
                {
                    fld = fld.replaceAll( ',', '')
                    def match = ( fld =~ /(ATM|BRCA1|BRCA2|CHEK2|P53|PTEN|MSH2) ?(c\.[^\(]+)/ )
                    if ( match.count == 1)
                    {
                        gene  = match[0][1]
                        if ( gene == 'P53' ) gene = 'TP53'
                        hgvsc = match[0][2]

                        var = hg.geneToTranscript(gene) + ':' + hgvsc
                        var = hg.normalise(var)
                    }
                    else
                        log.warn( "Couldn't parse variant " + fld)
                }
                row << [ 'gene'    : gene ]        // add gene of variant
                row << [ 'hgvsc'   : hgvsc ]       // add HGVSc variant
                row << [ 'variant' : var ]         // add normalised variant
                row << [ 'hgvsg'   : hgvsg ]       // add genomic variant

                return row
            }

        log.info( "Transforming from ${inFile}")
        def nlines = fileFilter( inFile, outFile, kconfabFilterRow )
        log.info( "Transformed ${nlines} lines from ${inFile} into ${outFile}")
    }

    /**
     * TSV filtering method for ref_bic table
     *
     * @param inFile    Input TSV File
     * @param outFile   Output TSV File
     */
    static ref_bic( File inFile, File outFile )
    {
        def bicFilterRow =
            {
                row ->

                String var   = ''
                String gene  = row['gene']
                String hgvsg = ''
                String hgvsc = row[ 'hgvsc' ]
                hgvsc = hgvsc.replace( ';', '' )

                if ( hgvsc?.contains( 'c.') )
                {
                    var = hg.geneToTranscript( gene ) + ':' + hgvsc     // Todo: add version to transcript
                    var = HGVS.normalise(var)
                }
                row << [ 'variant' : var ]         // add normalised variant
                row << [ 'hgvsg'   : hgvsg ]       // add genomic variant

                return row
            }

        log.info( "Transforming from ${inFile}")
        def nlines = fileFilter( inFile, outFile, bicFilterRow )
        log.info( "Transformed ${nlines} lines from ${inFile} into ${outFile}")
    }

    /**
     * TSV filtering method for ref_hgmd table - also used for ref_hgmdimputed
     *
     * @param inFile    Input TSV File
     * @param outFile   Output TSV File
     */
    static void ref_hgmd( File inFile, File outFile )
    {
        def hgmdFilterRow =
            {
                row ->
                String hgvs = row['hgvs']
                def var   = ''
                def hgvsc = ''
                def hgvsp = ''

                if ( ! (hgvs =~ 'N/A'))
                {
                    def vars = hgvs.split('%3B ')       // split hgvsc and hgvsp
                    assert vars.size() == 1 || vars.size() == 2
                    var   = HGVS.normalise(vars[0])
                    hgvsc = vars[0]
                    if ( vars.size() == 2 ) hgvsp = vars[1]
                }

                //  Add extra fields for HGMD
                //
                row << [ 'variant' : var ]         // add normalised variant
                row << [ 'hgvsc'   : hgvsc ]       // add HGVSc variant
                row << [ 'hgvsp'   : hgvsp ]       // add HGVSp variant

                return row
            }

        log.info( "Transforming from ${inFile}")
        def nlines = fileFilter( inFile, outFile, hgmdFilterRow )
        log.info( "Transformed ${nlines} lines from ${inFile} into ${outFile}")
    }

    /**
     * TSV filtering method for ref_clinvar table
     *
     * @param inFile    Input TSV File
     * @param outFile   Output TSV File
     */
    static void ref_clinvar( File inFile, File outFile )
    {
        def clinvarFilterRow =
            {
                row ->
                String hgvs = row['feature']
                def var   = ''
                def hgvsc = ''

                if ( hgvs =~ /^NM_/ )
                {
                    def vars = hgvs.split(':')       // split feature
                    assert vars.size() >= 2
                    hgvsc = vars[0] + ':' + vars[1]
                    var   = HGVS.normalise(hgvsc)
                }

                //  Add extra fields for HGMD
                //
                row << [ 'variant' : var ]         // add normalised variant
                row << [ 'hgvsc'   : hgvsc ]       // add HGVSc variant

                return row
            }

        log.info( "Transforming from ${inFile}")
        def nlines = fileFilter( inFile, outFile, clinvarFilterRow )
        log.info( "Transformed ${nlines} lines from ${inFile} into ${outFile}")
    }

    /**
     * TSV filtering method for ref_emory table
     *
     * @param inFile    Input TSV File
     * @param outFile   Output TSV File
     */
    static void ref_emory( File inFile, File outFile )
    {
        def emoryFilterRow =
            {
                row ->
                String var = row['egl_variant']
                row << [ 'variant' : HGVS.normalise(var) ]       // add normalised variant
                return row
            }

        log.info( "Transforming from ${inFile}")
        def nlines = fileFilter( inFile, outFile, emoryFilterRow )
        log.info( "Transformed ${nlines} lines from ${inFile} into ${outFile}")
    }

    /**
     * TSV filtering method for ref_iarc table
     *
     * @param inFile    Input TSV File
     * @param outFile   Output TSV File
     */
    static void ref_iarc( File inFile, File outFile )
    {
        def iarcFilterRow =
            {
                row ->
//                def transcript = hg.geneToTranscript('TP53')
                def transcript = 'NM_000546.5'
                def var = transcript + ':' + row['c_description']  // Todo: need to add version to transcript
                row << [ 'variant'    : HGVS.normalise(var) ]      // add normalised variant
                row << [ 'gene'       : 'TP53' ]                   // add normalised variant
                row << [ 'transcript' : transcript ]
                return row
            }

        log.info( "Transforming from ${inFile}")
        def nlines = fileFilter( inFile, outFile, iarcFilterRow )
        log.info( "Transformed ${nlines} lines from ${inFile} into ${outFile}")
    }
}
