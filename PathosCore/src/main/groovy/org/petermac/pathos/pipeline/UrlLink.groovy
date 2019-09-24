/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.pathos.pipeline

import groovy.util.logging.Log4j
import org.petermac.pathos.pipeline.HGVS
import org.petermac.util.Locator

/**
 * A class for the construction of all relevant URLs
 *
 * Author: Ken Doig
 * Date: 30/07/13
 * Time: 9:59 AM
 */

@Log4j
class UrlLink
{
    static def loc = Locator.instance          // system wide file locations

    /**
     * Return a link to sample files in the data repository
     *
     * @param seqrun    Seqrun to access
     * @param sample    Sample to access
     * @param suffix    Optional suffix of the URL eg <sample>.vcf
     * @return          URL to sample data
     */
    static String dataUrl( String seqrun, String sample, String suffix = "" )
    {
        def baseURL = "${loc.dataServer}/Pathology/${loc.samBase}/${seqrun}/${sample}/"

        if ( suffix ) baseURL = baseURL + suffix

        return baseURL
    }

    /**
     * generate a URL to the igvSession controllers dynamically generated IGV Session XML file
     *
     * baseURL generated from grails, absolute URL to IgvSession controller
     * @param baseUrl
     * @param sample
     * @return
     */
    static String igvSessionXMLUrl( String baseUrl, String seqrun, String sample,  String pos)
    {
        String igvURL = "http://localhost:60151/load?file=${baseUrl}/${seqrun}/${sample}.xml&amp;locus=${pos}"
        return igvURL
    }

    /**
     * Return a link to sample files in the data repository
     *
     * @return          URL to sample data
     */
    static String baseUrl()
    {
        def baseURL = "${loc.dataServer}"
        return baseURL
    }

    /**
     * Return a link to sample files in the data repository
     *
     * @param panel     Panel to access
     * @return          URL to sample data
     */
    static String panelUrl(String panel)
    {
        def panelUrl = "${loc.dataServer}" + "/Panels/" + panel
        return panelUrl
    }

    /**
     * Return a link to the contamination.png heatmap for a seqrun in the data repository
     * @param   seqrun    Seqrun to access
     * @return  String    URL to the heatmap file "Contamination.png"
     *
     * 13-Jan-2017 DKGM
     */
    static String contaminationUrl( String seqrun )
    {
        def baseURL = "${loc.dataServer}/Pathology/${loc.samBase}/${seqrun}/QC/Contamination.png"
        return baseURL
    }

    /**
     * Return a link to the multiQC html
     */
    static String multiQCUrl( String seqrun ) {
        return "${loc.dataServer}/Pathology/${loc.samBase}/${seqrun}/QC/MultiQC/multiqc_report.html"
    }

    /**
     * Return a link to a file that only exists if the pipeline has finished.
     * i.e. we can say the run is ready if this file exists
     *
     * @param   seqrun    Seqrun to access
     * @return            URL to pipeline report html
     */
    static String pipelineUrl( String seqrun, String platform )
    {
        def baseURL = ""

        if ( platform == "MiSeq") {
            baseURL = "${loc.dataServer}/Pathology/${loc.samBase}/${seqrun}/RunPipe/mp_Amplicon/doc/index.html"
        } else if ( platform == "NextSeq" ) {
            baseURL = "${loc.dataServer}/Pathology/${loc.samBase}/${seqrun}/QC/MultiQC/multiqc_report.html"
        }

        return baseURL
    }

    /**
     * Construct an IGV link for an IGV session file on the filesystem. Deprecated as of PATHOS-3392
     *
     * @param seqrun
     * @param sample
     * @param pos
     * @return      Constructed link
     */
    static String igv( String seqrun, String sample, String pos, Boolean demo = false )
    {
        String igvURL = "http://localhost:60151/load?file=${dataUrl(seqrun,sample)}IGV_Session.xml&amp;locus=${pos}"

        return igvURL
    }

