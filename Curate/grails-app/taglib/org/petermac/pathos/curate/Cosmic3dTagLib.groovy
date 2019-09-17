/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 * Author: Love Chris
 * Description: Presents url to cosmic 3d for refgene list table
 *
 */



package org.petermac.pathos.curate

class Cosmic3dTagLib
{
    def cosmic3d =
    {
        attr ->
            if ( attr.gene )
            {
                def url = 'http://cancer.sanger.ac.uk/cosmic3d/protein/'+attr.gene
                out << """<a href="${url}" target="_blank">Cosmic 3D</a>"""
            }
    }
}
