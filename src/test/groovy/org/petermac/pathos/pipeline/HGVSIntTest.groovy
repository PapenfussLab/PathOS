package org.petermac.pathos.pipeline

import org.petermac.pathos.pipeline.HGVS

/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 30/04/13
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
class HGVSIntTest extends GroovyTestCase
{

    String DB

    void setUp()
    {
        def env = System.getenv()

        DB = env["PATHOS_DATABASE"]

    }

    def muts = '''NM_000558.3:c.247_254C>A
                NM_000558.3: c.247_254delGCCCTGAGinsTGCA
                NM_000558.3: c.247_254delinsTGCA
                NM_000214.2: c.1880dupA
                NM_000214.2: c.1880dup
                NM_000551.3: c.165_166insA
                NM_000033.3: c.977_983delGGTATGT
                NM_000179.2:c.3647-53_3647-38del16
                NM_000059.3:c.*105A>C
                NM_000551.3: c.165_165delA
                NM_000551.3: c.165_165delinsA'''

    void testNormalise()
    {
        for ( mut in muts.split( '[,\n]' ))
        {
            def norm = HGVS.normalise( mut.trim())
            log.info( "${mut.trim()} ${norm}")
            assert norm
        }
    }

    // task needed to bootstrap DB
    void testGene()
    {
        def hg = new HGVS( DB )

        assertEquals( "Check gene converts OK ", 'NM_007294', hg.geneToTranscript('BRCA1'))
        assertEquals( "Check gene converts OK ", 'NM_000059', hg.geneToTranscript('BRCA2'))
        assertEquals( "Check gene converts OK ", 'NM_004333', hg.geneToTranscript('BRAF'))
    }

    void testConvert()
    {
        def hgvs = 'p.Leu858Arg'
        def hgvs_letter = HGVS.toAA1(hgvs)

        assertEquals( "To letter", "p.L858R", hgvs_letter)
    }

    void testInvalid()
    {
        def hgvs = 'p.Kdd858Arg'
        def hgvs_letter = HGVS.toAA1(hgvs)

        assertEquals( "To letter", "p.Kdd858R", hgvs_letter)

    }

