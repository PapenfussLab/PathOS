/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

class RefGene
{
    String	gene
    String  hgncid
    String  accession
    String  genedesc
    String  refseq

    static mapping =
    {
        table                   "ref_hgnc_genes"
        version                 false
        columns
        {
            gene	            column:	"gene"
            hgncid	            column:	"hgncid"
            accession	        column:	"accession"
            genedesc	        column:	"genedesc"
            refseq	            column:	"refseq"
        }
    }

    static constraints =
    {
        gene( unique: true)
        hgncid( nullable: true)
        accession( nullable: true)
        genedesc( nullable: true)
        refseq( nullable: true)
        refseq validator: { val, obj, errors ->
            //return (it.startsWith("NM_"))
            if (val != null && !(val.startsWith("NM_"))) {
                errors.rejectValue('refseq','Refseq transcript must start with "NM_"! Error ')
            }
           }


    }

    String	toString()
    {
        "${gene}"
    }
}
