<%@ page import="org.petermac.pathos.curate.SeqSample" %>

<r:require modules="circos"/>
<r:style>

    #circosControls p {
        text-align: center;
    }
    #circosControls p input {
        display: none;
    }
    #circosControls p label {
        min-width: 50px;
        width: 15%;
        padding: 2px 5px;
        margin: 2px;
        border: solid 1px black;
        float: left;
    }

    #circosControls p input[type=checkbox]:not(:checked) + label {
        background: rgba(0,0,0,0) !important;
    }
    .cs-layout path:nth-child(2) {
        %{--stroke: black !important;--}%
    }

</r:style>
<section id="seqSampleCircos">
    <div class="container outlined-box">
        <h1>Circos Plot</h1>

        <div class="row" id="circosDiv">
            <div id="circosControls" class="outlined-box row">
                <div id="chromo-pickers" class="col-xs-5 col-xs-offset-1"></div>
                <div class="col-xs-5">
                    <input type="button" value="Reset" onclick="reset()">
                    <input type="button" value="Filter" onclick="filter()">
                </div>
            </div>
            <div id="circosPlot"></div>
        </div>
    </div>
</section>

<r:script>
    console.log('Hello, drawing Circos plot now');


// Circos plot stuff
    var width = document.getElementById('circosDiv').offsetWidth || 600,
        radius = width / 2;

    const config = {
        container: '#circosPlot',
        width: width,
        height: width
    }

// Karyotype thing stuff
    const karyoConfig = {
        innerRadius: radius - 60,
        outerRadius: radius - 50,
        labels: {display: true},
        ticks: {display: true}
    };


// Animation stuff
    const speed = 1000;
    const tau = 2 * Math.PI; // http://tauday.com/tau-manifesto;
    //const filterSet = ['chr10', 'chr13', 'chr17'];


// Seq Variants
    var stuff = ${seqSample.seqVariants as grails.converters.JSON}

    var detected = {};
    var snvs = stuff.map(function(d){
        detected["chr"+d.chr] = true;


        return {
            block_id: "chr"+d.chr,
            start: parseInt(d.pos),
            end: parseInt(d.pos)+1
        }

    });
    var filterSet = Object.keys(detected);

    d3.select("#chromo-pickers")
        .selectAll("p")
        .data(GRCh37)
        .enter()
        .append('p')
        //.text((d) => d.id)
        .each(function(d){
            var that = d3.select(this);
            that.append("input").attrs({
                checked: 'checked',
                type: 'checkbox',
                id: 'check-'+d.id
            });
            that.append('label')
                .text(d.id)
                .attr("for", (d) => 'check-'+d.id)
                .style("background", (d) => d.color);
        }).on('mousedown', function(){
            console.log("CLICK!");
            clickTickbox();
        });

    var circos = new Circos( config )
        .layout( GRCh37, karyoConfig )
        .highlight('cytobands', cytobands, {
            innerRadius: radius - 60,
            outerRadius: radius - 50,
            opacity: 0.5,
            color: function (d) {
                return gieStainColor[d.gieStain]
            }
        })
        .highlight('snvs', snvs, {
            innerRadius: radius - 100,
            outerRadius: radius - 70,
            strokeWidth: 1,
            strokeColor: 'black',
            tooltipContent: function(d){
                return d.name
            }
        });

    const karyoArc = d3.arc()
        .innerRadius(circos._layout.conf.innerRadius)
        .outerRadius(circos._layout.conf.outerRadius)
        .cornerRadius(circos._layout.conf.cornerRadius)
        .startAngle((d) => d.start)
        .endAngle((d) => d.end);

    const karyoLabelArcRadius = circos._layout.conf.innerRadius + circos._layout.conf.labels.radialOffset;
    const karyoLabelArc = d3.arc()
        .innerRadius(karyoLabelArcRadius)
        .outerRadius(karyoLabelArcRadius)
        .startAngle((d) => d.start)
        .endAngle((d) => d.end);





