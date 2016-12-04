package org.petermac.pathos.pipeline

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import org.apache.log4j.*
import org.petermac.pathos.pipeline.HGVS
import org.petermac.pathos.pipeline.Mutalyzer
import org.petermac.pathos.pipeline.MutalyzerUtil

//LUIS EDIT
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import org.apache.http.auth.*
import org.apache.http.HttpHost
import org.apache.http.client.*
import static groovyx.net.http.Method.HEAD
import org.junit.Ignore
import org.junit.Test
import java.security.KeyStore

import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.SSLSocketFactory;

import static groovyx.net.http.Method.*
import org.apache.commons.codec.binary.Base64

/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 14/06/13
 * Time: 9:30 AM
 * To change this template use File | Settings | File Templates.
 */

class MutalyzerIntTest extends GroovyTestCase
{


    def env = System.getenv()

    String DB = env["PATHOS_DATABASE"]

    //  Batch job data
    //
    List muts =  [  "NM_015511.3:c.1093G>A",
                    "NM_005502.3:c.2473G>A",
                    "NM_000350.2:c.2828G>A",
                    "NM_001171.5:c.3421C>T",
                    "NM_000033.3:c.31_46delCGGGGGAACACGCTGA",
                    "NM_000033.3:c.38A>C",
                    "NM_000033.3:c.232_240delCTCCTGCGG",
                    "NM_000033.3:c.323C>T",
                    "NM_000033.3:c.406C>T",
                    "NM_007294.3:c.19_47del",
                    "NM_007294.3:c.32_33insC",
                    "NM_007294.3:c.61_61delA",
                    "NM_007294.3:c.69_70insAG"
                ]

    def mut

    void setUp()
    {
        mut = new Mutalyzer()
    }

    void testPing()
    {

        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"

        assert mut.ping()
    }


    void testUpgradePost()
    {


        def http = new HTTPBuilder('https://test.mutalyzer.nl/')

        if ( mut.proxyHost && mut.proxyPort )
            http.setProxy( mut.proxyHost, mut.proxyPort, 'http')

        http.request( POST )
        {
            uri.path = '/json/checkSyntax'

            send( ContentType.URLENC, [variant: 'AB026906.1:c.274del'])

            response.success =
            {
                resp ->
                println "POST response status: ${resp.statusLine}"
                assert resp.statusLine.statusCode == 200
            }
        }
    }

    void testInfo()
    {


        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"
        assertEquals( 'humgen@lumc.nl', mut.info())
    }

    void testRunMutalyzer()
    {

        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"
        def map = mut.runMutalyzer( "NM_007294", "c.117_118delTG")
        assertEquals( 'HGVSc', 'c.117_118del', map[ 'hgvsc' ])
        assertEquals( 'HGVSp', 'p.(Cys39*)', map[ 'hgvsp' ])
        assertEquals( 'Transcript', 'NM_007294.3', map[ 'transcript' ])
    }

    void testNumberConversion()
    {

        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"
        List hgs = mut.numberConversion( 'NM_007294.3:c.117_118delTG')
        assertEquals( 'HGVSg', 'NC_000017.10:g.41267759_41267760delCA', hgs[0] )
        hgs = mut.numberConversion( 'NM_007294.3:c.2082C>T')
        assertEquals( 'HGVSg', 'NC_000017.10:g.41245466G>A', hgs[0])
        hgs = mut.numberConversion( 'NM_007294.3:c.2082C>T', 'hg19')
        assertEquals( 'HGVSg', 'NC_000017.10:g.41245466G>A', hgs[0])
    }

    void testChromAccession()
    {


        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"
        assertEquals( "Chromosome", 'NC_000023.10', mut.chromAccession( 'X' ))

        for ( chr in 1..22 )
        {
            log.info( "Chr ${chr} " + mut.chromAccession( chr as String ))
        }
        ['X','Y'].each
        {
            chr ->
            log.info( "Chr ${chr} " + mut.chromAccession( chr ))
        }
    }

