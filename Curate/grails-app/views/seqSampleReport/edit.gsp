<%@ page import="org.petermac.pathos.curate.RefGene; grails.converters.JSON; org.petermac.pathos.curate.SeqSampleReport" %>
<!DOCTYPE html>
<html>
<head>
	<meta name="layout" content="main">
	<g:set var="entityName" value="${message(code: 'seqSampleReport.label', default: 'SeqSampleReport')}" />
	<title>${seqSampleReportInstance?.seqSample?.seqrun} - ${seqSampleReportInstance?.seqSample?.sampleName} - Edit Report</title>
	<parameter name="hotfix" value="off" />

	<g:javascript src="jquery.are-you-sure.js"/>
<r:style>

input:read-only, textarea:read-only {
	background: rgba(0,0,0,0.15);
	cursor: not-allowed;
}

input:-moz-read-only, textarea:-moz-read-only {
	background: rgba(0,0,0,0.15);
	cursor: not-allowed;
}

label {
	font-weight: 900;
}
label:before {
	content: "«";
}
label:after {
	content: "»";
}

label.cleanlabel:before {
	content: "";
}
label.cleanlabel:after {
	content: "";
}

#edit-seqSampleReport td:last-child, #edit-seqSampleReport th:last-child, #edit-seqSampleReport td:last-child input, #edit-seqSampleReport textarea {
	width:100%
}
td {
	vertical-align: top;
}

#edit-seqSampleReport {
	padding-bottom: 40px;
}
#sortable {
	padding: 0;
}
#sortable li {
	cursor: grab;
	cursor: -webkit-grab;
}
#sortable li:active {
	cursor: grabbing;
	cursor: -webkit-grabbing;
}
#citations {
	width: 100%;
	height: 100px;
	background: rgba(0,0,0,0.15) !important;
}
.curVariantReport {
	padding: 10px;
}

.hwt-container {
	width: 100%;
}

#citationLinks {
	list-style: none;
	padding: 0;
}

#citationLinks li {
	float: left;
	margin: 5px;
}
#citationLinks li a {
	padding: 5px;
	border: 1px solid black;
	border-radius: 3px;
}

input.savebutton {
	width: 100%;
}

.message:before{
	content: url(<g:context/>/images/skin/information.png);
	padding-right: 3px;
}
.message {
	text-align: center;
	font-size: 20px;
	border: 1px solid #b2d1ff;
	margin: 5px;
}

#seqSampleInfo h3 {
	text-align: center;
}
#documentInfoBox {
	padding: 5px;
}
#documentInfoBox h3 {
	margin: 5px;
}

#documentInfoBox p {
	margin: 0;
}

.seqSampleReportWarning {
	list-style: none;
	display: none;
	padding: 5px;
	border: dashed 3px red;
}

<g:if test="${pathosExport}">
#patientDetailsTbody {
	border: 1px solid black;
}

#auslabRefreshIcon {
	padding: 2px;
	float: right;
	cursor: pointer;
}

#auslabRefreshIcon.refreshing {
	-webkit-animation: rotate 6s linear infinite;
}

@-webkit-keyframes rotate {
	from{ -webkit-transform: rotate(0deg);   }
	to{   -webkit-transform: rotate(360deg); }
}
</g:if>

</r:style>

</head>
<body>

	<nav class="nav" role="navigation">
		<ul>
			<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			<li><a class="sample" href="<g:context/>/seqVariant/svlist/${seqSampleReportInstance.seqSample.id}">SeqSample - ${seqSampleReportInstance.seqSample.sampleName}</a></li>
			<li><a onclick="return confirm('Are you sure you want to clear this draft?')" href="<g:context/>/seqVariant/buildSeqSampleReport/${seqSampleReportInstance?.seqSample.id}" class="regenerate">Clear Draft</a></li>
			<li><a id="navSaveButton" class="save disabled" href="#saved" onclick="#disabled">Save Draft</a></li>
			<li><a class='pdf' href="#downloadPDF" onclick="viewPDF()">View PDF</a></li>
			<li><a class='word' href="#downloadWORD" onclick="viewWORD()">View Word</a></li>
<g:if test="${pathosExport}">
			<li><a class="pdf" href="#publish" onclick="publishPDF()">Publish</a></li>
</g:if>
			<li><a class="help" href="${jiraAddress}/confluence/display/PVS/Reporting+Mail+Merge+Fields" target="_blank">Help</a></li>

<sec:ifAnyGranted roles="ROLE_DEV">
	<li><span style="font-size:20px;">Dev:</span></li>
	<li><a class="pdf" href="<g:context/>/seqVariant/preparedReport?fileExt=pdf&test=true&id=${seqSampleReportInstance.id}" target="_blank">TEST PDF</a></li>
	<li><a class="magic" href="<g:context/>/seqSampleReport/inspect/${seqSampleReportInstance.seqSample.id}">Inspect</a></li>
