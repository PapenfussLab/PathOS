
<%@ page import="org.petermac.pathos.curate.FilterTemplate" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'filterTemplate.label', default: 'FilterTemplate')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-filterTemplate" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-filterTemplate" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list filterTemplate">
			

			
				<g:if test="${filterTemplateInstance?.templateName}">
				<li class="fieldcontain">
					<span id="templateName-label" class="property-label"><g:message code="filterTemplate.templateName.label" default="Template Name" /></span>
					
						<span class="property-value" aria-labelledby="templateName-label"><g:fieldValue bean="${filterTemplateInstance}" field="templateName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${filterTemplateInstance?.displayName}">
				<li class="fieldcontain">
					<span id="displayName-label" class="property-label"><g:message code="filterTemplate.displayName.label" default="Display Name" /></span>
					
						<span class="property-value" aria-labelledby="displayName-label"><g:fieldValue bean="${filterTemplateInstance}" field="displayName"/></span>
					
				</li>
				</g:if>

				<g:if test="${filterTemplateInstance?.template}">
					<li class="fieldcontain">
						<span id="template-label" class="property-label"><g:message code="filterTemplate.template.label" default="Template" /></span>

						<span class="property-value" aria-labelledby="template-label"><g:fieldValue bean="${filterTemplateInstance}" field="template"/></span>

					</li>
				</g:if>
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${filterTemplateInstance?.id}" />
					<g:link class="edit" action="edit" id="${filterTemplateInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
