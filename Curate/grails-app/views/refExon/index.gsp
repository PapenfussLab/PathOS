
<%@ page import="org.petermac.pathos.curate.RefExon" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'refExon.label', default: 'RefExon')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-refExon" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<%--<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>--%>
			</ul>
		</div>
		<div id="list-refExon" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="gene" title="${message(code: 'refExon.gene.label', default: 'Gene')}" />
					
						<g:sortableColumn property="refseq" title="${message(code: 'refExon.refseq.label', default: 'Refseq')}" />
					
						<g:sortableColumn property="exon" title="${message(code: 'refExon.exon.label', default: 'Exon')}" />
					
						<g:sortableColumn property="strand" title="${message(code: 'refExon.strand.label', default: 'Strand')}" />
					
						<g:sortableColumn property="idx" title="${message(code: 'refExon.idx.label', default: 'Idx')}" />
					
						<g:sortableColumn property="exonStart" title="${message(code: 'refExon.exonStart.label', default: 'Exon Start')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${refExonInstanceList}" status="i" var="refExonInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${refExonInstance.id}">${fieldValue(bean: refExonInstance, field: "gene")}</g:link></td>
					
						<td>${fieldValue(bean: refExonInstance, field: "refseq")}</td>
					
						<td>${fieldValue(bean: refExonInstance, field: "exon")}</td>
					
						<td>${fieldValue(bean: refExonInstance, field: "strand")}</td>
					
						<td>${fieldValue(bean: refExonInstance, field: "idx")}</td>
					
						<td>${fieldValue(bean: refExonInstance, field: "exonStart")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${refExonInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
