<%@ page import="org.petermac.pathos.curate.Tag" %>



<div class="fieldcontain ${hasErrors(bean: tagInstance, field: 'label', 'error')} ">
	<label for="label">
		<g:message code="tag.label.label" default="Label" />
		
	</label>
	<g:textField name="label" value="${tagInstance?.label}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: tagInstance, field: 'createdBy', 'error')} required">
	<label for="createdBy">
		<g:message code="tag.createdBy.label" default="Created By" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="createdBy" name="createdBy.id" from="${org.petermac.pathos.curate.AuthUser.list()}" optionKey="id" required="" value="${tagInstance?.createdBy?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: tagInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="tag.description.label" default="Description" />
		
	</label>
	<g:textArea name="description" cols="40" rows="5" maxlength="8000" value="${tagInstance?.description}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: tagInstance, field: 'isAuto', 'error')} ">
	<label for="isAuto">
		<g:message code="tag.isAuto.label" default="Is Auto" />
		
	</label>
	<g:checkBox name="isAuto" value="${tagInstance?.isAuto}" />
</div>

