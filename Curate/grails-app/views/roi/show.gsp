
<%@ page import="org.petermac.pathos.curate.Roi" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'roi.label', default: 'Roi')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-roi" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-roi" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list roi">
			
				<g:if test="${roiInstance?.panel}">
				<li class="fieldcontain">
					<span id="panel-label" class="property-label"><g:message code="roi.panel.label" default="Panel" /></span>
					
						<span class="property-value" aria-labelledby="panel-label"><g:link controller="panel" action="show" id="${roiInstance?.panel?.id}">${roiInstance?.panel?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${roiInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="roi.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${roiInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${roiInstance?.gene}">
				<li class="fieldcontain">
					<span id="gene-label" class="property-label"><g:message code="roi.gene.label" default="Gene" /></span>
					
						<span class="property-value" aria-labelledby="gene-label"><g:fieldValue bean="${roiInstance}" field="gene"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${roiInstance?.exon}">
				<li class="fieldcontain">
					<span id="exon-label" class="property-label"><g:message code="roi.exon.label" default="Exon" /></span>
					
						<span class="property-value" aria-labelledby="exon-label"><g:fieldValue bean="${roiInstance}" field="exon"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${roiInstance?.chr}">
				<li class="fieldcontain">
					<span id="chr-label" class="property-label"><g:message code="roi.chr.label" default="Chr" /></span>
					
						<span class="property-value" aria-labelledby="chr-label"><g:fieldValue bean="${roiInstance}" field="chr"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${roiInstance?.startPos}">
				<li class="fieldcontain">
					<span id="startPos-label" class="property-label"><g:message code="roi.startPos.label" default="Start Pos" /></span>
					
						<span class="property-value" aria-labelledby="startPos-label"><g:fieldValue bean="${roiInstance}" field="startPos"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${roiInstance?.endPos}">
				<li class="fieldcontain">
					<span id="endPos-label" class="property-label"><g:message code="roi.endPos.label" default="End Pos" /></span>
					
						<span class="property-value" aria-labelledby="endPos-label"><g:fieldValue bean="${roiInstance}" field="endPos"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${roiInstance?.amplicons}">
				<li class="fieldcontain">
					<span id="amplicons-label" class="property-label"><g:message code="roi.amplicons.label" default="Amplicons" /></span>
					
						<span class="property-value" aria-labelledby="amplicons-label"><g:fieldValue bean="${roiInstance}" field="amplicons"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${roiInstance?.manifestName}">
				<li class="fieldcontain">
					<span id="manifestName-label" class="property-label"><g:message code="roi.manifestName.label" default="Manifest Name" /></span>
					
						<span class="property-value" aria-labelledby="manifestName-label"><g:fieldValue bean="${roiInstance}" field="manifestName"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${roiInstance?.id}" />
					<g:link class="edit" action="edit" id="${roiInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
