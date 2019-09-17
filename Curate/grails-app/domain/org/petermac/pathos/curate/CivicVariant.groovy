package org.petermac.pathos.curate



import grails.persistence.Entity

@Entity
class CivicVariant
{
    String          variant_civic_url
    String          gene
    String          entrez
    String          variant
    String          summary
    String          variant_groups
    String          chromosome
    Integer         start
    Integer         stop
    String          reference_bases
    String          variant_bases
    String          representative_transcript
    String          ensembl_version
    String          reference_build
    String          chromosome2
    Integer         start2
    Integer         stop2
    String          representative_transcript2
    String          variant_types
    String          hgvs_expressions
    Date            last_review_date
    Float           civic_actionability_score


    static mapping =
    {
        summary                         ( type: 'text' )
    }

    static constraints =
    {
         variant_civic_url              (nullable: false)
         gene                           (nullable: false)
         entrez                         (nullable: true)
         variant                        (nullable: true)
         summary                        (nullable: true)
         variant_groups                 (nullable: true)
         chromosome                     (nullable: true)
         start                          (nullable: true)
         stop                           (nullable: true)
         reference_bases                (nullable: true)
         variant_bases                  (nullable: true)
         representative_transcript      (nullable: true)
         ensembl_version                (nullable: true)
         reference_build                (nullable: true)
         chromosome2                    (nullable: true)
         start2                         (nullable: true)
         stop2                          (nullable: true)
         representative_transcript2     (nullable: true)
         variant_types                  (nullable: true)
         hgvs_expressions               (nullable: true)
         last_review_date               (nullable: true)
         civic_actionability_score      (nullable: true)
    }

    String toString()
    {
        gene
    }


}
