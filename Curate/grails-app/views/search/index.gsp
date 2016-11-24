%{--
  - Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
  -
  - Organisation: Peter MacCallum Cancer Centre
  - Author: David Ma
  - Date: 14-April-2016
  -
  --}%

<%@ page import="grails.converters.JSON; org.petermac.pathos.curate.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">

    <title><g:if test="${params.q}">${params.q} - </g:if>PathOS Search</title>

    <parameter name="hotfix" value="off" />
<section id="search-page">
    <div class="container">
        <div class="row">
            <div id="search-results-heading" class="col-xs-12">
                <div id="searchbar"></div>
                <p class="text-center">We are showing <span id="numberShowing">0</span> of <span id="numberOfResults">0</span> results</p>
                <p id="search_time"></p>
            </div>
            <div id="loading_box" class="col-sm-offset-3 col-sm-6 col-xs-offset-1 col-xs-10 fb-box">
                <h1><span id="loaded_data">0</span> of 4 result sets loaded<br><span id="average_time"></span></h1>
                <img class="loading_logo" src="/PathOS/dist/img/pathos_logo_animated.svg">
            </div>
        </div>

        <div class="row text-center">
            <div class="col-xs-8" id="results">

            </div>
            <div class="col-xs-4" style="min-height: 1440px;">
                <div class="fb-box" id="legend" style="display: none;">
                    <h1>Legend</h1>
                    <ul id="legend_options">
                        <li><i id="" class="fa fa-cog" aria-hidden="true"></i></li>
                    </ul>

                    <div id="legend-boxes"><table></table></div>
                    <br>
                </div>
            </div>
        </div>
    </div>
</section>


<script>



$(document).ready(function(){

    $("#searchHeader").attr("id","searchResultsPage").detach().appendTo("#searchbar");

// This function keeps the legend box on the page, in the right position.
// Note that we have 2 magic numbers here, 70 and 105
// Fix if possible
    $(window).scroll(function() {
        if ($(window).scrollTop() > 70) {
            $("#searchResultsPage").attr("id", "searchHeader").detach().appendTo("#top-middle");
        } else {
            $("#searchHeader").attr("id", "searchResultsPage").detach().appendTo("#searchbar");
        }

        if ($(window).scrollTop() > $("#results").offset().top - 40 && $('#legend').height() < $(window).height() - 50) {
            d3.select("#legend")
                .style("position", "fixed")
                .style("top", "45px");
        } else {
            d3.select("#legend")
                .style("position", "absolute")
                .style("top", "0px");
        }
    });
});







/*
 * PathOS Search
 * Author:  David Ma
 * Date:    13-July-2016
 *
 * We run search on this page asynchronously
 *
 * When the page loads:
 * 1) get the q param from the URL
 * 2) launch 4 searches to the Search controller via ajax
 * 3) As the data comes in, update:
 *       - The loading message
 *       - The legend
 *       - Result boxes.
 *
 *
 *
 */
var timestamp = Date.now();

$.ajax('/PathOS/search/getAverageTime?'+$.param({q:PathOS.params().q}), {success: function(d){
    if(d > 0) {
        var speed = d3.precisionRound(d/1000, 1);
        d3.select("#average_time").text("This search normally takes "+speed+" seconds. ");
    }
}});

$.ajax('/PathOS/search/quickSearch?q='+PathOS.params().q, {success: function(d){
    update_loaded();
    handleSearchableData(d);
}});

data = null;
$.ajax('/PathOS/search/search?q='+PathOS.params().q, {success: function(d){
    data = d;
    update_loaded();
    handleSearchableData(d);
}});

var searchDepth = 10;
console.log("Requesting Deeper Data... "+searchDepth);
$.ajax('/PathOS/Search/deepSearch?o=10&q='+PathOS.params().q, {success: handleDeepData});

$.ajax('/PathOS/Search/svTags?q='+PathOS.params().q, {success: function(d){
    update_loaded();
    handleSvData(d);
}});

