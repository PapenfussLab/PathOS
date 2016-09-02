/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.util.Environment
import grails.util.GrailsUtil

//import net.sf.json.JSONObject
//import net.sf.json.JSONSerializer
//import net.sf.json.JSONArray
import org.grails.plugin.easygrid.Easygrid
import org.grails.plugin.easygrid.Filter
import org.grails.plugin.easygrid.FilterOperatorsEnum
import org.grails.plugin.easygrid.Filters

//import org.grails.plugin.easygrid.grids.JqGridMultiSearchService
import org.petermac.pathos.pipeline.UrlLink
import groovy.json.JsonSlurper
import org.petermac.util.Locator

//import groovy.json.JsonBuilder
import java.text.MessageFormat

import static org.grails.plugin.easygrid.GormUtils.applyFilter

import org.petermac.pathos.pipeline.UrlLink


/**
 * Controller for Sequenced Variants. Contains workflow logic for curating new variants and running reports
 */
@Easygrid
class SeqVariantController
{
    //  PathOS report services
    //
    def reportService

    //  Curation services for new variants
    //
    def curateService

    //  EasGrid services
    //
    def easygridService
    def easygridDispatchService
    def JqGridMultiSearchService

    //  Spring security
    //
    def SpringSecurityService

    static scaffold = SeqVariant

    /**
     * List all SeqVariant records
     *
     * @param max
     * @return
     */
    def allsvlist(Integer max)
    {
        params.max = Math.min(max ?: 20, 100)

        session.sampleId = params.id    //  save the sample ID for EasyGrid

        [ seqVariantList: SeqVariant.list(params), seqVariantTotal: SeqVariant.count()]
    }


    /**
     * Table definition for Easygrid
     */
    def allVariantsGrid =
        {
            dataSourceType          'gorm'
            domainClass             SeqVariant
            inlineEdit              false       // prevent automatic editing without action buttons being pressed
            enableFilter            true
            editable                false

            //  Export parameters
            //
            export
            {
                export_title  'AllVariants'
                maxRows       1000000            // Maximum number of variants per export
            }

            //  Grid jqgrid defaults
            //
            jqgrid
            {
                height          '100%'
                rowNum          20
                rowList =       [20, 50, 100]
                sortname        'seqrun'            //  Initial sort order by seqrun
                sortorder       'desc'
                sortable        true
                filterToolbar = [ searchOperators: false ]
            }

            columns
            {
                curated_id          { value { SeqVariant sv -> sv.curated?.id }; jqgrid { hidden true; hidedlg true }}
                curated_evd         { value { SeqVariant sv -> sv.curated?.evidence?.justification ?: (sv.curated ? 'NO EVIDENCE' : null)}; jqgrid { hidden true; hidedlg true }}
                id                  { type 'id'; key true; jqgrid { hidden true; hidedlg true }}
                seqrun
                                    {
                                        value   { SeqVariant sv -> sv.seqSample.seqrun.seqrun }
                                        jqgrid  { width "260"; formatter "showlink"; formatoptions { baseLinkUrl 'seqrunLink' }}
                                        sortClosure { sortOrder -> seqSample { seqrun { order( 'seqrun', sortOrder) }}}
                                        filterClosure
                                        {
                                            Filter filter -> seqSample { seqrun { applyFilter(delegate, filter.operator, 'seqrun', filter.value ) }}
                                        }
                                    }
                sampleName          { jqgrid { width "70"; formatter "showlink"; formatoptions { baseLinkUrl 'sampleLink' }}}
                filterFlag
                reportable          { jqgrid { formatter "checkbox"; formatoptions { disabled true }; editable false; edittype 'checkbox'; width "60"; align "center" } }
                curated             {
                                        name            'curated'
                                        value { SeqVariant sv -> sv.curated?.pmClass ?: '' }
                                        filterClosure
                                        {
                                            Filter filter -> curated { applyFilter(delegate, filter.operator, 'pmClass', filter.value ) }
                                        }

                                        //  because sv.curated can be null, applying this filter doesn't return curated=null rows
                                        //
                                        sortClosure     { sortOrder -> curated { order( 'pmClass', sortOrder)}}
                                    }
                gene
                variant             {
                                        value
                                        {
                                            SeqVariant sv -> return sv.variant
                                        }
                                        jqgrid
                                        {
                                            formatter "showlink"
                                            formatoptions
                                            {
                                                baseLinkUrl '/PathOS/annoVariant/listAnno/'
                                                target '_blank'
                                            }
                                        }
                                        filterClosure
                                        {
                                            Filter filter ->  applyFilter(delegate, filter.operator, 'variant', filter.value )
                                        }
                                    }
                hgvsc
                hgvsp
                consequence
                homopolymer
                varFreq
                varDepth
                readDepth
                varPanelPct
                varcaller
                numamps
                ampbias
                amps
                dbsnp               { enableFilter false; value { SeqVariant sv -> sv.dbsnp ? 'rs' + sv.dbsnp : '' }; jqgrid { width "70";     formatter "showlink"; formatoptions { baseLinkUrl  'dbsnpAction'; target  '_blank'}}}
                cosmic              { enableFilter false; value { SeqVariant sv -> sv.cosmic ? 'COSM' + sv.cosmic : '' }; jqgrid { width "75"; formatter "showlink"; formatoptions { baseLinkUrl 'cosmicAction'; target  '_blank'}}}
                cosmicOccurs
                gmaf
                esp
                exac
                exon
                cytoband
                googlelink          { value {'Google'}; enableFilter false; sortable false; jqgrid { align "center"; width "70"; formatter "showlink"; formatoptions {  baseLinkUrl '/PathOS/seqVariant/'; showAction 'googleSearchAction'; target  '_blank' }}}
                igv                 { value {'IGV'};    enableFilter false; sortable false; jqgrid { align "center"; width "40"; formatter "showlink"; formatoptions { baseLinkUrl '/PathOS/seqVariant/'; seqvar 'sv';  showAction 'igvAction' }}}
                alamut              { value {'Alamut'}; enableFilter false; sortable false; jqgrid { hidden = true; align "center"; width "70"; formatter "showlink"; formatoptions { baseLinkUrl 'alamutAction'; target  '_blank' }}}
                alamutClass
                cadd
                cadd_phred
                mutTasteCat
                siftCat
                polyphenCat
                lrtCat
                mutAssessCat
                fathmmCat
                metaSvmCat
                metaLrCat
                clinvarCat
                mutTasteVal
                siftVal
                polyphenVal
                lrtVal
                mutAssessVal
                fathmmVal
                metaSvmVal
                metaLrVal
                clinvarVal
                clin_sig
                ens_transcript
                ens_gene
                ens_protein
                ens_canonical
                refseq_mrna
                refseq_peptide
                existing_variation
                domains
                genedesc
                omim_ids
                biotype
                pubmed
                vepHgvsg
                vepHgvsc
                vepHgvsp
                mutStatus
                mutError
            }
        }

