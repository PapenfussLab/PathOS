<%@ page import="org.petermac.pathos.curate.RefExon" %>



<div class="fieldcontain ${hasErrors(bean: refExonInstance, field: 'gene', 'error')} ">
	<label for="gene">
		<g:message code="refExon.gene.label" default="Gene" />
		
	</label>
	<g:textField name="gene" value="${refExonInstance?.gene}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: refExonInstance, field: 'refseq', 'error')} ">
	<label for="refseq">
		<g:message code="refExon.refseq.label" default="Refseq" />
		
	</label>
	<g:textField name="refseq" value="${refExonInstance?.refseq}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: refExonInstance, field: 'exon', 'error')} ">
	<label for="exon">
		<g:message code="refExon.exon.label" default="Exon" />
		
	</label>
	<g:textField name="exon" value="${refExonInstance?.exon}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: refExonInstance, field: 'strand', 'error')} ">
	<label for="strand">
		<g:message code="refExon.strand.label" default="Strand" />
		
	</label>
	<g:textField name="strand" value="${refExonInstance?.strand}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: refExonInstance, field: 'idx', 'error')} ">
	<label for="idx">
		<g:message code="refExon.idx.label" default="Idx" />
		
	</label>
	<g:textField name="idx" value="${refExonInstance?.idx}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: refExonInstance, field: 'exonStart', 'error')} ">
	<label for="exonStart">
		<g:message code="refExon.exonStart.label" default="Exon Start" />
		
	</label>
	<g:textField name="exonStart" value="${refExonInstance?.exonStart}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: refExonInstance, field: 'exonEnd', 'error')} ">
	<label for="exonEnd">
		<g:message code="refExon.exonEnd.label" default="Exon End" />
		
	</label>
	<g:textField name="exonEnd" value="${refExonInstance?.exonEnd}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: refExonInstance, field: 'exonFrame', 'error')} ">
	<label for="exonFrame">
		<g:message code="refExon.exonFrame.label" default="Exon Frame" />
		
	</label>
	<g:textField name="exonFrame" value="${refExonInstance?.exonFrame}"/>
</div>