$.ajax('/PathOS/Search/svExact?q='+PathOS.params().q, {success: function(d){
    update_loaded();
    handleSvData(d);
}});

var seqvars = {},
    svDepth = 10,
    rawSVdata = null;

function handleSvData(data){
    rawSVdata = data;
    if(data.length > 0) {
        data.forEach(function(d){
            seqvars[d.id] = d;
        });

        var resultData = [];
        var tags = {};

        Object.keys(seqvars).sort().slice(0, svDepth).reverse().forEach(function(seqvar){
            var sv = seqvars[seqvar];

            resultData.push({
                gormid: seqvar,
                id: 'seqVariant-'+seqvar,
                score: 1,
                title: sv.gene+':'+sv.hgvsc,
                data: sv,
                link: '/PathOS/seqVariant/show/',
                type: 'seqVariant',
                typetitle: 'Sequenced Variant'
            });
            sv.tags.forEach(function(tag){
                tags[tag.id] = true;
            });
        });
        buildResults(resultData);

        buildLegend([{
            count: resultData.length,
            key: 'seqVariant',
            link: '/PathOS/seqVariant/list',
            tags: tags,
            title: 'Sequenced Variants',
            total: data.length
        }]);
    }
}

function handleSearchableData(data){
    d3.select("#legend").style("display", "block");

    var legendData = [];
    var resultData = [];

    var tables = Object.keys(data);

    tables.forEach(function(key){
        if(data[key].total && data[key].total > 0) {

            var tags = {};

            data[key].results.forEach(function(d, i){
                resultData.push({
                    type: key,
                    gormid: data[key].results[i].id,
                    id: key+"-"+data[key].results[i].id,
                    title: data[key].extra[i].string,
                    typetitle: data[key].name,
                    link: data[key].link,
                    score: data[key].scores[i],
                    result: data[key].results[i],
                    data: data[key].data[i],
                    extra: data[key].extra[i],
                    tags: data[key].tags[i]
                });

                data[key].tags[i].forEach(function(d){
                    tags[d.label] = d;
                });
            });

            var count = data[key].results.length;

            if(data[key].offset < data[key].total) {
                count += data[key].offset;
            } else {
                count = data[key].total;
            }

            legendData.push({
                key: key,
                title: data[key].name,
                count: count,
                total: data[key].total,
                link: data[key].table_link,
                tags: tags
            });
        }
    });


    resultData.sort(function(a, b){
        return b.score - a.score;
    });

    buildResults(resultData);
    buildLegend(legendData);


}

var deepData = false;
function handleDeepData(data){
    console.log("Deep data loaded.");
    searchDepth += 10;
    deepData = data;
}

