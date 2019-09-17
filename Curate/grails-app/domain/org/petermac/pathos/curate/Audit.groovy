/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class Audit
{
    String  category            // pipeline, lims(clarity), record(detente), curation, database
    String  seqrun              // key: seqrun
    String  variant             // key: variant
    String  sample              // key: sample
    String  task                // sequence, align, varcall, annotate, register, report, dbload, dbmerge
    Date    complete            // task complete date
    Integer elapsed             // elapsed time in minutes
    String  software            // software package used VarScan, Primal, BWA, Clarity, Detente, PathOS
    String  swVersion           // software version
    String  username            // free form text field (conforms to software user formats)
    String  description         // free form details eg command args

    static mapping =
            {
                description     ( type: 'text' )
            }

    static constraints =
            {
                category( inList: [ 'pipeline', 'lims', 'record', 'curation', 'database', 'report', 'transcript', 'context', 'masking', 'unknown'] )
                seqrun( nullable: true )
                variant( maxSize: 500, nullable: true )
                sample( nullable: true )
                task( blank: false, nullable: false)
                complete( nullable: false)
                elapsed( nullable: true )
                software( nullable: true )
                swVersion( nullable: true )
                username( blank: false, nullable: false)
                description( nullable: true )
            }
}
