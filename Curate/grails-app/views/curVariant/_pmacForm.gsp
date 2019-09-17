<%@ page import="org.petermac.pathos.curate.Evidence" %>

<r:style>

#pmacEvidenceForm {
	padding: 2px;
	background: #eee;
	cursor: not-allowed;
}


#pmacEvidenceForm div,
#pmacEvidenceForm label,
#pmacEvidenceForm input,
#pmacEvidenceForm textarea {
	cursor: not-allowed !important;
}

#pmacEvidenceForm textarea {
	background: #ddd;
}

</r:style>

<form id="pmacEvidenceForm">

	<h2>Calculated Classification</h2>

	<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'evidenceClass', 'error')} required">
		<g:varClass class="${evidenceInstance?.evidenceClass}"/>
	</div>

	<h2>Collected Evidence</h2>


	<fieldset class="form">
		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'justification', 'error')} ">
		<g:textArea readonly placeholder="Evidence to support the classification." name="justification" maxlength="8000" cols="10" rows="10" value="${evidenceInstance?.justification}"/>
		</div>

		<h2 style="text-decoration: underline">Pathogenic</h2>

		<h3>Stand-alone</h3>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathAloneTruncating', 'error')} ">
			<g:checkBox onclick="return false;" name="pathAloneTruncating" value="${evidenceInstance?.pathAloneTruncating}" />
			<tooltip:tip code="evidence.pathAloneTruncating.tip">
				<label for="pathAloneTruncating">
					<g:message code="evidence.pathAloneTruncating.label" default="Path Alone Truncating" />
				</label>
			</tooltip:tip>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathAloneKnown', 'error')} ">
			<g:checkBox onclick="return false;" name="pathAloneKnown" value="${evidenceInstance?.pathAloneKnown}" />
			<label for="pathAloneKnown">
				<g:message code="evidence.pathAloneKnown.label" default="Path Alone Known" />
			</label>
		</div>


		<h3>Strong</h3>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathStrongFunction', 'error')} ">
			<g:checkBox onclick="return false;" name="pathStrongFunction" value="${evidenceInstance?.pathStrongFunction}" />
			<label for="pathStrongFunction">
				<g:message code="evidence.pathStrongFunction.label" default="Path Strong Function" />
			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathStrongCase', 'error')} ">
			<g:checkBox onclick="return false;" name="pathStrongCase" value="${evidenceInstance?.pathStrongCase}" />
			<label for="pathStrongCase">
				<g:message code="evidence.pathStrongCase.label" default="Path Strong Case" />
			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathStrongCoseg', 'error')} ">
			<g:checkBox onclick="return false;" name="pathStrongCoseg" value="${evidenceInstance?.pathStrongCoseg}" />
			<label for="pathStrongCoseg">
				<g:message code="evidence.pathStrongCoseg.label" default="Path Strong Coseg" />
			</label>
		</div>


		<h3>Supporting</h3>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportHotspot', 'error')} ">
			<g:checkBox onclick="return false;" name="pathSupportHotspot" value="${evidenceInstance?.pathSupportHotspot}" />
			<label for="pathSupportHotspot">
				<g:message code="evidence.pathSupportHotspot.label" default="Path Support Hotspot" />
			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportGene', 'error')} ">
			<g:checkBox onclick="return false;" name="pathSupportGene" value="${evidenceInstance?.pathSupportGene}" />
			<label for="pathSupportGene">
				<g:message code="evidence.pathSupportGene.label" default="Path Support Gene" />
			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportInsilico', 'error')} ">
			<g:checkBox onclick="return false;" name="pathSupportInsilico" value="${evidenceInstance?.pathSupportInsilico}" />
			<label for="pathSupportInsilico">
				<g:message code="evidence.pathSupportInsilico.label" default="Path Support Insilico" />
			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportSpectrum', 'error')} ">
			<g:checkBox onclick="return false;" name="pathSupportSpectrum" value="${evidenceInstance?.pathSupportSpectrum}" />
			<label for="pathSupportSpectrum">
				<g:message code="evidence.pathSupportSpectrum.label" default="Path Support Spectrum" />
			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportGmaf', 'error')} ">
			<g:checkBox onclick="return false;" name="pathSupportGmaf" value="${evidenceInstance?.pathSupportGmaf}" />
			<label for="pathSupportGmaf">
				<g:message code="evidence.pathSupportGmaf.label" default="Path Support Gmaf" />
			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportIndel', 'error')} ">
			<g:checkBox onclick="return false;" name="pathSupportIndel" value="${evidenceInstance?.pathSupportIndel}" />
			<label for="pathSupportIndel">
				<g:message code="evidence.pathSupportIndel.label" default="Path Support Indel" />
			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportNovelMissense', 'error')} ">
			<g:checkBox onclick="return false;" name="pathSupportNovelMissense" value="${evidenceInstance?.pathSupportNovelMissense}" />
			<label for="pathSupportNovelMissense">
				<g:message code="evidence.pathSupportNovelMissense.label" default="Path Support Novel Missense" />
			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportLsdb', 'error')} ">
			<g:checkBox onclick="return false;" name="pathSupportLsdb" value="${evidenceInstance?.pathSupportLsdb}" />
			<label for="pathSupportLsdb">
				<g:message code="evidence.pathSupportLsdb.label" default="Path Support Lsdb" />
			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportCoseg', 'error')} ">
			<g:checkBox onclick="return false;" name="pathSupportCoseg" value="${evidenceInstance?.pathSupportCoseg}" />
			<label for="pathSupportCoseg">
				<g:message code="evidence.pathSupportCoseg.label" default="Path Support Coseg" />
			</label>
		</div>

		<h2 style="text-decoration: underline">Benign</h2>

		<h3>Stand-alone</h3>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignAloneGmaf', 'error')} ">
			<g:checkBox onclick="return false;" name="benignAloneGmaf" value="${evidenceInstance?.benignAloneGmaf}" />
			<label for="benignAloneGmaf">
				<g:message code="evidence.benignAloneGmaf.label" default="Benign Alone Gmaf" />
			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignAloneHealthy', 'error')} ">
			<g:checkBox onclick="return false;" name="benignAloneHealthy" value="${evidenceInstance?.benignAloneHealthy}" />
			<label for="benignAloneHealthy">
				<g:message code="evidence.benignAloneHealthy.label" default="Benign Alone Healthy" />
			</label>
		</div>


		<h3>Strong</h3>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignStrongFunction', 'error')} ">
			<g:checkBox onclick="return false;" name="benignStrongFunction" value="${evidenceInstance?.benignStrongFunction}" />
			<label for="benignStrongFunction">
				<g:message code="evidence.benignStrongFunction.label" default="Benign Strong Function" />
			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignStrongCase', 'error')} ">
			<g:checkBox onclick="return false;" name="benignStrongCase" value="${evidenceInstance?.benignStrongCase}" />
			<label for="benignStrongCase">
				<g:message code="evidence.benignStrongCase.label" default="Benign Strong Case" />

			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignStrongCoseg', 'error')} ">
			<g:checkBox onclick="return false;" name="benignStrongCoseg" value="${evidenceInstance?.benignStrongCoseg}" />
			<label for="benignStrongCoseg">
				<g:message code="evidence.benignStrongCoseg.label" default="Benign Strong Conseg" />

			</label>
		</div>

		<h3>Supporting</h3>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignSupportVariable', 'error')} ">
			<g:checkBox onclick="return false;" name="benignSupportVariable" value="${evidenceInstance?.benignSupportVariable}" />
			<label for="benignSupportVariable">
				<g:message code="evidence.benignSupportVariable.label" default="Benign Support Variable" />
			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignSupportInsilico', 'error')} ">
			<g:checkBox onclick="return false;" name="benignSupportInsilico" value="${evidenceInstance?.benignSupportInsilico}" />
			<label for="benignSupportInsilico">
				<g:message code="evidence.benignSupportInsilico.label" default="Benign Support Insilico" />
			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignSupportSpectrum', 'error')} ">
			<g:checkBox onclick="return false;" name="benignSupportSpectrum" value="${evidenceInstance?.benignSupportSpectrum}" />
			<label for="benignSupportSpectrum">
				<g:message code="evidence.benignSupportSpectrum.label" default="Benign Support Spectrum" />
			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignSupportLsdb', 'error')} ">
			<g:checkBox onclick="return false;" name="benignSupportLsdb" value="${evidenceInstance?.benignSupportLsdb}" />
			<label for="benignSupportLsdb">
				<g:message code="evidence.benignSupportLsdb.label" default="Benign Support Lsdb" />

			</label>
		</div>

		<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignSupportPath', 'error')} ">
			<g:checkBox onclick="return false;" name="benignSupportPath" value="${evidenceInstance?.benignSupportPath}" />
			<label for="benignSupportPath">
				<g:message code="evidence.benignSupportPath.label" default="Benign Support Path" />

			</label>
		</div>
	</fieldset>
</form>
