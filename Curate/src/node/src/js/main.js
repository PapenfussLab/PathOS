/*jshint esversion: 6 */
var PathOS = PathOS || {};

// Todo: If merging with Traceback branch, don't forget to use context instead of hardcoding /PathOS
PathOS.version = "PathOS.js build: 21st of August 2018";

PathOS.application = application || "/PathOS";

// Simple way for PathOS to store data
// Currently uses localStorage but could be upgraded in future.
// DKGM 28-06-2016
PathOS.data = {
	// Provide a key and a default object
	load: function(key, obj) {
		var result = obj || {};
		if (localStorage[`PathOS${PathOS.application}-${key}`]) {
			result = JSON.parse(localStorage[`PathOS${PathOS.application}-${key}`]);
		}
		return result;
	},
	// Save the object with the key
	save: function(key, obj) {
		localStorage[`PathOS${PathOS.application}-${key}`] = JSON.stringify(obj);
	},
	// Clear the key's data
	clear: function(key) {
		delete localStorage[`PathOS${PathOS.application}-${key}`];
	},
	clean: function() {
		delete localStorage[`PathOS${PathOS.application}-history`];
		delete localStorage[`PathOS${PathOS.application}-modules`];
	}
};

PathOS.criteria = {
    acmgClasses: {
        0: "Unclassified",
        1: "C1: Not pathogenic",
        2: "C2: Unlikely pathogenic",
        3: "C3: Unknown pathogenicity (Level A)",
        4: "C3: Unknown pathogenicity (Level B)",
        5: "C3: Unknown pathogenicity",
        6: "C3: Unknown pathogenicity (Level C)",
        7: "C4: Likely pathogenic",
        8: "C5: Pathogenic"
    },
    drawAcmg: function(div, options) {
        const span = d3.select("#"+div).html("").append("div"),
              text = options.text || "Unclassified";

        let classes = options.classes || "";
        if (text == 'Unclassified') {
        	classes += " cv-Unclassified";
		} else {
            classes += " cv-C"+text.slice(1,2);
		}

		classes += " bordered-classification";

		span.classed(classes, true)
			.text(text);
    },
	drawAmp: function(div, options) {
        const span = d3.select("#"+div).html("").append("div"),
              text = options.text || "Unclassified";

        let classes = options.classes || "";
        classes += " amp-"+text.replace(" ","-");

        classes += " bordered-classification";

        span.classed(classes, true)
            .text(text);
	},
	drawOverall: function(div, options) {
        const span = d3.select("#"+div).html("").append("div"),
            text = options.text || "Unclassified";

        let classes = options.classes || "";

        var overallMapping = {
            "Unclassified": "overall-Unclassified",
            "CS: Clinically Significant": "overall-CS",
            "UCS: Unclear Clinical Significance": "overall-UCS",
            "NCS: Not Clinically Significant": "overall-NCS"
        };

        classes += " "+overallMapping[text];

        classes += " bordered-classification";

        span.classed(classes, true)
            .text(text);
	}
};

/**
 * Process the date on the front end, so we don't waste server time.
 * Expects a string like: "2016-12-13T13:00:00Z"
 */
PathOS.formatDate = function(string) {
	return string ? new Date(string).toLocaleDateString("en-GB", {day: 'numeric', month: 'long', year: 'numeric'}) : "No date in system";
};

PathOS.graphSpace = function(config) {
	const graphSpace = this;
	graphSpace.toBeLoaded = null;
	graphSpace.graphs = [];

	const graphWidth = 800;
	const graphHeight = 600;

	const div = config.div;
	const infoBar = div.append("div")
		.classed("infoBar", true);

	infoBar.append("h1").text(config.title);
	const table = infoBar.append("table");
	const thead = table.append("thead").append("tr");

    thead.append("th").text("Sample");
    thead.append("th").text("Type");
	thead.append("th").text("Intersect");
    thead.append("th").text("Action");

	const tbody = table.append("tbody");


	div.append("div").classed("workspace", true);
	const tabs = div.append("div").classed("tabs", true);

	const graphSvg = div.append("div").classed("graphdiv", true)
		.append("svg").attr("viewBox", "0 0 "+graphWidth+" "+graphHeight)
		.append("g").attrs({
			width: graphWidth,
			height: graphHeight
		});

    graphSvg.append("rect") // throw a background & border in?
        .attrs({
            width: graphWidth,
            height: graphHeight,
            fill: "white",
            stroke: "black",
            'stroke-width': '2px'
        });

	this.control = null;
	this.samples = [];
	this.currentGraph = null;

	this.applyData = function(samples) {
        graphSpace.toBeLoaded = samples.length;

        graphSpace.color = d3.scaleOrdinal(config.colours)
            .domain(samples.map((d) => d.name));

            tbody.selectAll("tr")
            .data(samples)
            .enter()
            .append("tr")
            .each(function(d){
				const that = d3.select(this)
					.attr("id", "tr-"+d.name);
                that.append("td").text(d.name);
                that.append("td").text(d.type);
                let intersect = that.append("td").text("?");

                const last = that.append("td").classed("action", true);
                const loading = last.append("img")
                    .style("width", "46px")
                    .attr("src", `${PathOS.application}/dist/images/pathos_logo_animated.svg`);

                d3.tsv(d.tsv_url, function(data){
                    loading.remove();
                    let sample = new Sample({
                        id: d.name,
                        info: d,
                        data: data,
                        intersect: intersect,
						td: last,
                        action: last,
                        color: graphSpace.color(d.name),
						// min: data.map((d)=>parseInt(d["Mean coverage"])).reduce((a,b) => Math.min(a,b)),
						// max: data.map((d)=>parseInt(d["Mean coverage"])).reduce((a,b) => Math.max(a,b)),
                        values: data.map(function(d){ return { region: d.Region, coverage: d["Mean coverage"] };})
                    });
					graphSpace.samples.push(sample);
					if(d.type === "control") {
						graphSpace.control = sample;
					}

                    if(--graphSpace.toBeLoaded <= 0) {
                        $("#loadingCharts").remove();
                    	if(graphSpace.control) {
                            initGraphs(graphSpace.control);
						} else {
                    		table.selectAll("td:last-child")
								.text("Control?")
								.on("click.pickControl", function(d){
									$("#tr-"+d.id).insertBefore(".infoBar table tbody tr:first-child");

									table.selectAll("td:last-child")
										.text("Loaded!")
										.on("click.pickControl", null);

									graphSpace.control = sample;
                                    initGraphs(graphSpace.control);
								});
						}
					}
                });
            });
    };

	function initGraphs(control) {
		console.log(control);

		control.data.intersect.text("N/A");
		control.data
			.action.text("N/A")
			.on("click", null)
			.style("color", "")
			.style("background", "");

        // I guess we could calculate the intersect here..?
		// control.data.data.forEach(function(d){
		// 	console.log(d);
		// });

		graphSpace.graphs.forEach(function(graph){
			graph.init(control);
			graph.hideGraph();
		});
        graphSpace.currentGraph.showGraph();
	}

	this.addGraph = function(config) {
		let graph = new Graph(config);

		graphSpace.graphs.push(graph);
		if(!graphSpace.currentGraph) {
			graphSpace.currentGraph = graph;
		}
	};

	const Graph = function(config) {
		const graph = this;

		this.id = config.id;
		this.name = config.name;
		this.displayedData = displayedData = {};

        tabs.append("div")
            .classed("tab", true)
            .append("a")
            .attr("href", "#linegraph")
            .on("click", function(){
                graph.showGraph();
            })
            .text(config.name);


        let svg = graphSvg.append("g")
			.classed("graph", true)
			.attr("id", "graph-"+config.id);

		this.margin = margin = {top: 20, right: 20, bottom: 30, left: 50};
		this.width = graphWidth - margin.left - margin.right;
		this.height = graphHeight - margin.top - margin.bottom;
		this.g = g = svg.append("g").attr("transform", "translate("+margin.left+" "+margin.top+")");

        this.xAxis = g.append("g")
            .attr("class", "axis axis--x")
            .attr("transform", "translate(0," + this.height + ")");

        this.xLabel = this.xAxis.append("text")
            .style("text-anchor", "middle")
            .style("font-size", "16px")
            .attr("x", this.width/2)
            .attr("y", 6)
            .attr("dy", "0.9em")
            .attr("fill", "#000");

        this.yAxis = g.append("g")
            .attr("class", "axis axis--y");
        this.yLabel = this.yAxis.append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 6)
            .attr("dy", "0.71em")
            .attr("fill", "#000");

        this.scatterNodes = scatterNodes = g.append("g")
            .classed("scatterNodes", true);

        var mask = g.append("g");
        mask.append("rect")
            .attrs({
                x: 0,
                y: -(this.margin.top - 1),
                width: this.width + this.margin.right - 1,
                height: this.margin.top - 1,
                fill: "white"
            });

        this.yBounds = function() {
        	let min = -100,
				max = 100;
        	const data = this.displayedData;

        	Object.keys(data).forEach(function(sample){
                min = data[sample].reduce((a,b) => Math.min(a,b));
                max = data[sample].reduce((a,b) => Math.max(a,b));
			});

        	return [min, max];
		};

        this.expand = function(){
            console.log("expanding...");
            graph.y.domain(graph.yBounds());

            graph.yAxis.transition()
                .duration(750)
                .call(d3.axisLeft(graph.y));
            expando.text("").on("click", graph.collapse);
            graph.repaint();
        };

        this.collapse = function(){
            console.log("collapsing...");
            graph.y.domain(graph.defaultYdomain);
            graph.yAxis.transition()
                .duration(750)
                .call(d3.axisLeft(graph.y));
            expando.text("").on("click", graph.expand);
            graph.repaint();
        };

        this.repaint = function(){
            console.log("Repainting...");
            d3.select("#linegraph .control path")
                .transition()
                .duration(750)
                .attr("d", function(d) { return line(d.values); });

            this.scatterNodes.selectAll(".sample circle")
                .transition()
                .duration(750)
                .attr("cy", function(d){
                    return graph.y(d);
                });
        };




        var expando = mask.append("g")
            .style("text-anchor", "middle")
            .style("font-size", "20px")
            .style("transform", "translate("+this.width/2+"px, 0)")
            .style("cursor", "pointer")
            .append("text")
            .classed("fa", true)
            .text("")
            .on("click", graph.expand);



        this.showGraph = function() {
            if(graphSpace.currentGraph) {
				graphSpace.currentGraph.hideGraph();
            }
            graphSpace.currentGraph = this;
            this.g.style("display", "inherit");
        };

        this.hideGraph = function() {
            this.g.style("display", "none");
		};

		this.add = config.add;
        this.init = config.init;
	};


	const Sample = function(blob) {
		this.data = blob;
        blob.action.text("Loaded!")
            .datum(blob)
            .style("cursor", "pointer")
            .style("user-select", "none")
            .style("color", blob.color)
            .on("click", toggleSample);
	};

    this.removeSample = function(sample) {
        delete displayedData[sample];

        var svg = d3.selectAll(".svg-"+sample);

        svg.selectAll("circle")
            .transition()
            .duration(500)
            .attr("r", 0)
            .remove();

        setTimeout(function(){
            svg.remove();
        }, 600);
    };


	function toggleSample(d) {
        var that = d3.select(this);

        if(that.classed("loaded")) {
            that.classed("loaded", false);
            that.styles({
                background: "none",
                color: graphSpace.color(d.id)
            });
            graphSpace.removeSample(d.id);
        } else {
            that.classed("loaded", true);
            that.styles({
                background: graphSpace.color(d.id),
                color: "black"
            });

            if(graphSpace.control) {
				graphSpace.graphs.forEach(function(graph){
					graph.add(d);
				});
            }
        }
	}
};







