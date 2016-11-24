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

    /**
     * Testing that connection and one or more tables exist
     * sql.rows('show tables')
     */
    void testTables()
    {
        def db = new DbConnect( DB )

        Sql sql = db.sql()

        def vars = sql.rows('show tables')

        assert vars.size() != 0 : "[T E S T]: no tables in databse ${DB}"

    }

    /**
     * TESTING Query
     *  SELECT COUNT(DISTINCT gene) as cnt  FROM cur_variant
     */
    void testConnectMpTest()
    {
        def db = new DbConnect( DB )

         assertEquals('Schemas are Different', 'dblive', db.schema )


        Sql sql = db.sql()

        def vars = sql.firstRow('''
                                    SELECT COUNT(DISTINCT gene) as cnt
                                    FROM cur_variant
                               '''
        )

            assert vars.cnt > 0 : "[T E S T]: SELECT COUNT(DISTINCT gene) as cnt FROM cur_variant returns 0"

    }

    /**
     * TESTING Query
     *  select	count(distinct variant) as cnt  from	seq_variant
     */
    void testConnectMpProd()
    {
        def db = new DbConnect( DB )

        assertEquals('[T E S T]: Schemas are Different', 'dblive', db.schema )


        Sql sql = db.sql()

        def vars = sql.firstRow(
                '''
                select	count(distinct variant) as cnt
                from	seq_variant
                ''' )

            assert vars.cnt > 0 : "[T E S T]: select count(distinct variant) as cnt from deq_variant returns 0"

    }

    /**
     * Testing Multiple Queries
     * 1) select	count(*) as cnt  from	seq_variant as var  where   variant regexp 'ins'
     * 2) select count(*) as cnt from seq_varaint as var where variant regexp 'del'
     * 3) elect count(*) as cnt from seq_variant as var  where   variant not regexp 'del' and  variant not regexp 'ins'
     */
    void testCountVariantTypes()
    {
        def db = new DbConnect( DB )


        Sql sql = db.sql()

        def vars = sql.firstRow('''
                                select	count(*) as cnt
                                from	seq_variant as var
                                where   variant regexp 'ins'
                                ''' )



        assert vars.cnt > 0 : "[T E S T]: select count(*) as cnt from seq_variant as var where varaint regexp 'ins' returns 0"


        vars = sql.firstRow(    '''
                                select	count(*) as cnt
                                from	seq_variant as var
                                where   variant regexp 'del'
                                ''' )


        assert vars.cnt > 0 : "[T E S T]: select count(*) as cnt from seq_varaint as var where variant regexp 'del' retunrs 0"



        vars = sql.firstRow(    '''
                                select	count(*) as cnt
                                from	seq_variant as var
                                where   variant not regexp 'del'
                                and     variant not regexp 'ins'
                                ''' )


        assert vars.cnt > 0 : "[T E S T]: select count(*) as cnt from seq_variant as var  where   variant not regexp 'del' and     variant not regexp 'ins' returns 0"

    }

    /**
     * TESTING Query
     *  select count(*) as cnt from seq_variant as var
     */
    void testConnectPrTest()
    {
        def db = new DbConnect( DB )


        assertEquals('[T E S T]: Schemas are Different', 'dblive', db.schema )


        Sql sql = db.sql()
        def vars = sql.firstRow(
                '''
                select	count(*) as cnt
                from	seq_variant as var
                ''' )

        println( "Found ${vars} variants")

        assert vars.cnt > 0 : "[T E S T]: select count(*) as cnt from seq_variant as var returns 0"

    }

    /**
     * TESTING Query
     *  select count(*) as cnt from cur_variant as var
     */
    void testConnectPrProd()
    {
        def db = new DbConnect( DB )

        assertEquals('[T E S T]: Schemas are Different', 'dblive', db.schema )

        Sql sql = db.sql()
        def vars = sql.firstRow(
                '''
                select	count(*) as cnt
                from	cur_variant as var
                ''' )

        println( "Found ${vars} variants")

        assert vars.cnt > 0 : "[T E S T]: select count(*) as cnt from cur_variant as var returns 0"

    }

    /**
     * Checking   Boolean valid( Boolean orm = false )
     */
    void testValid()
    {
        def db = new DbConnect( DB )
        assert db.valid() : "[T E S T] assert 1/2 : Invalid connection to DB"
        assert db.valid(true) : "[T E S T] assert 2/2 : Invalid connection to DB"

    }

    /**
     * TESTING invalid connection
     * TODO: cant be tested original code has  System.exit(1) if invalid
     */
//    void testFailed_InputDB()
//    {
//       def Error = shouldFail { new DbConnect( "FAIL" )}
//    }

}