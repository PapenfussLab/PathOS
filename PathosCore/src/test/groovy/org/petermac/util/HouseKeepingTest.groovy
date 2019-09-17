package org.petermac.util

/**
 * Created by lara luis on 25/01/2016.
 */
class HouseKeepingTest extends GroovyTestCase
{
    //TODO Leave this test for later, wierd perfomance
    String DB
    static Locator loc  = Locator.instance
    void setUp()
    {
        def env = System.getenv()

        DB = loc.pathosEnv

    }
    /*  DEPRECATED
//    void testCheckifup()
//    {
//        def hk = new HouseKeeping()
//
//       // hk.checkifup("pa_test")
//        println(DB)
//        hk.checkifup("pa_dev", true)
//        //hk.checkifup("pa_reseach")
//    }

    void testCheckDir()
    {
        def hk = new HouseKeeping()

        hk.checkdir(["local","dev","PathOS","PathosCore","src","test","resources"] , true, false)

        hk.checkdir(["local","dev","PathOS","PathosCore","src","test","resources"] , false, false)
        // Set last to true for JIRA
        hk.checkdir(["local","dev","PathOS","PathosCore","src","test","resources"] , false, false)

    }

    void testFiltercheck()
    {
        def hk = new HouseKeeping()
        hk.filtercheck(DB, true, false)
        //Todo: fix filtercheck?
        //assert false
    }


    //Takes a while
    void testBackup()
    {
        def hk = new HouseKeeping()
       // hk.backup(DB)
    }

    void testArchive()
    {
        def hk = new HouseKeeping()
       // hk.archive(DB)

    }

    */
}
