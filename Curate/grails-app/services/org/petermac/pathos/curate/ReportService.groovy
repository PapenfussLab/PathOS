/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import groovy.sql.Sql
import groovy.util.logging.Log4j
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.codehaus.groovy.grails.web.util.WebUtils
import org.codehaus.groovy.runtime.StackTraceUtils
import org.petermac.util.Locator

import java.text.MessageFormat

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Responsible for the assembly of clinical reports for a SeqSample
 *
 * User: Kenneth Doig
 * Date: 29/08/13
 */

@Log4j
class ReportService
{
    def loc = Locator.instance                      // file locator

    def grailsApplication

    def servletContext

    def springSecurityService

    def reportRenderService

    def patientService

    def AuditService

    /**
     * Construct a template File for reporting
     *
     * @param sample    sample to report on
     * @return          Template File suitable for sample "Template_<test_set>_[Var|Fail|Neg].docx"
     */
    public File loadTemplate( SeqSampleReport ssr, Boolean test )
    {
        SeqSample sample = ssr.seqSample
        if ( test )
        {
            File templateFile = new File( loc.repDir, "${sample.sampleName} Template.docx")
            if ( ! templateFile.exists())
            {
                templateFile = new File( loc.repDir, "Test Template.docx" )
                if ( ! templateFile.exists())
                {
                    log.warn("Test Template file doesn't exist: " + templateFile)
                }
            }
            return templateFile
        }

        //  Default report type for reportable variants
        //
        String type = "var"

        //  No reportable variants ?
        //
        int nvars = ssr.curVariantReports.size()
        if ( ! nvars )
        {
            type = "neg"
        }

        //  Failed Sample QC ?
        //
        if ( sample.authorisedQc && ! sample.passfailFlag )
        {
            type = "fail"
        }

        //  todo use test code while we wait for mapping from Auslab
        //
        String testCode = sample?.patSample?.patAssays?.find { true }?.testSet
               testCode = testCode?.trim()

        if ( ! testCode )
        {
            log.warn( "No PatAssays found when making template for Sample ${sample} test ${test}" )

            //  fall back to default template
            //
            return  chooseDefaultTemplate( type )
        }

        //  Set template name from test code and report type
        //
        String tf = "Template_${testCode}_${type}.docx"

        //  Open Template document
        //
        File templateFile = new File( loc.repDir, tf )
        if ( ! templateFile.exists())
        {
            log.warn( "Template file for ${testCode} doesn't exist: " + templateFile )

            //  Set default template if none found
            //
            templateFile = chooseDefaultTemplate( type )
        }

        return templateFile
    }

    /**
     * Construct a default template for reporting
     * We use the default template when we are unable to find our specific template
     * @param type
     * @return
     */
    private File chooseDefaultTemplate(String type )
    {
        //  Open Template document
        //
        File templateFile = new File(loc.repDir, "Template_default_${type}.docx")
        if ( ! templateFile.exists())
        {
            log.warn("Default Template file doesn't exist: " + templateFile)
        }
        return templateFile
    }

    /**
     * Set temporary output file for reporting
     *
     * @param sample    sample to report on
     * @param fileExt   type of document to report
     *
     * @return          File suitable for sample
     */
    private File outputFile( SeqSample sample, String fileExt )
    {
        //  Allocate a temp file
        //
        File tempDir = WebUtils.getTempDir(servletContext)
        File outfile = new File( tempDir, "Report_${sample?.sampleName}.${fileExt}" )

        return outfile
    }

    /**
     * Save report into /payload directory
     *
     * @param   outfile File holding report output
     * @return          File saved to
     */
    private File copyReportToArchive( File outfile )
    {
        //  copy the file to a permanent payload directory and keep it there
        //
        def webroot = loc.pathos_home
        String newpath = webroot+"/Report/Generated"

        if (FilenameUtils.getExtension(outfile.getName()) == 'pdf') {
            newpath = newpath + "/pdf"
        } else {
            newpath = newpath + "/word"
        }

        def now = new Date()
        def timeStamp= now.format("yyyyMMdd'T'HHmmss.SSS")
        newpath = newpath + "/" + timeStamp + "_" + outfile.getName()

        File newfile = new File(newpath)
        println "Copying " + outfile + " to " + newfile
        FileUtils.copyFile( outfile, newfile )

        return newfile
    }

