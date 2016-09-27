package org.petermac.pathos.curate

import grails.converters.JSON
import org.apache.lucene.queryParser.QueryParser


//    - Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
//    -
//    - Organisation: Peter MacCallum Cancer Centre
//    - Author: David Ma
//    - Date: 14-April-2016


class SearchController
{

    /**
     * Action to perform search on a query string
     *
     * @param q     String to search for
     */
def index =
{
}

def quickSearch = {
    render getResults(params.q as String, 1, 0) as JSON
}

def search = {
    render getResults(params.q as String, 10, 0) as JSON
}

def deepSearch = {
    render getResults(params.q as String, 10, params.o as Integer) as JSON
}

def putTime = {
    def result = 'fail'
    def query = params.q as String
    def speed = params.s as Integer
    def user  = params.u as String
    def time  = params.t as String
    if(query && speed && params && time) {
        new SearchTimes([
                query: query,
                speed: speed,
                user:  user,
                time:  time
        ]).save();
        result = 'success'
    }
    render result
}

def getAverageTime = {
    def q = params.q
    def result = -1

    def history = SearchTimes.findAllByQuery(q)


    if (history) {
        def total = 0;
        history.speed.each {
            total += it
        }
        result = total / history.size()
    }

    render result
}

def svTags = {
    def q = params.q.trim()
    def query =
    """
    select
        x from SeqVariant x
    join
        x.tags as t
    where
        t.label like :query
    """

    render SeqVariant.executeQuery(query, [query: '%'+q+'%']) as JSON
}

def svExact = {
    def q = params.q.trim()
    def query =
            """
    select
        x from SeqVariant x
    where
        x.hgvsc = :query
        or
        x.hgvsg = :query
        or
        x.sampleName = :query
    """

    render SeqVariant.executeQuery(query, [query: q]) as JSON
}

    def getResults( String q, Integer n, Integer o) {
        def searchResults = [:]
        if ( !n ) {
            n = 10;
        }
        if ( !o ) {
            o = 0;
        }

        if ( q ) {
            q = q.trim()
            searchResults.seqSample = trySearch ([
                    q:              q,
                    n:              n,
                    o:              o,
                    name:           "Sequenced Sample",
                    table:          SeqSample,
                    link:           "/PathOS/seqVariant/svlist/",
                    table_link:     "/PathOS/seqSample/list",
                    title_field:    "sampleName"
            ])

            searchResults.patSample = trySearch ([
                    q:              q,
                    n:              n,
                    o:              o,
                    name:           "Patient Sample",
                    table:          PatSample,
                    link:           "/PathOS/patSample/show/",
                    table_link:     "/PathOS/patSample/list",
                    title_field:    "sample"
            ])

            searchResults.seqrun = trySearch ([
                    q:              q,
                    n:              n,
                    o:              o,
                    name:           "Sequenced Run",
                    table:          Seqrun,
                    link:           "/PathOS/seqrun/show?id=",
                    table_link:     "/PathOS/seqrun/list",
                    title_field:    "seqrun"
            ])

            searchResults.curVariant = trySearch ([
                    q:              q,
                    n:              n,
                    o:              o,
                    name:           "Curated Variant",
                    table:          CurVariant,
                    link:           "/PathOS/curVariant/show?id=",
                    table_link:     "/PathOS/curVariant/list",
                    title_field:    "gene"
            ])

            searchResults.pubmed = trySearch ([
                    q:              q,
                    n:              n,
                    o:              o,
                    name:           "Pubmed Article",
                    table:          Pubmed,
                    link:           "/PathOS/Pubmed?id=",
                    table_link:     "/PathOS/Pubmed",
                    title_field:    "title"
            ])

            searchResults.tag = trySearch ([
                    q:              q,
                    n:              n,
                    o:              o,
                    name:           "PathOS Tag",
                    table:          Tag,
                    link:           "/PathOS/tag/show/",
                    table_link:     "/PathOS/tag/list",
                    title_field:    "label"
            ])
        }
        return searchResults
    }

    Map convertObjectToMap( def object ) {
        Map result = [:]
        object.properties.each { prop, val ->
            switch(prop) {
                case ["seqVariants", "seqSamples", "patAssays"]:
                    result[prop] = val.size()
                    break;
                default:
                    result[prop] = val
                    break;
            }
        }
        return result
    }


