package org.petermac.pathos.curate


import grails.persistence.Entity
import org.petermac.pathos.pipeline.Locus

/**
 *  Class to encapsulate curated variant keys, currently it's typically HGVSg
 */
@Entity
class GrpVariant
{
    String      accession           //  specific key to variant in SeqVariant/SeqCnv/SeqSv eg HGVSx including refseq -- for now this is HGVSg
    String      muttyp = 'SNV'      //  mutation type
    String      description         //  Optional description of variant

    static constraints =
    {
        accession( nullable: false  )
        description( nullable:true )


        muttyp(  inList:    [
                            "SNV",
                            "CNV",
                            "SV"
                            ], blank: false )
    }

    static mapping =
    {
        autoTimestamp true
        //accession     index: 'comb_index', unique: true
        //accession     indexColumn: [name:'grp_variant_accession', index:'comb_index', unique: true ]
    }


    String	toString()
    {
        "${muttyp}: ${accession}"
    }

}