/**
 * Apparently this is from mustache.js
 * https://stackoverflow.com/a/12034334
 *
 * Added for svlist, to sanitise titles, but it doesn't seem to be working.
 * So I'm commenting it out.
 * DKGM 22-Nov-2017
 */
// PathOS.escapeHtml = function(string) {
//     var entityMap = {
//         '&': '&amp;',
//         '<': '&lt;',
//         '>': '&gt;',
//         '"': '&quot;',
//         "'": '&#39;',
//         '/': '&#x2F;',
//         '`': '&#x60;',
//         '=': '&#x3D;'
//     };
//
//     return String(string).replace(/[&<>"'`=\/]/g, function (s) {
//         return entityMap[s];
//     });
// };

/**
 * This function adds a module to the sidebar.
 *
 *
 *
 * @param config
 *
 */

PathOS.module = function(config) {

	//console.log("Adding PathOS module... "+config.name);
	var that = this;
	PathOS.modules.map[config.name] = {
		data: config,
		object: that
	};

	// Get the div or create one.
	var div = d3.select('#'+config.name);
	if(div.empty()) {
		div = this.div = d3.select("#sidebar")
			.append("div")
			.classed("module", true)
			.attr("id", config.name);
	}

	// Get the title or create one.
	var title = div.select(".moduletitle");
	if(title.empty()) {
		title = div.append('table').classed("moduletitle", true).append('tr');

		title.append('td')
			.classed("modulelabel", true)
			.datum(config)
			.on("click", that.toggle)
		.append("a")
			.attr('href', "#" + config.name)
		.append("h1")
			.text(config.title);
	} else {
		title.select('td').classed("modulelabel", true).datum(config).on("click", that.toggle);
	}

	title.insert('td','td')
		.datum(config)
		.on("click", that.toggle)
	.append("a")
		.attr('href', "#"+config.name)
	.append("i")
		.classed("fa fa-minus-square minimise", true);

	//buttons = title.append('td').classed('buttons', true);


	// Get the content or create one.


	function buildHistoryRow(d){

		var type = '';
		if(d.title.toLowerCase().indexOf('seqrun') >= 0) {
			type = 'Seqrun';
		} else if(d.title.toLowerCase().indexOf('pubmed') >= 0) {
			type = 'Pubmed';
		} else if(d.title.toLowerCase().indexOf('sequenced variants list') >= 0 || d.title.toLowerCase().indexOf('edit report') >= 0
		) {
			type = 'SeqSample';
		} else if(d.title.toLowerCase().indexOf('curvar') >= 0 || d.title.toLowerCase().indexOf('curated variants list') >= 0) {
			type = 'CurVariant';
		} else if(d.title.toLowerCase().indexOf('patient') >= 0) {
			type = 'PatSample';
		}

        //
		//row.append('td').append('i').classed('document-icon fa fa-wpforms', true)
		//	.classed(type, true);


		var row = body.append('tr');
		row.append('td')
			.append('a')
			.attr('href', d.url).text(d.title).classed(type, true);

		row.append('td').text(PathOS.timeSince(d.time));
	}

	if(config.type) {
		var content = div.append('div').classed('content', true);
		switch (config.type) {
			case 'changelog':

				var versions = Object.keys(config.data);

				versions.forEach(function(version){
					content.append("h4").text(version).styles({
						margin: "0 5px"
					});
					var list = content.append("ul");
					config.data[version].forEach(function(d){
						list.append("li").text(d);
					});
				});
				break;
			case 'tags':

				var edit = title.append('td').attr('id', 'tags_edit_button')
					.on('click',function(){
						$("#tags").toggleClass("editing");

						$('#tags_edit_button i').toggleClass('fa-pencil-square-o');
						$('#tags_edit_button i').toggleClass('fa-pencil-square');


					})
					.append("a").attr('href', "#"+config.name);

				//edit.append('p').text("(Editing)");
				edit.append('span').html("(Editing)&nbsp;");
				edit.append("i").classed("button fa fa-pencil-square-o", true);

				title.append('td').append("a").attr('href', "#"+config.name)
					.append("i")
					.classed("fa fa-times", true)
					.style("padding-bottom", '1px')
					.on('click', function(d){
						d3.select("#"+config.name).style('display','none');
						config.closeModule();
					});

				d3.select("#tags").style('display','none');
				var row = content.append('table')
					.append('thead')
					.append("tr").classed('row',true);
				row.append('th').text(config.data.object);
				row.append('th').text("").attr("id","object_id").style('text-align','right');

				var box = content.append('div').classed('outlined-box tags_field', true)
					.attr("id", 'moduleTagBox')
					.on('click', function(){
						$('#tag_text_area').focus();
					});

				config.data.tags.forEach(function(tag){
					PathOS.tags.drawTag(box, tag, true);
				});

				box.append('textarea')
					.attr('id', 'tag_text_area')
					.attr('placeholder', 'Enter Tags Here');

				$("#tag_text_area").autocomplete({
				    source: config.data.availableTags
				});


				$('body').on("keydown", function(e){
					if (e && e.keyCode && e.keyCode == 13 && $(document.activeElement).is("#tag_text_area")){

						var tag = $('#tag_text_area').val().trim();
						$('#tag_text_area').val('');

						//console.log("submitting tag! "+tag);
						if (tag && tag !== '' && PathOS.tags.current_object) {
							PathOS.tags.addTag(box, tag);
						}

					} else if (e && e.keyCode && e.keyCode == 8 && $(document.activeElement).is("#tag_text_area") && $("#tag_text_area").val() === ""){
						if($("#moduleTagBox.tags_field .tagdiv:last").length !== 0){
							if ($("#moduleTagBox.tags_field .tagdiv:last").hasClass('deleteFlag')) {
								var data = d3.select($("#moduleTagBox.tags_field .tagdiv:last")[0]).datum();

								if(confirm('Remove tag "'+data.label+'" from this object?')) {
									var params = {
										type: PathOS.controller,
										objid: PathOS.tags.current_object,
										tagid: data.id
									};
									$.ajax({
										type: "DELETE",
										url: `${PathOS.application}/tag/removeLink?${$.param(params)}`,
                                        error: function(d){
                                            alert("You are currently logged out, please refresh the page and log back in.");
                                        },
                                        success: function (result) {
											if(result != 'fail') {
												$('.tag-'+data.id).remove();
												if(reloadGrid) {
													reloadGrid();
												}
											}
										},
										cache: false,
										contentType: false,
										processData: false
									});
								}
							} else {
								$("#moduleTagBox.tags_field .tagdiv:last").toggleClass("deleteFlag").on('click', function(){
									var data = d3.select($("#moduleTagBox.tags_field .tagdiv:last")[0]).datum();
									var params = {
										type: PathOS.controller,
										objid: PathOS.tags.current_object,
										tagid: data.id
									};
									$.ajax({
										type: "DELETE",
										url: `${PathOS.application}/tag/removeLink?${$.param(params)}`,
                                        error: function(d){
                                            alert("You are currently logged out, please refresh the page and log back in.");
                                        },
                                        success: function (result) {
											if(result != 'fail') {
												$('.tag-'+data.id).remove();
												if(reloadGrid) {
													reloadGrid();
												}
											}
										},
										cache: false,
										contentType: false,
										processData: false
									});
								});
							}
						}
					}
				});


				break;
			case 'history':
				var table = content.append('table'),
					head = table.append('thead').append('tr'),
					body = table.append('tbody');
				head.append('th').text('Page');
				head.append('th').text('T');

				config.data.forEach(buildHistoryRow);


				body.append('span').append('a').attr('href', '#')
					.text("See more").on('click', function(){
					PathOS.history.more().forEach(buildHistoryRow);

					$(this).remove();
				});
				break;
			case 'established':
				content.remove();
				break;
			default:
				var ul = content.append("ul")
					.style('clip', "rect(0px, 1000px, 0px, 0px)");

				if(config.data instanceof Array) {
					config.data.forEach(function(d){
						ul.append("li").html(d);
					});
				} else {
					Object.keys(config.data).forEach(function(key){
						ul.append("li").html(key+": "+config.data[key]);
					});
				}
				break;
		}
	}

	if(div.classed('hide') || config.hide) {
		div.classed('hide', false);
		that.toggle(config.name);
	}

};