</sec:ifAnyGranted>

		</ul>
	</nav>


<div id="edit-seqSampleReport" role="main">
<div class="container outlined-box">

	<section class="row">
		<g:hasErrors bean="${seqSampleReportInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${seqSampleReportInstance}" var="error">
					<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
		</g:hasErrors>
	</section>





		<g:form name="seqSampleReportForm" method="post" >
			<g:hiddenField name="id" value="${seqSampleReportInstance?.id}" />
			<g:hiddenField name="version" value="${seqSampleReportInstance?.version}" />
			<fieldset class="form">
				<h1 id="mismatchSS" class="seqSampleReportWarning">You have made changes in PathOS that require your report draft to be cleared</h1>

				<ul id="oldCVs" class="seqSampleReportWarning"></ul>

				<section id="seqSampleInfo">
					<div class="row">

						<div class="col-xs-4">
							<h3>Patient</h3>
							<table>
								<thead>
									<tr>
										<th>Mail merge</th>
										<th>Value</th>
									</tr>
								</thead>
								<tbody id="patientDetailsTbody">
									<tr>
										<td><label for="patient">patient</label></td>
										<td><g:textField readonly="1" name="patient" value="${seqSampleReportInstance?.patient()}"/></td>
									</tr>
									<tr>
										<td><label for="urn">urn</label></td>
										<td><g:textField readonly="1" name="urn" value="${seqSampleReportInstance?.urn()}"/></td>
									</tr>
									<tr>
										<td><label for="dob">dob</label></td>
										<td><g:textField readonly="1" name="dob" value="${seqSampleReportInstance?.dob()}"/></td>
									</tr>
									<tr>
										<td><label for="age">age</label></td>
										<td><g:textField readonly="1" name="age" value="${seqSampleReportInstance?.age()}"/></td>
									</tr>
									<tr>
										<td><label for="sex">sex</label></td>
										<td><g:textField readonly="1" name="sex" value="${seqSampleReportInstance?.sex()}"/></td>
									</tr>
<g:if test="${pathosExport}">
									<tr>
										<td>Auslab data <i id="auslabRefreshIcon" class="fa fa-refresh" aria-hidden="true"></i></td>
										<td id="auslabMessage"></td>
									</tr>
