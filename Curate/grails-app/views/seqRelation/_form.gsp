<%@ page import="org.petermac.pathos.curate.SeqRelation" %>
<style type="text/css">

#addSample {
	text-decoration: underline;
	color: blue;
	cursor: pointer;

}</style>
%{--CSS Files--}%
<link href="<g:resource plugin='easygrid' dir='jquery-ui-1.11.0' file='jquery-ui.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
<link href="<g:resource dir='css/jquery-ui-1.11.0.custom' file='jquery-ui.theme.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
<link href="<g:resource plugin='easygrid' dir='jquery.jqGrid-4.6.0/css' file='ui.jqgrid.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
<link href="<g:resource plugin='easygrid' dir='jquery.jqGrid-4.6.0/plugins' file='ui.multiselect.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />

%{--Javascript Files--}%
<g:javascript src="quasipartikel/jquery.min.js" />
<g:javascript src="quasipartikel/jquery-ui.min.js" />
<g:javascript src="quasipartikel/ui.multiselect.js" />
<g:javascript src='jquery/jquery.jgrowl.js' plugin='spring-security-ui'/>


<div class="fieldcontain ${hasErrors(bean: seqRelationInstance, field: 'base', 'error')} ">
	<label for="base" style="float:left;text-align:right;">
		<g:message code="seqRelation.base.label" default="Base" />
		
	</label>	 <span class="property-value">${seqRelationInstance?.base}</span>
	<%--<g:textField name="base" value="${seqRelationInstance?.base}"/>--%>
</div>
<g:if test="${seqRelationInstance?.samples()}">
	<div class="fieldcontain ${hasErrors(bean: seqRelationInstance, field: 'samples', 'error')} ">
		<label for="samples" style="float:left;text-align:right;">
			<g:message code="seqRelation.samples.label" default="Samples" />
			<%--- need a lookup -- select & autocomplete RUN and Sample ... js edit a hidden div to add it. going to be quite agricultural. ----%>
		</label>
		<%-- show list of samples --%>
		<g:each in="${seqRelationInstance?.samples().sort{it.id}}" var="s">
			 <span id="show_${s.seqrun}_${s.sampleName}" class="property-value"><g:link controller="seqSample" action="show" id="${s.id}">${s.seqrun} ${s.sampleName} ${s.sampleType?"(${s.sampleType})":"(no sample type)"}</g:link> <g:checkBox name="remove_${s.id}" value="${s.id}" checked="false"/> Remove <br/></span>
		</g:each>
	</div>
</g:if>


<div class="fieldcontain">
	<%--- samples to be added ---%>
	<label for="samples" style="float:left;text-align:right;">Samples to Add</label>
	<span id="samples_to_add" class="property-value" style="color:grey;"></span>

</div>
<br/>
<div class="fieldcontain">
	<label style="float:left;text-align:right;">Add new SeqSample:</label>
	<span class="property-value">
		<g:select id="seqrun_add" name="seqrun_add" from="${org.petermac.pathos.curate.Seqrun.list(sort:"runDate",order:"desc")}" noSelection="${['': 'Select seqrun...']}"
				onChange="${remoteFunction( action:'updateSeqSamplesForSeqrun',
				params: '\'seqrun=\'+escape(this.value)',
				update: [success: 'seqsample_add'] )}" />

		<g:select name="seqsample_add" from="[]" noSelection="${[null: 'Select sample...']}" />

		<%-- AES grails 3 changes this: use constrainedProperties, not constraints, when we eventually upgrade --%>
		<g:select name="seqsample_set_type" from="${org.petermac.pathos.curate.SeqSample.constraints.sampleType['inList']}" noSelection="${[null: 'No sample type']}"  />

		<span id="addSample">Add</span>
	</span>


</div>
<div id="hiddenfields"><%--use this div to append hidden fields to--%></div>


<%-- updateSeqSamplesForSeqrun --%>
<script>

	//remote function call to remove seqsample
	$( document ).ready(function() {
		var order = 0
		//	add a new seqsample to this seqrelation
		//	this adds a seqsample to the form - the form must still be submitted for the ss to be added
		//	since this form is for both seqrelation create and edit
		$('#addSample').click(function(){

			var seqrun = $('#seqrun_add').val()
			var ss = $('#seqsample_add').val()
            var st = $('#seqsample_set_type').val()
            if (st == null || st == "null") { st = "" }
			//	add a new hidden field to the form. json arrya with seqrun and ss
			//	only do this if one does not already exist (to prevent the user from adding the same ss multiple times)
			if (ss  && !$("#add_"+seqrun+"_"+ss).length   && !$("#show_"+seqrun+"_"+ss).length) {

				$('<input />').attr('type', 'hidden')
						.attr('name', "add_"+seqrun+"_"+ss)
						.attr('id', "add_"+seqrun+"_"+ss)
						.attr('value', '[{"seqrun":"'+seqrun+'","seqsample":"'+ss+'","sampletype":"'+st+'","order":"'+order+'"}]')


						.appendTo('#hiddenfields');

				//	we must preserve order (for derived samples, this matters). order is not guaranteed when we get the form data back in the controller.
				order = order + 1

				//	append to a div to show the user
			    //
                    if (st == null || st == "null" || st == "") { st = " (no sample type)" }

					$('#samples_to_add').append("<div>" + seqrun + " " + ss + " (sample type:" + st + ')</div>')
			}

		});

		$("#seqsample_add").change(function() {
			//get seqsample by name and run
			//set sampletype to its value
			var selectedSample = $('#seqsample_add').val()
			var selectedRun = $('#seqrun_add').val()


			${remoteFunction(action:'getSampleTypeBySampleNameAndSeqrun', params: '{seqsample: selectedSample,seqrun: selectedRun}', update: [success: 'seqsample_set_type'] )} // ,

		});






	});
</script>
