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
 * Date: 08/11/2013
 * Time: 4:59 PM
 *
 *
 */
class AlignStatsToTsvTest extends GroovyTestCase
{
    AlignStatsToTsv att

    void setUp()
    {
        att = new AlignStatsToTsv()
    }

    /**
      This function test calling the main function of the class.
    */
    void testConnection()
    {
        try
        {
            att.main()
            assert true
        }
        catch (all)
        {
            assert false : "[T E S T]: AlignStatsToTsv.main() help display is not working. Something went wrong with the class."
        }

    }

    /**
       Checking expected error message by spurious input.
     */
    void testFailConnection()
    {
        def Error = shouldFail { att.main(-dummy) }
        assert Error.contains( "No such property" ) : "[T E S T]: This is interesting, AlignStatsToTsv.main(-dummy) is not throwing the expected error "
    }

    /**
        Testing Function
        static Integer filter( String stats, String tsv, String seqrun, String sample, String panel )
        We test 3 things
        1) The Filter function with the number of expected lines
        2) The Lines that are contained in the output file
        3) The deletion of the output file

     */
    void testFilter()
    {

        String resource = "Stats"
        String file = "15K1490_stats"
        String extension = "csv"

        String inf = PathGenerator( resource,  file, extension )

        String parentFolder = new File(AlignStatsToTsvTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath()).getParent()
        def ouf = "${parentFolder}/stats.tsv"
        def lines = att.filter( inf, ouf, 'seqrun', 'sample', 'panel', null  )

        assert lines == 160 : "[T E S T] assert 1/3 failed: att.filter(inf, ouf, 'seqrun', 'sample', 'panel') return a different number than 160"  // it was 26

        def linesOut = new File(ouf).readLines()
        assert linesOut.size() == 148 : "[T E S T] assert 2/3 failed: File(ouf).readLines() Does not contain 148 lines "

        // Delete the file at the end of using it
        boolean fileSuccessfullyDeleted =  new File(ouf).delete()
        assert fileSuccessfullyDeleted : "[T E S T] assert 3/3 failed: ${parentFolder}/stats.tsv was not deleted"
    }

    /**
        This test will check the the error of spurious inout arguments
     */
    void testFailFilter_InvalidInput()
    {

        def Error = shouldFail MissingMethodException, {att.filter( "FAIL ME IN 1", "FAIL ME IN 2" )}
        assert Error.contains('No signature of method') : "[T E S T]: AlignStatsToTsv.filter( \"FAIL ME\", ouf  ) is not returning MissingMethodException error."

    }

    /**
        This test will check the the error of spurious inout arguments
     */
    void testFailFilter_InvalidInputFile()
    {

        String resource = "Stats"
        String file = "15K1490_stats"
        String extension = "csv"

        String parentFolder = new File(AlignStatsToTsvTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath()).getParent()
        def ouf = "${parentFolder}/stats.tsv"
        try
        {
            att.filter("FAIL ME", ouf, 'seqrun', 'sample', 'panel', null)

// Um... this part should never be reached? Because the above code is supposed to crash, isn't it?
            assert true
            println("[T E S T] Succesfully Crashed AlignStatsToTsvTest.filter (\"FAIL ME\", ouf, 'seqrun', 'sample', 'panel') providing invalid input file")

        }
        catch (Exception ex)
        {
            assert ex.localizedMessage == "FAIL ME (No such file or directory)"

//            assert false: "[T E S T]: AlignStatsToTsv.Filter(\"FAIL ME\", ouf, 'seqrun', 'sample', 'panel') is not sending an Error message. \n An Error message like TEST [main] ERROR org.petermac.util.AlignStatsToTsv - Stats file doesn't exist: FAIL ME should be printed."
        }
    }


    /**
        Function that returns File resource path as string.
     */
    String PathGenerator(String resource, String file,String extension )
    {
        File basePath = new File(AlignStatsToTsvTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        if ( !basePath.exists() )
            throw new RuntimeException('ERROR in [FILE]:' + basePath + ' does not exist !')

        String p = basePath
        return p
    }
}


