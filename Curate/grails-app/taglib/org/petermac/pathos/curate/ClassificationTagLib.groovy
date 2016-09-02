/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

class ClassificationTagLib
{
    /**
     * Tabular colouring for 5 class IARC variant classification
     *
     * Todo: make this generic for form fields as well
     */
    def varClass =
    {
        attr ->
            if ( attr.class )
            {
                def cls = 'none'
                def fg  = '#000000'
                def bg  = '#ffffff'

                def match = ( attr.class =~ /C([1-5]): / )
                if ( match.count == 1)
                {
                    cls = match[0][1]
                }


                //  Colourings for classification severity
                //  Colours derived from http://colorbrewer2.org/
                //
                switch (cls)
                {
                    case ('1'): fg = '#000000'; bg = '#fffdc1'; break
                    case ('2'): fg = '#000000'; bg = '#f4d374'; break
                    case ('3'): fg = '#000000'; bg = '#e89e53'; break
                    case ('4'): fg = '#ffffff'; bg = '#d65430'; break
                    case ('5'): fg = '#ffffff'; bg = '#ae2334'; break
                    default   : fg = '#000000'; bg = '#ffffff';
                }

                out << """<noop style="color: ${fg}; margin-right:-2px; margin-left:-2px; padding:4px; background-color: ${bg}">${attr.class}</noop>"""
            }
    }
}
