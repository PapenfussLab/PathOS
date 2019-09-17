<%@ page import="org.petermac.pathos.curate.CurVariantReport" %>

<div class="col-xs-4">
	<table>
		<thead>
			<tr>
				<th>Mail merge</th>
				<th>Value</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td><label for="sample">sample</label></td>
				<td><g:textField readonly="1" name="sample" value="${curVariantReportInstance?.sample}"/></td>
			</tr>
			<tr>
				<td><label for="gene">gene</label></td>
				<td><g:textField readonly="1" name="gene" value="${curVariantReportInstance?.gene}"/></td>
			</tr>
			<tr>
				<td><label for="exon">exon</label></td>
				<td><g:textField readonly="1" name="exon" value="${curVariantReportInstance?.exon}"/></td>
			</tr>
			<tr>
				<td><label for="class">class</label></td>
				<td><g:textField class="cv-${curVariantReportInstance?.pmClass?.split(":")[0]}" readonly="1" name="class" value="${curVariantReportInstance?.pmClass}"/></td>
			</tr>
			<tr>
				<td><label for="ampClass">ampClass</label></td>
				<td><g:textField class="amp-${curVariantReportInstance?.ampClass?.replace(" ","-") ?: "Unclassified"}" readonly="1" name="ampClass" value="${curVariantReportInstance?.ampClass}"/></td>
			</tr>
			<tr>
				<td><label for="clinicalSignificance">clinicalSignificance</label></td>
				<td><g:textField readonly="1" name="clinicalSignificance" value="${curVariantReportInstance?.clinicalSignificance}"/></td>
			</tr>
			<tr>
				<td><label for="refseq">refseq</label></td>
				<td><g:textField readonly="1" name="refseq" value="${curVariantReportInstance?.refseq}"/></td>
			</tr>
			<tr>
				<td><label for="hgvsc">hgvsc</label></td>
				<td><g:textField readonly="1" name="hgvsc" value="${curVariantReportInstance?.hgvsc}"/></td>
			</tr>
			<tr>
				<td><label for="hgvsp">hgvsp</label></td>
				<td><g:textField readonly="1" name="hgvsp" value="${curVariantReportInstance?.hgvsp}"/></td>
			</tr>
			<tr>
				<td><label for="refseqNP">refseqNP</label></td>
				<td><g:textField readonly="1" name="refseqNP" value="${curVariantReportInstance?.refseqNP}"/></td>
			</tr>
			<tr>
				<td><label for="aaChange">aaChange</label></td>
				<td><g:textField readonly="1" name="aaChange" value="${curVariantReportInstance?.aaChange}"/></td>
			</tr>
			<tr>
				<td><label for="varreaddepth">varreaddepth</label></td>
				<td><g:textField readonly="1" name="varreaddepth" value="${curVariantReportInstance?.varreaddepth}"/></td>
			</tr>
			<tr>
				<td><label for="totalreaddepth">totalreaddepth</label></td>
				<td><g:textField readonly="1" name="totalreaddepth" value="${curVariantReportInstance?.totalreaddepth}"/></td>
			</tr>
			<tr>
				<td><label for="afpct">afpct</label></td>
				<td><g:textField readonly="1" name="afpct" value="${curVariantReportInstance?.afpct}"/></td>
			</tr>
		</tbody>
	</table>
</div>
<div class="col-xs-8">
	<div class="fieldcontain ${hasErrors(bean: curVariantReportInstance, field: 'mut', 'error')} ">
		<label for="mut">mut</label><br>
		<g:textArea class="highlightPMIDs" name="mut" cols="40" rows="7" maxlength="8000" value="${curVariantReportInstance?.mut}"/>
	</div>

	<div class="fieldcontain ${hasErrors(bean: curVariantReportInstance, field: 'genedesc', 'error')} ">
		<label for="genedesc">genedesc</label><br>
		<g:textArea class="highlightPMIDs" name="genedesc" cols="40" rows="7" maxlength="8000" value="${curVariantReportInstance?.genedesc}"/>
	</div>
</div>

