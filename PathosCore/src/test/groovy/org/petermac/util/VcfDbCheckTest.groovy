package org.petermac.util

/**
 * Created by lara luis on 27/01/2016.
 */

import static groovy.io.FileType.FILES

class VcfDbCheckTest extends GroovyTestCase
{

    String DB

    void setUp()
    {
        def env = System.getenv()

        DB = env["PATHOS_DATABASE"]
        println "Connecting to: ${DB}"


    }

    // Maybe this test is really intense
    // consider make it more simple
    void    testConnect()
    {
        def checker = new VcfDbCheck()

        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension ="vcf"

        String seqrun = '150217_SN1055_0250_AC66EMACXX'
        String sample = '12M9931'
        String dbname = DB

        def vcfFiles = []

        def basePath = new File(TsvTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        def outErrFile = "${basePath.getParent()}/CheckVcfsErr.err"

        new File( basePath.getParent() ).eachFileRecurse(FILES) {
            if(it.name.endsWith('.vcf')) {
               // println it
                vcfFiles.add(it)
            }
        }

        // This will make the test faster
        if(vcfFiles.size() >= 2)
            vcfFiles = vcfFiles[0..2]

        def nrow = checker.checkVcfs( seqrun, sample, vcfFiles, dbname, new File(outErrFile), true, false )

    }


}
