<%@ page import="org.petermac.pathos.curate.Transcript" %>



<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'accession', 'error')} ">
	<label for="accession">
		<g:message code="transcript.accession.label" default="Accession" />
		
	</label>
	<g:textField name="accession" value="${transcriptInstance?.accession}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'build', 'error')} ">
	<label for="build">
		<g:message code="transcript.build.label" default="Build" />
		
	</label>
	<g:textField name="build" value="${transcriptInstance?.build}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'cds_size', 'error')} required">
	<label for="cds_size">
		<g:message code="transcript.cds_size.label" default="Cdssize" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="cds_size" type="number" value="${transcriptInstance.cds_size}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'cds_start', 'error')} required">
	<label for="cds_start">
		<g:message code="transcript.cds_start.label" default="Cdsstart" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="cds_start" type="number" value="${transcriptInstance.cds_start}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'cds_stop', 'error')} required">
	<label for="cds_stop">
		<g:message code="transcript.cds_stop.label" default="Cdsstop" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="cds_stop" type="number" value="${transcriptInstance.cds_stop}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'chr_refseq', 'error')} ">
	<label for="chr_refseq">
		<g:message code="transcript.chr_refseq.label" default="Chrrefseq" />
		
	</label>
	<g:textField name="chr_refseq" value="${transcriptInstance?.chr_refseq}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'chromosome', 'error')} ">
	<label for="chromosome">
		<g:message code="transcript.chromosome.label" default="Chromosome" />
		
	</label>
	<g:textField name="chromosome" value="${transcriptInstance?.chromosome}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'exCount', 'error')} required">
	<label for="exCount">
		<g:message code="transcript.exCount.label" default="Ex Count" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="exCount" type="number" value="${transcriptInstance.exCount}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'exSize', 'error')} required">
	<label for="exSize">
		<g:message code="transcript.exSize.label" default="Ex Size" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="exSize" type="number" value="${transcriptInstance.exSize}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'exon_starts', 'error')} required">
	<label for="exon_starts">
		<g:message code="transcript.exon_starts.label" default="Exonstarts" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="exon_starts" type="number" value="${transcriptInstance.exon_starts}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'exon_stops', 'error')} required">
	<label for="exon_stops">
		<g:message code="transcript.exon_stops.label" default="Exonstops" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="exon_stops" type="number" value="${transcriptInstance.exon_stops}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'genbuild', 'error')} ">
	<label for="genbuild">
		<g:message code="transcript.genbuild.label" default="Genbuild" />
		
	</label>
	<g:textField name="genbuild" value="${transcriptInstance?.genbuild}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'gene', 'error')} ">
	<label for="gene">
		<g:message code="transcript.gene.label" default="Gene" />
		
	</label>
	<g:textField name="gene" value="${transcriptInstance?.gene}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'lrg', 'error')} ">
	<label for="lrg">
		<g:message code="transcript.lrg.label" default="Lrg" />
		
	</label>
	<g:textField name="lrg" value="${transcriptInstance?.lrg}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'preferred', 'error')} ">
	<label for="preferred">
		<g:message code="transcript.preferred.label" default="Preferred" />
		
	</label>
	<g:checkBox name="preferred" value="${transcriptInstance?.preferred}" />
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'refseq', 'error')} ">
	<label for="refseq">
		<g:message code="transcript.refseq.label" default="Refseq" />
		
	</label>
	<g:textField name="refseq" value="${transcriptInstance?.refseq}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'source', 'error')} ">
	<label for="source">
		<g:message code="transcript.source.label" default="Source" />
		
	</label>
	<g:textField name="source" value="${transcriptInstance?.source}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'strand', 'error')} ">
	<label for="strand">
		<g:message code="transcript.strand.label" default="Strand" />
		
	</label>
	<g:textField name="strand" value="${transcriptInstance?.strand}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'ts_size', 'error')} required">
	<label for="ts_size">
		<g:message code="transcript.ts_size.label" default="Tssize" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="ts_size" type="number" value="${transcriptInstance.ts_size}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'ts_start', 'error')} required">
	<label for="ts_start">
		<g:message code="transcript.ts_start.label" default="Tsstart" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="ts_start" type="number" value="${transcriptInstance.ts_start}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: transcriptInstance, field: 'ts_stop', 'error')} required">
	<label for="ts_stop">
		<g:message code="transcript.ts_stop.label" default="Tsstop" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="ts_stop" type="number" value="${transcriptInstance.ts_stop}" required=""/>
</div>

