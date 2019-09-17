<%@ page import="org.petermac.pathos.curate.CivicVariant" %>



<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'variant_civic_url', 'error')} ">
	<label for="variant_civic_url">
		<g:message code="civicVariant.variant_civic_url.label" default="Variantcivicurl" />
		
	</label>
	<g:textField name="variant_civic_url" value="${civicVariantInstance?.variant_civic_url}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'gene', 'error')} ">
	<label for="gene">
		<g:message code="civicVariant.gene.label" default="Gene" />
		
	</label>
	<g:textField name="gene" value="${civicVariantInstance?.gene}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'entrez_id', 'error')} ">
	<label for="entrez_id">
		<g:message code="civicVariant.entrez_id.label" default="Entrezid" />
		
	</label>
	<g:textField name="entrez_id" value="${civicVariantInstance?.entrez_id}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'variant', 'error')} ">
	<label for="variant">
		<g:message code="civicVariant.variant.label" default="Variant" />
		
	</label>
	<g:textField name="variant" value="${civicVariantInstance?.variant}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'summary', 'error')} ">
	<label for="summary">
		<g:message code="civicVariant.summary.label" default="Summary" />
		
	</label>
	<g:textArea name="summary" cols="40" rows="5" maxlength="6000" value="${civicVariantInstance?.summary}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'variant_groups', 'error')} ">
	<label for="variant_groups">
		<g:message code="civicVariant.variant_groups.label" default="Variantgroups" />
		
	</label>
	<g:textField name="variant_groups" value="${civicVariantInstance?.variant_groups}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'chromosome', 'error')} ">
	<label for="chromosome">
		<g:message code="civicVariant.chromosome.label" default="Chromosome" />
		
	</label>
	<g:textField name="chromosome" value="${civicVariantInstance?.chromosome}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'start', 'error')} ">
	<label for="start">
		<g:message code="civicVariant.start.label" default="Start" />
		
	</label>
	<g:field name="start" type="number" value="${civicVariantInstance.start}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'stop', 'error')} ">
	<label for="stop">
		<g:message code="civicVariant.stop.label" default="Stop" />
		
	</label>
	<g:field name="stop" type="number" value="${civicVariantInstance.stop}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'reference_bases', 'error')} ">
	<label for="reference_bases">
		<g:message code="civicVariant.reference_bases.label" default="Referencebases" />
		
	</label>
	<g:textField name="reference_bases" value="${civicVariantInstance?.reference_bases}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'variant_bases', 'error')} ">
	<label for="variant_bases">
		<g:message code="civicVariant.variant_bases.label" default="Variantbases" />
		
	</label>
	<g:textField name="variant_bases" value="${civicVariantInstance?.variant_bases}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'representative_transcript', 'error')} ">
	<label for="representative_transcript">
		<g:message code="civicVariant.representative_transcript.label" default="Representativetranscript" />
		
	</label>
	<g:textField name="representative_transcript" value="${civicVariantInstance?.representative_transcript}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'ensembl_version', 'error')} ">
	<label for="ensembl_version">
		<g:message code="civicVariant.ensembl_version.label" default="Ensemblversion" />
		
	</label>
	<g:textField name="ensembl_version" value="${civicVariantInstance?.ensembl_version}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'reference_build', 'error')} ">
	<label for="reference_build">
		<g:message code="civicVariant.reference_build.label" default="Referencebuild" />
		
	</label>
	<g:textField name="reference_build" value="${civicVariantInstance?.reference_build}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'chromosome2', 'error')} ">
	<label for="chromosome2">
		<g:message code="civicVariant.chromosome2.label" default="Chromosome2" />
		
	</label>
	<g:textField name="chromosome2" value="${civicVariantInstance?.chromosome2}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'start2', 'error')} ">
	<label for="start2">
		<g:message code="civicVariant.start2.label" default="Start2" />
		
	</label>
	<g:field name="start2" type="number" value="${civicVariantInstance.start2}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'stop2', 'error')} ">
	<label for="stop2">
		<g:message code="civicVariant.stop2.label" default="Stop2" />
		
	</label>
	<g:field name="stop2" type="number" value="${civicVariantInstance.stop2}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'representative_transcript2', 'error')} ">
	<label for="representative_transcript2">
		<g:message code="civicVariant.representative_transcript2.label" default="Representativetranscript2" />
		
	</label>
	<g:textField name="representative_transcript2" value="${civicVariantInstance?.representative_transcript2}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'variant_types', 'error')} ">
	<label for="variant_types">
		<g:message code="civicVariant.variant_types.label" default="Varianttypes" />
		
	</label>
	<g:textField name="variant_types" value="${civicVariantInstance?.variant_types}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'hgvs_expressions', 'error')} ">
	<label for="hgvs_expressions">
		<g:message code="civicVariant.hgvs_expressions.label" default="Hgvsexpressions" />
		
	</label>
	<g:textArea name="hgvs_expressions" cols="40" rows="5" maxlength="6000" value="${civicVariantInstance?.hgvs_expressions}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicVariantInstance, field: 'last_review_date', 'error')} ">
	<label for="last_review_date">
		<g:message code="civicVariant.last_review_date.label" default="Lastreviewdate" />
		
	</label>
	<g:textField name="last_review_date" value="${civicVariantInstance?.last_review_date}"/>
</div>

