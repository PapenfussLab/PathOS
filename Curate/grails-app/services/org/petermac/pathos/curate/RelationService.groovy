/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken, seleznev andrei
 */

package org.petermac.pathos.curate
import groovy.util.logging.Log4j
import org.petermac.util.Locator
import org.petermac.pathos.pipeline.SampleName

import java.text.MessageFormat


/**
 * Created for PathOS
 *
 * Description:
 *
 * Service with utilities to manipulate seq sample relationships
 *
 * Author: Ken Doig, Andrei Seleznev
 * Date:   20-07-2016
 */

@Log4j
class RelationService {

    def sessionFactory
    /**
     * Drop all SeqRelation records
     *
     * @param   relation    Optional relation type to delete
     */
    static void dropAllRelations( String relation = null )
    {
        //  Drop all relations, then drop the SeqRelation object
        //
        def srs = []
        if ( relation )
            srs = SeqRelation.findAllByRelation( relation ).collect { it.id }
        else
            srs = SeqRelation.findAll().collect { it.id }

        log.info( "Deleting ${srs.size()} relations")

        // do this to avoid java.util.ConcurrentModificationException
        // when modifying in a loop
        //
        for ( srid in srs )
        {
            def sr = SeqRelation.get(srid)
            log.info( "Deleting relation ${sr}")
            def sss = sr.samples().collect { it.id }
            for ( ssid in sss )
            {
                def ss = SeqSample.get(ssid)
                ss.removeFromRelations(sr)
            }
            try {
                sr.delete()
            } catch (Exception e) {
                println "Failed to delete sr ${sr} id ${sr.id} - possibly it references non-existing seqsamples. Please delete manually."
             }
        }
    }

    /**
     * Add duplicate relation records for a list of PatSamples
     *
     * @param   patSamples  List of PatSample
     * @param   check       Check for existing relations
     * @return              Number of added records
     */
    static int assignDuplicateRelation( List<PatSample> patSamples, boolean check )
    {
        int cnt = 0

        log.info( "Setting Duplicate relation for ${patSamples.size()} PatSamples")

        //  Add patient duplicate relations
        //
        for ( ps in patSamples )
        {
            //log.warn( "patSamples=${patSamples} ps=${ps}")
            if ( ps?.seqSamples?.size() >= 2 )
            {
                //log.debug( "Patient Sample ${ps} SeqSamples=${ps.seqSamples.size()}")

                //  Do we already have a SeqRelation for this SeqSample ? If not, create one
                //
                SeqRelation duprel = ( check ? SeqRelation.findByRelationAndBase( "Duplicate", ps.sample ) : null )
                if ( ! duprel ) duprel = new SeqRelation( relation: 'Duplicate', base: ps.sample ).save()

                //  Add SeqSample to relationship
                //
                for ( ss in ps.seqSamples )
                {
                    //  Add a dup relationship if we don't have one
                    //
                    if ( ! duprel.samples()?.contains(ss))
                    {
                        ss.addToRelations( duprel ).save()
                        ++cnt
                    }
                    else
                        log.warn("Already in relation ${duprel}")
                }
            }
        }

        return cnt
    }

    /**
     * Add replicate relation records for a list of Seqrun
     *
     * @param   seqruns     List of Seqrun
     * @param   check       Check for existing relations
     * @return              Number of added records
     */
    static int assignReplicateRelation( List<Seqrun> seqruns, boolean check )
    {
        int cnt = 0

        log.info( "Setting Replicate relation for ${seqruns.size()} Seqruns")

        //  Add SeqSample replicate relations
        //
        for (sr in seqruns) {

            if (!sr.seqSamples) {
                continue    //skip seqruns with no seqsamples
            }

            log.info( "Seqrun ${sr} SeqSamples=${sr.seqSamples.size()}")

            //  Create a list of sample names for run
            //
            List  sampleList = sr.seqSamples.sampleName
            Map   replicates = [:]      // Map of sample basename to a List of replicates in a Seqrun

            // Group the samples according to their basename.
            //
            for (sample in sr.seqSamples) {
                def sn = SampleName.baseName(sample.sampleName)
                if (!replicates[sn]) {
                    replicates[sn] = []
                }
                replicates[sn] << sample
            }

            //  Create a replicate relation for each replicate of 2 or more samples
            //
            for (rep in replicates) {
                List<SeqSample> sams = rep.value
                if (sams.size() < 2) {
                    continue
                }

                // Name the replicate group with the seqrun and basename
                // so that replicates of the same sample in different
                // seqruns get separate records.
                //
                String repName = "${sr.seqrun}-${rep.key}"

                //  Do we already have a SeqRelation for this SeqSample ? If not, create one
                //
                SeqRelation reprel = (check ? SeqRelation.findByRelationAndBase( "Replicate", repName) : null)
                if (! reprel) {
                    reprel = new SeqRelation( relation: 'Replicate', base: repName).save()
                }

                for (ss in sams) {
                    if (!reprel.samples()?.contains(ss)) {
                        ss.addToRelations(reprel).save()
                        cnt++
                    }
                }
            }
        }

        return cnt
    }

