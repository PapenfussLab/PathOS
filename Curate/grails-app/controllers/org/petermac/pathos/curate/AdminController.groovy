/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.converters.JSON
import org.petermac.util.Locator

class AdminController
{
    def loc = Locator.instance                      // file locator

    def AuditService
    def springSecurityService


    def migrateOverall() {
        List<CurVariant> curVariants = CurVariant.list()

        int counter = 0

        curVariants.each { cv ->
            if (cv.overallClass == null) {
                cv.overallClass = "Unclassified"
                cv.save()
                counter++
            }
        }

        render counter
    }

    def migrateAMP() {
        List<CurVariant> curVariants = CurVariant.list()

        int counter = 0

        curVariants.each { cv ->
            if (cv.fetchAmpEvidence() == null) {
                AmpEvidence ampEvidence = new AmpEvidence( curVariant: cv )
                cv.ampClass = "Unclassified"
                ampEvidence.save()
                counter++
            }
        }

        render counter
    }

    def migrateACMG() {
        List<CurVariant> curVariants = CurVariant.list()

        int counter = 0

        curVariants.each { cv ->

            if (cv.fetchAcmgEvidence() == null) {
                EvidenceService.migrateACMG( cv )
                counter++
            }
        }

//        Saving is done by the service
//        CurVariant.saveAll(curVariants)

        render counter
    }


    def fetchCurVariants() {

//        Long[] curVariants = new JsonSlurper().parseText(request.JSON.curVariants)

        Long[] curVariants = request.JSON.curVariants


        Map results = [
            curVariants: curVariants.collect { CurVariant.get(it) },
            numberOfTimesReported: curVariants.collect { it ->
                CurVariant cv = CurVariant.get(it)
                [
                    id: cv.id,
                    count: CurVariantReport.countByCurVariant(cv)
                ]
            }
        ]

        render results as JSON
    }



    def reportUpload() {
        HashMap map = [
            tests: LabAssay.getAll().collect { it.testSet } as Set<String>,
            outcomes: ["var", "neg", "fail"]
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
                AuditService.audit([
                    category    : 'report',
                    task        : 'template upload',
                    description : "New Report Template docx uploaded: ${filename}"
                ])

                render "success"
            } catch (all) {
                render "no transfer"
            }
        } else {
            render "fail"
        }
    }
}
