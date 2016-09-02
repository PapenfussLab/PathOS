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

    static constraints =
        {
            justification( maxSize: 8000, nullable: true )

            pathAloneTruncating()
            pathAloneKnown()

            pathStrongFunction()
            pathStrongCase()
            pathStrongCoseg()

            pathSupportHotspot()
            pathSupportGene()
            pathSupportInsilico()
            pathSupportSpectrum()
            pathSupportGmaf()
            pathSupportIndel()
            pathSupportCoseg()
            pathSupportLsdb()
            pathSupportNovelMissense()

            benignAloneGmaf()
            benignAloneHealthy()

            benignStrongFunction()
            benignStrongCase()
            benignStrongCoseg()

            benignSupportVariable()
            benignSupportInsilico()
            benignSupportSpectrum()
            benignSupportLsdb()
            benignSupportPath()

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
