/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.pathos.curate

import org.petermac.pathos.pipeline.UrlLink

class PubmedTagLib
{
    def pubmedUrl =
    {
        attr ->
            if ( attr.pubmed )
            {
                def url = UrlLink.pubmed( attr.pubmed )
                out << """<a href="${url}" target="_blank">PMC${attr.pubmed}</a>"""
            }
    }
}
