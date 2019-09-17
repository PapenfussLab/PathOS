/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */



package org.petermac.annotate

import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import org.apache.commons.codec.binary.Base64
import org.petermac.pathos.pipeline.HGVS
import org.petermac.util.Pubmed

import static groovyx.net.http.Method.POST

/**
 * MyVariant interface class for mining myvariant.info annotations
 *
 * Author:  Ken Doig
 * Date:    24/02/17
 */

@Log4j
class MyVariant
{
    static JsonSlurper slurper = new JsonSlurper()

    private static          def         baseURL   = 'http://myvariant.info'
    private static          def         mutURL    = baseURL + '/v1/variant/'

    /**
     * Constructor:
     *
     */
    MyVariant( String mutHost = baseURL )
    {
        //  Set myvariant host
        //
        baseURL = mutHost
        mutURL  = baseURL + '/v1/variant/'
    }

    /**
     * Check server is up
     * To test use % curl 'http://myvariant.info/v1/query?q=chr1:g.35367C>T'
     *
     * @return      true if server available
     */
    public static Boolean ping()
    {
        def url = "/v1/query?q=chr1:g.35367C>T"

        String ret = Pubmed.getUrl( baseURL + url )

        Map res = slurper.parseText( ret ) as Map

        return res.keySet().contains( 'total' )
    }

    /**
     * Submit a batch job to MyVariant server
     * To test use % curl -d 'fields=dbnsfp.genename' -d 'ids=chr1:g.35367C>T,chr7:g.55241707G>T,chr16:g.28883241A>G' 'http://myvariant.info/v1/variant'
     *
     * @param   hgsvsg  List of HGVSg variants to annotate
     * @param   fields  List of field names to return
     * @return          List of Maps, one for each query variant
     */
    static List<Map> submitBatch( List hgvsgs, List fields )
    {
        String jsonret = ''

        //   Setup HTTPBuilder with proxy and destination host
        //
        def http = new HTTPBuilder( baseURL )

        //  Submit a POST request as URL encoded data in body: Expect a JSON response
        //
        http.request( POST, ContentType.TEXT )
        {
            uri.path = '/v1/variant'

            send ContentType.URLENC, [ fields: fields.join(','), ids: hgvsgs.join(',') ]

            response.success =
            {
                resp, json ->
                    jsonret = json.text
                    log.debug( "Success! ${resp.statusLine} ${jsonret}" )
                    assert resp.status == 200
            }

            response.failure =
            {
                resp, json ->
                    log.error( "Request failed with status ${resp.statusLine} ${jsonret}" )
                    assert resp.status != 200
            }
        }

        List<Map> result = slurper.parseText( jsonret ) as List

        return result
    }

    /**
     * Submit a batch job to MyVariant server
     *
     * @param   hgsvsg  List of HGVSg variants to annotate
     * @param   fields  List of field names to return
     * @return          List of Maps, one for each query variant
     */
    static List<Map> submit( List hgvsgs, List fields = ['all'] )
    {
        List<Map> allvars = []

        while ( hgvsgs.size())
        {
            List chunk  = hgvsgs.size() > 999 ? hgvsgs[0..999] : hgvsgs[0..-1]        //  First 1000 entries
                 hgvsgs = hgvsgs.size() > 1000 ? hgvsgs[1000..-1] : []                //  The rest of the list

            allvars += submitBatch( chunk, fields )
        }

    return allvars
    }

    private static final SEPARATOR = '.'

    /**
     * Format a printable String of variants
     *
     * @param   hgsvsg  List of HGVSg variants to annotate
     * @param   fields  List of field names to return
     * @return          List<String>, one for each query variant
     */
    static List<String> prettyPrint(List hgvsgs, int width, List fields = ['all'] )
    {
        List<Map>     vars       = submit( hgvsgs, fields )
        List<String>  prettyVars = []

        for ( var in vars )
        {
            String display = render( '', var, 'myv', width )
            prettyVars << display
        }

        return prettyVars
    }

    /**
     * Recursivley render a Map
     *
     * @param display
     * @param var
     * @param prefix
     * @return
     */
    static String render( String display, Map var, String prefix, int width )
    {
        //  if prefix is empty, we dont need a leading SEPARATOR
        //
        if ( prefix ) prefix += SEPARATOR

        for ( kv in var)
        {
            if ( var."$kv.key" in Map )
            {
                display = render( display, kv.value as Map, "${prefix}${kv.key}", width )
            }
            else
            {
                display += sprintf( "\n%-${width}s%s", "${prefix}${kv.key}:", kv.value)
            }
        }

        return display
    }

    static List<Map> flatMaps( List<Map> maps, String prefix = "myv" )
    {
        List<Map> flatmaps = []

        for ( map in maps )
        {
            flatmaps << flatten( map, prefix )
        }

        return flatmaps
    }

    static Map flatten( Map m, String prefix )
    {
        Map flatmap = [:]

        //  if prefix is empty, we dont need a leading SEPARATOR
        //
        if ( prefix ) prefix += SEPARATOR

        for ( kv in m )
        {
            if ( m."$kv.key" in Map )
            {
                flatmap += flatten( kv.value as Map, "${prefix}${kv.key}" )
            }
            else
            {
                //  Note we must coerce the key to be a String otherwise it is a GString possibly
                //  causing downstream code to fail such as asserts
                //
                flatmap << [ ("${prefix}${kv.key}".toString()): (kv.value).toString() ]
            }
        }

        return flatmap
    }
}
