/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.clarity

import groovy.util.logging.Log4j

/*	ListSamples.groovy
 *
 *	List all LIMS samples
 *
 */

@Log4j
class ListSamples
{
    static main( args )
    {
        def clarity = new Clarity( 'test')

        //The base URI for this class instance to use.  Replace server and IP with accurate information.
        URI baseURI = new URI( clarity.limsHost + '/samples/' )

        //Get a single Project by limsid
        log.debug( "Request UIR " + baseURI )
        Node samples = GLSRestApiUtils.httpGET(baseURI.toString(), clarity.user, clarity.pass)

        samples.each
                {
                    println "LIMSid:\t" + it.@limsid
                }
    }
}