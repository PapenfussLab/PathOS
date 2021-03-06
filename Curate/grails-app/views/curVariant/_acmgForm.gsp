<%@ page import="org.petermac.pathos.curate.Evidence" %>

<script src="<g:context/>/js/jquery.floatThead.min.js?v=<g:render template='/gitHash'/>"></script>

<h2>Calculated ACMG Classification</h2>

<div id="calculatedACMG"></div>

<button style="margin-top: 5px;" onclick="calculateACMG()">Calculate ACMG</button>

<i id="ACMG-rules-help" style="cursor:pointer;" onclick="window.open('http://www.pathos.co/acmg/rules.png','ACMG Rules','height=800,width=600');" class="criteria-info-icon fa fa-info-circle"></i>
<a href="https://www.nature.com/articles/gim201530" target="_blank">Richards et al. (2015) DOI: 10.1038/gim.2015.30</a>

<h2>Collected ACMG Evidence</h2>

<form id="acmgEvidenceForm">
    <fieldset class="form">

    <div>
        <g:textArea class="unprocessed-blob" placeholder="Evidence to support the ACMG classification." name="acmgJustification" maxlength="8000" cols="10" rows="6" value="${acmgEvidence.acmgJustification}"/>

        <g:textArea name="blob" value="${acmgEvidence.acmgJustification}"></g:textArea>
        <input name="currentUser" id="currentUser" value="">

    </div>

        <button type="button" class="toggle-button" onclick="clickDescriptionToggle()">Toggle Descriptions</button>
        <button type="button" class="toggle-button" onclick="clickToggleEvidence()">Toggle Evidence</button>


    <div id="acmgEvidence" class="row">

        <g:each in="${['pathogenic','benign']}" var="type">
            <div class="col-sm-6">
                <table id="table-${type}" class="floatingHead">
                    <thead>
                    <tr>
                        <th>N/A</th>
                        <th style="border-right: none;"></th>
                        <th style="border-left: none;">${type.capitalize()}</th>
                        <td>Met</td>
                        <td>Not met</td>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${acmg.count}" var="i">
                        <g:if test="${acmg[type][i]}">
                            <g:set var="box" value="${acmg[type][i]}"/>
                            <g:set var="boxClass" value="${box.toString().split(/\d$/)[0]}"/>
                            <g:set var="boxVal" value="${acmgEvidence[box]}"/>

                            <tr id="${box}-row" class="${boxVal == 'na' || boxVal == 'unset' && defaultCriteria[type][box]?.na ? 'disabled' : boxVal == 'yes' ? 'green' : ''}">
                                <td rowspan="2" class="na-checkbox">
                                    <g:if test="${true || acmg.na.contains(box)}">
                                    <input boxtype="${box}" class="na-label na-input" type="radio" name="${box}" id="${box}-na" value="na" ${boxVal == 'na' || boxVal == 'unset' && defaultCriteria[type][box]?.na ? 'checked' : ''}>
                                    </g:if>
                                </td>
                                <td class="acmg-marker">
                                    <span class="${type.capitalize()} ${boxClass}"></span>
                                    <input class="unset" type="radio" name="${box}" id="${box}-unset" value="unset" ${boxVal == 'unset' && !defaultCriteria[type][box]?.na ? 'checked' : ''}>
                                </td>
                                <td class="acmg-description" title="<g:message code="acmgEvidence.${box}.description"/>">
                                    <label class="acmg-label" for="${box}-yes">
                                        <h4 class="${boxClass}">${box} - <g:message code="acmgEvidence.${box}.label"/></h4>
                                        <p><g:message code="acmgEvidence.${box}.description"/></p>
                                    </label>

                                </td>
                                <td rowspan="2" class="acmg-yes">
                                    <input boxtype="${box}" type="radio" name="${box}" id="${box}-yes" value="yes" ${boxVal == 'yes' ? 'checked' : ''}>
                                </td>
                                <td rowspan="2" class="acmg-no">
                                    <input type="radio" name="${box}" id="${box}-no" value="no" ${boxVal == 'no' ? 'checked' : ''}>
                                </td>
                            </tr>
                            <tr id="${box}-meta-row" class="${boxVal == 'na' || boxVal == 'unset' && defaultCriteria[type][box]?.na ? 'disabled' : boxVal == 'yes' ? 'green' : ''}">
                                <td colspan="2" class="criteria-text">
                                    <i id="${box}-toggle-text" onclick="$(this).toggleClass('show-text').addClass('has-text');" class="toggle-text fa fa-pencil-square-o"></i>

                                    <i id="${box}-toggle-history" onclick="$(this).toggleClass('show-history');" class="toggle-history fa fa-history"></i>

                                    <g:if test="${acmg.info.contains(box)}">
                                    <i id="${box}-toggle-help" onclick="window.open('http://www.pathos.co/acmg/${box}.png','${box} info','height=600,width=800');" class="criteria-info-icon fa fa-info-circle"></i>
                                    </g:if>

                                    <select form="acmgEvidenceForm" id="${box}-dropdown" name="${box}-dropdown" class="criteria-strength">
                                        <g:each in="${defaultCriteria[type][box].options}" var="option">
                                            <option value="${option}" <g:if test="${option == defaultCriteria[type][box].default}">default selected</g:if>>${option}</option>
                                        </g:each>
                                    </select>
                                    <textarea class="criteria-metadata" id="${box}-text" name="${box}-text" form="acmgEvidenceForm" placeholder="Evidence to support ${box} criteria"></textarea>

                                    <div id="${box}-history" class="acmg-history">
                                        <p>Edit history for ${box}:</p>
                                        <ul></ul>
                                    </div>
                                </td>
                            </tr>
                            </g:if>
                    </g:each>
                    </tbody>

                </table>
            </div>
        </g:each>

    </div>

    </fieldset>
