/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 */

package org.petermac.annotate
import groovy.json.JsonBuilder
import groovy.util.logging.Log4j
import org.petermac.pathos.pipeline.HGVS
import org.petermac.util.DbConnect
import org.petermac.util.RunCommand
import org.petermac.util.Tsv
import org.petermac.util.Vcf
import org.petermac.util.VepToTsv

/**
 * Created for PathOS.
 *
 * Description:
 *
 * This is the class for IARC TSV annotation
 *
 * 01   Andrei Seleznev     4-May-2015      Initial
 * 02   Ken Doig            3-Jun-2015      Rewrite for TSV rowMaps
 */
@Log4j
class IarcDataSource extends VarDataSource
{
    static def code = DS.IARC.code()

    IarcDataSource( String rdb )
    {
        super( rdb )
    }

    /**
     * Add a list of TSV rowMap variants into cache after calling Mutalyzer
     *
     * @param vars  List of Maps variants
     * @return      Number of variants added (note: some may be added twice as aliases)
     */
    static int addToCache( List<Map> vars, boolean useCache = true )
    {
        def jb   = new JsonBuilder()
        int nadd = 0

        //  Look for valid rows
        //
        List<Map> novelVars = []
        for ( var in vars )
        {
            String  hgvsg = "chr17:${var.g_description}"
            Map     parts = HGVS.parseHgvsG(hgvsg)
            if ( parts )
            {
                if ( parts.muttype == 'ins' && ! (parts.bases =~ /[ATGC]+/))
                {
                    log.warn( "invalid ins format ${hgvsg}")
                }
                else
                {
                    var.hgvsg = hgvsg
                    novelVars << var
                }
            }
        }

        //  Look up variants from cache first
        //
        List nvs = []
        if ( useCache )
            nvs = notInCache( code, novelVars.hgvsg )

        if ( nvs )
        {
            for ( var in novelVars )
            {
                jb(var)

                Map params =    [
                                data_source:        code,
                                hgvsg:              var.hgvsg,
                                attr:               'mut_map',
                                value:              jb.toString(),
                                version:            'R17',
                                gene:               'TP53',
                                hgvsc:              var.c_description,
                                hgvsp:              var.ProtDescription
                                ]

                List ids = saveValueMap( params )
                if ( ! ids ) log.warn( "Couldn't add ${params}")
                nadd += ids.size()
            }
        }

        return nadd
    }
}
