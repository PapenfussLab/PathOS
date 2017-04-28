/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.petermac.util.JiraNotifier
import org.petermac.util.Locator

import java.text.MessageFormat

class EvidenceController
{
    LinkGenerator grailsLinkGenerator
    static def loc = Locator.instance
    static def env = loc.pathosEnv

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

            CurVariant var = variantInstance
            def jnotifier = new JiraNotifier()

            def cvlink = grailsLinkGenerator.link(controller: 'curVariant', action: 'show', id: var.id, absolute: true)

            HashMap pmColors = [
                    "Unclassified": '#000',
                    "C1: Not pathogenic": '#000',    // It should be #fffdc1 but that would be unreadable
                    "C2: Unlikely pathogenic": '#f4d374',
                    "C3: Unknown pathogenicity": '#e89e53',
                    "C4: Likely pathogenic": '#d65430',
                    "C5: Pathogenic": '#ae2334'
            ]
            def classColor = pmColors[variantInstance.evidence.evidenceClass]
            def prevClassColor = pmColors[prevClass]

            def seqSs = variantInstance.allSeqSamples()
            def size = seqSs.size()
            def message = "This Curated Variant has too many related Sequenced Variants to show here.\nPlease use PathOS to view the *${size} Sequenced Variants*."

            if (size <= 20) {

                message = "It is present in the following Sequenced Samples:\n\n"

                seqSs.sort{ a, b -> b.sampleName <=> a.sampleName }.each {
                    def ssLink = grailsLinkGenerator.link(controller: 'seqVariant', action: 'svlist', id: it?.id, absolute: true)
                    message = message + "* [${it.sampleName}|${ssLink}]\n"
                }
            }

            def issueSummary = "CurVariant changed evidence class: ${var} is now ${variantInstance.evidence.evidenceClass} and was ${prevClass}"

            def valout =
            """*Triggered by:* ${currentUser.getDisplayName()} ([~${currentUser.getEmail().split('@')[0]}]) ${currentUser.getEmail()}

            *Curated Variant:* [${var}|${cvlink}]
            *New evidence class:* {color:${classColor}}${variantInstance.evidence.evidenceClass}{color}
            *Old evidence class:* {color:${prevClassColor}}${prevClass}{color}

            ${message}"""

            if ( env != "pa_prod" ) {
                issueSummary = "TEST! ${issueSummary}"
                valout = "TEST! NOT A REAL ISSUE!\n${valout}"
            }

            def response
            if ( env != "pa_local" ) {
                response = jnotifier.createJiraIssue(issueSummary, valout, "Task", "molpath")
            }

            if (response) {
                if (response.containsKey('errors')) {
                    println "Error creating issue! Response:"
                    println response
                }

                if (response.containsKey('id') && response.containsKey('key')) {
                    println "Issue created. Issue ${response['id']} ${response['key']} "

                    int newIssueId = response['id'] as int //cast to int
                    println "-------Curl response:--------"
                    println response
                    println "------------------------"
                    //make an issue object
                    //make a new issue
                    def jiraIssue = new JiraIssue(triggered_by: currentUser, issueType: 'changed_class', curVariant: variantInstance, issueIdentifier: response['key']).save(flush: true, failOnError:true)
                }
            } else {
                println "No response from JiraIssue"
            }
        }

        //  Set curator
        //
        variantInstance.classified = currentUser

        log.info("In EvidenceController update: ${variantInstance.evidence.evidenceClass}")

        if ( ! variantInstance.save(flush: true))
        {
            println "We have errors in the Evidence Controller Update"
            variantInstance.errors.each {
                println it
            }
            render(view: "edit", model: [evidenceInstance: variantInstance.evidence])
            return
        }

        //  Create audit message
        //
        def audit_msg = "Set classification for ${variantInstance.toString()} to ${variantInstance.pmClass} from ${prevClass}"
        def audit     = new Audit(  category:    'curation',
                                    variant:     variantInstance.toString(),
                                    complete:    new Date(),
                                    elapsed:     0,
                                    software:    'PathOS',
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
