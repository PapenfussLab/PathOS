<%@ page import="org.petermac.pathos.curate.Trial" %>



<div class="fieldcontain ${hasErrors(bean: trialInstance, field: 'study', 'error')} ">
	<label for="study">
		<g:message code="trial.study.label" default="Study" />
		
	</label>
	<g:textField name="study" value="${trialInstance?.study}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: trialInstance, field: 'briefTitle', 'error')} ">
	<label for="briefTitle">
		<g:message code="trial.briefTitle.label" default="Brief Title" />
		
	</label>
	<g:textArea name="briefTitle" cols="40" rows="5" maxlength="3000" value="${trialInstance?.briefTitle}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: trialInstance, field: 'molecularAlterations', 'error')} ">
	<label for="molecularAlterations">
		<g:message code="trial.molecularAlterations.label" default="Molecular Alterations" />
		
	</label>
	<g:textArea name="molecularAlterations" cols="40" rows="5" maxlength="1000" value="${trialInstance?.molecularAlterations}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: trialInstance, field: 'status', 'error')} ">
	<label for="status">
		<g:message code="trial.status.label" default="Status" />
		
	</label>
	<g:textField name="status" value="${trialInstance?.status}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: trialInstance, field: 'startDate', 'error')} ">
	<label for="startDate">
		<g:message code="trial.startDate.label" default="Start Date" />
		
	</label>
	<g:textField name="startDate" value="${trialInstance?.startDate}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: trialInstance, field: 'interventions', 'error')} ">
	<label for="interventions">
		<g:message code="trial.interventions.label" default="Interventions" />
		
	</label>
	<g:textField name="interventions" value="${trialInstance?.interventions}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: trialInstance, field: 'score', 'error')} ">
	<label for="score">
		<g:message code="trial.score.label" default="Score" />
		
	</label>
	<g:textField name="score" value="${trialInstance?.score}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: trialInstance, field: 'title', 'error')} ">
	<label for="title">
		<g:message code="trial.title.label" default="Title" />
		
	</label>
	<g:textArea name="title" cols="40" rows="5" maxlength="8000" value="${trialInstance?.title}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: trialInstance, field: 'locations', 'error')} ">
	<label for="locations">
		<g:message code="trial.locations.label" default="Locations" />
		
	</label>
	<g:textField name="locations" value="${trialInstance?.locations}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: trialInstance, field: 'overallContact', 'error')} ">
	<label for="overallContact">
		<g:message code="trial.overallContact.label" default="Overall Contact" />
		
	</label>
	<g:textField name="overallContact" value="${trialInstance?.overallContact}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: trialInstance, field: 'phase', 'error')} ">
	<label for="phase">
		<g:message code="trial.phase.label" default="Phase" />
		
	</label>
	<g:textField name="phase" value="${trialInstance?.phase}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: trialInstance, field: 'studyType', 'error')} ">
	<label for="studyType">
		<g:message code="trial.studyType.label" default="Study Type" />
		
	</label>
	<g:textArea name="studyType" cols="40" rows="5" maxlength="1000" value="${trialInstance?.studyType}"/>
</div>