    /**
     * Generate and display PDF Report for sample
     *
     * @return
     */
    def reportPdf()
    {
        def currentUser = springSecurityService.currentUser as AuthUser


        //  Get SeqVariant Record
        //
        SeqSample sam = SeqSample.get( params.id )
        def noTemplate = false
        //  Generate report
        //
        def bytes
        try {
         bytes = reportService.sampleReport( sam, 'pdf', currentUser.getUsername(), meta(name:'app.version') as String)
        } catch (FileNotFoundException e) {
            noTemplate = true
        }

        if ( bytes && bytes.size())
        {
            response.contentType  = "application/pdf"
            response.contentLength = bytes.size()
            response.outputStream << bytes
            response.outputStream.flush()
        }
        else
        {
            if (noTemplate) {
                flash.message = "Couldn't generate report - there is no template file for this panel."
            } else {
                flash.message = "Couldn't generate report, please check log files"
            }


            //  Go back to original screen
            //
            redirect( action: "svlist", id: params.id )
        }
    }

    /**
     * Generate MS Word Report for sample
     *
     * @return
     */
    def reportWord()
    {
        def currentUser = springSecurityService.currentUser as AuthUser


        //  Get SeqVariant Record
        //
        SeqSample sam = SeqSample.get( params.id )
        def noTemplate = false
        //  Generate report
        //
        def bytes
        try {
            bytes = reportService.sampleReport(sam, 'docx', currentUser.getUsername(), meta(name: 'app.version') as String)
        } catch (FileNotFoundException e) {
           
            noTemplate = true
        }
        //  Send Word document to browser as a byte stream
        //
        if ( bytes && bytes.size())
        {
            response.addHeader("Content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document; charset=utf-8")
//            response.addHeader("Content-Type", "application/msword; charset=utf-8")
            response.contentLength = bytes.size()
            response.outputStream << bytes
            response.outputStream.flush()
        }
        else
        {
            if (noTemplate) {
                flash.message = "Couldn't generate report - there is no template file for this panel."
            } else {
                flash.message = "Couldn't generate report, please check log files"
            }
            //  Go back to original screen
            //
            redirect( action: "svlist", id: params.id )
        }
    }

    def getCurrentUserGridPrefs() {
        //println ('getting current user grid prefs')
        def slurper = new JsonSlurper()
        def currentUser = springSecurityService.currentUser as AuthUser

        Expando thesePrefs = new Expando()

        thesePrefs.prefsColumnsShown = ''
        thesePrefs.prefsColumnsHidden = ''
        thesePrefs.prefsGridInfo = ''
        thesePrefs.prefsColumnRemap = ''
        thesePrefs.filters = ''
        //also grab user's filter preferences here. somehow we will pass them to the javascript
        def thisUserPrefs = UserPersonalPrefs.findByAuthUser(currentUser) as UserPersonalPrefs

        if(thisUserPrefs) {

            thesePrefs.prefsColumnsShown = thisUserPrefs.getColumnsShown()
            thesePrefs.prefsColumnsHidden = thisUserPrefs.getColumnsHidden()
            thesePrefs.prefsColumnRemap = thisUserPrefs.getColumnOrderRemap()

            if (thisUserPrefs.getGridInfoJson()) {
                thesePrefs.prefsGridInfo = slurper.parseText(thisUserPrefs.getGridInfoJson())
                if (thesePrefs.prefsGridInfo.postData.filters) {
                    thesePrefs.filters = thesePrefs.prefsGridInfo.postData.filters

                }
            }
        }
        
        return thesePrefs
    }

    def getReviewedSampleGridPrefs(thisSample) {
        //println ('getting current user grid prefs')
        def slurper = new JsonSlurper()
        def currentUser = springSecurityService.currentUser as AuthUser

        Expando thesePrefs
        thesePrefs = new Expando()

        thesePrefs.prefsColumnsShown = ''
        thesePrefs.prefsColumnsHidden = ''
        thesePrefs.prefsGridInfo = ''
        thesePrefs.prefsColumnRemap = ''
        thesePrefs.filters = ''
        //also grab user's filter preferences here. somehow we will pass them to the javascript
        def thisSamplePrefs = ReviewedSamplePrefs.findBySeqSample(thisSample)
        if(thisSamplePrefs) {

            thesePrefs.prefsColumnsShown = thisSamplePrefs.getColumnsShown()
            thesePrefs.prefsColumnsHidden = thisSamplePrefs.getColumnsHidden()
            thesePrefs.prefsColumnRemap = thisSamplePrefs.getColumnOrderRemap()

            if (thisSamplePrefs.getGridInfoJson()) {
                thesePrefs.prefsGridInfo = slurper.parseText(thisSamplePrefs.getGridInfoJson())
                if (thesePrefs.prefsGridInfo.postData.filters) {
                    thesePrefs.filters = thesePrefs.prefsGridInfo.postData.filters

                }
            }
        }

        return thesePrefs
    }

