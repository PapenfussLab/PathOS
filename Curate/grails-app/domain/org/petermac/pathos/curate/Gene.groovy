/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Authors: David Ma, Chris Love
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

/**
 *  New Gene table, to replace RefGene
 *
 *  DKGM & CGL 12-July
 */

@Entity
class Gene
{
    String  gene
    String  hgncid
    String  chr
    String  entrezid
    String  aliases
    String  genedesc

    static mapping =
    {
        genedesc    ( type: 'text' )
    }

    static constraints =
    {
        gene        ( unique: true )
        hgncid      ( unique: true )
        chr         ( nullable: true )
        entrezid    ( nullable: true )
        aliases     ( nullable: true, maxSize: 2000 ) // Leaving as VARCHAR(2000) for search
        genedesc    ( nullable: true )
    }

    String	toString()
    {
        "${gene}"
    }

    /**
     * Find all the pathways that this gene belongs to
     * @return
     */
    Set<Pathway> pathways() {
        Gene gene = this;
        Set<Pathway> results = []

        Pathway.list().each { pathway ->
            if (pathway.genelist.genes.contains( gene )) {
                results.add(pathway);
            }
        }

        return results;
    }
}





