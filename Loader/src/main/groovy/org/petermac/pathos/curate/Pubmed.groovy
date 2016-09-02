// Created by David Ma
// david.ma@petermac.org
// 4th of February 2016

package org.petermac.pathos.curate


import grails.persistence.Entity

@Entity
class Pubmed implements Taggable {

    String	pmid
    String	doi
    Date	date
    String	journal
    String	title
    String	authors
    String	affiliations
    String	volume
    String	issue
    String	pages
    String	abstrct
    String	pdf

    static hasMany = [ tags: Tag ]

    static mapping = {
        name date: "1970-01-01 00:00:00"
    }

    static  searchable =
            {
                only = [ 'pmid', 'doi', 'journal', 'title', 'authors', 'affiliations', 'abstrct', 'tags' ]
                tags component: true
            }

    String toString()
    {
        title
    }

    static constraints =
    {
        pmid( unique: true, nullable: false)
        doi( nullable: true )
        date( nullable: false )
        journal( nullable: true )
        title( nullable: true )
        authors( nullable: true )
        affiliations( nullable: true )
        volume( nullable: true )
        issue( nullable: true )
        pages( nullable: true )
        abstrct( nullable: true, maxSize: 9999 )
        pdf( nullable: true )
    }
}
