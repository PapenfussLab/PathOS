
<%@ page import="org.petermac.pathos.curate.EvidenceService; grails.converters.JSON; org.petermac.pathos.curate.CurVariant; org.petermac.pathos.curate.SeqVariant" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'variant.label', default: 'CurVariant')}" />
		<title>${variantInstance} - <g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-curVariant" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<nav class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="edit" action="edit" id="${variantInstance.id}">Edit Variant</g:link></li>
				<g:render template="deleteButton" model="[id: variantInstance.id]"/>
			</ul>
		</nav>
		<div id="show-curVariant" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			    <div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list variant">
				<li class="fieldcontain">
					<span id="variant-label" class="property-label">Variant</span>
					<span class="property-value" aria-labelledby="variant-label">${variantInstance}</span>
				</li>
				<li class="fieldcontain">
					<span id="clinContext-label" class="property-label">Clinical Context</span>
					<span class="property-value" aria-labelledby="clinContext-label"><b>${variantInstance.clinContext}</b>
- <a href="#show-all-contexts" onclick='PathOS.variant.viewer({hgvsg:"${variantInstance.hgvsg}", contextCode:"${variantInstance.clinContext.code}"})'>Inspect other Clinical Contexts</a></span>
				</li>
				<li class="fieldcontain">
					<span id="group-variant-label" class="property-label">Group Variant</span>
					<span class="property-value" aria-labelledby="group-variant-label"><b>${variantInstance.grpVariant? variantInstance.grpVariant : 'None'}</b></span>
				</li>
				<li class="fieldcontain">
					<span id="originating-variant-label" class="property-label">Originating Variant</span>
					<span class="property-value" aria-labelledby="originating-variant-label">
						<g:if test="${originating}">
							<g:link controller='seqVariant' action='svlist' id='${originating?.seqSample?.id}'>${originating?.seqSample.sampleName} : ${originating.seqSample.seqrun} : ${originating}</g:link>
						</g:if>
						<g:else>
							None
						</g:else>
				</li>


				<g:if test="${variantInstance?.variant}">
				<li class="fieldcontain">
					<span id="seqVariant-label" class="property-label"><g:message code="seqVariant.variant.label" default="Variant" /></span>
					<span class="property-value" aria-labelledby="seqVariant-label"><g:fieldValue bean="${variantInstance}" field="variant"/></span>
				</li>
				</g:if>
			
				<g:if test="${variantInstance?.gene}">
				<li class="fieldcontain">
					<span id="gene-label" class="property-label"><g:message code="seqVariant.gene.label" default="Gene" /></span>
					<span class="property-value" aria-labelledby="gene-label"><g:fieldValue bean="${variantInstance}" field="gene"/></span>
				</li>
				</g:if>
			
				<g:if test="${variantInstance?.gene_type}">
				<li class="fieldcontain">
					<span id="gene_type-label" class="property-label"><g:message code="variant.gene_type.label" default="Gene Type" /></span>
					<span class="property-value" aria-labelledby="gene_type-label"><g:fieldValue bean="${variantInstance}" field="gene_type"/></span>
				</li>
				</g:if>
			
				<g:if test="${variantInstance?.hgvsc}">
				<li class="fieldcontain">
					<span id="hgvsc-label" class="property-label"><g:message code="seqVariant.hgvsc.label" default="Hgvsc" /></span>
					<span class="property-value" aria-labelledby="hgvsc-label"><g:fieldValue bean="${variantInstance}" field="hgvsc"/></span>
				</li>
				</g:if>
			
				<g:if test="${variantInstance?.hgvsp}">
				<li class="fieldcontain">
					<span id="hgvsp-label" class="property-label"><g:message code="seqVariant.hgvsp.label" default="Hgvsp" /></span>
					<span class="property-value" aria-labelledby="hgvsp-label"><g:fieldValue bean="${variantInstance}" field="hgvsp"/></span>
				</li>
				</g:if>

				<g:if test="${variantInstance?.consequence}">
				<li class="fieldcontain">
					<span id="consequence-label" class="property-label"><g:message code="seqVariant.consequence.label" default="Consequence" /></span>
					<span class="property-value" aria-labelledby="consequence-label"><g:fieldValue bean="${variantInstance}" field="consequence"/></span>
				</li>
				</g:if>

				<li class="fieldcontain">
					<span id="overallClass-label" class="property-label">Clinical Significance</span>
					<span class="property-value" aria-labelledby="overallClass-label">${variantInstance?.overallClass ?: 'Unclassified'}</span>
				</li>

				<li class="fieldcontain">
					<span id="overallReason-label" class="property-label">Clinical Significance Override Reason</span>
					<span class="property-value" aria-labelledby="classOverrideReason-label">${variantInstance?.overallReason ?: 'Unclassified'}</span>
				</li>

				<li class="fieldcontain">
					<span id="pmClass-label" class="property-label">ACMG Classification</span>
					<span class="property-value" aria-labelledby="pmClass-label"><g:varClass class="${variantInstance?.pmClass}"/></span>
				</li>

				<li class="fieldcontain">
					<span id="classOverrideReason-label" class="property-label">ACMG Classification Override Reason</span>
					<span class="property-value" aria-labelledby="classOverrideReason-label">${variantInstance?.classOverrideReason ?: "No override - Calculated from Evidence"}</span>
				</li>

				<li class="fieldcontain">
					<span id="ampClass-label" class="property-label">AMP Classification</span>
					<span class="property-value" aria-labelledby="ampClass-label">${variantInstance?.ampClass}</span>
				</li>

				<li class="fieldcontain">
					<span id="ampReason-label" class="property-label">AMP Classification Override Reason</span>
					<span class="property-value" aria-labelledby="ampReason-label">${variantInstance?.ampReason ?: "No override - Calculated from Evidence"}</span>
				</li>

				<g:if test="${variantInstance?.alamutClass}">
				<li class="fieldcontain">
					<span id="alamutClass-label" class="property-label"><g:message code="seqVariant.alamutClass.label" default="Alamut Class" /></span>
					<span class="property-value" aria-labelledby="alamutClass-label"><g:varClass class="${variantInstance.alamutClass}"/></span>
				</li>
				</g:if>
			
				<g:if test="${variantInstance?.siftCat}">
				<li class="fieldcontain">
					<span id="siftClass-label" class="property-label"><g:message code="seqVariant.siftClass.label" default="Sift Class" /></span>
					<span class="property-value" aria-labelledby="siftClass-label"><g:fieldValue bean="${variantInstance}" field="siftCat"/></span>
				</li>
				</g:if>
			
				<g:if test="${variantInstance?.polyphenCat}">
				<li class="fieldcontain">
					<span id="polyphenClass-label" class="property-label"><g:message code="seqVariant.polyphenClass.label" default="Polyphen Class" /></span>
					<span class="property-value" aria-labelledby="polyphenClass-label"><g:fieldValue bean="${variantInstance}" field="polyphenCat"/></span>
				</li>
				</g:if>
			
				<g:if test="${variantInstance?.gene_pathway}">
				<li class="fieldcontain">
					<span id="gene_pathway-label" class="property-label"><g:message code="variant.gene_pathway.label" default="Genepathway" /></span>
					<span class="property-value" aria-labelledby="gene_pathway-label"><g:fieldValue bean="${variantInstance}" field="gene_pathway"/></span>
				</li>
				</g:if>
			
				<g:if test="${variantInstance?.gene_process}">
				<li class="fieldcontain">
					<span id="gene_process-label" class="property-label"><g:message code="variant.gene_process.label" default="Geneprocess" /></span>
					<span class="property-value" aria-labelledby="gene_process-label"><g:fieldValue bean="${variantInstance}" field="gene_process"/></span>
				</li>
				</g:if>
			
				<g:if test="${variantInstance?.chr}">
				<li class="fieldcontain">
					<span id="chr-label" class="property-label"><g:message code="seqVariant.chr.label" default="Chr" /></span>
                    <span class="property-value" aria-labelledby="chr-label">
                        ${variantInstance?.chr}:${variantInstance?.pos} ${variantInstance?.exon}
                    </span>
				</li>
				</g:if>
			
				<g:if test="${variantInstance?.cosmic}">
				<li class="fieldcontain">
					<span id="cosmic-label" class="property-label"><g:message code="seqVariant.cosmic.label" default="Cosmic" /></span>
					<span class="property-value" aria-labelledby="cosmic-label"><g:cosmicUrl cosmic="${variantInstance.cosmic}"/></span>
				</li>
				</g:if>


                <g:if test="${variantInstance?.gene}">
                <li class="fieldcontain">
                    <span id="histogram" class="property-label"><g:message code="variant.histogram.label" default="Histogram" /></span>

                    <span class="property-value" aria-labelledby="histogram">
                        <g:cosmicHistoUrl gene="${variantInstance.gene}" hgvsp="${variantInstance.hgvsp}"/>
                    </span>

                </li>
                </g:if>

				<g:if test="${variantInstance?.dbsnp}">
				<li class="fieldcontain">
					<span id="dbsnp-label" class="property-label"><g:message code="seqVariant.dbsnp.label" default="dbSNP" /></span>
					<span class="property-value" aria-labelledby="dbsnp-label"><g:dbsnpUrl  dbsnp="${variantInstance.dbsnp}"/></span>
				</li>
				</g:if>

				<li class="fieldcontain">
					<span id="alamutURL-label" class="property-label"><g:message code="variant.alamutURL.label" default="Alamut" /></span>
                    <span class="property-value" aria-labelledby="alamutURL-label"><g:alamutUrl chr="${variantInstance.chr}" pos="${variantInstance.pos}"/></span>
                </li>

				<li class="fieldcontain">
					<span id="ucscURL-label" class="property-label"><g:message code="variant.ucscURL.label" default="UCSC" /></span>
                    <span class="property-value" aria-labelledby="ucscURL-label"><g:ucscUrl chr="${variantInstance.chr}" pos="${variantInstance.pos}"/></span>
                </li>

				<li class="fieldcontain">
					<span id="reportDesc-label" style="margin-right: 20px" class="property-label">Report Description</span>
					<textarea readonly style="margin-left: 0" class="highlightClass property-value" aria-labelledby="reportDesc-label"><g:fieldValue bean="${variantInstance}" field="reportDesc"/></textarea>
				</li>

				<li class="fieldcontain">
					<span id="evidence-label" class="property-label">Calculated ACMG Classification</span>
					
					<span class="property-value" aria-labelledby="evidence-label">
						<g:varClass class="${variantInstance.fetchAcmgEvidence().classification}"/>
					</span>
				</li>

				<li class="fieldcontain">
					<span id="evidenceDesc-label" style="margin-right: 20px" class="property-label">ACMG Evidence Description</span>
					<textarea readonly style="margin-left: 0" class="highlightClass property-value" aria-labelledby="evidenceDesc-label">${ EvidenceService.extractAcmgJustification(variantInstance.fetchAcmgEvidence()?.acmgJustification) }</textarea>
				</li>

				<li class="fieldcontain">
					<span id="amp-evidence-label" class="property-label">Calculated AMP Classification</span>
					<span class="property-value" aria-labelledby="amp-evidence-label">
						${variantInstance.fetchAmpEvidence()?.classification}
					</span>
				</li>
				<li class="fieldcontain">
					<span id="ampEvidenceDesc-label" style="margin-right: 20px" class="property-label">AMP Evidence Description</span>
					<textarea readonly style="margin-left: 0" class="highlightClass property-value" aria-labelledby="ampEvidenceDesc-label">${ variantInstance.fetchAmpEvidence()?.ampJustification }</textarea>
				</li>

				<li class="fieldcontain">
					<span id="pmid-label" class="property-label">Linked Pubmed Articles</span>
					<span class="property-value" aria-labelledby="pmid-label"><ul><g:displayPMIDs cv="${variantInstance.id}"/></ul></span>
				</li>

				<g:if test="${variantInstance?.classified}">
				<li class="fieldcontain">
					<span id="classified-label" class="property-label"><g:message code="variant.classified.label" default="Classified" /></span>
					
						<span class="property-value" aria-labelledby="classified-label"><g:link controller="User" action="show" id="${variantInstance?.classified?.id}">${variantInstance?.classified?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>

				<li class="fieldcontain">
					<span id="authorisedFlag-label" class="property-label">Authorised</span>

					<span class="property-value" aria-labelledby="authorised-label">${variantInstance?.authorisedFlag ? "Authorised" : "Not Authorised" }</span>

				</li>

				<li class="fieldcontain">
					<span id="authorised-label" class="property-label">Authorisor</span>
					<g:if test="${variantInstance?.authorised}">
					<span class="property-value" aria-labelledby="authorised-label"><g:link controller="User" action="show" id="${variantInstance?.authorised?.id}">${variantInstance?.authorised?.encodeAsHTML()}</g:link></span>
					</g:if>
				</li>

				<li class="fieldcontain">
					<span id="lastAuthorised-label" class="property-label">Date of last Authorisation</span>

					<span class="property-value" aria-labelledby="lastAuthorised-label"><g:formatDate date="${variantInstance?.lastAuthorised}" /></span>
				</li>

				<g:if test="${variantInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="variant.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${variantInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${variantInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="variant.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${variantInstance?.lastUpdated}" /></span>
					
				</li>

					<g:if test="${jiraIssues}">
						<g:each in="${jiraIssues}" var="issue">
							<li class="fieldcontain">
								<span id="jiraIssue-label" class="property-label"><g:message code="variant.jiraissue.label" default="JIRA Issue" /></span>

								<span class="property-value" aria-labelledby="jiraIssue-label"><a target="_blank" href="${jiraAddress}/jira/browse/${issue.issueIdentifier}">${issue.issueIdentifier}</a> - ${issue.issueType}</span>

							</li>
						</g:each></g:if>
				</g:if>
				<g:showPageTags/>
			</ol>
		</div>
<script>
	console.log("${variantInstance?.id}")
	<g:showPageTagsScript tags="${variantInstance?.tags as grails.converters.JSON}" id="${variantInstance?.id}" controller="curvariant"/>



%{--
DKGM 24-November-2016
Ajax stuff so that we can show all of the Linked Sequenced Variants on demand
--}%

	var allSeqVariantsLoaded = false;
	function toggleLinkedSeqVariants(){
		$("#linked-seqVariants").toggleClass("hidden");

		if(!allSeqVariantsLoaded) {
			allSeqVariantsLoaded = true;
			$.ajax({
				type: "GET",
				url: "<g:context/>/curVariant/linkedSeqVars/?id="+${variantInstance?.id},
				success: function(d){
						d3.select("#slow-load-warning").classed("hidden", true);

						var list = d3.select("#linked-seqVariants");
						d.sort(function(a,b){
							return b.seqSample - a.seqSample;
						}).forEach(function(ss){
							list.append("li")
								.classed("property-value", true)
								.append("b")
								.append("a")
								.attr("href", "<g:context/>/seqVariant/svlist/"+ss.seqSample)
								.text(ss.string);
						});
					},
				cache: false,
				contentType: false,
				processData: false
			});
		}
	}

	PathOS.pubmed.applyHighlight('highlightClass');

</script>
	</body>
</html>
































