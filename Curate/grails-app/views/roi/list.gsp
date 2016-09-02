
<%@ page import="org.petermac.pathos.curate.Roi" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'roi.label', default: 'Roi')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
    <r:require module="filterpane" />
</head>
<body>
<a href="#list-roi" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
    </ul>
</div>
<div id="list-roi" class="content scaffold-list" role="main" style="white-space: nowrap; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]" /></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <filterpane:currentCriteria domainBean="Roi"
                                removeImgDir="images" removeImgFile="bullet_delete.png" fullAssociationPathFieldNames="no" />

    <table>
        <thead>
        <tr>
            
            <th><g:message code="roi.panel.label" default="Panel" /></th>
            
            <g:sortableColumn property="name" title="${message(code: 'roi.name.label', default: 'Name')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="gene" title="${message(code: 'roi.gene.label', default: 'Gene')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="exon" title="${message(code: 'roi.exon.label', default: 'Exon')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="chr" title="${message(code: 'roi.chr.label', default: 'Chr')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="startPos" title="${message(code: 'roi.startPos.label', default: 'Start Pos')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="endPos" title="${message(code: 'roi.endPos.label', default: 'End Pos')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="amplicons" title="${message(code: 'roi.amplicons.label', default: 'Amplicons')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="manifestName" title="${message(code: 'roi.manifestName.label', default: 'Manifest Name')}"  params="${filterParams}"/>
            
        </tr>
        </thead>
        <tbody>
        <g:each in="${roiList}" status="i" var="roiInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                
                <td><g:link action="show" id="${roiInstance.id}">${fieldValue(bean: roiInstance, field: "panel")}</g:link></td>
                
                <td>${fieldValue(bean: roiInstance, field: "name")}</td>
                
                <td>${fieldValue(bean: roiInstance, field: "gene")}</td>
                
                <td>${fieldValue(bean: roiInstance, field: "exon")}</td>
                
                <td>${fieldValue(bean: roiInstance, field: "chr")}</td>
                
                <td>${fieldValue(bean: roiInstance, field: "startPos")}</td>
                
                <td>${fieldValue(bean: roiInstance, field: "endPos")}</td>
                
                <td>${fieldValue(bean: roiInstance, field: "amplicons")}</td>
                
                <td>${fieldValue(bean: roiInstance, field: "manifestName")}</td>
                
            </tr>
        </g:each>
        </tbody>
    </table>
    <div class="pagination">
        <filterpane:paginate total="${roiCount}" domainBean="Roi"/>
        <filterpane:filterButton text="Filter" appliedText="Change Filter"/>
        <filterpane:isNotFiltered>No filter</filterpane:isNotFiltered>
        <filterpane:isFiltered>Filtered!</filterpane:isFiltered>
    </div>
    <filterpane:filterPane domain="Roi" dialog="y"/>
</div>
</body>
</html>
