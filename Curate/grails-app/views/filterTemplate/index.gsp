
<%@ page import="org.petermac.pathos.curate.FilterTemplate" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'filterTemplate.label', default: 'FilterTemplate')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-filterTemplate" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-filterTemplate" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="template" title="${message(code: 'filterTemplate.template.label', default: 'Template')}" />
					
						<g:sortableColumn property="templateName" title="${message(code: 'filterTemplate.templateName.label', default: 'Template Name')}" />
					
						<g:sortableColumn property="displayName" title="${message(code: 'filterTemplate.displayName.label', default: 'Display Name')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${filterTemplateInstanceList}" status="i" var="filterTemplateInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${filterTemplateInstance.id}">${fieldValue(bean: filterTemplateInstance, field: "template")}</g:link></td>
					
						<td>${fieldValue(bean: filterTemplateInstance, field: "templateName")}</td>
					
						<td>${fieldValue(bean: filterTemplateInstance, field: "displayName")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${filterTemplateInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
