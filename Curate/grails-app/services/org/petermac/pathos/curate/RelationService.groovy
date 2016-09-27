/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken, seleznev andrei
 */

package org.petermac.pathos.curate
import groovy.util.logging.Log4j
import org.petermac.util.Locator


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
            def sss = sr.samples.collect { it.id }
            for ( ssid in sss )
            {
                def ss = SeqSample.get(ssid)
                ss.removeFromRelations(sr)
            }
            sr.delete()
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
                    if ( ! duprel.samples?.contains(ss))
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
     * Add duplicate relation records for a list of Seqrun
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
        for ( sr in seqruns )
        {

            if (!sr.seqSamples) {
                continue    //skip seqruns with no seqsamples
            }

            log.info( "Seqrun ${sr} SeqSamples=${sr.seqSamples.size()}")

            //  Create a list of sample names for run
            //
            List  sampleList = sr.seqSamples.sampleName
            Map   replicates = [:]      // Map of sample basename to a List of replicates in a Seqrun

            // Loop through samples and create a Map of Lists of samples with a
            // common base sample name after any trailing suffix of  "-nnn" has been stripped off
            //
            for ( sample in sr.seqSamples )
            {
                //  Match sample-nnn
                //
                def match = ( sample.sampleName =~ /(.*)\-\d$/ )
                if ( match.count == 1 )
                {
                    String base = match[0][1]

                    //  Create a Map keyed on the sample base name
                    //
                    if ( ! replicates[base] )
                    {
                        replicates[base] = []
                        if ( sampleList.contains(base))
                            replicates[base] << sr.seqSamples.find{ it.sampleName == base }        //  add base name to replicates if in seqrun
                    }
                    replicates[base] << sample
                }
            }

            //  Create a replicate relation for each replicate of 2 or more samples
            //
            for ( rep in replicates )
            {
                List<SeqSample> sams = rep.value
                if ( sams.size() < 2 ) continue

                //  Do we already have a SeqRelation for this SeqSample ? If not, create one
                //
                SeqRelation reprel = ( check ? SeqRelation.findByRelationAndBase( "Replicate", rep.key as String ) : null )
                if ( ! reprel ) reprel = new SeqRelation( relation: 'Replicate', base: rep.key ).save()

                for ( ss in sams )
                {
                    if ( ! reprel.samples?.contains(ss))
                    {
                        ss.addToRelations( reprel ).save()
                        cnt++
                    }
                    else
                        log.warn("Already in relation ${reprel}")
                }
            }
        }

        return cnt
    }



    /**
     *  Returns primary SeqSample of the SeqRelation
     *  Primary is defined as latest SeqSample (by date) in a Duplicate relaitonship,  or first ('having the first number, lexicographically')  SeqSample if a Replicate relationship
     *  null if not dup or rep as other relationships aren't hiearchical
     * @param seqRelation
     * @return
     */
    static SeqSample getPrimary(seqRelation) {

        switch(seqRelation.relation) {
            case ('Duplicate'):

                // For duplicates, seqsample is primary if it's the earliest
                // iterate over seqsamples, store earleirst
                //
                SeqSample oldestSample = null
                for (ss in seqRelation.samples) {

                    if (!oldestSample) {
                        oldestSample = ss
                    } else if (ss.seqrun.runDate.before(oldestSample.seqrun.runDate)) { //if this one is older than the current oldest
                        oldestSample = ss
                    }
                }

                return oldestSample
                break;

             case ('Replicate'):
                // Seqsample is primary if has first number lexicographically e.g. it has no '-<number>' or "-number" is lowest
                // Note that behaviour is underfined for Replicate relationships with incorrect sample naming scheme

                SeqSample primary = null

                for (ss in seqRelation.samples) {
                    if(!ss.toString().contains('-')) {      //if no '-' in here, it's going to be the primary
                        primary = ss //if no '-<number>' then we're taking this as primary
                        continue     //go on with loop
                    }

                    if(!primary) {  //first iteration of loop, set this one primary
                        primary = ss
                    } else {

                        //see our lexo score for both our 'current primary' sample and for 'the ss we are looking at now'
                        //and assign current ss as primary if it wins
                        //
                        def primScore = 0
                        def ssScore = 0
                        if (primary.sampleName.contains('-') && primary.sampleName.split('-').last().isNumber()) { //split it if it's goT a '-number' ending
                                primScore = primary.sampleName.split('-').last().toInteger()
                        }
                        if (ss.sampleName.contains('-') && ss.sampleName.split('-').last().isNumber()) {
                               ssScore = ss.sampleName.split('-').last().toInteger()
                        }

                        if (primScore > ssScore) {  //the SS we are looking at now is "earlier" lexiconagraphocally than our current primary - make it our prmiary
                            if(primary.sampleName.count('-') >= ss.sampleName.count('-')) {   //if seqsample has more '-' than primary, it's lower lexicographically, don't assign in that case (i.e. 1-A-2-1 is lower than 1-A-2 and the latter is primary)
                                primary = ss
                            }
                        }
                    }
                }

                return primary
                break;

             default:
                return null
        }

    }
}
