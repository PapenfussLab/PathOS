/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import groovy.util.logging.Log4j

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Provide summarising stats for Seqrun and SeqSample QC pages
 *
 * User: doig ken
 * Date: 11/11/2013
 */

import org.petermac.util.Statistics

@Log4j
class StatsService
{

    /**
     * Get the panels for a run
     *
     * @param sr    Seqrun to query
     * @return      List of panel names
     */
    static List panels( Seqrun sr )
    {
        //  Collect stats for each panel in the seqrun (SQL group by)
        //
        def panels = AlignStats.withCriteria
        {
            eq( "seqrun", sr.seqrun )

            projections
            {
                groupProperty("panelName")
            }
        }

        return panels
    }

    /**
     * Find all NTC amplcon stats with more than numreads reads
     *
     * @param sr        Seqrun object
     * @param numreads  Number of reads threshold
     * @return          List of AlignStats rows
     */
    static List ntcAmplicons( Seqrun sr, int numreads )
    {
        //  Collect stats for each NTC Amplicon in the seqrun (SQL group by)
        //
        def amps = AlignStats.withCriteria
        {
            eq( 'seqrun', sr.seqrun )
            ilike( 'sampleName', 'NTC%')
            ge( 'readsout', numreads )
            not { eq( 'amplicon', 'SUMMARY') }
        }

        return amps
    }

    /**
     * Find all Low read amplcons for a sample
     *
     * @param ss        SeqSample object
     * @param numreads  Number of reads threshold
     * @return          List of AlignStats rows
     */
    static List lowAmplicons( SeqSample ss, int numreads )
    {
        //  Collect stats for each sample in the seqrun (SQL group by)
        //
        def amps = AlignStats.withCriteria
        {
            eq( 'seqrun', ss.seqrun.seqrun )
            eq( 'sampleName', ss.sampleName )
            le( 'readsout', numreads )
            not { ilike( 'amplicon', 'Off_%') }
            not { eq(    'amplicon', 'SUMMARY') }
        }

        return amps
    }

    /**
     * Gat Map of all summary stats for a sample
     *
     * @param ss        SeqSample object
     * @return          List of Summary rows keyed by title
     */
    static Map sampleSummary( SeqSample ss )
    {
        //  Collect stats for each sample in the seqrun (SQL group by)
        //
        def amps = AlignStats.withCriteria
        {
            eq( 'seqrun', ss.seqrun.seqrun )
            eq( 'sampleName', ss.sampleName )
            eq( 'amplicon', 'SUMMARY')
        }

        assert amps.size() <= 1 : "only one summary per sample"
        if ( ! amps ) return null

        Map attr = [:]
        List rows = amps[0].sampleStats?.replace('|','#')?.split('#')
        for ( String row in rows )
        {
            def parts = row.split( ':')
            assert parts.size() == 2
            attr[parts[0]] = parts[1]
        }

        return attr
    }

    /**
     * Extract percent good amplicons from align stats Todo: fix aligner stats
     *
     * @param ss    SeqSample to query
     * @return      percent good amplicons
     */
    static Double ampPct( SeqSample ss )
    {
        Map attr = sampleSummary( ss )
        if ( ! attr ) return null

        def ar = attr['No. Amplicons > 0.2 x ReadsOut']
        assert ar : 'expecting stats attribute'
        def match = (ar =~ /\d ([\d\.]+)%/)
        if ( ! match ) return null

        return match[0][1] as Double
    }

    /**
     * Extract mean no of reads from align stats Todo: fix aligner stats
     *
     * @param ss    SeqSample to query
     * @return      mean reads out
     */
    static Integer ampReads( SeqSample ss )
    {
        Map attr = sampleSummary( ss )
        if ( ! attr ) return null

        def ar = attr['Min Mean Max ReadsOut']
        assert ar : 'expecting stats attribute'
        def match = (ar =~ /\d+ (\d+) \d+/)
        if ( ! match ) return null

        return match[0][1] as Integer
    }

