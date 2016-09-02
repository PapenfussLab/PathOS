var PathOS = PathOS || {};

PathOS.version = "PathOS.js build: 26th of May 2016";


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
		delete localStorage.PathOSHistory;
		delete localStorage['PathOS-modules'];
	}
};


/*
 * This function adds a module to the sidebar.
 *
 *
 *
 *
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
	if(div[0][0] === null) {
		div = this.div = d3.select("#sidebar")
			.append("div")
			.classed("module", true)
			.attr("id", config.name);
	}

	// Get the title or create one.
	var title = div.select(".moduletitle");
	if(title[0][0] === null) {
		title = div.append('table').classed("moduletitle", true).append('tr');

		title.append('td').classed("modulelabel", true).datum(config).on("click", that.toggle).append("a").attr({
			href: "#" + config.name
		}).append("h1").text(config.title);
	} else {
		title.select('td').classed("modulelabel", true).datum(config).on("click", that.toggle);
	}

	title.insert('td','td').datum(config).on("click", that.toggle).append("a").attr({
		href: "#"+config.name
	}).append("i")
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

				var edit = title.append('td').attr({
					id: 'tags_edit_button'
				}).on('click',function(){
						$("#tags").toggleClass("editing");

						$('#tags_edit_button i').toggleClass('fa-pencil-square-o');
						$('#tags_edit_button i').toggleClass('fa-pencil-square');


					})
					.append("a").attr({
					href: "#"+config.name
				});

				//edit.append('p').text("(Editing)");
				edit.append('span').html("(Editing)&nbsp;");
				edit.append("i").classed("button fa fa-pencil-square-o", true);

				title.append('td').append("a").attr({
					href: "#"+config.name
				}).append("i")
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

				var box = content.append('div').classed('fb-box tags_field', true)
					.attr("id", 'moduleTagBox')
					.on('click', function(){
						$('#tag_text_area').focus();
					});

				config.data.tags.forEach(function(tag){
					PathOS.tags.drawTag(box, tag, true);
				});

				$(box.append('textarea').attr({
					id: 'tag_text_area',
					placeholder: 'Enter Tags Here'
				})).autocomplete({
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


				body.append('span').append('a').attr({
					href:'#'
				}).text("See more").on('click', function(){
					PathOS.history.more().forEach(buildHistoryRow);

					$(this).remove();
				});
				break;
			case 'established':
				content.remove();
				break;
			default:
				var ul = content.append("ul")
					.style({
						clip: "rect(0px, 1000px, 0px, 0px)"
					});

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
					hide: {}
				};
				PathOS.data.save("modules", PathOS.modules.settings);
			}
		}



		d3.select("#sidebar-footer").append('span')
			.append('a').attr({
			href: '#'
		}).on('click', function(d){

			if(PathOS.modules.menuVisible) {
				PathOS.modules.menu.hide();
			} else {
				PathOS.modules.menu.show();
			}
		}).append('i').attr({
			class: 'fa-lg fa fa-cog',
			'aria-hidden': true
		});
	},
	menu: {
		show: function(){
			console.log("showing settings!");

			var menu = d3.select('body').append('div').attr({
					id: 'overlay'
				}).on('click', PathOS.modules.menu.hide)
			.append('div').on('click', function() { d3.event.stopPropagation(); })
				.attr({
					id: 'moduleMenu',
					class: 'fb-box'
				});

			menu.append('a').attr('href', '#').on('click', PathOS.modules.menu.hide)
				.append("i").classed("fa fa-close fa-lg", true);

			header = menu.append('div').attr({
				id: 'mmHeader'
			}).append('h1').text("Put a menu here, when we have options that need to be set.")
				.append('p').text("Or maybe some help info or something? I dunno.");

			header.append('p').append("a").attr({
				href: "https://115.146.86.118/jira/secure/Dashboard.jspa"
			}).text("Here, have a link to Jira.");

			header.append('p').append('a').attr('href', 'https://115.146.86.118/confluence/display/PVS/Path-OS+Variant+System').text("And here's one for Confluence.");


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
	params = {};
	var string = window.location.search.substring(1);
	var arr = string.split("&");
	arr.forEach(function(q){
		if(q.indexOf("=") > 0) {
			var thing = q.split("=");
			params[thing[0]] = thing[1].indexOf(',') >= 0 ? thing[1].split(',') : thing[1];
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

var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'June', 'July', 'Aug', 'Sept', 'Oct', 'Nov', 'Dec'];

PathOS.date = function(d, size) {
	var date = '';
	if (Date.parse(d) > 0) {
		var arr = d.split('T')[0].split('-');
		if(size && size == 'full') {
			date = arr[2] + '\xa0' + months[parseInt(arr[1])-1] + '\xa0' + arr[0];
		} else {
			date = arr[2] + '\xa0' + months[parseInt(arr[1])-1];
		}
	}
	return date;
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
		.attr({
			class: 'option',
			id: 'option-'+key,
			type: 'checkbox'
		});
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
		localStorage.PathOSHistory = JSON.stringify(PathOS.history.json);
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
							PathOS.tags.drawTag(d3.select("#tags .fb-box"), tag, true);
						});
					} else {
						PathOS.tags.nullObject(d3.select("#tags .fb-box"));
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

			div.attr({
				class: "tagdiv tag-"+data.id
			}).datum(data).classed("isAuto", data.isAuto)
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

			div.append('span').attr({
			}).text(data.label);


			var text = data.description || "Enter Description Here.";
			var tooltip = div.append('div').classed('tooltip fb-box', true);

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
			tooltip.append('input').attr({
				value: text
			}).on("keydown", function(){
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
						if (div.select(".tag-"+ d.id)[0][0] === null) {
							PathOS.tags.drawTag(div, d, true);
							$('#tag_text_area').val('');
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

	if(data.authorised === null) {
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
			igv.createBrowser(PathOS.igv.div, PathOS.igv.options);
			$("#footer-message").remove();
		}
		// Browse to the locus that the user wants to see
		igv.browser.search(locus);
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
























PathOS.init = function(options){
	PathOS.modules.init(options);
	PathOS.hotkeys.init();
	PathOS.history.init();
	//PathOS.safety.init();
};



























