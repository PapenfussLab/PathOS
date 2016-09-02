/*
 * Copyright (c) 2013. PathOS SeqSample Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import org.grails.plugin.easygrid.Easygrid

import java.text.MessageFormat

@Easygrid
class SeqSampleController
{
    static scaffold = true
    def SpringSecurityService

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
            def audit = new Audit(category: 'curation',
                    seqrun: ss.seqrun.seqrun,
                    patSample: ss.sampleName,
                    complete: new Date(),
                    elapsed: 0,
                    software: 'Path-OS',
                    swVersion: meta(name: 'app.version'),
                    task: 'sample qc',
                    username: currentUser.getUsername(),
                    description: audit_msg)

            if (!audit.save(flush: true)) {
                audit?.errors?.allErrors?.each
                        {
                            log.error(new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                        }
                log.error("Failed to log audit message: ${audit_msg}")
            }
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
                    }

            columns
                    {
                        id          { type 'id'; jqgrid { hidden = true } }
                        sampleName  { jqgrid { formatter "showlink"; formatoptions { baseLinkUrl 'sampleLink' }}}
                        sample
                                {
                                    jqgrid { formatter "showlink"; formatoptions { baseLinkUrl "patientLink"} }
                                    value { ss -> ss?.patSample?.sample }
                                    filterClosure
                                            {
                                                filter ->
                                                    sample { ilike('sample', "%${filter.paramValue}%") }
                                            }
                                    enableFilter true
                                }
                        seqrun
                                {
                                    jqgrid { formatter "showlink"; formatoptions { baseLinkUrl "seqrunLink"} }
                                    value { ss -> ss?.seqrun?.seqrun }
                                    filterClosure
                                            {
                                                filter ->
                                                    seqrun { ilike('seqrun', "%${filter.paramValue}%") }
                                            }
                                    enableFilter true
                                }
                        panel
                                {
                                    value { ss -> ss?.panel?.manifest }
                                    filterClosure
                                            {
                                                filter ->
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
                                            {
                                                filter ->
                                                    firstReviewBy { ilike('displayName', "%${filter.paramValue}%") }
                                            }
                                    enableFilter true
                                }
                        finalReviewBy
                                {
                                    value { ss -> ss?.finalReviewBy?.displayName }
                                    filterClosure
                                            {
                                                filter ->
                                                    finalReviewBy { ilike('displayName', "%${filter.paramValue}%") }
                                            }
                                    enableFilter true
                                }
                        authorisedQc
                                {
                                    value { ss -> ss?.authorisedQc?.displayName }
                                    filterClosure
                                            {
                                                filter ->
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
     * Link to Sample curation Page
     *
     * @return
     */
    def sampleLink()
    {
        if ( params.id )
        {

            redirect( controller: "seqVariant", action: "svlist", id: params.id )
        }
    }

    /**
     * Link to Patient Sample Page
     *
     * @return
     */
    def patientLink()
    {

        if ( params.id )
        {
            def ss = SeqSample.get( params.id )

            redirect( controller: "sample", action: "show", id: ss.patSample.id )
        }
    }

    /**
     * Link to Seqrun Page
     *
     * @return
     */
    def seqrunLink()
    {
        if ( params.id )
        {
            def ss = SeqSample.get( params.id )

            redirect( controller: "seqrun", action: "show", id: ss.seqrun.id )
        }
    }
}
