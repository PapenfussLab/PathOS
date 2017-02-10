package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class PatAssay
{
    String  testSet
    String  testName
    Panel   panel
    Date    authDate
    PatSample  patSample
    String  genes               //  Comma separated list of genes to filter on for this test
                                //  Todo: Should really be in an Assay object not a Test instance

    static belongsTo = [ patSample: PatSample ]

    static constraints =
        {
            testSet()
            testName()
            patSample()
            panel( nullable: true )
            authDate( nullable: true)
            genes( maxSize: 2000, nullable: true )
        }

    String	toString()
    {
        "${testSet}:${testName}"
    }
}