    void testTranscripts()
    {


        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"
        Object[] exp = [
                'NM_007294.3','NM_007297.3','NM_007298.3', 'NM_007299.3', 'NM_007300.3', 'NR_027676.1',
                'NM_007294.2', 'NM_007295.2', 'NM_007296.2', 'NM_007297.2', 'NM_007298.2', 'NM_007299.2',
                'NM_007300.2', 'NM_007302.2', 'NM_007303.2', 'NM_007304.2', 'NM_007305.2', 'LRG_292t1'
        ]

        Object[] act = mut.getTranscriptsByGeneName( 'BRCA1' )
        println exp.sort()
        println act.sort()

        assertArrayEquals( exp.sort(), act.sort())
    }

    void testBatch()
    {

        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"
        def res = mut.batch( muts, "PositionConverter" )
        log.info res
    }

    void testParse()
    {

        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"
        def hgvs = "NM_000033.3:c.31_46delCGGGGGAACACGCTGA"
        Map m = mut.parse(hgvs)

        assert m['transcript'] == "NM_000033.3"
        assert m['hgvs'] == "c.31_46delCGGGGGAACACGCTGA"

        hgvs = "NM_000033.3(BRCA1_v001):c.31_46delCGGGGGAACACGCTGA"
        m = mut.parse(hgvs)

        assert m['transcript'] == "NM_000033.3"
        assert m['hgvs'] == "c.31_46delCGGGGGAACACGCTGA"
        assert m['gene'] == "BRCA1"
    }

    void testBatchNameChecker()
    {

        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"
        List vars = mut.batchNameChecker( muts )

        //  Check results match inputs
        //
        vars.eachWithIndex
        {
            Map var, i ->
            println( "Variant ${i}:  inp=${muts[i]} var=${var}" )
            assertEquals( "Inputs", muts[i], var.in )
        }
    }

    void testBatchSyntaxChecker()
    {


        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"
        List vars = mut.batchSyntaxChecker( muts )

        //  Check results match inputs
        //
        vars.eachWithIndex
        {
            var, i ->
            //log.info( "Variant ${i}:  " + var )
            assertEquals( "Inputs", muts[i], var['Input'])
            assertEquals( "Inputs", "OK", var['Status'])
        }

        def errmuts = muts
        String muta = muts[3]
        errmuts[3] = muta.replace( "NM_001171.5", "")
        vars = Mutalyzer.batchSyntaxChecker( errmuts )

        //  Check results fail for modified variant
        //
        vars.eachWithIndex
        {
            var, i ->
            //log.info( "Variant ${i}:  " + var )
            assertEquals( "Inputs", muts[i], var['Input'])
            if ( i != 3 ) assertEquals( "Result", "OK", var['Status'])
            if ( i == 3 ) assertNotSame( "Result", "OK", var['Status'])
        }
    }

    void testBatchPositionConverter()
    {


        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"
        List vars = mut.batchPositionConverter( muts )

        //  Check results match inputs
        //
        vars.eachWithIndex
        {
            var, i ->
            log.info( "Variant ${i}:  " + var )
            assert var['variant'] != 'FAILED'
        }
    }

    void testBatchHGVSg()
    {


        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"
        List gmuts = [ 'chr11:g.111959693G>T', 'chr11:g.111959693G>T']

        List vars = mut.batchPositionConverter( gmuts )

        //  Check results match inputs
        //
        vars.eachWithIndex
        {
            var, i ->
                log.info( "Variant ${i}:  " + var )
        }
    }

    void testHttpPing()
    {

        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"

        def http = new HTTPBuilder( 'https://mutalyzer.nl/' )
        def proxyHost = 'localhost' //System.properties.find { it.key == 'https.proxyHost'}?.value as String
        def proxyPort = 3128 //System.properties.find { it.key == 'https.proxyPort'}?.value as Integer
        if ( proxyHost && proxyPort )
            http.setProxy( proxyHost, proxyPort, 'http')

        http.request( POST )
        {
            uri.path = 'json/ping'

            // Note: Set ContentType before body or risk null pointer.
            //
            // requestContentType = ContentType.JSON
            response.success =
                {
                    resp ->
                        assert resp.status == 200
                }
            response.failure =
                {
                    resp, json ->
                        log.info "Request failed with status ${resp.statusLine} ${json}"
                        assert resp.status != 200
                        assert false, "Request failed"
                }
        }
    }

