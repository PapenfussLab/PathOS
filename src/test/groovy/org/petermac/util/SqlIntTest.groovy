/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 24/05/13
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */

package org.petermac.util

import groovy.sql.Sql
import org.petermac.util.DbConnect

class SqlIntTest extends GroovyTestCase
{

    //Todo: legacy code
//    def db
//    Sql sql
//    def qry = '''
//        select	sample_name,
//        		gene,
//        		hgvsc,
//        		hgvsp,
//        		ifnull(alamut_class,'none') as alamut_class
//        from	seq_variant
//        where	sample_name = '12M2208'
//        '''
//
    void setUp()
    {
//        db  = new DbConnect( 'pa_test' )
//        sql = db.sql()
    }
//
   void testConnectMpTest()
    {
//        assertEquals('Assign DbConnect test', 'dbtest', db.schema )
//
//        def vars = sql.firstRow(
//                '''
//                select	count(*) as cnt
//                from	seq_variant as var
//                ''' )
//
//        assert 0 == vars.cnt
//    }
//
//    void testRows()
//    {
//        def rs = sql.rows(qry)
//
//        //  Only expecting a few reportable variant for this sample
//        //
//        int exp = 0     // Todo: waiting for all refseq to have a .version suffix
//        assertEquals( "Number of variants for sample", exp, rs.size())
//    }
//
//    void testConnection()
//    {
//        def c = sql.getConnection()
//        assert c.getClass() =~ 'com.mysql.jdbc.JDBC4Connection'
    }
}