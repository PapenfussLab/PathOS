/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.pathos.curate

import groovy.util.logging.Log4j

/**
 * Created for PathOS.
 *
 * Description:
 * Pubmed methods and functions go in this file.
 *
 * Author: DKGM (David Ma)
 * Date: 28/11/2016
 */

@Log4j
class PubmedService
{

    /**
     * DKGM 6-December-2016
     *
     * Find all the properly formatted PMIDs in the Report Description (reportDesc)
     * and the Evidence Description (justification) of a Curated Variant
     *
     * @param cv
     * @return ArrayList of PMIDs
     */
    static ArrayList<Long> allPMIDsFromCurVariant ( CurVariant cv ) {

        ArrayList<Long> results = PubmedService.listOfPMIDs( cv?.reportDesc )

        PubmedService.listOfPMIDs( cv?.evidence?.justification ).each {
            if( results.indexOf( it ) == -1 ) {
                results.push( it )
            }
        }

        return results
    }

    /**
     * DKGM 28-November-2016
     *
     * This is a function which parses a string
     * and pulls out the properly formatted PMIDs
     * passing them back as an ordered list.
     *
     * PMIDs should be formatted like this:
     * [PMID: 124123]
     *
     * Multiple PMIDs should be formatted like this:
     * [PMID: 1245123, 1231241]
     *
     * Anything else will not be parsed.
     * The current strategy is to help the users on the front end
     * and assume that the database is clean
     *
     * @param string
     * @return list of PMIDs found
     */
    static ArrayList<Long> listOfPMIDs ( String string ) {
        Set<Long> results = []

        def regex = /\[PMID: (?:(?:\d+)(?:, )?)+\]/
        def PMIDs = (string =~ regex)

        def getNumbers = /\d+/
        for (def i = 0; i < PMIDs.count; i++) {
            def numbers = (PMIDs[i] =~ getNumbers)
            for(def j = 0; j < numbers.count; j++) {
                results.add(numbers[j])
            }
        }

        return results as ArrayList<Long>
    }

    /**
     * Get Citation for a Pubmed Article
     *
     * DKGM 28-November-2016
     */
    static String buildCitation( Pubmed article ) {
        def result = ''

        if(article) {
            def authors = article.authors.split(',')

            if (authors.length > 1) {
                result += authors[0] + " et al."
            } else {
                result += article.authors
            }
            result += " ("+article.date.format("yyyy")+"). "
            result += " "+article?.title
            result += " "+article?.journal
            result += " "+article?.volume
            if(article.issue) {
                result += " ("+article.issue+")"
            }
            result += ", pp. "+article?.pages+"."
        }

        return result
    }

}

