    def cnvGrid =
    {
        dataSourceType          'gorm'
        domainClass             SeqCnv
        inlineEdit              true
        enableFilter            true
        //addFunction             'columnChooser'
        //savePrefsFunction      'saveFilterPrefs'
        //resetPrefsFunction      'resetFilterPrefs'
        externalParams (['id'])                     // Pass 'id' parameter through to easygrid methods
        // Needed to activate globalFilterClosure{}
        export
                {
                    export_title  'SeqVariants'
                    maxRows       1000000             // Maximum number of variants per export
                }

        //  Grid jqgrid defaults
        //
        jqgrid
                {
                    height      '100%'
                    rowNum      20
                    rowList     = [ 20, 50, 100 ]
                    editable    false
                    sortable    true

                    //  Pre-fab templates for quick filtering
                    //
                    navGrid     =   [ searchOpts:   [   tmplNames: ["Actionable CNVs"], tmplFilters: ["actionableCnvs"]]]
                    filterToolbar = [ searchOperators: false ]
                }

        //  Applies for all records returned
        //
        globalFilterClosure
                {
                    seqSample { eq( 'id', params.id ? params.id as long : -1 ) }
                }

        //  Layout of columns for Curation Grid
        //
        columns
                {
                    seqSample
                    gene
                    cnv_type
                    chr
                    startpos
                    endpos
                    lr_mean
                    lr_median
                    lr_sd
                    gainloss
                    pval
                    n
                    probes_pct
                    pval_adj
                }
    }
    /**
     * Table definition for Master Curation CurVariant Table in Easygrid format
     */
    def curationGrid =
    {
        dataSourceType          'gorm'
        domainClass             SeqVariant
        inlineEdit              true
        enableFilter            true
        addFunction             'columnChooser'
        savePrefsFunction      'saveFilterPrefs'
        resetPrefsFunction      'resetFilterPrefs'
        externalParams (['id'])                     // Pass 'id' parameter through to easygrid methods
                                                    // Needed to activate globalFilterClosure{}
        export
                {
                    export_title  'SeqVariants'
                    maxRows       1000000             // Maximum number of variants per export
                }

        //  Grid jqgrid defaults
        //
        jqgrid
        {
            height      '100%'
            rowNum      20
            rowList     = [ 20, 50, 100 ]
            editable    false
            sortable    true

            //  Pre-fab templates for quick filtering
            //
            navGrid     =   [ searchOpts:   [   tmplNames:      ["Top Somatic", "Colorectal", "Melanoma", "Lung", "GIST", "Top Germline", "Top Haem", "MPN Simple", "BRCA Only", "Reportable", "Rahman Genes", "TARGET Genes"],
                                                tmplFilters:    [ 'topSom','topCrc','topMel','topLung','topGist','topGerm','topHaem','mpnSimple','brcaOnly','reportableVars','rahmanGenes','targetGenes']]
                            ]
            filterToolbar = [ searchOperators: false ]
        }

        //  Applies for all records returned
        //
        globalFilterClosure
        {
            seqSample { eq( 'id', params.id ? params.id as long : -1 ) }
            order( 'reportable', 'desc' )
            order( 'curated',    'desc' )
        }

        //  Layout of columns for Curation Grid
        //
        columns
        {
            curated_id          { value { SeqVariant sv -> sv.curated?.id }; jqgrid { hidden true; hidedlg true }}
            curated_evd {
                value {

                    SeqVariant sv ->
                        def curUser
                        if (sv.curated?.classified?.username) {
                            curUser = sv.curated.classified.username
                        } else {
                            curUser = "Unknown"
                        }
                        //  sv.curated?.evidence?.justification ? sv.curated?.evidence?.justification + " Curated by: " + sv.curated.classified.username  : (sv.curated ? (sv.curated?.classified?.username ? 'NO EVIDENCE' + " Curated by: ${sv.curated.classified.username}" : 'NO EVIDENCE') : null)}; jqgrid { hidden true; hidedlg true }}
                        sv.curated?.evidence?.justification ? sv.curated?.evidence?.justification + " Curated by: " + curUser : (sv.curated ? (curUser ? 'NO EVIDENCE' + " Curated by: ${curUser}" : 'NO EVIDENCE') : null)
                }; jqgrid { hidden true; hidedlg true }
            }


            id                  { type 'id'; key true; jqgrid { hidden true; hidedlg true }}
            act                 {
                                    type        'actions'
                                    sortable    false
                                    jqgrid      {
                                                    hidedlg true;
                                                    formatoptions
                                                    {
                                                        delbutton       false;
                                                        onSuccess       true;
                                                        afterSave       'f:afterEdit';
                                                        afterRestore    'f:reloadGrid'
                                                   }
                                                }
                                }

            filterFlag
            reportable          { jqgrid { formatter "checkbox"; formatoptions { disabled true}; editable true; edittype "checkbox"; width "60"; align "center" } }
            curate
            {
                sortable        false
                enableFilter    false
                value           { SeqVariant sv -> sv.curated ? true : false }
                jqgrid          { formatter "checkbox"; formatoptions { disabled true }; editable true; edittype "checkbox"; width "60"; align "center" }
            }
            curated
            {
                name            'curated'
                sortable        false
                value { SeqVariant sv -> sv.curated?.pmClass ?: '' }
                filterClosure
                {
                    Filter filter -> curated { applyFilter(delegate, filter.operator, 'pmClass', filter.value ) }
                }
            }
            sampleName
            gene
            variant
            {
                value   { SeqVariant sv -> return sv.variant }
                jqgrid  {
                            formatter "showlink"
                            formatoptions
                            {
                                baseLinkUrl '/PathOS/annoVariant/listAnno/'
                                target '_blank'
                            }
                        }
                filterClosure
                        {
                            Filter filter ->  applyFilter(delegate, filter.operator, 'variant', filter.value )
                        }
            }
            hgvsc
            hgvsp
            consequence
            homopolymer
            zygosity
            {
                value
                {
                    SeqVariant sv ->
                        def zyg = 'Unknown'
                        def af  = sv.varFreq ?: 0
                        if ( af > 80 ) zyg = 'Hom'                   //  Homozygous variant
                        if ( 20 < af && af < 65 ) zyg = 'Het'        //  Heterozygous variant
                        return zyg

                }
                sortable     false
                enableFilter false
            }
            varcaller
            numamps
            ampbias
            amps
            varFreq  
            varDepth
            readDepth
            varPanelPct //{  enableFilter false;  }
            dbsnp
            {
                value { SeqVariant sv -> sv.dbsnp ? 'rs' + sv.dbsnp : '' }
                jqgrid { width "75"; formatter "showlink"; formatoptions { baseLinkUrl '/PathOS/seqVariant/'; showAction 'dbsnpAction'; target '_blank' }}
                filterClosure
                {
                    Filter filter ->
                        def v = filter.value.replaceAll( /\D+/, '')
                        applyFilter( delegate, FilterOperatorsEnum.CN, 'dbsnp', v )
                }
            }
            gmaf        //{  enableFilter false;  }
            esp         //{  enableFilter false;  }
            exac        //{  enableFilter false;  }
            cosmicOccurs
            cosmic
            {
                value { SeqVariant sv -> sv.cosmic ? 'COSM' + sv.cosmic : '' }
                jqgrid { width "75"; formatter "showlink"; formatoptions { baseLinkUrl '/PathOS/seqVariant/'; showAction 'cosmicAction'; target '_blank' }}
                filterClosure
                {
                    Filter filter ->
                        def v = filter.value.replaceAll( /\D+/, '')
                        applyFilter( delegate, FilterOperatorsEnum.CN, 'cosmic', v )
                }
            }
            exon
            cytoband
            googlelink      { value {'Google'}; enableFilter false; sortable false; jqgrid { align "center"; width "70"; formatter "showlink"; formatoptions { baseLinkUrl '/PathOS/seqVariant/'; showAction 'googleSearchAction'; target  '_blank' }}}
            igv             { value {'IGV'};    enableFilter false; sortable false; jqgrid { align "center"; width "40"; formatter "showlink"; formatoptions { baseLinkUrl '/PathOS/seqVariant/'; seqvar 'sv'; showAction 'igvAction' }}}
            alamut          { value {'Alamut'}; enableFilter false; sortable false; jqgrid { align "center"; width "70"; formatter "showlink"; formatoptions { baseLinkUrl '/PathOS/seqVariant/'; showAction 'alamutAction'; target  '_blank' }}}
            cadd
            cadd_phred
            mutTasteCat
            siftCat
            polyphenCat
            clinvarCat
            lrtCat
            mutAssessCat
            fathmmCat
            metaSvmCat
            metaLrCat
            mutTasteVal
            siftVal
            polyphenVal
            lrtVal
            mutAssessVal
            fathmmVal
            metaSvmVal
            metaLrVal
            clinvarVal
            clin_sig
            pubmed
            ens_transcript
            ens_gene
            ens_protein
            ens_canonical
            refseq_mrna
            refseq_peptide
            existing_variation
            domains
            genedesc
            omim_ids
            biotype
            vepHgvsg
            vepHgvsc
            vepHgvsp
            mutStatus
            mutError
        }
    }

