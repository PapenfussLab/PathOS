package org.petermac.pathos.curate



import org.grails.plugin.filterpane.FilterPaneUtils
import org.springframework.dao.DataIntegrityViolationException

class SeqRelationController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def filterPaneService

    def list(Integer max) {
        params.max = Math.min(max ?: 25, 100)
        if(!params.max) params.max = 10
        [ seqRelationList: SeqRelation.list( params ), filterParams: FilterPaneUtils.extractFilterParams(params) ]
    }

    def filter = {
        if(!params.max) params.max = 25
        render( view: 'list',
                model:[ seqRelationList:    filterPaneService.filter( params, SeqRelation ),
                        seqRelationCount:   filterPaneService.count( params, SeqRelation ),
                        filterParams:                       FilterPaneUtils.extractFilterParams(params),
                        params:                             params
                ]
        )
    }

    def create() {
        [seqRelationInstance: new SeqRelation(params)]
    }

    def save() {

        def seqRelationInstance = new SeqRelation(params)
        if (!seqRelationInstance.save(flush: true)) {
            render(view: "create", model: [seqRelationInstance: seqRelationInstance])
            return
        }

        // add any samples passed from params
        addSamplesToSeqRelation(params,seqRelationInstance)


        flash.message = message(code: 'default.created.message', args: [message(code: 'seqRelation.label', default: 'SeqRelation'), seqRelationInstance.id])
        redirect(action: "show", id: seqRelationInstance.id)
    }

    def show(Long id) {
        def seqRelationInstance = SeqRelation.get(id)
        if (!seqRelationInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqRelation.label', default: 'SeqRelation'), id])
            redirect(action: "list")
            return
        }

        [seqRelationInstance: seqRelationInstance]
    }

    def edit(Long id) {
        def seqRelationInstance = SeqRelation.get(id)




        if (!seqRelationInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqRelation.label', default: 'SeqRelation'), id])
            redirect(action: "list")
            return
        }

        [seqRelationInstance: seqRelationInstance]
    }

    def update(Long id, Long version) {

        def seqRelationInstance = SeqRelation.get(id)
        if (!seqRelationInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqRelation.label', default: 'SeqRelation'), id])
            redirect(action: "list")
            return
        }

        // add any samples passed from params
        addSamplesToSeqRelation(params,seqRelationInstance)

        // cycle through params and remove samples that have been checkboxed
        params.each{ k, v ->
            if (k.toString().startsWith('remove_')) {
                //value is id to remove
                def removeSample = SeqSample.get(v)
                if (removeSample) seqRelationInstance.removeFromSamples(removeSample)
            }
        }


        if (version != null) {
            if (seqRelationInstance.version > version) {
                seqRelationInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'seqRelation.label', default: 'SeqRelation')] as Object[],
                          "Another user has updated this SeqRelation while you were editing")
                render(view: "edit", model: [seqRelationInstance: seqRelationInstance])
                return
            }
        }

        seqRelationInstance.properties = params

        if (!seqRelationInstance.save(flush: true)) {
            render(view: "edit", model: [seqRelationInstance: seqRelationInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'seqRelation.label', default: 'SeqRelation'), seqRelationInstance.id])
        redirect(action: "show", id: seqRelationInstance.id)
    }

    def delete(Long id) {
        def seqRelationInstance = SeqRelation.get(id)
        if (!seqRelationInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqRelation.label', default: 'SeqRelation'), id])
            redirect(action: "list")
            return
        }

        try {
            seqRelationInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'seqRelation.label', default: 'SeqRelation'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'seqRelation.label', default: 'SeqRelation'), id])
            redirect(action: "show", id: id)
        }
    }

    /**
     * Adds SeqSamples to an SeqRelationInstance for our CRUD
     * takes params and assumes the _form.gsp style (hidden fields with ids starting with "add_"
     * @param params
     * @param seqRelationInstance
     * @return
     */
    def addSamplesToSeqRelation(Map params, SeqRelation seqRelationInstance) {

        //  we also set sample types to (T/N/TN) if a TumourNomrla seqrelation
        //
        def updateTNSampleTypes = false
        if (seqRelationInstance.relation == 'TumourNormal') updateTNSampleTypes = true


        //  cycle through samplename seqrun pairs to be added (passed as json) and add the seqrun
        //
        params.each{ k, v ->
            //see what begins with "add_"
            if (k.toString().startsWith('add_')) {

                //slurp the json and add
                def slurper = new groovy.json.JsonSlurper()
                def newSs
                try {
                    newSs = slurper.parseText(v)
                } catch (Exception e) {
                    println "Error while parsing new samples to be added"
                    log.warn("Error while parsing new samples to be added: trying to parse malformed JSON string " + v)
                }

                if(newSs?.seqrun && newSs?.seqsample) {
                    def run = Seqrun.findBySeqrun(newSs.seqrun)
                    if (run) {
                        def seqsample = SeqSample.findBySeqrunAndSampleName(run,newSs.seqsample)
                        if(seqsample)       seqRelationInstance.addToSamples(seqsample)

                        if(updateTNSampleTypes) {   //check suffix, if its T/N/TN set sampletype appropriately
                            //get suffix and if it's a TN one, set accordingly
                            if(seqsample.sampleName.contains('-')) {
                                def splitname = seqsample.sampleName.split('-')
                                def oldSampleType = seqsample.sampleType
                                switch (splitname[splitname.size() - 1]) {
                                    case 'N':
                                        seqsample.setSampleType('Normal')
                                        break;
                                    case 'T':
                                        seqsample.setSampleType('Tumour')
                                        break;
                                    case 'TN':
                                        seqsample.setSampleType('TumourNormal')
                                        break;

                                }
                                if (oldSampleType != seqsample.getSampleType()) {
                                    //warn us somewhee
                                    log.warn('Overwrote sample type of SeqSample Id' + seqsample.id + ' from ' + oldSampleType + ' to ' + seqsample.getSampleType() + ' when adding to SeqRelation Id ' + seqRelationInstance.id)
                                    println ('Overwrote sample type of SeqSample Id' + seqsample.id + ' from ' + oldSampleType + ' to ' + seqsample.getSampleType() + ' when adding to SeqRelation Id ' + seqRelationInstance.id)
                                }
                            }
                        }
                    }
                }
            }
        }




        //warn if overwriting

    }

    //  called by RemoteFunction from _form.gsp
    //
    def updateSeqSamplesForSeqrun = {
        def seqrun = params['seqrun']
        def seqsamples = SeqSample.findAllBySeqrun(Seqrun.findBySeqrun(seqrun))
        render(template:'seqsamplelist', model:[seqsamples:seqsamples])
    }


}
