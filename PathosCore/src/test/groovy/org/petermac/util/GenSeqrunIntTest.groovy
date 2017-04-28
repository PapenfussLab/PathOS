/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
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
 * Date: 15/01/2016
 * Time: 2:16 PM
 */

// SEEMS TO CALL ONLY UTIL Stuff
class GenSeqrunIntTest extends GroovyTestCase
{

    String DB
    Boolean isPaLocal

    VcfDbCheck vdc = null

    void setUp()
    {
        vdc = new VcfDbCheck()
        assert vdc instanceof VcfDbCheck :"[T E S T]: Fail in loading the constructor."

        def env = System.getenv()

        DB = env["PATHOS_DATABASE"]

        if(DB == 'pa_local')
            isPaLocal = true
        else
            isPaLocal = false

    }

    /**
     * TESTING   static Map runVcfCompare( File qryf, File reff )
     * using multiple input paramters
     */
    void testGermlineVcfCompareSample__151217_M01053_0311_000000000_AJHWD_15K5074() { vcfCompareRegressionSample( '151217_M01053_0311_000000000-AJHWD', '15K5074') }

    void testGermlineVcfCompareSeqrun__151217_M01053_0311_000000000_AJHWD() { vcfCompareRegressionSeqrun( '151217_M01053_0311_000000000-AJHWD') }

    void testGermlineVcfDbCheckSample_151217_M01053_0311_000000000_AJHWD_15K5074() { vcfDbCheckRegressionSample( DB, '151217_M01053_0311_000000000-AJHWD', '15K5074') }

    void testGermlineVcfDbCheckSeqrun__151217_M01053_0311_000000000_AJHWD() { vcfDbCheckRegressionSeqrun( '151217_M01053_0311_000000000-AJHWD') }

    /**
     * TESTING  static Map runVcfCompare( File qryf, File reff )
     * using multiple input paramters
     */
    void vcfCompareRegressionSeqrun( String seqrun )
    {
        String resource_EXP = "NGS/expect/${seqrun}"

        File expectedSeqrun = new File(GenSeqrunIntTest.getClass().getResource( "/${resource_EXP}" ).getPath())

        assert expectedSeqrun.exists() : "[T E S T]: File does not exist (${expectedSeqrun})"

        expectedSeqrun.eachDir
                {
                    println "Checking ${it.name}"
                    vcfCompareRegressionSample( seqrun, it.name )
                }
    }

    /**
     * TESTING CORE of static Map runVcfCompare( File qryf, File reff )
     */
    void vcfCompareRegressionSample( String seqrun, String sample )
    {
        String resource_ACT = "NGS/actual/${seqrun}/${sample}"
        String resource_EXP = "NGS/expect/${seqrun}/${sample}"
        String file = "15K5074"
        String extension ="vcf"


        File actualVcf = new File(GenSeqrunIntTest.getClass().getResource( "/${resource_ACT}/${file}.${extension}" ).getPath())
        File expectVcf = new File(GenSeqrunIntTest.getClass().getResource( "/${resource_EXP}/${file}.${extension}" ).getPath())

        if ( ! actualVcf.exists() || ! expectVcf.exists())
        {
            println "Ignoring ${seqrun} ${sample}"
            return
        }

        Map comp = new VcfCompare().runVcfCompare( actualVcf, expectVcf )


        assert comp.fn == 0 : "No false negatives expected ${comp}"
        assert comp.fp == 0 : "No false positives expected ${comp}"
    }

    /**
     * TESTING CORE of static Map runVcfCompare( File qryf, File reff )
     */
    void vcfDbCheckRegressionSeqrun( String seqrun )
    {
        String resource_ACT = "NGS/expect/${seqrun}"

        File expectedSeqrun = new File(GenSeqrunIntTest.getClass().getResource( "/${resource_ACT}" ).getPath())

        assert expectedSeqrun.exists() : "It does not exist"

        expectedSeqrun.eachDir
                {
                    println "Checking ${it.name}"
                    vcfDbCheckRegressionSample( DB, seqrun, it.name )
                }
    }

    /**
     *
      TESTING
     * static private int checkVcfs( def seqrun, def sample, List<File> vcfs, String rdb, File errFile, boolean ignore, boolean annotate )
     */
    void vcfDbCheckRegressionSample( String dbname, String seqrun, String sample )
    {


        String resource_EXP = "NGS/expect/${seqrun}/${sample}"
        String file = "15K5074"
        String extension ="vcf"


        File expectVcf = new File(GenSeqrunIntTest.getClass().getResource( "/${resource_EXP}/${file}.${extension}" ).getPath())

        if ( ! expectVcf.exists())
        {
            println "Ignoring ${seqrun} ${sample}"
            return
        }

        def nrow = vdc.checkVcfs( seqrun, sample, [expectVcf], dbname, new File('err'), true, false )

        assert "rows=${nrow} vars=${vdc.totvar}"== "rows=140 vars=0" :"rows=140 vars=0 is not the output of ${nrow} and ${vdc.totvar}"

    }
}
