/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: David Ma
 */



package org.petermac.pathos.curate

class SeqrunTagLib
{
    def seqrunLink =
    { attr ->
        out << """<a href='${UtilService.context()}/seqrun/show/${attr.seqrun}'>${attr.seqrun}</a>"""
    }
}















