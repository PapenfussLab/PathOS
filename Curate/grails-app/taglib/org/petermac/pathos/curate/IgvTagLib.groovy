/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import org.petermac.pathos.pipeline.UrlLink
import grails.util.Environment

class IgvTagLib
{
    def igvUrl =
    {
        attr ->
            if ( attr.seqVariant )
            {
                def env = Environment.getCurrentEnvironment().name
                def sv = attr.seqVariant
                def url = UrlLink.igv( sv.seqSample.seqrun.seqrun, sv.sampleName, sv.chr + ':' + sv.pos, env == 'demo' )
                out << """<a href="${url}" target="_blank">IGV</a>"""
            }
    }
}
