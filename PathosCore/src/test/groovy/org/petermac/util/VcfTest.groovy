package org.petermac.util
import org.codehaus.groovy.reflection.ReflectionUtils
/**
 * Created for PathOS.
 *
 * Description:
 *
 *
 *
 * User: doig ken
 * Date: 10/07/2014
 * Time: 4:01 PM
 */
class VcfTest extends GroovyTestCase
{

    void testFileLoad()
    {
        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension ="vcf"

        def vcf = new Vcf(PathGeneratorFile( resource,  file, extension ))
        def nlines  = vcf.load()
        assert nlines == 52
        Map tm = vcf.getTsvMap()

        assert tm.preamble.size() == 111
        assert tm.cols.size() == 10
        assert tm.rows.size() == nlines


    }

    void testFileLoadStr()
    {
        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension ="vcf"

        String path = PathGeneratorStr( resource,  file, extension )

        def vcf = new Vcf("file:${path}")

        def nlines  = vcf.load()


        assert nlines == 52
        Map tm = vcf.getTsvMap()



        assert tm.preamble.size() == 111
        assert tm.cols.size() == 10
        assert tm.rows.size() == nlines


    }

    void testSort()
    {
        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension ="vcf"

        def vcf = new Vcf(PathGeneratorFile( resource,  file, extension ))
        vcf.load()
        vcf.sort()
    }

    void testCloneConsturctor()
    {
        String resource = "Vcf/Examples"
        String file = "tumour"
        String extension ="vcf"

        def vcf = new Vcf(PathGeneratorFile( resource,  file, extension ))
        vcf.load()
        def cvcf = new Vcf(vcf)
    }

    void testHasMultiAllele()
    {
        String resource = "Vcf/NormaliseVcf"
        String file = "multiallele"
        String extension ="vcf"

        def vcf = new Vcf(PathGeneratorFile( resource,  file, extension ))
        vcf.load()

        assert vcf.hasMultiAllele()
    }

    void testMultiallelesplit()
    {
        String resource = "Vcf/NormaliseVcf"
        String file = "multiallele"
        String extension ="vcf"

        def vcf = new Vcf(PathGeneratorFile( resource,  file, extension ))
        vcf.load()
        assert vcf.splitAlleles() != null
    }

