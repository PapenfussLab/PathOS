
<%@ page import="org.petermac.pathos.curate.PatSample" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'sample.label', default: 'Sample')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-sample" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-sample" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list sample">
			
				<g:if test="${sampleInstance?.sample}">
				<li class="fieldcontain">
					<span id="sample-label" class="property-label"><g:message code="sample.sample.label" default="Sample" /></span>
					
						<span class="property-value" aria-labelledby="sample-label"><g:fieldValue bean="${sampleInstance}" field="sample"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${sampleInstance?.patient}">
				<li class="fieldcontain">
					<span id="patient-label" class="property-label"><g:message code="sample.patient.label" default="Patient" /></span>
				    <span class="property-value" aria-labelledby="patient-label">
                        <g:link controller="patient" action="show" id="${sampleInstance?.patient?.id}"><g:patient value='name' patient="${sampleInstance?.patient?.id}"/></g:link>
                    </span>
					
				</li>
				</g:if>
			
				<g:if test="${sampleInstance?.owner}">
				<li class="fieldcontain">
					<span id="owner-label" class="property-label"><g:message code="sample.owner.label" default="Owner" /></span>
					
						<span class="property-value" aria-labelledby="owner-label"><g:link controller="User" action="show" id="${sampleInstance?.owner?.id}">${sampleInstance?.owner?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${sampleInstance?.ca2015}">
				<li class="fieldcontain">
					<span id="ca2015-label" class="property-label"><g:message code="sample.ca2015.label" default="Ca2015" /></span>
					
						<span class="property-value" aria-labelledby="ca2015-label"><g:formatBoolean boolean="${sampleInstance?.ca2015}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${sampleInstance?.collectDate}">
				<li class="fieldcontain">
					<span id="collectDate-label" class="property-label"><g:message code="sample.collectDate.label" default="Collect Date" /></span>
					
						<span class="property-value" aria-labelledby="collectDate-label"><g:formatDate date="${sampleInstance?.collectDate}" format="dd-MMM-yyyy" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${sampleInstance?.rcvdDate}">
				<li class="fieldcontain">
					<span id="rcvdDate-label" class="property-label"><g:message code="sample.rcvdDate.label" default="Rcvd Date" /></span>
					
						<span class="property-value" aria-labelledby="rcvdDate-label"><g:formatDate date="${sampleInstance?.rcvdDate}" format="dd-MMM-yyyy" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${sampleInstance?.requestDate}">
				<li class="fieldcontain">
					<span id="requestDate-label" class="property-label"><g:message code="sample.requestDate.label" default="Request Date" /></span>
					
						<span class="property-value" aria-labelledby="requestDate-label"><g:formatDate date="${sampleInstance?.requestDate}" format="dd-MMM-yyyy" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${sampleInstance?.requester}">
				<li class="fieldcontain">
					<span id="requester-label" class="property-label"><g:message code="sample.requester.label" default="Requester" /></span>
					
						<span class="property-value" aria-labelledby="requester-label"><g:fieldValue bean="${sampleInstance}" field="requester"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${sampleInstance?.tumour}">
				<li class="fieldcontain">
					<span id="tumour-label" class="property-label"><g:message code="sample.tumour.label" default="Tumour" /></span>
					
						<span class="property-value" aria-labelledby="tumour-label"><g:fieldValue bean="${sampleInstance}" field="tumour"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${sampleInstance?.pathlab}">
				<li class="fieldcontain">
					<span id="pathlab-label" class="property-label"><g:message code="sample.pathlab.label" default="Pathlab" /></span>
					
						<span class="property-value" aria-labelledby="pathlab-label"><g:fieldValue bean="${sampleInstance}" field="pathlab"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${sampleInstance?.tumourType}">
				<li class="fieldcontain">
					<span id="tumourType-label" class="property-label"><g:message code="sample.tumourType.label" default="Tumour Type" /></span>
					
						<span class="property-value" aria-labelledby="tumourType-label"><g:fieldValue bean="${sampleInstance}" field="tumourType"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${sampleInstance?.stage}">
				<li class="fieldcontain">
					<span id="stage-label" class="property-label"><g:message code="sample.stage.label" default="Stage" /></span>
					
						<span class="property-value" aria-labelledby="stage-label"><g:fieldValue bean="${sampleInstance}" field="stage"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${sampleInstance?.formalStage}">
				<li class="fieldcontain">
					<span id="formalStage-label" class="property-label"><g:message code="sample.formalStage.label" default="Formal Stage" /></span>
					
						<span class="property-value" aria-labelledby="formalStage-label"><g:fieldValue bean="${sampleInstance}" field="formalStage"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${sampleInstance?.seqSamples}">
				<li class="fieldcontain">
					<span id="seqSamples-label" class="property-label"><g:message code="sample.seqSamples.label" default="Seq Samples" /></span>
					
						<g:each in="${sampleInstance.seqSamples}" var="s">
						    <span class="property-value" aria-labelledby="seqSamples-label">
                                <g:link controller="seqVariant" action="svlist" id="${s?.id}">${s?.encodeAsHTML()}</g:link>&nbsp;
                                <g:link controller="seqrun" action="show" id="${s.seqrunId}">${s?.seqrun?.encodeAsHTML()}</g:link>
                            </span>
						</g:each>
					
				</li>
				</g:if>

                <g:if test="${sampleInstance?.patAssays}">
                    <li class="fieldcontain">
                        <span id="seqVariants-label" class="property-label"><g:message code="patient.patAssays.label" default="Pat Assays"/></span>
                        <g:each in="${sampleInstance?.patAssays}" var="s">
                            <span class="property-value" aria-labelledby="patAssays-label"><g:link controller="patAssay" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></span>
                        </g:each>
                    </li>
                </g:if>

            </ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${sampleInstance?.id}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