</g:if>
								</tbody>
								<tbody>
									<tr>
										<td><label for="address">address</label></td>
										<td><g:textArea rows="5" maxlength="255" name="address" value="${seqSampleReportInstance?.address}"/></td>
									</tr>
									<tr>
										<td><label for="phone">phone</label></td>
										<td><g:textField name="phone" value="${seqSampleReportInstance?.phone}"/></td>
									</tr>
									<tr>
										<td><label for="requester">requester</label></td>
										<td><g:textField readonly="1" name="requester" value="${seqSampleReportInstance?.requester()}"/></td>
									</tr>
									<tr>
										<td><label for="extref">extref</label></td>
										<td><g:textField readonly="1" name="extref" value="${seqSampleReportInstance?.extref()}"/></td>
									</tr>
									<tr>
										<td><label for="location">location</label></td>
										<td><g:textField readonly="1" name="location" value="${seqSampleReportInstance?.location()}"/></td>
									</tr>
									<tr>
										<td><label for="requestAddress">requestAddress</label></td>
										<td><g:textArea rows="5" maxlength="255" name="requestAddress" value="${seqSampleReportInstance?.requestAddress}"/></td>
									</tr>
									<tr>
										<td><label for="copyTo">copyTo</label></td>
										<td><g:textField name="copyTo" value="${seqSampleReportInstance?.copyTo}"/></td>
									</tr>
								</tbody>
							</table>
						</div>
						<div class="col-xs-4">
							<h3>Sequenced Sample</h3>
							<table>
								<thead>
									<tr>
										<th>Mail merge</th>
										<th>Value</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td><label for="morphology">morphology</label></td>
										<td><g:textField name="morphology" value="${seqSampleReportInstance?.morphology}"/></td>
									</tr>
									<tr>
										<td><label for="site">site</label></td>
										<td><g:textField name="site" value="${seqSampleReportInstance?.site}"/></td>
									</tr>
									<tr>
										<td><label for="tumour_pct">tumour_pct</label></td>
										<td><g:textField name="tumour_pct" value="${seqSampleReportInstance?.tumour_pct}"/></td>
									</tr>
									<tr>
										<td><label for="collect_date">collect_date</label></td>
										<td><g:textField name="collect_date" value="${seqSampleReportInstance?.collect_date}"/></td>
									</tr>
									<tr>
										<td><label for="rcvd_date">rcvd_date</label></td>
										<td><g:textField name="rcvd_date" value="${seqSampleReportInstance?.rcvd_date}"/></td>
									</tr>
									<tr>
										<td><label for="ampReads">ampReads</label></td>
										<td><g:textField readonly="1" name="ampReads" value="${seqSampleReportInstance?.ampReads}"/></td>
									</tr>
									<tr>
										<td><label for="ampPct">ampPct</label></td>
										<td><g:textField readonly="1" name="ampPct" value="${seqSampleReportInstance?.ampPct}"/></td>
									</tr>
									<tr>
										<td><label for="lowAmps">lowAmps</label></td>
										<td><g:textField readonly="1" name="lowAmps" value="${seqSampleReportInstance?.lowAmps}"/></td>
									</tr>
									<tr>
										<td><label for="rois">rois</label></td>
										<td><g:textArea readonly="1" name="rois" rows="5" maxlength="1000" value="${seqSampleReportInstance?.rois}"/></td>
									</tr>
									<tr>
										<td><label for="specimen">specimen</label></td>
										<td><g:textField name="specimen" value="${seqSampleReportInstance?.specimen}"/></td>
									</tr>
									<tr>
										<td><label for="sampleType">sampleType</label></td>
										<td><g:textField name="sampleType" value="${seqSampleReportInstance?.sampleType}"/></td>
									</tr>
									<tr>
										<td><label for="histologicalFeatures">histologicalFeatures</label></td>
										<td><g:textField name="histologicalFeatures" value="${seqSampleReportInstance?.histologicalFeatures}"/></td>
									</tr>
									<tr>
										<td><label for="uncoveredRegions">uncoveredRegions</label></td>
										<td><g:textArea rows="5" maxlength="1000" name="uncoveredRegions" value="${seqSampleReportInstance?.uncoveredRegions}"/></td>
									</tr>
								</tbody>
							</table>
						</div>
						<div class="col-xs-4">
							<h3 style="color:rgba(0,0,0,0);">PathOS Admin</h3>
							<table>
								<thead>
									<tr>
										<th>Mail merge</th>
										<th>Value</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td><label for="sample">sample</label></td>
										<td><g:textField readonly="1" name="sample" value="${seqSampleReportInstance?.seqSample?.sampleName}"/></td>
									</tr>
									<tr>
										<td><label for="isdraft">isdraft</label></td>
										<td><g:textField readonly="1" name="isdraft" value="${seqSampleReportInstance?.isdraft()}"/></td>
									</tr>
									<tr>
										<td><label for="clinContext">clinContext</label></td>
										<td><g:textField readonly="1" name="clinContext" value="${seqSampleReportInstance?.clinContext()}"/></td>
									</tr>
									<tr>
										<td><label for="firstReviewer">firstReviewer</label></td>
										<td><g:textField readonly="1" name="firstReviewer" value="${seqSampleReportInstance?.firstReviewer()}"/></td>
									</tr>
									<tr>
										<td><label for="firstReviewedDate">firstReviewedDate</label></td>
										<td><g:textField readonly="1" name="firstReviewedDate" value="${seqSampleReportInstance?.firstReviewedDate()}"/></td>
									</tr>
									<tr>
										<td><label for="secondReviewer">secondReviewer</label></td>
										<td><g:textField readonly="1" name="secondReviewer" value="${seqSampleReportInstance?.secondReviewer()}"/></td>
									</tr>
									<tr>
										<td><label for="secondReviewedDate">secondReviewedDate</label></td>
										<td><g:textField readonly="1" name="secondReviewedDate" value="${seqSampleReportInstance?.secondReviewedDate()}"/></td>
									</tr>
								<tr>
									<td><label for="finalReviewer">finalReviewer</label></td>
									<td><g:textField readonly="1" name="finalReviewer" value="${seqSampleReportInstance?.finalReviewer()}"/></td>
								</tr>
								<tr>
									<td><label for="finalReviewedDate">finalReviewedDate</label></td>
									<td><g:textField readonly="1" name="finalReviewedDate" value="${seqSampleReportInstance?.finalReviewedDate()}"/></td>
								</tr>
								</tbody>
							</table>
							<div id="documentInfoBox">
								<label class="cleanlabel">Template:</label>
								<input id="templateNameDisplay" readonly=1 value="${templateName}" style="width:100%;"/>

							<g:if test="${allowForced}">
								<div>
									%{--<label class="cleanlabel" for="docx">[Admin] Specific .docx Template:</label>--}%
									<input type="hidden" id="filename" name="filename" value="${seqSampleReportInstance.seqSample.sampleName} Template.docx"/>
									<input type="file" accept=".docx" id="docx" name="docx"/>
								</div>
<r:script>
	templateName = $("#templateNameDisplay").val();
	$("#docx").on("change", function(){
		if($("#docx").val()) {
			$("#templateNameDisplay").val("Single-use .docx detected");
		} else {
			$("#templateNameDisplay").val(templateName);
		}
	});
