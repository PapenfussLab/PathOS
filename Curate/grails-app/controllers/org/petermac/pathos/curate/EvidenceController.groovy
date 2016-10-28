/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.util.GrailsUtil
import org.petermac.util.JiraNotifier

import java.text.MessageFormat

class EvidenceController
{
    def evidenceService
    def SpringSecurityService
    static allowedMethods = [ update: "POST" ]

    def edit(Long id)
    {
        def variantInstance = CurVariant.get(id)
        log.info("In evd edit: ${variantInstance}")

        if (!variantInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'variant.label', default: 'CurVariant'), id])
            redirect( controller: "curVariant", action: "list")
            return
        }

        [variantInstance: variantInstance, evidenceInstance: variantInstance.evidence]
    }

    def update(Long id, Long version)
    {
        def currentUser = springSecurityService.currentUser as AuthUser
        def variantInstance = CurVariant.get(id)
        log.info("In evd update: ${variantInstance} id=${id}")

        if (!variantInstance)
        {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'variant.label', default: 'CurVariant'), id])
            redirect( controller: "curVariant", action: "list")
            return
        }

        if (version != null)
        {
            if (variantInstance.version > version)
            {
                variantInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'variant.label', default: 'CurVariant')] as Object[],
                        "Another user has updated this CurVariant while you were editing")
                render(view: "edit", model: [evidenceInstance: variantInstance.evidence])
                return
            }
        }

        //  Update evidence parameters and calculate evidence classification
        //
        def prevClass = variantInstance.pmClass
        variantInstance.evidence.properties = params
        variantInstance.evidence.evidenceClass = evidenceService.inferClass( variantInstance.evidence )
        variantInstance.pmClass = variantInstance.evidence.evidenceClass

        //jira notifier to molpath ops
        if ( prevClass != variantInstance.evidence.evidenceClass )
        { // Create notifier

            def jnotifier = new JiraNotifier()
            def basepath
            def isTest = true
            switch (GrailsUtil.environment) {
                case ['pa_prod']:
                    basepath = 'http://bioinf-pathos:8080/PathOS'
                    isTest = false
                    break;
                case ['pa_uat','pa_stage','pa_dev']:
                    basepath = 'http://bioinf-pathos-test:8080/PathOS'
                    break;
                default:
                    basepath = 'http://localhost:8080/PathOS'
                    break;
            }

            def varlist = ''


            def valout = "Triggered by ${currentUser.getDisplayName()} (${currentUser.getUsername()}) ${currentUser.getEmail()} " + "\n" + "\n"
            valout = valout + "CurVariant ${variantInstance} (HGVSP ${variantInstance.hgvsp}) changed evidence class to ${variantInstance.evidence.evidenceClass} from ${prevClass}. It is present in the following seqsamples:"
            if (isTest) {
                valout = "TEST! NOT A REAL ISSUE! ${valout}"
            }

            def maxLinks = 20

            //build a string of hyperlinks but cap it at a certain amount
            def seenSeqSamples = []
            def seqvars = SeqVariant.findAllByCurated(variantInstance)
            def counter = 0
            //sort seqvars so we get the latest first

            seqvars = seqvars.sort{ -it.id }

            for ( seqv in seqvars ) {
                def seqsamp = SeqSample.findById(seqv.seqSampleId)
                if (seqsamp) {
                    if (!seenSeqSamples.contains(seqv.seqSampleId)) {
                        if (counter < maxLinks) {
                            varlist = varlist + "\n" + "${seqsamp.sampleName} ${basepath}/seqVariant/svlist/${seqsamp.id}"
                        }
                        counter = counter + 1
                        seenSeqSamples.add(seqv.seqSampleId)
                    }
                }
            }
            if (counter >= maxLinks) {
                varlist = varlist + "\n... plus ${counter - maxLinks} more for a total of ${counter} SeqSamples"
            }

            valout = valout + "\n" + varlist

            def issueSummary = "CurVariant changed evidence class: ${variantInstance} is now ${variantInstance.evidence.evidenceClass} and was ${prevClass}"
            if (isTest) {
                issueSummary = "TEST! ${issueSummary}"
            }



            def response = jnotifier.createJiraIssue("${issueSummary}", "${valout}", "Task","molpath")

            if (response) {
                if (response.containsKey('errors')) {
                    println "Error creating issue! Response:"
                    println response
                }

                if (response.containsKey('id') && response.containsKey('key')) {
                    println "Issue created. Issue ${response['id']} ${response['key']} "

                    //assign it to pathos ops now
                    int newIssueId = response['id'] as int //cast to int
                    jnotifier.assignJiraIssue('molpath.ops', newIssueId)
                    jnotifier.addWatcherToJiraIssue('molpath.ops', newIssueId)
                    println "-------Curl response:--------"
                    println response
                    println "------------------------"
                    //make an issue object
                    //make a new issue
                    def jiraIssue = new JiraIssue(triggered_by: currentUser, issueType: 'changed_class', curVariant: variantInstance, issueIdentifier: response['key']).save(flush: true, failOnError:true)
                }
            }
        }

        //  Set curator
        //
        variantInstance.classified = currentUser

        log.info("In EvidenceController update: ${variantInstance.evidence.evidenceClass}")

        if ( ! variantInstance.save(flush: true))
        {
            render(view: "edit", model: [evidenceInstance: variantInstance.evidence])
            return
        }

        //  Create audit message
        //
        def audit_msg = "Set classification for ${variantInstance.variant} to ${variantInstance.pmClass} from ${prevClass}"
        def audit     = new Audit(  category:    'curation',
                                    variant:     variantInstance.variant,
                                    complete:    new Date(),
                                    elapsed:     0,
                                    software:    'Path-OS',
                                    swVersion:   meta(name: 'app.version'),
                                    task:        'classification',
                                    username:    currentUser.getUsername(),
                                    description: audit_msg )

        if ( ! audit.save( flush: true ))
        {
            audit?.errors?.allErrors?.each
            {
                log.error( new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
            }
            log.error( "Failed to log audit message: ${audit_msg}")
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'variant.label', default: 'CurVariant'), variantInstance.id])
        redirect( controller: "curVariant", action: "show", id: variantInstance.id)
    }
}
