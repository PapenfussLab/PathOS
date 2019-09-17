<%@ page import="org.petermac.pathos.curate.PatAssay" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'patAssay.label', default: 'SampleTest')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-patAssay" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                               default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-patAssay" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list patAssay">

        <g:if test="${patAssayInstance?.testSet}">
            <li class="fieldcontain">
                <span id="testSet-label" class="property-label"><g:message code="patAssay.testSet.label"
                                                                           default="Test Set"/></span>

                <span class="property-value" aria-labelledby="testSet-label"><g:fieldValue bean="${patAssayInstance}"
                                                                                           field="testSet"/></span>

            </li>
        </g:if>

        <g:if test="${patAssayInstance?.testName}">
            <li class="fieldcontain">
                <span id="testName-label" class="property-label"><g:message code="patAssay.testName.label"
                                                                            default="Test Name"/></span>

                <span class="property-value" aria-labelledby="testName-label"><g:fieldValue bean="${patAssayInstance}"
                                                                                            field="testName"/></span>

            </li>
        </g:if>

        <g:if test="${patAssayInstance?.patSample}">
            <li class="fieldcontain">
                <span id="sample-label" class="property-label"><g:message code="patAssay.sample.label"
                                                                          default="Sample"/></span>

                <span class="property-value" aria-labelledby="sample-label"><g:link controller="patSample" action="show"
                                                                                    id="${patAssayInstance?.patSample?.id}">${patAssayInstance?.patSample?.encodeAsHTML()}</g:link></span>

            </li>
        </g:if>

            <li class="fieldcontain">
                <span id="labAssay-label" class="property-label">Lab Assay</span>
        <g:if test="${patAssayInstance?.labAssay}">
                <span class="property-value" aria-labelledby="labAssay-label"><g:link controller="labAssay" action="show" id="${patAssayInstance?.labAssay?.id}">${patAssayInstance?.labAssay?.encodeAsHTML()}</g:link></span>
        </g:if>
        <g:else>
            <span class="property-value" aria-labelledby="labAssay-label">No Lab Assays</span>
        </g:else>

            </li>

        <g:if test="${patAssayInstance?.authDate}">
            <li class="fieldcontain">
                <span id="authDate-label" class="property-label"><g:message code="patAssay.authDate.label"
                                                                            default="Auth Date"/></span>

                <span class="property-value" aria-labelledby="authDate-label"><g:formatDate
                        date="${patAssayInstance?.authDate}" format="dd-MMM-yyyy"/></span>

            </li>
        </g:if>

    </ol>
    <g:form>
        <fieldset class="buttons">
            <g:hiddenField name="id" value="${patAssayInstance?.id}"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
