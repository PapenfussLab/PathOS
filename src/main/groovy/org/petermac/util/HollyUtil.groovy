/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */





package org.petermac.util

import groovy.json.JsonSlurper
import groovy.util.logging.Log4j

/**
 * Holly Class for REST API utilities
 *
 * Description:
 *
 * Methods to retrieve Holly data via REST
 *
 * Author:  Ken Doig
 * Date:    28-Oct-2015
 */

@Log4j
class HollyUtil
{
    static JsonSlurper slurper = new JsonSlurper()

    //  Todo: this should be a parameter
    //
    private static final def  hollypURL = 'http://bioinf-pathos/Holly/pathologist/'

    /**
     * Extract Pathologist review of a sample from Holly
     *
     * @param   sample      Episode number to query
     * @return              Map of entry [sample:, pathologist:, site:, morphology:
     */
    static Map getSample( String sample )
    {
        //  Construct URL returning XML
        //
        def url = hollypURL + "getPathologistSample?sampleId=${sample}"
        def res = getUrl( url )
        if ( ! res ) return [:]

        //  Unpack return JSON
        //
        res = slurper.parseText(res)

        log.debug("result=${res}")
        if ( res == ["Not Found."] ) return [:]
        if ( ! res.review ) return [:]

        //  Unpack attributes into Map
        //
        Map ret =   [
                        sample:             res.sampleId,                             // episode number
                        hAndE:              res.hAndE,                                // No of H&E slides
                        methylGreen:        res.methylGreen,                          // No of methyl green slides
                        lastUpdated:        res.lastUpdated,                          // Date of last modification
                        slideComments:      res.comments,                             // Comments on
                        slideTech:          res.pathologistUser.username,             // Lab tech. that entered slide info
                        retSite:            res.review.externalDiagnosis.description, // Anatomical site of tissue from pathology report
                        repMorphology:      res.review.histologicalTyping.description,// Pathology report morphology
                        pathMorphology:     res.review.internalFinding.description,   // Reviewing pathologist morphology opinion
                        tumourPct:          res.review.proportionOfTumourCells,       // % of tumour cells in area marked by pathologist
                        pathComments:       res.review.comments,                      // Reviewing pathologist comments
                        pathologist:        res.review.pathologistUser.username       // Reviewing pathologist
                    ]

        //  Remove embedded crap in comments
        //
        if ( ret.slideComments ) ret.slideComments = ret.slideComments.replaceAll( "[\\t\\n\\r]", ' ' )
        if ( ret.pathComments  ) ret.pathComments  = ret.pathComments.replaceAll( "[\\t\\n\\r]", ' ' )

        return ret
    }

    /**
     * Retrieve REST URL - various errors possible
     * Todo: deal with errors in more detail
     *
     * @param   url URL to retrieve
     * @return      Raw text from response
     */
    static String getUrl( String url )
    {
        try
        {
            def ret = url.toURL()
            return ret.text
        }
        catch( Exception ex )
        {
            log.warn( "Exception when retrieving URL[${url}]: " + ex )
            return null
        }

        return null
    }
}