    void testHeader()
    {
        String sample = 'Sample'
        String s = "##fileformat=VCFv4.1\n" +
                "##source=VcfTest\n" +
                "##INFO=<ID=ADP,Number=1,Type=Integer,Description=\"Average per-sample depth of bases with Phred score >= 20\">\n" +
                "##INFO=<ID=WT,Number=1,Type=Integer,Description=\"Number of samples called reference (wild-type)\">\n" +
                "##INFO=<ID=HET,Number=1,Type=Integer,Description=\"Number of samples called heterozygous-variant\">\n" +
                "##INFO=<ID=HOM,Number=1,Type=Integer,Description=\"Number of samples called homozygous-variant\">\n" +
                "##INFO=<ID=NC,Number=1,Type=Integer,Description=\"Number of samples not called\">\n" +
                "##INFO=<ID=HGVSg,Number=1,Type=String,Description=\"HGVSg format of variant\">\n" +
                "##INFO=<ID=numAmps,Number=1,Type=String,Description=\"Number of amplicons with variant / amplicons including locus\">\n" +
                "##INFO=<ID=amps,Number=1,Type=String,Description=\"Amplicon names with variant\">\n" +
                "##INFO=<ID=gene,Number=1,Type=String,Description=\"gene of variant\">\n" +
                "##INFO=<ID=ampbias,Number=1,Type=String,Description=\"is there a bias across amplicons for variant\">\n" +
                "##INFO=<ID=fsRescue,Number=1,Type=String,Description=\"Can variant be rescued from a frameshift in phase\">\n" +
                "##INFO=<ID=homopolymer,Number=1,Type=String,Description=\"Is variant next to a homopolymer run\">\n" +
                "##FILTER=<ID=str10,Description=\"Less than 10% or more than 90% of variant supporting reads on one strand\">\n" +
                "##FILTER=<ID=indelError,Description=\"Likely artifact due to indel reads at this position\">\n" +
                "##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Genotype\">\n" +
                "##FORMAT=<ID=GQ,Number=1,Type=Integer,Description=\"Genotype Quality\">\n" +
                "##FORMAT=<ID=SDP,Number=1,Type=Integer,Description=\"Raw Read Depth as reported by SAMtools\">\n" +
                "##FORMAT=<ID=DP,Number=1,Type=Integer,Description=\"Quality Read Depth of bases with Phred score >= 20\">\n" +
                "##FORMAT=<ID=RD,Number=1,Type=Integer,Description=\"Depth of reference-supporting bases (reads1)\">\n" +
                "##FORMAT=<ID=AD,Number=1,Type=Integer,Description=\"Depth of variant-supporting bases (reads2)\">\n" +
                "##FORMAT=<ID=FREQ,Number=1,Type=String,Description=\"Variant allele frequency\">\n" +
                "##FORMAT=<ID=PVAL,Number=1,Type=String,Description=\"P-value from Fisher's Exact Test\">\n" +
                "##FORMAT=<ID=RBQ,Number=1,Type=Integer,Description=\"Average quality of reference-supporting bases (qual1)\">\n" +
                "##FORMAT=<ID=ABQ,Number=1,Type=Integer,Description=\"Average quality of variant-supporting bases (qual2)\">\n" +
                "##FORMAT=<ID=RDF,Number=1,Type=Integer,Description=\"Depth of reference-supporting bases on forward strand (reads1plus)\">\n" +
                "##FORMAT=<ID=RDR,Number=1,Type=Integer,Description=\"Depth of reference-supporting bases on reverse strand (reads1minus)\">\n" +
                "##FORMAT=<ID=ADF,Number=1,Type=Integer,Description=\"Depth of variant-supporting bases on forward strand (reads2plus)\">\n" +
                "##FORMAT=<ID=ADR,Number=1,Type=Integer,Description=\"Depth of variant-supporting bases on reverse strand (reads2minus)\">\n" +
                "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\t${sample}\n"



        def vcf = new Vcf()
        assert vcf.header( 'VcfTest', []) == s

        s = "##fileformat=VCFv4.1\n" +
                "##source=VcfTest\n" +
                "##INFO=<ID=ADP,Number=1,Type=Integer,Description=\"Average per-sample depth of bases with Phred score >= 20\">\n" +
                "##INFO=<ID=WT,Number=1,Type=Integer,Description=\"Number of samples called reference (wild-type)\">\n" +
                "##INFO=<ID=HET,Number=1,Type=Integer,Description=\"Number of samples called heterozygous-variant\">\n" +
                "##INFO=<ID=HOM,Number=1,Type=Integer,Description=\"Number of samples called homozygous-variant\">\n" +
                "##INFO=<ID=NC,Number=1,Type=Integer,Description=\"Number of samples not called\">\n" +
                "##INFO=<ID=HGVSg,Number=1,Type=String,Description=\"HGVSg format of variant\">\n" +
                "##INFO=<ID=numAmps,Number=1,Type=String,Description=\"Number of amplicons with variant / amplicons including locus\">\n" +
                "##INFO=<ID=amps,Number=1,Type=String,Description=\"Amplicon names with variant\">\n" +
                "##INFO=<ID=gene,Number=1,Type=String,Description=\"gene of variant\">\n" +
                "##INFO=<ID=ampbias,Number=1,Type=String,Description=\"is there a bias across amplicons for variant\">\n" +
                "##INFO=<ID=fsRescue,Number=1,Type=String,Description=\"Can variant be rescued from a frameshift in phase\">\n" +
                "##INFO=<ID=homopolymer,Number=1,Type=String,Description=\"Is variant next to a homopolymer run\">\n" +
                "##INFO=<ID=dummy,Number=1,Type=String,Description=\"dummy INFO field\">\n" +
                "##FILTER=<ID=str10,Description=\"Less than 10% or more than 90% of variant supporting reads on one strand\">\n" +
                "##FILTER=<ID=indelError,Description=\"Likely artifact due to indel reads at this position\">\n" +
                "##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Genotype\">\n" +
                "##FORMAT=<ID=GQ,Number=1,Type=Integer,Description=\"Genotype Quality\">\n" +
                "##FORMAT=<ID=SDP,Number=1,Type=Integer,Description=\"Raw Read Depth as reported by SAMtools\">\n" +
                "##FORMAT=<ID=DP,Number=1,Type=Integer,Description=\"Quality Read Depth of bases with Phred score >= 20\">\n" +
                "##FORMAT=<ID=RD,Number=1,Type=Integer,Description=\"Depth of reference-supporting bases (reads1)\">\n" +
                "##FORMAT=<ID=AD,Number=1,Type=Integer,Description=\"Depth of variant-supporting bases (reads2)\">\n" +
                "##FORMAT=<ID=FREQ,Number=1,Type=String,Description=\"Variant allele frequency\">\n" +
                "##FORMAT=<ID=PVAL,Number=1,Type=String,Description=\"P-value from Fisher's Exact Test\">\n" +
                "##FORMAT=<ID=RBQ,Number=1,Type=Integer,Description=\"Average quality of reference-supporting bases (qual1)\">\n" +
                "##FORMAT=<ID=ABQ,Number=1,Type=Integer,Description=\"Average quality of variant-supporting bases (qual2)\">\n" +
                "##FORMAT=<ID=RDF,Number=1,Type=Integer,Description=\"Depth of reference-supporting bases on forward strand (reads1plus)\">\n" +
                "##FORMAT=<ID=RDR,Number=1,Type=Integer,Description=\"Depth of reference-supporting bases on reverse strand (reads1minus)\">\n" +
                "##FORMAT=<ID=ADF,Number=1,Type=Integer,Description=\"Depth of variant-supporting bases on forward strand (reads2plus)\">\n" +
                "##FORMAT=<ID=ADR,Number=1,Type=Integer,Description=\"Depth of variant-supporting bases on reverse strand (reads2minus)\">\n" +
                "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tTEST\n"

        assert vcf.header( 'VcfTest', ['dummy'], 'TEST') == s


    }

