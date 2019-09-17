<%@ page import="grails.converters.JSON; org.petermac.pathos.pipeline.UrlLink; org.petermac.pathos.curate.*" %>

<r:require module="pmac-easygrid"/>

<script src="<g:context/>/js/jquery.jqGrid.override.js?v=<g:render template='/gitHash'/>"></script>

<r:style type="text/css">
.ui-jqgrid .ui-jqgrid-htable th     { vertical-align: top; }
.ui-jqgrid .ui-jqgrid-htable th div { height: 30px; }
.ui-tabs { font-size: 12px; }
#filterNotification   {
    margin: 5px;
    font-size: 12px;

    width: 900px;
    /*color:#666666;*/
}
#filterShowMessage {
    text-decoration: underline;
    color: blue;
    cursor: pointer;

}

#cnvfilterNotification   {
    margin: 5px;
    font-size: 12px;

    width: 900px;
    /*color:#666666;*/
}
#cnvfilterShowMessage {
    text-decoration: underline;
    color: blue;
    cursor: pointer;

}
.reportsection {
    color: #666666;
    font-size: 0.8em;
    margin: 1em 2.1em;
    padding: 0 0.25em;
}

#firstReviewRevokeForm {
    overflow: auto;
}

#finalReviewRevokeForm {
    overflow: auto;
}


.ui-tabs {
    padding: 0em;
    position: relative;
}
.hideshowbutton {
    colour:blue;
    text-decoration:underline;
    cursor: pointer
}


#pat_sample_hollyinfo .fieldcontain .property-value {
    overflow: auto;
    word-wrap: break-word;
    white-space: normal;
}

td, th {
    line-height: 0.9em;
}


%{--th[aria-selected="true"] {--}%
    %{--background: linear-gradient(#E4F2FB,#E4F2FB,#AECBE4,#AECBE4) !important;--}%
%{--}--}%

.ui-dialog { z-index: 1003 !important ;}


#tag_text_area {
    width: 100%;
}
#tags .tagdiv span {
    font-size: 14px;
}

#ui-id-2 {
    z-index: 99999;
}

#showTags span {
    font-size: 11.2px;
}

#ViewTbl_curation_table_2 {
    position: absolute;
    bottom: 0;
}

body {
    overflow: auto;
}
%{--
#gridContainerDiv {
    height: 100%;
}
body::-webkit-scrollbar {
    position: fixed;
    bottom: 0;
    width: 15px !important;
    height: 15px !important;
    background-color: #333;
}

body::-webkit-scrollbar-thumb {
    border: 2px solid #222;
    border-radius: 10px;
    background: #07447c;
}
body::-webkit-scrollbar-corner {
    background-color: #333;
}
--}%


a.cv-button.authorised {
    cursor: pointer;
    opacity: 1;
}
a.cv-button {
    cursor: help;
    opacity: 0.5;
}
#splashScreen {
    text-align: center;
    margin: 50px;
    padding: 10px;
    border: solid 3px black;
}
#splashScreen input {
    margin: 0 10px;
}
</r:style>

<div id="tabs">
    <g:if test="${cnvSize > 0}">
        <ul>
            <li><a href="#tabs-1">SeqVariants</a></li>
            <li><a href="#tabs-2">SeqCNVs</a></li>
        </ul>
    </g:if>

    <div id="tabs-1">

        <div id="gridContainerDiv">

            <div id="filterNotification">
                A <span id="appliedfiltertype"></span>filter has been applied. <span id="postfilter_count">0</span> of <span id="prefilter_count">${svSize}</span> records pass filter. <span id="filterShowMessage">Show filter details</span> <br/>
                <div id="filterDescription" style="display:none;"></div>
                <br/>

            </div>

            <grid:grid  name="curation">
                <grid:set multiSort="true"/>
                <grid:set sortorder="desc"/>


%{--  We can set arbitrary multiple sort orders!!!!  --}%
%{--  We should let users order the columns as they wish  --}%
%{--  We should default it with ACMG and Other Contexts first  --}%
                %{--<grid:set sortname="acmgCurVariant+desc%2C+ampCurVariant+desc"/>--}%
                %{--<grid:set sortname="acmgCurVariant desc,allCuratedVariants desc,ampCurVariant desc,overallCurVariant desc,reportable desc"/>--}%
                <grid:set sortname="${sortPriority.tokenize(",").each { it + " desc"}.join(",")}"/>

                %{--<grid:set sortname="overallCurVariant"/>--}%

                <grid:set rowNum="${svlistRows}"/>
                <grid:set caption='${svSize} Total Sequenced Variants'/>

                <grid:set col='act' width="45" formatter="f:editAction"/>

                <grid:set col='acmgCurVariant' formatter="f:acmgCVFormatter" cellattr='f:acmgTooltip' width="100" />

                <grid:set col='ampCurVariant' formatter="f:ampCVFormatter" cellattr='f:ampTooltip' width="100"/>
                <grid:set col='overallCurVariant' formatter="f:overallCVFormatter" cellattr='f:csTooltip' width="90" />

                <grid:set col='allCuratedVariants' formatter="f:allCVFormatter" cellattr="f:allCVcellattr" width="65" editable="false"/>

                <grid:set col='gene'            width="70"  formatter='f:geneFormatter' editable='false'/>
                <grid:set col='siftCat'         width="45"  formatter='f:classFormatter' editable='false'/>
                <grid:set col='mutTasteCat'     width="45"  formatter='f:classFormatter' editable='false'/>
                <grid:set col='polyphenCat'     width="45"  formatter='f:classFormatter' editable='false'/>
                <grid:set col='lrtCat'          width="45"  formatter='f:classFormatter' editable='false'/>
                <grid:set col='mutAssessCat'    width="45"  formatter='f:classFormatter' editable='false'/>
                <grid:set col='fathmmCat'       width="45"  formatter='f:classFormatter' editable='false'/>
                <grid:set col='metaSvmCat'      width="45"  formatter='f:classFormatter' editable='false'/>
                <grid:set col='metaLrCat'       width="45"  formatter='f:classFormatter' editable='false'/>
                <grid:set col='variant'         width="130" editable='false'/>
                <grid:set col='sampleName'      width="65"  hidden='f:true' editable='false' />
                <grid:set col='clin_sig'        width="70"  editable='false'/>
                <grid:set col='filterFlag'      width="100" editable='false' cellattr='f:filterFlagTooltip'/>
                <grid:set col='reportable'      formatter="f:allowCheckbox"/>
                <grid:set col='curate'          formatter="f:allowCheckbox"/>
                <grid:set col='hgvsc'           width="170" editable='false'/>
                <grid:set col='hgvsp'           width="150" editable='false'/>
                <grid:set col='consequence'     width="150" editable='false'/>
                <grid:set col='gmaf'            width="50"  editable='false' formatter='number'/>
                <grid:set col='esp'             width="50"  editable='false' formatter='number'/>
                <grid:set col='exac'            width="60"  editable='false'/>
                <grid:set col='exon'            width="70"  editable='false'/>
                <grid:set col='cytoband'        width="50"  hidden='true' editable='false'/>
                <grid:set col='cadd'            width="50"  editable='false' formatter='number'/>
                <grid:set col='cadd_phred'      width="50"  editable='false' formatter='number'/>
                <grid:set col='zygosity'        width="70"  align= 'center'  editable='false' hidden="${seqSample?.panel?.panelGroup != 'MP FLD Germline Production'}"/>
                <grid:set col='varFreq'         width="65"  editable='false' />
                <grid:set col='varDepth'        width="55"  editable='false' formatter='integer'/>
                <grid:set col='readDepth'       width="55"  editable='false' formatter='integer'/>
                <grid:set col='cosmicOccurs'    width="150" hidden='f:true'  editable='false'/>
                <grid:set col='pubmed'          width="70"  hidden='f:true'  editable='false'/>
                <grid:set col='varPanelPct'     width="80"  hidden='f:false'  editable='false' />
                <grid:set col='varPanelPctFormula'     width="80"  hidden='f:false'  editable='false' />
                <grid:set col='ens_transcript'  width="70"  hidden='f:true'  editable='false'/>
                <grid:set col='ens_gene'        width="70"  hidden='f:true'  editable='false'/>
                <grid:set col='ens_protein'     width="70"  hidden='f:true'  editable='false'/>
                <grid:set col='ens_canonical'   width="70"  hidden='f:true'  editable='false'/>
                <grid:set col='refseq_mrna'     width="90"  hidden='f:true'  editable='false'/>
                <grid:set col='refseq_peptide'  width="90"  hidden='f:true'  editable='false'/>
                <grid:set col='existing_variation' width="70" hidden='f:true'  editable='false'/>
                <grid:set col='domains'         width="70"  hidden='f:true'  editable='false'/>
                <grid:set col='genedesc'        width="70"  hidden='f:true'  editable='false'/>
                <grid:set col='omim_ids'        width="70"  hidden='f:true'  editable='false'/>
                <grid:set col='biotype'         width="70"  hidden='f:true'  editable='false'/>
                <grid:set col='cosmic'          formatter="f:annoFormatter" editable='false'/>
                <grid:set col='siftVal'         width="45"  hidden='f:true' editable='false'/>
                <grid:set col='mutTasteVal'     width="45"  hidden='f:true' editable='false'/>
                <grid:set col='polyphenVal'     width="45"  hidden='f:true' editable='false'/>
                <grid:set col='clinvarVal'      width="45"  hidden='f:true' editable='false'/>
                <grid:set col='clinvarCat'      width="45"  hidden='f:true' formatter='f:classFormatter' editable='false'/>
                <grid:set col='lrtVal'          width="45"  hidden='f:true' editable='false'/>
                <grid:set col='mutAssessVal'    width="45"  hidden='f:true' editable='false'/>
                <grid:set col='fathmmVal'       width="45"  hidden='f:true' editable='false'/>
                <grid:set col='metaSvmVal'      width="45"  hidden='f:true' editable='false'/>
                <grid:set col='metaLrVal'       width="45"  hidden='f:true' editable='false'/>
                <grid:set col='vepHgvsg'        width="150" hidden='f:true' editable='false'/>
                <grid:set col='vepHgvsc'        width="150" hidden='f:true' editable='false'/>
                <grid:set col='vepHgvsp'        width="150" hidden='f:true' editable='false'/>
                <grid:set col='mutStatus'       width="150" hidden='f:true' editable='false'/>
                <grid:set col='mutError'        width="150" hidden='f:true' editable='false'/>
                <grid:set col='varcaller'       width="70"  hidden='f:false' editable='false'/>
                <grid:set col='amps'            width="70"  hidden='f:true'  editable='false'/>
                <grid:set col='numamps'         width="70"  hidden='f:false' editable='false'/>
                <grid:set col='ampbias'         width="70"  hidden='f:false'  editable='false'/>
                <grid:set col='homopolymer'     width="55"  hidden='f:false' editable='false'/>
                <grid:set col='tags' width="200" title="false" formatter="f:tagsFormatter" label="Tags"/>
            </grid:grid>

            <grid:exportButton name="curation" formats="['csv', 'excel']" exportId="123456"/>
            <br/>
        </div>

    </div>
    <g:if test="${cnvSize > 0}">
        <div id="tabs-2" style="padding: 0;">

            <div style="margin: 2em;" id="snvGridContainerDiv-1">

                <grid:grid  name="cnv">
                    <grid:set caption='Sequenced CNVs'/>
                    <grid:set col='seqSample'           width="70"  hidden='f:true'  editable='false'/>
                    <grid:set col='tags'                width="200" title="false" formatter="f:tagsFormatter" label="Tags"/>
                    <grid:set col='chr'                 width="100" hidden='f:false' editable='false'/>
                    <grid:set col='start'               width="70"  hidden='f:false' editable='false'/>
                    <grid:set col='end'                 width="70"  hidden='f:false' editable='false'/>
                    <grid:set col='resolution'          width="100" hidden='f:false' editable='false'/>
                    <grid:set col='arm'                 width="70"  hidden='f:false' editable='false'/>
                    <grid:set col='gene'                width="100" hidden='f:false' editable='false'/>
                    <grid:set col='exon'                width="70"  hidden='f:false' editable='false'/>
                    <grid:set col='copyNumber'          width="70"  hidden='f:false' editable='false' formatter="f:realFormatter"/>
                    <grid:set col='copyNumberStdDev'    width="70"  hidden='f:false' editable='false' formatter="f:realFormatter"/>
                    <grid:set col='zScore'              width="70"  hidden='f:false' editable='false' formatter="f:realFormatter"/>
                    <grid:set col='transcript'          width="100" hidden='f:false' editable='false'/>
                    <grid:set col='gaffa'               width="100" hidden='f:false' editable='false' formatter="f:linksFormatter"/>

                </grid:grid>
                <grid:exportButton name="cnv" formats="['csv', 'excel']" exportId="123457"/>
            </div>
        </div>
    </g:if>
