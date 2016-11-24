package org.petermac.pathos.curate

import grails.util.GrailsUtil
import org.petermac.util.JiraNotifier
import org.petermac.util.Locator

import java.text.MessageFormat

class VarLinkService {
    static def loc = Locator.instance   // file locator
    def SpringSecurityService

    /**
     * get all cur variants linked to a seq variant
     * @param sv
     * @return
     */
    ArrayList getCurVariantsForSeqVariant(SeqVariant sv) {
        def curList = []
        VarLink.findAllBySeqVariant(sv).each {
            curList.add(it.curVariant)
        }
        return curList
    }

    /**
     * Get the CurVariant for the context of this SV
     * return null if none
     *
     * @param SeqVariant sv
     * @return CurVariant
     */
    CurVariant getCurrentCV( SeqVariant sv ) {
        CurVariant result = null;
        ClinContext cc = sv.seqSample.clinContext;

        sv.varLinks.each { link ->
            if(link?.curVariant?.clinContext?.code == cc?.code) {
                result = link.curVariant;
            }
        }

        return result;
    }


    /**
     * Get the CurVariant matching a seqvariant and a context
     * @param SeqVariant sv
     * @return CurVariant
     */
    CurVariant getCurVariantMatchingContext( SeqVariant sv, ClinContext cc ) {
        CurVariant result = null;

        sv.varLinks.each { link ->
            if(link.curVariant.clinContext == cc) {
                result = link.curVariant;
            }
        }

        return result;
    }



    /**
     * Get the CurVariants which are not generic or current context
     *
     * @param sv
     * @return
     */
    ArrayList<CurVariant> getOtherCurVariantsForSeqVariant(SeqVariant sv) {
        ArrayList<CurVariant> curList = [];
        ClinContext cc = sv.seqSample.clinContext;

        sv.varLinks.each { link ->
            if(link.curVariant.clinContext != null && link.curVariant.clinContext != cc)
            {
                curList.add( link.curVariant );
            }
        }

        return curList
    }

    /**
     * get all seq variants linked to a cur variant
     * @param cv
     * @return
     */
    ArrayList getSeqVariantsForCurVariant(CurVariant cv) {
        def seqList = []
        VarLink.findAllByCurVariant(cv).each {
            seqList.add(it.seqVariant)
        }
        return seqList
    }

    /**
     * get the preferred cur variant for a seq variant
     * @param sv
     * @return null if none set, otherwsie the originating CurVariant
     */
    CurVariant getPreferredCurVariantForSeqVariant(SeqVariant sv) {
        def result = null
        VarLink.findAllBySeqVariant(sv).each {
            if (it.preferred) {
                result = it.curVariant
            }
        }
        return result
    }

    /**
     * get the generic cur variant for a seq variant
     * @param sv
     * @return null if none set, otherwsie the originating CurVariant
     */
    CurVariant getGenericCurVariantForSeqVariant(SeqVariant sv) {
        def result = null
        VarLink.findAllBySeqVariant(sv).each {
            if (it.curVariant.clinContext == null) {
                result = it.curVariant
            }
        }
        return result
    }

    /**
     * get the SeqVariant that's originating for a CurVariant
     * @param cv
     * @return null if none set, otherwsie the originating SeqVariant
     */
    SeqVariant getOriginatingSeqVariantForCurVariant(CurVariant cv) {

        VarLink.findAllByCurVariant(cv).each {
            if (it.originating) {
                return it.seqVariant
            }
        }
        return null
    }

    /**
     * Set one given curvar to preferred for a seqvar (that is, set their varlink to preferred true)
     * true if it worked, false if no change
     * @param sv
     * @param cv
     * @return
     */
    boolean setPreferredCurVariantForSeqVariant(CurVariant cv, SeqVariant sv) {

        VarLink.findAllBySeqVariant(sv).each {
            if (it.preferred) {
               it.preferred = false
               it.save()
            }

            if (it == cv) {
                it.preferred = true
                it.save()
                return true
            }
        }

        return false

    }

    /**
     * DKGM 18-November-2016:
     * This is a function written by AES, date unknown
     * It is called by createNewCurVariantFromSeqVariant
     *
     * @param sv
     * @param cc
     * @return
     */
    CurVariant makeCurVar(SeqVariant sv, ClinContext cc = null) {
        println 'making new CurVariant!!!!!!'

        //  create a curvariant with  explicit sv parameters
        CurVariant var = new CurVariant (variant: sv.variant, clinContext: cc, hgvsc: sv.hgvsc, hgvsp: sv.hgvsp, gene:sv.gene, hgvsg: sv.hgvsg, consequence: sv. consequence, siftCat: sv.siftCat, chr: sv.chr, pos: sv.pos, exon: sv.exon, ens_variant: sv.ens_variant, cosmic: sv.cosmic, dbsnp: sv.dbsnp, polyphenCat: sv.polyphenCat, alamutClass: sv.alamutClass)


        //  Set up Evidence embedded class
        //  AES: is this needed? copied from CurateService.
        def evd = new Evidence()
        evd.evidenceClass  = "Unclassified"
        evd.save()
        var.evidence       = evd
        var.authorisedFlag = false

        //  Set up GrpVarint embedded class
        def gpv = new GrpVariant(accession:sv.hgvsg, muttyp:'SNV')
        var.setGrpVariant(gpv)

        var.save(flush: true)

        return var
    }

    CurVariant fetchCurVariant( String hgvsg, ClinContext cc ) {
        //        DKGM 18-November-2016
        //        This is what it should be:
        //  return CurVariant.findByClinContextAndGrpVariant(cc, grpVariant);
        //        But since we're in transition, this is what we have that works:

        ArrayList<CurVariant> list = CurVariant.findAllByClinContext(cc);
        CurVariant result = null;
        list.each {
            if(it.hgvsg == hgvsg) {
                result = it;
            }
        }
        return result;
    }

