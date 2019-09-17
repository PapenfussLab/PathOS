package org.petermac.pathos.curate


import grails.persistence.Entity

@Entity
class Preferences {

    AuthUser    user
    Integer     svlistRows
    Integer     numberOfSeqruns
    String      panelList
    Boolean     compressedView = false
    Boolean     skipGeneMask = false
    Boolean     d3heatmap = false
    String      sortPriority = "acmgCurVariant,allCuratedVariants,ampCurVariant,overallCurVariant,reportable"

    static mapping =
    {
        panelList               (type: 'text')
    }

    static constraints =
    {
        svlistRows          nullable: true
        numberOfSeqruns     nullable: true
        panelList           nullable: true
        compressedView      nullable: true
        skipGeneMask        nullable: true
        d3heatmap           nullable: true
    }
}

