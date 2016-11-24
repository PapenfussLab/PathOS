package org.petermac.util

/**
 * Created by lara luis on 25/01/2016.
 */
class LogTestTest extends GroovyTestCase
{
    /**
     * TESTING def dosome()
     */
    void testlogTest()
    {
        def lt = new LogTest()
        assert lt instanceof LogTest :"[T E S T]: cannot create instance LogTests()"
        lt.dosome()

    }
}
