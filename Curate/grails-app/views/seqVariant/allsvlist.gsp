%{--
  - Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
  -
  - Organisation: Peter MacCallum Cancer Centre
  - Author: doig ken
  --}%

<%@ page import="org.petermac.pathos.curate.SeqVariant" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="All Sequenced Variants"/>
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
<a href="#list-seqVariant" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>

<div id="list-seqVariant" class="content scaffold-list" role="main"
     style="white-space: nowrap; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
</div>

<div style="margin: 2em; overflow: auto;">
    <grid:grid  name="allVariants" >
        <grid:set caption='All Sequenced Variants'/>
        <grid:set col='curated'         width="130" formatter='f:variantFormatter'/>
        <grid:set col='gene'            width="70"  formatter='f:geneFormatter'/>
        <grid:set col='siftCat'         width="45"  formatter='f:classFormatter'/>
        <grid:set col='mutTasteCat'     width="45"  formatter='f:classFormatter'/>
        <grid:set col='polyphenCat'     width="45"  formatter='f:classFormatter'/>
        <grid:set col='lrtCat'          width="45"  formatter='f:classFormatter'/>
        <grid:set col='mutAssessCat'    width="45"  formatter='f:classFormatter'/>
        <grid:set col='fathmmCat'       width="45"  formatter='f:classFormatter'/>
        <grid:set col='metaSvmCat'      width="45"  formatter='f:classFormatter'/>
        <grid:set col='metaLrCat'       width="45"  formatter='f:classFormatter'/>
        <grid:set col='pubmed'          width="70" hidden='f:true' />
        <grid:set col='varPanelPct'     width="70" hidden='f:false' formatter='number'/>
        <grid:set col='ens_transcript'  width="70" hidden='f:true' />
        <grid:set col='ens_gene'        width="70" hidden='f:true' />
        <grid:set col='ens_protein'     width="70" hidden='f:true' />
        <grid:set col='ens_canonical'   width="70" hidden='f:true' />
        <grid:set col='refseq_mrna'     width="90" hidden='f:true' />
        <grid:set col='refseq_peptide'  width="90" hidden='f:true' />
        <grid:set col='existing_variation' width="70" hidden='f:true' />
        <grid:set col='domains'         width="70" hidden='f:true' />
        <grid:set col='genedesc'        width="70" hidden='f:true' />
        <grid:set col='omim_ids'        width="70" hidden='f:true' />
        <grid:set col='biotype'         width="70" hidden='f:true' />
        <grid:set col='siftVal'         width="45"  hidden='f:true' editable='false'/>
        <grid:set col='mutTasteVal'     width="45"  hidden='f:true' editable='false'/>
        <grid:set col='polyphenVal'     width="45"  hidden='f:true' editable='false'/>
        <grid:set col='clinvarVal'      width="45"  hidden='f:true' editable='false'/>
        <grid:set col='clinvarCat'      width="45"  hidden='f:true' formatter='f:classFormatter'/>
        <grid:set col='lrtVal'          width="45"  hidden='f:true' editable='false'/>
        <grid:set col='mutAssessVal'    width="45"  hidden='f:true' editable='false'/>
        <grid:set col='fathmmVal'       width="45"  hidden='f:true' editable='false'/>
        <grid:set col='metaSvmVal'      width="45"  hidden='f:true' editable='false'/>
        <grid:set col='metaLrVal'       width="45"  hidden='f:true' editable='false'/>
        <grid:set col='varFreq'         width="70"  editable='false' />
        <grid:set col='varDepth'        width="55"  editable='false' formatter='integer'/>
        <grid:set col='readDepth'       width="55"  editable='false' formatter='integer'/>
        <grid:set col='varcaller'       width="70"  hidden='f:false' editable='false'/>
        <grid:set col='amps'            width="70"  hidden='f:true'  editable='false'/>
        <grid:set col='numamps'         width="70"  hidden='f:false' editable='false'/>
        <grid:set col='ampbias'         width="70"  hidden='f:false'  editable='false'/>
        <grid:set col='homopolymer'     width="45"  hidden='f:false' editable='false'/>
        <grid:set col='gmaf'            width="50"  editable='false' formatter='number'/>
        <grid:set col='esp'             width="50"  editable='false' formatter='number'/>
        <grid:set col='exac'            width="70"  editable='false' />
        <grid:set col='cosmicOccurs'    width="150" hidden='f:true'  editable='false'/>
        <grid:set col='exon'            width="70"  editable='false'/>
        <grid:set col='cytoband'        width="50"  editable='false'/>
        <grid:set col='cadd'            width="50"  editable='false' formatter='number'/>
        <grid:set col='cadd_phred'      width="50"  editable='false' formatter='number'/>
        <grid:set col='vepHgvsg'        width="150" hidden='f:true' editable='false'/>
        <grid:set col='vepHgvsc'        width="150" hidden='f:true' editable='false'/>
        <grid:set col='vepHgvsp'        width="150" hidden='f:true' editable='false'/>
        <grid:set col='mutStatus'       width="150" hidden='f:true' editable='false'/>
        <grid:set col='mutError'        width="150" hidden='f:true' editable='false'/>
    </grid:grid>
    <grid:exportButton name="allVariants" formats="['csv', 'excel']"/>
    <br/>