    /**
     * Get read stats for a Panel upto and finishing at this seqrun
     * Todo: the panels are grouped by first 8 characters. This is a hack until panel group is added to AlignStats
     *
     * @param srname    Seqrun name to finish on
     * @param panel     Panel to report on
     * @return          Map of stats, one entry per seqrun containing the panel
     */
    static Map getReadStats( String srname, String panel )
    {
        //  Collect stats for each panel in the seqrun (SQL group by)
        //

        def panelLike = panel + '%'
        if (panel.length() >= 8) {
            panelLike = panel.substring(0,8) + '%'
        }
        def reads = AlignStats.withCriteria
        {
            and
            {
                le( 'seqrun', srname )
                ilike( 'panelName', panelLike )
                eq( 'amplicon', 'SUMMARY')                   // Summary records only
                not { ilike('sampleName', 'NTC%') }          // ignore so stats aren't biased
            }

            projections
            {
                groupProperty("seqrun")
                groupProperty("sampleName")
                max("totreads")
            }
        }

        //  Sum the reads by seqrun over all samples
        //
        Integer max = 0
        Map  res   = [:]
        Map  upper = [:]
        Map  lower = [:]

        try
        {
            for ( sample in reads )
            {
                def seqrun = (sample[0] as String).substring(0,10)
                if ( ! res[seqrun] ) res[seqrun] = 0
                res[seqrun] += sample[2]
                if ( res[seqrun] > max ) max = res[seqrun] as Integer
            }
        }
        catch ( Exception e )
        {
            log.error( "Number conversion error in getReadStats() reads=${reads} err=${e}")
        }

        //  Calculate mean and sd for all samples as they are read
        //  Only use last 10 runs for stats eg a sliding window of 10
        //
        def count  = 0
        def list = []
        def q = list as Queue
        for ( seqrun in res.keySet())
        {
            if ( count < 10 )
            {
                q << res[seqrun]
                ++count
            }
            else
            {
                q.poll()
                q << res[seqrun]
            }
            Double mean = Statistics.mean(q)
            Double sd   = Statistics.stddev(q)
            upper[seqrun] = mean + 2 * sd
            lower[seqrun] = mean - 2 * sd
        }

        //  return a map of lists for each run
        //
        return  [
                    labels: res.keySet(),
                    data:   res.values().toList(),
                    upper:  upper.values().toList(),
                    lower:  lower.values().toList()
                ]
    }

    /**
     * Format historical seqrun data for line chart
     *
     * @param sr
     * @param panel
     * @return
     */
    static String seqrunReadChart( Seqrun sr, String panel )
    {
        Map m = getReadStats( sr.seqrun, panel)

        //  Format data for Google Charts
        //
        // """[["dataset","label 1","label 2","label 3"],["data 1",13,0,100],["data 2",0,0,100],["data 3",0,23,100]]"""
        //
        List labels = [ '+2 sd', 'Total panel reads', '-2 sd' ]
        String ds = """[["data","${labels[0]}","${labels[1]}","${labels[2]}"]"""

        //  Add each data row
        //
        int i = 0
        for ( label in m.labels )
        {
            List val = [m.upper.get(i),m.data.get(i),m.lower.get(i)]
            ++i
            ds += """,["${label}",${val[0]},${val[1]},${val[2]}]"""
        }

        return ds + ']'
    }

