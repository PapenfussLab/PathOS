<%@ page import="org.petermac.pathos.curate.Drug" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'drug.label', default: 'Drug')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>

    <r:require modules="export"/>

    %{--CSS Files--}%
    <link href="<g:resource plugin='easygrid' dir='jquery-ui-1.11.0' file='jquery-ui.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
    <link href="<g:resource dir='css/jquery-ui-1.11.0.custom' file='jquery-ui.theme.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
    <link href="<g:resource plugin='easygrid' dir='jquery.jqGrid-4.6.0/css' file='ui.jqgrid.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />

    %{--Javascript Files--}%
    <g:javascript src="quasipartikel/jquery.min.js" />
    <g:javascript src="quasipartikel/jquery-ui.min.js" />
    <g:javascript src="quasipartikel/ui.multiselect.js" />
    <script src="<g:context/>/static/bundle-bundle_easygrid-jqgrid-dev_head.js" type="text/javascript" ></script>

    <style type="text/css">
    .ui-jqgrid .ui-jqgrid-htable th div { vertical-align: top; }
    .ui-jqgrid .ui-jqgrid-htable th div{
       height: 30px;
    }
    .ui-jqgrid .ui-jqgrid-htable td div{
       height: 30px;
    }
    #tag_text_area {
       width: 100%;
    }
    #tags .tagdiv span {
       font-size: 14px;
    }
        #drug_table td {
            line-height: 1em;
        }
</style>

</head>

<body>
<a href="#list-drug" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
<ul>
   <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
</ul>
</div>

<div id="list-drug" class="content scaffold-list" role="main" style="white-space: nowrap; overflow-x:auto">
<h1><g:message code="default.list.label" args="[entityName]"/></h1>
<g:if test="${flash.message}">
   <div class="message" role="status">${flash.message}</div>
</g:if>

<div style="margin: 2em; overflow: auto">
   <grid:grid  name="drug" >
       <grid:set caption='Drugs'/>
       <grid:set col="alias" label="Alias" width="140"  />
       <grid:set col="approved" label="Approved" width="70"  />
       <grid:set col="approvedConditionMatch" label="Approved Condition Match" width="70"  />
       <grid:set col="approvedConditions" label="Approved Conditions" width="140"  />
       <grid:set col="badge" label="Badge" width="90"  />
       <grid:set hidden='f:true' col="brands" label="Brands" width="70"  />
       <grid:set col="contraindicatedAlterations" label="Contradicted Alterations" width="150"  />
       <grid:set col="description" label="Description" width="150"  />
       <grid:set col="experimental" label="Experimental" width="70"  />
       <grid:set col="experimentalConditions" label="Experimental Conditions" width="70"  />
       <grid:set hidden='f:true' col="externalIds" label="External IDs" width="70"  />
       <grid:set col="molecularExperimentalTargets" label="Molecular Experimental Targets" width="70"  />
       <grid:set col="molecularTargets" label="Molecular Targets" width="70"  />
       <grid:set col="name" label="Name" width="140"  />
       <grid:set col="status" label="Status" width="70"  />
       <grid:set hidden='f:true' col="synonyms" label="Synonyms" width="70"  />


   </grid:grid>
   <grid:exportButton name="drug" formats="['csv', 'excel']"/>
</div>

</div>

<r:script>
    $( document ).ready(function(){
        var params = PathOS.params();
        if(params.target_gene) {
            $("#gs_molecularTargets").val(params.target_gene);
            setTimeout( function(){
                $("#drug_table")[0].triggerToolbar();
            }, 500);
        }
    });
</r:script>

</body>
</html>
