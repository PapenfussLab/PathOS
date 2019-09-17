/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.pathos.loader

import groovy.sql.Sql
import groovy.util.logging.Log4j
import org.petermac.annotate.VarDataSource
import org.petermac.pathos.curate.*
import org.petermac.pathos.pipeline.HGVS
import org.petermac.util.Classify
import org.petermac.util.Tsv
import org.codehaus.groovy.runtime.StackTraceUtils

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Extracted from DBLoader to simplify adding SeqVariant records
 *
 * User: Ken Doig
 * Date: 22/02/2017
 */

@Log4j
class LoadSeqVariant
{
    //  Gene masking values
    //
    static HashSet<String>    filterGenes       = []
    static String         filterAssay       = 'noAssay'

    /**
     * Add the sequenced variants to GORM
     *
     * @param   tsvvcf  TSV file of all variants for loading - output of Vcf2Tsv.class
     * @param   adb     Annotation database
     * @param   usemyv  Use MyVariant annotation
     * @return          Count of SeqVariants added
     */
    static int addSeqVariant( File tsvvcf, String adb, String seqrunName, boolean usemyv = false )
    {
        int cnt = 0
        log.info('Adding Sequenced Variants - simplified')

        //  Annotation DataSource
        //
        VarDataSource vds = new VarDataSource(adb)

        //  Load variants as TSV file
        //
        Tsv tsv = new Tsv( tsvvcf )
        int nrows = tsv.load( true )
        log.info( "Loaded ${nrows} rows from ${tsvvcf}" )
        if ( ! nrows ) return 0

        //  Convert TSV to a List of Maps
        //
        List<Map> vcfmaps = tsv.rowMaps

        lastss = null   //  clear last seqsample before the loop - otherwise a reload of the same sample with another gene mask will break

        //  Loop through variants
        //
        for ( varmap in vcfmaps )
        {
            cnt += loadVariant( varmap, vds, usemyv )
        }

        return cnt
    }

    /**
     * Load a variant into Gorm DB
     *
     * @param var       Map of variant
     * @param sql       Sql instance for loading
     * @param vds       Annotation Data Source
     * @param usemyv    True if MyVariant annotations required
     * @return          No of records loaded
     */
    static int loadVariant( Map var, VarDataSource vds, boolean usemyv )
    {
        Map mut, myv, vep, svmap
        String variant = var.HGVSg

        log.debug( "Adding variant ${variant}" )

        //  Find the SeqSample for the variant
        //
        SeqSample seqSample = getSeqSample( var )
        if ( ! seqSample ) return 0

        //  Create a SV parameter Map
        //
        try
        {
            if ( ! usemyv )
            {
                //  Load cached annotations
                //
                mut = loadAnnotation( variant, 'MUT', vds )
                vep = loadAnnotation( variant, 'VEP', vds )

                if ( ! ( mut && vep )) return 0

                svmap = mapAnnotationVep( seqSample, var, mut, vep )
            }
            else
            {
                //  Load cached annotations
                //
                mut = loadAnnotation( variant, 'MUT', vds )
                myv = loadAnnotation( variant, 'MYV', vds )

                if ( ! ( mut && myv)) return 0

                svmap = mapAnnotationMyv( seqSample, var, mut, myv )
            }
        }
        catch( Exception e )
        {
            StackTraceUtils.sanitize(e).printStackTrace()
            log.error( "Exiting: Couldn't map annotation for ${variant} err=" + e.toString())
            return 0
        }

        //  Empty variant Map
        //
        if ( ! svmap ) return 0

        //  Mask variants by gene
        //
        if ( isMasked( svmap )) return 0

        return saveSeqVariant( svmap )
    }

