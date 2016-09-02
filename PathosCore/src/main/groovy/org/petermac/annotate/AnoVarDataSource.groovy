/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.annotate

import groovy.json.JsonBuilder
import groovy.util.logging.Log4j
import org.petermac.util.FileUtil
import org.petermac.util.RunCommand
import org.petermac.util.Tsv

/**
 * Created for PathOS.
 *
 * Description:
 *
 * This is the class for Annovar annotation
 *
 * User: Ken Doig
 * Date: 23-Nov-2014
 */

@Log4j
class AnoVarDataSource extends VarDataSource
{
    static def code = DS.Annovar.code()

    AnoVarDataSource( String rdb )
    {
        super( rdb )
    }

    /**
     * Add a list of annovar format variants into cache after calling Annovar
     *
     * @param vars  List of annovar variants
     * @return      Number of variants added
     */
    static int addToCache( List anolines, boolean useCache = true )
    {
        def jb   = new JsonBuilder()
        int nadd = 0

        Map  hgs       = anoToHgvsg( anolines )
        List novelVars = hgs.collect{it.key}

        //  Look up variants from cache first
        //
        if ( useCache )
        {
            novelVars = notInCache( code, novelVars )
            if ( novelVars.size())
                log.info( "Found ${hgs.size()-novelVars.size()}/${hgs.size()} in ${code} cache")
        }

        if ( novelVars )
        {
            //  Annovar all uncached variants
            //
            List<Map> mutvars  = runAnnovar( novelVars, hgs )

            //  Remove duplicates
            //
            List<Map> uniqvars = mutvars.unique { var -> var.Otherinfo }
            int dups = mutvars.size() - uniqvars.size()
            if ( dups > 0 )
                log.info( "Removed ${dups} duplicates")

            for ( mut in uniqvars )
            {
                //  convert Map to JSON object
                //
                jb(mut)

                //  Truncate long genes
                //
                String gene = mut['Gene.refGene']
                if ( gene.size() > 48 )
                {
                    log.warn( "Annovar gene truncated ${mut['Gene.refGene']}")
                    gene = gene.substring(0,48)
                }

                //  Add to cache using HGVSg as the key
                //
                String hgvsg = mut.Otherinfo
                Map params =    [
                                data_source:        code,
                                hgvsg:              hgvsg,
                                attr:               'mut_map',
                                value:              jb.toString(),
                                hgvsc:              mut.hgvsc,
                                hgvsp:              mut.hgvsp,
                                version:            '2013-08-23',
                                gene:               gene
                                ]

                List ids = saveValueMap( params )
                if ( ! ids ) log.warn( "Couldn't add ${params}")
                nadd += ids.size()
            }
        }

        return nadd
    }

    /**
     * Run Annovar
     *
     * @return          List of HGVSg vars
     * @param lines     Lines to use
     * @return          List of annotated vars
     */
    static List<Map> runAnnovar( List vars, Map anolines )
    {
        //  Dump variants into a temporary file (clear it first)
        //
        //  Dump variants into a temporary file
        //  Todo: replace with FileUtil.tmpFile( 'anv_' ) which is deleted on exit
        //
        File tmpFile = FileUtil.tmpFixedFile( '/tmp', 'anv_' )

        for ( var in vars )
            tmpFile << anolines[var]

        //  Run Annovar
        //
        log.info( "Running Annovar on ${vars.size()} vars" )
        def avcmd = "RunAnnovar.sh ${tmpFile.absolutePath} ${tmpFile.absolutePath}.tsv"
        def sout  = new RunCommand( avcmd ).run()
        if ( sout ) log.warn( "Annovar command output: " + sout )

        //  Read in Annovar output from specific file
        //
        tmpFile = new File( tmpFile.absolutePath + ".tsv" )
        if ( ! tmpFile.canRead())
        {
            log.fatal( "No Annovar results in ${tmpFile}")
            return []
        }

        //  Read Annovar output as TSV file and render as a List of Maps
        //
        Tsv tsv = new Tsv( tmpFile )
        tsv.load( true )
        List<Map> muts = tsv.getRowMaps()
        log.info( "Created ${muts.size()} Annovar annotations")

        return muts
    }

    /**
     * Convert Annovar lines into HGVSg keyed Map (last field in line)
     *
     * @param lines     Lines to convert
     * @return          Map of lines vars
     */
    static Map anoToHgvsg( List<String> lines )
    {
        Map hgs = [:]

        for ( line in lines )
        {
            def flds  = line.tokenize()
            def hgvsg = flds[-1]                // HGVSg is the last column in Annovar TSV rows
            assert hgvsg.startsWith('chr')      // Should always be a chr type variant
            hgs << [(hgvsg): line]
        }

        return hgs
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
        getValueMap( code, var )
    }
}
