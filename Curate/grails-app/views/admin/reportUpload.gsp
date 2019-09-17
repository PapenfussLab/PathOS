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

<style>

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
            <p>Mail Merge field <a href="https://atlassian.petermac.org.au/confluence/display/PVS/Reporting+Mail+Merge+Fields">info</a>.</p>
        </div>
    </div>
    <div class="row">
        <form id="report_form" action="upload_report" style="margin: 50px;" method="POST">

            <div class="row outlined-box">
                <div class="col-xs-4">
                    <h3 class="text-center">Pick a .docx file to upload</h3>
                    <input class="text-center" type="file" accept=".docx" id="docx" name="docx"/>
                </div>

                <div class="col-xs-4 outlined-box">
                    <h3>Test Name</h3>
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
                    <input style="background: rgba(0,0,0,0.1); cursor: not-allowed;" readonly="1" id="filename" name="filename"/>
                </div>
                <div class="col-xs-6">
                    <h3>Upload</h3>
                    <input type="button" value="Upload to Server" onclick="upload_report()">
                </div>
            </div>
        </form>
    </div>
</div>


<script>

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
    .on("input", refresh_filename);

function refresh_filename() {
    var parts = ["Template"];

    if($("#input-test").val()) parts.push($("#input-test").val().trim());
    if($("#input-outcome").val()) parts.push($("#input-outcome").val().trim());

    $("#filename").val(parts.join("_")+".docx");
}

function upload_report(callback){
    if(document.getElementById("docx").files[0] && document.getElementById("docx").files[0].size < 30000000) {
        if(document.getElementById("docx").files[0].name.split(".").pop().toLowerCase() == "docx") {
            var formData = new FormData($("#report_form")[0]);

            $.ajax({
                type: "POST",
                url: "<g:context/>/admin/upload_report",
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

if (PathOS.params().code) {
    console.log("We have a code!")
    $("#input-test").val(PathOS.params().code);
    var outcome = "var";
    if (PathOS.params().template.indexOf("_fail.docx") > 0) outcome = "fail";
    if (PathOS.params().template.indexOf("_neg.docx") > 0) outcome = "neg";
    $("#input-outcome").val(outcome);
    refresh_filename();
}

</script>
</body>
</html>

















