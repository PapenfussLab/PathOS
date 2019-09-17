<%@ page import="org.petermac.pathos.curate.SeqSample" %>
<r:style>
#geneBox {
    height: 300px;
    overflow: scroll;
}
#donutBox {
    height: 300px;
}

#geneBox h3, #donutBox h3 {
    text-align: center;
}
#geneSVG, #donutSVG {
    width: 100%;
}

svg {
    font-family: 'Roboto', sans-serif;
    color: #333333;
}

/* Add shadow effect to chart. If you don't like it, get rid of it. */
svg {
    -webkit-filter: drop-shadow( 0px 3px 3px rgba(0,0,0,.3) );
    filter: drop-shadow( 0px 3px 3px rgba(0,0,0,.25) );
}

/*Styling for the lines connecting the labels to the slices*/
polyline{
    opacity: .3;
    stroke: black;
    stroke-width: 2px;
    fill: none;
}

/* Make the percentage on the text labels bold*/
.labelName tspan {
    font-style: normal;
    font-weight: 700;
}

/* In biology we generally italicise species names. */
.labelName {
    font-size: 0.9em;
    font-style: italic;
}

</r:style>

<section id="svGenes">
    <div class="container row">
        <div id="geneBox" class="col-xs-12 col-sm-6 outlined-box">
            <h3>Genes</h3>
            <svg id="geneSVG"></svg>
        </div>
        <div id="donutBox" class="col-xs-12 col-sm-6 outlined-box">
            <h3>Pathways</h3>
        </div>
    </div>
</section>



<r:script>

$.ajax({
    url:"<g:context/>/SeqSample/geneInfo/${seqSample.id}",
    complete: function(d){
        var data = d.responseJSON;
        data = data.sort((a,b) => b.svInfo.length - a.svInfo.length);

        var height = 60 * data.length + 40

        d3.select("#geneSVG").attrs({
            viewBox: "0 0 650 " + height
            //height: height + "px"
        }).append("g").attrs({
            id: "geneLollipop",
        })
        .selectAll(".lollipop")
            .data(data)
            .enter()
            .append("g")
            .each(lollipop);


        var pathways = {};
        data.forEach(function(gene){
            gene.pathways.forEach(function(pathway){
                pathways[pathway.name] = pathways[pathway.name] || pathway;

                pathways[pathway.name].counter = pathways[pathway.name].counter || 0;

                pathways[pathway.name].counter = pathways[pathway.name].counter + gene.svInfo.length

                console.log("this gene "+gene.gene)
                console.log("has this many variants: "+gene.svInfo.length)
                console.log("we're tallying them to this pathway: "+pathway.name)
            });
        });

        console.log(pathways);
        var totalVariants = ${seqSample.seqVariants.size()} || 1000;

        Object.keys(pathways).forEach(function(pathway){
            var segment = d3.select("#pathway-"+pathways[pathway].id);

            console.log(pathways[pathway].counter);

            var variantAbundance = pathways[pathway].counter / totalVariants;

            segment.attr("fill", "rgba(255,0,0,"+variantAbundance+")");
        });
    }
});


function lollipop(data, index){
    //console.log(data);
    //console.log(this);
    //console.log(index);

    var box = d3.select(this).attrs({
        class: "lollipop"
    }).styles({
        transform: sprintf("matrix(1,0,0,1,20,%d)", 60 * index + 20 )
    });

    box.append("rect").attrs({
        x: 0,
        y: 0,
        width: 625,
        height: 50,
        fill: "rgba(255,255,255, 1)"
    });

    box.append("text")
        .text(data.gene)
        .attrs({
            x: 15,
            y: 25
        });

    box.append("text")
        .text(data.svInfo.length + " variants")
        .attrs({
            x: 15,
            y: 40
        })

    var lineY = 35,
        sizeFactor = data.size / 500;

    box.append("line")
        .attrs({
            x1: 100,
            y1: lineY,
            x2: 600,
            y2: lineY,
            style: "stroke: black; stroke-width:2;"
        });

    box.selectAll(".variant").data(data.svInfo).enter().append("g")
    .each(function(d){
        var relativePos = d.pos - data.start;
        var percentDistance = relativePos / data.size;
        var pixels = sizeFactor * percentDistance;

        var variant = d3.select(this);

var colorLookup = [
"#fff",
"#fffdc1",
"#f4d374",
"#e89e53",
"#d65430",
"#ae2334"
]

var rand = 10 + 18 * Math.random();

        variant.append("circle").attrs({
            cx: pixels + 150,
            cy: lineY - rand,
            r: 5,
            style: "stroke: black; stroke-width:1; opacity: 0.5;",
            fill: d.maxPmClass ? colorLookup[d.maxPmClass] : "white"
        });

        variant.append("line").attrs({
            x1: pixels + 150,
            y1: lineY - rand,
            x2: pixels + 150,
            y2: lineY,
            style: "stroke: black; stroke-width:1; opacity: 0.5;"
        })
    });

    box.selectAll(".exon").data(data.exons).enter().append("g")
    .each(function(d){
        var relativePos = d.start - data.start;
        var percentDistance = relativePos / data.size;
        var pixels = sizeFactor * percentDistance;

        var exonEnd = d.end - data.start;
        var perDistToEnd = exonEnd / data.size;
        var endPixels = sizeFactor * perDistToEnd;

        var exon = d3.select(this);
        exon.append("rect").attrs({
            x: pixels + 150,
            y: lineY,
            width: endPixels - pixels,
            height: 5,
            fill: "green"
        });
    });

}


