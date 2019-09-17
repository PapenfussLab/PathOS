/*
 * Copyright (c) 2019. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: David Ma
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

/**
 * Domain Class for CurVariant weights
 */
@Entity
class CvWeight
{
    String      guideline
    String      classification
    Integer     weight

    String	toString()
    {
        "${guideline} ${classification}"
    }
}
