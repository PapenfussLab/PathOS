/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.pipeline

/**
 * Created for PathOS.
 *
 * Description:
 *
 *
 *
 * User: doig ken
 * Date: 10/02/2014
 * Time: 11:03 AM
 */
class SampleNameTest extends GroovyTestCase
{
    void testBaseName() {
        String sn

        sn = SampleName.baseName("000888")

        assert "000888" == sn, "Testing simple baseName"

        sn = SampleName.baseName("000888-1")

        assert "000888" == sn, "Testing replicate baseName"

        sn = SampleName.baseName("13k1234--080000-1")

        assert "13K1234--080000" == sn, "Testing replicate Tumour--Normal baseName"
    }

    void testDecomposeTumourNormal() {
        List tn
        
        tn = SampleName.decomposeTumourNormal("000888")

        assert null == tn, "Testing simple decomposeTumourNormal"

        tn = SampleName.decomposeTumourNormal("13k1234--080000")

        assert tn != null, "Testing decomposeTumourNormal on simple Tumour-Normal is non-null"
        assert tn.size() == 2, "Testing decomposeTumourNormal on simple Tumour-Normal has two components"
        assert "13K1234" == tn[0], "Testing decomposeTumourNormal on simple Tumour-Normal tumour component"
        assert "080000" == tn[1], "Testing decomposeTumourNormal on simple Tumour-Normal normal component"

        tn = SampleName.decomposeTumourNormal("13k1234--080000-3")

        assert tn != null, "Testing decomposeTumourNormal on replicate Tumour-Normal is non-null"
        assert tn.size() == 2, "Testing decomposeTumourNormal on replicate Tumour-Normal has two components"
        assert "13K1234" == tn[0], "Testing decomposeTumourNormal on replicate Tumour-Normal tumour component"
        assert "080000" == tn[1], "Testing decomposeTumourNormal on replicate Tumour-Normal normal component"
    }

    void testPosControl()
    {
        assert SampleName.isPosControl( "xxCONTROLyy" )
        assert SampleName.isPosControl( "xxCTRLyy" )
        assert SampleName.isPosControl( "xxNA12878yy" )
    }

    void testNegControl()
    {
        assert SampleName.isNegControl( "NTCxx" )
        assert ! SampleName.isNegControl( "xNTCxx" )
    }

    /**
     * Test sample normalisation
     */
    void testSampleName()
    {
        assert SampleName.clean('1?2/34_5.67()') == '1-2-34-5-67--'
    }
}
