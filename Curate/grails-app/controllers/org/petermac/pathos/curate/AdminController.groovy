/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import org.petermac.util.Locator

class AdminController
{
    def loc = Locator.instance                      // file locator

    //  PathOS variant filtering services
    //
    def varFilterService

    //  PathOS evidence classification services
    //
    def evidenceService

    def springSecurityService

    def admin()
    {
    }

    /**
     * Set the filtering flags action
     *
     * @return
     */
    def filter()
    {
        //  Apply all filter rules to all Variants
        //
        def mod = varFilterService.applyFilter( session, true )

        flash.message = "Filter applied to ${mod} variants"

        redirect( controller: "admin", action: "admin" )
    }

    def reclassify()
    {
        int mod = 0

        //  reclassify all curated Variants
        //
        for ( var in CurVariant.findAllByClassifiedIsNotNull())
        {
            def reclass = evidenceService.inferClass( var.evidence )
            if ( var.evidence.evidenceClass != reclass )
            {
                log.info( "Modifying ${var} from ${var.evidence.evidenceClass} to ${reclass}" )
                var.evidence.evidenceClass = reclass
                if ( var.authorisedFlag ) var.pmClass = reclass
                if ( ! var.save())
                {
                    log.error( "Failed to modify classification for ${var}")
                }
                mod++
            }
        }

        flash.message = "Reclassified ${mod} variants"

        redirect( controller: "admin", action: "admin" )
    }

    def reportUpload() {
        HashMap map = [
            seqruns: Seqrun.list().take(20).collect { it.seqrun } as Set<String>,
            panels: Panel.getAll().collect { it.panelGroup } as Set<String>,
            tests: PatAssay.getAll().collect {
                if(it.testName =~ /^(FLD_REP.*|FAM1.*)$/) {
                    return it.testName
                }
            } as Set,
            outcomes: ["Var", "Neg", "Fail"]
        ]

        return map
    }

    def reset_template(){
        try {
            new File(loc.repDir + "Test Template.docx").bytes = new File(loc.repDir + "Original Test Template.docx").bytes;
            render "Test Template Reset"
        } catch(e){
            render e
        }
    }

    def upload_report() {
        def file = params.docx
        String filename = params.filename?.trim() ?: "temp"

        // Should we validate this..?
        if(filename =~ /^.*$/) {
            try {
                if(!(filename =~ /^.*\.docx$/)) filename += ".docx"
                file.transferTo(new File(loc.repDir + filename))

                //  Create audit message
                //
                def audit_msg = "New Report Template docx uploaded: ${filename}"
                def audit     = new Audit(
                        category:    'report',
                        task:        'template upload',
                        complete:    new Date(),
                        elapsed:     0,
                        software:    'PathOS',
                        swVersion:   meta(name:'app.version') as String ?: "",
                        username:    (springSecurityService.currentUser as AuthUser).username ?: "",
                        description: audit_msg
                )

                audit.save( flush: true )

                render "success"
            } catch (all) {
                render "no transfer"
            }
        } else {
            render "fail"
        }
    }
}
