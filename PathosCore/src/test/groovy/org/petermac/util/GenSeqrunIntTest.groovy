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

        def env = System.getenv()

        DB = env["PATHOS_DATABASE"]

        if(DB == 'pa_local')
            isPaLocal = true
        else
            isPaLocal = false

    }

    void testGermlineVcfCompareSample__151217_M01053_0311_000000000_AJHWD_15K5074() { vcfCompareRegressionSample( '151217_M01053_0311_000000000-AJHWD', '15K5074') }

    void testGermlineVcfCompareSeqrun__151217_M01053_0311_000000000_AJHWD() { vcfCompareRegressionSeqrun( '151217_M01053_0311_000000000-AJHWD') }

    void testGermlineVcfDbCheckSample_151217_M01053_0311_000000000_AJHWD_15K5074() { vcfDbCheckRegressionSample( DB, '151217_M01053_0311_000000000-AJHWD', '15K5074') }

    void testGermlineVcfDbCheckSeqrun__151217_M01053_0311_000000000_AJHWD() { vcfDbCheckRegressionSeqrun( '151217_M01053_0311_000000000-AJHWD') }

    void vcfCompareRegressionSeqrun( String seqrun )
    {
        String resource_EXP = "NGS/expect/${seqrun}"

        File expectedSeqrun = new File(GenSeqrunIntTest.getClass().getResource( "/${resource_EXP}" ).getPath())
        println expectedSeqrun

        assert expectedSeqrun.exists()

        expectedSeqrun.eachDir
                {
                    println "Checking ${it.name}"
                    vcfCompareRegressionSample( seqrun, it.name )
                }
    }

    //  Check expected VCF file matches actual VCF for seqrun and sample
    //
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

    //  Check all sample VCFs in a seqrun match the Database
    //
    void vcfDbCheckRegressionSeqrun( String seqrun )
    {
        String resource_ACT = "NGS/expect/${seqrun}"

        File expectedSeqrun = new File(GenSeqrunIntTest.getClass().getResource( "/${resource_ACT}" ).getPath())

        assert expectedSeqrun.exists()

        expectedSeqrun.eachDir
                {
                    println "Checking ${it.name}"
                    vcfDbCheckRegressionSample( DB, seqrun, it.name )
                }
    }

    //  Check VCF file matches Database for seqrun and sample
    //
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

        assert "rows=${nrow} vars=${vdc.totvar}"== "rows=140 vars=0"

        //assert vdc.toterr == 0 : "Errors found between DB [${dbname}] and VCF [$expectVcf]"
    }
}
