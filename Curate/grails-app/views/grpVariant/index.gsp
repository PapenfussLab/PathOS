
<%@ page import="org.petermac.pathos.curate.GrpVariant" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'grpVariant.label', default: 'GrpVariant')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-grpVariant" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-grpVariant" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="muttyp" title="${message(code: 'grpVariant.muttyp.label', default: 'Muttyp')}" />
					
						<g:sortableColumn property="accession" title="${message(code: 'grpVariant.accession.label', default: 'Accession')}" />
					
						<th><g:message code="grpVariant.createdBy.label" default="Created By" /></th>
					
						<g:sortableColumn property="dateCreated" title="${message(code: 'grpVariant.dateCreated.label', default: 'Date Created')}" />
					
						<g:sortableColumn property="lastUpdated" title="${message(code: 'grpVariant.lastUpdated.label', default: 'Last Updated')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${grpVariantInstanceList}" status="i" var="grpVariantInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${grpVariantInstance.id}">${fieldValue(bean: grpVariantInstance, field: "muttyp")}</g:link></td>
					
						<td>${fieldValue(bean: grpVariantInstance, field: "accession")}</td>
					
						<td>${fieldValue(bean: grpVariantInstance, field: "createdBy")}</td>
					
						<td><g:formatDate date="${grpVariantInstance.dateCreated}" /></td>
					
						<td><g:formatDate date="${grpVariantInstance.lastUpdated}" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${grpVariantInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
