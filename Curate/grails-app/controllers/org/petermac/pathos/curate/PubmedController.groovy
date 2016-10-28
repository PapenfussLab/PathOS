// Created by David Ma
// david.ma@petermac.org
// 4th of February 2016


package org.petermac.pathos.curate

import org.apache.commons.io.FileUtils

// Uhhh... this was giving me naming issues, so I aliased it.
import org.petermac.util.Pubmed as PubmedUtility
import grails.converters.*



import org.grails.plugin.easygrid.Easygrid

import java.text.MessageFormat
import java.text.SimpleDateFormat


@Easygrid
class PubmedController {
    //  Scaffold everything else
    //
    static scaffold = Pubmed

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
                    authors
                    date
                    title
                    journal
                    volume
                    issue
                    pages
                    affiliations

                    // Hide these:
                    id
                    pmid
                    doi
                    abstrct
                    pdf
                    tags
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
        Pubmed entry = Pubmed.findByPmid(params.int('pmid'));

        if(entry) {
            def result = ''
            def fail = false

//          This is a more complicated way of adding authors, and would be better...
//          but it doesn't work on all cases because the data is not formatted well :(
//            def authors = entry.authors.split(',')
//            if(authors.length < 0) {
//                fail = true
//            } else if (authors.length >= 1) {
//                def blah = []
//                authors.each({
//                    blah.push it.substring(2)+', '+it.substring(0, 1)+'.'
//                })
//                result = blah.join(" and ")
//            }

            result += entry.authors.split(',').join(" and ")
            result += " ("+entry.date.format("yyyy")+"). "
            result += " "+entry?.title
            result += " "+entry?.journal
            result += " "+entry?.volume
            if(entry.issue) {
                result += " ("+entry.issue+")"
            }
            result += ", pp. "+entry?.pages+"."


            if(!fail) {
                render result
            } else {
                render 'Fail of some sort'
            }
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

    //perhaps we should do some error checking to see if the pmid is any good...
    def fetch_pmid(String pmid){
        Pubmed entry = Pubmed.findByPmid(pmid);

        if(entry) {
            render "exists-"+entry.getId()
        } else {


            def result = PubmedUtility.fetchArticle(pmid) as JSON

            System.out.println(result)

            render result
        }
    }

    def add_pmid(){
        Pubmed entry = Pubmed.findByPmid(params.pmid);

        if(entry) {
            render "exists-"+entry.getId()
        } else {

            Map data = PubmedUtility.fetchArticle(params.pmid)

            data.affiliations = data.authors?.affiliation?.join(',').take(255)
            data.authors = data.authors?.name?.join(',').take(255)
            data.keywords = data.keywords?.join(',').take(255)
            data.date = new Date().parse("yyyy-mm-dd", data.date)
            data.abstrct = data.abstract

            def newEntry = new Pubmed(data)

            if(newEntry.save(flush:true, failOnError: true)){
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
                file.transferTo(new File("/pathology/NGS/Pubmed/${filename}.pdf"))
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


}

























