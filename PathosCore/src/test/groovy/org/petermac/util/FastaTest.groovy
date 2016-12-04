package org.petermac.util

/**
 * Created by lara luis on 22/01/2016.
 */
class FastaTest extends GroovyTestCase
{
    void testFastaConstructor()
    {
        String resource = "GATK"
        String file = "15K4895_S6_L001_R1_001"
        String extension = "fastq.gz"

        def ffile = new File(FastaTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())

        boolean fastq = false
        def fasta = new Fasta(ffile, fastq)
    }

    void testFastaFastq()
    {
        String resource = "GATK"
        String file = "15K4895_S6_L001_R1_001"
        String extension = "fastq.gz"

        def ffile = new File(FastaTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())

        boolean fastq = true
        def fasta = new Fasta(ffile, fastq)
    }


    void testLoad()
    {
        String resource = "GATK"
        String file = "dummy"
        String extension = "fasta"

        def ffile = new File(FastaTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        boolean fastq = false
        def fasta = new Fasta(ffile, fastq)

        assert fasta.load().size() == 1

    }

    void testLoadFq()
    {
        String resource = "GATK"
        String file = "15K4895_S6_L001_R1_001"
        String extension = "fastq.gz"

        def ffile = new File(FastaTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())

        boolean fastq = true
        def fasta = new Fasta(ffile, fastq)
        assert fasta.load() == []

    }

    void testLoadFasta()
    {
        String resource = "GATK"
        String file = "dummy"
        String extension = "fasta"

        def ffile = new File(FastaTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())
        boolean fastq = false
        def fasta = new Fasta(ffile, fastq)
        assert fasta.loadFasta().size() == 1
    }

    void testReadLength()
    {
        String resource = "GATK"
        String file = "15K4895_S6_L001_R1_001"
        String extension = "fastq.gz"

        def ffile = new File(FastaTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())

        boolean fastq = true
        def fasta = new Fasta(ffile, fastq)

        assert fasta.readLength() == 154
    }

    void testreadStats()
    {
        String resource = "GATK"
        String file = "15K4895_S6_L001_R1_001"
        String extension = "fastq.gz"

        def ffile = new File(FastaTest.getClass().getResource( "/${resource}/${file}.${extension}" ).getPath())

        boolean fastq = true
        def fasta = new Fasta(ffile, fastq)

        def keys = fasta.readStats().keySet()  as String[]

        assert keys == ['readSize', 'readLen', 'fileSize']
    }

}