</div>

<div id="svlistContextMenu" class="hidden">
    <table>
        <tr id="contextMenuName" class="hidden">
            <th colspan='2'><span id="contextMenuGene"></span> - <span id="contextMenuHGVSC"></span></th>
        </tr>
        <tr id="cmCuration">
            <td><i class="fa fa-pencil-square-o" aria-hidden="true"></i></td>
            <td><a id="cmCurationLink">Mark for Curation</a></td>
        </tr>

        <tr id="cmReportable">
            <td><i class="fa fa-address-card-o" aria-hidden="true"></i></td>
            <td><a id="cmReportableLink">Mark as Reportable</a></td>
        </tr>

        <tr id="cmIGV">
            <td><img src="<g:context/>/images/igv_logo.png" class="contextMenuLogo"></td>
            <td><a>IGV</a></td>
        </tr>

        <tr id="cmGoogle">
            <td><img src="<g:context/>/images/google_logo.png" class="contextMenuLogo"></td>
            <td><a target="_blank" href="#">Google</a></td>
        </tr>

        <tr id="cmAlamut">
            <td><img src="<g:context/>/images/alamut_logo.png" class="contextMenuLogo"></td>
            <td><a target="_blank" href="#">Alamut</a></td>
        </tr>


    </table>
</div>













<r:script>

    $(function() {
        $( "#tabs" ).tabs();
    });

    function hideshowreports(element)
    {
        var which = document.getElementById(element)
        if (!which)
            return
        if ($('#hideshowreports').text() == 'Hide previously generated reports') {
            $('#'+element).slideToggle("fast");
            $('#hideshowreports').text('Show previously generated reports');
            }
        else {

             $('#'+element).slideToggle("fast");
            $('#hideshowreports').text('Hide previously generated reports');
        }

        return false;

    }

function reloadGrid()
{
    jQuery('#curation_table')
        // .setGridParam({sortname:'allCuratedVariants',sortorder:'desc'})
        .trigger('reloadGrid');

<g:if test="${cnvSize > 0}">
    setTimeout(function(){
        jQuery("#cnv_table").trigger('reloadGrid');
    }, 1000);
</g:if>

    return true;
}


/**
* Add explanatory tooltip descriptions for filterflags
*
* @param rowId
* @param val        List of comma separated filter flags
* @param rawObject
* @param cm
* @param rdata
* @returns {string} Cell title attribute title="tooltip description"
*/
function filterFlagTooltip(rowId, val, rawObject, cm, rdata)
{
    var arr = val.split(',');
    var ttl = '';
    for(var i = 0; i <  arr.length; i++)
    {
        desc = '';
        if ( arr[i] == 'pass' ) desc = "${VarFilterService.filters['pass']}";
        if ( arr[i] == 'pnl'  ) desc = "${VarFilterService.filters['pnl' ]}";
        if ( arr[i] == 'gaf'  ) desc = "${VarFilterService.filters['gaf' ]}";
        if ( arr[i] == 'con'  ) desc = "${VarFilterService.filters['con' ]}";
        if ( arr[i] == 'vad'  ) desc = "${VarFilterService.filters['vad' ]}";
        if ( arr[i] == 'vrd'  ) desc = "${VarFilterService.filters['vrd' ]}";
        if ( arr[i] == 'vaf'  ) desc = "${VarFilterService.filters['vaf' ]}";
        if ( arr[i] == 'nof'  ) desc = "${VarFilterService.filters['nof' ]}";
        if ( arr[i] == 'blk'  ) desc = "${VarFilterService.filters['blk' ]}";
        if ( arr[i] == 'sin'  ) desc = "${VarFilterService.filters['sin' ]}";
        if ( arr[i] == 'amp'  ) desc = "${VarFilterService.filters['amp' ]}";
        if ( arr[i] == 'oor'  ) desc = "${VarFilterService.filters['oor' ]}";
        if ( ttl.length > 1 ) ttl = ttl + '\n';
        ttl = ttl + arr[i] + ': ' + desc;
    }

    var entityMap = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;',
        '/': '&#x2F;',
        '`': '&#x60;',
        '=': '&#x3D;'
    };

    ttl = String(ttl).replace(/[&<>"'`=\/]/g, function (s) {
        return entityMap[s];
    });

    return 'title="' + ttl + '"';
}


/**
* Add cosmic details tooltip for curated variant
*
* @param rowId      the id of the row
* @param val        the value which will be added in the cell
* @param rawObject  the raw object of the data row
* @param cm         all the properties of this column listed in the colModel
* @param rdata      the data row which will be inserted in the row
*
* @returns {string} Cell title attribute title="tooltip description"
*/
function cosmicTooltip(rowId, val, rawObject, cm, rdata)
{
    if ( rdata['cosmic'] == '' ) return '';

    var ttl = rdata['cosmicOccurs'];

        var entityMap = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;',
        '/': '&#x2F;',
        '`': '&#x60;',
        '=': '&#x3D;'
    };

    ttl = String(ttl).replace(/[&<>"'`=\/]/g, function (s) {
        return entityMap[s];
    });

    return 'title="' + ttl + '"';
}

<r:style>
.external_annotations img {
    width: 16px;
    cursor: pointer;
    height: 16px;
    margin: 2px;
}
.external_annotations img:hover {
    border: 2px solid grey;
}


</r:style>



function annoFormatter( cellvalue, options, rowObject ) {
    var id = rowObject[2];
    var output = ""
    var sandbox = d3.select("body").append("div");

    var box = sandbox.append("div")
        .classed("external_annotations", true)
        .attr("id","external-annotations-"+id);

    output = sandbox.html();
    sandbox.remove();

    $.ajax({
        url: PathOS.application+"/SeqVariant/fetchAnnotations/"+id,
        complete: function(d){
            //do some stuff when it comes back.
            if(d.status == 200 && d.responseJSON) {
                var data = d.responseJSON;
                var box = d3.select("#external-annotations-"+id);

                function addBoxLink(d) {
                    box.append("a").attrs({
                        target: "_blank",
                        href: d.link,
                        title: d.data
                    }).append("img").attrs({
                        src: d.icon
                    });
                }

                if(data.cosmic) {
                    if(data.cosmic.indexOf(",") >= 0) {
                        data.cosmic.split(",").forEach(function(d) {
                            var cosm = d.slice(4);
                            addBoxLink({
                                icon: PathOS.application+"/images/cosmic_icon.png",
                                title: data.cosmicOccurs,
                                link: "https://cancer.sanger.ac.uk/cosmic/mutation/overview?genome=37&id="+cosm
                            });
                        });
                    } else if (data.cosmic.indexOf("COSM") >= 0) {
                        var cosm = data.cosmic.slice(4);
                        addBoxLink({
                            icon: PathOS.application+"/images/cosmic_icon.png",
                            title: data.cosmicOccurs,
                            link: "https://cancer.sanger.ac.uk/cosmic/mutation/overview?genome=37&id="+cosm
                        });
                    } else {
                        addBoxLink({
                            icon: PathOS.application+"/images/cosmic_icon.png",
                            title: data.cosmicOccurs,
                            link: PathOS.application+"/seqVariant/cosmicAction?id="+id
                        });
                    }
                }

                if(data.civic.length > 0) {
                    data.civic.forEach(function(civic){
                        addBoxLink({
                            icon: PathOS.application+"/images/civic_icon.png",
                            title: civic.civicInfo,
                            link: PathOS.application+"/civicVariant/show?id="+civic.civicId
                        });
                    });
                }

                if(data.drug) {
                    addBoxLink({
                        icon: PathOS.application+"/images/molecularmatch_icon.png",
                        title: data.drugInfo,
                        link: PathOS.application+"/drug/list?target_gene="+data.drug
                    });
                }
                if(data.trial) {
                    addBoxLink({
                        icon: PathOS.application+"/images/molecularmatch_icon.png",
                        title: data.trialInfo,
                        link: PathOS.application+"/trial/list?target_gene="+data.trial
                    });
                }

                if(data.dbsnp) {
                    if(data.dbsnp.indexOf("rs") > 0) {
                        var dbsnp = data.dbsnp.slice(2);
                        addBoxLink({
                            icon: PathOS.application+"/images/dbSNP_logo.png",
                            title: "dbSNP",
                            link: "https://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs="+dbsnp
                        });
                    } else {
                        addBoxLink({
                            icon: PathOS.application+"/images/dbSNP_logo.png",
                            title: "dbSNP",
                            link: PathOS.application+"/seqVariant/dbsnpAction?id="+id
                        });
                    }
                }
            }
        }
    });

    return output;

}


















