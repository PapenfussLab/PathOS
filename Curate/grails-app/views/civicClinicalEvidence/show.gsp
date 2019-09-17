
<%@ page import="org.petermac.pathos.curate.CivicClinicalEvidence" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'civicClinicalEvidence.label', default: 'CivicClinicalEvidence')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-civicClinicalEvidence" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>

			</ul>
		</div>
		<div id="show-civicClinicalEvidence" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list civicClinicalEvidence">
			
				<g:if test="${civicClinicalEvidenceInstance?.gene}">
				<li class="fieldcontain">
					<span id="gene-label" class="property-label"><g:message code="civicClinicalEvidence.gene.label" default="Gene" /></span>
					
						<span class="property-value" aria-labelledby="gene-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="gene"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.entrez}">
				<li class="fieldcontain">
					<span id="entrez-label" class="property-label"><g:message code="civicClinicalEvidence.entrez.label" default="Entrez" /></span>
					
						<span class="property-value" aria-labelledby="entrez-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="entrez"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="civicClinicalEvidence.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.disease}">
				<li class="fieldcontain">
					<span id="disease-label" class="property-label"><g:message code="civicClinicalEvidence.disease.label" default="Disease" /></span>
					
						<span class="property-value" aria-labelledby="disease-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="disease"/></span>
					
				</li>
				</g:if>

				<g:if test="${civicClinicalEvidenceInstance?.doid}">
				<li class="fieldcontain">
					<span id="doid-label" class="property-label"><g:message code="civicClinicalEvidence.doid.label" default="Doid" /></span>
					
						<span class="property-value" aria-labelledby="doid-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="doid"/></span>
					
				</li>
				</g:if>

				<g:if test="${civicClinicalEvidenceInstance?.phenotypes}">
					<li class="fieldcontain">
						<span id="phenotypes-label" class="property-label"><g:message code="civicClinicalEvidence.phenotypes.label" default="Phenotypes" /></span>

						<span class="property-value" aria-labelledby="phenotypes-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="phenotypes"/></span>

					</li>
				</g:if>

				<g:if test="${civicClinicalEvidenceInstance?.drugs}">
				<li class="fieldcontain">
					<span id="drugs-label" class="property-label"><g:message code="civicClinicalEvidence.drugs.label" default="Drugs" /></span>
					
						<span class="property-value" aria-labelledby="drugs-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="drugs"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.evidence_type}">
				<li class="fieldcontain">
					<span id="evidence_type-label" class="property-label">Evidence type</span>
					
						<span class="property-value" aria-labelledby="evidence_type-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="evidence_type"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.evidence_direction}">
				<li class="fieldcontain">
					<span id="evidence_direction-label" class="property-label">Evidence direction</span>
					
						<span class="property-value" aria-labelledby="evidence_direction-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="evidence_direction"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.evidence_level}">
				<li class="fieldcontain">
					<span id="evidence_level-label" class="property-label">Evidence level</span>
					
						<span class="property-value" aria-labelledby="evidence_level-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="evidence_level"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.clinical_significance}">
				<li class="fieldcontain">
					<span id="clinical_significance-label" class="property-label">Clinical significance</span>
					
						<span class="property-value" aria-labelledby="clinical_significance-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="clinical_significance"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.evidence_statement}">
				<li class="fieldcontain">
					<span id="evidence_statement-label" class="property-label">Evidence statement</span>
					
						<span class="property-value" aria-labelledby="evidence_statement-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="evidence_statement"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.pmid}">
				<li class="fieldcontain">
					<span id="pmid-label" class="property-label"><g:message code="civicClinicalEvidence.pmid.label" default="Pmid" /></span>
						<span class="property-value" aria-labelledby="pmid-label">
							<a href="<g:context/>/Pubmed?pmid=${civicClinicalEvidenceInstance.pmid}" target="_blank">${civicClinicalEvidenceInstance.pmid}</a>
						</span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.citation}">
				<li class="fieldcontain">
					<span id="citation-label" class="property-label"><g:message code="civicClinicalEvidence.citation.label" default="Citation" /></span>
					
						<span class="property-value" aria-labelledby="citation-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="citation"/></span>
					
				</li>
				</g:if>

				<g:if test="${civicClinicalEvidenceInstance?.nctIds}">
					<li class="fieldcontain">
						<span id="nctIds-label" class="property-label"><g:message code="civicClinicalEvidence.nctIds.label" default="nctIds" /></span>

						<span class="property-value" aria-labelledby="nctIds-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="nctIds"/></span>

					</li>
				</g:if>

				<g:if test="${civicClinicalEvidenceInstance?.rating}">
				<li class="fieldcontain">
					<span id="rating-label" class="property-label"><g:message code="civicClinicalEvidence.rating.label" default="Rating" /></span>
					
						<span class="property-value" aria-labelledby="rating-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="rating"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.evidence_status}">
				<li class="fieldcontain">
					<span id="evidence_status-label" class="property-label">Evidence status</span>
					
						<span class="property-value" aria-labelledby="evidence_status-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="evidence_status"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.civicVariant}">
				<li class="fieldcontain">
					<span id="civicVariant-label" class="property-label"><g:message code="civicClinicalEvidence.civicVariant.label" default="Civic Variant" /></span>
					
						<span class="property-value" aria-labelledby="civicVariant-label"><g:link controller="civicVariant" action="show" id="${civicClinicalEvidenceInstance?.civicVariant?.id}">${civicClinicalEvidenceInstance?.civicVariant?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.civicGene}">
				<li class="fieldcontain">
					<span id="civicGene-label" class="property-label"><g:message code="civicClinicalEvidence.civicGene.label" default="Civic Gene" /></span>
					
						<span class="property-value" aria-labelledby="civicGene-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="civicGene"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.chromosome}">
				<li class="fieldcontain">
					<span id="chromosome-label" class="property-label"><g:message code="civicClinicalEvidence.chromosome.label" default="Chromosome" /></span>
					
						<span class="property-value" aria-labelledby="chromosome-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="chromosome"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.start}">
				<li class="fieldcontain">
					<span id="start-label" class="property-label"><g:message code="civicClinicalEvidence.start.label" default="Start" /></span>
					
						<span class="property-value" aria-labelledby="start-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="start"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.stop}">
				<li class="fieldcontain">
					<span id="stop-label" class="property-label"><g:message code="civicClinicalEvidence.stop.label" default="Stop" /></span>
					
						<span class="property-value" aria-labelledby="stop-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="stop"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.reference_bases}">
				<li class="fieldcontain">
					<span id="reference_bases-label" class="property-label">Reference bases</span>
					
						<span class="property-value" aria-labelledby="reference_bases-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="reference_bases"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.variant_bases}">
				<li class="fieldcontain">
					<span id="variant_bases-label" class="property-label">Variant bases</span>
					
						<span class="property-value" aria-labelledby="variant_bases-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="variant_bases"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.representative_transcript}">
				<li class="fieldcontain">
					<span id="representative_transcript-label" class="property-label">Representative transcript</span>
					
						<span class="property-value" aria-labelledby="representative_transcript-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="representative_transcript"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.chromosome2}">
				<li class="fieldcontain">
					<span id="chromosome2-label" class="property-label"><g:message code="civicClinicalEvidence.chromosome2.label" default="Chromosome2" /></span>
					
						<span class="property-value" aria-labelledby="chromosome2-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="chromosome2"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.start2}">
				<li class="fieldcontain">
					<span id="start2-label" class="property-label"><g:message code="civicClinicalEvidence.start2.label" default="Start2" /></span>
					
						<span class="property-value" aria-labelledby="start2-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="start2"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.stop2}">
				<li class="fieldcontain">
					<span id="stop2-label" class="property-label"><g:message code="civicClinicalEvidence.stop2.label" default="Stop2" /></span>
					
						<span class="property-value" aria-labelledby="stop2-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="stop2"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.representative_transcript2}">
				<li class="fieldcontain">
					<span id="representative_transcript2-label" class="property-label"><g:message code="civicClinicalEvidence.representative_transcript2.label" default="Representativetranscript2" /></span>
					
						<span class="property-value" aria-labelledby="representative_transcript2-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="representative_transcript2"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.ensembl_version}">
				<li class="fieldcontain">
					<span id="ensembl_version-label" class="property-label">Ensembl version</span>
					
						<span class="property-value" aria-labelledby="ensembl_version-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="ensembl_version"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.reference_build}">
				<li class="fieldcontain">
					<span id="reference_build-label" class="property-label">Reference build</span>
					
						<span class="property-value" aria-labelledby="reference_build-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="reference_build"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.variant_summary}">
				<li class="fieldcontain">
					<span id="variant_summary-label" class="property-label">Variant summary</span>
					
						<span class="property-value" aria-labelledby="variant_summary-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="variant_summary"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.variant_origin}">
				<li class="fieldcontain">
					<span id="variant_origin-label" class="property-label">Variant origin</span>
					
						<span class="property-value" aria-labelledby="variant_origin-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="variant_origin"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.last_review_date}">
				<li class="fieldcontain">
					<span id="last_review_date-label" class="property-label">Last review date</span>
					
						<span class="property-value" aria-labelledby="last_review_date-label"><g:fieldValue bean="${civicClinicalEvidenceInstance}" field="last_review_date"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.evidence_civic_url}">
				<li class="fieldcontain">
					<span id="evidence_civic_url-label" class="property-label">Evidence Civic url</span>
					
						<span class="property-value" aria-labelledby="evidence_civic_url-label">
							<a target="_blank" href="${civicClinicalEvidenceInstance.evidence_civic_url}">${civicClinicalEvidenceInstance.evidence_civic_url}</a>
						</span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.variant_civic_url}">
				<li class="fieldcontain">
					<span id="variant_civic_url-label" class="property-label">Variant Civic url</span>
					
						<span class="property-value" aria-labelledby="variant_civic_url-label">
							<a target="_blank" href="${civicClinicalEvidenceInstance.variant_civic_url}">${civicClinicalEvidenceInstance.variant_civic_url}</a>
						</span>
					
				</li>
				</g:if>
			
				<g:if test="${civicClinicalEvidenceInstance?.gene_civic_url}">
				<li class="fieldcontain">
					<span id="gene_civic_url-label" class="property-label">Gene Civic url</span>
					
						<span class="property-value" aria-labelledby="gene_civic_url-label">
							<a target="_blank" href="${civicClinicalEvidenceInstance.gene_civic_url}">${civicClinicalEvidenceInstance.gene_civic_url}</a>
						</span>
					</span>

				</li>
				</g:if>
			
			</ol>

		</div>
	</body>
</html>
