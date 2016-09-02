
<%@ page import="org.petermac.pathos.curate.RefExon" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'refExon.label', default: 'RefExon')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
    <r:require module="filterpane" />
</head>
<body>
<a href="#list-refExon" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
       <%-- <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li> --%>
    </ul>
</div>
<div id="list-refExon" class="content scaffold-list" role="main" style="white-space: nowrap; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]" /></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <filterpane:currentCriteria domainBean="RefExon"
                                removeImgDir="images" removeImgFile="bullet_delete.png" fullAssociationPathFieldNames="no" />

    <table>
        <thead>
        <tr>
            
            <g:sortableColumn property="gene" title="${message(code: 'refExon.gene.label', default: 'Gene')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="refseq" title="${message(code: 'refExon.refseq.label', default: 'Refseq')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="exon" title="${message(code: 'refExon.exon.label', default: 'Exon')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="strand" title="${message(code: 'refExon.strand.label', default: 'Strand')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="idx" title="${message(code: 'refExon.idx.label', default: 'Idx')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="exonStart" title="${message(code: 'refExon.exonStart.label', default: 'Exon Start')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="exonEnd" title="${message(code: 'refExon.exonEnd.label', default: 'Exon End')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="exonFrame" title="${message(code: 'refExon.exonFrame.label', default: 'Exon Frame')}"  params="${filterParams}"/>
            
        </tr>
        </thead>
        <tbody>
        <g:each in="${refExonList}" status="i" var="refExonInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                
                <td><g:link action="show" id="${refExonInstance.id}">${fieldValue(bean: refExonInstance, field: "gene")}</g:link></td>
                
                <td>${fieldValue(bean: refExonInstance, field: "refseq")}</td>
                
                <td>${fieldValue(bean: refExonInstance, field: "exon")}</td>
                
                <td>${fieldValue(bean: refExonInstance, field: "strand")}</td>
                
                <td>${fieldValue(bean: refExonInstance, field: "idx")}</td>
                
                <td>${fieldValue(bean: refExonInstance, field: "exonStart")}</td>
                
                <td>${fieldValue(bean: refExonInstance, field: "exonEnd")}</td>
                
                <td>${fieldValue(bean: refExonInstance, field: "exonFrame")}</td>
                
            </tr>
        </g:each>
        </tbody>
    </table>
    <div class="pagination">
        <filterpane:paginate total="${refExonCount}" domainBean="RefExon"/>
        <filterpane:filterButton text="Filter" appliedText="Change Filter"/>
        <filterpane:isNotFiltered>No filter</filterpane:isNotFiltered>
        <filterpane:isFiltered>Filtered!</filterpane:isFiltered>
    </div>
    <filterpane:filterPane domain="RefExon" dialog="y"/>
</div>
</body>
</html>