</r:script>

							</g:if>
								<p style="display: none;"><span style="font-weight: 900;">Templates for this sample:</span> <a href="${templateDownloadLink}_var.docx" target="_blank">var</a> / <a href="${templateDownloadLink}_neg.docx" target="_blank">neg</a> / <a href="${templateDownloadLink}_fail.docx" target="_blank">fail</a></p>
								<p><span style="font-weight: 900;">Billing Code:</span>
								<g:each in="${billingCodes}" var="billingCode">
									<g:showTestSet billingCode="${billingCode}" name="${templateName}"/>
								</g:each>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-xs-12">
							<table>
								<tr>
									<td><label for="clinicalDetails">clinicalDetails</label></td>
									<td><g:textArea name="clinicalDetails" class="highlightPMIDs" rows="5" maxlength="8000" value="${seqSampleReportInstance?.clinicalDetails}"/></td>
								</tr>
								<tr>
									<td><label for="resultSummary">resultSummary</label></td>
									<td><g:textArea name="resultSummary" class="highlightPMIDs" rows="5" maxlength="8000" value="${seqSampleReportInstance?.resultSummary}"/></td>
								</tr>
								<tr>
									<td><label for="recommendations">recommendations</label></td>
									<td><g:textArea name="recommendations" class="highlightPMIDs" rows="5" maxlength="8000" value="${seqSampleReportInstance?.recommendations}"/></td>
								</tr>
							</table>
						</div>
					</div>
				</section>

				<section id="curVariants">
					<div class="row">


<div class="col-xs-6">
	<h2>Curated Variants</h2>
	<g:if test="${seqSampleReportInstance?.curVariantReports.size() < 1}">
		<p>To add a variant to your report, mark it as "reportable" on the <a href="<g:context/>/seqVariant/svlist/${seqSampleReportInstance.seqSample.id}" target="_blank">svlist page</a></p>
	</g:if>
	<ol id="sortable"></ol>
	<input type="button" value="Add Variant" class="savebutton" onclick="addCurVariant()">
</div>


<r:style>
#loading-banner.show {
	display: block;
}
#loading-banner {
	display: none;
	z-index: 1;
	position: fixed;
	left: 25%;
	top: 300px;
	text-align: center;
	width: 50%;
	max-width: 400px;
	padding: 5px;
	color: transparent;
	font-size: 1px;
	border: 1px solid #2694e8;
	background: #3baae3 url(<g:context/>/css/jquery-ui-1.11.0.custom/images/ui-bg_glass_50_3baae3_1x400.png) 50% 50% repeat-x;
}
#loading-banner::before {
	content: url(<g:context/>/dist/images/pathos_logo_animated.svg);
}
</r:style>
<div id="loading-banner"></div>




<r:script>
function addCurVariant() {
	try {
		templateName = templateName || $("#templateNameDisplay").val();
		templateName = templateName.replace("_neg.docx", "_var.docx");
		if($("#templateNameDisplay").val() != "Single-use .docx detected") {
			$("#templateNameDisplay").val(templateName);
		}
	} catch(err) {
		console.error(err);
	}

	var hgvsg = prompt("What is the hgvsg? Leave blank for none.");

	var package = {
		id: ${seqSampleReportInstance?.id},
		hgvsg: hgvsg
	};
	$("#loading-banner").addClass("show");

	$.ajax({
		type: "POST",
		url: "<g:context/>/SeqSampleReport/makeNewCurVariantReport",
		complete: function(d){
			console.info("Make New CurVariant Report: ", d);
			addToSortableList(d.responseJSON, false);
			buildCVR(d.responseJSON, hgvsg, false);
			$("#loading-banner").removeClass("show");
		},
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		data: JSON.stringify(package)
	});
}

