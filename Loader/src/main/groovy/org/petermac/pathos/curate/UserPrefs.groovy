package org.petermac.pathos.curate


import grails.persistence.Entity

@Entity
class UserPrefs {

    Set<String> columnsShown = new HashSet<String>()
    Set<String> columnsHidden = new HashSet<String>()
    String columnOrderRemap
    String gridInfoJson
    static hasMany = [columnsShown: String, columnsHidden: String]
    static constraints =
            {
                columnOrderRemap nullable: true, maxSize: 9999
                gridInfoJson nullable: true, maxSize: 9999
            }
    

}

