/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Authors: David Ma, Chris Love
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

/**
 *  Genelist table
 *  To store gene lists, either for panels or for pathways
 *
 *  DKGM & CGL 12-July
 */

@Entity
class Genelist
{
    static hasMany = [ genes: Gene ]

    String toString()
    {
        "${genes.join(",")}"
    }
}
