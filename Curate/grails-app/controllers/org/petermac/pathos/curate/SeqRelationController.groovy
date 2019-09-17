package org.petermac.pathos.curate



import org.grails.plugin.filterpane.FilterPaneUtils
import org.springframework.dao.DataIntegrityViolationException

class SeqRelationController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def filterPaneService
    def DerivedSampleService

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

    /**
     * given an encoded sample from our edit or create form, return the actual seqsample object
     */
    SeqSample parseFormToSeqSample(v) {
        def slurper = new groovy.json.JsonSlurper()

        def newSs

        try {
            newSs = slurper.parseText(v)
            String srun = newSs.seqrun.size() > 0 ?  newSs.seqrun[0] :""
            String ssname = newSs.seqsample.size() > 0 ?  newSs.seqsample[0] :""
            Seqrun run = Seqrun.findBySeqrun(srun)

            def seqsample = SeqSample.findBySeqrunAndSampleName(run,ssname)
            return seqsample
        } catch (Exception e) {
            log.warn("Error while parsing new samples to be added: trying to parse malformed JSON string " + v)
            return null
        }
    }

    Integer parseFormGetOrder(v) {
        def slurper = new groovy.json.JsonSlurper()

        def newSs

        try {
            newSs = slurper.parseText(v)
            Integer order = newSs.order.size() > 0 ?  newSs.order[0] :""
            return order
        } catch (Exception e) {
            log.warn("Error while parsing new samples to be added: trying to parse malformed JSON string " + v)
            return null
        }


    }

    /**
     * derived sample information is implied by name
     * given an sr, get its derived sample
     * @param sr
     * @return
     */
    SeqSample findDerivedSampleForSeqRelation(SeqRelation sr) {

        //  assemble list of samples, refuse if not pair or if srs dont match
        def samples = []
        for (s in sr.samples()) samples.add(s)
        if(samples.size() != 2) return null
        if(samples[0].seqrun != samples[1].seqrun)  return null

        //  search by name
        def srun = samples[0].seqrun
        def dss = new DerivedSampleService()
        def derivedName = dss.derivedSampleName(sr.relation.toString().toLowerCase(),samples)
        def derivedSample = SeqSample.findBySeqrunAndSampleNameAndSampleType(srun,derivedName,'Derived')


        return derivedSample
    }


    def save() {
        def derived = null

        //  if we have a Derived relationship, create a derived seqsample
        //
        if(['Minus','Intersect','Union'].contains(params.relation)) {

            int countNewSamples = 0
            def allSs = new ArrayList<SeqSample>()

            //  grab the add params. we have two samples - get the order, smallest order goes
            //  in front of the list
            def prevOrder = -1
            params.each { k, v ->
                if (k.toString().startsWith('add_')) {
                    def seqsample = parseFormToSeqSample(v)
                    def order = parseFormGetOrder(v)


                    if(order < prevOrder) {     //this sample should be first in list
                        allSs.add(0,seqsample)
                        println "order ${order} goes first for " + seqsample
                    } else {
                        allSs.add(seqsample)    //this sample should be second in list
                        println "order ${order} goes second for " + seqsample
                    }
                    prevOrder = order
                }
            }
            println "Our list:"
            println allSs

            //  a bunch of safety checks
            //
            if (allSs.size() != 2) {
                flash.message = "Cannot create a Derived SeqRelation - need exactly two samples "
                render(view: "create", model: [seqRelationInstance: new SeqRelation(params)])
                return
            }

            if (allSs[0].seqrun != allSs[1].seqrun) {
                flash.message = "Samples for a Derived SeqRelation must have the same seqrun"
                render(view: "create", model: [seqRelationInstance: new SeqRelation(params)])
                return
            }
            def dss = new DerivedSampleService()

            //  check if this exists
            if(SeqSample.findBySampleNameAndSeqrun(dss.derivedSampleName(params.relation.toLowerCase(),[allSs[0],allSs[1]]),allSs[0].seqrun)) {
                flash.message = "Error:  unable to create derived sample, sample with this name already exists"
                render(view: "create", model: [seqRelationInstance: new SeqRelation(params)])
                return null
            }


            //create a derived sample now, based on the relation
            derived = dss.createDerivedSample(params.relation.toString().toLowerCase(),[allSs[0],allSs[1]])

        }


        def seqRelationInstance = new SeqRelation(params)
        println params
        if (!seqRelationInstance.save(flush: true, failOnError: true)) {
            println "failed to save"
            render(view: "create", model: [seqRelationInstance: seqRelationInstance])
            return
        }
        println "saved"

        // add any samples passed from params to the SeqRelation
        //
        def warnings = addSamplesToSeqRelation(params,seqRelationInstance)

        // add our new derived sample
        //
        if ( derived ) {

            seqRelationInstance.save(flush: true,failOnError:true)
            derived.save(flush:true,failOnError:true)
            seqRelationInstance.setDerivedSampleName(derived.sampleName)
            seqRelationInstance.setDerivedSampleSeqrunName(derived.seqrun.seqrun)
        }

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

        //  see if there is a derived sample
        //def derivedSample = findDerivedSampleForSeqRelation(seqRelationInstance)
        def derivedSample = seqRelationInstance.derivedSample() //todo fix view an dont pass this explicit
        [seqRelationInstance: seqRelationInstance, derivedSample: derivedSample]
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

        if (seqRelationInstance.derivedSample()) {
            def derSample = SeqSample.get(seqRelationInstance.derivedSample().id) //check if SeqSample stored in derivedsample exists
            if (derSample) {
                flash.message = "Cannot delete SeqRelation - a Derived sample exists and must be deleted first"
                redirect(action: "list")
                return
            }
        }
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
     * @return  warnings a List of warning messages
     */
    private List<String> addSamplesToSeqRelation(Map params, SeqRelation seqRelationInstance) {

        //  we also set sample types to (T/N/TN) if a TumourNomrla seqrelation
        //
        def updateTNSampleTypes = false
        if (seqRelationInstance.relation == 'TumourNormal') updateTNSampleTypes = true

        def warnings = []

        //  cycle through samplename seqrun pairs to be added (passed as json) and add the seqrun
        //
        params.each{ k, v ->
            if (k.toString().startsWith('add_')) {  //these are seqsampels to add

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
                    String stype = newSs.sampletype.size() > 0 ?  newSs.sampletype[0] :""
                    SeqSample seqsample = parseFormToSeqSample(v)

                    if(seqsample) {
                        def oldSampleType = seqsample.sampleType
                        //seqRelationInstance.addToSamples(seqsample)
                        seqsample.addToRelations(seqRelationInstance)

                        if(stype) {

                            seqsample.setSampleType(stype)

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
