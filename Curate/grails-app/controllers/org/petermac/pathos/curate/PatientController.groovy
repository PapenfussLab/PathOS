/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

class PatientController
{
    static scaffold = true


    def find(String urn) {
        Patient patient = Patient.findByUrn(urn)
        redirect(action: "show", id: patient.id)
    }

}
