<%@ page import="org.petermac.pathos.curate.FilterTemplate" %>
<span style="color:red;">Warning: The template field is injected directly into the PathOS curation grid, and there are no validation checks for it here. If you enter malformed code, the PathOS curation grid will break. Use this functionality at own risk. </span>




<div class="fieldcontain ${hasErrors(bean: filterTemplateInstance, field: 'templateName', 'error')} ">
	<label for="templateName">
		<g:message code="filterTemplate.templateName.label" default="Template Name" />
		
	</label>
	<g:textField name="templateName" value="${filterTemplateInstance?.templateName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: filterTemplateInstance, field: 'displayName', 'error')} ">
	<label for="displayName">
		<g:message code="filterTemplate.displayName.label" default="Display Name" />
		
	</label>
	<g:textField name="displayName" value="${filterTemplateInstance?.displayName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: filterTemplateInstance, field: 'template', 'error')} ">
	<label for="template">
		<g:message code="filterTemplate.template.label" default="Template" />

	</label>
	<g:textArea name="template" cols="40" rows="5" maxlength="9999" value="${filterTemplateInstance?.template}"/>
</div>