/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



//
//	LoadAlamut.groovy
//
//	Load Alamut raw XML files
//
//	Usage:
//
//	01	kdoig	20-May-2013
//

package org.petermac.pathos.pipeline

import groovy.util.logging.Log4j

@Log4j
class LoadAlamut
{
    static def mutalyser = new Mutalyzer()

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
		def cli = new CliBuilder(   usage: "LoadAlamut [options]",
                                    header: '\nAvailable options (use -h for help):\n',
                                    footer: '\nLoad Alamut curated mutations from XML data files\n')

		//	Options to Submit
        //
        cli.with
                {
                    h( longOpt: 'help',		    'this help message' )
                    o( longOpt: 'output',  args: 1, 'output file [mp_alamut.tsv]' )
                    d( longOpt: 'datadir', args: 1, required:true, 'Data source directory' )
                    r( longOpt: 'rdb',     args: 1, required:true, 'RDB to use for transcripts' )
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

        //  Set output file
        //
        def ofile = new File( opt.output ?: 'mp_alamut.tsv' )

        log.info( "Start Alamut Load from ${srcdir} " + args )

        //
        //  Perform data load
        //
		dataload( srcdir, ofile, opt.rdb )

        log.info( "Done: processed ${srcdir}" )
    }

    /**
     * Mainline data loader
     *
     * @param srcdir    Data source directory
     * @param outfile   Output file for mutations
     * @param rdb       RDB to get transcripts
     */
    static void dataload( File srcdir, File outfile, String rdb )
	{
        if ( ! srcdir.exists())
        {
            log.fatal( "Source data directory doesn't exist " + srcdir.absolutePath )
            return
        }

        //  Loop through all mutation XML files *.mut
        //
        srcdir.eachFileMatch(~/.*\.mut/)
        {
            mutfile ->
            log.info( "Processing XML file: " + mutfile )
            readMutation( mutfile, outfile, rdb )
        }
    }

    /**
     * Unpack XML file mutation file in Alamut format
     *
     * @param fxml      File of ALamut mutation in XML format
     * @param ofile     Output file for mutations
     * @param rdb       RDB to get transcripts
     */
    static void readMutation( File fxml, File ofile, String rdb )
    {
        //  Create Groovy XML parser
        //
        def mutlist = new XmlParser().parse(fxml)

        NodeList muts = mutlist.children()

        if ( ! muts )
            return

        def hg = new HGVS( rdb )     // HGVS formatting Class

        for ( mut in muts)
        {
            //  Convert mutation to a Map
            //
            Map alamut = mutToMap( mut as Node )

            //  Convert to Hg19 of old genome build
            //
//            if ( alamut.assembly =~ /NCBI ?36/ ) alamut = convertHg19( alamut )
            if ( alamut.assembly =~ /NCBI ?36/ )
            {
                log.warn( "Ignoring hg18 variant")
                continue
            }

            if ( alamut.assembly =~ /GRCh38/ )
            {
                log.warn( "Ignoring hg20 variant")
                continue
            }

            //  Check for canonical transcript: otherwise report and ignore
            //
            if ( ! alamut.refseq.startsWith(hg.geneToTranscript(alamut.gene)))
            {
                log.warn( "Incorrect transcript for gene ${alamut.gene} ${alamut.refseq}:${alamut.hgvsc} should be ${hg.geneToTranscript( alamut.gene )}")
                continue
            }

            //  Output mutation to file
            //
            outputMutation(alamut, ofile)
        }
    }

    /**
     * Convert an old hg18 mut into hg19 Todo: this should convert HGVSg
     *
     * @param   mut Muation Map
     * @return      Updated Map
     */
    static Map convertHg19( Map mut )
    {
        def var = mut.refseq + ':' + mut.hgvsc

        List hgvsgs = mutalyser.numberConversion( mut.hgvsg, 'hg18' )
        for ( hg in hgvsgs)
        {
            if ( var == hg )
                return mut
        }

        log.warn( "HG18 unmatched variant ${var} ${mut.gene} ${hgvsgs}")
        return mut
    }

