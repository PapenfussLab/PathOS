package org.petermac.pathos.curate




class ClinContextController {

    def springSecurityService
    static scaffold = ClinContext



    def save()
    {
        params.createdBy = springSecurityService.currentUser as AuthUser

        ClinContext cc = new ClinContext(params)
        if (!cc.save(flush: true))
        {
            render(view: "create", model: [clinContextInstance: cc])
            return
        }


        flash.message = /Clinical Context "${cc}" created/

        redirect(action: "show", id: cc.id)
    }
}
