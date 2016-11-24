package org.petermac.util

/**
 * Created by lara luis on 27/01/2016.
 */

import static groovy.io.FileType.FILES

class VcfDbCheckTest extends GroovyTestCase
{

    String DB

    //TODO we need more tests and asserts in here
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

        String seqrun = '12K0304_CCCAACCT-TAAGACA'
        String sample = '12K0304'
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
