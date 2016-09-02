package org.petermac.pathos.curate


import grails.persistence.Entity

@Entity
class VcfUpload {
    //VcfUpload thisUpload = new VcfUpload(user: currentUser, filePath: outFile.getPath(),queue: queue, sampleName: sampleName, panel: thisPanel).save(flush:true,failOnError:true)

    AuthUser user
    String filePath
    String env
    Seqrun seqrun
    SeqSample seqSample
    Date dateCreated

    static constraints =
            {

                filePath nullable: true
                seqSample nullable: true
                env nullable: true

            }

    static mapping = {
        autoTimestamp true
    }

}

