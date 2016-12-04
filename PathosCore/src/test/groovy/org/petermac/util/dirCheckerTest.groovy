/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 */

package org.petermac.util

/**
 * Created by seleznev andrei on 19/12/2014.
 */
class DirCheckerTest extends GroovyTestCase {

    void testDir()
    {
        def checker = new DirChecker()
        def ret = checker.validateSamplesInDir('/NOT_A_DIR/')

        assert ret.isEmpty()
    }

    void testDirs()
    {
        def checker = new DirChecker()
        System.getProperty("java.class.path", ".")
                .tokenize(File.pathSeparator).each
                {
                    String fp = new File(it).getParent()
                    assert checker.validateOne(fp) ==['BAM', 'IGV', 'VCF']
                }

    }

    void testOneDir()
    {
        String dirPath = DirCheckerTest.getClass().getResource( '/Pathology/14M5510' ).getPath()
        def checker = new DirChecker()
        def arrpath = dirPath.split('/')
        def samplename = arrpath[arrpath.size() - 1]

        assert checker.validateDirContents(dirPath, samplename, true) == []

    }

    void testvalidateAll()
    {
        String dirPath = DirCheckerTest.getClass().getResource( '/Pathology/14M5510' ).getPath()
        def checker = new DirChecker()
        assert checker.validateAll(dirPath) != [:]
    }

    //requires file from Pathology samples
    void testvalidateSampleDir()
    {
        String dirPath = DirCheckerTest.getClass().getResource( '/Pathology/14M5510' ).getPath()
        def checker = new DirChecker()
        assert checker.validateSampleDir(dirPath) == [:]

    }


}
