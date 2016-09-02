/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

class VarZygosityTagLib
{
    def varZygosity =
    {
        attr ->
            if ( attr.var )
            {
                SeqVariant sv = attr.var
                def zyg = ""
                if ( sv.seqSample?.panel?.panelGroup == 'Fluidigm Germline'  )
                {
                    zyg = 'Unknown'

                    def af = sv.varFreq

                    //  Homozygous variant
                    //
                    if ( af > 80 ) zyg = 'Hom'

                    //  Heterozygous variant
                    //
                    if ( 20 < af && af < 65 ) zyg = 'Het'
                }

                //  Warning colour (white on red) if Germline but not clearly Het/Hom
                //
                if ( zyg == 'Unknown' )
                {
                    def fg = '#ffffff'
                    def bg = '#ff0000'
                    out << """<noop style="color: ${fg}; background-color: ${bg}">${zyg}</noop>"""
                    return
                }

                //  Germline Zygosity
                //
                out << """${zyg}"""
            }
    }
}
