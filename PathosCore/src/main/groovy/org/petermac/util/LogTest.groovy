/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.util

//  Test logging works kdd

import groovy.util.logging.Log4j

@Log4j
class LogTest {
    def dosome() {
        log.info( 'Logging!' )
        log.warn( 'Warning!' )
        log.error( 'Error!' )
        log.fatal( 'Fatal!' )
    }

    static main( args ) {
        new LogTest().dosome()

        println "Done!"
    }
}
