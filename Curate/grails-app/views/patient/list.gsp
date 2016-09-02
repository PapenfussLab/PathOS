<%@ page import="org.petermac.pathos.curate.Patient" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'patient.label', default: 'Patient')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <r:require module="filterpane"/>
</head>

<body>
<a href="#list-patient" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                              default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>

<div id="list-patient" class="content scaffold-list" role="main"
     style="white-space: nowrap; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <filterpane:currentCriteria domainBean="Patient"
                                removeImgDir="images" removeImgFile="bullet_delete.png"
                                fullAssociationPathFieldNames="no"/>

    <table>
        <thead>
        <tr>
            <g:sortableColumn property="fullName" title="${message(code: 'patient.fullName.label', default: 'Full Name')}" params="${filterParams}"/>

            <g:sortableColumn property="urn" title="${message(code: 'patient.urn.label', default: 'Urn')}" params="${filterParams}"/>

            <g:sortableColumn property="dob" title="${message(code: 'patient.dob.label', default: 'Dob')}" params="${filterParams}"/>

            <g:sortableColumn property="sex" title="${message(code: 'patient.sex.label', default: 'Sex')}" params="${filterParams}"/>
        </tr>
        </thead>
        <tbody>
        <g:each in="${patientList}" status="i" var="patientInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show" id="${patientInstance.id}"><g:patient value='name' patient="${patientInstance.id}"/></g:link></td>

                <td><g:patient value='urn' patient="${patientInstance.id}"/></td>

                <td><g:patient value='dob' patient="${patientInstance.id}"/></td>

                <td>${fieldValue(bean: patientInstance, field: "sex")}</td>
            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <filterpane:paginate total="${patientCount}" domainBean="Patient"/>
        <filterpane:filterButton text="Filter" appliedText="Change Filter"/>
        <filterpane:isNotFiltered>No filter</filterpane:isNotFiltered>
        <filterpane:isFiltered>Filtered!</filterpane:isFiltered>
    </div>
    <filterpane:filterPane domain="Patient" dialog="y"/>
</div>
</body>
</html>
