/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: David Ma DKGM
 * Date: 9-May-2017
 */



package org.petermac.pathos.curate

class ReportsTagLib
{
    def utilService

// Unused except for dev stuff
    def downloadPdfLink = { attr ->
        SeqSampleReport ssri = attr.ssri
        String output = "<a class='pdf' target='_blank' href='${utilService.context()}/seqVariant/preparedReport?fileExt=pdf&id=${ssri.id}' download='${ssri.seqSample.seqrun.seqrun}_${ssri.seqSample.sampleName}'>View PDF</a>"

        if ( ssri.reportFilePath != "none" ) {
            output = "<a class='pdf' target='_blank' href='${utilService.context()}/${ssri.reportFilePath.split(/(\/Generated\/|\/web-app\/|\/webapps\/)/)[1]}' download='${ssri.seqSample.seqrun.seqrun}_${ssri.seqSample.sampleName}'>View PDF</a>"
        }
        out << output
    }

// Unused except for dev stuff, e.g. seqSampleReport/inspect page
// perhaps you should test if the file exists
    def publishPdfLink = { attr ->
        SeqSampleReport ssri = attr.ssri
        String output = '<a class="list disabled" href="#">Publish</a>'

        if( ssri.reportFilePath == "none" ) {
            output = "<a id='publishButton' class='pdf' onclick='markPublished()' target='_blank' href='${utilService.context()}/seqVariant/preparedReport?fileExt=pdf&publish=1&id=${ssri.id}' download='${ssri.seqSample.seqrun.seqrun}_${ssri.seqSample.sampleName}'>Publish</a>"
        }
        out << output
    }

// Unused except for dev stuff
    def downloadWordLink = { attr ->
        SeqSampleReport ssri = attr.ssri
        String output = '<li><a class="word disabled" href="#">View WORD</a></li>'

        if( ssri.reportFilePath == "none" ) {
            output = "<a id='viewWord' class='word' target='_blank' href='${utilService.context()}/seqVariant/preparedReport?fileExt=docx&id=${ssri.id}' download='${ssri.seqSample.seqrun.seqrun}_${ssri.seqSample.sampleName}'>View WORD</a>"
        }
        out << output
    }

    def showTestSet = { attr ->
        String billingCode = attr.billingCode;
        String name = attr.name
        out << "<a href='${utilService.context()}/admin/reportUpload?code=${billingCode}&template=${name}'>${billingCode}</a>"
    }

    def qualityControl = { attr ->
        if ( attr.seqSample ) {
            SeqSample ss = attr.seqSample
            if ( ss.authorisedQcFlag ) {
                if ( ss.passfailFlag ) {
                    out << "QC Passed"
                } else {
                    out << "QC Failed"
                }
                out << " by ${ss.authorisedQc}"
            } else {
                out << "QC not set yet"
            }
        }
    }
}







