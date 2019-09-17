package org.petermac.pathos.pipeline

import java.text.SimpleDateFormat


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
        String ampfFile = "outFileTest" + new Date().format("yyyyMMddHHmmss") + ".tsv"

        def pan = new MakePanel()
        assert pan.runAmplicon(panel, ampfFile)

    }

    void testRunBed()
    {
        String panel = 'CancerGNA10212011_170_190_Viewermanifest'
        String bedFile = "outFile" +  new Date().format("yyyyMMddHHmmss") + ".bed"

        def pan = new MakePanel()
        assert pan.runBed(  panel,  bedFile )
    }

}
