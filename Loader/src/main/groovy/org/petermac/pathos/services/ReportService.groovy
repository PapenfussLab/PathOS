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

    /**
     * Main reporting method. Generates a report from a sample after curation
     *
     * @param sample    SeqSample to report on
     * @param hidePat   Hide the patient details on the report
     * @param fileExt   Output file type currently supported .pdf, .docx, .doc, .html
     * @param swVersion Optional Version for audit reporting
     * @return          Raw report of sample bytes
     */
    byte[] sampleReport( SeqSample sample, Boolean hidePat, String fileExt, String swVersion = '', Boolean test ) throws FileNotFoundException
    {
        //  Open files
        //
        File outfile   = setOutput( sample, fileExt )
        List templates = setTemplates( sample, test )
        if ( ! templates )
        {
            log.warn( "No templates found for sample ${sample}")
            throw new FileNotFoundException()
        }

        //  Open database Todo: This should only use Grails Domain classes
        //
        Sql sql = Sql.newInstance(  grailsApplication.config.dataSource.url,
                                    grailsApplication.config.dataSource.username,
                                    grailsApplication.config.dataSource.password,
                                    grailsApplication.config.dataSource.driverClassName )

        //  Generate report into web-apps directory
        //
        reportRenderService.runReport( sample, hidePat, sql, templates, outfile )

        //  Failed to create a report
        //
        if ( ! outfile.exists()) return null

        //  Copy file to payload directory in web context
        //
        File newfile = copyReportToArchive( outfile )

        //  Create new SeqSampleReport record
        // AuthUser currentUser = AuthUser.findByUsername(springSecurityService.currentUser);
        def currentUser = springSecurityService.currentUser as AuthUser

        //  Todo: catch an exception if one occurs
        //
        SeqSampleReport newReport = new SeqSampleReport(seqSample: sample, user: currentUser, reportFilePath: newfile.getPath() ).save(flush: true, failOnError: true)

        //  Create audit message
        //
        def audit_msg = "Reported on ${sample.sampleName}"
        def audit     = new Audit(  category:    'curation',
                                    sample:      sample.sampleName,
                                    seqrun:      sample.seqrun.seqrun,
                                    complete:    new Date(),
                                    elapsed:     0,
                                    software:    'PathOS',
                                    swVersion:   swVersion,
                                    task:        'report',
                                    username:    currentUser.username,
                                    description: audit_msg )

        if ( ! audit.save( flush: true ))
        {
            audit?.errors?.allErrors?.each
            {
                log.error( new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
            }
            log.error( "Failed to log audit message: ${audit_msg}")
        }

        //  Send PDF document to browser as a byte stream
        //
        return outfile.readBytes()
    }

    /**
     * Construct a template File for reporting
     *
     * @param sample    sample to report on
     * @return          List of Files suitable for sample "<panelGroup> [Var|Fail|Neg] Template.docx"
     */
    public List setTemplates( SeqSample sample, Boolean test )
    {
        //  Default report type for reportable variants
        //
        String type   = " Var "

        //  No reportable variants ?
        //
        int nvars = sample.seqVariants.findAll{it.reportable}.size()
        if ( ! nvars ) type = " Neg "

        //  Failed Sample QC ?
        //
        if ( sample.authorisedQc && ! sample.passfailFlag ) type = " Fail "

        def pgs = setPanelGroup( sample )

        //  Validate template files, only return ones that exist
        //
        List templateFiles = []
        for ( String pg in pgs )
        {
            //  Set template name from panel group and report type
            //
            String tf = pg.trim() + type + "Template.docx"

            //  Open Template document
            //
            def templateFile = new File( loc.repDir, tf )
            if ( templateFile.exists())
            {
                templateFiles << templateFile
            }
            else
                log.warn( "Template file doesn't exist: " + templateFile)
        }

        //  Set default template if none found
        //
        if ( ! templateFiles )
        {
            //  Open Template document
            //
            def templateFile = new File( loc.repDir, "Default" + type + "Template.docx" )
            if ( templateFile.exists())
            {
                templateFiles << templateFile
            }
            else
                log.warn( "Template file doesn't exist: " + templateFile)
        }

        if ( test ) {
            def templateFile = new File( loc.repDir, "Test Template.docx" )
            if ( templateFile.exists())
            {
                templateFiles = []
                templateFiles << templateFile
            }
            else
                log.warn( "Template file doesn't exist: " + templateFile)
        }

        return templateFiles
    }

    /**
     * Construct a panel group prefix for the template file
     * Append the first test set name (if any) for somatic panels only
     *
     * @param sample    Sample to report on
     * @return          List of Either the panel group for this sample or the panel group appended with the test set name
     */
    private static List setPanelGroup( SeqSample sample )
    {
        def pg = sample.panel.panelGroup
        def st = sample.patSample?.patAssays

        //  If not somatic or familial or no tests, just use panelGroup
        //
        if ((pg != 'MP FLD Somatic Production' && pg != 'MP Germline Capture Assay') || ! st ) return [ pg ]

        //  Find all test sets for this patient sample
        //
        def testSets = st.collect { it.testName.trim() }

        //  If no test sets use default template for panelGroup
        //
        if ( testSets.size() == 0 ) return [ pg ]

        log.info( "Sample ${sample} has patAssays ${testSets}" )

        //  prepend the panelGroup to testSets and return the list
        //
        return testSets.collect{ pg + ' ' + it }
    }

    /**
     * Set temporary output file for reporting
     *
     * @param sample    sample to report on
     * @param fileExt   type of document to report
     *
     * @return          File suitable for sample
     */
    private File setOutput( SeqSample sample, String fileExt )
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
        def webroot = servletContext.getRealPath('/')
        String newpath = webroot+"/payload"

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
}