    /**
     * Things we need for PATHOS-2269 MVP
     * DKGM 13-April-2017
     *
     * Mostly copy+pasted from ReportRenderService
     *
     * @param SeqSample
     * @return SeqSampleReport
     */
    static def ampliconRoiService = new AmpliconRoiService()    // only need a new because stand alone doesn't have Spring

    public static SeqSampleReport makeNewSeqSampleReport( SeqSample sample, currentUser )
    {
        String template = "Test_name.pdf" // name of the output file..???

        //  Calculate amplicon QC stats
        //
        Map ampQC = ampliconRoiService.setAmpliconQc( sample, template )

        //  Calculate ROI QC stats
        //  Note, we are truncating to 1000 chars because any more than that is not worth printing in a report. ROI QC stats should be improved in future.
        //
        String rr = ampliconRoiService.roiReport( sample, template )
        if( rr.length() > 1000 ) {
            rr = rr.take(996) + "..."
        }

        // End copy+paste
        HashMap data = [
                        seqSample:          sample,
                        user:               currentUser,
                        reportFilePath:     "none",
                        sample:             sample.sampleName,
                        patient:            sample.patSample?.patient?.fullName,
                        urn:                sample.patSample?.patient?.urn,
                        dob:                sample.patSample?.patient?.dob?.format("d-MMM-yyyy"),
                        age:                sample.patSample?.patient?.age,
                        sex:                sample.patSample?.patient?.sex,
//                        requester:          sample.patSample?.requester,
//                        location:           sample.patSample?.pathlab,
                        morphology:         sample.patSample?.repMorphology,
                        site:               sample.patSample?.retSite,
                        tumour_pct:         sample.patSample?.tumourPct,
                        collect_date:       sample.patSample?.collectDate?.format("d-MMM-yyyy"),
                        rcvd_date:          sample.patSample?.rcvdDate?.format("d-MMM-yyyy"),
                        ampReads:           ampQC?.ampReads,
                        ampPct:             ampQC?.ampPct,
                        lowAmps:            ampQC?.lowAmps,
                        rois:               rr,
//                        isdraft:            sample.finalReviewBy ? 'FINAL' : 'DRAFT',
//                        clinContext:        sample.clinContext?.toString(),
//                        firstReviewer:      sample.firstReviewBy?.displayName,
//                        firstReviewedDate:  sample?.firstReviewedDate?.format("d-MMM-yyyy h:mm a"),
//                        secondReviewer:     sample.secondReviewBy?.displayName,
//                        secondReviewedDate: sample?.secondReviewedDate?.format("d-MMM-yyyy h:mm a"),
//                        finalReviewer:      sample.finalReviewBy?.displayName,
//                        finalReviewedDate:  sample?.finalReviewedDate?.format("d-MMM-yyyy h:mm a"),
                        clinicalDetails:    "",
                        resultSummary:      "",
                        recommendations:    "",
                        uncoveredRegions:   "",
                        citations:          ""
                        ]

        SeqSampleReport newReport = new SeqSampleReport(data)

        List<CurVariantReport> curVariantReports = makeVariantMaps(sample, newReport)

        newReport.setCurVariantReports(curVariantReports);
        newReport.citations = generateCitations(curVariantReports, null);

        newReport.save(flush: true, failOnError: true)
        curVariantReports.each { it.save() }

        return newReport
    }

    /**
     * Create a String of citations from all curated variant texts
     *
     * @param   curVariantReports   List of CV report records
     * @param   ssr                 The seqSampleReport
     * @param   fields (Optional)   A list of fields to pull citations from
     *
     * @return
     */
    static String generateCitations(
        List<CurVariantReport> curVariantReports,
        SeqSampleReport ssr,
        String[] fields = ['clinicalDetails', 'resultSummary', 'recommendations', 'mut', 'genedesc']
    ) {

        String text = ""
        ArrayList<String> citations = []

        if ( ssr ) {
            if ( fields.contains('clinicalDetails') )
                text += ssr.clinicalDetails ?: ""

            if ( fields.contains('resultSummary') )
                text += ssr.resultSummary   ?: ""

            if ( fields.contains('recommendations') )
                text += ssr.recommendations ?: ""
        }

        curVariantReports.each
        { CurVariantReport cvr ->
            if ( fields.contains('mut') )
                text += cvr.mut ?: ""

            if ( fields.contains('genedesc') )
                text += cvr.genedesc ?: ""
        }

        ArrayList<Long> pmids = PubmedService.listOfPMIDs(text);

        pmids.each
        {
            pmid ->
            Pubmed article = Pubmed.findByPmid( pmid as String );
            citations.push("[PMID: $pmid] " + PubmedService.buildCitation(article))
        }

        return citations.join("\n")
    }

