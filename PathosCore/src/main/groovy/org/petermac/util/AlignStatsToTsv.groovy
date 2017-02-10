/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

/**
 * Reformat aligner stats for database load Todo: get JE to do this by default
 *
 * User: doig ken
 * Date: 08/11/13
 * Time: 12:50 PM
 *
 */

import groovy.util.logging.Log4j

@Log4j
class AlignStatsToTsv
{
    /**
     * Main execution thread
     *
     * @param args  CLI arguments
     */
    static void main(args)
    {
        def cli = new CliBuilder(
                usage: 'AlignStatsToTsv [options] in.stats out.tsv\nAlignStatsToTsv --hybrid [options] in.stats1 in.stats2 out.tsv',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nConvert an Aligner stats file to normalised DB format TSV\n')

        cli.with
        {
            h(      longOpt: 'help',   'Usage Information',    required: false)
            s(      longOpt: 'sample', 'Sample Name', args: 1, required: false)
            r(      longOpt: 'seqrun', 'SeqRun Name', args: 1, required: false)
            p(      longOpt: 'panel',  'Panel Name',  args: 1, required: false)
            hyb(    longOpt: 'hybrid', 'Panel Name',           required: false)
        }
        def opt = cli.parse(args)

        if ( ! opt ) return
        if ( opt.h || ( ! opt.hybrid && opt.arguments().size() != 2) || ( opt.hybrid && opt.arguments().size() != 3))
        {
            cli.usage()
            return
        }

        //  Get files
        //
        List<String> extra = opt.arguments()

        //  Run the program
        //
        log.info("AlignStatsToTsv " + args )

        def nlines = 0
        if ( ! opt.hybrid )
        {
            nlines = filter( extra[0], extra[1], opt.seqrun, opt.sample, opt.panel )
        }
        else
        {
            nlines = hybridStats( [ extra[0], extra[1] ], extra[2], opt.seqrun, opt.sample, opt.panel )
        }

        log.info("Done, processed ${nlines} lines")
    }

    /**
     * Filter an Amplicon alignment stats file and output as formatted TSV file
     *
     * @param stats         Input  stats.csv file
     * @param tsv           Output stats.tsv file
     * @param seqrun        Seqrun to embed as field
     * @param sample        Sample to embed as field
     * @param panel         Panel  to embed as field
     * @param panelGroup    Panel group to embed as field
     * @return              Number of lines processed
     */
    static Integer filter( String stats, String tsv, String seqrun, String sample, String panel )
    {
        def statsFile = new File( stats )
        def tsvFile   = new File( tsv   )

        if ( ! statsFile.exists())
        {
            log.error("Stats file doesn't exist: " + statsFile.name)
            return 0
        }

        //  Clear out existing file
        //
        if ( tsvFile.exists())
            tsvFile.delete()

        //  Read all lines in
        //
        List lines = statsFile.readLines()

        //  Header must be first line, try a TSV format file
        //
        def fs = '\t'
        List header = lines[0].split(fs)

        //  If not a tab separated file, try a comma separated file
        //
        if ( header.size() == 1 )
        {
            fs = ','
            header = lines[0].split(fs)
        }

        //  If not a known header then exit
        //
        if ( ! (header[0] == 'AmpliconName'))
        {
            log.error( "Unrecognised stats header " + header )
            return 0
        }

        //  Index of fields of interest
        //
        def AMPLICON = 0
        def REGION   = 1
        def READSOUT = 7

        //  Find summary stats
        //
        def totreads = 0
        def unmapped = 0
        def goodAmp  = 0
        int nlines   = 0
        boolean collectStats = false
        String sampleStats = ''

        log.debug( "found ${lines.size()}")

        //  first pass through stats to collect summary info
        //
        for ( String line in lines )
        {
            List<String> flds = line.split(fs)
            log.debug( "found ${flds}")

            if ( flds[0] == 'Total Number Of Reads:' )          totreads = flds[1]
            if ( flds[0] == 'Unmapped Reads:' )                 unmapped = flds[1]
            if ( flds[0] == 'No. Amplicons > 0.2 x ReadsOut:' ) goodAmp  = (flds[1] == 'NA' ? 0 : flds[1])

            //  A hack to concantenate the untidy formatting of the sample stats Todo: clean up stats summary
            //
            if ( flds[0] == 'No mate pairs:' )
                collectStats = true

            if ( flds[0].startsWith('No. Reads'))
            {
                sampleStats += line.replace(fs,' ') + ':' + lines[nlines+1].replace(fs,' ')
            }

            if ( collectStats && flds[0].contains(':'))
            {
                sampleStats = sampleStats + flds.join(' ') + "|"
            }

            ++nlines
        }

        if ( ! nlines ) return nlines

        //  Output header
        //
        tsvFile <<  """##   Generated by AlignStatsToTsv from ${statsFile.name}
##
##
#seqrun\tsample\tpanel\tdeprecated\tamplicon\tregion\treadsout\ttotreads\tunmapped\tgoodamp\tsamplestats
"""
        //  Output single summary line: flagged with 'SUMMARY' in Amplicon column
        //
        List out = [ seqrun, sample, panel, '', 'SUMMARY', '-', '0' ]

        out << totreads
        out << unmapped
        out << goodAmp
        out << sampleStats

        tsvFile << out.join('\t') + '\n'


        //  Output all lines as TSV file - second pass through stats file
        //
        for ( String line in lines )
        {
            out = [ seqrun, sample, panel, '' ]
            List flds = line.split( fs )

            //  Check we have an amplicon line <chr>:<start>-<end>
            //
            if ( ! (flds[REGION] =~ /(\d+|[XY]):\d+-\d+/ )) continue

            //  collect output fields
            //
            out << flds[AMPLICON]
            out << flds[REGION]
            out << flds[READSOUT]
            out << '0'
            out << '0'
            out << '0'
            out << ''

            tsvFile << out.join('\t') + '\n'
        }

        return nlines
    }

    /**
     * Convert hybrid stats files into an AlignStats format for backwards compatibility with Amplicon stats
     * Todo: this needs to be removed and replaced with a uniform pipeline QC interface
     *
     * @param stats     List of stats file to process
     * @param tsv       TSV file to output
     * @param seqrun    Seqrun of stats
     * @param sample    Sample of stats
     * @param panel     Panel of stats
     * @return          Lines processed
     */
    static Integer hybridStats( List<String> stats, String tsv, String seqrun, String sample, String panel )
    {
        Integer nlines = 0
        def tsvFile = new File(tsv)

        //  Clear out existing file
        //
        if (tsvFile.exists())
            tsvFile.delete()

        Map tsvData = [:]
        String sampleStats = ''

        //  Process stats files
        //
        for ( fname in stats )
        {
            def statsFile = new File(fname)
            if ( ! statsFile.exists())
            {
                log.error("Stats file doesn't exist: " + fname)
                return 0
            }
            log.info( "Processing ${statsFile}")

            //  Read all lines in
            //
            List lines = statsFile.readLines()

            //  Header must be first line, try a TSV format file
            //
            def fs = '\t'
            List header = lines[0].split(fs)

            //  If not a tab separated file, try a comma separated file
            //
            if ( header.size() == 1 )
            {
                fs = ','
                header = lines[0].split(fs)
            }

            //  Remove any delimiter characters
            //
            lines[1] = lines[1].replaceAll( /\|/, '_' )     //  Remove |
            lines[1] = lines[1].replaceAll( /:/, '_' )      //  Remove :
            List data = lines[1].split(fs)

            assert header.size() == data.size(), "Header columns ${header.size()} doesn't match data columns  ${data.size()} in ${fname}"

            header.eachWithIndex { String entry, int i -> sampleStats += "|${entry}:${data[i]}" }
            header.eachWithIndex { String entry, int i -> tsvData << [(entry): data[i]] }
        }

        //  remove leading '|' from sampleStats
        //
        if ( sampleStats[0] == '|' ) sampleStats = sampleStats[1..-1]

        //  Output header
        //
        ++nlines
        tsvFile <<  """##   Generated by AlignStatsToTsv from ${stats}
##
##
#seqrun\tsample\tpanel\tdeprecated\tamplicon\tregion\treadsout\ttotreads\tunmapped\tgoodamp\tsamplestats
"""
        //  Output single summary line: flagged with 'SUMMARY' in Amplicon column
        //
        List out = [ seqrun, sample, panel, '', 'SUMMARY', '-', '0' ]

        //  Total reads:49668306
        //  Mapped reads:49427579

        out << tsvData['Total reads']
        out << "${Integer.parseInt(tsvData['Total reads']) - Integer.parseInt(tsvData['Mapped reads'])}"
        out << '0' //goodAmp
        out << sampleStats

        tsvFile << out.join('\t') + '\n'
        ++nlines

        //  Output dummy amplicon line: flagged with 'allroi' in Amplicon column
        //
        out = [ seqrun, sample, panel, '', 'allroi', '-' ]

        out << tsvData['Total reads']       // readsout
        out << tsvData['Total reads']       // totreads
        out << "${Integer.parseInt(tsvData['Total reads']) - Integer.parseInt(tsvData['Mapped reads'])}"  // unmapped
        out << '1'                          // goodamp
        out << ''                           // sample_stats

        tsvFile << out.join('\t') + '\n'
        ++nlines

        return nlines
    }
}
