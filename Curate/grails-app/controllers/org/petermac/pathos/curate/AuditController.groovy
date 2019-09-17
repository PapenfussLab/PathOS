package org.petermac.pathos.curate



import org.grails.plugin.filterpane.FilterPaneUtils
import org.springframework.dao.DataIntegrityViolationException

class AuditController
{
    static scaffold = Audit

    static allowedMethods = []


    def index()
    {
        redirect(action: "list", params: params)
    }

    def filterPaneService

    def list(Integer max)
    {
        params.max = Math.min(max ?: 25, 100)
        params.order = "desc"
        params.sort  = "id"

        if (!params.max) params.max = 10
        [auditList: Audit.list(params), filterParams: FilterPaneUtils.extractFilterParams(params)]
    }

    def filter = {
        if (!params.max) params.max = 25
        params.order = "desc"
        params.sort  = "id"

        render(view: 'list',
                model: [auditList: filterPaneService.filter(params, Audit),
                        auditCount: filterPaneService.count(params, Audit),
                        filterParams: FilterPaneUtils.extractFilterParams(params),
                        params: params
                ]
        )
    }
}
