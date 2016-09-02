package org.petermac.pathos.pipeline


/**
 * Created for PathOS.
 *
 * Description:
 *
 *
 *
 * User: doig ken
 * Date: 23/02/2015
 * Time: 2:10 PM
 */

class MakePanelIntTest extends GroovyTestCase
{

    void testConsturctor()
    {
        def pan = new MakePanel()
    }

    void testRunAmplicon()
    {
        String panel = 'CancerGNA10212011_170_190_Viewermanifest'
        String ampfFile = "outFile.tsv"

        def pan = new MakePanel()
        assert pan.runAmplicon(panel, ampfFile)

    }

    void testRunBed()
    {
        String panel = 'CancerGNA10212011_170_190_Viewermanifest'
        String bedFile = "outFile.bed"

        def pan = new MakePanel()
        assert pan.runBed(  panel,  bedFile )
    }

}
