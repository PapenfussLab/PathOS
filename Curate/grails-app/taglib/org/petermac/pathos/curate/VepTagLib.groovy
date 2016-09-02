/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.pathos.curate

import org.petermac.pathos.pipeline.UrlLink

class VepTagLib
{
    def vepUrl =
    {
        attr ->
            if ( attr.seqSample )
            {
                def sam = attr.seqSample
                def det = attr.detail       // display details
                def lbl = det ? 'VEP Details' : 'VEP Summary'
                def url = UrlLink.vep( sam.seqrun.seqrun, sam.sampleName, det )
                out << """<a href="${url}" target="_blank">${lbl}</a>"""
            }
    }
}
