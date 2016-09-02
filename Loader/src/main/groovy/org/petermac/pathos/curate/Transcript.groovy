package org.petermac.pathos.curate


import grails.persistence.Entity

@Entity
class Transcript {
    String genbuild	        //GRCh37
    String build	        //hg19
    String chromosome	    //chr17
    String chr_refseq       //NC_000017.10
    String accession	    //NM_007294
    String refseq	        //NM_007294.2
    String gene	            //BRCA1
    String strand	        //reverse
    int ts_size	            //81154
    int cds_size	        //78418
    String source	        //ncbi
    int exSize	            //8127
    int exCount	            //23
    int ts_start	        //41196314
    int ts_stop	            //41277468
    int cds_start	        //41197695
    int cds_stop	        //41276113
    Long exon_starts	    //9223372036854775807
    Long exon_stops	        //9223372036854775807
    Boolean preferred	    //1
    String lrg	            //LRG_292

    static constraints =
    {
        lrg( nullable: true)
    }

    static      mapping =
            {
                gene   index: 'gene_idx'
                build     index: 'build_idx'
                chr_refseq  index: 'chr_refseq_idx'
                accession     index: 'accession_idx'
            }
}
