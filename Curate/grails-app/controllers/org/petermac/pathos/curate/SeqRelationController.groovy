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
        println "aa"

        def c = SeqSample.constraints.sampleType['inList']
        println c

        [seqRelationInstance: new SeqRelation(params)]
    }

    def save() {

        def seqRelationInstance = new SeqRelation(params)
        if (!seqRelationInstance.save(flush: true)) {
            render(view: "create", model: [seqRelationInstance: seqRelationInstance])
            return
        }

        /*
        seqsample_set_type
         */

        // add any samples passed from params
        def warnings = addSamplesToSeqRelation(params,seqRelationInstance)


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
        def warnings = addSamplesToSeqRelation(params,seqRelationInstance)

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

        for (warning in warnings) {
            println warning
            // add warnings about changes sample types
            flash.message += '<br/>' + warning //not the right way

        }
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
     * @return  warnings a List of warnings
     */
    private List<String> addSamplesToSeqRelation(Map params, SeqRelation seqRelationInstance) {

        //  we also set sample types to (T/N/TN) if a TumourNomrla seqrelation
        //
        def updateTNSampleTypes = false
        if (seqRelationInstance.relation == 'TumourNormal') updateTNSampleTypes = true
        def warnings = []
        println params
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
                    // these are 3 arraylists of size 1, or at least they should nbe
                    def srun = newSs.seqrun.size() > 0 ?  newSs.seqrun[0] :""
                    def ssname = newSs.seqsample.size() > 0 ?  newSs.seqsample[0] :""
                    def stype = newSs.sampletype.size() > 0 ?  newSs.sampletype[0] :""

                    def run = Seqrun.findBySeqrun(srun)

                    if (run) {
                        def seqsample = SeqSample.findBySeqrunAndSampleName(run,ssname)

                        if(seqsample) {
                            def oldSampleType = seqsample.sampleType
                            seqRelationInstance.addToSamples(seqsample)

                            if(stype) {
                                println stype
                                println stype.getClass()
                                seqsample.setSampleType(stype)
                                println seqsample.getSampleType()
                                println "---"
                                if (oldSampleType != seqsample.sampleType) {
                                    //warn us
                                    log.warn('Overwrote sample type of SeqSample Id' + seqsample.id + ' from ' + oldSampleType + ' to ' + seqsample.sampleType + ' when adding to SeqRelation Id ' + seqRelationInstance.id)
                                    println ('Overwrote sample type of SeqSample Id' + seqsample.id + ' from ' + oldSampleType + ' to ' + seqsample.sampleType + ' when adding to SeqRelation Id ' + seqRelationInstance.id)
                                    warnings.add('Overwrote sample type of SeqSample ' + seqsample.sampleName + ' from ' + oldSampleType?oldSampleType:"none" + ' to ' + seqsample.sampleType)
                                }
                            }
                        }
                    }
                }
            }
        }

        return warnings

    }

    //  called by RemoteFunction from _form.gsp
    //
    def updateSeqSamplesForSeqrun = {
        def seqrun = params['seqrun']
        def seqsamples = SeqSample.findAllBySeqrun(Seqrun.findBySeqrun(seqrun))
        render(template:'seqsamplelist', model:[seqsamples:seqsamples])
    }

    //  called by RemoteFunction from _form.gsp
    //
    def getSampleTypeBySampleNameAndSeqrun = {
        def ss = params['seqsample']
        def sr = params['seqrun']
        SeqSample sSample
        println ss
        println sr
        println "sfsdf"
        def thisrun = Seqrun.findBySeqrun(sr)
        if (thisrun) {
            sSample = SeqSample.findBySampleNameAndSeqrun(ss, thisrun)
        }

        if (sSample) {
            render(template:'sampletypelist', model:[sampletype:sSample.sampleType])
        } else {
            return null
        }

        //render(template:'seqsamplelist', model:[seqsamples:seqsamples])

    }

}
