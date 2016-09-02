
<%@ page import="org.petermac.pathos.curate.Transcript" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'transcript.label', default: 'Transcript')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-transcript" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-transcript" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="accession" title="${message(code: 'transcript.accession.label', default: 'Accession')}" />
					
						<g:sortableColumn property="build" title="${message(code: 'transcript.build.label', default: 'Build')}" />
					
						<g:sortableColumn property="cds_size" title="${message(code: 'transcript.cds_size.label', default: 'Cdssize')}" />
					
						<g:sortableColumn property="cds_start" title="${message(code: 'transcript.cds_start.label', default: 'Cdsstart')}" />
					
						<g:sortableColumn property="cds_stop" title="${message(code: 'transcript.cds_stop.label', default: 'Cdsstop')}" />
					
						<g:sortableColumn property="chr_refseq" title="${message(code: 'transcript.chr_refseq.label', default: 'Chrrefseq')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${transcriptInstanceList}" status="i" var="transcriptInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${transcriptInstance.id}">${fieldValue(bean: transcriptInstance, field: "accession")}</g:link></td>
					
						<td>${fieldValue(bean: transcriptInstance, field: "build")}</td>
					
						<td>${fieldValue(bean: transcriptInstance, field: "cds_size")}</td>
					
						<td>${fieldValue(bean: transcriptInstance, field: "cds_start")}</td>
					
						<td>${fieldValue(bean: transcriptInstance, field: "cds_stop")}</td>
					
						<td>${fieldValue(bean: transcriptInstance, field: "chr_refseq")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${transcriptInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
