/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class Evidence
{
    Boolean     pathAloneTruncating     = false
    Boolean     pathAloneKnown          = false

    Boolean     pathStrongFunction      = false
    Boolean     pathStrongCase          = false
    Boolean     pathStrongCoseg         = false

    Boolean     pathSupportHotspot      = false
    Boolean     pathSupportGene         = false
    Boolean     pathSupportInsilico     = false
    Boolean     pathSupportSpectrum     = false
    Boolean     pathSupportGmaf         = false
    Boolean     pathSupportIndel        = false
    Boolean     pathSupportNovelMissense= false
    Boolean     pathSupportLsdb         = false
    Boolean     pathSupportCoseg        = false

    Boolean     benignAloneGmaf         = false
    Boolean     benignAloneHealthy      = false

    Boolean     benignStrongFunction    = false
    Boolean     benignStrongCase        = false
    Boolean     benignStrongCoseg       = false

    Boolean     benignSupportVariable   = false
    Boolean     benignSupportInsilico   = false
    Boolean     benignSupportSpectrum   = false
    Boolean     benignSupportLsdb       = false
    Boolean     benignSupportPath       = false

    String      evidenceClass           = "Unclassified"
    String      justification


    static mapping =
        {
            justification       (type: 'text')
            
        }

    static constraints =
        {
            justification       ( nullable: true )

            pathAloneTruncating nullable: true
            pathAloneKnown nullable: true

            pathStrongFunction nullable: true
            pathStrongCase nullable: true
            pathStrongCoseg nullable: true

            pathSupportHotspot nullable: true
            pathSupportGene nullable: true
            pathSupportInsilico nullable: true
            pathSupportSpectrum nullable: true
            pathSupportGmaf nullable: true
            pathSupportIndel nullable: true
            pathSupportCoseg nullable: true
            pathSupportLsdb nullable: true
            pathSupportNovelMissense nullable: true

            benignAloneGmaf nullable: true
            benignAloneHealthy nullable: true

            benignStrongFunction nullable: true
            benignStrongCase nullable: true
            benignStrongCoseg nullable: true

            benignSupportVariable nullable: true
            benignSupportInsilico nullable: true
            benignSupportSpectrum nullable: true
            benignSupportLsdb nullable: true
            benignSupportPath nullable: true

            evidenceClass(  inList: [
                                    "Unclassified",
                                    "C1: Not pathogenic",
                                    "C2: Unlikely pathogenic",
                                    "C3: Unknown pathogenicity",
                                    "C4: Likely pathogenic",
                                    "C5: Pathogenic"
                                    ])

        }

    static searchable = true
//    static  searchable =
//    {
//        only = [ 'justification' ]
//    }

    String	toString()
    {
        evidenceClass
    }
}
