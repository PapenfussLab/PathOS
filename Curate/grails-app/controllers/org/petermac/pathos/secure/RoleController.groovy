package org.petermac.pathos.secure

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.util.GrailsNameUtils
import org.springframework.dao.DataIntegrityViolationException

class RoleController extends grails.plugin.springsecurity.ui.RoleController {


    def save() {
        //disable role edit
       redirect action: 'search'
    }

    def delete() {
        //disable role edit
        redirect action: 'search'
    }

    def edit() {

        String upperAuthorityFieldName = GrailsNameUtils.getClassName(
                SpringSecurityUtils.securityConfig.authority.nameField, null)

        def role = params.name ? lookupRoleClass()."findBy$upperAuthorityFieldName"(params.name) : null
        if (!role) role = findById()
        if (!role) return

        //setIfMissing 'max', 10, 100  don't set a max: give us all users

        def roleClassName = GrailsNameUtils.getShortName(lookupRoleClassName())
        def userField = GrailsNameUtils.getPropertyName(GrailsNameUtils.getShortName(lookupUserClassName()))

        def users = lookupUserRoleClass()."findAllBy$roleClassName"(role, params)*."$userField"
        int userCount = lookupUserRoleClass()."countBy$roleClassName"(role)

        [role: role, users: users, userCount: userCount]
    }

}
