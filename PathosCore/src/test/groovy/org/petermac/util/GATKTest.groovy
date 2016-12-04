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

    void setUp()
    {
        gatk = new GATK()
    }

    // New reference genome
    void testGetBases()
    {

       // Fails
        String bases = new GATK().getBases( 'gi|428186265|gb|JH992965.1|:100-140' )	//'5:100000-100040'

        assert bases == 'GCTGCATCTTCGATGACTGCTGACAGCTCGCTGACGAGCTG'

        bases = gatk.getBases( 'gi|428186265|gb|JH992965.1|:500-540' )
        assert bases == 'CGATAGACGTGTTGCCGCCGTTGCACAGCCCCTCCATCTCC'
    }

    // New reference genome
    void testValidateVcf()
    {

        String resource = "Vcf/NormaliseVcf"
        String file = "multiallele"
        String extension = "vcf"

        String basePath = new File(GATKTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())

        int ret = gatk.validateVcf( basePath )
        assert ret == -1// 0

        //  VCF with error
        file = "err"
        basePath = new File(GATKTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        println basePath
        ret = gatk.validateVcf( basePath )

        assert ret != 0


    }


}
