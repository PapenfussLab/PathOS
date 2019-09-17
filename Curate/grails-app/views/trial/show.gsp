
<%@ page import="org.petermac.pathos.curate.Trial" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'trial.label', default: 'Trial')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-trial" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-trial" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list trial">
			
				<g:if test="${trialInstance?.study}">
				<li class="fieldcontain">
					<span id="study-label" class="property-label"><g:message code="trial.study.label" default="Study Id" /></span>
					
						<span class="property-value" aria-labelledby="study-label"><g:fieldValue bean="${trialInstance}" field="study"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${trialInstance?.briefTitle}">
				<li class="fieldcontain">
					<span id="briefTitle-label" class="property-label"><g:message code="trial.briefTitle.label" default="Brief Title" /></span>
					
						<span class="property-value" aria-labelledby="briefTitle-label"><g:fieldValue bean="${trialInstance}" field="briefTitle"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${trialInstance?.molecularAlterations}">
				<li class="fieldcontain">
					<span id="molecularAlterations-label" class="property-label"><g:message code="trial.molecularAlterations.label" default="Molecular Alterations" /></span>
					
						<span class="property-value" aria-labelledby="molecularAlterations-label"><g:fieldValue bean="${trialInstance}" field="molecularAlterations"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${trialInstance?.status}">
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="trial.status.label" default="Status" /></span>
					
						<span class="property-value" aria-labelledby="status-label"><g:fieldValue bean="${trialInstance}" field="status"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${trialInstance?.startDate}">
				<li class="fieldcontain">
					<span id="startDate-label" class="property-label"><g:message code="trial.startDate.label" default="Start Date" /></span>
					
						<span class="property-value" aria-labelledby="startDate-label"><g:fieldValue bean="${trialInstance}" field="startDate"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${trialInstance?.interventions}">
				<li class="fieldcontain">
					<span id="interventions-label" class="property-label"><g:message code="trial.interventions.label" default="Interventions" /></span>
					
						<span class="property-value" aria-labelledby="interventions-label"><g:fieldValue bean="${trialInstance}" field="interventions"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${trialInstance?.score}">
				<li class="fieldcontain">
					<span id="score-label" class="property-label"><g:message code="trial.score.label" default="Score" /></span>
					
						<span class="property-value" aria-labelledby="score-label"><g:fieldValue bean="${trialInstance}" field="score"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${trialInstance?.title}">
				<li class="fieldcontain">
					<span id="title-label" class="property-label"><g:message code="trial.title.label" default="Title" /></span>
					
						<span class="property-value" aria-labelledby="title-label"><g:fieldValue bean="${trialInstance}" field="title"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${trialInstance?.locations}">
				<li class="fieldcontain">
					<span id="locations-label" class="property-label"><g:message code="trial.locations.label" default="Locations" /></span>
					
						<span class="property-value" aria-labelledby="locations-label"><g:fieldValue bean="${trialInstance}" field="locations"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${trialInstance?.overallContact}">
				<li class="fieldcontain">
					<span id="overallContact-label" class="property-label"><g:message code="trial.overallContact.label" default="Overall Contact" /></span>
					
						<span class="property-value" aria-labelledby="overallContact-label"><g:fieldValue bean="${trialInstance}" field="overallContact"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${trialInstance?.phase}">
				<li class="fieldcontain">
					<span id="phase-label" class="property-label"><g:message code="trial.phase.label" default="Phase" /></span>
					
						<span class="property-value" aria-labelledby="phase-label"><g:fieldValue bean="${trialInstance}" field="phase"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${trialInstance?.studyType}">
				<li class="fieldcontain">
					<span id="studyType-label" class="property-label"><g:message code="trial.studyType.label" default="Study Type" /></span>
					
						<span class="property-value" aria-labelledby="studyType-label"><g:fieldValue bean="${trialInstance}" field="studyType"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${trialInstance?.id}" />
					<g:link class="edit" action="edit" id="${trialInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
