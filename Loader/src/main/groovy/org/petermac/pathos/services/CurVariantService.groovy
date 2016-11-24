/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import groovy.util.logging.Log4j

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Methos to link SeqVariant to CurVariant
 *
 * User: Ken Doig
 * Date: 11/10/2013
 */

@Log4j
class CurVariantService
{
    /**
     * Find all CurVariant matching a SeqVariants genomic position
     *
     * @param   sv  SeqVariant to match
     * @return      List of CurVariants with a genomic locus matching SeqVariant
     */
    static List<CurVariant> findCurVariantsByGenomic( SeqVariant sv )
    {
        def cvs =   CurVariant.withCriteria
                    {
                        eq( "hgvsg", sv.hgvsg )
//                        eq( "grpVariant.accession", sv.hgvsg )
//                        eq( "grpVariant.accession", sv.hgvsc )
//                        eq( "grpVariant.accession", sv.hgvsp )
                    }

        log.info( "In findCurVariantsByGenomic(sv=${sv}) = cvs[${cvs?.size()}]" )

        return cvs
    }
}