    /**
     * Save a SeqVariant record
     *
     * @param svmap     Map of SeqVariant parameters
     * @return          1 if saved, 0 otherwise
     */
    static int saveSeqVariant( Map svmap )
    {
        log.debug( "Adding new var [${svmap.seqSample} ${svmap.hgvsg}] = ${SeqVariant.findBySeqSampleAndVariant( svmap.seqSample , svmap.hgvsg )}")

        //  Check if SeqVariant exists: look for specific SeqSample object and variant string
        //
        if ( SeqVariant.findBySeqSampleAndVariant( svmap.seqSample , svmap.hgvsg )) {
            log.info("Refusing to import ${svmap.hgvsg} seqsample ${svmap.seqSample} id ${svmap.seqSample.id} because it already exists")
            log.info(SeqVariant.findBySeqSampleAndVariant( svmap.seqSample, svmap.hgvsg ))
            return 0
        }

        //  Save record
        //
        def sv = new SeqVariant( svmap )

        //  Save the new SeqVariant instance
        //
        return DbLoader.saveRecord( sv, false ) ? 1 : 0
    }

    /**
     * Get annotation JSON from DB for variant
     *
     * @param variant       HGVSg variant
     * @param dataSource    DataSource to use
     * @param vds           VarDataSource object
     * @return
     */
    static Map loadAnnotation( String variant, String dataSource, VarDataSource vds)
    {
        //  Retrieve annotation parameters for variant
        //
        Map ano = vds.getValueMap( dataSource, variant )
        if ( ! ano )
        {
            log.error( "Missing ${dataSource} annotation for ${variant}")
            return null
        }

        return ano
    }