PathOS.module.prototype.toggle = function(item){

	if(d3.event) {
		d3.event.preventDefault();
	}
	var name = typeof item == 'string' ? item : d3.select(this).datum().name;

	var id = "#" + name;

	$(id).toggleClass("hidden");
	$(id+" i.minimise").toggleClass("fa-minus-square");
	$(id+" i.minimise").toggleClass("fa-plus-square");

	var height = $(id).height();

	if(d3.select(id).classed("hidden")) {
		d3.select(id).style("min-height", "1px");

		if(PathOS.user) {
			PathOS.modules.settings[PathOS.user].hide[name] = true;
			PathOS.data.save("modules", PathOS.modules.settings);
		}
	} else {
		d3.select(id).style("min-height", height + "px");

		if(PathOS.user) {
			delete PathOS.modules.settings[PathOS.user].hide[name];
			PathOS.data.save("modules", PathOS.modules.settings);
		}
	}
};

PathOS.module.prototype.data = function(){
	var d = d3.select(this).datum();
	console.log(d);
};




PathOS.history = {
    json: [],

    /**
     * Add a webpage to the history
     *
     * example:
     * PathOS.history.add({
	 *	title: document.title,
	 *	url: window.location.href,
	 *	time: Date()
     * @param title, url, time
     */
    add: function (d){
        PathOS.history.json.forEach(function(old, i){
            if(old.title == d.title){
                PathOS.history.json.splice(i, 1);
            }
        });
        PathOS.history.json.push(d);
        if(PathOS.history.json.length > 50) {
            PathOS.history.json = PathOS.history.json.slice(20);
        }
        PathOS.data.save("history", PathOS.history.json);
    },
    clear: function() {
        PathOS.history.json = [];
        PathOS.data.clear("history");
    },
    show: function(d) {
        var arr = PathOS.history.json;
        var n = d || 10;
        return (arr.length < n ? arr : arr.slice(arr.length - n)).reverse();
    },
    more: function() {
        var arr = PathOS.history.json;
        var n = 50;

        return (arr.length < n ? arr : arr.slice(arr.length - n)).reverse().slice(10);
    },
    init: function(){
        PathOS.history.json = PathOS.data.load("history", []);
    }
};









/**
 *
 * @type {{menuVisible: boolean, settings: {}, map: {}, init: PathOS.modules.init, menu: {show: PathOS.modules.menu.show, hide: PathOS.modules.menu.hide}}}
 */

menuItems = [
	{
		category: "Sample page",
        type: "boolean",
		title: "Compressed view",
		description: "Use a compressed version of the Sequenced Sample Page.",
		field: "compressedView"
	},
	// {
	// 	category: "Sample page",
	// 	type: "boolean",
	// 	title: "Gene Mask",
	// 	description: "Skip Gene Mask verification on Sequenced Sample Page.",
	// 	field: "skipGeneMask"
	// },
	{
		category: "Sample page",
		type: "dropdown",
		title: "Number of Sequenced Variants to show",
		options: [ 20, 100, 200, 1000 ],
		description: "Default number of Sequenced Variants to load per page on the datagrid.",
		field: "svlistRows"
	},
	{
		category: "Home page",
		type: "number",
		title: "Latest Sequenced Runs",
		description: "How many sequenced runs should be shown on the home page?",
		field: "numberOfSeqruns",
		default: 10
	},
	{
		category: "Home page",
		type: "panels",
		title: "Panel Groups",
		description: "Which panels do you want to see on your home page?"
	},
	{
		category: "Seqrun page",
		type: "boolean",
		title: "New Heatmap",
		description: "Use the new D3 based heatmap.",
		field: "d3heatmap"
	},
	{
		category: "History",
        type: "button",
		title: "Clear History",
		description: "Clear recently visited PathOS pages.",
		action: PathOS.history.clear
	},
	{
		category: "Help",
		type: "links",
		links: [
			{
				title: "Jira",
				url: "https://atlassian.petermac.org.au/jira/secure/Dashboard.jspa"
			},
			{
				title: "Confluence",
				url: "https://atlassian.petermac.org.au/confluence/display/PVS/PathOS+Variant+System"
			}
		]
	}
];