    /**
     * Convert Alamut XML tree into a parameter Map
     *
     * @param mut   XML node of mutation
     * @return      Map of parameters
     */
    static Map mutToMap( Node mut )
    {
        Map alamut = [:]

        String gene = mut.'@geneSym'
        assert gene, "need a genSym"
        alamut << [gene: gene]

        def assembly = mut.'@refAssembly'
        assert assembly, "need a refAssembly"
        alamut << [assembly: assembly]

        def chr = mut.'@chr'
        assert chr, "need a chromosome"

        def pathog = mut.Pathogenic[0]
        assert pathog, "need a Pathogenic clause ${alamut['gene']} ${alamut['hgvsc']}"
        alamut << [pathogenic: pathog.@val]

        //  Optional note tag
        //
        def note = mut.Note[0]
        def noteFld = note?.@val
        alamut << [note: (noteFld ? cleanText(noteFld) : '') ]

        //  Variant details
        //
        def var = mut.Variant[0]
        assert var, "need a Variant"
        alamut << [vartype: var.'@type']
        alamut << [pos:     var.'@pos']
        alamut << [ref:     var.'@baseFrom']
        alamut << [alt:     var.'@baseTo']
        def hgvsg = "chr${chr}:" + var.gNomen[0].'@val'
        alamut << [hgvsg:   hgvsg]

        //  Set Transcript and c., p. HGVS
        //
        def transcript = var.Nomenclature[0]
        alamut << [refseq: transcript.'@refSeq']
        alamut << [hgvsc:  transcript.cNomen[0]?.'@val']
        alamut << [hgvsp:  transcript.pNomen[0]?.'@val']

        //  Create normalised variant
        //
        def normvar = transcript.'@refSeq' + ':' + transcript.cNomen[0]?.'@val'
        normvar = HGVS.normalise(normvar)

        alamut << [variant: normvar]

        //  Normalise the classification scheme to be 5-level
        //
        def classif = mut.Classification[0]
        if ( classif )
        {
            //  Convert Alamut classification into normalised form
            //
            def cl  = classif.@val
            def lev = classif.@index as int
            if ( cl =~ 'Simple')
            {
                if ( lev == 2 ) lev = 3
                if ( lev == 3 ) lev = 5
            }
            def clname = 'unknown'
            switch (lev)
            {
                case 1: clname = 'C1: Not pathogenic'; break
                case 2: clname = 'C2: Unlikely pathogenic'; break
                case 3: clname = 'C3: Unknown pathogenicity'; break
                case 4: clname = 'C4: Likely pathogenic'; break
                case 5: clname = 'C5: Pathogenic'; break
                default:
                    log.error( "Unknown classification level ${lev} for ${alamut['gene']} ${alamut['hgvsc']}")
            }

            alamut << [classtype:  cl]
            alamut << [classlevel: clname]
        }
        else
        {
            alamut << [classtype: '']
            alamut << [classlevel: '']
        }

        //  Process all Sample Occurrences of the variant
        //
        def samples  = []
        def comments = []
        NodeList occurs = mut.Occurrences[0].children()
        if (occurs)
        {
            occurs.each()
                    {
                        occur ->
                            def udate = occur.Updated[0]?.'@date'
                            def utime = occur.Updated[0]?.'@time'
                            assert udate, "need a @date attribute"
                            alamut << [udate: udate]     // only keep the last update occurence
                            alamut << [utime: utime]     // only keep the last update occurence

                            def sample = occur.Patient[0]?.text()
                            if ( sample )
                            {
                                sample = cleanText( sample )
                                if ( sample.size() > 0 )
                                    samples << sample.replace( ' ', '' )        // keep sample list
                            }

                            def comment = occur.Comment[0]?.text()
                            if ( comment )
                            {
                                comment = cleanText( comment )
                                if ( comment.size() > 0 )
                                    comments << comment                         // accumulate comments
                            }

                            //  Unused attributes
                            //
                            def rna       = occur.RNAAnalysis[0]?.text()
                            def phenotype = occur.Phenotype[0]?.text()
                    }
        }

        alamut << [samples:  samples.join(',')]
        alamut << [comments: comments.join(';')]

        if ( ! alamut['udate']) { alamut << [udate: ''] }
        if ( ! alamut['utime']) { alamut << [utime: ''] }

        return alamut
    }

    static def header = true        // output header flag

    /**
     * Output a mutation to an output sink. Also output a header the first time round
     *
     * @param mut       Map of mutation attributes
     * @param outfile   Output stream
     */
    static void outputMutation( Map mut, File outfile )
    {
        //  output header if first time
        //
        if ( header )
        {
            header = false
            if ( outfile.exists()) outfile.delete()     // clear it out first
            outfile << '#'                              // tsvheader flag
            outfile << mut.keySet().join('\t') + '\n'   // header from row Map
        }

        //  output this mutation as a single TSV line
        //
        if ( mut.variant )
            outfile << mut.values().join('\t') + '\n'
        else
            log.warn( "Missing variant for " + mut.hgvsg )
    }

    /**
     * Clean text field suitable for dataload into Database
     *
     * @param field     Data field to cleanup
     * @return          Cleaned field
     */
    static String cleanText( String field )
    {
        field = removeHTML( field )
        field = field.replace( '\t', ' ')
        field = field.replace( '\n', ' ')
        return field
    }

    static def xmls = new XmlSlurper()

    /**
     * Remove HTML formatting and return raw text
     *
     * @param html  String with HTML content
     * @return      String with raw text
     */
    static String removeHTML( String html )
    {
        if ( html[0] == '<' )
        {
            //  Remove HTML tags
            //
            xmls.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
            xmls.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
            def h = xmls.parseText( html )

            return h.body
        }

        return html
    }
}

