package org.petermac.pathos.curate

import org.springframework.dao.DataIntegrityViolationException

class LabAssayController {
    static scaffold = LabAssay
    def AuditService

    def save() {
        def labAssayInstance = new LabAssay(params)
        if (!labAssayInstance.save(flush: true)) {
            render(view: "create", model: [labAssayInstance: labAssayInstance])
            return
        }

        String description = "Created a new Lab Assay ${labAssayInstance.toString()} with genes: ${labAssayInstance.genes}"
        AuditService.audit([
            category    : 'masking',
            task        : 'Lab Assay Created',
            description : description
        ])

        flash.message = description
        redirect(action: "show", id: labAssayInstance.id)
    }

    def update(Long id, Long version) {
        def labAssayInstance = LabAssay.get(id)
        if (!labAssayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'labAssay.label', default: 'LabAssay'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (labAssayInstance.version > version) {
                labAssayInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'labAssay.label', default: 'LabAssay')] as Object[],
                          "Another user has updated this LabAssay while you were editing")
                render(view: "edit", model: [labAssayInstance: labAssayInstance])
                return
            }
        }

        labAssayInstance.properties = params

//      A labAssay's gene list (genes) should be stored as unique, with no spaces and in upper case.
        if( params.genes ) {
            String genes = params.genes.toUpperCase().tokenize(', ').unique().sort().join(',')
            labAssayInstance.setGenes(genes)
        }

        if (!labAssayInstance.save(flush: true)) {
            render(view: "edit", model: [labAssayInstance: labAssayInstance])
            return
        }

        String description = "Edited Lab Assay ${labAssayInstance.toString()} with genes: ${labAssayInstance.genes}"
        AuditService.audit([
            category    : 'masking',
            task        : 'Lab Assay Edited',
            description : description
        ])

        flash.message = description
        redirect(action: "show", id: labAssayInstance.id)
    }

    def delete(Long id) {
        def labAssayInstance = LabAssay.get(id)
        if (!labAssayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'labAssay.label', default: 'LabAssay'), id])
            redirect(action: "list")
            return
        }
        String labAssay = labAssayInstance.toString()

        try {
            labAssayInstance.delete(flush: true)

            String description = "Deleted Lab Assay ${labAssay}"
            AuditService.audit([
                category    : 'masking',
                task        : 'Lab Assay Deleted',
                description : description
            ])
            flash.message = description

            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'labAssay.label', default: 'LabAssay'), id])
            redirect(action: "show", id: id)
        }
    }
}