PathOS.modules = {
	menuVisible: false,
	settings: {},
	map: {},
	init: function( options ){
		//get settings if they exist, or use default settings

		// console.log("loading module settings");

		// Load settings
		PathOS.modules.settings = PathOS.data.load("modules");

		PathOS.controller = options.controller;
		PathOS.action = options.action;

		// If user's settings exist...
		if (options.user) {
			PathOS.user = options.user;
			PathOS.username = options.username;
			if (PathOS.modules.settings[PathOS.user]) {
				//set things like reordering tables and history
				Object.keys(PathOS.modules.settings[PathOS.user].hide).forEach(function (d) {
					if (document.getElementById(d) && !d3.select("#" + d).classed("hidden")) {

						if (PathOS.modules.map[d]) {
							PathOS.modules.map[d].object.toggle(d);
						} else {
							d3.select("#" + d).classed("hide", true);
						}

					}
				});

				if(PathOS.modules.settings[PathOS.user].sidebar[window.location.pathname]) {
					if (PathOS.modules.settings[PathOS.user].sidebar[window.location.pathname] == 'show') {
						d3.select("#wrapper").classed("toggled", false);
						d3.select("#sidebar-toggle i").classed("fa-chevron-left", false);
						d3.select("#sidebar-toggle i").classed("fa-chevron-right", true);
					} else if (PathOS.modules.settings[PathOS.user].sidebar[window.location.pathname] == 'hide') {
						d3.select("#wrapper").classed("toggled", true);
						d3.select("#sidebar-toggle i").classed("fa-chevron-left", true);
						d3.select("#sidebar-toggle i").classed("fa-chevron-right", false);
					}
				}

			} else {
				PathOS.modules.settings[PathOS.user] = {
					sidebar: {},
					hide: {},
					svlistIGV: "ask"
				};
				PathOS.data.save("modules", PathOS.modules.settings);
			}
		}


		d3.select("#sidebar-footer")
		.append('span')
		.append('a')
			.attr('href', '#')
			.on('click', function(){
				if(PathOS.modules.menuVisible) {
					PathOS.modules.menu.hide();
				} else {
					PathOS.modules.menu.show();
				}
			}).append('i')
			.attr('class', 'fa-lg fa fa-cog')
			.attr('aria-hidden', 'true');
	},
	menu: {
		show: function(){
			var menu = PathOS.overlay.init();

			menu.attr("id", "PathOS-options");

			menu.append("div").attr('id', 'mmHeader')
				.append('h1').text("PathOS Options");

			// Todo: Don't hardcode PathOS in Traceback, etc.
			$.ajax({
				url: `${PathOS.application}/preferences/fetchPreferences`,
                error: function(d){
                    alert("You are currently logged out, please refresh the page and log back in.");
                },
                success: function(d){
                    preferences = d.preferences;
                    // console.log(preferences);

                    if(PathOS.user) {
                        var prevCategory = "";

                        menu.append("table")
							.attr("id", "menuItems")
                            .selectAll("tr")
                            .data(menuItems)
                            .enter()
                            .append("tr")
                            .each(function(d, i){
                                var row = d3.select(this);

                                if(prevCategory == d.category) {
                                    row.append('td');
                                } else {
                                    row.append('td').text(d.category);
                                }
                                prevCategory = d.category;

                                var box = row.append('td');
                                box.append("h3").text(d.title);
                                // box.append("p").text(d.description);

                                switch (d.type) {
                                    case "boolean":
										var thisBoolean = false;
										if(preferences && preferences[d.field]) {
                                            thisBoolean = preferences[d.field];
										}

                                        var div = box.append("div").classed("checkbox-div", true);
                                        div.append("input").attrs({
                                            type: "checkbox",
                                            id: d.field
                                        }).property("checked", thisBoolean);
                                        div.append("label").attrs({
                                            for: d.field
                                        }).text(d.description);
                                        break;

									case "dropdown":
                                        var select = box.append("select")
											.attr("id", d.field);
                                        box.append("p").text(d.description);
                                        d.options.forEach(function(option){
                                            var thing = select.append("option")
                                                .attr("value", option)
                                                .text(option);
                                            if(preferences && option == preferences[d.field]) {
                                                thing.attr("selected", true);
											}
                                        });
                                        break;

                                    case "number":

                                    	var thisNumber = d.default;
                                    	if(preferences && preferences[d.field]) {
                                    		thisNumber = preferences[d.field];
										}

                                        box.append("p").text(d.description);
                                        box.append("input")
											.attr("id", d.field)
											.style("width", "150px")
											.attr("value", thisNumber);
                                        break;

                                    case "button":
                                        box.append("input")
											.classed("menuButton", true)
                                            .attr("type","button")
                                            .attr("value", d.description)
											.on("click", d.action);
                                        break;

                                    case "links":
                                        d.links.forEach(function(link){
                                            box.append("a")
                                                .attrs({
                                                    target: "_blank",
                                                    href: link.url
                                                }).text(link.title);
                                        });
                                        break;

                                    case "panels":
                                    	var thisPanelList = "";
										if (preferences && preferences.panelList) {
                                            thisPanelList = preferences.panelList;
										}


                                    	var panelDiv = box.append("div");
										panelDiv.append("p")
											.text("Panels being shown: ")
											.append("span")
											.datum(thisPanelList)
											.text(thisPanelList ? thisPanelList.split(",").join(", ") : "All panels.")
											.attr("id", "previousPanels");

                                        panelDiv.append("a").attrs({
											id: "lookUpPanelButton",
                                            href: "#lookupPanels"
                                        }).on("click", function(d){
                                            panelDiv.remove();

                                            $.ajax({
                                                url: `${PathOS.application}/Panel/fetchAllData`,
                                                error: function(d){
                                                    alert("You are currently logged out, please refresh the page and log back in.");
                                                },
                                                success: function(data){
                                                    // console.log(data);

                                                    var groups = {};
                                                    data.forEach(function(panel){
                                                        var blob = {
                                                            manifest: panel[0],
                                                            group: panel[1],
                                                            description: panel[2]
                                                        };

                                                        groups[blob.group] = groups[blob.group] || [];
                                                        groups[blob.group].push(blob);

                                                    });

                                                    var groupNames = Object.keys(groups).sort();

                                                    var panels = box
                                                        .append("div")
														.attr("id", "panelMenu");

                                                    panels.selectAll("div.panelSelector")
                                                        .data(groupNames)
                                                        .enter()
                                                        .append("div")
														.classed("panelSelector", true)
                                                        .each(function(group, i){
                                                            var panelSelector = d3.select(this);
                                                            panelSelector.attr("id", `panelSelectorDiv-${i}`);


                                                            var data = groups[group].sort((a,b) => b.manifest - a.manifest);

                                                            var header = panelSelector.append("span")
																.classed("panelGroupHeader", true);
                                                            header.append("input").attrs({
                                                                // checked: preferences[d.field],
                                                                type: "checkbox",
                                                                id: "panelSelector-"+i
                                                            }).on("change", function(d){
                                                            	var newSetting = d3.select(this).property("checked");

                                                                panelSelector.selectAll("input[type='checkbox']")
																	.property("checked", newSetting);
															});

                                                            // property('checked', true);

                                                            header.append("label").attrs({
                                                                for: "panelSelector-"+i
                                                            }).text(group);

                                                            var body = panelSelector.append("div")
																.attr("id", `panelSelectorManifests-${i}`);

                                                            data.forEach(function(manifest, j){
                                                                var span = body.append("span")
																	.attr("title", manifest.description);

                                                                var checkbox = span.append("input").attrs({
                                                                    // checked: preferences[d.field],
                                                                    type: "checkbox",
																	class: "manifestCheckbox",
                                                                    id: `manifest-${i}-${j}`
                                                                }).datum(manifest.manifest).on("change", function(d){
                                                                    var newSetting = d3.select(this).property("checked");

                                                                    if(newSetting) {
                                                                    	var anyUnchecked = false;
                                                                    	body.selectAll("input[type='checkbox']")
																			.each(function(d){
																				if(!d3.select(this).property("checked")) {
                                                                                    anyUnchecked = true;
																				}
																			});
                                                                    	if(!anyUnchecked) {
                                                                    		header.select("input[type='checkbox']").property("checked", true);
																		}
																	} else {
                                                                        header.select("input[type='checkbox']").property("checked", false);
																	}
																});

                                                                if(thisPanelList.indexOf(manifest.manifest) >= 0) {
                                                                    checkbox.property("checked", true);
                                                                    var anyUnchecked = false;
                                                                    body.selectAll("input[type='checkbox']")
                                                                        .each(function(d){
                                                                            if(!d3.select(this).property("checked")) {
                                                                                anyUnchecked = true;
                                                                            }
                                                                        });
                                                                    if(!anyUnchecked) {
                                                                        header.select("input[type='checkbox']").property("checked", true);
                                                                    }
																}

                                                                span.append("label").attrs({
                                                                    for: `manifest-${i}-${j}`
                                                                }).text(manifest.manifest);
															});

                                                        });

                                                }
                                            });
                                        }).text("Look up panels");
                                        break;
                                    default:
                                }



/*
$("#compressedView").is(':checked')
$("#skipGeneMask").is(':checked')
$('#svlistRows option:selected').text()
 */





                            });

                        var saveAndResetDiv = menu.append("div").attr("id", "saveAndReset");

                        saveAndResetDiv.append("a").attrs({
							id: "menuSaveButton",
							href: "#save"
						}).text("Save")
							.on("click", function(d){

								var panelList = "";
								if(d3.select("#previousPanels").data()[0]) {
                                    panelList = d3.select("#previousPanels").data()[0];
								} else if(d3.select("#previousPanels").data()[0] === "") {
									// console.log("Show all panels");
                                } else if(d3.select("#panelMenu").datum()) {
									panelList = d3.selectAll(".manifestCheckbox:checked").data().join();
								}

								var package = {
									panelList: panelList,
									numberOfSeqruns: $("#numberOfSeqruns").val(),
									svlistRows: $("#svlistRows").val(),
									// skipGeneMask: $("#skipGeneMask").is(':checked'),
									compressedView: $("#compressedView").is(':checked'),
                                    d3heatmap: $("#d3heatmap").is(':checked')
								};

								// console.log("Package is", package);

								$.ajax({
									url: `${PathOS.application}/preferences/saveSettings`,
									type: "POST",
									success: function(d){
										// console.log("Hey!");
										// console.log(d);
										alert("Preferences saved, reloading window.");
										location.reload();

									},
                                    error: function(e) {
                                        console.error(e);
										alert("You are currently logged out, please refresh the page and log back in.");
                                    },
                                    contentType: "application/json; charset=utf-8",
                                    dataType: "json",
                                    data: JSON.stringify(package)
								});

							}
						);

                        saveAndResetDiv.append("a").attrs({
                            id: "menuResetBUtton",
                            href: "#reset"
                        }).text("Reset Settings")
                            .on("click", function(d){
                                $.ajax({
                                    url: `${PathOS.application}/preferences/deleteSettings`,
                                    success: function(d){
                                        alert("Your preferences have been reset.");
                                        location.reload();
                                    },
									error: function(e) {
                                    	console.error(e);
                                        alert("You are currently logged out, please refresh the page and log back in.");
									}
                                });
                            });


                    } else {
                        var links = menu.append("div")
                            .attr('id','pathos-menu-links')
                            .classed("row", true)
                            .append('p')
                            .text("Links to: ");

                        links.append("a")
                            .attr('href', "https://atlassian.petermac.org.au/jira/secure/Dashboard.jspa")
                            .text("Jira");
                        links.append("span").text(" - ");
                        links.append('a').attr('href', 'https://atlassian.petermac.org.au/confluence/display/PVS/PathOS+Variant+System').text("Confluence");
                        links.append("span").text(" - ");
                        links.append('a').attr('href', 'http://pathos.co/help').text("Help");
                    }


				}
			});
		}
	}
};


/**
 * This bundle is for the svlist page and showing Curated Variants
 *
 * DKGM 21-October-2016
 *
 */