/**
* This is to solve PATHOS-2474
* We're overwriting the traditional easygrid action boxes
* DKGM 29-June-2017
*/
function allowCheckbox( cellvalue, options, rowObject ) {
    var output = '<input type="checkbox" checked="checked">'
    if(cellvalue == "False" || cellvalue == false) {
        output = '<input type="checkbox">'
    }

    return output
}

/**
* This is to solve PATHOS-2474
* We're overwriting the traditional easygrid action boxes
* DKGM 29-June-2017
*/
function editRow(id) {
    $("#rowActions_"+id).toggleClass("editing");
}

/**
* This is to solve PATHOS-2474
* We're overwriting the traditional easygrid action boxes
* DKGM 29-June-2017
*/
function saveRow(id) {
    d3.select("#gbox_curation_table .loading").style("display", "block");

    var curate = $('tr[id='+id+'] [aria-describedby="curation_table_curate"] input').is(":checked");
    var reportable = $('tr[id='+id+'] [aria-describedby="curation_table_reportable"] input').is(":checked");

    console.log(id + " is "+ reportable +" "+ curate);
    var package = {
        id: id,
        curate: curate,
        reportable: reportable
    };
    curationInlineEdit(package);
}

// For the right click contextmenu
function markAsReportable() {
    var id = PathOS.tags.current_object;
    if (id) {
        var package = {
            id: id,
            reportable: true
        };
        curationInlineEdit(package);
    } else {
        alert("Error");
    }
    hideContextMenu();
}

// For the right click contextmenu
function markForCuration() {
    var id = PathOS.tags.current_object;
    if (id) {
        var package = {
            id: id,
            curate: true
        };
        curationInlineEdit(package);
    } else {
        alert("Error");
    }
    hideContextMenu();
}

function curationInlineEdit(package) {
    $.ajax({
        type: "POST",
        url: PathOS.application+"/SeqVariant/curationInlineEdit",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: JSON.stringify(package),
        complete: function(d){
            if(d.status == 500) {
                console.log(d);
                PathOS.notes.addError("Warning, server error. Please notify admins.");
            } else {
                console.log(d.responseJSON);
                if(d.responseJSON.message) {
                    d.responseJSON.message.forEach(function(message){
                        PathOS.notes.add(message);
                    })
                }
            }
            reloadGrid();
        }
    });
}

/**
* This is to solve PATHOS-2474
* We're overwriting the traditional easygrid action boxes
* DKGM 29-June-2017
*/
function editAction( cellvalue, options, rowObject ) {
    var output = '';
    // seqVariant id, hardcoded in the seqVariantController, curationgrid definition
    var id = rowObject[2];
    var sandbox = d3.select("body").append("div");
    var rowActions = sandbox.append("div").attrs({
        id: "rowActions_"+id,
        class: "rowActions"
    });

    rowActions.append("div").attrs({
        title: "Edit selected row",
        class: "editButton ui-pg-div ui-inline-edit",
        id: "editButton_"+id,
        onclick: "editRow("+id+")"
    }).append("span").classed("ui-icon ui-icon-pencil", true);

    rowActions.append("div").attrs({
        title: "Submit",
        class: "saveButton ui-pg-div ui-inline-edit",
        id: "saveButton_"+id,
        onclick: "saveRow("+id+")"
    }).append("span").classed("ui-icon ui-icon-disk", true);

    rowActions.append("div").attrs({
        title: "Cancel",
        class: "cancelButton ui-pg-div ui-inline-edit",
        id: "cancelButton_"+id,
        onclick: "editRow("+id+")"
    }).append("span").classed("ui-icon ui-icon-cancel", true);

    var box = sandbox.append("div")
        .classed("tags_field", true);

    output = sandbox.html();
    sandbox.remove();

    return output;
}


/**
*
*/
function acmgFormatter( cellvalue, options, rowObject ) {
    // console.log(cellvalue);
    // console.log(options);
    // console.log(rowObject);
    return cellvalue
}


/**
* Formatter for colouring and linking to a curated CurVariant
*
* @param cellvalue      PLON 5-level pathogenicity string
* @param options
* @param rowObject      Array of cell values: expecting the first value to be the ID of the linked CurVariant
* @returns {string}     Link to curated CurVariant nicely coloured by pathogenicity
*/
function acmgCVFormatter( cellvalue, options, rowObject )
{

    /*
    We use the number 8 here because in is defined in SeqVariantController/curationGrid
    it is the 9th variable that list. Easygrid injects code into svlist which is how the
    grid knows what column does what.
    Unfortunately this information is not accessible in javascript so we have a magic number here.
    DKGM 8-December-2016
     */
    var allContexts = JSON.parse(rowObject[11]);
    var hgvsg = rowObject[19];
    var current_cc_match = null;

    allContexts.forEach(function(d){
        if(PathOS.svlist.ccc(d.clinContext, seqSample.clinContext)) {
            current_cc_match = d;
        }
    });

    if( current_cc_match && PathOS.svlist.ccc(current_cc_match.clinContext, seqSample.clinContext) ) {
        return drawCV(current_cc_match, options.rowId, hgvsg);
    } else {
        return "";
    }
}

var ampClassLookup = {
    "Unclassified": "Unclassified",
    "Tier IV": "C1",
    "Tier III": "C3",
    "Tier II": "C4",
    "Tier I": "C5"
};

function ampCVFormatter( cellvalue, options, rowObject )
{
    if( cellvalue ) {
        var allContexts = JSON.parse(rowObject[11]);
        var hgvsg = rowObject[19];
        var current_cc_match = null;

        allContexts.forEach(function(d){
            if(PathOS.svlist.ccc(d.clinContext, seqSample.clinContext)) {
                current_cc_match = d;
            }
        });

        return "<a target='_blank' href='<g:context/>/curVariant/show?id="+current_cc_match.id+"' class='cvlabel cv-"+ampClassLookup[cellvalue]+"'>"+cellvalue+"</a> "
    } else {
        return "";
    }
};

var overallClassLookup = {
    "Unclassified": "Unclassified",
    "NCS: Not Clinically Significant": "C1",
    "UCS: Unclear Clinical Significance": "C3",
    "CS: Clinically Significant": "C5"
};

function overallCVFormatter( cellvalue, options, rowObject )
{
    if( cellvalue ) {
        var allContexts = JSON.parse(rowObject[11]);
        var hgvsg = rowObject[19];
        var current_cc_match = null;

        allContexts.forEach(function(d){
            if(PathOS.svlist.ccc(d.clinContext, seqSample.clinContext)) {
                current_cc_match = d;
            }
        });

        return "<a target='_blank' href='<g:context/>/curVariant/show?id="+current_cc_match.id+"' class='cvlabel cv-"+overallClassLookup[cellvalue]+"'>"+cellvalue.split(":")[0]+"</a> "
    } else {
        return "";
    }
};



/**
* DKGM 27-June-2017
* This function formats the easygrid cell so that tags are shown in a useful way.
* Parts of this should be refactored to main.js so that it is more useable.
*
*/
function tagsFormatter( cellvalue, options, rowObject )
{
    // Cell value should be a map with two parts:
    //  meta:
    //      domainClass:    the name of the domain class the tag is being attached to
    //      objId:          the id of the specific domain object we're attaching tags to
    //  tags:               the list of tag objects with the id, label, etc

    var cellData = JSON.parse(cellvalue);

    var output = '';

    var domCls = cellData.meta.domainClass;
    var objId = cellData.meta.objId;
    var id = domCls + "-" + objId;

    var sandbox = d3.select("body").append("div");

    var box = sandbox.append("div")
        .classed("tags_field", true)
        .attr("data-dom-cls", domCls)
        .attr("data-obj-id", objId);

    box.append("span")
        .classed("inline-tag", true)
        .text("+")
        .attr("id", "tagAdder-"+objId)
        .attr("onclick", "addTag(this.id)")
        .style("cursor", "pointer");

    cellData.tags.sort(function(a,b){return a.id - b.id;})
        .forEach(function(tag){
            box.append("span")
                .classed("inline-tag", true)
                .attr("data-tag-id", tag.id)
                .attr("title", tag.description || "No Description")
                .text(tag.label);
        });

    output = sandbox.html();
    sandbox.remove();

    return output;
}

function addTag(elemId) {
    console.log("ok we're adding this tag ", elemId);

    var elem = document.getElementById(elemId);
    var domCls = elem.parentNode.getAttribute("data-dom-cls");
    var id = elem.parentNode.getAttribute("data-obj-id");

    console.log('domCls = ' + domCls);
    console.log('objId = ' + id);

    var overlay = PathOS.overlay.init({
        styles: {
            position: "fixed",
            top: "50%",
            left: "30%",
            height: "150px",
            width: "250px",
            background: "rgba(255,255,255,1)",
            "z-index": "99999"
        }
    });

    $("#overlay-dialog-box")
        .detach()
        .appendTo("#tagAdder-"+id)
        .keydown(function(event){
            event.stopPropagation();
        });

    overlay.append("h3").text("Add a tag");

    var table = overlay.append("table");

    var tr = table.append("tr");
    tr.append("td").text("Label");
    tr.append("td").append("input")
        .attr("id", "popup-tag-label")
        .on("change", updateDescription);

    tr = table.append("tr");
    tr.append("td").text("Description");
    tr.append("td").append("textarea")
        .attrs({
            id: "popup-tag-description",
            rows: 5,
            maxlength: 1000
        });

    tr = table.append("tr");
    tr.append("td");
    tr.append("td").append("input")
        .attr("id", "tag-add-input")
        .attr("data-dom-cls", domCls)
        .attr("data-obj-id", id)
        .attr("type", "button")
        .attr("value", "Save")
        .on("click", savePopupTag);

    $("#popup-tag-label").autocomplete({
        source: Object.keys(allTags),
        select: updateDescription
    });

    function updateDescription(){
        var label = $("#popup-tag-label").val();
        var key = lowercaseAllTags.indexOf(label.toLowerCase());
        if(key >= 0) {
            var description = allTags[Object.keys(allTags)[key]];
            if(description) {
                $("#popup-tag-description").val(description);
            }
        }
    }

    function savePopupTag() {
        var domCls = $("#tag-add-input").attr("data-dom-cls");
        var id = $("#tag-add-input").attr("data-obj-id") * 1;

        console.log('domCls = ' + domCls);
        console.log('id = ' + id + ' ' + (typeof id));

        var label = $("#popup-tag-label").val();
        var description = $("#popup-tag-description").val();

        var package = {
            id: id,
            label: label,
            description: description,
            type: domCls
        };

        console.log(package);
        if(package.label !== "") {
            $.ajax({
                type: "POST",
                url: "<g:context/>/Tag/betterAddTag",
                complete: function(d){
                    //console.log("Success");
                    //console.log(d.responseJSON);
                    PathOS.overlay.close();
                    reloadGrid();
                },
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                data: JSON.stringify(package)
            });
        } else {
            alert("Please enter a label your tag!");
        }
    }
}



