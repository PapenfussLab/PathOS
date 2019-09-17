/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.converters.JSON
import org.grails.plugin.easygrid.Easygrid
import org.grails.plugin.easygrid.Filter
import org.petermac.pathos.pipeline.UrlLink
import org.petermac.util.Locator
import org.springframework.dao.DataIntegrityViolationException

import static org.grails.plugin.easygrid.GormUtils.applyFilter

@Easygrid
class CurVariantController
{
    static scaffold = CurVariant
    def SpringSecurityService

    static def loc = Locator.instance
    static def env = loc.pathosEnv

    def AuditService
    JiraService jiraService
    def CurateService

    static allowedMethods = [ update: "POST" ]

    def index()
    {
        redirect(action: "list", params: params)
    }

    def create()
    {
        [variantInstance: new CurVariant(params)]
    }

    def save()
    {
        def variantInstance = new CurVariant(params)
        if (!variantInstance.save(flush: true))
        {
            render(view: "create", model: [variantInstance: variantInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'variant.label', default: 'CurVariant'), variantInstance.id])
        redirect(action: "show", id: variantInstance.id)
    }

    def show(Long id)
    {
        def variantInstance

        if (id) {    //in case this is called directly
            variantInstance = CurVariant.get(id)
        }

        //get jiras for this
        def jiraIssues = JiraIssue.findAllByCurVariant(variantInstance)

        if (!variantInstance)
        {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'variant.label', default: 'CurVariant'), id])
            redirect(action: "list")
            return
        }
        Locator loc = Locator.instance
        def jiraAddress = loc.jiraAddress

        SeqVariant originating = null
        try {
            originating = variantInstance.originating
        } catch (e) {
            log.error("Can't find originating variant for CurVariant")
            log.error(e)
        }

        [ variantInstance:variantInstance, jiraIssues:jiraIssues, jiraAddress:jiraAddress, originating:originating ]
    }

    
    static private Map defaultCriteria = [
        benign: [
            BA1: [
                default: 'Stand Alone',
                options: ['Stand Alone', 'Supporting', 'Strong']
            ],
            BS1: [
                default: 'Strong',
                options: ['Stand Alone', 'Strong', 'Supporting']
            ],
            BS2: [
                default: 'Strong',
                options: ['Stand Alone', 'Strong', 'Supporting']
            ],
            BS3: [
                default: 'Strong',
                options: ['Stand Alone', 'Strong', 'Supporting']
            ],
            BS4: [
                default: 'Strong',
                options: ['Stand Alone', 'Strong', 'Supporting']
            ],
            BP1: [
                default: 'Supporting',
                options: ['Stand Alone', 'Strong', 'Supporting']
            ],
            BP2: [
                default: 'Supporting',
                options: ['Stand Alone', 'Strong', 'Supporting']
            ],
            BP3: [
                default: 'Supporting',
                options: ['Stand Alone', 'Strong', 'Supporting']
            ],
            BP4: [
                default: 'Supporting',
                options: ['Strong', 'Supporting']
            ],
            BP5: [
                default: 'Supporting',
                options: ['Stand Alone', 'Strong', 'Supporting']
            ],
            BP6: [
                default: 'Supporting',
                options: ['Stand Alone', 'Strong', 'Supporting']
            ],
            BP7: [
                default: 'Supporting',
                options: ['Stand Alone', 'Strong', 'Supporting']
            ]
        ],
        pathogenic: [

            PVS1: [
                default: 'Very Strong',
                options: ['Very Strong', 'Strong', 'Moderate', 'Supporting']
            ],
            PS1: [
                default: 'Strong',
                options: ['Strong', 'Moderate', 'Supporting']
            ],
            PS2: [
                default: 'Strong',
                options: ['Very Strong', 'Strong', 'Moderate', 'Supporting']
            ],
            PS3: [
                default: 'Strong',
                options: ['Strong', 'Moderate', 'Supporting']
            ],
            PS4: [
                default: 'Strong',
                options: ['Strong', 'Moderate', 'Supporting']
            ],
            PM1: [
                default: 'Moderate',
                options: ['Strong', 'Moderate', 'Supporting']
            ],
            PM2: [
                default: 'Moderate',
                options: ['Strong', 'Moderate', 'Supporting']
            ],
            PM3: [
                default: 'Moderate',
                options: ['Very Strong', 'Strong', 'Moderate', 'Supporting']
            ],
            PM4: [
                default: 'Moderate',
                options: ['Strong', 'Moderate', 'Supporting']
            ],
            PM5: [
                default: 'Moderate',
                options: ['Strong', 'Moderate', 'Supporting']
            ],
            PM6: [
                default: 'Moderate',
                options: ['Strong', 'Moderate', 'Supporting']
            ],
            PP1: [
                default: 'Supporting',
                options: ['Strong', 'Moderate', 'Supporting']
            ],
            PP2: [
                default: 'Supporting',
                options: ['Strong', 'Moderate', 'Supporting']
            ],
            PP3: [
                default: 'Supporting',
                options: ['Strong', 'Moderate', 'Supporting']
            ],
            PP4: [
                default: 'Supporting',
                options: ['Strong', 'Moderate', 'Supporting']
            ],
            PP5: [
                default: 'Supporting',
                options: ['Strong', 'Moderate', 'Supporting']
            ]
        ]
    ]


    def edit(Long id)
    {
        def variantInstance = CurVariant.get(id)
        if (!variantInstance)
        {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'variant.label', default: 'CurVariant'), id])
            redirect(action: "list")
            return
        }

