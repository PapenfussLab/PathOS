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

    static	hasMany = [ seqSamples: SeqSample ]

    static constraints =
    {
        manifest(    unique: true )
        description( nullable: true)
        panelGroup(  nullable: false )
    }

    String	toString()
    {
        manifest
    }

    static mapping =
    {
        sort panelGroup: "asc"

    }
}