    /**
     * Infer the replicate relations and duplicate relations to which this sample belongs.
     *
     * @param sample    The SeqSample
     * @return nothing
     */
    static void inferReplicatesAndDuplicates(SeqSample sample) {
        String base = SampleName.baseName(sample.sampleName)
        Map samplesBySeqrun = [:]

        // If the patSample is set, then gather up the SeqSamples with this PatSample.
        //
        if (sample.patSample) {
            SeqSample.findAllByPatSample(sample.patSample).each { s ->
                if (s.id == sample.id) {
                    return
                }
                if (SampleName.baseName(s.sampleName) != base) {
                    // TumourNormal samples hang off the same PatSample,
                    // but are considered separately, so the sample had
                    // better have the same baseName.
                    return
                }
                if (!samplesBySeqrun[s.seqrun.id]) {
                    samplesBySeqrun[s.seqrun.id] = []
                }
                samplesBySeqrun[s.seqrun.id] << s
            }
        }

        // grovel over the seqrun and find the samples with the same basename
        //
        for (s in SeqSample.findAllBySeqrun(sample.seqrun)) {
            if (s.id == sample.id) {
                continue
            }
            if (SampleName.baseName(s.sampleName) != base) {
                continue
            }
            if (!samplesBySeqrun[s.seqrun.id]) {
                samplesBySeqrun[s.seqrun.id] = []
            }
            samplesBySeqrun[s.seqrun.id] << s
        }

        if (samplesBySeqrun.size() == 0) {
            return
        }

        // If the base sample occurs in multiple seqruns,
        // then we need to make duplicate relation entries for
        // the seqrun from which this sample came too!
        // 
        Boolean makeDups = (samplesBySeqrun.size() > 1)
        samplesBySeqrun.each { sid, sss ->
            List<SeqSample> allTheSeqSamples = [sample] + sss

            List rels = []
            if (sample.seqrun.id == sid) {
                // Find or create the base SeqRelation record.
                //
                String relName = "${sample.seqrun}-${base}"
                SeqRelation rel = SeqRelation.findByRelationAndBase('Replicate', relName)
                if (!rel) {
                    rel = new SeqRelation(relation: 'Replicate', base: relName)
                }
                rels << rel
            } 
            if (sample.seqrun.id != sid || makeDups) {
                String relName = base
                SeqRelation rel = SeqRelation.findByRelationAndBase('Duplicate', relName)
                if (!rel) {
                    rel = new SeqRelation(relation: 'Duplicate', base: relName)
                }
                rels << rel
            }

            for (rel in rels) {
                for (ss in allTheSeqSamples) {
                    if (!rel.samples()?.contains(ss)) {
                        ss.addToRelations(rel).save()
                    }
                }
            }
        }
    }

    /**
     * given a set of SeqSamples and a relation, return if there is a relation w/ these seqsamples exists
     * @param relation
     * @param ss
     * @return
     */
    static boolean checkIfSeqRelationExists(Set<SeqSample> ss,String relation) {
        def srs = SeqRelation.findAllByRelation(relation)
        for (sr in srs) {
            if (sr.samples() as Set == ss)  return true
        }
        return false
    }

    /**
     * given a list of seqSamples, create a TN SeqRelation
     * @param ss list of seqsamples
     * @relation type of relation
     * @return
     */
    static SeqRelation assignRelationToSeqSamples(Set<SeqSample> ss, String relation) {
        if(checkIfSeqRelationExists(ss,relation)) {

            log.error("SeqRelation ${relation} " + ss + " already exists, refusing to create")
            return null
        }

        //  create and validate
        //
        def sr = new SeqRelation(relation: relation)
        for (sample in ss) {
            sample.addToRelations(sr)
        }

        if(!sr.save()) {
            log.error("Failure to Save SeqRelation type ${relation} list: " + ss)
            sr?.errors?.allErrors?.each {
                log.error(new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                println (new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
            }

            //  Discard transient object
            //
            sr.discard()
            return null
        }

        return sr
    }

    /**
     * A list of all the relationships we want to show for a certain seqSample
     * @param seqSample
     * @return
     */
    static ArrayList<HashMap> relationships(SeqSample seqSample) {
        ArrayList<HashMap> results = []

        seqSample.relations
//        This comparator should be stored in the domain but I don't know how to do that.
// DKGM April 2018
            .sort{ a,b ->
                [ "Replicate", "Duplicate", "TumourNormal", "Trio", "TimeSeries", "Minus", "Union", "Intersect" ].indexOf(a.relation) <=> [ "Replicate", "Duplicate", "TumourNormal", "Trio", "TimeSeries", "Minus", "Union", "Intersect" ].indexOf(b.relation)
            }
            .each{ relation ->
                relation
                    .samples()
                    .findAll { it != seqSample }
                    .each { ss ->
                        results.push([
                            relation: relation.relation,
                            seqSample: ss
                        ])
                    }
            }

        return results
    }

}
