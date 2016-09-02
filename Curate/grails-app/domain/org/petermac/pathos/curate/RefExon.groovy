/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.pathos.curate

class RefExon
{
    String	gene
    String  refseq
    String  exon
    String  strand
    String  idx
    String  exonStart
    String  exonEnd
    String  exonFrame

    static mapping =
    {
        table                   "ref_exon"
        version                 false
        columns
        {
            gene	            column:	"gene"
            refseq	            column:	"refseq"
            exon	            column:	"exon"
            strand	            column:	"strand"
            idx	                column:	"idx"
            exonStart	        column:	"exonStart"
            exonEnd             column:	"exonEnd"
            exonFrame	        column:	"exonFrame"
        }
    }

    static constraints =
    {
        gene()
        refseq()
        exon()
        strand()
        idx( nullable: true)
        exonStart()
        exonEnd()
        exonFrame()
    }

    String	toString()
    {
        "(${gene})${refseq}:${exon}"
    }
}
