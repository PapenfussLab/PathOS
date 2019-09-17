/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.pathos.pipeline

/**
 * Create Panel based files from Amplicon manifests
 *
 * Author:  Ken Doig
 * Date:    5-Aug-2014
 */

import groovy.util.logging.Log4j
import org.petermac.util.Locator
import org.petermac.util.Tsv

@Log4j
class MakePanel
{
    static def loc = Locator.instance      // Locator class for file locations

    /**
     * Main execution thread
     *
     * @param args  CLI arguments
     */
    static void main(args)
    {
        def cli = new CliBuilder(
            usage: 'MakePanel [options] panel',
            header: '\nAvailable options (use -h for help):\n',
            footer: '\nCreate Panel files in various formats\n')

        cli.with
        {
            h(longOpt: 'help',      required: false, 'Usage Information' )
            a(longOpt: 'amplicon',  args: 1, 'Amplicon file to output' )
            b(longOpt: 'bed',       args: 1, 'BED file to output' )
        }
        def opt = cli.parse(args)

        if ( ! opt ) return
        if ( opt.h || opt.arguments().size() != 1)
        {
            cli.usage()
            return
        }

        //  Run the program
        //
        log.info("MakePanel " + args )

        //  Extract file names
        //
        List<String> extra = opt.arguments()
        def panel  = extra[0]

        //  Create Amplicon file
        //
        if ( opt.amplicon )
        {
            if ( ! new MakePanel().runAmplicon( panel, opt.amplicon ))
                log.error( "Create Amplicon file failed")
        }

        //  Create BED file
        //
        if ( opt.bed )
        {
            if ( ! new MakePanel().runBed( panel, opt.bed ))
                log.error( "Create BED file failed")
        }

        log.info("Done, processed panel ${panel}")
    }

    /**
     * Create an Amplicon file from a panel
     *
     * @param panel     Panel file to parse 10:89624142-89624321	23	23	PTEN_1_1
     * @param ampFile   Amplicon file to output
     * @return          true if OK
     */
    boolean runAmplicon( String panel, String ampFile )
    {
        //  Create output file
        //
        File outf = setOutputFile( panel, ampFile, true )
        if ( ! outf ) return false
        //  Check panel
        //
        File pf = getPanelPrimer( panel )
        if ( ! pf ) return false

        //  Load primers as a TSV file
        //
        Tsv primers = new Tsv(pf)
        if ( ! primers.load( false ))
        {
            log.error( "Couldn't load file ${pf.absolutePath}")
            return false
        }

        //  Process rows of primer file
        //
        List<List> rows = primers.tsvMap.rows
        for ( row in rows )
        {
            assert row.size() == 4, "Must have 4 columns in primer file ${pf.absolutePath}"
            outf << fmtAmplicon( panel, row )
        }

        return true
    }

    /**
     * Format a row for output
     *
     * @param panel     Primer line to parse
     * @param line
     * @return
     */
    String fmtAmplicon( String panel, List<String> line )
    {
        List cols = [panel]

        //  Parse primer file
        //  10:89624142-89624321	23	23	PTEN_1_1
        //
        def pos      = line[0]
        def prim1    = line[1]
        def prim2    = line[2]
        def name     = line[3]

        //  Parse genomic position
        //
        Map pmap = HGVS.parseChrPos( pos )

        if ( pmap == [:] || ! prim1.isInteger() || ! prim2.isInteger())
        {
            log.error( "Invalid format of line [${line}]")
            return ''
        }

        cols << pmap.chr                                // chromosome
        cols << (pmap.pos as int)-1 + (prim1 as int)    // start pos
        cols << pmap.endpos - (prim2 as int)            // end pos
        cols << name                                    // amplicon name
        cols << prim1                                   // primer 1 size
        cols << prim2                                   // primer 2 size

        return cols.join('\t') + '\n'
    }

    /**
     * Create an BED file from a panel
     *
     * @param panel     Panel to process
     * @param bedFile   BED file to output
     * @return          true if OK
     */
    boolean runBed( String panel, String bedFile )
    {
        //  Create output file
        //
        File outf = setOutputFile( panel, bedFile )
        if ( ! outf )
            return false

        //  Check panel
        //
        File pf = getPanelPrimer( panel )
        if ( ! pf )
            return false

        int nlines = MakeBed.makeBed( pf, outf, false, false, setRoiFile( panel ) )

        return nlines > 0
    }

    /**
     * Get panel File
     *
     * @param panel     Panel to find
     * @return          File of panel
     */
    File getPanelPrimer( String panel )
    {
        def pnl = new Panel(panel)
        if ( ! pnl.valid())
        {
            log.error( "Panel files don't exist ${pnl.panelPath}")
            return null
        }

        File pf = new File(pnl.panelPrimers)
        assert pf.exists(), "Missing manifest primer file"

        return pf
    }

    /**
     * Set output file
     *
     * @param panel     Panel to process
     * @param out       Output file name
     * @param header    True if header required
     * @return          File of output
     */
    File setOutputFile( String panel, String out, boolean header = false )
    {
        out = loc.panelDir + panel + loc.fs + out
        File outf = new File(out)
        if ( outf.exists())
        {
            log.warn( "File ${outf.absolutePath} already exists")
            return null
        }

        //  Create directories if necessary
        //
        outf.parentFile.mkdirs()

        //  Set header
        //
        if ( header ) setAmpliconHeader( panel, outf )
        return outf
    }

    /**
     * Generate header for output file
     *
     * @param panel     Panel to process
     * @param outf      Output File
     */
    void setAmpliconHeader( String panel, File outf )
    {
        outf << """##    Created by MakePanel for panel ${panel}
##
#Panel\tChr\tStartPos\tEndPos\tAmplicon\tPrimerLen1\tPrimerLen2
"""
    }

    /**
     * Set ROI File
     *
     * @param panel     Panel to process
     * @return          File for ROI file
     */
    File setRoiFile( String panel )
    {
        String roi = loc.panelDir + panel + loc.fs + 'ROI.tsv'
        File roif = new File(roi)

        return roif.exists() ? roif : null
    }
}
