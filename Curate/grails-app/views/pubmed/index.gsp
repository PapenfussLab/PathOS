%{--
  - Author: David Ma
  -         david.ma@petermac.org
  -         2016-2-4
  --}%

<%@ page import="grails.converters.JSON; org.petermac.pathos.curate.Pubmed" %>
<!DOCTYPE html>
<html>
<head>
	<meta name="layout" content="main">
	<g:set var="entityName" value="${message(code: 'pubmed.label', default: 'Pubmed table')}"/>
	<title><g:message code="default.list.label" args="[entityName]"/></title>

	<r:require modules="export"/>


	%{--CSS Files--}%
	<link href="<g:resource plugin='easygrid' dir='jquery-ui-1.11.0' file='jquery-ui.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
	<link href="<g:resource dir='css/jquery-ui-1.11.0.custom' file='jquery-ui.theme.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
	<link href="<g:resource plugin='easygrid' dir='jquery.jqGrid-4.6.0/css' file='ui.jqgrid.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
	<link href="<g:resource plugin='easygrid' dir='jquery.jqGrid-4.6.0/plugins' file='ui.multiselect.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />

	%{--Javascript Files--}%
	<script src="/PathOS/static/bundle-bundle_easygrid-jqgrid-dev_head.js" type="text/javascript" ></script>
	<g:javascript src='jquery/jquery.jgrowl.js' plugin='spring-security-ui'/>


	<parameter name="hotfix" value="off" />


	<style type="text/css">
	.ui-jqgrid .ui-jqgrid-htable th     { vertical-align: top; }
	.ui-jqgrid .ui-jqgrid-htable th div { height: 30px; }


	.ui-jqgrid tr.jqgrow td {
		white-space: normal !important;
		height:auto;
		vertical-align:text-top;
		padding: 4px;
		line-height: 1.15em;
	}

	/*David's CSS*/
	.cursorWait {
		cursor: wait;
	}

	th[aria-selected="true"] {
		background: linear-gradient(#E4F2FB,#E4F2FB,#AECBE4,#AECBE4) !important;
	}

	.ui-state-lowlight,
	.ui-widget-content .ui-state-lowlight,
	.ui-widget-header .ui-state-lowlight {
		border: 1px solid #5ed5da;
		background: #B7E0FF;
		color: #207776;
	}






	/*Copying this from svlist.gsp*/

	<%--loading jGrowl in a stylesheet breaks the colours on our jqgrid. i'm still not sure why, but a workaround
    is putting the CSS inline instead of loading it from 'spring-security-ui css jgrowl --%>
	div.jGrowl{z-index:9999;color:#fff;font-size:12px;position:absolute}
	body > div.jGrowl{position:fixed}
	div.jGrowl.top-left{left:0;top:0}
	div.jGrowl.top-right{right:0;top:0}
	div.jGrowl.bottom-left{left:0;bottom:0}
	div.jGrowl.bottom-right{right:0;bottom:0}
	div.jGrowl.center{top:0;width:50%;left:25%}
	div.center div.jGrowl-notification,div.center div.jGrowl-closer{margin-left:auto;margin-right:auto}
	div.jGrowl div.jGrowl-notification,div.jGrowl div.jGrowl-closer{background-color:#000;opacity:.85;-ms-filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=85);filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=85);zoom:1;width:235px;padding:10px;margin-top:5px;margin-bottom:5px;font-family:Tahoma,Arial,Helvetica,sans-serif;font-size:1em;text-align:left;display:none;-moz-border-radius:5px;-webkit-border-radius:5px}
	div.jGrowl div.jGrowl-notification{min-height:40px}
	div.jGrowl div.jGrowl-notification,div.jGrowl div.jGrowl-closer{margin:10px}
	div.jGrowl div.jGrowl-notification div.jGrowl-header{font-weight:700;font-size:.85em}
	div.jGrowl div.jGrowl-notification div.jGrowl-close{z-index:99;float:right;font-weight:700;font-size:1em;cursor:pointer}
	div.jGrowl div.jGrowl-closer{padding-top:4px;padding-bottom:4px;cursor:pointer;font-size:.9em;font-weight:700;text-align:center}






	</style>

</head>

<body>

<div id="list-pubmed" class="content scaffold-list" role="main"
	 style="white-space: nowrap; overflow-x:auto">
	<br>
	<input style="margin-left: .5em" placeholder="Enter a PMID" type="text" id="pmid_box">
	<input type="button" value="Look up Pubmed Article" onclick="findPMID()">

	<g:if test="${flash.message}">
		<div class="message" role="status">${flash.message}</div>
	</g:if>

	<div style="margin: .5em; width: 100%; overflow: auto">
		<grid:grid  name="pubmed" >
			<grid:set caption='Pubmed table' width="100%"/>
			<grid:set col="pmid"         width="120" />
			<grid:set col="date"         width="120" />
			<grid:set col="journal"         width="120" />
			<grid:set col="title"         width="120" />
			<grid:set col="authors"         width="120" />
			<grid:set col="affiliations"         width="120" />
			<grid:set col="abstrct"         width="120" />
		</grid:grid>
		<grid:exportButton name="pubmed" formats="['csv', 'excel']"/>
	</div>
</div>


<r:script>
var month = {
    "01": "Jan",
    "02": "Feb",
    "03": "Mar",
    "04": "Apr",
    "05": "May",
    "06": "June",
    "07": "July",
    "08": "Aug",
    "09": "Sept",
    "10": "Oct",
    "11": "Nov",
    "12": "Dec"
}


datastore = {
    add: function(obj){
        var pmid = obj[10];
        datastore[pmid] = {
            id: obj[0],
            tags: obj[1],
            authors: obj[2],
            date: obj[3],
            title: obj[4],
            journal: obj[5],
            volume: obj[6],
            issue: obj[7],
            pages: obj[8],
            affiliations: obj[9],
            pmid: obj[10],
            doi: obj[11],
            pdf: obj[12],
            abstract: obj[13],
        }
    },
    addJSON: function(d){
        var authors = d.authors
        datastore[d.pmid] = d;
        datastore[d.pmid].affiliations = firstAffiliation(authors);
        datastore[d.pmid].authors = parseAuthors(authors);
    }
};

var colModel = [
	{
		name: "id",
		hidden: true,
	},
    {
        name: "tags",
        label: "Tags",
        hidden: true,
        formatter: function(val, opt, obj) {
            var string = "";
            if(val){
                var array = []
                val.forEach(function(d){
                    array.push('<a href="/PathOS/search/search?q='+d.label+'">'+d.label+'</a>');
                });
                string = array.join(", ")
            }
            return string;
        }
    },
    {
        name: "authors",
        label: "Authors",
        width: 200,
        formatter: function(val, opt, obj) {

            if (Array.isArray(obj)) {
                datastore.add(obj);
            }
            var result = "";
            if(val){
                result = val.split(",").join(", ");
            }
            return val;
        }
    },
    {
        name: "date",
        label: "Date",
        width: 60,
        align: "center",
        search: false,
        formatter: function(val, opt, obj) {
            var result = "";
            if (val) {
                result = month[val.split("-")[1]]+" "+val.split("-")[0];
            }
            return result;
        }
    },
    {
        label:"Title",
        name:"title",
        width: 370
    },
    {
        name: "journal",
        label: "Journal",
        width: 120
    },
    {
        name: "volume",
        label: "Vol (Iss) : Pages",
        search: false,
        width: 95,
        formatter: function (val, opt, obj){
            var pmid = Array.isArray(obj) ? obj[10] : obj.pmid
                d = datastore[pmid];
            var result = "";
            if(d.volume){
                result += d.volume;
            }
            if(d.issue) {
                result += "("+d.issue+")";
            }
            if(d.pages) {
                result += ":"+ d.pages;
            }
            return result;
        }
    },
    {
        name: "issue",
        search: false,
        hidden: true
    },
    {
        name: "page",
        search: false,
        hidden: true
    },
    {
        name: "affiliations",
        label: "Affiliations",
        width: 240
    },
    {
        name: "pmid",
		formatter: function(pmid, opt, obj) {
			var string = ""
			if(pmid != null && pmid != "null") {
				string = "<span style='line-height:1.5em;'><a href='https://www.ncbi.nlm.nih.gov/pubmed/"+pmid+"' target='_blank'>"+pmid+"</a></span>";
			}
            return string
        }
    },
    {
        name: "doi",
        formatter: function(doi, opt, obj) {
        	var string = "";
            if(doi != null && doi != "null") {
                string = "<span style='line-height:1.5em;'><a href='http://dx.doi.org/"+encodeURI(doi)+"' target='_blank'>"+doi+"</a></span>";
            }
            return string
        }
    },
    {
        name: "pdf",
        formatter: function(pdf, opt, obj) {
        	var string = "";
        	if(pdf != "null" && pdf != null) {
        		string = "<span style='line-height:1.5em;'><a href='http://bioinf-ensembl.petermac.org.au/Pubmed/"+ d.pdf+"' target='_blank'>[PDF]</a></span>";
        	}
            return string
        }
    },
    {
        name: "abstrct",
        label: "Abstract",
        width: 300,
        formatter: function(val, opt, obj) {
            if(val) {
                var string = val.substring(0, 150) + "...";
                if (string == "...") {
                    string = "No abstract available."
                }
                return string;
            } else {
                return "";
            }
        },
        cellattr: function(rowId, val, rawObject) {
            var string = val;
            if (val && rawObject && rawObject[11]) {
                string = "title='" + rawObject[11].replace(/'/gi, "&apos") + "'";
            }
            return string
        }
    }
];


jQuery("#pubmed_table").jqGrid({
    url: '/PathOS/pubmed/pubmedRows',
    loadError: easygrid.loadError,
    pager: '#pubmedPager',
    viewrecords: true,
    rowList: [10,25,50,100],
    height: "100%",
    sortable: true,
    width: "100%",
    caption: "Pubmed table",
    datatype: "json",
    multiSort: true,
    rowNum: 10,
    sortname: "date",
    sortorder: "desc",
    cmTemplate: {
        "searchoptions": {
            "clearSearch": false,
            "sopt": ["cn", "nc", "eq", "ne", "bw", "ew"]
        },
        "editable": true,
        "width": "120",
        "search": true
    },
    colModel: colModel,
	onSelectRow: function(id){
	   PathOS.tags.update_object(getCurrentpmid());
	}
});



jQuery('#pubmed_table')
    .jqGrid('filterToolbar', {
                "searchOperators":false,
                "stringResult":true
            });



jQuery('#pubmed_table')
    .jqGrid('navGrid', '#pubmedPager', {
        "add": false,
        "view": true,
        "edit": false,
        "del": false,
        "search": true,
        "refresh": true
    },
    {}, //edit
    {
        "afterSubmit": easygrid.afterSubmit('pubmed_table'),
        "errorTextFormat": easygrid.errorTextFormat('pubmed_table'),
        "reloadAfterSubmit": false,
        "closeAfterAdd": true
    }, //add
    {}, //delete
    {
        "multipleSearch": true,
        "multipleGroup": true,
        "showQuery": true,
        "caption": "Multi-clause Searching",
        "closeAfterSearch": true,
        "sopt": ["eq", "ne", "lt", "le", "gt", "ge", "bw", "bn", "ew", "en", "cn", "nc", "nu", "nn"]
    }, //search
    {
        "caption": "Pubmed Data",
        "closeOnEscape": true,
        afterclickPgButtons: fixNavGrid,
        beforeShowForm: fixNavGrid
    } //view
);

$("#pubmed_table_date").attr("aria-selected", "true");

/*
 *  jqgrid has something called a "navGrid"
 *  More info here: http://www.trirand.com/jqgridwiki/doku.php?id=wiki:navigator
 *
 *  Easygrid has simple defaults to set up a navGrid but it's not very pretty.
 *  We use this function to clean up the navGrid and make it behave properly.
 *
 *
 *
 *
 *
 *
 ***/

var nav_data = {};
function fixNavGrid(){
    // This function cleans up the navgrid
    var pmid = $("#v_pmid span a").html();
    nav_data = datastore[pmid];

    window.history.replaceState({}, pmid, "/PathOS/Pubmed?pmid="+pmid);
	document.title = pmid + " - Show PubMed";

	PathOS.history.add({
		title: document.title,
		url: window.location.href,
		time: Date()
	});

    // Change the size of the navgrid, and make it word wrap
    //$("#viewmodpubmed_table").width(600).height(800);
    //$("#ViewGrid_pubmed_table").height(800);
    $(".CaptionTD").width(75);
    $(".DataTD").css("white-space", "normal");
    $(".DataTD").css("padding","5px");

    // Rearrange the fields
    $("#trv_title").insertBefore("#trv_authors").css({
        "font-weight": "bold",
        "font-size": "12px"
    });

    // Get rid of a stupid whitespace inserted by easygrid..?
    $("#v_pmid").html("<span>"+$("#v_pmid span").html()+"</span>")
    $("#v_doi").html("<span>"+$("#v_doi span").html()+"</span>")
    $("#v_date").html("<span>"+$("#v_date span").html()+"</span>")
    $("#v_journal").html("<span>"+$("#v_journal span").html()+"</span>")
    $("#v_title").html("<span>"+$("#v_title span").html()+"</span>")
    $("#v_authors").html("<span>"+$("#v_authors span").html()+"</span>")
    $("#v_affiliations").html("<span>"+$("#v_affiliations span").html()+"</span>")
    $("#v_volume").html("<span>"+$("#v_volume span").html()+"</span>")
    $("#v_issue").html("<span>"+$("#v_issue span").html()+"</span>")
    $("#v_pages").html("<span>"+$("#v_pages span").html()+"</span>")
    $("#v_abstrct").html("<span>"+$("#v_abstrct span").html()+"</span>")
    $("#v_pdf").html("<span>"+$("#v_pdf span").html()+"</span>")

    $("#v_abstrct span").html(datastore[pmid].abstract);


    $("#pdf_form").remove();
    $("#tag_form").remove();

    $("#v_pdf").prepend('<form id="pdf_form" action="upload_pdf" style="float:right; margin-top: 10px; margin-right: 10px;" method="POST"><input type="file" accept=".pdf" id="pdf" name="pdf" disabled/><input type="text" id="pdf_pmid" name="pdf_pmid" style="display:none;"/><input type="button" value="Upload" onclick="upload_pdf()"></form>');

	$.ajax({
		url: "/PathOS/pubmed/check_pmid?pmid="+pmid,
		success: function(d){
			if(d == "null") {
				var save = '<br><a href="#" onclick="addPMID('+pmid+');return false">[SAVE ROW]</a>';
				var old_html = $("#v_pmid>span>span").html().replace("<br>","");

				$("#v_pmid>span").html("<p class='ui-state-lowlight' style='width: 80px; text-align: center;'>"+old_html+save+"</p>");
			}
		}
	})

       setTimeout( function(){
           $("#pdf").removeAttr("disabled");
           $("#pdf_pmid").val(pmid);
       }, 50);

   }


   function upload_pdf(){
	   var pmid = $("#v_pmid span a").html();
       if(document.getElementById("pdf").files[0].size < 30000000) {
           if(document.getElementById("pdf").files[0].name.split(".").pop().toLowerCase() == "pdf") {
               var http = new XMLHttpRequest();
               var formData = new FormData($("#pdf_form")[0]);

               $.ajax({
                   type: "POST",
                   url: "/PathOS/pubmed/upload_pdf",
                   data: formData,
                   success: function (d) {
                       console.log("File upload response: "+d);
                       if (d == "success") {
                           $.jGrowl("File successfully uploaded.");
                           d3.selectAll("#trv_pdf #v_pdf span, tr.ui-state-highlight td[aria-describedby='pubmed_table_pdf']")
								.append('a')
								.attr("href", "http://bioinf-ensembl.petermac.org.au/Pubmed/"+pmid+".pdf")
								.attr("target", "_blank")
								.text("[PDF]")

                            PathOS.tags.addTag(d3.select(".tags_field"), "pdf")

                       } else if (d == "no transfer") {
                           $.jGrowl("File could not be uploaded.");
                       } else {
                           $.jGrowl("File was not uploaded.");
                       }
                   },
                   cache: false,
                   contentType: false,
                   processData: false
               });
           } else {
               alert("Please upload a .pdf");
           }
       } else {
           alert("Please upload a file smaller than 30mb");
       }
   }

   function addPMID(pmid){
       console.log("Adding PMID: "+pmid);

        var url = "/PathOS/Pubmed/add_pmid?pmid="+pmid;
        $.ajax({
            url: url,
            method: "POST",
            success: function(d){
					console.log(d);
					if (d == "saved") {
						$.jGrowl("Article saved to database.");
						$('#'+pmid+' .ui-state-lowlight')
							.html($('#'+pmid+' .ui-state-lowlight').html().split('<br>')[0])
							.removeClass('ui-state-lowlight');
						if($("#v_pmid .ui-state-lowlight").html()) {
							$("#v_pmid .ui-state-lowlight")
								.html($("#v_pmid .ui-state-lowlight").html().split('<br>')[0])
								.removeClass('ui-state-lowlight');
						}
					} else {
						$.jGrowl("Article was not saved to database. Please refresh page and try again.");
					}
				}
            });
   }

   function showRow(id) {
       var d = datastore[id];

       // If this entry hasn't been shown on our grid yet, it won't be in datastore. We need to pull it from the database.
       if (d) {
           $("#" + id).remove();
           $("#" + d.id).remove();

           $("#pubmed_table").jqGrid('addRowData', d.pmid, {
               pmid: d.pmid,
               doi: d.doi,
               date: d.date,
               journal: d.journal,
               volume: d.volume,
               issue: d.issue,
               pages: d.pages,
               title: d.title,
               authors: d.authors,
               affiliations: d.affiliations,
               id: d.pmid,
               abstrct: d.abstract,
               pdf: d.pdf

           }, 'first');

           $("#" + d.pmid).click();
		   $('#view_pubmed_table').click();
       } else {
           var filters = encodeURIComponent('{"groupOp":"AND","rules":[{"field":"id","op":"eq","data":"'+id+'"}]}');
           var url = '/PathOS/pubmed/pubmedRows?_search=true&filters='+filters;


           $.ajax({
               type: "GET",
               url: url,
               success: function (d) {
                   console.log(d);
                   if(d && d.rows && d.rows[0] && d.rows[0].cell) {
                       datastore.add(d.rows[0].cell);
                       showRow(d.rows[0].cell[10]);
                   }
               },
               cache: false,
               contentType: false,
               processData: false
           });
       }
   }


function findPMID(pmid){

	if(parseInt(pmid) > 0) {
		$("#pmid_box").val(parseInt(pmid));
	} else {
		pmid = $("#pmid_box").val().trim();
	}

	if(pmid && pmid !== ''){

	   var regex = /^\d{0,20}$/;

       if (regex.exec(pmid) == "" || regex.exec(pmid) == null) {
           $.jGrowl("Please enter a valid PubMed ID 'PMID'");
       } else if (datastore[pmid]) {
           $.jGrowl("This PMID has already been looked up.");
           showRow(pmid);
       } else {

           $("html").addClass("cursorWait");
           var http = new XMLHttpRequest();
           http.open("POST", "/PathOS/Pubmed/fetch_pmid", true);
           http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
           var params = "pmid=" + pmid;
           http.send(params);
           http.onload = function (e) {
               $("html").removeClass("cursorWait");
               console.log(http.responseText);

               if (http.responseText.split("-")[0] == "exists") {
                   $.jGrowl("Entry already exists.");

                   var id = http.responseText.split("-")[1];
                   showRow(id);

               } else {
                   var d = JSON.parse(http.responseText);
                   if(!d.pmid) {
                       $.jGrowl("PMID not found.");
                   } else {


   console.log("This happens when... PMID is valid, pmid hasn't been shown on the table...")
   console.log("PMID is sent to server, and the server hasn't seen that record in the database.")

                       datastore.addJSON(d);
                       showRow(d.pmid);
                       $("#"+d.pmid+" td:nth-child(11)").addClass("ui-state-lowlight")

                       var html = $("#"+d.pmid+" td:nth-child(11)").html();
                       var save = '<br><a href="#" onclick="addPMID('+d.pmid+');return false">[SAVE ROW]</a>';

                       $("#"+d.pmid+" td:nth-child(11)").html(html+save)

                   }
               }
           }
       }
   }
}


   function parseAuthors(arr){
       var result = [];
       arr.forEach(function(d){
           result.push(d.name);
       });

       console.log('parsing authors... '+ result.join())
       return result.join();
   }
   function parseAffiliations(arr){
       var result = [];
       arr.forEach(function(d){
           result.push(d.affiliation);
       });

       console.log('parsing affiliations... '+ result.join())
       return result.join();
   }
   function firstAffiliation(arr){
       var result = ""
       if(arr && arr[0]) {
           result = arr[0].affiliation;
       }
       return result;
   }







   PathOS.hotkeys.add(74, down);
   PathOS.hotkeys.add(40, down);
   PathOS.hotkeys.add(75, up);
   PathOS.hotkeys.add(38, up);
   PathOS.hotkeys.add(13, function (){
       if(!navOpen() && !$(document.activeElement).is("#tag_text_area")) {
           $("#view_pubmed_table").click();
       }
   });
   PathOS.hotkeys.add(27, function (){
	   $("#close_tags_module").click();
	   $("#cData").click();
   });

   function down(){
       if(navOpen()) {
           $("#nData").click()
       } else {
           var pos = getCurrentSelection() + 1;
           if (pos < $(".jqgrow").length) {
               $(".jqgrow")[pos].click();
           }
       }
   }
   function up(){
       if(navOpen()) {
           $("#pData").click()
       } else {
           var pos = getCurrentSelection() - 1;
           if(pos >= 0) {
               $(".jqgrow")[pos].click();
           }
       }
   }

   // Keyboard Shortcuts.
   $('body').on("keydown", function(e){
       if (e && e.keyCode && e.keyCode == 13 && $(document.activeElement).is("#pmid_box")){
           findPMID();
       }
   })


   // Double Click to open a new row.
   $("body").dblclick(function(d){
       var id = d.originalEvent.path[1].id;
       if ($(d.originalEvent.path[0]).attr("role") == "gridcell") {
           $("#view_pubmed_table").click();
       }
   })







	function unselect_article() {
		$("#cData").click()
		$(".ui-state-highlight").toggleClass('ui-state-highlight');
		window.history.replaceState({}, "Pubmed", "/PathOS/Pubmed");
	}

   function getCurrentSelection(){
       var pos = -1;
       $(".jqgrow").each( function(d){
           if($($(".jqgrow")[d]).attr("tabindex") == 0) {
               pos = d;
           }
       })
       return pos;
   }

   function getCurrentpmid(){
	   PathOS.tags.current_object = $('.ui-state-highlight td[aria-describedby="pubmed_table_pmid"]').attr('title');
       return PathOS.tags.current_object;
   }

   function navOpen(){
       var result = true;
       if($("#viewmodpubmed_table").length == 0) {
           result = false;
       }
       if($("#viewmodpubmed_table").attr("aria-hidden") != "false") {
           result = false;
       }
       return result;
   }

$(document).ready( function(){

	if(!PathOS.params().pmid && !PathOS.params().id) {
		window.history.replaceState({}, "Pubmed", "/PathOS/Pubmed");
	}

	if (PathOS.params().pmid) {
		setTimeout(function(){
			findPMID(PathOS.params().pmid);
		}, 500);
	} else if (PathOS.params().id) {
	   $.ajax({
		   type: "GET",
		   url: '/PathOS/Pubmed/check_id/'+PathOS.params().id,
		   success: function (pmid) {
		   		if(pmid != "Fail") {
					findPMID(pmid);
				}
		   },
		   cache: false,
		   contentType: false,
		   processData: false
	   });
	}


	var availableTags = <g:allTags/>;
	var tagModule = PathOS.tags.buildModule({
		object: 'Pubmed',
		tags: [],
		availableTags: availableTags,
		closeModule: unselect_article
	});
})

	$(document).ready(function(){
	var width = $(window).width() - 430;

	d3.select("#pubmedPager")
		.style('padding', 1)
		.style('width', width+'px')
		.style('position', 'fixed')
		.style('bottom', 0);
	})

	d3.select("#pubmed")
		.style("margin-bottom", '20px')
</r:script>


</body>

</html>

















