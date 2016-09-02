/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.pathos.curate

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Controls the Evidence curation/classification process for variants
 *
 * User: Kenneth Doig
 * Date: 29/08/13
 */

class EvidenceService
{

    /**
     * Algorithmic classification based on Evidence object
     *
     * @param   evidence  Evidence object to classify
     * @return            IARC 5 class classification string
     */
    String inferClass( Evidence evidence)
    {
        Map m = countCriteria( evidence )
        log.debug( "after count: ${m}")

        if ( m.pathAlone >= 1 || m.pathStrong >= 2 || m.pathStrong == 1 && m.pathSupport >= 3)
            return "C5: Pathogenic"

        if ( m.pathStrong == 1 && m.pathSupport >= 2 || m.pathSupport >= 4 )
            return "C4: Likely pathogenic"

        if ( m.benignAlone >= 1 || m.benignStrong >= 2 || m.benignStrong == 1 && m.benignSupport >= 3)
            return "C1: Not pathogenic"

        if ( m.benignStrong == 1 && m.benignSupport >= 2 || m.benignSupport >= 4 )
            return "C2: Unlikely pathogenic"

        return "C3: Unknown pathogenicity"
    }

    /**
     * Count the types of evidence for a CurVariant
     *
     * @param   evidence    Evidence object to count
     * @return              Map of evidence type counts
     */
    private static Map countCriteria( Evidence evidence )
    {
        Map m = [:]
        m.pathAlone     = 0
        m.pathStrong    = 0
        m.pathSupport   = 0
        m.benignAlone   = 0
        m.benignStrong  = 0
        m.benignSupport = 0

        if ( evidence.pathAloneTruncating )     m.pathAlone++
        if ( evidence.pathAloneKnown )          m.pathAlone++

        if ( evidence.pathStrongFunction )      m.pathStrong++
        if ( evidence.pathStrongCase )          m.pathStrong++
        if ( evidence.pathStrongCoseg )         m.pathStrong++

        if ( evidence.pathSupportHotspot )      m.pathSupport++
        if ( evidence.pathSupportGene )         m.pathSupport++
        if ( evidence.pathSupportInsilico )     m.pathSupport++
        if ( evidence.pathSupportSpectrum )     m.pathSupport++
        if ( evidence.pathSupportGmaf )         m.pathSupport++
        if ( evidence.pathSupportIndel )        m.pathSupport++
        if ( evidence.pathSupportNovelMissense )m.pathSupport++
        if ( evidence.pathSupportLsdb )         m.pathSupport++
        if ( evidence.pathSupportCoseg )        m.pathSupport++

        if ( evidence.benignAloneGmaf )         m.benignAlone++
        if ( evidence.benignAloneHealthy )      m.benignAlone++

        if ( evidence.benignStrongFunction )    m.benignStrong++
        if ( evidence.benignStrongCase )        m.benignStrong++
        if ( evidence.benignStrongCoseg )       m.benignStrong++

        if ( evidence.benignSupportVariable )   m.benignSupport++
        if ( evidence.benignSupportInsilico )   m.benignSupport++
        if ( evidence.benignSupportSpectrum )   m.benignSupport++
        if ( evidence.benignSupportLsdb )       m.benignSupport++
        if ( evidence.benignSupportPath )       m.benignSupport++

        return m
    }
}