    /**
     *  Test using HTTPBuilder for direct gets of monitor Jobs
     *  https://mutalyzer.nl/json/monitorBatchJob?job_id=0f826a27-02a2-4cec-8d08-a74f13f35e97
     */
    void testHttpMonitor()
    {

        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"

        def http = new HTTPBuilder( 'https://mutalyzer.nl/' )
        def proxyHost = 'localhost' //System.properties.find { it.key == 'https.proxyHost'}?.value as String
        def proxyPort =  3128 //System.properties.find { it.key == 'https.proxyPort'}?.value as Integer
        if ( proxyHost && proxyPort )
            http.setProxy( proxyHost, proxyPort, 'http')

        def html = http.get( path: '/json/monitorBatchJob', contentType: ContentType.TEXT, query: [ job_id: '0f826a27-02a2-4cec-8d08-a74f13f35e97'] )

        assert html.getClass() == StringReader
        println( "Result=${html}")
    }

    //  Test to connect to Mutalyzer and send a POST message with the parameters in the body of
    //  the message, response is JSON mapped into a nested HashMap structure
    //
    void testHttpPost()
    {

        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"

        //  https://mutalyzer.nl/json/checkSyntax?variant=AB026906.1:c.274del
        //  Request is application/x-www-form-urlencoded
        //  Response is JSON encoded

        //Logger.getRootLogger().setLevel(Level.DEBUG)

        def http = new HTTPBuilder( 'https://mutalyzer.nl' )
        def proxyHost = 'localhost' //System.properties.find { it.key == 'https.proxyHost'}?.value as String
        def proxyPort = 3128 //System.properties.find { it.key == 'https.proxyPort'}?.value as Integer
        if ( proxyHost && proxyPort )
            http.setProxy( proxyHost, proxyPort, 'http')

        http.request( POST )
        {
            uri.path = '/json/checkSyntax'

            //body = [process: process, argument: 'hg19', data: data]
            //
            send ContentType.URLENC, 'variant=AB026906.1:c.274del'

            response.success =
            {
                resp, json ->
                    log.info "Success! ${resp.statusLine} ${json}"
                    assert resp.status == 200

                    //  parse JSON HashMap response
                    //
                    assert json.valid == true
            }

            response.failure =
            {
                resp, json ->
                    log.info "Request failed with status ${resp.statusLine} ${json}"
                    assert resp.status != 200
                    assert false, "Request failed"
            }
        }
    }

    void testHttpPostPosChecker()
    {


        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"

        //Logger.getRootLogger().setLevel(Level.DEBUG)

        def http = new HTTPBuilder( 'https://test.mutalyzer.nl' )

        if ( mut.proxyHost && mut.proxyPort )
            http.setProxy( mut.proxyHost, mut.proxyPort, 'http')

        http.request( POST, ContentType.TEXT )
        {
            uri.path = '/json/submitBatchJob'

            //body = [process: process, argument: 'hg19', data: data]
            //
            send ContentType.URLENC, [process: 'PositionConverter', argument: 'hg19', data: Base64.encodeBase64String(muts.join(',') as byte[]) ]

            response.success =
                {
                    resp, json ->
                        String job = json.text
                        job = job.replaceAll('"','')
                        log.info "Success! ${resp.statusLine} ${job}"
                        assert resp.status == 200

                        //  parse JSON HashMap response
                        //
                        assert job =~ /.{8}-.{4}-.{4}-.{4}-.{12}/

                }

            response.failure =
                {
                    resp, json ->
                        log.info( "Request failed with status ${resp.statusLine} ${json}")
                        assert resp.status != 200
                        assert false, "Request failed"
                }
        }
    }

    void testHttpPostBulk()
    {


        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"

        Logger.getRootLogger().setLevel(Level.DEBUG)


        String resource = "Dummy"
        String file = "emory"
        String extension = "txt"

        File mf = new File(MutalyzerIntTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())

        assert mf.exists()

        //  Read in test muts
        //
        List ml = mf.readLines()
        assert ml.size() == 8346
        //def testmuts = ml[0..1]
        List testmuts = ['NM_019109.4:c.*17_18dupTA']  // returns 'NM_019109.4:c.*17_18dupTA<tab>(mapping): End position is smaller than the begin position.'
        testmuts << ml[0]
        testmuts << ml[1]

        def mutl = mut.batchPositionConverter(testmuts)
        mutl.each()
        {
            println( "${it}")
        }
    }

