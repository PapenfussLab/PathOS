/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.annotate

import groovy.json.JsonBuilder
import groovy.util.logging.Log4j
import org.petermac.pathos.pipeline.MutalyzerUtil

/**
 * Created for PathOS.
 *
 * Description:
 *
 * This is the class for Mutalyser annotation
 *
 * User: Ken Doig
 * Date: 23-Nov-2014
 */

@Log4j
class MutVarDataSource extends VarDataSource
{
    static def      code    = DS.Mutalyzer.code()       //  Code for this DataSource loader
    static String   rdb                                 //  RDB to use
    static String   mutHost = null                      //  Mutalyzer Host to use

    /**
     * Constructor with Mutalyzer Host
     *
     * @param rdb       Cache database
     * @param mutHost   Mutalyzer Host
     */
    MutVarDataSource( String rdb, String mutHost )
    {
        super( rdb )
        this.rdb     = rdb
        this.mutHost = mutHost
    }

    /**
     * Add a list of HGVSg variants into cache and then return them
     *
     * @param vars  List of HGVSg variants
     * @return      List of Maps of variants (note: some may be added twice as aliases)
     */
    static List<Map> addGetCache( List vars, boolean useCache = true )
    {
        int nadd = addToCache( vars, useCache )
        log.info( "Added ${nadd} variants to Mutalyzer cache")

        return getMutValues( vars )
    }

    /**
     * Add a list of HGVSg variants into cache after calling Mutalyzer
     *
     * @param vars  List of HGVSg variants
     * @return      Number of variants added (note: some may be added twice as aliases)
     */
    static int addToCache( List vars, boolean useCache = true )
    {
        def jb   = new JsonBuilder()
        int nadd = 0

        List novelVars = vars

        //  Look up variants from cache first
        //
        if ( useCache )
        {
            novelVars = notInCache( code, vars )
            if ( vars.size())
                log.info( "Found ${vars.size()-novelVars.size()}/${vars.size()} in ${code} cache")
        }

        if ( ! novelVars ) return nadd

        //  Mutalyze all uncached variants
        //  Todo: this needs to pass though a parameterised mutalyzer host in the future
        //
        List<Map> mutvars = new MutalyzerUtil( mutHost ).normaliseVariants( novelVars, rdb, null )

        for ( mut in mutvars )
        {
            //  convert Map to JSON object
            //
            jb(mut)

            //  Add to cache using HGVSg as the key
            //
            Map params =    [
                            data_source:        code,
                            hgvsg:              mut.hgvsg,
                            attr:               'mut_map',
                            value:              jb.toString(),
                            hgvsc:              mut.hgvsc,
                            hgvsp:              mut.hgvsp,
                            version:            '2.0',
                            gene:               mut.gene
                            ]

            //  Add variant if not in cache
            //
            if ( notInCache( code, [ mut.hgvsg ] ))
            {
                List ids = saveValueMap( params )
                if ( ! ids ) log.fatal( "Couldn't add ${params}")
                nadd += ids.size()
            }

            //  Add an alias record if the variant is renamed from another
            //
            if ( mut.variant && (mut.hgvsg != mut.variant) && notInCache( code, [ mut.variant ]))
            {
                params.hgvsg = mut.variant
                params.alias = mut.hgvsg
                List ids = saveValueMap( params )
                if ( ! ids ) log.fatal( "Couldn't add ${params}")
                nadd += ids.size()
            }
        }

        return nadd
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
     * Get a List of values from cache
     *
     * @param vars          List of keys to check
     * @return              List of values in cache
     */
    static List<Map> getMutValues( List<String> vars )
    {
        getValueMaps( code, vars )
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
