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

    void testPathosHome() {
        assert '../PathOSHome' == loc.pathos_home // should be /usr/local/dev/Pathos ?
        File basePath = new File(loc.pathos_home)
        //assert basePath.exists()
    }

    void testEtcDir()
    {
        // /usr/local/dev/PathOS/PathosCore/src/etc
        assert '../PathOSHome/etc/' == loc.etcDir
        File basePath = new File( loc.etcDir )
        // assert basePath.exists()
    }

    void testSamDir()
    {
        assert '/pathology/NGS/Samples/Molpath/' == loc.samDir // there is no folder named pathology
        File basePath = new File(loc.samDir)
        //assert basePath.exists()
    }

    //AS: disabled as this should run as a test user "Tester"
    //void testDBUName()
    // {
    //     assert loc.prop.get('db.username') == 'bioinformatics'
    //}

    // This required to modify the properties file to create the path
    void testBackupDir()
    {
        assert loc.backupDir('')  == '/pathology/NGS/PathOS/Backup/'
    }


}
