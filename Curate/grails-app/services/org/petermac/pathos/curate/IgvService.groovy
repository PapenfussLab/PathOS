package org.petermac.pathos.curate

import groovy.text.GStringTemplateEngine
import org.petermac.pathos.pipeline.UrlLink
import org.petermac.util.Locator

/**
 * service for generating templates for igv session files
 */
class IgvService {
    def loc = Locator.instance

    /**
     * construct an IGV GStringTemplateEngine template, renderign an XML IGV session file for a given SeqSample
     * @param seqSample seqsample sample to render for
     * @return
     */
    String constructIgvTemplate(SeqSample seqSample) {
        String manifest = seqSample.panel?.manifest?: ""
        String dataUrl = UrlLink.dataUrl(seqSample.seqrun.seqrun,seqSample.sampleName)

        //def url = UrlLink.igv( sv.seqSample.seqrun.seqrun, sv.sampleName, sv.chr + ':' + sv.pos )

        def dataServer 	= "${loc.dataServer}"
        def igv_template= "${loc.pathos_home}/etc/IGV_Session_Template.xml"
        def engine  	= new GStringTemplateEngine()
        def template 	= new File(igv_template)
        def binding 	= [ hostURL: dataServer, panelDir: "Panels/${manifest}", sampleDir: "Pathology/${loc.samBase}/${seqSample.seqrun.seqrun}/${seqSample.sampleName}", sample: seqSample.sampleName ]

        //  add <pos>?
        def session 	= engine.createTemplate(template).make(binding).toString()
        //redirect( url: url )
        return session.toString()
    }
}