    void testKconfabParse()
    {
        def g = '"""CHEK2c.1100delC (p.Thr367MetfsX15)'

        g = g.replaceAll( ',', '')
        def match = ( g =~ /(BRCA1|CHEK2)(c\.[^\(]+)/ )
        if ( match.count == 1)
        {
            log.info( "Parse var, in: " + g + " out " + match[0])
            def gene  = match[0][1]
            def hgvsc = match[0][2]

            log.info( "gene ${gene} hc [${hgvsc}] ")
        }

        g = 'BRCA2c.9976A>T (p.Lys3326X)'
        match = ( g =~ /(ATM|BRCA1|BRCA2|CHEK2)(c\.[^\(]+)/ )
        if ( match.count == 1)
        {
            log.info( "Parse var, in: " + g + " out " + match[0])
            def gene  = match[0][1]
            def hgvsc = match[0][2]

            log.info( "gene ${gene} hc [${hgvsc}] ")
        }
    }

    void testParse()
    {
        def h = 'NC_000002.11:g.47702181C>T'
        def m = HGVS.parseHgvsG(h)

        assert m.hgvstype == 'g'
        assert m.muttype  == 'snp'
        assert m.pos    == '47702181'
        assert m.endpos == '47702181'
        assert m.transcript == 'NC_000002.11'
        assert m.chr == 'chr2'

        h = 'NC_000013.10:g.32953603delC'
        m = HGVS.parseHgvsG(h)

        assert m.hgvstype == 'g'
        assert m.muttype  == 'del'
        assert m.pos == '32953603'
        assert m.transcript == 'NC_000013.10'
        assert m.chr == 'chr13'

        h = 'NC_000013.10:g.32915027_32915028insAGCT'
        m = HGVS.parseHgvsG(h)

        assert m.hgvstype == 'g'
        assert m.muttype  == 'ins'
        assert m.bases  == 'AGCT'
        assert m.pos == '32915027'
        assert m.endpos == '32915028'
        assert m.transcript == 'NC_000013.10'
        assert m.chr == 'chr13'

        h = 'NC_000013.10:g.32954030dup'
        m = HGVS.parseHgvsG(h)

        assert m.hgvstype == 'g'
        assert m.muttype  == 'dup'
        assert m.pos == '32954030'
        assert m.transcript == 'NC_000013.10'
        assert m.chr == 'chr13'

    }

    void testHasTypeP()
    {
        String var = 'NM_000314.4:p.648G>A'
        assert  HGVS.isHgvsP(var)

    }


    void testCompareVars()
    {
        String var1 = 'NM_000314.4:c.648G>A'
        String var2 = 'NM_000314.4:c.254-30del'


        assert HGVS.compareHgvsg(var1, var2)
        // Todo: Why this is true??
        assert HGVS.compareHgvsg('NM_000314.4:c.648G>A', '') //&& false

    }

    void testOffset()
    {

        String var1 = 'NM_000314.4:c.648G>A'
        String var2 = 'NM_000142.4:c.610A>G'//

        // Todo: What input?
        assert  HGVS.baseOffset( var1, var1 ) == 0 &&  HGVS.baseOffset( var1, var2 ) == 0 //&& false
    }

    void testTranscriptionVerison()
    {
       String ts = 'NM_123456.78'
        String ts2 = "NM_000314.4"
        assert   HGVS.transcriptNoVersion( ts ) == "NM_123456" &&  HGVS.transcriptNoVersion( ts2 ) == "NM_000314"
    }

    void testEnsToMap()
    {
        String var = "7_140453136_A/C"
        def map = HGVS.ensToMap(var)

        assert map.keySet() as String[] == ['chr', 'pos', 'ref', 'alt'] &&
                map.size() == 4 &&
                map['chr'] == '7' &&
                map['pos'] == 140453136 &&
                map['ref'] == 'A' &&
                map['alt'] == 'C' &&
                HGVS.ensToMap("NM_000314") == null
    }

    void testNormaliseVcfVar()
    {
        String chr = "7"
        String pos = "140453136"
        String ref = "A"
        String alt = "C"

        def map =  HGVS.normaliseVcfVar(  chr,  pos,  ref,  alt )
        println(map)
        assert map.size() == 8 &&
                map.keySet() as String[] == ["chr", "pos", "endpos", "ref", "alt", "ensvar", "hgvsg", "annovar"]&&
                map['chr'] == '7' &&
                map['pos'] == 140453136 &&
                map['ref'] == 'A' &&
                map['alt'] == 'C' &&
                map['hgvsg'] == "chr7:g.140453136A>C"

    }

    void testMapToAnnoVar()
    {
        String chr = "7"
        String pos = "140453136"
        String ref = "A"
        String alt = "C"

        def map =  HGVS.normaliseVcfVar(  chr,  pos,  ref,  alt )
        assert map['annovar'] == HGVS.mapToAnnovar( map )
    }



    void testParseHgvsC() {
        //NM_000314.4:c.254-30del
        //NM_000314.4:c.-21T>C
        //NM_000314.4:c.648G>A
        //NM_000314.4:c.802-5_802-3del
        //NM_000314.4:c.*10dup
        //NM_000142.4:c.1535-29_1535-28insGCC

        def h = 'NM_000314.4:c.648G>A'
        def m = HGVS.parseHgvsC(h)
        println m
        assert m.hgvstype == 'c'
        assert m.muttype  == 'snp'
        assert m.pos == '648'
        assert m.transcript == 'NM_000314.4'

        h = 'NM_000314.4:c.-21T>C'
        m = HGVS.parseHgvsC(h)
        println m
        assert m.hgvstype == 'c'
        assert m.muttype  == 'snp'
        assert m.pos == '-21'
        assert m.transcript == 'NM_000314.4'

        h = 'NM_000314.4:c.254-30del'
        m = HGVS.parseHgvsC(h)
        println m
        assert m.hgvstype == 'c'
        assert m.muttype  == 'del'
        assert m.pos == '254'
        assert m.transcript == 'NM_000314.4'

        h = 'NM_000314.4:c.802-5_802-3del'
        m = HGVS.parseHgvsC(h)
        println m
        assert m.hgvstype == 'c'
        assert m.muttype  == 'del'
        assert m.pos == '802-5_802-3'
        assert m.transcript == 'NM_000314.4'

        h = 'NM_000314.4:c.*10dup'
        m = HGVS.parseHgvsC(h)
        println m
        assert m.hgvstype == 'c'
        assert m.muttype  == 'dup'
        assert m.pos == '*10'
        assert m.transcript == 'NM_000314.4'

        h = 'NM_000142.4:c.1535-29_1535-28insGCC'
        m = HGVS.parseHgvsC(h)
        println m
        assert m.hgvstype == 'c'
        assert m.muttype  == 'ins'
        assert m.pos == '1535-29_1535-28'
        assert m.transcript == 'NM_000142.4'

        h = 'NM_000314.4:c.802-5_802-3del'
        m = HGVS.parseHgvsC(h)
        println m
        assert m.hgvstype == 'c'
        assert m.muttype  == 'del'
        assert m.pos == '802-5_802-3'
        assert m.transcript == 'NM_000314.4'

    }

    void testChrPosParse()
    {
    String inpos = '10:182354-182355'

        Map m = HGVS.parseChrPos( inpos )

        assert m.chr == '10'
        assert m.pos == 182354
        assert m.endpos == 182355
    }

    void testEns()
    {
        assert HGVS.ensToHgvsg( '7_27447014_T/-' ) == 'chr7:g.27447014del'
        assert HGVS.ensToHgvsg( '7_27447014_T/G' ) == 'chr7:g.27447014T>G'
        assert HGVS.ensToHgvsg( '9_27447015_-/T' ) == 'chr9:g.27447014_27447015insT'
        assert HGVS.ensToHgvsg( '7_55242467_AATTAAGAGAAGCAACAT/-' ) == 'chr7:g.55242467_55242484del'
        assert HGVS.ensToHgvsg( '2_212578393_A/G' ) == 'chr2:g.212578393A>G'
    }
}