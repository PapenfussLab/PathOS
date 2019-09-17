/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 * util jira notifier
 */

package org.petermac.util

import groovy.json.JsonSlurper
import groovy.json.StringEscapeUtils
import groovy.util.logging.Log4j
/**
 *  Jira integration class to log issues directly from an application
 *
 *  Created by Andrei Seleznev  on 12/12/2014
 */
@Log4j
class JiraNotifier
{
    /**
     * Makes the actual REST call to our Jira running on nectar
     * @param issueData
     * @param issueId
     * @param usePut
     * @return
     **/
    static def makeJiraCurlCall(String issueData, int issueId = 0, boolean usePut = false, String extraUrl = '')
    {
        //  Load address, proxy, credentials from Locator
        //
        Locator loc = Locator.instance

        String jiraAdd = loc.jiraAddress + "/jira/rest/api/2/issue/"    //append the issue API
        String proxyString = loc.jiraProxy
        String userName = loc.jiraUsername
        String userPass = loc.jiraPass

        String baseurl = jiraAdd    //note this has a trailing slash by default

        //  add issue id to base address
        if ( issueId != 0 )
        {
            baseurl = baseurl+"${issueId}"
        }

        //  add extra to url if needed
        //
        baseurl = baseurl + extraUrl

        //  make an issue data file: dump the text in there so we can curl it up
        //
        def dataFile = File.createTempFile('tmpjiradata','.txt')
        dataFile.write(issueData)

        def method = "PUT"
        if ( ! usePut ) method = "POST"

        if (proxyString == "''") proxyString = ''

        //  curl it up
        //
        String curlCommand = "curl ${proxyString} --connect-timeout 10 --max-time 40 -k -D- -u ${userName}:${userPass} -X ${method} --data-binary @${dataFile.getPath()} -H \"Content-Type: application/json\" ${baseurl}"
        println "JIRA: Curling"
        println curlCommand
        log.warn("JIRA curling")
        log.warn(curlCommand)
        def response = new RunCommand( curlCommand ).run()

        return response
    }

    /**
     * Assign a Jira issue to a user
     * @param assignee
     * @param issueId
     * @return
     */
    static Map assignJiraIssue(String assignee, int issueId)
    {
        String issueData = /{ "fields" : { "assignee" : {"name" : "${assignee}"}}}/
        println issueData
        def response = makeJiraCurlCall(issueData,issueId,true)

        HashMap returnResponse = [:]

        def respArr = response.split('\n')

        for (respLine in respArr) { //iterate and grab the JSON line. it could be anywhere in the response.
            if (respLine.startsWith('{')) {
                JsonSlurper slurper = new JsonSlurper()
                HashMap parsed = slurper.parseText(respLine) as HashMap
                returnResponse = parsed
            }
        }

        return returnResponse
    }

    /**
     * Assign a watcher to a Jira Issue to a user
     * @param assignee
     * @param issueId
     * @return
     */
    static Map addWatcherToJiraIssue(String watcher, int issueId)
    {
        String issueData = /"${watcher}"/
        println issueData
        def response = makeJiraCurlCall( issueData, issueId, false, '/watchers')

        HashMap returnResponse = [:]

        def respArr = response.split('\n')

        for (respLine in respArr) { //iterate and grab the JSON line. it could be anywhere in the response.
            if (respLine.startsWith('{')) {
                JsonSlurper slurper = new JsonSlurper()
                HashMap parsed = slurper.parseText(respLine) as HashMap
                returnResponse = parsed
            }
        }

        return returnResponse
    }

    /**
     * Make a new Jira issue for the PathOS project
     * @param summary
     * @param desc
     * @param issuetype
     * @return
     */
    static HashMap createJiraIssue(String summary, String desc, String issuetype, String project = 'pathos')
    {
        //  set projectid. should do away with this eventually, its hardcoded
        //
        int projectid = 0
        if (project == 'pathos')  projectid = 10000
        if (project == 'molpath') projectid = 10500

        summary = StringEscapeUtils.escapeJava(summary)
        desc    = StringEscapeUtils.escapeJava(desc)

        String issueData = """{
    "fields": {
        "project": {
            "id": "${projectid}"
        },
        "summary": "${summary}",
        "description": "${desc.replaceAll(":p", "\\\\\\\\:p")}",
        "issuetype": {
            "name": "${issuetype}"
        }
    }
}"""

        def response = makeJiraCurlCall( issueData, 0, false, '' )

        HashMap returnResponse = [:]
        if (!response) {
            println "ERROR could not connect! No response!"
            return returnResponse
        }
        def respArr = response.split('\n')

        for (respLine in respArr) { //iterate and grab the JSON line. it could be anywhere in the response
            if (respLine.startsWith('{')) {
                JsonSlurper slurper = new JsonSlurper()
                HashMap parsed = slurper.parseText(respLine) as HashMap
                returnResponse = parsed
            }
        }
        return returnResponse
    }
}
