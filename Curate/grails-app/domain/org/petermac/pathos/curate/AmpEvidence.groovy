/*
 * Copyright (c) 2018. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: David Ma
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class AmpEvidence
{

    String      classification = "Unclassified"
    String      ampJustification  = ""
    String      therapeuticRating = "unset"
    String      therapeuticCategory = "unset"
    String      therapeuticText = ""
    String      diagnosisRating = "unset"
    String      diagnosisCategory = "unset"
    String      diagnosisText = ""
    String      prognosisRating = "unset"
    String      prognosisCategory = "unset"
    String      prognosisText = ""
    CurVariant  curVariant

    static mapping = {
        ampJustification       (type: 'text')
        therapeuticText        (type: 'text')
        diagnosisText          (type: 'text')
        prognosisText          (type: 'text')
    }

    static List<String> ratingTypes = [
        "unset", "levelA", "levelB", "levelC", "levelD"
    ]

    static constraints = {
        curVariant          ( nullable: false, unique: true )
        ampJustification    ( nullable: true )
        classification      ( nullable: false )
        therapeuticRating   ( inList: ratingTypes )
        diagnosisRating     ( inList: ratingTypes )
        prognosisRating     ( inList: ratingTypes )
        therapeuticText     ( nullable: true )
        diagnosisText       ( nullable: true )
        prognosisText       ( nullable: true )
    }

    static searchable = true

    String toString() {
        classification
    }









//    @Override
//    public int compareTo(AmpEvidence other) {
//        return Integer.compare(classifications[this.classification], classifications[other.classification])
//    }

}
