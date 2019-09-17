/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.annotate

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import org.petermac.pathos.pipeline.HGVS
import org.petermac.pathos.pipeline.Transcript
import org.petermac.util.FileUtil
import org.petermac.util.Locator
import org.petermac.util.RunCommand
import org.petermac.util.Tsv
import org.petermac.util.Vcf
import org.petermac.util.VepToTsv

/**
 * Created for PathOS.
 *
 * Description:
 *
 * This is the class for VEP annotation
 *
 * User: Ken Doig
 * Date: 23-Nov-2014
 */

@Log4j
class VepVarDataSource extends VarDataSource
{
    static Locator  loc  = Locator.instance
    static HGVS     hg
    static def      code = DS.VEP.code()

    VepVarDataSource( String rdb )
    {
        super( rdb )
        hg = new HGVS( rdb )
    }

    /**
     * Add a list of VCF format variants into cache after calling VEP
     *
     * @param   vcflines    List of VCF format lines
     * @param   useCache    True if cache lookup is done
     * @return              Number of variants added
     */
    static int addToCache( List vcflines, boolean useCache = true )
    {
        def jb   = new JsonBuilder()
        int nadd = 0

        Map  hgs       = vcfToHgvsg( vcflines )
        List novelVars = hgs.collect{it.key}

        //  Look up variants from cache first
        //
        if ( useCache )
        {
            novelVars = notInCache( code, novelVars )
            if ( vcflines.size())
                log.info( "Found ${vcflines.size()-novelVars.size()}/${vcflines.size()} in ${code} cache")

            if ( ! novelVars ) return nadd
        }

        //  VEP all uncached variants
        //
        List<Map> mutvars = runVep( novelVars, hgs )

        //  Filter by transcript
        //
        List<Map> vars = transcriptFilter( mutvars )

        //  Process all muts individually
        //
        for ( var in vars )
        {
            //  convert Map to JSON object
            //
            jb( var )

            //  Add to cache using HGVSg as the key
            //
            Map params   =  [
                    data_source:        code,
                    hgvsg:              var.hgvsg,
                    attr:               'mut_map',
                    value:              jb.toString(),
                    hgvsc:              var.besttx?.hgvsc,
                    hgvsp:              var.besttx?.hgvsp,
                    version:            'v90',
                    gene:               var.besttx?.gene_symbol
            ]

            List ids = saveValueMap( params )
            if ( ! ids ) log.warn( "Couldn't add ${params}")
            nadd += ids.size()
        }

        return nadd
    }

    /**
     * Run VEP
     *
     * @return          List of HGVSg vars
     * @param lines     Lines to use
     * @return          List of annotated vars
     */
    static List<Map> runVep( List vars, Map veplines )
    {
        //  Create a temp VCF file for VEP to process
        //
        File tmpFile = createVepVcf( vars, veplines )

        //  Run VEP
        //
        log.info( "Running VEP on ${vars.size()} vars" )
        def cmd   = "${loc.mpVepPath} ${tmpFile.absolutePath} ${tmpFile.absolutePath}.vep"
        def sout  = new RunCommand( cmd ).run()
        if ( sout ) log.warn( "VEP command output:\n" + sout )

        //  Convert VEP JSON output into Maps
        //
        List<Map> muts = loadJson( tmpFile.absolutePath + ".vep" )
        log.info( "Found ${muts.size()} VEP annotations")

        return muts
    }

    /**
     * Create temporary VCF file for VEP
     *
     * @param   vars        Variants to add to VCF
     * @param   veplines    VCF lines to output
     * @return              vcf File created
     */
    private static File createVepVcf( List vars, Map veplines )
    {
        File tmpFile = null

        try
        {
            //  Dump variants into a temporary file
            //
            tmpFile = FileUtil.tmpFixedFile( '/tmp', 'vep_' )

            //  Output header
            //
            tmpFile << Vcf.vepHeader()

            //  Output variants
            //
            for ( var in vars )
                tmpFile << veplines[var]
        }
        catch( Exception e )
        {
            log.fatal( "Couldn't create temp VCF file ${tmpFile} " + e )
            System.exit(1)
        }

        return tmpFile
    }

