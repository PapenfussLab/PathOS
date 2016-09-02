/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.report

import groovy.text.GStringTemplateEngine

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Create a report description
 *
 * Author:  Ken Doig
 * Date:    20-Dec-2015
 */
class Template
{
    static String fillTemplate( String template, Map params )
    {
        //  Convert template file and bind to context variables
        //
        def engine     = new GStringTemplateEngine()
        def reportDesc = engine.createTemplate(template).make(params).toString()

        return reportDesc
    }
}