    /**
     * Get read stats for samples for this seqrun
     *
     * @param srname    Seqrun name to finish on
     * @param panel     Panel to report on
     * @return          Map of stats, one entry per sample containing the panel
     */
    static Map getSampleStats( String srname, String panel )
    {
        //  Collect stats for each panel in the seqrun (SQL group by)
        //
        def samples = AlignStats.withCriteria
        {
            and
            {
                eq( 'seqrun', srname )
                eq( 'panelName', panel )
                not { eq( 'amplicon', 'SUMMARY') }
            }

            projections
            {
                groupProperty("sampleName")
                sum("readsout")
            }
        }

        //  Sum the reads by seqrun over all samples
        //
        int  tot = 0
        for ( sample in samples )
        {
            tot += sample[1] as int
        }

        Double avg   = samples.size() ? tot / samples.size() : 0.0
        Map  average = [:]
        Map  upper   = [:]
        Map  lower   = [:]

        for ( sample in samples )
        {
            def sam = sample[0] as String
            def dat = sample[1] as int
            if ( dat > avg )
            {
                average[sam] = avg
                upper[sam]   = dat - avg
                lower[sam]   = 0
            }
            else
            {
                average[sam] = 0
                upper[sam]   = 0
                lower[sam]   = dat
            }
        }

        //  return a map of lists for each run
        //
        return      [
                        labels: average.keySet(),
                        upper:  upper.values().toList(),
                        avg:    average.values().toList(),
                        lower:  lower.values().toList()
                    ]
    }

    /**
     * Format Run sample data for bar chart
     *
     * @param sr
     * @param panel
     * @return
     */
    static String seqrunSampleChart( Seqrun sr, String panel )
    {
        Map m = getSampleStats( sr.seqrun, panel)

        //  Format data for Google Charts
        //
        List labels = ['Average','Below Average','Above Average']
        String ds = """[["data","${labels[0]}","${labels[1]}","${labels[2]}"]"""

        //  Add each data row
        //
        int i = 0
        for ( label in m.labels )
        {
            List val = [m.avg.get(i),m.lower.get(i),m.upper.get(i)]
            ++i
            ds += """,["${label}",${val[0]},${val[1]},${val[2]}]"""
        }

        return ds + ']'
    }

    /**
     * Get read stats for a Amplicons for this seqrun
     *
     * @param srname    Seqrun name to finish on
     * @param panel     Panel to report on
     * @return          Map of stats, one entry per sample containing the panel
     */
    static Map getAmpliconStats( String srname, String panel )
    {
        //  Collect stats for each panel in the seqrun (SQL group by)
        //
        def amplicons = AlignStats.withCriteria
        {
            and
            {
                eq( 'seqrun', srname )
                eq( 'panelName',  panel )
                not { like('amplicon', "Off%") }
                not { ilike('sampleName', 'NTC%')  }
                not { eq( 'amplicon', 'SUMMARY') }

            }

            projections
            {
                groupProperty("amplicon")
                groupProperty("sampleName")
                sum("readsout")
            }
            order("readsout")
        }

        //  Bin amplicon read depth
        //
        Map  ok  = [:]
        Map  nak = [:]
        int  binSize = 100
        for ( def i = 0; i < 70; ++i )
        {
            int depth = i * binSize
            int count = amplicons.findAll{ depth <= it[2] && it[2] < depth+binSize }.size()
            if ( depth < 100 )
            {
                ok[depth] = 0
                nak[depth] = count
            }
            else
            {
                ok[depth] = count
                nak[depth] = 0
            }
        }

        //  return a map of lists for each run
        //
        return  [
                    labels: ok.keySet(),
                    ok:     ok.values().toList(),
                    nak:    nak.values().toList()
                ]
    }

    /**
     * Format Run sample data for bar chart
     *
     * @param sr
     * @param panel
     * @return
     */
    static String seqrunAmpliconChart( Seqrun sr, String panel )
    {
        Map m = getAmpliconStats( sr.seqrun, panel)

        //  Format data for Google Charts
        //
        List labels = ['OK','Below 100 reads']
        String ds = """[["data","${labels[0]}","${labels[1]}"]"""

        //  Add each data row
        //
        int i = 0
        for ( label in m.labels )
        {
            List val = [m.ok.get(i),m.nak.get(i)]
            ++i
            ds += """,["${label}",${val[0]},${val[1]}]"""
        }

        return ds + ']'
    }

}
