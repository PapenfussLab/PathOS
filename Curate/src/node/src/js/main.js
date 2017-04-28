var PathOS = PathOS || {};

PathOS.version = "PathOS.js build: 29th of March 2017";


// Simple way for PathOS to store data
// Currently uses localStorage but could be upgraded in future.
// DKGM 28-06-2016
PathOS.data = {
	// Provide a key and a default object
	load: function(key, obj) {
		var result = obj || {};
		if (localStorage["PathOS-"+key]) {
			result = JSON.parse(localStorage["PathOS-"+key]);
		}
		return result;
	},
	// Save the object with the key
	save: function(key, obj) {
		localStorage["PathOS-"+key] = JSON.stringify(obj);
	},
	// Clear the key's data
	clear: function(key) {
		delete localStorage["PathOS-"+key];
	},
	clean: function() {
		delete localStorage['PathOS-history'];
		delete localStorage['PathOS-modules'];
	}
};

PathOS.pmClasses = {
	0: "Unclassified",
	1: "C1: Not pathogenic",
	2: "C2: Unlikely pathogenic",
	3: "C3: Unknown Pathogenicity",
	4: "C4: Likely pathogenic",
	5: "C5: Pathogenic"
};

/**
 * Process the date on the front end, so we don't waste server time.
 * Expects a string like: "2016-12-13T13:00:00Z"
 */
