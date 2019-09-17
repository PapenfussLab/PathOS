/*
 * Copyright (c) 2018. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: andreiseleznev
 */

package org.petermac.pathos.curate

import grails.converters.XML

/**
 * Created by andreiseleznev on 11/9/18.
 */
class IgvSessionController {

    /**
     * render igv session XML file from igvService
     */
    def sessionXml() {
        //  url mapping gives params: seqrunName, sampleName, optionally seqsampleId
        if (!params.seqrunName || !params.sampleName) {
            log.error("sessionXML caleld in IGV controller but no params")
        }

        try {
            Seqrun sr = Seqrun.findBySeqrun(params.seqrunName)
            SeqSample ss = SeqSample.findBySeqrunAndSampleName(sr, params.sampleName)
            def igv = new IgvService()

            //  probably have to set header etc
            render(text: igv.constructIgvTemplate(ss), contentType: "text/xml")
        } catch (NullPointerException e) {
            //  no such sr or ss
            render ""
        }
    }
}
