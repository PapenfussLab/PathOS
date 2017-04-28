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


    static		hasMany     = [ seqVariants: SeqVariant, relations: SeqRelation, tags: Tag  ]

    static      searchable  =
    {
        except = [ 'seqVariants', 'relations' ]
        panel component: true
        tags component: true
    }

    static      constraints =
    {
        seqrun()
        patSample( nullable: true )
        sampleName()
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
        sampleType( inList: [ "Control", "NTC", "Replicate", "Tumour", "Normal", "TumourNormal" ], nullable: true )
        clinContext( nullable: true )
    }

    //  Indexes on sampleName, seqrun
    //
    static      mapping =
    {
        seqrun      index: 'seqrun_idx'
        sampleName  index: 'sample_name_idx'
        sampleType       index: 'sample_type_idx'

    }

    String	toString()
    {
        sampleName
    }
}
