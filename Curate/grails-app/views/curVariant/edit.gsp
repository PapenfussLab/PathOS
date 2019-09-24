<%@ page import="org.petermac.pathos.curate.CurVariant" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'variant.label', default: 'CurVariant')}"/>
    <title>${variantInstance} - <g:message code="default.edit.label" args="[entityName]"/></title>
    <tooltip:resources/>

    <parameter name="hotfix" value="off" />


<r:style>
.container-page {
    background: white;
    padding-bottom: 50px;
}

#overlay.hidden {
    display: none;
}
#overlay {
    position: fixed;
    width: 100%
    height: 100%;
    background: rgba(0,0,0,0.5);
}
#overlay img {
    position: fixed;
    width: 50%;
    margin: 10% 25%;
}

#curatedClassification {
    padding: 0 0 0 13px;
}
#toggleEditPmClass {
    padding: 0 5px;
    cursor: pointer;
}

#customPmClass.hidden #editSigClass,
#customPmClass.hidden #editAmpClass,
#customPmClass.hidden #editPmClass {
    display: none;
}

#customPmClass.hidden #displaySigClass,
#customPmClass.hidden #displayAmpPmClass,
#customPmClass.hidden #displayPmClass {
    display: block;
}

#customPmClass #editPmClass {
    display: block;
}

#customPmClass #displayPmClass {
    display: none;
}

#classOverrideReason {
    border: solid 1px lightgrey;
    margin-top: 0;
    padding: 2px;
}

.row {
    margin: 0;
}

input#ampSync, input#acmgSync, input#overallSync {
    display: none;
}
div.override-read pre {
    text-align: left;
    font-family: "HelveticaNeue-Light","Helvetica Neue Light","Open Sans","Helvetica Neue",Helvetica,"Lucida Grande",Arial,sans-serif;
    overflow-x: auto;
    white-space: pre-wrap;
    word-wrap: break-word;
}

</r:style>

</head>

<body>

<nav class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="list" action="show" id="${variantInstance.id}">Show Variant</g:link></li>
        <li><a class="save" href="#saved" onclick="update()">Save Variant</a></li>
        <g:render template="deleteButton" model="[id: variantInstance.id]"/>
    </ul>
</nav>

<div id="overlay" class="hidden">
    <img src="<g:context/>/dist/images/pathos_logo_animated.svg">
</div>

<div id="curVariant-edit" class="section container-page">
    <div class="row">
        <h1 style="text-align: center">${variantInstance}</h1>

        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>
        <g:hasErrors bean="${variantInstance}">
            <ul class="errors" role="alert">
                <g:eachError bean="${variantInstance}" var="error">
                    <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                            error="${error}"/></li>
                </g:eachError>
            </ul>
        </g:hasErrors>

        <div class="content col-md-6" style="z-index: 1;" role="main">

            <g:form method="post" name="curVariantForm">
                <fieldset class="form">
                <g:hiddenField name="id" value="${variantInstance?.id}"/>
                <g:hiddenField name="version" value="${variantInstance?.version}"/>

                <h2 style="text-align: center">Edit Curated Variant</h2>

                <div id="curatedClassification">
                    <div id="cvOverrides">
                        <h2 style="text-align: center">Curated Classification <i id="toggleEditPmClass" onclick="toggleOverride()" class="fa fa-edit" aria-hidden="true"></i></h2>


                        <table>
                            <tr>
                                <td>Classification</td>
                                <td>Ranking</td>
                                <td>Reason</td>
                            </tr>
                            <tr>
                                <td><p>Clinical Significance</p>
                                    <input name="overallSync" id="overallSync" value="true">
                                    <g:if test="${variantInstance?.overallReason}">
                                        <a id="overallClear" class="clearOverride" title="Clear Override" href="#clearOverride" onclick="clearOverallOverride()"><i class="fa fa-undo"></i></a>
                                        <r:script>$("#overallSync").val("false");</r:script>
                                    </g:if>
                                </td>
                                <td>
                                    <div id="overallClassRanking"></div>