PathOS.svlist = {
	evidence: {
		'pathAloneTruncating':"Truncating variant (nonsense, frameshift, canonical splice site, initiation codon) in a known tumour suppressor gene",
		'pathAloneKnown':"Same missense change as a previously established pathogenic variant",
		'pathStrongFunction':"Well-established in vitro or in vivo functional studies support a deleterious effect on the gene or gene product",
		'pathStrongCase':"Case-control studies show enrichment in cases",
		'pathStrongCoseg':"<b>For familial cancer only:</b> Proband's family study shows co-segregation with cancer",
		'pathSupportHotspot':"Located near a known mutational hot-spot or within a well-characterised functional domain",
		'pathSupportGene':"Occurs in a gene with high clinical specificity and sensitivity for the cancer",
		'pathSupportInsilico':"Multiple types of computational evidence support a deleterious effect on the gene or gene product (PolyPhen, SIFT, Mutation Taster ,conservation, evolution, splicing)",
		'pathSupportSpectrum':"Type of variant fits known mutation spectrum for the gene",
		'pathSupportGmaf':"Absent from ESP and 1000 Genomes data, or frequency is below highest global minor allele frequency (GMAF) expected for autosomal dominant disease (0.4%)",
		'pathSupportIndel':"In-frame deletion/insertion in a well characterised functional domain",
		'pathSupportNovelMissense':"Novel missense change at an amino acid where a different missense change is pathogenic",
		'pathSupportLsdb':"Noted as pathogenic in a curated locus specific database",
		'pathSupportCoseg':"<b>For familial cancer only:</b> Proband's family study shows co-segregation with disease",
		'benignAloneGmaf':"Exists in ESP and 1000 Genomes >= 0.4% GMAF",
		'benignAloneHealthy':"<b>For familial cancer only:</b> For a fully penetrant cancer syndrome, observed in a healthy adult individual",
		'benignStrongFunction':"Well-established in vitro or in vivo functional studies shows no deleterious effect on protein function or splicing",
		'benignStrongCase':"Case control studies show comparable frequencies",
		'benignStrongCoseg':"<b>For familial cancer only:</b> Variant fails to co-segregate with disease in a family study",
		'benignSupportVariable':"Located in a region without a characterised function or away from known mutation hot-spots",
		'benignSupportInsilico':"Multiple types of computational evidence suggest no impact on gene or gene product (PolyPhen, SIFT, Mutation Taster, conservation, evolution, splicing)",
		'benignSupportSpectrum':"Type of variant does not fit known mutation spectrum for the gene",
		'benignSupportLsdb':"Noted as benign in a curated locus specific database",
		'benignSupportPath':"<b>For familial cancer only:</b> For a fully penetrant cancer syndrome, observed with another pathogenic variant in the same individual"
	},
	acmgCriteria: {
        PVS1: "Null Variants",
        PS1: "Established Pathogenic Variant",
        PS2: "Confirmed de novo variant",
        PS3: "Functional studies support damaging effect",
        PS4: "Enrichment in cases",
        PM1: "Mutational hotspot/functional domain",
        PM2: "Absent from population studies",
        PM3: "Detected <i>in trans</i>",
        PM4: "Protein length changes",
        PM5: "Novel missense change",
        PM6: "Assumed de novo",
        PP1: "Cosegregation with disease",
        PP2: "Missense variants are a common mechanism of disease",
        PP3: "Computational evidence shows deleterious effect",
        PP4: "Patient's phentoype/family history highly specific for the disease",
        PP5: "Sources report as pathogenic",
        BA1: "In population studies >5%",
        BS1: "High allele frequency",
        BS2: "Observed in a healthy adult",
        BS3: "Functional studies show no damaging effect",
        BS4: "Lacking segregation",
        BP1: "Does not fit mutational spectrum",
        BP2: "Observed with another pathogenic variant",
        BP3: "In-frame deletions/insertions in a region without a known function",
        BP4: "Computational evidence shows no impact",
        BP5: "Alternate molecular basis for disease",
        BP6: "Sources report as benign",
        BP7: "Predicted as benign synonymous variant"
	},
	// Clin Context Comparitor
	// DKGM 18-Nov-2016
	// Rewrite this better. This is just hacked for a demo.
	ccc: function(a, b) {
		var result = false;
		if (a === null || b === null) {
			if (a === null && b === null) {
				result = true;
			}
		} else {
			if (a.id == b.id) {
				result = true;
			}
		}
		return result;
	}
};


PathOS.overlay = {
	init: function(options) {
		options = options || {};
		var styles = options.styles || {};
		var attrs = options.attrs || {};


		var overlay = d3.select('body')
			.append('div')
                .attr('id', 'overlay')
                .on('click', PathOS.overlay.close)
                .append('div')
                    .on('click', function() { d3.event.stopPropagation(); })
                    .attr("id", "overlay-dialog-box")
                    .attrs(attrs)
                    .styles(styles)
                    .classed('outlined-box', true)
                    .classed("container", true);

		overlay.append('a')
            .attr('href', '#closeOverlay')
            .on('click', PathOS.overlay.close)
			.append("i").classed("fa fa-close fa-lg", true);

		var box = overlay.append("div");

		box.append("div")
			.attr("id", "overlay_loading")
			.append("img")
			.classed("loading_logo", true)
			.attr("src", `${PathOS.application}/dist/images/pathos_logo_animated.svg`);

		PathOS.hotkeys.off();
		$('body').on('keydown', function(e){
			if(e && e.keyCode && e.keyCode == 27 && !$(document.activeElement).is("input") && !$(document.activeElement).is("textarea") && !e.altKey && !e.metaKey && !e.ctrlKey){
				PathOS.overlay.close();
			}
		});

		if(options && options.callback) options.callback(PathOS.overlay.loaded);
		else PathOS.overlay.loaded();

		return box;
	},
	loaded: function() {
		d3.select("#overlay_loading").remove();
	},
	close: function() {
		d3.select("#overlay").remove();
		d3.select("#overlay-dialog-box").remove();
		PathOS.hotkeys.init();
	},
	drawTable: function(box, info, id){
		box.select("table").remove();

		var table = box.append("table")
			.attr("id", id)
			.classed("infoTable", true);

		var tbody = table.append("tbody");

		Object.keys(info).forEach(function(row){
			var r = tbody.append("tr");
			r.append("td").html(row).classed("property-label", true);
			r.append("td").html(info[row]).classed("property-value", true);
		});
	}
};


PathOS.curVariant = {
	init: function(data) {
		console.log("Drawing a cur variant?");
		console.log(data);
	}
};

/**
 * This bundle is for the variant viewer.
 */
PathOS.variant = {
	viewer: function( data ) {
console.log("loading variant viewer", data);

		var hgvsg = data.hgvsg || "",
			svid = data.svid || null;

		var box = {
			overlay: PathOS.overlay.init()
		};

		box.overlay.attrs({
			id: "variantViewer"
		});

		box.title = box.overlay.append("h1").text("Variant: "+hgvsg);



		// First info section

		box.info = {
			row: box.overlay.append("div").classed('row', true)
		};

		box.info.left = box.info.row.append("div").classed('col-xs-6', true);
		box.info.right = box.info.row.append("div").classed('col-xs-6', true);

		box.info.leftTable = box.info.left.append("table")
			.classed("infoTable", true);

		box.info.hgvs = box.info.leftTable.append("tr");
		box.info.hgvsTitle = box.info.hgvs.append("td").text("HGVS");
		box.info.hgvsTd = box.info.hgvs.append("td").text(hgvsg);

		box.info.gene = box.info.leftTable.append("tr");
		box.info.gene.append("td").text("Gene");
		box.info.gene.append("td").classed("geneTd", true).text("No gene");

		box.info.samples = box.info.leftTable.append("tr");
		box.info.samples.append("td").text("Samples");
		box.info.sampleTd = box.info.samples.append("td").text("No samples");

		// Curated Variant section

		box.cv = {
			row: box.overlay.append("div").classed('row', true).attr("id", 'cv-list')
		};

		box.cv.row.append("h2").text("Curated Variants:");

		box.cv.table = box.cv.row.append('table');

		box.cv.thead = box.cv.table.append("thead").append("tr");

		box.cv.thead.append("th").styles({
			'padding-left': 0,
			width: "10px"
		});
		box.cv.thead.append("th").text("Context").styles({
			padding: "2px",
			width: "15%"
		});
		box.cv.thead.append("th").text("Report Description");
		box.cv.thead.append("th").text("Evidence Description");
		box.cv.thead.append("th").text("Classification");
		box.cv.tbody = box.cv.table.append("tbody");

		if ( hgvsg !== "" ) {
			PathOS.variant.doHgvsgStuff(box, hgvsg);
		} else if (svid) {
			$.ajax(`${PathOS.application}/seqVariant/lookUpSV/${svid}`, {
				error: function(d){
					console.log("error", d);
					PathOS.overlay.close();
					alert("You are currently logged out, please refresh the page and log back in.");
				},
				success: function(d){
					PathOS.variant.drawSV(box, d);
					PathOS.variant.doHgvsgStuff(box, d.hgvsg);
				}
			});
		}
	},
	doHgvsgStuff: function(box, hgvsg) {
		box.title.text("Variant: " + hgvsg);
		PathOS.variant.drawCVs(box, hgvsg);

		$.ajax(`${PathOS.application}/seqVariant/countSVs/?hgvsg=${hgvsg}`, {
            error: function(d){
                console.log("error", d);
                PathOS.overlay.close();
                alert("You are currently logged out, please refresh the page and log back in.");
            },
			success: function(d){
				box.info.sampleTd.html("");
				box.info.sampleTd.append("a").attrs({
					target: "_blank",
					href: `${PathOS.application}/search?q=${hgvsg}`
				}).text(d + " samples");
			}
		});
	},
	drawSV: function(box, data) {
		console.log(box);
		console.log(data);
		var hgvs = box.info.hgvsTd.append("ul").attrs({
			id: 'hgvs-list'
		}).classed("showList", true);

		var span = box.info.hgvsTitle
			.html("")
			.append("a")
			.attrs({
				href: "#hgvs"
			})
			.on('click', function(d){
                $("#hgvs-list").toggleClass("showList");
                $("#hgvsToggle").toggleClass("toggle");
                $("#hgvsToggle").toggleClass("toggled");
            })
			.append("span");

        span.append("i")
			.attr("id", "hgvsToggle")
			.classed("fa toggled", true);

		span.append("p").text("HGVS");

		hgvs.append("li").text(data.sv.hgvsg);
		hgvs.append("li").text("HGVSg: "+data.sv.hgvsg);
		hgvs.append("li").text("HGVSc: "+data.sv.hgvsc);
		hgvs.append("li").text("HGVSp: "+data.sv.hgvsp);





		var info = {
			"Sample": data.sv.sampleName,
			"Clinical Context": data.cc,
			"Consequences": data.sv.consequence,
			"Variant Caller": data.sv.varcaller,
			"Amplicon Count": data.sv.numamps,
			"Amplicon Bias": data.sv.ampbias,
			"Variant Frequency": data.sv.varFreq,
			"Variant Depth": data.sv.varDepth,
			"Panel Var %": d3.format(".4")(data.sv.varPanelPct)+"%",
			"dbSNP": data.sv.dbsnp,
			"GMAF %": data.sv.gmaf,
			"ESP %": data.sv.esp,
			"ExAC %": data.sv.exac,
			"Cosmic": data.sv.cosmic ? `<a target='_blank' href='${PathOS.application}/seqVariant/cosmicAction?id=${data.sv.id}' title='${data.sv.cosmicOccurs}'>COSM${data.sv.cosmic}</a>` : "",
			"Exon": data.sv.exon,
			"Cytoband": data.sv.cytoband,
			"CADD Raw": d3.format("(.2f")(data.sv.cadd),
			"CADD Scaled": d3.format("(.2f")(data.sv.cadd_phred)
		};

		PathOS.overlay.drawTable(box.info.right, info, "svInfoTable");

		var firstTd = d3.select("#svInfoTable tbody tr td");
		var temp = firstTd.html();
		span = firstTd.html("")
			.append("a")
			.attrs({
				href: "#toggleSvInfoTable"
			}).on('click', function(d){
				$("#svInfoTable").toggleClass("showDetails");
                $("#sampleToggle").toggleClass("toggle");
                $("#sampleToggle").toggleClass("toggled");
			}).append("span");

        span.append("i")
            .attr("id", "sampleToggle")
            .classed("fa toggle", true);

        span.append("p").text(temp);

		setTimeout(function(){
			$(".cvRow-"+data.contextCode).addClass("expand");
		}, 300);

	},
	drawCVs: function (box, hgvsg) {
		$.ajax(`${PathOS.application}/curVariant/allCurVariantsFor?hgvsg=${hgvsg}`, {
            error: function(d){
                console.log("error", d);
                PathOS.overlay.close();
                alert("You are currently logged out, please refresh the page and log back in.");
            },
			success: function(d){
				box.info.gene.select(".geneTd").text(d.gene);
				if(d.curVariants && d.curVariants.length > 0) {
					d.curVariants.forEach(function(cv){
						var row = null;
						if(cv.contextCode == "Generic") {
							row = box.cv.tbody.insert("tr", "tr");
						} else {
							row = box.cv.tbody.append("tr");
						}
						row.attrs({
							class: "cvRow-"+cv.contextCode
						});

						row.append('td').styles({
							'padding-left': '5px',
							cursor: 'pointer'
						}).on('click', function(){
							$(".cvRow-"+cv.contextCode).toggleClass("expand");
						}).append("i").attrs({
							'class': "fa toggle",
							'aria-hidden': "true"
						});

						PathOS.variant.addCVrow(row, cv);
					});
				}
			}
		});
	},
	addCVrow: function(row, data) {

		var context = row.append("td");

			context.append("a").attrs({
				target: "_blank",
				href: `${PathOS.application}/curVariant/show?id=${data.id}`
			}).text(data.context);

		row.append("td")
			.classed("description", true)
			.append('textarea')
			.text(data.reportDesc);

		var justification = "",
			blob = {};
		if(data.evidence && data.evidence.acmgJustification) {
			try {
				blob = JSON.parse(data.evidence.acmgJustification);
                justification = blob.acmgJustification;
            } catch(e) {
                justification = data.evidence.acmgJustification;
            }
		}

		row.append("td")
			.classed("description", true)
			.append('textarea')
			.text(justification);

		var evidence = row.append("td").classed("evidence-td", true);
		var pmClass = data.pmClass.split(":")[0];
		evidence.append("p")
			.text(data.pmClass)
			.classed("cvlabel cv-"+pmClass, true);

		var count = 0;

		var list = evidence.append("ul")
			.attr("id", "evidence-list-"+data.id)
			.classed("evidence-list", true);

		if (data.evidence) {
			Object.keys(PathOS.svlist.acmgCriteria).forEach(function(key){
				if(data.evidence[key] == 'yes') {
					count++;
					list.append('li').html(`<b>${key}:</b> ${PathOS.svlist.acmgCriteria[key]}`);
				}
			});
		}

	}
};













