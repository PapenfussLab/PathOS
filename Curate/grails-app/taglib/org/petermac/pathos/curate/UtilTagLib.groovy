/*
 * Copyright (c) 2018. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: David Ma
 */



package org.petermac.pathos.curate

class UtilTagLib
{
    def utilService

//    Get the application context
    def context =
    { attr ->
        out << utilService.context()
    }
}
