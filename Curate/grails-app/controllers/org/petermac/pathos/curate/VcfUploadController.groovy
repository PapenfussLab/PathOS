/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 */

package org.petermac.pathos.curate

import grails.util.Environment

import grails.util.GrailsUtil
import org.petermac.util.Locator
import org.petermac.util.RunCommand

/**
 * Created by seleznev andrei on 16/09/2015.
 */
class VcfUploadController {

    def springSecurityService

    //  Application locator
    //
    def loc  = Locator.instance

    def upload() {
        def env =  grails.util.Environment.getCurrentEnvironment().name
        render(view: 'upload', model: [userparams: null, env: env])

    }

    def saveupload()
    {
        def env = grails.util.Environment.getCurrentEnvironment().name
        def currentUser = springSecurityService.currentUser as AuthUser
        def webroot = servletContext.getRealPath('/')

        String uploadpath = webroot + 'payload/vcf_upload/'

        def f = request.getFile('vcfUpload')

        if (f.empty) {
            flash.message = 'File cannot be empty'
            render(view: 'upload', model: [userparams: params,  env: env])

            return
        }

        if (env == 'pa_prod') {
            if  (!(currentUser.authorities.any { it.authority == "ROLE_ADMIN" })) {
                flash.message = 'Only administrator users can upload a file on this system'
                render(view: 'upload', model: [userparams: params,  env: env])

                return
            }
        }

        def now = new Date()
        def timeStamp= now.format("yyMMdd'T'HHmmss")
        def originalextension

        //  validate vcf
        //
        String originalfilename = f.getOriginalFilename()
        if (originalfilename.lastIndexOf('.') == -1)
        {
            //no dot
            originalextension = ''
        }
        else
        {
            originalextension = originalfilename.substring(originalfilename.lastIndexOf('.'), originalfilename.length())
        }

        if (originalextension.toLowerCase() != '.vcf')
        {
            flash.message = "File ${originalfilename} doesn't have a VCF extension"
            render(view: 'upload', model: [userparams: params])
            return
        }

        //  check that the filename only has alphanumeric chars & whitespace
        //
        if (originalfilename.replaceAll(" ","") != originalfilename.replaceAll("[^0-9_a-zA-Z\\(\\)\\%\\-\\.]", ""))
        {
            flash.message = 'Filename must be alphanumeric'
            render(view: 'upload', model: [userparams: params])
            return
        }

        String newfilename = originalfilename + '.' + timeStamp
        def outFile = new File(uploadpath + newfilename)
        f.transferTo(outFile)

        String seqrunName = ""
        if (params?.seqrun) {
            seqrunName = stripNonAlphanum(params.seqrun)
        }

        String panelName = ""
        if (params?.panel) {
            panelName = stripNonAlphanum(params.panel)
            Panel thisPanel = Panel.findByManifest(panelName)
            if ( ! thisPanel ) {
                flash.message = 'No such panel: ' + panelName
                render(view: 'upload', model: [userparams: params,  env: env])
                return
            }
        }

        //  Validate Seqrun
        //
        if (!seqrunName) {
            flash.message = 'You must specify a seqrun'
            render(view: 'upload', model: [userparams: params,  env: env])
            return
        }

        //  Check it exists
        //
        Seqrun thisSeqrun = Seqrun.findBySeqrun(seqrunName)

        //  seqrun must exist if we're on production, otherwise free form
        //
        if ( env == 'pa_prod' )
        {
            if ( thisSeqrun?.seqrun ) {
                seqrunName = thisSeqrun.seqrun
            } else {
                flash.message = 'No such seqrun: ' + seqrunName
                render(view: 'upload', model: [userparams: params,  env: env])
                return
            }
        }

        //  Construct the commands to perform upload
        //
        //todo we direly need to call vcfloader directly and not from a shell
        String shellcommand =   "export PATH=${loc.pathos_home}/bin:\$PATH && export PATHOS_CONFIG=/pathology/NGS/${env}/etc/pathos.properties && " +
                                "VcfLoader --rdb ${env} --columns ${loc.pathos_home}/etc/vcfcols.txt"

        if ( params?.filterFlag ) { shellcommand += " --filter" }
        if ( seqrunName         ) { shellcommand += " --seqrun ${seqrunName}" }
        if ( panelName          ) { shellcommand += " --panel  ${panelName}" }
        shellcommand += " " + outFile.path

        println "#### panelName=${panelName}\nparams=${params}\n${shellcommand}"
        Map responseMap = new RunCommand( shellcommand ).runMap()

        //todo in the future we ARE letting them upload SeqRun and SeqSample if they dont exist

        def output = ""
        def outerr = ""
        if (responseMap?.stdout)
        {
            output = responseMap?.stdout?.encodeAsHTML()?.replaceAll('\n', '<br/>\n')
        }
        if (responseMap?.stderr)
        {
            outerr = responseMap?.stderr?.encodeAsHTML()?.replaceAll('\n', '<br/>\n')
        }

        def outex =  responseMap?.exit

        //todo check for success by exit code and save only

        if (outex.toInteger() == 0)
        {
            VcfUpload thisUpload = new VcfUpload(user: currentUser, dateCreated: now, filePath: outFile.getPath(), seqrun: thisSeqrun).save(flush: true, failOnError: false)
            println "Saved vcfupload record"
            println thisUpload
        }


        render view: 'uploadsuccess', model: [stdout: output, stderr: outerr, exitval: outex, command: shellcommand]
    }

    /**
     * Sanitise user input - we're running this in shell!
     *
     * @param   input   String to clean
     * @return          Cleaned String
     */
    def stripNonAlphanum(String input)
    {
        return input.replaceAll( "[^A-Za-z0-9*-_]", "" )
    }
}




