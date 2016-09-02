/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class CurVariant implements Taggable
{
    String	variant
    String	ens_variant
    String	gene
    String	gene_type
    String	gene_pathway
    String	gene_process
    String	hgvsc
    String	hgvsp
    String	hgvsg
    String	consequence
    String	chr
    String	pos
    String	exon
    String	pmClass = "Unclassified"
    String	reportDesc
    AuthUser	classified
    AuthUser	authorised
    Boolean authorisedFlag = false
    Date	dateCreated = new Date()
    Date	lastUpdated = new Date()
    String	alamutClass
    String  siftCat
    String  polyphenCat
    String	cosmic
    String	dbsnp
    Evidence evidence
    ClinContext clinContext

    static embedded = ['evidence']

    static hasMany	= [seqVariants: SeqVariant,  tags: Tag ]

    static constraints =
    {
        variant( unique: true, blank: false )
        ens_variant(nullable: true)
        gene()
        gene_type(nullable: true)
        hgvsc()
        hgvsp( nullable: true )
        //hgvsg( unique: 'mutContext' )
        consequence(nullable: true)
        pmClass(  inList:	[
                "Unclassified",
                "C1: Not pathogenic",
                "C2: Unlikely pathogenic",
                "C3: Unknown pathogenicity",
                "C4: Likely pathogenic",
                "C5: Pathogenic"
        ], blank: false )
        alamutClass( nullable: true )
        siftCat( nullable: true )
        polyphenCat( nullable: true )
        gene_pathway(nullable: true)
        gene_process(nullable: true)
        chr(nullable: true)
        pos(nullable: true)
        exon(nullable: true)
        cosmic(nullable: true)
        dbsnp( nullable: true)
        reportDesc( maxSize: 8000, nullable: true )
        evidence()
        classified( nullable: true )
        authorisedFlag()
        authorised( nullable: true )
        dateCreated( nullable: true )
        lastUpdated()
        seqVariants( nullable: true )
        clinContext( nullable: true )
    }

    static mapping =
    {
        sort alamutClass: "desc"
    }

    static  searchable =
    {
        only = [ 'gene', 'hgvsc', 'hgvsg', 'hgvsp', 'reportDesc', 'evidence', 'tags' ]
        evidence component: true
        tags component: true
    }

    String	toString()
    {
         "${gene}:${hgvsc} ${hgvsp} ${(authorisedFlag && pmClass) ? pmClass : 'NotAuth'} ${clinContext?clinContext:'No context'}"
    }
}
