package org.petermac.pathos.curate


import grails.persistence.Entity

@Entity
class SeqCnv implements Taggable {
    /**
     * The sample this CNV belongs to
     */
    SeqSample seqSample

    /**
     * The chromosome for the CNV
     */
    String chr

    /**
     * The start of the CNV locus (1-based, inclusive)
     */
    Integer start

    /**
     * The end of the CNV locus (1-based, inclusive)
     */
    Integer end

    /**
     * The resolution of the SNV region.
     *
     * Resolution applies in the following order,
     * from low resolution (wide) to high resolution (narrow):
     *  chr     whole chromosome
     *  arm     chromosome arm (chrp|chrq)
     *  gene    whole gene
     *  exon    single exon
     */
    String resolution

    /**
     * The arm of the locus, if the resolution is arm or greater.
     */
    String arm

    /**
     * If the resolution is gene or exon, then the gene of the CNV
     */
    String gene

    /**
     * If the resolution is exon, then the gene of the CNV
     */
    String exon

    /**
     * If the resolution is gene or exon, then the transcript of the gene
     * to use for interpreting the exon.
     */
    String transcript

    /**
     * Inferred copy number.
     */
    Double copyNumber

    /**
     * The standard deviation on the estimated copy number.
     */
    Double copyNumberStdDev

    /**
     * The number of standard deviations of the copy number estimate
     * from the population mean.
     */
    Double zScore

    static hasMany = [ tags: Tag ]

    static constraints = {
        // All the constraints on the locus:
        //
        seqSample(nullable: false)
        chr(nullable: false, blank: false, unique: ['start', 'end', 'resolution', 'seqSample'])
        start(nullable: false, min: 1)
        end(nullable: false, min: 1)
        resolution(inList: ['chr', 'arm', 'gene', 'exon'], nullable: false)
        arm(nullable: true, inList: ['chrp', 'chrq'])
        gene(nullable: true)
        exon(nullable: true)
        transcript(nullable: true)

        // All the constraints on the CNV data
        copyNumber(nullable: true)
        copyNumberStdDev(nullable: true)
        zScore(nullable: true)
    }

    String toString() {
        return "${seqSample.seqrun.seqrun}:${seqSample.sampleName} ${chr}:${start}-${end} ${copyNumber}"
    }
}
