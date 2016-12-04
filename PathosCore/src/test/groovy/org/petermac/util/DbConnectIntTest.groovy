/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 10/04/13
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */

package org.petermac.util

import groovy.sql.Sql

class DbConnectIntTest extends GroovyTestCase
{

    String DB

    void setUp()
    {
        def env = System.getenv()

        DB = env["PATHOS_DATABASE"]


    }

    void testTables()
    {
        def db = new DbConnect( DB )

        Sql sql = db.sql()

        def vars = sql.rows('show tables')


        assert vars.Tables_in_dbtest.size() >= 10
        println vars.Tables_in_dbtest.contains("align_stats") &&
                    vars.Tables_in_dbtest.contains("seq_sample") &&
                    vars.Tables_in_dbtest.contains("seq_variant") &&
                    vars.Tables_in_dbtest.contains("transcript") &&
                    vars.Tables_in_dbtest.size() >= 25



    }

    void testConnectMpTest()
    {
        def db = new DbConnect( DB )

         assertEquals('Assign DbConnect prod', 'dbtest', db.schema )


        Sql sql = db.sql()

        def vars = sql.firstRow('''
                                    SELECT COUNT(DISTINCT gene) as cnt
                                    FROM cur_variant
                               '''
        )

            assert vars.cnt == 0

    }

    void testConnectMpProd()
    {
        def db = new DbConnect( DB )

        assertEquals('Assign DbConnect prod', 'dbtest', db.schema )


        Sql sql = db.sql()

        def vars = sql.firstRow(
                '''
                select	count(distinct variant) as cnt
                from	seq_variant
                ''' )

            assert vars.cnt == 1


    }

    void testCountVariantTypes()
    {
        def db = new DbConnect( DB )


        Sql sql = db.sql()

        def vars = sql.firstRow('''
                                select	count(*) as cnt
                                from	seq_variant as var
                                where   variant regexp 'ins'
                                ''' )



        assert vars.cnt == 0


        vars = sql.firstRow(    '''
                                select	count(*) as cnt
                                from	seq_variant as var
                                where   variant regexp 'del'
                                ''' )


        assert vars.cnt == 0



        vars = sql.firstRow(    '''
                                select	count(*) as cnt
                                from	seq_variant as var
                                where   variant not regexp 'del'
                                and     variant not regexp 'ins'
                                ''' )


        assert vars.cnt == 1



    }

    void testConnectPrTest()
    {
        def db = new DbConnect( DB )


        assertEquals('Assign DbConnect test', 'dbtest', db.schema )


        Sql sql = db.sql()
        def vars = sql.firstRow(
                '''
                select	count(*) as cnt
                from	seq_variant as var
                ''' )

        println( "Found ${vars} variants")

        assert vars.cnt == 1

    }

    void testConnectPrProd()
    {
        def db = new DbConnect( DB )


        assertEquals('Assign DbConnect test', 'dbtest', db.schema )


        Sql sql = db.sql()
        def vars = sql.firstRow(
                '''
                select	count(*) as cnt
                from	cur_variant as var
                ''' )

        println( "Found ${vars} variants")


        assert vars.cnt == 0

    }

}