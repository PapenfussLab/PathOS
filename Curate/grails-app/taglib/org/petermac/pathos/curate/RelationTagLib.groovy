/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.pathos.curate

import org.petermac.pathos.pipeline.UrlLink

class RelationTagLib
{
    /**
     * A TagLib to render sample relationships
     */
    def relation =
    {
        attr ->
            if ( attr.sample )
            {
                SeqSample ss = SeqSample.get(attr.sample)

                List shown = [ss.id]
                for ( rel in ss.relations )
                {
                    for ( rs in rel.samples )
                    {
                        if ( rs.id in shown ) continue // ignore repeat samples
                        String seqrun = ''             // dont display if seqrun is the same
                        if ( rs.seqrun.id != ss.seqrun.id ) seqrun = "${rs.seqrun.seqrun.substring(0,6)}:"
                        out <<  "<li>${rel.relation}: ${g.link( action: "svlist", controller: "seqVariant", id: rs.id, "${seqrun}${rs.sampleName}")} <a id='relationLink-${rs.id}' href='#none' onclick=\"addToIGV('${rs.sampleName}','${UrlLink.dataUrl(rs.seqrun.toString(),rs.sampleName)}', '${rs.id}')\">Load into IGV.js</a></li>"
                        shown << rs.id
                    }
                }
            }
    }
}
