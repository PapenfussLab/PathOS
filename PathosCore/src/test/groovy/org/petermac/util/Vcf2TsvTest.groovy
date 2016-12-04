package org.petermac.util

/**
 * Created by lara luis on 27/01/2016.
 */
class Vcf2TsvTest extends GroovyTestCase
{

    void testComapre()
    {
        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension ="vcf"

        String fileOut = "tumourOut"
        String extensionOut ="tsv"

        File cvff = new File(Vcf2TsvTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        String ResPath = cvff.getParent()
        File tsvf = new File( "/${ResPath}/${fileOut}.${extensionOut}"  )
        println tsvf

        def converter = new Vcf2Tsv()
        // define parameters: String sample = '', String seqrun = '', String panel = ''
        converter.vcf2Tsv(cvff, tsvf, '','','', cvff)


    }


}
