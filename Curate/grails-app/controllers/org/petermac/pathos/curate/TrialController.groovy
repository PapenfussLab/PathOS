/*
 * Copyright (c) 2013. PathOS SeqSample Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Love Chris
 */

package org.petermac.pathos.curate

import org.grails.plugin.easygrid.Easygrid
import java.text.MessageFormat

@Easygrid
class TrialController {

    static scaffold = true
    def SpringSecurityService


    /**
     * Table definition for Easygrid
     */
    def trialGrid =
    {
        dataSourceType  'gorm'
        domainClass     Trial
        enableFilter    true
        editable        false
        inlineEdit      false

        //  Grid jqgrid defaults
        //
        jqgrid
        {
            height        = '100%'
            width         = '100%'
            rowNum        = 20
            rowList       = [20, 50, 100]
            sortable      = true
            filterToolbar = [ searchOperators: false ]
        }

        columns
        {
            id          { type 'id'; jqgrid { hidden = true } }
            study
            briefTitle
            molecularAlterations
            phase
            status
            studyType
            startDate
            interventions
            score
            title
            locations
            overallContact

        }
    }

}