    /**
     * Execute a search on an object
     *
     * @param           Map config, which has params:
     * String name      A descriptive name of the table
     * Closure search   which runs the Searchable search on a domain class
     * String link      The URI to the table, after which the id of the element is appended
     * @return          Searchable Map of hits
     */
    Map trySearch( Map config )
    {
        try
        {
            // This allows us to search partial matches, but also rank whole matches higher than partials

            def q = QueryParser.escape(config.q)

            def luceneQuery = "(${q}) OR (*${q}*)"

            Map m = config.table.search(luceneQuery, [ max: config.n, offset: config.o ])


            m.link          = config.link
            m.name          = config.name
            m.table_link    = config.table_link
            m.title_field   = config.title_field
                                    // m.results comes from Searchable
            m.data          = []    // m.data is the PathOS class, which converts to a Map
            m.extra         = []    // m.extra stores deeper information from the PathOS class's hasMany fields. These need to be custom grabbed.
            m.tags          = []

            /**
             *
             * m.results are the results from the Searchable index
             * Unfortunately, these do not contain full data on the objects
             * We must pull this data from these search results and pass them to the front end for rendering.
             *
             * DKGM 24 June 2016
             */
            def iterator =  m.results.iterator()
            while (iterator.hasNext()) {
                def it = iterator.next()

                def obj = config?.table?.get(it?.id)

                if (obj) {

                    def extra = [
                            string: obj.toString()
                    ]

                    def tags = []
                    if (obj instanceof Taggable) {
                        obj.tags.each { tag ->
                            tags.push tag
                        }
                    }


                    switch(config.name) {
                        case 'Sequenced Sample':
                                def seqVariants = []
                                obj.seqVariants.each { sv ->
                                        Map data = [
                                                id: sv.id,
                                                name: sv.toString(),
                                                hgvsc: sv.hgvsc,
                                                hgvsp: sv.hgvsp,
                                                hgvsg: sv.hgvsg,
                                                gene: sv.gene,
                                                curated: sv.curated?.pmClass
                                        ]
                                        seqVariants.push(data)
                                    }
                                extra.panel = it.panel.toString()
                                extra.seqVariants = seqVariants
                            break
                        case 'Patient Sample':
                                def patAssays = []
                                obj.patAssays.each {
                                    patAssays.push([
                                            name: it.toString(),
                                            id: it.id
                                    ])
                                }
                                def seqSamples = []
                                obj.seqSamples.each {
                                    seqSamples.push([
                                            name: it.toString(),
                                            seqrun: it.seqrun,
                                            id: it.id
                                    ])
                                }
                                def patient = obj.patient
                                extra.patient = patient
                                extra.patAssays = patAssays
                                extra.seqSamples = seqSamples
                            break
                        case 'Sequenced Run':
                                def seqSamples = []
                                obj.seqSamples.each {
                                    seqSamples.push([
                                        name: it.toString(),
                                        seqrun: it.seqrun,
                                        id: it.id
                                    ])
                                }
                                extra.seqSamples = seqSamples
                            break
                        case 'Curated Variant':
                                def seqVariants = SeqVariant.findAllByCurated(obj)
                                extra.seqVariants = seqVariants
                            break
                        default:
                            break
                    }
                    m.data.push convertObjectToMap(obj)
                    m.tags.push tags
                    m.extra.push extra

                } else {
                    //  if an element exists in
                    //
                    iterator.remove()
                    m.total = m.total-1
                }
            }

            //  calc dislay & hits before r
            Integer display = Math.min( m.max as int, m.total as int)
            m.hits          = "${display}/${m.total}"

            return m
        }
        catch( Exception exp)
        {
            log.debug( "Search Error: ${exp.message}", exp )
            return [error: "Search Error: ${exp.message}"]
        }
    }

def tables(){

    def result = [
        Patient: [
                count: Patient.count(),
                title: "Patients",
                link: "/PathOS/Patient/list"
        ],
        PatSample: [
                count: PatSample.count(),
                title: "Patient Samples",
                link: "/PathOS/PatSample/list"
        ],
        Seqrun: [
                count: Seqrun.count(),
                title: "Sequencing Runs",
                link: "/PathOS/Seqrun/list"
        ],
        Panel: [
                count: Panel.count(),
                title: "Panels",
                link: "/PathOS/Panel/list"
        ],
        Amplicon: [
                count: Amplicon.count(),
                title: "Amplicons",
                link: "/PathOS/Amplicon/list"
        ],
        Roi: [
                count: Roi.count(),
                title: "Regions of Interest",
                link: "/PathOS/Roi/list"
        ],
        RefGene: [
                count: RefGene.count(),
                title: "Genes",
                link: "/PathOS/RefGene/list"
        ],
        RefExon: [
                count: RefExon.count(),
                title: "Exons",
                link: "/PathOS/RefExon/list"
        ],
        SeqSample: [
                count: SeqSample.count(),
                title: "Sequenced Samples",
                link: "/PathOS/SeqSample/list"
        ],
        SeqVariant: [
                count: SeqVariant.count(),
                title: "Sequenced Variants",
                link: "/PathOS/seqVariant/allsvlist"
        ],
        CurVariant: [
                count: CurVariant.count(),
                title: "Curated Variants",
                link: "/PathOS/CurVariant/list"
        ],
        Transcript: [
                count: Transcript.count(),
                title: "Transcripts",
                link: "/PathOS/Transcript/list"
        ],
        Audit: [
                count: Audit.count(),
                title: "Data Audit Trail",
                link: "/PathOS/Audit/list"
        ],
        Pubmed: [
                count: Pubmed.count(),
                title: "PubMed Articles",
                link: "/PathOS/Pubmed"
        ],
        ClinContext: [
                count: ClinContext.count(),
                title: "Clinical Context",
                link: "/PathOS/ClinContext/list"
        ]
    ]

    render result as JSON
}



    // This is restricted to admins and devs
    def reindex() {

        SeqSample.reindex()
        PatSample.reindex()
        Seqrun.reindex()
        CurVariant.reindex()
        Pubmed.reindex()
        Tag.reindex()
        render "Finished reindexing"
    }

}