</div>
<script>
    $(".menuButton .csv, .menuButton .excel").click(function(event){
        var records = jQuery("#allVariants_table").getGridParam('records');
        if(records > 5000 && records < 75000) {
            if(!confirm("You're requesting "+records+" rows of data, this may take up to 5 mins. Are you sure you want to do this?")) {
                event.preventDefault();
            }
        } else if (records > 75000) {
            alert("You can't download more than 75000 rows of data this way, please filter your query and try again, or raise a JIRA issue to request a manual dump of this data.");
            event.preventDefault();
        }
    });
</script>

<script>
    var allTags = <g:allTags/>;
    var tagModule = PathOS.tags.buildModule({
        object: 'seqvariant',
        tags: [],
        availableTags: Object.keys(allTags)
    });

    var current_id = false;
    $("#allVariants_table").on('click', function(){
        setTimeout(function(){
            if (current_id != $(".ui-row-ltr.ui-state-highlight").attr('id')) {
                current_id = $(".ui-row-ltr.ui-state-highlight").attr('id');
                PathOS.tags.update_object(current_id);
            }
        }, 200);
    });
</script>


</body>

<r:script>
    /**
     * Formatter for colouring and linking to a curated CurVariant
     *
     * @param cellvalue      PLON 5-level pathogenicity string
     * @param options
     * @param rowObject      Array of cell values: expecting the first value to be the ID of the linked CurVariant
     * @returns {string}     Link to curated CurVariant nicely coloured by pathogenicity
     */
    function variantFormatter( cellvalue, options, rowObject )
    {
        if ( cellvalue == null || cellvalue.length == 0 ) return '';

        var fld = classFormatter( cellvalue, options, rowObject );

        //  Todo: this is a maintenance risk - hard wired link and positional parameter to pass an object linkage through to Javascript
        //

        return "<a href='<g:context/>/curVariant/sampleLink?id=" + rowObject[0] + "'>" + fld + '</a>';
    }


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

    /**
    * Formatter for gene linking to genecards
    *
    * @param cellvalue      gene
    * @param options
    * @param rowObject      Row
    * @returns {string}     Link to gene cards www.genecards.org/cgi-bin/carddisp.pl?gene=TP53
    */
    function geneFormatter( cellvalue, options, rowObject )
    {
        //  Link to Gene cards for this gene
        //
        return "<a href='http://www.genecards.org/cgi-bin/carddisp.pl?gene=" + cellvalue + "' target='_blank'>" + cellvalue + '</a>';
    }


    setTimeout(function(){
        $("#allVariants_table_seqrun").attr("aria-selected", "true");
    }, 500);

</r:script>

</html>
