
<%@ page import="org.petermac.pathos.curate.RefExon" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'refExon.label', default: 'RefExon')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-refExon" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<%-- <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li> --%>
			</ul>
		</div>
		<div id="show-refExon" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list refExon">
			
				<g:if test="${refExonInstance?.gene}">
				<li class="fieldcontain">
					<span id="gene-label" class="property-label"><g:message code="refExon.gene.label" default="Gene" /></span>
					
						<span class="property-value" aria-labelledby="gene-label"><g:fieldValue bean="${refExonInstance}" field="gene"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${refExonInstance?.refseq}">
				<li class="fieldcontain">
					<span id="refseq-label" class="property-label"><g:message code="refExon.refseq.label" default="Refseq" /></span>
					
						<span class="property-value" aria-labelledby="refseq-label"><g:fieldValue bean="${refExonInstance}" field="refseq"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${refExonInstance?.exon}">
				<li class="fieldcontain">
					<span id="exon-label" class="property-label"><g:message code="refExon.exon.label" default="Exon" /></span>
					
						<span class="property-value" aria-labelledby="exon-label"><g:fieldValue bean="${refExonInstance}" field="exon"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${refExonInstance?.strand}">
				<li class="fieldcontain">
					<span id="strand-label" class="property-label"><g:message code="refExon.strand.label" default="Strand" /></span>
					
						<span class="property-value" aria-labelledby="strand-label"><g:fieldValue bean="${refExonInstance}" field="strand"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${refExonInstance?.idx}">
				<li class="fieldcontain">
					<span id="idx-label" class="property-label"><g:message code="refExon.idx.label" default="Idx" /></span>
					
						<span class="property-value" aria-labelledby="idx-label"><g:fieldValue bean="${refExonInstance}" field="idx"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${refExonInstance?.exonStart}">
				<li class="fieldcontain">
					<span id="exonStart-label" class="property-label"><g:message code="refExon.exonStart.label" default="Exon Start" /></span>
					
						<span class="property-value" aria-labelledby="exonStart-label"><g:fieldValue bean="${refExonInstance}" field="exonStart"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${refExonInstance?.exonEnd}">
				<li class="fieldcontain">
					<span id="exonEnd-label" class="property-label"><g:message code="refExon.exonEnd.label" default="Exon End" /></span>
					
						<span class="property-value" aria-labelledby="exonEnd-label"><g:fieldValue bean="${refExonInstance}" field="exonEnd"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${refExonInstance?.exonFrame}">
				<li class="fieldcontain">
					<span id="exonFrame-label" class="property-label"><g:message code="refExon.exonFrame.label" default="Exon Frame" /></span>
					
						<span class="property-value" aria-labelledby="exonFrame-label"><g:fieldValue bean="${refExonInstance}" field="exonFrame"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<%--<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${refExonInstance?.id}" />
					<g:link class="edit" action="edit" id="${refExonInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>--%>
		</div>
	</body>
</html>
