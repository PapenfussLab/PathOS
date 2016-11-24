/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import com.aspose.words.*
import groovy.sql.Sql
import groovy.util.logging.Log4j
import org.petermac.util.Locator
import java.sql.ResultSet
import java.util.List


/**
 * Created for PathOS.
 *
 * Description:
 *
 * Responsible for the Rendering of reports for a SeqSample using the Aspose Java to MSWord library
 *
 * User: Kenneth Doig
 * Date: 29/08/13
 */

@Log4j
class ReportRenderService
{
    static def ampliconRoiService = new AmpliconRoiService()    // only need a new because stand alone doesn't have Spring

    static def dbConnection

    static Sql sql

    /**
     * Mainline report generator
     *
     * @param sample    Sample to report on
     * @param hidePat   Hide the patient details on the report
     * @param sql       Sql database connection Sql class
     * @param template  List of template files to use (files must be a word doc)
     * @param outfile   Output file (extension determines the file format eg .docx, .pdf, .html)
     * @return          Filename of report
     */
    public String runReport( SeqSample sample, Boolean hidePat, Sql sql, List<File> templates, File outfile )
    {
        setAsposeLicense()

        this.sql = sql

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

            Document doc = mergeSampleDocument( sample, hidePat, template )

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
     * Set the License for Aspose (if any)
     */
    public static void setAsposeLicense()
    {
        def loc = Locator.instance

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
    }

    /**
     * Run document merge on sample data
     *
     * @param sample    SeqSample
     * @param hidePat   Hide the patient details on the report
     * @param template  Template File
     * @return          Merged document
     */
    private static Document mergeSampleDocument( SeqSample sample, Boolean hidePat, File template )
    {
        Document doc = new Document( template.path )

        //  Show all MailMerge fields
        //
        def fields = doc.getMailMerge().getFieldNames()
        for ( field in fields )
            log.info( "Merge field: $field" )

        // Populate the Dataset with the parent data and the data from the child table
        //
        DataSet dataSet = getDataSet( sample, hidePat, template.name );

        // Merge the data with the document template
        //
        doc.getMailMerge().executeWithRegions(dataSet);

        return doc;
    }

    /**
     * Create a dataset to be used to populate a MS Word merge document
     *
     * @param   sample              Sample to report on
     * @param   hidePatientDetails  Hide the patient details on the report
     * @param   template            Name of output file
     * @return                      DataSet to be merged with document
     */
    private static DataSet getDataSet( SeqSample sample, Boolean hidePatientDetails, String template )  throws Exception
    {
        //  Convert sample/patient to DataTable object
        //
        DataTable sampleHeader = createSampleTable( sample, hidePatientDetails, template )

        // Note that mail merging using DataSet or DataTable classes is like working with disconnected data. All data needed for the mail merge
        // operation must be loaded into memory and must be scrollable. Either the ResultSets objects must be scrollable and open
        // for the duration of mail merge or the data can be loaded in a CachedRowSet.
        //
        DataSet dataSet = new DataSet();
        dataSet.getTables().add( sampleHeader )

        //  Convert variants to DataTable object
        //
        DataTable variants = createVariantTable( sample )
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
     * Create a DataTable for Aspose document renderer from SeqSample variants
     *
     * @param sample        Sample to use for reportable variants
     * @param hidePatient   Supress patient details
     * @param template      Report filename (contains assay within filename) Todo: remove this
     * @return              DataTable object of variants (needed for Aspose library)
     */
    private static DataTable createSampleTable( SeqSample sample, Boolean hidePatient, String template )
    {
        //  Convert Sample/Patient data into a Map of attributes
        //
        Map sam = getSampleMap( sample, hidePatient, template )

        //  Convert the Map of attributes into a ResultSet for Aspose ingestion
        //
        String fields = sam.collect { kv -> "'$kv.value' as $kv.key"}.join(',')
        ResultSet rs= executeQuery( "select $fields from dual" )

        //  Convert to DataTable object
        //
        return new DataTable( rs, "Samples")
    }

    /**
     * Create a DataTable for Aspose document renderer from SeqSample variants
     *
     * @param sample    Sample to use for reportable variants
     * @return          DataTable object of variants (needed for Aspose library)
     */
    private static DataTable createVariantTable( SeqSample sample )
    {
        //  Create a List of Maps of variant attributes from GORM
        //
        List vars = getVariantMaps( sample )

        if ( ! vars ) return null           // no variants

        //  Convert List of Maps into a temporary DB table to generate a ResultSet for Aspose to
        //  use in its MailMerge operation

        //  Use the first Map element for the header columns
        //
        String cols = vars[0].collect { kv -> "$kv.key text"}.join(',')
        String sqlc = "create temporary table t1 ($cols)"
        sql.execute( sqlc )

        for ( var in vars )
        {
            cols = var.collect { kv -> "$kv.key"}.join(',')
            String vals = var.collect { kv -> "'$kv.value'"}.join(',')
            sqlc = "insert into t1 ($cols) values ($vals)"
            sql.execute( sqlc )
        }

        //  Read temp table created into a ResultSet for loading into Aspose
        //
        ResultSet rs = executeQuery("select * from t1")

        //  Clean up temp table
        //
        sql.execute( "drop temporary table t1")

        //  Convert ResultSet into DataTable and return
        //
        return new DataTable( rs, "Variants");
    }

    /**
     * Extract all Patient and Sample reporting attributes and convert to a Map ready for rendering
     * All Map keys must match MS-Word MailMerge fields
     *
     * @param sample                SeqSample to report on
     * @param hidePatientDetails    True if Patient identifiers should be suppresssed
     * @param template              File name of report - used to determine the TestSet
     * @return                      Map of reporting attributes keyed by MailMerge field name
     */
    private static Map getSampleMap( SeqSample sample, Boolean hidePatientDetails, String template )
    {
        //  Calculate amplicon QC stats
        //
        Map ampQC = ampliconRoiService.setAmpliconQc( sample, template )

        //  Calculate ROI QC stats
        //
        String rr = ampliconRoiService.roiReport( sample, template )

        Map sam =   [
                    sample:         sample.sampleName,
                    patient:        sample.patSample.patient.fullName,
                    urn:            sample.patSample.patient.urn,
                    dob:            sample.patSample.patient.dob.format("dd-MMM-yyyy"),
                    sex:            sample.patSample.patient.sex,
                    requester:      sample.patSample.requester,
                    location:       sample.patSample.pathlab,
                    morphology:     sample.patSample.repMorphology,
                    extref:         '',
                    site:           sample.patSample.retSite,
                    tumour_pct:     sample.patSample.tumourPct,
                    collect_date:   sample.patSample.collectDate.format("dd-MMM-yy"),
                    rcvd_date:      sample.patSample.rcvdDate.format("dd-MMM-yy"),
                    ampReads:       ampQC.ampReads,
                    ampPct:         ampQC.ampPct,
                    lowAmps:        ampQC.lowAmps,
                    rois:           rr,
                    isdraft:        sample.finalReviewBy ? '' : 'DRAFT'
                    ]

        //  Suppress patient identifiers
        //
        if ( hidePatientDetails )
        {
            sam.patient = 'hidden'
            sam.urn     = 'hidden'
            sam.dob     = ''
            sam.sex     = 'U'
        }

        return sam
    }

    /**
     * Create a List of Maps for each reportable variant
     * Maps are created directly from Gorm objects
     *
     * @param   sample  Sample to be reported
     * @return          List of Maps for each reportable variant
     */
    static private List getVariantMaps( SeqSample sample )
    {
        //  Find all reportable variants for sample
        //
        def svs = sample.seqVariants.findAll { sv -> sv.reportable && sv.curated }

        //  Convert variants to a List of Maps
        //
        List vars = []
        for ( sv in svs )
        {
            Map var =   [
                        sample:         sample.sampleName,
                        gene:           sv.gene,
                        refseq:         (sv.hgvsc).replaceAll(~/:.*/,''),
                        hgvsc:          (sv.hgvsc).replaceAll(~/.*:/,''),
                        hgvsp:          sv.hgvsp,
                        varreaddepth:   sv.varDepth,
                        totalreaddepth: sv.readDepth,
                        afpct:          sv.varFreq,
                        mut:            sv.curated.reportDesc
                        ]

            vars << var

            log.info( "Variant: $var.hgvsc" )
        }

        return vars
    }

    /**
     * Execute an SQL query and return the resulting rows
     *
     * @param query     SQL query string
     * @return          ResultSet of rows found by query
     */
    static private ResultSet executeQuery(String query) throws Exception
    {
        def stmt = dbConnection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
        stmt.executeQuery(query)
    }
}
