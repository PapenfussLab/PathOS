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
import java.text.MessageFormat

import static org.grails.plugin.easygrid.GormUtils.applyFilter

@Easygrid
class CurVariantController
{
    static scaffold = CurVariant
    def SpringSecurityService
    def VarLinkService

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

        Integer numberOfVarLinks = variantInstance?.varLinks.size()

        [ variantInstance:variantInstance, jiraIssues:jiraIssues, jiraAddress:jiraAddress, numberOfVarLinks:numberOfVarLinks, originating:variantInstance?.originatingSeqVariant() ]
    }
    def linkedSeqVars(Long id)
    {
        CurVariant variantInstance

        if (id) {    //in case this is called directly
            variantInstance = CurVariant.get(id)
        }
        ArrayList<SeqVariant> linkedSeqVars = variantInstance?.linkedSeqVariants()

        ArrayList<HashMap> results = linkedSeqVars.collect {
            [
                id: it?.id,
                seqSample: it?.seqSample?.id,
                string: it?.seqSample?.toString() +":"+ it?.toString()
            ]
        }

        render results as JSON
    }

    def edit(Long id)
    {
        def variantInstance = CurVariant.get(id)
        if (!variantInstance)
        {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'variant.label', default: 'CurVariant'), id])
            redirect(action: "list")
            return
        }



        [ variantInstance: variantInstance ]
    }




    /**
     * Update a variant after curation
     *
     * This routine checks for a change in authorisation (need to be an administrator)
     * Also sets the classification of the variant
     * If anything changed, log an audit record
     *
     * @param id        Id for CurVariant
     * @param version   Vesrion no for CurVariant
     * @return
     */
    def update(Long id, Long version)
    {
        def currentUser = springSecurityService.currentUser as AuthUser
        def variantInstance = CurVariant.get(id) //todo not variant, but groupVariant variant
        if (!variantInstance)
        {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'variant.label', default: 'CurVariant'), id])
            redirect(action: "list")
            return
        }

        if (version != null)
        {
            if (variantInstance.version > version)
            {
                variantInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'variant.label', default: 'CurVariant')] as Object[],
                          "Another user has updated this CurVariant while you were editing")
                render(view: "edit", model: [variantInstance: variantInstance])
                return
            }
        }

        //  Check for change of Authorsation flag
        //

        //  NOTE: we need to cast params.authorisedQcFlag because it is set to "on" not "true"
        //  This is a very nasty issue - must also cater for a null flag value
        //
        Boolean usrAuth = params.authorisedFlag ?: false
        boolean setAuth = false
        def audit_msg   = ""
        if ( variantInstance.authorisedFlag != usrAuth )
        {
            //  Check we have an administrator

            if (!(currentUser.authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_DEV" }))
            {
                flash.message = "You must be an Administrator to authorise !"
                render(view: "edit", model: [variantInstance: variantInstance])
                return
            }

            //  Set (or reset) authorised OldUser
            //
            params.authorised = ( params.authorisedFlag ? currentUser : null )
            setAuth = true
            audit_msg = "Changed authorisation to ${usrAuth} by ${currentUser} "
            log.info( audit_msg )
        }

        //  Create audit message
        //
        audit_msg = "Set authorisation to ${usrAuth} for ${variantInstance.grpVariant.accession} to ${variantInstance.pmClass}"

        def audit = new Audit(  category:    'curation',
                                variant:     ${variantInstance.grpVariant.accession} ,
                                complete:    new Date(),
                                elapsed:     0,
                                software:    'Path-OS',
                                swVersion:   meta(name: 'app.version'),
                                task:        'curation authorisation',
                                username:    currentUser.getUsername(),
                                description: audit_msg )

        //  Set properities
        //
        variantInstance.properties = params

        if ( ! variantInstance.save(flush: true))
        {
            variantInstance?.errors?.allErrors?.each
            {
                log.error( new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
            }
            log.error( "Failed to save CurVariant: ${audit_msg}")

            render(view: "edit", model: [variantInstance: variantInstance])
            return
        }

        //  Save audit record of update
        //
        if ( ! audit.save( flush: true ))
        {
            audit?.errors?.allErrors?.each
            {
                log.error( new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
            }
            log.error( "Failed to log audit message: ${audit_msg}")
        }

        //  return to show page
        //
        flash.message = message(code: 'default.updated.message', args: [message(code: 'variant.label', default: 'CurVariant'), variantInstance.id])
        redirect(action: "show", id: variantInstance.id)
    }

    /**
     * Delete a variant and all SeqVariants linked to it
     *
     * @param id    CurVariant object id
     * @return
     */
    def delete(Long id)
    {

        def currentUser = springSecurityService.currentUser as AuthUser
        def variantInstance = CurVariant.get(id)
        if (!variantInstance)
        {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'variant.label', default: 'CurVariant'), id])
            redirect(action: "list")
            return
        }

        try
        {

            //  Remove all SeqVariant links to the CurVariant record
            //


            def seqvars = variantInstance.linkedSeqVariants()
            for ( SeqVariant seqvar in seqvars ) {                        // Note: cant use var.seqVariants in loop
                //variantInstance.removeFromSeqVariants(seqvar)
                //delete var link
                def varlinks = VarLink.findAllBySeqVariantAndCurVariant(seqvar,variantInstance)
                for (vl in varlinks)
                {
                    vl.delete()
                }
            }

            //   Remove all JiraIssue linksto the CurVariant record
            //
            def variantIssues = JiraIssue.findAllByCurVariant(variantInstance)
            for ( varIssue in variantIssues ) {
                varIssue.setCurVariant(null)
                varIssue.save(flush: true)
            }

            //  Delete the CurVariant record
            //
            variantInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'variant.label', default: 'CurVariant'), id])

            //  Audit message
            //
            //  Create audit message
            //
            def audit_msg = "Deleted CurVariant ${variantInstance.toString()} and ${seqvars.size()} SeqVariants"

            def audit = new Audit(  category:    'curation',
                                    variant:     ${variantInstance.toString()},
                                    complete:    new Date(),
                                    elapsed:     0,
                                    software:    'Path-OS',
                                    swVersion:   meta(name: 'app.version'),
                                    task:        'variant deletion',
                                    username:    currentUser.getUsername(),
                                    description: audit_msg )

            //  Save audit record of deletion
            //
            if ( ! audit.save( flush: true ))
            {
                audit?.errors?.allErrors?.each
                {
                    log.error( new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                }
                log.error( "Failed to log audit message: ${audit_msg}")
            }

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
                                            {
                                                Filter filter ->  applyFilter(delegate, filter.operator, 'clinContext', filter.value )
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


    def lookUpCV ( Long id )
    {
        HashMap m = [:]
        CurVariant cv = CurVariant.get( id )
        if (cv) {
            m.cv = cv.properties

//            def list = VarLinkService.g


        }




        render m
    }

    def getSV ( long id ) {
//        render org.petermac.pathos.curate.VarLinkService.get
    }

    def updateCV () {
        def id = params.id
        def report = params.report
        def evidence = params.evidence

        CurVariant cv = CurVariant.get(id);

        cv.reportDesc = report;
        cv.save();

        cv.evidence.justification = evidence;
        cv.evidence.save();

        render "Updated, please refresh page"
    }

    def newCV () {
        def id = params.id
        def cc = params.cc

        ClinContext context = ClinContext.findByDescription(cc);

        SeqVariant sv = SeqVariant.get(id);

        VarLinkService.createNewCurVarFromSeqVar(sv, context);

        render "Updated, please refresh page"
    }




}













































