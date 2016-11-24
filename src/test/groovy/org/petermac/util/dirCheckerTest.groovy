package org.petermac.util

/**
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 * Created by seleznev andrei on 19/12/2014.
 */
class DirCheckerTest extends GroovyTestCase {

    /**
     * TESTING public boolean isEmpty()
     */
    void testDir()
    {
        def checker = new DirChecker()
        def ret = checker.validateSamplesInDir('/NOT_A_DIR/')

        assert ret.isEmpty() : "[T E S T]: is NOT_A_DIR a dir?"
    }

    /**
     * TESTING List<String> validateOne(String dirpath)
     */
    void testDirs()
    {
        def checker = new DirChecker()
        System.getProperty("java.class.path", ".")
                .tokenize(File.pathSeparator).each
                {
                    String fp = new File(it).getParent()
                    assert checker.validateOne(fp) ==['BAM', 'IGV', 'VCF'] : "The list do not contain ['BAM', 'IGV', 'VCF']"
                }

    }

    /**
     * TESTING List<String> validateDirContents( dir, samplename, mut )
     * TODO: is '/Pathology/14M5510' a good path, maybe we need to check the resource folder.
     */
    void testOneDir()
    {
        String dirPath = DirCheckerTest.getClass().getResource( '/Pathology/14M5510' ).getPath()
        def checker = new DirChecker()
        def arrpath = dirPath.split('/')
        def samplename = arrpath[arrpath.size() - 1]

        assert checker.validateDirContents(dirPath, samplename, true) == [] : "[T E S T]: /Pathology/14M5510' It is not empty"

    }

    /**
     * TESTING  static List<String> validateDirContents( dir, samplename, mut )
     * TODO: is '/Pathology/14M5510' a good path, maybe we need to check the resource folder.
     */
    void testvalidateAll()
    {
        String dirPath = DirCheckerTest.getClass().getResource( '/Pathology/14M5510' ).getPath()
        def checker = new DirChecker()
        assert checker.validateAll(dirPath) != [:] : "[T E S T]: /Pathology/14M5510' The value is empty, it should contain something"
    }

    /**
     * TESTING HashMap validateSampleDir( String dirpath )
     * TODO: is '/Pathology/14M5510' a good path, maybe we need to check the resource folder.
     */
    void testvalidateSampleDir()
    {
        String dirPath = DirCheckerTest.getClass().getResource( '/Pathology/14M5510' ).getPath()
        def checker = new DirChecker()
        assert checker.validateSampleDir(dirPath) == [:] : "[T E S T]: /Pathology/14M5510' The value is not empty when it should be"

    }


}
