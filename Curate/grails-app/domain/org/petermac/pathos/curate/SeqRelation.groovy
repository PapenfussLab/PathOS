/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

/**
 * Domain Class to capture relationships between SeqSample groups
 * In particular Replicates, Duplicates, TumourNormals, Trios
 *
 * User: Ken Doig
 * Date: 29-Apr-16  Initial Create
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

/**
 * A SeqRelation object is a wrapper for grouping samples that
 * belong together: replicates, tumour-normal pairs, etc.
 *
 * A note on nomenclature:
 * We wish to distinguish repeated sequencing of a sample when it
 * is done across multiple sequence runs (duplicates), and when it
 * is done multiple times in a single sequence run (replicates).
 * Our nomenclature doesn't currently distinguish technical replicates
 * using the same library, and technical replicates using separate libraries
 * prepared from the same sample.
 */
@Entity
class SeqRelation
{
    /**
     * The relation property describes the relationship for the group
     * (see the inList declaration below).
     */
    String  relation

    /**
     * The base property gives the label that identifies the group.
     *
     * Prior to PathOS 1.4, this was just the basename of the sample,
     * but this lead to problems where multiple sequences runs have
     * replicates for the same sample.
     *
     * From PathOS 1.4, the base property is a key whoes composition
     * depends on the relationship. For duplicates, it is the basename
     * of the sample, but for replicates it is seqrun-sample, so that
     * replicates in separate sequence runs are stored separately.
     */
    String  base = 'none'           // common base sample

    // sample derived from this seqrelation, if there is one:
    //
    String derivedSampleName         // pointing directly to a seqSample breaks the ss-sr hasMasy relationship
    String derivedSampleSeqrunName   // so instead we store name+run (unique identifiers)



    static  constraints =
    {
        relation( inList: [ "Replicate", "Duplicate", "TumourNormal", "Trio", "TimeSeries", "Minus", "Union", "Intersect" ]  )
        base( nullable: true )

        derivedSampleName( nullable: true )
        derivedSampleSeqrunName( nullable: true )
    }

    //  Indexes on relation and base
    //
    static      mapping =
    {
        base        index: 'base_idx'
        relation    index: 'relation_idx'
    }

    String	toString()
    {
        "${base}[${samples()?.size()}]:${relation}"
    }

    //  return all samples with this as relation
    //
    Set<SeqSample> samples() {
        return SeqSample.executeQuery("select ss from org.petermac.pathos.curate.SeqSample ss, org.petermac.pathos.curate.SeqRelation sr where sr in elements(ss.relations) AND sr.id=${this.id}")
    }

    //  return this seqRelation's derived sample as a SeqSample
    //
    SeqSample derivedSample() {
        return SeqSample.findBySampleNameAndSeqrun(this.derivedSampleName,Seqrun.findBySeqrun(this.derivedSampleSeqrunName))
    }
}
