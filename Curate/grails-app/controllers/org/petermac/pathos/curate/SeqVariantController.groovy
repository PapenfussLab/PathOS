/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.converters.JSON
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import org.grails.plugin.easygrid.Easygrid
import org.grails.plugin.easygrid.Filter
import org.grails.plugin.easygrid.FilterOperatorsEnum
import groovy.json.JsonSlurper
import org.petermac.util.Locator

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
    def seqSampleService

    //  EasyGrid services
    //
    def easygridService

    //  Spring security
    //
    def springSecurityService
    def AuditService

    def loc = Locator.instance

    def utilService

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
                sortable        true
                filterToolbar = [ searchOperators: false ]
            }

            columns
            {
                curated_id          { value { SeqVariant sv -> sv.currentCurVariant()?.id }; jqgrid { hidden true; hidedlg true }}
                curated_evd         { value { SeqVariant sv -> sv.currentCurVariant()?.evidence?.justification ?: (sv.currentCurVariant() ? 'NO EVIDENCE' : null)}; jqgrid { hidden true; hidedlg true }}
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
                panel
                {
                    value   { SeqVariant sv -> sv.seqSample.panel.manifest }
                    jqgrid  { width "150"; formatter "showlink"; formatoptions { baseLinkUrl 'panelLink' }}
                    sortClosure { sortOrder -> seqSample { panel { order( 'manifest', sortOrder) }}}
                    filterClosure
                    {
                        Filter filter -> seqSample { panel { applyFilter(delegate, filter.operator, 'manifest', filter.value ) }}
                    }
                }
                sampleName          { jqgrid { width "70"; formatter "showlink"; formatoptions { baseLinkUrl 'sampleLink' }}}
                filterFlag
                reportable          { jqgrid { formatter "checkbox"; formatoptions { disabled true }; editable false; edittype 'checkbox'; width "60"; align "center" } }
                curated             {
                                        name            'curated'
                                        value { SeqVariant sv -> sv.currentCurVariant()?.pmClass ?: '' }
                                        filterClosure
                                        {
                                            Filter filter -> curated { applyFilter(delegate, filter.operator, 'pmClass', filter.value ) }
                                        }

                                        //  because sv.currentCurVariant() can be null, applying this filter doesn't return curated=null rows
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
                                                baseLinkUrl '../../annoVariant/listAnno/'
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
                varPanelPctFormula  //this is numerator and denominator for VarPanelPct
                {
                            value { SeqVariant sv ->
                                if(sv.varSamplesSeenInPanel && sv.varSamplesSeenInPanel) {
                                    return "${sv.varSamplesSeenInPanel}/${sv.varSamplesTotalInPanel}"
                                } else {
                                    return ''
                                }


                            }
                            filterClosure
                                    {
                                        Filter filter ->
                                            applyFilter( delegate,filter.operator, 'varPanelPctFormula', filter.value )
                                    }
                            enableFilter false
                            sortable false
                }
                varcaller
                numamps
                ampbias
                amps
                dbsnp               { enableFilter false; value { SeqVariant sv -> sv.dbsnp ?  sv.dbsnp  : '' }; jqgrid { width "70"; formatter "showlink"; formatoptions { baseLinkUrl  'dbsnpAction'; target  '_blank'}}}
                cosmic              { enableFilter false; value { SeqVariant sv -> sv.cosmic ? sv.cosmic : '' }; jqgrid { width "75"; formatter "showlink"; formatoptions { baseLinkUrl 'cosmicAction'; target  '_blank'}}}
                cosmicOccurs
                gmaf
                esp
                exac
                exon
                cytoband
                googlelink          { value {'Google'}; enableFilter false; sortable false; jqgrid { align "center"; width "70"; formatter "showlink"; formatoptions { showAction 'googleSearchAction'; target  '_blank' }}}
                igv                 { value {'IGV'};    enableFilter false; sortable false; jqgrid { align "center"; width "40"; formatter "showlink"; formatoptions { seqvar 'sv';  showAction 'igvAction'; target '_blank' }}}
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
     * Generate a new SeqSampleReport for a given SeqSample
     * Including the CurVariantReports needed
     *
     * @param id
     * @return
     */
    def buildSeqSampleReport( Long id )
    {
        SeqSample ss = SeqSample.get( id )

        if ( !ss ) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqSample.label', default: 'No seqSample found'), id])
            redirect(controller: "seqSample", action: "list")
            return
        }

        def currentUser = springSecurityService.currentUser as AuthUser

        SeqSampleReport ssr = ReportService.makeNewSeqSampleReport(ss, currentUser)

        redirect(controller: "seqSampleReport", action: "edit", id: ssr.id)
    }

    /**
     * Decide whether current user should see patient demographics
     *
     * @return  True if we should hide patient
     */
    Boolean hidePatient()
    {
        def currentUser = springSecurityService.currentUser as AuthUser

        //  we hide patient details if the user is not an admin, curator, or lab
        //
        if ( currentUser && currentUser.authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_CURATOR" || it.authority == "ROLE_LAB"} )
            return Boolean.FALSE

        return Boolean.TRUE
    }

    /**
     * Find current users preferences for Grid display
     *
     * @return
     */
    def getCurrentUserGridPrefs()
    {
        def slurper = new JsonSlurper()
        def currentUser = springSecurityService.currentUser as AuthUser

        Expando thesePrefs = new Expando()

        thesePrefs.prefsColumnsShown = ''
        thesePrefs.prefsColumnsHidden = ''
        thesePrefs.prefsGridInfo = ''
        thesePrefs.prefsColumnRemap = ''
        thesePrefs.filters = ''

        //  also grab user's filter preferences here. somehow we will pass them to the javascript
        //
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



    def cnvGrid =
    {
        dataSourceType          'gorm'
        domainClass             SeqCnv
        inlineEdit              false
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
                    id                  { type 'id'; key true; jqgrid { hidden true; hidedlg true }}
                    seqSample
                    tags {
                        enableFilter true
                        filterClosure { Filter filter ->
                            tags {
                                applyFilter(delegate, filter.operator, 'label', filter.value )
                            }
                        }
                        value { SeqCnv cnv ->
                            Map results = [
                                meta: [
                                    domainClass: 'seqcnv',
                                    objId: cnv.id
                                ],
                                tags: []
                            ]
                            cnv.tags.each { tag ->
                                Map map = [
                                    id: tag.id,
                                    label: tag.label,
                                    isAuto: tag.isAuto,
                                    createdBy: tag.createdBy,
                                    description: tag.description
                                ]
                                results.tags << map
                            }
                            return "${results as JSON}"
                        }
                    }
                    chr
                    start
                    end
                    resolution
                    arm
                    gene
                    transcript
                    exon
                    copyNumber
                    copyNumberStdDev
                    zScore
                    gaffa {
                        value { SeqCnv cnv ->
                            Locator.instance.links?.linksForObject('SeqCnv', cnv)?.gaffa ?: ""
                        }
                    }
                }
    }
    /**
     * Table definition for Master Curation CurVariant Table in Easygrid format
     */
    def curationGrid =
    {
        dataSourceType          'gorm'
        domainClass             SeqVariant
        inlineEdit              false
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
            rowList     = [ 20, 100, 200, 1000 ]
            editable    false
            sortable    true

            //  Pre-fab templates for quick filtering
            //
            navGrid     =   [ searchOpts:   [   tmplNames:      ["Top Somatic", "Colorectal", "Melanoma", "Lung", "GIST", "Top Germline", "Top Haem", "MPN Simple", "BRCA Only", "Reportable", "Rahman Genes", "TARGET Genes", "Germline genes list for CCP", "ALLOCATE genes list for CCP", "Traceback","IBMD","PHT"],
                                                tmplFilters:    [ 'topSom','topCrc','topMel','topLung','topGist','topGerm','topHaem','mpnSimple','brcaOnly','reportableVars','rahmanGenes','targetGenes', 'germlineCCP', 'allocateCCP', 'topTraceback','topIBMD',"topPHT"]]
            ]
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
            curated_id          { value { SeqVariant sv -> sv.currentCurVariant()?.id }; jqgrid { hidden true; hidedlg true }}
            curated_evd {
                value { SeqVariant sv ->
                    Map result = [
                        acmg: '',
                        amp: '',
                        cs: ''
                    ]
                    CurVariant cv = sv.currentCurVariant()
                    if (cv) {
                        String curUser
                        if (cv.classified?.username) {
                            curUser = sv.currentCurVariant().classified.username
                        } else {
                            curUser = "Unknown"
                        }
                        String blob = cv.fetchAcmgEvidence()?.acmgJustification
                        String acmg = 'NO EVIDENCE'

                        if (blob) {
                            try {
                                Object jsonObj = new JsonSlurper().parseText( blob )
                                acmg = jsonObj?.acmgJustification ?: 'NO EVIDENCE'
                            } catch (e) {}
                        }

                        result.acmg = "${acmg} Curated By ${curUser}"

                        blob = cv.fetchAmpEvidence()?.ampJustification
                        String amp = 'NO EVIDENCE'
                        if (blob) {
                            try {
                                Object jsonObj = new JsonSlurper().parseText( blob )
                                amp = jsonObj?.ampJustification ?: 'NO EVIDENCE'
                            } catch (e) {
                                amp = blob
                            }
                        }
                        result.amp = "${amp} Curated By ${curUser}"

                        result.cs = cv.overallReason ?: "Unclassified"
                    }
                    return (result as JSON) as String
                }; jqgrid { hidden true; hidedlg true }
            }


            id                  { type 'id'; key true; jqgrid { hidden true; hidedlg true }}
            tags {
                enableFilter true
                filterClosure { Filter filter ->
                    tags {
                        applyFilter(delegate, filter.operator, 'label', filter.value )
                    }
                }
                value { SeqVariant sv ->
                    Map results = [
                            meta: [
                                    domainClass: 'seqvariant',
                                    objId: sv.id
                            ],
                            tags: []
                    ];
                    sv.tags.each { tag ->
                        HashMap map =
                                [
                                        id: tag.id,
                                        label: tag.label,
                                        isAuto: tag.isAuto,
                                        createdBy: tag.createdBy,
                                        description: tag.description
                                ]
                        results.tags << map
                    }
                    return "${results as JSON}"
                }
            }
            act
            {
                value {'Submit'}
                enableFilter false;
                sortable false;
            }
            filterFlag
            reportable
            {
                jqgrid
                {
                    hidden false;
                    formatter "checkbox";
                    formatoptions
                    {
                        disabled true
                    };
                    editable true;
                    edittype "checkbox";
                    width "60";
                    align "center";
                }
            }
            curate
            {
                sortable        false
                enableFilter    false
                value
                {
                    SeqVariant sv ->
//                        sv.seqSample ? true : false

                        SeqSample ss = sv.seqSample
                        ClinContext cc = ss.clinContext

                        return sv.curatedInContext(cc)
                }
                jqgrid
                {
                    hidden false;
                    formatter "checkbox";
                    formatoptions
                    {
                        disabled true
                    };
                    editable true;
                    edittype "checkbox";
                    width "60";
                    align "center";
                }
            }
            acmgCurVariant
            {
                jqgrid
                {
                    firstsortorder 'desc'
                }
                sortProperty 'acmgSort'
                enableFilter false
                value
                { return "" //this column is populated from allCuratedVariants in javascript svlist formatter function for this field
                }
            }
            ampCurVariant
            {
                jqgrid
                {
                    firstsortorder 'desc'
                }
                enableFilter false // worry about filtering in a future version -DKGM 1/2/19
                value
                { SeqVariant sv ->
                    sv.currentCurVariant()?.ampClass ?: ""
                }
                sortProperty 'ampSort'
            }
            overallCurVariant
            {
                jqgrid
                {
                    firstsortorder 'desc'
                }
                enableFilter false // worry about filtering in a future version -DKGM 12/2/19
                value
                { SeqVariant sv ->
                    sv.currentCurVariant()?.overallClass ?: ""
                }
                sortProperty 'overallSort'
            }
            allCuratedVariants
            {
                jqgrid
                {
                    firstsortorder 'desc'
                }
                enableFilter false
                value
                { SeqVariant sv ->
                    ArrayList<CurVariant> list = sv.allCurVariants();
                    ArrayList<HashMap> results = [];
                    list.each { cv ->
                        HashMap map =
                        [
                            clinContext: cv.clinContext,
                            authorisedFlag: cv.authorisedFlag,
                            authorised: cv.authorised,
                            pmClass: cv.pmClass,
                            id: cv.id,
                        ]

                        results.push(map)
                    }

                    return "${results as JSON}"
                }
                sortProperty 'maxPmClass'
            }
            sampleName
            gene
            varFreq
            varPanelPct
            varDepth
            readDepth
            varPanelPctFormula  //this is nominator and denominator for VarPanelPct
            {
                value
                { SeqVariant sv ->
                    if(sv.varSamplesSeenInPanel && sv.varSamplesSeenInPanel) {
                        return "${sv.varSamplesSeenInPanel}/${sv.varSamplesTotalInPanel}"
                    } else {
                        return ''
                    }
                }
                filterClosure
                { Filter filter ->
                    applyFilter( delegate,filter.operator, 'varPanelPctFormula', filter.value )
                }
                enableFilter false
                sortable false
            }
            variant
            {
                value   { SeqVariant sv -> return sv.variant }
                jqgrid  {
                            formatter "showlink"
                            formatoptions
                            {
                                baseLinkUrl '../../annoVariant/listAnno/'
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
            dbsnp
            {
                value { SeqVariant sv -> sv.dbsnp ? sv.dbsnp : '' }
                jqgrid { width "75"; formatter "showlink"; formatoptions { baseLinkUrl '../../seqVariant/'; showAction 'dbsnpAction'; target '_blank' }}
                filterClosure
                {
                    Filter filter ->
                        def v = filter.value.replaceAll( /\D+/, '')
                        if (filter.value == 'r' || filter.value == 's' || filter.value == 'rs') {
                            //search for Not Empty, that is what user's trying to do.
                            applyFilter( delegate, FilterOperatorsEnum.NE, 'dbsnp', v )
                        } else {
                            //if stripping non numeric leaves us w/ a blank string, put it back
                            if (v == '') {
                                v = filter.value
                            }
                            applyFilter( delegate, FilterOperatorsEnum.CN, 'dbsnp', v )
                        }
                }
            }
            gmaf        //{  enableFilter false;  }
            esp         //{  enableFilter false;  }
            exac        //{  enableFilter false;  }
            cosmicOccurs
            cosmic
            {
                value { SeqVariant sv -> sv.cosmic ? sv.cosmic : '' }
                jqgrid { width "75"; formatter "showlink"; formatoptions { showAction 'cosmicAction'; target '_blank' }}
                enableFilter false
            }
            exon
            cytoband
            googlelink      { value {'Google'}; enableFilter false; sortable false; jqgrid { align "center"; width "70"; formatter "showlink"; formatoptions { baseLinkUrl '../../seqVariant/'; showAction 'googleSearchAction'; target  '_blank' }}}
            igv             { value {'IGV'};    enableFilter false; sortable false; jqgrid { align "center"; width "40"; formatter "showlink"; formatoptions { baseLinkUrl '../../seqVariant/'; showAction 'igvAction'; target '_blank'}}}
            alamut          { value {'Alamut'}; enableFilter false; sortable false; jqgrid { align "center"; width "70"; formatter "showlink"; formatoptions { baseLinkUrl '../../seqVariant/'; showAction 'alamutAction'; target  '_blank'}}}
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
    private String parseFilterGroup(HashMap jsonObj)
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
        def currentUser = springSecurityService.currentUser as AuthUser

        SeqSample thisSeqSample
        Seqrun thisSeqrun

        if ( params.id && params.id?.isNumber() ) {
            thisSeqSample = SeqSample.get(params.id)
            if (thisSeqSample) {
                thisSeqrun = thisSeqSample.seqrun
            }
        } else if (params.seqrunName && params.sampleName) {
            thisSeqrun = Seqrun.findBySeqrun( params.seqrunName )
            thisSeqSample = SeqSample.findBySeqrunAndSampleName( thisSeqrun, params.sampleName )   //combo of samplename and seqrun is unique
            if (thisSeqSample) {
                params.id = thisSeqSample.id
            }
        }

        //  fail gracefully if no seqrun or seqsample
        //
        if(!thisSeqSample || !thisSeqrun) {
            render(view: "notfound", controller:"seqVariant", model:[sid:params.id? params.id:params.sampleName] )
            return
        }

        def isFirstReviewed = false
        def isFinalReviewed = false
        def ownFirstReview = false
        def ownSecondReview = false

        //also pass a var: if its set we set bg colour and disable

        def thisPrefs   //user prefs live here. will be loaded either from personal or from saved filter.

        //  sample has been reviewed, grab the reviewed filter prefs instead
        //
        if ( thisSeqSample.firstReviewBy )
        {
            isFirstReviewed = true

            if (thisSeqSample.firstReviewBy?.username == currentUser.username ) {
                ownFirstReview = true
            }

            if (thisSeqSample.secondReviewBy?.username == currentUser.username ) {
                ownSecondReview = true
            }

            if ( thisSeqSample.finalReviewBy )
            {
                isFinalReviewed = true
            }
        }


        Boolean vcfExists = false
        File vcf = seqSampleService.retrieveSampleVcf(thisSeqSample.seqrun.seqrun, thisSeqSample.sampleName)
        if (vcf.exists()) {
            vcfExists = true
        }


        thisPrefs = getCurrentUserGridPrefs()   //grab user's set grid prefs


        if (thisPrefs?.filters) {
            def gridconf = easygridService.getGridConfig('seqVariant', 'curation')
            gridconf.userFilter = thisPrefs.filters
            easygridService.setGridConfig('seqVariant', 'curation', gridconf)
        }

        int svSize = SeqVariant.countBySeqSample(thisSeqSample)
        int cnvSize = SeqCnv.countBySeqSample(thisSeqSample)

        //  grab reports
        //
        def viewReports = SeqSampleReport.findAllBySeqSample(thisSeqSample)

        ArrayList<ClinContext> clinContextList = ClinContext.findAll()

        Preferences preferences = Preferences.findByUser(currentUser)
        Boolean compressedView = preferences?.compressedView ?: false
        Integer svlistRows = preferences?.getSvlistRows() ?: 200
        Boolean skipGeneMask = thisSeqSample?.panel?.skipGeneMask ?: false

        String sortPriority = preferences?.sortPriority ?: "acmgCurVariant,allCuratedVariants,ampCurVariant,overallCurVariant,reportable"
//        String sortPriority = "acmgCurVariant,allCuratedVariants,ampCurVariant,overallCurVariant,reportable";

        def labAssays = thisSeqSample.labAssays()

        return  [seqSample: thisSeqSample, isFirstReviewed: isFirstReviewed, isFinalReviewed: isFinalReviewed, ownFirstReview: ownFirstReview, ownSecondReview: ownSecondReview, prefsShowCols: thisPrefs?.prefsColumnsShown,prefsHideCols: thisPrefs?.prefsColumnsHidden,prefsColumnRemap: thisPrefs?.prefsColumnRemap,prefsGridInfo: thisPrefs?.prefsGridInfo, svSize: svSize, cnvSize: cnvSize, viewReports: viewReports, clinContextList: clinContextList, svlistRows: svlistRows, skipGeneMask: skipGeneMask, compressedView:compressedView, labAssays: labAssays, vcfExists: vcfExists, sortPriority: sortPriority ]
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
     * DKGM 18-November-2016
     *
     * We must preserve the current SOP
     * We cannot change people's workflow without massive retraining costs
     *
     * Current workflow:
     * The rows on the svlist page can be highlighted to made "active"
     * Then the checkboxes on each row can be checked
     * Then the edit button can be clicked, revealing a "save" button.
     * When clicked, this save button will submit a form
     * This requests the function "curationInlineEdit()"
     *
     * This form submits a few parameters
     * These parameters can be checked to see what the user wanted to do.
     *
     * @return
     *
     *
     * Refactored by DKGM 29-June-2017 to solve PATHOS-2474
     */
    def curationInlineEdit()
    {
        def sv = SeqVariant.get( request.JSON.id )
        HashMap result = [ message: [] ];

        if(sv.getReportable() != request.JSON.reportable) {
            result.message.push("Reportable for ${sv} changed to "+request.JSON.reportable);
            sv.setReportable(request.JSON.reportable)
        }

        /**
         * DKGM 21-November-2016
         *
         * The user has requested an SV be marked for curation.
         * Mark it as such, if it hasn't already been marked.
         *
         */
        if ( request.JSON.curate == true )
        {
            // Try to curate this variant
            // If it has already been curated, that's fine, nothing will happen.
            Boolean success = sv.curate()
            if(success) {
                String message = "SV [${sv}] has been marked for Curation."
                result.message.push(message);
                flash.message = message;
            } else {
                log.error("Curation Error: curation failed on SeqVariant id ${sv.id}")
                String message = "Error: Failed to mark sv [${sv}] for curation. Please contact a PathOS administrator.";
                result.message.push(message);
                flash.message = message
            }
        }

        render result as JSON
    }

    /**
     * Link to local IGV instance
     *
     * @return
     */
    def igvAction()
    {

        if ( params.id )
        {
            SeqVariant sv = SeqVariant.get( params.id )
            if (sv != null ) {
                def g = new ApplicationTagLib()
                String baseLink = g.createLink(controller: 'IgvSession', absolute: 'true', action: 'sessionXml')
                //     String baseLink = grailsLinkGenerator.link(controller: 'igvSession')

                def url = UrlLink.igvSessionXMLUrl(baseLink, sv.seqSample.seqrun.seqrun, sv.seqSample.sampleName, sv.chr + ':' + sv.pos)

// Alternative:
// def url = UrlLink.igv( sv?.seqSample?.seqrun?.seqrun, sv?.sampleName, sv?.chr + ':' + sv?.pos )

                redirect(url: url)
            }
        } else {
            render "Error, Sequenced Variant not found."
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
     * Link to Panel Page
     *
     * @return
     */
    def panelLink()
    {
        if ( params.id )
        {
            SeqVariant sv = SeqVariant.get( params.id )
            redirect( controller: "panel", action: "show", id: sv.seqSample.panel.id )
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


    /** Update mut context for a seqsample
     *
     */
    def updateClinContext =
    {

                Long ssid = params.seqsampleid as Long

                def ss = SeqSample.get(ssid)

                def currentUser = springSecurityService.currentUser as AuthUser
                if (!(currentUser.authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_DEV" ||  it.authority == "ROLE_CURATOR" || it.authority == "ROLE_LAB" }))
                {
                    flash.message = "Only Curator and Administrator users can change the clinical context of a sequenced sample."
                    println "Only Curator and Administrator users can change the clinical context of a sequenced sample."
                    redirect( action: "svlist", params: [id: ssid])
                } else {


                    def thisMutContext = ClinContext.findByCode(params.clinContext)   //this comes in as code
                    if (params.thisClinContext == 'None' || params.clinContext == '' || !thisMutContext) {
                        ss.setClinContext(ClinContext.generic())
                    } else {
                        ss.setClinContext(thisMutContext)
                    }

                    flash.message = "Set clinical context to " + params.clinContext

                    //  audit log message
                    //
                    AuditService.audit([
                        category:    'context',
                        task:        'clin context update',
                        sample:      ss.toString(),
                        description: "Set clinical context to '" + params.clinContext + "' for SeqSample ${ss.toString()} ID ${ss.id}"
                    ])



                    redirect(action: "svlist", params: [id: ssid])
                }
    }

    /**
     * Check that a user is allowed to revoke something.
     * Then revoke it.
     * Then log the message & return the user.
     */
    def revokeReview(Long id, String review) {
        SeqSample ss = SeqSample.get(id)
        if (!ss) {
            flash.error = "Can't find the SeqSample?"
            redirect( url: "${utilService.context()}/" )
            return
        }

        AuthUser currentUser = springSecurityService.currentUser as AuthUser

        String audit_msg = seqSampleService.revokeReview(ss, review)

        //  Log an audit message
        //
        AuditService.audit([
            category    : 'curation',
            seqrun      : ss.seqrun.seqrun,
            sample      : ss.sampleName,
            task        : "sample curation status",
            description : audit_msg
        ])

        flash.message = audit_msg
        redirect( action: "svlist", id: id )
        return
    }

    /**
     * Check that the user is allowed to authorise something.
     * Then Authorise it.
     * Then log the message & return the user.
     */
    def authoriseReview(Long id, String review) {
        SeqSample ss = SeqSample.get(id)
        if (!ss) {
            flash.error = "Can't find the SeqSample?"
            redirect( url: "${utilService.context()}/" )
            return
        }

        AuthUser currentUser = springSecurityService.currentUser as AuthUser

        HashMap messages = seqSampleService.authoriseReview(ss, currentUser, review)

        //  Log an audit message
        //
        AuditService.audit([
            category    : 'curation',
            seqrun      : ss.seqrun.seqrun,
            sample      : ss.sampleName,
            task        : "sample curation status",
            description : messages.errors.toString() + messages.warnings.toString()
        ])

        flash.errors = messages.errors
        flash.messages = messages.warnings

        redirect( action: "svlist", id: id )
        return
    }

    /**
     * Look up CurVariants for CurVariant overlay
     * @param id
     * @return
     */
    def lookUpCVs ( Long id )
    {
        Long cvid = SeqVariant.get( id )?.currentCurVariant()?.id;
        redirect(controller: "curVariant", action: "lookUpCV", params: [
            id: cvid,
            svid: id
        ])
    }

    def countSVs ( String hgvsg )
    {
        render SeqVariant.countByHgvsg( hgvsg )
    }

    def lookUpSV ( Long id )
    {
        SeqVariant sv = SeqVariant.get( id )
        HashMap results = [error:'SeqVariant not found']
        if( sv ) {
            results = [
                sv: sv,
                hgvsg: sv.hgvsg,
                cc: sv.seqSample?.clinContext?.description,
                contextCode: sv.seqSample?.clinContext?.code,
                clinContextId: sv.seqSample?.clinContext?.id
            ]
        }
        render results as JSON
    }

    /**
     * Look up Tags for Tags overlay
     * @param id
     * @return
     */
    def lookUpTags( Long id ) {
        SeqVariant sv = SeqVariant.get(id);
        if (sv) {
            HashMap map = [hgvsg: sv.hgvsg, tags: sv.tags]
            render map as JSON
        } else {
            render "Error"
        }
    }

    /**
     * Generate and display PDF Report for sample using SeqSampleReport instead of SeqSample
     *
     * This should live in seqSampleReport controller or a service?
     * But I'm having trouble getting beans to work.
     * -DKGM 13-April-2017
     * @return PDF
     */
    def preparedReport()
    {
//        We should fail if there is no id...
//        if(!params.id) render "ERROR"

        SeqSampleReport ssr = SeqSampleReport.get(params.id)
        String fileExt = params.fileExt ?: "pdf"
        Boolean test = params.test as Boolean ?: false
        Boolean publish = params.publish as Boolean ?: false

        if (!ssr) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'seqSampleReport.label', default: 'No seqSampleReport found'), params.id])
            redirect(controller: "seqSampleReport", action: "list")
            return
        }

        String errorMessage = "Couldn't generate report, please check log files"

        //  Generate report
        //
        byte[] bytes
        try
        {
            bytes = reportService.generateReport(ssr, meta(name: 'app.version') as String, test, fileExt, publish)
        }
        catch (Exception e)
        {
            errorMessage = e.message
        }

        if (bytes && bytes.size())
        {
            response.setHeader "Content-disposition", "attachment; filename=${ssr.seqSample.seqrun.seqrun}_${ssr.seqSample.sampleName}.${fileExt}"
            response.contentType = fileExt == 'docx' ? "application/vnd.openxmlformats-officedocument.wordprocessingml.document" : "application/pdf"
            response.contentLength = bytes.size()
            response.outputStream << bytes
            response.outputStream.flush()
        }
        else
        {

            // Display error message and go back to seqSampleReport screen
            //
            flash.message = errorMessage
            redirect(controller: "seqSampleReport", action: "edit", id: params.id)
        }
    }

    /**
     * This function should clone the SSR so we have a snapshot of the SSR sent to Auslab
     * Send the user to the new one
     */
    def cloneSSR() {
        SeqSampleReport ssr = SeqSampleReport.get(params.id)
        SeqSampleReport newSSR = new SeqSampleReport([
            seqSample:              ssr.seqSample,
            reportFilePath:         "none",
            dateCreated:            ssr.dateCreated,
            user:                   ssr.user,
            sample:                 ssr.sample,
//            patient:	            ssr.patient,
//            urn:	                ssr.urn,
//            dob:	                ssr.dob,
//            age:	                ssr.age,
//            sex:	                ssr.sex,
//            requester:	            ssr.requester,
//            location:	            ssr.location,
            morphology:	            ssr.morphology,
            site:	                ssr.site,
            tumour_pct:	            ssr.tumour_pct,
            collect_date:	        ssr.collect_date,
            rcvd_date:	            ssr.rcvd_date,
            ampReads:	            ssr.ampReads,
            ampPct:	                ssr.ampPct,
            lowAmps:            	ssr.lowAmps,
            rois:               	ssr.rois,
//            isdraft:	            ssr.isdraft,
//            clinContext:	        ssr.clinContext,
//            firstReviewer:	        ssr.firstReviewer,
//            firstReviewedDate:	    ssr.firstReviewedDate,
//            secondReviewer:	        ssr.secondReviewer,
//            secondReviewedDate:	    ssr.secondReviewedDate,
//            finalReviewer:	        ssr.finalReviewer,
//            finalReviewedDate:  	ssr.finalReviewedDate,
            citations:	            ssr.citations,
            clinicalDetails:	    ssr.clinicalDetails,
            resultSummary:      	ssr.resultSummary,
            recommendations:    	ssr.recommendations,
            address:	            ssr.address,
            phone:	                ssr.phone,
            requestAddress:	        ssr.requestAddress,
            copyTo:             	ssr.copyTo,
            specimen:	            ssr.specimen,
            sampleType:	            ssr.sampleType,
            histologicalFeatures:	ssr.histologicalFeatures,
            uncoveredRegions:		ssr.uncoveredRegions
        ])
        newSSR.save();

        ssr.curVariantReports.each { cvr ->
            HashMap properties = [
                seqSampleReport:    newSSR,
                curVariant:         cvr.curVariant,
                sample:	            cvr.sample,
                gene:	            cvr.gene,
                refseq:	            cvr.refseq,
                hgvsc:	            cvr.hgvsc,
                hgvsp:	            cvr.hgvsp,
                varreaddepth:	    cvr.varreaddepth,
                totalreaddepth:	    cvr.totalreaddepth,
                afpct:	            cvr.afpct,
                exon:	            cvr.exon,
                pmClass:	        cvr.pmClass,
                ampClass:           cvr.ampClass,
                clinicalSignificance: cvr.clinicalSignificance,
                mut:	            cvr.mut,
                genedesc:	        cvr.genedesc
            ]
            CurVariantReport newCVR = new CurVariantReport(properties);
            newSSR.addToCurVariantReports(newCVR);
        }

        newSSR.save();

        redirect(controller: "seqSampleReport", action: "edit", id: newSSR.id);
    }




    def fetchAnnotations( Long id )
    {
        SeqVariant sv = SeqVariant.get(id);
        HashMap result = [
                fail: "It didn't work"
        ]
        if(sv) {
            result = [
                ok: "We found a SV!",
                gene: sv.gene,
                hgvsc: sv.hgvsc,
                curatedVariant: sv.currentCurVariant(),
                reportable: sv.reportable,
                civic: []
            ]

            Set<CivicVariant> civicIds = []

            String hgvsg_suffix = ""
            try {
                hgvsg_suffix = sv.hgvsg.split(":")[1] ?: ""
                if(hgvsg_suffix && CivicVariant.findByHgvs_expressionsLike( "%${hgvsg_suffix}%" )){
                    CivicVariant civ = CivicVariant.findByHgvs_expressionsLike( "%${hgvsg_suffix}%" )
                    civicIds.add(civ)
                }
            } catch (e) {}


            String hgvsc_suffix = ""
            try {
                sv.hgvsc.split(":")[1] ?: ""
                if(hgvsc_suffix && CivicVariant.findByHgvs_expressionsLike( "%${hgvsc_suffix}%" )){
                    CivicVariant civ = CivicVariant.findByHgvs_expressionsLike( "%${hgvsc_suffix}%" )
                    civicIds.add(civ)
                }
            } catch (e) {}

            if(sv.hgvsp) {
                String hgvsp_suffix = ""
                try {
                    sv.hgvsp.split(":")[1] ?: ""
                    if(hgvsp_suffix && CivicVariant.findByHgvs_expressionsLike( "%${hgvsp_suffix}%" )){
                        CivicVariant civ = CivicVariant.findByHgvs_expressionsLike( "%${hgvsp_suffix}%" )
                        civicIds.add(civ)
                    }
                } catch (e) {}
            }

            civicIds.each { CivicVariant civ ->
                result.civic.push([
                    civicId: civ.id,
                    civicInfo: civ.variant + " - " + civ.summary
                ])
            }

            if(sv.cosmic) {
                result.cosmic = sv.cosmic
                result.cosmicOccurs = sv.cosmicOccurs
            }

            if(Drug.findByMolecularTargets(sv.gene)) {
                result.drug = sv.gene;
                List<Drug> drugs = Drug.findAllByMolecularTargets(sv.gene);
                result.drugInfo = "${drugs.size()} drugs - " + drugs.join(", ");
            }

            if(Trial.findByMolecularAlterations(sv.gene)) {
                result.trial = sv.gene;
                List<Trial> trials = Trial.findAllByMolecularAlterations(sv.gene);
                result.trialInfo = "${trials.size()} trials";
            }

            if(sv.dbsnp) {
                result.dbsnp = sv.dbsnp
            }

        }

        render result as JSON
    }



}






















