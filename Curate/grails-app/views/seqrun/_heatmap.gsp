<%@ page import="org.petermac.pathos.curate.Seqrun; org.petermac.pathos.curate.StatsService" %>

<r:style>
    #heatmap-div svg {
        width: 100%;
        height: 100%;
    }

    .heatmap-x-label {
        font-size: 10px;
        color: black;
        text-anchor: end;
    }

    .heatmap-y-label {
        font-size: 10px;
        color: black;
        text-anchor: end;
    }
    rect.bordered {
        stroke: #E6E6E6;
        stroke-width: 1px;
    }

    line.slider {
        fill: none;
        stroke: black;
        stroke-width: 5;
        shape-rendering: crispEdges;
        cursor: ew-resize;
    }
    #heatmapContainer.shrink {
        height: 300px;
        overflow: hidden;
    }
    #heatmapExpando {
        text-align: center;
        cursor: pointer;
    }

</r:style>

<div id="heatmapContainer" class="shrink chartBox">
    <h3>Heatmap</h3>
    <img class="loading_logo" src="<g:context/>/dist/images/pathos_logo_animated.svg">
    <div id="heatmap-div"></div>
</div>
<div id="heatmapExpando"><i class="fa fa-chevron-down fa-3x" aria-hidden="true"></i></div>

<r:script>

$("#heatmapExpando").on("click", function(d){
    $("#heatmapContainer").removeClass("shrink");
    $("#heatmapExpando").remove();
});

