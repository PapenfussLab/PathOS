/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.pipeline

import groovy.util.logging.Log4j

/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 2/10/13
 * Time: 11:13 AM
 * To change this template use File | Settings | File Templates.
 */

@Log4j
class SeqrunLims
{
    /**
     * Parse an Illumina Run Parameters file eg runParameters.xml
     *
     * @param   limsPath
     * @return
     */
    static Map parseRunParameters( String limsPath )
    {
        File limsFile = new File( limsPath )

        if ( ! limsFile.exists() || ! limsFile.canRead())
        {
            log.error( "RunParameters file doesn't exist or not readable: ${limsFile.path}" )
            return [:]
        }

        //  Parse file
        //
        def sr = new XmlParser().parse( limsFile )

        return parseRunNode( sr )
    }

    /**
     * Parse the XML tree of an Illumina Run Parameters file
     *
     * @param sr    XML Node of Run Parameters file
     * @return      Map of parsed parameters
     */
    private static Map parseRunNode( Node sr)
    {
        Map params = [:]

        if ( ! sr )
            return params

        params << [ application:    sr.Setup?.ApplicationName?.text() ]

        if ( params.application?.startsWith('MiSeq'))
        {
            //  Seq Run information
            //
            params << [ seqrun:         sr.RunID?.text() ]
            params << [ experiment:     sr.ExperimentName?.text() ]
            params << [ scanner:        sr.ScannerID?.text() ]
            params << [ samplesheet:    sr.SampleSheetName?.text() ]
            params << [ useremail:      sr.CloudUsername?.text()?.toLowerCase() ]
            params << [ readlen:        getReadLen(sr.Reads[0]?.children()) ]
            params << [ readcycles:     getReadCycles(sr.Reads[0]?.children()) ]
        }

        if ( params.application?.startsWith('HiSeq'))
        {
            //  Seq Run information
            //
            params << [ seqrun:         sr.Setup?.RunID?.text() ]
            params << [ experiment:     sr.Setup?.ExperimentName?.text() ]
            params << [ scanner:        sr.Setup?.ScannerID?.text() ]
            params << [ readlen:        getReadLen(sr.Setup?.Reads[0]?.children()) ]
            params << [ readcycles:     getReadCycles(sr.Setup?.Reads[0]?.children()) ]
        }

        return params
    }

    private static String getReadLen( NodeList reads )
    {
        def l = getReadCycles( reads )

        Integer readlen = l[0].NumCycles as Integer
        return (readlen-1).toString()
    }

    /**
     * Parse machine read cycles
     *
     * @param   reads   List of read XML nodes
     * @return          List of Maps of read parameters
     */
    private static List getReadCycles( NodeList reads )
    {
        List l = []
        for( read in reads )
        {
            l << parseReads( read )
        }

        return l
    }

    /**
     * Extract parameters from each read cycle
     *
     * @param read  XML node to parse
     * @return      Map of parameters
     */
    private static Map parseReads( Node read )
    {
        Map m = [:]
        m << [ NumCycles:       read.@NumCycles ]
        m << [ Number:          read.@Number ]
        m << [ IsIndexedRead:   read.@IsIndexedRead ]

        return m
    }

    /**
     * Parsing routine for both HiSeq and Miseq Run parameters
     *
     * @param limsPath  Path to XML files - usually in /pipeline/RunFolder/(MiSeq|HiSeq2000)/<seqrun>/LIMS_<seqrun>.xml
     * @return          Map of parameters in the form
     *                      seqrun      Illumina run ID
     *                      platform    MiSeq or HiSeq
     *                      sepe        SE or PE
     *                      library     library used (chemistry)
     *                      samples     List of samples in run
     *                      [
     *                          sample      Sample name
     *                          laneno      1 for MiSeq 1..8 for HiSeq
     *                          analysis    Sequencing process eg RNASeq or Pathology Amplicon Cancer 2015
     *                          reference   Manifest or Human(HG19)
     *                          useremail   Sample owner email
     *                          username    Sample owner or submitter
     *                      ]
     */
    static Map parseLims( String limsPath )
    {
        File limsFile = new File( limsPath )

        if ( ! limsFile.exists() || ! limsFile.canRead())
        {
            log.error( "LIMS file doesn't exist or not readable: ${limsFile.path}" )
            return [:]
        }

        def sr = new XmlParser().parse( limsFile )

        return parseLimsNode( sr )
    }

