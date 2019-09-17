
<%@ page import="org.petermac.pathos.curate.Seqrun" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'seqrun.label', default: 'Seqrun')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-seqrun" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-seqrun" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="seqrun" title="${message(code: 'seqrun.seqrun.label', default: 'Seqrun')}" />
					
						<g:sortableColumn property="runDate" title="${message(code: 'seqrun.runDate.label', default: 'Run Date')}" />
					
						<g:sortableColumn property="platform" title="${message(code: 'seqrun.platform.label', default: 'Platform')}" />
					
						<g:sortableColumn property="sepe" title="${message(code: 'seqrun.sepe.label', default: 'Sepe')}" />
					
						<g:sortableColumn property="readlen" title="${message(code: 'seqrun.readlen.label', default: 'Readlen')}" />
					
						<g:sortableColumn property="library" title="${message(code: 'seqrun.library.label', default: 'Library')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${seqrunInstanceList}" status="i" var="seqrunInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${seqrunInstance.id}">${fieldValue(bean: seqrunInstance, field: "seqrun")}</g:link></td>
					
						<td><g:formatDate date="${seqrunInstance.runDate}" /></td>
					
						<td>${fieldValue(bean: seqrunInstance, field: "platform")}</td>
					
						<td>${fieldValue(bean: seqrunInstance, field: "sepe")}</td>
					
						<td>${fieldValue(bean: seqrunInstance, field: "readlen")}</td>
					
						<td>${fieldValue(bean: seqrunInstance, field: "library")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${seqrunInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
