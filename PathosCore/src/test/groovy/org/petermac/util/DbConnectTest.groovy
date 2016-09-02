package org.petermac.util

/**
 * Created by lara luis on 21/01/2016.
 */
class DbConnectTest extends GroovyTestCase
{

    void testTest()
    {
        assert DBConnection("pa_test" )
    }

    boolean DBConnection(String database)
    {
        def db        = new DbConnect( database )
        boolean valid =  db.valid()
        db.sql()
        return valid
    }
}
