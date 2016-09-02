/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

class AdminController
{
    //  PathOS variant filtering services
    //
    def varFilterService

    //  PathOS evidence classification services
    //
    def evidenceService

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
}
