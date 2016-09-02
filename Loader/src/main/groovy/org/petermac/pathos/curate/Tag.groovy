/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

/**
 * Created by seleznev andrei on 2/05/2016.
 */
@Entity
class Tag {
    static searchable = true

    String label
    boolean isAuto
    AuthUser createdBy
    String description

    static mapping =
    {
        isAuto default: false
    }

    static constraints =
    {
        label ( unique: true, nullable: false )
        createdBy ( nullable: false )
        description ( maxSize: 8000, nullable: true )
    }

    String	toString()
    {
        "${label}"
    }
}
