<%@ page import="org.petermac.pathos.curate.PatAssay" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'patAssay.label', default: 'patAssay')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <r:require module="filterpane"/>
</head>

<body>
<a href="#list-patAssay" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                 default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>

<div id="list-patAssay" class="content scaffold-list" role="main"
     style="white-space: nowrap; width: device-width; height: device-height; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <filterpane:currentCriteria domainBean="PatAssay"
                                removeImgDir="images" removeImgFile="bullet_delete.png"
                                fullAssociationPathFieldNames="no"/>

    <table>
        <thead>
        <tr>

            <g:sortableColumn property="testSet"
                              title="${message(code: 'patAssay.testSet.label', default: 'Test Set')}"
                              params="${filterParams}"/>

            <g:sortableColumn property="testName"
                              title="${message(code: 'patAssay.testName.label', default: 'Test Name')}"
                              params="${filterParams}"/>

            <th><g:message code="patAssay.patSample.label" default="Sample"/></th>

            <th><g:message code="patAssay.panel.label" default="Panel"/></th>

            <g:sortableColumn property="authDate"
                              title="${message(code: 'patAssay.authDate.label', default: 'Auth Date')}"
                              params="${filterParams}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${patAssayList}" status="i" var="patAssayInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show"
                            id="${patAssayInstance.id}">${fieldValue(bean: patAssayInstance, field: "testSet")}</g:link></td>

                <td>${fieldValue(bean: patAssayInstance, field: "testName")}</td>

                <td>${fieldValue(bean: patAssayInstance, field: "patSample")}</td>

                <td>${fieldValue(bean: patAssayInstance, field: "panel")}</td>

                <td><g:formatDate date="${patAssayInstance.authDate}" format="dd-MMM-yyyy"/></td>

            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <filterpane:paginate total="${patAssayCount}" domainBean="PatAssay"/>
        <filterpane:filterButton text="Filter" appliedText="Change Filter"/>
        <filterpane:isNotFiltered>No filter</filterpane:isNotFiltered>
        <filterpane:isFiltered>Filtered!</filterpane:isFiltered>
    </div>
    <filterpane:filterPane domain="PatAssay" dialog="y"/>
</div>
</body>
</html>
