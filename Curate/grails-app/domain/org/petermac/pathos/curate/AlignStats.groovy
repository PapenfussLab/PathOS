/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class AlignStats
{
    String	    panelName               // convenience property - can be inferred from sample
    String	    amplicon                // amplicon name or 'SUMMARY' for alignment summary record
    String	    seqrun                  // convenience property - can be inferred from sample
    String	    sampleName              // convenience property - can be inferred from sample
    String	    location
    int 	    readsout
    int 	    totreads                // only in SUMMARY record
    int 	    unmapped                // only in SUMMARY record
    int 	    goodamp                 // only in SUMMARY record
    String      sampleStats             // only in SUMMARY record

    static constraints =
    {
        seqrun()
        sampleName()
        panelName()
        amplicon()
        location(nullable: true)
        readsout()
        totreads()
        unmapped()
        goodamp()
        sampleStats( nullable: true )
    }
    //  Indexes on sampleName, seqrun
    //
    static      mapping =
            {
                sampleName  index: 'align_stats_idx1'
                seqrun      index: 'align_stats_idx2'
                panelName   index: 'align_stats_idx3'
                amplicon    index: 'align_stats_idx4'
                sampleStats (type: 'text')
            }

    String	toString()
    {
        "${sampleName}:${amplicon}"
    }
}