function buildCVR(data, hgvsg, prexisting){
	var id = "cvr-" + data.id;
	var box = d3.select("#"+id);

	if(!prexisting) {
		box = d3.select("#curVariantReports").insert("div", "div");
	}

	box.classed("curVariantReport row uncuratedVariant", true)
		.attr("id", id);

	var title_text = "Additional Variant";
	if (data.hgvsc) {
	    title_text += " - "+data.hgvsc;
	} else if (hgvsg) {
	    title_text += " - "+hgvsg
	}

	var title = box.append("h3");

	if (hgvsg) {
	    title = title.append("a").attrs({
			href: "#none",
			onclick: "PathOS.variant.viewer({hgvsg:'"+hgvsg+"'})"
		});
	}

	title.text(title_text).style("padding-left", "50px");

	var left = box.append("div").classed("col-xs-4", true)
		.append("table");
	var lhead = left.append("thead").append("tr");
	lhead.append("th").text("Mail merge");
	lhead.append("th").text("Value");

	var mailmerge = [ "sample", "gene", "exon", "class", "ampClass", "clinicalSignificance", "refseq", "hgvsc", "hgvsp", "refseqNP", "aaChange", "varreaddepth", "totalreaddepth", "afpct"];

	var lbody = left.append("tbody");

	mailmerge.forEach(function(d){
		var row = lbody.append("tr").classed("tr-"+d, true);
		row.append("td").append("label").attr("for", d).text(d);
		row.append("td").append("input")
			.attrs({
				type: "text",
				name: d,
				id: d
			});
	});

	var right = box.append("div").classed("col-xs-8", true);
	var mut = right.append("div").classed("fieldcontain", true);
	mut.append("label").attr("for", "mut").text("mut");
	mut.append("br");
	mut.append("div").append("textarea")
		.attrs({
			class: "highlightPMIDs hwt-input hwt-content",
			name: "mut",
			cols: 40,
			rows: 7,
			maxlength: 8000,
			id: 'mut'
		});
	var genedesc = right.append("div").classed("fieldcontain", true);
	genedesc.append("label").attr("for", 'genedesc').text('genedesc');
	genedesc.append("br");
	genedesc.append("textarea")
		.attrs({
			class: "highlightPMIDs hwt-input hwt-content",
			name: "genedesc",
			cols: 40,
			rows: 7,
			maxlength: 8000,
			id: 'genedesc'
		});

	box.select("input[name='sample']").attr("value", data.sample)
		.attr("readonly", 1);

	box.select("textarea[name='mut']").text(data.mut);
	box.select("textarea[name='genedesc']").text(data.genedesc);

	box.select("input[name='mut']").attr("value", data.mut);
	box.select("input[name='genedesc']").attr("value", data.genedesc);
	box.select("input[name='gene']").attr("value", data.gene);
	box.select("input[name='exon']").attr("value", data.exon);
	box.select("input[name='class']").attr("value", data.pmClass);
	box.select("input[name='ampClass']").attr("value", data.ampClass);
	box.select("input[name='clinicalSignificance']").attr("value", data.clinicalSignificance);
	box.select("input[name='refseq']").attr("value", data.refseq);
	box.select("input[name='hgvsc']").attr("value", data.hgvsc);
	box.select("input[name='hgvsp']").attr("value", data.hgvsp);
	box.select("input[name='refseqNP']").attr("value", data.refseqNP);
	box.select("input[name='aaChange']").attr("value", data.aaChange);
	box.select("input[name='varreaddepth']").attr("value", data.varreaddepth);
	box.select("input[name='totalreaddepth']").attr("value", data.totalreaddepth);
	box.select("input[name='afpct']").attr("value", data.afpct);

	box.select(".tr-gene label").html("")
		.append("a").text("gene").attr("href", "#none")
		.attr("title", "Click to update «genedesc» for this variant")
		.style("cursor", "pointer")
		.on("click", function(d){
			var gene = $("#"+id+" .tr-gene td input").val();
			$.get("<g:context/>/refGene/genedesc?gene="+gene)
				.success(function(d){
					if(d) box.select("textarea[name='genedesc']").text(d);
					else alert('Gene "'+gene+'" not found')
				});
		});
	$('#seqSampleReportForm').trigger('rescan.areYouSure');
	PathOS.pubmed.applyHighlight("highlightPMIDs");
}
</r:script>


<div class="col-xs-6" style="padding-bottom: 10px;">
	<h3>Citations</h3>
	<textarea readonly="1" class="highlightPMIDs" id="citations">${seqSampleReportInstance.citations}</textarea>
	<ul id="citationLinks"></ul>
</div>

</div>
<r:style>
	#sortable {
		list-style: none;
	}
	#sortable li:before {
		font: normal normal normal 14px/1 FontAwesome;
		content: "\f07d ";
		padding: 0 3px;
	}
