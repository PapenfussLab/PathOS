<%@ page import="org.petermac.pathos.pipeline.UrlLink; org.petermac.pathos.curate.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'seqrun.label', default: 'Seqrun')}"/>
    <title>${seqrunInstance?.seqrun} - Show Seqrun</title>
    <g:javascript src="jsapi.js" />
    <tooltip:resources/>

    <parameter name="footer" value="on" />

    <link rel="stylesheet" href="${resource(dir: 'css', file: 'jheatmap-1.0.0.css')}" type="text/css">
    <g:javascript src="jheatmap-1.0.1.js" />

    <style>
    /* To center the heatmap */
    table.heatmap {
        width: auto;
        background-color: white;
        background-image: none;
    }
    #heatmap th {
        background-image: none;
        background-color: white;
        padding: 0;
    }
    #heatmap td {
        padding: 0;
    }
    </style>

</head>

<body>
<a href="#show-seqrun" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                             default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-seqrun" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list seqrun">

        <g:if test="${seqrunInstance?.seqrun}">
            <li class="fieldcontain">
                <span id="seqrun-label" class="property-label">
                    <g:message code="seqrun.seqrun.label" default="Seqrun"/>
                </span>
                <span class="property-value" aria-labelledby="seqrun-label">
                    <g:fieldValue bean="${seqrunInstance}" field="seqrun"/>
                </span>
            </li>
        </g:if>

        <g:if test="${seqrunInstance?.runDate}">
            <li class="fieldcontain">
                <span id="runDate-label" class="property-label">
                    <g:message code="seqrun.runDate.label" default="Run Date"/>
                </span>
                <span class="property-value" aria-labelledby="runDate-label">
                    <g:formatDate date="${seqrunInstance?.runDate}" format="dd-MMM-yyyy"/>
                </span>
            </li>
        </g:if>

        <g:if test="${seqrunInstance?.platform}">
            <li class="fieldcontain">
                <span id="platform-label" class="property-label">
                    <g:message code="seqrun.platform.label" default="Platform"/>
                </span>
                <span class="property-value" aria-labelledby="platform-label">
                    <g:fieldValue bean="${seqrunInstance}" field="platform"/>
                    (<g:fieldValue bean="${seqrunInstance}" field="scanner"/>)
                </span>
            </li>
        </g:if>


        <g:if test="${seqrunInstance?.readlen}">
            <li class="fieldcontain">
                <span id="readlen-label" class="property-label">Reads</span>
                <span class="property-value" aria-labelledby="readlen-label">
                    <g:fieldValue bean="${seqrunInstance}" field="readlen"/>
                    (<g:fieldValue bean="${seqrunInstance}" field="sepe"/>)
                </span>
            </li>
        </g:if>

        <li class="fieldcontain">
            <span id="pipelog-label" class="property-label"><g:message code="seqrun.pipelog.label" default="Pipeline Log"/></span>
            <span class="property-value" aria-labelledby="pipelog-label">
                <g:link url="${UrlLink.pipelineUrl( seqrunInstance.seqrun )}" target="_blank">bpipe log</g:link>
            </span>
        </li>

        <li class="fieldcontain">
            <span id="passfail-label" class="property-label"><g:message code="seqrun.passfail.label" default="Pass/Fail"/></span>
            <span class="property-value" aria-labelledby="passfail-label">
                <g:if test="${ ! seqrunInstance?.authorisedFlag}">
                    <g:form action="authoriseRun" id="${seqrunInstance?.id}">
                        <g:select name="passfail" from="${['Pass','Fail']}" value="${params.passfail}" noSelection="['': '-Select QC-']"/>
                        <g:textField name="qcComment" />
                        <g:submitButton name="authorise" value="Authorise"/>
                    </g:form>
                </g:if>
                <g:else>
                    <g:qcPassFail authorised="${seqrunInstance.authorisedFlag}" passfailFlag="${seqrunInstance.passfailFlag}" />
                </g:else>
            </span>
        </li>


        <g:if test="${seqrunInstance?.qcComment}">
            <li class="fieldcontain">
                <span id="comment-label" class="property-label">
                    <g:message code="seqSample.comment.label" default="QC Comments"/>
                </span>
                <span class="property-value" aria-labelledby="comment-label">
                    ${seqrunInstance.qcComment}
                </span>
            </li>
        </g:if>

        <g:if test="${seqrunInstance?.authorised}">
            <li class="fieldcontain">
                <span id="authorised-label" class="property-label"><g:message code="seqrun.authorised.label" default="Authorised"/></span>
                <span class="property-value" aria-labelledby="authorised-label">
                    <g:form action="authoriseRun" id="${seqrunInstance?.id}">
                        ${seqrunInstance.authorised}
                        <g:submitButton name="authorise" value="Revoke"/>
                    </g:form>
                </span>
            </li>
        </g:if>

        <g:if test="${seqrunInstance?.panelList}">
            <li class="fieldcontain">
                <span id="panelList-label" class="property-label">
                    <g:message code="seqrun.panelList.label" default="Panel List"/>
                </span>
                <span class="property-value" aria-labelledby="panelList-label">
                    <g:fieldValue bean="${seqrunInstance}" field="panelList"/>
                </span>
            </li>
        </g:if>

        <g:showPageTags/>

