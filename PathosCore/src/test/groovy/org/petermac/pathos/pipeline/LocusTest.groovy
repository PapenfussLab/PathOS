package org.petermac.pathos.pipeline

/**
 * Created for PathOS.
 *
 * Description:
 *
 *
 *
 * User: doig ken
 * Date: 23/02/2015
 * Time: 11:22 AM
 */
class LocusTest extends GroovyTestCase
{
    void testJoin()
    {
        String fasta = """>header\nAAAAA\nGGGGG\nCCCCC\nTTTTT"""

        List lines = fasta?.split( "\n" )
        assert lines.size() >= 2
        println lines
        String bases = lines[1..-1].join('')

        assert bases == "AAAAAGGGGGCCCCCTTTTT"
    }

    void testStringConstructor()
    {
        String locus = "1:81-254"
        def loc = new Locus(locus)
        assert loc.toString() == locus

        locus  = "1:159138663-1254218344"
        def locBigger = new Locus(locus)
        assert locBigger.toString() == locus
    }

    void testMultiInput()
    {
        String chr = "17"
        String startPos = "100"
        String endPos = "500"

        def loc = new Locus(chr, startPos, endPos)
        assert loc.toString() =="${chr}:${startPos}-${endPos}"
    }

    void testIntConstructor()
    {
        String chr = "17"
        Integer startPos = 81
        Integer endPos = 254

        def loc = new Locus(chr, startPos, endPos)
        assert loc.toString() == "${chr}:${startPos}-${endPos}"

    }

    void testOrder()
    {
        String chr = "17"
        Integer startPos = 254
        Integer endPos = 81

        def loc = new Locus(chr, startPos, endPos)
        assert loc.toString() == "${chr}:${endPos}-${startPos}"
    }

    void testLocations()
    {
        String chr = "17"
        Integer startPos = 81
        Integer endPos = 254

        def loc = new Locus(chr, startPos, endPos)

        assert loc.startPos() == startPos && loc.endPos() == endPos

         startPos = 254
         endPos = 81
        def locInv = new Locus(chr, startPos, endPos)

        assert locInv.startPos()  == endPos && locInv.endPos() == startPos
    }

    void testOverlap()
    {
        String chr = "17"
        Integer startPos = 81
        Integer endPos = 254

        def loc = new Locus(chr, startPos, endPos)

        chr = "17"
        startPos = 100
        endPos = 200

        def locSpan = new Locus(chr, startPos, endPos)

        assert loc.overlap(locSpan) && locSpan.overlap(loc)

    }

    void testNonOveralp()
    {
        String chr = "17"
        Integer startPos = 81
        Integer endPos = 254

        def loc = new Locus(chr, startPos, endPos)

        chr = "18"
        startPos = 100
        endPos = 200

        def locSpan = new Locus(chr, startPos, endPos)

        assert !loc.overlap(locSpan) && !locSpan.overlap(loc)
    }

    void testContain()
    {
        String chr = "17"
        Integer startPos = 81
        Integer endPos = 254

        def loc = new Locus(chr, startPos, endPos)

        chr = "17"
        startPos = 100
        endPos = 200

        def locSpan = new Locus(chr, startPos, endPos)

        assert loc.contains(locSpan) && !locSpan.contains(loc)
    }

    void testWrongBases()
    {
        //TODO check because is failing
        String chr = "gi|428186265|gb|JH992965.1|"
        Integer startPos = 100
        Integer endPos   = 140

        def loc = new Locus(chr, startPos, endPos)
        println( loc.bases() )

        //assert loc.bases() == null
    }

    void testBases()
    {
        String chr = "5"
        Integer startPos = 180915260
        Integer endPos   = 180915270

        def loc = new Locus(chr, startPos, endPos)

        assert loc.toString() == "5:180915260-180915270"
        // Provide a valid chr position and end
        //assert false
    }

    void testRevCompliment()
    {
        String chr = "5"
        Integer startPos = 180915260
        Integer endPos   = 180915270

        def loc = new Locus(chr, startPos, endPos)

        assert loc.revcom("GGGGAAAAAAAATTTATATAT") == "ATATATAAATTTTTTTTCCCC"

    }
}















