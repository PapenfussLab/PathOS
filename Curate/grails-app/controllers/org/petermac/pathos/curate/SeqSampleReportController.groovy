package org.petermac.pathos.curate

import grails.converters.JSON
import org.grails.plugin.filterpane.FilterPaneUtils
import org.petermac.util.Locator
import org.springframework.dao.DataIntegrityViolationException

class SeqSampleReportController {

    def loc = Locator.instance                      // file locator
    def SpringSecurityService
    PatientService patientService = new PatientService()
    def utilService

    def inspect( Long id ) {
        if(id) {
            SeqSample ss = SeqSample.get(id)
            ArrayList<SeqSampleReport> ssr = SeqSampleReport.findAllBySeqSample(ss)
            return [ seqSample: ss, seqSampleReports: ssr ]
        } else {
            flash.message = message(code: "Error, no such ${id}", args: [message(code: 'error', default: 'SeqSampleReport'), id])
            redirect(uri:'/')
        }
    }

    // This is here because of CRUD reasons.
    // I've removed "save", "update" and "delete", but I'm leaving show as a redirec to "edit"
    // Just in case I've left a "show" somewhere.
    // DKGM 14-July-2017
    def show(Long id) {
        redirect(action: "edit", params: params)
    }

    /**
     * If we have existing SeqSampleReports for this SeqSample, redirect there
     * Otherwise, make a new SeqSampleReport
     * @param seqSample.id
     * @return
     */
    def link(Long id) {
        SeqSample seqSample = SeqSample.get(id)
        if (!seqSample) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqSample.label', default: 'SeqSample'), id])
            redirect(controller: "SeqSample", action: "list")
            return
        }