<h2>Quality Control Charts</h2>
<a id="toggleQC" href="#showQC" class="disabled" onclick="showQC()"><span id="toggleQClabel">Show Run QC</span> <span id="loadingCharts">(Loading Charts...)</span> <span id="loadingHeatmap">(Loading Heatmap...)</span></a>

<div id="qualityControlCharts">
    <div id="seqrunSummary">
        <h3>Sequenced Run Summary</h3>
        <g:seqrunQC seqrun="${seqrunInstance}"/>
    </div>

    <div id="googleCharts">
        <h3>Charts</h3>
        <img class="loading_logo" src="/PathOS/dist/img/pathos_logo_animated.svg">
    </div>

    <div id="heatmapContainer">
        <h3>Heatmap</h3>
        <img class="loading_logo" src="/PathOS/dist/img/pathos_logo_animated.svg">
        <div id="heatmap"></div>

        <br/><br/>
        <%-- HERE YOU PUT the Min Max and Middle values for the HEATMAP! the input boxes that is. --%>
        Heatmap Saturation (reads) Min: <input type="text" id="heatmapMin" name="heatmapMin" value="0"/>
        Max: <input type="text" id="heatmapMax" name="heatmapMax" value="1400"/>
        <input type="button" onclick="changeHeatmapValues()" value="Change"/>
        <div id="errorDiv" style="color:red"></div>
    </div>

    <div id="ntcReads">
        <h3>NTC Reads</h3>

        <g:if test="${StatsService.ntcAmplicons(seqrunInstance,20)}">
        <table border="1" style="width: 400pt">
            <thead>
            <tr>
                <th>Sample</th>
                <th>Amplicon</th>
                <th>Reads</th>
            </tr>
            </thead>
            <g:each in="${StatsService.ntcAmplicons(seqrunInstance,20)}" var="amp">
                <tr>
                    <td>${amp.sampleName}</td>
                    <td>${amp.amplicon}</td>
                    <td>${amp.readsout}</td>
                </tr>
            </g:each>
        </table>
        </g:if>
        <g:else>
            <g:if test="${StatsService.ntcAmplicons(seqrunInstance,0)}">
                <p>There are less than 20 NTC Reads for this Sequenced Run.</p>
            </g:if>
            <g:else>
                <p>This are no NTC Reads for this Sequenced Run.</p>
            </g:else>
        </g:else>
    </div>
</div>



<g:if test="${seqrunInstance?.seqSamples}">
    <span id="seqSamples-label" class="property-label">
        <h2>List of Samples</h2>
        <br/><br/>
    </span>
    <table>
        <thead>
        <tr>
            <th>Seq. Sample</th>
            <th>QC</th>
            <th>Raw Variants</th>
            <th>Curated</th>
            <th>Review</th>
            <th>Filter Passed</th>
            <th>Reportable</th>
            <th>IGV.js</th>
            <th>IGV</th>
            <th>Panel</th>
            <th>Analysis</th>
            <th>User</th>
        </tr>
        </thead>

        <g:each in="${seqrunInstance.seqSamples.sort{a,b -> a.sampleName <=> b.sampleName}}" var="s">
            <tr>
                <td>
                    <g:link controller="seqVariant" action="svlist" id="${s?.id}">
                        ${s?.encodeAsHTML()}
                    </g:link>
                </td>
                <td>
                    <g:link controller="seqSample"  action="showQC" id="${s?.id}">
                        <g:if test="${s.authorisedQcFlag}">
                            <g:qcPassFail authorised="${true}" passfailFlag="${s.passfailFlag}" />
                        </g:if>
                        <g:else>
                            Set QC
                        </g:else>
                    </g:link></td>
                <td>${s.seqVariants.size()}</td>
                <td>${s.seqVariants.findAll{ it.curated != null }.size()}</td>
                <td>
                    <g:if test="${s.finalReviewBy}"><noop style="color: black; background-color: white;padding-left: 10px; padding-right: 8px; text-decoration: none">Final</noop></g:if>

                    <g:elseif test="${s.firstReviewBy}"><span style="color: black; background-color: #EBEBEB;padding-left: 10px; padding-right: 12px; text-decoration: none">First</span></g:elseif>
                    <g:else>&nbsp;</g:else>

                </td>
                <td>${s.seqVariants.findAll{ it.filterFlag == 'pass' }.size()}</td>
                <td>${s.seqVariants.findAll{ it.reportable }.size()}</td>
                <td id="igv-open-${s.sampleName}">
                    <tooltip:tip code="Open this sample with the in-browser IGV">
                        <a href="#none" onclick='launchIGV({
                            seqrun: "${s.seqrun.seqrun}",
                            sample: "${s.sampleName}",
                            panel:  "${s.panel}",
                            dataUrl: "${ UrlLink.dataUrl (s.seqrun.seqrun, s.sampleName, '')}"
                        })'>View Sample</a>
                    </tooltip:tip>
                </td>
                <td>
                    <tooltip:tip code="seqrun.merge.tip">
                        <a href="http://localhost:60151/load?file=${org.petermac.pathos.pipeline.UrlLink.dataUrl(s.seqrun.seqrun,s.sampleName,s.sampleName+".vcf")},${org.petermac.pathos.pipeline.UrlLink.dataUrl(s.seqrun.seqrun,s.sampleName,s.sampleName+".bam")}&merge=true">Merge</a>
                    </tooltip:tip>
                </td>
                <td>${s.panel}</td>
                <td>${s.analysis}</td>
                <td>${s.userName}</td>
            </tr>
        </g:each>
    </table>
