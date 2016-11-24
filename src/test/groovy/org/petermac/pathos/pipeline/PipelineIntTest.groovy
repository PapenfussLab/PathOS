package org.petermac.pathos.pipeline

/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 15/04/13
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
class PipelineIntTest extends GroovyTestCase
{
    //Pipeline    pipe
    Panel       panel
    File        ifile
    def         sample = "MSH2-ex7-2r-2-6"
    def         seqrun = "130218_M01053_0021_000000000-A34FK"


    void setUp()
    {
       // pipe  = new Pipeline()
        panel = new Panel( "CancerGNA10212011_170_190_Viewermanifest" )
        ifile = new File("IGV_Session.xml")
    }

    //TODO: commented not used
    void testVersion()
    {
       // assertEquals( "Version", "Molecular Pathology Pipeline version 1.0", pipe.version)
    }

    void testPanel()
    {
        //assert panel.valid(), "Panel ${panel} should exist"
    }

    void testAlign()
    {
        //  Won't work on laptop IDE
        //
        //  pipe.align( sample, seqrun, xxx, panel )
    }

    void testVariant_call()
    {
        //  Won't work on laptop IDE
        //
        // pipe.variant_call( sample, seqrun, panel )
    }

    void testAnnotate()
    {
        //  Won't work on laptop IDE
        //
        // pipe.annotate( sample, seqrun, panel )
    }



    void testIgv_session()
    {
        //  Remove IGV file
        //
//        ifile.delete()
//        assert ! ifile.exists(), "Deleted file exists"
//
//        pipe.igv_session( seqrun, sample, panel )
//
//        assert ifile.exists(), "IGV session complete"
    }
}