    // Make a curVariant map from a CurVariant instead of a SeqVariant
    static Map makeCurVarMap( CurVariant cv, SeqSampleReport ssr ) {
        String references = ''

        CurVariant generic_cv = CurVariant.findByClinContextAndHgvsg(ClinContext.generic(), cv.hgvsg)

        String reportDesc = generic_cv?.reportDesc ?: ''
        String pmClass    = generic_cv?.pmClass ?: ''

        //  Add the report description for the current disease context
        //
        if (cv != generic_cv)
        {
            reportDesc += "\n"
            reportDesc += cv?.reportDesc ?: ''
            pmClass     = cv?.pmClass ?: ''
        }

        //  Collect all the references of the Pubmed IDs
        //
        PubmedService.listOfPMIDs(reportDesc).each
            {
                PMID ->
                    references += "[PMID: ${PMID}] " + PubmedService.buildCitation( Pubmed.findByPmid( PMID.toString())) + "\n"
            }

        //  Get gene
        //
        RefGene rg = RefGene.findByGene( cv.gene )

        Map variant =   [
                seqSampleReport:ssr,
                curVariant:     cv,
                sample:         ssr.seqSample.sampleName,
                gene:           cv.gene,
                refseq:         cv.hgvsc?.replaceAll(~/:.*/,''),
                hgvsc:          cv.hgvsc?.replaceAll(~/.*:/,''),
                hgvsp:          cv.hgvsp,
                refseqNP:       cv.hgvsp?.replaceAll(~/:.*/,''),
                aaChange:       cv.hgvsp?.replaceAll(~/.*:/,'') ?: "?",
                varreaddepth:   "",
                totalreaddepth: "",
                afpct:          "",
                exon:           cv.exon,
                pmClass:        pmClass,
                ampClass:       cv.ampClass ?: "",
                overallClass:   cv.overallClass ?: "",
                mut:            reportDesc,
                genedesc:       rg?.genedesc,
                citations:      references
        ]

        log.info( "Variant: ${variant.gene}:${variant.hgvsc}" )

        return variant
    }

    // Make a curVariant map from a SeqVariant
    static Map makeCurVarMap( SeqVariant sv, SeqSampleReport ssr ) {
        String reportDesc = /Sequenced Variant "${sv.hgvsg}" not yet Curated/
        String pmClass = 'none'
        String references = ''

        //  Get the report description for the null disease context
        //
        CurVariant generic_cv = sv.genericCurVariant()

        if( generic_cv )
        {
            reportDesc = generic_cv?.reportDesc ?: ''
            pmClass    = generic_cv?.pmClass ?: ''

            //  Add the report description for the current disease context
            //
            if (sv.currentCurVariant() != generic_cv)
            {
                reportDesc += "\n"
                reportDesc += sv.currentCurVariant()?.reportDesc ?: ''
                pmClass     = sv.currentCurVariant()?.pmClass ?: ''
            }

            //  Collect all the references of the Pubmed IDs
            //
            PubmedService.listOfPMIDs(reportDesc).each
                {
                    PMID ->
                        references += "[PMID: ${PMID}] " + PubmedService.buildCitation( Pubmed.findByPmid( PMID.toString())) + "\n"
                }
        }

        //  Get gene
        //
        RefGene rg = RefGene.findByGene( sv.gene )

        Map variant =   [
                seqSampleReport:ssr,
                curVariant:     sv.currentCurVariant(),
                sample:         ssr.seqSample.sampleName,
                gene:           sv.gene,
                refseq:         sv.hgvsc?.replaceAll(~/:.*/,''),
                hgvsc:          sv.hgvsc?.replaceAll(~/.*:/,''),
                hgvsp:          sv.hgvsp,
                refseqNP:       sv.hgvsp?.replaceAll(~/:.*/,''),
                aaChange:       sv.hgvsp?.replaceAll(~/.*:/,'') ?: "?",
                varreaddepth:   sv.varDepth,
                totalreaddepth: sv.readDepth,
                afpct:          sv.varFreq,
                exon:           sv.exon,
                pmClass:        pmClass,
                ampClass:       sv.currentCurVariant()?.ampClass ?: "",
                clinicalSignificance:   sv.currentCurVariant()?.overallClass ?: "",
                mut:            reportDesc,
                genedesc:       rg?.genedesc,
                citations:      references
        ]

        log.info( "Variant: ${variant.gene}:${variant.hgvsc}" )

        return variant
    }

