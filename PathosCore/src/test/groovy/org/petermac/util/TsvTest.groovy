/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Test suite for TSV file parsing and manipulation
 *
 * User: doig ken
 * Date: 22/06/2014
 * Time: 7:29 PM
 */
class TsvTest  extends GroovyTestCase
{
    void testUrlLoad()
    {
        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension = "vcf"

        def tsv = new Tsv(PathGeneratorFile( resource,  file, extension))

        def nlines  = tsv.load( true )

        assert nlines == 52
        assert tsv.nrows() == nlines

        Map tm = tsv.getTsvMap()

        assert tm.preamble.size() == 111
        assert tm.cols.size() == 10
        assert tm.rows.size() == nlines

        println ( "Columns in VCF")

        def idx = 0
        for ( col in tsv.cols )
        {
            println( "${idx}: ${col}")
            ++idx
        }
    }

    void testFileLoad()
    {
        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension = "vcf"

        def tsv = new Tsv(PathGeneratorFile( resource,  file, extension))

        def nlines  = tsv.load( true )

        assert nlines == 52

        Map tm = tsv.getTsvMap()

        assert tm.preamble.size() == 111
        assert tm.cols.size() == 10
        assert tm.rows.size() == nlines
    }

    void testFileLoadVep()
    {
        String resource = "Vcf/Examples"
        String file = "vep"
        String extension = "tsv"

        def tsv = new Tsv(PathGeneratorFile( resource,  file, extension))

        def nlines  = tsv.load( true )

        assert nlines == 82

        List<Map> rm = tsv.getRowMaps()
        int ncol = tsv.cols.size()

        assert rm.size() == nlines
        for ( row in rm )
            assert row.size() == ncol
    }

    void testFileLoadAno()
    {
        String resource = "Vcf/Examples"
        String file = "ano"
        String extension = "tsv"

        def tsv = new Tsv(PathGeneratorFile( resource,  file, extension))

        def nlines  = tsv.load( true )

        assert nlines == 18

        List<Map> rm = tsv.getRowMaps()

        assert rm.size() == nlines
        for ( row in rm )
            println row
    }

