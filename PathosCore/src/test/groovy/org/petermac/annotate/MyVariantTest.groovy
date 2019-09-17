/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.annotate

import org.apache.log4j.Level
import org.apache.log4j.Logger

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Test MyVariant class methods
 *
 * User: doig ken
 * Date: 24/02/2017
 * Time: 5:15 PM
 */
class MyVariantTest extends GroovyTestCase
{
    void testPing()
    {
//        Logger.getRootLogger().setLevel(Level.DEBUG)

        assert MyVariant.ping()
    }

    void testBatch()
    {
//        Logger.getRootLogger().setLevel(Level.DEBUG)

        List vars = [ 'chr1:g.35367C>T', 'chr7:g.55241707G>T', 'chr16:g.28883241A>G' ]
        List<Map> result = MyVariant.submitBatch( vars, ['all'] )

        assert result?.query == vars

        //  Check we got a Map back for each query variant
        //
        for ( var in vars )
        {
            def v = result.find { v -> v.query == var }
            assert v

            println v.keySet()
        }
    }

    void testFields()
    {
        List vars = [ 'chr16:g.28883241A>G' ]
        List flds = [ 'dbnsfp.genename' ]

        List<Map> result = MyVariant.submitBatch( vars, flds )

        Map varano = result.find { Map var -> var.query == vars[0]}
        assert varano?.dbnsfp?.genename == 'SH2B1'
        assert varano?.cosmic?.genename == null
    }

    void testSubmit()
    {
        //  Generate 3000 dummy variants
        //
        List vars = []
        for ( int i=0; i < 3000; ++i )
        {
//            vars << "chr1:g.${35000+i}insA"         // use an insert so we dont need reference genome
            vars << "chr7:g.${140453136+i}A>T"        // chr7:g.140453136A>Tuse and insert so we dont need reference genome
        }

        //  Get annotations
        //
        List<Map> result = MyVariant.submit( vars )

        assert result?.query == vars                // take a slice of List of Maps

        //  Check we got a Map back for each query variant
        //
        for ( var in vars )
        {
            def v = result.find { v -> v.query == var }
            assert v
            println( "${var} cons=${v.cadd?.consequence}")
        }
    }

    void testPrint()
    {
        List vars = [ 'chr16:g.28883241A>G' ]       // SH2B1: p.Thr484Ala
             vars = [ 'chr7:g.140453136A>T' ]       // BRAF: V600E

        //  Get annotations
        //
        List<String> result = MyVariant.prettyPrint( vars, 50 )

        assert  result
        println result[0]
    }

    void testPrintFields()
    {
        List vars   = [ 'chr16:g.28883241A>G', 'chr7:g.140453136A>T' ]       // SH2B1: p.Thr484Ala, BRAF: V600E
        List fields = [ 'dbnsfp.genename', 'cadd.consequence', 'cadd.rawscore' ]

        //  Get annotations
        //
        List<String> result = MyVariant.prettyPrint( vars, 50, fields )

        assert result.size() == 2
        assert result[1] == "\nmyv._id:                                          chr7:g.140453136A>T\n" +
                            "myv.query:                                        chr7:g.140453136A>T\n" +
                            "myv.cadd._license:                                http://bit.ly/2TIuab9\n" +
                            "myv.cadd.consequence:                             NON_SYNONYMOUS\n" +
                            "myv.cadd.rawscore:                                6.641785\n" +
                            "myv.dbnsfp.genename:                              BRAF\n" +
                            "myv.dbnsfp._license:                              http://bit.ly/2VLnQBz"

        for ( var in result )
        {
            println var
        }
    }

    void testFlatMap()
    {
        List vars   = [ 'chr16:g.28883241A>G', 'chr7:g.140453136A>T' ]       // SH2B1: p.Thr484Ala, BRAF: V600E
        List fields = [ 'dbnsfp.genename', 'cadd.consequence', 'cadd.rawscore' ]

        List<Map> myv = MyVariant.submit( vars, fields )
        //  Get annotations
        //
        List<Map> result = MyVariant.flatMaps( myv, '' )

        assert  result.size() == 2

        assert result[0][ '_id'] == 'chr16:g.28883241A>G'
        assert result[0]['query'] == 'chr16:g.28883241A>G'
        assert result[0]['cadd.consequence'] == 'NON_SYNONYMOUS'
        assert result[0]['cadd.rawscore'] == '0.427697'
        assert result[0]['dbnsfp.genename'] == 'SH2B1'
    }

    void testLargeFlatMap()
    {
        List vars   = [ 'chr7:g.140453136A>T' ]       // SH2B1: p.Thr484Ala, BRAF: V600E
        List fields = [ 'dbnsfp', 'cadd' ]

        //  Get annotations
        //
        List<Map> myv = MyVariant.submit( vars, fields )

        //  Get annotations
        //
        List<Map> result = MyVariant.flatMaps( myv, '' )

        assert result.size() == 1
        assert result[0].size() == 268

        for ( kv in result[0] )
        {
            println sprintf( "%-50s%s", kv.key, kv.value)
        }
    }

    void testFlatten()
    {
        Map m = [ a:1, b:2, c:'letC' ]
        assert MyVariant.flatten( m, 'prefix' ) == [ 'prefix.a':"1", 'prefix.b':"2", 'prefix.c':"letC" ]

        m = [ a:1, b:2, c:'letC', d:[c1:4] ]
        assert MyVariant.flatten( m, '' ) == [ 'a':'1', 'b':'2', 'c':'letC', 'd.c1':'4' ]
    }
}
