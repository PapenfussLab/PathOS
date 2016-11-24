/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

import groovy.util.logging.Log4j

import java.text.SimpleDateFormat

/**
 * A class for Date manipulation utilities
 *
 * Author: Ken Doig
 * Date: 30/07/13
 * Time: 9:59 AM
 */

@Log4j
class DateUtil
{
    /**
     * Parse a date safely catching exceptions
     *
     * @param   sdf         SimpleDateFormat object with date format
     * @param   rawdate     date to parse
     * @return              parsed date or null if it failed
     */
    static String safeParse( SimpleDateFormat sdf, String rawDate )
    {
        String parseDate = null
        try
        {
            parseDate = sdf.parse( rawDate )
        }
        catch (Exception ex )
        {
            log.error( "Couldn't parse data [${rawDate}] " + ex )
            parseDate = null
        }

        return parseDate
    }

    /**
     * Parse a date safely catching exceptions
     *
     * @param   sdf         SimpleDateFormat object with date format
     * @param   rawdate     date to parse
     * @return              parsed date or null if it failed
     */
    static Date dateParse( SimpleDateFormat sdf, String rawDate )
    {
        Date parseDate = null
        try
        {
            parseDate = sdf.parse( rawDate )
        }
        catch (Exception ex )
        {
            log.error( "Couldn't parse data [${rawDate}] " + ex )
            parseDate = null
        }

        return parseDate
    }
}
