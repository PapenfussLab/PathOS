/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class SeqVariant implements Taggable
{
    String		variant
    String		ens_variant
    String		gene
    String		consequence
    String	    hgvsc
    String	    hgvsp
    String	    hgvspAa1
    String	    hgvsg
    SeqSample	seqSample
    String      sampleName      // convenience property of seqSample.toString()
    Integer		readDepth
    Integer		varDepth
    Float		varFreq
    Integer		fwdReadDepth
    Integer		fwdVarDepth
    Integer		revReadDepth
    Integer		revVarDepth
    CurVariant	curated
    String      cosmic
    String      dbsnp
    String      chr
    String      pos
    String      exon
    String	    alamutClass
    Boolean     filtered
    String      filterFlag
    Boolean     reportable
    Double      varPanelPct     // populated by Filtering
    Double      gmaf
    String		ens_transcript
    String		ens_gene
    String		ens_protein
    String		ens_canonical
    String		refseq_mrna
    String		refseq_peptide
    String		existing_variation
    String		domains
    String		genedesc
    String		cytoband
    String		omim_ids
    String		clin_sig
    String		biotype
    String		pubmed
    Double      esp
    String      cosmicOccurs
    Double      cadd
    Double      cadd_phred
    Double      exac
    String      clinvarCat
    String      lrtCat
    String      mutTasteCat
    String      mutAssessCat
    String      fathmmCat
    String      metaSvmCat
    String      metaLrCat
    String      siftCat
    String      polyphenCat
    String      clinvarVal
    String      lrtVal
    String      mutTasteVal
    String      mutAssessVal
    String      fathmmVal
    String      metaSvmVal
    String      metaLrVal
    String      siftVal
    String      polyphenVal
    String      vepHgvsg
    String      vepHgvsc
    String      vepHgvsp
    String      mutStatus
    String      mutError
    String      numamps
    String      amps
    String      ampbias
    String      homopolymer
    String      varcaller

    static constraints =
        {
            variant(     maxSize: 500 )
            ens_variant( maxSize: 500 )
            gene()
            curated( nullable: true )
            filtered()
            filterFlag( nullable: true )
            reportable()
            consequence()
            hgvsc( maxSize: 500 )
            hgvsg( maxSize: 500, nullable: true)
            hgvsp( maxSize: 500, nullable: true )
            vepHgvsg( maxSize: 500, nullable: true )
            vepHgvsc( maxSize: 500, nullable: true )
            vepHgvsp( maxSize: 500, nullable: true )
            hgvspAa1( maxSize: 500, nullable: true )
            sampleName()
            readDepth()
            varDepth()
            varFreq( nullable: true )
            fwdReadDepth( nullable: true )
            fwdVarDepth( nullable: true )
            revReadDepth( nullable: true )
            revVarDepth( nullable: true)
            chr( nullable: true)
            pos( nullable: true)
            exon( nullable: true)
            cosmic( nullable: true)
            dbsnp( nullable: true)
            alamutClass( nullable: true )
            gmaf( nullable: true )
            varPanelPct( nullable: true )
            ens_transcript( nullable: true)
            ens_gene( nullable: true)
            ens_protein( nullable: true)
            ens_canonical( nullable: true)
            refseq_mrna( nullable: true)
            refseq_peptide( nullable: true)
            existing_variation( nullable: true)
            domains( maxSize: 2000, nullable: true)
            genedesc( nullable: true)
            cytoband( nullable: true)
            omim_ids( maxSize: 2000, nullable: true)
            clin_sig( nullable: true)
            biotype( nullable: true)
            pubmed( maxSize: 2000, nullable: true)
            esp( nullable: true)
            cosmicOccurs( maxSize: 1000, nullable: true )
            cadd( nullable: true )
            cadd_phred( nullable: true )
            exac( nullable: true )
            clinvarCat( nullable: true )
            lrtCat( nullable: true )
            mutTasteCat( nullable: true )
            mutAssessCat( nullable: true )
            fathmmCat( nullable: true )
            metaSvmCat( nullable: true )
            metaLrCat( nullable: true )
            siftCat( nullable: true )
            polyphenCat( nullable: true )
            clinvarVal( maxSize: 2000, nullable: true )
            lrtVal( nullable: true )
            mutTasteVal( nullable: true )
            mutAssessVal( nullable: true )
            fathmmVal( nullable: true )
            metaSvmVal( nullable: true )
            metaLrVal( nullable: true )
            siftVal( nullable: true )
            polyphenVal( nullable: true )
            mutStatus( maxSize: 500, nullable: true )
            mutError(  maxSize: 500, nullable: true )
            numamps( nullable: true )
            amps( nullable: true )
            ampbias( nullable: true )
            homopolymer( nullable: true )
            varcaller( nullable: true )
        }

    static hasMany = [ tags: Tag ]

    //  Indexes on seqSample,sampleName,variant,hgvsg
    //
    static      mapping =
    {
        seqSample   index: 'seq_sample_idx' //, unique: true
        variant     index: 'variant_idx'
        sampleName  index: 'sample_name_idx'
        hgvsg       index: 'hgvsg_idx' //, unique: true
        hgvsc       index: 'hgvsc_idx'
    }

    String	toString()
    {
        "${gene}:${hgvsc}"
    }
}