</r:style>
<r:script>

	/**
	* This is how we do a report.
	* DKGM June 2017
	* @param data
	*/
	function doReport(data) {
		// Save any info on the page
		save();
		$("#loading-banner").addClass("show");

		// Upload the template, if it exists
		if(document.getElementById("docx") && document.getElementById("docx").files[0]) {
			if( document.getElementById("docx").files[0].size < 30000000) {
				if(document.getElementById("docx").files[0].name.split(".").pop().toLowerCase() == "docx") {
					var formData = new FormData($("#seqSampleReportForm")[0]);
					$.ajax({
						type: "POST",
						url: "<g:context/>/admin/upload_report",
						data: formData,
						success: function (d) {
							console.info("File upload response: ", d);
							if (d == "success") {
								console.info("File successfully uploaded.");
								data.test = true;
								visitPage(data);
							} else if (d == "no transfer") {
								alert("Template file could not be uploaded.");
							} else {
								alert("Template file was not uploaded.");
							}
						},
						cache: false,
						contentType: false,
						processData: false
					});
				} else {
					alert("Please upload a .docx");
				}
			} else {
				alert("Please select a .docx file under 30mb");
			}
		} else {
			console.info("No template needed.");
			visitPage(data);
		}

		// Split the logic if we're trying to publish.
		function visitPage(data) {
			if(data.publish) {
				$("#loading-banner").addClass("show");
				$.ajax({
					url: "<g:context/>/seqVariant/preparedReport?"+ $.param(data),
					complete: function(d){
						$("#loading-banner").removeClass("show");
						console.info("AJAX prepareReport", d);
						if(confirm("Published to Auslab.")) {
							window.location = "<g:context/>/seqVariant/cloneSSR?" + $.param(data);
						} else {
							window.location = "<g:context/>/seqVariant/cloneSSR?" + $.param(data);
						}
					}
				})
			} else {
				$("#loading-banner").removeClass("show");
				window.open("<g:context/>/seqVariant/preparedReport?" + $.param(data));
			}
		}

	}

	function publishPDF() {
		doReport({
			publish: true,
			fileExt: "pdf",
			id: ${seqSampleReportInstance.id}
		});
	}

	function viewPDF() {
		doReport({
			fileExt: "pdf",
			id: ${seqSampleReportInstance.id}
		});
	}

	function viewWORD() {
		doReport({
			fileExt: "docx",
			id: ${seqSampleReportInstance.id}
		});
	}
	function save() {
		var cvrs = [];

		$(".curVariantReport").each(function(d, i){
			cvrs.push(parseInt($(i).attr("id").split("-")[1]));
		})

		cvrs.forEach((cvr) => saveCurVariant(cvr));
		saveSeqSampleReportInfo();
		$('#seqSampleReportForm').trigger('reinitialize.areYouSure');
	}

	function saveSeqSampleReportInfo() {
		var package = {
			id: ${seqSampleReportInstance?.id},
			clinicalDetails: $("#clinicalDetails").val(),
			resultSummary: $("#resultSummary").val(),
			recommendations: $("#recommendations").val(),
			address: $("#address").val(),
			phone: $("#phone").val(),
			requestAddress: $("#requestAddress").val(),
			copyTo: $("#copyTo").val(),
			curVariantReports: d3.selectAll("#sortable li").data().map( d => d.id ),
			specimen: $("#specimen").val(),
			sampleType: $("#sampleType").val(),
			histologicalFeatures: $("#histologicalFeatures").val(),
			uncoveredRegions: $("#uncoveredRegions").val(),
			morphology: $("#morphology").val(),
			site: $("#site").val(),
			tumour_pct: $("#tumour_pct").val(),
			collect_date: $("#collect_date").val(),
			rcvd_date: $("#rcvd_date").val()
		}

		$.ajax({
			type: "POST",
			url: "<g:context/>/SeqSampleReport/saveSeqSampleReportInfo",
			complete: function(d){
				console.log(d);
				if(d.status == 200 ) {
					$("#version").val(d.responseJSON.version);
					console.info("Saved seqSampleReportInfo, new version is: ", d.responseJSON.version);
					console.info("Success");
					PathOS.notes.add("Saved the Draft Sequenced Sample Report");
				} else if (d.status == 412) {
					console.error(d.responseJSON);
					alert("Error, there is an invalid character in this report.");
				}
			},
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			data: JSON.stringify(package)
		});
	}

</r:script>


<div class="row" id="curVariantReports">
	<g:each in="${seqSampleReportInstance?.curVariantReports}" var="cvr">

	<g:if test="${cvr.curVariant}">
	<div id="cvr-${cvr?.id}" class="curVariantReport row">
		<h3 style="padding-left: 50px"><a href="#none" onclick="PathOS.variant.viewer({svid: ${seqSampleReportInstance.seqSample.seqVariants.find { it.hgvsg == cvr.curVariant.hgvsg }?.id}})">${cvr?.gene} - ${cvr?.hgvsc}</a></h3>
		<g:render template="curVariantReportForm" model="[curVariantReportInstance: cvr]"/>
	</div>
	</g:if>
	<g:else>
		<div id="cvr-${cvr?.id}"></div>
		<r:script>buildCVR(${cvr as JSON}, ${cvr?.curVariant?.hgvsg ?: '""'}, true);</r:script>
	</g:else>
	</g:each>
</div>
				</section>
			</fieldset>
		</g:form>
	</div>