<r:script>
    PathOS.criteria.drawOverall("overallClassRanking", {text: "${variantInstance?.overallClass ?: "Unclassified"}"});
</r:script>

                                </td>
                                <td>

                                    <div id="overallReasonRead" class="override-read">
                                        <pre>${variantInstance?.overallReason ?: "Unclassified"}</pre>
                                    </div>

                                    <div id="overallOverride" class="override-edit">

                                        %{--This next bit has an id & name so submitting the form works.--}%
                                        <select id="overallClass" name="overallClass" onchange='$("#overallSync").val("false")'>
                                            <option selected disabled hidden>Choose Classification</option>
                                            <option value="Unclassified">Unclassified</option>
                                            <option value="CS: Clinically Significant">CS: Clinically Significant</option>
                                            <option value="UCS: Unclear Clinical Significance">UCS: Unclear Clinical Significance</option>
                                            <option value="NCS: Not Clinically Significant">NCS: Not Clinically Significant</option>
                                        </select>
                                        <textarea onchange='$("#overallSync").val("false")' id="overallReason" name="overallReason" placeholder="Justification for overriding the Clinical Significance Rating." style="height: 50px; margin: 5px 0;">${variantInstance.overallReason}</textarea>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <p>ACMG</p>
                                    <input name="acmgSync" id="acmgSync" value="true">
                                    <g:if test="${variantInstance?.classOverrideReason}">
                                        <a id="ACMGClear" class="clearOverride" title="Clear Override" href="#clearOverride" onclick="clearACMGOverride()"><i class="fa fa-undo"></i></a>
                                        <r:script>$("#acmgSync").val("false");</r:script>
                                    </g:if>
                                </td>
                                <td>
                                    <div id="curatedACMG"></div>
<r:script>
    PathOS.criteria.drawAcmg("curatedACMG", {text: "${variantInstance.pmClass}"});
</r:script>
                                </td>
                                <td>
                                    <div id="ACMGReasonRead" class="override-read">
                                        <pre>${variantInstance?.classOverrideReason ?: "Calculated from Evidence"}</pre>
                                    </div>

                                    <div id="ACMGOverride" class="override-edit">

%{--This next bit has an id & name so submitting the form works.--}%
                                        <select id="pmClass" name="pmClass" onchange='$("#acmgSync").val("false")'>
                                            <option selected disabled hidden>Choose Classification</option>
                                            <option value="Unclassified">Unclassified</option>
                                            <option value="C1: Not pathogenic">C1: Not pathogenic</option>
                                            <option value="C2: Unlikely pathogenic">C2: Unlikely pathogenic</option>
                                            <option value="C3: Unknown pathogenicity (Level C)">C3: Unknown pathogenicity (Level C)</option>
                                            <option hidden value="C3: Unknown pathogenicity">C3: Unknown pathogenicity</option>
                                            <option value="C3: Unknown pathogenicity (Level B)">C3: Unknown pathogenicity (Level B)</option>
                                            <option value="C3: Unknown pathogenicity (Level A)">C3: Unknown pathogenicity (Level A)</option>
                                            <option value="C4: Likely pathogenic">C4: Likely pathogenic</option>
                                            <option value="C5: Pathogenic">C5: Pathogenic</option>
                                        </select>
                                        <textarea id="classOverrideReason" name="classOverrideReason" placeholder="Justification for overriding the calculated ACMG classification." style="height: 50px; margin: 5px 0;" onchange='$("#acmgSync").val("false")'>${variantInstance.classOverrideReason}</textarea>
                                    </div>

                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <p>AMP</p>
                                    <input name="ampSync" id="ampSync" value="true">
                                    <g:if test="${variantInstance?.ampReason}">
                                        <a id="AMPClear" class="clearOverride" title="Clear Override" href="#clearOverride" onclick="clearAMPOverride()"><i class="fa fa-undo"></i></a>
                                        <r:script>$("#ampSync").val("false");</r:script>
                                    </g:if>
                                </td>
                                <td>
                                    <div id="curatedAMP"></div>
