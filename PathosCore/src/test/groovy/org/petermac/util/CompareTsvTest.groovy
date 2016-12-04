package org.petermac.util

/**
 * Created by lara luis on 21/01/2016.
 */
class CompareTsvTest extends GroovyTestCase
{

    void testMain()
    {
        def ts = new CompareTsv()
    }

    void testComaprisonCall()
    {

        String resource = "Vcf/Examples"
        String file = "unpacked_prostate"
        String extension = "tsv"
        String fileExtra = "unpacked_mcri"
        int lines = 0

        assert CompareTsvFiles( resource,  file,  extension,  fileExtra,  lines)

    }

    // clean this one
    void testComaprisonAuto()
    {

        String resource = "Vcf/Examples"
        String file = "unpacked"
        String extension = "tsv"
        String fileExtra = "unpacked"
        int lines = 18
        assert CompareTsvFiles( resource,  file,  extension,  fileExtra,  lines)

        file = "vep"
        fileExtra = "vep"
        lines = 82
        assert CompareTsvFiles( resource,  file,  extension,  fileExtra,  lines)

        file = "unpacked_prostate"
        fileExtra = "unpacked_prostate"
        lines = 9931
        assert CompareTsvFiles( resource,  file,  extension,  fileExtra,  lines)

        file = "unpacked_mcri"
        fileExtra = "unpacked_mcri"
        lines = 170
        assert CompareTsvFiles( resource,  file,  extension,  fileExtra,  lines)

        // TODO; takes too much time
        //file = "unpacked_exome"
        //fileExtra = "unpacked_exome"
        //lines = 1
        //assert CompareTsvFiles( resource,  file,  extension,  fileExtra,  lines)

    }

    void testCompareFiles()
    {
        String resource = "Vcf/Examples"
        String file = "unpacked"
        String extension = "tsv"
        String fileExtra = "unpacked_prostate"
        int lines = 0
        assert CompareTsvFiles( resource,  file,  extension,  fileExtra,  lines)

        fileExtra = "unpacked_mcri"
        lines = 0
        assert CompareTsvFiles( resource,  file,  extension,  fileExtra,  lines)

        fileExtra = "vep"
        lines = 0
        assert CompareTsvFiles( resource,  file,  extension,  fileExtra,  lines)

        file = "unpacked_prostate"
        fileExtra = "unpacked_mcri"
        lines = 0
        assert CompareTsvFiles( resource,  file,  extension,  fileExtra,  lines)

    }

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