        List<SeqSampleReport> list = SeqSampleReport.findAllBySeqSample(seqSample)
        if ( !list.isEmpty() && list.last() && list.last().sample ) {
            redirect( controller: "SeqSampleReport", action: "edit", id: list.last().id )
        } else {
            redirect( controller: "SeqVariant", action: "buildSeqSampleReport", id: seqSample.id )
        }
    }

    def edit(Long id) {
        SeqSampleReport seqSampleReportInstance = SeqSampleReport.get(id)
        if (!seqSampleReportInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqSampleReport.label', default: 'SeqSampleReport'), id])
            redirect(uri:'/')
            return
        }

        // Bad code, please remove when Detente Billing code mess is sorted out
        // -DKGM 21-June-2017
        ReportService rs = new ReportService()
        File template = rs.loadTemplate(seqSampleReportInstance, false)
        String billingCode = seqSampleReportInstance?.seqSample?.patSample?.patAssays?.find { true }?.testSet

        String templateDownloadLink = "${utilService.context()}/seqSampleReport/downloadReportTemplate?filename=Template_"
        templateDownloadLink += billingCode ?: "default";

        ArrayList<String> billingCodes = seqSampleReportInstance?.seqSample?.patSample?.patAssays?.collect { it.testSet }

        // Allow forced template
        // Sorry for the weird if statements here,
        // but we should be showing this stuff on a naive install.
        // -DKGM 21-June-2017
        Boolean allowForced = true;
        String env = loc.pathosEnv
        AuthUser currentUser = SpringSecurityService.currentUser as AuthUser

        if ( env == "pa_prod" || env == "pa_uat" ) {
            allowForced = false;
            if ( currentUser && currentUser.authorities.any {
                it.authority == "ROLE_ADMIN" || it.authority == "ROLE_DEV" || it.authority == "ROLE_CURATOR"
            }) {
                allowForced = true;
            }
        }

        Boolean pathosExport = loc.pathosExport ?: false;
        String jiraAddress = loc.jiraAddress
        [seqSampleReportInstance: seqSampleReportInstance, templateName: template.name, templateDownloadLink: templateDownloadLink, billingCodes: billingCodes, allowForced: allowForced, pathosExport: pathosExport, jiraAddress:jiraAddress]    }

    /**
     * This could probably use some safety...
     * Please check this before releasing to production
     * DKGM 9-May-2017
     * @param filename
     * @return
     */
    def downloadReportTemplate(String filename)
    {
        File theFile = new File( loc.repDir, filename )
        response.setHeader "Content-disposition", "attachment; filename=$filename"
        render(file: theFile, contentType: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document; charset=utf-8')
    }

    def saveCurVariantReportInfo()
    {
        HashMap result = [:]
        try {
            result.id = request.JSON.id

            Long id = request.JSON.id
            CurVariantReport cvr = CurVariantReport.get(id)

            String mut = request.JSON.mut
            if(mut) cvr.setProperty("mut", mut)

            String genedesc = request.JSON.genedesc
            if(genedesc) cvr.setProperty("genedesc", genedesc)

            String gene = request.JSON.gene
            if(gene) cvr.setProperty("gene", gene)

            String exon = request.JSON.exon
            if(exon) cvr.setProperty("exon", exon)

            String pmClass = request.JSON.pmClass
            if(pmClass) cvr.setProperty("pmClass", pmClass)

            String refseq = request.JSON.refseq
            if(refseq) cvr.setProperty("refseq", refseq)

            String hgvsc = request.JSON.hgvsc
            if(hgvsc) cvr.setProperty("hgvsc", hgvsc)

            String hgvsp = request.JSON.hgvsp
            if(hgvsp) cvr.setProperty("hgvsp", hgvsp)

            String refseqNP = request.JSON.refseqNP
            if(refseqNP) cvr.setProperty("refseqNP", refseqNP)

            String aaChange = request.JSON.aaChange
            if(aaChange) cvr.setProperty("aaChange", aaChange)

            String varreaddepth = request.JSON.varreaddepth
            if(varreaddepth) cvr.setProperty("varreaddepth", varreaddepth)

            String totalreaddepth = request.JSON.totalreaddepth
            if(totalreaddepth) cvr.setProperty("totalreaddepth", totalreaddepth)

            String afpct = request.JSON.afpct
            if(afpct) cvr.setProperty("afpct", afpct)

            cvr.save(flush:true)

        } catch (Exception e) {
            // PATHOS-2523
            // The only known error is the utf8 bug.
            // If more errors become known, we might want to return different codes
            // DKGM 17-July-2017
            response.status = 412
            log.error("Exception ${e} in SeqSampleReportController saveCurVariantReportInfo()")
            result.error = e.localizedMessage ?: "Unknown error"
        }

        render result as JSON
    }

    def refreshCitations(Long id){
        SeqSampleReport ssr = SeqSampleReport.get(id)
        String citations = ReportService.generateCitations(ssr.curVariantReports, ssr);
        ssr.citations = citations

        render citations
    }

    def saveSeqSampleReportInfo() {
        Long id = request.JSON.id
        SeqSampleReport ssr = SeqSampleReport.get(id);
        HashMap result = [
            id: id
        ]

        try {

            String  clinicalDetails     = request.JSON.clinicalDetails
            String  resultSummary       = request.JSON.resultSummary
            String  recommendations     = request.JSON.recommendations
            String  address             = request.JSON.address
            String  phone               = request.JSON.phone
            String  requestAddress      = request.JSON.requestAddress
            String  copyTo              = request.JSON.copyTo
            List    cvrs = request.JSON.curVariantReports?.collect { CurVariantReport.get(it) }
            String  specimen            = request.JSON.specimen
            String  sampleType          = request.JSON.sampleType
            String  histologicalFeatures = request.JSON.histologicalFeatures
            String  uncoveredRegions    = request.JSON.uncoveredRegions
            String  morphology          = request.JSON.morphology
            String  site                = request.JSON.site
            String  tumour_pct          = request.JSON.tumour_pct
            String  collect_date        = request.JSON.collect_date
            String  rcvd_date           = request.JSON.rcvd_date

            if(clinicalDetails) ssr.setClinicalDetails(clinicalDetails)
            if(resultSummary) ssr.setResultSummary(resultSummary)
            if(recommendations) ssr.setRecommendations(recommendations)
            if(address) ssr.setAddress(address)
            if(phone) ssr.setPhone(phone)
            if(requestAddress) ssr.setRequestAddress(requestAddress)
            if(copyTo) ssr.setCopyTo(copyTo)
            if(cvrs) ssr.setCurVariantReports(cvrs)
            if(specimen) ssr.setSpecimen(specimen)
            if(sampleType) ssr.setSampleType(sampleType)
            if(histologicalFeatures) ssr.setHistologicalFeatures(histologicalFeatures)
            if(uncoveredRegions) ssr.setUncoveredRegions(uncoveredRegions)
            if(morphology) ssr.setMorphology(morphology)
            if(site) ssr.setSite(site)
            if(tumour_pct) ssr.setTumour_pct(tumour_pct)
            if(collect_date) ssr.setCollect_date(collect_date)
            if(rcvd_date) ssr.setRcvd_date(rcvd_date)

            ssr.save(flush:true)

        } catch (Exception e) {
            // PATHOS-2523
            // The only known error is the utf8 bug.
            // If more errors become known, we might want to return different codes
            // DKGM 17-July-2017
            response.status = 412
            result.error = e.rootCause?.localizedMessage ?: "Unknown error"

        }
        result.version = ssr.getVersion()

        render result as JSON
    }

    def makeNewCurVariantReport() {
        Long id = request.JSON?.id

        if ( id ) {

            String hgvsg = request.JSON?.hgvsg ?: ""
            hgvsg = hgvsg.trim()

        SeqSampleReport ssr = SeqSampleReport.get(id);
        ClinContext cc = ssr.seqSample.clinContext
        CurVariant cv = CurVariant.findByClinContextAndHgvsg(cc, hgvsg) ?: CurVariant.findByClinContextAndHgvsg(ClinContext.generic(), hgvsg) ?: null

        CurVariantReport cvr

        if(cv) {
            cvr = new CurVariantReport(ReportService.makeCurVarMap(cv, ssr)).save()
        } else {
            cvr = new CurVariantReport([seqSampleReport: ssr, sample: ssr.seqSample.sampleName]).save()
        }

        render cvr as JSON;

        } else {
            response.status = 400
            HashMap error = [error: "error, no seqSampleReport id provided"]
            render error as JSON
        }
    }

    def removeCurVariantReport(Long cvr) {
        CurVariantReport variant = CurVariantReport.get(cvr)
        if(variant) {
            SeqSampleReport ssr = variant.seqSampleReport;

            ssr.removeFromCurVariantReports(variant);
            ssr.save()

            variant.delete(flush: true)

            render "success"
        } else {
            render "fail?"
        }
    }

    def refreshPatientDetails(Long id) {
        HashMap result = [success: -1]
        SeqSampleReport ssr = SeqSampleReport.get(id);
        PatSample ps = ssr?.seqSample?.patSample

        if(ps) {

            // This service sets the details for the patient object directly
            result["success"] = patientService.refreshPatient(ps);

            if (result.success > 0) {
                result['patient']   = ssr.patient()
                result['urn']       = ssr.urn()
                result['dob']       = ssr.dob()
                result['age']       = ssr.age()
                result['sex']       = ssr.sex()
            }
        }

        render result as JSON;
    }



}





















