<%@ page import="org.petermac.pathos.pipeline.UrlLink; org.petermac.pathos.curate.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'seqrun.label', default: 'Seqrun')}"/>
    <title>${seqrunInstance?.seqrun} - <g:message code="default.show.label" args="[entityName]"/></title>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'jheatmap-1.0.0.css')}" type="text/css">

    <g:javascript src="jsapi.js" />
    <g:javascript src="quasipartikel/jquery.min.js" />

    <g:javascript src="jheatmap-1.0.1.js" />
    <r:script>
        google.load('visualization', '1', {packages: ['corechart']});

    </r:script>
    <tooltip:resources/>
    <style>
    /* To center the heatmap */
    table.heatmap {
        /*margin: 0px auto;*/
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
                    <g:formatDate date="${seqrunInstance?.runDate}"/>
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
                <span id="readlen-label" class="property-label"><g:message code="seqrun.readlen.label" default="Reads"/></span>
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
                <span id="comment-label" class="property-label"><g:message code="seqSample.comment.label" default="QC Comments"/></span>
                <span class="property-value" aria-labelledby="comment-label">
                    ${seqrunInstance.qcComment}
                </span>
            </li>
        </g:if>

        <g:if test="${seqrunInstance?.authorisedFlag}">
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



        <a href="#" onclick="hideshow(document.getElementById('chartdiv'));hideshow(document.getElementById('heatmapcontainer'))">Show Run QC</a>
        <br><br>

        <r:script type="text/javascript">
            function hideshow(which)
            {
                if (!document.getElementById)
                    return
                if (which.style.display=="inline")
                    which.style.display="none"
                else
                    which.style.display="inline"
            }
        </r:script>

        <span id="chartdiv" style="display: none">
            <h2>Seqrun QC</h2>



        <br/><br/>



            <g:seqrunQC seqrun="${seqrunInstance}"/>

        <br/>

            <h2>Seqrun QC Charts</h2>
        <br/><br/>


            <g:each in="${StatsService.panels(seqrunInstance)}" status="i" var="panel">

                <r:script>
          function changeHeatmapValues() {
                            var midval = Math.round($('#heatmapMax').val() / 2)
                            var maxval = Math.round($('#heatmapMax').val())
                            var minval = Math.round($('#heatmapMin').val())
                           // console.log($('#heatmapMax').val() )
                            //console.log($('#heatmapMin').val() )
                            console.log(midval)

                             if($('#heatmapMax').val() % 1 === 0 && $('#heatmapMin').val() % 1 === 0 ){

                                 $('#heatmap').empty()
                                 $('#heatmap').heatmap(
                                                {
                                                    data: {
                                                        values: new jheatmap.readers.TableHeatmapReader({ url: "${heatmapTsvLoc}" })
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
                             $('#errorDiv').text("Please use numbers only")
                             //console.log("User entered non-integer for heatmap")
                            }
                        }


            $(document).ready(function () {
                    $('#heatmap').heatmap(
                    {
                        data: {
                            values: new jheatmap.readers.TableHeatmapReader({ url: "${heatmapTsvLoc}" })
                        },

                        init: function(heatmap) {
                            heatmap.size.width = 850;
                            heatmap.size.height = 550;
                            heatmap.cols.zoom = 3;  // width in pixels (minimum = 3)
                            heatmap.rows.zoom = 15; // height in pixels (minimum = 3)
                  /*          heatmap.cols.zoom = 5;
            heatmap.cols.zoom = 8;

            heatmap.cols.labelSize.width = 500;*/


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



        });

console.log("what the fuck...");
console.log("${i}");

                    function drawReadChart${i}()
                    {
                        // Create and populate the data table.
//                        var data = google.visualization.arrayToDataTable( [["dataset","label 1","label 2","label 3"],["data 1",13,0,100],["data 2",0,0,100],["data 3",0,23,100]] );
                        var data = google.visualization.arrayToDataTable( ${StatsService.seqrunReadChart(seqrunInstance,panel)} );

                        // Create and draw the visualization.
                        new google.visualization.LineChart(document.getElementById("readChart${i}")).
                                draw(data, {
                                            title:      "Historical Runs by Panel ${panel}",
                                            width:      1000, height: 300,
                                            vAxis:      {title: "Reads", textStyle: {fontSize: 8}},
                                            hAxis:      {title: "Seq Run (Date_Miseq)", slantedText: true, slantedTextAngle:90, textStyle: {fontSize: 8} },
                                            chartArea:  {left:100,top:30,width:"50%",height:"60%"}
                                            });
                    }

                    function drawSampleChart${i}()
                    {
                        // Create and populate the data table.
                        //
                        var data = google.visualization.arrayToDataTable( ${StatsService.seqrunSampleChart(seqrunInstance,panel)} );

                        // Create and draw the visualization.
                        //
                        new google.visualization.ColumnChart(document.getElementById("sampleChart${i}")).
                                draw(data, {
                                            title:      "Sample Reads",
                                            width:      1000, height: 300,
                                            isStacked:  true,
                                            vAxis:      {title: "Reads", maxValue: 700000 , textStyle: {fontSize: 8}},
                                            //vAxis:      {title: "Reads", textStyle: {fontSize: 8}},
                                            hAxis:      {title: "Sample", slantedText: true, slantedTextAngle:90, textStyle: {fontSize: 8} },
                                            chartArea:  {left:100,top:30,width:"50%",height:"60%"}
                                            });
                    }

                    function drawAmpliconChart${i}()
                    {
                        // Create and populate the data table.
                        //
                        var data = google.visualization.arrayToDataTable( ${StatsService.seqrunAmpliconChart(seqrunInstance,panel)} );

                        // Create and draw the visualization.
                        //
                        new google.visualization.ColumnChart(document.getElementById("ampliconChart${i}")).
                                draw(data, {
                                            title:      "Amplicon Reads",
                                            width:      1000, height: 300,
                                            isStacked:  true,
                                            vAxis:      {title: "Amplicons",  maxValue: 1400, textStyle: {fontSize: 8}},
                                            //vAxis:      {title: "Amplicons", textStyle: {fontSize: 8}},
                                            hAxis:      {title: "Read Depth", slantedText: true, slantedTextAngle:90, textStyle: {fontSize: 8} },
                                            chartArea:  {left:100,top:30,width:"50%",height:"60%"}
                                            });
                    }

                    google.setOnLoadCallback(drawReadChart${i});
                    google.setOnLoadCallback(drawSampleChart${i});
                    google.setOnLoadCallback(drawAmpliconChart${i});
                </r:script>

                <div id="readChart${i}"     style="width: 1000px; height: 300px;"></div>
                <div id="sampleChart${i}"   style="width: 1000px; height: 300px;"></div>
                <div id="ampliconChart${i}" style="width: 1000px; height: 300px;"></div>

            </g:each>

            <h2>NTC Reads</h2>
            <br><br>
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

        </span>
    <br/><br/>
<g:if test="${heatmapTsvLoc}">
    <span id="heatmapcontainer" style="display: none;">
        <br/><h2>Heatmap</h2> <br/><br/>
        <div id="heatmap"></div>

        <br/><br/>
        <%-- HERE YOU PUT the Min Max and Middle values for the HEATMAP! the input boxes that is. --%>
        Heatmap Saturation (reads) Min: <input type="text" id="heatmapMin" name="heatmapMin" value="0"/>
        Max: <input type="text" id="heatmapMax" name="heatmapMax" value="1400"/>
        <input type="button" onclick="changeHeatmapValues()" value="Change"/>
        <div id="errorDiv" style="color:red"></div>

    </span>
</g:if>
        <br/><br/>

        <g:if test="${seqrunInstance?.seqSamples}">
            <span id="seqSamples-label" class="property-label">




                <h2>Samples</h2>
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
</body>
</html>
