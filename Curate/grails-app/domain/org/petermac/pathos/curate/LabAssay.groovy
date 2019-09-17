/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.pathos.curate

import grails.persistence.Entity


/**
 *  LabAssay Class for billable assay entities
 *
 *  Description:
 *  A LabAssay holds the reportable genes for this detente assay code
 *
 *  Author:     Ken Doig    9-Mar-17
 */
@Entity
class LabAssay
{
    String      testSet                     //  Detente test code eg M123
    String      testName                    //  Detente assay name
    Panel       panel                       //  Assay panel object
    String      genes                       //  Default gene set that will be copied over to matching PatAssays as masking set (if set)
    String      panelGeneric                //  Auslab panel1
    String      panelGenericName            //  Auslab panel1 name
    String      panelReportable             //  Auslab panel2
    String      panelReportableName         //  Auslab panel2 name
    String      returnCode                  //  Auslab return code
    String      returnName                  //  Auslab return code name

    static mapping =
        {
            genes       (type: 'text')
        }

    static constraints =
        {
            testSet(    unique:   true )
            testName(   unique:   true )
            panel(      nullable: true )
            genes       ( nullable: true )
            panelGeneric(     nullable: true )
            panelGenericName( nullable: true )
            panelReportable(     nullable: true )
            panelReportableName( nullable: true )
            returnCode( nullable: true )
            returnName( nullable: true )
        }

    String	toString()
    {
        "${testSet}:${testName}"
    }
}
