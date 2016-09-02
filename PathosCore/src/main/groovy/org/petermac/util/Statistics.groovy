/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

import static java.lang.Math.*
import static java.math.RoundingMode.CEILING

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Utilitiy class for basic statistics - could be replaced by library
 *
 * User: doig ken
 * Date: 18/11/2013
 * Time: 9:40 AM
 */
class Statistics
{
    static Double mean( Collection list )
    {
        list.with
        {
            scale(sum() / size())
        }
    }

    static Double stddev( Collection list )
    {
        if ( list.size() <= 1 ) return 0.0

        Double mean = mean(list)
        scale(sqrt(list.collect{ num -> pow(num - mean,2) }.sum() / (list.size() - 1)))
    }

    static String confidenceInterval(Collection list)
    {
        list.with
        {
            confidenceInterval( list, mean(), 2*stddev())
        }
    }

    static String confidenceInterval(Double mean, Double squireStdev) {
        "${scale(mean - squireStdev)}..${scale(mean + squireStdev)}"
    }

    static String stats(Collection list)
    {
        list.with
        {
            def mean  = mean(  list )
            def stdev = stddev( list )
            return  [
                    mean:       mean,
                    stddev:     stdev,
                    confint:    confidenceInterval( mean, 2*stdev )
                    ]
        }
    }

    static Double scale(BigDecimal value)
    {
        value.setScale(3, CEILING)
    }
}

