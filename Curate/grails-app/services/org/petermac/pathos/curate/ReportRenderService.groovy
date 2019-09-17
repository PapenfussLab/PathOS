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
import java.sql.Connection
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
 *
 * Updated: 8-June-2017 by DKGM
 */

@Log4j
class ReportRenderService
{
    static Connection dbConnection

    static Sql sql

    /**
     * Mainline report generator
     *
     * @param ssr       SeqSampleReport to run a report on
     * @param hidePat   Hide the patient details on the report
     * @param sql       Sql database connection Sql class
     * @param template  List of template files to use (files must be a word doc)
     * @param outfile   Output file (extension determines the file format eg .docx, .pdf, .html)
     * @return          Filename of report
     */
    public String runPreparedReport(SeqSampleReport ssr, Boolean hidePat, Sql sql, File template, File outfile)
    {
        setAsposeLicense()

        this.sql = sql

        //  Set db connection
        //
        dbConnection = sql.getConnection()

        //  Report on the sample
        //
        log.info( "Generating report for sample ${ssr.sample} from ${template} into ${outfile}")

        Document report = mergePreparedSampleDocument( ssr, hidePat, template )

        //  Save in output file
        //
        report.save( outfile.absolutePath )

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

    private static Document mergePreparedSampleDocument( SeqSampleReport ssr, Boolean hidePat, File template )
    {
        Document doc = new Document( template.path )

        //  Show all MailMerge fields
        //
        String[] fields = doc.getMailMerge().getFieldNames()
        for ( field in fields )
            log.info( "Merge field: $field" )

        // Populate the Dataset with the parent data and the data from the child table
        //
        DataSet dataSet = getPreparedDataSet( ssr, hidePat, template.name, fields );

        // Merge the data with the document template
        //
        if ( dataSet )
            doc.getMailMerge().executeWithRegions(dataSet);

        return doc;
    }


    private static DataSet getPreparedDataSet( SeqSampleReport ssr, Boolean hidePatientDetails, String template, String[] fields )  throws Exception
    {

        // Only pull citations for the fields that we're using.
        String citations = ReportService.generateCitations(ssr.curVariantReports, ssr, fields)

        //  Convert sample/patient to DataTable object
        //
        DataTable sampleHeader = createPreparedSampleTable( ssr, hidePatientDetails, citations )

        // Note that mail merging using DataSet or DataTable classes is like working with disconnected data. All data needed for the mail merge
        // operation must be loaded into memory and must be scrollable. Either the ResultSets objects must be scrollable and open
        // for the duration of mail merge or the data can be loaded in a CachedRowSet.
        //
        DataSet dataSet = new DataSet();
        if ( ! dataSet.getTables())
        {
            log.error( "No sample table found for [${ssr.sample}] and template [${template}]" )
            return null
        }
        dataSet.getTables().add( sampleHeader )

        // If there are any reportable variants, collect them and build their relationships.
        if ( ssr.curVariantReports.size() ) {
            //  Convert variants to DataTable object
            DataTable variants = createPreparedVariantTable( ssr, citations )
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
        }

        return dataSet
    }

    private static DataTable createPreparedSampleTable( SeqSampleReport ssr, Boolean hidePatient, String citations )
    {

        ArrayList<Long> pmids = PubmedService.listOfPMIDs(citations)

        //  Convert Sample/Patient data into a Map of attributes
        //
        Map sam = [
            sample              :	ssr.sample,
            patient             :	ssr.patient(),
            urn                 :	ssr.urn(),
            dob                 :	ssr.dob(),
            age                 :	ssr.age(),
            sex                 :	ssr.sex(),
            requester           :	ssr.requester(),
            extref              :	ssr.extref(),
            location            :	ssr.location(),
            morphology          :	ssr.morphology,
            site                :	ssr.site,
            tumour_pct          :	ssr.tumour_pct,
            collect_date        :	ssr.collect_date,
            rcvd_date           :	ssr.rcvd_date,
            ampReads            :	ssr.ampReads,
            ampPct              :	ssr.ampPct,
            lowAmps             :	ssr.lowAmps,
            rois                :	ssr.rois,
            isdraft             :	ssr.isdraft(),
            clinContext         :	ssr.clinContext(),
            firstReviewer       :	ssr.firstReviewer(),
            firstReviewedDate   :	ssr.firstReviewedDate(),
            secondReviewer      :	ssr.secondReviewer(),
            secondReviewedDate  :	ssr.secondReviewedDate(),
            finalReviewer       :	ssr.finalReviewer(),
            finalReviewedDate   :	ssr.finalReviewedDate(),
            citations           :	convertCitations(pmids, citations),
            clinicalDetails     :   convertCitations(pmids, ssr.clinicalDetails),
            resultSummary       :   convertCitations(pmids, ssr.resultSummary),
            recommendations     :   convertCitations(pmids, ssr.recommendations),
            address             :   ssr.address,
            phone               :   ssr.phone,
            requestAddress      :   ssr.requestAddress,
            copyTo              :   ssr.copyTo,
            specimen            :   ssr.specimen,
            sampleType          :   ssr.sampleType,
            histologicalFeatures :  ssr.histologicalFeatures,
            uncoveredRegions    :   ssr.uncoveredRegions
        ]

        //  Convert the Map of attributes into a ResultSet for Aspose ingestion
        //

        // Create a temporary table
        String cols = sam.collect { kv -> "$kv.key text"}.join(',')
        String sqlc = "create temporary table t2 ($cols) CHARACTER SET utf8"
        sql.execute( sqlc )

        // Insert data
        cols = sam.collect { kv -> "$kv.key"}.join(',')
        String vals = sam.collect {'?'}.join(',')
        sqlc = "insert into t2 ($cols) values ($vals)"
        sql.execute( sqlc, sam.collect { it.value } )

        // select data
        ResultSet rs = executeQuery("select * from t2")

        //  Convert to DataTable object
        //
        return new DataTable( rs, "Samples")
    }

    private static String convertCitations(ArrayList<Long> pmids, String string){
        pmids = pmids ?: []
        string = string ?: ""
        try {
            def groups = (string =~ /\[PMID: (\d+(?:, \d+)*)]/)

            String[] clean = string.split(/\[PMID: \d+(, \d+)*]/)
            String result = ""

            if(clean.length > 0) {
                result = clean[0]
            }

            groups.eachWithIndex { def group, int i ->
                if(group[1]) {
                    def citations = group[1]
                        .split(", ")
                        .collect { pmid -> pmids.indexOf(pmid as Long)+1 }
                        .sort()

                    result += "["+citations.join(', ')+"]"
                }
                if( i+1 < clean.length) {
                    result += clean[i+1]
                }
            }
            return result
        } catch(e) {
            log.warn(e)
            return string
        }
    }

    private static DataTable createPreparedVariantTable( SeqSampleReport ssr, String citations )
    {
        ArrayList<Long> pmids = PubmedService.listOfPMIDs(citations)

        //  Create a List of Maps of variant attributes from GORM
        //
        List<HashMap> vars = ssr.curVariantReports.collect({ CurVariantReport it ->
            return [
                sample			:	it.sample,
                gene			:	it.gene,
                refseq			:	it.refseq,
                hgvsc			:	it.hgvsc,
                hgvsp			:	it.hgvsp,
                refseqNP        :   it.refseqNP,
                aaChange        :   it.aaChange,
                varreaddepth	:	it.varreaddepth,
                totalreaddepth	:	it.totalreaddepth,
                afpct			:	it.afpct,
                exon			:	it.exon,
                class			:	it.pmClass,   // Note that the mailMerge field is "class"
                ampClass		:	it.ampClass,
                clinicalSignificance	:	it.clinicalSignificance,
                mut			    :	convertCitations(pmids, it.mut),
                genedesc		:	convertCitations(pmids, it.genedesc)
            ]}) ?: null


        if ( ! vars ) return null           // no variants

        //  Convert List of Maps into a temporary DB table to generate a ResultSet for Aspose to
        //  use in its MailMerge operation

        //  Use the first Map element for the header columns
        //
        String cols = vars[0].collect { kv -> "$kv.key text"}.join(',')
        String sqlc = "create temporary table t1 ($cols) CHARACTER SET utf8"
        sql.execute( sqlc )

        cols = vars[0].collect { kv -> "$kv.key"}.join(',')
        String vals = vars[0].collect {'?'}.join(',')
        sqlc = "insert into t1 ($cols) values ($vals)"

        for ( Map var in vars )
            sql.execute( sqlc, var.collect { it.value } )

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
