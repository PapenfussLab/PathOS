<%@ page import="org.petermac.pathos.curate.Seqrun; org.petermac.pathos.curate.StatsService" %>

<r:require module="google-charts"/>

<script src="https://www.google.com/uds/?file=visualization&amp;v=1&amp;packages=corechart" type="text/javascript"></script>

<div id="googleCharts" class="chartBox">
    <h3>Charts</h3>
    <img class="loading_logo" src="<g:context/>/dist/images/pathos_logo_animated.svg">
</div>

<r:script>

    // Chart stuff
    // Refactored to run asynchronously by DKGM 18-July-2016
    // Moved to a _template.gsp by DKGM 14-February-2017

    var data = false;

    $.ajax({
        url: '<g:context/>/Seqrun/fetchStats?id=${seqrunInstance?.id}',
        success:function(d){
            d3.select("#loadingCharts").remove();
            data = d;
            console.log("Charts Data loaded!");
            $("#googleCharts .loading_logo").remove()
            drawCharts();
        }
    });

    google.load('visualization', '1', {packages: ['corechart']});

    function showQC(event) {
        if(event) {
            event.preventDefault();
        }
        $("#qualityControlCharts").toggleClass("show");
        if($("#qualityControlCharts").hasClass("show")) {
            d3.select("#toggleQC").attr("href", "#none");
            d3.select("#toggleQClabel").text("Hide Run QC");
        } else {
            d3.select("#toggleQC").attr("href", "#showQC");
            d3.select("#toggleQClabel").text("Show Run QC");
        }
    }


    function drawCharts() {
        console.log("Drawing charts!");
        var div = d3.select("#googleCharts");

        data.panels.forEach(function (panel, i) {
            div.append("div").attr('id', 'readChart' + i).classed("GCdiv", true);
            div.append("div").attr('id', 'sampleChart' + i).classed("GCdiv", true);
            div.append("div").attr('id', 'ampliconChart' + i).classed("GCdiv", true);

            drawReadChart(JSON.parse(data.readChart[i]), i, panel);
            drawSampleChart(JSON.parse(data.sampleChart[i]), i);

            <g:if test="${seqrunInstance.panelList?.contains('Pathology_hyb')}">
            </g:if>
            <g:else>
                drawAmpliconChart(JSON.parse(data.ampliconChart[i]), i);
            </g:else>

        });
    }

    function drawReadChart(data, i, panel) {
        var data = google.visualization.arrayToDataTable( data );
        new google.visualization.LineChart(document.getElementById("readChart"+i)).
                draw(data, {
            title:      "Historical Runs by Panel "+panel,
            width:      1000,
            height:     400,
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
            width:      1000,
            height:     370,
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



</r:script>