    /**
     * Load in a file of JSON lines and return a Map
     *
     * @param   jname   File of JSON lines
     * @return          List<Map> of converted JSON lines
     */
    static List<Map> loadJson( String jname )
    {
        def       js   = new JsonSlurper()
        List<Map> vars = []

        //  Read in VEP output from specific file
        //
        File vepFile = new File( jname )
        if ( ! vepFile.canRead())
        {
            log.error( "No VEP results in ${jname} exiting...")
            return vars
        }

        //  Load in all lines as Maps
        //
        int nl = 0
        vepFile.eachLine
        {
            String line ->

                try
                {
                    ++nl
                    vars << ( js.parseText( line ) as Map )
                }
                catch ( Exception e )
                {
                    log.error( "Json Parse Exception: ${e.toString()}\nline ${nl}:\n${line}")
                }
        }

        return vars
    }

    /**
     * Convert VEP lines into HGVSg keyed Map of lines
     *
     * @param lines     Lines to convert
     * @return          Map of lines vars
     */
    static Map vcfToHgvsg( List<String> lines )
    {
        Map hgs = [:]

        for ( line in lines )
        {
            def row  = line.tokenize()
            assert row.size() >= 5, "VCF line doesn't have enough columns [$line]"

            //  The third column is the hgvsg string
            //
            def hgvsg = row[2]
            assert hgvsg.startsWith('chr')
            hgs << [(hgvsg): line]
        }

        return hgs
    }

    /**
     * Filter out uninteresting transcripts and variants
     * Loop though all variants and only keep the "best" for each HGVSg variant
     *
     * @param   vars    List<Map> of VEP transcript annotation
     * @return          Filtered Map
     */
    private static List<Map> transcriptFilter( List<Map> vars )
    {
        Map<String,Map> bestVars = [:]                                      //  Best transcript for a variant

        for ( var in vars )
        {
            var.hgvsg = var.id
            if ( ! var.hgvsg?.startsWith('chr'))
            {
                //  unparseable variant
                //
                log.error("Missing HGVSg for ${var}")
                continue
            }

            //  Loop through transcripts looking for preferred TX for gene and best TX for the locus if multiple
            //
            for (Map tx in var.transcript_consequences)
            {
                String prefts = hg.geneToTranscript(tx.gene_symbol)         // Preferred TS for gene
                String tss    = tx.transcript_id                            // This Refseq transcript
                log.debug("In TX loop: hgvsc=${tx.hgvsc} hgvsp=${tx.hgvsp} gene=${tx.gene_symbol} preferred=${prefts} transcript=${tss}")

                //  Ignore this vep annotation if
                // (i)   no preferred tx
                // (ii)  no coding HGVSc
                // (iii) not preferred for the VEP TX gene
                //
                if ( ! prefts || ! tx.hgvsc || ! tss?.startsWith(prefts+'.')) continue

                //  Select the "best" transcript if there are multiple for a variant
                //
                log.debug("Best=${bestVars[var.hgvsg]?.besttx?.hgvsc} Target=${tx.hgvsc}")
                if ( Transcript.selectCoding( bestVars[var.hgvsg]?.besttx?.hgvsc, tx.hgvsc ))
                {
                    var.besttx = tx
                    bestVars[var.hgvsg] = var
                    log.debug("Setting besttx=${tx.hgvsc}")
                }
            }
        }

        return bestVars.values() as List<Map>
    }

    /**
     * Delete keys from cache
     *
     * @param vars          List of keys to delete
     * @return
     */
    static void removeFromCache( List vars )
    {
        removeFromCache( code, vars)
    }


    /**
     * Get a Map of values from cache
     *
     * @param  var          Variant to check
     * @return              Map of variant values in cache
     */
    static Map getValueMap( String var )
    {
        getValueMap( code as String, var )
    }
}
