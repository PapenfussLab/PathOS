/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.pathos.curate

class QcTagLib
{
    def qcPassFail =
    {
        attr ->
            if ( attr.authorised )
            {
                if ( ! attr.authorised )
                {
                    //  Not authorised yet
                    //
                    out << 'QC not authorised'
                    return
                }

                //  Set Pass/Fail flag colours
                //
                def fg = '#ffffff'
                def bg = '#af0000'
                def passfail = "Failed"

                if ( attr.passfailFlag )
                {
                    fg = '#ffffff'
                    bg = '#00af00'
                    passfail = "Passed"
                }

                //  Pass Fail
                //
                out << """<noop style="color: ${fg}; background-color: ${bg};padding-left: 10px;padding-right: 7px">${passfail}</noop>"""
            }
    }
}
