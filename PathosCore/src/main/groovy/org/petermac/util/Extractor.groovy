/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

import com.aspose.words.Document
import com.aspose.words.License
import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger

/**
 * Extract the text from a list of MSWord documents
 *
 * Author:  Kenneth Doig
 * Date:    15-Dec-15
 */

@Log4j
class Extractor
{
    static Map reportMap = [:]

    /**
     * main method for CLI execution
     *
     * @param args
     */
    static void main( args )
    {
        //	Collect and parse command line args
        //
        def cli = new CliBuilder(   usage: "Extractor [options] in.doc ...",
                                    header: '\nAvailable options (use -h for help):\n',
                                    footer: '\nConvert a list of Word files to text\n')

        //	Options to command
        //
        cli.with
        {
            h( longOpt: 'help',		    'this help message' )
            d( longOpt: 'debug',		'turn on debugging' )
            o( longOpt: 'output',       args: 1, 'output file [word.txt]' )
            r( longOpt: 'rdb',          args: 1, required: true, 'RDB to use' )
            t( longOpt: 'tsv',          args: 1, 'File name for TSV records [word.tsv]' )
            l( longOpt: 'license',      args: 1, 'Aspose license file' )
        }

        def opt = cli.parse( args )
        if ( ! opt ) return

        List argin = opt.arguments()
        if ( opt.h || argin.size() < 1 )
        {
            cli.usage()
            return
        }

        //  Debug ?
        //
        if ( opt.debug ) Logger.getRootLogger().setLevel(Level.DEBUG)
        log.debug( "Debugging turned on!" )

        //  Open files
        //
        List<File> inFiles = []
        for ( inf in argin )
        {
            def infile = new File( inf as String )
            if ( ! infile.exists())
            {
                log.fatal( "File ${infile.name} doesn't exist")
                continue
            }

            if ( infile.isFile())
                inFiles << infile
        }

        if ( ! inFiles)
        {
            log.fatal( "No data files to process")
            return
        }

        //  Set output file
        //
        def ofile = new File( opt.output ?: 'word.txt' )
        ofile.delete()

        //  Open TSV file and zero it if required
        //
        File tsvfile = new File( opt.tsv ?: 'word.tsv' )
        tsvfile.delete()

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

        //  Perform data load
        //
        log.info( "Start Extractor " + (args.size() > 10 ? args[0..10] + "..." : args))

        //  Process VCFs
        //
        int ntxt = extractWord( inFiles, opt.rdb, ofile, tsvfile )

        log.info( "Done: processed ${inFiles.size()} files, found ${ntxt} objects" )
    }

    /**
     * Extract Word text
     *
     * @param   inws    List of Word Files
     * @param   rdb     RDB to cache variants in
     * @return          Number of xxx output
     */
    private static int extractWord( List<File> inws, String rdb, File ofile, File tfile )
    {
        //  set up header for TSV file
        //
        boolean headout = true
        List header = [ 'sample', 'genes', 'hgvscs', 'hgvsps', 'analysis',
                        'patient', 'urn', 'dob', 'sex',
                        'pathlab', 'extref', 'specimen', 'samtype', 'tumourpct', 'blockid',  'collected', 'received',
                        'reported', 'authorised', 'reportedby',
                        'summary', 'clinind',
                        'assay', 'assaygenes', 'filename', 'parsed']

        for ( wf in inws )
        {
            Map m = [ filename: wf.absolutePath, parsed: new Date(), genes: [], hgvscs: [], hgvsps: [], analysis: []]
            String txt = wordToText( wf )
            ofile << "Parsing: ${m.filename}"
            ofile << txt
            m = textToMap( m, txt )
            if ( headout )
            {
                tfile << "##     Parsed by Extrator on ${new Date()}\n##\n#"
                tfile << header.join('\t') + '\n'
                headout = false
            }

            //  Output columns in order
            //
            List cols = []
            for ( col in header )
                cols << m[col]

            tfile << cols.join('\t') + '\n'
        }

        ofile << "\nDone:\n"

        return inws.size()
    }

    /**
     * Convert MS Word document to raw text
     *
     * @param wf    Word doc to dump
     * @return      Text extracted from file
     */
    static String wordToText( File wf )
    {
        //  Loading comment for reporting
        //
        log.info( "Opening: [${wf}]")

        //  Convert to Aspose Document
        //
        Document doc = new Document( wf.path )

        //  Declare an instance of the converter
        //
        DocToTxtWriter myConverter = new DocToTxtWriter()

        // This is the well known Visitor pattern. Get the model to accept a visitor.
        // The model will iterate through itself by calling the corresponding methods
        // on the visitor object (this is called visiting).
        //
        // Note that every node in the object model has the Accept method so the visiting
        // can be executed not only for the whole document, but for any node in the document.
        //
        doc.accept( myConverter )

        //  Dump Map from document
        //
        String txt = myConverter.getText()

        // Once the visiting is complete, we can retrieve the result of the operation,
        // that in this example, has accumulated in the visitor.
        //
        return myConverter.getText()
    }

