package org.petermac.pathos.curate

import org.apache.commons.io.FileUtils
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import org.petermac.util.Locator
import org.petermac.util.RunCommand
import org.petermac.util.Vcf
import grails.converters.JSON
import groovy.json.JsonSlurper
import org.petermac.pathos.curate.VcfAnomaly
import java.nio.file.Files
import java.nio.file.Paths

import java.util.concurrent.*
import javax.annotation.*
/**
 * Created by seleznev andrei on 16/09/2015.
 */
class VcfUploadController {

    def springSecurityService
    def Locator
    def UploaderService
    def SeqSampleService
    //  Application locator
    //
    static Locator loc  = org.petermac.util.Locator.instance
    static final CUSTOM_PANEL_MESSAGE = "Specify panel" //text and selectfield value for user-specified panel (as opposed to a dropdown with seqrun's panels
    static final BASH_EXPORT_PATH_CONFIG = "export PATH=${loc.pathos_home}/bin:\$PATH && export PATHOS_CONFIG=${loc.pathos_home}/etc/pathos.properties && "
    static final MUTHOST = loc.mutalyzer
    static final boolean validateVcfGATK = false

    /**
     * closure for dropzone js upload validation
     **/
    static Closure validateClosure = { File file ->
        return validateVcfFileList([file])
    }

    /**
     * upload file from dropzone, validate it, and render appropriate json response. called from dropzone js
     * @return
     */
    def fileupload() {
        //  perform upload by calling UploadService
        //
        def f = request.getFile('fileupload')
        def now = new Date()
        def timeStamp = now.format("yyMMdd'T'HHmmss")
        String newfilename = f.getOriginalFilename().replaceAll(" ","").replaceAll(".vcf","") + '.' + timeStamp + ".vcf"
        String uploadDir = servletContext.getRealPath('/') + 'payload/vcf_upload/'
        String uploadDest = uploadDir + newfilename

        //  upload and validate uploaded file
        //  validateClosure needs to be a closure that performs your server side validation with returnvalue.errors a List of errors
        //
        UploadResult upload = uploaderService.upload(f,uploadDest,validateClosure)

        //  service will return appropriate json response with code based on results
        //
        render uploaderService.uploadResponse(upload)
    }


    /**
     * upload page, with dropzone uploader and process vcf upload submission form
     * @return
     */
    def upload() {
        def env =  loc.pathosEnv
        def currentUser = springSecurityService.currentUser as AuthUser

        boolean uploaded = false
        def loadresult
        if (params.process && params.uploadedFilesJson) {    // submit button clicked, so process the files uploaded through dropzone & call vcfloader
             loadresult = performVcfLoad(params)
             if (loadresult.uploads) {
               redirect(action: 'uploadsuccess', params: [uploadedFiles:loadresult.uploads,messages:loadresult.messages])   // we've successfully kicked off load
             }
        }

        def panelList
        if(params?.panellist) {
            panelList = params.panelList
        }
        else {  panelList = [CUSTOM_PANEL_MESSAGE]
        }

        render(view: 'upload', model: [defaultEmail: currentUser.email, env: env, panelList: panelList, errorMsg: loadresult?.error])
    }

    def uploadsuccess() {
        render(view: 'uploadsuccess', model: [uploads: params.uploadedFiles, messages: params.messages])
    }

