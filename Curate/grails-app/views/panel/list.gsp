<%@ page import="org.petermac.pathos.curate.SeqSample; org.petermac.pathos.curate.Panel" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'panel.label', default: 'Panel')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <r:require module="filterpane"/>
</head>

<body>
<a href="#list-panel" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                            default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>

<div id="list-panel" class="content scaffold-list" role="main"
     style="white-space: nowrap; width: device-width; height: device-height; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <filterpane:currentCriteria domainBean="Panel"
                                removeImgDir="images" removeImgFile="bullet_delete.png"
                                fullAssociationPathFieldNames="no"/>

    <table>
        <thead>
        <tr>

            <g:sortableColumn property="manifest" title="${message(code: 'panel.manifest.label', default: 'Manifest')}"
                              params="${filterParams}"/>

            <th>No. Samples</th>

            <g:sortableColumn property="panelGroup"
                              title="${message(code: 'panel.panelGroup.label', default: 'Filter Group')}"
                              params="${filterParams}"/>

            <g:sortableColumn property="description"
                              title="${message(code: 'panel.description.label', default: 'Description')}"
                              params="${filterParams}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${panelList}" status="i" var="panelInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show" id="${panelInstance.id}">${fieldValue(bean: panelInstance, field: "manifest")}</g:link></td>

                <td>${SeqSample.countByPanel(panelInstance)}</td>

                <td>${fieldValue(bean: panelInstance, field: "panelGroup")}</td>

                <td>${fieldValue(bean: panelInstance, field: "description")}</td>
            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <filterpane:paginate total="${panelCount}" domainBean="Panel"/>
        <filterpane:filterButton text="Filter" appliedText="Change Filter"/>
        <filterpane:isNotFiltered>No filter</filterpane:isNotFiltered>
        <filterpane:isFiltered>Filtered!</filterpane:isFiltered>
    </div>
    <filterpane:filterPane domain="Panel" dialog="y"/>
</div>
</body>
</html>