PathOS.buildBlock = function (div, data){
	data.forEach(function(d){
		var line = div.append('p');

		if(d.link && d.link !== '') {
			line.append('span').classed('bold', true).text(d.title+": ");
			line.html(line.html()+'<a href="'+d.link+'">'+ d.words+'</a>');
		} else {
			line.append('span').classed('bold', true).text(d.title+": ");
			line.html(line.html()+d.words);
		}
	});
};



PathOS.classify = function (d){
	return d.toLowerCase().replace(" ", "-");
};

PathOS.params = function() {
	var params = {};
	var string = window.location.search.substring(1);
	var arr = string.split("&");

	arr.forEach(function(q){
		if(q.indexOf("=") > 0) {
			var stuff = q.split("="),
				name = stuff[0],
				data = decodeURIComponent(stuff[1].replace(/\+/g, " "));

			params[name] = data;
		}
	});

	return params;
};

PathOS.evidence = {
	benignAloneGmaf: "Benign Stand-alone Gmaf",
	benignAloneHealthy: "Benign Stand-alone Healthy",
	benignStrongCase: "Benign Stand-alone Strong-case",
	benignStrongCoseg: "Benign Stand-alone Coseg",
	benignStrongFunction: "Benign Strong Function",
	benignSupportInsilico: "Benign Support Insilico",
	benignSupportLsdb: "Benign Support Local Sequence Database",
	benignSupportPath: "Benign Support Pathology",
	benignSupportSpectrum: "Benign Support Spectrum",
	benignSupportVariable: "Benign Support Variable",
	pathAloneKnown: "Pathology Stand-alone Known",
	pathAloneTruncating: "Pathology Stand-alone Truncating",
	pathStrongCase: "Pathology Strong Case",
	pathStrongCoseg: "Pathology Strong Coseg",
	pathStrongFunction: "Pathology Strong Function",
	pathSupportCoseg: "Pathology Support Coseg",
	pathSupportGene: "Pathology Support Gene",
	pathSupportGmaf: "Pathology Support Gmaf",
	pathSupportHotspot: "Pathology Support Hotspot",
	pathSupportIndel: "Pathology Support Indel",
	pathSupportInsilico: "Pathology Support Insilico",
	pathSupportLsdb: "Pathology Support Local Sequence Database",
	pathSupportNovelMissense: "Pathology Support Novel Missense",
	pathSupportSpectrum: "Pathology Support Spectrum"
};


// Add hotkeys to any PathOS page.
// init is called once in layouts/main.gsp
// use PathOS.hotkeys.off(); to turn off the hotkeys on a page.
 PathOS.hotkeys = {
 	keys: {
		27: function(){
			d3.selectAll(".tagdiv .tooltip.edit").classed("edit", false);
		}
	},
 	add: function(key, action){
 		this.keys[key] = action;
 	},
 	off: function(){
 		$('body').off("keydown");
 	},
 	init: function(){
 		var keys = this.keys;
		$('body').on("keydown", function(e){
			if(e && e.keyCode && keys[e.keyCode] && !$(document.activeElement).is("input") && !$(document.activeElement).is("textarea") && !e.altKey && !e.metaKey && !e.ctrlKey){
					keys[e.keyCode]();
			}
		});
 	},
 	testMode: function(){
 		var keys = this.keys;
		$('body').on("keydown", function(e){
			if(e && e.keyCode && !$(document.activeElement).is("input") && !e.altKey && !e.metaKey && !e.ctrlKey){
					console.log("You pushed: "+e.keyCode);
				if (keys[e.keyCode]) {
					console.log("It has this function:");
					console.log(keys[e.keyCode]);
				}
			}
		});
 	}
 };


PathOS.timeSince = function (date) {
	var val = "",
		d = parseInt(date) || Date.parse(date),
		c = Date.now(),
		i = c - d;

	if( d > 0 ) {
		if (i < 60000 ) {
			val = Math.floor(i / 1000) + "s";
		} else if (i < 3600000 ) {
			val = Math.floor(i / 60000) + "m";
		} else if (i < 86400000 ) {
			val = Math.floor(i / 3600000) + "h";
		} else if ( i < 604800000 ) {
			val = Math.floor(i / 86400000) + "d";
		} else {
			val = Math.floor(i / 604800000) + "w";
		}
	}

	return val;
};




// This function adds a checkbox to your page that uses local storage
// The option will persist over screen refreshes.
// This allows us to flip a class on a set of things.
// Very useful for changing colours or hiding elements.
//
// div = where you want the checkbox to go
// label = the label of the checkbox
// key = the class that we look for, which we put a toggle on.
// 		The added class will be called "key"-toggle
// value = default value of the checkbox (optional, default is false)


PathOS.addOption = function(div, label, key, value){
	var checkbox = div.append('label');
	checkbox.append('input')
		.attr('class', 'option')
		.attr('id', 'option-'+key)
		.attr('type', 'checkbox');
	checkbox.html(checkbox.html()+" "+label+"<br>");
	checkbox.on('change', function(){
		$('.'+key).toggleClass(key+'-toggle');
		localStorage[PatHOS.application+key+'-option'] = document.getElementById("option-"+key).checked;
	});


	// Set it to true if the default is true or if the flag has been set to true.
	if(value) {
		if(typeof localStorage[PatHOS.application+key+'-option'] == 'undefined' ||
				localStorage[PatHOS.application+key+'-option'] === true ||
				localStorage[PatHOS.application+key+'-option'] == 'true'
		){
			checkbox.select('input').attr('checked', value);
			d3.selectAll('.'+key).classed(key+'-toggle', true);
		}
	} else {
		if(typeof localStorage[PatHOS.application+key+'-option'] != 'undefined' &&
			localStorage[PatHOS.application+key+'-option'] === true ||
			localStorage[PatHOS.application+key+'-option'] == 'true'
		){
			checkbox.select('input').attr('checked', true);
			d3.selectAll('.'+key).classed(key+'-toggle', true);
		}
	}
	// The logic here could probably be more elegant, but I cbf thinking right now.

};

