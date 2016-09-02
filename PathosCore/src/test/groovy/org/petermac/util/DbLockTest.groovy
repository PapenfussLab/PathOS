package org.petermac.util

/**
 * Created by lara luis on 22/01/2016.
 */
class DbLockTest extends GroovyTestCase
{

    String DB

    void setUp()
    {
        def env = System.getenv()

        DB = env["PATHOS_DATABASE"]

    }

    void testHasLock()
    {

        CleanUp()

        def dbc = new DbLock(DB)
        dbc.createLock()

        if(dbc.hasLock())
        {
            println "Reseting Lock ${dbc.hasLock()}, for testing..."
            dbc.clearLock(dbc.hasLock())
        }
        else
        {
            println "No Lock..."
        }

        CleanUp()
    }

    void testClearLock()
    {

        def dbc = new DbLock(DB)
        dbc.createLock()
        dbc.setLock()

        def m = dbc.clearLock(dbc.hasLock())
        assert m ==[:]
        CleanUp()

    }

    void testSetLock()
    {
        def dbc = new DbLock(DB)
        dbc.createLock()

        assert dbc.setLock().keySet().size() == 4

        CleanUp()
    }

    void CleanUp()
    {
        def dbc = new MysqlCommand(DB)
        println "RMOVING DB LOCK...."
        dbc.run( "DROP TABLE IF EXISTS db_lock" )
    }



}
