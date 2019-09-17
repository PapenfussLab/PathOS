/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.converters.JSON
import org.grails.plugin.easygrid.Easygrid
import java.text.SimpleDateFormat


@Easygrid
class SeqrunController
{
    //  Scaffold everything else
    //
    static scaffold = Seqrun
    def SpringSecurityService
    def AuditService
    def utilService

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

        AuditService.audit([
            category    : 'curation',
            seqrun:      sr.seqrun,
            task:        'seqrun qc',
            description : audit_msg
        ])


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
                property        panelList
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
        Seqrun seqrunInstance
        def seqrunparam
        if ( params.id ) {
            def id = params.id
            seqrunInstance = Seqrun.get(id)
            seqrunparam = id
        } else if ( params.seqrunName ) {
            seqrunInstance = Seqrun.findBySeqrun(params.seqrunName)
            if(!seqrunInstance) {
                try {
                    seqrunInstance = Seqrun.get(params.seqrunName.toInteger())
                } catch (Exception e){
                    seqrunInstance = null
                }
            }
            seqrunparam = params.seqrunName
        } else if ( params.seqSample ) {
            seqrunInstance = SeqSample.get(params.seqSample)?.seqrun
            seqrunparam = params.seqSample
        } else { seqrunparam = null }

        if (!seqrunInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqrun.label', default: 'Seqrun'), seqrunparam])
            redirect(action: "list")
            return
        }

        AuthUser currentUser = springSecurityService.currentUser as AuthUser
        Boolean d3heatmap = Preferences.findByUser(currentUser)?.d3heatmap ?: false

        [seqrunInstance: seqrunInstance, d3heatmap: d3heatmap]
    }

    def ampliconHeatmapData(Long id){
        Seqrun sr = Seqrun.get(id)

        HashMap amplicons = [:]
        List data = AlignStats.findAllBySeqrunAndAmpliconNotEqual(sr, "SUMMARY").collect {
            amplicons[it.amplicon] = it.location
            return [it.sampleName, it.amplicon, it.readsout]
        }

        HashMap results = [
            headers: ["sample_name", "amplicon", "readsout"],
            amplicons: amplicons,
            data: data
        ]
        render results as JSON
    }

    def deleteHeatmapTsv(Long id) {
        try {
            Seqrun thisSeqrun = Seqrun.get(id)
            def webroot = servletContext.getRealPath('/')
            String seqrunQCFileLoc = webroot+"/payload/seqrun_qc_heatmap/" + thisSeqrun.seqrun + '.tsv'

            File heatmapTsv = new File(seqrunQCFileLoc)

            render heatmapTsv.delete()
        } catch(e) {
            response.status = 500
            render e
        }
    }

    def fetchHeatmapTsv(Long id){
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



        String heatmapTsvLoc = "${utilService.context()}/payload/seqrun_qc_heatmap/${thisSeqrun.seqrun}.tsv"
        if (!statsFound) {
            heatmapTsvLoc = ""  //if he have no align stats - set this to empty and then the view will not show heatmap
            heatmapTsv.delete()     //clean up, it might exist with a blank ehader
        }

        render heatmapTsvLoc;
    }

    def fetchStats(Long id){
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

    def fetchSamples(String seqrun) {
        Map results = [:]
        if(seqrun) {
            Seqrun sr = Seqrun.findBySeqrun(seqrun);
            results = [
                seqrun: seqrun,
                id: sr.id,
                samples: sr.seqSamples.collect {[it.sampleName, it.id]}
            ]
        }
        render results as JSON;
    }

    def latestRuns() {
        Integer max = params.max ? Integer.parseInt(params.max) : 10
        List<String> panelList = params?.panelList?.tokenize(",")?.toArray() ?: []

        ArrayList<Panel> panels = Panel.findAllByManifestInList(panelList)

        ArrayList<Seqrun> seqruns = []

        if(panels.empty) {
            seqruns = Seqrun.executeQuery("select x from Seqrun x order by x.runDate desc", [offset: 0, max: max])
        } else {
        seqruns = Seqrun.executeQuery("""select sr 
from Seqrun sr, SeqSample ss
where ss.seqrun = sr
and
ss.panel in :panels
group by sr.seqrun
order by sr.runDate desc""", [offset: 0, max: max, panels: panels])

            if(seqruns.empty) {
                seqruns = Seqrun.executeQuery("select x from Seqrun x order by x.runDate desc", [offset: 0, max: max])
            }
        }

        ArrayList<HashMap> results = []

        seqruns.each {
            results.push([
                    runDate: formatDate(date:it.runDate, format:'dd MMM yyyy'),
                    seqrun: it.seqrun,
                    panelList: it.panelList,
                    library: it.library,
                    experiment: it.experiment,
                    platform: it.platform,
                    authorised: it.authorised,
                    passfailFlag: it.passfailFlag
            ])
        }

        render results as JSON
    }





    def create() {
        [seqrunInstance: new Seqrun(params)]
    }

    def save() {
        def seqrunInstance = new Seqrun(params)
        if(params.seqrun.contains(" ")) {
            flash.message = "Seqrun cannot contain spaces"
        }
        if (!seqrunInstance.save(flush: true)) {
            render(view: "create", model: [seqrunInstance: seqrunInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'seqrun.label', default: 'Seqrun'), seqrunInstance.id])
        redirect(action: "show", id: seqrunInstance.id)
    }

    def edit(Long id) {
        def seqrunInstance = Seqrun.get(id)
        if (!seqrunInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqrun.label', default: 'Seqrun'), id])
            redirect(action: "list")
            return
        }

        [seqrunInstance: seqrunInstance]
    }

    def update(Long id, Long version) {
        def seqrunInstance = Seqrun.get(id)
        if (!seqrunInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqrun.label', default: 'Seqrun'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (seqrunInstance.version > version) {
                seqrunInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'seqrun.label', default: 'Seqrun')] as Object[],
                        "Another user has updated this Seqrun while you were editing")
                render(view: "edit", model: [seqrunInstance: seqrunInstance])
                return
            }
        }

        seqrunInstance.properties = params

        if (!seqrunInstance.save(flush: true)) {
            render(view: "edit", model: [seqrunInstance: seqrunInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'seqrun.label', default: 'Seqrun'), seqrunInstance.id])
        redirect(action: "show", id: seqrunInstance.id)
    }





}



































































