package org.petermac.pathos.curate


import grails.persistence.Entity

@Entity
class SeqSampleReport {

    //seqsample
    SeqSample seqSample
    String reportFilePath //need to store path to generated report
    Date dateCreated
    AuthUser user



    static mapping = {
        autoTimestamp true
    }
}
