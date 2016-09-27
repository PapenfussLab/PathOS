package org.petermac.pathos.loader

import org.petermac.util.VcfDbCheck


/**
 * Created by lara luis on 16/09/2016.
 */
class VcfLoaderTest extends GroovyTestCase {

    VcfLoader vl
    String DB


    void setUp()
    {
        vl = new VcfLoader()
        def env = System.getenv()
        DB = env["PATHOS_DATABASE"]
        println "Connecting to: ${DB}"
    }

    void testMain()
    {
        vl.main()
        assert true
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
        String rdb = "pa_local"
        def dss = []
        String errFileName = "errFile"
        String vcfcolsFileName = "vcfcols"
        String resource = "Vcf"
        boolean filter = true
        String extension="vcf"
        String dbname = DB

        def VcfNames = [
                        'tumour']//,
//                        'mini_tumour',
//                        'input1',
//                        'IC080T_merged_dups_realign_recal_somatic_mutect',
//                        'IC050T_merged_dups_realign_recal_somatic_mutect',
//                        'Romeo1_Biopsy',
//                        'Romeo1_Post'
//                        ]


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

        assert true
    }

    ////
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

    ////
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