    /**
     * Provide a mapping from VCF attributes and annotations to SeqVariant domain class
     *
     * Expecting annotations from MyVariant and Mutalyzer only
     *
     * @param vcf   Map of VCF parameters
     * @param mut   Map of Mutalyzer parameters
     * @param myv   Map of MyVariant parameters
     * @return      Map of SeqVariant parameters
     */
    static Map mapAnnotationMyv( SeqSample seqSample, Map vcf, Map mut, Map myv )
    {
        // vcf =
        // [sample, seqrun, panel, CHROM, POS, ID, REF, ALT, QUAL, FILTER, ADP, WT, HET,
        // HOM, NC, GT, GQ, SDP, DP, RD, AD, FREQ, PVAL, RBQ, ABQ, RDF, RDR, ADF, ADR,
        // HGVSg, HGVSc, HGVSp, gene, lrg, status, muterr, numAmps, amps, ampbias, homopolymer, Identified]
        //
        // mut =
        // [gene, hgvsg, error, transcripts, lrg, hgvsc, refseq, hgvsp, variant, filterts]
        //

        Map svmap =     [
                        seqSample:          seqSample,
                        sampleName:         vcf.sample,
                        variant:            vcf.HGVSg,
                        ens_variant:        ensemblVar( vcf ),
                        gene:               vcf.gene,
                        filtered:           false,
                        filterFlag:         'nof',
                        reportable:         false,
                        consequence:        myv.cadd?.consequence ?: 'none found',
                        hgvsc:              vcf.HGVSc,
                        hgvsg:              vcf.HGVSg,
                        hgvsp:              vcf.HGVSp,
                        hgvspAa1:           HGVS.toAA1(vcf.HGVSp),
                        vepHgvsc:           null,
                        vepHgvsp:           null,
                        readDepth:          parseDepths(vcf).dp,
                        varDepth:           parseDepths(vcf).ad,
                        varFreq:            parseDepths(vcf).vaf,
                        fwdReadDepth:       parseDepths(vcf).rdf,
                        fwdVarDepth:        parseDepths(vcf).adf,
                        revReadDepth:       parseDepths(vcf).rdr,
                        revVarDepth:        parseDepths(vcf).adr,
                        chr:                vcf.CHROM,
                        pos:                vcf.POS,
                        exon:               myv.cadd?.exon ? 'ex' + myv.cadd?.exon : null,
                        cosmic:             myv.cosmic?.cosmic_id,
                        dbsnp:              myv.dbsnp?.rsid,
                        gmaf:               myv.cadd?.'1000g'?.af ? myv.cadd?.'1000g'?.af * 100.0 : 0.0,
                        ens_transcript:     null, //myv.dbnsfp?.ensembl?.transcriptid?.toString(),
                        ens_gene:           null, //myv.dbnsfp?.ensembl?.geneid?.toString(),
                        ens_protein:        null, //myv.dbnsfp?.ensembl?.proteinid?.toString(),
                        ens_canonical:      null,
                        refseq_mrna:        null,
                        refseq_peptide:     null,
                        existing_variation: null,
                        domains:            null,
                        genedesc:           null,
                        cytoband:           myv.clinvar?.cytogenic,
                        omim_ids:           myv.clinvar?.omim,
                        clin_sig:           myv.dbnsfp?.clinvar?.clinsig,
                        biotype:            myv.snpeff?.ann?.transcript_biotype,
                        pubmed:             myv.docm?.pubmed_id,
                        cadd:               myv.cadd?.rawscore ?: 0.0,
                        cadd_phred:         myv.cadd?.phred ?: 0.0,
                        exac:               myv.dbnsfp?.exac?.af ? myv.dbnsfp?.exac?.af * 100.0 : 0.0,
                        esp:                myv.dbnsfp?.esp6500?.ea_af ? myv.dbnsfp?.esp6500?.ea_af * 100.0 : 0.0,
                        cosmicOccurs:       '',
                        metaLrVal:          myv.dbnsfp?.metalr?.pred,
                        metaLrCat:          Classify.metaLr( myv.dbnsfp?.metalr?.pred ),
                        siftVal:            myv.dbnsfp?.sift?.pred,
                        siftCat:            Classify.sift( myv.dbnsfp?.sift?.pred ),
                        clinvarVal:         null,
                        clinvarCat:         null,
                        lrtVal:             myv.dbnsfp?.lrt?.pred,
                        lrtCat:             Classify.lrt( myv.dbnsfp?.lrt?.pred ),
                        mutTasteVal:        myv.dbnsfp?.mutationtaster?.pred,
                        mutTasteCat:        Classify.mutTaste( myv.dbnsfp?.mutationtaster?.pred ),
                        mutAssessVal:       myv.dbnsfp?.mutationassessor?.pred,
                        mutAssessCat:       Classify.mutAssess( myv.dbnsfp?.mutationassessor?.pred ),
                        fathmmVal:          myv.dbnsfp?.fathmm?.pred?.toString(),
                        fathmmCat:          Classify.fathmm( myv.dbnsfp?.fathmm?.pred?.toString() ),
                        metaSvmVal:         myv.dbnsfp?.metasvm?.pred,
                        metaSvmCat:         Classify.metaSvm( myv.dbnsfp?.metasvm?.pred ),
                        polyphenVal:        myv.dbnsfp?.polyphen2?.hvar?.pred,
                        polyphenCat:        Classify.polyphen( myv.dbnsfp?.polyphen2?.hvar?.pred ),
                        mutStatus:          vcf.status,
                        mutError:           vcf.muterr,
                        numamps:            vcf.numAmps,
                        amps:               vcf.amps,
                        ampbias:            vcf.ampbias,
                        homopolymer:        vcf.homopolymer,
                        varcaller:          vcf.Identified,
                        ]

        return svmap
    }

