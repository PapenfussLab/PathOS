/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Compare two TSV files looking for changes in values
 *
 * User: doig ken
 * Date: 21/10/2013
 * Time: 12:36 PM
 */

package org.petermac.util

import groovy.util.logging.Log4j

@Log4j
class CompareTsv
{
    /**
     * Main execution thread
     *
     * @param args  CLI arguments
     */
    static void main(args)
    {
        def cli = new CliBuilder(
                usage: 'CompareTsv [options] f1.tsv f2.tsv',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nCompare two TSV files\n')

        cli.with
        {
            h(longOpt: 'help', 'Usage Information')
            k(longOpt: 'keys', args: 1, required: true, 'Columns to use as keys eg col1name,col2name...' )
            i(longOpt: 'ignore', args: 1, 'Columns to ignore in comparison eg col1name,col2name...' )
            a(longOpt: 'always', args: 1, 'Columns to always output eg col1name,col2name...' )
        }
        def opt = cli.parse(args)

        if ( ! opt ) return
        if ( opt.h || opt.arguments().size() != 2)
        {
            cli.usage()
            return
        }

        //  Get files to compare
        //
        List<String> extra = opt.arguments()
        def tsv1File = new File( extra[0])
        def tsv2File = new File( extra[1])

        if ( ! tsv1File.exists() || ! tsv2File.exists())
        {
            log.fatal( "Missing files to compare [${extra}]")
            return
        }

        //  find keys
        //
        List keys = []
        if ( opt.keys )
            keys = (opt.keys as String).split(",")

        //  find columns to ignore
        //
        List ignore = []
        if ( opt.ignore )
            ignore = (opt.ignore as String).split(",")

        //  find columns to always display
        //
        List always = []
        if ( opt.always )
            always = (opt.always as String).split(",")

        //  Run the program
        //
        log.info("CompareTsv " + args )

        def mm = compareTsv( tsv1File, tsv2File, keys, ignore, always )

        log.info("Done, TSV files [${tsv1File}] and [${tsv2File}] mismatched=${mm}")
    }

    /**
     * Method to compare two TSV files, report to stdout
     *
     * @param f1        File 1 to compare
     * @param f2        File 2 to compare
     * @param keys      Columns name to match lines
     * @param ignore    Columns to ignore for comparison
     * @param always    Columns to always display
     * @return          number of mismatched lines
     */
    static int compareTsv( File f1, File f2, List keys, List ignore, List always )
    {
        def lines1 = getLines( f1 )
        def lines2 = getLines( f2 )

        if ( lines1.size() != lines2.size())
        {
            println( "\n## Files differ in number of rows [${f1.name} ${lines1.size()}, ${f2.name} ${lines2.size()}]")
        }

        def matches = matchLines( lines1, lines2, keys )
        matchLines( lines2, lines1, keys )

        println( "\n## Lines with mismatched columns (ignored columns: ${ignore}) :")
        int ok = 0
        int mm = 0
        for ( match in matches )
        {
            def cols = compareCols( match.first, match.second, ignore, always )
            if ( cols.first.size() || cols.second.size() )
            {
                ++mm
            }
            else
                ++ok
        }
        println ( "## Matched lines ${ok} Mismatched lines ${mm}" )

        return mm
    }

    /**
     * Compare two rows column by column
     *
     * @param row1  Row Map to match
     * @param row2  Row Map to match
     * @return      Paired Map of List of mismatch columns
     */
    static Map compareCols( Map row1, Map row2, List ignore, List always )
    {
        List cols1 = []
        List cols2 = []
        for ( kv in row1 )
        {
            if ( kv.key in always || (! (kv.key in ignore) && kv.value != row2.get(kv.key))) cols1.add( kv )
        }
        for ( kv in row2 )
        {
            if ( kv.key in always || (! (kv.key in ignore) && kv.value != row1.get(kv.key))) cols2.add( kv )
        }
        return [first: cols1, second: cols2 ]
    }

    /**
     * Find matching lines and output mismatched lines
     *
     * @param lines1    List of row maps
     * @param lines2    List of row maps
     * @param keys      Key columns to natch on
     * @return          Paired List of matched rows
     */
    static List<Map> matchLines( List<Map> lines1, List<Map> lines2, List keys )
    {
        List matching = []
        for ( row1 in lines1 )
        {
            def found = false
            for ( row2 in lines2 )
                if ( keyMatch( row1, row2, keys ))
                {
                    found = true
                    Map match = [first:row1, second:row2]
                    matching.add(match)
                    break
                }

            //  Report row not matched
            //
            // kdd if ( ! found ) println printKeys( row1, keys )
        }

        return matching
    }

    /**
     * Make a string out of keys
     * @param row   Row to output
     * @param keys  List of keys
     * @return      String of keys from row
     */
    static String printKeys( Map row, List keys )
    {
        def k = []
        for ( key in keys )
            k << row.get(key)

        return k.join('\t')
    }

    /**
     * Test if keys match
     *
     * @param row1  Map of first row
     * @param row2  Map of second row
     * @param keys  List of keys to check
     * @return      true if they match
     */
    static boolean keyMatch( Map row1, Map row2, List keys )
    {
        for ( key in keys )
            if ( row1.get(key) != row2.get(key))
                return false

        return true
    }


    /**
     * Read in lines and use header as a map key
     *
     * @param infile    TSV file to read
     * @return          List of row maps
     */
    static List<Map> getLines( File infile )
    {
        def  header = []
        List lines  = []

        //  Process each tab separated line
        //
        infile.eachLine
        {
            line ->

                if ( line =~ '^#' )
                {
                    line = line.replaceFirst('#','')
                    header = line.split("\t")
                }
                else
                {
                    //  add a sentinel space to preserve trailing null TSV fields
                    //  This is a bug in the way Groovy splits Strings
                    //
                    def fields = (line + ' ' ).split("\t")

                    //  Remove sentinel space in last field
                    //
                    fields[fields.size()-1] = fields[fields.size()-1].replace( / $/ , '')

                    //  Check all fields are there
                    //
                    if ( header.size() != fields.size())
                        log.error("File ${f1} Line ${nlines} header size (${header.size()}) mismatches data fields (${fields.size()})")

                    //  Convert row to map
                    //
                    def fldmap = [ header,fields].transpose().collectEntries { it }

                    lines.add( fldmap )
                }
        }

        return lines
    }
}
