/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
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
 * Date: 03/04/2015
 * Time: 5:15 PM
 */

//Cannot find Class
class SmithWatermanTest extends GroovyTestCase
{
    SmithWaterman sw

    //TODO: check error
    void setUp()
    {
        sw = new SmithWaterman()
    }

    void testAlign()
    {
        def ref = "CAGCCTTTCTGACCCGGAAATCAAAATAGGCACAACAAA"
        def qry = "CTGAGCCGGTAAATC"
        def res = sw.align( qry, ref )

        println res

        Map expect = [score:21, cigar: '9M1I5M', refStart:8, refEnd:21, qryStart:0, qryEnd:14]

        println( "Target [${res.refStart}..${res.refEnd}] ${ref[res.refStart..res.refEnd]}")
        println( "Query  [${res.qryStart}..${res.qryEnd}] ${qry[res.qryStart..res.qryEnd]}")

        assert expect == res
    }

    void testFormat()
    {
        def ref = "CAGCCTTTCTGACCCGGAAATCAAAATAGGCACAACAAA"
        def qry = "CTGAGCCGGTAAATC"
        def res = sw.align( qry, ref )

        println sw.format( qry, ref, res )
        assert true //didnt break
    }

    void testMergePair()
    {
        def ref = "CAGCCTTTCTGACCCGGAAATCAAAATAGGCACAACAAA"
        def qry = "CTGAGCCGGTAAATC"
        def match = "CCGGTAAATC"
        def fmt = [ ref: ref, align: match, qry: qry ]

        println sw.mergePair(fmt)
        assert sw.mergePair(fmt) == "CNGNNNNNNTNANNCGGAAATCAAAATAGGCACAACAAA"

    }
    //newVar( int pos, String ref, String alt, String refseq )

    void testNewVar()
    {
        int pos = 10
        String ref = "CAGCCTTTCTGACCCGGAAATCAAAATAGGCACAACAAA"
        String alt = "-"
        String refseq = "CAGCCTTTCTGACCCGGAAATCAAAATAGGCACAACAAA"
        def mapped = sw.newVar(  pos,  ref,  alt,  refseq )

        assert mapped.containsKey('pos') &&  mapped.containsKey('ref') && mapped.containsKey('alt') == true
    }

    void testVariants()
    {
       // Formatted alignment fmt = [ ref: ref, align: match, qry: qry ]
        def fmt = ["ref":"CAGCCTTTCTGACCCGG-AAATCAAAATAGGCACAACAAA","align":"||||*||||*|||||", "qry":"CTGAGCCGGTAAATC"]
        def list =  sw.variants(fmt)
        assert  list.size() == 2 &&
                list[0]["pos"] == 4 &&
                list[1]["pos"] == 9 &&
                list[0]["ref"] == "C" &&
                list[1]["ref"] == "T" &&
                list[0]["alt"] == "G" &&
                list[1]["alt"] == "T"
    }

}
