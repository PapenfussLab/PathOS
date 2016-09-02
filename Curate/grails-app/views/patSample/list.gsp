<%@ page import="org.petermac.pathos.curate.PatSample" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'sample.label', default: 'Sample')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
    <r:require module="filterpane" />
</head>
<body>
<a href="#list-sample" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="list-sample" class="content scaffold-list" role="main" style="white-space: nowrap; width: device-width; height: device-height; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]" /></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <filterpane:currentCriteria domainBean="PatSample"
                                removeImgDir="images" removeImgFile="bullet_delete.png" fullAssociationPathFieldNames="no" />

    <table>
        <thead>
        <tr>

            <g:sortableColumn property="sample" title="${message(code: 'sample.sample.label', default: 'Sample')}"  params="${filterParams}"/>

            <th><g:message code="sample.patient.label" default="Patient" /></th>

            <th><g:message code="sample.owner.label" default="Owner" /></th>

            <g:sortableColumn property="ca2015" title="${message(code: 'sample.ca2015.label', default: 'Ca2015')}"  params="${filterParams}"/>

            <g:sortableColumn property="collectDate" title="${message(code: 'sample.collectDate.label', default: 'Collect Date')}"  params="${filterParams}"/>

            <g:sortableColumn property="rcvdDate" title="${message(code: 'sample.rcvdDate.label', default: 'Rcvd Date')}"  params="${filterParams}"/>

            <g:sortableColumn property="requestDate" title="${message(code: 'sample.requestDate.label', default: 'Request Date')}"  params="${filterParams}"/>

            <g:sortableColumn property="requester" title="${message(code: 'sample.requester.label', default: 'Requester')}"  params="${filterParams}"/>

            <g:sortableColumn property="tumour" title="${message(code: 'sample.tumour.label', default: 'Tumour')}"  params="${filterParams}"/>

            <g:sortableColumn property="pathlab" title="${message(code: 'sample.pathlab.label', default: 'Pathlab')}"  params="${filterParams}"/>

            <g:sortableColumn property="tumourType" title="${message(code: 'sample.tumourType.label', default: 'Tumour Type')}"  params="${filterParams}"/>

            <g:sortableColumn property="stage" title="${message(code: 'sample.stage.label', default: 'Stage')}"  params="${filterParams}"/>

            <g:sortableColumn property="formalStage" title="${message(code: 'sample.formalStage.label', default: 'Formal Stage')}"  params="${filterParams}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${patSampleList}" status="i" var="sampleInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show" id="${sampleInstance.id}">${fieldValue(bean: sampleInstance, field: "sample")}</g:link></td>

                <td><g:patient value='name' patient="${sampleInstance?.patient?.id}"/></td>

                <td>${fieldValue(bean: sampleInstance, field: "owner")}</td>

                <td><g:formatBoolean boolean="${sampleInstance.ca2015}" /></td>

                <td><g:formatDate date="${sampleInstance.collectDate}"  format="dd-MMM-yyyy" /></td>

                <td><g:formatDate date="${sampleInstance.rcvdDate}"  format="dd-MMM-yyyy" /></td>

                <td><g:formatDate date="${sampleInstance.requestDate}"  format="dd-MMM-yyyy" /></td>

                <td>${fieldValue(bean: sampleInstance, field: "requester")}</td>

                <td>${fieldValue(bean: sampleInstance, field: "tumour")}</td>

                <td>${fieldValue(bean: sampleInstance, field: "pathlab")}</td>

                <td>${fieldValue(bean: sampleInstance, field: "tumourType")}</td>

                <td>${fieldValue(bean: sampleInstance, field: "stage")}</td>

                <td>${fieldValue(bean: sampleInstance, field: "formalStage")}</td>

            </tr>
        </g:each>
        </tbody>
    </table>
    <div class="pagination">
        <filterpane:paginate total="${patSampleCount}" domainBean="PatSample"/>
        <filterpane:filterButton text="Filter" appliedText="Change Filter"/>
        <filterpane:isNotFiltered>No filter</filterpane:isNotFiltered>
        <filterpane:isFiltered>Filtered!</filterpane:isFiltered>
    </div>
    <filterpane:filterPane domain="PatSample" dialog="y"/>
</div>
</body>
</html>
