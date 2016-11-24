package org.petermac.util

import groovyx.net.http.HTTPBuilder
import org.petermac.pathos.pipeline.Mutalyzer

/**
 * Created by lara luis on 25/01/2016.
 */
class GoogleCountTest extends GroovyTestCase
{
    //TODO: This does not work anymore, connections is refused
    void testGetResultsCount()
    {
        def gg = new GoogleCount()

        def mut = new Mutalyzer()

        def http = new HTTPBuilder('https://www.google.com.au/search?q=')


        if ( mut.proxyHost && mut.proxyPort )
            http.setProxy( mut.proxyHost, mut.proxyPort, 'https')


       // assert gg.getResultsCount('cancer','https://www.google.com.au/search?q=' , false) > 0


        //fails with http bamboo
//        assert (gg.getResultsCount('cancer','http://www.google.com.au/search?q=' , false) > 0 &&
//                gg.getResultsCount('cancer','https://www.google.com.au/search?q=' , false) > 0)

        // Todo scholar fails
        //assert (gg.getResultsCount('cancer','http://www.google.com.au/search?q=' , true) > 0 &&
          //      gg.getResultsCount('cancer','https://www.google.com.au/search?q=' , true) > 0), "Scholar Fails, cant connect"
    }
//
//    void testGetResultsCountScholar()
//    {
//        // Scholar is not working
//        def gg = new GoogleCount()
//
//        // Does not work
//        assert (gg.getResultsCount('cancer','http://scholar.google.com.au/scholar?q=' , false) > 0 &&
//                gg.getResultsCount('cancer','https://scholar.google.com.au/scholar?q=' , false) > 0), "Scholar fails, cant connect"
//
//        assert (gg.getResultsCount('cancer','http://scholar.google.com.au/scholar?q=' , true) > 0 &&
//                gg.getResultsCount('cancer','https://scholar.google.com.au/scholar?q=' , true) > 0), "Scholar fails, cant connect"
//    }
}