var seqSample = { clinContext: { id: ${(seqSample?.clinContext?.id) ?: 0} }};

function allCVFormatter( cellvalue, options, rowObject )
{
    var data = JSON.parse(cellvalue),
        result = "",
        worstClass = 0,
        pmClasses = ['Unclassified', 'C1', 'C2', 'C3', 'C4', 'C5'],
        title = [];
    if(data.length >= 1) {
        data.forEach(function(cv){
            var pmClass = cv.pmClass.split(":")[0];
            var code = cv.clinContext ? cv.clinContext.code : "Generic";
            title.push(pmClass+":"+code);
            if(pmClasses.indexOf(pmClass) > worstClass) {
                worstClass = pmClasses.indexOf(pmClass);
            }
        });
        result = "<a href='#cv-"+options.rowId+"' class='cv-"+pmClasses[worstClass]+"' title='"+title.join("\n")+"' onclick='PathOS.variant.viewer({svid:"+options.rowId+"})'>"+data.length+"</a>"
    }
    return result;
}


function allCVcellattr(rowId, val, rawObject, cm, rdata)
{
    if (val === "null"){
        console.log("val is a string that says null");
    } else {

    }
    return "";
}


function drawCV(cv, sv, hgvsg) {
    var name = "Generic",
        id = cv.id,
        pmClass = "Unclassified",
        ssClassText = "";

    if ( seqSample.clinContext === null ) {
        if( cv.clinContext === null ) {
            ssClassText = "ssClass ";
        }
    } else {
        if( cv.clinContext !== null ) {
            if ( seqSample.clinContext.id === cv.clinContext.id ) {
                ssClassText = "ssClass ";
            }
        }
    }

    if(cv.pmClass.indexOf(":") > -1) {
        pmClass = cv.pmClass.split(":")[0];
    } else {
        pmClass = cv.pmClass;
    }
    if(cv.clinContext) {
        name = cv.clinContext.code;
    }

    var authorised = "";

    if(cv.authorisedFlag) {
        authorised = " authorised";
    } else {
        if(pmClass && pmClass == "C4" || pmClass == "C5") {
            PathOS.notes.add(pmClass+' CurVariant "'+hgvsg+'" is not authorised');
        }
    }


    return "<a target='_blank' href='<g:context/>/curVariant/show?id="+id+"' class='"+ssClassText+"cvlabel cv-button cv-"+pmClass+authorised+"'>"+pmClass+":"+name+"</a> "
}






/**
* Add evidence tooltip for curated variant
*
* @param rowId      the id of the row
* @param val        the value which will be added in the cell
* @param rawObject  the raw object of the data row
* @param cm         all the properties of this column listed in the colModel
* @param rdata      the data row which will be inserted in the row
*
* @returns {string} Cell title attribute title="tooltip description"
*/
function acmgTooltip(rowId, val, rawObject, cm, rdata)
{
    var warning = "";

    if(val.indexOf("authorised") < 0) {
        warning = "Warning, variant is not authorised\n\n";
    }

    if ( val == '' ) return '';

    let titleText = warning + JSON.parse(rdata['curated_evd']).acmg;

    titleText = PathOS.escapeHtml(titleText);

    return 'title="' + titleText + '"';
}

function ampTooltip(rowId, val, rawObject, cm, rdata) {
    return 'title="'+PathOS.escapeHtml(JSON.parse(rdata['curated_evd']).amp)+'"';
}

function csTooltip(rowId, val, rawObject, cm, rdata) {
    return 'title="'+PathOS.escapeHtml(JSON.parse(rdata['curated_evd']).cs)+'"';
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

/**
* Formatter for CNV doubles
*
* @param cellvalue      double
* @param options
* @param rowObject      Row
* @returns {string}     neatly formatted double
*/
function realFormatter(cellvalue, options, rowObject)
{
    if ( cellvalue == null) return '';
    return cellvalue.toFixed(2);
}

/**
* Formatter for lists of links.
*
* @param cellvalue      list of link specifications
* @param options
* @param rowObject      Row
* @returns {string}     list of anchor tags
*/
function linksFormatter(cellvalue, options, rowObject)
{
    if ( cellvalue == null) return '';
    var res = '';
    for (var i = 0; i < cellvalue.length; i++) {
        var lnk = cellvalue[i];
        res += "<a href='" + lnk.url + "' target='_blank'>" + lnk.anchor + "</a>";
    }
    return res;
}

/**
* Column Chooser function - called when grid navigation icon clicked
*/
function columnChooser()
{
    jQuery('#curation_table').jqGrid(
        'columnChooser',
        {
            msel:   'multiselect',
            dlog:   'dialog',
            height: 500
        }
    )
}

/**
* get url GET param
**/
function getUrlParameter(sParam)
{
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++)
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam)
        {
            return sParameterName[1];
        }
    }
}

//reset prefs (either sample, if its reviewed, or personal - wrapper function for those two cases)
function resetPrefs() {

            resetCurateFilterPrefs()

}
function resetFilterPrefs()
{
    //ajax: are you sure?
    //remote call to reset everything (in fact, delete user pref object for our user)
    //reload grid w everything reset
    if(confirm("Are you sure you want to reset your column preferences?"))
    {
        sampleId = ${seqSample?.id}
    //Ok button pressed...
    ${remoteFunction(controller: 'seqVariant', action: 'resetUserGridPrefs',onComplete: 'window.location.reload()')};

        //$.jGrowl("Your preferences have been reset");
        //var curationGrid = $('#curation_table');

        // curationGrid.trigger('reloadGrid');

    }

}


function resetReviewedCurateFilterPrefs()
{
    //ajax: are you sure?
    //remote call to reset everything (in fact, delete sample pref object for our sample)
    //reload grid w everything reset
    if(confirm("Are you sure you want to reset the review seq sample preferences? This will clear them completely"))
    {
        sampleId = ${seqSample?.id}
    ${remoteFunction(controller: 'seqVariant', action: 'resetReviewedGridPrefs',onComplete: 'window.location.reload()',params: '{ssid: sampleId}')};

    }

}


//save reviewed sample filter settings
function saveReviewCurateFilterPrefs()
{
    outstring = '';
    showncols = [];
    hiddencols = [];
    showncolsstring = '';
    hiddencolsstring = '';

    var allColumns = $("#curation_table").jqGrid("getGridParam", "colModel").slice();
    allColumns.splice(0,1);


    for (var i=0; i < allColumns.length; i++) {
        colname = allColumns[i].name;


        if($('#curation_table_'+colname).is(':visible'))
        {
            //outstring = outstring + colname + ' VIS' + '\n';
            showncols.push(colname);
            showncolsstring = showncolsstring + colname +','
        } else {
            // outstring = outstring + colname + ' HIDDEN' + '\n';
            hiddencols.push(colname);
            hiddencolsstring = hiddencolsstring + colname +','
        }
    }
    hiddencolsJson = JSON.stringify(hiddencols);
    showncolsJson = JSON.stringify(showncols);
    /* save prefs... controller: 'SeqVariant',  */
    /*getGridParam("postData").filters;*/


    sampleId = ${seqSample?.id}

    searchFilterString = ( $("#curation_table").getGridParam("postData").filters  );
    if(!searchFilterString) {
        searchFilterString = '';
    }

    var gridInfo = new Object();
    var curationGrid = $('#curation_table');
    gridInfo.sortname = curationGrid.jqGrid('getGridParam', 'sortname');
    gridInfo.sortorder = curationGrid.jqGrid('getGridParam', 'sortorder');

    gridInfo.postData = curationGrid.jqGrid('getGridParam', 'postData');
    //gridInfo.search = curationGrid.jqGrid('getGridParam', 'search');

    var gridInfoParamsJson = (JSON.stringify(gridInfo));
    var remapOrder =  curationGrid.jqGrid('getGridParam', 'remapColumns');


    remapOrder = String(remapOrder)

    ${remoteFunction(controller: 'seqVariant', action: 'saveReviewedGridPrefs', params: '{columnsShowJSON: showncolsJson, columnsHideJSON: hiddencolsJson, gridColumnOrder: remapOrder, gridInfoJSON: gridInfoParamsJson, ssid: sampleId}')};


      PathOS.notes.add("The reviewed grid preferences have been saved");
}

//save filter prefs function: save either user grid settings (if not reviewed) or sample settings (if reviewed)
function saveFilterPrefs()
{
    if ( ${seqSample.finalReviewBy ? true : false } ) {
        <sec:ifAnyGranted roles="ROLE_ADMIN, ROLE_DEV, ROLE_CURATOR">
            saveReviewCurateFilterPrefs()
        </sec:ifAnyGranted>
        <sec:ifNotGranted roles="ROLE_ADMIN, ROLE_DEV, ROLE_CURATOR">
            PathOS.notes.addError("You cannot save the filter settings for a reviewed sample unless you are the administrator or curator");
        </sec:ifNotGranted>
     } else {
        saveCurateFilterPrefs()
     }

}

//save personal user filter settings
function saveCurateFilterPrefs()
{
    outstring = '';
    showncols = [];
    hiddencols = [];
    showncolsstring = '';
    hiddencolsstring = '';

    var allColumns = $("#curation_table").jqGrid("getGridParam", "colModel").slice();
    allColumns.splice(0,1);


    for (var i=0; i < allColumns.length; i++) {
        colname = allColumns[i].name;


        if($('#curation_table_'+colname).is(':visible'))
        {
            //outstring = outstring + colname + ' VIS' + '\n';
            showncols.push(colname);
            showncolsstring = showncolsstring + colname +','
        } else {
            // outstring = outstring + colname + ' HIDDEN' + '\n';
            hiddencols.push(colname);
            hiddencolsstring = hiddencolsstring + colname +','
        }
    }
    hiddencolsJson = JSON.stringify(hiddencols);
    showncolsJson = JSON.stringify(showncols);
    /* save prefs... controller: 'SeqVariant',  */
    /*getGridParam("postData").filters;*/


    sampleId = ${seqSample?.id}

    searchFilterString = ( $("#curation_table").getGridParam("postData").filters  );
    if(!searchFilterString) {
        searchFilterString = '';
    }

    var gridInfo = new Object();
    var curationGrid = $('#curation_table');
    gridInfo.sortname = curationGrid.jqGrid('getGridParam', 'sortname');
    gridInfo.sortorder = curationGrid.jqGrid('getGridParam', 'sortorder');

    gridInfo.postData = curationGrid.jqGrid('getGridParam', 'postData');
    //gridInfo.search = curationGrid.jqGrid('getGridParam', 'search');

    var gridInfoParamsJson = (JSON.stringify(gridInfo));
    var remapOrder =  curationGrid.jqGrid('getGridParam', 'remapColumns');


    remapOrder = String(remapOrder)

    ${remoteFunction(controller: 'seqVariant', action: 'saveUserGridPrefs', params: '{columnsShowJSON: showncolsJson, columnsHideJSON: hiddencolsJson, gridColumnOrder: remapOrder, gridInfoJSON: gridInfoParamsJson}')};


    PathOS.notes.add("Your preferences have been saved");

}

