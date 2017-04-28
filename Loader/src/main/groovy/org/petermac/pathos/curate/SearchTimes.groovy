package org.petermac.pathos.curate


import grails.persistence.Entity

@Entity
class SearchTimes {

    String  query
    Integer speed
    String  user
    String time
    Integer numberOfResults
    String pathosVersion

    static constraints =
            {
                numberOfResults nullable: true
                pathosVersion nullable: true
            }
}
