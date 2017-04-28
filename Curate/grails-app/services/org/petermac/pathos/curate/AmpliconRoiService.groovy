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

    //  Genes in assay: this is now stored in the SampleTest table Todo: use database not this hard coded table
    //
    static Map assayGenes  =    [
                                FLD_REP_CRC:        ['BRAF', 'KRAS', 'NRAS', 'RNF43', 'PIK3CA' ],
                                FLD_REP_MEL:        ['BRAF', 'KIT',  'NRAS', 'RAC1' ],
                                FLD_REP_LUNG:       ['BRAF', 'EGFR', 'KRAS', 'MET' ],
                                FLD_REP_GIST:       ['KIT', 'PDGFRA' ],
                                FAM1_FAM_ONE:	    ['AIP', 'APC', 'ATM', 'AXIN2', 'BAP1', 'BMPR1A', 'BRCA1', 'BRCA2', 'BRIP1', 'BUB1B', 'CDC73', 'CDH1', 'CDK4', 'CDKN1B', 'CDKN2A', 'CHEK2', 'CSDE1', 'DICER1', 'ENGLN1', 'EGLN2', 'EPAS1', 'EPCAM', 'EXT1', 'EXT2', 'FH', 'FLCN', 'GATA2', 'GREM1', 'HOXB13', 'IDH1', 'KIF1B', 'MAX', 'MDH2', 'MEN1', 'MET', 'MLH1', 'MSH2', 'MSH6', 'MUTYH', 'NF1', 'NF2', 'NTHL1', 'PALB2', 'PMS2', 'POLD1', 'POLE', 'POT1', 'PRKAR1A', 'PTCH1', 'PTEN', 'RAD51C', 'RAD51D', 'RB1', 'RET', 'RUNX1', 'SCG5', 'SDHA', 'SDHAF2', 'SDHB', 'SDHC', 'SDHD', 'SMAD4', 'SMARCA4', 'SMARCB1', 'SMARCE1', 'STK11', 'SUFU', 'TMEM127', 'TP53', 'TSC1', 'TSC2', 'VHL' ],
                                FAM1_SPECTRUM:	    ['APC', 'ATM', 'BAP1', 'BMPR1A', 'BRCA1', 'BRCA2', 'BRIP1', 'CDH1', 'CDK4', 'CDKN2A', 'CHEK2', 'EPCAM', 'GREM1', 'MLH1', 'MSH2', 'MSH6', 'MUTYH', 'PALB2', 'PMS2', 'POLD1', 'POLE', 'PTEN', 'RAD51C', 'RAD51D', 'SMAD4', 'STK11', 'TP53' ],
                                FAM1_BRCA:	        ['BRCA1', 'BRCA2' ],
                                FAM1_BRCA_PLUS:	    ['ATM', 'BRCA1', 'BRCA2', 'PALB2', 'TP53' ],
                                FAM1_BR_OV_PR_PA:	['ATM', 'BRCA1', 'BRCA2', 'BRIP1', 'CDH1', 'CDKN2A', 'CHEK2', 'HOXB13', 'PALB2', 'PTEN', 'RAD51C', 'RAD51D', 'STK11', 'TP53' ],
                                FAM1_CRC_ENDOM:	    ['APC', 'EPCAM', 'MLH1', 'MSH2', 'MSH6', 'MUTYH', 'NTHL1', 'PMS2', 'POLD1', 'POLE', 'PTEN', 'STK11' ],
                                FAM1_OV_CRC:	    ['BRCA1', 'BRCA2', 'BRIP1', 'EPCAM', 'MLH1', 'MSH2', 'MSH6', 'PMS2', 'RAD51C', 'RAD51D' ],
                                FAM1_MMR:	        ['EPCAM', 'MLH1', 'MSH2', 'MSH6', 'PMS2' ],
                                FAM1_POLY:	        ['APC', 'AXIN2', 'BMPR1A', 'BUB1B', 'GREM1', 'MUTYH', 'NTHL1', 'POLD1', 'POLE', 'PTEN', 'SMAD4', 'STK11' ],
                                FAM1_CRC_POLY:	    ['APC', 'AXIN2', 'BMPR1A', 'BUB1B', 'EPCAM', 'GREM1', 'MLH1', 'MSH2', 'MSH6', 'MUTYH', 'NTHL1', 'PMS2', 'POLD1', 'POLE', 'PTEN', 'SMAD4', 'STK11' ],
                                FAM1_PARA_PHEO:	    ['CSDE1', 'EGLN1', 'EGLN2', 'EPAS1', 'FH', 'IDH1', 'KIF1B', 'MAX', 'MDH2', 'NF1', 'RET', 'SDHA', 'SDHAF2', 'SDHB', 'SDHC', 'SDHD', 'TMEM127', 'VHL' ],
                                FAM1_SKIN:	        ['BAP1', 'CDK4', 'CDKN2A', 'FH', 'FLCN', 'POT1', 'PTCH1', 'RB1', 'SUFU' ],
                                FAM1_ENDOC:	        ['AIP', 'CDC73', 'CDKN1B', 'MEN1', 'PRKAR1A', 'PTEN', 'RET', 'VHL' ],
                                FAM1_RENAL:	        ['FH', 'FLCN', 'MET', 'SDHB', 'SDHC', 'SDHD', 'TSC1', 'TSC2', 'VHL' ],
                                FAM1_SARC:	        ['APC', 'EXT1', 'EXT2', 'RB1', 'TP53' ],
                                FAM1_HAEM:          ['ADAMTS13', 'C3', 'CALR', 'CD46', 'CEBPA', 'CFB', 'CFH', 'CFHR1', 'CFI', 'CSF3R', 'CTC1', 'DGKE', 'DKC1', 'EGLN1', 'ELANE', 'EPAS1', 'EPOR', 'G6PC3', 'GATA1', 'GATA2', 'GFI1', 'HAX1', 'JAK2', 'LYST', 'MPL', 'NHP2', 'NOP10', 'PRF1', 'RAB27A', 'RPL11', 'RPL35A', 'RPL5', 'RPS10', 'RPS17', 'RPS19', 'RPS24', 'RPS26', 'RPS7', 'RTEL1', 'RUNX1', 'SBDS', 'SH2B3', 'SH2D1A', 'SRP72', 'STX11', 'STXBP2', 'TERC', 'TERT', 'THBD', 'THPO', 'TINF2', 'UNC13D', 'VHL', 'VWF', 'WAS', 'WRAP53', 'XIAP', 'TP53']
                                ]

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
     * @param   sampleTest  Assay test set name
     * @return              List of genes (if any) in a sampleTest
     */
    static List<String> sampleTestGenes( String sampleTest)
    {
        List genes = assayGenes[sampleTest.trim()] as List

        return genes
    }
}
