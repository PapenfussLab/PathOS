/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



//
//	LoadDetente.groovy
//
//	Convert Detente SQL extract into TSV file
//
//  The process is:
//
//  1.	Go to /pmc-qmi/apps/Molpathsql/ directory
//  2.	Find all files ending in .csv (case insensitive)
//  3.	Get the date of each file
//  4.	Count the rows in each file
//  5.	Select the most recent file having
//      a) more than 40,000 rows,
//      b) starts with a quoted episode number e.g. "nnKnnnn" or "nnMnnnn" and
//      c) being less than 7 days old e.g. Ignore old files
//
//	Usage:  LoadDetente /pmc-qmi/Molpathsql mp_detente.tsv
//
//	01	kdoig	28-Jan-2014
//

package org.petermac.pathos.pipeline

import groovy.util.logging.Log4j
import au.com.bytecode.opencsv.CSVReader

@Log4j
class LoadDetente
{
    /**
     * main method for CLI execution
     *
     * @param args
     */
    static void main( args )
	{
		//
		//	Collect and parse command line args
		//
		def cli = new CliBuilder(   usage: "LoadDetente [options] indir out.tsv",
                                    header: '\nAvailable options (use -h for help):\n',
                                    footer: '\nLoad Detente records from CSV data files\n')

		//	Options to Submit
        //
        cli.with
                {
                    h( longOpt: 'help',		    'this help message' )
                    o( longOpt: 'output',  args: 1, 'output file [mp_detente.tsv]' )
                    d( longOpt: 'datadir', args: 1, required:true, 'Data source directory' )
                }
		
		def opt = cli.parse( args )
        if ( ! opt ) return
        if ( opt.h )
        {
            cli.usage()
            return
        }

        //
        //  Find source directory
        //
        def srcdir = new File(opt.datadir as String)
        if ( ! srcdir.exists())
        {
            log.fatal( "Source data directory doesn't exist " + srcdir.name )
            return
        }

        //  Set output file
        //
        def ofile = new File( opt.output ?: 'mp_detente.tsv' )

        log.info( "Start Detente Load from ${srcdir} " + args )

        //
        //  Perform data load
        //
		dataload( srcdir, ofile )

        log.info( "Done: processed ${srcdir}" )
    }

    /**
     * Mainline data loader
     *
     * @param srcdir    Data source directory
     * @param outfile   Data output file
     */
    static void dataload( File srcdir, File outfile )
	{
        if ( ! srcdir.exists() || ! srcdir.isDirectory())
        {
            log.fatal( "Invalid source directory for LoadDetente: [${srcdir}]" )
            return
        }

        File csvfile = findValidCsv( srcdir )
        if ( ! csvfile )
            return

        log.info( "Processing CSV file: " + csvfile + " into " + outfile )

        //  output header
        //
        header( outfile, csvfile )

        //  Parse CSV file
        //
        CSVReader reader = new CSVReader( new FileReader( csvfile));
        List lines = reader.readAll();

        //  dump out each record as TSV file
        //
        for ( String[] line in lines )
        {
            outfile << line.join("\t") + "\n"
        }
    }

    /**
     * Find the CSV file that is suitable to load
     *
     * @param   csvfile File object to validate
     * @return          File if valid Detente CSV file
     */
    static File findValidCsv( File srcdir )
    {
        //  Loop through all .CSV or .csv extract files
        //
        double latest = 99
        File   found  = null
        int    nlines = 0
        srcdir.eachFileMatch(~/.*\.[Cc][Ss][Vv]/)
        {
            csvfile ->

                //  Find age in days of file
                //
                double age = (new Date().time - csvfile.lastModified()) / (24 * 3600 * 1000)
                if ( age > 7 )
                    return found     // older than a week

                //  Count the lines
                //
                List<String> lines = []
                csvfile.eachLine{ lines<< it }
                if ( lines.size() < 40000 )
                    return found     //  too small

                //  Must start with a quoted sample number nn[MK]nnnn
                //
                if ( ! lines[0] =~ /\"\d\d[MK]\d\d\d\d\",/ )
                    return found     // doesn't start with a sample number

                //  Keep the most recent
                //
                if ( age < latest )
                {
                    latest = age
                    found  = csvfile
                    nlines = lines.size()
                }
        }

        if ( found )
            log.info( "${found} age: ${latest} rows: ${nlines}")
        else
            log.warn( "No valid CSV found in ${srcdir}")

        return found
    }

    /**
     * Create empty file with a header
     *
     * @param outf      File to create and populate
     * @param inf       Source of data
     */
    static void header( File outf, File inf )
    {
        if ( outf.exists())
            outf.delete()     // clear it out first

        outf << "## File created by LoadDetente from ${inf}\n##\n#"
        outf << "sample\tpatient\trequest_date\tcollect_date\trcvd_date\tauth_date\turn\tlocation\tpay_cat\trequester\ttest_set\ttest_desc\tdob\tsex\n"
    }

    /**
     * Clean text field suitable for dataload into Database
     *
     * @param field     Data field to cleanup
     * @return          Cleaned field
     */
    static String cleanText( String field )
    {
        field = field.replace( '\t', ' ')
        field = field.replace( '\n', ' ')
        return field
    }
}

