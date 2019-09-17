package org.petermac.pathos.curate

import grails.converters.JSON
import org.petermac.util.Locator
import org.grails.plugin.easygrid.Easygrid
import org.grails.plugin.easygrid.Filter
import org.grails.plugin.easygrid.FilterOperatorsEnum
import org.grails.plugin.easygrid.Filters


import org.grails.plugin.filterpane.FilterPaneUtils
import org.petermac.annotate.DataSource
import org.petermac.annotate.MutVarDataSource
import org.springframework.dao.DataIntegrityViolationException

import java.text.MessageFormat

import static org.grails.plugin.easygrid.GormUtils.applyFilter
import static org.grails.plugin.easygrid.GormUtils.applyFilter
import static org.grails.plugin.easygrid.GormUtils.applyFilter
import static org.grails.plugin.easygrid.GormUtils.applyFilter

@Easygrid
class TranscriptController {
    //TODO for Transcripts:
    //disable all CRUD except for list
    //all can view, only admin can edit

    //  EasGrid services
    //
    def easygridService
    def easygridDispatchService
    def JqGridMultiSearchService
    def springSecurityService
    def AuditService
    def loc = Locator.instance

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def thisBuild = 'hg19'  //the build we're interested in for our transcripts

    def index() {
        redirect(action: "list", params: params)
    }

    def translist(Integer max)
    {
        params.max = Math.min(max ?: 20, 100)

        //session.sampleId = params.id    //  save the sample ID for EasyGrid

        //[ seqVariantList: SeqVariant.list(params), seqVariantTotal: SeqVariant.count()]
    }

    /**
     * Table definition for Master Curation CurVariant Table in Easygrid format
     */
    def transcriptGrid =
            {
                dataSourceType          'gorm'
                domainClass             Transcript
                inlineEdit              true
                enableFilter            true
                externalParams (['id'])                     // Pass 'id' parameter through to easygrid methods
                externalParams (['build'])
                //  Applies for all records returned
                //

                // Needed to activate globalFilterClosure{}
                export
                        {
                            export_title  'Transcript'
                            maxRows       1000000             // Maximum number of variants per export
                        }

                //  Grid jqgrid defaults
                //
                jqgrid
                        {
                            height      '100%'
                            rowNum      20
                            rowList     = [ 20, 50, 100 ]
                            editable    false
                            sortable    true
                            filterToolbar = [ searchOperators: false ]
                        }
                globalFilterClosure
                        {
                            eq('build',thisBuild)  //only show where build is hg19
                        }


                beforeSave {    //params ->


                    if (params.oper == 'edit')
                    {
                        def currentUser = springSecurityService.currentUser as AuthUser
                        def errorMsg
                        if (currentUser.authorities.any { it.authority == "ROLE_ADMIN"  || it.authority == "ROLE_DEV" }) {
                            def isAdmin = true
                        } else {
                            //errorMsg = "Only administrators may change the transcript"

                            throw new RuntimeException("User is not admin") //todo we need a better way to do this, terrible. this is caught in easygrid and afterError gets called
                            return false
                        }

                        if (params.preferred == 'Yes') {
                            //we're setting a new preferred transcript. ensure we can't do more than one.
                            def thisTranscript = Transcript.get(params.id)
                            def thisGene = thisTranscript.gene
                            def theseGeneTranscripts = Transcript.findAllByGeneAndBuild(thisGene,thisBuild)


                            for (genetrans in theseGeneTranscripts) {
                                if (genetrans.id != params.id && genetrans.preferred == true)
                                {
                                    //errorMsg = "Gene " + thisGene + " cannot have more than one preferred transcript"

                                    throw new RuntimeException("Gene cannot have more than one preferred transcript") //todo we need a better way to do this, terrible. this is caught in easygrid and afterError gets called
                                    return false

                                }
                            }
                        }

                        //remove from cache
                        def rdb = loc.pathosEnv
                        def dsource = new DataSource(rdb)
                        def thisTranscript = Transcript.get(params.id)

                        dsource.removeRegionFromCache( 'MUT', thisTranscript.chromosome, thisTranscript.ts_start, thisTranscript.ts_stop )
                        dsource.removeGeneFromCache(   'MUT', thisTranscript.gene )
                        dsource.removeGeneFromCache(   'VEP', thisTranscript.gene )

                        //  audit log message. realistically here we only ever set preferred on-off, no other update
                        //
                        def audit_msg = "Changed Transcript ID ${thisTranscript.id} ${thisTranscript.gene} ${thisTranscript.build} ${thisTranscript.accession} ${thisTranscript.chr_refseq} set preferred to ${params.preferred} from ${thisTranscript.preferred} "

                        AuditService.audit([
                            category    : 'transcript',
                            task        : 'transcript change',
                            description : audit_msg
                        ])

                    }

                    //session.setAttribute('transcriptErrorMsg',null)

                    params.remove('oper')
                    params
                }
                columns
                        {
                            id                  { type 'id'; key true; jqgrid { hidden true; hidedlg true } }

                            act                 {
                                type        'actions'
                                sortable    false
                                jqgrid      {
                                    hidedlg true;
                                    formatoptions
                                            {
                                                delbutton       false;
                                                onSuccess       true;
                                                afterSave       'f:afterEdit';  //pass success or failure here somehow?
                                                onError         'f:afterError';
                                                afterRestore    'f:reloadGrid'
                                            }
                                }
                            }

                            genbuild	//GRCh37
                            build	//hg19
                            chromosome	//chr17
                            chr_refseq //NC_000017.10
                            accession	//NM_007294
                            refseq	//NM_007294.2
                            preferred    { jqgrid { formatter "checkbox"; formatoptions { disabled true}; editable true; edittype "checkbox"; width "60"; align "center" } }	//1

                            gene	//BRCA1
                            strand	//reverse
                            ts_size	//81154
                            cds_size	//78418
                            source	//ncbi
                            exSize	//8127
                            exCount	//23
                            ts_start	//41196314
                            ts_stop	//41277468
                            cds_start	//41197695
                            cds_stop	//41276113
                            exon_starts	//9223372036854775807
                            exon_stops	//9223372036854775807
                            lrg	//LRG_292
                        }
            }