    private static Map textToMap( Map m, String doc )
    {
        reportMap = m

        List<String> lines = doc.split( '\n' )
        int nline = 0
        while ( nline < lines.size())
        {
            //  keep next line for processing
            //
            String nextl = (nline <= lines.size() ? lines[nline+1] : '' )

            //  Add all the report attributes
            //
            mapAdd( lines[nline], nextl )

            //  Add the list of mutations
            //
            if ( lines[nline].startsWith('MUTATIONS DETECTED'))
            {
                nline += mutsAdd( lines[nline..-1])
                continue
            }

            //  Add the matching analysis for each mutation
            //
            if ( lines[nline].startsWith('Individual Variant Analysis'))
            {
                nline += analysisAdd( lines[nline..-1])
                continue
            }
            ++nline
        }

        //  Check counts of variants
        //
        int ngene = reportMap.genes.size()
        if ( reportMap.hgvscs.size()   != ngene ) log.error( "Incorrect number of HGSVc variants")
        if ( reportMap.hgvsps.size()   != ngene ) log.error( "Incorrect number of HGSVp variants")
        if ( reportMap.analysis.size() != ngene ) log.warn(  "Incorrect number of analyses")


        return reportMap
    }

    private static int mutsAdd( List<String> lines )
    {
        int nline = 0
        assert lines[nline++] == 'MUTATIONS DETECTED'
        nline += 2
        while ( nline < lines.size()-1)
        {
            if ( ! mutAdd( lines[nline].trim(), lines[nline+1].trim())) break
            nline += 2
        }

        return nline
    }

    private static boolean mutAdd( String gene, String mut )
    {
        //  mut should be in form: c.1940A>T, p.Asn647Ile
        //
        if ((gene =~ /\w+/) && (mut =~ /[cp]\..*/))
        {
            if ( ! reportMap.genes  ) reportMap.genes  = []
            if ( ! reportMap.hgvscs ) reportMap.hgvscs = []
            if ( ! reportMap.hgvsps ) reportMap.hgvsps = []

            reportMap.genes << gene

            //  split mut into parts
            //
            String[] parts = mut?.split(',')
            if ( parts?.size() == 2 )
            {
                reportMap.hgvscs << parts[0]?.trim()
                reportMap.hgvsps << parts[1]?.trim()
            }
            else
            {
                reportMap.hgvscs << mut?.trim()
                reportMap.hgvsps << '-'
            }

            return true
        }

        return false
    }

    private static int analysisAdd( List<String> lines )
    {
        int nline = 0
        assert lines[nline++] == 'Individual Variant Analysis'

        List<String> genes = new ArrayList(reportMap.genes as List)
        int          nmut  = genes.unique().size()
        while ( nmut && nline < lines.size())
        {
            if ( lines[nline] =~ /\w+:.*/ )
            {
                if ( ! lines[nline].startsWith(genes[genes.size()-nmut]))
                    log.warn( "Analysis doesn't match genelist ${genes} analysis=${lines[nline]}")

                reportMap.analysis << lines[nline]
                --nmut
            }
            ++nline
        }

        return nline
    }

    private static void mapAdd( String tok, String nextl )
    {
        addElement( 'Sample:', 'sample', tok )

        //  patient details
        //
        addElement( 'Patient:', 'patient', tok )
        addElement( 'URN:', 'urn', tok )
        addElement( 'DOB:', 'dob', tok )
        addElement( 'SEX:', 'sex', tok )

        //  physical sample details
        //
        addElement( 'Sample type –', 'samtype', tok )
        addElement( 'Estimated tumour burden of sample –', 'tumourpct', tok )
        addElement( 'Specimen:', 'specimen', tok )
        addElement( 'Block ID:', 'blockid', tok )
        addElement( 'Location:', 'pathlab', tok )
        addElement( 'Ext Ref:', 'extref', tok )
        addElement( 'Collected:', 'collected', tok )
        addElement( 'Received:', 'received', tok )

        //  report signoffs
        //
        addElement( 'Reported:', 'reported', tok )
        addElement( 'Reported by:', 'reportedby', tok )
        addElement( 'Authorised by:', 'authorised', tok )

        //  assay details
        //
        addNextElement( 'ASSAY', 'assay', tok, nextl )
        addNextElement( 'Genes', 'assaygenes', tok, nextl )

        //  pathology/curation details
        //
        addNextElement( 'SUMMARY', 'summary', tok, nextl )
        addElement( 'Clinical indication –', 'clinind', tok )
    }

    private static void addElement( String pattern, String key, String text )
    {
        if ( text.trim().startsWith(pattern))
            reportMap[(key)] = text.trim().replaceFirst( pattern, '').trim()
    }

    private static void addNextElement( String pattern, String key, String text, String nextl )
    {
        if ( text.trim().startsWith(pattern))
            reportMap[(key)] = nextl.trim().replaceFirst( pattern, '').trim()
    }

}