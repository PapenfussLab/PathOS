
<%@ page import="org.petermac.pathos.curate.Amplicon" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'amplicon.label', default: 'Amplicon')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
    <r:require module="filterpane" />
</head>
<body>
<a href="#list-amplicon" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
      <%--  <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li> --%>
    </ul>
</div>
<div id="list-amplicon" class="content scaffold-list" role="main" style="white-space: nowrap; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]" /></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <filterpane:currentCriteria domainBean="Amplicon"
                                removeImgDir="images" removeImgFile="bullet_delete.png" fullAssociationPathFieldNames="no" />

    <table>
        <thead>
        <tr>
            
            <g:sortableColumn property="panel" title="${message(code: 'amplicon.panel.label', default: 'Panel')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="amplicon" title="${message(code: 'amplicon.amplicon.label', default: 'Amplicon')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="chr" title="${message(code: 'amplicon.chr.label', default: 'Chr')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="startpos" title="${message(code: 'amplicon.startpos.label', default: 'Startpos')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="endpos" title="${message(code: 'amplicon.endpos.label', default: 'Endpos')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="primerlen1" title="${message(code: 'amplicon.primerlen1.label', default: 'Primerlen1')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="primerlen2" title="${message(code: 'amplicon.primerlen2.label', default: 'Primerlen2')}"  params="${filterParams}"/>
            
        </tr>
        </thead>
        <tbody>
        <g:each in="${ampliconList}" status="i" var="ampliconInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                
                <td><g:link action="show" id="${ampliconInstance.id}">${fieldValue(bean: ampliconInstance, field: "panel")}</g:link></td>
                
                <td>${fieldValue(bean: ampliconInstance, field: "amplicon")}</td>
                
                <td>${fieldValue(bean: ampliconInstance, field: "chr")}</td>
                
                <td>${fieldValue(bean: ampliconInstance, field: "startpos")}</td>
                
                <td>${fieldValue(bean: ampliconInstance, field: "endpos")}</td>
                
                <td>${fieldValue(bean: ampliconInstance, field: "primerlen1")}</td>
                
                <td>${fieldValue(bean: ampliconInstance, field: "primerlen2")}</td>
                
            </tr>
        </g:each>
        </tbody>
    </table>
    <div class="pagination">
        <filterpane:paginate total="${ampliconCount}" domainBean="Amplicon"/>
        <filterpane:filterButton text="Filter" appliedText="Change Filter"/>
        <filterpane:isNotFiltered>No filter</filterpane:isNotFiltered>
        <filterpane:isFiltered>Filtered!</filterpane:isFiltered>
    </div>
    <filterpane:filterPane domain="Amplicon" dialog="y"/>
</div>
</body>
</html>