    void test3primeHgsvc()
    {

        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"

        Logger.getRootLogger().setLevel(Level.DEBUG)

        List threePrime =
        [
                'NM_004985.3:c.32_33insTGG',
                'NM_004985.3:c.35_36insTGG',
                'NM_004985.3:c.38_39insTGG',
                'NM_004985.3:c.33_35dup',
                'NM_004985.3:c.36_38dup'
        ]

        def mutl = mut.batchNameChecker(threePrime)

        def i = 0
        for ( Map muta in mutl )
        {
            boolean ok = (++i == 5)
            def result = muta.transcript + ':' + muta.hgvsc
            assert ok == (result == muta.in )
            println( "mut ${ok}\t${result}\t${muta.in}\t${muta.gene}\t${muta.error}")
        }
    }

    // TODO: Check the assert of this test case
//    void test3primeHgvsg()
//    {
//
//        def mut = new Mutalyzer('localhost', 3128 )
//
//        if ( ! mut.ping()) assert false, "Can't connect to Mutalyser"
//
//        Logger.getRootLogger().setLevel(Level.DEBUG)
//
//        List<Map> threePrime =
//        [
//            [expect: 'chr4:g.1803689G>A',           actual: 'chr4:g.1803689G>A'],
//            [expect: 'chr8:g.38285914_38285916del',	actual: 'chr8:g.38285931_38285933del'],
//            [expect: 'chr10:g.89692740del',         actual: 'chr10:g.89692737del'],
//            [expect: 'chr10:g.89692740dup',         actual: 'chr10:g.89692738_89692739insT'],
//            [expect: 'chr10:g.89720648dup',         actual: 'chr10:g.89720648_89720649insT'],
//            [expect: 'chr10:g.89725239dup',         actual: 'chr10:g.89725239_89725240insT'],
//            [expect: 'chr10:g.89725304dup',         actual: 'chr10:g.89725304_89725305insT'],
//            [expect: 'chr12:g.25398281_25398283dup',actual: 'chr12:g.25398286_25398287insCCA'],
//            [expect: 'chr17:g.7579644_7579659del',  actual: 'chr17:g.7579669_7579684del']
//        ]
//
//
//        //  Convert to HGVSc List
//        //
//        List<Map> result = mut.normaliseVariants( threePrime.actual, DB )
//
//        //  Verify output
//        //
//        List<String>  expect = threePrime.expect
//        println expect
//
//        List<Boolean> flags = [true, true, false, false, false, true, true, true, false]
//
//        result.eachWithIndex
//        {
//            Map act, int i ->
//                println( "%%% Final chk ${act} ${expect[i]}")
//                assert HGVS.compareHgvsg( act.hgvsg , expect[i] ) == flags[i]
//        }
//
//    }


    //TODO: File to largo for this test
//    void testAllVar()
//    {
//        if ( ! Mutalyzer.ping()) assert false, "Can't connect to Mutalyser"
//
//        def mf = new File( 'Mutalyzer', 'allvar.txt' )
//        assert mf.exists()
//
//        //  Read in test muts
//        //
//        List ml = mf.readLines()
////        assert ml.size() == 101, 'lines in file'
////        ml = ml[0..100]
//
//        //  Perform validation
//        //
//        def mutl = Mutalyzer.batchNameChecker(ml)
//
//        //  Create output file
//        //
//        def of = new File( 'Mutalyzer', 'allvar.out' )
//        of.delete()
//        of << """##   Generated by MutCheck
//##
//#Variant\tStatus\tError\tCorrected\tTranscript\tGene\tHGVSc\tHGVSp
//"""
//
//        def validMut = 0
//        def errors   = 0
//        for ( Map mut in mutl )
//        {
//            def status = 'OK'
//            def result = mut.hgvsc ? mut.transcript + ':' + mut.hgvsc : mut.in
//            if ( result != mut.in ) status = 'EDIT'
//            if ( mut.error )
//            {
//                ++errors
//                status = 'ERROR'
//                result = ''
//                println mut.error
//            }
//            else
//                ++validMut
//
//            List outRow = [ mut.in, status, mut.error, result, mut.transcript ?:'', mut.gene ?:'', mut.hgvsc ?:'', mut.hgvsp ?:'' ]
//            of << outRow.join('\t') + '\n'
//        }
//
//        println( "In ${ml.size()} Out ${mutl.size()} OK ${validMut} Errors ${errors}")
//    }
}
