package org.petermac.pathos.secure

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.authentication.dao.NullSaltSource
import org.petermac.pathos.curate.AuthUser

class UserController extends grails.plugin.springsecurity.ui.UserController {
    def search() {
        [enabled: 0, accountExpired: 0, accountLocked: 0, passwordExpired: 0]
    }
    def SpringSecurityService
    def show(Long id) {
            AuthUser userInstance = AuthUser.findById(id)
            if (!userInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'OldUser'), id])
                    redirect(action: "list")
                    return
                }

            def isAdmin

            def currentUser = springSecurityService.currentUser as AuthUser
            if (currentUser.authorities.any { it.authority == "ROLE_ADMIN"|| it.authority == "ROLE_DEV"  }) {
                isAdmin = true
            } else {
                isAdmin = false
            }
            [userInstance: userInstance, loggedInAdmin: isAdmin]
        }


    def userSearch() {

        boolean useOffset = params.containsKey('offset')
        setIfMissing 'max', 25, 25
        setIfMissing 'offset', 0

        def hql = new StringBuilder('FROM ').append(lookupUserClassName()).append(' u WHERE 1=1 ')
        def queryParams = [:]

        def userLookup = SpringSecurityUtils.securityConfig.userLookup
        String usernameFieldName = userLookup.usernamePropertyName
        String displayNameFieldName = "displayName" //userLookup.displayNamePropertyName
        String emailFieldName = "email" //userLookup.emailPropertyName
//        for (name in [username: usernameFieldName]) {
//            if (params[name.key]) {
//                hql.append " AND LOWER(u.${name.value}) LIKE :${name.key}"
//                queryParams[name.key] = params[name.key].toLowerCase() + '%'
//            }
//        }
        for (name in [username: usernameFieldName,displayName: displayNameFieldName,email: emailFieldName]) {
            if (params[name.key]) {
                hql.append " AND LOWER(u.${name.value}) LIKE :${name.key}"
                queryParams[name.key] = params[name.key].toLowerCase() + '%'
            }
        }



        String enabledPropertyName = userLookup.enabledPropertyName
        String accountExpiredPropertyName = userLookup.accountExpiredPropertyName
        String accountLockedPropertyName = userLookup.accountLockedPropertyName
        String passwordExpiredPropertyName = userLookup.passwordExpiredPropertyName

        for (name in [enabled: enabledPropertyName,
                accountExpired: accountExpiredPropertyName,
                accountLocked: accountLockedPropertyName,
                passwordExpired: passwordExpiredPropertyName]) {
            Integer value = params.int(name.key)
            if (value) {
                hql.append " AND u.${name.value}=:${name.key}"
                queryParams[name.key] = value == 1
            }
        }

        int totalCount = lookupUserClass().executeQuery("SELECT COUNT(DISTINCT u) $hql", queryParams)[0]

        Integer max = params.int('max')
        Integer offset = params.int('offset')

        String orderBy = ''
        if (params.sort) {
            orderBy = " ORDER BY u.$params.sort ${params.order ?: 'ASC'}"
        }

        def results = lookupUserClass().executeQuery(
                "SELECT DISTINCT u $hql $orderBy",
                queryParams, [max: max, offset: offset])




        def resultsAll

        if (results.size() == max) { //do a second query to get all users for our mailTo link
                resultsAll = lookupUserClass().executeQuery(
                "SELECT DISTINCT u $hql $orderBy",
                queryParams)
        } else { //if we have less results than max, this saves us running a query
            resultsAll = results
        }

        //build a string of emails for am mailto link
        def String mailtoString = ''
        for (foundUser in resultsAll) {
            mailtoString = mailtoString + foundUser.email + ','
        }
        if (mailtoString.length() > 1) {
            mailtoString = mailtoString.substring(0, mailtoString.length() - 1)
        }

        def model = [results: results, totalCount: totalCount, searched: true, mailtoString: mailtoString]

        // add query params to model for paging
        for (name in ['username', 'displayName','email','enabled', 'accountExpired', 'accountLocked',
                'passwordExpired', 'sort', 'order']) {
            model[name] = params[name]
        }

        render view: 'search', model: model
    }




    def save() {
        def user = lookupUserClass().newInstance(params)
        if (params.password) {
            String salt = saltSource instanceof NullSaltSource ? null : params.username
            user.password = springSecurityUiService.encodePassword(params.password, salt)
        }
        if (!user.save(flush: true)) {
            render view: 'create', model: [user: user, authorityList: sortedRoles()]
            return
        }

        addRoles(user)
        flash.message = "${message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), user.id])}"
        redirect action: 'edit', id: user.id
    }

    def edit() {
        String usernameFieldName = SpringSecurityUtils.securityConfig.userLookup.usernamePropertyName

        def user = params.username ? lookupUserClass().findWhere((usernameFieldName): params.username) : null
        if (!user) user = findById()
        if (!user) return

        return buildUserModel(user)
    }

    def update() {
        String passwordFieldName = SpringSecurityUtils.securityConfig.userLookup.passwordPropertyName

        def user = findById()
        if (!user) return
        if (!versionCheck('user.label', 'User', user, [user: user])) {
            return
        }

        def oldPassword = user."$passwordFieldName"
        user.properties = params
        if (params.password && !params.password.equals(oldPassword)) {
            String salt = saltSource instanceof NullSaltSource ? null : params.username
            user."$passwordFieldName" = springSecurityUiService.encodePassword(params.password, salt)
        }

        if (!user.save(flush: true)) {
            render view: 'edit', model: buildUserModel(user)
            return
        }

        String usernameFieldName = SpringSecurityUtils.securityConfig.userLookup.usernamePropertyName

        lookupUserRoleClass().removeAll user
        addRoles user
        userCache.removeUserFromCache user[usernameFieldName]
        flash.message = "${message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), user.id])}"
        redirect action: 'edit', id: user.id
    }
}
