package org.petermac.pathos.pipeline



/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 2/10/13
 * Time: 11:19 AM
 * To change this template use File | Settings | File Templates.
 */
class SeqrunLimsTest  extends GroovyTestCase
{

    //  Parse MiSeq LIMS.xml file
    //
    void testValidateMiSeq()
    {
        String resource = "Pipeline"
        String file = "LIMS_130822_M00139_0022_000000000-A5D1J"
        String extension ="xml"


        String f   = PathGeneratorStr( resource,  file, extension )
        println f
        def srl = new SeqrunLims()
        def m   = srl.parseLims( f  )

        //  doesn't exist
        //

        //m = srl.parseLims( f )
        assert m.seqrun   == '130822_M00139_0022_000000000-A5D1J'
        assert m.platform == 'MiSeq'
        assert m.sepe == 'PE'
        assert m.library == 'Ca2015_TSCAP_Plate11_Pool2'

        List sams = m.samples
        assert 31 == sams.size()

        Map sample = sams[23]

        assert sample.sample    == '13K0341'
        assert sample.analysis  == 'Pathology Amplicon Cancer 2015'
        assert sample.reference == 'CancerGNA10212011_170_190_Viewermanifest'
        assert sample.username  == 'Ravikiran Vedururu'
        assert sample.useremail == 'ravikiran.vedururu@petermac.org'
    }


    //  Test HiSeq LIMS.xml parsing
    //
    void testValidateHiSeq()
    {
        String resource = "Pipeline"
        String file = "LIMS_130916_SN1055_0170_AC2JWTACXX"
        String extension ="xml"

        String f   = PathGeneratorStr( resource,  file, extension )
        def srl = new SeqrunLims()
        def m = srl.parseLims( f )

        assert m.seqrun   == '130916_SN1055_0170_AC2JWTACXX'
        assert m.platform == 'HiSeq2000'
        assert m.sepe == 'PE'
        assert m.library == 'MGC367'

        List sams = m.samples
        assert 16 == sams.size() // 44

        Map sample = sams[5]

        assert sample.sample    == 'AOCS-116-2-7'
        assert sample.analysis  == 'RNA-Seq'
        assert sample.reference == 'Human(HG19)'
        assert sample.username  == 'Elizabeth Christie'
        assert sample.useremail == 'elizabeth.christie@petermac.org'
    }

    //  Test MiSeq runParameters.xml parsing
    //
    void testParseRunParametersMiSeq()
    {
        String resource = "Pipeline"
        String file = "runParameters_MiSeq"
        String extension ="xml"

        String f   = PathGeneratorStr( resource,  file, extension )
        def srl = new SeqrunLims()
        def m = srl.parseRunParameters( f )

        assert m.seqrun         == '130822_M00139_0022_000000000-A5D1J'
        assert m.experiment     == 'Ca2015_TSCAP_Plate11_Pool2'
        assert m.application    == 'MiSeq Control Software'
        assert m.scanner        == 'M00139'
        assert m.samplesheet    == 'CA2015_TSCAP Plate11 Pool2'
        assert m.useremail      == 'ravikiran.vedururu@petermac.org'
        assert m.readlen        == '150'
        assert m.readcycles     == [    [NumCycles:'151', Number:'1', IsIndexedRead:'N'],
                                        [NumCycles:'8', Number:'2', IsIndexedRead:'Y'],
                                        [NumCycles:'8', Number:'3', IsIndexedRead:'Y'],
                                        [NumCycles:'151', Number:'4', IsIndexedRead:'N']
                                    ]
    }

    //  Test MiSeq runParameters.xml parsing
    //
    void testParseRunParametersHiSeq()
    {
        String resource = "Pipeline"
        String file = "runParameters_HiSeq2000"
        String extension ="xml"

        String f   = PathGeneratorStr( resource,  file, extension )
        def srl = new SeqrunLims()
        def m = srl.parseRunParameters( f )

        assert m.seqrun         == '130917_SN1055_0171_BD2HNBACXX'
        assert m.experiment     == 'MGC370-373'
        assert m.application    == 'HiSeq Control Software'
        assert m.scanner        == 'SN1055'
        assert m.samplesheet    == null
        assert m.useremail      == null
        assert m.readlen        == '100'
        assert m.readcycles     == [    [NumCycles:'101', Number:'1', IsIndexedRead:'N'],
                                        [NumCycles:'9', Number:'2', IsIndexedRead:'Y'],
                                        [NumCycles:'101', Number:'3', IsIndexedRead:'N']
                                    ]

    }

    void testParseRunInfoMiSeq()
    {
        String resource = "Pipeline"
        String file = "RunInfo_MiSeq"
        String extension ="xml"

        String f   = PathGeneratorStr( resource,  file, extension )
        def m = SeqrunLims.parseRunInfo( f )

        assert m.id         == '130614_M00139_0011_000000000-A4WPE'
        assert m.flowcell     == '000000000-A4WPE'
        assert m.instrument    == 'M00139'
        assert m.date        == '130614'
        assert m.readlen    == '150'
        assert m.lanes     == '1'
        assert m.readcycles     == [
                [NumCycles:'151', Number:'1', IsIndexedRead:'N'],
                [NumCycles:'8', Number:'2', IsIndexedRead:'Y'],
                [NumCycles:'8', Number:'3', IsIndexedRead:'Y'],
                [NumCycles:'151', Number:'4', IsIndexedRead:'N']
        ]
    }

    void testParseRunInfoHiSeq()
    {
        String resource = "Pipeline"
        String file = "RunInfo_HiSeq2000"
        String extension ="xml"

        String f   = PathGeneratorStr( resource,  file, extension )
        def m = SeqrunLims.parseRunInfo( f )

        assert m.id         == '131125_SN1055_0179_AC2V4LACXX'
        assert m.flowcell     == 'C2V4LACXX'
        assert m.instrument    == 'SN1055'
        assert m.date        == '131125'
        assert m.readlen    == '100'
        assert m.lanes     == '8'
        assert m.readcycles     == [
                [NumCycles:'101', Number:'1', IsIndexedRead:'N'],
                [NumCycles:'9', Number:'2', IsIndexedRead:'Y'],
                [NumCycles:'101', Number:'3', IsIndexedRead:'N']
        ]
    }

    String PathGeneratorStr(String resource, String file,String extension )
    {
        File basePath = new File(SeqrunLimsTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        if ( !basePath.exists() )
            throw new RuntimeException('ERROR in [FILE]:' + basePath + ' does not exist !')

        String p = basePath
        return p
    }

    File PathGeneratorFile(String resource, String file,String extension )
    {
        File basePath = new File(SeqrunLimsTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        if ( !basePath.exists() )
            throw new RuntimeException('ERROR in [FILE]:' + basePath + ' does not exist !')


        return basePath
    }
}