    def filterPaneService




    def list(Integer max) {
        //def transError = session.getAttribute('transcriptErrorMsg')

        //if (transError) {
          //  flash.message = transError
            //println transError
        //}
        def isAdmin = false
        def currentUser = springSecurityService.currentUser as AuthUser
        def errorMsg
        if (currentUser.authorities.any { it.authority == "ROLE_ADMIN"  || it.authority == "ROLE_DEV" }) {
            isAdmin = true
        }

        params.max = Math.min(max ?: 25, 100)
        if(!params.max) params.max = 10
        [ transcriptList: Transcript.list( params ), filterParams: FilterPaneUtils.extractFilterParams(params), isAdmin: isAdmin ]
    }

    def filter = {
        if(!params.max) params.max = 25
        render( view: 'list',
                model:[ transcriptList:    filterPaneService.filter( params, Transcript ),
                        transcriptCount:   filterPaneService.count( params, Transcript ),
                        filterParams:                       FilterPaneUtils.extractFilterParams(params),
                        params:                             params
                ]
        )
    }

    def create() {
        [transcriptInstance: new Transcript(params)]
    }

    def save() {
        def transcriptInstance = new Transcript(params)
        if (!transcriptInstance.save(flush: true)) {
            render(view: "create", model: [transcriptInstance: transcriptInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'transcript.label', default: 'Transcript'), transcriptInstance.id])
        redirect(action: "show", id: transcriptInstance.id)
    }

    def show(Long id) {
        def transcriptInstance = Transcript.get(id)
        if (!transcriptInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'transcript.label', default: 'Transcript'), id])
            redirect(action: "list")
            return
        }

        [transcriptInstance: transcriptInstance]
    }

    def edit(Long id) {
        def transcriptInstance = Transcript.get(id)
        if (!transcriptInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'transcript.label', default: 'Transcript'), id])
            redirect(action: "list")
            return
        }

        [transcriptInstance: transcriptInstance]
    }

    def update(Long id, Long version) {
        def transcriptInstance = Transcript.get(id)
        if (!transcriptInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'transcript.label', default: 'Transcript'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (transcriptInstance.version > version) {
                transcriptInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'transcript.label', default: 'Transcript')] as Object[],
                          "Another user has updated this Transcript while you were editing")
                render(view: "edit", model: [transcriptInstance: transcriptInstance])
                return
            }
        }

        transcriptInstance.properties = params

        if (!transcriptInstance.save(flush: true)) {
            render(view: "edit", model: [transcriptInstance: transcriptInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'transcript.label', default: 'Transcript'), transcriptInstance.id])
        redirect(action: "show", id: transcriptInstance.id)
    }

    def delete(Long id) {
        def transcriptInstance = Transcript.get(id)
        if (!transcriptInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'transcript.label', default: 'Transcript'), id])
            redirect(action: "list")
            return
        }

        try {
            transcriptInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'transcript.label', default: 'Transcript'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'transcript.label', default: 'Transcript'), id])
            redirect(action: "show", id: id)
        }
    }

    /**
     * Rest interface to fetch a locus based on gene and pass back a JSON
     * See here: https://github.com/igvteam/igv.js/wiki/Browser/
     *
     * DKGM | David Ma
     * 17-August-2016
     *
     */

    def locusJSON() {
        def genome = params.genome ?: "hg19",
            name   = params.name;

        if(name) {
            Transcript[] beans = Transcript.findAllByGeneAndBuildAndPreferred(name, genome, true)

            if(beans) {
                def results = []
                beans.each({ it ->
                    results.push ([
                                    "chromosome" : "$it.chromosome",
                                    "start" : it.cds_start,
                                    "end" : it.cds_stop
                                  ])
                })
                render results as JSON
            } else {
                render "[]"
            }
        } else {
            render "[]"
        }
    }






}




















