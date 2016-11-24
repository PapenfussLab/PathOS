package org.petermac.util

/**
 * Created by lara luis on 22/01/2016.
 */
class FastaTest extends GroovyTestCase
{
    /**
     * TESTING  Fasta( File ffile, boolean fastq = false )
     */
    void testFastaConstructor()
    {
        String resource = "GATK"
        String file = "15K4895_S6_L001_R1_001"
        String extension = "fastq.gz"

        def ffile = new File(FastaTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())

        boolean fastq = false
        def fasta = new Fasta(ffile, fastq)
        assert fasta instanceof Fasta :"[T E S T]: Cannot create instance Fasta()"
    }

    /**
     * TESTING  Fasta( File ffile, boolean fastq = false )
     * in a different setting
     */
    void testFastaFastq()
    {
        String resource = "GATK"
        String file = "15K4895_S6_L001_R1_001"
        String extension = "fastq.gz"

        def ffile = new File(FastaTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())

        boolean fastq = true
        def fasta = new Fasta(ffile, fastq)
        assert fasta instanceof Fasta :"[T E S T]: Cannot create instance Fasta()"
    }

    /**
     * TESTING List<Map> load()
     */
    void testLoad()
    {
        String resource = "GATK"
        String file = "dummy"
        String extension = "fasta"

        def ffile = new File(FastaTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        boolean fastq = false
        def fasta = new Fasta(ffile, fastq)


        assert fasta.load().size() == 1 : "[T E S T]: Size is not ONE ${fasta.load().size()}"

    }

    /**
     * TESTING List<Map> load()
     * in a different setting
     */
    void testLoadFq()
    {
        String resource = "GATK"
        String file = "15K4895_S6_L001_R1_001"
        String extension = "fastq.gz"

        def ffile = new File(FastaTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())

        boolean fastq = true
        def fasta = new Fasta(ffile, fastq)
        assert fasta.load() == [] : "[T E S T]: It is not an empty "

    }

    /**
     * TESTING Integer readLength()
     */
    void testLoadFasta()
    {
        String resource = "GATK"
        String file = "dummy"
        String extension = "fasta"

        def ffile = new File(FastaTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        boolean fastq = false
        def fasta = new Fasta(ffile, fastq)
        assert fasta.loadFasta().size() == 1 : "[T E S T]: Size is different form 1"
    }

    /**
     * TESTING Integer readLength()
     * in a different setting
     */
    void testReadLength()
    {
        String resource = "GATK"
        String file = "15K4895_S6_L001_R1_001"
        String extension = "fastq.gz"

        def ffile = new File(FastaTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())

        boolean fastq = true
        def fasta = new Fasta(ffile, fastq)

        assert fasta.readLength() == 154 : "[T E S T]: The length is different form 154"
    }

    /**
     * TESTING Map readStats()
     */
    void testreadStats()
    {
        String resource = "GATK"
        String file = "15K4895_S6_L001_R1_001"
        String extension = "fastq.gz"

        def ffile = new File(FastaTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())

        boolean fastq = true
        def fasta = new Fasta(ffile, fastq)

        def keys = fasta.readStats().keySet()  as String[]

        assert keys == ['readSize', 'readLen', 'fileSize'] :"[T E S T]: keys are different from ['readSize', 'readLen', 'fileSize']"
    }

}