    //  recursive function used to display human readable filter
    //
    String parseFilterGroup(HashMap jsonObj)
    {
        Map operands = [ "eq" :"=", "ne":"<>","lt":"<","le":"<=","gt":">","ge":">=","bw":"LIKE","bn":"NOT LIKE","in":"IN","ni":"NOT IN","ew":"LIKE","en":"NOT LIKE","cn":"LIKE","nc":"NOT LIKE","nu":"IS NULL","nn":"ISNOT NULL"]
        //println jsonObj
        //println "##"
        String filterReadable = '['
        def rules = jsonObj['rules']
        def groupOp = jsonObj['groupOp']
        def i = 0
        for (rule in rules) {

            def operand
            if(operands[rule['op']]) {
                 operand = operands[rule['op']]
            } else {
                 operand = rule['op']
            }

            //check where to put % wildcards depending on operand. we only have: begins, ends, contains, does not contain, equal, not equal. all but hte last two need wildcards somewhere

            switch (rule['op']) {
                case ['bw']:  //begins with
                    rule['data'] = rule['data'] + '%'
                    break
                case ['ew']:
                    rule['data'] = '%'+rule['data']
                    break
                case ['cn','nc']:   //contains or does not contain
                    rule['data'] = '%'+rule['data']+'%'
                    break
            }

            filterReadable = filterReadable + rule['field'] + (" " + operand) + (" " + "'"+rule["data"]+"'")
            if (i + 1 != rules.size())
            {
                filterReadable = filterReadable + '<br/>' + groupOp + ' '
            }
            i = i + 1
        }
        filterReadable = filterReadable + ']'

        if (jsonObj['groups'])
        {
            for (group in jsonObj['groups'])
            {
                filterReadable = "[" + filterReadable + "<br/>${groupOp}<br/>" + parseFilterGroup(group) + "]"
            }
        }
        return filterReadable
    }

    //  render a human readable version of a jquery filter
    //
    def filterToReadable(String filters)
    {
        def jsonObj = new JsonSlurper().parseText( filters )

        def filterReadable = parseFilterGroup(jsonObj) //call iterative function to get our readable filter string

        render filterReadable
    }


    String filterToSql(String filters)
    {
        def jsonObj = new JsonSlurper().parseText( filters )
        String filterSql

        filterSql = parseFilterGroup(jsonObj).replaceAll("\\[",'(').replaceAll("\\]",')')   //get the human readable filter, replace square brackets with normal ones
        //now replace ' colname ' (note the spaces! we need to replace whole words only, otherwise we can malform) with ' hqlfieldname '

        return filterSql
    }



   

