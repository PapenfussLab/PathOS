<%@ page import="org.petermac.pathos.curate.ClinContext" %>



<div class="fieldcontain ${hasErrors(bean: clinContextInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="clinContext.description.label" default="Description" />
		
	</label>
	<g:textField name="description" value="${clinContextInstance?.description}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: clinContextInstance, field: 'code', 'error')} ">
	<label for="code">
		<g:message code="clinContext.code.label" default="Code" />
		
	</label>
	<g:textField name="code" value="${clinContextInstance?.code}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: clinContextInstance, field: 'createdBy', 'error')} ">
	<label for="createdBy">
		<g:message code="clinContext.createdBy.label" default="Created By" />
		
	</label>
	<g:select id="createdBy" name="createdBy.id" from="${org.petermac.pathos.curate.AuthUser.list()}" optionKey="id" value="${clinContextInstance?.createdBy?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

