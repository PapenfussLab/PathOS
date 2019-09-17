/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class Panel
{
    String	manifest
    String  panelGroup
    String  description
    Boolean skipGeneMask = false

    static	hasMany = [ seqSamples: SeqSample ]

    static constraints =
    {
        manifest(    unique: true )
        description( nullable: true)
        panelGroup(  nullable: false )
        skipGeneMask        ( nullable: true )
    }

    String	toString()
    {
        manifest
    }

    static searchable = true

    static mapping =
    {
        sort panelGroup: "asc"

    }

//    ArrayList<String> allGroups(){
//        return Panel.executeQuery("select panel.panelGroup from Panel panel group by panelGroup")
//    }

}