    /**
     * Provide a mapping from VCF attributes and annotations to SeqVariant domain class
     *
     * Expecting annotations from VEP and Mutalyzer only
     *
     * @param vcf   Map of VCF parameters
     * @param mut   Map of Mutalyzer parameters
     * @param vep   Map of VEP parameters
     * @return      Map of SeqVariant parameters
     */
    static Map mapAnnotationVep( SeqSample seqSample, Map vcf, Map mut, Map vep )
    {
        // vcf =
        // [sample, seqrun, panel, CHROM, POS, ID, REF, ALT, QUAL, FILTER, ADP, WT, HET,
        // HOM, NC, GT, GQ, SDP, DP, RD, AD, FREQ, PVAL, RBQ, ABQ, RDF, RDR, ADF, ADR,
        // HGVSg, HGVSc, HGVSp, gene, lrg, status, muterr, numAmps, amps, ampbias, homopolymer, Identified]
        //
        // vep =
        // {
        //        "besttx":
        //        {
        //            "gene_symbol":"KIT",
        //            "transcript_id":"NM_000222.2",
        //            "hgvsc":"NM_000222.2:c.1608G>A",
        //            "hgvsp":"NP_000213.1:p.Met536Ile",
        //            "gene_id":3815,
        //            "exon":"10/21",
        //            "codons":"atG/atA",
        //            "amino_acids":"M/I",
        //            "consequence_terms":["missense_variant"],
        //            "strand":1,
        //            "biotype":"protein_coding",
        //            "pick":1,
        //            "impact":"MODERATE",
        //            "protein_id":"NP_000213.1",
        //            "fathmm_pred":"T,T",
        //            "lrt_pred":"D",
        //            "mutationassessor_pred":"L",
        //            "sift_pred":"D,D",
        //            "metasvm_pred":"T",
        //            "mutationtaster_pred":"D,D",
        //            "metalr_pred":"T",
        //            "polyphen2_hvar_pred":"B,B",
        //            "variant_allele":"A",
        //            "sift_score":0.02,
        //            "sift_prediction":"deleterious",
        //            "polyphen_prediction":"benign",
        //            "polyphen_score":0.059,
        //            "allele_num":1
        //        },
        //        "id":"chr21:g.36259205T>C",
        //        "hgvsg":"chr21:g.36259205T>C",
        //        "start":55593451,
        //        "minimised":1,
        //        "assembly_name":"GRCh37",
        //        "strand":1,
        //        "allele_string":"G/A",
        //        "seq_region_name":"4",
        //        "most_severe_consequence":"missense_variant",
        //        "end":55593451,
        //        "variant_class":"SNV",
        //        "transcript_consequences":[
        //            {"consequence_terms":["missense_variant"],...},
        //            {"consequence_terms":["missense_variant"],...}
        //    ]
        //    }
        //
        // mut =
        // [gene, hgvsg, error, transcripts, lrg, hgvsc, refseq, hgvsp, variant, filterts]
        //

        Map svmap =     [
                        seqSample:          seqSample,
                        sampleName:         vcf.sample,
                        variant:            vcf.HGVSg,
                        ens_variant:        vep.id ?: '',
                        gene:               vep.besttx?.gene_symbol,
                        filtered:           false,
                        filterFlag:         null,
                        reportable:         false,
                        consequence:        vep.besttx?.consequence_terms?.join(',') ?: 'none found',
                        hgvsc:              vcf.HGVSc,
                        hgvsg:              vcf.HGVSg,
                        hgvsp:              vcf.HGVSp,
                        hgvspAa1:           HGVS.toAA1(vcf.HGVSp),
                        vepHgvsc:           vep.besttx?.hgvsc,
                        vepHgvsp:           vep.besttx?.hgvsp,
                        readDepth:          parseDepths(vcf).dp,
                        varDepth:           parseDepths(vcf).ad,
                        varFreq:            parseDepths(vcf).vaf,
                        fwdReadDepth:       parseDepths(vcf).rdf,
                        fwdVarDepth:        parseDepths(vcf).adf,
                        revReadDepth:       parseDepths(vcf).rdr,
                        revVarDepth:        parseDepths(vcf).adr,
                        chr:                vcf.CHROM,
                        pos:                vcf.POS,
                        exon:               vep.besttx?.exon ? 'ex' + vep.besttx?.exon : '',
                        cosmic:             getCosmic( vep.colocated_variants ),
                        dbsnp:              getDbsnp(  vep.colocated_variants ),
                        ens_transcript:     vep.besttx?.transcript_id,
                        ens_gene:           vep.besttx?.gene_symbol,
                        ens_protein:        vep.besttx?.hgvsp,
                        existing_variation: getColocated( vep.colocated_variants, "id" )?.join(','),
                        clin_sig:           getColocated( vep.colocated_variants, "clin_sig" )?.join(','),
                        pubmed:             getColocated( vep.colocated_variants, "pubmed" )?.join(','),
                        cadd:               vep.besttx?.cadd_raw,                                   // Double
                        cadd_phred:         vep.besttx?.cadd_phred,                                 // Double
                        exac:               getMaf( vep.colocated_variants, 'gnomad_maf' ),         // Double
                        gmaf:               getMaf( vep.colocated_variants, 'minor_allele_freq' ),  // Double
                        esp:                getMaf( vep.colocated_variants, 'ea_maf' ),             // Double
                        metaLrVal:          vep.besttx?.metalr_pred,
                        metaLrCat:          Classify.metaLr( vep.besttx?.metalr_pred ),
                        siftVal:            vep.besttx?.sift_pred,
                        siftCat:            Classify.sift((vep.besttx?.sift_pred as String)?.split(',') as List ),
                        lrtVal:             vep.besttx?.lrt_pred,
                        lrtCat:             Classify.lrt( vep.besttx?.lrt_pred ),
                        mutTasteVal:        vep.besttx?.mutationtaster_pred,
                        mutTasteCat:        Classify.mutTaste(( vep.besttx?.mutationtaster_pred as String)?.split(',') as List),
                        mutAssessVal:       vep.besttx?.mutationassessor_pred,
                        mutAssessCat:       Classify.mutAssess( vep.besttx?.mutationassessor_pred ),
                        fathmmVal:          vep.besttx?.fathmm_pred,
                        fathmmCat:          Classify.fathmm(( vep.besttx?.fathmm_pred as String)?.split(',') as List),
                        metaSvmVal:         vep.besttx?.metasvm_pred,
                        metaSvmCat:         Classify.metaSvm( vep.besttx?.metasvm_pred ),
                        polyphenVal:        vep.besttx?.polyphen2_hvar_pred,
                        polyphenCat:        Classify.polyphen( vep.besttx?.polyphen2_hvar_pred as List ),
                        mutStatus:          vcf.status,
                        mutError:           vcf.muterr,
                        numamps:            vcf.numAmps,
                        amps:               vcf.amps,
                        ampbias:            vcf.ampbias,
                        homopolymer:        vcf.homopolymer,
                        varcaller:          vcf.Identified,
                        ]

        log.debug( "Set SeqVariant Map=${svmap}\n\nVEP=${vep}")

        return svmap
    }