    /**
     * Construct a VEP web summary link
     *
     * @param seqrun
     * @param sample
     * @param detail    True if VEP details required
     *
     * @return      Constructed link
     */
    static String vep( String seqrun, String sample, Boolean detail = false )
    {
        String vepURL = "${dataUrl(seqrun,sample)}${sample}"

        if ( detail )
            vepURL = vepURL + '.vep.html'
        else
            vepURL = vepURL + '.vep_summary.html'

        return vepURL
    }

    /**
     * Link to trigger Alamut Package
     *
     * @param pos
     * @return
     */
    static String alamut( String pos)
    {
        String alamutURL  = "http://localhost:10000/show?request=${pos}"
        return alamutURL
    }

    /**
     * Link to dbSNP reference page
     *
     * @param dbsnpid
     * @return
     */
    static String dbsnp( String dbsnpid)
    {
        String dbsnpURL = ""

        if ( dbsnpid )
            dbsnpURL  = "http://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=${dbsnpid}"

        return dbsnpURL
    }

    /**
     * Link to PNG for CNV viewer Todo: deprecated
     * @param seqrun
     * @param sample
     * @return
     */
    static String cnvUrl( String seqrun, String sample)
    {
        def baseURL = dataUrl( seqrun, sample, "${sample}.cnv.png" )

        return baseURL
    }

    /**
     * Create URL for Gaffa Copy Number Browser
     *
     * @param version   Gaffa version cnb or gaffa
     * @param seqrun    Seqrun
     * @param sample    SeqSample
     * @param panel     Capture panel
     * @return          Formatted URL
     */
    static String gaffaUrl( String seqrun, String sample, String panel )
    {
        def baseURL = loc.cnvViewerUrl

        baseURL += "/?collection=${loc.samBase}&panel=${panel}&run=${seqrun}&sample=${sample}&locked=TRUE&index_path=CNV"

        return baseURL
    }


    /**
     * Link to Cosmic reference page
     *
     * @param cosmicid
     * @return
     */
    static String cosmic( String cosmicid)
    {
        String cosmicURL = ""

        if ( cosmicid )
            cosmicURL = "https://cancer.sanger.ac.uk/cosmic/mutation/overview?genome=37&id=${cosmicid}"

        return cosmicURL
    }


    /**
     * Link to Cosmic reference page
     *
     * @param   gene      Gene to display
     * @param   hgvsp     Protein mutation to get the position from
     * @return            URL to COSMIC histogram page
     */
    static String histogram( String gene, String hgvsp )
    {
        String histURL = ""

        if ( gene )
        {
            def m = (hgvsp =~ /p\.[A-Z][a-z]?[a-z]?(\d+)\D/)
            if ( m.count == 1 )
            {
                int pos = m[0][1] as int
                def win = 20    // Window size of AAs either side of protein mut position
                histURL = "https://cancer.sanger.ac.uk/cosmic/gene/analysis?ln=${gene}&start=${pos-win}&end=${pos+win}"
            }
            else
                histURL = "https://cancer.sanger.ac.uk/cosmic/gene/analysis?ln=${gene}"
        }

        return histURL
    }

    /**
     * Link to Pubmed abstract
     *
     * @param pubmedid
     * @return
     */
    static String pubmed( String pubmedid)
    {
        String pubmedURL = ""

        if ( pubmedid )
            pubmedURL  = "https://www.ncbi.nlm.nih.gov/pubmed/${pubmedid}"

        return pubmedURL
    }

    /**
     * Link to UCSC geneome browser
     *
     * @param pos
     * @return
     */
    static String ucsc( String pos)
    {
        String ucscURL  = "http://genome.ucsc.edu/cgi-bin/hgTracks?position=chr${pos}&hgt.out3=10x&g=lovd"
        return ucscURL
    }

