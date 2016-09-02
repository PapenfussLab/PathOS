package org.petermac.pathos.curate


import grails.persistence.Entity
import org.petermac.pathos.pipeline.Locus

@Entity
class GrpVariant {

    String      muttyp = 'SNV'      //  mutation type
    String      accession           //  specific key to variant in SeqVariant/SeqCnv/SeqSv eg hgvsX including refseq -- for now this is HGVSG
    AuthUser    createdBy
    Date        dateCreated = new Date()
    Date        lastUpdated = new Date()


    static constraints =
            {
                muttyp(  inList:    [
                        "SNV",
                        "CNV",
                        "SV",
                ], blank: false )
            }


    static mapping = {
        autoTimestamp true
    }

}