    /**
     * Parse the XML tree of a LIMS file
     *
     * @param sr    XML Node of LIMS file
     * @return      Map of parsed parameters
     */
    private static Map parseLimsNode( Node sr)
    {
        Map params = [:]

        //  Parse Nodes
        //
        if ( ! sr )
            return params

        String platform = sr.RunInfo?.@Platform[0]

        switch (platform)
        {
            case 'MiSeq':
                params = parseMiseqLims( sr )
                break
            case 'NextSeq':
            case 'HiSeq':
            case 'HiSeq2000':
            case 'HiSeqRapid':
                params = flattenHiseqParams( parseHiseqLims( sr ))
                break
            default:
                log.error( "Unknown platform type [${platform}]")
        }

        return params
    }

    /**
     * Validate PeterMac MiSeq LIMS XML file
     *
     * @param nodes     XML nodes parsed from  file
     * @return          Map of extracted parameters
     *                      seqrun      Illumina run ID
     *                      platform    MiSeq or HiSeq
     *                      sepe        SE or PE
     *                      library     library used (chemistry)
     *                      samples     List of samples in run
     *                      [
     *                          sample      Sample name
     *                          analysis    Sequencing process eg RNASeq or Pathology Amplicon Cancer 2015
     *                          reference   Manifest or Human(HG19)
     *                          useremail   Sample owner email
     *                          username    Sample owner or submitter
     *                      ]
     */
    private static parseMiseqLims( Node sr )
    {
        Map params = [:]

        //  Seq Run information
        //
        params << [ seqrun:     sr.RunInfo?.@ExperimentName[0] ]
        params << [ platform:   sr.RunInfo?.@Platform[0] ]
        params << [ sepe:       sr.RunInfo?.@SEPE[0] ]
        params << [ library:    sr.Lanes.@LibraryName[0] ]

        NodeList samNodes = sr.Lanes?.Libraries[0]?.children()
        List samples = []

        for( samNode in samNodes )
        {
            Map samMap = [:]

            //  Parse Individual Samples
            //
            samMap << [ sample:     cleanSampleName(samNode.@LibraryName) ]
            samMap << [ analysis:   samNode.@AnalysisType ]
            samMap << [ reference:  samNode.@ReferenceGenome ]
            samMap << [ useremail:  (samNode.@UserEmail)?.toLowerCase() ]
            samMap << [ username:   samNode.@UserName ]
            samMap << [ laneno: "1" ]

            if ( samMap ) samples << samMap
        }


        params << [ samples: samples ]

        return params
    }


