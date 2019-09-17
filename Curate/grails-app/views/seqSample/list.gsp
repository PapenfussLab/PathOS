<%@ page import="org.petermac.pathos.curate.SeqSample" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'seqSample.label', default: 'SeqSample')}"/>
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
    .ui-jqgrid .ui-jqgrid-htable th     { vertical-align: top; }
    .ui-jqgrid .ui-jqgrid-htable th div { height: 30px; }
    
    #tag_text_area {
        width: 100%;
    }
    #tags .tagdiv span {
        font-size: 14px;
    }
    </style>

</head>

<body>
<a href="#list-seqSample" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>

<div id="list-seqSample" class="content scaffold-list" role="main" style="white-space: nowrap; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <div style="margin: 2em; overflow: auto">
        <grid:grid  name="seqsample" >
            <grid:set caption='Sequenced Samples'/>
            <grid:set col="sampleName"       width="70"  />
            <grid:set col="patSample"           width="70"  />
            <grid:set col="seqrun"           width="170"  />
            <grid:set col="panel"            width="170"  />
            <grid:set col="userEmail"        width="140"  />
            <grid:set col="analysis"         width="140"  />
            <grid:set col="firstReviewBy"        width="140"  />
            <grid:set col="finalReviewBy"     width="140"  />
            <grid:set col="authorisedQc"     width="140"  />
            <grid:set col="authorisedQcFlag" width="40" align="center"  formatter="checkbox" />
            <grid:set col="passfailFlag"     width="40" align="center"  formatter="checkbox" />
            <grid:set col="qcComment"        width="100"  />
        </grid:grid>
        <grid:exportButton name="seqsample" formats="['csv', 'excel']"/>
    </div>

</div>
<script>
    var allTags = <g:allTags/>;
    var tagModule = PathOS.tags.buildModule({
        object: 'SeqSample',
        tags: [],
        availableTags: Object.keys(allTags)
    });

    var current_id = false;
    $("#seqsample_table").on('click', function(){
        setTimeout(function(){
            if (current_id != $(".ui-row-ltr.ui-state-highlight").attr('id')) {
                current_id = $(".ui-row-ltr.ui-state-highlight").attr('id');
                PathOS.tags.update_object(current_id);
            }
        }, 200);
    });
</script>
</body>
</html>
