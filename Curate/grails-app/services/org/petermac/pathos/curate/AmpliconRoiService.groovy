/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import groovy.util.logging.Log4j
import org.petermac.pathos.pipeline.Locus
import org.petermac.util.Locator

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Responsible collecting ROI and Amplicon coverage data for a Sample and Test_Set (Assay)
 *
 * User: Kenneth Doig
 * Date: 29/08/13
 */

@Log4j
class AmpliconRoiService
{
    def loc = Locator.instance                      // file locator

    static def statsService = new StatsService()    // only need a new because stand alone doesn't have Spring

    /**
     * Calculate report amplicon QC stats
     *
     * @param sample    Sample to query
     * @param template  Name of output file
     * @return          Map of QC stats [   lowAmps:   <multiline text of failed amplicons>,
     *                                      ampReads:  <mean amp reads>,
     *                                      ampPct:    <% of amp more than 0.2 reads> ]
     */
    static public Map setAmpliconQc( SeqSample sample, String template )
    {
        int  noreads = 100
        Map  qcAmp   = [:]
        List<AlignStats> lowAmps = statsService.lowAmplicons( sample, noreads )

        //  Dont list failed amplicons if this is a "Fail" report
        //
        int genesFailed = 0
        String amplicons = ''

        if (! (template =~ /Fail/) )
        {
            //  Hack to filter amplicons by test set genes Todo: use DB to store test set genes or wait till this
            //  goes away by reporting attrition
            //

            for ( amp in lowAmps)
            {
                if ( ampHasGene(amp, templateToSampleTest(template)))
                {
                    amplicons += "${amp.amplicon} (reads ${amp.readsout})\n"
                }
                else
                {
                    genesFailed = genesFailed + 1
                }
            }
        }

        String lowQc = "There were ${lowAmps.size()-genesFailed} low read amplicons with <${noreads} aligned reads:\n"

        if ( template =~ /Fail/ )
        {
            lowQc += "not listed\n"
        }
        else if ( genesFailed == lowAmps.size())
        {
            lowQc += "not listed\n"
        }
        else
        {
            lowQc = lowQc + amplicons
        }

        qcAmp['lowAmps']  = lowQc
        qcAmp['ampReads'] = statsService.ampReads( sample )
        qcAmp['ampPct']   = statsService.ampPct( sample )

        return qcAmp
    }

    /**
     * Convert a report template file into a test_set name
     * Todo: this is a hack needs to be removed
     *
     * @param   template    Report file name
     * @return              Test_set name of form FLD_REP_XXX where xxx in [CRC,MEL,LUNG,GIST]
     */
    static private String templateToSampleTest( String template )
    {
        String sampleTest = ''
        def match = ( template =~ /(FLD_REP_[A-Z]+)/ )
        if ( match.count == 1 )
        {
            sampleTest = match[0][1]	//	Sample test name embedded in template filename
        }
        match = ( template =~ /(FAM1_[A-Z_+]+)/ )
        if ( match.count == 1 )
        {
            sampleTest = match[0][1]	//	Sample test name embedded in template filename
        }

        return sampleTest
    }

    /**
     * Check if Amplicon is in gene list for Assay
     *
     * @param amp           Amplicon to test
     * @param sampleTest    Test_set of sample
     * @return              true if assay doesn't have a gene filter or amp gene is in assay
     */
    static private boolean ampHasGene( AlignStats amp, String sampleTest )
    {
        List genes = sampleTestGenes(sampleTest)

        for ( gene in genes )
            if ( amp.amplicon =~ /${gene}/ ) return true

        return false
    }

    /**
     * Create a report formatted list of ROIs
     *
     * @param   sample      sample to report
     * @param   template    Report template file name
     * @return              report formatted String of ROIs
     */
    static public String roiReport( SeqSample sample, String template)
    {
        String sampleTest = templateToSampleTest( template )

        return roiCoverage( sample, sampleTest ).collect { "${it.name} (coverage ${it.coverage})" }.join('\n')
    }

    /**
     * Create a List of Maps for all ROIs with their minimum coverage
     *
     * @param   sample      Sample to process
     * @param   sampleTest  Test_set of sample
     * @return              List of Maps [name: <roiname>, coverage: <cov>]...
     */
    static public List<Map> roiCoverage( SeqSample sample, String sampleTest )
    {
        List roicovs = []

        //  Find all ROIs for the panel
        //
        List<Roi> rois = Roi.findAllByPanel( sample.panel )
        if ( ! rois ) return roicovs

        //  Collect all panel amplicons and their coverage
        //
        List<Map> amps = AlignStats.findAllBySeqrunAndSampleName( sample.seqrun.seqrun, sample.sampleName )

        //  Loop through ROIs and find overlapping Amplicons
        //
        for ( roi in rois )
        {
            if ( sampleTest && ! roiHasGene( roi, sampleTest)) continue

            def roir = new Locus( roi.chr, roi.startPos, roi.endPos )

            //  Collect amplicons that overlap ROI
            //
            List ol = []
            for ( amp in amps)
            {
                if ( amp.amplicon == 'SUMMARY' ) continue
                def ampr = new Locus( amp.location )
                if ( roir.overlap( ampr )) ol << amp
            }

            //  Collect list of ROIs and their minimum coverage for overlapping amplicons
            //
            roicovs << [name: roi.name, coverage: minCoverage( roir, ol )]
        }

        return roicovs
    }

    /**
     * Check if ROI is in gene list for Assay
     *
     * @param roi           ROI to test
     * @param sampleTest    template to look up test_set genes
     * @return              true if assay doesn't have a gene filter or roi gene is in assay
     */
    static private boolean roiHasGene( Roi roi, String sampleTest)
    {
        List genes = sampleTestGenes(sampleTest)

        return roi.gene in genes
    }

    /**
     * Find the minimum coverage for the region of interest
     *
     * @param roi   ROI to test
     * @param amps  Overlapping amplicons
     * @return      min coverage over whole ROI
     */
    static private Integer minCoverage( Locus roi, List amps )
    {
        Integer minc = null
        for ( int pos = roi.startPos(); pos <= roi.endPos(); pos++ )
        {
            def base = new Locus( roi.chr, pos, pos )

            //  Add up coverage for all amplicons at this base
            //
            int cov = 0
            for ( amp in amps )
                if ( base.overlap( new Locus(amp.location)))
                    cov += amp.readsout

            //  Keep the minimum coverage for this ROI
            //
            if ( ! minc || (cov < minc)) minc = cov
        }

        if ( minc == null )
        {
            log.error( "No coverage for ROI ${roi}")
        }

        return minc
    }

    /**
     * Find set of genes in a Sample Test for variant filtering
     *
     * @param   testSet  Assay test set
     * @return           List of genes (if any) in a testSet
     */
    static List<String> sampleTestGenes( String testSet)
    {
        LabAssay labAssay = LabAssay.findByTestSet( testSet.trim())
        String   genes    = labAssay?.genes
        if ( ! genes ) return []

        //  Convert a comma separated list of genes into a List
        //
        List geneList = genes.replaceAll(/ /,'').tokenize(',')

        return geneList
    }

}