    /**
     * Copied from ReportRenderService
     * We should be able to use that function, but it is private.
     * I would also like to add a reference to the original "CurVariant" at this point
     *
     * -DKGM 13-April-2017 PATHOS-2269
     */
    static private List<CurVariantReport> makeVariantMaps(SeqSample sample, SeqSampleReport ssr )
    {
        //  Find all reportable variants for sample
        ArrayList<SeqVariant> svs = sample.reportableVariants()

        //  Convert variants to a List of Maps
        //
        List variants = []
        for ( sv in svs )
        {
            variants.push( makeCurVarMap(sv, ssr) )
        }

        List<CurVariantReport> curVariantReports = []

        variants.each { variant ->
            CurVariantReport cvr = new CurVariantReport(variant)
            curVariantReports.add(cvr)
        }

        return curVariantReports
    }

    /**
     * Generate a report as a byte stream (PDF or MSWord format)
     *
     * @param   ssr       SeqSampleReport data of report
     * @param   swVersion PathOS version number
     * @param   test      True if a test report
     * @param   fileExt   Extension of report eg pdf, docx
     * @param   publish   True if it should be published
     * @return
     * @throws  Exception
     */
    byte[] generateReport( SeqSampleReport ssr, String swVersion = '', Boolean test, String fileExt, Boolean publish ) throws Exception
    {
        Boolean hidePat = false;
        SeqSample sample = ssr.seqSample;

        //  Open files
        //
        File outfile  = outputFile( sample, fileExt )
        File template = loadTemplate( ssr, test )

        if ( ! template.exists())
        {
            String message = "No templates found for sample ${ssr.seqSample}"
            log.warn( message )
            throw new FileNotFoundException(message)
        }

        //  Open database Todo: This should only use Grails Domain classes
        //
        Sql sql = Sql.newInstance(
            grailsApplication.config.dataSource.url,
            grailsApplication.config.dataSource.username,
            grailsApplication.config.dataSource.password,
            grailsApplication.config.dataSource.driverClassName
        )

        //  Generate report into web-apps directory
        //
        try
        {
            reportRenderService.runPreparedReport( ssr, hidePat, sql, template, outfile )
        }
        catch ( Exception e )
        {
            log.error( "Failed to render report " + e )
            StackTraceUtils.sanitize(e).printStackTrace()
            throw e
        }

        //  Failed to create a report
        //
        if ( ! outfile.exists()) return null

        //  Copy file to payload directory in web context
        //
        File newfile = copyReportToArchive( outfile )

        //  Create new SeqSampleReport record
        // AuthUser currentUser = AuthUser.findByUsername(springSecurityService.currentUser);
        //        def currentUser = springSecurityService.currentUser as AuthUser

        //  Todo: catch an exception if one occurs
        //
        // Don't make a new seqsample... perhaps update the old one?
        //        SeqSampleReport newReport = new SeqSampleReport(seqSample: sample, user: currentUser, reportFilePath: newfile.getPath() ).save(flush: true, failOnError: true)

        def audit_msg = "Reported on ${sample.sampleName} as a ${fileExt}"

        //  Only update the filepath if it is a PDF?
        //
        if ( publish )
        {
            audit_msg = "Published on ${sample.sampleName} as a ${fileExt}"
            ssr.setReportFilePath(newfile.getPath())

            try {
                String yaml =  patientService.publishToAuslab( ssr.seqSample.patSample, outfile )
                println yaml
                log.info(yaml)
            } catch (Exception e) {
                println e
                log.error(e);
            }
        }

        //  Create audit message
        //
        AuditService.audit([
            category    : 'report',
            task        : 'report',
            sample      : sample.sampleName,
            seqrun      : sample.seqrun.seqrun,
            description : audit_msg
        ])

        //  Send PDF document to browser as a byte stream
        //
        return outfile.readBytes()
    }
}















