package org.petermac.util

/**
 * Created by lara luis on 21/01/2016.
 */
class CompareTsvTest extends GroovyTestCase
{

    /**
        Checking class
     */
    void testMain()
    {
        def ts = new CompareTsv()
        assert ts instanceof  CompareTsv : "[T E S T]: cannot create class instance"
    }

    /**
        Comparing single file
        static int compareTsv( File f1, File f2, List keys, List ignore, List always )
     */
    void testComaprisonCall()
    {

        String resource = "Vcf/Examples"
        String file = "unpacked_prostate"
        String extension = "tsv"
        String fileExtra = "unpacked_mcri"
        int lines = 0

        assert CompareTsvFiles( resource,  file,  extension,  fileExtra,  lines) : "[T E S T]: Tsv Files ${file} and ${fileExtra} have different lines than ${lines}"

    }


    /**
        Comparing multiple files
        static int compareTsv( File f1, File f2, List keys, List ignore, List always )
     */
    void testMultipleComparison_EqualFile()
    {
        String resource = "Vcf/Examples"
        String extension = "tsv"

        def Assertions = []
        def ErrorMsgs = []

        // Input Data
        def Files = ['unpacked', 'vep', 'unpacked_prostate', 'unpacked_mcri'] // unpacked_exome takes too much time
        def Lines = [18, 82, 9931, 170 ]

        // Check all files
        for(def i = 0; i < Lines.size(); i++)
        {
            Assertions.add( CompareTsvFiles( resource,  Files[i],  extension, Files[i] , Lines[i])  )
            ErrorMsgs.add( "[T E S T]: Tsv Files ${Files[i]} and ${Files[i]} have different lines than ${Lines[i]}")
        }

        // Perform assertions
        for(def i = 0; i < Lines.size(); i++)
        {
            assert Assertions[i] : ErrorMsgs[i]
        }

    }

    /**
        Comparing multiple files
        static int compareTsv( File f1, File f2, List keys, List ignore, List always )
    */
    void testMultipleComparison_DifferentFile()
    {
        String resource = "Vcf/Examples"
        String extension = "tsv"

        def Files = ['unpacked', 'unpacked', 'unpacked', 'unpacked_prostate']
        def Comps = ['unpacked_prostate', 'unpacked_mcri', 'vep', 'unpacked_mcri']
        def Lines = [0, 0, 0, 0 ]

        def Assertions = []
        def ErrorMsgs = []

        // Check all files
        for(def i = 0; i < Lines.size(); i++)
        {
            Assertions.add( CompareTsvFiles( resource,  Files[i],  extension, Comps[i] , Lines[i])  )
            ErrorMsgs.add( "[T E S T]: Tsv Files ${Files[i]} and ${Comps[i]} have different lines than ${Lines[i]}")
        }

        // Perform assertions
        for(def i = 0; i < Lines.size(); i++)
        {
            assert Assertions[i] : ErrorMsgs[i]
        }

    }

    /**
        Main Function to test
        static int compareTsv( File f1, File f2, List keys, List ignore, List always )
        from two different files
     */
    boolean CompareTsvFiles(String resource, String file, String extension, String fileExtra, int lines)
    {

        def file_A = new Tsv(PathGeneratorFile( resource,  file, extension))
        File fa = PathGeneratorFile( resource,  file, extension)
        file_A.load(true)

        file = fileExtra

        def file_B = new Tsv(PathGeneratorFile( resource,  file, extension))
        File fb = PathGeneratorFile( resource,  file, extension)
        file_B.load(true)


        def comparison =  CompareTsv.compareTsv(fa, fb,file_A.cols,  [], file_A.cols)
        return comparison == lines
    }

    File PathGeneratorFile(String resource, String file,String extension )
    {
        File basePath = new File(CompareTsvTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        if ( !basePath.exists() )
            throw new RuntimeException('ERROR in [FILE]:' + basePath + ' does not exist !')


        return basePath
    }

}