    /**
     * Link to PathOS Word MolPATH report
     *
     * @param sample
     * @return
     */
    static String reportDoc( String homeDir, String sample)
    {
        String reportURL = ""
        File   reportFile = new File( homeDir + "/Report/Generated/Report_${sample}.docx")

        //  Check if we have a report
        //
        if ( reportFile.exists())
            reportURL = "file://${reportFile.path}"

        return reportURL
    }

    /**
     * Link to PathOS Word MolPATH report
     *
     * @param sample
     * @return
     */
    static String reportHtml( String homeDir, String sample)
    {
        String reportURL = ""
        File   reportFile = new File( homeDir + "/Report/Generated/Report_${sample}.html")

        //  Check if we have a report
        //
        if ( reportFile.exists())
            reportURL = "file://${reportFile.path}"

        return reportURL
    }

    /**
     * Link to FastQC report    Todo: should use URLs to data repository only
     *
     * Generate URLs for accessing FastQC reports eg.
     * http://bioinf-ensembl.petermac.org.au/Pathology/Molpath/141214_M01053_0162_000000000-ACML4/14K0900-A/14K0900-A_CAGATCCA-TAGACCTA_L001_R2_001_fastqc/fastqc_report.html
     *
     * @param   seqrun      Seqrun name
     * @param   sample      Sample name
     * @param   cloud       True if this is being returned for an external could server instance
     * @return              List of FastQC report HTML files
     */
    static List fastqcUrl( String seqrun, String sample, Boolean cloud = false)
    {
        List fqcl = []

        if ( cloud )
            return cloudFastQC( seqrun, sample )

        //  Sample data directory
        //
        def qcDir = new File("${loc.samDir}${seqrun}/${sample}/QC")

        if ( ! qcDir.exists()) return []

        //  Find all fastqc directories
        //
        qcDir.eachFileMatch( ~/.*_fastqc/ )
        {
            def html = new File( it, 'fastqc_report.html' )

            if ( html.exists())
            {
                def path = html.path.replaceFirst( loc.samDir, "Pathology/${loc.samBase}/")
                path = "${loc.dataServer}/${path}"
                fqcl << path
            }
        }

        return fqcl.sort()
    }

    /**
     * Link to cloud FastQC report   Todo: create sym links to FastQC.html files in sample dir
     *
     * @param   seqrun      Seqrun name
     * @param   sample      Sample name
     * @return              List of FastQC report HTML files
     */
    static List cloudFastQC( String seqrun, String sample )
    {
        //  Assume we always have two hardwired URLs for testing on cloud instance
        //
        List fqcl = []

        def path = dataUrl( seqrun, sample )
        fqcl << path + "FastQCread1.html"
        fqcl << path + "FastQCread2.html"

        return fqcl
    }



