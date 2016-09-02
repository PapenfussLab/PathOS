package org.petermac.pathos.pipeline
/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 20/05/13
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
class LoadAlamutIntTest extends GroovyTestCase
{
    File xmlfile1
    File xmlfile2
    File xmlfile3
    File outfile
    def exp = [ '#gene':'PIK3CA',
                assembly:'GRCh37',
                vartype:'Substitution',
                note:'',
                pos:'178936091',
                ref:'G',
                alt:'A',
                hgvsg:'chr3:g.178936091G>A',
                hgvsc:'c.1633G>A',
                hgvsp:'p.Glu545Lys',
                refseq: 'NM_006218.2',
                classtype:'CMGS_VGKL_5',
                classlevel:'C5: Pathogenic',
                pathogenic:'yes',
                udate:'2013-05-22',
                utime:'17:21:05',
                samples:'12M0201,12K0027']

    void setUp()
    {
        xmlfile1 = new File('Alamut/XML/PIK3CA.mut')
        xmlfile2 = new File('Alamut/XML/IDH1.mut')
        xmlfile3 = new File('Alamut/XML/APC.mut')

        outfile = new File('out.tsv')
    }

    //TODO: comented not used

    void testReadXML()
    {
//        assert xmlfile1.exists()
//
//        LoadAlamut la = new LoadAlamut()
//
//        outfile.delete()
//        la.header = true
//        la.readMutation(xmlfile1,outfile)
//
//        def lines = new File(outfile.name).readLines()
//        assert lines.size() == 9
//        def header = lines[0].split('\t')
//        def data   = lines[1].split('\t')
//        assertEquals( "Match cols ", header.size(), data.size())
//
//        Map act = [:]
//        header.eachWithIndex { fld, i -> act[(fld)] = data[i]}
//        //log.info( "Cols ${act}" )
//        exp.each
//        {
//            assertEquals( "Match columns: ${it.key}", exp[it.key], act[it.key])
//        }
    }

    void testComment()
    {
//        assert xmlfile1.exists()
//
//        def xmlp = new XmlParser()
//        def mutlist = xmlp.parse(xmlfile1)
//
//        NodeList muts = mutlist.children()
//        assert muts.size() == 8
//        def comment = muts[0].Occurrences[0].Occurrence[0].Comment[0]
//        def commentHTML = comment.text()
//        assert comment.name() == 'Comment'
//        assert commentHTML[0] == '<'
//
//        //  Parse HTML (need to disable downloading DTD via network)
//        //
//        def s = new XmlSlurper()
//        s.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
//        s.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
//
//
//        def h = s.parseText( commentHTML )
//        assert h.body == 'Oncocarta and Truseq '
    }

//    void testReadXML2()
//    {
//        assert xmlfile2.exists()
//
//        LoadAlamut la = new LoadAlamut()
//
//        outfile.delete()
//        la.readMutation( xmlfile2 ,outfile )
//
//        def lines = new File(outfile.name).readLines()
//        assert lines.size() == 4
//        def header = lines[0].split('\t')
//
//        for( i in 1..lines.size()-1)
//        {
//            def data   = (lines[i]+' ').split("\t")
//            log.info( "Line ${i}: [${lines[i]}]" )
//            assertEquals( "Match cols ", header.size(), data.size())
//            Map act = [:]
//            header.eachWithIndex { fld, idx -> act[(fld)] = data[idx]}
//        }
//    }

    void testHg18()
    {
//        if ( ! Mutalyzer.ping()) return // assert false, "Can't connect to mutalyzer.nl"
//        assert xmlfile3.exists()
//
//        LoadAlamut la = new LoadAlamut()
//        outfile.delete()
//        la.readMutation( xmlfile3 ,outfile )
    }

}
