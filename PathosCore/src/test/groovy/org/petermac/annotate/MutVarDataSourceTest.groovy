/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.annotate

/**
 * Created for PathOS.
 *
 * Description:
 *
 *
 *
 * User: doig ken
 * Date: 08/01/2015
 * Time: 8:31 AM
 */
class MutVarDataSourceTest extends GroovyTestCase
{
    MutVarDataSource ds

    void setUp()
    {
       // ds = new MutVarDataSource('pa_local')
    }

    void testAddToCache()
    {
//        List vars = [ 'chr10:g.89720648del', 'chr4:g.55599436T>C' ]
//
//        List<Map> maps = ds.getValues( vars )
//        println maps
//        println maps.count {it}
//        if ( maps.count{it} != 2 )
//        {
//            ds.removeFromCache( vars )
//            ds.addToCache( vars )
//            maps = ds.getValues( vars )
//        }
//
//        assert maps.count{it} == 2
//
//        for ( map in maps )
//        {
//            println "Key ${map.hgvsg} ${map.gene} ${map.status}"
//        }
//
//        assert maps.hgvsg == vars
//        assert maps[0].gene == 'PTEN'
//        assert maps[1].gene == 'KIT'
    }
}
