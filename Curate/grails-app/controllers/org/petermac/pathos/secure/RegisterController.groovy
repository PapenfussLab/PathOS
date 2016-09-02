package org.petermac.pathos.secure

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.authentication.dao.NullSaltSource
import grails.plugin.springsecurity.ui.RegistrationCode

class RegisterController extends grails.plugin.springsecurity.ui.RegisterController {

    def register() {
        redirect( controller: "user" , action: "auth")
        return false
    }
    def index() {
        redirect( controller: "user" , action: "auth")
        return false
    }

    def resetPassword(ResetPasswordCommand command) {

        String token = params.t

        def registrationCode = token ? RegistrationCode.findByToken(token) : null
        if (!registrationCode) {
            flash.error = message(code: 'spring.security.ui.resetPassword.badCode')
            redirect uri: SpringSecurityUtils.securityConfig.successHandler.defaultTargetUrl
            return
        }

        if (!request.post) {
            return [token: token, command: new ResetPasswordCommand()]
        }

        command.username = registrationCode.username
        command.validate()

        if (command.hasErrors()) {
            return [token: token, command: command]
        }

        String salt = saltSource instanceof NullSaltSource ? null : registrationCode.username
        RegistrationCode.withTransaction { status ->
            String usernameFieldName = SpringSecurityUtils.securityConfig.userLookup.usernamePropertyName
            def user = lookupUserClass().findWhere((usernameFieldName): registrationCode.username)
            user.password = springSecurityUiService.encodePassword(command.password, salt)
            user.passwordExpired = 0
            user.save()
            registrationCode.delete()
        }

        springSecurityService.reauthenticate registrationCode.username

        flash.message = message(code: 'spring.security.ui.resetPassword.success')

        def conf = SpringSecurityUtils.securityConfig
        String postResetUrl = conf.ui.register.postResetUrl ?: conf.successHandler.defaultTargetUrl
        redirect uri: postResetUrl
    }

}

class ResetPasswordCommand {
    String username
    String password
    String password2

    static constraints = {
        password blank: false, validator: RegisterController.passwordValidator
        password2 validator: RegisterController.password2Validator
    }
}
