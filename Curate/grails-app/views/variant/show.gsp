
<%@ page import="org.petermac.pathos.curate.CurVariant" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'variant.label', default: 'CurVariant')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-variant" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-variant" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			    <div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list variant">
			
				<g:if test="${variantInstance?.variant}">
				<li class="fieldcontain">
					<span id="variant-label" class="property-label"><g:message code="seqVariant.variant.label" default="Variant" /></span>
					<span class="property-value" aria-labelledby="variant-label"><g:fieldValue bean="${variantInstance}" field="variant"/></span>
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
			
				<g:if test="${variantInstance?.pmClass}">
				<li class="fieldcontain">
					<span id="pmClass-label" class="property-label"><g:message code="variant.pmClass.label" default="Pm Class" /></span>
					<span class="property-value" aria-labelledby="pmClass-label"><g:varClass class="${variantInstance.pmClass}"/></span>
				</li>
				</g:if>
			
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

				<g:if test="${variantInstance?.reportDesc}">
				<li class="fieldcontain">
					<span id="reportDesc-label" class="property-label"><g:message code="variant.reportDesc.label" default="Report Desc" /></span>
					
						<span class="property-value" aria-labelledby="reportDesc-label"><g:fieldValue bean="${variantInstance}" field="reportDesc"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${variantInstance?.evidence}">
				<li class="fieldcontain">
					<span id="evidence-label" class="property-label"><g:message code="variant.evidence.label" default="Evidence" /></span>
					
						<span class="property-value" aria-labelledby="evidence-label">
                            <g:link controller="evidence" action="edit" id="${variantInstance?.id}">
                                <g:varClass class="${fieldValue(bean: variantInstance, field: "evidence")}"/>
                            </g:link>
                        </span>
				</li>
				</g:if>
			
				<g:if test="${variantInstance?.classified}">
				<li class="fieldcontain">
					<span id="classified-label" class="property-label"><g:message code="variant.classified.label" default="Classified" /></span>
					
						<span class="property-value" aria-labelledby="classified-label"><g:link controller="User" action="show" id="${variantInstance?.classified?.id}">${variantInstance?.classified?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${variantInstance?.authorised}">
				<li class="fieldcontain">
					<span id="authorised-label" class="property-label"><g:message code="variant.authorised.label" default="Authorised" /></span>
					
						<span class="property-value" aria-labelledby="authorised-label"><g:link controller="User" action="show" id="${variantInstance?.authorised?.id}">${variantInstance?.authorised?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
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
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${variantInstance?.id}" />
					<g:link class="edit" action="edit" id="${variantInstance?.id}">Edit Variant</g:link>
                    <g:link class="edit" controller="evidence" action="edit" id="${variantInstance?.id}">Edit Evidence</g:link>
					<g:actionSubmit class="delete"
                                    action="delete"
                                    value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                                    onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