function buildLegend(data){

    d3.select("#legend-boxes table")
        .selectAll("tr")
        .data(data, function(d){return "legend-"+d.key;})
            .each(function(d){

                var row = d3.select(this);
                var showing = $(".resultbox."+d.key).length;
                row.select('.result-text .legend_showing').text(showing);
                row.select('.result-text .legend_total').text(d.total);

                var legendTagDiv = row.select(".legendTagDiv")
                legendTagDiv.selectAll('.tagdiv')
                        .data(Object.keys(d.tags))
                        .enter()
                        .append('div').classed("dummydiv", true)
                        .each(function(tag){
                            d3.select(".dummydiv").remove();
                            legendTagDiv.style("display", 'block');
                            if(d.key == "seqVariant") {
                                PathOS.tags.drawTagById(legendTagDiv, tag);
                            } else {
                                PathOS.tags.drawTag(legendTagDiv, d.tags[tag]);
                            }
                        });
            })
        .enter()
            .append("tr")
            .each(function(d){
                var row = d3.select(this)
                            .classed('legend-box fb-box ' + d.key, true)
                            .attr('id', 'legend-' + d.key);

                var left = row.append('td'),
                    right = row.append('td');

                left.append('p')
                    .append('a')
                        .attr('href', d.link)
                        .text(d.title);

                right.append('p')
                    .append('a')
                        .attr('href', '#none')
                        .on('click', function(d){
                            if(d.key == 'seqVariant') {
                                svDepth += 10;
                                handleSvData(rawSVdata);
                            } else {
                                handleSearchableData(deepData);
                                console.log("Requesting Deeper Data... "+searchDepth);
                                $.ajax('/PathOS/Search/deepSearch?o='+searchDepth+'&q='+PathOS.params().q, {success: handleDeepData});
                            }

                            d3.selectAll(".legend-box .hider").text("[show]");
                            d3.selectAll(".legend-box").classed("hide", true);
                            d3.selectAll(".resultbox").classed("hidden", true);
                            d3.selectAll(".legend-box .legend_showing").text(0);

                            legendShow(d.key)
                        })
                        .classed("result-text", true)
                        .html('Results: <span class="legend_showing">'+ d.count +'</span>/<span class="legend_total">'+ d.total+'</span>');


                left.append('p')
                        .attr('id', 'tags-'+ d.key)
                        .text('Tags:');
                var legendTagDiv = left.append('div')
                        .classed('legendTagDiv', true)
                        .style("display", 'none');



                legendTagDiv.selectAll('.tagdiv')
                    .data(Object.keys(d.tags))
                    .enter()
                    .append('div').classed("dummydiv", true)
                    .each(function(tag){
                        d3.select(".dummydiv").remove();
                        legendTagDiv.style("display", 'block');
                        if(d.key == "seqVariant") {
                            PathOS.tags.drawTagById(legendTagDiv, tag);
                        } else {
                            PathOS.tags.drawTag(legendTagDiv, d.tags[tag]);
                        }
                    });

                right.append("span").append('a')
                    .attr("id", "legend-show-"+d.key)
                    .attr("href", "#none")
                    .classed("hider", true)
                    .text("[hide]").on('click',function(d){
                        $(".legend-box."+d.key).toggleClass("hide");

                        if (d3.select(".legend-box."+d.key).classed("hide")) {
                            legendHide(d.key);
                        } else {
                            legendShow(d.key);
                        }
                    });
            });
    updateCounts();

    function legendShow(key){
        d3.selectAll(".resultbox."+key).classed("hidden", false);
        var count = d3.select(".legend-box."+key).classed("hide", false).datum().count;
        d3.select(".legend-box."+key+" .legend_showing").text(count);
        $("#legend-show-"+key).text("[hide]");
        updateCounts();
    }

    function legendHide(key){
        d3.selectAll(".resultbox."+key).classed("hidden", true);
        d3.select(".legend-box."+key).classed("hide", true)
            .select(".legend_showing").text(0);
        $("#legend-show-"+key).text("[show]");
        updateCounts();
    }


};


function updateCounts(){
    var numberShowing = $(".resultbox:not(.hidden)").length,
        numberOfResults = 0;

    d3.selectAll("#legend-boxes table tr").each(function(d){
        numberOfResults += d.total;
        d3.select("#numberShowing").text(numberShowing);
        d3.select("#numberOfResults").text(numberOfResults);
    });
}


function buildResults(data){
    d3.select("#results")
        .selectAll("div")
        .data(data, function(d){return d.id;})
        .enter()
        .insert("div", ".seqVariant").attr("id", function(d){return d.id;})
        .each(function(d, i){
            var div = d3.select("#"+ d.id)
                .classed("resultbox fb-box "+ d.type, true);
            div.append('span').classed('anchor', true).attr('id', 'anchor-'+ d.id);

            var header = div.append('div').classed('row', true);

            header.append('div').classed('col-xs-8', true)
                .append('h1').append('a').attr('href', d.link + d.gormid).text(d.title).classed('title', true);

            header.append('div').classed('col-xs-4', true)
                    .append('h1').style('text-align', 'right').append('a').attr('href', d.link+ d.gormid).text(d.typetitle);

            if( Object.keys(drawResultBox).indexOf(d.type) >= 0 ){
                drawResultBox[d.type](div, d);
            }

            var box = div.append('div')
                    .classed("resultTagBox", true);

            if(d.type == "seqVariant") {
                d.data.tags.forEach(function(tag) {
                    PathOS.tags.drawTagById(box, tag.id, false);
                });
            } else if(d.tags && d.tags.length > 0 ){
                d.tags.forEach(function(tag){
                    PathOS.tags.drawTag(box, tag);
                });
            }

            div.append("p").classed("fade dev", true).text(d.type+" | Score: "+ d.score);

            if($(this)[0].scrollHeight > 200) {
                div.classed("expandable", true);
                div.append("div")
                    .attr("id", "expando-"+i)
                    .classed("expando", true)
                    .datum(d).on("click", expandResultBox)
                .append("p")
                    .style("color","#4478ac")
                    .style("margin", "auto")
                    .style("margin-top", "10px")
                    .style("width", "1%")
                .append("i").classed("fa fa-chevron-down fa-lg", true);
            }

        });
    applyOptions();
    updateCounts();
};

