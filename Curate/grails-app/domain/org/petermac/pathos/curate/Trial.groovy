package org.petermac.pathos.curate



import grails.persistence.Entity

@Entity
class Trial
{

    String      status
    String      startDate
    String      interventions
    String      molecularAlterations
    String      score
    String      title
    String      locations
    String      briefTitle
    String      overallContact
    String      phase
    String      study
    String      studyType

    static      constraints =
    {
        study                   (nullable: true)
        briefTitle              (nullable: true)
        molecularAlterations    (nullable: true)
        phase                   (nullable: true)
        status                  (nullable: true)
        studyType               (nullable: true)
        startDate               (nullable: true)
        interventions           (nullable: true)
        score                   (nullable: true)
        title                   (nullable: true)
        locations               (nullable: true)
        overallContact          (nullable: true)

    }

    //  Indexes on drug name
    //
    static mapping =
    {
        study                   (type: 'text')
        briefTitle              (type: 'text')
        molecularAlterations    (type: 'text')
        studyType               (type: 'text')
        title                   (type: 'text')
        overallContact          (type: 'text')
        locations               (type: 'text')
    }


    String toString()
    {
        briefTitle
    }
}
