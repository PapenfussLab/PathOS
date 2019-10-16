/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.pathos.curate

import grails.converters.JSON
import groovy.util.logging.Log4j
import org.apache.commons.collections.set.ListOrderedSet
import org.petermac.util.Pubmed as PubmedUtility

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
    /***
     * DKGM 24-July-2019
     * Find all the citations for a CurVariant
     */
    static HashMap citeCurVariant ( CurVariant cv ) {

        AcmgEvidence acmg = cv.fetchAcmgEvidence()
        AmpEvidence amp = cv.fetchAmpEvidence()

        HashMap result = [
            report: [
                pmids: PubmedService.listOfPMIDs( cv?.reportDesc )
            ],
            evidence: [
                acmg: [
                    evidence: [
                        pmids: PubmedService.listOfPMIDs( acmg?.fetchAcmgJustification() )
                    ]
                ],
                amp: [
                        evidence: [
                        pmids: PubmedService.listOfPMIDs( amp?.ampJustification )
                    ],
                    therapeutic: [
                        pmids: PubmedService.listOfPMIDs( amp?.therapeuticText )
                    ],
                    diagnosis: [
                        pmids: PubmedService.listOfPMIDs( amp?.diagnosisText )
                    ],
                    prognosis: [
                        pmids: PubmedService.listOfPMIDs( amp?.prognosisText )
                    ]
                ],
                archive: [
                        evidence: [
                        pmids: PubmedService.listOfPMIDs( cv?.evidence?.justification )
                    ]
                ]
            ]
        ]

        try {
            JSON.parse( acmg?.acmgJustification ).criteria.each {
                if(it.value) result.evidence.acmg[it.key] = [pmids: PubmedService.listOfPMIDs(it.value)]
            }
        } catch( e ) {}

        fetchCitations(result.report)
        result.evidence.acmg.each { fetchCitations(it.value as HashMap) }
        result.evidence.amp.each { fetchCitations(it.value as HashMap) }
        fetchCitations(result.evidence.archive.evidence)

        return result
    }

    private static void fetchCitations( HashMap set ) {
        set.citations = set.pmids.collect { Pubmed.findByPmid(it)?.fetchCitation() ?: "" }
        set.titles = set.pmids.collect { Pubmed.findByPmid(it)?.title ?: "" }
    }




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

/* Should probably not show old archived PMIDs on the /CurVariant/show page.
        if( cv?.evidence?.justification ) {
            PubmedService.listOfPMIDs( cv?.evidence?.justification ).each {
                if( results.indexOf( it ) == -1 ) {
                    results.add( it )
                }
            }
        }
*/
        if( cv?.fetchAcmgEvidence() ) {
            PubmedService.listOfPMIDs( cv?.fetchAcmgEvidence().acmgJustification ).each {
                if( results.indexOf( it ) == -1 ) {
                    results.push( it )
                }
            }
        }

        if( cv?.fetchAmpEvidence() ) {
            PubmedService.listOfPMIDs( cv?.fetchAmpEvidence().ampJustification ).each {
                if( results.indexOf( it ) == -1 ) {
                    results.push( it )
                }
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
        ListOrderedSet<Long> results = []

        def regex = /\[PMID: \d+(, \d+)*\]/
        def PMIDs = (string =~ regex)

        def getNumbers = /\d+/
        for (def i = 0; i < PMIDs.count; i++) {
            def numbers = (PMIDs[i] =~ getNumbers)
            for(def j = 0; j < numbers.count; j++) {
                results.add(numbers[j] as Long)
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
        if(article && article.citation && article.citation.length() > 0) {
            return article.citation
        }

        String result = ''

        if(article) {
            ArrayList<String> authors = article?.authors?.tokenize(',')

            if (authors == null || authors?.size() == 0) {
                log.warn("Pubmed Article ${article} has no authors")
            } else if (authors?.size() == 1) {
                result = article.authors.toString()
            } else if (authors?.size() > 1) {
                result = authors.first() + " et al."
            }

            if (article.date) result += " (${article.date?.format("yyyy")}). "
            if (article.title) result += " ${article.title}"
            if (article.abbreviation) {
                result += " ${article?.abbreviation}"
            } else if (article.journal) {
                result += " ${article.journal}"
            }
            if (article.volume) result += " ${article.volume}"
            if (article.issue) result += " (${article.issue})"
            result += article.pages ? ", pp. ${article.pages}." : "."
        } else {
            result = 'Article not in database'
        }

        return result
    }

    /**
     * Make a citation using a temporary Pubmed article map.
     * @param article
     * @return
     *
     * DKGM 21-August-2017
     */
    static String buildCitation ( Map article ) {

        String result = ''

        if (article) {
            ArrayList<String> authors = article?.authors?.name?.join(',')?.take(255)?.tokenize(',')

            if (authors == null || authors?.size() == 0) {
                log.warn("Pubmed Article ${article} has no authors")
            } else if (authors?.size() == 1) {
                result = article.authors.toString()
            } else if (authors?.size() > 1) {
                result = authors.first() + " et al."
            }

            if (article?.date) {
                result += " ("+new Date().parse("yyyy-mm-dd", article?.date)?.format("yyyy")+")."
            }

            if (article?.title) {
                result += " "+article?.title
            }
            if (article?.abbreviation || article?.journal) {
                result += " "+(article?.abbreviation ?: article?.journal)
            }
            if (article?.volume) {
                result += " "+article?.volume
            }
            if (article.issue) {
                result += " ("+article.issue+")"
            }
            if (article?.pages) {
                result += ", pp. "+article?.pages
            }
            result += "."
        } else {
            result = 'Error'
        }

        return result
    }

    /**
     * Download or update an article from Pubmed
     */
    static Pubmed updateArticle ( String pmid ) {
        Pubmed entry = Pubmed.findByPmid(pmid)
        Map data = PubmedUtility.fetchArticle(pmid)

        data.affiliations = data?.authors?.affiliation?.join(',')?.take(255)
        data.authors = data?.authors?.name?.join(',')?.take(255)
        data.keywords = data?.keywords?.join(',')?.take(255)
        if(data.date) {
            data.date = new Date().parse("yyyy-mm-dd", data.date)
        }
        data.abstrct = data.abstract

        if ( !entry ) {
            entry = new Pubmed(data)
        } else {
            entry.setProperties(data)
            entry.setCitation(null)
        }

        entry.save( flush: true )

        return entry
    }

}

