</form>


<r:script>
    $("#currentUser").val(PathOS.username);
    PathOS.criteria.drawAcmg("calculatedACMG", {text:"${acmgEvidence.classification}"});

    const defaultCriteria = ${defaultCriteria as grails.converters.JSON};

    try {
        var json = JSON.parse($("#acmgJustification").val());

        Object.keys(json.criteria).forEach(function(criterion){
            $("#"+criterion+"-text").val(json.criteria[criterion]);
            if(json.criteria[criterion] && json.criteria[criterion] != "") {
                $("#"+criterion+"-toggle-text").addClass("has-text");
            }
        });


        Object.keys(json.dropdowns).forEach(function(dropdown){
            $("#"+dropdown+"-dropdown").val(json.dropdowns[dropdown]);
            var type = dropdown.slice(0,1) == "P" ? "pathogenic" : "benign";

            if(json.dropdowns[dropdown] && json.dropdowns[dropdown] != defaultCriteria[type][dropdown].default ) {
                $("#"+dropdown+"-toggle-text").addClass("has-text");
            }
        });


        $("#acmgJustification").val(json.acmgJustification);


        Object.keys(json.history).forEach(function(criterion){
            json.history[criterion].forEach(function(history){
                var li = d3.select("#"+criterion+"-history ul").append("li");
                li.text(history.message +" - "+history.data+" by "+history.user+" on "+history.date);
            });
        });


    } catch (e) {
        console.log("Not Json", e);
    }
    $("#acmgJustification").removeClass("unprocessed-blob");

    var acmgToggles = PathOS.data.load("acmgToggles", {
        hideDescriptions: false,
        showEvidence: false
    });

    if(acmgToggles.hideDescriptions) {
        toggleCriteriaDescriptions();
    }
    if(acmgToggles.showEvidence) {
        toggleCriteriaEvidence();
    }

    function clickToggleEvidence() {
        acmgToggles.showEvidence = !acmgToggles.showEvidence;
        PathOS.data.save("acmgToggles", acmgToggles);
        toggleCriteriaEvidence();
    }

    function toggleCriteriaEvidence() {
        $(".has-text").toggleClass("show-text");
    }

    function clickDescriptionToggle() {
        acmgToggles.hideDescriptions = !acmgToggles.hideDescriptions;
        PathOS.data.save("acmgToggles", acmgToggles);
        toggleCriteriaDescriptions();
    }

    function toggleCriteriaDescriptions() {
        $("#acmgEvidence td p").toggleClass("hidden");
    }

    $("#acmgEvidence .na-label").on('click', function( event ){
        var boxtype = d3.select(this).attr("boxtype");
        d3.selectAll("#"+boxtype+"-row, #"+boxtype+"-meta-row").classed("disabled", true);
        d3.selectAll("#"+boxtype+"-row, #"+boxtype+"-meta-row").classed("green", false);
    });

    $("#acmgEvidence .acmg-yes input[type='radio']").on('click', function( event ){
        var boxtype = d3.select(this).attr("boxtype");
        console.log("lol, this clickkk", boxtype);
        d3.selectAll("#"+boxtype+"-row, #"+boxtype+"-meta-row").classed("disabled", false);
        d3.selectAll("#"+boxtype+"-row, #"+boxtype+"-meta-row").classed("green", true);
    });

    $("#acmgEvidence label.acmg-label").on('click', function( event ){
        event.preventDefault();
        var id = $(this).attr('for');
        var yes = $("#"+id);
        var no = $("#"+id.replace('yes', 'no'));
        var unset = $("#"+id.replace('yes', 'unset'));
        var boxtype = id.split("-")[0];

        if (yes.is(":checked")) {
            yes.prop("checked", false);
            unset.prop("checked", false);
            no.prop("checked", true);
            d3.selectAll("#"+boxtype+"-row, #"+boxtype+"-meta-row").classed("green", false);
        } else if (no.is(":checked")) {
            yes.prop("checked", false);
            no.prop("checked", false);
            unset.prop("checked", true);
            d3.selectAll("#"+boxtype+"-row, #"+boxtype+"-meta-row").classed("green", false);
        } else {
            unset.prop("checked", false);
            no.prop("checked", false);
            yes.prop("checked", true);
            d3.selectAll("#"+boxtype+"-row, #"+boxtype+"-meta-row").classed("green", true);
        }

        d3.selectAll("#"+boxtype+"-row, #"+boxtype+"-meta-row").classed("disabled", false);

    });

    $("#acmgEvidence input[type='radio']").on('mousedown', function(d){
        var that = $(this);
        var boxtype = $(this).attr("id").split('-')[0];
        d3.selectAll("#"+boxtype+"-row, #"+boxtype+"-meta-row").classed("disabled", false);
        d3.selectAll("#"+boxtype+"-row, #"+boxtype+"-meta-row").classed("green", false);
        var unset = $("#"+boxtype+"-unset");

        if (that.is(":checked")) {
            setTimeout(function(){
                that.prop("checked", false);
                unset.prop("checked", true);
                d3.selectAll("#"+boxtype+"-row, #"+boxtype+"-meta-row").classed("green", false);
                d3.selectAll("#"+boxtype+"-row, #"+boxtype+"-meta-row").classed("disabled", false);
            }, 300);
        }
    });

    function calculateACMG(callback) {

        var package = {
            acmgEvidence: $("#acmgEvidenceForm").serializeArray()
        };


        $.ajax({
            url: '/PathOS/curVariant/calculateAcmg',
            type: "POST",
            success: function(d){
                PathOS.criteria.drawAcmg("calculatedACMG",{text: d.classification});

                var flag = d.classification.split(":")[0] || "Unclassified";
                $("#acmgFlag").text(flag)
                    .removeClass()
                    .addClass("cv-"+flag, true);

                if(callback) {
                    callback(d.classification);
                }
            },
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(package)
        });

    }


    $('table.floatingHead').floatThead({
        top: 40,
        position: 'fixed'
    });


</r:script>

