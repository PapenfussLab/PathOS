package org.petermac.util

/**
 * Created by lara luis on 22/01/2016.
 */
class ExtractorTest extends GroovyTestCase {

    void testWordToText()
    {
        String resource = "Word"
        String file = "shakespeare"
        String extension = "docx"

        def doc =   PathGeneratorFile( resource,  file, extension)
        def ext = new Extractor()
        assert ext.wordToText(doc) instanceof String

    }

    void testExtractWord()
    {
        String resource = "Word"
        String file = "shakespeare"
        String extension = "docx"

        def doc =   PathGeneratorFile( resource,  file, extension)
        File basePath = PathGeneratorFile( resource,  file, extension)
        def srcPath =  basePath.getParent()
        def ofile =  new File("${srcPath}/ofile")
        def tfile =  new File("${srcPath}/tfile")

        def ext = new Extractor()
        //need an apropiate file for this
        assert ext.extractWord([doc,doc],"pa_test", ofile,  tfile) == 2

        File postOfile = new File("${srcPath}/ofile")
        File postTfile = new File("${srcPath}/tfile")
        print postOfile
        assert postOfile.exists() &&  postTfile.exists()
    }


    void testAddMethods()
    {
        def ext = new Extractor()
        String gene = "c.1940A>T"
        String mut =  "p.Asn647Ile"
        ext.mutAdd( gene, mut)


    }

//    // Requires Word Files
//    void testGetReportMap()
//    {
//        assert false
//    }
//
//    //Requires word files
//    void testSetReportMap()
//    {
//        assert false
//    }

    File PathGeneratorFile(String resource, String file,String extension )
    {
        File basePath = new File(ExtractorTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        if ( !basePath.exists() )
            throw new RuntimeException('ERROR in [FILE]:' + basePath + ' does not exist !')


        return basePath
    }
}
