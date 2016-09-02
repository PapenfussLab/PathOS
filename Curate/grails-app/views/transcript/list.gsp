
<%@ page import="org.petermac.pathos.curate.Transcript" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'transcript.label', default: 'Transcript')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
    <r:require module="filterpane" />
    <link href="<g:resource plugin='easygrid' dir='jquery-ui-1.11.0' file='jquery-ui.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
    <link href="<g:resource dir='css/jquery-ui-1.11.0.custom' file='jquery-ui.theme.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
    <link href="<g:resource plugin='easygrid' dir='jquery.jqGrid-4.6.0/css' file='ui.jqgrid.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
    <link href="<g:resource plugin='easygrid' dir='jquery.jqGrid-4.6.0/plugins' file='ui.multiselect.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />

    %{--CSS Files--}%
    <link href="<g:resource plugin='easygrid' dir='jquery-ui-1.11.0' file='jquery-ui.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
    <link href="<g:resource dir='css/jquery-ui-1.11.0.custom' file='jquery-ui.theme.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
    <link href="<g:resource plugin='easygrid' dir='jquery.jqGrid-4.6.0/css' file='ui.jqgrid.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
    %{--<link href="<g:resource plugin='easygrid' dir='jquery.jqGrid-4.6.0/plugins' file='ui.multiselect.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />--}%

    %{--Javascript Files--}%
    <g:javascript src="quasipartikel/jquery.min.js" />
    <g:javascript src="quasipartikel/jquery-ui.min.js" />
    <g:javascript src="quasipartikel/ui.multiselect.js" />
    <script src="/PathOS/static/bundle-bundle_easygrid-jqgrid-dev_head.js" type="text/javascript" ></script>
    <g:javascript src='jquery/jquery.jgrowl.js' plugin='spring-security-ui'/>

    <style type="text/css">
    .ui-jqgrid .ui-jqgrid-htable th     { vertical-align: top; }
    .ui-jqgrid .ui-jqgrid-htable th div { height: 30px; }


    <%--loading jGrowl in a stylesheet breaks the colours on our jqgrid. i'm still not sure why, but a workaround
    is putting the CSS inline instead of loading it from 'spring-security-ui css jgrowl --%>
    div.jGrowl{z-index:9999;color:#fff;font-size:12px;position:absolute}
    body > div.jGrowl{position:fixed}
    div.jGrowl.top-left{left:0;top:0}
    div.jGrowl.top-right{right:0;top:0}
    div.jGrowl.bottom-left{left:0;bottom:0}
    div.jGrowl.bottom-right{right:0;bottom:0}
    div.jGrowl.center{top:0;width:50%;left:25%}
    div.center div.jGrowl-notification,div.center div.jGrowl-closer{margin-left:auto;margin-right:auto}
    div.jGrowl div.jGrowl-notification,div.jGrowl div.jGrowl-closer{background-color:#000;opacity:.85;-ms-filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=85);filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=85);zoom:1;width:235px;padding:10px;margin-top:5px;margin-bottom:5px;font-family:Tahoma,Arial,Helvetica,sans-serif;font-size:1em;text-align:left;display:none;-moz-border-radius:5px;-webkit-border-radius:5px}
    div.jGrowl div.jGrowl-notification{min-height:40px}
    div.jGrowl div.jGrowl-notification,div.jGrowl div.jGrowl-closer{margin:10px}
    div.jGrowl div.jGrowl-notification div.jGrowl-header{font-weight:700;font-size:.85em}
    div.jGrowl div.jGrowl-notification div.jGrowl-close{z-index:99;float:right;font-weight:700;font-size:1em;cursor:pointer}
    div.jGrowl div.jGrowl-closer{padding-top:4px;padding-bottom:4px;cursor:pointer;font-size:.9em;font-weight:700;text-align:center}

</style>


</head>
<body>
<a href="#list-transcript" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>

    </ul>
</div>
<div id="list-transcript" class="content scaffold-list" role="main" style="white-space: nowrap; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]" /></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>


    <div style="margin: 2em; overflow: auto;" id="gridContainerDiv">
        <grid:grid  name="transcript" >
            <grid:set caption='Transcript Grid'/>
            <g:if test="${isAdmin}">
                <grid:set col='act'  width="70" />
            </g:if>
            <g:else>
                <grid:set col='act'  width="70" hidden='f:true' />
            </g:else>
            <grid:set col='genbuild'  width="70"  editable='false'  hidden='f:true' />
            <grid:set col='build'     width="70"  editable='false'   />
            <grid:set col='chromosome'  width="80"  editable='false'/>
            <grid:set col='chr_refseq'  width="80"  editable='false'  hidden='f:true'/>
            <grid:set col='accession'  width="90"  editable='false'/>
            <grid:set col='refseq'  width="90"  editable='false'/>
            <grid:set col='preferred'  width="60"  editable='true'/>
            <grid:set col='gene'  width="75"  editable='false'/>
            <grid:set col='strand'  width="75"  editable='false'/>
            <grid:set col='ts_size'  width="75"  editable='false'/>
            <grid:set col='cds_size'  width="75"  editable='false'/>
            <grid:set col='source'  width="75"  editable='false'/>
            <grid:set col='exSize'  width="75"  editable='false'/>
            <grid:set col='exCount'  width="75"  editable='false'/>
            <grid:set col='ts_start'  width="75"  editable='false'/>
            <grid:set col='ts_stop'  width="75"  editable='false'/>
            <grid:set col='cds_start'  width="75"  editable='false'/>
            <grid:set col='cds_stop'  width="75"  editable='false'/>
            <grid:set col='exon_starts'  width="100"  editable='false'  hidden='f:true' />
            <grid:set col='exon_stops'  width="100"  editable='false'  hidden='f:true' />
            <grid:set col='lrg'  width="75"  editable='false'/>
        </grid:grid>
        <grid:exportButton name="transcript" formats="['csv', 'excel']" exportId="12345"/>

        <br/>
    </div>

</div>


<r:script>
<%-- window.onload = loadSavedUserGrid(); --%>


     function afterEdit()
     {
         //jQuery.jgrid.info_dialog('Info', 'Record changed !', jQuery.jgrid.edit.bClose, {buttonalign: 'right'});
         reloadGrid();

         //check if we have an error
//         var msg = "${session.transcriptErrorMsg}"

  //       var msg2 = ${remoteFunction(controller: 'transcript', action: 'getErrorMessage')}; //onComplete: 'window.location.reload()
    //     alert(msg2)

    //     if (msg != "") {
//          $.jGrowl(msg)
    //     } else {
            $.jGrowl("Record changed")
        // }

         return true;
     }


     function afterError()
     {
         //jQuery.jgrid.info_dialog('Info', 'Record changed !', jQuery.jgrid.edit.bClose, {buttonalign: 'right'});
         reloadGrid();
         $.jGrowl("Cannot change record. Genes cannot have more than one preferred transcript.")
         return true;
     }

     function reloadGrid()
     {
         jQuery('#transcript_table').trigger('reloadGrid');
         return true;
     }

</r:script>

</body>
</html>