    void testMinus()
    {
        //  Expected VCF
        //
        String resource = "Vcf/Examples"
        String file = "expected"
        String extension ="vcf"

        Vcf exp = new Vcf( PathGeneratorFile( resource,  file, extension ) )
        assert exp.load() == 38

        //  Actual VCF
        //
        file = "actual"
        File basePath =  PathGeneratorFile( resource,  file, extension )
        Vcf act = new Vcf( basePath )
        assert act.load() == 18



        //  Difference
        //
        Vcf min = exp.minus( act )
        assert min

        def srcPath =  basePath.getParent()
        file = "minus"

        assert min.write( "${srcPath}/${file}.${extension}" )


        File tf = PathGeneratorFile( resource,  file, extension )
        def lines = tf.readLines()
        assert lines.size() == 44

        //  Now the other way
        //
        min = act.minus( exp )
        assert min.nrows() == 0
    }

    void testIntersect()
    {
        //  Expected VCF
        //

        String resource = "Vcf/Examples"
        String file = "expected"
        String extension ="vcf"

        Vcf exp = new Vcf(PathGeneratorFile( resource,  file, extension ))
        assert exp.load() == 38

        //  Actual VCF
        //
        file = "actual"
        File basePath = PathGeneratorFile( resource,  file, extension )

        Vcf act = new Vcf( basePath )
        assert act.load() == 18


        //  Difference
        //
        def ins = exp.intersect( act )
        assert ins

        file = "intersect"
        def srcPath =  basePath.getParent()

        assert ins.write( "${srcPath}/${file}.${extension}" )

        File tf = PathGeneratorFile( resource,  file, extension )

        def lines = tf.readLines()
        assert lines.size() == 42
    }

