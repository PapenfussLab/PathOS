%{--
  - Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
  -
  - Organisation: Peter MacCallum Cancer Centre
  - Author: doig ken
  --}%

<%@ page import="org.petermac.pathos.curate.CivicVariant" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Civic Variant list</title>

    <r:require modules="export"/>

    %{--CSS Files--}%
    <link href="<g:resource plugin='easygrid' dir='jquery-ui-1.11.0' file='jquery-ui.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
    <link href="<g:resource dir='css/jquery-ui-1.11.0.custom' file='jquery-ui.theme.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
    <link href="<g:resource plugin='easygrid' dir='jquery.jqGrid-4.6.0/css' file='ui.jqgrid.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
    %{--<link href="<g:resource plugin='easygrid' dir='jquery.jqGrid-4.6.0/plugins' file='ui.multiselect.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />--}%

    %{--Javascript Files--}%
    <g:javascript src="quasipartikel/jquery.min.js" />
    <g:javascript src="quasipartikel/jquery-ui.min.js" />
    <g:javascript src="quasipartikel/ui.multiselect.js" />
    <script src="<g:context/>/static/bundle-bundle_easygrid-jqgrid-dev_head.js" type="text/javascript" ></script>

<r:style>
    .ui-jqgrid .ui-jqgrid-htable th     { vertical-align: top; }
    .ui-jqgrid .ui-jqgrid-htable th div { height: 30px; }

    #tag_text_area {
        width: 100%;
    }
    #tags .tagdiv span {
        font-size: 14px;
    }
    #civicVariant_table td {
        line-height: 1em;
    }
</r:style>

</head>

<body>

<div id="list-civicVariant" class="content scaffold-list" role="main"
     style="white-space: nowrap; overflow-x:auto">
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <div style="margin: 2em; overflow: auto">
        <grid:grid  name="civicVariant" >
            <grid:set caption='Civic Variants'/>
            <grid:set col="variant" label="Variant" formatter="f:variant" width="180" />
            <grid:set col="summary" label="Summary" width="280" />
            <grid:set col="variant_bases" label="Variant Bases" width="55" />
            <grid:set col="reference_bases" label="Reference Bases" width="55" />
            <grid:set col="gene" label="Gene" width="70" />
            <grid:set col="entrez" label="Entrez" width="55" />
            <grid:set col="hgvs_expressions" label="HGVS Expressions" width="200" />
            <grid:set col="chromosome" label="Chromosome" width="55" />
            <grid:set col="start" label="Start" width="60" />
            <grid:set col="stop" label="Stop" width="60" />
            <grid:set col="representative_transcript" label="Representative Transcript" width="100" />
            <grid:set col="variant_types" label="Variant Types" width="200" />
            <grid:set col="last_review_date" label="Last Review Date" width="100" />

        </grid:grid>
        <grid:exportButton name="civicVariant" formats="['csv', 'excel']"/>
    </div>
</div>
<r:script>

function variant(data) {
    return "<a target='_blank' href='"+data[1]+"'>"+data[0]+"</a>";
}

</r:script>
</body>
</html>
