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
    HashMap results = getResults(params.q as String, 1, 0)
    render results as JSON
}

def search = {
    HashMap results = getResults(params.q as String, 10, 0)
    render results as JSON
}

def deepSearch = {
    HashMap results = getResults(params.q as String, 10, params.o as Integer)
    render results as JSON
}

def searchSeqSamples(String q) {
    HashMap results = [seqSample:[
        extra: [],
        link: "/PathOS/seqVariant/svlist/",
        name: "Sequenced Sample",
        offset: 0,
        results: [],
        scores: [],
        table_link: "/PathOS/seqSample/list",
        tags: [],
        title_field: "sampleName",
    ]];

    def query =
"""
select
    x from SeqSample x
where
    x.sampleName like :q
    or
    x.seqrun.seqrun like :q
"""

    def tagQuery =
"""
select
    x from SeqSample x
join
    x.tags as t
where
    t.label like :q
    or
    t.description like :q
    or
    t.createdBy.username like :q
    or
    t.createdBy.displayName like :q
"""


    if(q) {
        q = q.trim();
        ArrayList<SeqSample> samples = SeqSample.executeQuery(query, [q: "%${q}%", offset: 0, max: 50]);
        ArrayList<SeqSample> taggedSamples = SeqSample.executeQuery(tagQuery, [q: "%${q}%", offset: 0, max: 50]);

        for (SeqSample x : taggedSamples){
            if (!samples.contains(x))
                samples.add(x);
        }

        results.seqSample.total = samples.size();
        results.seqSample.max = samples.size();
        results.seqSample.hits = results.seqSample.max+"/"+results.seqSample.max;
        results.seqSample.results = samples;

        samples.each({ it ->
            results.seqSample.extra.push([title:it.sampleName]);
            results.seqSample.scores.push(2);

            ArrayList<Tag> tags = [];
            it.tags.each({ tag ->
                tags.push(tag);
            })
            results.seqSample.tags.push(tags);
        })
    }

    render results as JSON
}






def putTime = {
    def result = 'fail'
    String query = params.q as String
    Integer speed = params.s as Integer
    String user  = params.u as String
    String time  = params.t as String
    Integer results = params.r as Integer
    String version = params.v as String
    if(query && speed && params && time) {
        new SearchTimes([
                query: query,
                speed: speed,
                user:  user,
                time:  time,
                numberOfResults: results,
                pathosVersion: version
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
        or
        t.description like :query
        or
        t.createdBy.username like :query
        or
        t.createdBy.displayName like :query
"""
    List results = SeqVariant.executeQuery(query, [query: '%'+q+'%', offset: 0, max: 10])

def counter =
"""
    select
        count(*) from SeqVariant x
    join
        x.tags as t
    where
        t.label like :query
        or
        t.description like :query
        or
        t.createdBy.username like :query
        or
        t.createdBy.displayName like :query
"""

    int count = SeqVariant.executeQuery(counter, [query: '%'+q+'%'])[0]

    render ( [ results: results, count: count ] as JSON )
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
    List results = SeqVariant.executeQuery(query, [query: q, offset: 0, max: 10])

    def counter =
"""
    select
        count(*) from SeqVariant x
    where
        x.hgvsc = :query
        or
        x.hgvsg = :query
        or
        x.sampleName = :query
"""

    int count = SeqVariant.executeQuery(counter, [query: q])[0]

    render ( [ results: results, count: count ] as JSON )
}


    private HashMap getResults( String q, Integer n = 10, Integer o = 10) {
        HashMap searchResults = [:]

        if ( q ) {
            q = q.trim()

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

    /**
     * Execute a search on an object
     *
     * @param           Map config, which has params:
     * String name      A descriptive name of the table
     * Closure search   which runs the Searchable search on a domain class
     * String link      The URI to the table, after which the id of the element is appended
     * @return          Searchable Map of hits
     */
    private HashMap trySearch( HashMap config )
    {
        try
        {
            // This allows us to search partial matches, but also rank whole matches higher than partials

            String q = QueryParser.escape(config.q)
            String luceneQuery = "(${q}) (*${q}*)"

            def m = config.table.search(luceneQuery, [ defaultOperator: "OR", max: config.n, offset: config.o ])
            m.link          = config.link
            m.name          = config.name
            m.table_link    = config.table_link
            m.title_field   = config.title_field
                                    // m.results comes from Searchable
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
                        title: obj.toString()
                    ]

                    def tags = []
                    if (obj instanceof Taggable) {
                        obj.tags.each { tag ->
                            tags.push tag
                        }
                    }

                    switch(config.name) {
                        case 'Sequenced Run':
                            // Note, if any of these values are null, they will be skipped.
                            // Also note, these values are hardcoded into the search
                            // -DKGM 8-March-2017
                            extra.seqSamples = SeqSample.executeQuery("select x.id, x.sampleName, x.authorisedQcFlag, x.passfailFlag from SeqSample x where x.seqrun = :sr", [sr:obj])

                            break
                        case 'Curated Variant':
                            extra.seqVariants = SeqVariant.countByHgvsc(obj.grpVariant);
                            break
                        case 'Tag':
                            extra.createdBy = obj.createdBy.toString()
                            break
                        default:
                            break
                    }

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
        catch( Exception exp )
        {
            log.debug( "Search Error: ${exp.message}", exp )
            return [error: "Search Error: ${exp.message}"]
        }
    }


def patSampleLookup(Long id) {
    PatSample ps = PatSample.get(id);
    HashMap extra = [:];

    if (ps) {
        extra = [
            patient: ps.patient,
            seqSamples: ps.seqSamples,
            seqruns: ps.seqSamples.collect { it.seqrun.toString() }
        ]
    }

    render extra as JSON;
}

def seqSampleLookup(Long id){
    SeqSample ss = SeqSample.get(id);
    HashMap extra = [:]

    if(ss) {
        extra = [
            title: ss.sampleName,
            seqrun: ss.seqrun.toString(),
            panel: ss.panel.toString(),
            seqVariants: SeqVariant.countBySeqSample(ss),
            curVariants: SeqVariant.executeQuery("select x from SeqVariant x where x.seqSample = :ss and x.maxPmClass != null", [ss:ss])
        ]
    }

    render extra as JSON;
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
//  Don't show this table for now, because it is not used. DKGM 20-December-2016
//        Amplicon: [
//                count: Amplicon.count(),
//                title: "Amplicons",
//                link: "/PathOS/Amplicon/list"
//        ],
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
        ],
        SeqRelation: [
                count: SeqRelation.count(),
                title: "Sequenced Relations",
                link: "/PathOS/SeqRelation/list"
        ],
        Tag: [
                count: Tag.count(),
                title: "Tags",
                link: "/PathOS/Tag/list"
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