    void testUnpack()
    {
        //  Actual VCF
        //
        String resource = "Vcf/Examples"
        String file = "actual"
        String extension ="vcf"

        File basePath = PathGeneratorFile( resource,  file, extension )

        Vcf act = new Vcf(basePath)

        assert act.load()  == 18
        assert act.nrows() == 18

        Tsv unp = act.unpack()
        assert unp.nrows() == 18

        Map tm = unp.tsvMap
        List cols = unp.cols
        assert cols == ['CHROM', 'POS', 'ID', 'REF', 'ALT', 'QUAL', 'FILTER', 'GT', 'GQ', 'SDP', 'DP', 'RD',
                'AD', 'FREQ', 'PVAL', 'RBQ', 'ABQ', 'RDF', 'RDR', 'ADF', 'ADR', 'ADP', 'WT', 'HET', 'HOM', 'NC' ]

        assert cols.size() == tm.rows[0].size()

        //  Check we haven't changed source tsvMap
        //
        cols = act.cols
        assert cols == ['CHROM', 'POS', 'ID', 'REF', 'ALT', 'QUAL', 'FILTER', 'INFO', 'FORMAT', 'AR001' ]

        file = "unpacked"
        extension ="tsv"
        def srcPath =  basePath.getParent()
        unp.write( "${srcPath}/${file}.${extension}" )

        File tf = PathGeneratorFile( resource,  file, extension )
        def lines = tf.readLines()
        assert lines.size() == 43

    }

    void testRowMap()
    {
        String resource = "Vcf/Examples"
        String file = "actual"
        String extension ="vcf"
        File basePath = PathGeneratorFile( resource,  file, extension )

        //  Actual VCF
        //
        def vcf = new Vcf( basePath )
        assert vcf.load()  == 18
        assert vcf.nrows() == 18

        List<Map> orig = vcf.getRowMaps()

        //  Modify the various types of fields in a VCF
        //
        List<Map> rows = []
        for ( row in vcf.getRowMaps() )
        {
            row.CHROM = 'X'             //  Key field
            row.GT    = '123/4567'      //  Format field
            row.NC    = '789'           //  Info field
            rows << row
        }

        //  Save the mods to disk
        //
        vcf.setRowMaps( rows )
        assert vcf.validate()

        file = "rowmap"
        def srcPath =  basePath.getParent()
        vcf.write( "${srcPath}/${file}.${extension}" )

        //  Load it back and compare with original
        //


        def mod = new Vcf(PathGeneratorFile( resource,  file, extension ))
        assert mod.load()  == 18

        def modrows = mod.getRowMaps()
        modrows.eachWithIndex
        {
            Map entry, int i ->
                assert orig[i].CHROM != entry.CHROM
                assert entry.CHROM == 'X'
                assert orig[i].GT != entry.GT
                assert entry.GT == '123/4567'
                assert orig[i].NC != entry.NC
                assert entry.NC == '789'
                assert orig[i].POS == entry.POS
        }
    }

    void testMRCI()
    {
        String resource = "Vcf/Examples"
        String file = "MUW09_S4_L001_R1_001"
        String extension = "vcf"

        File basePath = PathGeneratorFile( resource,  file, extension )
        def mcri = new Vcf(basePath)

        mcri.load()
        assert mcri.nrows() == 170

        Tsv unp = mcri.unpack()
        assert unp.nrows() == 170

        def srcPath =  basePath.getParent()
        file = "unpacked_mcri"
        extension="tsv"
        unp.write( "${srcPath}/${file}.${extension}" )

    }

//    void testExome()
//    {
//        def ex = new Vcf( new File('KM1-R.vcf'))
//        ex.load()
//        assert ex.nrows() == 105614
//
//        Tsv unp = ex.unpack()
//        assert unp.nrows() == 105614
//
//        unp.write( 'unpacked_exome.tsv' )
//    }
//
//    void testProstate()
//    {
//        def ex = new Vcf( new File('Bravo1_Biopsy.vcf'))
//        ex.load()
//        assert ex.nrows() == 9931
//
//        Tsv unp = ex.unpack()
//        assert unp.nrows() == 9931
//
//        unp.write( 'unpacked_prostate.tsv' )
//    }