    /**
     * validate parameters for vcfupload , get parameters for VcfLoader
     * @param params upload parameters
     * @return parsed upload parameters
     */
    Map setupVcfLoad(params) {

        //  Validate Seqrun
        //
        String seqrunName = cleanString(params.seqrun)
        if(!seqrunName) {
            return ['error': "Invalid seqrun name."]
        }

        //  Validate Gene Mask:
        //
        SeqSampleService ssService = new SeqSampleService()
        String geneMask = ""
        if(params.genemask) {
            geneMask = ssService.cleanGeneMask(params.genemask)
        }


        //  Check seqrun exists
        //
        boolean createSeqrun = false
        Seqrun thisSeqrun = Seqrun.findBySeqrun(seqrunName)
        if(!thisSeqrun) {
            createSeqrun = true
        }


        //  Deal with uploaded files
        //
        ArrayList<File> fileList = []
        try {
            def fileUploadPathList = new JsonSlurper().parseText(params.uploadedFilesJson)

            fileUploadPathList.each { it ->
                fileList.push(new File(it.toString()))
            }
        } catch (Exception e) {
            log.info("Error in parsing list of uploaded files: ${e}")
            return ['error': "Error in parsing list of uploaded files."]
        }


        //  validate file list. this is for security, validate function would have already been called for each file upload in a normal workflow
        //
        Map validFiles = validateVcfFileList(fileList)
        ArrayList uploadedFiles = validFiles.validFiles


        if(! (validFiles?.errors?.isEmpty())) {
            flash.message = validFiles.errors.join(".")
            log.info("Errors in uploaded VCFS: ${validFiles.errors}")
            println("Errors in uploaded VCFS: ${validFiles.errors}")
            return ['error':"Errors in uploaded VCFS: ${validFiles.errors}"]
        }

        if (fileList.empty) {
            return ['error':"File cannot be empty"]
        }


        //  Validate Gene Mask: if we HAVE an existing sample and seqrun, already with variants,
        //  We do not let new variant with mask be loaded.
        //  Here we also make sure we don't have two seqSamples with the same name going in with the same name...
        //
        String customError = ""
        List allSampleNames = []
        fileList.each { it ->
            String sampleName = new Vcf(it).sampleName()

            if (allSampleNames.contains(sampleName)) {
                customError = "You are uploading two VCFs that have the same sample name. You cannot upload two VCFs for the same sample at the same time."
            } else {
                allSampleNames.add(sampleName)
            }

            SeqSample existingSample
            if(thisSeqrun) {
                existingSample = SeqSample.findBySampleNameAndSeqrun(sampleName, thisSeqrun)
                if(existingSample && existingSample?.seqVariants?.size() > 0 && params.genemask.trim() != '') {
                    if(SeqSampleService.cleanGeneMask(params.genemask) != SeqSampleService.cleanGeneMask(existingSample.geneMask().join(','))) {
                        //  do not let user to change gene mask on existing sample
                        customError = "You set a custom gene mask but sample ${existingSample} has variants and a different gene mask already set."
                    }
                }
            }
        }

        if (customError) {
            return ['error':customError]
        }

        def now = new Date()
        def timeStamp = now.format("yyMMdd'T'HHmmss")

        String panel = params.panellist
        if (   params.panellist == CUSTOM_PANEL_MESSAGE ) {     //  get custom panel from input if its custom
            panel = params.custompanel
        }

        Panel p = Panel.findByManifest(cleanString(panel))

        if(!panel || !p) {
            String panelmsg = panel?"Could not find panel with manifest: ${panel}":"You must specify a panel manifest"
            return ['error':panelmsg]
        }

        //  By now our vcf files are valid.

        //  do all creation in a session
        //  create seqrun first if needed
        //
        ArrayList<SeqSample> createdSeqSamples = []

        SeqSample.withSession {  session ->

            if (createSeqrun) {
                thisSeqrun = new Seqrun(seqrun: seqrunName, passfailFlag:false, authorisedFlag:false,runDate: new Date())
                thisSeqrun.save(flush:true,failOnError:true)
                log.info("Created new seqrun " + thisSeqrun)
                session.flush()
            }

            //  create seqSamples for the vcfs if they dont exist
            //
            createdSeqSamples = createSeqSamplesForLoadIfNeeded(fileList,thisSeqrun,p)

            session.flush()
            session.clear()
        }

        //  construct list of all seqsamples we're loading in
        ArrayList<SeqSample> allAffectedSeqSamples = []
        fileList.each { it ->
            String sampleName = new Vcf(it).sampleName()
            def processedSample = SeqSample.findBySampleNameAndSeqrun(sampleName,thisSeqrun)

            if (!processedSample) {
                String errormsg = "Error, processing VCF Upload for ${sampleName} ${thisSeqrun} but this sample does not exist"
                log.warn(errormsg)
                return ['error':errormsg]
            }

            allAffectedSeqSamples.add(processedSample)
        }

        //  set gene masks for seq samples we are loading into
        //
        if(geneMask) {
            for (ss in allAffectedSeqSamples) {
                SeqSampleService.changeGeneMask(ss, geneMask)
            }
        }

        //  Construct the command to perform upload
        //
        boolean filter = false // was --filter
        if (params?.filterFlag) {
            filter = true
        }

        HashMap uploadParams = [:]  //  parameters for upload function

        uploadParams.panel =  cleanString(panel)
        uploadParams.filter = filter
        uploadParams.seqrunName = seqrunName
        uploadParams.uploadedFiles = uploadedFiles
        uploadParams.createdSeqSamples = createdSeqSamples
        uploadParams.isSeqrunCreated = createSeqrun
        uploadParams.allSeqSamples = allAffectedSeqSamples
        uploadParams.outputPath = servletContext.getRealPath('/')+"/payload/vcf_upload_out_${timeStamp}"  //  will redirect stdout/err here
        uploadParams.userEmail =  params?.email?.replaceAll(" ","")?.replaceAll( '[^A-Za-z0-9@\\-_\\.]', "" )

        return uploadParams

    }

