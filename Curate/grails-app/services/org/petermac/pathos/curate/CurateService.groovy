/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.util.GrailsUtil
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.petermac.util.JiraNotifier

import java.text.MessageFormat

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Create a new Curated CurVariant object
 * Used from the SeqVariantController
 *
 * User: doig ken
 * Date: 11/10/2013
 */

class CurateService
{
    def sessionFactory
    def springSecurityService
    def grailsLinkGenerator

    boolean createVariant(SeqVariant sv, ClinContext mc = null)
    {
        log.info( "Creating new CurVariant from ${sv}")

        sv.properties.tags = null   //no tags!
        //	Create CurVariant as domain class - copy all SeqVariant properties across
        //
        def var    = new CurVariant( sv.properties )
        var.clinContext = mc

        //  Set up Evidence embedded class
        //
        def evd = new Evidence()
        evd.evidenceClass  = "Unclassified"
        evd.save()
        var.evidence       = evd
        var.authorisedFlag = false



        //  Save new CurVariant
        //
        if ( ! var.save())
        {
            var?.errors?.allErrors?.each
                    {
                        log.error( new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                    }

            //  Discard transient object
            //
            var.discard()
            return false
        }

        //	Add all instances of this variant found in CurVariant, but only if clinContext is Null
        //  Non-Null Clinical Context variants are essentially always 'orphans' in that they will not belong to any SeqVariants
        //
        if (!mc) {
            def seqvars = SeqVariant.findAllByVariant( sv.variant )
            log.debug( "Found ${seqvars.size()} SeqVariants for ${sv}")

            //  Create links to sequenced variant
            //
            seqvars.each { var.addToSeqVariants(it) }
        }

        //  make a JIRA issue: notify MP_OPS that variant needs curation
        //
        if (true) {    //we could disable it in Demo env. or just fix the proxy. leaving this block on for now.
            //println "NEW ISSUE"

            def jnotifier = new JiraNotifier()
            def currentUser = springSecurityService.currentUser as AuthUser

            def valout = "Triggered by ${currentUser.getDisplayName()} (${currentUser.getUsername()}) ${currentUser.getEmail()} " + "\n" + "\n"

            valout = valout + "New CurVariant ${var} (HGVSP ${var.hgvsp}) has been created from SeqVariant ${sv} and needs curation. Curate here: "
            def basepath
            def isTest = true

            //  Todo: this needs to use grails methods to generate a link
            //  e.g.
            //  LinkGenerator grailsLinkGenerator
            def link = grailsLinkGenerator.link(controller: 'curVariant', action: 'show', id: var.id, absolute: true)

            switch (GrailsUtil.environment) {
                case "pa_prod":
                    isTest = false  //only if we are on the PA PROD environment do we make an issue
                    break;
            }

            //println "env: " + GrailsUtil.environment
            //def link = "${basepath}/curVariant/show/${var.hgvsg}"
            valout = valout + " ${link}"

            def issueSummary = "New CurVariant needs curation: ${var}"

            if (isTest) {
                valout = "TEST! NOT A REAL ISSUE! ${valout}"
                issueSummary = "TEST! ${issueSummary}"
            }

            //println "our var is ${var}"
            def response = jnotifier.createJiraIssue(issueSummary, "${valout}", "Task","molpath")

            if (response) {
                if (response.containsKey('errors')) {
                    println "Error creating issue! Response:"
                    println response
                }

                if (response.containsKey('id') && response.containsKey('key')) {
                    println "Issue created. Issue ${response['id']} ${response['key']} "

                    //assign it to pathos ops now
                    int newIssueId = response['id'] as int //cast to int
                    jnotifier.assignJiraIssue('molpath.ops', newIssueId)    //todo assign to someone who needs it
                    jnotifier.addWatcherToJiraIssue('molpath.ops', newIssueId)
                    def jiraIssue = new JiraIssue(triggered_by: currentUser, issueType: 'new_variant', curVariant: var, issueIdentifier: response['key']).save(flush: true, failOnError:true)

                }
            }

        }
       return true
    }
}
