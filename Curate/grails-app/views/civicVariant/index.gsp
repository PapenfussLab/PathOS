
<%@ page import="org.petermac.pathos.curate.CivicVariant" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'civicVariant.label', default: 'CivicVariant')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-civicVariant" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-civicVariant" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="variant_civic_url" title="${message(code: 'civicVariant.variant_civic_url.label', default: 'Variantcivicurl')}" />
					
						<g:sortableColumn property="gene" title="${message(code: 'civicVariant.gene.label', default: 'Gene')}" />
					
						<g:sortableColumn property="entrez_id" title="${message(code: 'civicVariant.entrez_id.label', default: 'Entrezid')}" />
					
						<g:sortableColumn property="variant" title="${message(code: 'civicVariant.variant.label', default: 'Variant')}" />
					
						<g:sortableColumn property="summary" title="${message(code: 'civicVariant.summary.label', default: 'Summary')}" />
					
						<g:sortableColumn property="variant_groups" title="${message(code: 'civicVariant.variant_groups.label', default: 'Variantgroups')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${civicVariantInstanceList}" status="i" var="civicVariantInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${civicVariantInstance.id}">${fieldValue(bean: civicVariantInstance, field: "variant_civic_url")}</g:link></td>
					
						<td>${fieldValue(bean: civicVariantInstance, field: "gene")}</td>
					
						<td>${fieldValue(bean: civicVariantInstance, field: "entrez_id")}</td>
					
						<td>${fieldValue(bean: civicVariantInstance, field: "variant")}</td>
					
						<td>${fieldValue(bean: civicVariantInstance, field: "summary")}</td>
					
						<td>${fieldValue(bean: civicVariantInstance, field: "variant_groups")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${civicVariantInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
