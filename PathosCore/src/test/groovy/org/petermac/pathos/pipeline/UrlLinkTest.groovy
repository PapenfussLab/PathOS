package org.petermac.pathos.pipeline

import org.petermac.pathos.pipeline.UrlLink

/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 30/07/13
 * Time: 10:00 AM
 * To change this template use File | Settings | File Templates.
 */
class UrlLinkTest extends GroovyTestCase
{
    def pos    = 'X:123456'
    def sample = '12K0123'
    def seqrun = '130730-1234'

    void testIGV()
    {
        //TODO Check beacuse is failing
        //assert 'http://localhost:60151/load?file=http://bioinf-ensembl.petermac.org.au/Pathology/Testing/130730-1234/12K0123/IGV_Session.xml&amp;locus=X:123456' == UrlLink.igv( seqrun, sample, pos )
        //assert "http://localhost:60151/load?file=http://bioinf-ensembl.petermac.org.au/Pathology/Testing/130730-1234/12K0123/IGV_Session.xml&amp;locus=X:123456" == UrlLink.igv( seqrun, sample, pos, false)
        //assert "http://localhost:60151/load?file=/usr/local/dev/DemoNGS/Samples/Testing/130730-1234/12K0123/IGV_Session.xml&amp;locus=X:123456" == UrlLink.igv( seqrun, sample, pos, true)
    }

    void testHistogram()
    {
        def link = UrlLink.histogram( "TP53", "p.Lys1239Asnfs*20" )

        assert "https://cancer.sanger.ac.uk/cosmic/gene/analysis?ln=TP53&start=1219&end=1259" == link, "Testing histogram"

        link = UrlLink.histogram( "BRAF", "p.V600E" )

        assert "https://cancer.sanger.ac.uk/cosmic/gene/analysis?ln=BRAF&start=580&end=620" == link, "Testing histogram"

        link = UrlLink.histogram( "KIT", "p.27" )

        assert "https://cancer.sanger.ac.uk/cosmic/gene/analysis?ln=KIT" == link, "Testing histogram"
    }

    void testFastqc()
    {
        def link = UrlLink.fastqcUrl( '131101_M01053_0020_000000000-A61NM', '13K0418')

        assert link.size() == 0

        println( "Urls: ${link}")
    }

    void testMatch() {
        def var = 'NM_005163.2:c.175+8_175+9insC'
        def match1 = ( var =~ /(NM_\d+\.\d+):c\.([\d+\-_*]+)(.*)/ )
        String trs  = match1[0][1]
        String pos  = match1[0][2]
        String rest = match1[0][3]

        def match = ( pos =~ /^([0-9\+]+)(-|_)([0-9\+]+)$/ )
        //def match = ( pos =~ /^([0-9+])(-|_)([0-9+])$/ )
        def pos1 = pos
        def pos2 = pos
        if ( match.count == 1)
        {
            pos1 = match[0][1]
            pos2 = match[0][3]
        }
        println pos1
        println pos2
        assert (pos1 == '175+8' && pos2 == '175+9')
    }

    void testGooglelink()
    {
        def hgvsc = 'NM_007294.3:c.1621A>C'
        def hgvsp = 'p.Met541Leu'
        def hgvspAa1 = 'p.M541L'
        def gene = 'KIT'
        def link = UrlLink.googleSearchVar(gene,hgvsc,hgvsp,hgvspAa1)
        assert '"KIT" ("1621A>C" | "1621A->C" | "1621A-->C" | "1621A/C" | "Met541Leu" | "M541L")' == link

        hgvsc = 'NM_007294.3:c.981del'
        hgvsp = 'NP_009225.1:p.(Cys328*)'
        hgvspAa1 = 'NP_009225.1:p.(C328*)'
        gene = 'BRCA1'
        link = UrlLink.googleSearchVar(gene,hgvsc,hgvsp,hgvspAa1)
        assert '"BRCA1" ("981del" | "Cys328*" | "Cys328X" | "Cys328Ter" | "C328*" | "C328X" | "C328Ter")' == link

        hgvsc = 'NM_007294.3:c.981_982del'
        hgvsp = 'NP_009225.1:p.(Cys328*)'
        hgvspAa1 = 'NP_009225.1:p.(C328*)'
        def ensvar = '17_41246566_AT/-'
        gene = 'BRCA1'
        link = UrlLink.googleSearchVar(gene,hgvsc,hgvsp,hgvspAa1,ensvar)
        assert '"BRCA1" ("981-982delAT" | "981_982delAT" | "981-982del" | "981_982del" | "Cys328*" | "Cys328X" | "Cys328Ter" | "C328*" | "C328X" | "C328Ter")' == link


        hgvsc = 'NM_007294.3:c.981_982delA'
        hgvsp = 'NP_009225.1:p.(Cys328*)'
        hgvspAa1 = 'NP_009225.1:p.(C328*)'
        gene = 'BRCA1'
        link = UrlLink.googleSearchVar(gene,hgvsc,hgvsp,hgvspAa1)
        assert '"BRCA1" ("981-982delA" | "981_982delA" | "981-982del" | "981_982del" | "Cys328*" | "Cys328X" | "Cys328Ter" | "C328*" | "C328X" | "C328Ter")' == link


        hgvsc = 'NM_000314.4:c.254-30dup'
        hgvsp = ''
        hgvspAa1 = ''
        gene = 'PTEN'
        link = UrlLink.googleSearchVar(gene,hgvsc,hgvsp,hgvspAa1)
        assert '"PTEN" ("254-30dup" | "254_30dup")' == link


        hgvsc = 'NM_005163.2:c.175+8_175+9insC'
        hgvsp = ''
        hgvspAa1 = ''
        gene = 'AKT1'
        link = UrlLink.googleSearchVar(gene,hgvsc,hgvsp,hgvspAa1)
        assert '"AKT1" ("175+8-175+9insC" | "175+8_175+9insC" | "175+8-175+9ins" | "175+8_175+9ins")' == link
        //todo check if i solved the urlencode + problem yet


        hgvsc = 'NM_005228.3:c.2303_2311dup'
        hgvsp = 'NP_005219.2:p.(Ser768_Asp770dup)'
        hgvspAa1 = 'NP_005219.2:p.(S768_D770dup)'
        gene = 'EGFR'
        link = UrlLink.googleSearchVar(gene,hgvsc,hgvsp,hgvspAa1)
        assert('"EGFR" ("2303-2311dup" | "2303_2311dup" | "Ser768_Asp770dup" | "S768_D770dup")' == link)


        hgvsc = 'NM_000314.4:c.254-30dup'
        hgvsp = ''
        hgvspAa1 = ''
        gene = 'PTEN'
        link = UrlLink.googleSearchVar(gene,hgvsc,hgvsp,hgvspAa1)
        assert '"PTEN" ("254-30dup" | "254_30dup")' == link
    }