/////////////////////////
// FILTER TEMPLATES    //
/////////////////////////

//  Actionable CNVs
//
var actionableCnvs =
{ "groupOp": "AND",
    "groups": [
    {"groupOp": "OR",
      "rules": [
        { "field": "gene", "op": "eq", "data": "MYC" },
        { "field": "gene", "op": "eq", "data": "HER2" }
      ]
    }],
      "rules": [
        { "field": "pval", "op": "lt", "data": "0.05" },
        { "field": "lr_median", "op": "ge", "data": "1.5"}
      ]
};

//  Top Somatic Variants Search
//
var topSom =
{ "groupOp": "AND",
      "rules": [
        { "field": "filterFlag", "op": "nc", "data": "blk" },
        { "field": "filterFlag", "op": "nc", "data": "pnl" },
        { "field": "filterFlag", "op": "nc", "data": "gaf" },
        { "field": "filterFlag", "op": "nc", "data": "con" },
        { "field": "filterFlag", "op": "nc", "data": "sin" },
        { "field": "varFreq", "op": "ge", "data": "3"},
        { "field": "varcaller", "op": "ne", "data": "Canary" }
      ]
};

//  Haem Variants Search
//
var topHaem =
{ "groupOp": "AND",
      "rules": [
        { "field": "filterFlag", "op": "nc", "data": "blk" },
        { "field": "filterFlag", "op": "nc", "data": "pnl" },
        { "field": "filterFlag", "op": "nc", "data": "gaf" },
        { "field": "filterFlag", "op": "nc", "data": "con" }
      ]
};

//  Top Somatic CRC Search
//
var topCrc =
{ "groupOp": "AND",
    "groups": [
    {"groupOp": "OR",
      "rules": [
        { "field": "gene", "op": "eq", "data": "BRAF" },
        { "field": "gene", "op": "eq", "data": "KRAS" },
        { "field": "gene", "op": "eq", "data": "RNF43" },
        { "field": "gene", "op": "eq", "data": "NRAS" }
      ]
    }],
      "rules": [
        { "field": "filterFlag", "op": "nc", "data": "blk" },
        { "field": "filterFlag", "op": "nc", "data": "pnl" },
        { "field": "filterFlag", "op": "nc", "data": "gaf" },
        { "field": "filterFlag", "op": "nc", "data": "con" },
        { "field": "filterFlag", "op": "nc", "data": "sin" },
        { "field": "varFreq", "op": "ge", "data": "3"},
        { "field": "varcaller", "op": "ne", "data": "Canary" }
      ]
};

//  BRCA1/2 Only
//
var brcaOnly =
{ "groupOp": "AND",
    "groups": [
    {"groupOp": "OR",
      "rules": [
        { "field": "gene", "op": "eq", "data": "BRCA1" },
        { "field": "gene", "op": "eq", "data": "BRCA2" }
      ]
    }],
      "rules": [
        { "field": "filterFlag", "op": "nc", "data": "blk" },
        { "field": "filterFlag", "op": "nc", "data": "pnl" },
        { "field": "varFreq", "op": "ge", "data": "10"}
      ]
};

//  MPN Simple Search
//
var mpnSimple =
{ "groupOp": "AND",
    "groups": [
    {"groupOp": "OR",
      "rules": [
        { "field": "gene", "op": "eq", "data": "JAK2"  },
        { "field": "gene", "op": "eq", "data": "MPL"   },
        { "field": "gene", "op": "eq", "data": "CALR"  },
        { "field": "gene", "op": "eq", "data": "KIT"  },
        { "field": "gene", "op": "eq", "data": "SF3B1"  },
        { "field": "gene", "op": "eq", "data": "CSF3R"  },
        { "field": "gene", "op": "eq", "data": "ASXL1" }
      ]
    }],
      "rules": [
        { "field": "filterFlag", "op": "nc", "data": "blk" },
        { "field": "filterFlag", "op": "nc", "data": "pnl" },
        { "field": "filterFlag", "op": "nc", "data": "gaf" }
       ]
};


//  Top Somatic Melanoma Search
//
var topMel =
{ "groupOp": "AND",
    "groups": [
    {"groupOp": "OR",
      "rules": [
        { "field": "gene", "op": "eq", "data": "BRAF" },
        { "field": "gene", "op": "eq", "data": "NRAS" },
        { "field": "gene", "op": "eq", "data": "RAC1" },
        { "field": "gene", "op": "eq", "data": "KIT" }
      ]
    }],
      "rules": [
        { "field": "filterFlag", "op": "nc", "data": "blk" },
        { "field": "filterFlag", "op": "nc", "data": "pnl" },
        { "field": "filterFlag", "op": "nc", "data": "gaf" },
        { "field": "filterFlag", "op": "nc", "data": "con" },
        { "field": "filterFlag", "op": "nc", "data": "sin" },
        { "field": "varFreq", "op": "ge", "data": "3"},
        { "field": "varcaller", "op": "ne", "data": "Canary" }
      ]
};

//  Top Somatic Lung Search
//
var topLung =
{ "groupOp": "AND",
    "groups": [
    {"groupOp": "OR",
      "rules": [
        { "field": "gene", "op": "eq", "data": "BRAF" },
        { "field": "gene", "op": "eq", "data": "KRAS" },
        { "field": "gene", "op": "eq", "data": "MET" },
        { "field": "gene", "op": "eq", "data": "EGFR" }
      ]
    }],
      "rules": [
        { "field": "filterFlag", "op": "nc", "data": "blk" },
        { "field": "filterFlag", "op": "nc", "data": "pnl" },
        { "field": "filterFlag", "op": "nc", "data": "gaf" },
        { "field": "filterFlag", "op": "nc", "data": "con" },
        { "field": "filterFlag", "op": "nc", "data": "sin" },
        { "field": "varFreq", "op": "ge", "data": "3"},
        { "field": "varcaller", "op": "ne", "data": "Canary" }
      ]
};

//  Top Somatic GIST Search
//
var topGist =
{ "groupOp": "AND",
    "groups": [
    {"groupOp": "OR",
      "rules": [
        { "field": "gene", "op": "eq", "data": "PDGFRA" },
        { "field": "gene", "op": "eq", "data": "KIT" }
      ]
    }],
      "rules": [
        { "field": "filterFlag", "op": "nc", "data": "blk" },
        { "field": "filterFlag", "op": "nc", "data": "pnl" },
        { "field": "filterFlag", "op": "nc", "data": "gaf" },
        { "field": "filterFlag", "op": "nc", "data": "con" },
        { "field": "filterFlag", "op": "nc", "data": "sin" },
        { "field": "varFreq", "op": "ge", "data": "3"},
        { "field": "varcaller", "op": "ne", "data": "Canary" }
      ]
};

//  Top Germline Variants Search
//
var topGerm =
{ "groupOp": "AND",
      "rules": [
        { "field": "filterFlag", "op": "nc", "data": "blk" },
        { "field": "filterFlag", "op": "nc", "data": "pnl" },
        { "field": "filterFlag", "op": "nc", "data": "gaf" },
        { "field": "filterFlag", "op": "nc", "data": "con" },
        { "field": "varFreq", "op": "ge", "data": "15"},
        { "field": "varcaller", "op": "ne", "data": "Canary" }
      ]
};

//  Reportable variants
//
var reportableVars =
{ "groupOp": "AND",
      "rules": [
        { "field": "reportable", "op": "eq", "data": "1"}
      ]
} ;

//  Rahman Gene List Filter
//
var rahmanGenes =
    {"groupOp": "AND",
      "rules": [
        { "field": "gene", "op": "in", "data": "ABCB11,ALK,APC,ATM,AXIN2,BAP1,BLM,BMPR1A,BRCA1,BRCA2,BRIP1,BUB1B,CBL,CDC73,CDH1,CDK4,CDKN1B,CDKN2A,CEBPA,CHEK2,COL7A1,CYLD,DDB2,DICER1,DIS3L2,DKC1,DOCK8,EGFR,ELANE,ERCC2,ERCC3,ERCC4,ERCC5,EXT1,EXT2,FAH,FANCA,FANCC,FANCG,FH,FLCN,GATA2,GBA,GJB2,GPC3,HFE,HMBS,HRAS,ITK,KIT,MAX,MEN1,MET,MLH1,MSH2,MSH6,MTAP,MUTYH,NBN,NF1,NF2,PALB2,PDGFRA,PHOX2B,PMS2,POLD1,POLE,POLH,PRKAR1A,PRSS1,PTCH1,PTEN,PTPN11,RAD51C,RAD51D,RB1,RECQL4,RET,RHBDF2,RMRP,RUNX1,SBDS,SDHA,SDHAF2,SDHB,SDHC,SDHD,SERPINA1,SH2D1A,SLC25A13,SMAD4,SMARCA4,SMARCB1,SMARCE1,SOS1,SRY,STAT3,STK11,SUFU,TERT,TGFBR1,TMEM127,TNFRSF6,TP53,TRIM37 ,TSC1,TSC2,UROD,VHL,WAS,WRN,WT1,XPA,XPC"
        }
    ]
};

//  TARGET Gene List Filter
//
var targetGenes =
    {"groupOp": "AND",
      "rules": [
        { "field": "gene", "op": "in", "data": "ABL1,AKT1,AKT2,AKT3,ALK,APC,AR,ARAF,ASXL1,ATM,ATR,AURKA,BAP1,BCL2,BRAF,BRCA1,BRCA2,BRD2,BRD3,BRD4,NUTM1,CCND1,CCND2,CCND3,CCNE1,CDH1,CDK12,CDK4,CDK6,CDKN1A,CDKN1B,CDKN2A,CDKN2B,CEBPA,CREBBP,CRKL,CTNNB1,DDR2,DNMT3A,EGFR,EPHA3,ERBB2,ERBB3,ERBB4,ERCC2,ERG,ERRFI1,ESR1,ETV1,ETV4,ETV5,ETV6,EWSR1,EZH2,FBXW7,FGFR1,FGFR2,FGFR3,FLCN,FLT3,GNA11,GNAQ,GNAS,HRAS,IDH1,IDH2,IGF1R,JAK2,JAK3,KDR,KIT,KRAS,MAP2K1,MAP2K2,MAP2K4,MAP3K1,MAPK1,MAPK3,MCL1,MDM2,MDM4,MED12,MEN1,MET,MITF,MLH1,KMT2A,MPL,MSH2,MSH6,MTOR,MYC,MYD88,NF1,NF2,NFKBIA,NKX2-1,NOTCH1,NOTCH2,NPM1,NRAS,NTRK3,PDGFRA,PDGFRB,PIK3CA,PIK3CB,PIK3R1,PTCH1,PTEN,RAB35,RAF1,RARA,RB1,RET,RHEB,RNF43,ROS1,RSPO2,RUNX1,SMAD2,SMAD4,SMARCA4,SMARCB1,SMO,STK11,SYK,TET2,TMPRSS2,TP53,TSC1,TSC2,VHL,WT1,XPO1,ZNRF3,PALB2,CSF1R,HNF1A,PTPN11,SRC"
        }
    ]
};