</r:script>

<r:script>


    var donut = donutChart()
            .width(650)
            .height(300)
            .cornerRadius(1) // sets how rounded the corners are on each slice
            .padAngle(0.015) // effectively dictates the gap between slices
            .variable('size')
            .category('name');

    $.ajax({
        url:"<g:context/>/SeqSample/pathwayInfo/${seqSample.id}",
        complete: function(d){
            var data = d.responseJSON;
            console.log(data);
            d3.select("#donutBox")
                .datum(data)
                .call(donut);
        }
    });

    function donutChart() {
        var width,
            height,
            margin = {top: 10, right: 10, bottom: 10, left: 10},
            variable, // value in data that will dictate proportions on chart
            category, // compare data by
            padAngle, // effectively dictates the gap between slices
            floatFormat = d3.format('.4r'),
            cornerRadius, // sets how rounded the corners are on each slice
            percentFormat = d3.format(',.2%');

        var color = {
            "Cell Survival": "#6cbf7f",
            "Cell Fate": "#90aad2",
            "Genome Maintenance": "#e65760"
        }
        var processes = [{
            size: 6,
            process: "Cell Survival"
        },{
            size: 5,
            process: "Cell Fate"
        },{
            size: 1,
            process: "Genome Maintenance"
        }]


        function chart(selection){
            selection.each(function(data) {
//console.log("Drawing donut chart");
//console.log(data);

                // generate chart

                // ===========================================================================================
                // Set up constructors for making donut. See https://github.com/d3/d3-shape/blob/master/README.md
                var radius = Math.min(width, height) / 2;

                // creates a new pie generator
                var pie = d3.pie()
                        .value(function(d) { return floatFormat(d[variable]); })
                        .sort(null);

                // contructs an arc generator. This will be used for the donut. The difference between outer and inner
                // radius will dictate the thickness of the donut
                var arc = d3.arc()
                        .outerRadius(radius * 0.34)
                        .innerRadius(radius * 0.2)
                        .cornerRadius(cornerRadius)
                        .padAngle(padAngle);

                // this arc is used for aligning the text labels
                var outerArc = d3.arc()
                        .outerRadius(radius * 0.45)
                        .innerRadius(radius * 0.45);
                // ===========================================================================================

                // ===========================================================================================
                // append the svg object to the selection
                var svg = selection.append('svg')
                        .attr('id', 'donutSVG')
                        .attr('viewBox', "0 0 650 400")
                        .attr('width', width + margin.left + margin.right)
                        .attr('height', height + margin.top + margin.bottom)
                        .append('g')
                        .attr('transform', 'matrix(1.4,0,0,1.4,' + width / 2 + ',' + height / 2 + ')');
                // ===========================================================================================

                // ===========================================================================================
                // g elements to keep elements within svg modular
                svg.append('g').attr('class', 'slices');
                svg.append('g').attr('class', 'processes');
                svg.append('g').attr('class', 'labelName');
                svg.append('g').attr('class', 'lines');
                // ===========================================================================================

                // ===========================================================================================
                // add and colour the donut slices
                var path = svg.select('.slices')
                        .datum(data).selectAll('path')
                        .data(pie)
                        .enter().append('path')
                        .attr("id", (d) => "pathway-" + d.data.id)
                        //.attr('fill', (d) => color[d.data.process] )
                        .attr('fill', (d) => "grey" )
                        .attr('d', arc);
                // ===========================================================================================

                newArc = d3.arc()
                        .outerRadius(radius * 0.4)
                        .innerRadius(radius * 0.35)
                        .cornerRadius(cornerRadius)
                        .padAngle(padAngle);

                var outerPath = svg.select('.processes')
                        .datum(processes).selectAll('path')
                        .data(pie)
                        .enter().append('path')
                        .attr('fill', function(d) { return color[d.data.process]; })
                        .attr('d', newArc);

                // ===========================================================================================
                // add text labels
                var label = svg.select('.labelName').selectAll('text')
                        .data(pie)
                        .enter().append('text')
                        .attr('dy', '.35em')
                        .html(function(d) {
                    // add "key: value" for given category. Number inside tspan is bolded in stylesheet.
                    return d.data[category];
                })
                        .attr('transform', function(d) {

                    // effectively computes the centre of the slice.
                    // see https://github.com/d3/d3-shape/blob/master/README.md#arc_centroid
                    var pos = outerArc.centroid(d);

                    // changes the point to be on left or right depending on where label is.
                    pos[0] = radius * 0.56 * (midAngle(d) < Math.PI ? 1 : -1);
                    return 'translate(' + pos + ')';
                })
                        .style('text-anchor', function(d) {
                    // if slice centre is on the left, anchor text to start, otherwise anchor to end
                    return (midAngle(d)) < Math.PI ? 'start' : 'end';
                });
                // ===========================================================================================

                // ===========================================================================================
                // add lines connecting labels to slice. A polyline creates straight lines connecting several points
                var polyline = svg.select('.lines')
                        .selectAll('polyline')
                        .data(pie)
                        .enter().append('polyline')
                        .attr('points', function(d) {

                    // see label transform function for explanations of these three lines.
                    var pos = outerArc.centroid(d);
                    pos[0] = radius * 0.55 * (midAngle(d) < Math.PI ? 1 : -1);
                    return [arc.centroid(d), outerArc.centroid(d), pos]
                });
                // ===========================================================================================

                // ===========================================================================================
                // add tooltip to mouse events on slices and labels
                d3.selectAll('.labelName text, .slices path').call(toolTip);
                // ===========================================================================================

                // ===========================================================================================
                // Functions

                // calculates the angle for the middle of a slice
                function midAngle(d) { return d.startAngle + (d.endAngle - d.startAngle) / 2; }

                // function that creates and adds the tool tip to a selected element
                function toolTip(selection) {

                    // add tooltip (svg circle element) when mouse enters label or slice
                    selection.on('mouseenter', function (data) {

                        svg.append('text')
                                .attr('class', 'toolCircle')
                                .attr('dy', -15) // hard-coded. can adjust this to adjust text vertical alignment in tooltip
                                .html(toolTipHTML(data)) // add text to the circle.
                                .style('font-size', '.9em')
                                .style('text-anchor', 'middle'); // centres text in tooltip

                        svg.append('circle')
                                .attr('class', 'toolCircle')
                                .attr('r', radius * 0.19) // radius of tooltip circle
                                .style('fill', color[data.data.process]) // colour based on category mouse is over
                                .style('fill-opacity', 0.30);

                    });

                    // remove the tooltip when mouse leaves the slice/label
                    selection.on('mouseout', function () {
                        d3.selectAll('.toolCircle').remove();
                    });
                }

                // function to create the HTML string for the tool tip. Loops through each key in data object
                // and returns the html string key: value
                function toolTipHTML(data) {

                    var tip = '',
                            i   = 0;

                    for (var key in data.data) {

                        // if value is a number, format it as a percentage
                        var value = (!isNaN(parseFloat(data.data[key]))) ? percentFormat(data.data[key]) : data.data[key];

                        // leave off 'dy' attr for first tspan so the 'dy' attr on text element works. The 'dy' attr on
                        // tspan effectively imitates a line break.
                        if (i === 0) tip += '<tspan x="0">' + key + ': ' + value + '</tspan>';
                        else tip += '<tspan x="0" dy="1.2em">' + key + ': ' + value + '</tspan>';
                        i++;
                    }

                    return tip;
                }
                // ===========================================================================================

            });
        }

        // getter and setter functions. See Mike Bostocks post "Towards Reusable Charts" for a tutorial on how this works.
        chart.width = function(value) {
            if (!arguments.length) return width;
            width = value;
            return chart;
        };

        chart.height = function(value) {
            if (!arguments.length) return height;
            height = value;
            return chart;
        };

        chart.margin = function(value) {
            if (!arguments.length) return margin;
            margin = value;
            return chart;
        };

        chart.radius = function(value) {
            if (!arguments.length) return radius;
            radius = value;
            return chart;
        };

        chart.padAngle = function(value) {
            if (!arguments.length) return padAngle;
            padAngle = value;
            return chart;
        };

        chart.cornerRadius = function(value) {
            if (!arguments.length) return cornerRadius;
            cornerRadius = value;
            return chart;
        };

        chart.variable = function(value) {
            if (!arguments.length) return variable;
            variable = value;
            return chart;
        };

        chart.category = function(value) {
            if (!arguments.length) return category;
            category = value;
            return chart;
        };

        return chart;
    }


</r:script>


