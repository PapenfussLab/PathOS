package org.petermac.pathos.curate

import grails.converters.JSON

// Author: David Ma
// Date: 13-March-2018

class PreferencesController
{
    def SpringSecurityService

    private Preferences preferences() {
        AuthUser currentUser = springSecurityService.currentUser as AuthUser
        Preferences preferences = Preferences.findByUser(currentUser) as Preferences

        if(!preferences) {
            preferences = new Preferences([user: currentUser])
            preferences.save()
        }

        return preferences
    }

    private Preferences lookupPreferences() {

        AuthUser currentUser = springSecurityService.currentUser as AuthUser
        Preferences preferences = Preferences.findByUser(currentUser) as Preferences

        if(!preferences) {
            preferences = null
        }

        return preferences
    }

    def fetchPreferences() {
        HashMap result = [
            preferences: lookupPreferences()
        ]
        render result as JSON
    }

    def listOfPanels() {
        def result = Panel.list().collect { it.manifest }.sort()
        render result as JSON
    }

    def changeCompressedView() {
        Boolean compressedView = params.compressedView ? params.compressedView == "true" : false

        Preferences preferences = preferences()

        preferences.setCompressedView(compressedView)
        preferences.save()

        render preferences as JSON
    }

    def lookupPanelList() {
        Preferences preferences = lookupPreferences()

        render preferences?.panelList ?: ""
    }

    def changePanelList() {
        String panelList = params.panelList ?: ""

        Preferences preferences = preferences()

        preferences.setPanelList(panelList)
        preferences.save()

        render preferences as JSON
    }


    def lookupSvlistRows() {
        Preferences preferences = lookupPreferences()

        render preferences?.svlistRows ?: 200
    }

    def changeSvlistRows(Integer rows) {
        println "Hello"
        println rows
        Preferences preferences = preferences()

        preferences.setSvlistRows(rows)
        preferences.save()

        println preferences as JSON
        render preferences as JSON
    }

    def lookupNumberOfSeqruns() {
        Preferences preferences = lookupPreferences()

        render preferences?.numberOfSeqruns ?: 10
    }

    def changeNumberOfSeqruns(Integer numberOfSeqruns) {
        Preferences preferences = preferences()

        preferences.setNumberOfSeqruns(numberOfSeqruns)
        preferences.save()
        render preferences as JSON
    }

    def saveSettings() {
        if(request?.JSON) {
            def data = request.JSON

            println data

            Preferences preferences = preferences()

            preferences.setPanelList(data.panelList ?: "")
            preferences.setNumberOfSeqruns(Integer.parseInt(data.numberOfSeqruns) ?: 10)
            preferences.setCompressedView(data.compressedView ?: false)
//            preferences.setSkipGeneMask(data.skipGeneMask ?: false)
            preferences.setSortPriority(data.sortPriority ?: "acmgCurVariant,allCuratedVariants,ampCurVariant,overallCurVariant,reportable")
            preferences.setSvlistRows(Integer.parseInt(data.svlistRows) ?: 20)
            preferences.setD3heatmap(data.d3heatmap ?: false)






            preferences.save()

            render 200
        } else {
            render 500
        }
    }

    def deleteSettings() {
        Preferences preferences = lookupPreferences()
        try {
            preferences.delete()
            render 200
        } catch (Error e) {
            render e
        }
    }

}
