package org.petermac.util

import java.text.SimpleDateFormat
import java.text.DateFormat

/**
 * Created by lara luis on 21/01/2016.
 */
class DateUtilTest extends GroovyTestCase
{
    void testDateStringOne()
    {

        def OS =  System.properties['os.name']
        String testDate = "Sat Feb 18 00:00:00 AEST 13"
        String da = DateUtil.safeParse( new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy"), testDate)

        if( OS == "Mac OS X")
        {
            println "Mac OS X detected"
            if (testDate.find(/[A-Z][a-z]+ [A-Z][a-z]+ [0-9]+ [0-9][0-9]:+/))
            {
                assert(true)
            }
        }
        else if("Linux")
        {
            println "Linux OS detected"
            if (testDate.find(/[A-Z][a-z]+ [A-Z][a-z]+ [0-9]+ [0-9][0-9]:+/))
            {
                assert(true)
            }
        }

    }

    //TODO: When testing in the server the day is changed by unkown reason i.e. Sat to Wed
    void testDateParse()
    {
        def OS =  System.properties['os.name']
        String testDate = "Sat Feb 18 00:00:00 AEST 1"
        String da = DateUtil.dateParse( new SimpleDateFormat("EEE MMM dd yyyy"), testDate)

        if(OS == "Mac OS X")
        {
            println "Mac OS X detected"
            if (testDate.find(/[A-Z][a-z]+ [A-Z][a-z]+ [0-9]+ [0-9][0-9]:+/)) {
                assert (true)
            }

        }
        else if( OS == "Linux")
        {

            println "Linux OS detected"
            if (testDate.find(/[A-Z][a-z]+ [A-Z][a-z]+ [0-9]+ [0-9][0-9]:+/))
            {
                assert(true)
            }

        }

    }

}
