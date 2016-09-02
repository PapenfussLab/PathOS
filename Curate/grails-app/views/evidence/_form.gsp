<%@ page import="org.petermac.pathos.curate.Evidence" %>

<h2>Classification</h2>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'evidenceClass', 'error')} required">
    <g:varClass class="${evidenceInstance?.evidenceClass}"/>
</div>

<br>

<tooltip:tip code="evidence.justification.tip">
    <h2>Collected Evidence</h2>
</tooltip:tip>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'justification', 'error')} ">
    <g:textArea name="justification" maxlength="8000" cols="10" rows="10" value="${evidenceInstance?.justification}"/>
</div>

<h2 style="text-decoration: underline"><br>Pathogenic<br></h2>
<br>

<h3>Stand-alone</h3>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathAloneTruncating', 'error')} ">
    <g:checkBox name="pathAloneTruncating" value="${evidenceInstance?.pathAloneTruncating}" />
    <tooltip:tip code="evidence.pathAloneTruncating.tip">
    <label style="text-align: left; margin-left: 10px; width: 75%" for="pathAloneTruncating">
        <g:message code="evidence.pathAloneTruncating.label" default="Path Alone Truncating" />
    </label>
    </tooltip:tip>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathAloneKnown', 'error')} ">
    <g:checkBox name="pathAloneKnown" value="${evidenceInstance?.pathAloneKnown}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="pathAloneKnown">
		<g:message code="evidence.pathAloneKnown.label" default="Path Alone Known" />
	</label>
</div>

<br>
<h3>Strong</h3>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathStrongFunction', 'error')} ">
    <g:checkBox name="pathStrongFunction" value="${evidenceInstance?.pathStrongFunction}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="pathStrongFunction">
		<g:message code="evidence.pathStrongFunction.label" default="Path Strong Function" />
	</label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathStrongCase', 'error')} ">
    <g:checkBox name="pathStrongCase" value="${evidenceInstance?.pathStrongCase}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="pathStrongCase">
		<g:message code="evidence.pathStrongCase.label" default="Path Strong Case" />
	</label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathStrongCoseg', 'error')} ">
    <g:checkBox name="pathStrongCoseg" value="${evidenceInstance?.pathStrongCoseg}" />
    <label style="text-align: left; margin-left: 10px; width: 75%" for="pathStrongCoseg">
        <g:message code="evidence.pathStrongCoseg.label" default="Path Strong Coseg" />
    </label>
</div>

<br>
<h3>Supporting</h3>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportHotspot', 'error')} ">
    <g:checkBox name="pathSupportHotspot" value="${evidenceInstance?.pathSupportHotspot}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="pathSupportHotspot">
		<g:message code="evidence.pathSupportHotspot.label" default="Path Support Hotspot" />
	</label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportGene', 'error')} ">
    <g:checkBox name="pathSupportGene" value="${evidenceInstance?.pathSupportGene}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="pathSupportGene">
		<g:message code="evidence.pathSupportGene.label" default="Path Support Gene" />
	</label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportInsilico', 'error')} ">
    <g:checkBox name="pathSupportInsilico" value="${evidenceInstance?.pathSupportInsilico}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="pathSupportInsilico">
		<g:message code="evidence.pathSupportInsilico.label" default="Path Support Insilico" />
	</label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportSpectrum', 'error')} ">
    <g:checkBox name="pathSupportSpectrum" value="${evidenceInstance?.pathSupportSpectrum}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="pathSupportSpectrum">
		<g:message code="evidence.pathSupportSpectrum.label" default="Path Support Spectrum" />
	</label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportGmaf', 'error')} ">
    <g:checkBox name="pathSupportGmaf" value="${evidenceInstance?.pathSupportGmaf}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="pathSupportGmaf">
		<g:message code="evidence.pathSupportGmaf.label" default="Path Support Gmaf" />
	</label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportIndel', 'error')} ">
    <g:checkBox name="pathSupportIndel" value="${evidenceInstance?.pathSupportIndel}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="pathSupportIndel">
		<g:message code="evidence.pathSupportIndel.label" default="Path Support Indel" />
	</label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportNovelMissense', 'error')} ">
    <g:checkBox name="pathSupportNovelMissense" value="${evidenceInstance?.pathSupportNovelMissense}" />
    <label style="text-align: left; margin-left: 10px; width: 75%" for="pathSupportNovelMissense">
        <g:message code="evidence.pathSupportNovelMissense.label" default="Path Support Novel Missense" />
    </label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportLsdb', 'error')} ">
    <g:checkBox name="pathSupportLsdb" value="${evidenceInstance?.pathSupportLsdb}" />
    <label style="text-align: left; margin-left: 10px; width: 75%" for="pathSupportLsdb">
        <g:message code="evidence.pathSupportLsdb.label" default="Path Support Lsdb" />
    </label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'pathSupportCoseg', 'error')} ">
    <g:checkBox name="pathSupportCoseg" value="${evidenceInstance?.pathSupportCoseg}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="pathSupportCoseg">
		<g:message code="evidence.pathSupportCoseg.label" default="Path Support Coseg" />
	</label>
