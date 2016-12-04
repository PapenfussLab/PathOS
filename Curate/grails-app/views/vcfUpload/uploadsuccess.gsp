%{--
  - Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
  -
  - Organisation: Peter MacCallum Cancer Centre
  - Author: seleznev andrei
  --}%

<%--
  Created by IntelliJ IDEA.
  User: seleznev andrei
  Date: 16/09/2015
  Time: 1:30 PM
--%>

<%@ page import="org.petermac.pathos.curate.VarFilterService; org.petermac.pathos.curate.SeqVariant; org.petermac.pathos.curate.PatSample" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Upload VCF</title>
    <tooltip:resources/>
    <g:javascript src="quasipartikel/jquery.min.js" />

    <style type="text/css">

    #scriptOutput   {

        background-color: #F9F9F9;
        border: 1px dashed #2F6FAB;
        color: black;
        line-height: 1.1em;
        padding: 1em;
        font-family: Courier New,Courier,Lucida Sans Typewriter,Lucida Typewriter,monospace;


    }
    #scriptOutputErr   {

        background-color: #F9F9F9;
        border: 1px dashed #2F6FAB;
        color: black;
        line-height: 1.1em;
        padding: 1em;
        font-family: Courier New,Courier,Lucida Sans Typewriter,Lucida Typewriter,monospace;


    }

    #page-body {
        line-height: 1.5;
        margin: 2em;
    }
    </style>
</head>

<body>
<div class="nav" role="navigation">
    <%--<ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" controller="admin" action="filter" params="${params}">Apply Filtering</g:link></li>
        <li><g:link class="create" controller="admin" action="reclassify" params="${params}">Re-classify Variants</g:link></li>
    </ul>--%>
</div>

<div id="admin-stats" class="content scaffold-list" role="main"
     style="white-space: nowrap; overflow-x:auto">

    <h1>Uploaded and ran VcfToPathOS</h1>
    <p>Metadata file: ${metadataFile}</p>
    <div id="page-body" role="main">
       <code>${command}</code><br/><p><i>Exit status:</i> ${exitval} </p><br/>
<p>
        Script output:</p><br/>
<div id="scriptOutput"><code>${stdout}</code></div>
        <br/><br/>

        <p>Script error output:  </p><br/>

        <div id="scriptOutputErr"><code>${stderr}</code></div>

    </div>

</div>
</body>
</html>
