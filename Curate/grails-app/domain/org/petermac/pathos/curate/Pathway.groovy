/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Authors: David Ma, Chris Love
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

/**
 *  Pathway table, to store pathway info
 *
 *  DKGM & CGL 12-July
 */

@Entity
class Pathway
{
    String          name
    String          url
    String          process
    String          description
    Genelist        genelist

    static mapping =
    {
        description    ( type: 'text' )
    }

    static constraints =
    {
        name        ( unique: true )
        url         ( nullable: true )
        process     ( nullable: true )
    }

    String	toString()
    {
        "${name}"
    }
}
