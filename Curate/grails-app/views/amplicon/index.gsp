
<%@ page import="org.petermac.pathos.curate.Amplicon" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'amplicon.label', default: 'Amplicon')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-amplicon" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<%--<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>--%>
			</ul>
		</div>
		<div id="list-amplicon" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="panel" title="${message(code: 'amplicon.panel.label', default: 'Panel')}" />
					
						<g:sortableColumn property="amplicon" title="${message(code: 'amplicon.amplicon.label', default: 'Amplicon')}" />
					
						<g:sortableColumn property="chr" title="${message(code: 'amplicon.chr.label', default: 'Chr')}" />
					
						<g:sortableColumn property="startpos" title="${message(code: 'amplicon.startpos.label', default: 'Startpos')}" />
					
						<g:sortableColumn property="endpos" title="${message(code: 'amplicon.endpos.label', default: 'Endpos')}" />
					
						<g:sortableColumn property="primerlen1" title="${message(code: 'amplicon.primerlen1.label', default: 'Primerlen1')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${ampliconInstanceList}" status="i" var="ampliconInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${ampliconInstance.id}">${fieldValue(bean: ampliconInstance, field: "panel")}</g:link></td>
					
						<td>${fieldValue(bean: ampliconInstance, field: "amplicon")}</td>
					
						<td>${fieldValue(bean: ampliconInstance, field: "chr")}</td>
					
						<td>${fieldValue(bean: ampliconInstance, field: "startpos")}</td>
					
						<td>${fieldValue(bean: ampliconInstance, field: "endpos")}</td>
					
						<td>${fieldValue(bean: ampliconInstance, field: "primerlen1")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${ampliconInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
