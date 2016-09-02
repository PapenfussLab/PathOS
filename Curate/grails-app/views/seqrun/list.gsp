%{--
  - Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
  -
  - Organisation: Peter MacCallum Cancer Centre
  - Author: doig ken
  --}%

<%@ page import="org.petermac.pathos.curate.Seqrun" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'seqrun.label', default: 'Sequencing Runs')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>

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
    <script src="/PathOS/static/bundle-bundle_easygrid-jqgrid-dev_head.js" type="text/javascript" ></script>

    <style type="text/css">
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
    </style>

</head>

<body>
<a href="#list-seqrun" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>

<div id="list-seqrun" class="content scaffold-list" role="main"
     style="white-space: nowrap; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <div style="margin: 2em; overflow: auto">
        <grid:grid  name="seqrun" >
            <grid:set caption='Sequencing Runs'/>
            <grid:set col="scanner"         width="80" />
            <grid:set col="qcComment"       width="90" />
            <grid:set col="library"         width="180" />
            <grid:set col="experiment"      width="180" />
            <grid:set col='panelList'           width="250" />
            <grid:set col='authorised'      width="90" />
            <grid:set col="sepe"            width="70" align="center" />
            <grid:set col="noSamples"       width="50" align="right" />
            <grid:set col="readlen"         width="50" align="right" />
            <grid:set col="platform"        width="70" formatter="f:platformFormatter" />
            <grid:set col="passfailFlag"    width="40" align="center"  formatter="checkbox" />
            <grid:set col="authorisedFlag"  width="40" align="center"  formatter="checkbox" />
        </grid:grid>
        <grid:exportButton name="seqrun" formats="['csv', 'excel']"/>
    </div>
</div>
<script>



    var tagModule = PathOS.tags.buildModule({
        object: 'Seqrun',
        tags: [],
        availableTags: <g:allTags/>
    });

    var current_id = false;
    $("#seqrun_table").on('click', function(){
        setTimeout(function(){
            if (current_id != $(".ui-row-ltr.ui-state-highlight").attr('id')) {
                current_id = $(".ui-row-ltr.ui-state-highlight").attr('id');
                PathOS.tags.update_object(current_id);
            }
        }, 200);
    });
















/* unbind columns that are unsortable in seqrun controller */

$( document ).ready(function() {
    $("#seqrun_table_noSamples").unbind("click");
    $("#seqrun_table_runDate").unbind("click");
   // $("#seqrun_table_panel").unbind("click");
    //$("#seqrun_table_platform").unbind("click");
    //$("#seqrun_table_sepe").unbind("click");
    //$("#seqrun_table_readlen").unbind("click");
  //  $("#seqrun_table_library").unbind("click");
   // $("#seqrun_table_experiment").unbind("click");
   // $("#seqrun_table_scanner").unbind("click");
   // $("#seqrun_table_authorisedFlag").unbind("click");
   // $("#seqrun_table_passfailFlag").unbind("click");
    $("#seqrun_table_authorised").unbind("click");
    $("#seqrun_table_qcComment").unbind("click");
});

/**
* Colour the sequencer platform type
 *
* @param cellvalue  Platform type string
* @param options
* @param rowObject
* @returns {string} Coloured platform wrapped in a noop tag
*/
function platformFormatter( cellvalue, options, rowObject )
{
    colour = '#000000';
    bg     = '#ffffff';
    if ( cellvalue == 'MiSeq'      )    {colour = '#000000'; bg = '#abdda4';}
    if ( cellvalue == 'NextSeq' )       {colour = '#000000'; bg = '#2b83ba';}
    if ( cellvalue == 'HiSeq' )         {colour = '#000000'; bg = '#fdae61';}
    if ( cellvalue == 'HiSeq2000'  )    {colour = '#000000'; bg = '#ffffbf';}
    if ( cellvalue == 'HiSeqRapid' )    {colour = '#000000'; bg = '#d7191c';}

    return "<noop style=\'color: " + colour + "; margin-right:-2px; margin-left:-2px; padding:4px; background-color: " + bg + "\'>" + cellvalue + "</noop>";
}


setTimeout(function(){
    $("#seqrun_table_seqrun").attr("aria-selected", "true");
}, 500);
</script>
</body>
</html>
