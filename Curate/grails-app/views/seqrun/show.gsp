<%@ page import="org.petermac.pathos.pipeline.UrlLink; org.petermac.util.Locator; org.petermac.pathos.curate.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'seqrun.label', default: 'Seqrun')}"/>
    <title>${seqrunInstance?.seqrun} - Show Sequencing Run</title>
    <parameter name="hotfix" value="off" />
    <parameter name="footer" value="on" />

</head>

<body>

<section id="seqrunInfo">
    <div class="container">

        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>

        <br>
        <div class="row">
            <div class="col-xs-8 col-xs-offset-2 outlined-box">
                <table class="infoTable">
                    <tr class="fieldcontain">
                        <td id="seqrun-label" class="property-label"><b>Sequencing Run</b></td>
                        <td class="property-value" aria-labelledby="seqrun-label">
                            ${seqrunInstance.seqrun}
                        </td>
                    </tr>

                    <g:if test="${seqrunInstance?.runDate}">
                        <tr class="fieldcontain">
                            <td id="runDate-label" class="property-label">
                                <g:message code="seqrun.runDate.label" default="Run Date"/>
                            </td>
                            <td class="property-value" aria-labelledby="runDate-label">
                                <g:formatDate date="${seqrunInstance?.runDate}" format="dd-MMM-yyyy"/>
                            </td>
                        </tr>
                    </g:if>

                    <g:if test="${seqrunInstance?.experiment}">
                        <tr class="fieldcontain">
                            <td id="experiment-label" class="property-label">Experiment</td>
                            <td class="property-value" aria-labelledby="experiment-label-label">
                                <g:fieldValue bean="${seqrunInstance}" field="experiment"/>
                            </td>
                        </tr>
                    </g:if>

                    <g:if test="${seqrunInstance?.panelList}">
                        <tr class="fieldcontain">
                            <td id="panelList-label" class="property-label">Panel List</td>
                            <td class="property-value" aria-labelledby="panelList-label">
                                <g:fieldValue bean="${seqrunInstance}" field="panelList"/>
                            </td>
                        </tr>
                    </g:if>

                    <g:if test="${seqrunInstance?.platform}">
                        <tr class="fieldcontain">
                            <td id="platform-label" class="property-label">
                                <g:message code="seqrun.platform.label" default="Platform"/>
                            </td>
                            <td class="property-value" aria-labelledby="platform-label">
                                <g:fieldValue bean="${seqrunInstance}" field="platform"/>
                                (<g:fieldValue bean="${seqrunInstance}" field="scanner"/>)
                            </td>
                        </tr>
                    </g:if>

                    <g:if test="${seqrunInstance?.readlen}">
                        <tr class="fieldcontain">
                            <td id="readlen-label" class="property-label">Reads</td>
                            <td class="property-value" aria-labelledby="readlen-label">
                                <g:fieldValue bean="${seqrunInstance}" field="readlen"/>
                                (<g:fieldValue bean="${seqrunInstance}" field="sepe"/>)
                            </td>
                        </tr>
                    </g:if>


