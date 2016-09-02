/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import org.petermac.pathos.pipeline.UrlLink

class UcscTagLib
{
    def ucscUrl =
    {
        attr ->
            if ( attr.chr && attr.pos )
            {
                def url = UrlLink.ucsc( attr.chr + ':' + attr.pos )
                out << """<a href="${url}" target="_blank">UCSC</a>"""
            }
    }
}
