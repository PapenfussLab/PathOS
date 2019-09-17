package org.petermac.pathos.curate


import grails.persistence.Entity

@Entity
class AnnoMetaData {

    String colName           //name of the column as named by the annotator
    String displayName       //name of the column as shown to the user
    String description       //human-readable description for user
    String annoDataSource    //datasource (ANV/IARC/etc)
    String category          //grouping field

    static mapping =
            {
                description     (type: 'text')
            }
    static constraints =
            {
                description ( nullable: true )
                colName ( unique:'annoDataSource' )
            }
}