    /**
     * Controller list action for Easygrid
     *
     * @param max
     * @return
     */
    def svlist()
    {
        def thisSeqSample
        def thisSeqrun

        if ( params.id && params.id?.isNumber() ) {
            thisSeqSample = SeqSample.get(params.id)
            if (thisSeqSample)  thisSeqrun = thisSeqSample.seqrun
        } else if (params.seqrunName && params.sampleName) {
            thisSeqrun = Seqrun.findBySeqrun(params.seqrunName)
            thisSeqSample = SeqSample.findBySeqrunAndSampleName(thisSeqrun,params.sampleName)   //combo of samplename and seqrun is unique
            if (thisSeqSample) params.id = thisSeqSample.id
        }

        //  fail gracefully if no seqrun or seqsample
        //
        if(!thisSeqSample || !thisSeqrun) {
            render(view: "notfound", controller:"seqVariant", model:[sid:params.id? params.id:params.sampleName] )
            return
        }

        def isFirstReviewed = false
        def isFinalReviewed = false


        //also pass a var: if its set we set bg colour and disable

        def thisPrefs   //user prefs live here. will be loaded either from personal or from saved filter.

        //  sample has been reviewed, grab the reviewed filter prefs instead
        //
        if ( thisSeqSample.firstReviewBy )
        {
            isFirstReviewed = true

            if ( thisSeqSample.finalReviewBy )
            {
                isFinalReviewed = true
                thisPrefs = getReviewedSampleGridPrefs(thisSeqSample)
            }
        }

        if (! thisSeqSample.finalReviewBy ) {

            thisPrefs = getCurrentUserGridPrefs()   //grab user's set grid prefs only if sample has passed Final Review
        }

        if (thisPrefs.filters) {

            def gridconf = easygridService.getGridConfig('seqVariant', 'curation')
            gridconf.userFilter = thisPrefs.filters
            easygridService.setGridConfig('seqVariant', 'curation', gridconf)
        }

        def allSeqVars = SeqVariant.findAllBySeqSample(thisSeqSample)
        def allCnvs = SeqCnv.findAllBySeqSample(thisSeqSample)


        //get current user as well: we need to know if user is admin or not on the svlist view, so we can disable/enable the buttons on the jqgrid
        def currentUser = springSecurityService.currentUser as AuthUser
        def isAdmin = false
        if (currentUser.authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_DEV"  }) {
            isAdmin = true
        }
        def isCurator = false
        if (currentUser.authorities.any { it.authority == "ROLE_CURATOR"  }) {
            isCurator = true
        }
        def isLab = false
        if (currentUser.authorities.any { it.authority == "ROLE_LAB"  }) {
            isLab = true
        }
        def isDev = false
        if (currentUser.authorities.any { it.authority == "ROLE_DEV"  }) {
            isDev = true
        }

        //grab image for CNV...

        def cnvUrl = null

        cnvUrl = UrlLink.cnvUrl(thisSeqrun.toString(), thisSeqSample.toString())
        Locator loc = Locator.instance


        def cnvViewerUrl = loc.cnvViewerUrl

        //grab reports
        def viewReports = SeqSampleReport.findAllBySeqSample(thisSeqSample)
        def basepath
        switch (GrailsUtil.environment) {
            case "pa_prod":
                basepath = 'http://bioinf-pathos:8080/PathOS'

                break;
            case ["pa_uat","pa_test"]:
                basepath = 'http://bioinf-pathos-test:8080/PathOS'
                break;
            default:
                basepath = 'http://localhost:8080/PathOS'
                break;
        }

        //load icd_o domains
        //
        def icdo = IcdO.getAll() //mut_context in patSample ---> new field   //the value in quetion --> hist_details
        //dropdown values ---> will be hard coded ---> put in in the code its a hack anyway
        //mutContext new field

        //FOR NOW: Not Specified

        ArrayList clinContextList = ClinContext.findAll() //toString will care of this


        def curatedSvPresent = false
        //also work out if any variants are curated
        //if they are, warn when changing mutcontext
        for (sv in thisSeqSample.seqVariants) {
            if  ( sv.curated ) {
                curatedSvPresent = true
            }
        }


        return  [seqSample: thisSeqSample, isFirstReviewed: isFirstReviewed, isFinalReviewed: isFinalReviewed, isAdmin: isAdmin, isCurator: isCurator, isLab: isLab, isDev: isDev, prefsShowCols: thisPrefs.prefsColumnsShown,prefsHideCols: thisPrefs.prefsColumnsHidden,prefsColumnRemap: thisPrefs.prefsColumnRemap,prefsGridInfo: thisPrefs.prefsGridInfo, svSize:allSeqVars.size(), cnvSize: allCnvs.size(), cnvUrl: cnvUrl, cnvViewerUrl: cnvViewerUrl, viewReports: viewReports, basepath: basepath, clinContextList: clinContextList, curatedSvPresent: curatedSvPresent ]
    }

    def googleSearchAction()
    {
        //  need: gene name, nucleotide offset, nuc ref/alt, aa offset, aa ref/alt
        //
        def googleurl = 'http://www.google.com'
        def searchString = ''

        if ( params.id ) {
            SeqVariant sv = SeqVariant.get(params.id)
            //(gene,hgvsc,hgvsp,hgvspAa1)
            def gene = sv.gene?: ''
            def hgvsc = sv.hgvsc?: ''
            def hgvsp = sv.hgvsp?: ''
            def hgvspAa1 = sv.hgvspAa1?: ''
            def ensvar = sv.ens_variant
            searchString = UrlLink.googleSearchVar(gene,hgvsc,hgvsp,hgvspAa1,ensvar)
            searchString = java.net.URLEncoder.encode(searchString)
        }

        redirect( url: "${googleurl}/search?q=${searchString}" )
    }

    /**
     * Inline editing of a variants reportable/curatable flags
     *
     * @return
     */
    def curationInlineEdit()
    {
        boolean changed = false
        def currentUser = springSecurityService.currentUser as AuthUser
        def sv = SeqVariant.get( params.id )
        def ss = sv.seqSample

        assert sv, "Missing SeqVariant in SeqVariantController.curationInlineEdit()"

        //  Set reportable flag if changed
        //
        if ( params.reportable )
        {
            if ((params.reportable == 'Yes') != sv.reportable )
            {
                sv.reportable = ! sv.reportable
                changed = true
            }
        }

        //  Create new CurVariant instance
        //

        if ( params.curate )
        {
            if ( params.curate == 'Yes' )
            {
                if ( ! sv.curated )
                {
                    //todo below you can pass ss.mutContext instead of null to make w/ seqsamples mut context
                    //todo this will need changing for 1.2  - will need code to both made null MC and specific MC curvars
                    if ( curateService.createVariant( sv, null ))
                    {
                        flash.message = "Created new curated CurVariant [${sv}]"

                        changed = true
                    }
                    else {
                        flash.message = "Failed to create curated CurVariant [${sv}]"

                    }
                }
                else {
                    flash.message = "CurVariant ${sv.curated} already exists"

                }
            }
        }

        //  Update record
        //
        if ( changed && ! sv.save( flush: true ))
        {
            sv.errors.each
            {
                log.error( "Failed to update SeqVariant [${sv.id}]" + it )
            }

            //  discard transient object
            //
            sv.discard()
        }

        redirect( action: "svlist", id: sv.seqSampleId )
    }

