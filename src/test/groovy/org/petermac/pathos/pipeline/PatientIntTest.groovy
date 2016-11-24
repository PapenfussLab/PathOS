/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 24/05/13
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */

package org.petermac.pathos.pipeline

import groovy.sql.Sql
import org.petermac.util.DbConnect

class PatientIntTest extends GroovyTestCase
{
    def db
    Sql sql
    def qry = '''
                select  distinct
                        patient,
                        sex,
                        urn,
                        dob
                from    mp_detente
                where   test_set = 'M965'
        '''

    // TODO: not used
    void setUp()
    {
//        db  = new DbConnect( 'pa_test' )
//        sql = db.sql()
    }

    void testCountRows()
    {
//        def rs = sql.rows(qry)
//
//        //  Current status of DB
//        //
//        assert 11 == rs.size()
    }

    void testRows()
    {
//        def rs = sql.rows(qry)
//
//        rs.each()
//        {
//            pat ->
//            assert pat.sex == 'M' || pat.sex == 'F'
//        }
    }
}