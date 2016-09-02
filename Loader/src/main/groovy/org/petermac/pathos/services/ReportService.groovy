/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import com.aspose.words.DataRelation
import com.aspose.words.DataSet
import com.aspose.words.DataTable
import com.aspose.words.Document
import com.aspose.words.License
import com.aspose.words.Node
import groovy.sql.Sql
import groovy.util.logging.Log4j
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.codehaus.groovy.grails.web.util.WebUtils
import org.petermac.util.Locator
import org.petermac.pathos.pipeline.Locus

import java.sql.ResultSet
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

    def SpringSecurityService

    static def statsService = new StatsService()    // only need a new because stand alone doesn't have Spring
    static def dbConnection

    //  Genes in assay: this is now stored in the SampleTest table Todo: use database not this hard coded table
    //
    static Map assayGenes  =    [
                                    FLD_REP_CRC:  ['BRAF', 'KRAS', 'NRAS', 'RNF43', 'PIK3CA' ],
                                    FLD_REP_MEL:  ['BRAF', 'KIT',  'NRAS', 'RAC1' ],
                                    FLD_REP_LUNG: ['BRAF', 'EGFR', 'KRAS', 'MET' ],
                                    FLD_REP_GIST: ['KIT', 'PDGFRA' ]
                                    //FLD_REP_SOMP: ['BRAF', 'KIT',  'NRAS', 'KRAS', 'EGFR', 'PDGFRA', 'MET', 'RNF43', 'PIK3CA', 'RAC1' ]

                                ]

    /**
     * Main reporting method. Generates a report from a sample after curation
     *
     * @param sample    SeqSample to report on
     * @param fileExt   Output file type currently supported .pdf, .docx, .doc, .html
     * @param userName  Optional user for audit reporting
     * @param swVersion Optional Version for audit reporting
     * @return          Raw report of sample bytes
     */
    byte[] sampleReport( SeqSample sample, String fileExt, String userName = 'none', String swVersion = '' ) throws FileNotFoundException
    {
        //  Open files
        //
        def outfile   = setOutput( sample, fileExt )
        def templates = setTemplates( sample )
        if ( ! templates )
        {
            log.warn( "No templates found for sample ${sample}")
            throw new FileNotFoundException()
            //return null
        }

        //  Open database Todo: This should only use Grails Domain classes
        //
        Sql sql = Sql.newInstance(  grailsApplication.config.dataSource.url,
                                    grailsApplication.config.dataSource.username,
                                    grailsApplication.config.dataSource.password,
                                    grailsApplication.config.dataSource.driverClassName )

        //  Generate report into web-apps directory
        //
        runReport( sample, sql, templates, outfile )


        //  copy the file to a permanant payload directory and keep it there
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
        FileUtils.copyFile(outfile,newfile)

        //  create new report
        //
        def currentUser = springSecurityService.currentUser as AuthUser

        //todo catch an exception if one occurs
        //
        SeqSampleReport newReport = new SeqSampleReport(seqSample: sample, user: currentUser, reportFilePath: newfile.getPath() ).save(flush: true, failOnError: true)

        //  Send PDF document to browser as a byte stream
        //
        if ( ! outfile.exists())
        {
            return null
        }

        //  Create audit message
        //
        def audit_msg = "Reported on ${sample.sampleName}"
        def audit     = new Audit(  category:    'curation',
                                    sample:      sample.sampleName,
                                    seqrun:      sample.seqrun.seqrun,
                                    complete:    new Date(),
                                    elapsed:     0,
                                    software:    'Path-OS',
                                    swVersion:   swVersion,
                                    task:        'report',
                                    username:    userName,
                                    description: audit_msg )

        if ( ! audit.save( flush: true ))
        {
            audit?.errors?.allErrors?.each
            {
                log.error( new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
            }
            log.error( "Failed to log audit message: ${audit_msg}")
        }

        return outfile.readBytes()
    }

    /**
     * Mainline report generator
     *
     * @param sample    Sample to report on
     * @param sql       Sql database connection Sql class
     * @param template  List of template files to use (files must be a word doc)
     * @param outfile   Output file (extension determines the file format eg .docx, .pdf, .html)
     *
     * return           Filename of report
     */
    String runReport( SeqSample sample, Sql sql, List<File> templates, File outfile )
    {
        //  Set Aspose license
        //
        String lf = loc.repDir + 'License' + loc.fs + 'Aspose.Total.Java.lic'
        def licenseFile = new File( lf )
        if (licenseFile.exists())
        {
            // If you don't specify a license, Aspose.Words works in evaluation mode.
            //
            log.info( "Setting Aspose license" )
            License asposeLicense = new License();
            asposeLicense.setLicense(licenseFile.getAbsolutePath());
        }
        else
            log.error( "Aspose license file doesn't exist " + licenseFile )

        //  Set db connection
        //
        dbConnection = sql.getConnection()

        //  Loop through templates performing merge of data with template
        //
        Document reports = null
        for ( template in templates )
        {
            //  Report on the sample
            //
            log.info( "Generating report for sample ${sample} from ${template} into ${outfile}")

            Document doc = mergeSampleDocument( sample, template )

            if ( reports )
            {
                Node last = reports.importNode( doc.getLastSection(), true )
                reports.appendChild(last)
            }
            else
                reports = doc
        }

        //  Save in output file
        //
        reports.save( outfile.absolutePath )

        return outfile.absolutePath
    }

    /**
     * Run document merge on sample data
     *
     * @param sample    SeqSample
     * @param template  Template File
     * @return          Merged document
     */
    Document mergeSampleDocument( SeqSample sample, File template )
    {
        Document doc = new Document( template.path )

        // Populate the Dataset with the parent data and the data from the child table
        //
        DataSet dataSet = getDataSet( sample, template.name );

        // Merge the data with the document template
        //
        doc.getMailMerge().executeWithRegions(dataSet);

        return doc;
    }


    /**
     * Create a dataset to be used to populate a MS Word merge document
     * ToDo: This should be generated from GORM Domain objects - not directly accessing RDB
     *
     * @param   sample      Sample to report on
     * @param   template    Name of output file
     * @return              DataSet to be merged with document
     */
    DataSet getDataSet( SeqSample sample, String template )  throws Exception
    {
        //  Calculate amplicon QC stats
        //
        def ampQC = setAmpliconQc( sample, template )

        //  Calculate ROI QC stats
        //
        String rr = roiReport( sample, template )
        //println rr

        // Note that mail merging using DataSet or DataTable classes is like working with disconnected data. All data needed for the mail merge
        // operation must be loaded into memory and must be scrollable. Either the ResultSets objects must be scrollable and open
        // for the duration of mail merge or the data can be loaded in a CachedRowSet.
        //
        DataSet dataSet = new DataSet();
        ResultSet rs

        //  Query to get all sample and patient details to put in report header
        //

        rs= executeQuery(   """
                            select  distinct
                                    ss.sample_name                           as sample,
                                    pat.full_name                            as patient,
                                    pat.urn,
                                    date_format(pat.dob,'%d-%b-%Y')          as dob,
                                    pat.sex,
                                    date_format(sam.collect_date,'%d-%b-%Y') as collect_date,
                                    date_format(sam.rcvd_date,'%d-%b-%Y')    as rcvd_date,
                                    sam.requester,
                                    sam.pathlab                              as location,
                                    sam.rep_morphology                       as morphology,
                                    sam.ret_site                             as site,
                                    sam.tumour_pct                           as tumour_pct,
                                    ''                                       as extref,
                                    ${ampQC.ampReads}                        as ampReads,
                                    ${ampQC.ampPct}                          as ampPct,
                                    '${ampQC.lowAmps}'                       as lowAmps,
                                    '${rr}'                                  as rois,
                                    (CASE WHEN ss.final_review_by_id IS NULL THEN "DRAFT" ELSE "" END) as isdraft
                            from	patient     as pat,
                                    pat_sample  as sam,
                                    seq_sample  as ss
                            where   pat.id = sam.patient_id
                            and     sam.id = ss.pat_sample_id
                            and     ss.id  = ${sample.id}
                            limit     1
                            """)

        //  we hide patient details if the user is not an admin, curator, or lab
        //
        def currentUser = springSecurityService?.currentUser as AuthUser

        def hidePatientDetails = true
        if ( currentUser && currentUser.authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_CURATOR" || it.authority == "ROLE_LAB"} )
            hidePatientDetails = false

        //  If no patient exists, or if we are hiding details, then create a dummy entry
        //
        if ( ! rs.next() || hidePatientDetails  )
        {
            rs= executeQuery(   """
                                select  distinct
                                        '${sample.sampleName}'  as sample,
                                        'No Patient'            as patient,
                                        ''                      as urn,
                                        '01-Jan-2000'           as dob,
                                        'U'                     as sex,
                                        '01-Jan-2000'           as collect_date,
                                        '01-Jan-2000'           as rcvd_date,
                                        ''                      as requester,
                                        ''                      as extref,
                                        ''                      as location,
                                        ''                      as morphology,
                                        ''                      as site,
                                        ''                      as tumour_pct,
                                        0                       as ampReads,
                                        0.0                     as ampPct,
                                        ''                      as lowAmps,
                                        ''                      as rois,
                                        ''                      as isdraft
                                from     dual
                                limit     1
                                """)

        }

        rs.beforeFirst()    // rewind cursor after testing with next()

        //  Convert to DataTable object
        //
        DataTable sampleHeader = new DataTable( rs, "Samples")
        dataSet.getTables().add( sampleHeader )

        //  Query to get each variant for a sample ordered by gene/position
        //
        rs= executeQuery(   """
                            select	distinct
                                    sv.sample_name as sample,
                                    sv.gene,
                                    substring_index(sv.hgvsc,':', 1) as refseq,
                                    substring_index(sv.hgvsc,':',-1) as hgvsc,
                                    ifnull(sv.hgvsp_aa1,sv.hgvsp) as hgvsp_aa1,
                                    sv.hgvsp,
                                    sv.exon,
                                    sv.var_depth  as varreaddepth,
                                    sv.read_depth as totalreaddepth,
                                    format(sv.var_depth*100/sv.read_depth,1) as afpct,
                                    ifnull(var.pm_class,'none') as class,
                                    ifnull(var.report_desc,concat('CurVariant ',sv.variant,' not yet curated.')) as mut,
                                    gd.genedesc,
                                    '' as refs
                            from	seq_variant as sv
                            left
                            join	cur_variant     as var
                            on		sv.curated_id = var.id
                            left
                            join	ref_hgnc_genes as gd
                            on		sv.gene = gd.gene
                            where	sv.seq_sample_id = ${sample.id}
                            and		sv.reportable = 1
                            order
                            by		sv.gene,
                                    sv.hgvsc
                            """);

        DataTable variants = new DataTable( rs, "Variants");
        dataSet.getTables().add( variants )

        // Add the relation between parent and child tables for nested MailMerge.
        // Add a DataRelation to specify relations between these tables.
        //
        String[] slist = ["sample"]

        //  The Samples table is the master table with relations to Variants linking
        //  on the sample variable
        //
        dataSet.getRelations().add( new DataRelation(
                                    "SamplesToVariants",
                                    "Samples",
                                    "Variants",
                                    slist,
                                    slist))

        return dataSet
    }

    /**
     * Execute an SQL query and return the resulting rows Todo: simplify this and use Groovy Result Sets
     *
     * @param query     SQL query string
     * @return          ResultSet of rows found by query
     */
    static ResultSet executeQuery(String query) throws Exception
    {
        def stmt = dbConnection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
        stmt.executeQuery(query)
    }

    /**
     * Construct a template File for reporting
     *
     * @param sample    sample to report on
     * @return          List of Files suitable for sample "<panelGroup> [Var|Fail|Neg] Template.docx"
     */
    List setTemplates( SeqSample sample )
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
        for ( pg in pgs )
        {
            //  Set template name from panel group and report type
            //
            String tf = pg + type + "Template.docx"

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

        return templateFiles
    }

    /**
     * Construct a panel group prefix for the template file
     * Append the first test set name (if any) for somatic panels only
     *
     * @param sample    Sample to report on
     * @return          List of Either the panel group for this sample or the panel group appended with the test set name
     */
    List setPanelGroup( SeqSample sample )
    {
        def pg = sample.panel.panelGroup
        def st = sample.patSample?.patAssays

        //  If not somatic or no tests, just use panelGroup
        //
        if ( pg != 'MP FLD Somatic Production' || ! st ) return [ pg ]

        //  Find all test sets for this patient sample
        //
        def testSets = st.collect { it.testName }

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
    File setOutput( SeqSample sample, String fileExt )
    {
        //  Allocate a temp file
        //
        File tempDir = WebUtils.getTempDir(servletContext)
        File outfile = new File( tempDir, "Report_${sample?.sampleName}.${fileExt}" )

        return outfile
        
    }

    /**
     * Calculate report amplicon QC stats
     *
     * @param sample    Sample to query
     * @param template  Name of output file
     * @return          Map of QC stats [   lowAmps:   <multiline text of failed amplicons>,
     *                                      ampReads:  <mean amp reads>,
     *                                      ampPct:    <% of amp more than 0.2 reads> ]
     */
    static private Map setAmpliconQc( SeqSample sample, String template )
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

            for ( amp in lowAmps) {
                if (ampHasGene(amp, template)) {

                    amplicons += "${amp.amplicon} (reads ${amp.readsout})\n"
                } else {

                    genesFailed = genesFailed + 1
                }
            }

        }

        String lowQc = "There were ${lowAmps.size()-genesFailed} low read amplicons with <${noreads} aligned reads:\n"

        if ( template =~ /Fail/ ) {
            lowQc += "not listed\n"
        } else if ( genesFailed == lowAmps.size() ) {
            lowQc += "not listed\n"
        } else{
            lowQc = lowQc + amplicons
        }


        qcAmp['lowAmps']  = lowQc
        qcAmp['ampReads'] = statsService.ampReads( sample )
        qcAmp['ampPct']   = statsService.ampPct( sample )

        return qcAmp
    }

    /**
     * Check if Amplicon is in gene list for Assay
     *
     * @param amp       Amplicon to test
     * @param template  template file name with embedded assay name
     * @return          true if assay doesn't have a gene filter or amp gene is in assay
     */
    static private boolean ampHasGene( AlignStats amp, String template )
    {
        List genes = []
        def match = ( template =~ /(FLD_REP_[A-Z]+)/ )
        if ( match.count == 1 )
        {
            def sampleTest = match[0][1]	//	Sample test name embedded in template filename
            genes = assayGenes[sampleTest] as List
        }
        if ( ! genes ) return true

        for ( gene in genes )
            if ( amp.amplicon =~ /${gene}/ ) return true

        return false
    }

    /**
     * Create a report formatted list of ROIs
     *
     * @param   sample      sample to report
     * @param   template    Template file name
     * @return              report formatted String of ROIs
     */
    static private String roiReport( SeqSample sample, String template )
    {
        return roiCoverage(sample,template).collect { "${it.name} (coverage ${it.coverage})" }.join('\n')
    }

    /**
     * Create a List of Maps for all ROIs with their minimum coverage
     *
     * @param   sample    Sample to process
     * @param   template  Template file name
     * @return            List of Maps [name: <roiname>, coverage: <cov>]...
     */
    static public List<Map> roiCoverage( SeqSample sample, String template )
    {
        List roicovs = []

        //  Find all ROIs for the panel
        //
        List<Roi> rois = Roi.findAllByPanel( sample.panel )
        if ( ! rois ) return roicovs

        //  Collect all panel amplicons and their coverage
        //
        List amps = AlignStats.findAllBySeqrunAndSampleName( sample.seqrun.seqrun, sample.sampleName )

        //  Loop through ROIs and find overlapping Amplicons
        //
        for ( roi in rois )
        {
            if ( template && ! roiHasGene( roi, template)) continue

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

            //println ( "ROI: ${roi.name} Amplicon Overlaps ${ol}")


            //  Collect list of ROIs and their minimum coverage for overlapping amplicons
            //
            roicovs << [name: roi.name, coverage: minCoverage( roir, ol )]
        }

        return roicovs
    }



    /**
     * Check if ROI is in gene list for Assay
     *
     * @param roi       ROI to test
     * @param template  template file name with embedded assay name
     * @return          true if assay doesn't have a gene filter or roi gene is in assay
     */
    static private boolean roiHasGene( Roi roi, String template )
    {
        List genes = []
        def match = ( template =~ /(FLD_REP_[A-Z]+)/ )
        if ( match.count == 1 )
        {
            def sampleTest = match[0][1]	//	Sample test name embedded in template filename
            genes = assayGenes[sampleTest] as List
        }
        if ( ! genes ) return false

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
}
