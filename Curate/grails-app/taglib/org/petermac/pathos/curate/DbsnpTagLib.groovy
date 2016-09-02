/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import org.petermac.pathos.pipeline.UrlLink

class DbsnpTagLib
{
    def dbsnpUrl =
    {
        attr ->
            if ( attr.dbsnp )
            {
                def url = UrlLink.dbsnp( attr.dbsnp )
                out << """<a href="${url}" target="_blank">rs${attr.dbsnp}</a>"""
            }
    }
}
