// Created by David Ma
// david.ma@petermac.org
// 4th of February 2016


package org.petermac.pathos.curate

import org.codehaus.groovy.grails.web.json.JSONElement
import org.petermac.util.Locator

// Uhhh... this was giving me naming issues, so I aliased it.
import org.petermac.util.Pubmed as PubmedUtility
import grails.converters.*



import org.grails.plugin.easygrid.Easygrid


@Easygrid
class PubmedController {
    //  Scaffold everything else
    //
    static scaffold = Pubmed
    def loc = Locator.instance                      // file locator

    /**
     * Table definition for Easygrid
     */
    def pubmedGrid =
            {
                dataSourceType 'gorm'
                domainClass Pubmed
                enableFilter true
                editable false
                inlineEdit false
                columns {
                    id     // Hide this one
                    tags
                    authors
                    date
                    title
                    journal
                    volume
                    issue
                    pages
                    affiliations
                    pmid
                    doi
                    pdf
                    abstrct
                    citation {
                        value { Pubmed article ->
                            PubmedService.buildCitation(article)
                        }
                    }
                }
            }

    def index() {
    }
    def list() {
        redirect url: "/Pubmed"
    }
    def show() {
        redirect url: "/Pubmed"
    }

    def cite() {
        Pubmed article = Pubmed.findByPmid(params.int('pmid'));

        if(article) {
            render PubmedService.buildCitation(article)
        } else {
            render "Not in system."
        }
    }

    /**
     * TODO: Document this rest api
     * @return
     */
    def check_id() {
        def id = params.int('id')
        if(id && Pubmed.get(id))
            render Pubmed.get(id).pmid
        else
            render "Fail"
    }

    def check_pmid(Long pmid) {
        render Pubmed.findByPmid(pmid)
    }

    //perhaps we should do some error checking to see if the pmid is any good...
    // This should probably be "Long pmid" DKGM 28-November-2016
    def fetch_pmid(String pmid){
        try {
            Pubmed entry = Pubmed.findByPmid(pmid);

            if (entry) {
                render "exists-" + entry.getId()
            } else {


                Map result = PubmedUtility.fetchArticle(pmid)
                result.citation = PubmedService.buildCitation(result)

                System.out.println(result)

                render result as JSON
            }
        } catch ( Exception e ) {
            log.error(e)
            render new HashMap([Error: e]) as JSON
        }
    }

    def update_article( String pmid ){
        render PubmedService.updateArticle( pmid ) as JSON
    }

    def add_pmid(){
        Pubmed entry = Pubmed.findByPmid(params.pmid);

        if(entry) {
            render "exists-"+entry.getId()
        } else {
            if(PubmedService.updateArticle(params.pmid)){
                render "saved"
            } else {
                render "failed"
            }
        }
    }

    def upload_pdf(){
        System.out.println("Lol here are your params...")
        System.out.println(params)
        System.out.println("And this is the request...")
        System.out.println(request)

        def file = params.pdf
        def filename = params.pdf_pmid

        // we should use a mime type checker...
        // grails withFormat? pdf?
        // Currently this just saves whatever file was uploaded. This is DANGEROUS.


// We're returning 3 different values: success, fail & no transfer
// no transfer = you didn't mount pathology on your machine!
// There's a slightly different warning ("File could not be uploaded.", vs. "File was not uploaded."
// so you will know what went wrong, without bothering the user.
        if(filename =~ /^\d+{0,20}$/) {

            try {
                file.transferTo(new File(loc.pubmedDir + filename + '.pdf'))
                Pubmed.findByPmid(filename)?.setPdf(filename + ".pdf")
                render "success"
            } catch (all) {
                render "no transfer"
            }
        } else {
            render "fail"
        }
//        response.sendError(200, 'Done')
    }

    def saveCitation(Long pmid, String citation) {
        if( pmid && citation ) {
            Pubmed article = Pubmed.findByPmid(pmid)
            if( article ) {
                article.setCitation(citation)
                article.save()
                render "Success"
            } else {
                render "Fail"
            }
        } else {
            render "Fail"
        }
    }



    def test() {
        Map result = [
            hello: 'world'
        ]

        String blah = "[1,2,3,{123:456}]"

        try {

            JSONElement thing = JSON.parse(blah)
            result.test = thing
        } catch (e) {
            result.error = e
        }

        try {
//            result.something = JSON.parse(null)
        } catch (e) {
            result.lol = e
        }


        render result as JSON
    }







}

