    /**
     * Test if variant gene is Masked for this panel
     *
     * @param   svmap   Variant Map
     * @return          True if variant is to be masked out of load
     */
    private static boolean isMasked( Map svmap )
    {
        //  Filter out genes not in PatAssay before loading
        //
        if ( filterGenes && ! (svmap.gene in filterGenes))
        {
            log.warn( "Filtered variant ${svmap.vepHgvsc} by gene ${svmap.gene} for PatAssay ${filterAssay} with genes ${filterGenes}")

            return true
        }

        return false
    }

    /**
     * Get 1000 genomes MAF
     *
     * @param   coloc   List of collocated variants
     * @return          GMAF as a %
     */
    private static Double getMaf( List<Map> colocs, String field )
    {
        List mafs = getColocated( colocs, field)

        if ( mafs.size() > 1 )
        {
            log.debug( "GMAF Multiple MAFs ${mafs}" )
        }
        if ( ! mafs )
        {
            return 0.0
        }

        def maf = mafs[0]

        //  GMAF style formatting [ATGC-]:0.123
        //
        if ( maf?.getClass() == String)
        {
            String dstring = maf

            //  Strip off <base>: prefix
            //
            if ( dstring?.contains(':'))
            {
                int i = dstring.lastIndexOf(':')
                if ( i < dstring.length()-1)
                    dstring = dstring[i+1..-1]
            }
            if ( dstring?.isDouble())
            {
                maf = Double.parseDouble( dstring )
            }
        }

        if ( maf.getClass() == BigDecimal || maf.getClass() == Double || maf.getClass() == Float )
        {
            return (maf * 100.0) as Double
        }

        return 0.0
    }

