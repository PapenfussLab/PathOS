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
    Integer age


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
            sex( inList: [
                "A", // Ambiguous
                "F", // Female
                "I", // Intersex
                "M", // Male
                "N", // Not applicable
                "O", // Other
                "U"  // Unknown
            ] )
        }

    static transients = ['age']

    String	toString()
    {
        urn
    }

    Integer getAge() {
        Date    now = new Date();
        Double  age = (now - this?.dob) / 365.25;
        return  Math.floor(age) as Integer
    }
}
