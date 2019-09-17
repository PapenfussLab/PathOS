/*
 * Copyright (c) 2018. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: David Ma
 */

package org.petermac.pathos.curate

import groovy.util.logging.Log4j

@Log4j
class UtilService
{
    def GrailsApplication

    String context() {
        String context = grailsApplication.metadata['app.context'] ?: "/PathOS"

        return "${context}"
    }



}









