
<%@ page import="org.petermac.pathos.curate.IcdO" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'icdO.label', default: 'IcdO')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-icdO" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-icdO" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="histCode" title="${message(code: 'icdO.histCode.label', default: 'Hist Code')}" />
					
						<g:sortableColumn property="histDetail" title="${message(code: 'icdO.histDetail.label', default: 'Hist Detail')}" />
					
						<g:sortableColumn property="histDetailCode" title="${message(code: 'icdO.histDetailCode.label', default: 'Hist Detail Code')}" />
					
						<g:sortableColumn property="histology" title="${message(code: 'icdO.histology.label', default: 'Histology')}" />
					
						<g:sortableColumn property="site" title="${message(code: 'icdO.site.label', default: 'Site')}" />
					
						<g:sortableColumn property="siteCode" title="${message(code: 'icdO.siteCode.label', default: 'Site Code')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${icdOInstanceList}" status="i" var="icdOInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${icdOInstance.id}">${fieldValue(bean: icdOInstance, field: "histCode")}</g:link></td>
					
						<td>${fieldValue(bean: icdOInstance, field: "histDetail")}</td>
					
						<td>${fieldValue(bean: icdOInstance, field: "histDetailCode")}</td>
					
						<td>${fieldValue(bean: icdOInstance, field: "histology")}</td>
					
						<td>${fieldValue(bean: icdOInstance, field: "site")}</td>
					
						<td>${fieldValue(bean: icdOInstance, field: "siteCode")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${icdOInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
