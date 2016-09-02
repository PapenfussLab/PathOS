/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Generate the QC block for a Seqrun
 * ToDo: Move all the processing into the align stats summarisation
 * Todo: this should be facilitated by the align stats output, not done by mining the data
 *
 * User: doig ken
 * Date: 11/11/2013
 * Time: 11:56 AM
 */

class SeqrunQcTagLib
{
    def seqrunQC =
    {
        attr ->
            def sr = attr.seqrun
            if ( sr )
            {
                //  Collect stats for each panel/sample in the seqrun (SQL group by)
                //
                def summarySample = AlignStats.withCriteria
                {
                    eq( "seqrun",  sr.seqrun )
                    not { ilike( 'sampleName', 'NTC%' ) }
                    not { ilike( 'amplicon',   'Off_%') }

                    projections
                    {
                        groupProperty("panelName")
                        groupProperty("sampleName")
                        max("totreads")
                        max("unmapped")
                        max("goodamp")
                        countDistinct("amplicon")
                        sum("readsout")
                    }
                }

                //  Loop though all samples and accumulate properties in a map
                //  Save the maps (one per panel) in a list
                //
                List<Map> panels = []
                Map pnl = [:]
                for ( sample in summarySample )
                {
                    //  new sample record
                    //
                    def panel = sample[0]
                    if ( panel != pnl.panel )
                    {
                        //  Start storing new panel stats
                        //
                        if ( pnl ) panels << pnl

                        pnl =   [
                                    panel:      panel,
                                    samples:    0,
                                    totreads:   0,
                                    unmapped:   0,
                                    goodamp:    0,
                                    sumreads:   0
                                ]
                    }

                    pnl.samples++
                    pnl.totreads  += sample[2]
                    pnl.unmapped  += sample[3]
                    pnl.goodamp   += sample[4]
                    pnl.amplicons =  sample[5] - 1      // minus allows for SUMMARY record
                    pnl.sumreads  += sample[6]
                }

                //  save last set of panel stats
                //
                if ( pnl ) panels << pnl

                //  output table if we have alignstats for panels (one row per panel)
                //
                if ( panels )
                {
                    out << """<table border="1"><thead><tr>"""

                    //  Table headers
                    //
                    out << "<th>Panel</th>"
                    out << "<th>Samples</th>"
                    out << "<th>Total Reads</th>"
                    out << "<th>Mean Amplicon Reads</th>"
                    out << "<th>Unmapped</th>"
                    out << "<th>Unmapped %</th>"
                    out << "<th>Amplicons</th>"
                    out << "<th>No of Amplicons > 20% Mean</th>"
                    out << "<th>% of Amplicons > 20% Mean</th>"

                    out << "</tr></thead>"

                    //  output stats for each panel as a table
                    //
                    for ( Map panel in panels )
                    {
                        out << "<tr>"

                        // Panel
                        out << outFld(panel.panel as String)

                        // No Samples
                        out << outFld(panel.samples as int)

                        // Total reads
                        out << outFld(panel.totreads as int)

                        //  Average reads
                        Double avgReads = (panel.sumreads as Double) / (panel.amplicons as Double)
                        out << outFld(avgReads)

                        // Unmapped
                        out << outFld(panel.unmapped as int)

                        // Unmapped %
                        Double unmappct = 100.0 * (panel.unmapped as Double) / (panel.totreads as Double)
                        out << outFld(unmappct)

                        //  No Amplicons
                        out << outFld( panel.amplicons as int )

                        //  Good amplicons
                        Double avgGoodAmp = (panel.goodamp as Double) / (panel.samples as Double)
                        out << outFld(avgGoodAmp)

                        //  Amplicon %
                        Double amppct = 100.0 * avgGoodAmp / (panel.amplicons as Double)
                        out << outFld(amppct)

                        out << "</tr>"
                    }

                    out << "</table>"
                }
            }
    }

    String outFld( String val)
    {
        """<td>${val}</td>"""
    }

    String outFld( int val)
    {
        """<td>${formatNumber(number:"${val}", format:"###,###,##0")}</td>"""
    }

    String outFld( Double val)
    {
        if ( val.isNaN() || val.infinite) return "<td>-</td>"
        """<td>${formatNumber(number:"${val}", format:"#,##0.0")}</td>"""
    }
}
