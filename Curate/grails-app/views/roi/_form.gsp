<%@ page import="org.petermac.pathos.curate.Roi" %>



<div class="fieldcontain ${hasErrors(bean: roiInstance, field: 'panel', 'error')} required">
	<label for="panel">
		<g:message code="roi.panel.label" default="Panel" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="panel" name="panel.id" from="${org.petermac.pathos.curate.Panel.list()}" optionKey="id" required="" value="${roiInstance?.panel?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: roiInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="roi.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${roiInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: roiInstance, field: 'gene', 'error')} ">
	<label for="gene">
		<g:message code="roi.gene.label" default="Gene" />
		
	</label>
	<g:textField name="gene" value="${roiInstance?.gene}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: roiInstance, field: 'exon', 'error')} ">
	<label for="exon">
		<g:message code="roi.exon.label" default="Exon" />
		
	</label>
	<g:textField name="exon" value="${roiInstance?.exon}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: roiInstance, field: 'chr', 'error')} ">
	<label for="chr">
		<g:message code="roi.chr.label" default="Chr" />
		
	</label>
	<g:textField name="chr" value="${roiInstance?.chr}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: roiInstance, field: 'startPos', 'error')} required">
	<label for="startPos">
		<g:message code="roi.startPos.label" default="Start Pos" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="startPos" type="number" value="${roiInstance.startPos}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: roiInstance, field: 'endPos', 'error')} required">
	<label for="endPos">
		<g:message code="roi.endPos.label" default="End Pos" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="endPos" type="number" value="${roiInstance.endPos}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: roiInstance, field: 'amplicons', 'error')} ">
	<label for="amplicons">
		<g:message code="roi.amplicons.label" default="Amplicons" />
		
	</label>
	<g:textField name="amplicons" value="${roiInstance?.amplicons}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: roiInstance, field: 'manifestName', 'error')} ">
	<label for="manifestName">
		<g:message code="roi.manifestName.label" default="Manifest Name" />
		
	</label>
	<g:textField name="manifestName" value="${roiInstance?.manifestName}"/>
</div>

