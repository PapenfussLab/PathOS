/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class SeqVariant implements Taggable
{
    String		variant
    String		ens_variant
    String		gene
    String		consequence
    String	    hgvsc
    String	    hgvsp
    String	    hgvspAa1
    String	    hgvsg
    SeqSample	seqSample
    String      sampleName      // convenience property of seqSample.toString()
    Integer		readDepth
    Integer		varDepth
    Float		varFreq
    Integer		fwdReadDepth
    Integer		fwdVarDepth
    Integer		revReadDepth
    Integer		revVarDepth
    String      cosmic
    String      dbsnp
    String      chr
    String      pos
    String      exon
    String	    alamutClass
    Boolean     filtered
    String      filterFlag
    Boolean     reportable
    Double      varPanelPct
    Integer         varSamplesSeenInPanel  //varSeenInPanel is how often that variant has been seen in a panel
    Integer         varSamplesTotalInPanel //varTotalInpanel is how many vars there are in that var's panel. that is, panel freq is: varSeenInPanel / varTotalInPanel
    Double      gmaf
    String		ens_transcript
    String		ens_gene
    String		ens_protein
    String		ens_canonical
    String		refseq_mrna
    String		refseq_peptide
    String		existing_variation
    String		domains
    String		genedesc
    String		cytoband
    String		omim_ids
    String		clin_sig
    String		biotype
    String		pubmed
    Double      esp
    String      cosmicOccurs
    Double      cadd
    Double      cadd_phred
    Double      exac
    String      clinvarCat
    String      lrtCat
    String      mutTasteCat
    String      mutAssessCat
    String      fathmmCat
    String      metaSvmCat
    String      metaLrCat
    String      siftCat
    String      polyphenCat
    String      clinvarVal
    String      lrtVal
    String      mutTasteVal
    String      mutAssessVal
    String      fathmmVal
    String      metaSvmVal
    String      metaLrVal
    String      siftVal
    String      polyphenVal
    String      vepHgvsg
    String      vepHgvsc
    String      vepHgvsp
    String      mutStatus
    String      mutError
    String      numamps
    String      amps
    String      ampbias
    String      homopolymer
    String      varcaller

    Integer     maxPmClass
    Integer     acmgSort
    Integer     ampSort
    Integer     overallSort

    static constraints =
        {
            variant(     maxSize: 500 )
            ens_variant( maxSize: 500 )
            gene()
            filtered()
            filterFlag( nullable: true )
            reportable()
            consequence()
            hgvsc( maxSize: 500 )
            hgvsg( maxSize: 500, nullable: true)
            hgvsp( maxSize: 500, nullable: true )
            vepHgvsg( maxSize: 500, nullable: true )
            vepHgvsc( maxSize: 500, nullable: true )
            vepHgvsp( maxSize: 500, nullable: true )
            hgvspAa1( maxSize: 500, nullable: true )
            sampleName()
            readDepth()
            varDepth()
            varFreq( nullable: true )
            fwdReadDepth( nullable: true )
            fwdVarDepth( nullable: true )
            revReadDepth( nullable: true )
            revVarDepth( nullable: true)
            chr( nullable: true)
            pos( nullable: true)
            exon( nullable: true)
            cosmic( nullable: true)
            dbsnp( nullable: true)
            alamutClass( nullable: true )
            gmaf( nullable: true )
            varPanelPct( nullable: true )
            ens_transcript( nullable: true)
            ens_gene( nullable: true)
            ens_protein( nullable: true)
            ens_canonical( nullable: true)
            refseq_mrna( nullable: true)
            refseq_peptide( nullable: true)
            existing_variation( nullable: true)
            domains( nullable: true)
            genedesc( nullable: true)
            cytoband( nullable: true)
            omim_ids( nullable: true)
            clin_sig( nullable: true)
            biotype( nullable: true)
            pubmed( nullable: true)
            esp( nullable: true)
            cosmicOccurs( nullable: true )
            cadd( nullable: true )
            cadd_phred( nullable: true )
            exac( nullable: true )
            clinvarCat( nullable: true )
            lrtCat( nullable: true )
            mutTasteCat( nullable: true )
            mutAssessCat( nullable: true )
            fathmmCat( nullable: true )
            metaSvmCat( nullable: true )
            metaLrCat( nullable: true )
            siftCat( nullable: true )
            polyphenCat( nullable: true )
            clinvarVal( nullable: true )
            lrtVal( nullable: true )
            mutTasteVal( nullable: true )
            mutAssessVal( nullable: true )
            fathmmVal( nullable: true )
            metaSvmVal( nullable: true )
            metaLrVal( nullable: true )
            siftVal( nullable: true )
            polyphenVal( nullable: true )
            mutStatus( maxSize: 500, nullable: true )
            mutError(  maxSize: 500, nullable: true )
            numamps( nullable: true )
            amps( nullable: true )
            ampbias( nullable: true )
            homopolymer( nullable: true )
            varcaller( nullable: true )
            varSamplesSeenInPanel( nullable: true)
            varSamplesTotalInPanel( nullable: true)
            maxPmClass( nullable: true)
            acmgSort (  nullable: true )
            ampSort (   nullable: true )
            overallSort ( nullable: true )
        }

    static hasMany = [ tags: Tag ] //,  curVariants: CurVariant ] //, varLinks: VarLink ]


    //  the mappedBy resolves the problem of multiple many-to-many and one-to-one relationships between curvar and seqvar
    //  there's a matching line in curvar. this specifies that the manytomany is between the below vars only.
    //  otherwise CurVar curated and SeqVar originating start getting messed up
    //  static mappedBy = [curVariant: 'originating]


    //  Indexes on seqSample,sampleName,variant,hgvsg
    //
    static      mapping =
    {
        //seqSample   index: 'seq_sample_idx' //, unique: true
        variant     index: 'variant_idx'
        sampleName  index: 'sample_name_idx'
        hgvsg       index: 'hgvsg_idx' //, unique: true
        hgvsc       index: 'hgvsc_idx'

        //  calculate max pmclass (needed for svlist sortorder)
        maxPmClass formula: "(SELECT max(cv_weight.weight) FROM seq_variant as sv, cur_variant AS cv, cv_weight WHERE cv_weight.guideline='ACMG' and cv_weight.classification = cv.pm_class and cv.grp_variant_accession=sv.hgvsg and sv.id=id)"

        acmgSort formula: "(SELECT cv_weight.weight FROM seq_variant as sv, cur_variant cv, seq_sample as ss, cv_weight WHERE sv.seq_sample_id = ss.id and ss.clin_context_id = cv.clin_context_id and cv_weight.classification = cv.pm_class and cv.grp_variant_accession=sv.hgvsg and cv_weight.guideline='ACMG' and sv.id=id)"

        ampSort formula: "(SELECT cv_weight.weight FROM seq_variant as sv, cur_variant cv, seq_sample as ss, cv_weight WHERE sv.seq_sample_id = ss.id and ss.clin_context_id = cv.clin_context_id and cv_weight.classification = cv.amp_class and cv.grp_variant_accession=sv.hgvsg and cv_weight.guideline='AMP' and sv.id=id)"

        overallSort formula: "(SELECT cv_weight.weight FROM seq_variant as sv, cur_variant cv, seq_sample as ss, cv_weight WHERE sv.seq_sample_id = ss.id and ss.clin_context_id = cv.clin_context_id and cv_weight.classification = cv.overall_class and cv.grp_variant_accession=sv.hgvsg and cv_weight.guideline='Overall' and sv.id=id)"

        // Field larger than 500 varchar
        domains            (type: 'text')
        omim_ids           (type: 'text')
        pubmed             (type: 'text')
        cosmic             (type: 'text')
        cosmicOccurs       (type: 'text')
        clinvarVal         (type: 'text')
        clin_sig           (type: 'text')
        existing_variation (type: 'text')
    }

//    static transients = ['maxPmClass']
//    Find a new way to sort svlist before adding this back in.
//    DKGM 7-6-18

    String	toString()
    {
        "${gene}:${hgvsc}"
    }

    def CurateService

    /**
     * Check if this SV is curated in a certain context.
     * I.e. there is a CurVariant AND it is linked
     *
     * @param cc
     * @return
     */
    boolean curatedInContext( ClinContext cc )
    {
        for (cv in this.allCurVariants()) {
            if (cv.clinContext?.code == cc?.code) return true
        }
        return false
    }

    ArrayList<CurVariant> allCurVariants(){
        return CurVariant.executeQuery("from CurVariant cv where cv.grpVariant.accession=:hgvsg",[hgvsg:this.hgvsg])
    }

    Integer numberOfCuratedContexts() {
        return CurVariant.executeQuery("select count(*) from CurVariant cv where cv.grpVariant.accession=:hgvsg",[hgvsg:this.hgvsg])[0];
    }


    // Get generic CurVariant for this SeqVariant
    // Return null if none exists
    // DKGM 6-June-2017
    CurVariant genericCurVariant()
    {
        ClinContext cc = ClinContext.generic()
        def thisCvs = CurVariant.executeQuery("select cv from CurVariant cv where cv.grpVariant.accession=:hgvsg and cv.clinContext=:cc",[hgvsg:this.hgvsg,cc:cc])

        assert (thisCvs?.size() < 2 || thisCvs == null)    // accession and cc combo should be unique
        return thisCvs[0]
    }
    //  get CurVariant matching this SeqVariant's SeqSample's context
    //  Return null if none exists
    CurVariant currentCurVariant()
    {
        ClinContext cc = this.seqSample?.clinContext
        CurVariant result = null
        if( cc ) {
            def thisCvs = CurVariant.executeQuery("from CurVariant cv where cv.grpVariant.accession=:hgvsg and cv.clinContext=:cc",[hgvsg:this.hgvsg,cc:cc])

            assert (thisCvs?.size() < 2 || thisCvs == null)    // accession and cc combo should be unique
            result = thisCvs[0]
        }
        return result
    }

    boolean curate()
    {
        return CurateService.createNewCurVariantsFromSeqVariant(this)
    }



}









