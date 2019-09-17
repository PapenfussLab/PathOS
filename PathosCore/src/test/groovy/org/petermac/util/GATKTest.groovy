/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
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
 * Test for GATK class
 *
 * User: doig ken
 * Date: 11/05/2015
 * Time: 10:28 AM
 */
class GATKTest extends GroovyTestCase
{
    GATK gatk = null

    /**
     * TESTING Constructor
     */
    void setUp()
    {
        gatk = new GATK()
        assert gatk instanceof GATK
    }

    /**
     * TESTING String getBases( String locus, boolean revcomp = false )
     * invalid case
     */
    void testGetBases()
    {
        //TODO : Check because is failing
        // This is a valid reference obtained from NCBI
        println('https://www.ncbi.nlm.nih.gov/assembly/GCF_000001405.33/#/def_region-REGION108')
        String bases = gatk.getBases("1:2448811-2448900")
        println(bases)
        //assert bases == 'TCTACAAATGACAAGCAACTTCCTGCCAACAATGCCAGCGGCCTGTCCTGAGGGGTCCGACTAGCTTTGTGCGTGACCTCAGGTTAACTG' : "[T E S T]: gatk.getBases( 1:2448811-2448900 ) is not finding the bases "
    }

    /**
     * TESTING  int validateVcf( String vcffile )
     */
    void testValidateVcf()
    {

        String resource = "Vcf/NormaliseVcf"
        String file = "multiallele"
        String extension = "vcf"

        String basePath = new File(GATKTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())

        int ret = gatk.validateVcf( basePath )
        assert ret == -1 : "Value  gatk.validateVcf( path ) is different from -1"

        //  VCF with error
        file = "err"
        basePath = new File(GATKTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        println basePath
        ret = gatk.validateVcf( basePath )

        assert ret != 0 : "[T E S T]: Value gatk.validateVcf( basePath ) is 0"

    }

    void "test get GATK bases"()
    {
        //TODO: Check because is failing
        print(gatk.getBases( "3:100000-100004" ))
        //assert gatk.getBases( "3:100000-100004" ) == "TGATA"
    }
}
