package org.petermac.pathos.loader

import org.petermac.util.VcfDbCheck


/**
 * Created by lara luis on 16/09/2016.
 */
////
// Testing VcfLaoder.
// It contains 1 setup 7 functions and 2 auxuliar functions
////////////////////////////////////////////////////////////////
class VcfLoaderTest extends GroovyTestCase {

    VcfLoader vl
    String DB

    // VcfLoader is dependant on DB
    // It hasto check if provided by env variable
    void setUp()
    {
        vl = new VcfLoader()
        def env = System.getenv()
        DB = env["PATHOS_DATABASE"]

    }

    ////
    // This will have to show the help function
    ////////
    void testMain()
    {
        vl.main()
        // if prints help, environment variables are correct
        assert true
    }

    ////
    // So Far this function returns 0
    ///////
    void testLoadGorm()
    {
        // Returns 0
       assert( vl.loadGorm( DB ) == 0 )

    }

    ////
    // This function will print a lot of warnings for some files
    /////////////////////////////////////////////////////////////
    void testNormaliseVcfs( )
    {

        def VcfNames = [
                        'tumour',
                        'mini_tumour',
                        'input1',
                        'IC080T_merged_dups_realign_recal_somatic_mutect',
                        'IC050T_merged_dups_realign_recal_somatic_mutect',
                        'Romeo1_Biopsy',
                        'Romeo1_Post'
                        ]
        String resource = "Vcf"
        String extension = "vcf"
        String rdb = DB
        def nocache = false

        List<File> Vcfs = GetFileResources(VcfNames, resource, extension)

        // Returns a list of files in temp
        assert(  vl.normaliseVcfs( Vcfs, rdb, nocache ).size() > 1 )

    }

    ////
    // This function checks the conversion from Vcf to TSV
    // similar to the test function TsvTest
    ////////////////////////////////////////////////////////
    void testTsvVcfs( )
    {
        def VcfNames = [
                'tumour',
                'mini_tumour',
                'input1',
                'IC080T_merged_dups_realign_recal_somatic_mutect',
                'IC050T_merged_dups_realign_recal_somatic_mutect',
                'Romeo1_Biopsy',
                'Romeo1_Post'
        ]

        String resource = "Vcf"
        String extension = "vcf"
        String rdb = DB
        String seqrun = "12K0304_CCCAACCT-TAAGACA"
        String panel = ""
        String vcfcolsFileName = "vcfcols"
        File vcfcols = PathGeneratorFile( resource,  vcfcolsFileName, "txt" )

        def nocache = false

        List<File> vcfs = GetFileResources(VcfNames, resource, extension)

        File tsv = new File( "vcfs.tsv" )
        tsv.delete()

        List<Map> sammap = vl.tsvVcfs( vcfs, tsv, seqrun, panel, vcfcols )


        List<File> normVcfs = vl.normaliseVcfs( vcfs, rdb, nocache )

        List<Map> Normsammap = vl.tsvVcfs( normVcfs, tsv, seqrun, panel, vcfcols )


        println("Original")

        assert ( sammap.size() > 1 )

        println("Normalised")
        assert ( Normsammap.size() > 1 )


    }

    //
    // If this function ran properly
    // it will retunr true
    ///////////
    void testLoadSeqrun()
    {
        def VcfNames = [
                'tumour',
                'mini_tumour',
                'input1',
                'IC080T_merged_dups_realign_recal_somatic_mutect',
                'IC050T_merged_dups_realign_recal_somatic_mutect',
                'Romeo1_Biopsy',
                'Romeo1_Post'
        ]
        String resource = "Vcf"
        String extension = "vcf"
        String rdb = DB
        String seqrun = "12K0304_CCCAACCT-TAAGACA"
        String panel = ""
        String vcfcolsFileName = "vcfcols"
        File vcfcols = PathGeneratorFile( resource,  vcfcolsFileName, "txt" )

        List<File> vcfs = GetFileResources(VcfNames, resource, extension)

        File tsv = new File( "vcfs.tsv" )
        tsv.delete()


        List<Map> Normsammap = vl.tsvVcfs( vcfs, tsv, seqrun, panel, vcfcols )

        println("Loading seqrun")

        // TODO: Check if,
        // This always returns true if executed correctly?
        assert( vl.loadSeqrun(Normsammap, rdb) )
    }

