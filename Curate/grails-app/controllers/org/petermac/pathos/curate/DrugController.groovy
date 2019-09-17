/*
 * Copyright (c) 2013. PathOS SeqSample Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.converters.JSON
import org.grails.plugin.easygrid.Easygrid

import java.text.MessageFormat

@Easygrid
class DrugController
{
    static scaffold = true
    def SpringSecurityService


    /**
     * Table definition for Easygrid
     */
    def drugGrid =
            {
                dataSourceType  'gorm'
                domainClass     Drug
                enableFilter    true
                editable        false
                inlineEdit      false

                //  Export parameters
                //
                export
                        {
                            export_title  'Drug'
                            maxRows       1000000          // Maximum number of samples per export
                        }

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
                            name
                            status
                            molecularTargets
                            approvedConditions
                            description
                            contraindicatedAlterations
                            experimental
                            experimentalConditions
                            brands
                            externalIds
                            molecularExperimentalTargets
                            synonyms
                            badge
                            alias
                            approvedConditionMatch
                            approved

                        }
            }

}


