/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 */

package org.petermac.pathos.curate

import grails.util.Holders
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.web.util.WebUtils

/**
 * Created by seleznev andrei on 13/11/2014.
 */


/*
 * Copyright (c) 2014. PathOS CurVariant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 *
 * This runs after a user logs on with Sprign Security and springSecurityService service is going.
 * Here we set a session var to the user's name so Tomcat can guess the logged in user for us
 */

import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent
import org.springframework.security.core.userdetails.UsernameNotFoundException

@Log4j
public class curateLoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {
    void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        def springSecurityService = Holders.grailsApplication.mainContext.getBean('springSecurityService')
        def currentUser = springSecurityService.currentUser as AuthUser

        def webUtils = WebUtils.retrieveGrailsWebRequest()
        def session = webUtils.getSession()
        if (currentUser) {

            def thisusername = currentUser.getUsername()
            session.setAttribute('userName', thisusername)
            springSecurityService.reauthenticate(currentUser.username)
            log.info( "${thisusername} logged in successfully" )

        }
        else {
            def guestusername = 'pathosguest'
            session.setAttribute('userName', guestusername)
            springSecurityService.reauthenticate(guestusername)
            log.info( "${guestusername} logged in successfully" )
        }



    }
}