</div>
<r:script>
function saveCurVariant(cvr) {
	console.info("saving: ", cvr);

	var package = {
		id: cvr,
		mut: $("#cvr-"+cvr+" textarea[name='mut']").val(),
		genedesc: $("#cvr-"+cvr+" textarea[name='genedesc']").val(),
		gene: $("#cvr-"+cvr+" input[name='gene']").val(),
		exon: $("#cvr-"+cvr+" input[name='exon']").val(),
		pmClass: $("#cvr-"+cvr+" input[name='class']").val(),
		ampClass: $("#cvr-"+cvr+" input[name='ampClass']").val(),
		clinicalSignificance: $("#cvr-"+cvr+" input[name='clinicalSignificance']").val(),
		refseq: $("#cvr-"+cvr+" input[name='refseq']").val(),
		hgvsc: $("#cvr-"+cvr+" input[name='hgvsc']").val(),
		hgvsp: $("#cvr-"+cvr+" input[name='hgvsp']").val(),
		varreaddepth: $("#cvr-"+cvr+" input[name='varreaddepth']").val(),
		totalreaddepth: $("#cvr-"+cvr+" input[name='totalreaddepth']").val(),
		afpct: $("#cvr-"+cvr+" input[name='afpct']").val()
	};
	console.info(package);

	$.ajax({
		type: "POST",
		url: "<g:context/>/SeqSampleReport/saveCurVariantReportInfo",
		complete: function(d){
			if (d.status == 200) {
				console.info("Saved curVariantReport for "+package.id+" - "+package.gene);
			} else if(d.status == 412) {
				console.error(d.responseJSON);
				alert("Error, there is an invalid character for: "+package.gene);
			}
		},
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		data: JSON.stringify(package)
	});
}

function refreshCitations(){
	var id = ${seqSampleReportInstance.id};
	$.get({
		url: "<g:context/>/SeqSampleReport/refreshCitations/"+id,
		complete: function(d){
			var citations = d.responseText;

			$("#citations").val(citations);
			d3.select("#citationLinks")
				.selectAll("li")
				.remove();

			d3.select("#citationLinks")
				.selectAll("li").data(PathOS.pubmed.findCitations(citations))
				.enter()
				.append("li")
				.append('a')
				.attr("target", "_blank")
				.attr("href", pmid => "<g:context/>/Pubmed?pmid="+pmid)
				.text(d => "[PMID: "+d+"]");
		}
	})
}

refreshCitations();
PathOS.pubmed.applyHighlight("highlightPMIDs");

	$( function() {
		${seqSampleReportInstance?.curVariantReports as grails.converters.JSON}.forEach(addToSortableList)

		$( "#sortable" ).sortable({
			update: function(){
				var order = d3.selectAll("#sortable li").data().map( d => d.id );
				order.forEach(function(cvr){
					$("#cvr-"+cvr).detach().appendTo("#curVariantReports");
				});
				refreshCitations();
			 	$("#navSaveButton").removeClass("disabled").attr("onclick", "save()");
			}
		});
		$( "#sortable" ).disableSelection();

	} );

function addToSortableList(data, append){
	console.info("Add to sortable list", data);
	var title = "Additional Variant";

	if(data.curVariant) {
		title = "<span class='cv-"+data.pmClass.split(":")[0]+"'>&nbsp;"+data.pmClass.split(':')[0]+"&nbsp;</span> - "+data.gene+" - "+data.hgvsc;
	} else {
		if( data.gene ) {
			title += " - " + data.gene;
		}
		if( data.hgvsc ) {
			title += " - " + data.hgvsc;
		}
	}

	var li = null;
	if( append ) {
		console.info("appending");
		li = d3.select("#sortable").append("li");
	} else {
		console.info("inserting");
		li = d3.select("#sortable").insert("li", "li");
	}

	li.datum(data)
		.classed("ui-state-default", true)
		.html(title);

	if( !data.curVariant ) {
		li.append("span")
			.attr("style", "padding-right: 5px; float: right; cursor: pointer;")
			.append("a")
			.append("i")
			.classed("fa fa-trash", true)
			.attr("href", "#removeVariant")
			.on("click", function(){
				if(confirm("Are you sure you want to delete this variant? This cannot be undone.")) {
					// Remove this CVR from the seqSampleReport
					$.ajax({
						type: "POST",
						url: "<g:context/>/seqSampleReport/removeCurVariantReport?cvr="+data.id,
						success: function(d){
							console.info("Remove CurVariant Report", d);
						}
					});
					// Delete the entry on the page
					d3.select("#cvr-"+data.id).remove();
					// Delete the sortable list element
					li.remove();
				}
			});
	}
}


