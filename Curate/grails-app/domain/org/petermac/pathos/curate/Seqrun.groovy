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
    transient String panelList = ""

    static	hasMany = [ seqSamples: SeqSample, tags: Tag ]

    static  searchable =
    {
        except = [ 'seqSamples' ]
        tags component: true
    }

    static mapping =
    {
        sort seqrun: "desc"
    }

    static constraints =
    {
        seqrun( unique: true, validator: { val -> if (val.contains(' ')) return 'value.hasASpace' } )
        runDate()
        platform( nullable: true )
        sepe( nullable: true )
        readlen( nullable: true )
        library( nullable: true )
        experiment( nullable: true )
        scanner( nullable: true )
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

    String getPanelList() {
        this.seqSamples.collect{ it?.panel?.manifest }.unique().sort().join(", ")
    }


    /**
     * get most common panel for a seqrun
     * @param sr
     * @return
     */
    Panel mostCommonPanel() {
        String panels = this?.panelList

        ArrayList panelList = panels?.contains(',')? panels.tokenize(','):[panels]

        String largestPanel = null
        int largestPanelCount = 0


        for(p in panelList) {
            def ss = SeqSample.findAllBySeqrunAndPanel(this,Panel.findByManifest(p))
            if(ss.size() > largestPanelCount ) {
                largestPanel = p

                largestPanelCount = ss.size()
            }
        }

        if (!largestPanel) {
            return null
        }

        return Panel.findByManifest(largestPanel)
    }
}