//  Germline genes list for CCP filter
//
var germlineCCP = {
	"groupOp": "AND",
	"rules": [{
		"field": "consequence",
		"op": "ne",
		"data": "intron_variant"
	}, {
		"field": "consequence",
		"op": "ne",
		"data": "upstream_gene_variant"
	}, {
		"field": "consequence",
		"op": "ne",
		"data": "downstream_gene_variant"
	}, {
		"field": "consequence",
		"op": "ne",
		"data": "synonymous_variant"
	}, {
		"field": "consequence",
		"op": "ne",
		"data": "5_prime_UTR_variant"
	}, {
		"field": "consequence",
		"op": "ne",
		"data": "3_prime_UTR_variant"
	}, {
		"field": "varPanelPct",
		"op": "le",
		"data": "15"
	}, {
		"field": "filterFlag",
		"op": "nc",
		"data": "oor"
	}, {
		"field": "gene",
		"op": "in",
		"data": "ALK,APC,ASXL1,ATM,BAP1,BCORL1,BLM,BRCA1,BRCA2,BRIP1,CBL,CDC73,CDH1,CDK4,CDKN1B,CDKN2A,CEBPA,CHEK2,CYLD,DICER1,DNMT3A,EGFR,ERCC2,FANCA,FANCC,FANCG,FH,FLCN,FUBP1,GATA2,GNAS,HRAS,IDH1,JAK2,KIT,MEN1,MET,MLH1,MRE11A,MSH2,MSH6,MUTYH,NF1,NF2,PALB2,PDGFRA,PMS2,POLD1,POLE,PRKAR1A,PTCH1,PTEN,PTPN11,RB1,RET,RUNX1,SDHA,SDHB,SDHC,SDHD,SF3B1,SMAD4,SMARCA4,SMARCB1,STAT3,STK11,SUFU,TERT,TET2,TP53,TSC1,TSC2,U2AF1,VHL,WT1"
	}]
};

// ALLOCATE genes list for CCP filter

var allocateCCP = {
	"groupOp": "AND",
	"rules": [{
		"field": "consequence",
		"op": "ne",
		"data": "intron_variant"
	}, {
		"field": "consequence",
		"op": "ne",
		"data": "upstream_gene_variant"
	}, {
		"field": "consequence",
		"op": "ne",
		"data": "downstream_gene_variant"
	}, {
		"field": "consequence",
		"op": "ne",
		"data": "synonymous_variant"
	}, {
		"field": "consequence",
		"op": "ne",
		"data": "5_prime_UTR_variant"
	}, {
		"field": "consequence",
		"op": "ne",
		"data": "3_prime_UTR_variant"
	}, {
		"field": "varPanelPct",
		"op": "le",
		"data": "15"
	}, {
		"field": "filterFlag",
		"op": "nc",
		"data": "oor"
	}, {
		"field": "gene",
		"op": "in",
		"data": "ARID1A,ATM,ATR,BARD1,BRAF,BRCA1,BRCA2,BRIP1,CHEK2,CTNNB1,EGFR,ERBB2,FANCA,FANCC,FANCD2,FANCE,FANCF,FGFR2,FOXL2,KIT,KRAS,MLH1,MRE11A,MSH2,MSH6,NBN,NF1,PALB2,PIK3CA,PMS2,PPP2R1A,PTEN,RAD50,RAD51,RAD51C,RAD51D,RB1,SMARCA4,TP53"
	}]
};

//  Top traceback Variants Search
//
var topTraceback =
{ "groupOp": "AND",
      "rules": [
        { "field": "filterFlag", "op": "nc", "data": "con" },
        { "field": "filterFlag", "op": "nc", "data": "pnl" },
        { "field": "exac", "op": "lt", "data": "5"}
      ]
};

//  IBMD (Inherited Bone Marrow Disorders)
//
var topIBMD =
{ "groupOp": "AND",
      "rules": [
        { "field": "consequence","op": "ne",	"data": "intron_variant"},
        { "field": "filterFlag", "op": "nc", "data": "blk" },
        { "field": "filterFlag", "op": "nc", "data": "pnl" },
        { "field": "filterFlag", "op": "nc", "data": "gaf" },
        { "field": "gene","op": "in","data": "ACD,ANKRD26,CEBPA,CSF3R,CTC1,DDX41,DKC1,ELANE,ERCC6L2,ETV6,FANCA,FANCC,FANCG,FANCM,G6PC3,GATA1,GATA2,HAX1,JAGN1,MPL,NHP2,PARN,RBM8A,RPL11,RPL15,RPL26,RPL35A,RPL5,RPS10,RPS19,RPS24,RPS26,RPS29,RPS7,RTEL1,RUNX1,SBDS,SRP72,TCIRG1,TERT,TINF2,VPS45,WAS"}
      ]
};

//  PHT (PanHaem Tumour)
//
var topPHT =
{ "groupOp": "AND",
      "rules": [
        { "field": "filterFlag", "op": "nc", "data": "blk" },
        { "field": "filterFlag", "op": "nc", "data": "pnl" },
        { "field": "filterFlag", "op": "nc", "data": "gaf" },
        { "field": "filterFlag", "op": "nc", "data": "con" },
        { "field": "gene","op": "in","data": "ARAF,ASXL1,ATM,ATR,BCL2,BCOR,BCORL1,BIRC3,BRAF,BTK,CALR,CARD11,CBL,CCND1,CD79B,CRBN,CREBBP,CSF3R,CSMD1,CXCR4,DDX3X,DIS3,DNMT3A,EP300,ERBB2,EZH2,FAM46C,FAT1,FBXW7,FGFR3,FLT3,FOXO1,FYN,GATA2,ID3,IDH1,IDH2,IKZF1,JAK1,JAK2,JAK3,KIT,KRAS,MAP2K1,MPL,MYD88,NFKBIE,NOTCH1,NOTCH2,NPM1,NRAS,PHF6,PIGA,PLCG1,PLCG2,PRDM1,PTPN11,RHOA,RRAGC,RUNX1,SETBP1,SF3B1,SOCS1,SRSF2,STAT3,STAT5B,STAT6,TCF3,TET2,TNFAIP3,TP53,TRAF2,U2AF1,WT1,XPO1,ZRSR2"}
      ]
};
///////////////////////////////////




    //$('#curation_table').jqGridAfterGridComplete = loadSavedUserGrid();

    // jQuery("#curation_table").jqGrid.jqGridAfterGridComplete = loadSavedUserGrid();


    $( document ).ready(function() {


            //
            //we need all ids of unfiltered jqgrid rows to pass to the controller for curation checks
            //this is hard due to pagination - only elements on current page are loaded
            //we do this as described in http://stackoverflow.com/questions/9775115/get-all-rows-not-filtered-from-jqgrid
            //override jqgid.from.select
            var oldFrom = $.jgrid.from,
                    lastSelected;

            $.jgrid.from = function (source, initalQuery) {
                var result = oldFrom.call(this, source, initalQuery),
                        old_select = result.select;
                result.select = function (f) {
                    lastSelected = old_select.call(this, f);
                    return lastSelected;
                };
                return result;
            };


    <sec:ifAnyGranted roles="ROLE_ADMIN, ROLE_DEV, ROLE_CURATOR, ROLE_LAB">
        original_context = $("#clinContext").val();
        $('#get-a-curator')
            .css('display', 'none');
        $('#updateClinContext')
            .prop('disabled',false);

        d3.select("#clinContext")
            .append("option")
            .attr("value", "NewCC")
            .html("Create New Clinical Context");

        $('#updateClinContextForm').submit(function(e){
                var self = this,
                    current_context = $("#clinContext").val();
                e.preventDefault();
                if(current_context == "NewCC") {
                    window.location = "<g:context/>/ClinContext/create";
                } else if(original_context == current_context) {
                    alert("You haven't changed the Clinical Context");
                } else {

    %{--/*
    * DKGM 24-November-2016
    * Adding some logic here to check if the reportable column has been ticked.
    * Easygrid is really weird, and there are 4 possible values for that checkbox:
    * "true", "false", "Yes", "on"
    * "true" means true
    * "false" means false
    * "Yes" means true, but the user has clicked that row in this session
    * "on" means false, but the user has clicked that row in this session
    *
    * If any of the checkboxes are true:
    * we should give the user a warning before allowing them to change contexts
    */--}%

        var giveWarning = false;
        $('td[aria-describedby="curation_table_reportable"] input').each(function(i, d){
            var value = $(d).val();
            console.log(value);
            if(value == "Yes" || value == "true") {
                giveWarning = true;
            }
        });
        if(giveWarning) {
            if(confirm("Some 'Sequenced Variants' from this 'Sequenced Sample' have already been marked as reportable in this 'Clinical Context', are you sure you want to change context? The new context might not have 'Curated Variant' entries yet, please remember to create them if needed for reports.")) {
                this.submit();
            }
        } else {
            this.submit();
        }
    }
});
    </sec:ifAnyGranted>


    $("#finalReviewForm").submit(function() {
      saveReviewCurateFilterPrefs()
       //alert(  $('#curation_table').jqGrid('getDataIDs'))
    });


   function disableGridButtons() {
             //disable buttons on grid
               $('#showhidecols').addClass('ui-state-disabled')
               $('#resetcols').addClass('ui-state-disabled')
               $('#saveuserprefs').addClass('ui-state-disabled')
               $('#search_curation_table').addClass('ui-state-disabled')
               $('#refresh_curation_table').addClass('ui-state-disabled')
               $('#view_curation_table').addClass('ui-state-disabled')
               jQuery("#curation_table").jqGrid('hideCol','act') //hide edit col?
   }


   function loadSavedUserGrid() {

       var colname = '';
       var curationGrid = $('#curation_table');

       var gridColOrder=[${prefsColumnRemap}]

        if(gridColOrder) {
            curationGrid.jqGrid('remapColumns',gridColOrder,true);
            curationGrid.trigger('reloadGrid');
        }
    <g:each var="colname" in="${prefsShowCols}">
    <%-- $('#curation_table_'+"${colname}").showCol()--%>
        curationGrid.jqGrid('showCol',"${colname}")
    </g:each>

    <g:each var="colname" in="${prefsHideCols}">
    <%--$('#curation_table_'+"${colname}").hideCol()--%>
        curationGrid.jqGrid('hideCol',"${colname}")
    </g:each>

    }

    //end function defintions
    //



        loadSavedUserGrid(); //this loads saved hidden cols and colorder


        $("#filterShowMessage").click(function(){
            $("#filterDescription").slideToggle("fast");
            if ($("#filterShowMessage").text() == 'Show filter details') {
                $("#filterShowMessage").text('Hide filter details')
            } else {
                $("#filterShowMessage").text('Show filter details')
            }

        });

        $("#cnvfilterShowMessage").click(function(){
            $("#cnvfilterDescription").slideToggle("fast");
            if ($("#cnvfilterShowMessage").text() == 'Show filter details') {
                $("#cnvfilterShowMessage").text('Hide filter details')
            } else {
                $("#cnvfilterShowMessage").text('Show filter details')
            }

        });


         $('#curation_table').jqGrid('setGridParam', {
                gridComplete: function(data){
                    console.log("Grid complete");
                    bindContextMenu();

                    postData =  $('#curation_table').jqGrid('getGridParam', 'postData');

                    if(postData.filters && postData.filters != '{"groupOp":"AND","rules":[]}') { //if a real filter is appied
                        $('#filterNotification').show();
                        //need to translate the filter...

    ${remoteFunction(controller: 'seqVariant', action: 'filterToReadable', params: '{filters: postData.filters}', update: "filterDescription")};
                        //alert(filter)
                        $('#postfilter_count').text($('#curation_table').jqGrid('getGridParam', 'records')); //54 or 32 (current total not incl pagination

                    } else {
                        $('#filterNotification').hide()
                    }
                }
         });


        //Here we set some element properties:
       //WORKAROUND: despite setting sortable: false, two of our columns are still sortable on click and give an powerassertionerror when clicked. make them unsortable.
       //todo do this programmattically
        $("#curation_table_report").unbind("click");
        $("#curation_table_curate").unbind("click");
        $("#curation_table_curated").unbind("click");
        $("#curation_table_igv").unbind("click");
        $("#curation_table_googlelink").unbind("click");
        $("#curation_table_alamut").unbind("click");
        $("#curation_table_panel").unbind("click");
        $("#curation_table_act").unbind("click");
        $("#curation_table_zygosity").unbind("click");


        //change tooltips since we dont want to hack easygrid and do it directly:
        $('#refresh_curation_table').prop('title', 'Reset Filters');
        $('#search_curation_table').prop('title', 'Apply Filters');


        //lock grid if needed
        if (${isFirstReviewed}) { //FFFFF5
            $('.ui-widget-content').css('background','none')
            $('.ui-widget-content').css('background-color','#EBEBEB') //EBEBEB
            $('.ui-widget-header').css('background','none')
            $('.ui-widget-header').css('background-color','#EBEBEB')
            $('.ui-state-default').css('background','none')
            $('.ui-state-default').css('background-color','#EBEBEB')
            $('.ui-tabs-anchor').css('color', 'black');
        }

        //lock grid if needed
        if (${isFinalReviewed}) {
            $('.ui-widget-content').css('background','none')
            $('.ui-widget-content').css('background-color','white')
            $('.ui-widget-header').css('background','none')
            $('.ui-widget-header').css('background-color','white')
            $('.ui-state-default').css('background','none')
            $('.ui-state-default').css('background-color','white')
            $('.ui-tabs-anchor').css('color', 'black');
        }


        /* Uncomment if you want to distinguish between userfilter and reviewedsamplefilter in the user interface */
        if (  ${false} ) {   //was if finalReviewed
            $('#appliedfiltertype').text("reviewed sample ")
        } else {
            $('#appliedfiltertype').text("user-created ")
        }
});


    var allTags = <g:allTags/>;
    var lowercaseAllTags = Object.keys( allTags ).map( (tag) => tag.toLowerCase() );

    var tagModule = PathOS.tags.buildModule({
        object: 'seqvariant',
        tags: [],
        availableTags: Object.keys(allTags)
    });



