package org.petermac.util

/**
 * Created by lara luis on 22/01/2016.
 */
class ExtractorTest extends GroovyTestCase {

    /**
     * TESTING static String wordToText( File wf )
     */
    void testWordToText()
    {
        String resource = "Word"
        String file = "shakespeare"
        String extension = "docx"

        def doc =   PathGeneratorFile( resource,  file, extension)
        def ext = new Extractor()
        assert ext.wordToText(doc) instanceof String :"[T E S T]: This is not an instance of string"

    }

    /**
     * TESTING  private static int extractWord( List<File> inws, String rdb, File ofile, File tfile )
     */
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
        assert postOfile.exists() &&  postTfile.exists() : "[T E S T]: Files ${postOfile} and ${postTfile} don't exist! "
    }

    /**
     * TESTING  private static boolean mutAdd( String gene, String mut )
     *
     */
    void testAddMethods()
    {
        def ext = new Extractor()
        String gene = "RAS"
        String mut =  "c.1940A>T, p.Asn647Ile"
        assert ext.mutAdd( gene, mut): "[T E S T]: gene, ${gene} or mut ${mut} is not in format (gene='word', mut=p.c.1940A>T, p.Asn647Ile)"
        mut = "c.1940A>T"
        assert ext.mutAdd( gene, mut): "[T E S T]: gene, ${gene} or mut ${mut} is not in format (gene='word', mut=p.c.1940A>T, p.Asn647Ile)"
    }


    File PathGeneratorFile(String resource, String file,String extension )
    {
        File basePath = new File(ExtractorTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        if ( !basePath.exists() )
            throw new RuntimeException('ERROR in [FILE]:' + basePath + ' does not exist !')


        return basePath
    }
}
