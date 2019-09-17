
<%@ page import="org.petermac.pathos.curate.CivicVariant" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'civicVariant.label', default: 'CivicVariant')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-civicVariant" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-civicVariant" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list civicVariant">
			
				<g:if test="${civicVariantInstance?.variant_civic_url}">
				<li class="fieldcontain">
					<span id="variant_civic_url-label" class="property-label">CiVIC</span>
					
						<span class="property-value" aria-labelledby="variant_civic_url-label"><a target="_blank" href="${civicVariantInstance.variant_civic_url}">${civicVariantInstance.variant_civic_url}</a></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.gene}">
				<li class="fieldcontain">
					<span id="gene-label" class="property-label"><g:message code="civicVariant.gene.label" default="Gene" /></span>
					
						<span class="property-value" aria-labelledby="gene-label"><g:fieldValue bean="${civicVariantInstance}" field="gene"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.entrez}">
				<li class="fieldcontain">
					<span id="entrez-label" class="property-label"><g:message code="civicVariant.entrez.label" default="Entrez" /></span>
					
						<span class="property-value" aria-labelledby="entrez-label"><g:fieldValue bean="${civicVariantInstance}" field="entrez"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.variant}">
				<li class="fieldcontain">
					<span id="variant-label" class="property-label"><g:message code="civicVariant.variant.label" default="Variant" /></span>
					
						<span class="property-value" aria-labelledby="variant-label"><g:fieldValue bean="${civicVariantInstance}" field="variant"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.summary}">
				<li class="fieldcontain">
					<span id="summary-label" class="property-label"><g:message code="civicVariant.summary.label" default="Summary" /></span>
					
						<span class="property-value" aria-labelledby="summary-label"><g:fieldValue bean="${civicVariantInstance}" field="summary"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.chromosome}">
				<li class="fieldcontain">
					<span id="chromosome-label" class="property-label"><g:message code="civicVariant.chromosome.label" default="Chromosome" /></span>
					
						<span class="property-value" aria-labelledby="chromosome-label"><g:fieldValue bean="${civicVariantInstance}" field="chromosome"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.start}">
				<li class="fieldcontain">
					<span id="start-label" class="property-label"><g:message code="civicVariant.start.label" default="Start" /></span>
					
						<span class="property-value" aria-labelledby="start-label"><g:fieldValue bean="${civicVariantInstance}" field="start"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.stop}">
				<li class="fieldcontain">
					<span id="stop-label" class="property-label"><g:message code="civicVariant.stop.label" default="Stop" /></span>
					
						<span class="property-value" aria-labelledby="stop-label"><g:fieldValue bean="${civicVariantInstance}" field="stop"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.reference_bases}">
				<li class="fieldcontain">
					<span id="reference_bases-label" class="property-label"><g:message code="civicVariant.reference_bases.label" default="Referencebases" /></span>
					
						<span class="property-value" aria-labelledby="reference_bases-label"><g:fieldValue bean="${civicVariantInstance}" field="reference_bases"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.variant_bases}">
				<li class="fieldcontain">
					<span id="variant_bases-label" class="property-label"><g:message code="civicVariant.variant_bases.label" default="Variantbases" /></span>
					
						<span class="property-value" aria-labelledby="variant_bases-label"><g:fieldValue bean="${civicVariantInstance}" field="variant_bases"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.representative_transcript}">
				<li class="fieldcontain">
					<span id="representative_transcript-label" class="property-label">Representative transcript</span>
					
						<span class="property-value" aria-labelledby="representative_transcript-label"><g:fieldValue bean="${civicVariantInstance}" field="representative_transcript"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.ensembl_version}">
				<li class="fieldcontain">
					<span id="ensembl_version-label" class="property-label">Ensembl version</span>
					
						<span class="property-value" aria-labelledby="ensembl_version-label"><g:fieldValue bean="${civicVariantInstance}" field="ensembl_version"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.reference_build}">
				<li class="fieldcontain">
					<span id="reference_build-label" class="property-label">Reference build</span>
					
						<span class="property-value" aria-labelledby="reference_build-label"><g:fieldValue bean="${civicVariantInstance}" field="reference_build"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.chromosome2}">
				<li class="fieldcontain">
					<span id="chromosome2-label" class="property-label"><g:message code="civicVariant.chromosome2.label" default="Chromosome2" /></span>
					
						<span class="property-value" aria-labelledby="chromosome2-label"><g:fieldValue bean="${civicVariantInstance}" field="chromosome2"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.start2}">
				<li class="fieldcontain">
					<span id="start2-label" class="property-label">Start</span>
					
						<span class="property-value" aria-labelledby="start2-label"><g:fieldValue bean="${civicVariantInstance}" field="start2"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.stop2}">
				<li class="fieldcontain">
					<span id="stop2-label" class="property-label">Stop</span>
					
						<span class="property-value" aria-labelledby="stop2-label"><g:fieldValue bean="${civicVariantInstance}" field="stop2"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.representative_transcript2}">
				<li class="fieldcontain">
					<span id="representative_transcript2-label" class="property-label">Representative transcript</span>
					
						<span class="property-value" aria-labelledby="representative_transcript2-label"><g:fieldValue bean="${civicVariantInstance}" field="representative_transcript2"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.variant_types}">
				<li class="fieldcontain">
					<span id="variant_types-label" class="property-label">Variant types</span>
					
						<span class="property-value" aria-labelledby="variant_types-label"><g:fieldValue bean="${civicVariantInstance}" field="variant_types"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.hgvs_expressions}">
				<li class="fieldcontain">
					<span id="hgvs_expressions-label" class="property-label">Hgvs expressions</span>
					
						<span class="property-value" aria-labelledby="hgvs_expressions-label"><g:fieldValue bean="${civicVariantInstance}" field="hgvs_expressions"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicVariantInstance?.last_review_date}">
				<li class="fieldcontain">
					<span id="last_review_date-label" class="property-label">Last review date</span>
					
						<span class="property-value" aria-labelledby="last_review_date-label"><g:fieldValue bean="${civicVariantInstance}" field="last_review_date"/></span>
					
				</li>
				</g:if>


				<li class="fieldcontain">
					<span id="civicClinicalEvidence-label" class="property-label">Civic Evidence</span>


<span class="property-value">

				<g:each in="${evidenceList}" var="evidence">
<p style="margin:0;"><a href="<g:context/>/CivicClinicalEvidence/show/${evidence.id}" target="_blank">${evidence}</a></p>
				</g:each>

</span>

				</li>

			</ol>

		</div>
	</body>
</html>