/* Initialise stuff, like:
 * Hotkeys,
 * History
 *
 *
 *
 *
 *
 */



// Saftey is for stopping people leaving the page if they've typed stuff.
// To be implimented
PathOS.safety = {
	change: false,
	saved: false,
	init: function(){
		$('textarea,input').keyup(function(){
			PathOS.safety.change = true;
		});

		$('.savebutton').on('click',function(){
			PathOS.safety.saved = true;
		});

		window.onbeforeunload = function() {
			if(PathOS.safety.change && !PathOS.saftey.saved) {
				return 'Testing';
			}
		};
	}
};

// This part of the PathOS library will be used to build a standardised tag system
// Perhaps a taglib might be more appropriate? I'm not sure which is better.
// I'm just going to get it working first, then optimise later.
// DKGM 30 June 2016
PathOS.tags = {
	current_object: false,
	update_object: function(d) {
		PathOS.tags.current_object = d;
		var params = {
			type: PathOS.controller,
			id: d
		};
		if(params.type && d) {
			$.ajax({
				type: 'GET',
				url: `${PathOS.application}/tag/fetchTags?${$.param(params)}`,
                error: function(d){
                    console.log("error", d);
                    alert("You are currently logged out, please refresh the page and log back in.");
                },
				success: function (d){
					console.log(d);
					if(d.error) {
						console.log("There was an error.");
					} else {
						d3.select("#tags").style('display','');
						$("#object_id").text(d.name);
						d3.selectAll("#tags .tagdiv").remove();
						if(d.tags) {
							d.tags.forEach(function(tag){
								PathOS.tags.drawTag(d3.select("#tags .outlined-box"), tag, true);
							});
						} else {
							PathOS.tags.nullObject(d3.select("#tags .outlined-box"));
							//alert("Can't find tags on an unsaved object");
						}
                    }
				},
				cache: false,
				contentType: false,
				processData: false
			});
		}
	},
	buildModule: function(data){
		console.log('Building tags module.');
		var tags = new PathOS.module({
			name: "tags",
			title: "Tags",
			type: "tags",
			data: data,
			closeModule: data.closeModule
		});
	},
	nullObject: function (div) {
		div.insert("div",":first-child").classed("tagdiv", true).append("label").text("You cannot add tags to it.");
		div.insert("div",":first-child").classed("tagdiv", true).append("label").text("Sorry, this object has not be saved.");
	},
	drawTagById: function(box, id, deletable) {
		$.ajax(`${PathOS.application}/Tag/lookUp?id=${id}`, {
			error: function(d){
                alert("You are currently logged out, please refresh the page and log back in.");
            },
			success: function(data){
				PathOS.tags.drawTag(box, data, deletable);
			}
		});
	},
	drawTag: function ( box, data, deletable ) {
		if (data == "fail") {
			alert("Sorry, you cannot set a reserved smart tag");
		} else {
			var div = data.isAuto ? box.insert('div', ':first-child'): box.insert('div', 'textarea');

			div.attr('class', "tagdiv tag-"+data.id)
				.datum(data)
				.classed("isAuto", data.isAuto)
				.on('click', function(d){
					var that = this;

					// If we're editing, and it's not auto, we need to delete this tag.
					if(!d.isAuto && d3.select("#tags").classed("editing")) {
						// DELETE TAG!!!
						var params = {
							type: PathOS.controller,
							objid: PathOS.tags.current_object,
							tagid: d.id
						};
						$.ajax({
							type: "DELETE",
							url: `${PathOS.application}/tag/removeLink?${$.param(params)}`,
                            error: function(d){
                                alert("You are currently logged out, please refresh the page and log back in.");
                            },
                            success: function (result) {
								if(result != 'fail') {
									$(that).remove();
									if(reloadGrid) {
										reloadGrid();
									}
								}
							},
							cache: false,
							contentType: false,
							processData: false
						});
					} else {
						// Otherwise, redirect to the search page.
						d3.select(that).select(".tooltip").classed("dismiss", true);
					}
				});

			div.append('span').text(data.label);


			var text = data.description || "Enter Description Here.";
			var tooltip = div.append('div').classed('tooltip outlined-box', true);

			tooltip.append("button").text("Search").on("click",function(d){
				d3.event.stopPropagation();
				window.location.href = `${PathOS.application}/search?q=${d.label}`;
			});

			tooltip.append("button").text("View").on("click",function(d){
				d3.event.stopPropagation();
				window.location.href = `${PathOS.application}/Tag/Show/${d.id}`;
			});

			tooltip.append("button").text("Edit").on("click", function(d){

			});


			tooltip.append('i').classed('fa fa-close fa-lg', true).on('click', function(){
				d3.event.stopPropagation();
				tooltip.classed("edit", false);
			});

			if (deletable) {
				tooltip.append('i').classed('fa fa-trash fa-lg', true).on('click', function(){
					d3.event.stopPropagation();
					if(confirm('Remove tag "'+data.label+'" from this object?')) {
						var params = {
							type: PathOS.controller,
							objid: PathOS.tags.current_object,
							tagid: data.id
						};
						$.ajax({
							type: "DELETE",
							url: `${PathOS.application}/tag/removeLink?${$.param(params)}`,
                            error: function(d){
                                alert("You are currently logged out, please refresh the page and log back in.");
                            },
                            success: function (result) {
								if(result != 'fail') {
									$('.tag-'+data.id).remove();
									if(reloadGrid) {
										reloadGrid();
									}
								}
							},
							cache: false,
							contentType: false,
							processData: false
						});
					}
				});
			}

			tooltip.append('i').classed('fa fa-check fa-lg', true).on('click', function(){
				d3.event.stopPropagation();
				tooltip.classed("dismiss", false);
			});

			//var text = data.description === "" ? "Enter Description Here." : data.description;
			tooltip.append('p').classed("tt_description", true).text(text);
			tooltip.append('input').attr('value', text).on("keydown", function(){
					var e = d3.event;
					if(e && e.keyCode && e.keyCode == 13) {
						var params = {
							description: $(this).val(),
							id: data.id
						};
						$.ajax(`${PathOS.application}/Tag/putDescription?${$.param(params)}`, {
                            error: function(d){
                                alert("You are currently logged out, please refresh the page and log back in.");
                            },
                            success: function(){
								$(".tag-"+data.id+" input").val(params.description);
								$(".tag-"+data.id+" p").text(params.description);
								tooltip.classed("edit", false);
								tooltip.classed("dismiss", true);
								if(reloadGrid) {
									reloadGrid();
								}
							}
						});
					}
				});



			tooltip.on("click", function(d){
				d3.event.stopPropagation();
				tooltip.classed("edit", true);
			});
		}
	},
	addTag: function (div, tag, controller, id) {
		if( typeof controller == 'undefined') {
			controller = PathOS.controller;
		}
		if( typeof id == 'undefined' ){
			id = PathOS.tags.current_object;
		}
		if( controller && tag && id ) {
			var params = {
				type: controller,
				id: id,
				tag: tag,
				user: PathOS.user
			};
			$.ajax({
				type: "POST",
				url: `${PathOS.application}/tag/addTag?${$.param(params)}`,
                error: function(d){
                    alert("You are currently logged out, please refresh the page and log back in.");
                },
                success: function (d) {
					if (typeof d == "string") {
						alert(d);
					} else {
						if (div.select(".tag-"+ d.id).empty()) {
							PathOS.tags.drawTag(div, d, true);
							$('#tag_text_area').val('');
							if(reloadGrid) {
								reloadGrid();
							}
						}
					}
				},
				cache: false,
				contentType: false,
				processData: false
			});
		} else {
			console.log("We don't have a controller, tag or id.");
			console.log(controller);
			console.log(tag);
			console.log(id);
		}
	}
};



PathOS.printQC = function(data) {
	var label = data.div.append("h4")
				.classed("flag", true);

	if(!data.authorised) {
		label.text("QC Not Set").classed("unknown", true);
	} else if (data.passfailFlag) {
		label.text("QC Passed").classed("passed", true);
	} else if (!data.passfailFlag) {
		label.text("QC Failed");
	}

};




// This code is for controlling IGV.js
// DKGM 31-August-2016

