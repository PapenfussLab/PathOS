/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.pathos.pipeline

import groovy.util.logging.Log4j
import com.aspose.words.License
import org.petermac.util.DbConnect
import com.aspose.words.Document

/**
 * Extract mutation description text from a set of word files stored in:
 *
 * /pathology$/MOLPATH/MOLECULAR GENETICS/Variant Database/Report Comment Link/<gene>/<mut>.doc
 *
 * Author: doig ken
 * Date: 27/05/13
 * Time: 4:05 PM
 */

@Log4j
class LoadMutText
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
        def cli = new CliBuilder(   usage: "LoadMutText [options]",
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nConvert mutation report text for database loading\n')

        //	Options to command
        //
        cli.with
        {
            h( longOpt: 'help',		    'this help message' )
            e( longOpt: 'environment', 	args: 1, "Environment to use (prod|test) [test]" )
            o( longOpt: 'output',       args: 1, 'output file [mp_mutdesc.tsv]' )
            m( longOpt: 'mutdir',       args: 1, required: true, 'Mutation source directory [.]' )
            l( longOpt: 'license',      args: 1, 'Aspose license file' )
        }

        def opt = cli.parse( args )
        if ( ! opt ) return
        if ( opt.h )
        {
            cli.usage()
            return
        }

        //  Set template directory
        //
        def mutDir = new File( opt.mutdir ?: "." )
        if ( ! mutDir.exists())
        {
            log.error( "Mutation directory doesn't exists " + mutDir )
            return
        }

        //  Set Aspose license
        //
        String lf = System.getenv().get("PATHOS_HOME") + "/Report/License/Aspose.Total.Java.lic"
        def licenseFile = new File( opt.license ?: lf )
        if (licenseFile.exists())
        {
            // If you don't specify a license, Aspose.Words works in evaluation mode.
            //
            log.info( "Setting Aspose license" )
            License license = new License();
            license.setLicense(licenseFile.getAbsolutePath());
        }
        else
            log.error( "Aspose license file doesn't exists " + licenseFile )

        //  Connect to database (default is mp_test)
        //
        def db  = new DbConnect( "mp_" + (opt.environment ?: "test" ))

        //
        //  Perform data load
        //
        log.info( "Start LoadMutText " + args )
        def ofile = opt.output ?: 'mp_mutdesc.tsv'
        def nmut  = load( mutDir, db, ofile )

        log.info( "Done: processed ${nmut} mutations into ${ofile}" )
    }

    /**
     * Mainline report generator
     *
     * @param sample
     * @param tdir
     * @param outfile
     */
    static Integer load( File mutDir, DbConnect db, String outfile )
    {
        //  Query database for mutation text file names
        //
        def qry =   """
                    select	gene,
                            replace(mc.protein,'X','*') as hgvsp,
                            mc.hgvs as hgvsc,
                            reportlink
                    from	mp_curated as mc
                    where	reportlink regexp 'Report'
                    and		protein    != ''
                    and     reportlink != ''
                    """

        //  Open output file
        //
        def ofile = new File( outfile )

        //  Loop through all files
        //
        def nmut = 0
        db.sql().eachRow(qry)
        {
            row ->
            ++nmut

            def mf = new File( mutDir.path + File.separator + row.reportlink )
            if ( ! mf.exists() && mf.isFile())
                log.error( "File doesn't exists ${mf}" )
            else
                ofile << row.gene + "\t" + row.hgvsc + "\t" + row.hgvsp + "\t" + wordToText( mf ) + "\n"
        }

        return nmut
    }

    /**
     * Convert MS Word document to raw text
     *
     * @param mf    Word doc to dump
     * @return      Text extracted from file
     */
    static String wordToText( File mf )
    {
        //  Loading comment for reporting
        //
        log.info( "Opening: [${mf}]")
        Document doc = new Document( mf.path )
        String alldoc = doc.getText()
        alldoc = alldoc.replace( '\r', ' ')         // replace carriage returns
        alldoc = alldoc.replace( '\t', ' ')         // replace tabs for TSV output
        return   alldoc.replace( '\f', '')          // delete line feeds
    }
}