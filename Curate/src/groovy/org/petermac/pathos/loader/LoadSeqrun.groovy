/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.pathos.loader

import groovy.util.logging.Log4j
import org.petermac.pathos.curate.*
import org.petermac.pathos.pipeline.SampleName
import java.text.SimpleDateFormat

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Create Seqrun and SeqSample records
 *
 * User: Ken Doig
 * Date: 27/02/2017
 */

@Log4j
class LoadSeqrun
{
    /**
     * Add the sequenced variants to GORM
     *
     * @param   srmap   Map of seqrun meta data
     * @return          Count of SeqVariants added
     */
    static int addSeqrun( Map srmap   )
    {
        int cnt = 0

        log.info('Adding Seqrun')
        saveSeqrun( srmap )

        log.info('Adding SeqSample')
        saveSeqSamples( srmap )

        return cnt
    }

    /**
     * Save a Seqrun record
     *
     * @param srmap     Map of Seqrun parameters
     * @return          1 if saved, 0 otherwise
     */
    static int saveSeqrun( Map srmap )
    {
        //  Check if Seqrun exists
        //
        if ( Seqrun.findBySeqrun( srmap.seqrun )) return 0

        //	Convert date format
        //
        def sdf = new SimpleDateFormat("yyMMdd")
        String seqrun = srmap.seqrun
        Date runDate = new Date()
        if ( seqrun =~ /\d{6}/ )
            runDate  = org.petermac.util.DateUtil.dateParse( sdf, seqrun[0..5] )


        //  Save record
        //
        def sr = new Seqrun(	seqrun:		srmap.seqrun,
                                platform:   srmap.platform,
                                sepe:       srmap.sepe,
                                library:    srmap.library,
                                runDate:	runDate,
                                experiment: srmap.experiment,
                                scanner:    srmap.scanner,
                                readlen:    srmap.readlen
                            )

        //  Save the new Seqrun instance
        //
        return DbLoader.saveRecord( sr, false) ? 1 : 0
    }

    /**
     * Save a sequenced sample
     *
     * @return  Count of rows added
     */
    static int saveSeqSamples( Map srmap )
    {
        log.info( 'Adding Sequenced Samples')

        int  cnt    = 0              // number of rows added
        List<String>    srs = []     // List of Seqrun    for this load
        List<String>    pss = []     // List of PatSample for this load

        for ( Map sam in srmap.samples )
        {
            //	Lookup seqrun
            //
            def seqrun = Seqrun.findBySeqrun( srmap.seqrun )
            if ( ! seqrun )
            {
                log.warn( "Couldn't find seqrun [${srmap.seqrun}]")
                continue
            }

            //  Check if SeqSample exists
            //
            if ( SeqSample.findBySampleNameAndSeqrun( sam.sample, seqrun )) continue

            //	Lookup sample
            //
            def patSample = PatSample.findBySample(SampleName.impliedPatientSampleName(sam.sample))
            if ( ! patSample )
            {
                log.debug( "Couldn't find patient sample [${sam.sample}]")
            }

            //	Lookup panel
            //
            def panel = Panel.findByManifest( sam.reference )
            if ( ! panel )
            {
                log.warn( "Couldn't find panel [${sam.reference}]" )
            }

            //  Save a list of patient samples and seqruns for adding duplicates and replicates
            //
            if ( patSample ) pss << patSample.sample
            srs << seqrun.seqrun

            //  Figure out sample type
            //  NTC: "NTC" prefix, Control: "CTRL", "CONTROL", "NA12878","NA19240" prefixes
            //
            String sampleType = null
            if ( sam.sample.startsWith("NTC")) {
                sampleType = "NTC"
            } else if ( sam.sample.startsWith("CTRL") || sam.sample.startsWith("CONTROL") || sam.sample.startsWith("NA12878") || sam.sample.startsWith("NA19240")) {
                sampleType = "Control"
            }

            //	Create RunSample as domain class
            //
            def ss = new SeqSample(	seqrun:		seqrun,
                                    patSample:	patSample,
                                    panel:		'',
                                    sampleName:	sam.sample,
                                    analysis:	sam.analysis,
                                    userName:	sam.username,
                                    userEmail:	sam.useremail,
                                    laneNo:	    sam.laneno,
                                    sampleType: sampleType
                            )

            if ( DbLoader.saveRecord( ss, true)) ++cnt
        }

        //  Add SeqSample duplicate relations for each Patient Sample
        //
        PatSample.withTransaction
        {
            status ->

                List<PatSample> patsamples = []     // List of PatSamples for this load

                for ( ps in pss.unique())
                    patsamples << PatSample.findBySample(ps)

                if ( patsamples )
                {
                    int c = RelationService.assignDuplicateRelation( patsamples , true )
                    log.info( "Assigned Duplicate to ${c} samples")
                }
        }

        //  Add SeqSample replicate relations for each Seqrun
        //
        Seqrun.withTransaction
        {
            status ->

                List<Seqrun> seqruns = []     // List of Seqrun    for this load

                for ( sr in srs.unique())
                    seqruns << Seqrun.findBySeqrun(sr)

                if ( seqruns )
                {
                    int c = RelationService.assignReplicateRelation( seqruns, true )
                    log.info("Assigned Replicate to ${c} samples")
                }
        }

        return cnt
    }
}