// Author: David Ma
// Adding IGV.js
// 4th of May 2016

var seqrun = "${seqSample?.seqrun}"
var sample = "${seqSample?.sampleName}"
var panel = "${ seqSample?.panel }"

// This is the default dataUrl. Replace it with something better using ajax.
var dataUrl = "${ UrlLink.dataUrl (
        seqSample?.seqrun.toString(),
        seqSample?.sampleName,
        ''
)
}";



var igvDiv = document.getElementById("igvDiv");

function addToIGV(sample, dataUrl, id){
    PathOS.igv.addBAM(sample, dataUrl);
    $("#relationLink-"+id).remove();
}

    $("#footer-message h1").text("This footer is used to view variants, click on one to get started.");


    var current_id = false,
        locus = "";

    var downsample = null;
    $("#curation_table").on('click', function(){
        setTimeout(function(){
            if ($("#curation_table .ui-row-ltr.ui-state-highlight").length > 0 &&
            current_id != $("#curation_table .ui-row-ltr.ui-state-highlight").attr('id')) {
                current_id = $(".ui-row-ltr.ui-state-highlight").attr('id');
                PathOS.tags.update_object(current_id);
                $("#footer-message").remove();

                var readDepth = $("#curation_table .ui-row-ltr.ui-state-highlight [aria-describedby='curation_table_readDepth']").html().replace(/,/g,"").trim();

                var locus = $("#curation_table .ui-row-ltr.ui-state-highlight [aria-describedby='curation_table_variant'] a").html().trim();
                var chr = locus.match(/chr\d+:g\./) ? locus.match(/chr(\d+:)g\./)[1] : "";
                var regex = /(\d+)/;
                var pos = parseInt(regex.exec(locus.split("g.")[1]));
                // var start = d3.format(",")(pos - 50);
                // var stop = d3.format(",")(pos + 50);
                var start = pos - 30;
                var stop = pos + 30;
                var location = chr+start+"-"+stop;
                console.log("Location is:", location);
                // var location = $(".ui-row-ltr.ui-state-highlight [aria-describedby='curation_table_gene'] a").html().trim();

                var igvAutoLoad = "ask";
                if(PathOS.modules.settings[PathOS.user] && PathOS.modules.settings[PathOS.user].svlistIGV) {
                    igvAutoLoad = PathOS.modules.settings[PathOS.user].svlistIGV;
                }


                if (panel.indexOf("MRD") === 0) {
                    var message = d3.select("#pathos-footer").insert("div", "#igvDiv").attr("id", "footer-message");
                    message.append("h1").text("Warning, this is an MRD sample, you should open this using Desktop IGV because in-browser IGV will probably crash your browser.");

                    message.append("a").text("View using Desktop IGV").attr("href", "<g:context/>/seqVariant/igvAction?id="+current_id);

                    message.append("span").text(" - ");

                    message.append("a").text("Downsample and open with in-browser IGV").attr("href", "#").on("click", function(){
                        $("#footer-message").remove();
                        downsample = true;
                        PathOS.igv.init(igvDiv, dataUrl, sample, panel, 2500, location);
                    });
                } else if ( downsample === false || igvAutoLoad == "auto" ) {
                    PathOS.igv.init(igvDiv, dataUrl, sample, panel, 50000, location);
                } else if ( downsample === true || igvAutoLoad == "downsample" ) {
                    PathOS.igv.init(igvDiv, dataUrl, sample, panel, 2500, location);
                } else {
                    var message = d3.select("#pathos-footer").insert("div", "#igvDiv").attr("id", "footer-message");

                    message.append("h1").attr("id", "main-button").append("a").text("Launch In-browser IGV").attr("href", "#").on("click", function(){
                        $("#footer-message").remove();
                        downsample = false;
                        PathOS.igv.init(igvDiv, dataUrl, sample, panel, 50000, location);
                    });

                    message.append('h1').attr("id", "igv-message").text("If your browser runs slowly, you might want to try downsampling or using Desktop IGV")

                    message.append("a").text("Launch In-browser IGV with downsampling").attr("href", "#").on("click", function(){
                        $("#footer-message").remove();
                        downsample = true;
                        PathOS.igv.init(igvDiv, dataUrl, sample, panel, 2500, location);
                    });

                    message.append("a").text("View using Desktop IGV").attr("href", "<g:context/>/seqVariant/igvAction?id="+current_id);
                }
            }
        }, 200);
    });