<r:script>
    PathOS.criteria.drawAmp("curatedAMP", {text: "${variantInstance.ampClass}"});
</r:script>
                                </td>
                                <td>

                                    <div id="AMPReasonRead" class="override-read">
                                        <pre>${variantInstance?.ampReason ?: "Calculated from Evidence"}</pre>
                                    </div>

                                    <div id="AMPOverride" class="override-edit" >
                                        <select id="ampClass" name="ampClass" onchange='$("#ampSync").val("false")'>
                                            <option selected disabled hidden>Choose Classification</option>
                                            <option value="Unclassified">Unclassified</option>
                                            <option value="Tier I">Tier I</option>
                                            <option value="Tier II">Tier II</option>
                                            <option value="Tier III">Tier III</option>
                                            <option value="Tier IV">Tier IV</option>
                                        </select>
                                        <textarea onchange='$("#ampSync").val("false")' id="ampReason" name="ampReason" placeholder="Justification for overriding the calculated AMP classification." style="height: 50px; margin: 5px 0;">${variantInstance.ampReason}</textarea>
                                    </div>

                                </td>
                            </tr>
                        </table>



                    </div>


                </div>

                    <g:render template="curVariantForm"/>
                </fieldset>
            </g:form>
        </div>

        <div id="evidence-edit" class="content col-md-6" style="border-left: 1px solid lightgrey; z-index: 0;">

            <fieldset id="evidenceRadio">
                <input type="radio" id="acmgRadio" name="evidenceRadio" checked />
                <input type="radio" id="ampRadio" name="evidenceRadio"  />
                <input type="radio" id="pmacRadio" name="evidenceRadio"  />

                <label for="acmgRadio" class="evidenceLabel" id="acmgLabel"><span>ACMG - <span id='acmgFlag' class="cv-${acmgEvidence.classification?.split(':')[0]}">${acmgEvidence.classification?.split(':')[0]}</span></span></label>

                <label for="ampRadio" class="evidenceLabel" id="ampLabel"><span>AMP - <span class="amp-${ampEvidence.classification?.replace(" ","-") ?: "Tier-III"}" style="padding:2px;" id="ampFlag">${ampEvidence.classification ?: "Tier III"}</span></span></label>

                <g:if test="${variantInstance?.evidence}">
                <label for="pmacRadio" class="evidenceLabel" id="pmacLabel"><span>Archive</span></label>
                </g:if>


                <div id="acmgBody" class="evidenceBody">
                <g:render template="acmgForm"/>
                </div>

                <div id="ampBody" class="evidenceBody">
                <g:render template="ampForm"/>
                </div>

                <g:if test="${variantInstance?.evidence}">
                <div id="pmacBody" class="evidenceBody">
                <g:render template="pmacForm" model="[evidenceInstance: variantInstance.evidence]"/>
                </div>
                </g:if>

            </fieldset>

        </div>
    </div>
</div>
<r:script>
// d3.selectAll(".evidenceTab").on("click", function(d){
//     // $("#someCheckbox").toggle
//     $('input[type=checkbox]#someCheckbox').trigger('click');
// });
</r:script>

%{--
DKGM 29-November-2016

Since this page is built using the grails 2.3.7 scaffold and built-in taglibs,
it is a bit difficult to mess with the code above.

The best way to manipulate this document is with some javascript, as follows.

The purpose of this javascript is to add some user-friendly instructions for PMID formatting

PMIDs should be included in the Report Description as such:
[PMID: 1212332]
or
[PMID: 121512, 3211233]
for multiple PMIDs.

To facilitate this, we're going to highlight correctly written PMIDs.

--}%
<r:script>

    PathOS.pubmed.applyHighlight('highlightClass');

    d3.select("label[for='reportDesc']")
        .style("vertical-align", "top")
        .html("Report Description<br><br>Pubmed IDs can be marked in this format:<br><mark>[PMID:&nbsp;12345]</mark>");

