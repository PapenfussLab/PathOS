/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import org.petermac.pathos.pipeline.UrlLink

class CosmicTagLib
{
    def cosmicUrl =
    {
        attr ->
            if ( attr.cosmic )
            {
                def url = UrlLink.cosmic( attr.cosmic )
                out << """<a href="${url}" target="_blank">COSM${attr.cosmic}</a>"""
            }
    }
}