/**
 * This function is only used for non hybrid-capture panels
 *
 */
    $.ajax('<g:context/>/Seqrun/ampliconHeatmapData?id='+${seqrunInstance?.id}, {success:function(d){
        $("#heatmapContainer .loading_logo").remove();
        $("#loadingHeatmap").remove();
        makeHeatmap(d);
    }});

    function makeHeatmap(blob) {
        var samples = {},
            amplicons = {},
            min = Number.MAX_SAFE_INTEGER,
            max = 0,
            buckets = [],
            labelHeight = 180,
            labelWidth = 260,
            blockSize = 15,
            bucketIndex = 1,
            colorScale = null,
            readsout_text = null;

        /*
         *  In this function we take:
         *    1. The heatmap data
         *    2. Process it line by line
         *    3. Put it into buckets so we can draw the slider
         *    3. Then draw the heatmap from the data
         */

        var data = [];
        blob.data.forEach(function(row){
            var obj = {
                sample_name: row[0],
                amplicon: row[1],
                readsout: row[2]
            };

            samples[obj.sample_name] = samples[obj.sample_name] || [];
            samples[obj.sample_name].push(obj);

            amplicons[obj.amplicon] = amplicons[obj.amplicon] || [];
            amplicons[obj.amplicon].push(obj);

            if (obj.readsout > max) {
                max = obj.readsout;
            }
            if (obj.readsout < min) {
                min = obj.readsout;
            }
            data.push(obj);
        });

        bucketIndex = (max * blockSize) / labelWidth;
        data.forEach(function(d){
            var item = Math.floor(d.readsout / bucketIndex);
            buckets[item] = buckets[item] + 1 || 1;
        });
        // House keeping, clean up the bucket array.
        for(i = 0; i < buckets.length; i++ ) {
            buckets[i] = buckets[i] || 1;
        }
        console.log(data.length);
        var total = data.length,
            sum = 0,
            first = 0,
            last = 0,
            modifier = 5;

        buckets.forEach(function(d, i){
            sum += d;
            if( sum < (total / modifier) ) first = i+1;
            if( sum < ((modifier - 1) * total) / modifier) last = i+2;
        });

        var colors = ["#ffffd9", "#edf8b1", "#c7e9b4", "#7fcdbb", "#41b6c4", "#1d91c0", "#225ea8", "#253494", "#081d58"];

        colorScale = d3.scaleQuantile()
                        .domain([min, max])
                        .range(colors);

        var heatmapHeight = blockSize * (Object.keys(amplicons).length),
            svgHeight = heatmapHeight + labelHeight + blockSize,
            heatmapWidth = blockSize * (Object.keys(samples).length),
            svgWidth = heatmapWidth + labelWidth + blockSize;

        var svg = d3.select("#heatmap-div").append("svg").attrs({
            id: "heatmap-svg",
            height: svgHeight,
            width: svgWidth,
            viewBox: "0 0 " + svgWidth + " " + svgHeight
        });

        // This is the grey background of the heatmap
        svg.append("rect").attrs({
            x: 0,
            y: 0,
            height: svgHeight,
            width: svgWidth,
            fill: 'rgba(0,0,0,0.2)'
        });
        // This is the actual heatmap chart
        var chart = svg.append("g").attrs({
            id: 'heatmap-chart',
            x: 0,
            y: 0
        }).style(
            'transform', 'matrix(1, 0, 0, 1, '+labelWidth+', '+labelHeight+')'
        );

        readsout_text = svg.append("g")
            .attrs({
                transform: "matrix(1 0 0 1 -10000 -10000)"
            });

        readsout_text.append("rect").attrs({
            x: 3,
            y: 3,
            height: 35,
            width: 65,
            fill: 'white'
        });

        readsout_text.append("text").text("Readsout")
            .attrs({
                x: 8,
                y: 18
            });
        readsout_text.append("text")
            .attrs({
                id: "readsout-label",
                x: 8,
                y: 32
            });

        var slider = svg.append("g").attrs({
            id: 'headmap-slider',
            x: 0,
            y: 0
        }).style(
            'transform', 'matrix(0.95, 0, 0, 0.85, 5, 5)'
        );

//		Slider Background
        slider.append('rect').attrs({
            width: labelWidth,
            height: labelHeight,
            fill: 'rgba(0,0,0,0.1)'
        });

        var blahIndex = (Math.max.apply(null, buckets) * blockSize) / labelHeight;

        // Weird off by one error...
        buckets.pop();

        slider.selectAll('g').data(buckets).enter()
                .append('g').each(function(d, i){

            var thing = d3.select(this);

            var squares = Math.ceil(d / blahIndex);

            for(var j = 0; j < squares; j++ ){
                thing.append("rect").attrs({
                    class: "slider-block",
                    height: blockSize,
                    width: blockSize,
                    x: i * blockSize,
                    y: labelHeight - ((j+1) * blockSize),
                    fill: colorScale(i * bucketIndex)
                }).datum(i * bucketIndex);
            }

        });

        var overlay = slider.append("g");

        var leftBound = first * blockSize,
            rightBound = last * blockSize;


        function init_drag(){
            leftBound  = d3.select("#left-line").datum();
            rightBound = d3.select("#right-line").datum();
        }

        overlay.append("rect")
        .attrs({
            id: 'middle',
            x: 0,
            y: 0,
            height: labelHeight,
            width: labelWidth,
            fill: 'rgba(0,0,0,0)'
        })
        .style("cursor", "pointer")
        .call(d3.drag()
            .on("start", init_drag)
            .on("drag", function(d){

                var x = d3.event.x;

                if(0 < x && x < labelWidth) {
                    rightBound += d3.event.dx;
                    leftBound += d3.event.dx;

                    if( leftBound < 0 ) leftBound = 0;
                    if( rightBound > labelWidth ) rightBound = labelWidth;

                    d3.select("#right-line")
                        .datum(rightBound)
                        .attrs({
                            transform: "matrix(1 0 0 1 "+rightBound+" 0)"
                        });

                    d3.select("#left-line")
                        .datum(leftBound)
                        .attrs({
                            transform: "matrix(1 0 0 1 "+leftBound+" 0)"
                        });

                    updateHeatmap();
                }
            })
        );

        var left_line = overlay
            .append("g")
            .datum(leftBound)
            .attrs({
                id: "left-line",
                transform: "matrix(1 0 0 1 "+leftBound+" 0)"
            });

        left_line.append("line").attrs({
            class: 'slider',
            x1: 0,
            y1: 0,
            x2: 0,
            y2: labelHeight
        });

        left_line.append("polygon").attrs({
            points: "0 0, 10 10, -10 10",
            fill: "black",
            stroke: "none",
            transform: "matrix(1 0 0 1 0 "+labelHeight+")"
        });

        left_line.append("text").attrs({
            id: "left-label",
            y: 25,
            transform: "matrix(1 0 0 1 0 "+labelHeight+")"
        });

        left_line.call(d3.drag()
            .on("start", init_drag)
            .on("drag", function(d){
                var x = d3.event.x;
                if(0 < x && x < labelWidth) {
                    leftBound = x;
                    d3.select("#left-line").datum(leftBound)
                        .attrs({
                            transform: "matrix(1 0 0 1 "+leftBound+" 0)"
                        });
                    updateHeatmap();
                }
            })
        );

        var right_line = overlay
                .append("g")
                .datum(rightBound)
                .attrs({
                    id: "right-line",
                    transform: "matrix(1 0 0 1 "+rightBound+" 0)"
                });

        right_line.append("line").attrs({
            class: 'slider',
            x1: 0,
            y1: 0,
            x2: 0,
            y2: labelHeight
        });

        right_line.append("polygon").attrs({
            points: "0 0, 10 10, -10 10",
            fill: "black",
            stroke: "none",
            transform: "matrix(1 0 0 1 0 "+labelHeight+")"
        });

        right_line.append("text").attrs({
            id: "right-label",
            y: 25,
            transform: "matrix(1 0 0 1 0 "+labelHeight+")"
        });

        right_line.call(d3.drag()
            .on("start", init_drag)
            .on("drag", function(d){
                var x = d3.event.x;
                if(0 < x && x < labelWidth) {
                    rightBound = x;
                    d3.select("#right-line").datum(rightBound)
                        .attrs({
                            transform: "matrix(1 0 0 1 "+rightBound+" 0)"
                        });
                    updateHeatmap();
                }
            })
        );

        paintHeatmap({
            blockSize: blockSize,
            svg: svg,
            chart: chart,
            labelHeight: labelHeight,
            labelWidth: labelWidth
        });
        updateHeatmap();


        function updateHeatmap(){
            var a = d3.select("#left-line").datum() * (max / labelWidth),
                b = d3.select("#right-line").datum() * (max / labelWidth);

            colorScale.domain([ a, b ]);

            d3.select("#left-label").text(Math.floor(a));
            d3.select("#right-label").text(Math.floor(b));

            d3.selectAll(".slider-block").attr("fill", function(d){
                return colorScale(d);
            });

            d3.selectAll(".heatmap-block").attr("fill", function(d){
                return colorScale(d.readsout);
            });
        }

        function paintHeatmap(config){
            var blockSize = config.blockSize,
                    svg = config.svg,
                    chart = config.chart,
                    labelHeight = config.labelHeight,
                    labelWidth = config.labelWidth;

            // Draw labels and axis
            // Then go row by row and draw boxes

            labelX = labelWidth;
            labelY = labelHeight;
            stupidShit = labelY;

            var xLabels = svg.append("g").attr("id", "heatmap-x-labels");
            var yLabels = svg.append("g").attr("id", "heatmap-y-labels");

            // Draw the x-axis
            Object.keys(samples).forEach(function (sample) {
                // I don't understand matrix transforms OKAY?!??!
                // I don't know why this works, but it does.
                stupidX = labelX + labelHeight;
                stupidY = stupidShit - labelWidth - 10;

                yLabels.append("text").text(sample).attrs({
                    dx: labelX,
                    dy: labelY,
                    class: 'heatmap-y-label'
                }).style(
                        'transform',
                        'matrix(0, 1, -1, 0, ' + stupidX + ', ' + stupidY+')'
                );

                labelX += blockSize;
                stupidShit = stupidShit - blockSize;
            });

            labelY = labelHeight;
            labelX = labelWidth;

            // Draw the y-axis and the rows
            Object.keys(amplicons).forEach(function (amplicon) {

                // y-axis
                xLabels.append("text").text(amplicon).attrs({
                    x: labelX - 10,
                    y: labelY + 10,
                    class: 'heatmap-x-label'
                });
                labelX = labelWidth;
                labelY += blockSize;
            });

            chart.selectAll(".row").data(Object.keys(amplicons))
                .enter()
                .append("g")
                .classed("row", true)
                .attr("id", function(d){return "row-"+d})
                .datum(function(d){
                    return amplicons[d];
                }).each(function(d, i){
                d3.select(this)
                    .selectAll("rect")
                    .data(d)
                    .enter()
                .append("rect").attrs({
                    class: 'bordered heatmap-block',
                    x: function(d, i){ return i * blockSize; },
                    y: i * blockSize,
                    rx: 2,
                    ry: 2,
                    height: blockSize,
                    width: blockSize,
                    fill: function(d){
                        return colorScale(d.readsout);
                    }
                }).on("mouseover", function(d){
                    var x = parseInt($(this).attr('x')) + labelWidth + blockSize,
                        y = parseInt($(this).attr('y')) + labelHeight + blockSize;

                    readsout_text.attrs({
                        transform: "matrix(1 0 0 1 "+x+" "+y+")"
                    });
                    d3.select("#readsout-label").text(d.readsout);
                }).on("mousedown", function(d){
//                    Do you want to load stuff in IGV..?
//                    $("#igv-"+d.sample_name).click();
                });

            });
        }
    }


</r:script>
