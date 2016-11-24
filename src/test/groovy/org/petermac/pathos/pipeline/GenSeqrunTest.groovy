package org.petermac.pathos.pipeline

/**
 * Created by lara luis on 2/02/2016.
 */
class GenSeqrunTest extends GroovyTestCase
{

    void testConstructor()
    {
        def gsr  = new GenSeqrun()
    }

    ///Volumes/Pathology/NGS/Samples/Molpath/130822_M00139_0022_000000000-A5D1J/13K0341

    void testProcessSeqrun()
    {

        String seqrun = '130822_M00139_0022_000000000-A5D1J'
        String sample = '13K0341'

        String resource = "Pipeline"
        String file = "LIMS_130822_M00139_0022_000000000-A5D1J"
        String extension ="xml"

        String limsxml = new File(GenSeqrunTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())


        String pipeline = 'mp_vcfAmplicon'

        String baseDir = new File(GenSeqrunTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath()).getParent()
        String outfile = baseDir + '/ProcessSeqrun.out'



        def gsr  = new GenSeqrun()
        assert gsr.processSeqrun(seqrun, sample, limsxml, pipeline, baseDir, outfile)


    }

    void testProcessSeqrunFalse()
    {
        String seqrun = '130822_M00139_0022_000000000-A5D1J'
        String sample = '13K0341'

        String resource = "Pipeline"
        String file = "LIMS_130822_M00139_0022_000000000-A5D1J"
        String extension ="xml"

        String limsxml = new File(GenSeqrunTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())

        String pipeline = 'mp_vcfAmplicon'

        String baseDir = new File(GenSeqrunTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath()).getParent()


        String outfile = baseDir + '/ProcessSeqrun.out'

        def gsr  = new GenSeqrun()

        assert false == gsr.processSeqrun(seqrun, sample, '', pipeline, baseDir, outfile)// remove ! for Bamboo

    }


}
