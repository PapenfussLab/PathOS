
<%@ page import="org.petermac.pathos.curate.RefGene" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'refGene.label', default: 'RefGene')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
    <r:require module="filterpane" />
</head>
<body>
<a href="#list-refGene" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
      <%--  <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li> --%>
    </ul>
</div>
<div id="list-refGene" class="content scaffold-list" role="main" style="white-space: nowrap; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]" /></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <filterpane:currentCriteria domainBean="RefGene"
                                removeImgDir="images" removeImgFile="bullet_delete.png" fullAssociationPathFieldNames="no" />

    <table>
        <thead>
        <tr>
            
            <g:sortableColumn property="gene" title="${message(code: 'refGene.gene.label', default: 'Gene')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="hgncid" title="${message(code: 'refGene.hgncid.label', default: 'Hgncid')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="accession" title="${message(code: 'refGene.accession.label', default: 'Accession')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="genedesc" title="${message(code: 'refGene.genedesc.label', default: 'Genedesc')}"  params="${filterParams}"/>
            
            <g:sortableColumn property="refseq" title="${message(code: 'refGene.refseq.label', default: 'Refseq')}"  params="${filterParams}"/>
            
        </tr>
        </thead>
        <tbody>
        <g:each in="${refGeneList}" status="i" var="refGeneInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                
                <td><g:link action="show" id="${refGeneInstance.id}">${fieldValue(bean: refGeneInstance, field: "gene")}</g:link></td>
                
                <td>${fieldValue(bean: refGeneInstance, field: "hgncid")}</td>
                
                <td>${fieldValue(bean: refGeneInstance, field: "accession")}</td>
                
                <td>${fieldValue(bean: refGeneInstance, field: "genedesc")}</td>
                
                <td>${fieldValue(bean: refGeneInstance, field: "refseq")}</td>
                
            </tr>
        </g:each>
        </tbody>
    </table>
    <div class="pagination">
        <filterpane:paginate total="${refGeneCount}" domainBean="RefGene"/>
        <filterpane:filterButton text="Filter" appliedText="Change Filter"/>
        <filterpane:isNotFiltered>No filter</filterpane:isNotFiltered>
        <filterpane:isFiltered>Filtered!</filterpane:isFiltered>
    </div>
    <filterpane:filterPane domain="RefGene" dialog="y"/>
</div>
</body>
</html>
