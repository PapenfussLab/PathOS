%{--
  - Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
  -
  - Organisation: Peter MacCallum Cancer Centre
  - Author: doig ken
  --}%

<%@ page import="org.petermac.pathos.curate.CivicClinicalEvidence" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Civic Evidence List</title>

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

    th[aria-selected="true"] {
        background: linear-gradient(#E4F2FB,#E4F2FB,#AECBE4,#AECBE4) !important;
    }

    #tag_text_area {
        width: 100%;
    }
    #tags .tagdiv span {
        font-size: 14px;
    }
    #civicClinicalEvidence_table td {
        line-height: 1em;
    }
</r:style>

</head>

<body>

<div id="list-civicClinicalEvidence" class="content scaffold-list" role="main"
     style="white-space: nowrap; overflow-x:auto">
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <div style="margin: 2em; overflow: auto">
        <grid:grid  name="civicClinicalEvidence" >
            <grid:set caption='Civic Clinical Evidence'/>
            <grid:set col="entrez" label="Gene" formatter='f:entrez' width="70" />
            <grid:set col="civicVariant" label="Variant" formatter='f:variant' width="70" />
            <grid:set col="disease" label="Disease" formatter='f:disease' width="200" />
            <grid:set col="drugs" label="Drugs" width="200" />
            <grid:set col="pmid" label="PubMed" formatter='f:pmid' width="200" />
            <grid:set col="evidence_type" label="Evidence Type" width="100" />
            <grid:set col="evidence_direction" label="Direction" width="100" />
            <grid:set col="evidence_level" label="Level" width="50" />
            <grid:set col="evidence_statement" label="Statement" width="250" />
            <grid:set col="evidence_status" label="Status" width="70" />
            <grid:set col="rating" label="Rating" width="50" />
            <grid:set col="clinical_significance" label="Clinical Significance" width="200" />
            <grid:set col="last_review_date" label="Last Review Date" width="200" />
        </grid:grid>
        <grid:exportButton name="civicClinicalEvidence" formats="['csv', 'excel']"/>
    </div>
</div>
<r:script>

function entrez(data) {
    return "<a href='https://www.ncbi.nlm.nih.gov/gene/?term="+data[1]+"'>"+data[0]+"</a>";
}

function variant(data) {
    return "<a href='<g:context/>/CivicVariant/show/"+data.id+"'>"+data.variant+"</a>";
}

function disease(data) {
    return "<a href='http://disease-ontology.org/term/DOID%3A"+data[1]+"'>"+data[0]+"</a>"
}

function pmid(data) {
    return "<a href='<g:context/>/Pubmed?pmid="+data[0]+"'>"+data[1]+"</a>";
}



</r:script>
</body>
</html>
