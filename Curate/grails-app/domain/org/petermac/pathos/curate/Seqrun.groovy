/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class Seqrun implements Taggable
{
    String	    seqrun
    Date	    runDate
    String	    platform
    String	    sepe
    String	    library
    String      experiment
    String      scanner                     // machine
    String      readlen                     // read length - MiSeq ~150bp
    AuthUser	authorised
    Boolean     authorisedFlag = false
    Boolean     passfailFlag   = false
    String      qcComment
    String      panelList
    static	hasMany = [ seqSamples: SeqSample, tags: Tag ]

    static  searchable =
    {
        only = [ 'seqrun', 'experiment', 'library', 'scanner', 'authorised', 'tags' ]
        tags component: true
    }

    static mapping =
    {
        panelList   formula: '(select GROUP_CONCAT(DISTINCT panel.manifest) from panel left join seq_sample on seq_sample.panel_id=panel.id left join seqrun on seq_sample.seqrun_id = seqrun.id WHERE panel.manifest IS NOT NULL AND panel.manifest != "null" AND seqrun.id=id)'
        sort seqrun: "desc"
    }

    static constraints =
    {
        seqrun( unique: true )
        runDate()
        platform()
        sepe()
        readlen()
        library()
        experiment()
        scanner()
        authorisedFlag()
        passfailFlag()
        authorised( nullable: true )
        panelList( nullable: true )
        qcComment( maxSize: 500, nullable: true )
    }

    String	toString()
    {
        seqrun
    }


}
