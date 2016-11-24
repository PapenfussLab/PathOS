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
    CurVariant	curated
    String      cosmic
    String      dbsnp
    String      chr
    String      pos
    String      exon
    String	    alamutClass
    Boolean     filtered
    String      filterFlag
    Boolean     reportable
    Double      varPanelPct     // populated by Filtering DEPRECATED
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
    ClinContext clinContext

    static constraints =
        {
            variant(     maxSize: 500 )
            ens_variant( maxSize: 500 )
            gene()
            curated( nullable: true )
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
            domains( maxSize: 2000, nullable: true)
            genedesc( nullable: true)
            cytoband( nullable: true)
            omim_ids( maxSize: 2000, nullable: true)
            clin_sig( nullable: true)
            biotype( nullable: true)
            pubmed( maxSize: 2000, nullable: true)
            esp( nullable: true)
            cosmicOccurs( maxSize: 1000, nullable: true )
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
            clinvarVal( maxSize: 2000, nullable: true )
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

            clinContext( nullable: true )
        }

    static hasMany = [ tags: Tag, varLinks: VarLink ]


    //  the mappedBy resolves the problem of multiple many-to-many and one-to-one relationships between curvar and seqvar
    //  there's a matching line in curvar. this specifies that the manytomany is between the below vars only.
    //  otherwise CurVar curated and SeqVar originating start getting messed up
  //  static mappedBy = [curVariants: 'seqVariants']


    //  Indexes on seqSample,sampleName,variant,hgvsg
    //
    static      mapping =
    {
        seqSample   index: 'seq_sample_idx' //, unique: true
        variant     index: 'variant_idx'
        sampleName  index: 'sample_name_idx'
        hgvsg       index: 'hgvsg_idx' //, unique: true
        hgvsc       index: 'hgvsc_idx'
    }

    String	toString()
    {
        "${gene}:${hgvsc}"
    }


    /**
     * returns panel freq (from 0-100)
     * @return
     */
    BigDecimal panelFreq() {
        try {
            BigDecimal pf = ( this.varSamplesSeenInPanel.div(this.varSamplesTotalInPanel) )
            return pf * 100
       } catch(all) {  //on any exception (null, divide-by-zero might happen)
            return 0
       }
    }

    /**
     * Check if this SV is curated in a certain context.
     * I.e. there is a CurVariant AND it is linked
     *
     * @param cc
     * @return
     */
    boolean curatedInContext(ClinContext cc)
    {
        boolean result = false;
        this.varLinks.each { VarLink link ->
            if (link?.curVariant?.clinContext == cc) {
                result = true;
            }
        }
        return result;
    }

    def VarLinkService

    //  get all cur variants that are linked (that is, have a var link) for this seq var
    //
    ArrayList linkedCurVariants()
    {
        return VarLinkService.getCurVariantsForSeqVariant(this)
    }

    CurVariant CurVariantMatchingContext(ClinContext cc = this.clinContext)
    {
        return VarLinkService.getCurVariantMatchingContext(this,cc)
    }

    CurVariant currentCurVariant()
    {
        return VarLinkService.getCurrentCV(this)
    }

    //  get the preferred cur variant for this seq variant
    //
    CurVariant preferredCurVariant()
    {
        return VarLinkService.getPreferredCurVariantForSeqVariant(this)
    }

    // set a cur variant as a preferred cur variant for this seq variant
    boolean makePreferredCurVariant(CurVariant cv)
    {
        return VarLinkService.setPreferredCurVariantForSeqVariant(cv,this)
    }

    boolean curate(ClinContext cc,defaultPreferred=false)
    {
        return VarLinkService.createNewCurVarFromSeqVar(this,cc,defaultPreferred)
    }
}
