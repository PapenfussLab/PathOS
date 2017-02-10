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
                def ampliconPanelStats = AlignStats.withCriteria
                        {
                            eq("seqrun", sr.seqrun)
                            not { ilike('panelName', 'Pathology_hyb%') }
                        }
                if (ampliconPanelStats) {

                    out << seqrunAmpliconQC( sr.seqrun)
                }

                def hybridPanelStats = AlignStats.withCriteria
                        {
                            eq("seqrun", sr.seqrun)
                            ilike('panelName', 'Pathology_hyb%')
                        }
                if (hybridPanelStats) {

                    out << seqrunHybridQC( sr.seqrun)
                }


            }
    }

    def seqrunAmpliconQC( sr )
    {

        def rout = ''
            if ( sr )
            {
                //  Collect stats for each panel/sample in the seqrun (SQL group by)
                //
                def summarySample = AlignStats.withCriteria
                {
                    eq( "seqrun",  sr )
                    not { ilike( 'sampleName', 'NTC%' ) }
                    not { ilike( 'amplicon',   'Off_%') } //todo not ilike panel hybrid
                    not { ilike( 'panelName',   'Pathology_hyb%') }

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
                    rout += """<table border="1"><thead><tr>"""

                    //  Table headers
                    //
                    rout += "<th>Panel</th>"
                    rout += "<th>Samples</th>"
                    rout += "<th>Total Reads</th>"
                    rout += "<th>Mean Amplicon Reads</th>"
                    rout += "<th>Unmapped</th>"
                    rout += "<th>Unmapped %</th>"
                    rout += "<th>Amplicons</th>"
                    rout += "<th>No of Amplicons > 20% Mean</th>"
                    rout += "<th>% of Amplicons > 20% Mean</th>"

                    rout += "</tr></thead>"

                    //  output stats for each panel as a table
                    //
                    for ( Map panel in panels )
                    {
                        rout += "<tr>"

                        // Panel
                        rout += outFld(panel.panel as String)

                        // No Samples
                        rout += outFld(panel.samples as int)

                        // Total reads
                        rout += outFld(panel.totreads as int)

                        //  Average reads
                        Double avgReads = (panel.sumreads as Double) / (panel.amplicons as Double)
                        rout += outFld(avgReads)

                        // Unmapped
                        rout += outFld(panel.unmapped as int)

                        // Unmapped %
                        Double unmappct = 100.0 * (panel.unmapped as Double) / (panel.totreads as Double)
                        rout += outFld(unmappct)

                        //  No Amplicons
                        rout += outFld( panel.amplicons as int )

                        //  Good amplicons
                        Double avgGoodAmp = (panel.goodamp as Double) / (panel.samples as Double)
                        rout += outFld(avgGoodAmp)

                        //  Amplicon %
                        Double amppct = 100.0 * avgGoodAmp / (panel.amplicons as Double)
                        rout += outFld(amppct)

                        rout += "</tr>"
                    }

                    rout += "</table>"
                    return rout
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

    def getMedian(ArrayList list)
    {
        Arrays.sort(list);
        Integer n = list.size();
        if (n == 1) return list[0]

        if( n % 2 == 0) {
            return list[ (n/2 as Integer) ] + list[ (((n/2) - 1) as Integer) ] / 2
        } else {
            return list[ (((n-1) / 2) as Integer) ]
        }
    }

    def seqrunHybridQC( sr )
            {
                def rout = ''
                    if ( sr )
                    {
                        //  Collect stats for each panel/sample in the seqrun (SQL group by)
                        //
                        def summarySample = AlignStats.withCriteria
                                {
                                    eq( "seqrun",  sr )
                                    not { ilike( 'sampleName', 'NTC%' ) }
                                    ilike( 'amplicon',   'SUMMARY')
                                    ilike( 'panelName', 'Pathology_hyb%')

                                    projections
                                            {
                                                groupProperty("panelName") //0
                                                groupProperty("sampleName")
                                                property("sampleStats")
                                            }
                                }


                        def allPanelsMap = [:]

                        //  Set up panel map
                        //
                        for ( sample in summarySample ) {
                            def panel = sample[0]

                            //we do NOT store percents, just values
                            def thisPnl =   [
                                    panel:      panel,
                                    totreads:   0,
                                    samples: 0,
                                    medianReads: 0,           //median = readsList.size() %2 != 0 ? values[ readsList.size()  / 2 ] : (values[ readsList.size() / 2] + values[ (readsList.size() -1 ) / 2 ]) /2  --- recalculated EACH TIME
                                    readsMapping:   0,
                                    mappedReadsDuplicate:   0,  //a percent
                                    readsOnTarget:    0,
                                    targetBasesOver100: 0,
                                    //targetBasesOver500: 0, we do NOT store
                                    readsList: [],
                            ]
                            allPanelsMap[panel] = thisPnl
                        }

                        //  populate it
                        //
                        for ( sample in summarySample ) {
                            def panel = sample[0]

                            //  cast our pipe-delimited string of stats key:val pairs into a map
                            def sstats = sample[2].tokenize("|")
                            if (sstats.size() > 1) {

                                println sstats
                                def statsmap = new HashMap<String,String>()
                                for ( entry in sstats) {
                                    //println "split " + entry.tokenize(":") + " " + entry.tokenize(":").size()
                                    if (entry.tokenize(":").size()  == 2 ) statsmap[ entry.tokenize(":")[0] ] = entry.tokenize(":")[1]
                                }

                                //todo try cratch blocks
                                //println statsmap

                                def readsMapping = statsmap['Mapped reads']
                                def mappedReadsDuplicate = ( (statsmap['% Mapped reads duplicates'] as Double) / 100.0) * (statsmap['Mapped reads'] as Double)
                                def readsOnTarget = ( (statsmap['% Reads OnTarget'] as Double ) / 100.0) * (statsmap['Total reads'] as Double)
                                def targetBasesOver100 = ( (statsmap['% Target bases >=100-fold Coverage'] as Double)   ) //* (statsmap['Total reads'] as Double)


                                allPanelsMap[panel].readsMapping += readsMapping as Integer
                                allPanelsMap[panel].mappedReadsDuplicate += mappedReadsDuplicate as Integer
                                allPanelsMap[panel].readsOnTarget += readsOnTarget as Integer
                                allPanelsMap[panel].totreads += statsmap['Total reads'] as Integer
                                allPanelsMap[panel].readsList.add( statsmap['Total reads'] as Integer )
                                allPanelsMap[panel].targetBasesOver100 =+ targetBasesOver100 as Integer
                                allPanelsMap[panel].samples++
                                allPanelsMap[panel].medianReads = getMedian(allPanelsMap[panel].readsList)//readsList.size() %2 != 0 ? readsList[ readsList.size()  / 2 ] : (readsList[ (readsList.size() / 2) - 1 ] + readsList[ readsList.size() / 2 ]) / 2.0
                            }

                        }


                        //  output table if we have alignstats for panels (one row per panel)
                        //
                        if ( allPanelsMap )
                        {
                            rout += """<table border="1"><thead><tr>"""

                            //  Table headers
                            //
                            rout += "<th>Panel</th>"
                            rout += "<th>Samples</th>"
                            rout += "<th>Median Reads per Sample</th>"
                            rout += "<th>Mean Reads per Sample</th>"
                            rout += "<th>% Reads Mapping</th>"
                            rout += "<th>% Mapped Reads Duplicate</th>"
                            rout += "<th>% Reads On Target</th>"
                            rout += "<th>Avg % target Bases >100-fold Coverage</th>"

                            rout += "</tr></thead>"

                            //  output stats for each panel as a table. CALC PERCENTS HERE
                            //
                            for(Map.Entry<String,Map> entry: allPanelsMap.entrySet())
                            {
                                String panelname = entry.getKey()
                                Map panel = entry.getValue()

                                rout += "<tr>"

                                // Panel
                                rout += outFld(panel.panel as String)

                                // No Samples
                                rout += outFld(panel.samples as int)

                                // Median Reads per Sample
                                rout += outFld(panel.medianReads as int)

                                // Mean Reads per sample
                                rout += outFld( (panel.totreads as Double) / (panel.samples as Double))
                                // % Reads Mapping

                                rout += outFld( 100.0 * (panel.readsMapping as Double) / (panel.totreads as Double ) )
                                // % Mapped Reads Duplicate

                                rout += outFld( 100.0 * (panel.mappedReadsDuplicate as Double) / (panel.totreads as Double) )

                                // % Reads on Target
                                rout += outFld( 100.0 * (panel.readsOnTarget as Double) / (panel.totreads as Double) )


                                // % Target bases > 100 avg
                                //todo possibly the total field is total bass? ask
                                rout += outFld( (panel.targetBasesOver100 as Double) / (panel.samples as Double ) )

                                rout += "</tr>"
                            }

                            rout += "</table>"
                        }
                    }
                return rout
            }
    


}
