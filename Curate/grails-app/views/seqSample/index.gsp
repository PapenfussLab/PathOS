
<%@ page import="org.petermac.pathos.curate.SeqSample" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'seqSample.label', default: 'SeqSample')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-seqSample" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-seqSample" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<th><g:message code="seqSample.seqrun.label" default="Seqrun" /></th>
					
						<th><g:message code="seqSample.patSample.label" default="Pat Sample" /></th>
					
						<g:sortableColumn property="sampleName" title="${message(code: 'seqSample.sampleName.label', default: 'Sample Name')}" />
					
						<th><g:message code="seqSample.panel.label" default="Panel" /></th>
					
						<g:sortableColumn property="dnaconc" title="${message(code: 'seqSample.dnaconc.label', default: 'Dnaconc')}" />
					
						<g:sortableColumn property="analysis" title="${message(code: 'seqSample.analysis.label', default: 'Analysis')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${seqSampleInstanceList}" status="i" var="seqSampleInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${seqSampleInstance.id}">${fieldValue(bean: seqSampleInstance, field: "seqrun")}</g:link></td>
					
						<td>${fieldValue(bean: seqSampleInstance, field: "patSample")}</td>
					
						<td>${fieldValue(bean: seqSampleInstance, field: "sampleName")}</td>
					
						<td>${fieldValue(bean: seqSampleInstance, field: "panel")}</td>
					
						<td>${fieldValue(bean: seqSampleInstance, field: "dnaconc")}</td>
					
						<td>${fieldValue(bean: seqSampleInstance, field: "analysis")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${seqSampleInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
