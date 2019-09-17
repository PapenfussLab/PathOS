package org.petermac.pathos.curate

class PreferencesTagLib {
    def springSecurityService

    static namespace = "preferences"

    def panelList = {
        AuthUser user = springSecurityService.currentUser as AuthUser
        Preferences preferences = Preferences.findByUser(user)
        String result = preferences?.panelList ? "panelList=${preferences.panelList}&" : ""

        out << result
    }

    def numberOfSeqruns = {
        AuthUser user = springSecurityService.currentUser as AuthUser
        Preferences preferences = Preferences.findByUser(user)
        String result = preferences?.numberOfSeqruns ?: 10

        out << result
    }

}
