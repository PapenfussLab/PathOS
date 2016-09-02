/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

import org.petermac.pathos.pipeline.VcfMerge

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Simple integration test for VcfMerging
 *
 * User: Ken Doig
 * Date: 26/05/2016
 * Time: 1:20 PM
 */
class VcfMergeTest extends GroovyTestCase
{
    static VcfMerge vm = new VcfMerge()

    void testVcfMerge()
    {
        def tumour = new Vcf( PathGeneratorFile( 'Vcf/Examples', 'tumour', 'vcf'))
        def tlines = tumour.load()
        assert tlines == 52
        Map tm = tumour.getTsvMap()

        def normal = new Vcf( PathGeneratorFile( 'Vcf/Examples', 'minus', 'vcf'))
        def nlines = normal.load()
        assert nlines == 20

        Vcf mrg = vm.mergeVcf( tumour, 'Tumour', normal, 'Normal' )

        //  All rows are annotated with merge status
        //
        List<Map> rows = mrg.rowMaps
        for ( row in rows )
        {
            assert row.Identified in ['Tumour','Normal','Intersection']
        }

        //  Merge is a union of two VCFs
        //
        println( "VCF lines: Primary=${tlines} Primary=${nlines} Merged=${rows.size()}")
        assert rows.size() <= nlines + tlines
        assert rows.size() >= Math.min( nlines, tlines )
        assert rows.size() == 60

    }

    static File PathGeneratorFile(String resource, String file,String extension )
    {
        File basePath = new File(VcfMergeTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        if ( ! basePath.exists() )
            throw new RuntimeException('ERROR in [FILE]:' + basePath + ' does not exist !')

        return basePath
    }
}
