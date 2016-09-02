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
 * Date: 18/11/2013
 * Time: 10:34 AM
 */
class StatisticsTest extends GroovyTestCase
{

    void testMean()
    {
        def l = [ 1, 2, 3 ]

        assert 2 == Statistics.mean(l)

        l = [100000,20000.2345,30000.22222]

        assert 50000.153 == Statistics.mean(l)
    }

    void testSd()
    {
        def l = [ 1, 2, 3 ]

        assert 1.0 == Statistics.stddev(l)
    }

    void testQueue()
    {
        def list = []
        def q = list as Queue

        q << 1
        q << 2
        q << 3

        assert 1.0 == Statistics.stddev(q)
    }

    void testCI()
    {
        def l = [ 1, 2, 3 ]
        assert Statistics.confidenceInterval(0.0, 1.0) == "-1.0..1.0"
    }

    void testStats()
    {
        assert Statistics.stats([ 1, 2, 3 ]) == "{mean=2.0, stddev=1.0, confint=0.0..4.0}"

    }

    // it is required to delete a CI method that is not operational
}
