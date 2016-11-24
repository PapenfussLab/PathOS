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

        assert 50000.153 == Statistics.mean(l) : "Mean from Statistics.mean(l) is wrong"
    }

    void testSd()
    {
        def l = [ 1, 2, 3 ]

        assert 1.0 == Statistics.stddev(l) :"Standard Deviation form Statistics.stddev(l) is wrong"
    }

    void testQueue()
    {
        def list = []
        def q = list as Queue

        q << 1
        q << 2
        q << 3

        assert 1.0 == Statistics.stddev(q): "Standard Deviation from Statistics.stddev(q) is wrong "
    }

    void testCI()
    {
        def l = [ 1, 2, 3 ]
        assert Statistics.confidenceInterval(0.0, 1.0) == "-1.0..1.0" : "Confidence interval from Statistics.confidenceInterval(0.0, 1.0)  is wrong"
    }

    void testStats()
    {
        assert Statistics.stats([ 1, 2, 3 ]) == "{mean=2.0, stddev=1.0, confint=0.0..4.0}":"Summary statistics from Statistics.stats([ 1, 2, 3 ]) are wrong"

    }

    // it is required to delete a CI method that is not operational
}
