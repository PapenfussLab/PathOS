/*
 * Author: David Ma
 * Date: 5-2-2016
 *
 * Purpose: This holds the keywords found in the Pubmed files
 *
 */

package org.petermac.pathos.curate


import grails.persistence.Entity

@Entity
class Keyword {

    String pmid
    String keyword

    static constraints =
            {
            }
}