    /**
     *
     * for a list of vcf files, create new seqsamples for the sample name in each file if those seqsample doesnt exist
     * @param fileList list of vcf Files
     * @param seqrun seqrun object for new seqsamples to belong to
     * @param panel panel object for new seqsamples to belong to
     * @return list of created samples
     */
    private ArrayList<SeqSample> createSeqSamplesForLoadIfNeeded(ArrayList<File> fileList, Seqrun seqrun, Panel panel) {
        ArrayList newSamples = []
        def currentUser = springSecurityService.currentUser as AuthUser
        //def sr = Seqrun.findBySeqrun(seqrun.seqrun)
        //def pl = Panel.findByManifest(panel.manifest)

            SeqSampleService ssService = new SeqSampleService()
            fileList.each { f ->
                String sampleName = new Vcf(f).sampleName()

                if (!sampleName) {
                    log.error("ERROR: No sample name in file ${f}")
                    //return errors
                } else if (!SeqSample.findBySeqrunAndSampleName(seqrun, sampleName)) {
                        def ss = ssService.makeNewSeqSample(seqrun,sampleName,panel,currentUser.username,currentUser.email,'VcfLoader','unknown')

                        def saved = ss.merge()  //merge instead of save avoids NonUniqueObjectException
                        newSamples.add(ss)

                    }

                    log.info("Created new sample " + sampleName + " " + seqrun.seqrun)

                }


        return newSamples
    }

    /**
     * called by frontend: for a given seqrun, render the name of the panel w/ the most samples in that run
     * @param seqrunName
     * @return
     */
    def suggestPanelForSeqrun( String seqrunName )
    {
        Seqrun sr = Seqrun.findBySeqrun(seqrunName)

        if( ! sr ) {
            render "noseqrun"
        }
        else if( sr?.mostCommonPanel()) {
            String largestPanelName = sr?.mostCommonPanel()?.toString()
            render largestPanelName
        }
    }


    /**
     * called by frontend: check if panel manifest exists
     * @param manifest
     * @return
     */
    def checkPanelExists(String manifest) {
        render "${Panel.findByManifest(manifest)}"
    }

    def checkSeqrun(String seqrunName) {
        Seqrun sr = Seqrun.findBySeqrun(seqrunName)
    }

    /**
     * called by frontend: populate Panels dropdown box
     */
    def updatePanelsForSeqrun = {
        def seqrun = params['seqrunName'].toString().trim()

        Seqrun sr = Seqrun.findBySeqrun(seqrun)
        ArrayList panelList = []

        if(sr) {
            String panels = sr?.panelList
            panelList = panels?.contains(',') ? panels.tokenize(',') : [panels]
        }

        //  add option for "New Panel"
        panelList.add(CUSTOM_PANEL_MESSAGE)
        render(template: 'panellist', model: [panelList: panelList])
    }


