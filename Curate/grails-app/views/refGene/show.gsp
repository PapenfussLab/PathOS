
<%@ page import="org.petermac.pathos.curate.RefGene" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'refGene.label', default: 'RefGene')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-refGene" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			<%--	<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li> --%>
			</ul>
		</div>
		<div id="show-refGene" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list refGene">
			
				<g:if test="${refGeneInstance?.gene}">
				<li class="fieldcontain">
					<span id="gene-label" class="property-label"><g:message code="refGene.gene.label" default="Gene" /></span>
					
						<span class="property-value" aria-labelledby="gene-label"><g:fieldValue bean="${refGeneInstance}" field="gene"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${refGeneInstance?.hgncid}">
				<li class="fieldcontain">
					<span id="hgncid-label" class="property-label"><g:message code="refGene.hgncid.label" default="Hgncid" /></span>
					
						<span class="property-value" aria-labelledby="hgncid-label"><g:fieldValue bean="${refGeneInstance}" field="hgncid"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${refGeneInstance?.accession}">
				<li class="fieldcontain">
					<span id="accession-label" class="property-label"><g:message code="refGene.accession.label" default="Accession" /></span>
					
						<span class="property-value" aria-labelledby="accession-label"><g:fieldValue bean="${refGeneInstance}" field="accession"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${refGeneInstance?.genedesc}">
				<li class="fieldcontain">
					<span id="genedesc-label" class="property-label"><g:message code="refGene.genedesc.label" default="Genedesc" /></span>
					
						<span class="property-value" aria-labelledby="genedesc-label"><g:fieldValue bean="${refGeneInstance}" field="genedesc"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${refGeneInstance?.refseq}">
				<li class="fieldcontain">
					<span id="refseq-label" class="property-label"><g:message code="refGene.refseq.label" default="Refseq" /></span>
					
						<span class="property-value" aria-labelledby="refseq-label"><g:fieldValue bean="${refGeneInstance}" field="refseq"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${refGeneInstance?.id}" />
					<g:link class="edit" action="edit" id="${refGeneInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
				<%--	PATHOS-493 DISABLING DELETE
				    <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				--%>
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
