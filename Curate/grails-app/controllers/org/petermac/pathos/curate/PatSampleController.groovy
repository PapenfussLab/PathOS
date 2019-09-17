/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

class PatSampleController
{
    static scaffold = true

    def show()
    {
        if ( params.sample ) {
            params.sampleName = params.sample
        } else if ( params.seqSample ){
            params.id = SeqSample.get(params.seqSample)?.patSample?.id
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

}


