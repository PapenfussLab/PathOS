<%@ page import="org.petermac.pathos.curate.RefGene" %>



<div class="fieldcontain ${hasErrors(bean: refGeneInstance, field: 'gene', 'error')} ">
	<label for="gene">
		<g:message code="refGene.gene.label" default="Gene" />
		
	</label>
	<g:textField name="gene" disabled value="${refGeneInstance?.gene}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: refGeneInstance, field: 'hgncid', 'error')} ">
	<label for="hgncid">
		<g:message code="refGene.hgncid.label" default="Hgncid" />
		
	</label>
	<g:textField name="hgncid" disabled value="${refGeneInstance?.hgncid}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: refGeneInstance, field: 'accession', 'error')} ">
	<label for="accession">
		<g:message code="refGene.accession.label" default="Accession" />
		
	</label>
	<g:textField name="accession" disabled value="${refGeneInstance?.accession}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: refGeneInstance, field: 'genedesc', 'error')} ">
	<label for="genedesc">
		<g:message code="refGene.genedesc.label" default="Genedesc" />
		
	</label>
	<g:textField name="genedesc" value="${refGeneInstance?.genedesc}"/>
</div>

