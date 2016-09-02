/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class Patient
{
    String	fullName
    Date	dob
    String	urn
    String	sex

    static	hasMany = [ patSamples: PatSample ]

    static  searchable =
        {
            only = [ 'fullName' ]
        }

    static constraints =
        {
            fullName()
            urn( unique: true )
            dob()
            sex(  inList: [ "M", "F", "U" ] )
        }

    String	toString()
    {
        fullName
    }
}
