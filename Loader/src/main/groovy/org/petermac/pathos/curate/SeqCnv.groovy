package org.petermac.pathos.curate


import grails.persistence.Entity

@Entity
class SeqCnv {

    SeqSample seqSample
    String gene
    String cnv_type
    String chr
    int startpos
    int endpos
    BigDecimal lr_mean
    BigDecimal lr_median
    BigDecimal lr_sd
    String gainloss
    BigDecimal pval
    int n
    BigDecimal probes_pct
    BigDecimal pval_adj

    static constraints =
            {
                gainloss(nullable: true)
                probes_pct(nullable: true)
                lr_sd(nullable: true)
                lr_median(nullable: true)
                lr_mean(nullable: true)
                seqSample(nullable: true)
                pval_adj (scale: 8)
                pval (scale: 8)
                lr_mean (scale: 8)
                lr_median (scale: 8)
                lr_sd (scale: 8)
                probes_pct (scale: 8)
            }
}
