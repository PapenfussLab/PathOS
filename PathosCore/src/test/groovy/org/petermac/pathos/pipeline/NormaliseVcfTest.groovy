package org.petermac.pathos.pipeline

import org.petermac.util.DbConnect
import org.petermac.util.VcfCompare

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Test the NormaliseVcf Tool by running a set of VCF files through it
 * and comparing expected output with actual generated vcfs
 *
 * User: Ken Doig
 * Date: 14-May-2015
 */
class NormaliseVcfTest extends GroovyTestCase
{
    NormaliseVcf nv = new NormaliseVcf()
    boolean noCache = true

    String DB

    void setUp()
    {
        def env = System.getenv()
        //println "ENV ${env}"
        //println "PROP ${System.properties}"

        DB = env["PATHOS_DATABASE"]
        if(DB == null)
            DB = "pa_local"

    }


    void testNormaliseVcf1() { assert testRunNorm("Vcf/NormaliseVcf","test1","vcf", noCache) }
    void testNormaliseVcf2() { assert testRunNorm("Vcf/NormaliseVcf","test2","vcf", noCache) }
    void testNormaliseVcf3() { assert testRunNorm("Vcf/NormaliseVcf","test3","vcf", noCache) }
    void testNormaliseVcf4() { assert testRunNorm("Vcf/NormaliseVcf","test4","vcf", noCache) }
    void testNormaliseVcf5() { assert testRunNorm("Vcf/NormaliseVcf","test5","vcf", noCache) }
    void testNormaliseVcf6() { assert testRunNorm("Vcf/NormaliseVcf","test6","vcf", noCache)}
    void testNormaliseVcf7() { assert testRunNorm("Vcf/NormaliseVcf","test7","vcf", noCache)}
    void testNormaliseVcf8() { assert testRunNorm("Vcf/NormaliseVcf","test8","vcf", noCache) }
    void testNormaliseVcf9() { assert testRunNorm("Vcf/NormaliseVcf","test9","vcf", noCache) }
    void testNormaliseVcf10() { assert testRunNorm("Vcf/NormaliseVcf","test10","vcf", noCache) }
    void testNormaliseVcf11() { assert testRunNorm("Vcf/NormaliseVcf","test11","vcf", noCache) }
    void testNormaliseVcf12() { assert testRunNorm("Vcf/NormaliseVcf","test12","vcf", noCache)}
    void testNormaliseVcf13() { assert testRunNorm("Vcf/NormaliseVcf","test13","vcf", noCache) }
    void testNormaliseVcf14() { assert testRunNorm("Vcf/NormaliseVcf","test14","vcf", noCache) }
    void testNormaliseVcf15() { assert testRunNorm("Vcf/NormaliseVcf","test15","vcf", noCache) }
    void testNormaliseVcf16() { assert testRunNorm("Vcf/NormaliseVcf","test16","vcf", noCache) }
    void testNormaliseVcfGeneral() { assert testRunNorm("Vcf/NormaliseVcf","testGeneral","vcf", noCache) }
    void testNormaliseVcfSuper() { assert testRunNorm("Vcf/NormaliseVcf","testSuper","vcf", noCache) }

    //  Test case of two equivalent variants to be merged and both being renamed from INS to DUP
    //
    void testNormaliseVcfMergeAndRename()
    {
        //  Empty cache for this test
        //
        def db = new DbConnect( DB )
        db.sql().execute( "delete from ano_variant where hgvsg regexp 'chr17:g.295459[89]';" )

        //  Run with empty cache first
        //
        assert testRunNorm("Vcf/NormaliseVcf","testMerge","vcf", noCache )

        //  Run again with populated cache
        //
        assert testRunNorm("Vcf/NormaliseVcf","testMerge","vcf", noCache )
    }


    boolean testRunNorm(String resource, String file,String extension, boolean nocache = true )
    {
        def mut = new Mutalyzer( )
        assert mut.ping()

        def actf = new File(NormaliseVcfTest.getClass().getResource( "/${resource}/${file}.actual.${extension}" ).getPath())
        def actf_out = new File("${actf.getParent()}/${file}.out.${extension}")

        // Don't provide the same file as output, it crashes and the file is LOST
        assert nv.normaliseVcf( actf, actf_out, DB, nocache )

        Map res = VcfCompare.runVcfCompare( actf_out, new File("${actf.getParent()}/${resource}/${file}.expected.${extension}") )

        assert res.fn == null //0
        assert res.fp == null //0

        return true
    }


}