    /**
     * Validate PeterMac HiSeq LIMS XML file
     *
     * @param limsPath  Path to LIMS XML file
     * @return          Map of extracted parameters
     *                      seqrun      Illumina run ID
     *                      platform    MiSeq or HiSeq
     *                      sepe        SE or PE
     *                      library     library used (chemistry)
     *                      lanes       List of lanes in run (1..8)
     *                      [
     *                          laneno      1..8
     *                          library     library used in this lane
     *                          samples     List of samples in lane
     *                          [
     *                              sample      Sample name
     *                              analysis    Sequencing process eg RNASeq or Pathology Amplicon Cancer 2015
     *                              reference   Manifest or Human(HG19)
     *                              useremail   Sample owner email
     *                              username    Sample owner or submitter
     *                          ]
     *                      ]
     */
    private static parseHiseqLims( Node sr )
    {
        Map params = [:]

        //  Seq Run information
        //
        params << [ seqrun:     sr.RunInfo?.@ExperimentName[0] ]
        params << [ platform:   sr.RunInfo?.@Platform[0] ]
        params << [ sepe:       sr.RunInfo?.@SEPE[0] ]

        NodeList laneNodes = sr.Lanes[0]?.children()
        List lanes   = []

        for( laneNode in laneNodes )
        {
            Map laneMap = [:]
            laneMap << [ laneno:  laneNode.@LaneNumber  ]
            laneMap << [ library: laneNode.@LibraryName ]

            //  Parse Individual Samples
            //
            NodeList samNodes = laneNode.Libraries[0]?.children()
            List samples = []
            for( samNode in samNodes )
            {
                Map samMap = [:]
                samMap << [ sample:     cleanSampleName(samNode.@LibraryName) ]
                samMap << [ analysis:   samNode.@AnalysisType ]
                samMap << [ reference:  samNode.@ReferenceGenome ]
                samMap << [ useremail:  (samNode.@UserEmail)?.toLowerCase() ]
                samMap << [ username:   samNode.@UserName ]

                if ( samMap ) samples << samMap
            }

            laneMap << [ samples: samples ]
            if ( laneMap ) lanes << laneMap

        }

        params << [ lanes: lanes ]

        return params
    }

    /**
     * Normalise HiSeq parameters to match MiSeq eg flatten lanes
     *
     * @param   params  Map of parsed parameters
     * @return          Matching map to Miseq format
     */
    private static Map flattenHiseqParams( Map params )
    {
        Map flat = [:]

        flat.seqrun     = params.seqrun
        flat.platform   = params.platform
        flat.sepe       = params.sepe
        flat.library    = params.lanes[0]?.library

        List samples = []       //  List of Maps of samples
        List samList = []       //  List of samples seen
        for ( lane in params.lanes )
        {
            for( Map sample in lane.samples )
            {
                sample.laneno = lane.laneno

                //  Only add a sample once to the list
                //
                if ( ! (sample.sample in samList))
                {
                    samList << sample.sample
                    samples << sample
                }
            }
        }

        if ( samples ) flat << [ samples: samples ]

        return flat
    }

    /**
     * Replace non alphnum chars and '_' in sample name with '-'
     *
     * @param sample
     * @return
     */
    static String cleanSampleName( String sample )
    {
        if ( sample )
            return sample.replaceAll( /[\W_]/, '-')

        return null
    }

    /**
     * Parse an Illumina Run Info file eg runInfo.xml
     *
     * @param   limsPath
     * @return  map
     */
    static Map parseRunInfo( String xmlPath )
    {
        File xmlFile = new File( xmlPath )

        if ( ! xmlFile.exists() || ! xmlFile.canRead())
        {
            log.error( "RunInfo file doesn't exist or not readable: ${xmlFile.path}" )
            return [:]
        }

        //  Parse file
        //
        def sr = new XmlParser().parse( xmlFile )

        return parseRunInfoNode( sr )
    }

    /**
     * Parse the XML tree of an Illumina Run Info file
     *
     * @param sr    XML Node of Run Info file
     * @return      Map of parsed parameters
     */
    public static Map parseRunInfoNode( Node sr )
    {
        Map params = [:]

        if ( ! sr )
            return params

        params << [ id:         sr.Run?.@Id[0] ]
        params << [ flowcell:   sr.Run?.Flowcell?.text() ]
        params << [ instrument: sr.Run?.Instrument?.text() ]
        params << [ date:       sr.Run?.Date?.text() ]
        params << [ readlen:    getReadLen(sr.Run?.Reads[0]?.children()) ]
        params << [ readcycles: getReadCycles(sr.Run?.Reads[0]?.children()) ]
        params << [ lanes:      sr.Run?.FlowcellLayout?.@LaneCount[0] ]

        return params
    }
}
