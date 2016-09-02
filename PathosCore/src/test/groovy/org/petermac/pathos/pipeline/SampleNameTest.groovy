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
    void testNormalise()
    {
        def sn = SampleName.normalise( "13k1234-20" )

        assert "13K1234" == sn, "Testing uppercase"

        sn = SampleName.normalise( "13l1234" )

        assert "13l1234" == sn, "Testing non-detente"

        sn = SampleName.normalise( " 13K1234" )

        assert " 13K1234" == sn, "Testing non-detente"
    }
}