</g:if>
    </ol>
</div>
<script>


/* To draw tags we need:
 *
 * 1) A div in the html
 * 2) The tag data, to draw existing tags
 * 3) Activate the textarea so we can enter new tags
 */

<g:showPageTagsScript tags="${seqrunInstance?.tags as grails.converters.JSON}" id="${seqrunInstance?.id}" controller="seqrun"/>

// Chart stuff
// Refactored to run asynchronously by DKGM 18-July-2016
var data = false;
var heatmapTsvLoc = false;
$.ajax('/PathOS/Seqrun/getStats?id='+${seqrunInstance?.id}, {success:function(d){
    d3.select("#toggleQC").classed('disabled', data && heatmapTsvLoc);
    d3.select("#loadingCharts").remove();
    d3.select("#googleCharts .loading_logo").remove();
    data = d;
    console.log("Data loaded!");
    if($("#qualityControlCharts").hasClass("show")) {
        drawCharts();
    }
}});

$.ajax('/PathOS/Seqrun/getHeatmap?id='+${seqrunInstance?.id}, {success:function(d){
    d3.select("#toggleQC").classed('disabled', data && heatmapTsvLoc);
    d3.select("#loadingHeatmap").remove();
    d3.select("#heatmapContainer .loading_logo").remove();
    heatmapTsvLoc = d;
    console.log("Heatmap loaded!");
    if($("#qualityControlCharts").hasClass("show")) {
        drawHeatmap();
    }
}});


google.load('visualization', '1', {packages: ['corechart']});

var chartsHaveBeenDrawn = false;
var heatMapHasBeenDrawn = false;

if(window.location.hash === "#showQC") {
    console.log("We should show the QC!");
    showQC();
}

function showQC(event) {
    if(event) {
        event.preventDefault();
    }
    $("#qualityControlCharts").toggleClass("show");
    if($("#qualityControlCharts").hasClass("show")) {
        d3.select("#toggleQC").attr("href", "#");
        d3.select("#toggleQClabel").text("Hide Run QC");
    } else {
        d3.select("#toggleQC").attr("href", "#showQC");
        d3.select("#toggleQClabel").text("Show Run QC");
    }

    if(heatmapTsvLoc && !heatMapHasBeenDrawn){
        drawHeatmap();
    }
    if(data && !chartsHaveBeenDrawn) {
        drawCharts();
    }
}

function drawHeatmap() {
    console.log("Drawing heatmap.");
    heatMapHasBeenDrawn = true;
    $('#heatmap').heatmap({
        data: {
            values: new jheatmap.readers.TableHeatmapReader({ url: heatmapTsvLoc })
        },
        init: function(heatmap) {
            heatmap.size.width = 850;
            heatmap.size.height = 550;
            heatmap.cols.zoom = 3;
            heatmap.rows.zoom = 15;

            heatmap.cells.decorators["readsout"] = new jheatmap.decorators.Heat({
                maxColor: [49,130,189],
                nullColor: [178,178,178],
                minColor: [222,235,247],
                midColor: [158,202,225],
                minValue: 0,
                midValue: 700,
                maxValue: 1400
            });
        }
    });
}

