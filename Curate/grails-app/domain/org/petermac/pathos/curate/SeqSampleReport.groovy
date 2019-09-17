package org.petermac.pathos.curate


import grails.persistence.Entity

/**
 * Object to record the state of a SeqSample when it is reported
 * DKGM 13-April-2017
 */

@Entity
class SeqSampleReport
{
    SeqSample   seqSample           // Link to the original seqSample
    String      reportFilePath      // Path to generated report (PDF)
    Date        dateCreated         // Date the seqSample was created
    AuthUser    user                // User who generated the report

// Information to be printed to a report
// Editable by an authorised user before the report is published
    String	sample
//    String	patient
//    String	urn
//    String	dob
//    String	age
//    String	sex
//    String	requester
//    String	location
    String	morphology
    String	site
    String	tumour_pct
    String	collect_date
    String	rcvd_date
    String	ampReads
    String	ampPct
    String	lowAmps
    String	rois
    List	curVariantReports
    String	citations
    String	clinicalDetails
    String	resultSummary
    String	recommendations
    String	address
    String	phone
    String	requestAddress
    String	copyTo
    String  specimen
    String  sampleType
    String  histologicalFeatures
    String  uncoveredRegions

    static hasMany = [ curVariantReports: CurVariantReport ]

    static mapping =
        {
            autoTimestamp       true
            rois                (type: 'text')
            citations           (type: 'text')
            clinicalDetails     (type: 'text')
            resultSummary       (type: 'text')
            recommendations     (type: 'text')
            uncoveredRegions    (type: 'text')
        }

    static constraints =
        {
            seqSample               (nullable: false)
            reportFilePath          (nullable: false)
            user                    (nullable: false)
            sample	            	(nullable: true)
//            patient	            	(nullable: true)
//            urn		                (nullable: true)
//            dob		                (nullable: true)
//            age	                  	(nullable: true)
//            sex	                   	(nullable: true)
//            requester		        (nullable: true)
//            location		        (nullable: true)
            morphology		        (nullable: true)
            site		            (nullable: true)
            tumour_pct	        	(nullable: true)
            collect_date	    	(nullable: true)
            rcvd_date	        	(nullable: true)
            ampReads		        (nullable: true)
            ampPct	            	(nullable: true)
            lowAmps	            	(nullable: true)
            rois	            	(nullable: true)
            citations				(nullable: true)
            clinicalDetails			(nullable: true)
            resultSummary			(nullable: true)
            recommendations			(nullable: true)
            address					(nullable: true)
            phone					(nullable: true)
            requestAddress			(nullable: true)
            copyTo					(nullable: true)
            specimen				(nullable: true)
            sampleType				(nullable: true)
            histologicalFeatures	(nullable: true)
            uncoveredRegions		(nullable: true)
        }

    String	toString()
    {
        "${seqSample}"
    }

    // SeqSample details should be consistent with live object
    String isdraft() {
        this.seqSample.finalReviewBy ? 'FINAL' : 'DRAFT'
    }
    String clinContext() {
        this.seqSample.clinContext?.toString()
    }
    String firstReviewer() {
        this.seqSample.firstReviewBy?.displayName
    }
    String firstReviewedDate() {
        this.seqSample.firstReviewedDate?.format("d-MMM-yyyy h:mm a")
    }
    String secondReviewer() {
        this.seqSample.secondReviewBy?.displayName
    }
    String secondReviewedDate() {
        this.seqSample.secondReviewedDate?.format("d-MMM-yyyy h:mm a")
    }
    String finalReviewer() {
        this.seqSample.finalReviewBy?.displayName
    }
    String finalReviewedDate() {
        seqSample.finalReviewedDate?.format("d-MMM-yyyy h:mm a")
    }


    // PatSample details should be consistent with live object
    String requester() {
        seqSample.patSample?.requester
    }
    String extref() {
        seqSample.patSample?.extSample
    }
    String location() {
        seqSample.patSample?.pathlab
    }
//    String morphology() {
//        seqSample.patSample?.repMorphology
//    }
//    String site() {
//        seqSample.patSample?.retSite
//    }
//    String tumour_pct() {
//        seqSample.patSample?.tumourPct
//    }
//    String collect_date() {
//        seqSample.patSample?.collectDate?.format("d-MMM-yyyy")
//    }
//    String rcvd_date() {
//        seqSample.patSample?.rcvdDate?.format("d-MMM-yyyy")
//    }


    // Patient details should be consistent with live object
    String patient() {
        seqSample.patSample?.patient?.fullName
    }

    String urn() {
        seqSample.patSample?.patient?.urn
    }

    String dob() {
        seqSample.patSample?.patient?.dob?.format("d-MMM-yyyy")
    }

    String age() {
        seqSample.patSample?.patient?.age
    }

    String sex() {
        seqSample.patSample?.patient?.sex
    }




}

























