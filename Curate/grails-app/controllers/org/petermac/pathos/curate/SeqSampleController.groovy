/*
 * Copyright (c) 2013. PathOS SeqSample Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.converters.JSON
import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.codehaus.groovy.runtime.StackTraceUtils
import org.grails.plugin.easygrid.Easygrid
import org.petermac.util.DbConnect
import org.petermac.util.DbLock
import org.petermac.util.DbUtil
import org.petermac.util.Locator

@Easygrid
@Log4j
class SeqSampleController
{
    static scaffold = true
    def SpringSecurityService
    def SeqSampleService
    def AuditService
    static def loc = Locator.instance

    def showQC(Long id)
    {
        def seqSampleInstance = SeqSample.get(id)
        if ( ! seqSampleInstance)
        {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqSample.label', default: 'SeqSample'), id])
            redirect(action: "list")
            return
        }

        [ seqSampleInstance: seqSampleInstance]
    }

    def authoriseSampleQc =
    {
        Long id         = params.id as Long
        String passfail = params?.passfail
        def currentUser = springSecurityService.currentUser as AuthUser

        if ( passfail == '' )
        {
            flash.message = 'Set the QC status before authorising'
            redirect(action: "showQC", id: id)
            return
        }

        def ss = SeqSample.get(id)
        if ( ! ss )
        {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqSample.label', default: 'SeqSample'), id])
            redirect(action: "list")
            return
        }

        //check role
        if (!(currentUser.authorities.any { it.authority == "ROLE_ADMIN"|| it.authority == "ROLE_DEV"  || it.authority == "ROLE_CURATOR" || it.authority == "ROLE_LAB"}))
        {
            flash.message = "You do not have sufficient access privileges to perform this action"
            redirect(action: "show", id: id)
            return
        }



        //if we're revoking we need to be admins
        if ( !(params?.passfail ) && !(currentUser.authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_DEV" } ) )
        {
            flash.message = "Only administrators can revoke QC !"
            redirect(action: "showQC", id: id)
            return
        }



        //check if SeqRun for this sample has passed QC
        if (passfail == 'Pass') {
            def sr = Seqrun.get(ss.seqrunId)

            if (!(sr.passfailFlag) || !(sr.authorisedFlag)) {
                flash.message = "This sample's seqrun ${sr} must pass QC first"
                redirect(action: "showQC", id: id)
                return
            }

        }

        if ( ! ss.authorisedQcFlag )
        {
            ss.authorisedQc     = currentUser
            ss.authorisedQcFlag = true
            ss.passfailFlag   = (passfail == 'Pass')
            ss.qcComment      = params?.qcComment
        }
        else
        {
            ss.authorisedQc     = null
            ss.authorisedQcFlag = false
            ss.passfailFlag   = false
            ss.qcComment      = null
        }

        //  Save updates
        //
        if ( ! ss.save(flush: true))
        {
            log.error( "Failed to update authorisation for [${ss}]")
        } else {

            //  Log an audit message
            //


            def audit_msg = "Set QC authorisation on ${ss.sampleName} to ${ss.passfailFlag ? 'Pass' : 'Fail'} Comment: ${ss.qcComment} "

            AuditService.audit([
                category    : 'curation',
                seqrun      : ss.seqrun.seqrun,
                sample      : ss.sampleName,
                task        : 'sample qc',
                description : audit_msg
            ])

        }

        redirect(action: "showQC", id: id)
    }

    /**
     * Table definition for Easygrid
     */
    def seqsampleGrid =
        {
            dataSourceType  'gorm'
            domainClass     SeqSample
            enableFilter    true
            editable        false
            inlineEdit      false

            //  Export parameters
            //
            export
                    {
                        export_title  'SeqSamples'
                        maxRows       1000000          // Maximum number of samples per export
                    }

            //  Grid jqgrid defaults
            //
            jqgrid
            {
                height        = '100%'
                width         = '100%'
                rowNum        = 20
                rowList       = [20, 50, 100]
                sortable      = true
                filterToolbar = [ searchOperators: false ]
                sortname      = 'id'
                sortorder     = 'desc'
            }

            columns
            {
                id          { type 'id'; jqgrid { hidden = true } }
                sampleName  { jqgrid { formatter "showlink"; formatoptions { baseLinkUrl '../../seqVariant/svlist' }}}
                patSample {
                    jqgrid {
                        formatter "showlink"
                        formatoptions {
                            baseLinkUrl     = "../../patSample/show"
                            idName          = "seqSample"
                        }
                    }
                    value { ss -> ss?.patSample?.sample }
                    filterClosure
                    { filter ->
                        patSample { ilike('sample', "%${filter.paramValue}%") }
                    }
                    enableFilter true
                }
                seqrun
                {
                    jqgrid { formatter "showlink"
                        formatoptions {
                            baseLinkUrl     = "../../seqrun/show"
                            idName          = "seqSample"
                        }
                    }
                    value { ss -> ss?.seqrun?.seqrun }
                    filterClosure
                    { filter ->
                        seqrun { ilike('seqrun', "%${filter.paramValue}%") }
                    }
                    enableFilter true
                }
                panel
                {
                    value { ss -> ss?.panel?.manifest }
                    filterClosure
                    { filter ->
                        panel { ilike('manifest', "%${filter.paramValue}%") }
                    }
                    enableFilter true
                }
                analysis
                userEmail
                firstReviewBy
                {
                    value { ss -> ss?.firstReviewBy?.displayName }
                    filterClosure
                    { filter ->
                            firstReviewBy { ilike('displayName', "%${filter.paramValue}%") }
                    }
                    enableFilter true
                }
                finalReviewBy
                {
                    value { ss -> ss?.finalReviewBy?.displayName }
                    filterClosure
                    { filter ->
                            finalReviewBy { ilike('displayName', "%${filter.paramValue}%") }
                    }
                    enableFilter true
                }
                authorisedQc
                {
                    value { ss -> ss?.authorisedQc?.displayName }
                    filterClosure
                    { filter ->
                            authorisedQc { ilike('displayName', "%${filter.paramValue}%") }
                    }
                    enableFilter true
                }
                authorisedQcFlag
                passfailFlag
                qcComment
            }
        }

    /**
     * view for editing gene mask and rerunning pipeline
     *
     * Model:
     *    seqSample: the seqSample requested
     *    genes: a unique list of all genes in all labAssays
     */
    def editGeneMask(Long id) {

        SeqSample seqSample = SeqSample.get(id)
        if (!seqSample) {
            flash.message = "Sequenced Sample ${id} not found"
            redirect(action: "list")
            return
        }

        Set<String> genes = []

        LabAssay.list().each { labAssay ->
            String assayGenes = labAssay.genes ?: ""
            assayGenes.tokenize(",").each { gene ->
                if(gene)     genes.add(gene.trim())
            }
        }

        Boolean vcfExists = false
        File vcf = SeqSampleService.retrieveSampleVcf(seqSample.seqrun.seqrun,seqSample.sampleName)
        if (vcf.exists()) {
            vcfExists = true
        }
        def currentUser = springSecurityService.currentUser as AuthUser
        [seqSample: seqSample, genes: genes, defaultEmail: currentUser.email, vcfExists:vcfExists]
    }


    /***
     * change the gene mask for a sample & reload its variants ( +the variants of any replicates in its seqrun)
     * @param id of seqsample to be changed
     * @return  called by view, renders map of errors & messages
     */
    def updateGeneMask(Long id)
    {
        HashMap result = [:]

        SeqSample seqSampleInstance = SeqSample.get(id)
        Long version = params.version

        if (!seqSampleInstance) {
            result.error = "No Sequenced Sample"
            render result as JSON
            return
        }

        if (version != null) {
            if (seqSampleInstance.version > version) {
                result.error = "This gene mask has already been edited in another window"
                render result as JSON
                return
            }
        }

        //   clean user inputted gene mask
        //
        String geneMask = SeqSampleService.cleanGeneMask(request?.JSON?.geneMask)

        //   rerun Seqsample with new mask
        //
        try {
            result.messages = rerunSeqSampleWithNewMask(seqSampleInstance,geneMask,request?.JSON?.runbackground?true:false,request?.JSON?.email?:"")
        } catch (Exception e) {
            log.error(e)
            StackTraceUtils.sanitize(e).printStackTrace()
            result.error = "Pipeline re-run has failed due to exception: ${e}"
        }

        render result as JSON
    }






    /**
     * rerun pipelineLoadPathOS on a seqSample
     * @param ss
     * @return
     */
    private ArrayList<String> rerunSeqSampleWithNewMask( SeqSample ss , String geneMask, Boolean background, String notifyEmail) {
        log.info ("Rerunning seqsample")

        //Logger.getRootLogger().setLevel(Level.DEBUG)

        ArrayList<String> messages = []

        Set<SeqSample> seqSamples = [ss]
        Set<SeqSample> reviewedSamples = []
        Seqrun seqrun = ss.seqrun
        ss.relations.findAll { it.relation == "Replicate" }.each { relation ->
            relation.samples().each { sample ->
                if (sample.firstReviewBy && sample.seqrun.seqrun == ss.seqrun.seqrun)   reviewedSamples.add(sample)
                if (sample.seqrun.seqrun == ss.seqrun.seqrun )    seqSamples.add(sample)
            }
        }

        //  if samples have already been reviewed, refuse
        //
        if(reviewedSamples) {
            messages.push("Refusing to run pipeline since sample ${reviewedSamples.join(',')} has already undergone first review")
            return messages
        }

        //  if we don't have all the VCFs, refuse
        //
        for (sample in seqSamples) {
            File vcf = SeqSampleService.retrieveSampleVcf(seqrun.seqrun,sample.sampleName)
            if (!vcf.exists()) {
                messages.push("Could not find VCF file for sample ${sample} ${seqrun}, cannot run.")
                return messages
            }
        }

        //  remove all variants before rerunning PipelineLoadPathOS. do this with dbutil: could not get the grails/hql delete to flush in time for reloadSample
        //
        def dbu = new DbUtil()
        def db = new DbConnect( loc.pathosEnv )
        dbu.sql = db.sql()

        def dbl = new DbLock( loc.pathosEnv, 5 )

        //  first check if lock is set
        if (dbl.hasLock()) {
            //  returnmessage
            messages.push("Another load operation is underway, reloading variants is not currently possible. Please try again later.")
            return messages
        }

        //  aquire lock for seqvar deletion
        //
        def lockMap = dbl.setLock()


        //  change gene mask to new mask
        //changeGeneMask
        String oldGeneMask = ss.geneMask()? ss.geneMask().join(",") : ""
        try {
            SeqSampleService.changeGeneMask(ss, geneMask)
            log.debug("rerunSampleWithNewMask just changed gene mask to ${geneMask} for sample ${ss}")
            messages.push("Changed gene mask to ${geneMask} for sample ${ss}.")
        } catch(Exception e) {
            log.error("Got exception while trying to change gene mask: ${e}")
            log.error(StackTraceUtils.sanitize(e).printStackTrace())
            messages.push("Could not change gene mask, aborting.")
            dbl.clearLock(lockMap)
            return messages
        }

        //  delete all seqvariants
        //
        Boolean failedDelete = false
        try {
            for (seqSample in seqSamples) {
                Integer deleteCount = dbu.deleteSampleVariant(seqSample.seqrun.seqrun, seqSample.sampleName)
                log.info("Deleted ${deleteCount} variants from ${seqSample}")
            }
        }   catch (Exception e) {
            log.error("Got exception while trying to dbutil delete variants: ${e}")
            log.error(StackTraceUtils.sanitize(e).printStackTrace())

            messages.push("Could not delete variants, aborting.")

            if(e.toString().contains('Lock wait timeout exceeded')) {
                messages.push("Some process is trying to operate on this sample and it is not available right now. Please try again later.")
            }
            if(e.toString().contains('Deadlock found')) {
                messages.push("Deadlock found - another gene mask change and reload is currently underway. Please try again later.")
            }
            //  ROLL BACK TO OLD MASK
            SeqSampleService.changeGeneMask(ss, oldGeneMask)
            log.info("Rolled back gene mask to " + oldGeneMask)
            println("Rolled back gene mask to " + oldGeneMask)

            failedDelete = true
        } finally {
            dbl.clearLock(lockMap)  // PipelineLoadPathOS will have its own lock
            db = null   //force gc
            dbu = null  //force gc

            if(failedDelete)  return messages   //  abort if we failed
        }


        //  rerun PipelineLoadPathOS to reload all seqVariants
        //
        def response
        String audit_msg = ""

        if(background) {
            String userEmail = notifyEmail?.replaceAll(" ","")?.replaceAll( '[^A-Za-z0-9@\\-_\\.]', "" )
            SeqSampleService.reloadVariants(seqSamples,seqrun,true,userEmail)  //  runs in background thrad

            audit_msg = "Sample rerun began in background on SeqRun ${ss.seqrun} Samples ${seqSamples.join(",")}. You will recieve an email on ${userEmail} when complete."

            log.info("Running in background")
            println audit_msg
        } else {
            Integer loaded = SeqSampleService.reloadVariants(seqSamples, seqrun, false, '')  //  runs in real time, user waits

            //  AES if we change the below msg, pls check the editGeneMask gsp - we grep it to see if we're fail or success for colour
            //
            audit_msg = "Sample ${loaded == -1 ? " run kicked off in background " : " loaded ${loaded} variants "} on SeqRun ${ss.seqrun} Samples ${seqSamples.join(",")}"
        }

        AuditService.audit([
                category   : 'masking',
                seqrun     : ss.seqrun.seqrun,
                sample     : ss.sampleName,
                task       : 'Rerun Sample',
                description: audit_msg
        ])
        messages.push(audit_msg)


        return messages

    }


    def svList() {
        def result = 'fail'
        if ( params.id ) {
            def ss = SeqSample.get( params.id )

            def array = []
            ss.seqVariants.each{
                array.push([
                        variant: it,
                        tags: it.tags
                ])
            }

            result = array as JSON
        }
        render result
    }




    def reportBuilderInfo(Long id) {
        SeqSample ss = SeqSample.get( id );
        HashMap results = [
            caseComment : ss.patSample?.caseComment,
            curVariants : ss.seqVariants.findAll { it.reportable }.collect { it.allCurVariants() }
        ]

        render results as JSON
    }

    def geneInfo ( Long id ) {
        SeqSample ss = SeqSample.get( id )
        ArrayList<HashMap> results = []

        try {
        ArrayList<Gene> genelist = ss.geneList()

        println genelist

        genelist.each { Gene gene ->

            if(gene) {

            ArrayList<SeqVariant> seqVars = ss.seqVariants.findAll { sv ->
                sv.filterFlag.contains('pass') && sv.gene == gene.gene
            }

            ArrayList<HashMap> svInfo = []

            seqVars.each{ sv ->
                svInfo.push([
                    hgvsg: sv.hgvsg,
                    pos: sv.pos as Integer,
                    id: sv.id,
                    maxPmClass: sv.maxPmClass
                ])
            }

            Transcript transcript = Transcript.findByBuildAndPreferredAndGene("hg19", true, gene.gene)

            ArrayList<HashMap> exons = [];

            RefExon.findAllByRefseq(transcript.accession).each { refExon ->
                exons.add([
                    exon: refExon.exon,
                    start: refExon.exonStart,
                    end: refExon.exonEnd
                ])
            }

            HashMap info = [
                gene: gene.gene,
                svInfo: svInfo,
                start: transcript.cds_start,
                stop: transcript.cds_stop,
                size: transcript.cds_size,
                pathways: gene.pathways(),
                exons: exons,
                refSeq: transcript.accession
            ]

            results.push(info)
            }
        }

        } catch (Exception e) {
            results.error = e
        }

        render results as JSON
    }


    def pathwayInfo(Long id) {
        SeqSample ss = SeqSample.get( id );

        ArrayList results = []
        ArrayList<Pathway> pathways = Pathway.list();

        pathways.each { pathway ->
            HashMap info = [
                name: pathway.name,
                id: pathway.id,
                process: pathway.process,
                size: 1
            ]
            results.push(info)
        }

        render results as JSON

    }


    def save() {
        ArrayList warnings = []
        Seqrun sampleSeqrun = null
        Panel samplePanel = null
        PatSample samplePatSample = null

        if(params.isEmpty()) { redirect(action: "create") }

        if(params.sampleName?.contains(" ")) {
            warnings.add("Sample name cannot contain whitespace")
            params.sampleName = null
        }

        //  cast params to appropriate objects and generate warnings
        //
        if(params?.seqrun && params?.seqrun instanceof String) {
            String seqrunname = params.seqrun
            sampleSeqrun = Seqrun.findBySeqrun(seqrunname)
            if(!sampleSeqrun) {
                warnings.add("Could not find seqrun ${seqrunname}")
            }
         }

        if(params?.panel && params?.panel instanceof String) {
            String panelname = params.panel
            samplePanel = Panel.findByManifest(panelname)
            if(!samplePanel) {
                warnings.add("Could not find panel ${panelname}")
            }
         }

        if(params?.patSample && params?.patSample instanceof String) {
            String patsamplename = params.patSample
            samplePatSample = PatSample.findBySample(patsamplename)
            if(!samplePatSample) {
                warnings.add("Could not find patient sample ${patsamplename}")
            }
        }



        if(sampleSeqrun) {
            if(SeqSample.findBySeqrunAndSampleName(sampleSeqrun,params.sampleName)) {
                warnings.add("A Seq Sample with the sample name ${params.sampleName} already exists in this SeqRun")
                params.sampleName = ''
            }
        }

        if(warnings) {
            flash.message = warnings.join(" - ")
        }
        def currentUser = springSecurityService.currentUser as AuthUser



        Boolean saved = false
        SeqSampleService ssService = new SeqSampleService()
        SeqSample seqSampleInstance = ssService.makeNewSeqSample(sampleSeqrun,params.sampleName,samplePanel,currentUser.username,currentUser.email,params.analysis,params.laneNo)

        if (!warnings) {
            saved = seqSampleInstance.merge()   //  get a NonUniqueObjectException without this
        }

        if (!saved) {
            render(view: "create", model: [seqSampleInstance: seqSampleInstance])
            return
        } else {
            flash.message = message(code: 'default.created.message', args: [message(code: 'seqSample.label', default: 'SeqSample'), seqSampleInstance.sampleName])
            redirect(action: "show", controller: "seqrun" , id: seqSampleInstance.seqrun.id)
        }

    }


    def edit(Long id) {
        def seqSampleInstance = SeqSample.get(id)
        if (!seqSampleInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqSample.label', default: 'SeqSample'), id])
            redirect(action: "list")
            return
        }

        [seqSampleInstance: seqSampleInstance]
    }

    def update(Long id, Long version) {
        def seqSampleInstance = SeqSample.get(id)
        if (!seqSampleInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqSample.label', default: 'SeqSample'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (seqSampleInstance.version > version) {
                seqSampleInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'seqSample.label', default: 'SeqSample')] as Object[],
                        "Another user has updated this SeqSample while you were editing")
                render(view: "edit", model: [seqSampleInstance: seqSampleInstance])
                return
            }
        }

        seqSampleInstance.properties = params
        
        if(params.seqrun) {
            seqSampleInstance.properties.seqrun = Seqrun.findBySeqrun(params.seqrun)
        }

        if(params.panel) {
            seqSampleInstance.properties.panel = Panel.findByManifest(params.panel)
        }

        if (!seqSampleInstance.save(flush: true)) {
            render(view: "edit", model: [seqSampleInstance: seqSampleInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'seqSample.label', default: 'SeqSample'), seqSampleInstance.id])
        redirect(action: "show", id: seqSampleInstance.id)
    }

    /**
     * Render the genelist for a SeqSample
     * Perhaps check that the seqVariants are "pass"?
     * Perhaps add some safety here?
     */
    def genelist(Long id) {
        def seqSample = SeqSample.get(id)

        def genelist = SeqVariant.executeQuery("select sv.gene from SeqVariant sv where sv.seqSample=:ss group by sv.gene", [ss:seqSample]) ?: []

        render genelist as JSON
    }


    def sampleBasics(Long id) {
        SeqSample ss = SeqSample.get(id)
        Map results = [
            seqrun      : ss.seqrun.seqrun,
            sampleName  : ss.sampleName,
            seqVariants : SeqVariant.countBySeqSample(ss),
            patSample   : ss.patSample ? true : false,
            urn         : ss.patSample?.patient?.urn,
            billingCode : ss?.patSample?.patAssays.any { pa -> pa?.testSet } ? "true" : "false",
            billingCodes : ss?.patSample?.patAssays.collect { pa -> pa?.testSet },
            alignStats  : AlignStats.countBySeqrunAndSampleName( ss.seqrun.seqrun, ss.sampleName ),
            vcf         : SeqSampleService.retrieveSampleVcf(ss.seqrun.seqrun, ss.sampleName).exists()
        ]

        render results as JSON
    }





}


