    void testAddColumn()
    {
        String resource = "Vcf/Examples"
        String file = "actual"
        String extension ="vcf"

        File basePath = PathGeneratorFile( resource,  file, extension )
        Vcf act = new Vcf(basePath)

        int nrows = act.load()
        assert nrows == 18

        List col = []

        for ( i in [0..nrows-1]) { col[i] = i }
        Map newInf = [ name: 'NEWI', cat: 'INFO',   type: 'Integer', description: 'A new test column']
        act.addColumn( newInf, col )
        Map newFmt = [ name: 'NEWF', cat: 'FORMAT', type: 'Integer', description: 'A new test column']
        act.addColumn( newFmt, col )

        def names = act.getMetaCols()['name']
        assert names.contains('NEWI')
        assert names.contains('NEWF')

        assert act.validate()


        Tsv added = act.unpack()

        def srcPath =  basePath.getParent()
        file = "added"
        extension="tsv"
        assert added.write("${srcPath}/${file}.${extension}")

        extension="vcf"
        Tsv tsv = new Tsv(PathGeneratorFile( resource,  file, extension ))
        tsv.load(true)

        assert tsv.getRowMaps()['INFO'][0].replaceAll('=[0-9]+',"").split(';').contains('NEWI')
        assert tsv.getRowMaps()['FORMAT'][0].split(':').contains('NEWF')
    }

    void testAddExistingColumn()
    {
        String resource = "Vcf/Examples"
        String file = "actual"
        String extension="vcf"

        File basePath = PathGeneratorFile( resource,  file, extension )
        Vcf act = new Vcf(basePath)

        int nrows = act.load()
        assert nrows == 18

        //  Populate the existing HET column in the INFO field and GT in the FORMAT field
        //
        List c = []
        for ( i in [0..nrows-1]) { c[i] = i }
        Map exInf = [ name: 'HET', cat: 'INFO',   type: 'Integer', description: 'An existing test column']
        Map exFmt = [ name: 'GT',  cat: 'FORMAT', type: 'String',  description: 'An existing test column']
        act.addColumn( exInf, c )
        act.addColumn( exFmt, c )

        //  Get and print meta cols
        //
        List cols = act.getMetaCols()
        for ( col in cols )
            println col

        //  Check by dumping out TSV file
        //
        assert act.validate()

        Tsv addtsv = act.unpack()

        file = "added"
        extension="tsv"
        def srcPath =  basePath.getParent()
        assert addtsv.write("/${srcPath}/${file}.${extension}")

        Tsv tsv = new Tsv(PathGeneratorFile( resource,  file, extension ))
        tsv.load(true)
        cols = tsv.cols
        assert cols.count{ it == 'HET'} == 1
        assert cols.count{ it == 'GT' } == 1

        //  Check all rows
        //
        List<Map> rows = tsv.getRowMaps()
        int i=0
        for ( row in rows )
        {
            assert (row.HET as int) == i
            assert (row.GT  as int) == i++
        }
    }

    void testDuplicateHeader()
    {

        String resource = "Vcf/Examples"
        String file = "dupDPinHeader"
        String extension="vcf"
        File basePath = PathGeneratorFile( resource,  file, extension )

        //  read in the test VCF
        //

        Vcf vcf = new Vcf(basePath)
        vcf.load()
        assert vcf.nrows() == 3

        //  Check all rows
        //
        List<Map> rows = vcf.getRowMaps()
        List<List> rowl = vcf.getRows()
        int i=0
        for ( row in rows )
        {
            List flds = rowl[i]
            List fmtHead = flds[8].tokenize(':')
            List fmtFlds = flds[9].tokenize(':')
            println ( "${fmtHead[3]}=${fmtFlds[3]}")

            //  verify that we have got DP back for FORMAT filed
            //
            assert row.DP == fmtFlds[3]

            //  Check info fields
            //
            assert row.INFO_DP == '123'
            ++i
        }
        file = "newDup"
        def srcPath =  basePath.getParent()
        vcf.write("/${srcPath}/${file}.${extension}")

    }

    String PathGeneratorStr(String resource, String file,String extension )
    {
        File basePath = new File(TsvTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        if ( !basePath.exists() )
            throw new RuntimeException('ERROR in [FILE]:' + basePath + ' does not exist !')

        String p = basePath
        return p
    }

    File PathGeneratorFile(String resource, String file,String extension )
    {
        File basePath = new File(TsvTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        if ( !basePath.exists() )
            throw new RuntimeException('ERROR in [FILE]:' + basePath + ' does not exist !')


        return basePath
    }
}
