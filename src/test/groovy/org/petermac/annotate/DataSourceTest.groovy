/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.annotate

import groovy.json.JsonBuilder

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Test DataSource cache operations
 *
 * User: Ken Doig
 * Date: 07/01/2015
 * Time: 9:47 PM
 */
class DataSourceTest  extends GroovyTestCase
{
    def ds

    void setUp()
    {
       // ds = new DataSource('pa_test')
    }

    void testSaveValue()
    {
        //  Add to cache
        //
       // ds.saveValue( DataSource.DS.Mutalyzer.code(), 'test', 'mut', 'xxx' )

        //  Check it got added
        //
//        List nic = ds.notInCache( DataSource.DS.Mutalyzer.code(), ['test'] )
//        assert( ! nic.size())

        //  Delete it
        //
       // ds.removeFromCache( DataSource.DS.Mutalyzer.code(), ['test'] )

        //  Check its gone
        //
//        nic = ds.notInCache( DataSource.DS.Mutalyzer.code(), ['test'] )
//        assert( nic == ['test'])
    }

    void testGetValue()
    {
        //  Add to cache
        //
        //ds.saveValue( DataSource.DS.Mutalyzer.code(), 'test', 'mut', 'xxx' )

        //  Check it got added
        //
//        String val = ds.getValue( DataSource.DS.Mutalyzer.code(), 'test' )
//        assert val == 'xxx'

        //  Delete it
        //
       // ds.removeFromCache( DataSource.DS.Mutalyzer.code(), ['test'] )
    }

    void testGetValues()
    {
        //  Add to cache
        //
//        ds.saveValue( DataSource.DS.Mutalyzer.code(), 'test1', 'mut', 'x1' )
//        ds.saveValue( DataSource.DS.Mutalyzer.code(), 'test2', 'mut', 'x2' )

        //  Check it got added
        //
        //List vals = ds.getValues( DataSource.DS.Mutalyzer.code(), ['test1','test2'] )
        //assert vals == ['x1','x2']

        //  Delete it
        //
       // ds.removeFromCache( DataSource.DS.Mutalyzer.code(), ['test1'] )

        //  Check it got added
        //
//        vals = ds.getValues( DataSource.DS.Mutalyzer.code(), ['test1','test2'] )
//        assert vals == [null,'x2']

        //  Delete it
        //
       // ds.removeFromCache( DataSource.DS.Mutalyzer.code(), ['test2'] )
    }

    void testJson()
    {
//        def jb = new JsonBuilder()
//
//        Map map = ['num':123, 'list':[1,2,3], 'str':'NM_1234.3:c.12-34C>G']
//
//        jb(map)
//        def strmap = jb.toString()
//        println strmap
//        println strmap.getClass()
//
//        def js = new groovy.json.JsonSlurper()
//        def newmap = js.parseText(strmap)
//
//        assert newmap == map
    }

    void testSaveValueMap()
    {
        //  Add to cache
        //
//        Map params =    [
//                            data_source:        DataSource.DS.Mutalyzer.code(),
//                            hgvsg:              'test',
//                            attr:               'mut',
//                            value:              'a value',
//                            hgvsc:              'c.123C>G',
//                            hgvsp:              'p.V600E',
//                            version:            'v0.9999',
//                            gene:               'BRAF'
//                        ]
//
//        ds.saveValueMap( params )
//
//        //  Add another
//        //
//        ds.saveValueMap( [data_source: DataSource.DS.VEP.code(), hgvsg: 'test', attr: 'mut', value: 'another value'] )
//
//        //  Check it got added
//        //
//        List nic = ds.notInCache( DataSource.DS.Mutalyzer.code(), ['test'] )
//        assert( ! nic.size())
//        nic = ds.notInCache( DataSource.DS.VEP.code(), ['test'] )
//        assert( ! nic.size())
//
//        //  Check it got added
//        //
//        List vals = ds.getValues( DataSource.DS.Mutalyzer.code(), ['test'] )
//        assert vals == ['a value']
//
//        //  Delete it
//        //
//        ds.removeFromCache( DataSource.DS.Mutalyzer.code(), ['test'] )
//        ds.removeFromCache( DataSource.DS.VEP.code(), ['test'] )
//
//        //  Check its gone
//        //
//        nic = ds.notInCache( DataSource.DS.Mutalyzer.code(), ['test'] )
//        assert( nic == ['test'])
    }

    //  Test that duplicate messages work
    //
    void testDuplicate()
    {
        //  Add record
        //
//        ds.saveValueMap( [data_source: DataSource.DS.VEP.code(), hgvsg: 'test', attr: 'mut', value: 'another value'] )
//
//        //  Check it got added
//        //
//        List nic = ds.notInCache( DataSource.DS.VEP.code(), ['test'] )
//        assert( ! nic.size())
//
//        //  Add duplicate
//        //
//        def res = ds.saveValueMap( [data_source: DataSource.DS.VEP.code(), hgvsg: 'test', attr: 'mut', value: 'another value'] )
//        assert res == []
//
//        //  Delete it
//        //
//        ds.removeFromCache( DataSource.DS.VEP.code(), ['test'] )
    }
}