    /**
     * validates a list of just-uploaded files for vcfupload and returns a list of paths of validated files
     * note we always want to do this on VCF processing form submission, after dropzone upload validation,
     * for security reasons (we also do this on dropzone upload validation to provide quicker feedback to user)
     *
     * @param fileList a List of uploaded vcf Files for validation
     * @return a map with a list of errors, and a List of file paths of the valid vcfs
     */
    static private HashMap<String,List> validateVcfFileList(ArrayList<File> fileList) {

        List<String> validFiles = []
        List<String> errors = []

        fileList.each { f ->
            //  validate vcf
            //
            String originalfilename = f.getName()
            String originalextension = ''

            if (originalfilename.lastIndexOf('.') != -1) {
                originalextension = originalfilename.substring(originalfilename.lastIndexOf('.'), originalfilename.length())
            }

            if (originalextension.toLowerCase() != '.vcf') {
                errors.add("File ${originalfilename} doesn't have a VCF extension")

            }

            //  check that the filename only has alphanumeric chars & whitespace
            //
            if (originalfilename != originalfilename.replaceAll("[^0-9_a-zA-Z\\(\\)\\%\\-\\.]", "")) {
                errors.add("Filename must be alphanumeric and have no whitespace: ${originalfilename}")

            }

            //  check sample has sample name
            def sampleName = new Vcf(f).sampleName()
            if (!sampleName) {
                errors.add('Could not find sample name in VCF - sample name expected to appear as last element in header line')
            }

            if (validateVcfGATK) {
                // Validate each VCF with GATK
                // org.petermac.util.GATK's commandline gatk does not work from Curate - something wrong w the build
                // we run GATK manually in commandline, which is what that class essentially does anyway
                //
                //String validateCommand = BASH_EXPORT_PATH_CONFIG + "GATK --validationTypeToExclude ALL -v '${ f.getAbsolutePath() }'"
                String validateCommand = BASH_EXPORT_PATH_CONFIG + "GATK -c -T ValidateVariants -V '${f.getAbsolutePath()}' -S LENIENT --validationTypeToExclude ALL"

                def ret = new RunCommand(validateCommand).runMap()
                println "Running ${validateCommand}"
                println "Return:"
                println ret
                log.info(ret)
                if (!ret.stdout.contains('records with no failures.')) {
                    errors.add("VCF file ${originalfilename} failed validation, please ensure it is not malformed")
                } else {
                    validFiles.add(f.path)
                }
            } else {
                validFiles.add(f.path)
            }
        }

        def retMap = new HashMap<String,List>()

        retMap.errors = errors
        retMap.validFiles = validFiles
        return retMap
    }

    /**
     * call vcfloader on uploaded vcfs. validates first.
     * return list of all files uploaded  if vcfloader kicked off in background , empty list if we fail validation
     *
     * @return
     */
    Map performVcfLoad(uploadParams)
    {
        Map loadparams = setupVcfLoad(uploadParams)
        //return ['error':"debug ${SeqSample.findBySampleNameAndSeqrun('NEWSAMPLE',Seqrun.findBySeqrun('181101_M01053_0796_000000000-C6BRY')).geneMask()}"]

        if(loadparams.error) {
            flash.message = loadparams.error
            println "Error in upload: "
            println loadparams.error
            return ['error':loadparams.error]
        }
        else {
            callUpload(loadparams)

            //  construct messages for user
            //
            ArrayList messages = []
            if (loadparams.isSeqrunCreated) {
                messages.add("Created new Seqrun ${loadparams.seqrunName}")
            }
            for( newSs in loadparams.createdSeqSamples ) {
                messages.add("Created new SeqSample ${newSs} in SeqRun ${loadparams.seqrunName}")
            }
            for(ss in loadparams.allSeqSamples) {
                messages.add("Submitted VCF for loading into SeqSample ${ss.sampleName} ${ss.seqrun.seqrun}")
            }
            messages.add("VCF Loading is in progress. You will receive an email at ${loadparams.userEmail} once it is complete.")

            return [uploads: loadparams.uploadedFiles, messages: messages]

        }

    }

    void callUpload(Map loadparams) {
        switch( loc.vcfUploadMethod.trim() ) {
            case "VcfLoader":
                performVcfLoaderUpload(loadparams)
                break;
            default:
                performAnomalyUpload(loadparams)
                break;

        }
    }