    void testWrite()
    {
        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension = "vcf"

        File basePath = PathGeneratorFile( resource,  file, extension)
        def tsv = new Tsv(basePath)

        def nlines  = tsv.load( true )

        assert nlines == 52

        file = "tsvout"
        extension = "tsv"

        // Note The difference in the path!
        def srcPath =  basePath.getParent()
        def res = tsv.write( "${srcPath}/${file}.${extension}" )

        assert res

        basePath = new File(TsvTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        println basePath
        Tsv tf = new Tsv(basePath)
        def lines = tf.load( true )

        assert lines == 52 // it was 164


    }

    void testMinus()
    {
        //  Expected VCF
        //
        String resource = "Vcf/Examples"
        String file = "expected"
        String extension = "vcf"

        def exp = new Tsv(PathGeneratorFile( resource,  file, extension))

        assert exp.load( true ) == 38

        //  Actual VCF
        //
        file = "actual"
        File basePath = PathGeneratorFile( resource,  file, extension)
        def act = new Tsv(basePath)

        assert act.load( true ) == 18

        //  Difference
        //
        List keys = [ 'CHROM', 'POS', 'REF', 'ALT' ]
        def min = exp.minus( act, keys )
        assert min
        println( "TsvTest ${min.getTsvMap()}")

        file = "minus"
        def srcPath =  basePath.getParent()
        assert min.write( "${srcPath}/${file}.${extension}" )

        def tf = new Tsv(PathGeneratorFile( resource,  file, extension))

        assert tf.load(true) == 20 // it was 44

        min = act.minus( exp, keys )
        assert min.nrows() == 0
    }

    void testIntersect()
    {
        //  Expected VCF
        //
        String resource = "Vcf/Examples"
        String file = "expected"
        String extension = "vcf"

        def exp = new Tsv(PathGeneratorFile( resource,  file, extension))

        assert exp.load( true ) == 38

        //  Actual VCF
        //
        file = "actual"
        File basePath = PathGeneratorFile( resource,  file, extension)
        def act = new Tsv(basePath)

        assert act.load( true ) == 18

        //  Difference
        //
        List keys = [ 'CHROM', 'POS', 'REF', 'ALT' ]
        def ins = exp.intersect( act, keys )
        assert ins

        file = "intersect"
        def srcPath =  basePath.getParent()

        assert ins.write( "${srcPath}/${file}.${extension}")

        def tf = new Tsv(PathGeneratorFile( resource,  file, extension))

        assert tf.load(true) == 18

    }

    void testAddColumn()
    {
        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension = "vcf"

        File basePath = PathGeneratorFile( resource,  file, extension)
        def tsv = new Tsv(basePath)

        int nrows = tsv.load(true)

        List col = []
        for ( i in [0..nrows-1]) { col[i] = i }

        assert tsv.addColumn( 'NEW', col)

        file = "added"
        extension ="tsv"

        def srcPath =  basePath.getParent()
        assert tsv.write("${srcPath}/${file}.${extension}")

        tsv = new Tsv(PathGeneratorFile( resource,  file, extension))
        tsv.load(true)

        //  Check column header
        //
        def cols = tsv.getCols()
        assert cols[-1] == 'NEW'

        //  Check each row
        //
        List rows = tsv.getTsvMap().rows
        rows.eachWithIndex{ List row, int i -> assert row[-1] == i as String }
    }

    //URL url = getClass().getClassLoader().getResource("someresource.xxx");

    void testgetInStream()
    {
        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension = "vcf"

        Tsv tsv = new Tsv(PathGeneratorFile( resource,  file, extension))
        assert tsv.getInStream() != null
        //def tsv = new Tsv(getClass().getClassLoader().getResource(PathGeneratorStr( resource,  file, extension )))

    }

    void testWriteList()
    {

        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension = "vcf"

        File basePath = PathGeneratorFile( resource,  file, extension)
        def tsv = new Tsv(basePath)

        def nlines  = tsv.load( true )

        assert nlines == 52

        file = "tsvout_cols"
        extension = "tsv"

        // Note The difference in the path!
        def srcPath =  basePath.getParent()
        def res = tsv.write( new File("${srcPath}/${file}.${extension}"), tsv.getCols() )

        assert res

        basePath = new File(TsvTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        println basePath
        Tsv tf = new Tsv(basePath)
        def lines = tf.load( true )


    }

    void testGetSetStream()
    {
        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension = "vcf"

        Tsv tsv = new Tsv(PathGeneratorFile( resource,  file, extension))
        def is = tsv.getInStream()
        tsv.setInStream( tsv.getInStream() )
        assert is == tsv.getInStream()
        //tsv.setFilename( tsv.getFilename() )

    }

    void testSetGetFileName()
    {
        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension = "vcf"

        Tsv tsv = new Tsv(PathGeneratorFile( resource,  file, extension))
        def fn = tsv.getFilename()
        tsv.setFilename( tsv.getFilename() )
        assertEquals(fn, tsv.getFilename() )
    }

    void testGetTsvMap()
    {
        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension = "vcf"

        Tsv tsv = new Tsv(PathGeneratorFile( resource,  file, extension))
        tsv.load(true)

        assert  (tsv.getTsvMap().keySet().contains('preamble') &&
                tsv.getTsvMap().keySet().contains('cols') &&
                tsv.getTsvMap().keySet().contains('rows')) == true
    }

    void testGetSetTsvMap()
    {
        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension = "vcf"

        Tsv tsv = new Tsv(PathGeneratorFile( resource,  file, extension))
        tsv.load(true)
        tsv.setTsvMap(tsv.getTsvMap())

        assert  (tsv.getTsvMap().keySet().contains('preamble') &&
                tsv.getTsvMap().keySet().contains('cols') &&
                tsv.getTsvMap().keySet().contains('rows')) == true

    }


    void testSetGetCols()
    {
        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension = "vcf"

        Tsv tsv = new Tsv(PathGeneratorFile( resource,  file, extension))
        tsv.load(true)
        def cols = tsv.getCols()
        tsv.setCols(tsv.getCols())

        assert  tsv.getCols() == cols
    }

    void testAddColBasic()
    {
        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension = "vcf"

        Tsv tsv = new Tsv(PathGeneratorFile( resource,  file, extension))
        tsv.load(true)

        //println tsv.getRowMaps()
        tsv.addColumn("TEST_ADD","DATA")
        def content = tsv.getRowMaps()[0]["TEST_ADD"]

        assert tsv.getCols().contains("TEST_ADD")
        assertEquals(content, 'DATA')

    }


    File PathGeneratorFile(String resource, String file,String extension )
    {
        File basePath = new File(TsvTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        if ( !basePath.exists() )
            throw new RuntimeException('ERROR in [FILE]:' + basePath + ' does not exist !')


        return basePath
    }

}