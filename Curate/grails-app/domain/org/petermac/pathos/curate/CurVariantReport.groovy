package org.petermac.pathos.curate


import grails.persistence.Entity

/**
 * Object to record the state of a CurVariant when it is reported
 * DKGM 13-April-2017
 */

@Entity
class CurVariantReport
{
    SeqSampleReport     seqSampleReport     // Link to the seqSample report that this belongs to
    CurVariant          curVariant          // Link to the original curvariant

// Information to be printed to a report
// Editable by an authorised user before the report is published
    String	sample
    String	gene
    String	refseq
    String	hgvsc
    String	hgvsp
    String	refseqNP
    String	aaChange
    String	varreaddepth
    String	totalreaddepth
    String	afpct
    String	exon
    String	pmClass
    String	ampClass
    String	clinicalSignificance
    String	mut
    String	genedesc

    static mapping =
        {
            mut             (type: 'text')
            genedesc        (type: 'text')
        }

    static      constraints =
        {
            seqSampleReport (nullable: false)
            curVariant      (nullable: true)
            sample	    	(nullable: true)
            gene	    	(nullable: true)
            refseq	    	(nullable: true)
            hgvsc	    	(nullable: true)
            hgvsp		    (nullable: true)
            refseqNP        (nullable: true)
            aaChange        (nullable: true)
            varreaddepth	(nullable: true)
            totalreaddepth	(nullable: true)
            afpct	    	(nullable: true)
            exon	    	(nullable: true)
            pmClass	    	(nullable: true)
            ampClass		(nullable: true)
            clinicalSignificance	(nullable: true)
            mut		        (nullable: true)
            genedesc		(nullable: true)
        }

    String	toString()
    {
        "${hgvsc}"
    }
}
