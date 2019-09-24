/*
 * Copyright (c) 2018. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: David Ma
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class AcmgEvidence
{
    String     PVS1           = "unset"
    String     PS1            = "unset"
    String     PS2            = "unset"
    String     PS3            = "unset"
    String     PS4            = "unset"
    String     PM1            = "unset"
    String     PM2            = "unset"
    String     PM3            = "unset"
    String     PM4            = "unset"
    String     PM5            = "unset"
    String     PM6            = "unset"
    String     PP1            = "unset"
    String     PP2            = "unset"
    String     PP3            = "unset"
    String     PP4            = "unset"
    String     PP5            = "unset"
    String     BA1            = "unset"
    String     BS1            = "unset"
    String     BS2            = "unset"
    String     BS3            = "unset"
    String     BS4            = "unset"
    String     BP1            = "unset"
    String     BP2            = "unset"
    String     BP3            = "unset"
    String     BP4            = "unset"
    String     BP5            = "unset"
    String     BP6            = "unset"
    String     BP7            = "unset"

    String      classification = "Unclassified"
    String      acmgJustification  = ""
    CurVariant  curVariant

    static mapping = {
        acmgJustification       (type: 'text')
    }

    static Map classifications = [
        "Unclassified" : 0,
        "C1: Not pathogenic": 1,
        "C2: Unlikely pathogenic": 2,
        "C3: Unknown pathogenicity": 3,
        "C4: Likely pathogenic": 4,
        "C5: Pathogenic": 5
    ]
    static List<String> checkboxTypes = [
        "yes", "no", "unset", "na"
    ]

    static constraints = {
        curVariant          ( nullable: false, unique: true )
        acmgJustification   ( nullable: true )
        classification      ( inList: classifications.keySet() as List )
        PVS1                ( nullable: false, inList: checkboxTypes )
        PS1                 ( nullable: false, inList: checkboxTypes )
        PS2                 ( nullable: false, inList: checkboxTypes )
        PS3                 ( nullable: false, inList: checkboxTypes )
        PS4                 ( nullable: false, inList: checkboxTypes )
        PM1                 ( nullable: false, inList: checkboxTypes )
        PM2                 ( nullable: false, inList: checkboxTypes )
        PM3                 ( nullable: false, inList: checkboxTypes )
        PM4                 ( nullable: false, inList: checkboxTypes )
        PM5                 ( nullable: false, inList: checkboxTypes )
        PM6                 ( nullable: false, inList: checkboxTypes )
        PP1                 ( nullable: false, inList: checkboxTypes )
        PP2                 ( nullable: false, inList: checkboxTypes )
        PP3                 ( nullable: false, inList: checkboxTypes )
        PP4                 ( nullable: false, inList: checkboxTypes )
        PP5                 ( nullable: false, inList: checkboxTypes )
        BA1                 ( nullable: false, inList: checkboxTypes )
        BS1                 ( nullable: false, inList: checkboxTypes )
        BS2                 ( nullable: false, inList: checkboxTypes )
        BS3                 ( nullable: false, inList: checkboxTypes )
        BS4                 ( nullable: false, inList: checkboxTypes )
        BP1                 ( nullable: false, inList: checkboxTypes )
        BP2                 ( nullable: false, inList: checkboxTypes )
        BP3                 ( nullable: false, inList: checkboxTypes )
        BP4                 ( nullable: false, inList: checkboxTypes )
        BP5                 ( nullable: false, inList: checkboxTypes )
        BP6                 ( nullable: false, inList: checkboxTypes )
        BP7                 ( nullable: false, inList: checkboxTypes )
    }

    static searchable = true

    String toString() {
        classification
    }


    @Override
    public int compareTo(AcmgEvidence other) {
        return Integer.compare(classifications[this.classification], classifications[other.classification])
    }

    Boolean anyUnset() {
        if ( PVS1 == 'unset' ) return true
        if ( PS1 == 'unset' ) return true
        if ( PS2 == 'unset' ) return true
        if ( PS3 == 'unset' ) return true
        if ( PS4 == 'unset' ) return true
        if ( PM1 == 'unset' ) return true
        if ( PM2 == 'unset' ) return true
        if ( PM3 == 'unset' ) return true
        if ( PM4 == 'unset' ) return true
        if ( PM5 == 'unset' ) return true
        if ( PM6 == 'unset' ) return true
        if ( PP1 == 'unset' ) return true
        if ( PP2 == 'unset' ) return true
        if ( PP3 == 'unset' ) return true
        if ( PP4 == 'unset' ) return true
        if ( PP5 == 'unset' ) return true
        if ( BA1 == 'unset' ) return true
        if ( BS1 == 'unset' ) return true
        if ( BS2 == 'unset' ) return true
        if ( BS3 == 'unset' ) return true
        if ( BS4 == 'unset' ) return true
        if ( BP1 == 'unset' ) return true
        if ( BP2 == 'unset' ) return true
        if ( BP3 == 'unset' ) return true
        if ( BP4 == 'unset' ) return true
        if ( BP5 == 'unset' ) return true
        if ( BP6 == 'unset' ) return true
        if ( BP7 == 'unset' ) return true
        return false
    }

}