    //
    // This function is essential in loading the dta to teh database
    void testVcfLoad()
    {
        def VcfNames = [
                'tumour',
                'mini_tumour',
                'input1',
                'IC080T_merged_dups_realign_recal_somatic_mutect',
                'IC050T_merged_dups_realign_recal_somatic_mutect',
                'Romeo1_Biopsy',
                'Romeo1_Post'
        ]
        String resource = "Vcf"
        String extension = "vcf"
        String rdb = DB
        String seqrun = "12K0304_CCCAACCT-TAAGACA"
        String panel = ""
        String vcfcolsFileName = "vcfcols"
        String table = "mp_seqrun"
        File vcfcols = PathGeneratorFile( resource,  vcfcolsFileName, "txt" )

        def nocache = false

        List<File> vcfs = GetFileResources(VcfNames, resource, extension)

        File tsv = new File( "vcfs.tsv" )
        tsv.delete()

        //List<File> normVcfs = vl.normaliseVcfs( vcfs, rdb, nocache )

        List<Map> Normsammap = vl.tsvVcfs( vcfs, tsv, seqrun, panel, vcfcols )

        assert( vl.vcfLoad( tsv,  table, DB ))

    }


    ////
    // Testing VcfLoader.loadVcf()
    // int loadVcf( List<File> vcfs, String seqrun, String panel, String rdb, List dss, File vcfcols, File errFile, boolean filter )
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void testLoadVcf()
    {
        ////
        // Parameters
        //////////////////////////
        String seqrun = "12K0304_CCCAACCT-TAAGACA"
        String sample = "12K0304"
        //String seqrun = "xxx"
        String panel = ""
        String rdb = DB
        def dss = []
        String errFileName = "errFile"
        String vcfcolsFileName = "vcfcols"
        String resource = "Vcf"
        boolean filter = true
        String extension="vcf"
        String dbname = DB

        def VcfNames = [
                        'tumour',
                        'mini_tumour',
                        'input1',
                        'IC080T_merged_dups_realign_recal_somatic_mutect',
                        'IC050T_merged_dups_realign_recal_somatic_mutect',
                        'Romeo1_Biopsy',
                        'Romeo1_Post'
                        ]


        ////
        // Extra files to call loadVcf
        ////////////////////////////////
        File vcfcols = PathGeneratorFile( resource,  vcfcolsFileName, "txt" )
        File errFile = PathGeneratorFile( resource,  errFileName, "txt" )


        List<File> Vcfs = GetFileResources(VcfNames, resource, extension)

        println("Vcf Files ")
        println(Vcfs)

        def vars = vl.loadVcf(Vcfs, seqrun, panel, rdb, dss, vcfcols, errFile, filter)



        println("NUMBER of VARS: " + vars)
        // if made it this far
        // has not been broken anything

        println ("DONE RUNNING db check ")
        //
        // Run VcfDbCheck to validate the file
        ////////////////////////////////////////
        def checker = new VcfDbCheck()
        def nrow = checker.checkVcfs( seqrun, sample, Vcfs, dbname, errFile, true, false )

        println("NROW : " + nrow)

        assert nrow > 1
    }

    //
    // Function to get the paths of the Vcf resources given by name
    /////////////////////////////////////////////////////////////////
    List<File> GetFileResources(List <String> vcfNames, String resource, String extension)
    {
        List<File> vcfPaths =[]
        for (item in vcfNames) {
            vcfPaths.add(PathGeneratorFile( resource,  item, extension ))
        }

        return vcfPaths
    }

    /////
    // Get File from input
    ////////////////////////
    File PathGeneratorFile(String resource, String file,String extension )
    {

        File basePath = new File(VcfLoaderTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        if ( !basePath.exists() )
            throw new RuntimeException('ERROR in [FILE]:' + basePath + ' does not exist !')

        return basePath
    }

}