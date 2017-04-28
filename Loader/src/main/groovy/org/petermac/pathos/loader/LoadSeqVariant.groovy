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
    /**
     * Add the sequenced variants to GORM
     *
     * @param   tsvvcf  TSV file of all variants for loading - output of Vcf2Tsv.class
     * @param   sql     Sql instance for loading
     * @param   adb     Annotation database
     * @return          Count of SeqVariants added
     */
    static int addSeqVariant( File tsvvcf, Sql sql, String adb )
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

        //  Loop through variants
        //
        for ( varmap in vcfmaps )
        {
            cnt += loadVariant( varmap, sql, vds )
        }

        return cnt
    }

    /**
     * Load a variant into Gorm DB
     *
     * @param var       Map of variant
     * @param sql       Sql instance for loading
     * @param vds       Annotation Data Source
     * @return          No of record loaded
     */
    static int loadVariant( Map var, Sql sql, VarDataSource vds )
    {
        String variant = var.HGVSg
        println variant

        //  Load annotations
        //
        Map mut = loadAnnotation( variant, 'MUT', vds )
        Map vep = loadAnnotation( variant, 'VEP', vds )
        Map anv = loadAnnotation( variant, 'ANV', vds )

        if ( ! anv || ! mut || ! vep ) return 0

        //  Create a SV parameter Map
        //
        Map svmap = mapAnnotation( var, mut, vep, anv )

        //  Save record
        //
        def sv = new SeqVariant( svmap )

        //  Save the new SeqVariant instance
        //
        return DbLoader.saveRecord( sv, false) ? 1 : 0
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
     * @param vcf   Map of VCF parameters
     * @param mut   Map of Mutalyzer parameters
     * @param vep   Map of VEP parameters
     * @param anv   Map of Annovar parameters
     * @return      Map of SeqVariant parameters
     */
    static Map mapAnnotation( Map vcf, Map mut, Map vep, Map anv )
    {
        // vcf =
        // [sample, seqrun, panel, CHROM, POS, ID, REF, ALT, QUAL, FILTER, ADP, WT, HET,
        // HOM, NC, GT, GQ, SDP, DP, RD, AD, FREQ, PVAL, RBQ, ABQ, RDF, RDR, ADF, ADR,
        // HGVSg, HGVSc, HGVSp, gene, lrg, status, muterr, numAmps, amps, ampbias, homopolymer, Identified]
        //
        // vep =
        // [EXON, RefSeq_mRNA, cDNA_position, SYMBOL, OMIM_count, HGVSp, ExAC_AF, Feature, pos, BIOTYPE,
        // Cytoband, CADD_RAW, Consequence, SIFT, PolyPhen, HGVSc, RefSeq_peptide, GeneName, DOMAINS, GMAF,
        // PUBMED, sample, Allele, Gene, seqrun, CANONICAL, CADD_PHRED, Feature_type, Protein_position, Amino_acids,
        // CLIN_SIG, hgvsg, CDS_position, Existing_variation, GeneDesc, COSMIC_count, Codons, CCDS, OMIM_ids, ENSP,
        // Uploaded_variation, chr]
        //
        // mut =
        // [gene, hgvsg, error, transcripts, lrg, hgvsc, refseq, hgvsp, variant, filterts]
        //
        // anv =
        // [GERP++_RS, esp6500si_all, End, CADD_phred, Alt, FATHMM_score, Start, 1000g2012apr_all,
        // Chr, clinvar_20140211, GeneDetail.refGene, MutationTaster_pred, Polyphen2_HDIV_score, Otherinfo,
        // SIFT_pred, CADD_raw, FATHMM_pred, MutationTaster_score, cosmic68, ExonicFunc.refGene, cg69,
        // MutationAssessor_score, AAChange.refGene, LRT_pred, snp138, Func.refGene, SIFT_score,
        // MutationAssessor_pred, SiPhy_29way_logOdds, LRT_score, Ref, phyloP100way_vertebrate, caddgt10,
        // Gene.refGene, VEST3_score, RadialSVM_score, RadialSVM_pred, LR_score, Polyphen2_HVAR_score, LR_pred,
        // phyloP46way_placental, Polyphen2_HDIV_pred, nci60, Polyphen2_HVAR_pred]

        Map svmap =     [
                        seqSample:          getSeqSample( vcf ),
                        sampleName:         vcf.sample,
                        variant:            vcf.HGVSg,
                        ens_variant:        vep.Uploaded_variation,
                        gene:               vcf.gene,
                        filtered:           false,
                        filterFlag:         'nof',
                        reportable:         false,
                        consequence:        vep.Consequence,
                        hgvsc:              vcf.HGVSc,
                        hgvsg:              vcf.HGVSg,
                        hgvsp:              vcf.HGVSp,
                        hgvspAa1:           HGVS.toAA1(vcf.HGVSp),
                        vepHgvsc:           vep.HGVSc,
                        vepHgvsp:           vep.HGVSp,
                        readDepth:          parseDepths(vcf).rd,
                        varDepth:           parseDepths(vcf).ad,
                        varFreq:            parseDepths(vcf).vaf,
                        fwdReadDepth:       parseDepths(vcf).rdf,
                        fwdVarDepth:        parseDepths(vcf).adf,
                        revReadDepth:       parseDepths(vcf).rdr,
                        revVarDepth:        parseDepths(vcf).adr,
                        chr:                vcf.CHROM,
                        pos:                vcf.POS,
                        exon:               vep.EXON ? 'ex' + vep.EXON : '',
                        cosmic:             getCosmic( vep.Existing_variation ),
                        dbsnp:              getDbsnp(  vep.Existing_variation ),
                        gmaf:               parseDouble(vep.GMAF),
                        ens_transcript:     vep.Feature,
                        ens_gene:           vep.Gene,
                        ens_protein:        vep.ENSP,
                        ens_canonical:      vep.CANONICAL,
                        refseq_mrna:        vep.RefSeq_mRNA,
                        refseq_peptide:     vep.RefSeq_peptide,
                        existing_variation: vep.Existing_variation,
                        domains:            vep.DOMAINS,
                        genedesc:           vep.GeneDesc,
                        cytoband:           vep.Cytoband,
                        omim_ids:           vep.OMIM_ids,
                        clin_sig:           vep.CLIN_SIG,
                        biotype:            vep.BIOTYPE,
                        pubmed:             vep.PUBMED,
                        cadd:               parseDouble(vep.CADD_RAW),
                        cadd_phred:         parseDouble(vep.CADD_PHRED),
                        exac:               parseDouble(vep.ExAC_AF) * 100.0,
                        esp:                parseDouble(anv.esp6500si_all),
                        cosmicOccurs:       anv.cosmic68,
                        metaLrVal:          anv.LR_pred,
                        metaLrCat:          Classify.metaLr( anv.LR_pred ),
                        siftVal:            anv.SIFT_pred,
                        siftCat:            Classify.sift( anv.SIFT_pred ),
                        clinvarVal:         anv.clinvar_20140211,
                        clinvarCat:         Classify.clinvar( anv.clinvar_20140211 ),
                        lrtVal:             anv.LRT_pred,
                        lrtCat:             Classify.lrt( anv.LRT_pred ),
                        mutTasteVal:        anv.MutationTaster_pred,
                        mutTasteCat:        Classify.mutTaste(  anv.MutationTaster_pred ),
                        mutAssessVal:       anv.MutationAssessor_pred,
                        mutAssessCat:       Classify.mutAssess( anv.MutationAssessor_pred ),
                        fathmmVal:          anv.FATHMM_pred,
                        fathmmCat:          Classify.fathmm( anv.FATHMM_pred ),
                        metaSvmVal:         anv.RadialSVM_pred,
                        metaSvmCat:         Classify.metaSvm( anv.RadialSVM_pred ),
                        polyphenVal:        anv.Polyphen2_HVAR_pred,
                        polyphenCat:        Classify.polyphen( anv.Polyphen2_HVAR_pred ),
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
     * Find SeqSample record for variant
     *
     * @param vcf
     * @return
     */
    static SeqSample getSeqSample( Map vcf )
    {
            //	Lookup Seqrun
            //
            def seqr = Seqrun.findBySeqrun( vcf.seqrun )
            if ( ! seqr )
            {
                log.warn("Couldn't find Seqrun [${vcf.seqrun}] Couldn't add SeqVariant [${vcf.HGVSg}:${vcf.HGVSc}]")
                return null
            }

            //	Lookup SeqSample
            //
            def runs = SeqSample.findBySeqrunAndSampleName( seqr, vcf.sample)
            if ( ! runs )
            {
                log.warn("Couldn't find SeqSample [${vcf.seqrun}:${vcf.sample}] Couldn't add SeqVariant [${vcf.HGVSg}:${vcf.HGVSc}]")
                return null
            }

        return runs
    }

    /**
     * Parse allele depths
     *
     * @param   vcf     Map of VCF params
     * @return          Map of ad:<allele depth>, rd:<read depth>, vaf:<variant allele depth>
     */
    static Map parseDepths(Map vcf )
    {
        Double  vaf  = null
        Integer ad   = null
        Integer rd   = null
        Integer adf  = null
        Integer rdf  = null
        Integer adr  = null
        Integer rdr  = null

        //  Set variant frequency AD and RD, otherwise use VCF FREQ
        //
        try
        {
            ad = Integer.parseInt(vcf.AD)
            rd = Integer.parseInt(vcf.RD)
            if ( rd != 0 )
                vaf = ad * 100.0 / rd
            else
            {
                //  VARSCAN style frequency with trailing %
                //
                if ( vcf.FREQ )
                {
                    String freq = vcf.FREQ.replaceAll('%','')
                    vaf = Double.parseDouble( freq )
                }
            }
        }
        catch (Exception e)
        {
            StackTraceUtils.sanitize(e).printStackTrace()
            return [ vaf:vaf, ad:ad, rd:rd, adf:adf, rdf:rdf, adr:adr, rdr:rdr ]
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
            return [ vaf:vaf, ad:ad, rd:rd, adf:adf, rdf:rdf, adr:adr, rdr:rdr ]
        }

        return [ vaf:vaf, ad:ad, rd:rd, adf:adf, rdf:rdf, adr:adr, rdr:rdr ]
    }

    /**
     * Parse a Double string possible with a [AGCT]: prefix
     *
     * @param   gmaf    Gmaf String
     * @return          Double GMAF
     */
    static Double parseDouble(String gmaf )
    {
        Double dgmaf = 0.0

        if ( gmaf )
        {
            try
            {
                if ( gmaf.contains(':'))
                {
                    int i = gmaf.lastIndexOf(':')
                    dgmaf = Double.parseDouble(gmaf.substring(i + 1)) * 100.0
                } else
                {
                    dgmaf = Double.parseDouble(gmaf)
                }
            }
            catch (Exception e)
            {
                StackTraceUtils.sanitize(e).printStackTrace()
                return dgmaf
            }
        }

        return dgmaf
    }

    /***********************************************************************************************************
     * Add the sequenced variants to GORM
     *
     * @param   sql     Sql instance for loading
     * @param   adb     Annotation database
     * @return          Count of SeqVariants added
     */
    static int addSeqVariants( Sql sql, String adb )
    {
        log.info( 'Adding Sequenced Variants')

        //  Annotation DataSource
        //
        VarDataSource vds = new VarDataSource( adb )

        int cnt    = 0              // number of rows added
        int rowcnt = 0              // row number

        //  Gene filtering values
        //
        List<String>   filterGenes       = []
        String         filterAssay       = 'noAssay'

        //  Count the total number of records to retrieve
        //
        def qry  = 'select count(*) as norows from mp_vcf'
        def rows = sql.rows(qry)
        int recs = rows[0].norows as int
        int page = 0
        int psiz = 100000          // chunk size in records
        log.info( "VCF variant rows : ${recs}")

        //  Chunk through the records in chunks of ${psiz} records
        //  Needed to avoid memory exhaustion
        //
        while ( page < recs )
        {
            //  Query RDB for all variants
            //  NOTE: totalreaddepth is VCF file DP
            //  NOTE: varreaddepth   is VCF file AD
            //  NOTE: freq           is VCF file FREQ
            //
            def qryVar = 	"""
                            select	vcf.ens_variant,
                                    vcf.seqrun,
                                    vcf.sample         as sampleName,
                                    vcf.hgvsg,
                                    vcf.hgvsc,
                                    vcf.hgvsp,
                                    vcf.totalreaddepth as readDepth,
                                    vcf.varreaddepth   as varDepth,
                                    vcf.freq           as vcfVaf,
                                    vcf.chr,
                                    vcf.pos,
                                    vcf.rdf            as fwdReadDepth,
                                    vcf.adf            as fwdVarDepth,
                                    vcf.rdr            as revReadDepth,
                                    vcf.adr            as revVarDepth,
                                    vcf.status         as mutStatus,
                                    vcf.muterr         as mutError,
                                    vcf.numamps,
                                    vcf.amps,
                                    vcf.ampbias,
                                    vcf.homopolymer,
                                    vcf.varcaller
                            from	mp_vcf as vcf
                            limit   ${page},${psiz}
                            """

            page += psiz
            rows = sql.rows( qryVar )
            log.info( "Variants retrieved: ${rows.size()}")
            SeqSample runs = null               //  Last SeqSample processed

            for ( row in rows )
            {
                ++rowcnt

                String variant = row.hgvsg
                if ( variant == '' )
                {
                    log.error( "Missing hgvsg variant at ${row.ens_variant}")
                    continue
                }

                //  Progress message
                //
                String msg = "Row: ${rowcnt} Processing SeqVariant [${row.seqrun}:${row.sampleName}:${variant}]"
                if ( rowcnt % 1000 == 0 ) log.info( msg )

                //  Skip lookups if the same sample name
                //
                if ( runs?.sampleName != row.sampleName )
                {
                    //	Lookup Seqrun
                    //
                    def seqr = Seqrun.findBySeqrun(row.seqrun)
                    if (!seqr)
                    {
                        log.warn("Row: ${rowcnt} Couldn't find Seqrun [${row.seqrun}] Couldn't add SeqVariant [${row.hgvsg}:${row.hgvsc}]")
                        continue
                    }

                    //	Lookup SeqSample
                    //
                    runs = SeqSample.findBySeqrunAndSampleName(seqr, row.sampleName)
                    if (!runs)
                    {
                        log.warn("Row: ${rowcnt} Couldn't find SeqSample [${row.seqrun}:${row.sampleName}] Couldn't add SeqVariant [${row.hgvsg}:${row.hgvsc}]")
                        continue
                    }

                    //  Reset filtering values with a change in SeqSample
                    //
                    filterGenes = []
                    filterAssay = 'noAssay'

                    //  Look at all assays for this sample (may be more than one)
                    //
                    SeqSample ss = SeqSample.findById(runs.id, [fetch: [patSample: 'eager']])

                    //  If we have a Patient, find the PasAssays
                    //
                    if ( ss.patSample )
                    {
                        //  Get the patient Sample (eagerly so we can get its patAssays
                        //
                        PatSample ps = PatSample.findById(ss.patSample.id, [fetch: [patAssays: 'eager']])

                        //  Get the patAssay names
                        //
                        List<String> patAssays = ps.patAssays.collect { it.testName }
                        log.info("Found ${patAssays} in ${ps}")

                        //  For each patAssay, find the genes in the assay
                        //
                        for ( pa in patAssays )
                        {
                            List genes = AmpliconRoiService.sampleTestGenes( pa )

                            //  Filtering only applies if we have an Assay that needs filtering
                            //
                            if (genes)
                            {
                                //  Save the genes used by the assay - make a union of all genes
                                //
                                log.debug( "filt ${filterGenes} genes ${genes}")
                                filterGenes = (filterGenes + genes).unique()
                                filterAssay = pa   //  Save the assay name
                            }
                        }
                    }
                }

                //  Check if SeqVariant exists: look for specific SeqSample object and variant string
                //
                if ( SeqVariant.findBySeqSampleAndVariant( runs, variant )) continue

                //  Retrieve VEP parameters for variant
                //
                Map vep = vds.getValueMap( 'VEP', variant )
                if ( ! vep )
                {
                    //  Try an alias if variant doesn't exists in VEP cache eg dups
                    //  Todo: get rid of this, getValueMap() looks up alias anyway
                    //
                    def alias = HGVS.ensToHgvsg( row.ens_variant )
                    vep = vds.getValueMap( 'VEP', alias )
                    if ( ! vep )
                    {
                        log.error( "Missing VEP annotation for ${variant}")
                        continue
                    }
                }

                //  Set VEP properties
                //
                row.gene                = vep.SYMBOL
                row.consequence         = vep.Consequence
                row.vepHgvsc			= vep.HGVSc
                row.vepHgvsp			= vep.HGVSp
                row.cosmic				= getCosmic( vep.Existing_variation )
                row.dbsnp				= getDbsnp(  vep.Existing_variation )
                row.exon				= vep.EXON ? 'ex' + vep.EXON : ''
                row.gmaf				= vep.GMAF
                if ( row.gmaf && row.gmaf.contains(':'))
                {
                    int i     = row.gmaf.lastIndexOf(':')
                    double gd = row.gmaf.substring(i+1) as double
                    row.gmaf  = gd * 100.0 as String
                }
                row.ens_transcript		= vep.Feature
                row.ens_gene			= vep.Gene
                row.ens_protein			= vep.ENSP
                row.ens_canonical		= vep.CANONICAL
                row.refseq_mrna			= vep.RefSeq_mRNA
                row.refseq_peptide		= vep.RefSeq_peptide
                row.existing_variation	= vep.Existing_variation
                row.domains				= vep.DOMAINS
                row.genedesc			= vep.GeneDesc
                row.cytoband			= vep.Cytoband
                row.omim_ids			= vep.OMIM_ids
                row.clin_sig			= vep.CLIN_SIG
                row.biotype				= vep.BIOTYPE
                row.pubmed				= vep.PUBMED
                row.cadd				= vep.CADD_RAW
                row.cadd_phred			= vep.CADD_PHRED
                row.exac				= vep.ExAC_AF

                //  Filter out genes not in PatAssay before loading
                //
                if ( filterGenes && ! (row.gene in filterGenes))
                {
                    log.warn( "Filtered variant ${row.vepHgvsc} by gene ${row.gene} for PatAssay ${filterAssay} with ${filterGenes.size()} genes")
                    continue
                }

                //  Add transcripts to VEP HGVS
                //
                if ( row.refseq_mrna && row.vepHgvsc )
                    row.vepHgvsc = row.refseq_mrna    + ':' + row.vepHgvsc  //  may have multiple eg nm1|nm2|nm3
                if ( row.refseq_peptide && row.vepHgvsp )
                    row.vepHgvsp = row.refseq_peptide + ':' + row.vepHgvsp  //  may have multiple eg np1|np2|np3

                //  Retrieve Annovar parameters for variant
                //
                Map anv = vds.getValueMap( 'ANV', variant )
                if ( ! anv )
                {
                    //  Try an alias if variant doesn't exists in Annovar cache eg dups
                    //
                    def alias = HGVS.ensToHgvsg( row.ens_variant )
                    anv = vds.getValueMap( 'ANV', alias )
                    if ( ! anv )
                    {
                        log.error( "Missing ANV annotation for ${variant}")
                        continue
                    }
                }

                //  Set Annovar parameters - insilico predictors
                //
                row.cosmicOccurs	=	anv.cosmic68
                def esp	            =	anv.esp6500si_all
                def clinvarVal	    =	anv.clinvar_20140211
                def siftCat	        =	anv.SIFT_pred
                def polyphenCat	    =	anv.Polyphen2_HVAR_pred
                def lrtCat	        =	anv.LRT_pred
                def mutTasteCat	    =	anv.MutationTaster_pred
                def mutAssessCat	=	anv.MutationAssessor_pred
                def fathmmCat	    =	anv.FATHMM_pred
                def metaSvmCat	    =	anv.RadialSVM_pred
                def metaLrCat	    =	anv.LR_pred

                //  Add additional properties to the SQL extracted ones
                //
                String clinvar = clinvarVal
                if ( clinvar?.length() > 250 ) clinvar = clinvar.substring(0,250)
                row <<  [
                        seqSample:      runs,
                        filtered:       false,
                        reportable:     false,
                        gmaf:           row.gmaf ? row.gmaf as Double : 0.0,
                        esp:            esp  ? (esp  as Double) * 100.0 : 0.0,
                        exac:           row.exac ? (row.exac as Double) * 100.0 : 0.0,
                        cadd:           row.cadd ? row.cadd as Double : null,
                        cadd_phred:     row.cadd_phred ? row.cadd_phred as Double : null,
                        clinvarVal:     clinvar,
                        clinvarCat:     Classify.clinvar( clinvar ),
                        lrtVal:         lrtCat,
                        lrtCat:         Classify.lrt(       lrtCat ),
                        mutTasteVal:    mutTasteCat,
                        mutTasteCat:    Classify.mutTaste(  mutTasteCat ),
                        mutAssessVal:   mutAssessCat,
                        mutAssessCat:   Classify.mutAssess( mutAssessCat ),
                        fathmmVal:      fathmmCat,
                        fathmmCat:      Classify.fathmm(    fathmmCat ),
                        metaSvmVal:     metaSvmCat,
                        metaSvmCat:     Classify.metaSvm(   metaSvmCat ),
                        metaLrVal:      metaLrCat,
                        metaLrCat:      Classify.metaLr(    metaLrCat ),
                        siftVal:        siftCat,
                        siftCat:        Classify.sift(      siftCat ),
                        polyphenVal:    polyphenCat,
                        polyphenCat:    Classify.polyphen(  polyphenCat ),
                        variant:        variant,
                        hgvsg:          variant
                ]

                //  Set variant frequency from varDepth and readDepth if they exist, otherwise use VCF VAF
                //
                if ( row.varDepth && row.readDepth )
                    row.varFreq = row.varDepth * 100 / row.readDepth
                else
                    row.varFreq = row.vcfVaf

                //  Validate gene and transcript  added kdd 03-aug-15
                //
                String transcript = HGVS.geneToTranscript( row.gene )
                String hgvsc      = row.hgvsc
                if ( ! transcript || ! hgvsc.startsWith( transcript ))
                {
                    log.error( "Transcript mismatch for gene=${row.gene} preferred=${transcript} HGVSc=${hgvsc} HGVSg=${row.hgvsg}")
                    continue
                }

                //  Set link to CurVariant table if this variant has been curated
                //  Todo: find curVariant by hgvsg and mutContext as well
                //
                //row.curated = CurVariant.findByHgvsg( row.hgvsg )

                //  Add 1 letter AA format
                //
                if ( row.hgvsp ) row.hgvspAa1 = HGVS.toAA1(row.hgvsp)       // add 1 letter AA HGVSp variant

                //  Remove extra parameters
                //
                row.remove( 'seqrun' )
                row.remove( 'vcfVaf' )

                //  Create new SeqVariant and bind properties via enriched map of properties from SQL
                //
                def sv = new SeqVariant( row as Map )

                //  Save the new SeqVariant instance
                //
                if ( ! DbLoader.saveRecord( sv, cnt % 2000 == 0)) continue

                ++cnt
            }
        }

        return cnt
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
}
