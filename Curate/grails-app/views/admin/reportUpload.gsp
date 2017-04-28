<%--
  Created by IntelliJ IDEA.
  User: madavid
  Date: 31/01/2017
  Time: 1:49 PM
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Report Template Upload</title>
    <parameter name="hotfix" value="off" />

    <g:javascript src='jquery/jquery.jgrowl.js' plugin='spring-security-ui'/>
    <link href="<g:resource dir='css/jquery-ui-1.11.0.custom' file='jquery-ui.theme.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />


<style>
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

input {
    width: 100%;
    margin-bottom: 5px;
}

input.disabled, .disabled, input[disabled].disabled {
    cursor: not-allowed;
}
h3 {
    text-align: center;
}
</style>
</head>
<body>

<div class="container outlined-box">
    <div class="row">
        <div class="col-xs-12">
            <h1>Report Template Upload</h1>
            <p>This page is for admins to upload report templates. There is a Peter Mac confluence page with further information about valid Mail Merge fields <a href="https://vm-115-146-91-157.melbourne.rc.nectar.org.au/confluence/display/PVS/Reporting+Mail+Merge+Fields">here</a>.</p>
        </div>
    </div>
    <div class="row">
        <form id="report_form" action="upload_report" style="margin: 50px;" method="POST">



            <div class="row outlined-box">
                <div class="col-xs-6">
                    <h3 class="text-center">Pick a .docx file to upload</h3>
                    <input class="text-center" type="file" accept=".docx" id="docx" name="docx"/>
                </div>
                <div class="col-xs-6">
                    <h3>Test your template on a Seqrun?</h3>
                    <div style="margin:5px;">
                        <span>Seqrun</span>
                        <select name="seqruns" id="seqruns">
                            <option value="none">Pick one</option>
                        </select>
                    </div>
                    <div style="margin:5px;">
                        <span>Sample</span>
                        <select name="seqsample" id="samples" class="disabled" disabled>
                            <option value="none">Pick one</option>
                        </select>
                    </div>

                    <div style="margin: auto;">
                        <input type="button" value="Reset Test Template" onclick="reset_template()" style="width: 45%; display: none;">
                        <input target="_blank" type="button" value="Test PDF" onclick="test_template('reportPdf')" style="width: 25%;" class="disabled" disabled>
                        <input type="button" value="Test Word" onclick="test_template('reportWord')" style="width: 25%;" class="disabled" disabled>
                    </div>
                </div>
            </div>


            <div class="row">

                <div class="col-xs-4 outlined-box">
                    <h3>Panel Group</h3>
                    <input list="panels" name="panel" id="input-panel"/>
                    <datalist id="panels"></datalist>
                </div>

                <div class="col-xs-4 outlined-box">
                    <h3>Test Name (optional)</h3>
                    <input list="tests" name="test" id="input-test"/>
                    <datalist id="tests"></datalist>
                </div>

                <div class="col-xs-4 outlined-box">
                    <h3>Var/Neg/Fail</h3>
                    <input list="outcomes" name="reported_outcome" id="input-outcome"/>
                    <datalist id="outcomes"></datalist>
                </div>

            </div>

            <div class="row outlined-box">
                <div class="col-xs-6">
                    <h3>Your file will be uploaded and saved as:</h3>
                    <input id="filename" name="filename"/>
                </div>
                <div class="col-xs-6">
                    <h3>Upload!</h3>
                    <p>This will upload your template, overwriting any current template with the same name.<br>Please test your template before overwiting.</p>
                    <input type="button" value="Upload to Server" onclick="upload_report()">
                </div>
            </div>



        </form>
    </div>
</div>


<script>

d3.select("#panels")
    .selectAll("option")
    .data(${panels as grails.converters.JSON})
    .enter()
    .append("option")
    .attr("value", function(d){ return d; });

d3.select("#tests")
    .selectAll("option")
    .data(${tests as grails.converters.JSON})
    .enter()
    .append("option")
    .attr("value", function(d){ return d; });

d3.select("#outcomes")
        .selectAll("option")
        .data(${outcomes as grails.converters.JSON})
        .enter()
        .append("option")
        .attr("value", function(d){ return d; });

d3.selectAll("#input-panel, #input-test, #input-outcome")
    .on("input", function(){
        var parts = [];

        if($("#input-panel").val()) parts.push($("#input-panel").val().trim());
        if($("#input-test").val()) parts.push($("#input-test").val().trim());
        if($("#input-outcome").val()) parts.push($("#input-outcome").val().trim());
        parts.push("Template.docx");

        $("#filename").val(parts.join(" "));
    });

d3.select("#seqruns")
    .on("input", function(){
        var val = $("#seqruns").val();

        if (val && val != "none") {
            $("#samples").removeAttr("disabled").removeClass("disabled");

            $.ajax("/PathOS/Seqrun/fetchSamples?seqrun="+val, {
                success: function(data){
                    d3.select("#samples")
                        .on("input", function(){
                            $(".disabled").removeAttr("disabled").removeClass("disabled");
                        })
                        .selectAll("option")
                        .data(data.samples.sort(function(a,b){ return a[0] < b[0] ? -1 : 1; }))
                        .enter()
                        .append("option")
                        .attr("value", function(d){ return d[1]; })
                        .text(function(d){ return d[0]; });
                }
            });
        }
    })
    .selectAll("option")
    .data(${seqruns as grails.converters.JSON})
    .enter()
    .append("option")
    .attr("value", function(d){ return d; })
    .text(function(d){ return d; });

function test_template(action){
    var seqsample = $("#samples").val();

    if(seqsample != 'none') {
        if(document.getElementById("docx").files[0]) {
            $("#filename").val("Test Template.docx");
            upload_report(function(){
                window.open("/PathOS/SeqVariant/"+action+"?test=true&id="+seqsample, "_blank");
            });
        } else {
            window.open("/PathOS/SeqVariant/"+action+"?test=true&id="+seqsample, "_blank");
        }
    }
}

function reset_template(){
    $.ajax({
        type: "GET",
        url: "/PathOS/admin/reset_template",
        success: function(d){
            if(d) {
                $.jGrowl(d);
            }
        }
    })
}

function upload_report(callback){
    if(document.getElementById("docx").files[0] && document.getElementById("docx").files[0].size < 30000000) {
        if(document.getElementById("docx").files[0].name.split(".").pop().toLowerCase() == "docx") {
            var formData = new FormData($("#report_form")[0]);

            $.ajax({
                type: "POST",
                url: "/PathOS/admin/upload_report",
                data: formData,
                success: function (d) {
                    console.log("File upload response: "+d);
                    if (d == "success") {
                        $.jGrowl("File successfully uploaded.");
                        if(callback){
                            callback();
                        }
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
            alert("Please upload a .docx");
        }
    } else {
        alert("Please upload a file smaller than 30mb");
    }
}
</script>
</body>
</html>