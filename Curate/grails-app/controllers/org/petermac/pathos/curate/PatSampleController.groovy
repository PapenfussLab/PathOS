/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import org.petermac.util.HollyUtil

class PatSampleController
{
    static scaffold = true

    def show()
    {
        if (params.sample)
        {
            params.sampleName = params.sample
        }

        def thisSample  = null
        def sampleparam

        if ( params.id )
        {
            def id      = params.id
            thisSample  = PatSample.get(id)
            sampleparam = id
        }
        else if ( params.sampleName )
        {
            // samplename is unqiue
            //
            thisSample  = PatSample.findBySample(params.sampleName)
            sampleparam = params.sampleName
        }
        else
        {
            sampleparam = null
        }

        if ( ! thisSample )
        {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'sample.label', default: 'sample'), sampleparam])
            redirect( action: "list" )
            return
        }

        [ sampleInstance: thisSample ]
    }

    /**
     * Returns true if Sample was found in Holly and loaded, False otherwise
     * @param sampleName
     */
    static boolean loadHollyData( String sampleName )
    {
        def holly       = new HollyUtil()
        def hollySample = holly.getSample( sampleName )

        // check for success
        //
        if ( ! hollySample )
            return false

        PatSample ps = PatSample.findBySample( sampleName )

        ps.hAndE            = hollySample.hAndE
        ps.slideComments    = hollySample.slideComments
        ps.slideTech        = hollySample.slideTech
        ps.retSite          = hollySample.retSite
        ps.repMorphology    = hollySample.repMorphology
        ps.methylGreen      = hollySample.methylGreen
        ps.pathMorphology   = hollySample.pathMorphology
        ps.tumourPct        = hollySample.tumourPct
        ps.pathComments     = hollySample.pathComments
        ps.pathologist      = hollySample.pathologist
        ps.hollyLastUpdated = hollySample.lastUpdated

        ps.save( flush: true )

        return true
    }
}
