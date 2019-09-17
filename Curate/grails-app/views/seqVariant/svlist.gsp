%{--
  - Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
  -
  - Organisation:   Peter MacCallum Cancer Centre
  - Author:         Ken Doig

  --}%

<%@ page import="grails.converters.JSON; org.petermac.pathos.pipeline.UrlLink; org.petermac.pathos.curate.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'curation.header.label')}"/>
<title>${seqSample?.seqrun} - ${seqSample?.sampleName} - <g:message code="default.list.label" args="[entityName]"/></title>

<r:require modules="export"/>

<parameter name="footer" value="on" />
%{--<parameter name="hotfix" value="off" />--}%

</head>

<body>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><a href="<g:context/>/SeqSampleReport/link/${seqSample.id}" class="list">Draft Report</a></li>

    </ul>
</div>

<g:if test="${compressedView}">
    <g:render template="niceSeqSampleInfo" />
</g:if>
<g:else>
    <g:render template="seqSampleInfo" />
</g:else>

%{--<g:render template="proteinpaint" />--}%

%{--<g:render template="circos" />--}%

%{--<g:render template="svGenes" />--}%

<g:render template="easygrid" />

</body>


</html>







