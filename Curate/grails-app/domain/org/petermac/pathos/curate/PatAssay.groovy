package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class PatAssay
{
    String      testSet
    String      testName
    Date        authDate
    PatSample   patSample
    LabAssay    labAssay

    static belongsTo = [ patSample: PatSample ]

    static constraints =
        {
            testSet()
            testName()
            patSample()
            labAssay( nullable: true)
            authDate( nullable: true)
        }

    String	toString()
    {
        "${testSet}:${testName}"
    }


}
