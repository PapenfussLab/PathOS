package org.petermac.pathos.pipeline

/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 2/10/13
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
class SubmitTest  extends GroovyTestCase
{
    void testExecute()
    {
        def seqrun = "Pipeline"
        def phases = ["align", "igv" ]
        def params = [ phases: phases, seqrun: '130926']

    }
}
