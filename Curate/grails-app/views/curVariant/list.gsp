
<%@ page import="org.petermac.pathos.curate.CurVariant" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'variant.label', default: 'Curated Variants')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>

    <r:require modules="export"/>

    %{--CSS Files--}%
    <link href="<g:resource plugin='easygrid' dir='jquery-ui-1.11.0' file='jquery-ui.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
    <link href="<g:resource dir='css/jquery-ui-1.11.0.custom' file='jquery-ui.theme.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
    <link href="<g:resource plugin='easygrid' dir='jquery.jqGrid-4.6.0/css' file='ui.jqgrid.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />

    %{--Javascript Files--}%
    %{--<g:javascript src="quasipartikel/jquery.min.js" />--}%
    %{--<g:javascript src="quasipartikel/jquery-ui.min.js" />--}%
    %{--<g:javascript src="quasipartikel/ui.multiselect.js" />--}%
    <script src="<g:context/>/static/bundle-bundle_easygrid-jqgrid-dev_head.js" type="text/javascript" ></script>

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
<a href="#list-variant" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="list-variant" class="content scaffold-list" role="main" style="white-space: nowrap; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]" /> ${variantInstance}</h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <div style="margin: 2em; overflow: auto">
        <grid:grid  name="variant" >
            <grid:set caption='Curated Variants'/>
            <grid:set col="authorisedFlag" width="40" align="center"  formatter="checkbox" />
            <grid:set col="clinContext"    width="70" formatter="f:ccFormatter" />
            <grid:set col="gene"           width="70" align="center" />
            <grid:set col="gene_type"      width="70" align="center" />
            <grid:set col="hgvsp"          width="70" />
            <grid:set col="exon"           width="90" />
            <grid:set col="pmClass"        width="130" formatter='f:classFormatter' />
        </grid:grid>
        <grid:exportButton name="variant" formats="['csv', 'excel']"/>
    </div>

</div>
<script>
    var allTags = <g:allTags/>;
    var tagModule = PathOS.tags.buildModule({
        object: 'CurVariant',
        tags: [],
        availableTags: Object.keys(allTags)
    });

    var curvar_id = false;
    $("#variant_table").on('click', function(){
        setTimeout(function(){
            if (curvar_id != $(".ui-row-ltr.ui-state-highlight").attr('id')) {
                curvar_id = $(".ui-row-ltr.ui-state-highlight").attr('id');
                PathOS.tags.update_object(curvar_id);
            }
        }, 200);
    });
</script>
</body>
<r:script>
    /**
     * Formatter for colouring a curated CurVariant
     *
     * @param cellvalue      PLON 5-level pathogenicity string
     * @param options
     * @param rowObject      Array of cell values
     * @returns {string}     Nicely coloured by pathogenicity
     */
    function classFormatter( cellvalue, options, rowObject )
    {
        if ( cellvalue == null || cellvalue.length == 0 ) return '';

        colour = '#000000';
        bg     = '#ffffff';
        var m = cellvalue.match( /^C\d/ );
        if ( m == 'C1' )    {colour = '#000000'; bg = '#fffdc1';}
        if ( m == 'C2' )    {colour = '#000000'; bg = '#f4d374';}
        if ( m == 'C3' )    {colour = '#000000'; bg = '#e89e53';}
        if ( m == 'C4' )    {colour = '#ffffff'; bg = '#d65430';}
        if ( m == 'C5' )    {colour = '#ffffff'; bg = '#ae2334';}

        var fld = "<noop style=\'color: " + colour + "; margin-right:-2px; margin-left:-2px; padding:4px; background-color: " + bg + "\'>" + cellvalue + '</noop>';

        return fld;
    }


    function ccFormatter( cellvalue, options, rowObject )
    {
        if ( cellvalue == null || cellvalue.length == 0 ) return '';

        return '<a href="<g:context/>/clinContext/show/'+cellvalue.id+'" title="'+cellvalue.description+'">'+cellvalue.code+'</a>';
    }


</r:script>
</html>
