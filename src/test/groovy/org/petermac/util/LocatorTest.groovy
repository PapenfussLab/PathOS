/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util


/**
 * Created for PathOS.
 *
 * Description:
 *
 *
 *
 * User: doig ken
 * Date: 18/10/2013
 * Time: 4:24 PM
 */
class LocatorTest extends GroovyTestCase {
    Locator loc

    void setUp() {
        loc = Locator.getInstance()
    }

    /**
     * TESTING static String pathos_home = '/pathology/NGS/PathOS'
     */
    void testPathosHome() {
        def env = System.getenv()
        def e = env["PATHOS_DATABASE"]
        assert loc.pathos_home.contains(e):"[T E S T]: not in the home dir"
    }

    /**
     * TESTING static String etcDir
     */
    void testEtcDir()
    {

        assert loc.etcDir.contains("/etc/"):"[T E S T]: invalid association of /etc/"
    }

    /**
     * TESTING static String samDir
     */
    void testSamDir()
    {
        assert loc.samDir.contains("Samples"):"[T E S T]: Incorrect samples dir"
    }

    /**
     * TESTING static String samDir
     */
    void testBackupDir()
    {
        assert loc.backupDir().contains('Backup') : "[T E S T]: Backup dir not assigned"
    }


}