<g:set var="samplesWithoutSeqVariants" value="${seqrunInstance
        .seqSamples
        .findAll { ss -> !ss.sampleName.contains("NTC")}
        .findAll { ss -> SeqVariant.countBySeqSample(ss) == 0}
        .size()}"/>
                    <tr class="fieldcontain">
                        <td id="run-status" class="property-label">Run Status</td>
                        <td class="property-value" aria-labelledby="run-status">
                            <span id="run-status-value">Checking run status...</span>
                            <span style="font-weight:900;${samplesWithoutSeqVariants > 0 ? "color:red;" : ""}">${samplesWithoutSeqVariants} ${samplesWithoutSeqVariants == 1 ? "sample has" : "samples have"} 0 Sequenced Variants.</span>
                        </td>
                    </tr>

                    <g:if test="${seqrunInstance.panelList?.contains('Pathology_hyb')}">
                    <tr class="fieldcontain">
                        <td>MultiQC</td>
                        <td><a target="_blank" href="${UrlLink.multiQCUrl( seqrunInstance.seqrun )}">Open Charts</a></td>
                    </tr>
                    <tr class="fieldcontain">
                            <td id="chronqc-label" class="property-label"><g:message code="seqrun.chronqc.label" default="ChronQC"/></td>
                            <td>
                            <table>
                            <g:each in="${seqrunInstance.panelList?.tokenize(", ")}" var="panel">
                                <tr style="border:0;padding:0">
                                    <td style="background:0">${panel}:</td>
                                    <td><a target="_blank" href="${Locator.dataServer}/Pathology/${Locator.samBase}/${seqrunInstance.seqrun}/QC/ChronQC/chronqc_output/${panel}.Run_level.chronqc.html">Run level</a>,
                                    <a target="_blank" href="${Locator.dataServer}/Pathology/${Locator.samBase}/${seqrunInstance.seqrun}/QC/ChronQC/chronqc_output/${panel}.All_Samples.chronqc.html">All Samples</a>,
                                    <a target="_blank" href="${Locator.dataServer}/Pathology/${Locator.samBase}/${seqrunInstance.seqrun}/QC/ChronQC/chronqc_output/${panel}.Sample_NA12878.chronqc.html">NA12878</a></td>
                                </tr>
                            </g:each>
                                </table>
                            </td>
                    </tr>

                        <tr class="fieldcontain">
                            <td>GaffaQC</td>
                            <td><ul style="margin:0;list-style:none;padding:0;">
                            <g:each in="${seqrunInstance.panelList?.tokenize(", ")}" var="panel">
                                <li><a target="_blank" href="${Locator.dataServer}/Pathology/${Locator.samBase}/${seqrunInstance.seqrun}/QC/GaffaQC/${panel}_metrics.pdf">${panel}</a></li>
                            </g:each>
                            </ul></td>
                        </tr>
                    </g:if>
                    <g:else>
                        <tr class="fieldcontain">
                            <td id="pipelog-label" class="property-label"><g:message code="seqrun.pipelog.label" default="Pipeline Log"/></td>
                            <td class="property-value" aria-labelledby="pipelog-label">
                                <g:link url="${UrlLink.pipelineUrl( seqrunInstance.seqrun, seqrunInstance.platform )}" target="_blank">bpipe log</g:link>
                            </td>
                        </tr>
                    </g:else>

                    <tr class="fieldcontain">
                        <td id="passfail-label" class="property-label"><g:message code="seqrun.passfail.label" default="Pass/Fail"/></td>
                        <td class="property-value" aria-labelledby="passfail-label">
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
                            <a id="toggleQC" href="#showQC"><span id="toggleQClabel">Show Run QC Charts</span> <span id="loadingCharts">(Loading Charts...)</span> <span id="loadingHeatmap">(Loading Heatmap...)</span></a>
                        </td>
                    </tr>

                    <g:if test="${seqrunInstance?.qcComment}">
                        <tr class="fieldcontain">
                            <td id="comment-label" class="property-label">
                                <g:message code="seqSample.comment.label" default="QC Comments"/>
                            </td>
                            <td class="property-value" aria-labelledby="comment-label">
                                ${seqrunInstance.qcComment}
                            </td>
                        </tr>
                    </g:if>

                    <g:if test="${seqrunInstance?.authorised}">
                        <tr class="fieldcontain">
                            <td id="authorised-label" class="property-label"><g:message code="seqrun.authorised.label" default="Authorised"/></td>
                            <td class="property-value" aria-labelledby="authorised-label">
                                <g:form action="authoriseRun" id="${seqrunInstance?.id}">
                                    ${seqrunInstance.authorised}
                                    <g:submitButton name="authorise" value="Revoke"/>
                                </g:form>
                            </td>
                        </tr>
                    </g:if>

                    <tr class="fieldcontain" id="showTags">
                        <td class="property-label">Tags</td>
                        <td id="showTagBox" class="outlined-box tags_field property-value">
                            <textarea id="showTagTextArea" placeholder="Enter Tags Here" class="ui-autocomplete-input" autocomplete="off"></textarea>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</section>

<section id="seqrunQcCharts">
    <div class="container">
        <div id="qc-outlined-box" class="row outlined-box">
            <h2 style="margin: 0;">Quality Control Charts</h2>
            <div id="qualityControlCharts">

                <div id="seqrunSummary" class="chartBox">
                    <h3>Summary Information</h3>
                    <g:seqrunQC seqrun="${seqrunInstance}"/>
                </div>
                <g:render template="googleCharts" />

                <g:if test="${seqrunInstance.panelList?.contains('Pathology_hyb')}">
                    <g:render template="hybContamination" />
                    <g:render template="hybROI" />
                    <r:script>$("#loadingHeatmap").remove();</r:script>
                </g:if>
                <g:else>
                    <g:if test="${d3heatmap}">
                        <g:render template="heatmap" />
                    </g:if>
                    <g:else>
                        <g:render template="oldHeatmap" />
                    </g:else>
                    <g:if test="${ seqrunInstance.seqSamples.findAll{ it.toString().contains("NTC") } }">
                        <g:render template="ntcReads" />
                    </g:if>
                </g:else>


            </div>
        </div>
    </div>
</section>

<g:render template="seqrunSamplesList" />

<script>
    /* To draw tags we need:
     *
     * 1) A div in the html
     * 2) The tag data, to draw existing tags
     * 3) Activate the textarea so we can enter new tags
     */

    <g:showPageTagsScript tags="${seqrunInstance?.tags as grails.converters.JSON}" id="${seqrunInstance?.id}" controller="seqrun"/>





    if(window.location.hash === "#showQC") {
        console.log("We should show the QC!");
        showQC();
    }

    d3.select("#toggleQC")
        .on('click', showQC);

    function showQC() {
        $("#seqrunQcCharts").toggleClass("show");

        if ($("#seqrunQcCharts").hasClass("show")) {
            d3.select("#toggleQC").attr("href", "#none");
            d3.select("#toggleQClabel").text("Hide Run QC");
        } else {
            d3.select("#toggleQC").attr("href", "#showQC");
            d3.select("#toggleQClabel").text("Show Run QC");
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

            PathOS.igv.init(igvDiv, d.dataUrl, d.sample, d.panel, 2500, "TP53");
            // This is part of TP53, a good a place as any to set our default view
            // PathOS.igv.search("chr17:7,579,423-7,579,856");
            // PathOS.igv.search("TP53");
            // PathOS.igv.search({
            //     "chromsome": "chr17",
            //     "start": 7579423,
            //     "end": 759856
            // });
            igvStarted = true;
        }
        $("#igv-open-" + d.sample).html("Loaded!");
    }

    if (PathOS.urlExists("${UrlLink.pipelineUrl( seqrunInstance.seqrun, seqrunInstance.platform )}")) {
        d3.select("#run-status-value").text("This run is ready.");
        console.log("It exists");
    } else {
        d3.select("#run-status-value").text("This run is still being processed.");
        console.log("cant find it");
    };

</r:script>
</body>
</html>








