/**
*   This function keeps the easygrid header on the page, even when you scroll down.
*   -DKGM, 23-Feb-2017
*/
    $(document).ready(function(){
        var offset = $("#gview_curation_table").offset().top,
            padding = $("#gview_curation_table div.ui-jqgrid-hdiv").height(),
            reasonableDistanceFromSide = 430,
            width = $(window).width() - reasonableDistanceFromSide,
            magicExtraSpaceFix = 44,
            gridHeader = d3.select("#gview_curation_table div.ui-jqgrid-hdiv")

        d3.select("#curationPager")
            .style('width', width+'px')
            .style('position', 'fixed')
            .style('bottom', 0);

        $(window).resize(refreshVariables);

		$('body').on("keydown.resize", function(e){
			if(e && e.keyCode && e.keyCode == 192 && !$(document.activeElement).is("input") && !$(document.activeElement).is("textarea") && !e.altKey && !e.metaKey && !e.ctrlKey){
                refreshVariables();
			}
		});

        $(window).scroll(adjustGridHeader);

        function adjustGridHeader(){
            var windowTop = $(window).scrollTop(),
                navHeight = $(".navbar-header").height();

            var floatTop = windowTop - offset + navHeight;

            if (windowTop + navHeight > offset + 17 - magicExtraSpaceFix) {
                gridHeader.style('position', 'absolute')
                    .style('z-index', 100)
                    .style('top', floatTop + 'px');

                // Bump the body a little?
                d3.select(".ui-jqgrid-bdiv")
                    .style("padding-top", padding + "px");

                // magic padding for no reason
                d3.select(".ui-jqgrid-hbox").style("padding-top", magicExtraSpaceFix + "px");

            } else {
                gridHeader
                    .style('position', '')
                    .style('top', 0 );

                d3.select(".ui-jqgrid-bdiv")
                    .style("padding-top", 0);

                // magic padding for no reason
                d3.select(".ui-jqgrid-hbox").style("padding-top", 0);
            }
        }

        function refreshVariables(){
            offset = $("#gview_curation_table").offset().top;
            magicExtraSpaceFix = 0;
            adjustGridHeader();

            width = $(window).width() - reasonableDistanceFromSide;
            d3.select("#curationPager").style('width', width+'px');
        }

    <g:if test="${!seqSample.firstReviewBy && !skipGeneMask}">
        <r:style>
            #tabs {
                display: none;
            }
            table#geneMaskTable tr td{
                line-height: 1em;
                vertical-align: top;
            }
        </r:style>

        var splash = d3.select("#page-content-wrapper").append("div").attrs({
            id: "splashScreen"
        });

        var seqSampleGenes = ${(seqSample.geneMask() ?: []) as JSON};

        var seqSampleGeneMask = "${(seqSample.sampleGeneMask == null ? 'Unknown gene mask applied' : seqSample.sampleGeneMask ?: 'No gene mask applied')}";

        var labAssays = ${labAssays as JSON};
        console.log(labAssays);


        var table = splash.append("table").attr("id", "geneMaskTable");
        var header = table.append("tr");
        header.append("th").text("Information");
        header.append("th").text("Genes");


        labAssays.forEach(function(assay){
            if(assay && assay.testName && assay.testSet && assay.genes) {
                var row = table.append("tr");
                row.append("td").html("Test set: "+assay.testName+"<br>Billing code: "+assay.testSet);

                var genes = assay.genes ? assay.genes.split(",").sort().join(", ") : "No gene mask, show everything";
                row.append("td").text(genes);
            }
        });

        var last = table.append("tr");
        last.append("td").text("Mask in use");
        last.append("td").text(seqSampleGeneMask.split(",").sort().join(", "));

        var detected = table.append("tr");
        detected.append("td").text("Genes detected");
        detected.append("td").attr("id", "genesDetected").text("Checking variants for genes...");

        $.ajax({
            url: "<g:context/>/SeqSample/genelist?id=${seqSample.id}",
            complete: function(d){
                if(d.status == 200 && d.responseJSON) {
                    var listOfGenes = d.responseJSON;

                    var genelist = d3.select("#genesDetected");
                    if(genelist) {
                        genelist.text(listOfGenes.join(", "));
                    }

                    if(seqSampleGenes.length > 0) {
                        listOfGenes.forEach(function(gene){
                            if(seqSampleGenes.indexOf(gene) < 0){
                                setTimeout(function(){
                                    PathOS.notes.addError(gene+" is detect in this sample, but should have been excluded by the Gene Mask");
                                }, 1000);
                            }
                        });
                    }
                }
            }
        });

        splash.append("input").attrs({
            type: "button",
            value: "Show Variants"
        }).on('click', function(){
            d3.select("#tabs").style("display","inherit");
            splash.remove();
            setTimeout(function(){
                refreshVariables();
            }, 1000);
        });

        <sec:ifAnyGranted roles="ROLE_UNMASKER,ROLE_DEV">

        splash.append("input").attrs({
            type: "button",
            value: "Change Gene Mask"
        }).on('click', function(){
            window.location.href = '<g:context/>/seqSample/editGeneMask/${seqSample.id}';
        });

        </sec:ifAnyGranted>

    </g:if>

    });



PathOS.hotkeys.add(190, toggleContextMenu);
PathOS.hotkeys.add(27, hideContextMenu);
$("body").on('click', hideContextMenu);
$("#svlistContextMenu").on('click', function(e){
    e.stopPropagation();
});

function hideContextMenu() {
    $("#svlistContextMenu").addClass("hidden");
}

function toggleContextMenu() {
    var id = PathOS.tags.current_object;
    if(id) {
        $("#svlistContextMenu").toggleClass("hidden");
        var top = $("#"+PathOS.tags.current_object).offset().top;
        var left = parseInt($(window).scrollLeft()) + 50;
        dressUpContextMenu(id, top, left);
    }
}

function bindContextMenu() {
    $("#curation_table tr")
        .off("contextmenu")
        .on( "contextmenu", function(e){
            e.preventDefault();

            d3.select("#svlistContextMenu")
                .classed("hidden", false);

            var id = $(this).attr('id');
            PathOS.tags.update_object(id);
            dressUpContextMenu(id, e.pageY, e.pageX);
        });
}

function dressUpContextMenu(id, top, left) {
    var menu = d3.select("#svlistContextMenu");
    repositionContextMenu();

    menu.select("#contextMenuName").classed("hidden", true);

    menu.select("#cmIGV").attrs({
        onclick: "window.open('"+PathOS.application+"/seqVariant/igvAction?id="+id+"','_blank')"
    });

    menu.select("#cmGoogle").attrs({
        onclick: "window.open('"+PathOS.application+"/seqVariant/googleSearchAction?id="+id+"','_blank')"
    });

    menu.select("#cmAlamut").attrs({
        onclick: "window.open('"+PathOS.application+"/seqVariant/alamutAction?id="+id+"','_blank')"
    });

    menu.select("#cmCuration")
        .attr("onclick", "markForCuration()")
        .select("#cmCurationLink").text("Mark for Curation");


    menu.select("#cmReportable")
        .attr("onclick", "markAsReportable()")
        .select("#cmReportableLink").html("")
        .append("a").text("Mark as Reportable");

    menu.selectAll(".annotation").remove();

// Basically copied from annoFormatter()
    $.ajax({
        url: PathOS.application+'/seqVariant/fetchAnnotations?id='+id,
        success: function(data){
            console.log(data);
            if(data.gene && data.hgvsc) {
                var hgvsc = data.hgvsc.split(":")[1] || data.hgvsc;
                menu.select("#contextMenuGene").text(data.gene);
                menu.select("#contextMenuHGVSC").text(hgvsc);
                menu.select("#contextMenuName").classed("hidden", false);
            }
            if(data.curatedVariant) {
                var authorised = data.curatedVariant.authorisedFlag ? "authorised" : "";
                var strength = data.curatedVariant.pmClass.indexOf(':') > -1 ? data.curatedVariant.pmClass.split(":")[0] : data.curatedVariant.pmClass;

                menu.select("#cmCuration")
                    .attr("onclick", "window.open('"+PathOS.application+"/curVariant/show/"+data.curatedVariant.id+"','_blank')")

                menu.select("#cmCurationLink").html('')
                    .append('a').attrs({
                        // target: '_blank',
                        // href: PathOS.application+"/curVariant/show/"+data.curatedVariant.id,
                        class: "cvlabel cv-button cv-"+strength+" "+authorised
                    }).html(data.curatedVariant.pmClass.replace(/ /g, "&nbsp;"));
            }
            if(data.reportable) {
                menu.select("#cmReportable")
                    .attr("onclick", "window.open('"+PathOS.application+"/SeqSampleReport/link/${seqSample.id}','_blank')")
                    .select("#cmReportableLink").text("View Report");
            }

            if(data.cosmic) {
                if(data.cosmic.indexOf(",") >= 0) {
                    data.cosmic.split(",").forEach(function(d) {
                        var cosm = d.slice(4);
                        addMenuLink({
                            icon: PathOS.application+"/images/cosmic_icon.png",
                            title: "Cosmic",
                            link: "https://cancer.sanger.ac.uk/cosmic/mutation/overview?genome=37&id="+cosm
                        });
                    });
                } else if (data.cosmic.indexOf("COSM") >= 0) {
                    var cosm = data.cosmic.slice(4);
                    addMenuLink({
                        icon: PathOS.application+"/images/cosmic_icon.png",
                        title: "Cosmic",
                        link: "https://cancer.sanger.ac.uk/cosmic/mutation/overview?genome=37&id="+cosm
                    });
                } else {
                    addMenuLink({
                        icon: PathOS.application+"/images/cosmic_icon.png",
                        title: "Cosmic",
                        link: PathOS.application+"/seqVariant/cosmicAction?id="+id
                    });
                }
            }

            if(data.civic.length > 0) {
                data.civic.forEach(function(civic){
                    addMenuLink({
                        icon: PathOS.application+"/images/civic_icon.png",
                        title: "CIViC",
                        link: PathOS.application+"/civicVariant/show?id="+civic.civicId
                    });
                });
            }

            if(data.drug) {
                addMenuLink({
                    icon: PathOS.application+"/images/molecularmatch_icon.png",
                    title: "Molecular Match Drug",
                    link: PathOS.application+"/drug/list?target_gene="+data.drug
                });
            }
            if(data.trial) {
                addMenuLink({
                    icon: PathOS.application+"/images/molecularmatch_icon.png",
                    title: "Molecular Match Trial",
                    link: PathOS.application+"/trial/list?target_gene="+data.trial
                });
            }

            if(data.dbsnp) {
                if(data.dbsnp.indexOf("rs") > 0) {
                    var dbsnp = data.dbsnp.slice(2);
                    addMenuLink({
                        icon: PathOS.application+"/images/dbSNP_logo.png",
                        title: "dbSNP",
                        link: "https://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs="+dbsnp
                    });
                } else {
                    addMenuLink({
                        icon: PathOS.application+"/images/dbSNP_logo.png",
                        title: "dbSNP",
                        link: PathOS.application+"/seqVariant/dbsnpAction?id="+id
                    });
                }
            }

            repositionContextMenu();
        }
    });

    function repositionContextMenu() {
        if(top + $("#svlistContextMenu").height() > $(window).height() + $(window).scrollTop()) {
            var difference = 20 + (top + $("#svlistContextMenu").height()) - ($(window).height() + $(window).scrollTop());
            top = top - difference;
        }
        menu.styles({
            top: top+"px",
            left: left+"px"
        });
    }

    function addMenuLink(d) {
        // todo: verify that d has icon, link & title.

        var tr = menu.select("table")
            .append("tr")
            .attr("onclick", "window.open('"+d.link+"','_blank')")
            .classed("annotation", true);

        tr.append('td').append("img").attrs({
            src: d.icon,
            class: "contextMenuLogo"
        });
        tr.append('td').append('a').text(d.title);
    }
};

    PathOS.hotkeys.add(74, down);
    PathOS.hotkeys.add(40, down);
    PathOS.hotkeys.add(75, up);
    PathOS.hotkeys.add(38, up);
    function down(){
        if(navOpen()) {
            $("#nData").click();
        } else {
            var pos = getCurrentSelection() + 1;
                if (pos < $("#curation_table .jqgrow").length) {
                $("#curation_table .jqgrow")[pos].click();
            }
        }
    }
    function up(){
        if(navOpen()) {
            $("#pData").click();
        } else {
            var pos = getCurrentSelection() - 1;
            if(pos >= 0) {
                $("#curation_table .jqgrow")[pos].click();
            }
        }
    }
    function getCurrentSelection(){
        var pos = -1;
            $("#curation_table .jqgrow").each( function(d){
                if($($("#curation_table .jqgrow")[d]).attr("tabindex") == 0) {
                    pos = d;
                }
            });
        return pos;
    }
    function navOpen() {
       var result = true;
       if($("#viewmodcuration_table").length == 0) {
           result = false;
       }
       if($("#viewmodcuration_table").attr("aria-hidden") != "false") {
           result = false;
       }
       return result;
    }




</r:script>















