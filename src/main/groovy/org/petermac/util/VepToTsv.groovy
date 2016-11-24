/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.util

/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 12/04/13
 * Time: 12:50 PM
 * To change this template use File | Settings | File Templates.
 */

import groovy.util.logging.Log4j

@Log4j
class VepToTsv
{
    /**
     * Main execution thread
     *
     * @param args  CLI arguments
     */
    static void main(args)
    {
        def cli = new CliBuilder(
                usage: 'VepToTsv [options] in.vep out.tsv',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nConvert an Ensembl VEP file to normalised DB format TSV\n')

        cli.with
                {
                    h(longOpt: 'help', 'Usage Information', required: false)
                    s(longOpt: 'sample', 'Sample Name', args: 1, required: false)
                    r(longOpt: 'seqrun', 'SeqRun Name', args: 1, required: false)
                    p(longOpt: 'panel', 'Panel Name', args: 1, required: false)
                    c(longOpt: 'columns', 'File of columns to output', args: 1, required: false)
                }
        def opt = cli.parse(args)

        if (!opt) return
        if (opt.h || opt.arguments().size() != 2)
        {
            cli.usage()
            return
        }

        //  Run the program
        //
        log.info("VepToTsv " + args )

        def nlines = execute( opt ) - 1

        log.info("Done, processed ${nlines} lines")
    }

    /**
     * Main execution thread
     *
     * @param   opt     Parsed CLI options
     * @return          lines processed
     */
    static Integer execute( opt )
    {
        List<String> extra = opt.arguments()
        def vepFile = new File( extra[0])
        def tsvFile = new File( extra[1])

        if (!vepFile.exists())
        {
            log.error("VEP file doesn't exist: " + vepFile.name)
            return 0
        }

        //  Parse basic fields as TSV file
        //
        def lines = []
        def header = []
        def cols = []
        def nlines = 0
        def extraidx = -1
        vepFile.splitEachLine('\t')
                {
                    line ->

                    // if ( nlines > 30 ) return nlines

                    //  skip over '##' header lines
                    //
                    if ( ! (line[0] =~ /^##/))
                    {
                        if (nlines++ == 0)
                        {
                            //  Digest header line looking for "Extra" header column
                            //
                            header = line
                            extraidx = header.findIndexOf { it == "Extra"}
                            assert( line[extraidx] == "Extra" )
                            header = header - ["Extra"]
                            header[0] = header[0].replace('#','')   // strip off '#' at start
                        }
                        else
                        {
                            //  Data line: find novel columns in Extra field
                            //
                            cols += addExtraKeys( cols, line[extraidx])
                            lines << line
                        }
                    }
                }

        //  set up TSV columns to output
        //
        cols = header + cols
        log.debug( "Columns found: \n" + cols.join('\t'))

        //  read in column list if provided
        //
        if ( opt.columns )
        {
            def colFile = new File( opt.columns as String )
            if ( ! colFile.canRead())
            {
                log.fatal( "Can't read column file: ${opt.columns}" )
                return 0
            }
            cols = []
            cols = colFile.eachLine { cols << it }
        }

        //  Output header lines
        //
        tsvFile.delete()
        tsvFile.createNewFile()
        tsvFile << "##  Created by VepToTsv\n##\n#"

        //  Todo: shameless hack to split up chr:pos location field - move to ETL framework
        //
        tsvFile << (cols.join("\t") + "\n").replace( 'Location', 'chr\tpos' )

        //  Process all the lines
        //
        lines.each
                {
                    line ->

                    def extras = line[extraidx]

                    //  Remove extras from line
                    //
                    line = line - [ extras ]
                    assert( line.size() == header.size())

                    //  Format the line to match all columns
                    //
                    tsvFile << tsvOutput( opt, cols, header, line, extras )
                }


        return nlines
    }

    /**
     * Extract a List of extra field headers from a key=value;... string
     *
     * @param header    List of currently known headers
     * @param extras    Extra k=v fields to be inspected fro new headers
     * @return          New headers found
     */
    static List addExtraKeys( header, extras )
    {
        def fields = []
        def kvpairs = extras.tokenize(';')
        for ( kvp in kvpairs )
        {
            //  Todo: use regexp - some values contain '='
            def kv = kvp.tokenize('=')
            if ( ! header.contains(kv[0]))
            {
                fields += kv[0]
            }
        }

        return fields
    }

    /**     Find additional columns in the Extra column
     *
     * @param opt       CLI options
     * @param cols      List of column headers
     * @param header    Fixed column header
     * @param line      Fixed field line to be output
     * @param extras    Extra key=value fields to be output
     * @return          Formatted TSV line to be output
     */
    static String tsvOutput( opt, cols, header, line, extras )
    {
        def outflds = []
        def kvpairs = extras.tokenize(';')

        //  Turn key=value pairs into a map
        //
        def fldmap = [:]
        kvpairs.each
        {
            kvp ->
            //  Todo: use regexp - some values contain '='
            def kv = kvp.tokenize('=')
            fldmap << [ (kv[0]) : kv[1] ]
        }

        //  Add fixed columns to Map
        //
        def idx = 0
        for ( fld in line )
        {
            fldmap << [ (header[idx++]) : fld ]
        }

        //  Add command line fields
        //
        if ( opt.sample ) fldmap << [ sample : opt.sample ]
        if ( opt.seqrun ) fldmap << [ seqrun : opt.seqrun ]
        if ( opt.panel )  fldmap << [ panel  : opt.panel ]

        //  Now output each defined column or '' if no value
        //
        for ( col in cols )
        {
            outflds << cleanFld( col, fldmap[col])
        }

        return outflds.join("\t") + "\n"
    }

    /**     Clean up field based on header name
     *      ToDo: this should be in the ETL system
     *
     * @param cleanFld
     * @param name      Name of field
     * @param field     Value of field
     * @return          Cleaned field value
     */
    static String cleanFld( name, field )
    {
        if ( ! field ) return ''

        //  Strip off "<transcript>:" prefix
        //
        if ( name == "HGVSp")
        {
            def match = (field =~ /(.*:)(p\..*)/ )
            if ( match.matches())
                field = match[0][2]
            else
                field = ''
        }

        //  Strip off "<transcript>:" prefix
        //
        if ( name == "HGVSc")
        {
            def match = (field =~ /(.*:)(.*)/ )
            if ( match.matches())
                field = match[0][2]
        }

        if ( name == "Location")
        {
            return field.replace( ':', '\t' )
        }

        return field
    }
}
