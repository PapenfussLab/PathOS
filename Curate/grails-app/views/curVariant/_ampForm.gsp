<%@ page import="org.petermac.pathos.curate.Evidence" %>

<g:set var="amp" value="${ampEvidence}"/>

<h2>Calculated  AMP Classification</h2>

<div id="calculatedAMP"></div>
<r:script>
    PathOS.criteria.drawAmp("calculatedAMP", {text:"${amp?.classification ?: "Unclassified"}"});
</r:script>

<button style="margin-top: 5px;" onclick="calculateAMP()">Calculate AMP</button>


<tooltip:tip code="evidence.justification.tip">
    <h2>Collected Somatic Evidence</h2>
</tooltip:tip>

<form id="ampEvidenceForm">
	<fieldset id="somaticEvidencePicker" class="form">


<div class="fieldcontain">
    <g:textArea placeholder="Evidence to support the AMP classification." name="ampJustification" maxlength="8000" cols="10" rows="6" value="${amp?.ampJustification}"/>
</div>




	<table id="ampEvidenceTable">
		<tr>
			<th>Category</th>
			<th><p>Therapeutic</p>
				<div class="amp-mode-div">
					<input class="amp-mode-radio" type="radio" name="therapeuticCategory" id="therapeutic-sensitive" value="sensitive" ${amp?.therapeuticCategory == 'sensitive' ? 'checked' : ''}>
					<label class="amp-mode-label" for="therapeutic-sensitive">Sensitive</label>

					<input class="amp-mode-radio" type="radio" name="therapeuticCategory" id="therapeutic-resistance" value="resistance" ${amp?.therapeuticCategory == 'resistance' ? 'checked' : ''}>
					<label class="amp-mode-label" for="therapeutic-resistance">Resistance</label>

					<input class="amp-mode-radio" type="radio" name="therapeuticCategory" id="therapeutic-unknown" value="unset" ${amp?.therapeuticCategory == 'unset' ? 'checked' : ''}>
				</div>
			</th>
			<th><p>Diagnosis</p>
				<div class="amp-mode-div">
					<input class="amp-mode-radio" type="radio" name="diagnosisCategory" id="diagnosis-positive" value="positive" ${amp?.diagnosisCategory == 'positive' ? 'checked' : ''}>
					<label class="amp-mode-label" for="diagnosis-positive">Positive</label>

					<input class="amp-mode-radio" type="radio" name="diagnosisCategory" id="diagnosis-negative" value="negative" ${amp?.diagnosisCategory == 'negative' ? 'checked' : ''}>
					<label class="amp-mode-label" for="diagnosis-negative">Negative</label>

					<input class="amp-mode-radio" type="radio" name="diagnosisCategory" id="diagnosis-unknown" value="unknown" ${amp?.diagnosisCategory == 'unset' ? 'checked' : ''}>
				</div>
			</th>
			<th><p>Prognosis</p>
				<div class="amp-mode-div">
					<input class="amp-mode-radio" type="radio" name="prognosisCategory" id="prognosis-positive" value="positive" ${amp?.prognosisCategory == 'positive' ? 'checked' : ''}>
					<label class="amp-mode-label" for="prognosis-positive">Positive</label>

					<input class="amp-mode-radio" type="radio" name="prognosisCategory" id="prognosis-negative" value="negative" ${amp?.prognosisCategory == 'negative' ? 'checked' : ''}>
					<label class="amp-mode-label" for="prognosis-negative">Negative</label>

					<input class="amp-mode-radio" type="radio" name="prognosisCategory" id="prognosis-unknown" value="unknown" ${amp?.prognosisCategory == 'unset' ? 'checked' : ''}>
				</div>
			</th>
		</tr>
		<tr>
			<td>Level&nbsp;A</td>
			<td><input type="radio" name="therapeuticRating" id="therapeuticA" value="levelA" ${amp?.therapeuticRating == 'levelA' ? 'checked' : ''}>
				<div>
					<label for="therapeuticA">
						<p>1. Biomarkers that predict response or resistance to FDA-approved therapies for a specific type of tumor
						<br>
						2. Biomarkers included in professional guidlines that predict response or resistance to therapies for a specific type of tumor</p>
					</label>
				</div>
			</td>
			<td><input type="radio" name="diagnosisRating" id="diagnosisA" value="levelA" ${amp?.diagnosisRating == 'levelA' ? 'checked' : ''}>
				<div>
					<label for="diagnosisA"><p>Biomarkers included in professional guidelines as diagnostic for a specific type of tumor</p></label>
				</div>
			</td>
			<td><input type="radio" name="prognosisRating" id="prognosisA" value="levelA" ${amp?.prognosisRating == 'levelA' ? 'checked' : ''}>
				<div>
				<label for="prognosisA"><p>Biomarkers included in professional guidelines as prognostic for a specific type of tumor</p></label>
				</div>
			</td>
		</tr>
		<tr>
			<td>Level&nbsp;B</td>
			<td><input type="radio" name="therapeuticRating" id="therapeuticB" value="levelB" ${amp?.therapeuticRating == 'levelB' ? 'checked' : ''}>
				<div>
					<label for="therapeuticB"><p>Biomarkers that predict response or resistance to therapies for a specific type of tumor based on well-powered studies with consensus from experts in the field</p></label>
				</div>
			</td>
			<td><input type="radio" name="diagnosisRating" id="diagnosisB" value="levelB" ${amp?.diagnosisRating == 'levelB' ? 'checked' : ''}>
				<div>
					<label for="diagnosisB"><p>Biomarkers of diagnostic signficance for a specific type of tumor based on well-powered studies with consensus from experts in the field</p></label>
				</div>
			</td>
			<td><input type="radio" name="prognosisRating" id="prognosisB" value="levelB" ${amp?.prognosisRating == 'levelB' ? 'checked' : ''}>
				<div>
					<label for="prognosisB"><p>Biomarkers of prognostic significance for a specific type of tumor based on well-powered studies with consensus from experts in the field</p></label>
				</div>
			</td>
		</tr>
		<tr>
			<td>Level&nbsp;C</td>
			<td><input type="radio" name="therapeuticRating" id="therapeuticC" value="levelC" ${amp?.therapeuticRating == 'levelC' ? 'checked' : ''}>
				<div>
					<label for="therapeuticC">
						<p>1. Biomarkers that predict response or resistance to therapies approved by the FDA or professional societies for a different type of tumor
						<br>
						2. Biomarkers that serve as inclusion criteria for clinical trials</p>
					</label>
				</div>
			</td>
			<td><input type="radio" name="diagnosisRating" id="diagnosisC" value="levelC" ${amp?.diagnosisRating == 'levelC' ? 'checked' : ''}>
				<div>
					<label for="diagnosisC"><p>Biomarkers of diagnostic signficance based on the results of multiple small studies</p></label>
				</div>
			</td>
			<td><input type="radio" name="prognosisRating" id="prognosisC" value="levelC" ${amp?.prognosisRating == 'levelC' ? 'checked' : ''}>
				<div>
					<label for="prognosisC"><p>Biomarkers of prognostic signficance based on the results of multiple small studies</p></label>
				</div>
			</td>
		</tr>
		<tr>
			<td>Level&nbsp;D</td>
			<td><input type="radio" name="therapeuticRating" id="therapeuticD" value="levelD" ${amp?.therapeuticRating == 'levelD' ? 'checked' : ''}>
				<div>
					<label for="therapeuticD"><p>Biomarkers that show plausible therapeutic significance based on preclinical studies</p></label>
				</div>
			</td>
			<td><input type="radio" name="diagnosisRating" id="diagnosisD" value="levelD" ${amp?.diagnosisRating == 'levelD' ? 'checked' : ''}>
				<div>
					<label for="diagnosisD"><p>Biomarkers that may assist disease diagnosis themselves or along with other biomarkers based on small studies or a few case reports</p></label>
				</div>
			</td>
			<td><input type="radio" name="prognosisRating" id="prognosisD" value="levelD" ${amp?.prognosisRating == 'levelD' ? 'checked' : ''}>
				<div>
					<label for="prognosisD"><p>Biomarkers that may assist disease prognosis themselves or along with other biomarkers based on small studies or a few case reports</p></label>
				</div>
			</td>
		</tr>

	</table>

	<div style="display: none;">
		<input type="radio" name="therapeuticRating" id="therapeuticLevel-unknown" value="unset" ${amp?.therapeuticRating == 'unset' ? 'checked' : ''}>
		<input type="radio" name="diagnosisRating" id="diagnosisLevel-unknown" value="unset" ${amp?.diagnosisRating == 'unset' ? 'checked' : ''}>
		<input type="radio" name="prognosisRating" id="prognosisLevel-unknown" value="unset" ${amp?.prognosisRating == 'unset' ? 'checked' : ''}>
	</div>


