/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 */

package org.petermac.pathos.curate

import grails.util.GrailsUtil
import org.petermac.util.Locator
import org.petermac.util.RunCommand

/**
 * Created by seleznev andrei on 16/09/2015.
 */
class VcfUploadController {

    def springSecurityService


    def upload() {
        def env =  GrailsUtil.environment
        render(view: 'upload', model: [userparams: null, env: env])

    }

    def saveupload() {
        def env =  GrailsUtil.environment
        def currentUser = springSecurityService.currentUser as AuthUser
        def webroot = servletContext.getRealPath('/')

        String uploadpath = webroot+'payload/vcf_upload/'
//        uploadpath = '/pathology/tmp/'   //put stuff here for now?

        //check if it exists? f.transferTo(new File(uploadpath + f.getOriginalFilename()))

        def f = request.getFile('vcfUpload')
        if (f.empty) {
            flash.message = 'File cannot be empty'
            render(view: 'upload', model: [userparams: params,  env: env])

            return
        }

        if (env == 'pa_prod') {
            if  (!(currentUser.authorities.any { it.authority == "ROLE_ADMIN" })) {
                flash.message = 'Only administrator users can upload a fuile on this enviornment'
                render(view: 'upload', model: [userparams: params,  env: env])

                return
            }
        }


        def now = new Date()
        def timeStamp= now.format("yyyyMMdd'T'HHmmss.SSS")
        def originalextention

        //validate vcf
        //

        String originalfilename = f.getOriginalFilename()
        if (originalfilename.lastIndexOf('.') == -1) {
            //no dot
            originalextention = ''
        } else {
            originalextention = originalfilename.substring(originalfilename.lastIndexOf('.'), originalfilename.length())
        }

        if (originalextention.toLowerCase() != '.vcf') {
            flash.message = 'File must have a VCF extension'
            render(view: 'upload', model: [userparams: params])
            return
        }


        //check that the filename only has alphanumeric chars & whitespace
        //
        if (originalfilename.replaceAll(" ","") != originalfilename.replaceAll("[^0-9_a-zA-Z\\(\\)\\%\\-\\.]", "")) {
            flash.message = 'Filename must be alphanumeric'
            render(view: 'upload', model: [userparams: params])
            return
        }


        String newfilename = timeStamp + "_" + originalfilename
        def outFile = new File(uploadpath + newfilename)
        f.transferTo(outFile)

        String seqSampleName = ""
        if (params.seqsamplename) {
            seqSampleName = stripNonAlphanum(params.seqsamplename) //sanitise user input - we're running this in shell!
        }

        if (!seqSampleName) {
            flash.message = 'You must specify a seqsample'
            render(view: 'upload', model: [userparams: params,  env: env])
            return
        }

        SeqSample thisSeqSample = SeqSample.findBySampleName(seqSampleName)

        if (!thisSeqSample && params.seqsamplename) {
            flash.message = 'No such SeqSample: ' + seqSampleName
            render(view: 'upload', model: [userparams: params ,  env: env])
            return
        }

        String queue = ""
        if (params.queue) {
            queue = stripNonAlphanum(params.queue) //sanitise user input - we're running this in shell!
        }

        String seqrunName = ""
        if (params?.seqrun) {
            seqrunName = stripNonAlphanum(params.seqrun) //sanitise user input - we're running this in shell!
        }

        if (!seqrunName) {
            flash.message = 'You must specify a seqrun'
            render(view: 'upload', model: [userparams: params,  env: env])
            return
        }

        Seqrun thisSeqrun = Seqrun.findBySeqrun(seqrunName)

        if (thisSeqrun?.seqrun) {
            seqrunName = thisSeqrun.seqrun
        } else {
            flash.message = 'No such seqrun: ' + seqrunName
            render(view: 'upload', model: [userparams: params,  env: env])
            return
        }

        //now we must make a seqrun tsv file and write metadata to it to prepeare for runpipe mp_vcfamplicon
        //
        def seqrunTsvPath = uploadpath + "VCFUpload_Seqrun_" + timeStamp + ".tsv"
        def seqrunTsvFile = new File(seqrunTsvPath)

        //write header
        //
        String line = '#seqrun\tsample\tpanel\tpipeline\tpipein\toutdir'
        seqrunTsvFile << line + '\n'

        //write the one line
        //
        String panelName = "vcfUpload" //todo allow user to specify, eventually
        String pipelineName = 'mp_vcfAmplicon'

        //todo testing
        Locator loc = Locator.instance
        String samplesRoot = loc.samDir //'/pathology/NGS/Samples/Testing/'

        String outputPath =  samplesRoot + '/' + seqrunName + '/' + seqSampleName

        line = seqrunName + '\t' + seqSampleName + '\t' + panelName + '\t' + pipelineName + '\t' + outFile.getPath() + '\t' + outputPath
        seqrunTsvFile << line


        //now we can call runtpipe
        //todo do we set pathos home or other env vars?
        //
        String runpipeHome = loc.pathos_home + "/bin/"

        String shellcommand = "sudo -u bioinf " + runpipeHome + "RunPipe -b -p mp_vcfAmplicon -r ${env}"
        if (seqrunName) { shellcommand += " -s ${seqrunName}" }
        if (seqSampleName) { shellcommand += " -a ${seqSampleName}" }
        shellcommand += " ${seqrunTsvPath}"



        def responseMap = [:]
        if (env == 'pa_local') {    //do not run Runpipe on PA LOCAL
            responseMap.stdout = 'You are on a local environment. Vcf upload functionality relies on the PathOS pipeline and will not be kicked off.\nCommand that would be ran:\n'+shellcommand
        } else if  (!seqrunTsvFile.isFile()) {
            responseMap.stdout = 'Unable to create metadata CSV file. Please create a JIRA issue and let us know of this problem.'
        } else {    //run it
            responseMap = new RunCommand( shellcommand ).runMap()
        }


        //todo in the future we ARE letting them upload SeqRun and SeqSample if they dont exist

        //todo save the upload on success only:
        //VcfUpload thisUpload = new VcfUpload(user: currentUser, dateCreated: now, filePath: outFile.getPath(),seqrun: thisSeqrun, seqsample: thisSeqSample).save(flush:true,failOnError:false)

        //what this does
        def output = ""
        def outerr = ""
        if (responseMap?.stdout) {
            output = responseMap?.stdout.encodeAsHTML().replaceAll('\n', '<br/>\n')
        }
        if (responseMap?.stderr) {
            outerr = responseMap?.stderr.encodeAsHTML().replaceAll('\n', '<br/>\n')
        }

        def outex =  responseMap?.exit

        def metadatafile =''
        if (seqrunTsvFile?.getPath()) {
            metadatafile = seqrunTsvFile.getPath()
        } else {
            metadatafile = 'Unable to create seqrun tsv. Attempted: ' + seqrunTsvPath
        }

        render view: 'uploadsuccess', model: [stdout: output, stderr: outerr, exitval: outex, command: shellcommand, metadataFile: metadatafile]

        //get form
        //upload file
//        render view: 'upload'
    }

    def stripNonAlphanum(String input) {
        return input.replaceAll("[^A-Za-z0-9*-_]", "")
    }




}