    /**
     * perform upload with vcfupload by loading the appropriate shell command and running in BG with a mail for results
     * @param loadparams
     */
    void performVcfLoaderUpload(loadparams) {

        log.info("Performing Vcf Loader Upload")
        SeqSampleService ssService = new SeqSampleService()

        ExecutorService executor = Executors.newSingleThreadExecutor()
        String link = new ApplicationTagLib().createLink(controller: "SeqVariant", action: "svlist", absolute: true)

        executor.execute {
            def nrows = ssService.performVcfLoaderLoad(loc.pathosEnv, MUTHOST, loadparams.seqrunName, loadparams.uploadedFiles.toSet(), loadparams.filter, loadparams.panel.toString(), '', false)
            log.info("Loaded ${nrows} variants")

            ArrayList<File> uploads = []
            for (String filepath in loadparams.uploadedFiles) {
                uploads.add(new File(filepath))
            }
            mailUserUploadResult(uploads,Seqrun.findBySeqrun(loadparams.seqrunName),loadparams.userEmail,link)

        }

        executor.shutdown()
    }

    //  todo pathos-4116 this should be in seqsampleservice
    /**
     * perform an upload with VcfAnomaly
     * @param loadparams
     */
    // String seqrunName List<String> uploadedfiles, Boolean filter, String panel, String userEmail
    void performAnomalyUpload(loadparams) {
        log.info("Performing anomaly upload")
        ExecutorService executor = Executors.newSingleThreadExecutor()
        Seqrun seqrun = Seqrun.findBySeqrun(loadparams.seqrunName)
        //Logger.getRootLogger().setLevel(Level.DEBUG)

        String link = new ApplicationTagLib().createLink(controller: "SeqVariant", action: "svlist", absolute: true)

        executor.execute {
            ArrayList<File> uploads = []
            VcfAnomaly anomaly = new VcfAnomaly()

            for (String filepath in loadparams.uploadedFiles) {
                uploads.add(new File(filepath))
            }

            log.info("Processing " + uploads)
            Integer processed = anomaly.processFiles(seqrun, uploads, loadparams.panel.toString() ?: '')

            log.info("Finished processing ${processed} files")

            if (loadparams.filter) {
                log.info("Filter set, running filter")
                anomaly.applyFilter(loc.pathosEnv)
            } else {
                log.info("Filter not set, not running filter")
            }



            mailUserUploadResult(uploads,seqrun,loadparams.userEmail,link)

            log.info("Thread reached the end.")

        }
        executor.shutdown()


    }

    /**
     * mail the user an upload resutl from a seqrun and a list of uploaded files
     * used for performAnomalyUpload
     *
     * @param uploads
     * @param seqrun
     * @param userEmail
     */
    static public void mailUserUploadResult( ArrayList<File> uploads, Seqrun seqrun, String userEmail, String baseLink) {
        //  now mail the user
        String ls = System.getProperty("line.separator")   //bash line seperator +
        String emailText = "Anomaly finished processing uploads for seqrun ${seqrun.seqrun}" + ls
        SeqSample loadedSS
        for(vcfFile in uploads) {
            String sampleName =  new Vcf(vcfFile).sampleName()
            loadedSS = SeqSample.findBySampleNameAndSeqrun(sampleName,seqrun)
            String link = baseLink + '/' + loadedSS.id
            if(loadedSS) {
                emailText = emailText + "Loaded Seqrun ${seqrun} Sample ${sampleName} which now has ${SeqVariant.countBySeqSample(loadedSS)} SeqVariants. ${link}"  + ls
            } else {
                emailText = emailText + "Seq Sample ${sampleName} was not loaded  " + ls
            }
        }

        File emailFile = File.createTempFile("emailTextVcfUpload",".tmp")
        Logger.getRootLogger().setLevel(Level.INFO)
        emailFile.write(emailText)


        String shellcommand = "mail -s 'Vcf Upload Completed' ${userEmail} < '${emailFile.getAbsolutePath()}'"
        //log.info("RAN " + shellcommand)
        println(shellcommand)
        Map responseMap = new RunCommand(shellcommand).runMap(true)   //  this spawns a new thread, does not run in bg

    }

    /**
     * Sanitise user input - we're running some of this in shell!
     *
     * Used to sanitise seqrun names and panel manifest names.
     * Should only allow characters usable in folder names.
     *
     * Note: this should probably be a service or utility, not a controller method.
     * Note: this is stripping () and spaces, which are currently used in some Panel Manifests.
     *
     * @param   input   String to clean
     * @return          Cleaned String
     */
    private def cleanString(String input)
    {
        if(!input) return ""
        return input.replaceAll( '[^-._A-Za-z0-9]', "" )
    }



}




