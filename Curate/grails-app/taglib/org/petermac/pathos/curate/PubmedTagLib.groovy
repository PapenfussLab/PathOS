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
    def utilService

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

            PMIDs.each { pmid ->
                result += stringifyPmid(pmid)
            }
        }
        out << result
    }

    private String stringifyPmid(def pmid) {
        String result

        Pubmed article = Pubmed.findByPmid(pmid)
        if (article) {
            result = """<li><h3>${article.title}</h3>
                    <p>${article.fetchCitation()}</p>
                    <a href="${utilService.context()}/pubmed?pmid=${pmid}">[PMID: ${pmid}]</a></li>"""
        } else {
            result = """<li id="pubmed-${pmid}">Article [PMID: ${pmid}] not found in PathOS database, <a class="newPubmedArticle" href="#lookUpNewArticle" onclick="PathOS.pubmed.lookUpNewArticle(${pmid})">click here to find it.</a></li>"""
        }

        return result
    }

}















