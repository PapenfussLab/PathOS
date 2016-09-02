/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

/**
 * Domain class for Panel regions of interest (ROI)
 *
 * An ROI is a small genomic region containing known deleterious mutations that should
 * be reported on for positive or negative findings
 *
 * Author: Ken Doig
 */

@Entity
class Roi
{
    Panel   panel
    String	manifestName
    String  name
    String  gene
    String  exon
    String  chr
    Integer startPos
    Integer endPos
    String  amplicons       // List of overlapping amplicon names

    static constraints =
        {
            panel( nullable: false )
//            manifestName( nullable: false )
            name( nullable: false )
            gene( nullable: false )
            exon( nullable: true )
            chr(  nullable: false )
            startPos(  nullable: false )
            endPos(    nullable: false )
            amplicons( nullable: true)
        }

    static mapping = {
        panel index: 'panel_idx'
        chr index:  'chr_idx'
        startPos index: 'starpos_idx'
        endPos index: 'endPos_idx'

    }

    String	toString()
    {
        "ROI ${name}"
    }
}