PathOS.formatDate = function(string) {
	return string ? new Date(string).toLocaleDateString("en-GB", {day: 'numeric', month: 'long', year: 'numeric'}) : "No date in system";
};

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
		} else if(d.title.toLowerCase().indexOf('sequenced variants list') >= 0) {
			type = 'SeqSample';
		} else if(d.title.toLowerCase().indexOf('curvar') >= 0) {
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
										url: "/PathOS/tag/removeLink?" + $.param(params),
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
										url: "/PathOS/tag/removeLink?" + $.param(params),
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

/**
 *
 * @type {{menuVisible: boolean, settings: {}, map: {}, init: PathOS.modules.init, menu: {show: PathOS.modules.menu.show, hide: PathOS.modules.menu.hide}}}
 */

PathOS.modules = {
	menuVisible: false,
	settings: {},
	map: {},
	init: function( options ) {
		//get settings if they exist, or use default settings

		console.log("loading module settings");

		// Load settings
		PathOS.modules.settings = PathOS.data.load("modules");

		PathOS.controller = options.controller;
		PathOS.action = options.action;

		// If user's settings exist...
		if (options.user) {
			PathOS.user = options.user;
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
			console.log("showing settings!");

			var menubox = d3.select('body')
				.append('div')
				.attr('id', 'overlay')
				.on('click', PathOS.modules.menu.hide)
			.append('div').on('click', function() { d3.event.stopPropagation(); })
				.attr('id', 'moduleMenu')
				.classed('outlined-box', true);

			menubox.append('a').attr('href', '#').on('click', PathOS.modules.menu.hide)
				.append("i").classed("fa fa-close fa-lg", true);

			menu = menubox.append("div");

			header = menu.attr('id', 'mmHeader')
				.append('h1').text("PathOS Options");


			function deselectIGV(){
				d3.selectAll("#IGV-options a").classed("selected", false);
			}

			if(PathOS.user) {
				var igv = menu.append("p")
					.attr('id', "IGV-options")
					.text("IGV.js Options: ");

				igv.append("p").text("In-browser IGV (also known as IGV.js) can load in the background while you browse PathOS. It can also downsample reads, which will make larger runs easier for your computer to handle.");

				igv.append("a")
					.attr('href',"#")
					.attr('id', "svlist-igv-auto")
					.text("Auto Load (no downsampling)")
					.on("click", function(){
						deselectIGV();
						d3.select(this).classed("selected", true);
						PathOS.modules.settings[PathOS.user].svlistIGV = "auto";
						PathOS.data.save("modules", PathOS.modules.settings);
					});
				igv.append("a")
					.attr('href', "#")
					.attr('id',"svlist-igv-downsample")
					.text("Auto Load (downsample to 2500)")
					.on("click", function(){
						deselectIGV();
						d3.select(this).classed("selected", true);
						PathOS.modules.settings[PathOS.user].svlistIGV = "downsample";
						PathOS.data.save("modules", PathOS.modules.settings);
					});
				igv.append("a")
					.attr('href',"#")
					.attr('id', "svlist-igv-ask")
					.text("Ask Before Loading IGV.js")
					.on("click", function(){
						deselectIGV();
						d3.select(this).classed("selected", true);
						PathOS.modules.settings[PathOS.user].svlistIGV = "ask";
						PathOS.data.save("modules", PathOS.modules.settings);
					});


				if(PathOS.modules.settings[PathOS.user]) {
					var svlistIGV = PathOS.modules.settings[PathOS.user].svlistIGV;
					if (typeof svlistIGV  == 'undefined' || svlistIGV == 'ask') {
						d3.select("#svlist-igv-ask").classed("selected", true);
					} else if (svlistIGV == 'downsample' ) {
						d3.select("#svlist-igv-downsample").classed("selected", true);
					} else if (svlistIGV == 'auto' ) {
						d3.select("#svlist-igv-auto").classed("selected", true);
					}
				}


				menu.append("p")
					.text("PathOS History: ")
				.append("a")
					.text("Clear History")
					.attr("href", "#")
					.attr('id', 'clearHistory')
					.on("click", function(){
						PathOS.data.clear("history");
						alert("History Cleared!");
					});
			}

			var links = menu.append('p').attr('id','pathos-menu-links').text("Links to: ");

			links.append("a")
				.attr('href', "https://vm-115-146-91-157.melbourne.rc.nectar.org.au/jira/secure/Dashboard.jspa")
				.text("Jira");
			links.append("span").text(" - ");
			links.append('a').attr('href', 'https://vm-115-146-91-157.melbourne.rc.nectar.org.au/confluence/display/PVS/PathOS+Variant+System').text("Confluence");
			links.append("span").text(" - ");
			links.append('a').attr('href', 'http://pathos.co/help').text("Help");


			//Turn off hotkeys, and make "esc" hide the menu
			PathOS.hotkeys.off();
			$('body').on('keydown', function(e){
				if(e && e.keyCode && e.keyCode == 27 && !$(document.activeElement).is("input") && !$(document.activeElement).is("textarea") && !e.altKey && !e.metaKey && !e.ctrlKey){
					PathOS.modules.menu.hide();
				}
			});
		},
		hide: function(){
			console.log("hiding settings!");

			// Get rid of the menu
			d3.select("#overlay").remove();
			// Bring back the hotkeys...
			PathOS.hotkeys.init();

		}
	}
};


/**
 * This bundle is for the svlist page and showing Curated Variants
 *
 * DKGM 21-October-2016
 *
 * @type {{showCV: PathOS.svlist.showCV, closeCV: PathOS.svlist.closeCV}}
 */

PathOS.svlist = {
	evidence: {
		'pathAloneTruncating':"Truncating variant (nonsense, frameshift, canonical splice site, initiation codon) in a known tumour suppressor gene",
		'pathAloneKnown':"Same missense change as a previously established pathogenic variant",
		'pathStrongFunction':"Well-established in vitro or in vivo functional studies support a deleterious effect on the gene or gene product",
		'pathStrongCase':"Case-control studies show enrichment in cases",
		'pathStrongCoseg':"<b><i>For familial cancer only:</i></b> Proband's family study shows co-segregation with cancer",
		'pathSupportHotspot':"Located near a known mutational hot-spot or within a well-characterised functional domain",
		'pathSupportGene':"Occurs in a gene with high clinical specificity and sensitivity for the cancer",
		'pathSupportInsilico':"Multiple types of computational evidence support a deleterious effect on the gene or gene product (PolyPhen, SIFT, Mutation Taster ,conservation, evolution, splicing)",
		'pathSupportSpectrum':"Type of variant fits known mutation spectrum for the gene",
		'pathSupportGmaf':"Absent from ESP and 1000 Genomes data, or frequency is below highest global minor allele frequency (GMAF) expected for autosomal dominant disease (0.4%)",
		'pathSupportIndel':"In-frame deletion/insertion in a well characterised functional domain",
		'pathSupportNovelMissense':"Novel missense change at an amino acid where a different missense change is pathogenic",
		'pathSupportLsdb':"Noted as pathogenic in a curated locus specific database",
		'pathSupportCoseg':"<b><i>For familial cancer only:</i></b> Proband's family study shows co-segregation with disease",
		'benignAloneGmaf':"Exists in ESP and 1000 Genomes >= 0.4% GMAF",
		'benignAloneHealthy':"<b><i>For familial cancer only:</i></b> For a fully penetrant cancer syndrome, observed in a healthy adult individual",
		'benignStrongFunction':"Well-established in vitro or in vivo functional studies shows no deleterious effect on protein function or splicing",
		'benignStrongCase':"Case control studies show comparable frequencies",
		'benignStrongCoseg':"<b><i>For familial cancer only:</i></b> Variant fails to co-segregate with disease in a family study",
		'benignSupportVariable':"Located in a region without a characterised function or away from known mutation hot-spots",
		'benignSupportInsilico':"Multiple types of computational evidence suggest no impact on gene or gene product (PolyPhen, SIFT, Mutation Taster, conservation, evolution, splicing)",
		'benignSupportSpectrum':"Type of variant does not fit known mutation spectrum for the gene",
		'benignSupportLsdb':"Noted as benign in a curated locus specific database",
		'benignSupportPath':"<b><i>For familial cancer only:</i></b> For a fully penetrant cancer syndrome, observed with another pathogenic variant in the same individual"
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
	},
	createCV: function ( sv ) {
		console.log("Ok, I guess we're making a CV now...");
		console.log("Your sv is: "+sv);
		console.log(this);

		var cc = $("#newCVcc").val();

		var params = {
			id: sv,
			cc: cc
		};
		$.ajax({
			type: "POST",
			url: "/PathOS/CurVariant/newCV?" + $.param(params),
			success: function (d) {
				if (typeof d == "string") {
					alert(d);
				} else {
					console.log(d);
				}
			},
			cache: false,
			contentType: false,
			processData: false
		});
	},
	saveCV: function ( cv ) {
		var report = 'fail';
		var evidence = 'lol';

		report = $("#cv-"+cv+" .report").val();
		evidence = $("#cv-"+cv+" .evidence").val();


		var params = {
			id: cv,
			report: report,
			evidence: evidence
		};
		$.ajax({
			type: "POST",
			url: "/PathOS/CurVariant/updateCV?" + $.param(params),
			success: function (d) {
				if (typeof d == "string") {
					alert(d);
				} else {
					console.log(d);
					//if (div.select(".tag-"+ d.id).empty()) {
					//	PathOS.tags.drawTag(div, d, true);
					//	$('#tag_text_area').val('');
					//}
				}
			},
			cache: false,
			contentType: false,
			processData: false
		});

		console.log("Trying to save: "+cv);
		console.log("report is: "+report);
		console.log("evidence is: "+evidence);
	},

	/**
	 * DKGM 21-November-2016
	 *
	 * Call this function from the svlist page to build an overlay.
	 * The overlay will show all Curated Variants for a specified Sequenced Variant.
	 *
	 * Build the overlay, then do an ajax call for the information.
	 * Key info:
	 * - SeqVariant info
	 * - List of Curated Variants
	 *
	 * @param sv
     */

	showCVs: function( sv ) {
		console.log("Showing all Curated Variants for this Sequenced Variant");


// Build the Overlay
		var cvbox = d3.select('body')
			.append('div')
			.attr('id', 'overlay')
			.on('click', PathOS.svlist.closeCV)
			.append('div').on('click', function() { d3.event.stopPropagation(); })
			.attr('id', 'show-cv')
			.classed('outlined-box', true)
			.classed("container", true);

		cvbox.append('a').attr('href', '#').on('click', PathOS.svlist.closeCV)
			.append("i").classed("fa fa-close fa-lg", true);

		var cvdiv = cvbox.append("div");

		cvdiv.append("img")
			.classed("loading_logo", true)
			.attr("id", "cv-loading")
			.attr("src", "/PathOS/dist/img/pathos_logo_animated.svg");

		$.ajax("/PathOS/SeqVariant/lookUpCVs?id="+sv, {success:function(d){
			d3.select("#cv-loading").remove();
			console.log(d);

			var header = cvdiv.attr('id', 'cvHeader')
				.append('h1').text("Sequenced Variant: "+ d.sv.hgvsc);

			console.log( "SV id is: " + sv );

			var infobox = cvdiv.append("div")
				.attr("id", "sv-info")
				.classed("row", true);

			//infobox.append("div")
			//	.classed("outlined-box", true)
			//	.append("h2")
			//	.text("Info to go in here");
// Add info about the seqvariant
//
			var seqVarBox = infobox.append("div")
				.classed("outlined-box", true)
				.classed("col-xs-5 col-xs-offset-1", true)
				.attr("id", "sv-infobox-div");

			seqVarBox.append("h2").text("Sequenced Variant information:");

			svClinContext = "None";
			if (d.sv.clinContext) {
				svClinContext = d.lookup.context(d.sv.context.id);
			}
			var box1 = {
				"Gene": d.sv.gene,
				"HGVSC": d.sv.hgvsc,
				"HGVSG": d.sv.hgvsg,
				"HGVSP": d.sv.hgvsp,
				"Clinical Context (from sample)": svClinContext,
				"Consequences": d.sv.consequence,
				"Variant Caller": d.sv.varcaller,
				"Amplicon Count": d.sv.numamps,
				"Amplicon Bias": d.sv.ampbias
			};
			var box2 = {
				"Variant Frequency": d.sv.varFreq,
				"Variant Depth": d.sv.varDepth,
				"Panel Var %": d3.format(".4")(d.sv.varPanelPct)+"%",
				"dbSNP": d.sv.dbsnp,
				"GMAF %": d.sv.gmaf,
				"ESP %": d.sv.esp,
				"ExAC %": d.sv.exac,
				"Cosmic": d.sv.cosmic ? "<a target='_blank' href='/PathOS/seqVariant/cosmicAction?id='"+ d.sv.id+" title='"+ d.sv.cosmicOccurs+"'>COSM"+ d.sv.cosmic+"</a>" : "",
				"Exon": d.sv.exon,
				"Cytoband": d.sv.cytoband,
				"CADD Raw": d3.format("(.2f")(d.sv.cadd),
				"CADD Scaled": d3.format("(.2f")(d.sv.cadd_phred)
			};

			drawTable(box1, seqVarBox);

			var otherBox = infobox.append("div")
				.classed("outlined-box", true)
				.classed("col-xs-5", true)
				.attr("id", "cv-infobox-div");

			drawTable(box2, otherBox);

// Add info about the Preferred Curated Variant
//			var preferredCVbox = infobox.append("div")
//				.classed("outlined-box", true)
//				.classed("col-xs-3", true)
//				.attr("id", "cv-infobox-div");
//			preferredCVbox.append("h2").text("Curated Variant Information:");
//
//			svClinContext = "None";
//			if (d.sv.clinContext) {
//				svClinContext = d.lookup.context(d.sv.context.id);
//			}
//
//			var tempPmClass = null;
//			if (d.preferred && d.preferred.pmClass) {
//				tempPmClass = d.preferred.pmClass;
//			}
//
//			var preferredData = {
//				"Classification": tempPmClass,
//				"Classified By": d.lookup.classified,
//				"Authorised By": d.lookup.authorised,
//				"Or maybe": "The person who classified this variant",
//				"And also": "The people who verified it"
//			};
//			drawTable(preferredData, preferredCVbox);
//
//
//
//			var buttons = infobox.append("div")
//				.attr("id", "cv-buttons")
//				.classed("xs-col-4", true)
//				.classed("outlined-box", true);
//
//
//			var newCV = buttons.append("div").classed("outlined-box", true);
//
//			newCV.append("h4").text("Add a new CV");
//			newCV.append("input")
//				.attr("id", "newCVcc")
//				.attr("placeholder", "Pick a Clinical Context");
//
//			var ccArray = [];
//			d.lookup.listOfCC.forEach(function(cc){
//				ccArray.push(cc.description);
//			});
//			console.log(ccArray);
//
//			$("#newCVcc").autocomplete({source: ccArray});
//
//			newCV.append("a")
//				.attr("href","#none")
//				.attr("onclick", "PathOS.svlist.createCV("+ d.sv.id+")")
//				.text("Create a new CV");
//
//
//
//
//
//
//			buttons.append("a").attr("href","#na").text("Add new CV").classed("cv-button", true);
//			buttons.append("br");
//			buttons.append("a").attr("href","#na").text("Another button to do another thing").classed("cv-button", true);
//			buttons.append("br");
//			buttons.append("a").attr("href","#na").text("More buttons!").classed("cv-button", true);



			cvdiv.append("h1").text("Curated Variants:");
			var row = cvdiv.append("div")
				.classed("row", true)
				.attr("id", "cv-list");

			var table = row.append('table');

			var thead = table.append("thead").append("tr");

			thead.append("th").text("Context").style("width", "15%");
			thead.append("th").text("Report Description");
			thead.append("th").text("Evidence Description");
			thead.append("th").text("Classification");

			var tbody = table.append("tbody");
			var cv = null;
			var evidence = null;
			if(d.generic) {
				if(d.currentCV && d.currentCV.id != d.generic.id) {
					addCVrow(d.generic, false);
					addCVrow(d.currentCV, true);
				} else {
					addCVrow(d.generic, true);
				}
			}
			d.otherCVs.forEach(function(data){
				addCVrow(data, false);
			});

			function addCVrow(data, highlight){
				var label = "Generic",
					id = data.id;
				if(data.clinContext) {
					label = d.lookup.context[data.clinContext.id];
				}
				cv = tbody.append("tr")
					.classed("current-context-cv", highlight);
				if(highlight){
					cv.append("td").append("h2").html("Current Context<br>")
						.append("a")
						.attr("href", "/PathOS/curVariant/show?id=" + data.id)
						.text(label);
				} else {
					cv.append("td")
						.append("a")
						.attr("href", "/PathOS/curVariant/show?id=" + data.id)
						.text(label);
				}
				cv.append("td").append("textarea").attr("readonly", true).style("width", "100%").text(data.reportDesc);
				cv.append("td").append("textarea").attr("readonly", true).style("width", "100%").text(data.evidence.justification);

				evidence = cv.append("td");

				var pmClass = data.pmClass.split(":")[0];
				evidence.append("p")
					.text(data.pmClass)
					.classed("cvlabel cv-"+pmClass, true);

				var count = 0;

				var button = evidence.append("a")
					.attr("href", "#none")
					.on("click", function(d){
						console.log("cliccckinggggg");
						$("#evidence-list-"+id).toggleClass("hidden");
					});

				var list = evidence.append("ul")
					.attr("id", "evidence-list-"+id)
					.classed("hidden", true);

				Object.keys(PathOS.svlist.evidence).forEach(function(key){
					if(data.evidence[key]) {
						count++;
						list.append('li').html(PathOS.svlist.evidence[key]);
					}
				});
				button.text("Show Evidence ("+count+")");
			}








            //
            //
            //
			//var name = row.append("div").classed("col-xs-1 outlined-box", true);
            //
			//var left = row.append("div").classed("col-xs-3 outlined-box", true);
            //
			//var middle = row.append("div").classed("col-xs-3 outlined-box", true);
            //
			//var right = row.append("div").classed("col-xs-5 outlined-box", true);
            //
			//name.append("h4").text("Name")
			//	.classed("cv-header", true);
			//left.append("h4").text("Generic Curated Variant")
			//	.classed("cv-header", true);
			//middle.append("h4").text("Preferred Curated Variant")
			//	.classed("cv-header", true);
            //
			//right.append("h4").text("Other Curated Variants")
			//	.classed("cv-header", true);
            //
            //
            //
			//name.append("div").append("p").text("Clinical Context").classed("cc-header", true);
			//name.append("div").append("p").text("Report Description").classed("report", true);
			//name.append("div").append("p").text("Evidence").classed("evidence", true);
            //
            //
            //
			//drawCVs(
			//	left, //.append("table").append("tbody").append("trow").append("td").append("div"),
			//	d.generic,
			//	d.lookup
			//);
            //
			//drawCVs(
			//	middle, //.append("table").append("tbody").append("trow").append("td").append("div"),
			//	d.preferred,
			//	d.lookup
			//);
            //
			//var otherCVs = right.append("table").append("tbody").append("trow");
			//d.otherCVs.forEach(function(cv){
			//	drawCVs(
			//		otherCVs.append("td").classed("cv-td", true).append("div"),
			//		cv,
			//		d.lookup
			//	);
			//});
            //



		}});

		function drawCVs(div, cv, lookup) {
			console.log(cv);

			div.attr("id", "cv-"+cv.id);

			var cc = "Generic";
			if (cv.clinContext !== null) {
				cc = lookup.context[cv.clinContext.id];
			}

			// Clinical Context...
			div.append("div")
				.append("p")
				.text(cc);

			div.append("a")
				.attr("href","#na")
				.attr("onclick", "PathOS.svlist.saveCV("+ cv.id +")")
				.text("Save CV")
				.classed("cv-button", true);

			// Report Description...
			div.append("div")
				.append("textarea")
				.classed("report", true)
				.html(cv.reportDesc);

			// Evidence Justification...
			div.append("div")
				.append("textarea")
				.classed("evidence", true)
				.html(cv.evidence.justification);
		}

		function drawTable(info, infobox){
			infobox.select("table").remove();

			var table = infobox.append("table")
				.attr("id", "svInfoTable")
				.classed("infoTable", true);

			var tbody = table.append("tbody");

			Object.keys(info).forEach(function(row){
				var r = tbody.append("tr");
				r.append("td").html(row).classed("property-label", true);
				r.append("td").html(info[row]).classed("property-value", true);
			});
		}




		//Turn off hotkeys, and make "esc" hide the menu
		PathOS.hotkeys.off();
		$('body').on('keydown', function(e){
			if(e && e.keyCode && e.keyCode == 27 && !$(document.activeElement).is("input") && !$(document.activeElement).is("textarea") && !e.altKey && !e.metaKey && !e.ctrlKey){
				PathOS.svlist.closeCV();
			}
		});
	},
	closeCV: function(){
		console.log("closing CV!");

		// Get rid of the menu
		d3.select("#overlay").remove();
		// Bring back the hotkeys...
		PathOS.hotkeys.init();

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
		d = Date.parse(date),
		c = Date.now(),
		i = c - d;

	if( d > 0 ) {
		if (i < 3600000 ) {
			val = Math.floor(i / 60000) + "m";
		} else if (i < 86400000 ) {
			val = Math.floor(i / 3600000) + "h";
		} else {
			val = Math.floor(i / 86400000) + "d";
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
		localStorage[key+'-option'] = document.getElementById("option-"+key).checked;
	});


	// Set it to true if the default is true or if the flag has been set to true.
	if(value) {
		if(typeof localStorage[key+'-option'] == 'undefined' ||
				localStorage[key+'-option'] === true ||
				localStorage[key+'-option'] == 'true'
		){
			checkbox.select('input').attr('checked', value);
			d3.selectAll('.'+key).classed(key+'-toggle', true);
		}
	} else {
		if(typeof localStorage[key+'-option'] != 'undefined' &&
			localStorage[key+'-option'] === true ||
			localStorage[key+'-option'] == 'true'
		){
			checkbox.select('input').attr('checked', true);
			d3.selectAll('.'+key).classed(key+'-toggle', true);
		}
	}
	// The logic here could probably be more elegant, but I cbf thinking right now.

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
				url: "/PathOS/tag/getTags?" + $.param(params),
				success: function (d){
					console.log(d);
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
		$.ajax("/PathOS/Tag/lookUp?id="+id, {success: function(data){
			PathOS.tags.drawTag(box, data, deletable);
		}});
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
							url: "/PathOS/tag/removeLink?" + $.param(params),
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
				window.location.href = "/PathOS/search?q="+d.label;
			});

			tooltip.append("button").text("View").on("click",function(d){
				d3.event.stopPropagation();
				window.location.href = "/PathOS/Tag/Show/"+d.id;
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
							url: "/PathOS/tag/removeLink?" + $.param(params),
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
						$.ajax("/PathOS/Tag/putDescription?"+ $.param(params), {
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
				url: "/PathOS/tag/addTag?" + $.param(params),
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

    //
    //
	//if(data.passfailFlag) {
	//	qcLabel.text("QC Passed").classed("passed", true);
	//} else {
	//	qcLabel.text("QC Failed");
	//}
    //
	//console.log("data is: ");
	//console.log(data);
	//if(data.authorisedQc === null) {
	//	authorisedLabel.text("QC Authorisation Not Set").classed("unknown", true);
	//} else if (data.authorisedQcFlag) {
	//	authorisedLabel.text("QC Authorised").classed("passed", true);
	//} else if (!data.authorisedQcFlag) {
	//	authorisedLabel.text("QC Not Authorised");
	//}
};




// This code is for controlling IGV.js
// DKGM 31-August-2016

PathOS.igv = {
	loaded: false,
	options: {},
	init: function(igvDiv, dataUrl, sample, panel, samplingDepth) {

		var baiUrl = dataUrl+sample+".bai",
			bamUrl = dataUrl+sample+".bam",
			vcfUrl = dataUrl+sample+".vcf";

		var baseUrl = dataUrl.split("Pathology")[0],
			panelBedUrl = baseUrl + "Panels/" + panel + "/Amplicon.bed",
			panelTsvUrl = baseUrl + "Panels/" + panel + "/Amplicon.tsv";

		PathOS.igv.div = igvDiv;
		PathOS.igv.options = {
			showKaryo: "hide",
			showNavigation: true,
			showCenterGuide: true,
			reference: {
				fastaURL: "//dn7ywbm9isq8j.cloudfront.net/genomes/seq/hg19/hg19.fasta", //perhaps we should change this to a peter mac hosted hg19?
				indexFile: "/PathOS/igv/hg19.fasta.fai",
				cytobandURL: "/PathOS/igv/cytoBand.txt",
				order: -9999 // This is overridden... you can't set it.... it gets defaulted to -9999, so let's set the order of the other tracks around this value.
			},
			tracks: [
				{
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
					url: '/PathOS/igv/hg19-RefSeqGenes.gtf.gz',
					indexURL: '/PathOS/igv/hg19-RefSeqGenes.gtf.gz.tbi',
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
	// This function applies a highlight to textareas that have a certain class
	applyHighlight: function(highlightClass){
		$('textarea.'+highlightClass).highlightWithinTextarea(function() {
			return /\[PMID: (?:\d+(?:, )?)+\]/g;
		});
	}
};























PathOS.init = function(options){
	PathOS.modules.init(options);
	PathOS.hotkeys.init();
	PathOS.history.init();
	//PathOS.safety.init();
};



























