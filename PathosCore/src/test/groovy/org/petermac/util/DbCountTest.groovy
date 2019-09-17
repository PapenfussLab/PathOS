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
    static Locator loc  = Locator.instance
    String DB

    void setUp()
    {
        def env = System.getenv()

        DB = loc.pathosEnv

    }

//    /**
//     * TESTING Map countTables( String dbase, List tables = [] )
//     */
//    void testCountTables()
//    {
//        def dbc = new DbCount()
//        Map m = dbc.countTables( DB, ['auth_user','patient','sample','seqrun','panel','seq_sample','seq_variant','variant','audit'] )
//
//
//        assert m.auth_user >= 1 &&
//                m.patient >= 1 &&
//                m.sample >= 1 &&
//                m.seq_sample >= 1 &&
//                m.seq_variant >= 1 &&
//                m.seqrun >= 1 &&
//                m.size() >= 4 : "[T E S T ]: One of the following tables is empty [auth_user, patient, sample, seq_sample, seq_variant, seqrun]  "
//
//    }

//    /**
//     * TESTING Map countTables( String dbase, List tables = [] ) in a different setting
//     */
//    void testCountUser()
//    {
//        def dbc = new DbCount()
//        Map m = dbc.countTables( DB, ['auth_user','patient','sample','seqrun','panel','seq_sample','seq_variant','variant','audit'] )
//
//        assert m.size() >= 4 : "[T E S T]: Map contains less than 4 tables ${m} "
//        assert m.sample >= 1 : "[T E S T]: Sample table is empty "
//    }

    /**
     * TESTING
     */
    void testDiff()
    {
        Map m = new DbCount().compare( DB, DB, ['patient','sample','seqrun','panel','seq_sample','seq_variant','variant','audit'] )

        for ( tbl in m )
        {
            def val = tbl.value as Map
            String s = sprintf( "Table: %-20s %8d %8d    %+3.1f%%", tbl.key, val.rows, val.diff, val.pct)
            assert tbl.key != null : "[T E S T]: null object when there should not be, ${tbl.key}"
            assert val.rows != null : "[T E S T]: null object when there should not be, ${val.rows}"
            assert val.pct != null : "[T E S T]: mull object ehn there should not be ${val.pct}"
        }

        assert m.size() != 0 : "[T E S T]: DbCount().compare( DB, DB, ['patient','sample','seqrun','panel','seq_sample','seq_variant','variant','audit'] ) is empty"

     }
}