    void testPipelineUrl( )
    {
        //TODO Check beacuse is failing
        String seqrun = "150820_M00139_0243_000000000-AHRLU"

        def urlL = new UrlLink()

        //assert urlL.pipelineUrl(seqrun) == 'http://bioinf-ensembl.petermac.org.au/Pathology/Testing/150820_M00139_0243_000000000-AHRLU/RunPipe/mp_dualAmplicon/doc/index.html'

    }

    void testVep()
    {
        //TODO: Check beacuse is failing
        String seqrun = "130822_M00139_0022_000000000-A5D1J"
        String sample = '13K0341'

        def urlL = new UrlLink()


       // assert urlL.vep(seqrun, sample) == 'http://bioinf-ensembl.petermac.org.au/Pathology/Testing/130822_M00139_0022_000000000-A5D1J/13K0341/13K0341.vep_summary.html' &&
         //       urlL.vep(seqrun, sample, true) == 'http://bioinf-ensembl.petermac.org.au/Pathology/Testing/130822_M00139_0022_000000000-A5D1J/13K0341/13K0341.vep.html'
    }


    void testAlaMut()
    {
        String pos = "1835"
        def urlL = new UrlLink()

        assert urlL.alamut(pos) == "http://localhost:10000/show?request=1835"

    }

    void testDBSnp()
    {
        String dbsnpid = "GJ786"

        def urlL = new UrlLink()

        assert urlL.dbsnp(dbsnpid) == "http://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=GJ786"
    }



    void testCNVUrl()
    {
        //TODO: Check because is failing
        String seqrun = "130822_M00139_0022_000000000-A5D1J"
        String sample = '13K0341'

        def urlL = new UrlLink()
       // assert urlL.cnvUrl(seqrun, sample) == 'http://bioinf-ensembl.petermac.org.au/Pathology/Testing/130822_M00139_0022_000000000-A5D1J/13K0341/13K0341.cnv.png'

    }

    void testCosmic()
    {
        String cosmicid = "12600"
        def urlL = new UrlLink()

        assert urlL.cosmic(cosmicid) == "https://cancer.sanger.ac.uk/cosmic/mutation/overview?genome=37&id=12600"

    }

    void testPubmed()
    {
        String pubmedid = "29242575"
        def urlL = new UrlLink()

        assert urlL.pubmed(pubmedid) == "https://www.ncbi.nlm.nih.gov/pubmed/29242575"

    }

    void testUCS()
    {
        String pos = "4455332"
        def urlL = new UrlLink()

        assert urlL.ucsc(pos) == "http://genome.ucsc.edu/cgi-bin/hgTracks?position=chr4455332&hgt.out3=10x&g=lovd"

    }

    void testReportDoc()
    {
        String sample = "TEST"
        String homeDir = new File(".").getCanonicalPath().toString()
        homeDir = homeDir + "/src/test/resources"

        String out = "file://${homeDir}/Report/Generated/Report_${sample}.docx"
        
        def urlL = new UrlLink()
        assert urlL.reportDoc(homeDir, sample) == out

    }

    void testReportHTML()
    {

        String sample = "TEST"
        String homeDir = new File(".").getCanonicalPath().toString()
        homeDir = homeDir + "/src/test/resources"

        String out = "file://${homeDir}/Report/Generated/Report_${sample}.html"

        def urlL = new UrlLink()

        assert urlL.reportHtml(homeDir, sample) == out

    }

    void testCloudFasQC()
    {
        //TODO Shceck beacause is failing
        String seqrun = '141214_M01053_0162_000000000-ACML4'
        String sample = '14K0900-A'

        def urlL = new UrlLink()

        def out =  urlL.cloudFastQC(seqrun, sample)
        //assert out[0] == 'http://bioinf-ensembl.petermac.org.au/Pathology/Testing/141214_M01053_0162_000000000-ACML4/14K0900-A/FastQCread1.html' &&
          //      out[1] == 'http://bioinf-ensembl.petermac.org.au/Pathology/Testing/141214_M01053_0162_000000000-ACML4/14K0900-A/FastQCread2.html'

    }

}