</div>

<h2 style="text-decoration: underline"><br>Benign<br></h2>
<br>
<h3>Stand-alone</h3>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignAloneGmaf', 'error')} ">
    <g:checkBox name="benignAloneGmaf" value="${evidenceInstance?.benignAloneGmaf}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="benignAloneGmaf">
		<g:message code="evidence.benignAloneGmaf.label" default="Benign Alone Gmaf" />
	</label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignAloneHealthy', 'error')} ">
    <g:checkBox name="benignAloneHealthy" value="${evidenceInstance?.benignAloneHealthy}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="benignAloneHealthy">
		<g:message code="evidence.benignAloneHealthy.label" default="Benign Alone Healthy" />
	</label>
</div>

<br>
<h3>Strong</h3>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignStrongFunction', 'error')} ">
    <g:checkBox name="benignStrongFunction" value="${evidenceInstance?.benignStrongFunction}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="benignStrongFunction">
		<g:message code="evidence.benignStrongFunction.label" default="Benign Strong Function" />
	</label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignStrongCase', 'error')} ">
    <g:checkBox name="benignStrongCase" value="${evidenceInstance?.benignStrongCase}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="benignStrongCase">
		<g:message code="evidence.benignStrongCase.label" default="Benign Strong Case" />
		
	</label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignStrongCoseg', 'error')} ">
    <g:checkBox name="benignStrongCoseg" value="${evidenceInstance?.benignStrongCoseg}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="benignStrongCoseg">
		<g:message code="evidence.benignStrongCoseg.label" default="Benign Strong Conseg" />
		
	</label>
</div>

<br>
<h3>Supporting</h3>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignSupportVariable', 'error')} ">
    <g:checkBox name="benignSupportVariable" value="${evidenceInstance?.benignSupportVariable}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="benignSupportVariable">
		<g:message code="evidence.benignSupportVariable.label" default="Benign Support Variable" />
		
	</label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignSupportInsilico', 'error')} ">
    <g:checkBox name="benignSupportInsilico" value="${evidenceInstance?.benignSupportInsilico}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="benignSupportInsilico">
		<g:message code="evidence.benignSupportInsilico.label" default="Benign Support Insilico" />
		
	</label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignSupportSpectrum', 'error')} ">
    <g:checkBox name="benignSupportSpectrum" value="${evidenceInstance?.benignSupportSpectrum}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="benignSupportSpectrum">
		<g:message code="evidence.benignSupportSpectrum.label" default="Benign Support Spectrum" />
		
	</label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignSupportLsdb', 'error')} ">
    <g:checkBox name="benignSupportLsdb" value="${evidenceInstance?.benignSupportLsdb}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="benignSupportLsdb">
		<g:message code="evidence.benignSupportLsdb.label" default="Benign Support Lsdb" />
		
	</label>
</div>

<div class="fieldcontain ${hasErrors(bean: evidenceInstance, field: 'benignSupportPath', 'error')} ">
    <g:checkBox name="benignSupportPath" value="${evidenceInstance?.benignSupportPath}" />
	<label style="text-align: left; margin-left: 10px; width: 75%" for="benignSupportPath">
		<g:message code="evidence.benignSupportPath.label" default="Benign Support Path" />
		
	</label>
</div>

