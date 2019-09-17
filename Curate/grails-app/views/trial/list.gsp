
<%@ page import="org.petermac.pathos.curate.Trial" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'trial.label', default: 'Trial')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
    <r:require module="export" />

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
    #trial_table td {
        line-height: 1em;
    }
    </style>
</head>

<body>
<a href="#list-trial" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        %{-- <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>--}%
    </ul>
</div>

<div id="list-trial" class="content scaffold-list" role="main" style="white-space: nowrap; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]" /></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <div style="margin: 2em; overflow: auto">
        <grid:grid  name="trial" >
            <grid:set caption='Trials'/>
            <grid:set col="study" label="Study" width="140"  />
            <grid:set col="briefTitle" label="Brief Title" width="300"  />
            <grid:set col="molecularAlterations" label="Molecular Alterations" width="70"  />
            <grid:set col="phase" label="Phase" width="100"  />
            <grid:set col="status" label="Status" width="70"  />
            <grid:set col="studyType" label="Study Type" width="100"  />
            <grid:set col="startDate" label="Start Date" width="100"  />
            <grid:set col="interventions" label="Interventions" width="70"  />
            <grid:set col="score" label="Score" width="50"  />
            <grid:set col="title" label="Title" width="70"  />
            <grid:set col="locations" label="Locations" width="70"  />
            <grid:set col="overallContact" label="Overall Contact" width="70"  />

        </grid:grid>
        <grid:exportButton name="trial" formats="['csv', 'excel']"/>
    </div>

</div>

<r:script>
    $( document ).ready(function(){
        var params = PathOS.params();
        if(params.target_gene) {
            $("#gs_molecularAlterations").val(params.target_gene);
            setTimeout( function(){
                $("#trial_table")[0].triggerToolbar();
            }, 500);
        }
    });
</r:script>
</body>
</html>
