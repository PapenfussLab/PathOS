package org.petermac.pathos.curate

class AuthTagLib {
    //this is a taglib to display AuthUser details. Since sec:loggedInUserInfo reads from GrailsUser
    //and not from our AuthUser, we need either a custom UserDetailsService or this taglib to show the
    //display name or the email.
    def springSecurityService


    static namespace = "authdetails"

    def displayName = { //attrs ->
        def thisauthuser = springSecurityService.currentUser as AuthUser
        out << "${thisauthuser.getDisplayName()}"
    }


    def roles = { //attrs ->

        def roles = springSecurityService.getPrincipal().getAuthorities()
        String roleList = roles.toString().replaceAll("ROLE_",'')
        out << "${roleList}"
    }

    def id = {
        out << "${springSecurityService.currentUser.id}"
    }
}
