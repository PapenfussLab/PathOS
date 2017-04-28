package org.petermac.util

import java.text.SimpleDateFormat
import java.text.DateFormat

/**
 * Created by lara luis on 21/01/2016.
 */
class DateUtilTest extends GroovyTestCase
{

    /**
        TESTING class constructor
    */
    void testDateUtil()
    {
        def du = new DateUtil()
        assert du instanceof DateUtil : "[T E S T]: Class DateUtil() failed to start"
    }

    /**
     * TESTING static Date dateParse( SimpleDateFormat sdf, String rawDate )
     */
    void testDateParse()
    {

        String testDate = "Sat Feb 18 00:00:00 AEST 1"
        String da = DateUtil.dateParse( new SimpleDateFormat("EEE MMM dd yyyy"), testDate)

        assert da.contains( "AEST 1" ) : "[T E S T] assert 1/2: cannot find pattern AEST 1"
        assert da.contains("Feb 18") : "[T E S T] assert 2/2: cannot find pattern Feb 18"

    }

    /**
     * TESTING Failed dateParse( SimpleDateFormat sdf, String rawDate)
     */
    void testFailed_DateParse()
    {

        def out = DateUtil.dateParse( new SimpleDateFormat("EEE MMM dd yyyy"), "FAIL")
        println("Error mesage should be printed containing Couldn't parse data [FAIL] java.text.ParseException: Unparseable date: \"FAIL\"")
        assert out == null :"[T E S T]: Providing wrong input return a valie different from null"

    }
    /**
     * TESTING wrong input
     */
    void testFailed_InvalidCall()
    {

        def Error = shouldFail MissingMethodException, { DateUtil.dateParse( new SimpleDateFormat("EEE MMM dd yyyy") ) }
        assert Error.contains("No signature of method") :"[T E S T]: Output is generated without providing second input"

    }

}
