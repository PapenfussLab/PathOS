
<%@ page import="org.petermac.pathos.curate.Drug" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'drug.label', default: 'Drug')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-drug" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-drug" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="alias" title="${message(code: 'drug.alias.label', default: 'Alias')}" />
					
						<g:sortableColumn property="approved" title="${message(code: 'drug.approved.label', default: 'Approved')}" />
					
						<g:sortableColumn property="approvedConditionMatch" title="${message(code: 'drug.approvedConditionMatch.label', default: 'Approved Condition Match')}" />
					
						<g:sortableColumn property="approvedConditions" title="${message(code: 'drug.approvedConditions.label', default: 'Approved Conditions')}" />
					
						<g:sortableColumn property="badge" title="${message(code: 'drug.badge.label', default: 'Badge')}" />
					
						<g:sortableColumn property="brands" title="${message(code: 'drug.brands.label', default: 'Brands')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${drugInstanceList}" status="i" var="drugInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${drugInstance.id}">${fieldValue(bean: drugInstance, field: "alias")}</g:link></td>
					
						<td>${fieldValue(bean: drugInstance, field: "approved")}</td>
					
						<td>${fieldValue(bean: drugInstance, field: "approvedConditionMatch")}</td>
					
						<td>${fieldValue(bean: drugInstance, field: "approvedConditions")}</td>
					
						<td>${fieldValue(bean: drugInstance, field: "badge")}</td>
					
						<td>${fieldValue(bean: drugInstance, field: "brands")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${drugInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