drawResultBox = {
    seqVariant: function(div, d){
        div.select(".col-xs-8")
            .append("h3")
            .append("a")
            .attr("href", "/PathOS/seqVariant/svlist/"+ d.data.seqSample.id)
            .text(d.data.sampleName);

        var row = div.append("row").classed("row", true);
        var block = row.append("div").classed("col-xs-6 block", true);

        block.append("h4").text("HGVS identifiers");
        var list = block.append("ul");
        list.append("li").append("p").text(d.data.hgvsg);
        list.append("li").append("p").text(d.data.hgvsc);
        list.append("li").append("p").text(d.data.hgvsp);

        var block = row.append("div").classed("col-xs-6 block", true);

        block.append('p').text("Variant Caller: "+ d.data.varcaller);
        block.append('p').text("Amplicons: "+ d.data.amps);
        block.append('p').text("Amplicon Count: "+ d.data.numamps);

        if(d.data.pubmed){
            block.append('a')
                .attr("href", "/PathOS/Pubmed?pmid="+d.data.pubmed)
                .text("Pubmed: "+ d.data.pubmed);
        }

    },
    pubmed: function(div, d){
        var authors = "",
                date = "";
        if(d.data.authors) {
            authors = d.data.authors.split(",").join(", ");

        }
        if(d.data.date) {
            date = " [" + d.data.date.split("-")[0] + "]";
        }

        div.append("h4")
            .datum(authors + date)
            .html(highlight);

        var citation = d.data.journal + "; " + d.data.volume + " (" + d.data.issue +") " + d.data.pages;
        if(d.data.doi != "null") {
            citation +=  "; doi: <a href='http://dx.doi.org/"+d.data.doi+"'>"+d.data.doi+"<a>";
        }

        div.append("h5").html(citation);

        if(d.data.abstrct) {
            div.append("p").datum(d.data.abstrct).html(highlight);
        }
    },
    seqSample: function(div, d){
        div.select(".col-xs-8")
                .append("h3")
                .append("a")
                .attr("href", "/PathOS/seqrun/show/"+d.data.seqrun.seqrun)
                .text(d.data.seqrun.seqrun);
        var data = d.data;
        var row = div.append("row").classed("row", true);
        var block = row.append("div").classed("col-xs-8 block", true);


        PathOS.buildBlock(block, [
            {
                title: 'Patient Sample',
                words: d.data.sampleName,
                link: d.data.patSample ? '/PathOS/patSample/show/'+d.data.patSample.id : ''
            },
            {
                title: 'First Reviewed Date',
                words: d.data.firstReviewedDate
            },
            {
                title: 'Final Reviewed Date',
                words: d.data.finalReviewedDate
            },
            {
                title: 'Panel',
                words: d.data.panel.manifest
            },
            {
                title: 'Analysis',
                words: d.data.analysis
            },
            {
                title: 'Total Variants',
                words: d.extra.seqVariants.length
            }
        ]);

        var block = row.append("div").classed("col-xs-4 block", true);

        PathOS.printQC({
            div: block,
            authorised: d.data.authorisedQc,
            passfailFlag: d.data.passfailFlag
        });

        if(d.extra.seqVariants.length > 0) {
            var box = div.append('div')
                .style("padding", "10px")
                .classed('block fb-box', true)

            box.append("h3").text("Curated Variants:")

            var table = box.append('table')
                    .style("width", "600px");

            var head = table.append('thead');
            head.append('th').text('Gene');
            head.append('th').text('Variant');
            head.append('th').text('Pm Class');
            var body = table.append('tbody');

            var curVariants = []
            d.extra.seqVariants.forEach(function(variant) {
                if (variant.curated) {
                    curVariants.push(variant);
                }
            });
            curVariants.sort(function(a,b){
                if(a.gene < b.gene) return -1;
                if(a.gene > b.gene) return 1;
                return 0;
            }).forEach(function(d){
                var row = body.append('tr').datum(d);

                row.append('td')
                .append('a')
                    .attr("href", "http://www.ncbi.nlm.nih.gov/nuccore/"+ d.hgvsc.split(':')[0])
                    .text(d.gene);

                row.append('td')
                    .append('a')
                    .attr("href", '/PathOS/curVariant/show/'+d.hgvsg)
                    .text(d.hgvsg);

                row.append('td').append('span').classed(d.curated.split(':')[0], true).text(d.curated);
            });

        } else {
            div.append('p').text("No variants found");
        }
    },
    curVariant: function(div, d){

        div.select(".title").text(d.data.gene +":"+ d.data.variant);


        var row = div.append("row").classed("row", true);
        var block = row.append("div").classed("col-xs-6 block", true);

        block.append("h4").text("HGVS identifiers");
        var list = block.append("ul");
        list.append("li").append("p").text(d.data.hgvsg);
        list.append("li").append("p").text(d.data.hgvsc);
        list.append("li").append("p").text(d.data.hgvsp);

        var block = row.append("div").classed("col-xs-6 block", true);
        var pmClass     = d.data.pmClass     ? d.data.pmClass.split(":")[0] : "Unclassified";
        block.append('p').text("Classification: ")
                .append("span").classed(pmClass, true).text(d.data.pmClass || pmClass);


        div.classed(pmClass, true)

        block.append("p").text("Number of Sequenced Samples with this Curated Variant in PathOS: " + d.extra.seqVariants.length);
//      add clinical context info here?

        var row = div.append("row").classed("row", true)
            .append("div").classed("col-xs-12", true);
        var evidence = [];

        Object.keys(PathOS.evidence).forEach(function(e){
            if(d.data.evidence[e]){
                evidence.push(PathOS.evidence[e]);
            }
        });
        var link = row.append('a').attr("href", '/PathOS/evidence/edit/'+ d.result.id);

        if(evidence.length > 0) {
            link.append('h3').text("Evidence");
            link.append('p').text(evidence.join(", "));
            if(d.data.evidence.justification) {
                row.append('p').datum(d.data.evidence.justification).html(highlight);
            }
        } else {
            link.append('h3').text("No evidence added yet")
        }




        var row = div.append("row").classed("row", true)
                .append("div").classed("col-xs-12", true)

        if(d.data.reportDesc) {
            row.append('a')
                .attr("href", '/PathOS/curVariant/edit/'+ d.result.id)
                .append('h3')
                .text("Report");
            row.append('p').datum(d.data.reportDesc).html(highlight);
        } else {
            row.append('a')
                .attr("href", '/PathOS/curVariant/edit/'+ d.result.id)
            .append('h3')
                .text("No report added yet");
        }
    },
    patSample: function(div, d){
        var row = div.append("row").classed("row", true);
        var block = row.append("div").classed("col-xs-6 block bold", true);
        block.append("p");

        <sec:ifAnyGranted roles="ROLE_DEV">
        block.append("p").text("Patient Name: ").append('a').attr("href", "/PathOS/patient/show/"+d.extra.patient.id).text("######");
        </sec:ifAnyGranted>


        <sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_CURATOR,ROLE_LAB,ROLE_VIEWER,ROLE_EXPERT">
        block.append("p").text("Patient Name: ").append('a').attr("href", "/PathOS/patient/show/"+d.extra.patient.id).text(d.extra.patient.fullName.split(",").join(", "));
        </sec:ifAnyGranted>


        block.append("p").text("Date of Birth: " + d.extra.dob);
        block.append("p").text("Sex: " + d.extra.patient.sex);
        block.append("p").text("URN: " + d.extra.patient.urn);


        var block = row.append("div").classed("col-xs-6 block", true);
        block.append("p").text("Requester: " + d.data.requester.split(",").join(", "));
        block.append("p").text("Collect Date: " + d.extra.collectDate);
        block.append("p").text("Received Date: " + d.extra.rcvdDate);
        block.append("p").text("Request Date: " + d.extra.requestDate);




        if(d.extra.seqSamples.length > 0) {


            var seqsam = d.extra.seqSamples

            seqsam.sort(function(a, b){
                var c = new Date(a.seqrun.runDate),
                    d = new Date(b.seqrun.runDate);
                return c > d ? -1 : c < d ? 1 : 0;
            });

            var box = div.append('div').classed('fb-box', true);

            var table = box.append('table');

            var header = table.append('thead').append('tr');
            header.append('th').text('Sample Name');
            header.append('th').text('Sequenced Run');
            header.append('th').text('Date');

            try {
                seqsam.forEach(function (d, i) {
                    var row = table.append('tr')

                    row.append('td')
                        .append('a')
                        .attr("href", "/PathOS/seqVariant/svlist/" + d.id)
                        .text(d.name);

                    row.append('td')
                        .append('a')
                        .attr("href", "/PathOS/seqrun/show?id=" + d.seqrun.id)
                        .text(d.seqrun.seqrun);

                    row.append('td').append('p').text(d.seqrun.runDate.split("T")[0])

                    if(i >= 9 && seqsam.length > 10) {
                        throw moreThan10
                    }
                })
            } catch(e) {
                var row = table.append('tr')
                row.append('td').append('p').text("11+")
                row.append('td').append('a').attr('href', d.link).text("See more results...");
            }

        } else {
            div.append('p').text("No runs found for this sample")
        }

        if(d.extra.patAssays.length > 0) {
            div.append("p").text("Patient Assays:")
            var list = div.append('ul')
            d.extra.patAssays.forEach(function(d){
                list.append('li')
                    .append('a')
                    .attr("href", "/PathOS/patAssay/show/"+d.id)
                    .text(d.name);
            })
        } else {
            div.append('p').text("No Patient Assays found")
        }
    },
    seqrun: function(div, d){
        var row = div.append("row").classed("row", true);
        var block = row.append("div").classed("col-xs-8 block", true);


        PathOS.buildBlock(block,[
            {
                title: 'Date',
                words: PathOS.date(d.data.runDate, 'full')
            },
            {
                title: 'Library',
                words: d.data.library
            },
            {
                title: 'Panel',
                words: d.data.panelList
            },
            {
                title: 'Platform',
                words: d.data.platform
            },
            {
                title: 'Scanner',
                words: d.data.scanner
            }
        ]);


        var block = row.append("div").classed("col-xs-4 block", true);

        PathOS.printQC({
            div: block,
            authorised: d.data.authorisedId,
            passfailFlag: d.data.passfailFlag
        });

        if(d.extra.seqSamples.length > 0) {

            var box = div.append('div')
                .style("padding", "10px")
                .classed('block fb-box', true);

            box.append("h3").text(d.extra.seqSamples.length + " Sequenced Samples:")

            var table = box.append('table')
                    .style("width", "600px");

            var head = table.append('thead');
            head.append('th').text('Sample');
            head.append('th').text('Date');
            head.append('th').text('Passed');
            head.append('th').text('Authorised');
            var body = table.append('tbody');


            d.extra.seqSamples.sort(function(a, b){
                if(a.name < b.name) return -1;
                if(a.name > b.name) return 1;
                return 0;
            }).forEach(function(entry){

                var row = body.append('tr').datum(entry);

                row.append('td')
                    .append('a')
                    .attr("href", '/PathOS/seqVariant/svlist/'+ d.title +'/'+ entry.name)
                    .text(entry.name);


                row.append('td').append('p').text(PathOS.date(entry.seqrun.runDate, 'full'));

                row.append('td').append('p').text(entry.seqrun.passfailFlag);
                row.append('td').append('p').text(entry.seqrun.authorisedFlag);

            })

        } else {
            div.append('p').text("No variants found");
        }
    },
    tag: function(div, d) {
        var row = div.append("row").classed("row", true);
        var block = row.append("div").classed("col-xs-8 block", true);


        PathOS.buildBlock(block,[
            {
                title: 'Description',
                words: d.data.description
            },
            {
                title: 'Tag Created By',
                words: d.data.createdBy.displayName
            }
        ]);
    }
};