    /**
     * Link to local IGV instance
     *
     * @return
     */
    def igvAction()
    {

        def currentUser = springSecurityService.currentUser as AuthUser


        if ( params.id )
        {

            SeqVariant sv = SeqVariant.get( params.id )
            def env = Environment.getCurrentEnvironment().name
            def url = UrlLink.igv( sv.seqSample.seqrun.seqrun, sv.sampleName, sv.chr + ':' + sv.pos, env == 'pa_local' )
            redirect( url: url )
        }
    }

    /**
     * Link to local Alamut application
     *
     * @return
     */
    def alamutAction()
    {
        def currentUser = springSecurityService.currentUser as AuthUser


        if ( params.id )
        {
            SeqVariant sv = SeqVariant.get( params.id )
            //def url = UrlLink.alamut( sv.chr + ':' + sv.pos )
            def url = UrlLink.alamut( sv.hgvsc )

            redirect( url: url )
        }
    }

    /**
     * Link to dbSNP web site
     *
     * @return
     */
    def dbsnpAction()
    {
        if ( params.id )
        {
            SeqVariant sv = SeqVariant.get( params.id )
            def url = UrlLink.dbsnp( sv.dbsnp )

            redirect( url: url )
        }
    }

    /**
     * Link to COSMIC web site for mutation
     *
     * @return
     */
    def cosmicAction()
    {
        if ( params.id )
        {
            SeqVariant sv = SeqVariant.get( params.id )
            def url = UrlLink.cosmic( sv.cosmic )

            redirect( url: url )
        }
    }

    /**
     * Link to Seqrun Page
     *
     * @return
     */
    def seqrunLink()
    {
        if ( params.id )
        {
            SeqVariant sv = SeqVariant.get( params.id )
            SeqSample ss = sv.seqSample
            String seqrunName = ss.seqrun.seqrun
            redirect( controller: "seqrun", action: "show", params: [seqrunName: seqrunName] ) //
            //redirect( controller: "seqrun", action: "show",  id: sv.seqSample.seqrun.id )
        }
    }

    /**
     * Link to Sample curation Page
     *
     * @return
     */
    def sampleLink()
    {
        if ( params.id )
        {
            //need to get seqrun and seqsample to build URL form it

            SeqVariant sv = SeqVariant.get( params.id )
            SeqSample ss = sv.seqSample
            String seqrunName = ss.seqrun.seqrun


            //redirect( action: "svlist", id: sv.seqSample.id )
            redirect( action: "svlist", params: [seqrunName: seqrunName, sampleName: ss.sampleName] )
        } else {

        }
    }

    def resetUserGridPrefs() {
        def currentUser = springSecurityService.currentUser as AuthUser
        //check if user has a curate filter yet. if not lets make one
        UserPersonalPrefs thisUserPrefs
        thisUserPrefs = UserPersonalPrefs.findByAuthUser(currentUser) as UserPersonalPrefs
        if(thisUserPrefs) {
            thisUserPrefs.delete()

        } else {
           // println "No user preds to delete"
        }


        render ''
    }

    def resetReviewedGridPrefs(ssid) {


        ReviewedSamplePrefs thisPrefs
        def thisSample = SeqSample.get(ssid)
        thisPrefs = ReviewedSamplePrefs.findBySeqSample(thisSample) as ReviewedSamplePrefs
        if(thisPrefs) {
            thisPrefs.delete()

        } else {
          //  println "No sample prefs to delete"
        }


        render ''
    }

    def saveReviewedGridPrefs(String columnsShowJSON, String columnsHideJSON,String gridColumnOrder, String gridInfoJSON, int ssid) {

        //get current user
        def thisSample = SeqSample.get(ssid)
        def slurper = new JsonSlurper()
        Set<String> columnsShow = new HashSet<String>(slurper.parseText(columnsShowJSON)) //we store these as unordered sets of strings
        Set<String> columnsHide = new HashSet<String>(slurper.parseText(columnsHideJSON))

        //gridInfo = slurper.parseText(gridInfoJson)

        def currentUser = springSecurityService.currentUser as AuthUser
        //check if user has a curate filter yet. if not lets make one
        ReviewedSamplePrefs thisPrefs
        thisPrefs = ReviewedSamplePrefs.findBySeqSample(thisSample)


        if (thisPrefs == null) {

            thisPrefs = new ReviewedSamplePrefs(seqSample: thisSample, columnsShown: columnsShow, columnsHidden: columnsHide, columnOrderRemap: gridColumnOrder, gridInfoJson: gridInfoJSON)
            thisPrefs.save(flush: true, failOnError: true)
        } else {

            thisPrefs.setColumnsShown(columnsShow)
            thisPrefs.setColumnsHidden(columnsHide)
            thisPrefs.setColumnOrderRemap(gridColumnOrder)
            thisPrefs.setGridInfoJson(gridInfoJSON)
        }

        render ''
    }

    def saveUserGridPrefs(String columnsShowJSON, String columnsHideJSON,String gridColumnOrder, String gridInfoJSON)
    {

        //get current user
        def slurper = new JsonSlurper()
        Set<String> columnsShow = new HashSet<String>(slurper.parseText(columnsShowJSON)) //we store these as unordered sets of strings
        Set<String> columnsHide = new HashSet<String>(slurper.parseText(columnsHideJSON))

        //gridInfo = slurper.parseText(gridInfoJson)

        def currentUser = springSecurityService.currentUser as AuthUser
        //check if user has a curate filter yet. if not lets make one
        UserPersonalPrefs thisUserPrefs
        thisUserPrefs = UserPersonalPrefs.findByAuthUser(currentUser) as UserPersonalPrefs


        if (thisUserPrefs == null) {

            thisUserPrefs = new UserPersonalPrefs(authUser: currentUser, columnsShown: columnsShow, columnsHidden: columnsHide, columnOrderRemap: gridColumnOrder, gridInfoJson: gridInfoJSON)
            thisUserPrefs.save(flush: true, failOnError: true)
        } else {

            thisUserPrefs.setColumnsShown(columnsShow)
            thisUserPrefs.setColumnsHidden(columnsHide)
            thisUserPrefs.setColumnOrderRemap(gridColumnOrder)
            thisUserPrefs.setGridInfoJson(gridInfoJSON)
        }

        //if (filter.value ) {
        //    println (filter.value)
        //}
        //thisUserPrefs = UserPrefs.findByAuthUser(currentUser) as UserPrefs

        //redirect( action: "svlist", params: [id: sampleId] )
        render ''
    }


