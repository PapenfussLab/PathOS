/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.pipeline

/**
 * Created for PathOS.
 *
 * Description:
 *
 *
 *
 * User: doig ken
 * Date: 11/09/2014
 * Time: 6:01 PM
 */
class TranscriptIntTest extends GroovyTestCase
{

    String DB
    void setUp()
    {
        def env = System.getenv()

        DB = env["PATHOS_DATABASE"]

    }

    void testConstructor()
    {
        String dbname = DB
        def trans = new Transcript(dbname)

    }

    void testLrgDb()
    {
        Map m = new Transcript(DB).lrg(DB)
        assert m.keySet().size() == m.size() // 509//511
    }

    void testLrg()
    {
        Map db    = Transcript.lrg(DB)  // DB version
        assert db.keySet().size() == db.size()
    }

    void testHgnc()
    {
        Map db    = Transcript.preferred(DB)  // DB version
        assert db.keySet().size() == db.size()
    }

    void testSelectTranscript()
    {
        def trans = new Transcript(DB)

        String chr = "7"
        String pos = "140453136"
        String ref = "A"
        String alt = "C"

        def map =  HGVS.normaliseVcfVar(  chr,  pos,  ref,  alt )
        map['transcripts'] = 'NM_000314,NM_207046,NM_206866,NM_203385'


        //TODO: What is a good input here?
        println trans.selectTranscript( map )
    }

    void testSelectBest()
    {
        def tss = ["NM_000314", "NM_199350", "NM_199246", "NM_198954", "NM_198541", "NM_198378", "NM_198149", "NM_197964"]

        def trans = new Transcript(DB)

        assert trans.selectBest(tss) == "NM_000314"
    }

    void testPosOffset()
    {
        String pos = "182354-182355"

        def trans = new Transcript(DB)
        //TODO: What is a good input here?
        assert trans.posOffset(pos) == 0
      //  assert false
    }

    void testSelectLrg()
    {
        def trans = new Transcript(DB)
        Map db  = trans.lrg(DB)

    }
}