//var blah = null;


    var updateCircos = function (layout) {
console.log("Updating circos plot!");

    var finalKeys = layout.reduce((acc, val) => acc.concat(val.len > 0 ? val.id : []), []),
        originalKeys = Object.keys(circos._layout.blocks).reduce((acc, val) => acc.concat(circos._layout.blocks[val].len > 0 ? val : []), []),
        newKeys = finalKeys.reduce( (acc, val) => acc.concat(originalKeys.indexOf(val) < 0 ? val : []), []);

    circos.layout( layout, karyoConfig );

    var filteredCytobands = cytobands;

    filteredCytobands = [];
    cytobands.forEach(function(d){
        if( finalKeys.indexOf(d.block_id) >= 0 ) {
            var obj = {};
            Object.keys(d).forEach((key) => obj[key] = d[key]);
            filteredCytobands.push(obj);
        }
    });

        circos.highlight('cytobands', filteredCytobands, {
            innerRadius: radius - 60,
            outerRadius: radius - 50,
            opacity: 0.5,
            color: function (d) {
                return gieStainColor[d.gieStain]
            }
        })

        .highlight('snvs', snvs, {
            innerRadius: radius - 100,
            outerRadius: radius - 70,
            strokeWidth: 1,
            strokeColor: 'black',
            tooltipContent: function(d){
                return d.name
            }
        })
        .render();





        newKeys.forEach(function(id){
            d3.selectAll("#arc-label"+id+" text")
                .style("opacity",0)
                .transition()
                .duration(speed)
                .style("opacity",1);
        });


        d3.select(".cytobands")
            .style("opacity",0)
            .transition()
            .duration(speed)
            .style("opacity",0.5);

        //
        //

    };


    updateCircos( GRCh37, false );

    function filter(){
        $("#chromo-pickers p input[type='checkbox']").prop('checked', false);
        filterSet.forEach(function(d){
            $("#check-"+d).prop('checked', true);
            console.log(d);
        })
        animateToNewLayout(tickBoxes());
    }

    function reset(){
        $("#chromo-pickers p input[type='checkbox']").prop('checked', true);
            animateToNewLayout(tickBoxes());
    }

    var timer = null;
    function clickTickbox(){
        console.log("queueing the thing...");
        window.clearTimeout(timer);
        timer = window.setTimeout(function(){
            console.log("DOING THE THING!");
            animateToNewLayout(tickBoxes());
        }, 500);
    }

    function tickBoxes(){
        var layout = [];
        var keys = [];

        d3.selectAll("#chromo-pickers input[checked=checked]:checked")
            .each(function(d){
                keys.push(d.id);
            });

        GRCh37.forEach(function(d){
            var obj = {}
            Object.keys(d).forEach((key) => obj[key] = d[key]);
            if( keys.indexOf(d.id) >= 0 ) {
                layout.push(obj);
            } else {
                obj.len = 0;
                layout.push(obj);
            }
        });

        return layout;
    }

    function animateToNewLayout( newLayout ){

        var newCircos = new Circos(config).layout( newLayout, karyoConfig );
        var lastPos = 0;
        var counter = 0;

        var originalKeys = Object.keys(circos._layout.blocks);

        originalKeys.forEach(function(id){
            var block = circos._layout.blocks[id],
                newBlock = newCircos._layout.blocks[id];

            var coords = {
                start: {
                    begin: block.start,
                    finish: newBlock.start,
                },
                end: {
                    begin: block.end,
                    finish: newBlock.end
                }
            }

            block.starter = d3.interpolate(coords.start.begin, coords.start.finish);
            block.finisher = d3.interpolate(coords.end.begin, coords.end.finish);

            d3.select("#"+id)
                .transition()
                .duration(speed * 2)
                .attrTween("d", arcTween(karyoArc))
                .on('start', () => counter++)
                .on('end', () => {if(!--counter) updateCircos(newLayout)});

            var label = d3.select("#arc-label"+id);

            if(newBlock.len > 0 && block.len > 0){
                d3.select("#arc-label"+id)
                    .transition()
                    .duration(speed * 2)
                    .attrTween("d", arcTween(karyoLabelArc))
                    .on('start', () => counter++)
                    .on('end', () => {if(!--counter) updateCircos(newLayout)});
            } else {
                d3.select("#arc-label"+id+"+text")
                    .style("opacity", 1)
                    .transition()
                    .style("opacity", 0);
            }


        });



        d3.select(".cytobands")
            .transition()
            .style("opacity",0)
            .remove();

        //d3.selectAll(".snvs g.block path")
        //    .transition()
        //    .duration(speed * 2)
        //    .attrTween("d", arcTween(circos.tracks.snvs.conf.arc));

        //Object.keys[circos.tracks].forEach(function(track){
        //    var myArc = circos.tracks[track].conf.arc;
        //
        //    d3.selectAll("."+track+" g.block path")
        //        .transition()
        //        .duration(speed * 2)
        //        .attrTween("d", arcTween(myArc, "track parent or something"));
        //});

    }



  //
  //d3.interval(function() {
  //  d3.selectAll(".cs-layout g path").transition()
  //      .duration(1000)
  //      .attrTween("d", arcTween(Math.random() * tau));
  //}, 2000);


  //

  function arcTween(arc) {
    // The function passed to attrTween is invoked for each selected element when
    // the transition starts, and for each element returns the interpolator to use
    // over the course of transition. This function is thus responsible for
    // determining the starting angle of the transition (which is pulled from the
    // element’s bound datum, d.endAngle), and the ending angle (simply the
    // newAngle argument to the enclosing function).
    return function(d) {

      // To interpolate between the two angles, we use the default d3.interpolate.
      // (Internally, this maps to d3.interpolateNumber, since both of the
      // arguments to d3.interpolate are numbers.) The returned function takes a
      // single argument t and returns a number between the starting angle and the
      // ending angle. When t = 0, it returns d.endAngle; when t = 1, it returns
      // newAngle; and for 0 < t < 1 it returns an angle in-between.

        var id = d.id || d.block_id;
        var starter = circos._layout.blocks[id].starter;
        var ender = circos._layout.blocks[id].finisher;

      // The return value of the attrTween is also a function: the function that
      // we want to run for each tick of the transition. Because we used
      // attrTween("d"), the return value of this last function will be set to the
      // "d" attribute at every tick. (It’s also possible to use transition.tween
      // to run arbitrary code for every tick, say if you want to set multiple
      // attributes from a single function.) The argument t ranges from 0, at the
      // start of the transition, to 1, at the end.
      return function(t) {

        // Calculate the current arc angle based on the transition time, t. Since
        // the t for the transition and the t for the interpolate both range from
        // 0 to 1, we can pass t directly to the interpolator.
        //
        // Note that the interpolated angle is written into the element’s bound
        // data object! This is important: it means that if the transition were
        // interrupted, the data bound to the element would still be consistent
        // with its appearance. Whenever we start a new arc transition, the
        // correct starting angle can be inferred from the data.
        d.start = starter(t);
        d.end = ender(t);

        // Lastly, compute the arc path given the updated data! In effect, this
        // transition uses data-space interpolation: the data is interpolated
        // (that is, the end angle) rather than the path string itself.
        // Interpolating the angles in polar coordinates, rather than the raw path
        // string, produces valid intermediate arcs during the transition.
        return arc(d);
      };
    };
  }

