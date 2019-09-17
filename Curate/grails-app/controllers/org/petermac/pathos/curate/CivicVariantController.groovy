package org.petermac.pathos.curate

import grails.converters.JSON
import org.grails.plugin.easygrid.Filter
import static org.grails.plugin.easygrid.GormUtils.applyFilter
import org.grails.plugin.filterpane.FilterPaneUtils
import org.springframework.dao.DataIntegrityViolationException
import org.grails.plugin.easygrid.Easygrid

@Easygrid
class CivicVariantController {

    static scaffold = true
    def SpringSecurityService

    /**
     * Table definition for Easygrid
     */
    def civicVariantGrid =
            {
                dataSourceType  'gorm'
                domainClass     CivicVariant
                enableFilter    true
                editable        false
                inlineEdit      false

                //  Export parameters
                //
                export
                        {
                            export_title  'CivicVariant'
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
                            variant
                            {
                                filterClosure
                                { Filter filter ->
                                    applyFilter(delegate, filter.operator, 'variant', filter.value)
                                }
                                value { v -> return [v.variant, v.variant_civic_url] }
                            }
                            summary
                            hgvs_expressions
                            reference_bases
                            variant_bases
                            gene
                            entrez
                            chromosome
                            start
                            stop
                            representative_transcript
                            variant_types
                            last_review_date

                        }
            }

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def filterPaneService

    def list(Integer max) {
        params.max = Math.min(max ?: 25, 100)
        if(!params.max) params.max = 10
        [civicVariantList: CivicVariant.list( params ), filterParams: FilterPaneUtils.extractFilterParams(params) ]
    }

    def filter = {
        if(!params.max) params.max = 25
        render( view: 'list',
                model:[ civicVariantList:    filterPaneService.filter( params, CivicVariant ),
                        civicVariantCount:   filterPaneService.count( params, CivicVariant ),
                        filterParams:                       FilterPaneUtils.extractFilterParams(params),
                        params:                             params
                ]
        )
    }

    def create() {
        [civicVariantInstance: new CivicVariant(params)]
    }

    def save() {
        def civicVariantInstance = new CivicVariant(params)
        if (!civicVariantInstance.save(flush: true)) {
            render(view: "create", model: [civicVariantInstance: civicVariantInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'civicVariant.label', default: 'CivicVariant'), civicVariantInstance.id])
        redirect(action: "show", id: civicVariantInstance.id)
    }

    def show(Long id) {
        def civicVariantInstance = CivicVariant.get(id)
        if (!civicVariantInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'civicVariant.label', default: 'CivicVariant'), id])
            redirect(action: "list")
            return
        }

//        ArrayList<CivicClinicalEvidence> evidenceList = CivicClinicalEvidence.executeQuery("from CivicClinicalEvidence where civicVariant.id =:varID", [varID:this.id])

        ArrayList<CivicClinicalEvidence>  evidenceList = CivicClinicalEvidence.findAllByCivicVariant(civicVariantInstance)

        [civicVariantInstance: civicVariantInstance, evidenceList: evidenceList]
    }

    def edit(Long id) {
        def civicVariantInstance = CivicVariant.get(id)
        if (!civicVariantInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'civicVariant.label', default: 'CivicVariant'), id])
            redirect(action: "list")
            return
        }

        [civicVariantInstance: civicVariantInstance]
    }

    def update(Long id, Long version) {
        def variantInstance = CivicVariant.get(id)
        if (!civicVariantInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'civicVariant.label', default: 'CivicVariant'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (civicVariantInstance.version > version) {
                civicVariantInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'civicVariant.label', default: 'CivicVariant')] as Object[],
                          "Another user has updated this CivicVariant while you were editing")
                render(view: "edit", model: [civicVariantInstance: civicVariantInstance])
                return
            }
        }

        civicVariantInstance.properties = params

        if (!civicVariantInstance.save(flush: true)) {
            render(view: "edit", model: [civicVariantInstance: civicVariantInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'civicVariant.label', default: 'CivicVariant'), civicVariantInstance.id])
        redirect(action: "show", id: civicVariantInstance.id)
    }

    def delete(Long id) {
        def civicVariantInstance = CivicVariant.get(id)
        if (!civicVariantInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'civicVariant.label', default: 'CivicVariant'), id])
            redirect(action: "list")
            return
        }

        try {
            civicVariantInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'civicVariant.label', default: 'CivicVariant'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'civicVariant.label', default: 'CivicVariant'), id])
            redirect(action: "show", id: id)
        }
    }

}
