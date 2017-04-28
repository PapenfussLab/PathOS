/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

/**
 *  Removed refseq field KDD 31-jan-17
 *  Made Grails domain table
 */

@Entity
class RefGene
{
    String	gene
    String  hgncid
    String  accession
    String  genedesc

    static constraints =
    {
        gene(      unique:   true)
        hgncid(    nullable: true)
        accession( nullable: true)
        genedesc(  nullable: true, maxSize: 5000)
    }

    String	toString()
    {
        "${gene}"
    }
}
