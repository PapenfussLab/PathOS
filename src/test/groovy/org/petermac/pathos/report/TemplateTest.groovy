/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.report

import static org.petermac.util.Pubmed.fetchArticle

/**
 * Created for PathOS.
 *
 * Description:
 *
 *
 *
 * User: doig ken
 * Date: 20/12/2015
 * Time: 6:57 PM
 */
class TemplateTest extends GroovyTestCase
{
    void testFillTemplate_generic()
    {
        def template = '${gene}: A $aa3 ${consequence} mutation was detected in ${gene}.'

        Map binding  = [ gene: 'NRAS', aa3: 'Gln61Arg', consequence: 'missense' ]
        assert Template.fillTemplate( template, binding ) == "NRAS: A Gln61Arg missense mutation was detected in NRAS."

        binding  = [ gene: 'SF3B1', aa3: 'Lys700Glu', consequence: 'missense' ]
        assert Template.fillTemplate( template, binding ) == "SF3B1: A Lys700Glu missense mutation was detected in SF3B1."

        binding  = [ gene: 'U2AF1', aa3: 'Ser34Phe', consequence: 'missense' ]
        assert Template.fillTemplate( template, binding ) == "U2AF1: A Ser34Phe missense mutation was detected in U2AF1."
    }

    void testFillTemplate_domain()
    {
        Map binding  = [ domain: 'GTP binding domain', aa3: 'Gln61Arg', pmid_nnnn: '[pmid:nnnn]' ]
        def template = 'The $aa3 occurs in the $domain (www.uniprot.org) and results in impaired hydrolysis of GTP and subsequent constitutive signaling[pmid:${org.petermac.util.Pubmed.fetchArticle(\'22589270\').pmid}]'

        assert Template.fillTemplate( template, binding ) == "The Gln61Arg occurs in the GTP binding domain (www.uniprot.org) and results in impaired hydrolysis of GTP and subsequent constitutive signaling[pmid:22589270]"
    }

    void testFillTemplate_pubmed()
    {
        Map pmc = fetchArticle('22589270')

        Map binding  = [ date: "$pmc.date", journal: "$pmc.journal" ]
        def template = 'The pubmed article title=${date},${journal}'

//        for ( kv in pmc )
//        {
//            println( "${kv.key}\t\t${kv.value}")
//        }

        assert Template.fillTemplate( template, binding ) == 'The pubmed article title=2012-05-16,Cancer research'
    }
}