    /**
     * DKGM 21-November-2016
     *
     * This function is for Role:Lab people
     * They select SeqVariants, and click the curate button to mark that SV.
     *
     * The expected behaviour is:
     * In the current context, link the SV to a CV.
     *
     * If there is already a linked CV, do nothing.
     * If there is an unlinked CV, link it.
     * If there is no CV in the null context, create it.
     * If there is no CV in this context, create it.
     *
     * Note: If a new CV is created, a Molpath Operations JIRA issue should be created.
     *
     * @param sv
     */
    boolean markSeqVariantAsCurated ( SeqVariant sv ) {
        ClinContext cc = sv?.seqSample?.clinContext; // cc means "Current Context"

        if (!sv.curatedInContext(null)) // sv does not have linked generic
        {
            CurVariant generic = fetchCurVariant ( sv.hgvsg, null );
            boolean newGeneric = false;
            if(!generic) {
                generic = makeCurVar ( sv, null );
            }
            def genericVarLink = new VarLink(curVariant:generic, seqVariant:sv, originating:newGeneric, preferred:false);
            genericVarLink.save( flush:true );
        }

        // Only do this stuff if cc is not null.
        // AND the sv is not curated in context.
        if(cc != null && !sv.curatedInContext(cc))
        {
            CurVariant inContext = fetchCurVariant( sv.hgvsg, cc );
            boolean newInContext = false;

            if(!inContext) {
                inContext = makeCurVar( sv, cc );
                newInContext = true;
            }

            def inContextVarLink = new VarLink(curVariant:inContext, seqVariant:sv, originating:newInContext, preferred:false)
            inContextVarLink.save(flush: true);
        }

        return sv.curatedInContext(cc);
    }

    /**
     * AES: this replaces CurateService's createVariant
     * Create a new CurVariant object from a given originating SeqVariant object
     * An optional ClinContext is applied to the CurVariant.
     *
     * @param   sv  SeqVariant object originating the CurVariant
     * @param   mc  Clinical disease context for the SeqVariant
     * @return      true if created OK
     */
    boolean createNewCurVariantFromSeqVariant( SeqVariant sv, ClinContext cc = null, boolean defaultPreferred = false )
    {

        //  return if a VarLink already exists from an cv with this ClinContext
        //
        def theseVarLinks = VarLink.findAllBySeqVariant(sv)
        for (vl in theseVarLinks) {
            if(vl.curVariant.clinContext == cc) {
                println "Refusing to create a new curvar: already have a CC " + cc + " curvariant for this seqvariant"
                log.warn("Already have a CC " + cc + " curvariant for this seqvariant")
                return false
            }
        }

        log.info( "Creating new CurVariant from ${sv}")
        println ( "Creating new CurVariant ")
        println sv

        sv.properties.tags = null   //no tags!
        sv.properties.clinContext = cc

        //	Create CurVariant as domain class - copy all SeqVariant properties across
        //
        //def var    = new CurVariant( sv.properties )

        def var = makeCurVar(sv,cc)


        //  Make a varlink
        //
        def varLink = new VarLink(curVariant:var,seqVariant:sv,originating:false,preferred:defaultPreferred)

//        varLink.save(failOnError:true)
//          Save new CurVariant
        //

        if ( ! var.save())
        {
            println "Failed To Save"
            println var?.errors?.allErrors
            var?.errors?.allErrors?.each { log.error(new MessageFormat(it?.defaultMessage)?.format(it?.arguments)) }

            //  Discard transient object
            //
            var.discard()
            return false
        }




        //  make a JIRA issue: notify MP_OPS that variant needs curation
        //  AS: disable this until we get ready for UAT
        if (false)
        {    //we could disable it in Demo env. or just fix the proxy. leaving this block on for now.

            def jnotifier = new JiraNotifier()
            def currentUser = springSecurityService.currentUser as AuthUser

            def valout = "Triggered by ${currentUser.getDisplayName()} (${currentUser.getUsername()}) ${currentUser.getEmail()} " + "\n" + "\n"

            valout = valout + "New CurVariant ${var} (HGVSP ${var.hgvsp}) has been created from SeqVariant ${sv} and needs curation. Curate here: "

            //  Todo: this needs to use grails methods to generate a link
            //  e.g.
            //  LinkGenerator grailsLinkGenerator

            def link = grailsLinkGenerator.link(controller: 'curVariant', action: 'show', id: var.id, absolute: true)

            valout = valout + " ${link}"

            def issueSummary = "New CurVariant needs curation: ${var}"

            if ( GrailsUtil.environment != "pa_prod" )
            {
                valout = "TEST! NOT A REAL ISSUE! ${valout}"
                issueSummary = "TEST! ${issueSummary}"
            }

            def response = jnotifier.createJiraIssue(issueSummary, "${valout.replaceAll(':p','\\:p')}", "Task","molpath")

            if (response)
            {
                if (response.containsKey('errors')) {
                    println "Error creating issue! Response:"
                    println response
                }

                if (response.containsKey('id') && response.containsKey('key'))
                {
                    println "Issue created. Issue ${response['id']} ${response['key']} "

                    //  assign it to pathos ops now
                    //
                    int newIssueId = response['id'] as int
                    jnotifier.assignJiraIssue('molpath.ops', newIssueId)    //todo assign to someone who needs it
                    jnotifier.addWatcherToJiraIssue('molpath.ops', newIssueId)
                    def jiraIssue = new JiraIssue(triggered_by: currentUser, issueType: 'new_variant', curVariant: var, issueIdentifier: response['key']).save(flush: true, failOnError:true)
                }
            }

        }
        return true
    }




}
