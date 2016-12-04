package org.petermac.util

/**
 * Created by lara luis on 20/01/16.
 */
class ClassifyTest  extends GroovyTestCase
{
    void testNewClassify()
    {

        def cl = new Classify()
    }

    void testClinvar()
    {

        assertEquals( Classify.clinvar("fld Clinvar field SIG=xxxx;"), "xxxx") &&
                assertEquals( Classify.clinvar("fld Clinvar field SIG=1234;"), "1234") &&
                assertEquals( Classify.clinvar("fld Clinvar field SIG=C[1-5];"), "C")
    }

    void testLtr()
    {
        assertEquals( Classify.lrt( "D" ), "C5")
        assertEquals( Classify.lrt( "U" ), "C3")
        assertEquals( Classify.lrt( "N" ), "C1")
        assert Classify.lrt("") == null

    }

    void testPolyphen()
    {
        assert Classify.polyphen('') == null
        assertEquals( Classify.polyphen('benign'), "C1")
        assertEquals( Classify.polyphen('B'), "C1")
        assertEquals( Classify.polyphen('possibly_damaging'), "C4")
        assertEquals( Classify.polyphen('P'), "C4")
        assertEquals( Classify.polyphen('probably_damaging'), "C5")
        assertEquals( Classify.polyphen('D'), "C5")

    }

    void testMutTaste()
    {
        assertEquals( Classify.mutTaste('A'), "C5")
        assertEquals( Classify.mutTaste('D'), "C4")
        assertEquals( Classify.mutTaste('P'), "C1")
        assertEquals( Classify.mutTaste('N'), "C1")
        assert Classify.mutTaste('') == null

    }

    void testMutAssess()
    {
        assertEquals( Classify.mutAssess('H'), "C5")
        assertEquals( Classify.mutAssess('M'), "C4")
        assertEquals( Classify.mutAssess('L'), "C3")
        assertEquals( Classify.mutAssess('N'), "C1")
        assert Classify.mutAssess('') == null
    }

    void testSift()
    {
        assertEquals(Classify.sift('deleterious'), "C5")
        assertEquals(Classify.sift('tolerated'), "C2")
        assert Classify.sift('') == null
        assert Classify.sift('TEST') == null
    }

    void testFathmm()
    {
        assertEquals(Classify.fathmm('D'), "C5")
        assertEquals(Classify.fathmm('T'), "C2")
        assert Classify.fathmm('') == null
        assert Classify.fathmm('TEST') == null
    }


    void testMetaSvm()
    {
        assertEquals(Classify.metaSvm('D'),"C5")
        assertEquals(Classify.metaSvm('T'),"C2")
        assert Classify.metaSvm('') == null
        assert Classify.metaSvm('TEST') == null
    }

    void testMetaLr()
    {
        assertEquals(Classify.metaLr('D'), "C5")
        assertEquals(Classify.metaLr('T'), "C2")
        assert  Classify.metaLr('') == null
        assert  Classify.metaLr('TEST') == null
    }

}
