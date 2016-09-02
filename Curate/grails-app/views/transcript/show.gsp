
<%@ page import="org.petermac.pathos.curate.Transcript" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'transcript.label', default: 'Transcript')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-transcript" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-transcript" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list transcript">
			
				<g:if test="${transcriptInstance?.accession}">
				<li class="fieldcontain">
					<span id="accession-label" class="property-label"><g:message code="transcript.accession.label" default="Accession" /></span>
					
						<span class="property-value" aria-labelledby="accession-label"><g:fieldValue bean="${transcriptInstance}" field="accession"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.build}">
				<li class="fieldcontain">
					<span id="build-label" class="property-label"><g:message code="transcript.build.label" default="Build" /></span>
					
						<span class="property-value" aria-labelledby="build-label"><g:fieldValue bean="${transcriptInstance}" field="build"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.cds_size}">
				<li class="fieldcontain">
					<span id="cds_size-label" class="property-label"><g:message code="transcript.cds_size.label" default="Cdssize" /></span>
					
						<span class="property-value" aria-labelledby="cds_size-label"><g:fieldValue bean="${transcriptInstance}" field="cds_size"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.cds_start}">
				<li class="fieldcontain">
					<span id="cds_start-label" class="property-label"><g:message code="transcript.cds_start.label" default="Cdsstart" /></span>
					
						<span class="property-value" aria-labelledby="cds_start-label"><g:fieldValue bean="${transcriptInstance}" field="cds_start"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.cds_stop}">
				<li class="fieldcontain">
					<span id="cds_stop-label" class="property-label"><g:message code="transcript.cds_stop.label" default="Cdsstop" /></span>
					
						<span class="property-value" aria-labelledby="cds_stop-label"><g:fieldValue bean="${transcriptInstance}" field="cds_stop"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.chr_refseq}">
				<li class="fieldcontain">
					<span id="chr_refseq-label" class="property-label"><g:message code="transcript.chr_refseq.label" default="Chrrefseq" /></span>
					
						<span class="property-value" aria-labelledby="chr_refseq-label"><g:fieldValue bean="${transcriptInstance}" field="chr_refseq"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.chromosome}">
				<li class="fieldcontain">
					<span id="chromosome-label" class="property-label"><g:message code="transcript.chromosome.label" default="Chromosome" /></span>
					
						<span class="property-value" aria-labelledby="chromosome-label"><g:fieldValue bean="${transcriptInstance}" field="chromosome"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.exCount}">
				<li class="fieldcontain">
					<span id="exCount-label" class="property-label"><g:message code="transcript.exCount.label" default="Ex Count" /></span>
					
						<span class="property-value" aria-labelledby="exCount-label"><g:fieldValue bean="${transcriptInstance}" field="exCount"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.exSize}">
				<li class="fieldcontain">
					<span id="exSize-label" class="property-label"><g:message code="transcript.exSize.label" default="Ex Size" /></span>
					
						<span class="property-value" aria-labelledby="exSize-label"><g:fieldValue bean="${transcriptInstance}" field="exSize"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.exon_starts}">
				<li class="fieldcontain">
					<span id="exon_starts-label" class="property-label"><g:message code="transcript.exon_starts.label" default="Exonstarts" /></span>
					
						<span class="property-value" aria-labelledby="exon_starts-label"><g:fieldValue bean="${transcriptInstance}" field="exon_starts"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.exon_stops}">
				<li class="fieldcontain">
					<span id="exon_stops-label" class="property-label"><g:message code="transcript.exon_stops.label" default="Exonstops" /></span>
					
						<span class="property-value" aria-labelledby="exon_stops-label"><g:fieldValue bean="${transcriptInstance}" field="exon_stops"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.genbuild}">
				<li class="fieldcontain">
					<span id="genbuild-label" class="property-label"><g:message code="transcript.genbuild.label" default="Genbuild" /></span>
					
						<span class="property-value" aria-labelledby="genbuild-label"><g:fieldValue bean="${transcriptInstance}" field="genbuild"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.gene}">
				<li class="fieldcontain">
					<span id="gene-label" class="property-label"><g:message code="transcript.gene.label" default="Gene" /></span>
					
						<span class="property-value" aria-labelledby="gene-label"><g:fieldValue bean="${transcriptInstance}" field="gene"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.lrg}">
				<li class="fieldcontain">
					<span id="lrg-label" class="property-label"><g:message code="transcript.lrg.label" default="Lrg" /></span>
					
						<span class="property-value" aria-labelledby="lrg-label"><g:fieldValue bean="${transcriptInstance}" field="lrg"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.preferred}">
				<li class="fieldcontain">
					<span id="preferred-label" class="property-label"><g:message code="transcript.preferred.label" default="Preferred" /></span>
					
						<span class="property-value" aria-labelledby="preferred-label"><g:formatBoolean boolean="${transcriptInstance?.preferred}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.refseq}">
				<li class="fieldcontain">
					<span id="refseq-label" class="property-label"><g:message code="transcript.refseq.label" default="Refseq" /></span>
					
						<span class="property-value" aria-labelledby="refseq-label"><g:fieldValue bean="${transcriptInstance}" field="refseq"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.source}">
				<li class="fieldcontain">
					<span id="source-label" class="property-label"><g:message code="transcript.source.label" default="Source" /></span>
					
						<span class="property-value" aria-labelledby="source-label"><g:fieldValue bean="${transcriptInstance}" field="source"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.strand}">
				<li class="fieldcontain">
					<span id="strand-label" class="property-label"><g:message code="transcript.strand.label" default="Strand" /></span>
					
						<span class="property-value" aria-labelledby="strand-label"><g:fieldValue bean="${transcriptInstance}" field="strand"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.ts_size}">
				<li class="fieldcontain">
					<span id="ts_size-label" class="property-label"><g:message code="transcript.ts_size.label" default="Tssize" /></span>
					
						<span class="property-value" aria-labelledby="ts_size-label"><g:fieldValue bean="${transcriptInstance}" field="ts_size"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.ts_start}">
				<li class="fieldcontain">
					<span id="ts_start-label" class="property-label"><g:message code="transcript.ts_start.label" default="Tsstart" /></span>
					
						<span class="property-value" aria-labelledby="ts_start-label"><g:fieldValue bean="${transcriptInstance}" field="ts_start"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transcriptInstance?.ts_stop}">
				<li class="fieldcontain">
					<span id="ts_stop-label" class="property-label"><g:message code="transcript.ts_stop.label" default="Tsstop" /></span>
					
						<span class="property-value" aria-labelledby="ts_stop-label"><g:fieldValue bean="${transcriptInstance}" field="ts_stop"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${transcriptInstance?.id}" />
					<g:link class="edit" action="edit" id="${transcriptInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
