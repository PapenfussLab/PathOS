/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.annotate

import groovy.json.JsonBuilder
import groovy.util.logging.Log4j
import org.petermac.pathos.pipeline.HGVS
import org.petermac.pathos.pipeline.Mutalyzer

/**
 * Created for PathOS.
 *
 * Description:
 *
 * This is the class for MyVariant API annotation
 * See http://myvariant.info
 *
 * 01   Ken Doig            25-Feb-2017      Initial
 */

@Log4j
class MyVariantVarDataSource extends VarDataSource
{
    static def code = DS.MyVariant.code()         // Datasocurce abbreviation for RDB

    MyVariantVarDataSource(String rdb )
    {
        super( rdb )
    }

    /**
     * Add a list of HGVSg variants into cache after calling MyVariant
     *
     * @param vars  List of HGVSg variants
     * @return      Number of variants added (note: some may be added twice as aliases)
     */
    static int addToCache( List vars, boolean useCache = true )
    {
        def jb   = new JsonBuilder()
        int nadd = 0

        //  Look up variants from cache first
        //
        if ( useCache )
        {
            int nvars = vars.size()
            vars = notInCache( code, vars )
            if ( vars.size())
                log.info( "Found ${nvars-vars.size()}/${nvars} in ${code} cache")
        }

        if ( vars )
        {
            //  Call MyVariant for all uncached variants
            //
            List<Map> mutvars  = MyVariant.submit( vars )

            for ( mut in mutvars )
            {
                //  convert Map to JSON object
                //
                jb(mut)

                //  Truncate long genes
                //
                String gene = mut?.dbnsfp?.genename ?: mut?.cadd?.genename
                if ( gene && gene.size() > 48 )
                {
                    log.warn( "MyVariant gene truncated ${gene}")
                    gene = gene.substring(0,48)
                }

                //  Add to cache using HGVSg as the key
                //
                Map params =    [
                        data_source:        code,
                        hgvsg:              mut?.query,
                        attr:               'myv_map',
                        value:              jb.toString(),
                        hgvsc:              'add hgvsc',
                        hgvsp:              'add hgvsp',
                        version:            '2017-02-25',
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
     * Delete all Invitae cache records
     *
     * @return
     */
    static boolean deleteAll()
    {
        return deleteAll( code )
    }
}
