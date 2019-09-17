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
    String	abbreviation
    String	title
    String	authors
    String	affiliations
    String	volume
    String	issue
    String	pages
    String	abstrct
    String	pdf
    String	citation

    static hasMany = [ tags: Tag ]

    static mapping =
    {
        abstrct             ( type: 'text' )
        affiliations        ( type: 'text' )
        authors             ( type: 'text' )
        title               ( type: 'text' )
        citation            ( type: 'text' )
    }

    static  searchable =
    {
        tags component: true
    }

    String toString()
    {
        title
    }

    static constraints =
    {
        pmid                ( unique: true, nullable: false)
        doi                 ( nullable: true )
        date                ( nullable: false )
        journal             ( nullable: true )
        abbreviation        ( nullable: true )
        title               ( nullable: true )
        authors             ( nullable: true )
        affiliations        ( nullable: true )
        volume              ( nullable: true )
        issue               ( nullable: true )
        pages               ( nullable: true )
        abstrct             ( nullable: true )
        pdf                 ( nullable: true )
        citation            ( nullable: true )
    }

    String fetchCitation() {
        this.citation ?: PubmedService.buildCitation(this)
    }

}
