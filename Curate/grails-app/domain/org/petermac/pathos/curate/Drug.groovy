/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class Drug implements Taggable
{
    String		name		    // drug Name
    String      alias
    String      approved
    String      approvedConditionMatch
    String      approvedConditions
    String      contraindicatedAlterations
    String      badge
    String      brands
    String      description
    String      experimental
    String      experimentalConditions
    String      externalIds
    String      molecularExperimentalTargets
    String      molecularTargets
    String      status
    String      synonyms


    static      constraints =
    {
        name                            (nullable: true)
        alias                           (nullable: true)
        approved                        (nullable: true)
        approvedConditionMatch          (nullable: true)
        approvedConditions              (nullable: true)
        badge                           (nullable: true)
        brands                          (nullable: true)
        contraindicatedAlterations      (nullable: true)
        description                     (nullable: true)
        experimental                    (nullable: true)
        experimentalConditions          (nullable: true)
        externalIds                     (nullable: true)
        molecularExperimentalTargets    (nullable: true)
        molecularTargets                (nullable: true)
        status                          (nullable: true)
        synonyms                        (nullable: true)

    }

    //  Indexes on drug name
    //
    static      mapping =
    {
        name                            ( index: 'drug_name_idx' )
        brands                          ( type: 'text' )
        approvedConditions              ( type: 'text' )
        contraindicatedAlterations      ( type: 'text' )
        description                     ( type: 'text' )
        externalIds                     ( type: 'text' )
        molecularExperimentalTargets    ( type: 'text' )
        synonyms                        ( type: 'text' )
    }


    String	toString()
    {
        name
    }

}
