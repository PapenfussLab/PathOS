package org.petermac.util

/**
 * Created by lara luis on 20/01/16.
 */
class ClassifyTest  extends GroovyTestCase
{
    /**
        Testing class creation
     */
    void testNewClassify()
    {

        def cl = new Classify()
        assert cl instanceof Classify:"[T E S T]: cannot load class Classify()"
    }

    /**
        Testing String clinvar( String fld )
        Multiple inputs are provided.
     */
    void testClinvar()
    {
        assert Classify.clinvar("FAIL") == null : "[T E S T]: Invalid input argument returns a non null value"
        assertEquals("[T E S T]: Sig=xxxx not found", Classify.clinvar("fld Clinvar field SIG=xxxx;"), "xxxx")     &&
        assertEquals("[T E S T]: SIG=1234 not found", Classify.clinvar("fld Clinvar field SIG=1234;"), "1234")     &&
        assertEquals( "[T E S T]: SIG=C[1-5] not found", Classify.clinvar("fld Clinvar field SIG=C[1-5];"), "C")
    }

    /**
         Testing static String lrt( String fld )
     */
    void testLtr()
    {
        assert Classify.lrt("") == null : "[T E S T]: Classify.lrt(\"\") is returning something different than null"
        assertEquals("[T E S T]: D is not equal to C5", Classify.lrt( "D" ), "C5") &&
        assertEquals( "[T E S T]: U is not equal to C3", Classify.lrt( "U" ), "C3") &&
        assertEquals( "[T E S T]: N is not equal to C1", Classify.lrt( "N" ), "C1")

    }

    /**
        static String Polyphen( String fld )
    */
    void testPolyphen()
    {
        assert Classify.polyphen('') == null : "[T E S T]: Classify.polyphen('') is returning something different than null"

        assertEquals( "[T E S T]: Beningn is not C1 class", Classify.polyphen('benign'), "C1")                     &&
        assertEquals( "[T E S T]: B is not C2 class", Classify.polyphen('B'), "C1")                                &&
        assertEquals( "[T E S T]: Possibly damaging is not C4 class",Classify.polyphen('possibly_damaging'), "C4") &&
        assertEquals( "[T E S T]: P is not C4 class", Classify.polyphen('P'), "C4")                                 &&
        assertEquals( "[T E S T]: probably damaging is not C5 class", Classify.polyphen('probably_damaging'), "C5") &&
        assertEquals( "[T E S T]: D is not C5 class",Classify.polyphen('D'), "C5")

    }

    /**
        static String mutTaste( String fld )
    */
    void testMutTaste()
    {
        assert Classify.mutTaste('') == null : "[T E S T]: Classify.mutTaste('') is returning something different than null"

        assertEquals( "[T E S T]: A is not C5 class", Classify.mutTaste('A'), "C5") &&
        assertEquals( "[T E S T]: D is not C4 class", Classify.mutTaste('D'), "C4") &&
        assertEquals( "[T E S T]: P is not C1 class", Classify.mutTaste('P'), "C1") &&
        assertEquals( "[T E S T]: N is not C1 class", Classify.mutTaste('N'), "C1")

    }

    /**
       static String mutAssess( String fld )
   */
    void testMutAssess()
    {
        assert Classify.mutAssess('') == null : "[T E S T]: Classify.mutAssess('') is returning something different than null"
        assertEquals( "[T E S T]: H is not C5 class", Classify.mutAssess('H'), "C5") &&
        assertEquals( "[T E S T]: M is not C4 class", Classify.mutAssess('M'), "C4") &&
        assertEquals( "[T E S T]: L is not C3 class", Classify.mutAssess('L'), "C3") &&
        assertEquals( "[T E S T]: N is not C1 class", Classify.mutAssess('N'), "C1")

    }

    /**
      static String sift( String fld )
    */
    void testSift()
    {
        assert Classify.sift('') == null : "[T E S T]: Classify.sift('') is returning something different than null"
        assert Classify.sift('TEST') == null : "[T E S T]: Classify.sift('TEST') "
        assertEquals( "[T E S T]: deleterious is not C5 class", Classify.sift('deleterious'), "C5") &&
        assertEquals( "[T E S T]: tolerated is not C2 class", Classify.sift('tolerated'), "C2")

    }

    /**
       static String fathmm( String fld )
    */
    void testFathmm()
    {
        assert Classify.fathmm('') == null : "[T E S T]: Classify.fathmm('') is returning something different than null"
        assert Classify.fathmm('TEST') == null : "[T E S T]: Classify.fathmm('TEST') is returning something different than null"
        assertEquals( "[T E S T]: D is not C5 class", Classify.fathmm('D'), "C5") &&
        assertEquals( "[T E S T]: T is not C2 class", Classify.fathmm('T'), "C2")

    }

    /**
        static String metaSvm( String fld )
    */
    void testMetaSvm()
    {
        assertEquals( "[T E S T]: D is not C5 class", Classify.metaSvm('D'),"C5") &&
        assertEquals( "[T E S T]: T is not C2 class", Classify.metaSvm('T'),"C2")
        assert Classify.metaSvm('') == null : "Classify.metaSvm('') is returning something different than null"
        assert Classify.metaSvm('TEST') == null :"Classify.metaSvm('TEST') is returning something different than null"
    }

    /**
        String metaLr( String fld )
    */
    void testMetaLr()
    {
        assert  Classify.metaLr('') == null : "Classify.metaLr('') is returning something different than null"
        assert  Classify.metaLr('TEST') == null : "Classify.metaLr('TEST') is returning something different than null"
        assertEquals( "D is not C5 class", Classify.metaLr('D'), "C5") &&
        assertEquals( "T is not C2 class", Classify.metaLr('T'), "C2")

    }

}
