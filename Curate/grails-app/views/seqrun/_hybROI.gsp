<%@ page import="grails.converters.JSON; org.petermac.pathos.curate.Seqrun; org.petermac.pathos.pipeline.UrlLink; org.petermac.pathos.curate.LabAssay" %>

<r:style>
#graphspace {
    height: 100%;
}
#graphspace svg {
    width: 100%;
}
#columnspace {
    height: 100%;
}

#columnspace li label {
    padding-left: 5px;
    cursor: pointer;
}

.line {
  fill: none;
  stroke: steelblue;
  stroke-width: 1.5px;
}
.fadeInstead .hide {
    display: inline;
    opacity: 0.1;
}

.hide {
    display: none;
}
.loading_logo {
    width: 400px;
}

#roiGraph circle, #hybGraph circle {
    fill-opacity: 0.6;
}

#roiGraph path.hidden, #hybGraph path.hidden {
    display: none;
}

#svg-NA12878 path, #waterfall-svg-NA12878 path {
    stroke: black !important;
}

</r:style>

<div id="hybGraph" class="row"></div>

<r:script>

/**
*
* To draw the ROI Graphs
* we need to:

initialise the workspace
configure it

load the data

specify the graphs

*
*/
var hybGraph = new PathOS.graphSpace({
    div: d3.select("#hybGraph"),
    title: "Hybrid Capture QC",
    colours: ['#1b9e77','#d95f02','#7570b3','#e7298a','#66a61e','#e6ab02','#a6761d','#666666']
});

var waterfallPlot = {
    id: "waterfall",
    name: "Waterfall Plot",
    init: function(sample){
        const control = sample.data;
        var g = this.g;

        // parse the control..?
        control.values.forEach(function(d){
            waterfallControl[d.region] = parseInt(d.coverage);
        });


        this.x = graphX = d3.scaleLinear().range([0, this.width]);
        this.y = graphY = d3.scaleLinear().range([this.height, 0]);

        var xAxisEnd = control.values.length;
        this.x.domain([ 0, xAxisEnd ]);
        this.defaultYdomain = [ -500, 500 ];
        this.y.domain(this.defaultYdomain);

        this.xAxis.call(d3.axisBottom(graphX).ticks(0));
        this.xLabel.text("Regions Of Interest, sorted by difference between Sample coverage and Control coverage");

        this.yAxis.call(d3.axisLeft(graphY));
        this.yLabel.text("Difference between Sample coverage and Control coverage");

        var controlSample = g.append("g")
            .datum(control)
            .attr("class", (d) => "svg-"+d.id)
            .classed("control", true);

        // var exonOrder = [];
        // order from lowest to highest
        control.values = [];
        control.data.sort(function(a,b){
            // return totals[a["Region"]] - totals[b["Region"]];
            return parseFloat(a["Mean coverage"]) - parseFloat(b["Mean coverage"]);
        }).forEach(function(d, i){
            //xPos[d.Region] = i;
            control.values.push({
                region: d.Region,
                coverage: d["Mean coverage"]
            });
            exonOrder.push(d.Region);
        });

        var line = d3.line()
            .curve(d3.curveBasis)
            .x(function(d) { return graphX(exonOrder.indexOf(d.region)); })
            .y(function(d) { return graphY(0); });

        controlSample.append("path")
            .attr("class", "line")
            .attr("d", function(d) { return line(d.values); })
            .style("stroke", "black");

    },
    add: function (data){
        var graphX = this.x;
        var graphY = this.y;

        var sample = this.scatterNodes
            .append("g")
            .datum(data)
            .attr("class", (d) => "svg-"+d.id)
            .classed("sample", true);

        var ratio = 500 / data.values.length;

        var array = data.values.map(function(d){
               return parseInt(d.coverage) - waterfallControl[d.region];
            }).sort(function(a,b){
                return b - a;
            });

        this.displayedData[data.id] = array;

        var intersect = Math.round((array.indexOf(0) * 100) / array.length) + "%";

        data.intersect.text(intersect);

        sample.selectAll("circle")
            .data(array)
            .enter()
            .append("circle")
            .attr("cx", function(d, i){return graphX(i);})
            .attr("cy", function(d){return graphY(d);})
            .attr("r", 0)
            .attr("fill", hybGraph.color(data.id))
            .transition()
            .duration(function(d, i){
                return i * ratio;
            })
            .transition()
            .duration(500)
            .attr("r", 1);
    }
};

