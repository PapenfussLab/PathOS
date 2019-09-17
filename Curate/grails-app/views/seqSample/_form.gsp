<%@ page import="org.petermac.pathos.curate.SeqSample" %>


<div class="fieldcontain ${hasErrors(bean: seqSampleInstance, field: 'sampleName', 'error')} ">
	<label for="sampleName">
		<g:message code="seqSample.sampleName.label" default="Sample Name" />
		<span class="required-indicator">*</span>

	</label>
	<g:textField name="sampleName" value="${seqSampleInstance?.sampleName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleInstance, field: 'seqrun', 'error')} required">
	<label for="seqrun">
		<g:message code="seqSample.seqrun.label" default="Seqrun" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="seqrun" value="${seqSampleInstance?.seqrun}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleInstance, field: 'patSample', 'error')} ">
	<label for="patSample">
		<g:message code="seqSample.patSample.label" default="Pat Sample" />
		
	</label>
	<g:textField name="patSample" value="${seqSampleInstance?.patSample}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleInstance, field: 'panel', 'error')} required">
	<label for="panel">
		<g:message code="seqSample.panel.label" default="Panel" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="panel" value="${seqSampleInstance?.panel}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqSampleInstance, field: 'analysis', 'error')} required">
	<label for="analysis">
		<g:message code="seqSample.analysis.label" default="Analysis"/>
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="analysis" value="${seqSampleInstance?.analysis}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: seqSampleInstance, field: 'laneNo', 'error')} required">
	<label for="laneNo">
		<g:message code="seqSample.laneNo.label" default="Lane No"/>
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="laneNo" value="${seqSampleInstance?.laneNo}"/>
</div>

<%--
<div class="fieldcontain"><label for="runDate">&nbsp;</label>
	<span id="showhide_other" style="text-decoration: underline;color:blue;cursor:pointer;">Show extra data</span>
</div>--%>



<%-- <div id="otherdata" style="display:none;"> --%>



	<div class="fieldcontain ${hasErrors(bean: seqSampleInstance, field: 'dnaconc', 'error')} ">
		<label for="dnaconc">
			<g:message code="seqSample.dnaconc.label" default="Dnaconc"/>

		</label>
		<g:field name="dnaconc" value="${fieldValue(bean: seqSampleInstance, field: 'dnaconc')}"/>
	</div>


<%-- </div> --%>
<r:script>
	$("#showhide_other").click(function()  {
		if( $("#showhide_other").text() == 'Show extra data') {
			$('#otherdata').css('display','block');
			$("#showhide_other").text('Hide extra data')
		} else {
			//hide
			$('#otherdata').css('display','none');
			$("#showhide_other").text('Show extra data')
		}
	});
</r:script>
