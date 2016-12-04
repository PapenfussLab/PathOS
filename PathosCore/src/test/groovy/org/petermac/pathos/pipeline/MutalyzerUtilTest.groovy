package org.petermac.pathos.pipeline

/**
 * Created by lara luis on 1/02/2016.
 */
class MutalyzerUtilTest extends GroovyTestCase
{

    String DB
    void setUp()
    {
        def env = System.getenv()

        DB = env["PATHOS_DATABASE"]

    }


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

    void testCosnturctor()
    {
        def mut = new MutalyzerUtil()

    }

    void testConvertVcf()
    {


        String resource = "Vcf/Examples"
        String file = "mini_tumour"
        String extension ="vcf"
        String outputFile = "tumour_out"
        String cacheDB = DB

        File infile = new File(MutalyzerUtilTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        File ofile = new File(MutalyzerUtilTest.getClass().getResource( "/${resource}/${outputFile}.${extension}").getPath())


        def mutUt = new MutalyzerUtil()

       // assert
        println mutUt.convertVcf(infile, ofile, cacheDB, false) == 52


    }

    void testVepFormat()
    {
        String hgvsg = "c.1701A>G"

        def mutUt = new MutalyzerUtil()
        def l =  mutUt.vepFormat( muts,  hgvsg )
        assert l.contains("NM_015511.3:c.1093G>A") &&
                l.contains("NM_005502.3:c.2473G>A") &&
                l.contains("NM_000033.3:c.31_46delCGGGGGAACACGCTGA") &&
                l.contains("PASS") &&
                l.contains("HGVSg=c.1701A>G") &&
                l.contains("0/1")

    }

    void testCahceVariants()
    {
        String cacheDB = DB

        def mutUt = new MutalyzerUtil()
        def list = mutUt.cacheVariants(muts, cacheDB)
        assert list.size() == 13 &&
         list[0].gene == "AAR2" &&
         list[1].gene == "ABCA1" &&
         list[2].gene == "ABCA4"



        //private static def refAltBases( String ref, String alt, String hgvsg )
    }

    void testRefAltBases()
    {
        String ref = "A"
        String alt = "G"
        String hgvsg = "chr17:g.41276247A>G"

        def mutUt = new MutalyzerUtil()
        assert mutUt.refAltBases(ref, alt, hgvsg) as String[] == ['A', 'G']

        // Todo: GATK how to query for a different genome without breaking HGVSG
        println mutUt.refAltBases(ref,alt, "chr17:g.41276247A>G")
        println mutUt.refAltBases(ref, alt, "chr17:g.254-30del")
        println mutUt.refAltBases(ref, alt, "chr17:g.254-30dup")
        println mutUt.refAltBases(ref, alt, "chr17:g.254-30ins")


        //assert false

    }

    void testMergeVar()
    {


        def mutUt = new MutalyzerUtil()

        def dup = ['CHROM':'17', 'POS':'180915260', 'REF':'A', 'ALT':'G', 'DP':'10', 'AD':'5, 10, 15, 20', 'status':'dup']
        def merge = ['CHROM':'17', 'POS':'180915260', 'REF':'A', 'ALT':'G', 'DP':'8', 'AD':'20, 30, 40, 50', 'status':'merge']

        def data = mutUt.mergeVar(dup, merge)

        println data

        assert data.CHROM == '17' &&
                data.POS == '180915260' &&
                data.REF == 'A' &&
                data.ALT == 'G' &&
                data.DP == '9' &&
                data.AD == '25,40,55,70' &&
                data.status == 'dup_MERGED'

    }


    void testCombineAD()
    {
        String ad1 = '5, 10, 15, 20'
        String ad2 = '20, 30, 40, 50'
        def mutUt = new MutalyzerUtil()
       assert  mutUt.combineAD(  ad1,  ad2 ).toString() == "25,40,55,70"
    }

}