    /**
     * Convert VCF variant to ensembl format
     *
     * @param   vcf     Map of vcf fields
     * @return          Ensembl format eg
     */
    static String ensemblVar( Map vcf )
    {
        //  Normalise and convert VCF variant
        //
        Map var = HGVS.normaliseVcfVar( vcf.CHROM, vcf.POS, vcf.REF, vcf.ALT )

        return var.ensvar ?: ''
    }

    //  Last SeqSample found
    //
    static SeqSample lastss = null

    /**
     * Find SeqSample record for variant
     *
     * @param vcf   Map of VCF variant attributes
     * @return
     */
    static SeqSample getSeqSample( Map vcf )
    {
        if ( ! lastss || lastss.sampleName != vcf.sample )
        {
            //	Lookup Seqrun
            //
            Seqrun seqr = Seqrun.findBySeqrun( vcf.seqrun )
            if ( ! seqr )
            {
                log.warn("Couldn't find Seqrun [${vcf.seqrun}] Couldn't add SeqVariant [${vcf.HGVSg}:${vcf.HGVSc}]")
                return null
            }

            //	Lookup SeqSample
            //
            SeqSample ss = SeqSample.findBySeqrunAndSampleName( seqr, vcf.sample )
            if ( ! ss )
            {
                log.warn("Couldn't find SeqSample [${vcf.seqrun}:${vcf.sample}] Couldn't add SeqVariant [${vcf.HGVSg}:${vcf.HGVSc}]")
                return null
            }

            lastss = ss

            assignMask( ss )
        }

        return lastss
    }

    /**
     * Set the global static vars that hold filter gene mask here
     *
     * @param   runs    Seqsample to lookup PatSample, PatAssay and genes for
     */
    static void assignMask( SeqSample runs )
    {
        //  Reset filtering values with a change in SeqSample
        //
        filterGenes = []
        filterAssay = 'noAssay'

        //  Look at all assays for this sample (may be more than one)
        //
        SeqSample ss = SeqSample.findBySampleNameAndSeqrun(runs.sampleName, runs.seqrun, [fetch: [patSample: 'eager']]) //, panel: 'eager']])

        //  check if sample already has a custom gene mask set (do not assume we are loading into a new seqsample
        //
        if (ss.sampleGeneMask) {
            filterGenes = (filterGenes + ss.geneMask()).unique()    //  set gene mask from sample
            filterAssay = ss.geneMaskAssayName()   //  Save the assay name

        } else {     //  Sample does not have sampleGeneMask set, it's new - mask, check assay for mask

            //  If we have a Patient, find the PasAssays
            //
            if (ss.patSample) {
                //   grab the gene mask for this sample ( it may either it belongs to an Assay that needs filtering, or may have a custom mask)
                //
                List genes = ss.geneMask()

                //  Filtering only applies if we have an Assay that needs filtering
                //
                if (genes) {
                    //  Save the genes used by the assay - make a union of all genes
                    //
                    filterGenes = (filterGenes + genes).unique()
                    log.info("Gene mask ${filterGenes}")
                    filterAssay = ss.geneMaskAssayName()   //  Save the assay name
                }
            }

            ss.setSampleGeneMask(filterGenes.join(","))
            ss.save()
        }
    }

