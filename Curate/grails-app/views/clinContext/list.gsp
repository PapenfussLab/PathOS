
<%@ page import="org.petermac.pathos.curate.ClinContext" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'clinContext.label', default: 'ClinContext')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
    <r:require module="filterpane" />
</head>
<body>
<a href="#list-clinContext" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
    </ul>
</div>
<div id="list-clinContext" class="content scaffold-list" role="main" style="white-space: nowrap; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]" /></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <filterpane:currentCriteria domainBean="ClinContext"
                                removeImgDir="images" removeImgFile="bullet_delete.png" fullAssociationPathFieldNames="no" />

    <table>
        <thead>
        <tr>
            
            <g:sortableColumn property="description" title="${message(code: 'clinContext.description.label', default: 'Description')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="code" title="${message(code: 'clinContext.code.label', default: 'Code')}"  params="${filterParams}"/>
            
            <th><g:message code="clinContext.createdBy.label" default="Created By" /></th>
            
        </tr>
        </thead>
        <tbody>
        <g:each in="${clinContextList}" status="i" var="clinContextInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                
                <td><g:link action="show" id="${clinContextInstance.id}">${fieldValue(bean: clinContextInstance, field: "description")}</g:link></td>
                
                <td>${fieldValue(bean: clinContextInstance, field: "code")}</td>
                
                <td>${fieldValue(bean: clinContextInstance, field: "createdBy")}</td>
                
            </tr>
        </g:each>
        </tbody>
    </table>
    <div class="pagination">
        <filterpane:paginate total="${clinContextCount}" domainBean="ClinContext"/>
        <filterpane:filterButton text="Filter" appliedText="Change Filter"/>
        <filterpane:isNotFiltered>No filter</filterpane:isNotFiltered>
        <filterpane:isFiltered>Filtered!</filterpane:isFiltered>
    </div>
    <filterpane:filterPane domain="ClinContext" dialog="y"/>
</div>
</body>
</html>
