<%@ page import="org.petermac.pathos.curate.Seqrun" %>



<div class="fieldcontain ${hasErrors(bean: seqrunInstance, field: 'seqrun', 'error')} ">
	<label for="seqrun">
		Seqrun Name<span class="required-indicator">*</span>
		
	</label>
	<g:textField name="seqrun" value="${seqrunInstance?.seqrun}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqrunInstance, field: 'runDate', 'error')} required">
	<label for="runDate">
		<g:message code="seqrun.runDate.label" default="Run Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="runDate" precision="day"  value="${seqrunInstance?.runDate}"  />
</div>

<div class="fieldcontain"><label for="runDate">&nbsp;</label>
<span id="showhide_other" style="text-decoration: underline;color:blue;cursor:pointer;">Show extra data</span>
</div>
<div id="otherdata" style="display:none;">

<div class="fieldcontain ${hasErrors(bean: seqrunInstance, field: 'platform', 'error')} ">
	<label for="platform">
		<g:message code="seqrun.platform.label" default="Platform" />
		
	</label>
	<g:textField name="platform" value="${seqrunInstance?.platform}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqrunInstance, field: 'sepe', 'error')} ">
	<label for="sepe">
		<g:message code="seqrun.sepe.label" default="Sepe" />
		
	</label>
	<g:textField name="sepe" value="${seqrunInstance?.sepe}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqrunInstance, field: 'readlen', 'error')} ">
	<label for="readlen">
		<g:message code="seqrun.readlen.label" default="Readlen" />
		
	</label>
	<g:textField name="readlen" value="${seqrunInstance?.readlen}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqrunInstance, field: 'library', 'error')} ">
	<label for="library">
		<g:message code="seqrun.library.label" default="Library" />
		
	</label>
	<g:textField name="library" value="${seqrunInstance?.library}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqrunInstance, field: 'experiment', 'error')} ">
	<label for="experiment">
		<g:message code="seqrun.experiment.label" default="Experiment" />
		
	</label>
	<g:textField name="experiment" value="${seqrunInstance?.experiment}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: seqrunInstance, field: 'scanner', 'error')} ">
	<label for="scanner">
		<g:message code="seqrun.scanner.label" default="Scanner" />
		
	</label>
	<g:textField name="scanner" value="${seqrunInstance?.scanner}"/>
</div>


</div>

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