//  ACMG metadata
        HashMap acmg = [
            count: [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15],
            pathogenic: ["PVS1","PS1","PS2","PS3","PS4","PM1","PM2","PM3","PM4","PM5","PM6","PP1","PP2","PP3","PP4","PP5"],
            benign: ["BA1","BS1","BS2","BS3","BS4","BP1","BP2","BP3","BP4","BP5","BP6","BP7"],
            na: ["PM3","BP5","BP1", "BP6", "PVS1", "PS2", "PM1", "PP4", "PP5", "BS2","PS1", "PM4","PM5","PM6","PP2","PP3","BP3","BP4","BP7","BS4"],
            info: ["PVS1","PM3","PM6","PS2","PS4"],
            pathogenicStrengths: ["Very Strong", "Strong", "Moderate", "Supporting"],
            benignStrengths: ["Stand Alone", "Strong", "Supporting"]
        ]

        AcmgEvidence acmgEvidence = variantInstance.fetchAcmgEvidence()
        AmpEvidence ampEvidence = variantInstance.fetchAmpEvidence()


        [ variantInstance: variantInstance, acmg: acmg, acmgEvidence: acmgEvidence, ampEvidence: ampEvidence, defaultCriteria: defaultCriteria ]
    }

    /**
     * If the new reason == old reason, it doesn't need a stamp
     * Unless the new reason is blank, it needs a stamp
     */
    private boolean reasonNeedsStamp(String newReason, String oldReason) {
        boolean result
        if ( newReason == null || newReason == "" || oldReason == null ) {
            result = true
        } else {

// mysql is might store different carriage returns vs what javascript is giving us.
            result = newReason.replaceAll("\\r\\n?", "\n") != oldReason.replaceAll("\\r\\n?", "\n")
        }
        return result
    }

    /**
     * Update a variant after a CurVariant has been edited
     * Check that the curVariant exists and JSON is good
     * Check version is ok
     *
     * Calculate & update ACMG
     * Calculate & update AMP
     *
     * If not overridden, then update the curVariant
     * Else don't update curVariant's acmg/amp value
     *
     * Check:
     *  - Only Admins, Devs & Curators can authorise a variant
     *
     *
     * Things to log:
     * If classification changes
     * If authorisation changes
     *
     *
     *
     *
     * ****
     * How this thing works:
     * Check things are valid
     * Call services
     * Do an update
     * Do audits / return errors / etc.
     *
     */
    def jsonUpdate () {

        println "Doing a json update"

        try {


// Prepare things...

            def json = request.JSON
            Map data = [
                curVariant   : [:],
                acmgEvidence : [:],
                ampEvidence  : [:]
            ]

            data.each { entity ->
                if (entity.key) {
                    json[entity.key].each { pair ->
                        data[entity.key][pair.name] = pair.value
                    }
                }
            }


//            println "Data is:"
//            println data

            CurVariant cv = CurVariant.get( Long.parseLong(data.curVariant.id) )

            if ( !cv || !data.curVariant.version ||  cv.version > Long.parseLong( data.curVariant.version) ) {
                // Todo: throw error
            }

            AuthUser currentUser = springSecurityService.currentUser as AuthUser
            String time = new Date().format("h:mm a d-MMM-yyyy")
            String user = currentUser.toString()



            data.history = [
                time: time,
                user: user,
                workgroup: "Peter Mac",
                summary: "NONE",
                actions: [:]
            ]




// Is this stuff for validation? Or should it be in the service?
            String prevAcmgClass = cv.pmClass
            String prevAmpClass = cv.ampClass
            String prevOverallClass = cv.overallClass

            Boolean acmgSync = data.curVariant.acmgSync == 'true'
            Boolean ampSync = data.curVariant.ampSync == 'true'

            Boolean prevAuth = cv.authorisedFlag
            AuthUser prevAuthUser = cv.authorised

            String authMessage = "No Auth Change"
            boolean usrAuth = data.curVariant.authorisedFlag ?: false

            if ( !prevAuth && usrAuth ) {
                if (!(currentUser.authorities.any {
                    it.authority == "ROLE_ADMIN" || it.authority == "ROLE_DEV" || it.authority == "ROLE_CURATOR"
                }))
                {
                    flash.message = "You must be an Administrator or Curator to authorise a CurVariant!"
                    render(view: "edit", model: [variantInstance: cv])
                    return
                }
            }

            data.curVariant.authorisedFlag = usrAuth
            if( usrAuth ) {
                data.curVariant.authorised = currentUser
                data.curVariant.lastAuthorised = new Date()
                authMessage = "Changed authorisation to ${data.curVariant.authorisedFlag} by ${currentUser}"

            } else {
                data.curVariant.authorised = null
                data.curVariant.lastAuthorised = null
                authMessage = "De-authorised this variant ${cv.toString()}, previous authorisor was: ${prevAuthUser}"
            }
            log.info( authMessage )










//  Calculate ACMG evidence, see if it is in sync (maybe update curVariant), and set it.
//  Calculate AMP evidence, see if it is in sync (maybe update curVariant) and set it.
//  If in sync, we sync (i.e. update CurVariant). Otherwise don't need to do anything.
//  Save everything.


            // Reject both and return 412 if we have an invalid character
            try {

//  Process data using services, to prepare a data blob which will be saved.
                data.acmgEvidence = EvidenceService.processACMG( data.acmgEvidence, defaultCriteria )

// If it's in sync, clear everything and put in the sync'd settings.
                if ( acmgSync ) {
                    if ( data.curVariant.pmClass != data.acmgEvidence.classification ) {
                        data.curVariant.pmClass = data.acmgEvidence.classification
                        data.curVariant.classOverrideReason = ""
//                        data.curVariant.classified = currentUser
                        data.curVariant.lastUpdated = new Date()
                    }
                } else {
// Otherwise...
// if it is not in-sync.
// Timestamp it if the override reason has changed or pm class has changed.
                    if (data.curVariant.pmClass ||
                        reasonNeedsStamp(data.curVariant.classOverrideReason, cv.classOverrideReason)
                    ) {
//                        data.curVariant.classified = currentUser
                        data.curVariant.lastUpdated = new Date()
                        data.curVariant.classOverrideReason += "\nSet by ${user} at ${time}"
                    }
                }

                cv.fetchAcmgEvidence().setProperties(data.acmgEvidence)






                data.ampEvidence.classification = EvidenceService.calculateAMP(data.ampEvidence)

// Possible legacy stuff? This block handles the case where some curVariants do not have AMP Evidence.
// It also calculates data.curVariant.ampEvidence
// It also sets the ampEvidence
// DKGM 2-April-2019
                if (cv.fetchAmpEvidence()) {
//                    println "Amp evidence already exists, so set it"

//                    println "setting this data..."
//                    println data.ampEvidence

                    AmpEvidence ampEvidence = cv.fetchAmpEvidence()
                    ampEvidence.setProperties(data.ampEvidence)
                    ampEvidence.save(flush: true)

                } else {
//                    println "Amp evidence does not exist, so create a new one and save it to the curVariant"

                    data.ampEvidence.curVariant = cv

//                    println data.ampEvidence

                    AmpEvidence ampEvidence = new AmpEvidence(data.ampEvidence)

                    ampEvidence.save(flush: true)

                    data.curVariant.ampEvidence = ampEvidence

//                    println "did it work?"
//                    println data.curVariant.ampEvidence
                }




// If amp is in sync, feel free to clober it in CurVariant.
                if ( ampSync ) {
                    if ( data.curVariant.ampClass != data.ampEvidence.classification ) {
                        data.curVariant.ampClass = data.ampEvidence.classification
                        data.curVariant.ampReason = ""
//                        data.curVariant.classified = currentUser
                        data.curVariant.lastUpdated = new Date()
                    }
                } else {
// Otherwise we check if the override reason or override class has changed, and therefore need a stamp.
                    if (data.curVariant.ampClass ||
                        reasonNeedsStamp(data.curVariant.ampReason, cv.ampReason)
                    ) {
//                        data.curVariant.classified = currentUser
                        data.curVariant.lastUpdated = new Date()
                        data.curVariant.ampReason += "\nSet by ${user} at ${time}"
                    }
                }





// See if Clinical Significance has changed, and stamp accordingly.
                if (data.curVariant.overallClass || data.curVariant.overallReason) {
                    if (data.curVariant.overallClass ||  reasonNeedsStamp(cv.overallReason, data.curVariant.overallReason)) {
//                        data.curVariant.classified = currentUser
                        data.curVariant.lastUpdated = new Date()
                        if (data.curVariant.overallClass == "Unclassified") {
                            data.curVariant.overallReason = ""
                        } else {
                            data.curVariant.overallReason += "\nSet by ${user} at ${time}"
                        }
                    }
                } else if (data.curVariant.overallReason == '') {
                    data.curVariant.overallClass = 'Unclassified'
                }




                cv.setProperties(data.curVariant)
                cv.save(flush: true)



// Only audit log after settings have been successfully set.
                if ( prevAuth != data.curVariant.authorisedFlag ) {
                    AuditService.audit([
                        category    : 'curation',
                        variant     : cv.toString(),
                        task        : 'curation authorisation',
                        description : authMessage
                    ])
                }

            } catch (Exception e) {
                log.error( "Failed to save CurVariant or Evidence")
                log.error( e )
                response.status = 412
                render e as JSON
                return
            }




            // If acmg evidence class has changed
            if ( prevAcmgClass != cv.pmClass )
            {
                data.curVariant.classified = currentUser
                cv.setProperties(data.curVariant)
                cv.save(flush: true)

// TOdo: Improve JIRA notifier
                if( loc.getJiraUsername() ) {
                    // Create notifier
                    try {
                        jiraService.notify(cv, prevAcmgClass, currentUser, 'ACMG', cv.classOverrideReason ?: 'Calculated from evidence', env)
                    } catch (Exception e) {
                        log.error("PathOS jiraService not working")
                        log.error( e )
                    }
                }

                // Audit Evidence change
                //
                log.info("ACMG classification updated: ${cv.pmClass}")
                AuditService.audit([
                    category    : 'curation',
                    variant     : cv.toString(),
                    task        : 'classification',
                    description : "Set ACMG classification for ${cv.toString()} to ${cv.pmClass} from ${prevAcmgClass} because ${cv.classOverrideReason ?: 'Calculated from evidence'}"
                ])
            }

            // If amp evidence class has changed
            if ( prevAmpClass != cv.ampClass )
            {
                data.curVariant.classified = currentUser
                cv.setProperties(data.curVariant)
                cv.save(flush: true)

                if( loc.getJiraUsername() ) {
                    // Create notifier
                    try {
                        jiraService.notify(cv, prevAmpClass, currentUser, 'AMP', cv.ampReason ?: 'Calculated from evidence', env)
                    } catch (Exception e) {
                        log.error("PathOS jiraService not working")
                        log.error( e )
                    }
                }

                // Audit Evidence change
                //
                log.info("AMP classification updated: ${cv.ampClass}")
                AuditService.audit([
                        category    : 'curation',
                        variant     : cv.toString(),
                        task        : 'classification',
                        description : "Set AMP classification for ${cv.toString()} to ${cv.ampClass} from ${prevAmpClass} because ${cv.ampReason ?: 'Calculated from evidence'}"
                ])
            }

            // If overall evidence class has changed
            if ( prevOverallClass != cv.overallClass )
            {
                data.curVariant.classified = currentUser
                cv.setProperties(data.curVariant)
                cv.save(flush: true)

                if( loc.getJiraUsername() ) {
                    // Create notifier
                    try {
                        jiraService.notify(cv, prevOverallClass, currentUser, 'Clinical Significance', cv.overallReason ?: 'Calculated from evidence', env)
                    } catch (Exception e) {
                        log.error("PathOS jiraService not working")
                        log.error( e )
                    }
                }

                // Audit Evidence change
                //
                log.info("Clinical Significance updated: ${cv.overallClass}")
                AuditService.audit([
                        category    : 'curation',
                        variant     : cv.toString(),
                        task        : 'classification',
                        description : "Set Clinical Significance for ${cv.toString()} to ${cv.overallClass} from ${prevOverallClass} because ${cv.overallReason}"
                ])
            }

            //  return to show page
            //
            HashMap result = [
                    success: "CurVariant '${cv}' updated!"
            ]
            render(result as JSON)
        } catch (e) {
            println "ERROR!!!"
//            println e.stackTrace
            println e

            render e as JSON
        }
    }

    def citations ( Long id ) {
        render PubmedService.citeCurVariant(CurVariant.get(id)) as JSON
    }


    /**
     * Delete a variant
     *
     * @param id    CurVariant object id
     * @return
     */
    def delete(Long id)
    {

        CurVariant variantInstance = CurVariant.get(id)
        if (!variantInstance)
        {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'variant.label', default: 'CurVariant'), id])
            redirect(action: "list")
            return
        }

        try
        {
            String variant = variantInstance.grpVariant
            ArrayList<String> contexts = []

            // If this variant is generic, delete the other contexts first.
            if(variantInstance.clinContext.varIsGeneric()) {
                variantInstance.allCurVariants().each { cv ->
                    contexts.push(cv.clinContext.code)

                    if(cv != variantInstance) {
                        CurateService.deleteCurVariant(cv)
                    }
                }
            }

            CurateService.deleteCurVariant(variantInstance)

            flash.message = "Deleted CurVariant ${variant} in contexts: ${contexts.join(", ")}"

            //  Back to list
            //
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e)
        {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'variant.label', default: 'CurVariant'), id])
            redirect(action: "show", id: id)
        }

    }


    /**
     * Table definition for Easygrid
     */
    def variantGrid =
        {
            dataSourceType  'gorm'
            domainClass     CurVariant
            enableFilter    true
            editable        false
            inlineEdit      false       // prevent automatic editing without action buttons being pressed

            //  Grid jqgrid defaults
            //
            jqgrid
                    {
                        height        = '100%'
                        width         = '100%'
                        rowNum        = 20
                        rowList       = [20, 50, 100]
                        sortable      = true
                        filterToolbar = [ searchOperators: false ]
                    }

            columns
                    {
                        id          { type 'id'; jqgrid { hidden = true } }
                        pmClass
                                {
                                    name            'pmClass'
                                    sortable        true
                                    value { CurVariant cv -> cv?.pmClass ?: '' }
                                    filterClosure
                                            {
                                                Filter filter ->  applyFilter(delegate, filter.operator, 'pmClass', filter.value )
                                            }
                                }
                        clinContext {
                                    name    'clinContext'
                                    sortable        true
                                    value { CurVariant cv -> cv?.clinContext ?: 'None' }
                                    filterClosure
                                    { Filter filter ->
                                        clinContext {
                                            applyFilter(delegate, filter.operator, 'code', filter.value )
                                        }
                                    }
                        }
                        variant
                                {
                                    value   { CurVariant cv -> return cv.variant }
                                    jqgrid  {
                                        formatter "showlink"
                                        formatoptions
                                                {
                                                    baseLinkUrl "linkToCurVar"
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
                        gene
                        gene_type
                        gene_pathway
                        gene_process
                        consequence
                        exon
                        //pmClass

                        classified
                                {
                                    value { ss -> ss?.classified?.displayName }
                                    filterClosure
                                            {
                                                filter ->
                                                    classified { ilike('displayName', "%${filter.paramValue}%") }
                                            }
                                    enableFilter true
                                }
                        authorised
                                {
                                    value { ss -> ss?.authorised?.displayName }
                                    filterClosure
                                            {
                                                filter ->
                                                    authorised { ilike('displayName', "%${filter.paramValue}%") }
                                            }
                                    enableFilter true
                                }
                        authorisedFlag
                        dbsnp           { value { v -> v.dbsnp  ? 'rs'   + v.dbsnp  : '' }; jqgrid { width "70"; formatter "showlink"; formatoptions { baseLinkUrl  'dbsnpAction'; target  '_blank'}}}
                        cosmic          { value { v -> v.cosmic ? 'COSM' + v.cosmic : '' }; jqgrid { width "75"; formatter "showlink"; formatoptions { baseLinkUrl 'cosmicAction'; target  '_blank'}}}
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
            CurVariant v = CurVariant.get( params.id )
            def url = UrlLink.dbsnp( v.dbsnp )

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
            CurVariant v = CurVariant.get( params.id )
            def url = UrlLink.cosmic( v.cosmic )

            redirect( url: url )
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
            def cv = CurVariant.get(params.id)


            //redirect( action: "svlist", id: sv.seqSample.id )
            redirect( action: "show", params: [hgvsg: cv.hgvsg] )
        } else {

        }
    }

    def linkToCurVar()
    {
        if ( params.id )
        {
            //  need to get seqrun and seqsample to build URL form it
            //
            def cv = CurVariant.get(params.id)

            //  redirect( action: "list", id: curVariant.id )
            //
            redirect( action: "show", controller:"curVariant", params: [id: cv.id] )
        }
    }

    /**
     * Fetch all curVariant data (including ACMG, AMP and Legacy evidence) that matches a hgvsg
     * from all clinical contexts
     */

    def allCurVariantsFor(String hgvsg) {
        HashMap results = [
            curVariants: []
        ]

        ArrayList<CurVariant> cvs = CurVariant.findAllByHgvsg( hgvsg )

        if(cvs && !cvs.isEmpty()) {
            results.gene = cvs[0].gene;

            cvs.each { cv ->
                results.curVariants.push([
                    id: cv.id,
                    clinContextId: cv?.clinContext?.id,
                    context: cv.clinContext.description,
                    contextCode: cv.clinContext.code,
                    reportDesc: cv.reportDesc,
                    pmClass: cv.pmClass,
                    acmgEvidence: cv.fetchAcmgEvidence(),
                    ampEvidence: cv.fetchAmpEvidence(),
                    legacyEvidence: cv.evidence  // careful, this can be null!
                ])
            }
        }

        render results as JSON
    }

    def lookUpCV ( Long id, Long svid ) {
        CurVariant originalCV = CurVariant.get(id)
        SeqVariant sv = null

        try {
            sv = originalCV.originating
        } catch(e) {
            log.error("CV's originating SV was deleted")
            log.error(e)
        }

        if ( svid) sv = SeqVariant.get( svid )

        HashMap context = [:]

        ArrayList<CurVariant> allCV = originalCV?.allCurVariants()

        allCV.each { cv ->
            context[cv.clinContext?.id] = cv.clinContext?.toString()
        }

        CurVariant generic = sv.genericCurVariant()

        HashMap m =
            [
                sv: sv,
                generic: generic,
                currentCV: sv.currentCurVariant(),
                allCV: allCV,
                lookup: [
                    context : context
                ]
            ]

        render m as JSON
    }

    def review ( Long max, Long offset ) {
        max = max ?: 100
        offset = offset ?: 0
        ArrayList curVariants = CurVariant.listOrderByDateCreated([ order: "desc", max: max, offset: offset ])

        Integer total = CurVariant.count()

        [curVariants: curVariants, offset: offset, max: max, total: total]
    }

    def toggleReviewed ( Long id ) {
        Tag reviewed = Tag.findByLabel("Reviewed")
        if (!reviewed) {
            reviewed = new Tag([label: "Reviewed", createdBy: AuthUser.get(1)])
        }
        String message = "Nothing happened"

        CurVariant cv = CurVariant.get(id)

        if(cv.tags.contains(reviewed)) {
            cv.removeFromTags(reviewed)
            message = "Tag removed"
        } else {
            cv.addToTags(reviewed)
            message = "Tag added"
        }

        render message
    }



    def calculateAcmg() {

        def json = request.JSON
        HashMap data = [
            acmgEvidence : [:]
        ]

        data.each { entity ->
            if (entity.key) {
                json[entity.key].each { pair ->
                    data[entity.key][pair.name] = pair.value
                }
            }
        }

        Map result = [:]

        Map blob = EvidenceService.mergeBlob(data.acmgEvidence, defaultCriteria)
        result.classification = EvidenceService.calculateACMG(blob)

        render result as JSON
    }


    def calculateAmp() {

        def json = request.JSON
        HashMap data = [
            ampEvidence : [:]
        ]

        data.each { entity ->
            if (entity.key) {
                json[entity.key].each { pair ->
                    data[entity.key][pair.name] = pair.value
                }
            }
        }

        Map result = [:]

        result.classification = EvidenceService.calculateAMP(data.ampEvidence)

        render result as JSON
    }

}













































