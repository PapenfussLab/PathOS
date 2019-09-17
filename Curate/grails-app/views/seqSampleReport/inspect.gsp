
<%@ page import="org.petermac.pathos.curate.SeqSampleReport; org.petermac.pathos.curate.SeqVariant; grails.converters.JSON" %>
<!DOCTYPE html>
<html>
<head>
	<meta name="layout" content="main">
	<title></title>
	%{--<parameter name="hotfix" value="off" />--}%
</head>

<body>
	<section id="seqSampleInfo">
		<div class="container outlined-box" style="padding-left: 40px;">
			<h1>SeqSample Info goes here</h1>
			%{--<p>${seqSample as JSON}</p>--}%

			<a href="<g:context/>/seqVariant/buildSeqSampleReport/${seqSample.id}">Draft New Report</a>

			<div class="row">

<p>The sample is: <a href="<g:context/>/seqVariant/svlist/${seqSample.id}">${seqSample.sampleName}</a> <- click to go back to svlist</p>

<p>The seqrun is: <a href="<g:context/>/Seqrun/show?id=${seqSample.seqrun.id}">${seqSample.seqrun.seqrun}</a></p>

<p>The panel group is: ${seqSample.panel.panelGroup}</p>

<p>Quality Control: <g:qualityControl seqSample="${seqSample}"/></p>

<p>There are ${seqSample.seqVariants.size()} variants.</p>
<p>Number marked as reportable: ${SeqVariant.countBySeqSampleAndReportable(seqSample, true)}</p>
<p>The templates are:</p>
<ul>
	<g:if test="${seqSample?.patSample?.patAssays?.find { true }?.testSet}">
	<li><g:link controller="seqSampleReport" action="downloadReportTemplate" params='[filename:"Template_${seqSample?.patSample?.patAssays?.find { true }?.testSet?.trim()}_fail.docx"]' target="_blank">Fail</g:link> (If QC fails, this report template is used)</li>
	<li><g:link controller="seqSampleReport" action="downloadReportTemplate" params='[filename:"Template_${seqSample?.patSample?.patAssays?.find { true }?.testSet?.trim()}_neg.docx"]' target="_blank">Neg</g:link> (If no variants are reportable, this report template is used)</li>
	<li><g:link controller="seqSampleReport" action="downloadReportTemplate" params='[filename:"Template_${seqSample?.patSample?.patAssays?.find { true }?.testSet?.trim()}_var.docx"]' target="_blank">Var</g:link> (If QC passes and there are reportable variants, this report template is used)</li>
	</g:if>
	<g:else>
	<li>N/A - No Test Set found!!!</li>
	</g:else>
</ul>
The default templates (these are used if the above templates don't exist):
			<ul>
				<li><g:link controller="seqSampleReport" action="downloadReportTemplate" params='[filename:"Template_default_fail.docx"]' target="_blank">Fail</g:link> (If QC fails, this report template is used)</li>
				<li><g:link controller="seqSampleReport" action="downloadReportTemplate" params='[filename:"Template_default_neg.docx"]' target="_blank">Neg</g:link> (If no variants are reportable, this report template is used)</li>
				<li><g:link controller="seqSampleReport" action="downloadReportTemplate" params='[filename:"Template_default_var.docx"]' target="_blank">Var</g:link> (If QC passes and there are reportable variants, this report template is used)</li>
			</ul>

<p>To edit the templates, visit the <a href="<g:context/>/admin/reportUpload">upload report template page</a> as an administrator.</p>
			</div>
		</div>
	</section>

	<section id="seqSampleReports">
		<div class="container outlined-box">
			<h1>List of SeqSample Reports</h1>
			%{--${seqSampleReports as JSON}--}%
			<table>
				<tr>
					<th>Date Created</th>
					<th>Report Creator</th>
					<th>Reported Variants</th>
					<th>Preview Mail Merge Fields</th>
					<th>Link to PDF</th>
					<th>Draft or final</th>
				</tr>
			<g:each in="${seqSampleReports.sort{a,b->b.dateCreated<=>a.dateCreated}}" status="i" var="ssri">
				<tr>
					<td>${ssri.dateCreated.format("d-MMM-yyyy hh:mm a")}</td>
					<td>${ssri.user}</td>
					<td>${ssri.curVariantReports.size()}</td>
					<td><a href="<g:context/>/seqSampleReport/edit/${ssri.id}">Preview</a></td>
					<td><g:downloadPdfLink ssri="${ssri}"/></td>
					<td>${ssri.isdraft()}</td>
				</tr>
			</g:each>
			</table>




	</div>
</section>







</body>
</html>




