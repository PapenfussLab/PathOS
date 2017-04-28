/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class PatSample implements Taggable
{
    String		sample
    Patient		patient
    Boolean		ca2015 = false
    String		tumour = "T"
    AuthUser	owner
    Date        collectDate
    Date        rcvdDate
    Date        requestDate
    String      requester
    String		pathlab
    String      tumourType
    String      stage
    String      formalStage

    //  Nov 2015: genologics fields from Holly
    //
    Integer     hAndE               // number of H&E Slides
    Integer     methylGreen         // number of methyn green slides
    String      slideComments       // Comments on slide
    String      slideTech           // Labtech that entered slide info
    String      retSite             // Anatomical site of tissue from pathology report
    String      repMorphology       // Pathology report morphology
    String      pathMorphology      // Reviewing pathologist morphology opinion
    BigDecimal  tumourPct           // % of tumour cells in area marked by pathologist
    String      pathComments        // Reviewing pathologist comments
    String      pathologist         // Reviewing pathologist
    String      hollyLastUpdated


    static hasMany = [ patAssays: PatAssay, seqSamples: SeqSample, tags: Tag ]


    static searchable =
        {
            except = [ 'seqSamples' ]
            owner component: true
            patient component: true
            tags component: true
        }

    static constraints =
        {
            sample( unique: true, blank: false)
            patient()
            owner()
            ca2015()
            collectDate( nullable: true)
            rcvdDate( nullable: true)
            requestDate( nullable: true)
            requester( nullable: true)
            tumour( inList: [ "T", "N" ], nullable: true )
            pathlab( nullable: true)
            tumourType( nullable: true)

            //all Holly fields set as nullable so we can import
            hAndE           nullable: true
            methylGreen     nullable: true
            slideTech       nullable: true
            retSite         nullable: true
            tumourPct       nullable: true
            pathologist     nullable: true
            hollyLastUpdated     nullable: true
            stage          (nullable: true)
            formalStage    (nullable: true)
            slideComments  (nullable: true, type: 'text', maxSize: 3000)
            pathComments   (nullable: true, type: 'text', maxSize: 3000)
            repMorphology  (nullable: true, type: 'text', maxSize: 3000)
            pathMorphology (nullable: true, type: 'text', maxSize: 3000)


        }

    String	toString()
    {
        sample
    }
}
