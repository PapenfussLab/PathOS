package org.petermac.util

/**
 * Created by lara luis on 27/01/2016.
 */
class VcfCompareTest extends GroovyTestCase
{
    //TODO we need more testing in this
    void testCompare()
    {

        String resource = "Vcf/Examples"
        String file = "actual"
        String extension ="vcf"

        def vcfA = new File(VcfCompareTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())

        file = "added"

        def vcfB = new File(VcfCompareTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())


        def vcfComp = new VcfCompare()
        def out =  vcfComp.runVcfCompare( vcfA, vcfB )


        assert out.tp == 18 && out.fp == 0 && out.fn == 0 :"vcfComp.runVcfCompare( vcfA, vcfB ) is failing or values are not as expected"

        file = "tumour"
        vcfA = new File(VcfCompareTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        out =  vcfComp.runVcfCompare( vcfA, vcfA )
        out.tp == 52 && out.fp == 0 && out.fn == 0


    }


}