function addLegendOption(option, flag, label, defaultState) {
    var li = d3.select("#legend_options").append('li').attr("class", 'option')
            .datum({
                option: option,
                flag: flag
            });

    li.append("input")
        .attr("id", "legendOption-" + flag)
        .attr("type", "checkbox")
    .on('change', function(){
        var state = document.getElementById("legendOption-" + flag).checked;
        d3.selectAll("."+option).classed(flag, state);
        localStorage["lo-"+flag] = state ? 1 : 0;
    });

    var state = parseInt(localStorage["lo-"+flag] || defaultState || 0);
    document.getElementById("legendOption-" + flag).checked = state;


    li.append("label")
        .attr("for", "legendOption-" + flag)
        .text(label);
}

addLegendOption("expandable", "expanded", "Expand All", 0);
addLegendOption("dev", "show", "Show Scores", 0);
addLegendOption("term", "highlight", "Highlight Terms", 0);
addLegendOption("term", "bold", "Bold Terms", 1);

function applyOptions(){
    d3.selectAll("#legend_options li.option").data().forEach(function(d){
        var state = document.getElementById("legendOption-" + d.flag).checked
        d3.selectAll("." + d.option).classed(d.flag, state);
    });
}


// This block handles all the expando stuff........
    function expandResultBox() {
        var d = d3.select(this).datum();
        d3.select("#" + d.id + ".expandable").classed("expanded", true);
    }


    function update_loaded() {
        var loaded = $("#loaded_data").html();
        if (++loaded < 4) {
            $("#loaded_data").html(loaded);
        } else if (loaded == 4) {

            var params = {
                s: Date.now() - timestamp,
                q: PathOS.params().q,
                t: timestamp,
                u: "<authdetails:displayName/>"
            }

            $.ajax('/PathOS/search/putTime?'+ $.param(params));

            d3.select("#search_time").text("Search took " + d3.precisionRound(params.s / 1000, 1) + " seconds");
            $("#loading_box").remove();
        }
    }


// This is for the search term highlighting.
    var terms = PathOS.params().q ? PathOS.params().q.split("+") : [];

    function highlight(d) {
        var html = d;
        terms.forEach(function (term) {
// simple version:
//        html = html.split(term).join("<span class='highlight highlightable'>" + term + "</span>");
// this version is not case sensitive...


// Watch out!!! This is 15 lines of code to do something that 1 line of code can do...
// All because you wanted it to be case insensitive...
            var len = term.length;
            var t = term.toLowerCase();
            var lower = html.toLowerCase();
            var index = lower.split(t);
            var arr = [];
            var pieces = [];
            var count = 0;

            index.forEach(function (fragment) {
                var a = count,
                        b = count + fragment.length;
                arr.push(html.slice(a, b));
                pieces.push(html.slice(b, b + len));

                count = count + fragment.length + len;
            });
            html = "";

// This part is so that the highlighting doesn't modify the case of the results.
            arr.forEach(function (piece, i) {
                if (pieces[i]) {
                    html += piece + "<span class='bold term'>" + pieces[i] + "</span>";
                } else {
                    html += piece;
                }
            });
        });
        return html;
    }




</script>
</body>
</html>




































































