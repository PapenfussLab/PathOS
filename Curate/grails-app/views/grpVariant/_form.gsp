<%@ page import="org.petermac.pathos.curate.GrpVariant" %>



<div class="fieldcontain ${hasErrors(bean: grpVariantInstance, field: 'muttyp', 'error')} required">
	<label for="muttyp">
		<g:message code="grpVariant.muttyp.label" default="Muttyp" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="muttyp" from="${grpVariantInstance.constraints.muttyp.inList}" required="" value="${grpVariantInstance?.muttyp}" valueMessagePrefix="grpVariant.muttyp"/>
</div>

<div class="fieldcontain ${hasErrors(bean: grpVariantInstance, field: 'accession', 'error')} ">
	<label for="accession">
		<g:message code="grpVariant.accession.label" default="Accession" />
		
	</label>
	<g:textField name="accession" value="${grpVariantInstance?.accession}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: grpVariantInstance, field: 'createdBy', 'error')} required">
	<label for="createdBy">
		<g:message code="grpVariant.createdBy.label" default="Created By" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="createdBy" name="createdBy.id" from="${org.petermac.pathos.curate.AuthUser.list()}" optionKey="id" required="" value="${grpVariantInstance?.createdBy?.id}" class="many-to-one"/>
</div>