</r:script>





<r:script>



function clearOverallOverride() {
    clearOverride("overall", function(callback){
        $("#overallClassRanking div")
            .text("Unclassified")
            .removeClass()
            .addClass("overall-Unclassified bordered-classification");
        $("#overallClass").val("Unclassified");
        $("#overallReason").val("");
        $("#overallReasonRead pre").text("Unclassified");
    }, "overallClass");
    $("#overallSync").val("true");
}

function clearACMGOverride() {
    clearOverride("ACMG", calculateACMG, "pmClass");
    $("#acmgSync").val("true");
}

function clearAMPOverride() {
    clearOverride("AMP", calculateAMP, "ampClass");
    $("#ampSync").val("true");
}

function clearOverride(type, calculate, dropdown) {
    if(confirm("Would you like to remove the override for "+type+"?")) {
        d3.select("#"+type+"Clear").remove();
        $("#"+type+"ReasonRead p").text("Calculated from Evidence");

        if(calculate) {
            calculate(function(value){
                // console.log("Setting value", value);
                $("#"+dropdown+">option").attr("selected", false);
                $("#"+dropdown+">option[value='"+value+"']").attr("selected", true);

                $("#curated"+type).html("");
                $("#calculated"+type+" div").clone().appendTo("#curated"+type);
                $("#"+type+"Override textarea").val("");
            });
        }
    }
}

function toggleOverride() {
    // alert("lol");
    // d3.select("#editPmClass").style("display", "block");
    // $("#customPmClass").toggleClass("hidden");

    $("#curatedClassification table").toggleClass("edit-mode");
    $("#toggleEditPmClass").remove();


    var overallOriginal = `${variantInstance?.overallReason ?: ""}`;
    var overall = $("#overallReason").val();
    if (overall && overall != overallOriginal) {
        overall = overall + " - By " + PathOS.username + " at " + Date();
    }
    $("#overallReasonRead pre").text(overall);


    var acmgOriginal = `${variantInstance?.classOverrideReason.toString() ?: ""}`;
    var acmg = $("#classOverrideReason").val();
    if (acmg && acmg != acmgOriginal) {
        acmg = acmg + " - By " + PathOS.username + " at " + Date();
    }
    $("#ACMGReasonRead pre").text(acmg);


    var ampOriginal = `${variantInstance?.ampReason ?: ""}`;
    var amp = $("#ampReason").val();
    if (amp && amp != ampOriginal) {
        amp = amp + " - By " + PathOS.username + " at " + Date();
    }
    $("#AMPReasonRead pre").text(amp);

}








function update(){
    $("#overlay").toggleClass("hidden");

    var package = {
        curVariant: $("#curVariantForm").serializeArray(),
        acmgEvidence: $("#acmgEvidenceForm").serializeArray(),
        // pmacEvidence: $("#pmacEvidenceForm").serializeArray(), // LEGACY. THIS SHOULD BE READ ONLY
        ampEvidence: $("#ampEvidenceForm").serializeArray()
    };

    $.ajax({
        url: "${createLink(action:'jsonUpdate', controller:'curVariant')}",
        type: "POST",
        complete: function( d ) {
            $("#overlay").toggleClass("hidden");
            if( d.status == 200 ) {
                PathOS.notes.add("Curated Variant information saved");
                console.log(d.responseJSON)
                location.reload();
            } else {
                console.error(d.responseJSON);
                PathOS.notes.addError("Error: Invalid Character");
            }
        },
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: JSON.stringify(package)
    });
}





</r:script>



</body>
</html>





























