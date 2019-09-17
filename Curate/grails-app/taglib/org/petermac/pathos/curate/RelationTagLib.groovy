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
                    for ( rs in rel.samples() )
                    {
                        // don't display the sample itself
                        if(rs.id == ss.id) continue

                        // dont display seqrun if seqrun is the same
                        //
                        String seqrun = ''
                        if ( rs.seqrun.id != ss.seqrun.id )
                        {
                            seqrun = rs.seqrun.seqrun + ':'
                            if ( seqrun =~ /\d{6}/ ) seqrun = rs.seqrun.seqrun.substring(0,6) + ':'  // display only date of seqrun
                        }
                        out <<  "<li>${g.link( action: "show", controller: "seqRelation", id: rel.id, "${rel.relation}")} : ${g.link( action: "svlist", controller: "seqVariant", id: rs.id, "${seqrun}${rs.sampleName}")} <a id='relationLink-${rs.id}' href='#none' onclick=\"addToIGV('${rs.sampleName}','${UrlLink.dataUrl(rs.seqrun.toString(),rs.sampleName)}', '${rs.id}')\">Load into IGV.js</a></li>"
                    }
                }
            }
    }
}
