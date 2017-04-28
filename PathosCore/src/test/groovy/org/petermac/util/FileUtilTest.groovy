package org.petermac.util

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;


/**
 * Created by lara luis on 25/01/2016.
 */
class FileUtilTest  extends GroovyTestCase
{

    /**
     * TESTING  static File copyUrl( String url )
     */
    void testCpyUrl()
    {
        String resource = "Dummy"
        String file = "test"
        String extension = "txt"

        def furl = "file:${PathGeneratorStr( resource,  file, extension)}"
        String dir = new File(FileUtilTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath()).getParent()
        String fname = "testOut.txt"

        def fu = new FileUtil()

        assert fu.copyUrl(furl, fname, dir).toString() == "${dir}/${fname}" : "[T E S T]: File Util names differ, ${fu.copyUrl(furl, fname, dir).toString() } and ${dir}/${fname}"

    }

    /**
     * TESTING static File copyUrl( String url )
     */
    void testCopyUrlSingle()
    {
        String resource = "Dummy"
        String file = "test"
        String extension = "txt"

        def furl = "file:${PathGeneratorStr( resource,  file, extension)}"

        def fu = new FileUtil()
        assert fu.copyUrl(furl).exists():"[T E S T]: File does not exist ${furl}"

    }

    /**
     * TESTING static File tmpFile( String prefix = 'mp_', String suffix = '.tmp' )
     */
    void testTempfile()
    {
        def fu = new FileUtil()
        assert fu.tmpFile( ).exists(): "[T E S T]: File does noe exist"
    }

    /**
     * TESTING static File tmpFile( String prefix = 'mp_', String suffix = '.tmp' )
     */
    void testTempfileDefined()
    {
        def fu = new FileUtil()
        assert fu.tmpFile("mp-",".temp" ).exists(): "[T E S T]: File does not exist"
    }

    void testTempFixedFile()
    {
        def fu = new FileUtil()
        assert !fu.tmpFixedFile( ).exists(): "[T E S T]: File does not exist"
    }

    /**
     * TRSTING static File tmpFixedFile( String path = '/tmp', String prefix = 'tmp_')
     */
    void testTempFixedFileDefined()
    {
        String resource = "Dummy"
        String file = "test"
        String extension = "txt"

        String dir = new File(FileUtilTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath()).getParent()

        def fu = new FileUtil()
        assert !fu.tmpFixedFile(dir, "temp_" ).exists() && fu.tmpFixedFile(dir, "temp_" ).getParent().toString() == dir :"[T E S T]: Dir does not exist"
    }

    String PathGeneratorStr(String resource, String file,String extension )
    {
        File basePath = new File(TsvTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        if ( !basePath.exists() )
            throw new RuntimeException('ERROR in [FILE]:' + basePath + ' does not exist !')

        String p = basePath
        return p
    }

}
