package org.petermac.pathos.curate


import grails.persistence.Entity

@Entity
class IcdO {
    String siteCode
    String site
    String histCode
    String histology
    String histDetailCode
    String histDetail

    static constraints =
            {
            }
}
