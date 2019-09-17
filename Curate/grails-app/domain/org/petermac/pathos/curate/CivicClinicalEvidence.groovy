package org.petermac.pathos.curate



import grails.persistence.Entity

@Entity
class CivicClinicalEvidence
{
    String          gene
    String          entrez
    String          name
    String          disease
    String          doid
    String          phenotypes
    String          drugs
    String          evidence_type
    String          evidence_direction
    String          evidence_level
    String          clinical_significance
    String          evidence_statement
    String          pmid
    String          citation
    String          nctIds
    Integer         rating
    String          evidence_status
//    String          evidence_id This is the ID of this object...
    CivicVariant    civicVariant //This is the id of.... the variant..?
    String          civicGene  // not used.... yet...
    String          chromosome
    String          start
    String          stop
    String          reference_bases
    String          variant_bases
    String          representative_transcript
    String          chromosome2
    String          start2
    String          stop2
    String          representative_transcript2
    String          ensembl_version
    String          reference_build
    String          variant_summary
    String          variant_origin
    String          last_review_date
    String          evidence_civic_url
    String          variant_civic_url
    String          gene_civic_url



    static mapping =
    {
        evidence_statement      ( type: 'text' )
        variant_summary         ( type: 'text' )
    }


//    Sorry for the mess...
    static constraints =
    {
        gene                            (nullable: true)
        entrez                      (nullable: true)
        name                      (nullable: true)
        disease                       (nullable: true)
        doid                          (nullable: true)
        phenotypes                     (nullable: true)
        drugs                          (nullable: true)
        evidence_type                   (nullable: true)
        evidence_direction              (nullable: true)
        evidence_level                (nullable: true)
        clinical_significance         (nullable: true)
        evidence_statement              (nullable: true)
        pmid                           (nullable: true)
        citation                           (nullable: true)
        nctIds                          (nullable: true)
        rating                          (nullable: true)
        evidence_status                 (nullable: true)
//        evidence_id                  (nullable: true)
        civicVariant     (nullable: true)
        civicGene                        (nullable: true)
        chromosome                        (nullable: true)
        start                           (nullable: true)
        stop                                (nullable: true)
        reference_bases                    (nullable: true)
        variant_bases                    (nullable: true)
        representative_transcript       (nullable: true)
        chromosome2                    (nullable: true)
        start2                         (nullable: true)
        stop2                          (nullable: true)
        representative_transcript2       (nullable: true)
        ensembl_version                (nullable: true)
        reference_build                (nullable: true)
        variant_summary                (nullable: true)
        variant_origin                    (nullable: true)
        last_review_date                (nullable: true)
        evidence_civic_url              (nullable: true)
        variant_civic_url                  (nullable: true)
        gene_civic_url                   (nullable: true)

    }

    String toString()
    {
        citation
    }
}
