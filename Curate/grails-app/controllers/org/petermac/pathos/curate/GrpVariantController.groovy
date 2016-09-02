package org.petermac.pathos.curate



import org.grails.plugin.filterpane.FilterPaneUtils
import org.springframework.dao.DataIntegrityViolationException

class GrpVariantController {
    def springSecurityService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def filterPaneService

    def list(Integer max) {
        params.max = Math.min(max ?: 25, 100)
        if(!params.max) params.max = 10
        [ grpVariantList: GrpVariant.list( params ), filterParams: FilterPaneUtils.extractFilterParams(params) ]
    }

    def filter = {
        if(!params.max) params.max = 25
        render( view: 'list',
                model:[ grpVariantList:    filterPaneService.filter( params, GrpVariant ),
                        grpVariantCount:   filterPaneService.count( params, GrpVariant ),
                        filterParams:                       FilterPaneUtils.extractFilterParams(params),
                        params:                             params
                ]
        )
    }

    def create() {

        [grpVariantInstance: new GrpVariant(params)]
    }

    def save() {
        def grpVariantInstance = new GrpVariant(params)
        grpVariantInstance.createdBy = springSecurityService.currentUser as AuthUser


        if (!grpVariantInstance.save(flush: true)) {
            render(view: "create", model: [grpVariantInstance: grpVariantInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'grpVariant.label', default: 'GrpVariant'), grpVariantInstance.id])
        redirect(action: "show", id: grpVariantInstance.id)
    }

    def show(Long id) {
        def grpVariantInstance = GrpVariant.get(id)
        if (!grpVariantInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'grpVariant.label', default: 'GrpVariant'), id])
            redirect(action: "list")
            return
        }

        [grpVariantInstance: grpVariantInstance]
    }

    def edit(Long id) {
        def grpVariantInstance = GrpVariant.get(id)
        if (!grpVariantInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'grpVariant.label', default: 'GrpVariant'), id])
            redirect(action: "list")
            return
        }

        [grpVariantInstance: grpVariantInstance]
    }

    def update(Long id, Long version) {
        def grpVariantInstance = GrpVariant.get(id)
        if (!grpVariantInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'grpVariant.label', default: 'GrpVariant'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (grpVariantInstance.version > version) {
                grpVariantInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'grpVariant.label', default: 'GrpVariant')] as Object[],
                          "Another user has updated this GrpVariant while you were editing")
                render(view: "edit", model: [grpVariantInstance: grpVariantInstance])
                return
            }
        }

        grpVariantInstance.properties = params

        if (!grpVariantInstance.save(flush: true)) {
            render(view: "edit", model: [grpVariantInstance: grpVariantInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'grpVariant.label', default: 'GrpVariant'), grpVariantInstance.id])
        redirect(action: "show", id: grpVariantInstance.id)
    }

    def delete(Long id) {
        def grpVariantInstance = GrpVariant.get(id)
        if (!grpVariantInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'grpVariant.label', default: 'GrpVariant'), id])
            redirect(action: "list")
            return
        }

        try {
            grpVariantInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'grpVariant.label', default: 'GrpVariant'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'grpVariant.label', default: 'GrpVariant'), id])
            redirect(action: "show", id: id)
        }
    }
}