PathOS.igv = {
	loaded: false,
	options: {},
	init: function(igvDiv, dataUrl, sample, panel, samplingDepth) {
		console.log("Running IGV init");

		var baiUrl = dataUrl+sample+".bai",
			bamUrl = dataUrl+sample+".bam",
			vcfUrl = dataUrl+sample+".vcf";

		var baseUrl = dataUrl.split("Pathology")[0],
			panelBedUrl = baseUrl + "Panels/" + panel + "/Amplicon.bed",
			panelTsvUrl = baseUrl + "Panels/" + panel + "/Amplicon.tsv";

		PathOS.igv.div = igvDiv;
		PathOS.igv.options = {
			//showKaryo: "hide",
            locus: "17:7,579,423-7,579,856",
			showNavigation: true,
			showCenterGuide: true,
			genome: "hg19",
			// reference: {
			// 	id: "hg19",
			// 	name: "Human hg19",
			// 	fastaURL: "//dn7ywbm9isq8j.cloudfront.net/genomes/seq/hg19/hg19.fasta", //perhaps we should change this to a peter mac hosted hg19?
			// 	indexFile: `${PathOS.application}/igv/hg19.fasta.fai`,
			// 	cytobandURL: `${PathOS.application}/igv/cytoBand.txt`,
			// 	order: -9999 // This is overridden... you can't set it.... it gets defaulted to -9999, so let's set the order of the other tracks around this value.
			// },
			tracks: [
				{
					name: "VCF",
					url: vcfUrl,
					type: "vcf",
					label: "VCF: "+sample,
					order: -10001
				},
				new PathOS.igv.BAM(baiUrl, bamUrl, sample, samplingDepth),
				{
					name: panel,
					url: panelBedUrl,
					indexURL: panelTsvUrl,
					displayMode: "EXPANDED",
					order: -9998
				},
				{
					url: `${PathOS.application}/igv/hg19-RefSeqGenes.gtf.gz`,
					indexURL: `${PathOS.application}/igv/hg19-RefSeqGenes.gtf.gz.tbi`,
					name: 'hg19 - Gencode v24',
					format: 'gtf',
					order: -9997,
					visibilityWindow: 10000000
				}
			]
		};

	},
	search: function(locus) {
		if(!PathOS.igv.loaded) { // First load! Let's do init stuff
			PathOS.igv.loaded = true;
			PathOS.igv.options.locus = locus;
			igv.createBrowser(PathOS.igv.div, PathOS.igv.options);
			$("#footer-message").remove();
		} else {
			// Browse to the locus that the user wants to see
			igv.browser.search(locus);
		}
	},
	addDirectBAM: function(bamUrl, message, samplingDepth) {
        samplingDepth = samplingDepth || 2500;
        message = message || "";
		if (bamUrl) {
            var baiUrl = bamUrl.slice(0, bamUrl.length - 4)+".bai";
            var newTrack = new PathOS.igv.BAM(baiUrl, bamUrl, sample, samplingDepth);

            if(PathOS.igv.loaded) {
                igv.browser.loadTrack(newTrack);
            } else {
            	if(!PathOS.igv.options.tracks) {
            		alert("Please initialise IGV.js");
				} else {
                    PathOS.igv.options.tracks.push(newTrack);
                    PathOS.notes.add(message+" BAM added to IGV.js");
                }
            }
		} else {
			alert("There was an error, url not found.");
		}
	},
	addBAM: function(sample, dataUrl, samplingDepth) {
		if(sample && dataUrl) {
			var baiUrl = dataUrl+sample+".bai",
				bamUrl = dataUrl+sample+".bam";
			var newTrack = new PathOS.igv.BAM(baiUrl, bamUrl, sample, samplingDepth);

			if(PathOS.igv.loaded) {
				igv.browser.loadTrack(newTrack);
			} else {
				PathOS.igv.options.tracks.push(newTrack);
			}
		} else {
			alert("There was an error, data not found.");
		}
	},
	BAM: function(baiUrl, bamUrl, sample, samplingDepth){

		// If samplingDepth is not provided, set  it to 2500.
		// This is currently used in seqrun/show.gsp
		samplingDepth = samplingDepth || 2500;

		this.indexURL = baiUrl;
		this.url = bamUrl;
		this.label = sample;
		this.type = "bam";
		this.alignmentRowHeight = 1;
		this.maxRows = 99999;
		this.order = -10000;
		this.height = 500;
		this.autoHeight = true;
		this.samplingDepth = samplingDepth;
		this.colorBy = "strand";
		this.negStrandColor = "rgb(150, 150, 230)";
		this.posStrandColor = "rgb(230, 150, 150)";
		this.deletionColor = "black";
		this.skippedColor = "rgb(150, 170, 170)";

		return this;
	}
};








PathOS.pubmed = {
	findCitations: function(text){
		var re = /\[PMID: (\d+)(?:, (\d+))*\]/g;
		var array;
		var results = [];

		while ((array = re.exec(text)) !== null) {
			results = results.concat(array[1].split(', '));
		}

		return results;
	},
	// This function applies a highlight to textareas that have a certain class
	applyHighlight: function(highlightClass){
		$('textarea.'+highlightClass).highlightWithinTextarea(function() {
			return /\[PMID: \d+(, \d+)*\]/g;
		});
	},
	lookUpNewArticle: function( pmid ) {
		console.log("Hey, we're looking for [PMID: %d]", pmid);

		var li =  d3.select("#pubmed-"+pmid).html("");

		li.append("img")
			.classed("loading_logo", true)
			.attr("src", `${PathOS.application}/dist/images/pathos_logo_animated.svg`);

		$.ajax({
			url: `${PathOS.application}/Pubmed/fetch_pmid?pmid=${pmid}`,
			complete: function(d){
				li.select("img.loading_logo").remove();

				if(d.status && d.status == 200) {
					if(true || d.responseJSON && d.responseJSON.citation && d.responseJSON.citation != "Error") {

						var citation = d.responseJSON.citation;
						var title = d.responseJSON.title;

						li.append("h3").text(title);
						li.append("p").text(citation);
						var link = li.append("a").attrs({
							href: "#add_pmid"
						}).text("Save [PMID: "+pmid+"]")
							.classed("newPubmedArticle", true)
							.on("click", function(){
								$.ajax({
									url: `${PathOS.application}/Pubmed/add_pmid?pmid=${pmid}`,
									complete: function(d){
										if(d && d.status && d.status == 500 && d.responseText == "saved") {
											PathOS.notes.addError("Error saving PMID, please try again later.");
										} else {
											PathOS.notes.add("Saved Pubmed Article: "+title);
											link.classed("newPubmedArticle", false)
												.text("[PMID: "+pmid+"]")
												.attr('href', `${PathOS.application}/pubmed?pmid=${pmid}`);
										}
									}
								});
							});
					} else {
						PathOS.notes.addError("https://eutils.ncbi.nlm.nih.gov/ was unreachable.");
					}
				} else {
					li.html("Error, couldn't look up PMID. Please refresh page and try again later.");
					PathOS.notes.addError("Couldn't look up PMID.");
				}
			}
		});



	}
};





PathOS.notes = {
	init: function(){
		PathOS.notes.refresh();
		d3.select("#notifications thead")
			.on('click', function(){
				$("#notifications").toggleClass("shrink");
			});

		$("body").on('click', PathOS.notes.hide);
		$("#notifications").on("click", function(event){
			event.stopPropagation();
		});
        $("#notifications a").on("click", function(event){
            event.stopPropagation();
        });

		PathOS.hotkeys.add(220, toggleNotifications);
		function toggleNotifications() {
			$("#notifications").toggleClass("shrink");
            $("#notifications").removeClass("peek");
			PathOS.notes.refresh();
		}
		d3.selectAll("#notifications tbody tr").classed("read", true);
	},
	hide: function(){
        $("#notifications").addClass("shrink");
        $("#notifications").removeClass("peek");
        PathOS.notes.refresh();
	},
	data: PathOS.data.load("notes", []),
	load: function(){
		PathOS.notes.data = PathOS.data.load("notes", []);
	},
	save: function(notes){
		PathOS.notes.data = notes || d3.selectAll('.notification').data();
		PathOS.data.save("notes", PathOS.notes.data);
	},
	addError: function(words) {
		PathOS.notes.add({
			type: 'error',
			message: words
		});
	},
	add: function(data) {
		data = typeof data === 'object' ? data : {
			message: data
		};

		data.type = data.type || 'message';
		data.page = document.title;
		data.url  = window.location.href;
		data.time = Date.now();

		d3.select("#notifications").classed("peek", true);

		PathOS.notes.data.unshift(data);
		PathOS.notes.save(PathOS.notes.data.slice(0,5));

		PathOS.notes.refresh();

        d3.select("#notifications .notification").classed('read', false);
		setTimeout(function(){
            d3.select("#notifications").classed("peek", false);
		}, 2000);

	},
	refresh: function(){
		PathOS.notes.load();

		var nodes = d3.select("#notifications tbody")
			.selectAll("tr")
			.data(PathOS.notes.data, function(d){
				return d.time;
			});

		nodes.enter()
			.append("tr")
			.classed("notification", true)
			.each(function(d){
				var tr = d3.select(this)
					.on('click', function(d){
						d3.select(this).classed('read', true);
						d.read = d3.select(this).classed('read');
						PathOS.notes.save();
					});

				var td = tr.append("td").text(d.message);
				td.append('br');
				td.append('a').attrs({
					href: d.url
				}).text(d.page);

				td.append('span').classed("timePassed", true);

				var icon = tr.append('td')
					.append('i')
					.classed('fa', true)
					.attrs({
						'aria-hidden': true
					});
				if(d.type == 'error') {
					tr.classed('error', true);
					icon.classed("fa-exclamation-triangle", true);
				} else {
					tr.classed('warning', true);
					icon.classed("fa-sticky-note", true);
				}

			}).merge(nodes)
			.each(function(d){
				var tr = d3.select(this);

				// tr.classed('read', true);
				tr.select('.timePassed')
					.text(PathOS.timeSince(d.time) + " ago");

			});

		nodes.exit().remove();
	},
	markAllAsRead: function(){
		d3.selectAll(".notification")
			//.classed("read", (d) => d.read = true)
			.classed("read", function(d){
				d.read = true;
				return true;
			});
		PathOS.notes.save();
	},
	clearNotes: function(){
		PathOS.notes.save([]);
		PathOS.notes.refresh();
	},
	open: function(){
		d3.select("#notifications").classed("shrink", false);
	},
	latestIsUnread: function(){
		return PathOS.notes.data[0] ? !PathOS.notes.data[0].read : false;
	}
};




































PathOS.init = function(options){
	PathOS.modules.init(options);
	PathOS.hotkeys.init();
	PathOS.history.init();
	// PathOS.safety.init();
};



