    /*
     * this gets a list of all variants in current grid that pass filter
     */
    def getCurrentGridVariants() {
        def gridConfig = easygridService.getGridConfig('seqVariant', 'curation')
        def listParams = easygridDispatchService.callGridImplListParams(gridConfig)
        listParams.maxRows = null //no maxrows (that paginates)



        def userfilter = gridConfig.userFilter
        if (!userfilter) {  //i think sometimes this isnt set if we use the back button instead of reloading a page. so grab the saved version in that case.
            userfilter = getCurrentUserGridPrefs().filters

        }


        Filters filters = new Filters()

        if(userfilter) {
            filters = JqGridMultiSearchService.multiSearchToCriteriaClosure(gridConfig, userfilter)
        }

        if (gridConfig.globalFilterClosure) {
            def f = new Filter()
            f.global = true

            f.searchFilter = gridConfig.globalFilterClosure

            filters << f
        }


        //we have to add seq sample id filter ourselves
        filters << easygridDispatchService.callGridImplFilters(gridConfig)
        // apply the selection component constraint filter ( if it's the case )
        if (gridConfig.autocomplete) {
            filters << easygridDispatchService.callACFilters(gridConfig)
        }

        //add the search form filters
        if (gridConfig.filterForm) {
            filters << easygridDispatchService.callFFFilters(gridConfig)
        }

        def currentVarList = easygridDispatchService.callDSList(gridConfig, listParams, filters)

        return currentVarList
    }

    /*
    check all seqvars when final review form submitted and return a map of errors and warnings (or false)
    our critera:
    SeqSample must have QC passed
    All reporting variants must be Curated
    All ticked curated variants are curated and authorised
     */
    Map checkReviewValidationErrors(String reviewType) {
        List<String>  errors = []
        List<String>  warnings = []
        Map out = [:]
        out['errors'] = errors
        out['warnings'] = warnings
        def currentVars = getCurrentGridVariants()




        def ss = SeqSample.get(params.id)

        if (!ss.authorisedQcFlag || !(ss.passfailFlag)) {
            errors.add("Cannot complete review: Sample must pass QC first")
        }
        def gridConfig = easygridService.getGridConfig('seqVariant', 'curation')



        for ( seqvar in currentVars ) {
            //REQUIREMENT: All reporting variants must be Curated
            if (seqvar.reportable && !seqvar.curatedId) {
                def reportError = "Cannot complete review: ${seqvar} is Reported but not Curated"
                errors.add(reportError)

            }

            //get variant for the seqvar
            def var = CurVariant.get(seqvar.curatedId)

            if (var) {
                //REQUIREMENT: warn if any C5 Pathogenic variants are not reported
                if (var.pmClass.contains('C5') && (!seqvar.reportable)) {
                    warnings.add("Warning: SeqVariant ${seqvar} / CurVariant ${var} is C5 Pathogenic but not reportable.")
                }
                //REQUIREMENT: All ticked curated variants are curated and authorised - but (since PATHOS-907) only during final review (
                if (seqvar.curatedId && reviewType == 'final') {
                    //if our seqvar is curated...
                    //ensure variant for this seqvariant is curated and authorised
                    if (!var.authorisedFlag) {
                        def authVarError = "Cannot complete review: The CurVariant record for seqVariant ${seqvar} is not Authorised"
                        errors.add(authVarError)
                    }
                }
            }
        }

        //PATHOS-541
        if (reviewType == 'final') {
            def thisSample = ss.patSample
            if (thisSample) {
                def thisPatient = thisSample.patient
                if (thisPatient) {
                    def thisPatientSamples = PatSample.findAllByPatient(thisPatient)
                    if (thisPatientSamples.size() > 1) {
                        for (psample in thisPatientSamples) {
                            if (psample.id != thisSample.id) {
                                def ss_string = ''
                                def pssamples = SeqSample.findAllByPatSample(psample)
                                if (pssamples) {
                                    for (pss in pssamples) {
                                        ss_string = ss_string + "<a href='${request.contextPath}/SeqSample/show/${pss.id}' style='color:red'>${pss}</a> "
                                    }
                                    warnings.add("Warning: this patient has another Sample with SeqSamples. Sample: <a href='${request.contextPath}/sample/show/${psample.id}' style='color:red'>${psample}</a> SeqSamples: ${ss_string}")

                                }
                            }
                        }
                    }
                }
            }
        }


        if(!errors.isEmpty())
        {
            out['errors'] = errors
        }


        if(!warnings.isEmpty())
        {
            out['warnings'] = warnings
        }


        return out
    }
    /** Update mut context for a seqsample
     *
     */
    def updateClinContext =
            {
                Long ssid = params.seqsampleid as Long

                def ss = SeqSample.get(ssid)
                def thisMutContext = ClinContext.findByCode(params.clinContext)   //this comes in as code
                if (params.thisClinContext == 'None' || params.clinContext == '') {
                    ss.setClinContext(null)
                } else {
                    ss.setClinContext(thisMutContext)
                }


                flash.message = "Set clinical context to " + params.clinContext


                redirect( action: "svlist", params: [id: ssid])
    }
    /** Update a holly sample
     *
     */

    def updateHolly =
    {
        Long ssid = (params.seqsampleid ?: null) as Long
        Long psid = (params.patsampleid ?: null) as Long

        def ss = SeqSample.get(ssid)

        def thisPatSample = ( psid ? PatSample.get(psid) : null )

        if ( thisPatSample && PatSampleController.loadHollyData(thisPatSample.sample))
        {
            def thisPatSampleUpdated = PatSample.get(psid)
            if (params.hollylastupdate != thisPatSampleUpdated.hollyLastUpdated && params.hollylastupdate != '0')
            {
                flash.message = "Patient details updated. This data update: " + thisPatSampleUpdated.hollyLastUpdated + " (previous data update: " + params.hollylastupdate + ")"
            }
            else if (params.hollylastupdate == '0')
            {
                //  new data update
                //
                flash.message = "Patient details found and loaded."
            }
            else
            {
                flash.message = "Patient details already up to date, no new data."
            }
        }
        else
        {
            flash.message = "No patient details available"
        }

        redirect( action: "svlist", params: [id: ssid, seqSample: ss] )
    }