function hideAll(){



    var allBlocks = d3.selectAll(".cs-layout g path:first-child")
        .transition()
        .duration((d,i) => (i-1) * speed)
        .transition()
        .duration(speed)
        .attrTween("d", arcTween("close"));
}


    ////d3.interval(function() {
    //    d3.selectAll(".cs-layout g path").transition()
    //        .duration(750)
    //        .attrTween("d", arcTween(Math.random() * tau));
    ////}, 1500);

function showAll(){
    var allBlocks = d3.selectAll(".cs-layout g path:first-child")
        .transition()
        .duration((d,i) => (i-1) * speed)
        .transition()
        .duration(speed)
        .attrTween("d", arcTween("open"));
}

function refreshCircos(){

}

d3.queue()
    .defer(d3.tsv, PathOS.application+"/circos/MP_FLD_GERMLINE_PRODUCTION.tsv")
    .await(roflmao);


function roflmao(error, data){
//console.log(data);


    var stuff = data.map(function(row){
        return {
            block_id: row.block,
            start: parseInt(row.pos),
            end: parseInt(row.pos) + 1
        }
    });

    circos
    .highlight('stuff', stuff, {
        innerRadius: radius - 150,
        outerRadius: radius - 100,
        strokeWidth: 1,
        strokeColor: 'red'
    })
    .render();

}


</r:script>





















































