
<%@ page import="org.petermac.pathos.curate.GrpVariant" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'grpVariant.label', default: 'GrpVariant')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-grpVariant" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-grpVariant" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list grpVariant">
			
				<g:if test="${grpVariantInstance?.muttyp}">
				<li class="fieldcontain">
					<span id="muttyp-label" class="property-label"><g:message code="grpVariant.muttyp.label" default="Muttyp" /></span>
					
						<span class="property-value" aria-labelledby="muttyp-label"><g:fieldValue bean="${grpVariantInstance}" field="muttyp"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${grpVariantInstance?.accession}">
				<li class="fieldcontain">
					<span id="accession-label" class="property-label"><g:message code="grpVariant.accession.label" default="Accession" /></span>
					
						<span class="property-value" aria-labelledby="accession-label"><g:fieldValue bean="${grpVariantInstance}" field="accession"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${grpVariantInstance?.createdBy}">
				<li class="fieldcontain">
					<span id="createdBy-label" class="property-label"><g:message code="grpVariant.createdBy.label" default="Created By" /></span>
					
						<span class="property-value" aria-labelledby="createdBy-label"><g:link controller="authUser" action="show" id="${grpVariantInstance?.createdBy?.id}">${grpVariantInstance?.createdBy?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${grpVariantInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="grpVariant.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${grpVariantInstance?.dateCreated}" format="dd-MMM-yyyy" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${grpVariantInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="grpVariant.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${grpVariantInstance?.lastUpdated}" format="dd-MMM-yyyy" /></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${grpVariantInstance?.id}" />
					<g:link class="edit" action="edit" id="${grpVariantInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
