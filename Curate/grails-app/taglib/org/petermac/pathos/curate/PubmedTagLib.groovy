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

    def displayPMIDs =
    { attr ->
        def result = ""
        if ( attr.cv ) {
            CurVariant cv = CurVariant.get(attr.cv)

            ArrayList<Long> PMIDs = PubmedService.allPMIDsFromCurVariant( cv )

            PMIDs.each {
                Pubmed article = Pubmed.findByPmid(it)
                if (article) {
                    result += """<li><h3>${article.title}</h3>${PubmedService.buildCitation(article)} <a href="/PathOS/pubmed?pmid=${it}"><br>[PMID: ${it}]</a></li>"""
                } else {
                    result += """<li>Article [PMID: ${it}] not found in PathOS database, <a href="/PathOS/pubmed?pmid=${it}">click here to add it.</a></li>"""
                }
            }
        }
        out << result
    }
}















