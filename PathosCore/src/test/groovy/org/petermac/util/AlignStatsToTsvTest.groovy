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
 */
class AlignStatsToTsvTest extends GroovyTestCase
{
    AlignStatsToTsv att

    void setUp()
    {
        att = new AlignStatsToTsv()
    }

    void testConnection()
    {
        // Load a basic instance
        att.main()
        // Usage output should be printed
        assert true
    }


    void testRead()
    {

        String resource = "Stats"
        String file = "15K1490_stats"
        String extension = "csv"

        String inf = PathGenerator( resource,  file, extension )

        String parentFolder = new File(AlignStatsToTsvTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath()).getParent()
        def ouf = "${parentFolder}/stats.tsv"
        println ouf

        def lines = att.filter( inf, ouf, 'seqrun', 'sample', 'panel'  )
        assert lines == 160 // it was 26

        def linesOut = new File(ouf).readLines()
        assert linesOut.size() == 148 // it was 15

        // Delete the file at the end of using it
        boolean fileSuccessfullyDeleted =  new File(ouf).delete()
        assert fileSuccessfullyDeleted
    }



    String PathGenerator(String resource, String file,String extension )
    {
        File basePath = new File(AlignStatsToTsvTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        if ( !basePath.exists() )
            throw new RuntimeException('ERROR in [FILE]:' + basePath + ' does not exist !')

        String p = basePath
        return p
    }
}


