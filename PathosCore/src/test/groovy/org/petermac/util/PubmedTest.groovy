/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Unit tests for Pubmed
 *
 * User: doig ken
 * Date: 28/10/2015
 * Time: 8:55 PM
 */
class PubmedTest extends GroovyTestCase
{
    void testPing()
    {
        Pubmed pm = new Pubmed()

        assert pm.ping()
    }

    void testArticle()
    {
        Pubmed pm = new Pubmed()

        assert pm.fetchArticle( '26370340' ).pmid == '26370340'
        Map art = pm.fetchArticle( 'pmID:26370340' )
        assert art.pmid == '26370340'

        assert art.journal == 'The Lancet. Oncology'

        for( attr in art)
            println "${attr.key}\t\t${attr.value}"
    }


    void testArticleDate()
    {
        Pubmed pm = new Pubmed()

        assert pm.fetchArticle( '12114654' ).pmid == '12114654'
        Map art = pm.fetchArticle( 'pmID:12114654' )
        assert art.pmid == '12114654'

        assert art.title == 'Expression of Hepatocyte Growth Factor (HGF) and its Receptor (MET) in Medullary Carcinoma of the Thyroid.'

        assert art.date == '2002-01-12'

        for( attr in art)
            println "${attr.key}\t\t${attr.value}"
    }

    void testNoArticle()
    {
        Pubmed pm = new Pubmed()

        Map art = pm.fetchArticle( 'rubbish' )
        assert art == [:]
    }

    void testAbstract()
    {
        Pubmed pm = new Pubmed()

        Map art = pm.fetchArticle( 'pmID:18787170' )

        for( attr in art)
            println "${attr.key}\t\t${attr.value}"
    }

}



