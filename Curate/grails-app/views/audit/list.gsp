<%@ page import="org.petermac.pathos.curate.Audit" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'audit.label', default: 'Audit')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <r:require module="filterpane"/>
</head>

<body>
<a href="#list-audit" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                            default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>

<div id="list-audit" class="content scaffold-list" role="main"
     style="white-space: nowrap; width: device-width; height: device-height; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <filterpane:currentCriteria domainBean="Audit"
                                removeImgDir="images" removeImgFile="bullet_delete.png"
                                fullAssociationPathFieldNames="no"/>

    <table>
        <thead>
        <tr>

            <g:sortableColumn property="category" title="${message(code: 'audit.category.label', default: 'Category')}"
                              params="${filterParams}"/>

            <g:sortableColumn property="seqrun" title="${message(code: 'audit.seqrun.label', default: 'Seqrun')}"
                              params="${filterParams}"/>

            <g:sortableColumn property="variant" title="${message(code: 'audit.variant.label', default: 'CurVariant')}"
                              params="${filterParams}"/>

            <g:sortableColumn property="sample" title="${message(code: 'audit.sample.label', default: 'Sample')}"
                              params="${filterParams}"/>

            <g:sortableColumn property="task" title="${message(code: 'audit.task.label', default: 'Task')}"
                              params="${filterParams}"/>

            <g:sortableColumn property="complete" title="${message(code: 'audit.complete.label', default: 'Complete')}"
                              params="${filterParams}"/>

            <g:sortableColumn property="elapsed" title="${message(code: 'audit.elapsed.label', default: 'Elapsed')}"
                              params="${filterParams}"/>

            <g:sortableColumn property="software" title="${message(code: 'audit.software.label', default: 'Software')}"
                              params="${filterParams}"/>

            <g:sortableColumn property="swVersion"
                              title="${message(code: 'audit.swVersion.label', default: 'Sw Version')}"
                              params="${filterParams}"/>

            <g:sortableColumn property="username" title="${message(code: 'audit.username.label', default: 'Username')}"
                              params="${filterParams}"/>

            <g:sortableColumn property="description"
                              title="${message(code: 'audit.description.label', default: 'Description')}"
                              params="${filterParams}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${auditList}" status="i" var="auditInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show"
                            id="${auditInstance.id}">${fieldValue(bean: auditInstance, field: "category")}</g:link></td>

                <td>${fieldValue(bean: auditInstance, field: "seqrun")}</td>

                <td>${fieldValue(bean: auditInstance, field: "variant")}</td>

                <td>${fieldValue(bean: auditInstance, field: "sample")}</td>

                <td>${fieldValue(bean: auditInstance, field: "task")}</td>

                <td><g:formatDate date="${auditInstance.complete}" format="dd-MMM-yyyy"/></td>

                <td>${fieldValue(bean: auditInstance, field: "elapsed")}</td>

                <td>${fieldValue(bean: auditInstance, field: "software")}</td>

                <td>${fieldValue(bean: auditInstance, field: "swVersion")}</td>

                <td>${fieldValue(bean: auditInstance, field: "username")}</td>

                <td>${fieldValue(bean: auditInstance, field: "description")}</td>

            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <filterpane:paginate total="${auditCount}" domainBean="Audit"/>
        <filterpane:filterButton text="Filter" appliedText="Change Filter"/>
        <filterpane:isNotFiltered>No filter</filterpane:isNotFiltered>
        <filterpane:isFiltered>Filtered!</filterpane:isFiltered>
    </div>
    <filterpane:filterPane domain="Audit"/>
</div>
</body>
</html>
