
<%@ page import="org.petermac.pathos.curate.Roi" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'roi.label', default: 'Roi')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-roi" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-roi" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<th><g:message code="roi.panel.label" default="Panel" /></th>
					
						<g:sortableColumn property="name" title="${message(code: 'roi.name.label', default: 'Name')}" />
					
						<g:sortableColumn property="gene" title="${message(code: 'roi.gene.label', default: 'Gene')}" />
					
						<g:sortableColumn property="exon" title="${message(code: 'roi.exon.label', default: 'Exon')}" />
					
						<g:sortableColumn property="chr" title="${message(code: 'roi.chr.label', default: 'Chr')}" />
					
						<g:sortableColumn property="startPos" title="${message(code: 'roi.startPos.label', default: 'Start Pos')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${roiInstanceList}" status="i" var="roiInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${roiInstance.id}">${fieldValue(bean: roiInstance, field: "panel")}</g:link></td>
					
						<td>${fieldValue(bean: roiInstance, field: "name")}</td>
					
						<td>${fieldValue(bean: roiInstance, field: "gene")}</td>
					
						<td>${fieldValue(bean: roiInstance, field: "exon")}</td>
					
						<td>${fieldValue(bean: roiInstance, field: "chr")}</td>
					
						<td>${fieldValue(bean: roiInstance, field: "startPos")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${roiInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
