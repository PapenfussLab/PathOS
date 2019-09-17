/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class SeqSample implements Taggable
{
    Panel		panel
    Float		dnaconc
    Seqrun		seqrun
    PatSample	patSample
    String		sampleName		    // Many test or research samples have no patient Sample record
    String      analysis
    String      userName
    String      userEmail
    String      laneNo

    Date        firstReviewedDate
    Date        secondReviewedDate
    Date        finalReviewedDate

    AuthUser	firstReviewBy       // Curator
    AuthUser	secondReviewBy      // Curator
    AuthUser	finalReviewBy       // CurVariant curation authorisation
    AuthUser    authorisedQc        // QC authorisation
    Boolean     authorisedQcFlag = false
    Boolean     passfailFlag     = false
    String      qcComment
    String      sampleType
    ClinContext clinContext

    String      sampleGeneMask  //custom gene mask for unmasking:

    static		hasMany     = [ seqVariants: SeqVariant, relations: SeqRelation, tags: Tag  ]

// seqSample does not need to be searchable!!!!!
// The search controller does not use it! We search seqSamples directly!
//    static      searchable  =
//    {
//        except = [ 'seqVariants', 'relations' ]
//        panel component: true
//        tags component: true
//    }



    static      constraints =
    {
        seqrun()
        patSample( nullable: true )
        sampleName(  validator: { val -> if (val.contains(' ')) return 'value.hasASpace' } )
        panel( nullable: false)
        dnaconc( nullable: true)
        analysis()
        userName()
        userEmail()
        laneNo()
        authorisedQcFlag()
        passfailFlag()
        finalReviewBy( nullable: true )
        firstReviewBy( nullable: true )
        secondReviewBy( nullable: true )
        firstReviewedDate( nullable: true )
        secondReviewedDate( nullable: true )
        finalReviewedDate( nullable: true )
        authorisedQc( nullable: true )
        qcComment( maxSize: 500, nullable: true )
        sampleType( inList: [ "Control", "NTC", "Replicate", "Tumour", "Normal", "TumourNormal", 'Derived' ], nullable: true )
        clinContext( nullable: true )
        sampleGeneMask( nullable: true )    //custom gene mask if set
    }

    //  Indexes on sampleName, seqrun
    //
    static      mapping =
    {
        seqrun      index: 'seqrun_idx'
        sampleName  index: 'sample_name_idx'
        sampleType       index: 'sample_type_idx'
        sampleGeneMask  type: 'text'
        relations   joinTable: [name: 'seq_sample_relations', key: 'seq_sample_id', column: 'seq_relation_id']
    }

    String	toString()
    {
        sampleName
    }

    /**
     * This function returns the list of valid genes from the variants of this SeqSample
     * @return
     */
    ArrayList<Gene> geneList() {
        Set<Gene> results = []
        this.seqVariants.each { sv ->
            if(sv.gene && sv.filterFlag.contains('pass')) {
                // Todo: add a warning if no valid gene is found.
                Gene.findAllByGene(sv.gene).each{ gene ->
                    results.add(gene)
                }
            }
        }
        return results as ArrayList<Gene>
    }

    Set<SeqVariant> reportableVariants() {
        return this.seqVariants.findAll { sv -> sv.reportable }
    }

    Set<CurVariant> curVariants() {
        Set<CurVariant> results = []
        ClinContext currentCC = this.clinContext
        this.seqVariants.each { sv ->
            results.add(CurVariant.findByClinContextAndHgvsg(currentCC, sv.hgvsg))
        }
        return results.findAll{ it }
    }

    /**
     * Get Lab Assays for this sample
     * @return
     */
    Set<LabAssay> labAssays() {
        Set<LabAssay> assays = []

        //  eagerly fetch patAssays for this patSample to avoid lazyloadingexception
        //
        if(!this.patSample) { return [] }

        PatSample ps = PatSample.findById(this.patSample?.id, [fetch: [patAssays: 'eager']])
        for ( pa in ps.patAssays ) {
            LabAssay.findAllByTestSet(pa.testSet).each { labAssay ->
                assays.add(labAssay)
            }
        }

        return assays
    }

    /**
     * gene mask for this seqsample: either get Custom, or fetch patSample's labassays
     * note! a blank ("") gene mask return value means: unmask everything
     * @return arraylist of genes to be masked for this seqsample specifically
     */
    ArrayList<String> geneMask() {
        //  grab genes, or default labassay ones if these aren't set

        if(usingDefaultGeneMask()) {    //if our custom gene mask is null, that means it's underfined and so we're using the default labassay gene mask. if our custom mask is empty: empty means "unmask everything"
            return this.defaultGeneMask()
        }
        return this.sampleGeneMask?.replaceAll(/ /,'')?.tokenize(',')?.unique()?.sort()
    }

    /**
     * default gene mask based on labassays connected to this seqsample
     * @return
     */
    ArrayList<String> defaultGeneMask() {

        Set<String> maskGenes = []

        //  eagerly fetch patAssays for this patSample to avoid lazyloadingexception
        //
        if(!this.patSample) { return [] }

        PatSample ps = PatSample.findById(this.patSample?.id, [fetch: [patAssays: 'eager']])
        for ( pa in ps.patAssays ) {
            //  we only *expect* one patassay to have a labassay w/ gene masking set but in theory nothing stops multiple passays from having that
            LabAssay.findAllByTestSet(pa.testSet).each { labAssay ->
                if(labAssay?.genes) {
                    labAssay.genes?.replaceAll(/ /,'')?.tokenize(',')?.each { gene ->
                        maskGenes.add(gene)
                    }
                }
            }

        }

        ArrayList<String> results = maskGenes.unique().sort()

        return results
    }

    /**
     * get the names of the labassays used for gene masking in sampleGeneMask, or custom if custom
     * this is for pipeline logging
     * @return
     */
    String geneMaskAssayName() {
        if(usingDefaultGeneMask()) {
            ArrayList labAssays = []

            PatSample ps = PatSample.findById(this.patSample?.id, [fetch: [patAssays: 'eager']])
            for ( pa in ps?.patAssays ) {
                //  we only *expect* one patassay to have a labassay w/ gene masking set but in theory nothing stops multiple passays from having that
                LabAssay.findAllByTestSet(pa.testSet).each { labAssay ->
                    if(labAssay?.testName) {
                        labAssays.add(labAssay.testName)
                    }
                }
            }
            if(!labAssays)  return "no assay with mask"
            return labAssays.join(", ")
        }

        return "custom gene mask"
    }

    /**
     * Checks sampleGeneMask, and if it is the default, returns true.
     * @return
     */
    Boolean usingDefaultGeneMask() {
        if( this.sampleGeneMask == null )
        {
            return true
        }

        Set<String> customGenes  = this.sampleGeneMask?.tokenize(',')?.collect { it.trim() }
        Set<String> defaultGenes = this.defaultGeneMask()

        return customGenes == defaultGenes
    }

}
