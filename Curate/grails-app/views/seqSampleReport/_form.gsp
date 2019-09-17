<%@ page import="org.petermac.pathos.curate.SeqSampleReport" %>



<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'seqSample', 'error')} required">
	<label for="seqSample">
		<g:message code="seqSampleReport.seqSample.label" default="Seq Sample" />
		<span class="required-indicator">*</span>
	</label>

	<span>${seqSampleReportInstance?.seqSample?.sampleName}</span>

</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'reportFilePath', 'error')} ">
	<label for="reportFilePath">
		<g:message code="seqSampleReport.reportFilePath.label" default="Report File Path" />
		
	</label>
	<span>${seqSampleReportInstance?.reportFilePath}</span>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'user', 'error')} required">
	<label for="user">
		<g:message code="seqSampleReport.user.label" default="User" />
		<span class="required-indicator">*</span>
	</label>
	<span>${seqSampleReportInstance?.user?.displayName}</span>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'sample', 'error')} ">
	<label for="sample">
		<g:message code="seqSampleReport.sample.label" default="Sample" />
		
	</label>
	<g:textField readonly="1" name="sample" value="${seqSampleReportInstance?.sample}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'patient', 'error')} ">
	<label for="patient">
		<g:message code="seqSampleReport.patient.label" default="Patient" />
		
	</label>
	<g:textField readonly="1" name="patient" value="${seqSampleReportInstance?.patient}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'urn', 'error')} ">
	<label for="urn">
		<g:message code="seqSampleReport.urn.label" default="Urn" />
		
	</label>
	<g:textField readonly="1" name="urn" value="${seqSampleReportInstance?.urn}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'dob', 'error')} ">
	<label for="dob">
		<g:message code="seqSampleReport.dob.label" default="Dob" />
		
	</label>
	<g:textField readonly="1" name="dob" value="${seqSampleReportInstance?.dob}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'age', 'error')} ">
	<label for="age">
		<g:message code="seqSampleReport.age.label" default="Age" />
		
	</label>
	<g:textField readonly="1" name="age" value="${seqSampleReportInstance?.age}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'sex', 'error')} ">
	<label for="sex">
		<g:message code="seqSampleReport.sex.label" default="Sex" />
		
	</label>
	<g:textField readonly="1" name="sex" value="${seqSampleReportInstance?.sex}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'requester', 'error')} ">
	<label for="requester">
		<g:message code="seqSampleReport.requester.label" default="Requester" />
		
	</label>
	<g:textField readonly="1" name="requester" value="${seqSampleReportInstance?.requester}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'location', 'error')} ">
	<label for="location">
		<g:message code="seqSampleReport.location.label" default="Location" />
		
	</label>
	<g:textField name="location" value="${seqSampleReportInstance?.location}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'morphology', 'error')} ">
	<label for="morphology">
		<g:message code="seqSampleReport.morphology.label" default="Morphology" />
		
	</label>
	<g:textField name="morphology" value="${seqSampleReportInstance?.morphology}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'extref', 'error')} ">
	<label for="extref">
		<g:message code="seqSampleReport.extref.label" default="Extref" />
		
	</label>
	<g:textField name="extref" value="${seqSampleReportInstance?.extref}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'site', 'error')} ">
	<label for="site">
		<g:message code="seqSampleReport.site.label" default="Site" />
		
	</label>
	<g:textField name="site" value="${seqSampleReportInstance?.site}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'tumour_pct', 'error')} ">
	<label for="tumour_pct">
		<g:message code="seqSampleReport.tumour_pct.label" default="Tumourpct" />
		
	</label>
	<g:textField name="tumour_pct" value="${seqSampleReportInstance?.tumour_pct}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'collect_date', 'error')} ">
	<label for="collect_date">
		<g:message code="seqSampleReport.collect_date.label" default="Collectdate" />
		
	</label>
	<g:textField readonly="1" name="collect_date" value="${seqSampleReportInstance?.collect_date}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'rcvd_date', 'error')} ">
	<label for="rcvd_date">
		<g:message code="seqSampleReport.rcvd_date.label" default="Rcvddate" />
		
	</label>
	<g:textField readonly="1" name="rcvd_date" value="${seqSampleReportInstance?.rcvd_date}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'ampReads', 'error')} ">
	<label for="ampReads">
		<g:message code="seqSampleReport.ampReads.label" default="Amp Reads" />
		
	</label>
	<g:textField readonly="1" name="ampReads" value="${seqSampleReportInstance?.ampReads}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'ampPct', 'error')} ">
	<label for="ampPct">
		<g:message code="seqSampleReport.ampPct.label" default="Amp Pct" />
		
	</label>
	<g:textField readonly="1" name="ampPct" value="${seqSampleReportInstance?.ampPct}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'lowAmps', 'error')} ">
	<label for="lowAmps">
		<g:message code="seqSampleReport.lowAmps.label" default="Low Amps" />
		
	</label>
	<g:textField readonly="1" name="lowAmps" value="${seqSampleReportInstance?.lowAmps}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'rois', 'error')} ">
	<label for="rois">
		<g:message code="seqSampleReport.rois.label" default="Rois" />
		
	</label>
	<g:textField readonly="1" name="rois" value="${seqSampleReportInstance?.rois}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'isdraft', 'error')} ">
	<label for="isdraft">
		<g:message code="seqSampleReport.isdraft.label" default="Isdraft" />
		
	</label>
	<g:textField readonly="1" name="isdraft" value="${seqSampleReportInstance?.isdraft}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'clinContext', 'error')} ">
	<label for="clinContext">
		<g:message code="seqSampleReport.clinContext.label" default="Clin Context" />
		
	</label>
	<g:textField name="clinContext" value="${seqSampleReportInstance?.clinContext}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'firstReviewer', 'error')} ">
	<label for="firstReviewer">
		<g:message code="seqSampleReport.firstReviewer.label" default="First Reviewer" /></label>
	<g:textField readonly="1" name="firstReviewer" value="${seqSampleReportInstance?.firstReviewer}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'firstReviewedDate', 'error')} ">
	<label for="firstReviewedDate">
		<g:message code="seqSampleReport.firstReviewedDate.label" default="First Reviewed Date" />
		
	</label>
	<g:textField readonly="1" name="firstReviewedDate" value="${seqSampleReportInstance?.firstReviewedDate}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'secondReviewer', 'error')} ">
	<label for="secondReviewer">
		<g:message code="seqSampleReport.secondReviewer.label" default="Second Reviewer" />
		
	</label>
	<g:textField readonly="1" name="secondReviewer" value="${seqSampleReportInstance?.secondReviewer}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'secondReviewedDate', 'error')} ">
	<label for="secondReviewedDate">
		<g:message code="seqSampleReport.secondReviewedDate.label" default="Second Reviewed Date" />
		
	</label>
	<g:textField readonly="1" name="secondReviewedDate" value="${seqSampleReportInstance?.secondReviewedDate}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'finalReviewer', 'error')} ">
	<label for="finalReviewer">
		<g:message code="seqSampleReport.finalReviewer.label" default="Final Reviewer" />
		
	</label>
	<g:textField readonly="1" name="finalReviewer" value="${seqSampleReportInstance?.finalReviewer}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'finalReviewedDate', 'error')} ">
	<label for="finalReviewedDate">
		<g:message code="seqSampleReport.finalReviewedDate.label" default="Final Reviewed Date" />
		
	</label>
	<g:textField readonly="1" name="finalReviewedDate" value="${seqSampleReportInstance?.finalReviewedDate}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleReportInstance, field: 'curVariantReports', 'error')} ">
	<label for="curVariantReports">
		<g:message code="seqSampleReport.curVariantReports.label" default="Cur Variant Reports" />
		
	</label>
	

<ul class="one-to-many">
<g:each in="${seqSampleReportInstance?.curVariantReports?}" var="c">
    <div class="outlined-box">
		<h1>${c.hgvsc}</h1>
		<g:render template="curVariantReportForm" model="[curVariantReportInstance: c]"/>
	</div>
</g:each>
</ul>

</div>

















