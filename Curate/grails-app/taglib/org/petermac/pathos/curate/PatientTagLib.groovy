/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.util.Environment

class PatientTagLib
{
    def springSecurityService

    def patient =
    {
        attr ->
            if ( attr.patient )
            {
                def env  = Environment.getCurrentEnvironment().name
                Patient pat = Patient.get(attr.patient)

                //  Obfuscate Patient details for non-Admin and non-Curator users
                //
                def currentUser = springSecurityService?.currentUser as AuthUser

                if ( currentUser && currentUser.authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_CURATOR" } )
                {
                    if (attr.value == 'name' ) out << """${pat.fullName}"""
                    if (attr.value == 'dob'  ) out << """${formatDate( date:pat.dob, format:'dd-MMM-yyyy')}"""
                    if (attr.value == 'urn'  ) out << """${pat.urn}"""
                }
                else
                {
                    if (attr.value == 'name' ) out << """A Patient ${pat?.id}"""
                    if (attr.value == 'dob'  ) out << """${formatDate( date:new Date(), format:'dd-MMM-yyyy')}"""
                    if (attr.value == 'urn'  ) out << """12/34/567"""
                }
            }
    }
}
