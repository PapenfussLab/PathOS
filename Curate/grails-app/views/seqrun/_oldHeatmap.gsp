<%@ page import="org.petermac.pathos.curate.Seqrun; org.petermac.pathos.curate.StatsService" %>



<link rel="stylesheet" href="${resource(dir: 'css', file: 'jheatmap-1.0.0.css')}" type="text/css">
<g:javascript src="jheatmap-1.0.1.js" />

<r:style>
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
</r:style>

<div id="heatmapContainer" class="chartBox">
    <h3>Heatmap</h3>
    <button onclick="refreshHeatmap()">Refresh heatmap</button>
    <img class="loading_logo" src="<g:context/>/dist/images/pathos_logo_animated.svg">
    <div id="heatmap"></div>

    <br/><br/>
    <%-- HERE YOU PUT the Min Max and Middle values for the HEATMAP! the input boxes that is. --%>
    Heatmap Saturation (reads) Min: <input type="text" id="heatmapMin" name="heatmapMin" value="0"/>
    Max: <input type="text" id="heatmapMax" name="heatmapMax" value="1400"/>
    <input type="button" onclick="changeHeatmapValues()" value="Change"/>
    <div id="errorDiv" style="color:red"></div>
</div>



<r:script>


var seqrunTsvLoc =   '<g:context/>/payload/seqrun_qc_heatmap/${seqrunInstance.seqrun}.tsv'

function refreshHeatmap() {

    $.ajax({
        url: "<g:context/>/Seqrun/deleteHeatmapTsv?id=${seqrunInstance?.id}",
        success: function(d){
            console.log("success", d);
            alert("Heatmap cache deleted, refreshing page.");
            location.reload();
        },
        error: function(d){
            console.log("Error", d);
            alert("There was an error.");
        },
        type: "POST"
    })

}

var heatMapHasBeenDrawn = false;

$.ajax('<g:context/>/Seqrun/fetchHeatmapTsv?id=${seqrunInstance?.id}', {success:function(d){
   d3.select("#loadingHeatmap").remove();
   d3.select("#heatmapContainer .loading_logo").remove();
   console.log("Heatmap loaded!");
   drawHeatmap();
}});


function drawHeatmap() {
   console.log("Drawing heatmap.");
   heatMapHasBeenDrawn = true;
   $('#heatmap').heatmap({
       data: {
           values: new jheatmap.readers.TableHeatmapReader({ url: seqrunTsvLoc })
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

function changeHeatmapValues() {
   var midval = Math.round($('#heatmapMax').val() / 2)
   var maxval = Math.round($('#heatmapMax').val())
   var minval = Math.round($('#heatmapMin').val())

   if($('#heatmapMax').val() % 1 === 0 && $('#heatmapMin').val() % 1 === 0 ){

       $('#heatmap').empty()
       $('#heatmap').heatmap(
           {
               data: {
                   values: new jheatmap.readers.TableHeatmapReader({ url: seqrunTsvLoc })
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


</r:script>