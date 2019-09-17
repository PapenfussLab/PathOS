package org.petermac.pathos.curate


import org.grails.plugin.easygrid.Filter
import static org.grails.plugin.easygrid.GormUtils.applyFilter
import org.grails.plugin.filterpane.FilterPaneUtils
import org.petermac.pathos.pipeline.UrlLink
import org.springframework.dao.DataIntegrityViolationException
import org.grails.plugin.easygrid.Easygrid

@Easygrid
class CivicClinicalEvidenceController {

    static scaffold = true
    def SpringSecurityService

    /**
     * Table definition for Easygrid
     */
    def civicClinicalEvidenceGrid =
            {
                dataSourceType  'gorm'
                domainClass     CivicClinicalEvidence
                enableFilter    true
                editable        false
                inlineEdit      false

                //  Export parameters
                //
                export
                        {
                            export_title  'CivicClinicalEvidence'
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
                            entrez
                            {
                                filterClosure
                                { Filter filter ->
                                    applyFilter(delegate, filter.operator, 'gene', filter.value)
                                }
                                value { v -> return [v.gene, v.entrez] }
                            }
                            civicVariant
                            disease {
                                filterClosure
                                { Filter filter ->
                                    applyFilter(delegate, filter.operator, 'disease', filter.value)
                                }
                                value { v -> return [v.disease, v.doid] }
                            }
                            drugs
                            pmid {
                                filterClosure
                                { Filter filter ->
                                    applyFilter(delegate, filter.operator, 'pmid', filter.value)
                                }
                                value { v -> return [v.pmid, v.citation] }
                            }
                            clinical_significance
                            evidence_type
                            evidence_direction
                            evidence_level
                            evidence_statement
                            evidence_status
                            rating
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
        [civicClinicalEvidenceList: CivicClinicalEvidence.list( params ), filterParams: FilterPaneUtils.extractFilterParams(params) ]
    }

    def filter = {
        if(!params.max) params.max = 25
        render( view: 'list',
                model:[ civicClinicalEvidenceList:    filterPaneService.filter( params, CivicClinicalEvidence ),
                        civicClinicalEvidenceCount:   filterPaneService.count( params, CivicClinicalEvidence ),
                        filterParams:                       FilterPaneUtils.extractFilterParams(params),
                        params:                             params
                ]
        )
    }

    def create() {
        [civicClinicalEvidenceInstance: new CivicClinicalEvidence(params)]
    }

    def save() {
        def clinicalEvidenceInstance = new CivicClinicalEvidence(params)
        if (!clinicalEvidenceInstance.save(flush: true)) {
            render(view: "create", model: [civicClinicalEvidenceInstance: clinicalEvidenceInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'civicClinicalEvidence.label', default: 'CivicClinicalEvidence'), clinicalEvidenceInstance.id])
        redirect(action: "show", id: clinicalEvidenceInstance.id)
    }

    def show(Long id) {
        def clinicalEvidenceInstance = CivicClinicalEvidence.get(id)
        if (!clinicalEvidenceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'civicClinicalEvidence.label', default: 'CivicClinicalEvidence'), id])
            redirect(action: "list")
            return
        }

        [civicClinicalEvidenceInstance: clinicalEvidenceInstance]
    }

    def edit(Long id) {
        def clinicalEvidenceInstance = CivicClinicalEvidence.get(id)
        if (!clinicalEvidenceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'civicClinicalEvidence.label', default: 'CivicClinicalEvidence'), id])
            redirect(action: "list")
            return
        }

        [civicClinicalEvidenceInstance: clinicalEvidenceInstance]
    }

    def update(Long id, Long version) {
        def clinicalEvidenceInstance = CivicClinicalEvidence.get(id)
        if (!clinicalEvidenceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'civicClinicalEvidence.label', default: 'CivicClinicalEvidence'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (clinicalEvidenceInstance.version > version) {
                clinicalEvidenceInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'civicClinicalEvidence.label', default: 'CivicClinicalEvidence')] as Object[],
                          "Another user has updated this CivicClinicalEvidence while you were editing")
                render(view: "edit", model: [civicClinicalEvidenceInstance: clinicalEvidenceInstance])
                return
            }
        }

        clinicalEvidenceInstance.properties = params

        if (!clinicalEvidenceInstance.save(flush: true)) {
            render(view: "edit", model: [clinicalEvidenceInstance: clinicalEvidenceInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'civicClinicalEvidence.label', default: 'CivicClinicalEvidence'), clinicalEvidenceInstance.id])
        redirect(action: "show", id: clinicalEvidenceInstance.id)
    }

    def delete(Long id) {
        def clinicalEvidenceInstance = CivicClinicalEvidence.get(id)
        if (!clinicalEvidenceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'civicClinicalEvidence.label', default: 'CivicClinicalEvidence'), id])
            redirect(action: "list")
            return
        }

        try {
            clinicalEvidenceInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'civicClinicalEvidence.label', default: 'CivicClinicalEvidence'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'civicClinicalEvidence.label', default: 'CivicClinicalEvidence'), id])
            redirect(action: "show", id: id)
        }
    }
}
