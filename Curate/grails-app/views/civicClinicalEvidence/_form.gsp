<%@ page import="org.petermac.pathos.curate.CivicClinicalEvidence" %>



<div class="fieldcontain ${hasErrors(bean: civicClinicalEvidenceInstance, field: 'variant_id', 'error')} required">
	<label for="variant_id">
		<g:message code="civicClinicalEvidence.variant_id.label" default="Variantid" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="variant_id" type="number" value="${civicClinicalEvidenceInstance.variant_id}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicClinicalEvidenceInstance, field: 'variant', 'error')} ">
	<label for="variant">
		<g:message code="civicClinicalEvidence.variant.label" default="Variant" />
		
	</label>
	<g:textField name="variant" value="${civicClinicalEvidenceInstance?.variant}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicClinicalEvidenceInstance, field: 'disease', 'error')} ">
	<label for="disease">
		<g:message code="civicClinicalEvidence.disease.label" default="Disease" />
		
	</label>
	<g:textField name="disease" value="${civicClinicalEvidenceInstance?.disease}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicClinicalEvidenceInstance, field: 'doid', 'error')} ">
	<label for="doid">
		<g:message code="civicClinicalEvidence.doid.label" default="Doid" />
		
	</label>
	<g:textField name="doid" value="${civicClinicalEvidenceInstance?.doid}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicClinicalEvidenceInstance, field: 'drugs', 'error')} ">
	<label for="drugs">
		<g:message code="civicClinicalEvidence.drugs.label" default="Drugs" />
		
	</label>
	<g:textField name="drugs" value="${civicClinicalEvidenceInstance?.drugs}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicClinicalEvidenceInstance, field: 'evidence_id', 'error')} required">
	<label for="evidence_id">
		<g:message code="civicClinicalEvidence.entrez_id.label" default="Evidenceid" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="evidence_id" type="number" value="${civicClinicalEvidenceInstance.evidence_id}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicClinicalEvidenceInstance, field: 'evidence_type', 'error')} ">
	<label for="evidence_type">
		<g:message code="civicClinicalEvidence.evidence_type.label" default="Evidencetype" />
		
	</label>
	<g:textField name="evidence_type" value="${civicClinicalEvidenceInstance?.evidence_type}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicClinicalEvidenceInstance, field: 'evidence_direction', 'error')} ">
	<label for="evidence_direction">
		<g:message code="civicClinicalEvidence.evidence_direction.label" default="Evidencedirection" />
		
	</label>
	<g:textField name="evidence_direction" value="${civicClinicalEvidenceInstance?.evidence_direction}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicClinicalEvidenceInstance, field: 'evidence_level', 'error')} ">
	<label for="evidence_level">
		<g:message code="civicClinicalEvidence.evidence_level.label" default="Evidencelevel" />
		
	</label>
	<g:textField name="evidence_level" value="${civicClinicalEvidenceInstance?.evidence_level}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicClinicalEvidenceInstance, field: 'evidence_statement', 'error')} ">
	<label for="evidence_statement">
		<g:message code="civicClinicalEvidence.evidence_statement.label" default="Evidencestatement" />
		
	</label>
	<g:textField name="evidence_statement" value="${civicClinicalEvidenceInstance?.evidence_statement}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicClinicalEvidenceInstance, field: 'evidence_status', 'error')} ">
	<label for="evidence_status">
		<g:message code="civicClinicalEvidence.evidence_status.label" default="Evidencestatus" />
		
	</label>
	<g:textField name="evidence_status" value="${civicClinicalEvidenceInstance?.evidence_status}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicClinicalEvidenceInstance, field: 'pubmed_id', 'error')} ">
	<label for="pubmed_id">
		<g:message code="civicClinicalEvidence.pubmed_id.label" default="Pubmedid" />
		
	</label>
	<g:textField name="pubmed_id" value="${civicClinicalEvidenceInstance?.pubmed_id}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicClinicalEvidenceInstance, field: 'citation', 'error')} ">
	<label for="citation">
		<g:message code="civicClinicalEvidence.citation.label" default="Citation" />
		
	</label>
	<g:textField name="citation" value="${civicClinicalEvidenceInstance?.citation}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicClinicalEvidenceInstance, field: 'rating', 'error')} ">
	<label for="rating">
		<g:message code="civicClinicalEvidence.rating.label" default="Rating" />
		
	</label>
	<g:textField name="rating" value="${civicClinicalEvidenceInstance?.rating}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicClinicalEvidenceInstance, field: 'clinical_significance', 'error')} ">
	<label for="clinical_significance">
		<g:message code="civicClinicalEvidence.clinical_significance.label" default="Clinicalsignificance" />
		
	</label>
	<g:textField name="clinical_significance" value="${civicClinicalEvidenceInstance?.clinical_significance}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: civicClinicalEvidenceInstance, field: 'last_review_date', 'error')} ">
	<label for="last_review_date">
		<g:message code="civicClinicalEvidence.last_review_date.label" default="Lastreviewdate" />
		
	</label>
	<g:textField name="last_review_date" value="${civicClinicalEvidenceInstance?.last_review_date}"/>
</div>

