/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

import groovy.util.logging.Log4j

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Utility class for generating Circos data files from genomic data (from file or RDB)
 *
 * User: Ken Doig
 * Date: 20-Apr-2014
 */

@Log4j
class CircosUtil
{
    static def sql = null

    /**
     * Main execution thread
     *
     * @param args  CLI arguments
     */
    static void main(args)
    {
        def cli = new CliBuilder(
                usage: 'CircosUtil [options] in.tsv out.bed',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nGenerate Circos data files\n')

        cli.with
                {
                    h(longOpt: 'help',      required: false, 'Usage Information' )
                    t(longOpt: 'fileType',  args: 1,  required: true, 'Input file type [amproi,exon,exonlabel]' )
                    m(longOpt: 'manifest',  args: 1,  required: true, 'Manifest name' )
                    p(longOpt: 'padding',   args: 1, 'Padding size for amplicon ROI [100bp]' )
                    r(longOpt: 'rdb',       args: 1, 'RDB to use (eg pa_local,pa_prod)' )
                }
        def opt = cli.parse(args)

        if ( ! opt ) return
        if ( opt.h || opt.arguments().size() != 2)
        {
            cli.usage()
            return
        }

        //  Open DB if needed
        //
        if ( opt.rdb )
        {
            def db = new DbConnect( opt.rdb )
            sql = db.sql()
        }
        else
        {
            log.fatal( "Missing RDB option")
            return
        }

        //  Run the program
        //
        log.info("CircosUtil " + args )

        //  Extract file names
        //
        List<String> extra = opt.arguments()
        def inFile   = extra[0]
        def circFile = extra[1]

        def nlines = 0

        if ( opt.fileType == 'amproi' )
            nlines = ampliconROI( inFile, circFile, opt.manifest, opt.padding ? opt.padding as int : 100 )

        if ( opt.fileType == 'exon' || opt.fileType == 'exonlabel' )
            nlines = exon( inFile, circFile, opt.fileType )

        if ( opt.fileType == 'amplicon' )
            nlines = amplicon( inFile, circFile, opt.manifest )

        if ( opt.fileType == 'gene' )
            nlines = gene( inFile, circFile, opt.fileType )

        log.info("Done, processed ${nlines} lines")
    }

    /**
     * Convert a list of genes into a TSV file of Circos parameters
     *
     * This routine generates the chromosome breaks (gaps between amplicons) to remove regions outside
     * the amplicons targeting regions (usually within exons)
     *
     * eg: KDR:(-55945.9;KDR:55946.4-55953.7;KDR:55979.8-55980.3;KDR:55980.5-)
     *
     * Usage: CircosUtil -t amproi -r demo -padding 10 gene_list.txt ../data/chr_breaks.circ
     *
     * @param inname    Input file of genes or 'all'
     * @param outname   TSV file to output suitable for loading into Circos
     * @param manifest  Amplicon manifest name to use
     * @param padding   Extra bases padding around amplicon ROI if needed
     * @return          No of lines converted
     */
    static Integer ampliconROI( String inname, String outname, String manifest, int padding )
    {
        def inFile   = new File( inname  )
        PrintStream circFile = System.out
        if ( outname != '-' )
        {
            File outf = new File( outname )
            outf.delete()
            circFile = new PrintStream( outf )
        }

        if ( ! inFile.exists())
        {
            log.error("Input file doesn't exist: " + inFile.name)
            return 0
        }

        //  output preamble
        //
        circFile << "chromosomes_breaks = "

        //  Parse basic fields as TSV file
        //
        def nlines = 0

        inFile.splitEachLine('	')
        {
            line ->
                nlines++
                def gene = line[0]
                log.info "Processing ${gene}"

                //  Setup gene query
                //
                def qry = 	"""
                            select	amp.startpos,
                            		amp.endpos,
                                    amp.amplicon
                            from	amplicon as amp
                            where	amp.panel = ${manifest}
                            and     amplicon regexp ${gene}
                            order
                            by		amp.startpos
                            """
                def rows   = sql.rows( qry )

                int  idx   = 0;
                long last  = 0

                for ( row in rows )
                {
                    if ( ++idx ==  1 )
                    {
                        def chrBreak = "${gene}:(-${pos(row.startpos,-padding,1000)};"
                        log.info chrBreak
                        circFile << chrBreak
                    }
                    else
                    {
                    //  Check amplicons aren't overlapping, otherwise move to next
                    //
                        if ( row.startpos - padding > last + padding )
                        {
                            def chrBreak = "${gene}:${pos(last,padding,1000)}-${pos(row.startpos,-padding,1000)};"
                            log.info chrBreak
                            circFile << chrBreak
                        }
                    }

                    //  Save
                    last = row.endpos

                    //  Process last amplicon
                    //
                    if ( idx ==  rows.size())
                    {
                        def chrBreak = "${gene}:${pos(row.endpos,padding,1000)}-);"
                        log.info chrBreak
                        circFile << chrBreak
                    }
                }

        }

        circFile << "\n"

        return nlines
    }

    /**
     * Extract exon locations and labels from ref_exon table
     *
     * @param inname    File of genes to output
     * @param outname   File with Circos formatted lines
     * @param type      File type to generate 'exon' exon list, 'exonlabel' exon labels
     * @return          No of lines processed
     */
    static Integer exon( String inname, String outname, String type )
    {
        def inFile   = new File( inname  )
        def circFile = new File( outname )

        if ( ! inFile.exists())
        {
            log.error("Input file doesn't exist: " + inFile.name)
            return 0
        }

        circFile.delete()

        def nlines = 0

        inFile.splitEachLine('	')
        {
            geneline ->
                nlines++
                def gene = geneline[0]
                log.info "Processing ${gene}"

                //  Setup gene query
                //
                def qry = 	"""
                            select	distinct
                                    ex.exonStart,
                            		ex.exonEnd,
                                    ex.idx,
                                    ex.exon
                            from	ref_exon as ex,
                                    ref_hgnc_genes rg
                            where   ex.gene   = ${gene}
                            and     ex.gene   = rg.gene
                            and     ex.refseq = rg.refseq
                            order
                            by		ex.idx
                            """
                def rows   = sql.rows( qry )
                for ( row in rows )
                {
                    List line = [ gene, row.exonStart, row.exonEnd ]
                    if ( type == "exon"  )     line << "name=${row.exon}"
                    if ( type == "exonlabel" ) line << "ex${row.idx}"
                    circFile << line.join("\t") + "\n"
                }
        }

        return nlines
    }


    /**
     * Extract gene locations and labels from ref_exon table
     *
     * @param inname    File of genes to output
     * @param outname   File with Circos formatted lines
     * @param type      File type to generate 'genelabel' gene labels
     * @return          No of lines processed
     */
    static Integer gene( String inname, String outname, String type )
    {
        def inFile   = new File( inname  )
        PrintStream circFile = System.out
        if ( outname != '-' )
        {
            File outf = new File( outname )
            outf.delete()
            circFile = new PrintStream( outf )
        }

        if ( ! inFile.exists())
        {
            log.error("Input file doesn't exist: " + inFile.name)
            return 0
        }

        def nlines = 0

        inFile.splitEachLine('	')
        {
            geneline ->
                nlines++
                def gene = geneline[0]
                log.info "Processing ${gene}"

                //  Setup gene query
                //
                def qry = 	"""
                            select	min(ex.exonStart) startpos
                            from	ref_exon as ex,
                                    ref_hgnc_genes rg
                            where   ex.gene   = ${gene}
                            and     ex.gene   = rg.gene
                            and     ex.refseq = rg.refseq
                            """
                def rows   = sql.rows( qry )
                for ( row in rows )
                {
                    List line = [ gene, row.startpos, row.startpos, gene ]
                    circFile << line.join("\t") + "\n"
                }
        }

        return nlines
    }


    /**
     * Extract amplicon details from Amplicon table
     *
     * @param inname    File of genes to output
     * @param outname   File with Circos formatted lines
     * @param panel     Panel for amplicons
     * @return          No of lines processed
     */
    static Integer amplicon( String inname, String outname, String panel )
    {
        def inFile   = new File( inname  )
        PrintStream circFile = System.out
        if ( outname != '-' )
        {
            File outf = new File( outname )
            outf.delete()
            circFile = new PrintStream( outf )
        }

        if ( ! inFile.exists())
        {
            log.error("Input file doesn't exist: " + inFile.name)
            return 0
        }

        def nlines = 0

        inFile.splitEachLine('	')
        {
            geneline ->
                nlines++
                def gene = geneline[0]
                log.info "Processing ${gene}"

                //  Setup gene query
                //
                def qry = 	"""
                            select  amplicon,
                                    startpos,
                                    endpos
                            from    amplicon
                            where   panel = $panel
                            and     amplicon regexp $gene
                            """
                def rows   = sql.rows( qry )
                for ( row in rows )
                {
                    List line = [ gene, row.startpos, row.endpos, "name=${row.amplicon}" ]
                    circFile << line.join("\t") + "\n"
                }
        }

        return nlines
    }

    /**
     * Reposition gap for circos chromosome units and round
     *
     * @param pos
     * @param pad
     * @param divisor
     * @return          position in circos chromosome units
     */
    static private Double pos( long pos, long pad, int divisor )
    {
        Double dpos = pos + pad

        return (dpos / divisor).round(1)
    }
}
