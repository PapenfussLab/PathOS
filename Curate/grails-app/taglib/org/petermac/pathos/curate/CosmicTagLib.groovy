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

                out << attr.cosmic.tokenize(",")
                        .collect({ it ->
                    String cosmicID = it.replace("COSM", "").trim()
                    String url = UrlLink.cosmic( cosmicID )
                    return """<a href="${url}" target="_blank">COSM${ cosmicID }</a>"""
                }).join(", ")

            }
    }
}
