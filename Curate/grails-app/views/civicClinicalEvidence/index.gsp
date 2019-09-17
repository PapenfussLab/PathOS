
<%@ page import="org.petermac.pathos.curate.CivicClinicalEvidence" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'civicClinicalEvidence.label', default: 'CivicClinicalEvidence')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-civicClinicalEvidence" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-civicClinicalEvidence" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="variant_id" title="${message(code: 'civicClinicalEvidence.variant_id.label', default: 'Variantid')}" />
					
						<g:sortableColumn property="variant" title="${message(code: 'civicClinicalEvidence.variant.label', default: 'Variant')}" />
					
						<g:sortableColumn property="disease" title="${message(code: 'civicClinicalEvidence.disease.label', default: 'Disease')}" />
					
						<g:sortableColumn property="doid" title="${message(code: 'civicClinicalEvidence.doid.label', default: 'Doid')}" />
					
						<g:sortableColumn property="drugs" title="${message(code: 'civicClinicalEvidence.drugs.label', default: 'Drugs')}" />
					
						<g:sortableColumn property="evidence_id" title="${message(code: 'civicClinicalEvidence.entrez_id.label', default: 'Evidenceid')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${civicClinicalEvidenceInstanceList}" status="i" var="civicClinicalEvidenceInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${civicClinicalEvidenceInstance.id}">${fieldValue(bean: civicClinicalEvidenceInstance, field: "variant_id")}</g:link></td>
					
						<td>${fieldValue(bean: civicClinicalEvidenceInstance, field: "variant")}</td>
					
						<td>${fieldValue(bean: civicClinicalEvidenceInstance, field: "disease")}</td>
					
						<td>${fieldValue(bean: civicClinicalEvidenceInstance, field: "doid")}</td>
					
						<td>${fieldValue(bean: civicClinicalEvidenceInstance, field: "drugs")}</td>
					
						<td>${fieldValue(bean: civicClinicalEvidenceInstance, field: "evidence_id")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${civicClinicalEvidenceInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
