/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.converters.JSON
import groovy.util.logging.Log4j

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Methos to link SeqVariant to CurVariant
 *
 * User: Ken Doig
 * Date: 11/10/2013
 */

@Log4j
class SeqVariantService
{
    /**
     * Retrieve VarLinks as JSON
     *
     *
     */
    static String getVarLinks( SeqVariant sv )
    {
        return sv?.varLinks as JSON ?: '{}'
    }
}
