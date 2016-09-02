<%@ page import="org.petermac.pathos.curate.Patient" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'patient.label', default: 'Patient')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-patient" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                              default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-patient" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list patient">

        <g:if test="${patientInstance?.fullName}">
            <li class="fieldcontain">
                <span id="fullName-label" class="property-label"><g:message code="patient.fullName.label" default="Full Name"/></span>
                <span class="property-value" aria-labelledby="fullName-label"><g:patient value='name' patient="${patientInstance.id}"/></span>
            </li>
        </g:if>

        <g:if test="${patientInstance?.urn}">
            <li class="fieldcontain">
                <span id="urn-label" class="property-label"><g:message code="patient.urn.label" default="Urn"/></span>
                <span class="property-value" aria-labelledby="urn-label"><g:patient value='urn' patient="${patientInstance.id}"/></span>
            </li>
        </g:if>

        <g:if test="${patientInstance?.dob}">
            <li class="fieldcontain">
                <span id="dob-label" class="property-label"><g:message code="patient.dob.label" default="Dob"/></span>
                <span class="property-value" aria-labelledby="dob-label"><g:patient value='dob' patient="${patientInstance.id}"/></span>
            </li>
        </g:if>

        <g:if test="${patientInstance?.sex}">
            <li class="fieldcontain">
                <span id="sex-label" class="property-label"><g:message code="patient.sex.label" default="Sex"/></span>
                <span class="property-value" aria-labelledby="sex-label"><g:fieldValue bean="${patientInstance}" field="sex"/></span>
            </li>
        </g:if>

        <g:if test="${patientInstance?.patSamples}">
            <li class="fieldcontain">
                <span id="seqVariants-label" class="property-label"><g:message code="patient.samples.label" default="Samples"/></span>
                <g:each in="${patientInstance?.patSamples}" var="s">
                    <span class="property-value" aria-labelledby="seqVariants-label"><g:link controller="patSample" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></span>
                </g:each>

            </li>
        </g:if>

    </ol>
    <g:form>
        <fieldset class="buttons">
            <g:hiddenField name="id" value="${patientInstance?.id}"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
