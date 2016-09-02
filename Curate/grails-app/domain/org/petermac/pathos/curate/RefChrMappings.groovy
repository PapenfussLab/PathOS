package org.petermac.pathos.curate


import grails.persistence.Entity

@Entity
class RefChrMappings {
    String chr
    String chr_genbank
    String chr_refseq
    static constraints =
            {
                chr( unique: true)
            }
}
