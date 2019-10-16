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


    private class Status {
        String title = "Unknown"
        String symbol = "tree"
        String link = "#noLink"
        String status = "halt"
    }

    private def display(Status status) {

        return "<a class='${status.status}' title='${status.title}' href='${status.link}'><i class='fa fa-${status.symbol}' aria-hidden='true'></i></a>"
    }

}















