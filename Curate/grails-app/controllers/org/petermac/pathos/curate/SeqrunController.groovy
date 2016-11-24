/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.converters.JSON
import org.grails.plugin.easygrid.Easygrid

import java.text.MessageFormat
import java.text.SimpleDateFormat


@Easygrid
class SeqrunController
{
    //  Scaffold everything else
    //
    static scaffold = Seqrun
    def SpringSecurityService

    /**
     * Action to authorise (or not) a Seqrun
     *
     * @param passfail  String to indicate pass or fail, if not set, message is returned
     * @param qcComment String of a QC comment to record reason
     */
    def authoriseRun =
    {
        def currentUser = springSecurityService.currentUser as AuthUser
        Long id         = params.id as Long
        String passfail = params?.passfail

        if ( passfail == '' )
        {
            flash.message = 'Set the QC status before authorising'
            redirect(action: "show", id: id)
            return
        }

        def sr = Seqrun.get( id )
        if ( ! sr )
        {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqrun.label', default: 'Seqrun'), id])
            redirect(action: "list")
            return
        }

        //check role
        if (!(currentUser.authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_DEV" || it.authority == "ROLE_CURATOR" || it.authority == "ROLE_LAB"}))
        {
            flash.message = "You do not have sufficient access privileges to perform this action"
            redirect(action: "show", id: id)
            return
        }



        //if we're revoking we need to be admins
        if ( !(params?.passfail ) && !(currentUser.authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_DEV" } ) )
        {
            flash.message = "Only administrators can revoke QC !"
            redirect(action: "show", id: id)
            return
        }

        if ( ! sr.authorisedFlag )
        {
            sr.authorised     = currentUser
            sr.authorisedFlag = true
            sr.passfailFlag   = (params?.passfail == 'Pass')
            sr.qcComment      = params?.qcComment

            //  Cascade a QC fail to all Run samples
            //
            if ( ! sr.passfailFlag )
            {
                for ( ss in sr.seqSamples)
                {
                    ss.authorisedQc     = currentUser
                    ss.authorisedQcFlag = true
                    ss.passfailFlag     = false
                    ss.qcComment        = "QC failed due to Seqrun failure"
                    ss.save()
                }
            }
        }
        else
        {
            //  Revoke all cascaded Sample QC fails
            //
            if ( ! sr.passfailFlag )
            {
                for ( ss in sr.seqSamples)
                {
                    ss.authorisedQc     = null
                    ss.authorisedQcFlag = false
                    ss.passfailFlag     = false
                    ss.qcComment        = null
                    ss.save()
                }
            }

            sr.authorised     = null
            sr.authorisedFlag = false
            sr.passfailFlag   = false
            sr.qcComment      = null
        }

        //  Save updates
        //
        if ( ! sr.save(flush: true))
        {
            log.error( "Failed to update authorisation for [${sr.seqrun}]")
        }

        //  Set audit record for authorisation
        //
        def audit_msg = "Set QC authorisation on ${sr.seqrun} to ${sr.passfailFlag ? 'Pass' : 'Fail'} Comment: ${sr.qcComment} "
        def audit     = new Audit(  category:    'curation',
                                    seqrun:      sr.seqrun,
                                    complete:    new Date(),
                                    elapsed:     0,
                                    software:    'Path-OS',
                                    swVersion:   meta(name: 'app.version'),
                                    task:        'seqrun qc',
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

        redirect(action: "show", id: id)
    }

    /**
     * Table definition for Easygrid
     */
    def seqrunGrid =
    {
        dataSourceType  'gorm'
        domainClass     Seqrun
        enableFilter    true
        editable        false
        inlineEdit      false

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
            sortname      'seqrun'            //  Initial sort order by seqrun
            sortorder     'desc'
        }

        columns
        {
            id          { type 'id'; jqgrid { hidden = true } }
            seqrun      { jqgrid { sortable true; width "240"; formatter "showlink"; formatoptions { baseLinkUrl "show"} } }
            runDate     { enableFilter false; value {sr -> new SimpleDateFormat('dd-MMM-yyyy').format(sr.runDate)}; jqgrid { sortable false; width "80" } }
            noSamples
            {
                label           'Samples'
                enableFilter    false
                value           { Seqrun sr -> sr.seqSamples.size() }
                sortable        false
            }
            platform
            sepe
            readlen
            panelList
            {
                label           'Panels'

                property    panelList


            }

            library
            experiment
            scanner
            authorisedFlag
            passfailFlag
            authorised
            {
                value { sr -> sr?.authorised?.displayName }
                filterClosure
                {
                    filter ->
                        authorised { ilike('displayName', "%${filter.paramValue}%") }
                }
            }
            qcComment
        }
    }

    /**
     * List a set of Seqrun records (Optionally export to file)
     *
     * @return
     */
    def list()
    {

        def currentUser = springSecurityService.currentUser as AuthUser

    }

    def show() {
        def seqrunInstance
        def seqrunparam
        if (params.id) {
            def id = params.id
            seqrunInstance = Seqrun.get(id)
            seqrunparam = id
        } else if (params.seqrunName ) {
            seqrunInstance = Seqrun.findBySeqrun(params.seqrunName)
            seqrunparam = params.seqrunName
        } else { seqrunparam = null }

        if (!seqrunInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqrun.label', default: 'Seqrun'), seqrunparam])
            redirect(action: "list")
            return
        }

        String seqrunTsvLoc =   '/PathOS/payload/seqrun_qc_heatmap/' +  seqrunInstance.seqrun + '.tsv'
        [seqrunInstance: seqrunInstance,heatmapTsvPath: seqrunTsvLoc]
    }

    def getHeatmap(Long id){
        Seqrun thisSeqrun = Seqrun.get(id)

        //check if we have a .tsv file for this - for the JS heatmap
        //generate it if we don't
        def webroot = servletContext.getRealPath('/')
        String seqrunQCFileLoc = webroot+"/payload/seqrun_qc_heatmap/" + thisSeqrun.seqrun + '.tsv'

        //check if exists
        def allreadsout = []
        File heatmapTsv = new File(seqrunQCFileLoc)

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        Date lastMonth = cal.getTime();
        def statsFound = false

        if (!heatmapTsv.exists() || (heatmapTsv.lastModified() < lastMonth.time) ) { //if it doesnt exist or if it was made over a week ago
            heatmapTsv.write("")    //blank it
            def seqsamps = SeqSample.findAllBySeqrun(thisSeqrun )
            heatmapTsv.append("sample_name" + '\t' + "amplicon" + '\t' + "readsout" + '\n')
            for (ss in seqsamps) {
                //readouts amplicon samplename seqrun from alignstats

                def astats = AlignStats.findAllBySeqrunAndSampleName(thisSeqrun,ss.sampleName)

                if (astats) {

                    for (astat in astats) {

                        String ampName
                        String amp = astat.amplicon
                        if(amp.contains('.')) {
                            ampName = amp.split("\\.")[0]
                        } else { ampName = amp }

                        if (astat.amplicon != "SUMMARY") {
                            statsFound = true
                            String newtsvline = ss.sampleName + '\t' + ampName + '\t' + astat.readsout + '\n'
                            //println newtsvline
                            allreadsout.add(astat.readsout)
                            heatmapTsv.append(newtsvline)
                        }
                    }
                }

            }

        } else {
            statsFound = true
        }


        String heatmapTsvLoc = '/PathOS/payload/seqrun_qc_heatmap/' +  thisSeqrun.seqrun + '.tsv'
        if (!statsFound) {
            heatmapTsvLoc = ""  //if he have no align stats - set this to empty and then the view will not show heatmap
            heatmapTsv.delete()     //clean up, it might exist with a blank ehader
        }

        render heatmapTsvLoc;
    }

    def getStats(Long id){
        Seqrun seqrunInstance = Seqrun.get(id);
        Map results = [
                panels: null,
                readChart: [],
                sampleChart: [],
                ampliconChart: []
        ]
        results.panels = StatsService.panels(seqrunInstance)
        results.panels.each { panel ->
            results.readChart.push StatsService.seqrunReadChart(seqrunInstance,panel)
            results.sampleChart.push StatsService.seqrunSampleChart(seqrunInstance,panel)
            results.ampliconChart.push StatsService.seqrunAmpliconChart(seqrunInstance,panel)
        }
        render results as JSON;
    }



    def latestRuns() {
        ArrayList<Seqrun> seqruns = Seqrun.list().take(10)
        ArrayList<HashMap> results = []

        seqruns.each {
            results.push([
                    runDate: formatDate(date:it.runDate, format:'dd MMM'),
                    seqrun: it.seqrun,
                    panelList: it.panelList,
                    library: it.library,
                    platform: it.platform,
                    authorised: it.authorised,
                    passfailFlag: it.passfailFlag
            ])
        }

        render results as JSON;
    }

}



































