</fieldset>
</form>


<r:script>

function calculateAMP(callback) {

	var package = {
	    ampEvidence: $("#ampEvidenceForm").serializeArray()
	};

	$.ajax({
		url: '/PathOS/curVariant/calculateAmp',
		type: 'POST',
		success: function(d){
			PathOS.criteria.drawAmp("calculatedAMP",{text: d.classification});

		    var flag = d.classification.replace(" ","-");
			$("#ampFlag").text(d.classification)
				.removeClass()
				.addClass("amp-"+flag, true);

			if(callback) {
				callback(d.classification);
			}
		},
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		data: JSON.stringify(package)
	});

}

$("#ampEvidenceForm label.amp-mode-label").on('click', function(d){
	event.preventDefault();

	var id = $(this).attr('for');
	var type = id.split("-")[0];
	var radio = $("#"+id);

	if(radio.is(":checked")) {
	    radio.prop("checked", false);
	    $("#"+type+"-unknown").prop("checked", true);
	} else {
		radio.prop("checked", true);
	}
});


$("#ampEvidenceTable tr td label").on('mousedown', function(d){
	event.preventDefault();

	var id = $(this).attr('for');
	var type = id.slice(0,-1);
	var radio = $("#"+id);

	if(radio.is(":checked")) {
		setTimeout(function() {
			// radio.prop("checked", false);
			$("#" + type + "Level-unknown").prop("checked", true);
		}, 300);
	} else {
		radio.prop("checked", true);
	}
});


</r:script>








