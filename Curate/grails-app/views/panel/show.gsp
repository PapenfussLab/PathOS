<%@ page import="org.petermac.pathos.curate.Seqrun; org.petermac.pathos.curate.Panel" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'panel.label', default: 'Panel')}"/>
    <title>${panelInstance?.manifest} - <g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-panel" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                            default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-panel" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list panel">

        <g:if test="${panelInstance?.manifest}">
            <li class="fieldcontain">
                <span id="manifest-label" class="property-label"><g:message code="panel.manifest.label" default="Manifest"/></span>
                <span class="property-value" aria-labelledby="manifest-label"><g:fieldValue bean="${panelInstance}" field="manifest"/></span>
            </li>
        </g:if>

        <g:if test="${panelInstance?.description}">
            <li class="fieldcontain">
                <span id="description-label" class="property-label"><g:message code="panel.description.label" default="Description"/></span>
                <span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${panelInstance}" field="description"/></span>
            </li>
        </g:if>

        <g:if test="${panelInstance?.panelGroup}">
            <li class="fieldcontain">
                <span id="panelGroup-label" class="property-label"><g:message code="panel.panelGroup.label" default="Filter Group"/></span>
                <span class="property-value" aria-labelledby="panelGroup-label"><g:fieldValue bean="${panelInstance}" field="panelGroup"/></span>
            </li>
        </g:if>

        <li class="fieldcontain">
            <span id="skipGeneMask-label" class="property-label">Gene Mask</span>
            <span class="property-value" aria-labelledby="skipGeneMask-label">
                ${panelInstance?.skipGeneMask ? "Don't show gene mask" : "Show gene mask"}
            </span>
        </li>

        <li class="fieldcontain">
            <span id="samples-label" class="property-label"><g:message code="panel.samples.label" default="Samples"/></span>
            <span class="property-value" aria-labelledby="samples-label">
                ${panelInstance?.seqSamples?.size()}
            </span>
        </li>

    </ol>
</div>
</body>
</html>
