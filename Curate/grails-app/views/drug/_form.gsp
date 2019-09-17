<%@ page import="org.petermac.pathos.curate.Drug" %>



<div class="fieldcontain ${hasErrors(bean: drugInstance, field: 'alias', 'error')} ">
	<label for="alias">
		<g:message code="drug.alias.label" default="Alias" />
		
	</label>
	<g:textField name="alias" value="${drugInstance?.alias}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: drugInstance, field: 'approved', 'error')} ">
	<label for="approved">
		<g:message code="drug.approved.label" default="Approved" />
		
	</label>
	<g:textField name="approved" value="${drugInstance?.approved}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: drugInstance, field: 'approvedConditionMatch', 'error')} ">
	<label for="approvedConditionMatch">
		<g:message code="drug.approvedConditionMatch.label" default="Approved Condition Match" />
		
	</label>
	<g:textField name="approvedConditionMatch" value="${drugInstance?.approvedConditionMatch}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: drugInstance, field: 'approvedConditions', 'error')} ">
	<label for="approvedConditions">
		<g:message code="drug.approvedConditions.label" default="Approved Conditions" />
		
	</label>
	<g:textField name="approvedConditions" value="${drugInstance?.approvedConditions}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: drugInstance, field: 'badge', 'error')} ">
	<label for="badge">
		<g:message code="drug.badge.label" default="Badge" />
		
	</label>
	<g:textField name="badge" value="${drugInstance?.badge}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: drugInstance, field: 'brands', 'error')} ">
	<label for="brands">
		<g:message code="drug.brands.label" default="Brands" />
		
	</label>
	<g:textArea name="brands" cols="40" rows="5" maxlength="7000" value="${drugInstance?.brands}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: drugInstance, field: 'contraindicatedAlterations', 'error')} ">
	<label for="contraindicatedAlterations">
		<g:message code="drug.contraindicatedAlterations.label" default="Contraindicated Alterations" />
		
	</label>
	<g:textArea name="contraindicatedAlterations" cols="40" rows="5" maxlength="3000" value="${drugInstance?.contraindicatedAlterations}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: drugInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="drug.description.label" default="Description" />
		
	</label>
	<g:textArea name="description" cols="40" rows="5" maxlength="3000" value="${drugInstance?.description}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: drugInstance, field: 'experimental', 'error')} ">
	<label for="experimental">
		<g:message code="drug.experimental.label" default="Experimental" />
		
	</label>
	<g:textField name="experimental" value="${drugInstance?.experimental}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: drugInstance, field: 'experimentalConditions', 'error')} ">
	<label for="experimentalConditions">
		<g:message code="drug.experimentalConditions.label" default="Experimental Conditions" />
		
	</label>
	<g:textField name="experimentalConditions" value="${drugInstance?.experimentalConditions}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: drugInstance, field: 'externalIds', 'error')} ">
	<label for="externalIds">
		<g:message code="drug.externalIds.label" default="External Ids" />
		
	</label>
	<g:textArea name="externalIds" cols="40" rows="5" maxlength="1000" value="${drugInstance?.externalIds}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: drugInstance, field: 'molecularExperimentalTargets', 'error')} ">
	<label for="molecularExperimentalTargets">
		<g:message code="drug.molecularExperimentalTargets.label" default="Molecular Experimental Targets" />
		
	</label>
	<g:textField name="molecularExperimentalTargets" value="${drugInstance?.molecularExperimentalTargets}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: drugInstance, field: 'molecularTargets', 'error')} ">
	<label for="molecularTargets">
		<g:message code="drug.molecularTargets.label" default="Molecular Targets" />
		
	</label>
	<g:textField name="molecularTargets" value="${drugInstance?.molecularTargets}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: drugInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="drug.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${drugInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: drugInstance, field: 'status', 'error')} ">
	<label for="status">
		<g:message code="drug.status.label" default="Status" />
		
	</label>
	<g:textField name="status" value="${drugInstance?.status}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: drugInstance, field: 'synonyms', 'error')} ">
	<label for="synonyms">
		<g:message code="drug.synonyms.label" default="Synonyms" />
		
	</label>
	<g:textArea name="synonyms" cols="40" rows="5" maxlength="2000" value="${drugInstance?.synonyms}"/>
</div>