    /**
     * Link for Google variant search
     * optionla ensVar will return a nucleotide for google search
     */
    static String googleSearchVar(String gene, String hgvsc, String hgvsp, String hgvspAa1, String ensVar = '') {
        //as per PATHOS-237 we strip all brackets
        hgvsp = hgvsp.replaceAll("\\(",'').replaceAll("\\)",'')
        hgvspAa1 = hgvspAa1.replaceAll("\\)",'').replaceAll("\\(",'')

        def searchStringInner
        def searchString

        def hgvsParsed = HGVS.parseHgvsC(hgvsc) //parse the hgvc to get the positions, mutation, type

        println hgvsParsed
        String pos = hgvsParsed.pos
        String endpos = hgvsParsed.endpos
        String mut = hgvsParsed.mut
        String type = hgvsParsed.muttype

        mut = mut.replaceAll(type,'') //we clear the type from mut: so mut is only the nucleotide/s (ACGT) , and type is the type (e.g. ins)

        //println "mut pos type: ${mut} ${pos} ${type}"

        if (hgvsParsed.muttype == 'ins' || hgvsParsed.muttype == 'del') {
            if (hgvsParsed.muttype == 'del' && mut == '' &&  ensVar) {
                //we have a deletion with no nucleotide listed. we need one for search, though, so we can grab one from ens_variant
                def ensvarmap = HGVS.ensToMap(ensVar)

// See PATHOS-3411
// It seems that ens_variant was changed sometime around v1.4.0
// The new del format is no longer parsed properly, and sometimes is returned as null
// DKGM 19-Sept-2018
                mut = ensvarmap ? ensvarmap['ref'] : ''
            }

            if (endpos != pos) {    //format the search string as in Alamut, for mutations spanning multiple nucleotides
                if (mut != '') {
                    searchStringInner = "\"${pos}-${endpos}${type}${mut}\" | \"${pos}_${endpos}${type}${mut}\" | \"${pos}-${endpos}${type}\" | \"${pos}_${endpos}${type}\""
                } else {
                    searchStringInner = "\"${pos}-${endpos}${type}\" | \"${pos}_${endpos}${type}\""
                }
            } else {    //format the search string as in Alamut for single-nucleotide mutations
                if (mut) {
                    searchStringInner = "\"${pos}${type}${mut}\" | \"${pos}${type}\""
                } else {
                    searchStringInner = "\"${pos}${type}\""
                }
            }
        } else if (hgvsParsed.muttype == 'snp') {  //format the search string as in Alamut for a SNP
            def muts = mut.split('>')   //get the ref and alt nucleotides in a list. t
            searchStringInner = "\"${pos}${muts[0]}>${muts[1]}\" | \"${pos}${muts[0]}->${muts[1]}\" | \"${pos}${muts[0]}-->${muts[1]}\" | \"${pos}${muts[0]}/${muts[1]}\""
        } else if (hgvsParsed.muttype == 'dup') {  //format the search string as in Alamut for a dup
            if (endpos != pos) {    //spanning several nucleotides
                searchStringInner = "\"${pos}-${endpos}${type}\" | \"${pos}_${endpos}${type}\""
            } else
            {   //spanning one nucleotide
                searchStringInner = "\"${pos}${type}\""
            }
        }

        //now parse the protein entry. this will be appended onto searchStringInner
        if (hgvsp) {

            String baseString
            if (hgvsp.contains(":")) {  //strip away unneccesary chars
                baseString = "${hgvsp.split(":")[1].substring(2)}"
            } else {
                baseString =  "${hgvsp.substring(2)}"
            }

            if (baseString.contains("*")) { //* is a stop codon. can also be represented as X or Ter (termination). so lets do that
                def baseStringX = baseString.replaceAll('\\*','X')
                def baseStringTer = baseString.replaceAll('\\*','Ter')
                searchStringInner = "${searchStringInner} | \"${baseString}\" | \"${baseStringX}\" | \"${baseStringTer}\""
            } else {
                searchStringInner = "${searchStringInner} | \"${baseString}\""
            }
        }

        //again for amino acid formatted hgvsp
        if (hgvspAa1) {
            String baseString
            if (hgvspAa1.contains(":")) {
                baseString = "${hgvspAa1.split(":")[1].substring(2)}"
            } else {
                baseString =  "${hgvspAa1.substring(2)}"
            }

            if (baseString.contains("*")) { //* is a stop codon. can also be represented as X or Ter (termination
                def baseStringX = baseString.replaceAll('\\*','X')
                def baseStringTer = baseString.replaceAll('\\*','Ter')
                searchStringInner = "${searchStringInner} | \"${baseString}\" | \"${baseStringX}\" | \"${baseStringTer}\""
            } else {
                searchStringInner = "${searchStringInner} | \"${baseString}\""
            }
        }

        //if we have a gene, wrap searchstringinner in brackets and stick it in front.
        if (gene) {
            searchString = "\"${gene}\" (${searchStringInner})"
        } else {
            searchString = searchStringInner
        }

        return searchString
    }
}
