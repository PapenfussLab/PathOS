/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: David Ma
 */

package org.petermac.pathos.curate

import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.petermac.util.JiraNotifier

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Talks to JIRA for PathOS
 *
 * User: David Ma (DKGM)
 * Date: 18/10/17
 */

class JiraService
{
    LinkGenerator grailsLinkGenerator

    /**
     * The logic for calling JIRA Notifier when a Curated Variant is updated.
     *
     * @param var
     * @param prevClass
     * @param currentUser
     * @param env
     * @return
     */
    String notify(CurVariant var, String prevClass, AuthUser currentUser, String type, String reason, String env) //todo add ACMG or AMP
    {
        JiraNotifier jnotifier = new JiraNotifier()

        def cvlink = grailsLinkGenerator.link(controller: 'curVariant', action: 'show', id: var.id, absolute: true)

        def newClass
        switch (type.toLowerCase()) {
            case 'amp':
                newClass = var.ampClass
                break
            case 'amcg':
                newClass = var.pmClass
                break
            case 'clinical significance':
                newClass = var.overallClass
                break
        }

        HashMap pmColors = [
                "Unclassified": '#000',
                "C1: Not pathogenic": '#000',    // It should be #fffdc1 but that would be unreadable
                "Tier IV": '#f4d374',
                "NCS: Not Clinically Significant": '#f4d374',
                "C2: Unlikely pathogenic": '#f4d374',
                "Tier III": '#e89e53',
                "UCS: Unclear Clinical Significance": '#e89e53',
                "C3: Unknown pathogenicity (Level A)": '#e89e53',
                "C3: Unknown pathogenicity (Level B)": '#e89e53',
                "C3: Unknown pathogenicity (Level C)": '#e89e53',
                "C3: Unknown pathogenicity": '#e89e53',
                "Tier II": '#d65430',
                "C4: Likely pathogenic": '#d65430',
                "Tier I": '#ae2334',
                "CS: Clinically Significant": '#ae2334',
                "C5: Pathogenic": '#ae2334'
        ]

        def classColor = pmColors[newClass] ?: '#000'
        def prevClassColor = pmColors[prevClass] ?: '#000'

        def seqSs = var.allSeqSamples()
        def size = seqSs.size()
        def message = "This Curated Variant has too many related Sequenced Variants to show here.\nPlease use PathOS to view the *${size} Sequenced Variants*."

        if (size <= 20) {

            message = "It is present in the following Sequenced Samples:\n\n"

            seqSs.sort{ a, b -> b.sampleName <=> a.sampleName }.each {
                def ssLink = grailsLinkGenerator.link(controller: 'seqVariant', action: 'svlist', id: it?.id, absolute: true)
                message = message + "* [${it.sampleName}|${ssLink}]\n"
            }
        }


        def issueSummary = "Set ${type} classification for ${var.toString()} to ${newClass} from ${prevClass} because ${reason}'"

        def valout =
                """*Triggered by:* ${currentUser.getDisplayName()} ([~${currentUser.getEmail().split('@')[0]}]) ${currentUser.getEmail()}

            *Curated Variant:* [${var}|${cvlink}]
            *New evidence class:* {color:${classColor}}${newClass}{color}
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
                def jiraIssue = new JiraIssue(triggered_by: currentUser, issueType: 'changed_class', curVariant: var, issueIdentifier: response['key']).save(flush: true, failOnError:true)
            }
        } else {
            println "No response from JiraIssue"
        }

        return "Finished"
    }
}
