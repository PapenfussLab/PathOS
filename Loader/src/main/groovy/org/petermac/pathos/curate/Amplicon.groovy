/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

/**
 * Domain Class for an amplicon
 */
@Entity
class Amplicon
{
    String	amplicon
    String	panel
    String	chr
    String	startpos
    String	endpos
    String	primerlen1
    String	primerlen2

    static constraints =
    {
        panel()
        amplicon()
        chr()
        startpos()
        endpos()
        primerlen1()
        primerlen2()
    }

    String	toString()
    {
        "${panel}:${amplicon}"
    }
}
