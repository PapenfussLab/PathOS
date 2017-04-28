/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

/**
 * Domain Class to capture relationships between SeqSample groups
 * In particular Replicates, Duplicates, TumourNormals, Trios
 *
 * User: Ken Doig
 * Date: 29-Apr-16  Initial Create
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class SeqRelation
{
    String  relation                // relationship type
    String  base = 'none'           // common base sample

    static  belongsTo   = [ SeqSample ]
    static	hasMany     = [ samples: SeqSample  ]

    static  constraints =
    {
        relation( inList: [ "Replicate", "Duplicate", "TumourNormal", "Trio", "TimeSeries" ]  )
        base( nullable: true )
    }

    //  Indexes on relation and base
    //
    static      mapping =
    {
        base        index: 'base_idx'
        relation    index: 'relation_idx'
    }

    String	toString()
    {
        "${base}[${samples?.size()}]:${relation}"
    }
}