    /**
     * Parse allele depths
     *
     * @param   vcf     Map of VCF params
     * @return          Map of ad:<allele depth>, dp:<read depth>, vaf:<variant allele depth>
     */
    static Map parseDepths(Map vcf )
    {
        Double  vaf  = null
        Integer ad   = null
        Integer dp   = null
        Integer adf  = null
        Integer rdf  = null
        Integer adr  = null
        Integer rdr  = null

        //  Set variant frequency AD and DP, otherwise use VCF FREQ
        //
        try
        {
            //  Todo: parsing AD and DP should look at header meta data lines
            //
            ad = Integer.parseInt( stripAlleles( vcf.AD, 'A', ','))       // retrieve ALT allele depth
            dp = Integer.parseInt( stripAlleles( vcf.DP, 'R', ','))       // retrieve REF allele depth
            if ( dp != 0 )
                vaf = ad * 100.0 / dp
            else
            {
                //  VARSCAN style frequency with trailing %
                //
                if ( vcf.FREQ )
                {
                    String freq = vcf.FREQ.replaceAll('%','')
                    vaf = parseDouble( freq )
                }
            }
        }
        catch (Exception e)
        {
            StackTraceUtils.sanitize(e).printStackTrace()
            return [ vaf:vaf, ad:ad, dp:dp, adf:adf, rdf:rdf, adr:adr, rdr:rdr ]
        }

        //  Set strand depths
        //
        try
        {
            if( vcf.ADF ) adf = Integer.parseInt(vcf.ADF)
            if( vcf.RDF ) rdf = Integer.parseInt(vcf.RDF)
            if( vcf.ADR ) adr = Integer.parseInt(vcf.ADR)
            if( vcf.RDR ) rdr = Integer.parseInt(vcf.RDR)
        }
        catch (Exception e)
        {
            StackTraceUtils.sanitize(e).printStackTrace()
            return [ vaf:vaf, ad:ad, dp:dp, adf:adf, rdf:rdf, adr:adr, rdr:rdr ]
        }

        return [ vaf:vaf, ad:ad, dp:dp, adf:adf, rdf:rdf, adr:adr, rdr:rdr ]
    }

    /**
     * Parse a Double string possible with a [AGCT]: prefix
     *
     * @param   gmaf    Gmaf String
     * @return          Double GMAF
     */
    private static Double parseDouble( String dstring )
    {
        if ( ! dstring ) return 0.0

        //  Basic Double
        //
        if ( ! dstring?.isDouble())
        {
            log.warn( "Invalid double ${dstring}" )
            return 0.0
        }

        return Double.parseDouble(dstring)
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
        if ( ! val ) return '0'

        List l = val.tokenize( sep )

        if ( l.size() == 2 )
        {
            if ( type == 'R' ) return l[0]      // return reference allele depth
            if ( type == 'A' ) return l[1]      // return alternate allele depth
        }

        return( val.isInteger() ? val : '0' )
    }

    /**
     * Return first COSMnnnn Cosmic ID found in vars
     *
     * @param vars
     * @return
     */
    private static String getCosmic( List<Map> coloVars )
    {
        List cosmics = []
        for ( keys in getColocated( coloVars, "id" ) )
        {
            if ( keys =~ /COSM(\d+)/ )
            {
                cosmics << keys
            }
        }

        return cosmics.join(',')
    }

    /**
     * Return first rsnnnn dbSNP ID found in vars
     *
     * @param vars
     * @return
     */
    private static String getDbsnp( List<Map> coloVars )
    {
        List dbsnps = []
        for ( keys in getColocated( coloVars, "id" ) )
        {
            if ( keys =~ /rs(\d+)/ )
            {
                dbsnps << keys
            }
        }

        return dbsnps.join(',')
    }

    /**
     * Find all named elements of a List<Map> by slicing
     *
     * @param   coloVars   List<Map) to search
     * @param   key        key of property to find
     * @return             List of found elements
     */
    private static List getColocated( List<Map> coloVars, String key )
    {
        if ( ! coloVars ) return []

        //  return a "slice" of the coloVars List of Maps
        //
        List slice = coloVars."${key}"

        return slice.minus( null )  // remove any nulls in the List
    }
}