$(function(){
	/**
	 * "Are you sure" stuff.
	 * This block of code prevents the user from exiting the page if new text has been added.
	 */
	$('#seqSampleReportForm').areYouSure();
    $('#seqSampleReportForm').on('dirty.areYouSure', function() {
      // Enable save button only as the form is dirty.
      $("#navSaveButton").removeClass("disabled").attr("onclick", "save()");
    });

    $('#seqSampleReportForm').on('clean.areYouSure', function() {
      // Form is clean so nothing to save - disable the save button.
      $("#navSaveButton").addClass("disabled").attr("onclick", "#disabled");
    });

<g:if test="${pathosExport}">
// Auslab refresh stuff. This doesn't need to exist in non-auslab deployments.

	$.ajax("<g:context/>/seqSampleReport/refreshPatientDetails/${seqSampleReportInstance.id}");
	d3.select("#auslabMessage").text("Click to refresh.");
	$("#auslabRefreshIcon").on("click", reloadPage);
	function reloadPage() {
		location.reload();
	}

%{--
	/**
	 * refreshPatient
	 * This block of code will refresh the patient details from Auslab on page load.
	 * Deprecated by PATHOS-2728, DKGM 8-September-2017
	 */
	function refreshPatient() {
		//console.log("refreshing!");
		$("#auslabRefreshIcon").off("click");
		$("#auslabRefreshIcon").addClass("refreshing");

		$.ajax({
			url: "<g:context/>/seqSampleReport/refreshPatientDetails/${seqSampleReportInstance.id}",
			complete: function(d){
				$("#auslabRefreshIcon").removeClass("refreshing");
				$("#auslabRefreshIcon").on("click", refreshPatient);
				console.info('Auslab Data: ', d);
				if( d.status == 500 || d.responseJSON.success == -1 ) {
					d3.select("#auslabMessage").text("Unable to retrieve patient details.");
				}  else if( d.responseJSON.success == -2 ) {
					d3.select("#auslabMessage").text("URN not found in Auslab.");
				}  else if( d.responseJSON.success == -3 ) {
					d3.select("#auslabMessage").text("Patient Sample ID not found in Auslab.");
				} else if( d.responseJSON.success == 0 ) {
					d3.select("#auslabMessage").text("Auslab checked, no details changed.");
				} else if( d.responseJSON.success > 0 ) {
					$("#patient").val(d.responseJSON.patient);
					$("#urn").val(d.responseJSON.urn);
					$("#dob").val(d.responseJSON.dob);
					$("#age").val(d.responseJSON.age);
					$("#sex").val(d.responseJSON.sex);
					d3.select("#auslabMessage").text("Auslab checked, details updated.");
				}
			}
		});
	}
--}%
</g:if>
});

// PATHOS-2530
// If the Clinical Context or "reportable" Curated Variants have changed on the SeqSample, give the user a warning.
// DKGM 17-July-2017
$( document ).ready(function() {
	var alreadyWarned = false;
	var seqSampleReportedCurVariants = ${seqSampleReportInstance.seqSample?.reportableVariants()?.collect {
		it?.currentCurVariant()?.id
	} as JSON} || [];
	var curVariantCCs = ${seqSampleReportInstance.curVariantReports?.collect { it?.curVariant?.id } as JSON} || [];
	var seqSampleCurVariants = ${seqSampleReportInstance.seqSample?.curVariants() as JSON} || [];

	seqSampleReportedCurVariants.forEach(function(cv){
		if(!alreadyWarned && curVariantCCs.indexOf(cv) < 0) {
			$("#mismatchSS")
				.text('The "reportable" Curated Variants in the Sequenced Sample are different to the Report. You may want to regenerate this page by clicking "Clear Draft".')
				.css("display", "block");
			alreadyWarned = true;
		}
	});

	curVariantCCs.forEach(function(cv){
		if(!alreadyWarned && cv && seqSampleReportedCurVariants.indexOf(cv) < 0) {
			$("#mismatchSS")
				.text('The Curated Variants in this Report are different to the "reportable" Curated Variants in the Sequenced Sample. You may want to regenerate this page by clicking "Clear Draft".')
				.css("display", "block");
			alreadyWarned = true;
		}
	});

	seqSampleCurVariants.forEach(function(cv){
	    if(cv.pmClass == "C5: Pathogenic" || cv.pmClass == "C4: Likely pathogenic") {
			if(curVariantCCs.indexOf(cv.id) < 0) {
				d3.select("#oldCVs")
					.style("display", "block")
					.append("li")
					.append("a")
					.attr("href", "<g:context/>/curVariant/show/"+cv.id)
					.text('The CurVariant "'+cv.hgvsg+'" is "'+cv.pmClass+'" but it has not been reported.');
			}
	    }
	});

    // Check that the curVariantReports with curVariants are authorised.
	var curVariants = ${seqSampleReportInstance.curVariantReports.collect({ it.curVariant }).findAll({it}) as JSON };

	curVariants.forEach(function(cv){
	    if(cv.authorisedFlag == false) {

			d3.select("#oldCVs")
				.style("display", "block")
				.append("li")
				.append("a")
				.text("This CurVariant is not authorised: "+cv.hgvsg)
				.attr("href", "<g:context/>/curVariant/show/"+cv.id);

	    }
	})





});

</r:script>


</body>
</html>

































