var scatterGraph = {
    id: "scatter",
    name: "Scatter Graph",
    init: function(sample) {
        const control = sample.data;
        var g = this.g;

        this.x = graphX = d3.scaleLinear().range([0, this.width]);
        this.y = graphY = d3.scaleLinear().range([this.height, 0]);

        var xAxisEnd = control.values.length;
        this.x.domain([ 0, xAxisEnd ]);
        this.defaultYdomain = [ 0, 800 ];
        this.y.domain(this.defaultYdomain);


        this.xAxis.call(d3.axisBottom(graphX).ticks(0));
        this.xLabel.text("Regions Of Interest, sorted by coverage of control: NA12878");

        this.yAxis.call(d3.axisLeft(graphY));
        this.yLabel.text("Mean Coverage");

        var controlSample = g.append("g")
            .datum(control)
            .attr("class", (d) => "svg-"+d.id)
            .classed("control", true);

        // var exonOrder = [];
        // order from lowest to highest
        control.values = [];
        control.data.sort(function(a,b){
            // return totals[a["Region"]] - totals[b["Region"]];
            return parseFloat(a["Mean coverage"]) - parseFloat(b["Mean coverage"]);
        }).forEach(function(d, i){
            //xPos[d.Region] = i;
            control.values.push({
                region: d.Region,
                coverage: d["Mean coverage"]
            });
            exonOrder.push(d.Region);
        });

        var line = d3.line()
            .curve(d3.curveBasis)
            .x(function(d) { return graphX(exonOrder.indexOf(d.region)); })
            .y(function(d) { return graphY(parseInt(d.coverage)); });

        controlSample.append("path")
            .attr("class", "line")
            .attr("d", function(d) { return line(d.values); })
            .style("stroke", "black");

    },
    add: function(data) {
        var graphX = this.x;
        var graphY = this.y;

        var sample = this.scatterNodes
            .append("g")
            .datum(data)
            .attr("class", (d) => "svg-"+d.id)
            .classed("sample", true);

        var ratio = 500 / data.values.length;

        sample.selectAll("circle")
            .data(data.values)
            .enter()
            .append("circle")
            .attr("cx", function(d){return graphX(exonOrder.indexOf(d.region));})
            .attr("cy", function(d){return graphY(d.coverage);})
            .attr("r", 0)
            .attr("fill", hybGraph.color(data.id))
            .transition()
            .duration(function(d, i){
                return i * ratio;
            })
            .transition()
            .duration(500)
            .attr("r", 2);
    }
};

hybGraph.addGraph(waterfallPlot);
hybGraph.addGraph(scatterGraph);










const genes = ${(LabAssay.findByTestName("Familial One Panel")?.genes?.tokenize(", ") ?: []) as JSON};

var exonOrder = [],
    waterfallControl = {};

// Grails pulls down the model and gives it to our JS
var samples = [];
var control = false;
var pairs = [];

lineGraphX = null,
lineGraphY = null,
waterfallGraphX = null,
waterfallGraphY = null;


// Extract the data

var rawSamples = [
    {
        name: "throwaway",
        tsv_url: "don't use this element, it's just a hack so the <each> stuff with a comma doesn't break things"
    }
    <g:each in="${seqrunInstance.seqSamples.sort { a, b -> a.sampleName <=> b.sampleName }}" var="ss">
    ,{
        name: "${ss.sampleName}",
        tsv_url: "${UrlLink.dataUrl(seqrunInstance.seqrun, ss.sampleName, 'QC/' + ss.sampleName + '_region_coverage.tsv')}"
    }
    </g:each>
].slice(1);

// Let's figure out if each sample is a: control, tumor, normal, pair or N/A
rawSamples.forEach(function(sample){
    if(sample.type) {
        // Already has a type...
    } else if(sample.name == "NA12878") {
        sample.type = "control";
        control = sample;
    } else if (sample.name.indexOf("--") > 0) {
        // This is a pair thing...
        sample.type = "pair";
        var tumor = sample.name.split("--")[0];
        var normal = sample.name.split("--")[1];
        var pair = {
            pair: sample
        };

        rawSamples.forEach(function(d){
            if(typeof d.type == 'undefined' || d.type == 'N/A') {
                if(d.name == tumor) {
                    d.type = 'tumor';
                    pair.tumor = d;
                } else if (d.name == normal) {
                    d.type = 'normal';
                    pair.normal = d;
                }
            }
        });
        pairs.push(pair);
    } else {
        sample.type = 'N/A';
    }
});

try {

// Now that we know the types, let's stack them.
if(control) {
    control.stacked = true;
    samples.push(control);
}
pairs.forEach(function(pair){
    pair.tumor.normal = pair.normal;
    pair.normal.tumor = pair.tumor;

    pair.pair.stacked = true;
    pair.tumor.stacked = true;
    pair.normal.stacked = true;
    samples.push(pair.tumor);
    samples.push(pair.normal);
});
rawSamples.forEach(function(sample){
    if(!sample.stacked) {
        sample.stacked = true;
        samples.push(sample);
    }
});

hybGraph.applyData(samples);

} catch (e) {
    console.log("Error loading trying to draw hybROI graph", e);
}

function load_all_data(){
    $("td.action").each(function(i, button){
        setTimeout(function(){
            button.click();
        }, i*1000);
    });
}


</r:script>





















