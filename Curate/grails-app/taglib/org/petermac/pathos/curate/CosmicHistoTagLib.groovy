/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.pathos.curate

import org.petermac.pathos.pipeline.UrlLink

class CosmicHistoTagLib
{
    def cosmicHistoUrl =
    {
        attr ->
            if ( attr.gene && attr.hgvsp )
            {
                def url = UrlLink.histogram( attr.gene, attr.hgvsp )
                out << """<a href="${url}" target="_blank">Mut. Histogram</a>"""
            }
    }
}
