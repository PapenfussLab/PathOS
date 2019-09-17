<%@ page import="org.petermac.pathos.curate.Seqrun; org.petermac.pathos.pipeline.UrlLink" %>

<r:style>

#contaminationHeatmap {
    height: 300px;
}

</r:style>


<div id="contamination" class="chartBox">
    <h3>Contamination Heatmap</h3>

    <g:if test="${seqrunInstance.panelList.contains('Pathology_hyb')}">
        <script>
            var contaminationUrl = "${ UrlLink.contaminationUrl(seqrunInstance.seqrun) }",
                    contaminationDiv = d3.select("#contamination");

            try {
                $.ajax({
                    url: contaminationUrl,
                    success: function(d){
                        contaminationDiv.append("p")
                            .text("Click the image to open in a new window.");

                        contaminationDiv.append("a")
                            .attr("href", contaminationUrl)
                            .attr("target", "_blank")
                            .append("img")
                            .attr("id", "contaminationHeatmap")
                            .attr("src", contaminationUrl);

                    },
                    error: function(xhr, ajaxOptions, thrownError){
                        contaminationDiv
                            .append("p").text("404 - contamination.png not found in sample folder, has the run finished yet?");
                    }
                })
            } catch(e){
                console.log(e);
            }
        </script>
    </g:if>
    <g:else>
        <p>Contamination heatmaps are only generated for Hybrid Capture panels.</p>
    </g:else>
</div>