function drawCharts(){
    console.log("Drawing charts.");
    chartsHaveBeenDrawn = true;
    var div = d3.select("#googleCharts");

    data.panels.forEach(function(panel, i){
        div.append("div").attr('id', 'readChart'+i).classed("GCdiv", true);
        div.append("div").attr('id', 'sampleChart'+i).classed("GCdiv", true);
        div.append("div").attr('id', 'ampliconChart'+i).classed("GCdiv", true);

        drawReadChart(JSON.parse(data.readChart[i]), i, panel);
        drawSampleChart(JSON.parse(data.sampleChart[i]), i);
        drawAmpliconChart(JSON.parse(data.ampliconChart[i]), i);
    });
}


function drawReadChart(data, i, panel) {
    var data = google.visualization.arrayToDataTable( data );
    new google.visualization.LineChart(document.getElementById("readChart"+i)).
    draw(data, {
        title:      "Historical Runs by Panel "+panel,
        width:      1000, height: 300,
        vAxis:      {title: "Reads", textStyle: {fontSize: 8}},
        hAxis:      {title: "Seq Run (Date_Miseq)", slantedText: true, slantedTextAngle:90, textStyle: {fontSize: 8} },
        chartArea:  {left:100,top:30,width:"50%",height:"60%"}
    });
}
function drawSampleChart(data, i) {
    var data = google.visualization.arrayToDataTable( data );
    new google.visualization.ColumnChart(document.getElementById("sampleChart"+i)).
    draw(data, {
        title:      "Sample Reads",
        width:      1000, height: 300,
        isStacked:  true,
        vAxis:      {title: "Reads", maxValue: 700000 , textStyle: {fontSize: 8}},
        hAxis:      {title: "Sample", slantedText: true, slantedTextAngle:90, textStyle: {fontSize: 8} },
        chartArea:  {left:100,top:30,width:"50%",height:"60%"}
    });
}
function drawAmpliconChart(data, i) {
    var data = google.visualization.arrayToDataTable( data );
    new google.visualization.ColumnChart(document.getElementById("ampliconChart"+i)).
    draw(data, {
        title:      "Amplicon Reads",
        width:      1000, height: 300,
        isStacked:  true,
        vAxis:      {title: "Amplicons",  maxValue: 1400, textStyle: {fontSize: 8}},
        hAxis:      {title: "Read Depth", slantedText: true, slantedTextAngle:90, textStyle: {fontSize: 8} },
        chartArea:  {left:100,top:30,width:"50%",height:"60%"}
    });
}

function changeHeatmapValues() {
    var midval = Math.round($('#heatmapMax').val() / 2)
    var maxval = Math.round($('#heatmapMax').val())
    var minval = Math.round($('#heatmapMin').val())

    if($('#heatmapMax').val() % 1 === 0 && $('#heatmapMin').val() % 1 === 0 ){

        $('#heatmap').empty()
        $('#heatmap').heatmap(
                {
                    data: {
                        values: new jheatmap.readers.TableHeatmapReader({ url: '${heatmapTsvPath}' })
                    },

                    init: function(heatmap) {
                        heatmap.size.width = 850;
                        heatmap.size.height = 550;


                        heatmap.cells.decorators["readsout"] = new jheatmap.decorators.Heat({
                            maxColor: [49,130,189],
                            nullColor: [178,178,178],
                            minColor: [222,235,247],
                            midColor: [158,202,225],
                            minValue: minval,
                            midValue: midval,
                            maxValue: maxval
                        });


                    }

                });
        $('#errorDiv').text("")

    } else {
        $('#errorDiv').text("Please use numbers only");
    }
}
</script>
<r:script>
    $("#footer-message h1").text('Click "View Sample" to load a sample into IGV.js');
    var igvStarted = false;
    function launchIGV(d){
        if(!$("#footer-toggle").hasClass("footerActive")) {
            $("#footer-toggle").click();
        }
        $("#footer-message").remove();

        if (d.panel.indexOf("MRD") === 0) {
            var message = d3.select("#pathos-footer").insert("div", "#igvDiv").attr("id", "footer-message");
            message.append("h1").text("Warning, this is an MRD sample, you should open this using Desktop IGV because in-browser IGV will probably crash your browser.");
        } else if(igvStarted) {
            PathOS.igv.addBAM(d.sample, d.dataUrl, 2500)
        } else {
            var igvDiv = document.getElementById("igvDiv");

            PathOS.igv.init(igvDiv, d.dataUrl, d.sample, d.panel, 2500);
            PathOS.igv.loaded = false;
            // This is part of TP53, a good a place as any to set our default view
            PathOS.igv.search("chr17:7,579,423-7,579,856");
            igvStarted = true;
        }
        $("#igv-open-"+d.sample).html("Loaded!");
    }
</r:script>
</body>
</html>


































