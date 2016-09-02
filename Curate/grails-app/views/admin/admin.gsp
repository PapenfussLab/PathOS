<%@ page import="org.petermac.pathos.curate.VarFilterService; org.petermac.pathos.curate.SeqVariant; org.petermac.pathos.curate.PatSample" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Admin</title>
    <tooltip:resources/>
</head>

<body>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" controller="admin" action="filter" params="${params}">Apply Filtering</g:link></li>
        <li><g:link class="create" controller="admin" action="reclassify" params="${params}">Re-classify Variants</g:link></li>
    </ul>
</div>

<div id="admin-stats" class="content scaffold-list" role="main"
     style="white-space: nowrap; overflow-x:auto">

    <h1>Admin</h1>

    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <ol class="property-list admin">

    <li class="fieldcontain">
        <span id="total-label" class="property-label">Variants</span>
        <span class="property-value" aria-labelledby="total-label">
            <g:formatNumber number="${SeqVariant.count()}" format="###,###,##0"/>
        </span>

    </li>
    <li class="fieldcontain">
        <g:each in="${VarFilterService.stats()}" status="i" var="flag">
            <g:set var="filterFlag" value="${flag.key[-3..-1]}"/>
            <tooltip:tip code="admin.${filterFlag}.tip">
                <g:if test="${flag.key!='Passed: pass'}">
                    <span id="filter-label" class="property-label">Filtered by ${flag.key}</span>
                </g:if>
                <g:else>
                    <b><span id="filter-value" class="property-label">Passed !</span></b>
                </g:else>
            </tooltip:tip>
            <span class="property-value" aria-labelledby="filter-label">
                <g:formatNumber number="${flag.value}" format="###,###,##0"/>
            </span>
        </g:each>
    </li>

    </ol>
</div>
</body>
</html>
