package org.petermac.util
/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */


/**
 * Created for PathOS.
 *
 * Description:
 *
 *
 *
 * User: doig ken
 * Date: 22/11/2013
 * Time: 3:08 PM
 */
class DbCountTest extends GroovyTestCase
{

    String DB

    void setUp()
    {
        def env = System.getenv()

        DB = env["PATHOS_DATABASE"]

    }


    void testCountTables()
    {
        def dbc = new DbCount()
        Map m = dbc.countTables( DB, ['auth_user','patient','sample','seqrun','panel','seq_sample','seq_variant','variant','audit'] )

        assert m.auth_user >= 1 &&
                m.patient >= 1 &&
                m.sample >= 1 &&
                m.seq_sample >= 1 &&
                m.seq_variant >= 1 &&
                m.seqrun >= 1 &&
                m.size() >= 4

    }

    // This sis redundant, consider removing it
    void testCountUser()
    {
        def dbc = new DbCount()
        Map m = dbc.countTables( DB, ['auth_user','patient','sample','seqrun','panel','seq_sample','seq_variant','variant','audit'] )

        println( m )

        assert m.size() >= 4
        assert m.sample >= 1
    }

    void testDiff()
    {
        Map m = new DbCount().compare( DB, DB, ['patient','sample','seqrun','panel','seq_sample','seq_variant','variant','audit'] )

        for ( tbl in m )
        {
            def val = tbl.value as Map
            String s = sprintf( "Table: %-20s %8d %8d    %+3.1f%%", tbl.key, val.rows, val.diff, val.pct)
            println( s )
        }

       // assert m.size() == 8

        assert m.audit.rows >= 1 &&
                m.audit.diff != null    &&
                m.audit.pct != null   &&
                m.panel.rows >= 1  &&
                m.panel.diff != null    &&
                m.panel.pct != null   &&
                m.patient.rows >= 1  &&
                m.patient.diff != null    &&
                m.patient.pct != null   &&
                m.sample.rows >= 1  &&
                m.sample.diff != null    &&
                m.sample.pct != null   &&
                m.seq_sample.rows >= 1  &&
                m.seq_sample.diff != null    &&
                m.seq_sample.pct != null   &&
                m.seq_variant.rows >= 1  &&
                m.seq_variant.diff != null    &&
                m.seq_variant.pct != null   &&
                m.seqrun.rows >= 1  &&
                m.seqrun.diff != null    &&
                m.seqrun.pct != null   &&
                m.variant.rows >= 0  &&
                m.variant.diff != null    &&
                m.size() >= 6
                //m.variant.pct == "Infinity"   &&


     }
}