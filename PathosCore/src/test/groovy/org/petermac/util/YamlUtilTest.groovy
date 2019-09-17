/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.util

/**
 * Test Yaml Utility functions
 */
class YamlUtilTest extends GroovyTestCase
{
    void testLoadYamlPatients()
    {
        File yfile = new File(YamlUtilTest.getClass().getResource( "/Yaml/Patients.yaml" ).getPath())

        List pats = YamlUtil.load( yfile )

        assert pats
        assert pats.size() == 2

        int n = 1
        for ( pat in pats )
        {
            assert pat
            println "Rec ${n++}"
            println pat
        }

        assert pats[0].source == [ 'petermac', 'auslab', 'iguana' ]
        def patient = pats[0].content
        assert patient
        assert patient.patSamples.size() == 2
        assert patient.patSamples[0].patAssays.size() == 2
        assert patient.patSamples[0].patAssays[0] == [ testSet:'M487', testName:'FLD_REP_MEL']

        patient = pats[1].content
        assert patient.patSamples.size() == 1
    }

    void testLoadYamlSeqruns()
    {
        File yfile = new File(YamlUtilTest.getClass().getResource( "/Yaml/Seqruns.yaml" ).getPath())

        List sruns = YamlUtil.load( yfile )

        assert sruns
        assert sruns.size() == 2

        int n = 1
        for ( sr in sruns )
        {
            assert sr
            println "Rec ${n++}"
            println sr
        }

        def sr = sruns[0].content
        assert sr.seqSamples.size() == 4
        assert sr.seqSamples[0] ==  [
                                    sampleName: '17K0874',
                                    sampleType: 'Tumour',
                                    panel:      'Germline_v4-8_071013_with_off_target_manifest',
                                    analysis:   'mp_dualAmplicon',
                                    userName:   'Ken Doig',
                                    userEmail:  'ken.doig@petermac.org',
                                    laneNo:     1
                                    ]
    }

    void testYamlDate()
    {
        File yfile = new File(YamlUtilTest.getClass().getResource( "/Yaml/Date.yaml" ).getPath())

        List pats = YamlUtil.load( yfile )

        assert pats
        assert pats.size() == 2

        int n = 1
        for ( pat in pats )
        {
            println pat
        }

        println pats[0].dob
        assert Date.parse( 'yyyy-MM-dd', pats[0].dob as String ) == Date.parse( 'dd/MM/yyyy', '18/10/1960')
    }

}