    /**
     * Authorise a review of a sample, either first or final
     */
    def authoriseReview =
    {
        Long id = params.id as Long
        def currentUser = springSecurityService.currentUser as AuthUser

        def warningmessage

        //User must be lab, curator, or admin
        //
        if (!(currentUser.authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_DEV" ||  it.authority == "ROLE_CURATOR" || it.authority == "ROLE_LAB"}))
        {
            flash.message = "You do not have sufficient privileges to perform this action"
            redirect( action: "svlist", id: id )
            return
        }

        //  Must be either the first or the final review button
        //
        if ( ! params.final && ! params.first && ! params.second ) return
        def reviewType = (params.final ? 'final' : params.second ? 'second' : 'first')
        //grab the seqsample
        //
        def ss = SeqSample.get(id)
        if ( ! ss )
        {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqSample.label', default: 'SeqSample'), id])
            redirect( action: "svlist", id: id )
            return
        }


        //are we revoking a review or authorising one?
        //
        def revoke = false
        //if ( (reviewType == 'final' && ss.finalReviewBy) || (reviewType == 'first' && ss.firstReviewBy) ||  (reviewType == 'second' && ss.secondReviewBy)) {
        if ( params.revoke )
        {
            revoke = true
            //check privilige to be safe - only Admins can revoke
            if (!(currentUser.authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_DEV" }))
            {
                flash.message = "You must be an administrator to revoke a review"
                redirect( action: "svlist", id: id )
                return
            }
        }

        //do not let revoke first review if second already done
        //
        if (reviewType == 'first' && revoke == true &&  ss.secondReviewBy) {
            flash.message = "Cannot revoke first review because the sample has already undergone second review"
            redirect( action: "svlist", id: id )
            return
        }

        //we could have concurrency issues where reviewers overwrite each other: one last check that we're not clobbering an existing review
        //
        if ( !revoke && ((reviewType == 'first' && ss.firstReviewBy) ||  (reviewType == 'second' && ss.secondReviewBy) || (reviewType == 'final' && ss.finalReviewBy))) {
            flash.message = "Cannot complete ${reviewType} review - this sample has already undergone ${reviewType} review"
            redirect( action: "svlist", id: id )
            return
        }



        //check for errors
        //
        if(! revoke ) {
            //need to validate our vars
            def valErrors = checkReviewValidationErrors(reviewType)
            if (valErrors.errors) {

                flash.errors = valErrors['errors']
                redirect( action: "svlist", id: id )    //if we have errors, return before we set review
                return
            }
            if (valErrors.warnings) {
                flash.errors = valErrors['warnings']

            }
        }

        //  Toggle finalReviewBy to current user
        //
        if ( reviewType == 'final')
        {
            if (!(currentUser.authorities.any { it.authority == "ROLE_ADMIN"  || it.authority == "ROLE_DEV" || it.authority == "ROLE_CURATOR"}))
            {
                flash.message = "You do not have sufficient privileges to perform this action"
                redirect( action: "svlist", id: id )
                return
            }
            //  The sample must be curated to have an authorisation change
            //
            if ( ! ss.firstReviewBy )
            {
                flash.message = "Sample has not yet passed First Review."

                redirect( action: "svlist", id: id )
                return
            }





            //  The authoriser cant be the curator.. unless we are revoking
            //
            if ( ss.firstReviewBy.displayName == currentUser.getDisplayName()  && !revoke)
            {
                def errorList = []
                errorList.add("Final Reviewer must be different to First Reviewer.")
                flash.errors = errorList
                redirect( action: "svlist", id: id )
                return
            }




            ss.finalReviewBy = (revoke ? null : currentUser)
            if (!revoke) {
                ss.finalReviewedDate = new Date()   //if NOT revoking
            }
        }
        else if (reviewType == 'first')   //first review
        {
            if ( ss.finalReviewBy )
            {
                flash.message = "Sample has already passed Final Review."
                redirect( action: "svlist", id: id )
                return
            }


            ss.firstReviewBy    = (revoke   ? null : currentUser)
            if (!revoke) {
                ss.firstReviewedDate = new Date()   //if NOT revoking
            }

        }  else if (reviewType == 'second')   //first review
        {
            if ( ss.finalReviewBy )
            {
                flash.message = "Sample has already passed Final Review."
                redirect( action: "svlist", id: id )
                return
            }

            if ( ss.firstReviewBy.displayName == currentUser.getDisplayName()  && !revoke)
            {
                def errorList = []
                errorList.add("Second Reviewer must be different to First Reviewer.")
                flash.errors = errorList
                redirect( action: "svlist", id: id )
                return
            }


            ss.secondReviewBy    = (revoke   ? null : currentUser  )

            if (!revoke) {
                ss.secondReviewedDate = new Date()   //if NOT revoking
            }

        }


        //  Save updates
        //
        if ( ! ss.save(flush: true))
        {
            log.error( "Failed to update ${reviewType} for [${ss}]")
        }

        //  Log an audit message
        //
        def audit_msg = "Successfully set Curation Review: ${reviewType} on ${ss.sampleName} to Final Review by: ${ss.finalReviewBy} First Review by: ${ss.firstReviewBy}  Second Review by: ${ss.secondReviewBy}"
        def audit     = new Audit(  category:    'curation',
                                    seqrun:      ss.seqrun.seqrun,
                                    sample:      ss.sampleName,
                                    complete:    new Date(),
                                    elapsed:     0,
                                    software:    'Path-OS',
                                    swVersion:   meta(name: 'app.version'),
                                    task:        "sample curation status",
                                    username:    currentUser.getUsername(),
                                    description: audit_msg )

        if ( ! audit.save( flush: true ))
        {
            audit?.errors?.allErrors?.each
            {
                log.error( new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
            }
            log.error( "Failed to log audit message: ${audit_msg}")

        }

        flash.message = audit_msg
        if(warningmessage) {
            flash.message = flash.message + '\n' + warningmessage
        }

        redirect( action: "svlist", params: [id: id, seqSample: ss] )
    }


}
