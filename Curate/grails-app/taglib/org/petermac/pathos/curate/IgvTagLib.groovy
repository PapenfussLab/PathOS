/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import org.petermac.pathos.pipeline.UrlLink
import grails.util.Environment

class IgvTagLib
{
    def igvUrl =
    {
        attr ->
            if ( attr.seqVariant )
            {
                def g = new ApplicationTagLib()
                def env = Environment.getCurrentEnvironment().name
                def sv = attr.seqVariant
                String baseLink = g.createLink(controller: 'IgvSession', absolute: 'true', action:'sessionXml')
                def url = UrlLink.igvSessionXMLUrl(baseLink, sv.seqSample.seqrun.seqrun, sv.seqSample.sampleName , sv.chr + ':' + sv.pos )
                out << """<a href="${url}" target="_blank">IGV</a>"""
            }
    }
}
