
<%@ page import="org.petermac.pathos.curate.Drug" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'drug.label', default: 'Drug')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-drug" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-drug" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list drug">
			
				<g:if test="${drugInstance?.alias}">
				<li class="fieldcontain">
					<span id="alias-label" class="property-label"><g:message code="drug.alias.label" default="Alias" /></span>
					
						<span class="property-value" aria-labelledby="alias-label"><g:fieldValue bean="${drugInstance}" field="alias"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${drugInstance?.approved}">
				<li class="fieldcontain">
					<span id="approved-label" class="property-label"><g:message code="drug.approved.label" default="Approved" /></span>
					
						<span class="property-value" aria-labelledby="approved-label"><g:fieldValue bean="${drugInstance}" field="approved"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${drugInstance?.approvedConditionMatch}">
				<li class="fieldcontain">
					<span id="approvedConditionMatch-label" class="property-label"><g:message code="drug.approvedConditionMatch.label" default="Approved Condition Match" /></span>
					
						<span class="property-value" aria-labelledby="approvedConditionMatch-label"><g:fieldValue bean="${drugInstance}" field="approvedConditionMatch"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${drugInstance?.approvedConditions}">
				<li class="fieldcontain">
					<span id="approvedConditions-label" class="property-label"><g:message code="drug.approvedConditions.label" default="Approved Conditions" /></span>
					
						<span class="property-value" aria-labelledby="approvedConditions-label"><g:fieldValue bean="${drugInstance}" field="approvedConditions"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${drugInstance?.badge}">
				<li class="fieldcontain">
					<span id="badge-label" class="property-label"><g:message code="drug.badge.label" default="Badge" /></span>
					
						<span class="property-value" aria-labelledby="badge-label"><g:fieldValue bean="${drugInstance}" field="badge"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${drugInstance?.brands}">
				<li class="fieldcontain">
					<span id="brands-label" class="property-label"><g:message code="drug.brands.label" default="Brands" /></span>
					
						<span class="property-value" aria-labelledby="brands-label"><g:fieldValue bean="${drugInstance}" field="brands"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${drugInstance?.contraindicatedAlterations}">
				<li class="fieldcontain">
					<span id="contraindicatedAlterations-label" class="property-label"><g:message code="drug.contraindicatedAlterations.label" default="Contraindicated Alterations" /></span>
					
						<span class="property-value" aria-labelledby="contraindicatedAlterations-label"><g:fieldValue bean="${drugInstance}" field="contraindicatedAlterations"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${drugInstance?.description}">
				<li class="fieldcontain">
					<span id="description-label" class="property-label"><g:message code="drug.description.label" default="Description" /></span>
					
						<span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${drugInstance}" field="description"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${drugInstance?.experimental}">
				<li class="fieldcontain">
					<span id="experimental-label" class="property-label"><g:message code="drug.experimental.label" default="Experimental" /></span>
					
						<span class="property-value" aria-labelledby="experimental-label"><g:fieldValue bean="${drugInstance}" field="experimental"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${drugInstance?.experimentalConditions}">
				<li class="fieldcontain">
					<span id="experimentalConditions-label" class="property-label"><g:message code="drug.experimentalConditions.label" default="Experimental Conditions" /></span>
					
						<span class="property-value" aria-labelledby="experimentalConditions-label"><g:fieldValue bean="${drugInstance}" field="experimentalConditions"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${drugInstance?.externalIds}">
				<li class="fieldcontain">
					<span id="externalIds-label" class="property-label"><g:message code="drug.externalIds.label" default="External Ids" /></span>
					
						<span class="property-value" aria-labelledby="externalIds-label"><g:fieldValue bean="${drugInstance}" field="externalIds"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${drugInstance?.molecularExperimentalTargets}">
				<li class="fieldcontain">
					<span id="molecularExperimentalTargets-label" class="property-label"><g:message code="drug.molecularExperimentalTargets.label" default="Molecular Experimental Targets" /></span>
					
						<span class="property-value" aria-labelledby="molecularExperimentalTargets-label"><g:fieldValue bean="${drugInstance}" field="molecularExperimentalTargets"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${drugInstance?.molecularTargets}">
				<li class="fieldcontain">
					<span id="molecularTargets-label" class="property-label"><g:message code="drug.molecularTargets.label" default="Molecular Targets" /></span>
					
						<span class="property-value" aria-labelledby="molecularTargets-label"><g:fieldValue bean="${drugInstance}" field="molecularTargets"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${drugInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="drug.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${drugInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${drugInstance?.status}">
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="drug.status.label" default="Status" /></span>
					
						<span class="property-value" aria-labelledby="status-label"><g:fieldValue bean="${drugInstance}" field="status"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${drugInstance?.synonyms}">
				<li class="fieldcontain">
					<span id="synonyms-label" class="property-label"><g:message code="drug.synonyms.label" default="Synonyms" /></span>
					
						<span class="property-value" aria-labelledby="synonyms-label"><g:fieldValue bean="${drugInstance}" field="synonyms"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${drugInstance?.id}" />
					<g:link class="edit" action="edit" id="${drugInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
