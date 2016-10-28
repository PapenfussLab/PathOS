%{--
  - Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
  -
  - Organisation:   Peter MacCallum Cancer Centre
  - Author:         Ken Doig

  --}%

<%@ page import="org.petermac.pathos.pipeline.UrlLink; org.petermac.pathos.curate.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'curation.header.label')}"/>
<title>${seqSample?.seqrun} - ${seqSample?.sampleName} - <g:message code="default.list.label" args="[entityName]"/></title>

<r:require modules="export"/>

<parameter name="footer" value="on" />

%{--CSS Files--}%
<link href="<g:resource plugin='easygrid' dir='jquery-ui-1.11.0' file='jquery-ui.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
<link href="<g:resource dir='css/jquery-ui-1.11.0.custom' file='jquery-ui.theme.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
<link href="<g:resource plugin='easygrid' dir='jquery.jqGrid-4.6.0/css' file='ui.jqgrid.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
<link href="<g:resource plugin='easygrid' dir='jquery.jqGrid-4.6.0/plugins' file='ui.multiselect.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />

%{--Javascript Files--}%
<g:javascript src="quasipartikel/jquery.min.js" />
<g:javascript src="quasipartikel/jquery-ui.min.js" />
<g:javascript src="quasipartikel/ui.multiselect.js" />
<g:javascript src='jquery/jquery.jgrowl.js' plugin='spring-security-ui'/>

<script src="/PathOS/static/bundle-bundle_easygrid-jqgrid-dev_head.js" type="text/javascript" ></script>

<script>
    $(function() {
        $( "#tabs" ).tabs();
    });
</script>

<style type="text/css">
.ui-jqgrid .ui-jqgrid-htable th     { vertical-align: top; }
.ui-jqgrid .ui-jqgrid-htable th div { height: 30px; }
.ui-tabs { font-size: 12px; }
#filterNotification   {
    margin: 5px;
    font-size: 12px;

    width: 900px;
    /*color:#666666;*/
}
#filterShowMessage {
    text-decoration: underline;
    color: blue;
    cursor: pointer;

}

#cnvfilterNotification   {
    margin: 5px;
    font-size: 12px;

    width: 900px;
    /*color:#666666;*/
}
#cnvfilterShowMessage {
    text-decoration: underline;
    color: blue;
    cursor: pointer;

}
.reportsection {
    color: #666666;
    font-size: 0.8em;
    margin: 1em 2.1em;
    padding: 0 0.25em;
}

#firstReviewRevokeForm {
    overflow: auto;
}

#finalReviewRevokeForm {
    overflow: auto;
}


.ui-tabs {
    padding: 0em;
    position: relative;
}
.hideshowbutton {
    colour:blue;
    text-decoration:underline;
    cursor: pointer
}


#pat_sample_hollyinfo .fieldcontain .property-value {
    overflow: auto;
    word-wrap: break-word;
    white-space: normal;
}

td, th {
    line-height: 0.9em;
}


th[aria-selected="true"] {
    background: linear-gradient(#E4F2FB,#E4F2FB,#AECBE4,#AECBE4) !important;
}

.ui-dialog { z-index: 1003 !important ;}

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


#tag_text_area {
    width: 100%;
}
#tags .tagdiv span {
    font-size: 14px;
}

#ui-id-2 {
    z-index: 99999;
}

</style>

</head>

<body>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        %{--<g:if test="${seqSample?.patSample}" >--}%
            <li><g:link class="list"   controller="seqVariant" action="reportPdf"  params="${params}" target="_blank"><g:message code="curation.reportPdf.button.label" target="_blank"/></g:link></li>
            <li><g:link class="create" controller="seqVariant" action="reportWord" params="${params}"><g:message code="curation.reportWord.button.label"/></g:link></li>
        %{--</g:if>--}%
    </ul>
</div>

<section id="svListInfo">
    <div class="container">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message"id="flashMessage"  role="status" >
            ${flash.message}</div>
    </g:if>
    <g:if test="${flash.errors}">
        <div class="message" id="flashErrorMessage" style="padding-left: 2.2em; text-indent: 0em; color:red;" role="status" >
            <g:each in="${flash.errors}" var="error">${error}<br/></g:each>
        </div>
    </g:if>



<div class="row">
<div class="col-sm-6">
    <ol class="property-list seqSample" id="pat_sample_defaultinfo">

        <g:if test="${seqSample?.seqrun}">
            <li class="fieldcontain">
                <span id="seqrun-label" class="property-label"><g:message code="seqSample.seqrun.label"/></span>
                <span class="property-value" aria-labelledby="seqrun-label">
                    <g:link controller="seqrun" action="show" id="${seqSample?.seqrun?.id}">${seqSample?.seqrun?.encodeAsHTML()}</g:link>
                </span>
            </li>
        </g:if>

        <g:if test="${seqSample?.sampleName}">
            <li class="fieldcontain">
                <span id="sampleName-label" class="property-label"><g:message code="seqSample.sampleName.label" /></span>
                <span class="property-value" aria-labelledby="sampleName-label">
                    ${seqSample.sampleName}
                </span>
            </li>
        </g:if>

        <g:if test="${seqSample?.patSample}">
            <li class="fieldcontain">
                <span id="sample-label" class="property-label"><g:message code="seqSample.patSample.label" /></span>
                <span class="property-value" aria-labelledby="sample-label">
                    <g:link controller="patSample" action="show" id="${seqSample?.patSample?.id}">
                        ${seqSample?.patSample?.encodeAsHTML()}
                    </g:link>
                    Pat Assays: ${seqSample.patSample?.patAssays?.collect { it.testName }}
                </span>
            </li>
        </g:if>

        <g:if test="${seqSample?.relations}">
            <li class="fieldcontain">
                <span id="relation-label" class="property-label"><g:message code="seqSample.relations.label" /></span>
                <span class="property-value" aria-labelledby="relation-label">
                    <ul id="seqrunRelations">
                        <g:relation sample="${seqSample.id}" />
                        <li id="seqRelationsToggle"><a href="#relation-label" onclick="document.querySelector('#seqrunRelations').classList.add('showAll')">Show More</a></li>
                    </ul>
                </span>
            </li>
        </g:if>

        <g:if test="${seqSample?.panel}">
            <li class="fieldcontain">
                <span id="panel-label" class="property-label"><g:message code="seqSample.panel.label" /></span>
                <span class="property-value" aria-labelledby="panel-label"><g:fieldValue bean="${seqSample}" field="panel"/></span>
            </li>
        </g:if>

        <g:if test="${seqSample?.dnaconc}">
            <li class="fieldcontain">
                <span id="dnaconc-label" class="property-label"><g:message code="seqSample.dnaconc.label" /></span>
                <span class="property-value" aria-labelledby="dnaconc-label"><g:fieldValue bean="${seqSample}" field="dnaconc"/></span>
            </li>
        </g:if>

        <g:if test="${seqSample?.analysis}">
            <li class="fieldcontain">
                <span id="analysis-label" class="property-label"><g:message code="seqSample.analysis.label" /></span>
                <span class="property-value" aria-labelledby="analysis-label"><g:fieldValue bean="${seqSample}" field="analysis"/></span>
            </li>
        </g:if>

        <g:if test="${seqSample?.userName}">
            <li class="fieldcontain">
                <span id="userName-label" class="property-label"><g:message code="seqSample.userName.label" /></span>
                <span class="property-value" aria-labelledby="userName-label">
                    <a href="mailto:${seqSample.userEmail}">${seqSample.userName}</a>
                </span>
            </li>
        </g:if>

        <li class="fieldcontain">
            <span id="vep-label" class="property-label">CNV</span>
            <span class="property-value"><a href='${cnvViewerUrl}/?file=${seqSample?.seqrun}.txt&sample=${seqSample}' target="_blank">CNV Browser</a></span> 
        </li>

        %{--Authorised QC --}%
        <li class="fieldcontain">
            <span id="authorisedQcFlag-label" class="property-label"><g:message code="seqSample.authorisedQcFlag.label" /></span>

            <span class="property-value" aria-labelledby="authorisedQcFlag-label">
                <g:link controller="seqSample"  action="showQC" id="${seqSample?.id}">
                    <g:if test="${seqSample?.authorisedQcFlag}">
                        <g:qcPassFail authorised="${true}" passfailFlag="${seqSample?.passfailFlag}" />
                    </g:if>
                    <g:else>
                        Set QC
                    </g:else>
                </g:link>
            </span>
        </li>

        <li class="fieldcontain">
            <span id="firstReviewBy-label" class="property-label"><g:message code="seqSample.firstReviewBy.label"/></span>

            <span class="property-value" aria-labelledby="firstReviewBy-label">

                <g:if test="${seqSample?.secondReviewBy}">
                    <g:form action="authoriseReview" id="${seqSample?.id}" name="firstReviewRevokeForm">
                        Completed by <g:fieldValue bean="${seqSample}" field="firstReviewBy"/> <g:if test="${seqSample.firstReviewedDate}">on <g:fieldValue bean="${seqSample}" field="firstReviewedDate"/></g:if>
                        <g:if test="${isAdmin || isDev}"> <%-- only admins can revoke --%>
                            <g:submitButton id="firstReviewRevoke" name="first" value="Revoke First Review"/>
                            <g:hiddenField name="revoke" value="revoke"/>
                        </g:if>

                    </g:form>


                    <g:form action="authoriseReview" id="${seqSample?.id}" name="firstReviewRevokeForm">
                        Completed by <g:fieldValue bean="${seqSample}" field="secondReviewBy"/> <g:if test="${seqSample.secondReviewedDate}">on <g:fieldValue bean="${seqSample}" field="secondReviewedDate"/></g:if>
                        <g:if test="${isAdmin || isDev}"> <%-- only admins can revoke --%>
                            <g:submitButton id="secondReviewRevoke" name="second" value="Revoke Second Review"/>
                            <g:hiddenField name="revoke" value="revoke"/>
                        </g:if>

                    </g:form>

                </g:if>

                <g:elseif test="${seqSample?.firstReviewBy}">
                    <g:form action="authoriseReview" id="${seqSample?.id}" name="firstReviewForm">
                        Completed by <g:fieldValue bean="${seqSample}" field="firstReviewBy"/> <g:if test="${seqSample.firstReviewedDate}">on <g:fieldValue bean="${seqSample}" field="firstReviewedDate"/></g:if>
                        <g:if test="${isAdmin || isDev}"> <%-- only admins can revoke --%>
                            <g:submitButton id="firstReviewRevoke" name="first" value="Revoke First Review"/>
                            <g:hiddenField name="revoke" value="revoke"/>
                        </g:if>

                    </g:form>

                    <g:if test="${(isAdmin || isDev || isCurator || isLab) && ! (seqSample?.finalReviewBy) }"> <%-- only curator, admin and lab can auth first --%>
                        <g:form action="authoriseReview" id="${seqSample?.id}" name="firstReviewForm">
                            <g:submitButton id="firstReviewAuth" name="second" value="Authorise Second Review"/>

                        </g:form>
                    </g:if>


                </g:elseif>

                <g:if test="${! (seqSample?.firstReviewBy) }">  <%-- not revieweed --%>

                    <g:if test="${isAdmin || isDev || isCurator || isLab}">
                        <g:form action="authoriseReview" id="${seqSample?.id}" name="firstReviewForm">
                            <g:submitButton id="firstReviewAuth" name="first" value="Authorise First Review"/>
                        </g:form>
                    </g:if>
                    <g:else>
                        Not Completed
                    </g:else>
                </g:if>

            </span>
        </li>
        <li class="fieldcontain">
            <span id="finalReviewBy-label" class="property-label"><g:message code="seqSample.finalReviewBy.label"/></span>

            <span class="property-value" aria-labelledby="finalReviewBy-label">
                <g:if test="${seqSample?.finalReviewBy}">
                    <g:form action="authoriseReview" id="${seqSample?.id}" name="finalReviewRevokeForm">
                        Completed by <g:fieldValue bean="${seqSample}" field="finalReviewBy"/> <g:if test="${seqSample.finalReviewedDate}">on <g:fieldValue bean="${seqSample}" field="finalReviewedDate"/></g:if>
                        <g:hiddenField name="formtype" id="formtype" value="finalReviewRevokeForm"/>
                        <g:if test="${isAdmin || isDev}"> <%-- only admins can revoke --%>
                            <g:submitButton id="finalReviewRevoke" name="final" value="Revoke Final Review"/>
                            <g:hiddenField name="revoke" value="revoke" />
                        </g:if>
                    </g:form>

                </g:if>
                <g:else>
                    <g:if test="${isAdmin || isCurator || isDev}"> <%-- only curator and admin can auth final --%>
                    <g:form action="authoriseReview" id="${seqSample?.id}" name="finalReviewForm">
                        <g:submitButton id="finalReviewAuth" name="final" value="Authorise Final Review"/>
                        <g:hiddenField name="formtype" id="formtype" value="finalReviewForm"/>
                    </g:form>
                    </g:if>
                    <g:else>
                        Not Completed
                    </g:else>
                </g:else>

            </span>
        </li>

        <li class="fieldcontain" id="showTags">
            <span class="property-label">Tags of SeqSample:<br>${seqSample?.toString()}</span>
            <div id="showTagBox" class="fb-box tags_field property-value">
                <textarea id="showTagTextArea" placeholder="Enter Tags Here" class="ui-autocomplete-input" autocomplete="off"></textarea>
            </div>
        </li>
    </ol>
</div>
    <script>
    var tags = ${seqSample?.tags as grails.converters.JSON},
            tagbox = d3.select("#showTagBox").on('click', function(){
                $('#showTagTextArea').focus();
            });

    tags.forEach(function(tag){
        PathOS.tags.drawTag(tagbox, tag);
    });
    $("#showTagTextArea").autocomplete({source:<g:allTags/>});

    var SeqSampleID = parseInt("${seqSample?.id}");
    $("#showTagTextArea").on("keydown", function(e){
        if (e && e.keyCode && e.keyCode == 13 && $(document.activeElement).is("#showTagTextArea")) {

            var tag = $('#showTagTextArea').val().trim();

            if (tag && tag !== '' && SeqSampleID) {
                PathOS.tags.addTag(tagbox, tag, "seqsample", SeqSampleID);
            }
            $('#showTagTextArea').val('');
        }  else if (e && e.keyCode && e.keyCode == 8 && $(document.activeElement).is("#showTagTextArea") && $("#showTagTextArea").val() === "") {
            if ($("#showTagBox.tags_field .tagdiv:last").length !== 0) {
                if ($("#showTagBox .tagdiv:last").hasClass('deleteFlag')) {
                    var data = d3.select($("#showTagBox.tags_field .tagdiv:last")[0]).datum();
                    if (confirm('Remove tag "' + data.label + '" from this object?')) {
                        var params = {
                            type: "seqsample",
                            objid: SeqSampleID,
                            tagid: data.id
                        };
                        $.ajax({
                            type: "DELETE",
                            url: "/PathOS/tag/removeLink?" + $.param(params),
                            success: function (result) {
                                if (result != 'fail') {
                                    $('#showTagBox .tag-' + data.id).remove();
                                }
                            },
                            cache: false,
                            contentType: false,
                            processData: false
                        });
                    }
                } else {
                    $("#showTagBox.tags_field .tagdiv:last").toggleClass("deleteFlag").on('click', function () {
                        var data = d3.select($("#showTagBox.tags_field .tagdiv:last")[0]).datum();
                        var params = {
                            type: "seqsample",
                            objid: SeqSampleID,
                            tagid: data.id
                        };
                        $.ajax({
                            type: "DELETE",
                            url: "/PathOS/tag/removeLink?" + $.param(params),
                            success: function (result) {
                                if (result != 'fail') {
                                    $('#showTagBox .tag-' + data.id).remove();
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
</script>
<div class="col-sm-6">
    <ol class="property-list seqSample"  id="pat_sample_hollyinfo">
<g:if test="${seqSample?.patSample?.hollyLastUpdated}">


    <g:if test="${seqSample?.patSample?.pathologist}">
        <li class="fieldcontain">
            <span id="seqrun-label" class="property-label">Pathologist Review</span>
            <span class="property-value" aria-labelledby="sampleName-label">
                ${seqSample.patSample.pathologist}
            </span>
        </li>
    </g:if>

    <li class="fieldcontain">

        <span id="seqrun-label" class="property-label"> </span>
        <span class="property-value" aria-labelledby="sampleName-label">
            <g:form action="updateHolly" id="${seqSample?.patSample?.id}" name="refreshHollyForm">
                <g:submitButton id="updateHolly" name="first" value="Update Patient Data"/>
                <g:hiddenField name="patsampleid" id="patsampleid" value="${seqSample?.patSample?.id}"/>
                <g:hiddenField name="seqsampleid" id="seqsampleid" value="${seqSample?.id}"/>
                <g:hiddenField name="hollylastupdate" id="hollylastupdate" value="${seqSample?.patSample?.hollyLastUpdated}"/>
            </g:form>
        </span>

    </li>




    <g:if test="${seqSample?.patSample?.pathComments}">
        <li class="fieldcontain">
            <span id="seqrun-label" class="property-label">Pathologist Comments</span>
            <span class="property-value" aria-labelledby="sampleName-label">
                ${seqSample.patSample.pathComments}
            </span>
        </li>
    </g:if>


    <g:if test="${seqSample?.patSample?.repMorphology}">

        <li class="fieldcontain">
            <span id="seqrun-label" class="property-label">Report Morphology</span>
            <span class="property-value" aria-labelledby="sampleName-label">
                ${seqSample.patSample.repMorphology}
            </span>
        </li>
    </g:if>

    <g:if test="${seqSample?.patSample?.pathMorphology}">
        <li class="fieldcontain">
            <span id="seqrun-label" class="property-label">Notes</span>
            <span class="property-value" aria-labelledby="sampleName-label">
                ${seqSample.patSample.pathMorphology}
            </span>
        </li>
    </g:if>


    <g:if test="${seqSample?.patSample?.retSite}">
        <li class="fieldcontain">
            <span id="seqrun-label" class="property-label">Tissue Site</span>
            <span class="property-value" aria-labelledby="sampleName-label">
                ${seqSample.patSample.retSite}
            </span>
        </li>
    </g:if>


    <g:if test="${seqSample?.patSample?.tumourPct}">
        <li class="fieldcontain">
            <span id="seqrun-label" class="property-label">Tumour %</span>
            <span class="property-value" aria-labelledby="sampleName-label">
                ${seqSample.patSample.tumourPct}
            </span>
        </li>
    </g:if>

   <%-- <g:if test="${seqSample?.sample?.hollyLastUpdated}">
        <li class="fieldcontain">
            <span id="seqrun-label" class="property-label">Last Update Details</span>
            <span class="property-value" aria-labelledby="sampleName-label">
                ${seqSample.sample.hollyLastUpdated}
            </span>
        </li>
    </g:if>--%>





</g:if>
<g:else>
        <li class="fieldcontain">
            <span id="seqrun-label" class="property-label">Patient Details</span>
            <span class="property-value" aria-labelledby="sampleName-label">
                <g:form action="updateHolly" id="${seqSample?.patSample?.id}" name="refreshHollyForm">
                    <g:submitButton id="updateHolly" name="first" value="Check Patient Details"/>
                    <g:hiddenField name="patsampleid" id="patsampleid" value="${seqSample?.patSample?.id}"/>
                    <g:hiddenField name="seqsampleid" id="seqsampleid" value="${seqSample?.id}"/>
                    <g:hiddenField name="hollylastupdate" id="hollylastupdate" value="0"/>
                </g:form>
            </span>
        </li>
</g:else>

    <li class="fieldcontain">

        <span id="seqrun-label" class="property-label"> </span>
        <span class="property-value" aria-labelledby="sampleName-label">
            <g:form action="updateHolly" id="${seqSample?.patSample?.id}" name="refreshHollyForm">
                <g:submitButton id="updateHolly" name="first" value="Update Patient Data"/>
                <g:hiddenField name="patsampleid" id="patsampleid" value="${seqSample?.patSample?.id}"/>
                <g:hiddenField name="seqsampleid" id="seqsampleid" value="${seqSample?.id}"/>
                <g:hiddenField name="hollylastupdate" id="hollylastupdate" value="${seqSample?.patSample?.hollyLastUpdated}"/>
            </g:form>
        </span>

    </li>

    <li class="fieldcontain">

        <span id="seqrun-label" class="property-label">Clinical Context</span>
        <span class="property-value" aria-labelledby="sampleName-label">
            <g:form action="updateClinContext" id="${seqSample?.id}" name="refreshHollyForm">


                <g:select id="clinContext" name="clinContext" from="${clinContextList}"  noSelection="${['None':'None']}" optionKey="code"  value="${seqSample.clinContext? seqSample.clinContext.code : 'None'}" />
                <g:submitButton id="updateClinContext" name="updateClinClontext" disabled="true" value="Change" style="margin-top:4px;"/>
                <g:hiddenField name="patsampleid" id="patsampleid" value="${seqSample?.patSample?.id}"/>
                <g:hiddenField name="seqsampleid" id="seqsampleid" value="${seqSample?.id}"/>
                <g:hiddenField name="hollylastupdate" id="hollylastupdate" value="${seqSample?.patSample?.hollyLastUpdated}"/>
            </g:form>
        </span>

    </li>

</ol>
</div>
</div>
    </div>
</section>


<g:if test="${cnvSize > 0}">
<div id="tabs">
    <ul>
        <li><a href="#tabs-1">SeqVariants</a></li>
        <li><a href="#tabs-2">SeqCNVs</a></li>
    </ul>
</g:if>
<div id="tabs-1" style="padding: 0px;">

<div style="margin: 2em; overflow: auto;" id="gridContainerDiv">

    <div id="filterNotification">
        A <span id="appliedfiltertype"></span>filter has been applied. <span id="postfilter_count">0</span> of <span id="prefilter_count">${svSize}</span> records pass filter. <span id="filterShowMessage">Show filter details</span> <br/>
        <div id="filterDescription" style="display:none;"></div>
        <br/>

    </div>

    <grid:grid  name="curation">
        <grid:set caption='${seqSample.seqVariants.size()} Total Sequenced Variants'/>
        <grid:set col='curated'         width="130" formatter='f:variantFormatter' editable='false'/>
        <grid:set col='gene'            width="70"  formatter='f:geneFormatter' editable='false'/>
        <grid:set col='siftCat'         width="45"  formatter='f:classFormatter' editable='false'/>
        <grid:set col='mutTasteCat'     width="45"  formatter='f:classFormatter' editable='false'/>
        <grid:set col='polyphenCat'     width="45"  formatter='f:classFormatter' editable='false'/>
        <grid:set col='lrtCat'          width="45"  formatter='f:classFormatter' editable='false'/>
        <grid:set col='mutAssessCat'    width="45"  formatter='f:classFormatter' editable='false'/>
        <grid:set col='fathmmCat'       width="45"  formatter='f:classFormatter' editable='false'/>
        <grid:set col='metaSvmCat'      width="45"  formatter='f:classFormatter' editable='false'/>
        <grid:set col='metaLrCat'       width="45"  formatter='f:classFormatter' editable='false'/>
        <grid:set col='variant'         width="130" editable='false'/>
        <grid:set col='sampleName'      width="65"  hidden='f:true' editable='false' />
        <grid:set col='clin_sig'        width="70"  editable='false'/>
        <grid:set col='filterFlag'      width="100" editable='false'/>
        <grid:set col='hgvsc'           width="170" editable='false'/>
        <grid:set col='hgvsp'           width="150" editable='false'/>
        <grid:set col='consequence'     width="150" editable='false'/>
        <grid:set col='gmaf'            width="50"  editable='false' formatter='number'/>
        <grid:set col='esp'             width="50"  editable='false' formatter='number'/>
        <grid:set col='exac'            width="50"  editable='false' />
        <grid:set col='exon'            width="70"  editable='false'/>
        <grid:set col='cytoband'        width="50"  editable='false'/>
        <grid:set col='cadd'            width="50"  editable='false' formatter='number'/>
        <grid:set col='cadd_phred'      width="50"  editable='false' formatter='number'/>
        <grid:set col='zygosity'        width="70"  align= 'center'  editable='false' hidden="${seqSample?.panel?.panelGroup != 'MP FLD Germline Production'}"/>
        <grid:set col='varFreq'         width="65"  editable='false' />
        <grid:set col='varDepth'        width="55"  editable='false' formatter='integer'/>
        <grid:set col='readDepth'       width="55"  editable='false' formatter='integer'/>
        <grid:set col='cosmicOccurs'    width="150" hidden='f:true'  editable='false'/>
        <grid:set col='pubmed'          width="70"  hidden='f:true'  editable='false'/>
        <grid:set col='varPanelPct'     width="70"  hidden='f:false'  editable='false' formatter='number'/>
        <grid:set col='ens_transcript'  width="70"  hidden='f:true'  editable='false'/>
        <grid:set col='ens_gene'        width="70"  hidden='f:true'  editable='false'/>
        <grid:set col='ens_protein'     width="70"  hidden='f:true'  editable='false'/>
        <grid:set col='ens_canonical'   width="70"  hidden='f:true'  editable='false'/>
        <grid:set col='refseq_mrna'     width="90"  hidden='f:true'  editable='false'/>
        <grid:set col='refseq_peptide'  width="90"  hidden='f:true'  editable='false'/>
        <grid:set col='existing_variation' width="70" hidden='f:true'  editable='false'/>
        <grid:set col='domains'         width="70"  hidden='f:true'  editable='false'/>
        <grid:set col='genedesc'        width="70"  hidden='f:true'  editable='false'/>
        <grid:set col='omim_ids'        width="70"  hidden='f:true'  editable='false'/>
        <grid:set col='biotype'         width="70"  hidden='f:true'  editable='false'/>
        <grid:set col='curated'         cellattr='f:curatedTooltip' editable='false'/>
        <grid:set col='filterFlag'      cellattr='f:filterFlagTooltip' editable='false'/>
        <grid:set col='cosmic'          cellattr='f:cosmicTooltip' editable='false'/>
        <grid:set col='siftVal'         width="45"  hidden='f:true' editable='false'/>
        <grid:set col='mutTasteVal'     width="45"  hidden='f:true' editable='false'/>
        <grid:set col='polyphenVal'     width="45"  hidden='f:true' editable='false'/>
        <grid:set col='clinvarVal'      width="45"  hidden='f:true' editable='false'/>
        <grid:set col='clinvarCat'      width="45"  hidden='f:true' formatter='f:classFormatter' editable='false'/>
        <grid:set col='lrtVal'          width="45"  hidden='f:true' editable='false'/>
        <grid:set col='mutAssessVal'    width="45"  hidden='f:true' editable='false'/>
        <grid:set col='fathmmVal'       width="45"  hidden='f:true' editable='false'/>
        <grid:set col='metaSvmVal'      width="45"  hidden='f:true' editable='false'/>
        <grid:set col='metaLrVal'       width="45"  hidden='f:true' editable='false'/>
        <grid:set col='vepHgvsg'        width="150" hidden='f:true' editable='false'/>
        <grid:set col='vepHgvsc'        width="150" hidden='f:true' editable='false'/>
        <grid:set col='vepHgvsp'        width="150" hidden='f:true' editable='false'/>
        <grid:set col='mutStatus'       width="150" hidden='f:true' editable='false'/>
        <grid:set col='mutError'        width="150" hidden='f:true' editable='false'/>
        <grid:set col='varcaller'       width="70"  hidden='f:false' editable='false'/>
        <grid:set col='amps'            width="70"  hidden='f:true'  editable='false'/>
        <grid:set col='numamps'         width="70"  hidden='f:false' editable='false'/>
        <grid:set col='ampbias'         width="70"  hidden='f:false'  editable='false'/>
        <grid:set col='homopolymer'     width="45"  hidden='f:false' editable='false'/>
    </grid:grid>

    <grid:exportButton name="curation" formats="['csv', 'excel']" exportId="123456"/>
    <br/>
</div>

</div>
<g:if test="${cnvSize > 0}">
<div id="tabs-2" style="padding: 0px;">

    <div style="margin: 2em; overflow: auto;" id="gridContainerDiv">

        <div id="cnvfilterNotification">
            A filter has been applied. <span id="cnvpostfilter_count">0</span> of <span id="cnvprefilter_count">${cnvSize}</span> records pass filter. <span id="cnvfilterShowMessage">Show filter details</span> <br/>
            <div id="cnvfilterDescription" style="display:none;"></div>
            <br/>

        </div>


    <grid:grid  name="cnv">
        <grid:set caption='Sequenced CNVs'/>

        <grid:set col='seqSample'       width="70" hidden='f:true' editable='false'/>
        <grid:set col='gene'        width="100" hidden='f:false' editable='false'/>
        <grid:set col='cnv_type'       width="70"  hidden='f:false' editable='false'/>
        <grid:set col='chr'            width="75"  hidden='f:false'  editable='false'/>
        <grid:set col='startpos'         width="70"  hidden='f:false' editable='false'/>
        <grid:set col='endpos'         width="70"  hidden='f:false'  editable='false'/>
        <grid:set col='lr_mean'     width="100"  hidden='f:false' editable='false'/>
        <grid:set col='lr_median'     width="100"  hidden='f:false' editable='false'/>
        <grid:set col='lr_sd'     width="100"  hidden='f:false' editable='false'/>
        <grid:set col='gainloss'     width="70"  hidden='f:false' editable='false'/>
        <grid:set col='n'     width="50"  hidden='f:false' editable='false'/>
        <grid:set col='probes_pct'     width="100"  hidden='f:false' editable='false'/>
        <grid:set col='pval'     width="100"  hidden='f:false' editable='false'/>
        <grid:set col='pval_adj'     width="100"  hidden='f:false' editable='false'/>

    </grid:grid>
    <grid:exportButton name="cnv" formats="['csv', 'excel']" exportId="123457"/>

        <br/><br/>
        <g:if test="${cnvUrl}">
        <span id="hideshowcnv" class="hideshowbutton" onclick="hideshowcnv('cnvImageDiv');">Show CNV image</span>

        <div id="cnvImageDiv"  style="display:none">

            <br/><br/>
            <a href='${cnvViewerUrl}/?file=${seqSample?.seqrun}.txt&sample=${seqSample}' target="_blank">CNV Viewer (experimental)</a>
            <br/><br/>
            <img src="${cnvUrl}"/> <%--
            <g:img dir='payload/cnv_images' file="${cnvUrl}" alt="Logo" border="0" /> --%>

        </div>
            <br/><br/>
        </g:if>
    </div>
</div>
</g:if>




</div>
</body>

<r:script>
<%-- window.onload = loadSavedUserGrid(); --%>


        function hideshowcnv(element)
        {
            var which = document.getElementById(element)
            if (!which)
                return
            if (which.style.display=="inline") {
                which.style.display="none";
                $('#hideshowcnv').text('Show CNV image');
                }
            else {
                which.style.display="inline";
                $('#hideshowcnv').text('Hide CNV image');
            }

            return false;

        }

        function hideshowreports(element)
        {
            var which = document.getElementById(element)
            if (!which)
                return
            if ($('#hideshowreports').text() == 'Hide previously generated reports') {
                $('#'+element).slideToggle("fast");
                $('#hideshowreports').text('Show previously generated reports');
                }
            else {

                 $('#'+element).slideToggle("fast");
                $('#hideshowreports').text('Hide previously generated reports');
            }

            return false;

        }

    function afterEdit()
    {
    //    jQuery.jgrid.info_dialog('Info', 'Record changed !', jQuery.jgrid.edit.bClose, {buttonalign: 'right'});
    reloadGrid();
        $.jGrowl("Record changed")
    return true;
    }

    function reloadGrid()
    {
    jQuery('#curation_table').trigger('reloadGrid');
    return true;
    }


    /**
    * Add explanatory tooltip descriptions for filterflags
    *
    * @param rowId
    * @param val        List of comma separated filter flags
    * @param rawObject
    * @param cm
    * @param rdata
    * @returns {string} Cell title attribute title="tooltip description"
    */
    function filterFlagTooltip(rowId, val, rawObject, cm, rdata)
    {
    var arr = val.split(',');
    var ttl = '';
    for(var i = 0; i <  arr.length; i++)
    {
        desc = '';
        if ( arr[i] == 'pass' ) desc = "${VarFilterService.filters['pass']}";
        if ( arr[i] == 'pnl'  ) desc = "${VarFilterService.filters['pnl' ]}";
        if ( arr[i] == 'gaf'  ) desc = "${VarFilterService.filters['gaf' ]}";
        if ( arr[i] == 'con'  ) desc = "${VarFilterService.filters['con' ]}";
        if ( arr[i] == 'vad'  ) desc = "${VarFilterService.filters['vad' ]}";
        if ( arr[i] == 'vrd'  ) desc = "${VarFilterService.filters['vrd' ]}";
        if ( arr[i] == 'vaf'  ) desc = "${VarFilterService.filters['vaf' ]}";
        if ( arr[i] == 'nof'  ) desc = "${VarFilterService.filters['nof' ]}";
        if ( arr[i] == 'blk'  ) desc = "${VarFilterService.filters['blk' ]}";
        if ( arr[i] == 'sin'  ) desc = "${VarFilterService.filters['sin' ]}";
        if ( arr[i] == 'amp'  ) desc = "${VarFilterService.filters['amp' ]}";
        if ( arr[i] == 'oor'  ) desc = "${VarFilterService.filters['oor' ]}";
        if ( ttl.length > 1 ) ttl = ttl + '\n';
        ttl = ttl + arr[i] + ': ' + desc;
    }

    return 'title="' + ttl + '"';
}

/**
* Add evidence tooltip for curated variant
*
* @param rowId      the id of the row
* @param val        the value which will be added in the cell
* @param rawObject  the raw object of the data row
* @param cm         all the properties of this column listed in the colModel
* @param rdata      the data row which will be inserted in the row
*
* @returns {string} Cell title attribute title="tooltip description"
*/
function curatedTooltip(rowId, val, rawObject, cm, rdata)
{
//    console.log("Testing: "+JSON.stringify(rdata))

    if ( val == '' ) return '';
    var ttl = rdata['curated_evd'];

    return 'title="' + ttl + '"';
}

/**
* Add cosmic details tooltip for curated variant
*
* @param rowId      the id of the row
* @param val        the value which will be added in the cell
* @param rawObject  the raw object of the data row
* @param cm         all the properties of this column listed in the colModel
* @param rdata      the data row which will be inserted in the row
*
* @returns {string} Cell title attribute title="tooltip description"
*/
function cosmicTooltip(rowId, val, rawObject, cm, rdata)
{
    if ( rdata['cosmic'] == '' ) return '';

    var ttl = rdata['cosmicOccurs'];
    return 'title="' + ttl + '"';
}

/**
* Formatter for colouring and linking to a curated CurVariant
*
* @param cellvalue      PLON 5-level pathogenicity string
* @param options
* @param rowObject      Array of cell values: expecting the first value to be the ID of the linked CurVariant
* @returns {string}     Link to curated CurVariant nicely coloured by pathogenicity
*/
function variantFormatter( cellvalue, options, rowObject )
{
    if ( cellvalue == null || cellvalue.length == 0 ) return '';

    var fld = classFormatter( cellvalue, options, rowObject );

    //  Todo: this is a maintenance risk - hard wired link and positional parameter to pass an object linkage through to Javascript
    //

    return "<a href='/PathOS/curVariant/sampleLink?id=" + rowObject[0] + "'>" + fld + '</a>';
}

/**
* Formatter for colouring a curated CurVariant
*
* @param cellvalue      PLON 5-level pathogenicity string
* @param options
* @param rowObject      Array of cell values
* @returns {string}     Nicely coloured by pathogenicity
*/
function classFormatter( cellvalue, options, rowObject )
{
    if ( cellvalue == null || cellvalue.length == 0 ) return '';

    colour = '#000000';
    bg     = '#ffffff';
    var m = cellvalue.match( /^C\d/ );
    if ( m == 'C1' )    {colour = '#000000'; bg = '#fffdc1';}
    if ( m == 'C2' )    {colour = '#000000'; bg = '#f4d374';}
    if ( m == 'C3' )    {colour = '#000000'; bg = '#e89e53';}
    if ( m == 'C4' )    {colour = '#ffffff'; bg = '#d65430';}
    if ( m == 'C5' )    {colour = '#ffffff'; bg = '#ae2334';}

    var fld = "<noop style=\'color: " + colour + "; margin-right:-2px; margin-left:-2px; padding:4px; background-color: " + bg + "\'>" + cellvalue + '</noop>';

    return fld;
}

/**
* Formatter for gene linking to genecards
*
* @param cellvalue      gene
* @param options
* @param rowObject      Row
* @returns {string}     Link to gene cards www.genecards.org/cgi-bin/carddisp.pl?gene=TP53
*/
function geneFormatter( cellvalue, options, rowObject )
{
    //  Link to Gene cards for this gene
    //
    return "<a href='http://www.genecards.org/cgi-bin/carddisp.pl?gene=" + cellvalue + "' target='_blank'>" + cellvalue + '</a>';
}

/**
* Column Chooser function - called when grid navigation icon clicked
*/
function columnChooser()
{
    jQuery('#curation_table').jqGrid(
        'columnChooser',
        {
            msel:   'multiselect',
            dlog:   'dialog',
            height: 500
        }
    )
}

/**
* get url GET param
**/
function getUrlParameter(sParam)
{
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++)
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam)
        {
            return sParameterName[1];
        }
    }
}

//reset prefs (either sample, if its reviewed, or personal - wrapper function for those two cases)
function resetPrefs() {
    if ( (${isFinalReviewed} )) {
        if (${isAdmin} || ${isCurator}) {
            resetReviewedCurateFilterPrefs()
        } else {  //this should never happen: button should be disabled for non admins
            $.jGrowl("Error: you cannot reset the filter settings for a reviewed sample unless you are an administrator or curator")
        }
     } else {
            resetCurateFilterPrefs()
     }
}
function resetFilterPrefs()
{
    //ajax: are you sure?
    //remote call to reset everything (in fact, delete user pref object for our user)
    //reload grid w everything reset
    if(confirm("Are you sure you want to reset your column preferences?"))
    {
        sampleId = ${seqSample?.id}
        //Ok button pressed...
        ${remoteFunction(controller: 'seqVariant', action: 'resetUserGridPrefs',onComplete: 'window.location.reload()')};

        //$.jGrowl("Your preferences have been reset");
        //var curationGrid = $('#curation_table');

        // curationGrid.trigger('reloadGrid');

    }

}


function resetReviewedCurateFilterPrefs()
{
    //ajax: are you sure?
    //remote call to reset everything (in fact, delete sample pref object for our sample)
    //reload grid w everything reset
    if(confirm("Are you sure you want to reset the review seq sample preferences? This will clear them completely"))
    {
        sampleId = ${seqSample?.id}
        ${remoteFunction(controller: 'seqVariant', action: 'resetReviewedGridPrefs',onComplete: 'window.location.reload()',params: '{ssid: sampleId}')};

    }

}


//save reviewed sample filter settings
function saveReviewCurateFilterPrefs()
{
    outstring = '';
    showncols = [];
    hiddencols = [];
    showncolsstring = '';
    hiddencolsstring = '';

    var allColumns = $("#curation_table").jqGrid("getGridParam", "colModel").slice();
    allColumns.splice(0,1);


    for (var i=0; i < allColumns.length; i++) {
        colname = allColumns[i].name;


        if($('#curation_table_'+colname).is(':visible'))
        {
            //outstring = outstring + colname + ' VIS' + '\n';
            showncols.push(colname);
            showncolsstring = showncolsstring + colname +','
        } else {
            // outstring = outstring + colname + ' HIDDEN' + '\n';
            hiddencols.push(colname);
            hiddencolsstring = hiddencolsstring + colname +','
        }
    }
    hiddencolsJson = JSON.stringify(hiddencols);
    showncolsJson = JSON.stringify(showncols);
    /* save prefs... controller: 'SeqVariant',  */
    /*getGridParam("postData").filters;*/


    sampleId = ${seqSample?.id}

    searchFilterString = ( $("#curation_table").getGridParam("postData").filters  );
    if(!searchFilterString) {
        searchFilterString = '';
    }

    var gridInfo = new Object();
    var curationGrid = $('#curation_table');
    gridInfo.sortname = curationGrid.jqGrid('getGridParam', 'sortname');
    gridInfo.sortorder = curationGrid.jqGrid('getGridParam', 'sortorder');

    gridInfo.postData = curationGrid.jqGrid('getGridParam', 'postData');
    //gridInfo.search = curationGrid.jqGrid('getGridParam', 'search');

    var gridInfoParamsJson = (JSON.stringify(gridInfo));
    var remapOrder =  curationGrid.jqGrid('getGridParam', 'remapColumns');


    remapOrder = String(remapOrder)

    ${remoteFunction(controller: 'seqVariant', action: 'saveReviewedGridPrefs', params: '{columnsShowJSON: showncolsJson, columnsHideJSON: hiddencolsJson, gridColumnOrder: remapOrder, gridInfoJSON: gridInfoParamsJson, ssid: sampleId}')};


      $.jGrowl("The reviewed grid preferences have been saved");
}

//save filter prefs function: save either user grid settings (if not reviewed) or sample settings (if reviewed)
function saveFilterPrefs()
{
     if ( (${isFinalReviewed} )) {
        if (${isAdmin} || ${isCurator}) {
            saveReviewCurateFilterPrefs()
        } else {  //this should never happen: button should be disabled for non admins
            $.jGrowl("Error: you cannot save the filter settings for a reviewed sample unless you are the administrator or curator")
        }
     } else {
            saveCurateFilterPrefs()
     }

}

//save personal user filter settings
function saveCurateFilterPrefs()
{
    outstring = '';
    showncols = [];
    hiddencols = [];
    showncolsstring = '';
    hiddencolsstring = '';

    var allColumns = $("#curation_table").jqGrid("getGridParam", "colModel").slice();
    allColumns.splice(0,1);


    for (var i=0; i < allColumns.length; i++) {
        colname = allColumns[i].name;


        if($('#curation_table_'+colname).is(':visible'))
        {
            //outstring = outstring + colname + ' VIS' + '\n';
            showncols.push(colname);
            showncolsstring = showncolsstring + colname +','
        } else {
            // outstring = outstring + colname + ' HIDDEN' + '\n';
            hiddencols.push(colname);
            hiddencolsstring = hiddencolsstring + colname +','
        }
    }
    hiddencolsJson = JSON.stringify(hiddencols);
    showncolsJson = JSON.stringify(showncols);
    /* save prefs... controller: 'SeqVariant',  */
    /*getGridParam("postData").filters;*/


    sampleId = ${seqSample?.id}

    searchFilterString = ( $("#curation_table").getGridParam("postData").filters  );
    if(!searchFilterString) {
        searchFilterString = '';
    }

    var gridInfo = new Object();
    var curationGrid = $('#curation_table');
    gridInfo.sortname = curationGrid.jqGrid('getGridParam', 'sortname');
    gridInfo.sortorder = curationGrid.jqGrid('getGridParam', 'sortorder');

    gridInfo.postData = curationGrid.jqGrid('getGridParam', 'postData');
    //gridInfo.search = curationGrid.jqGrid('getGridParam', 'search');

    var gridInfoParamsJson = (JSON.stringify(gridInfo));
    var remapOrder =  curationGrid.jqGrid('getGridParam', 'remapColumns');


    remapOrder = String(remapOrder)

    ${remoteFunction(controller: 'seqVariant', action: 'saveUserGridPrefs', params: '{columnsShowJSON: showncolsJson, columnsHideJSON: hiddencolsJson, gridColumnOrder: remapOrder, gridInfoJSON: gridInfoParamsJson}')};


    $.jGrowl("Your preferences have been saved");

}


//  Actionable CNVs
//
var actionableCnvs =
{ "groupOp": "AND",
    "groups": [
    {"groupOp": "OR",
      "rules": [
        { "field": "gene", "op": "eq", "data": "MYC" },
        { "field": "gene", "op": "eq", "data": "HER2" }
      ]
    }],
      "rules": [
        { "field": "pval", "op": "lt", "data": "0.05" },
        { "field": "lr_median", "op": "ge", "data": "1.5"}
      ]
};
<%-- this sets filter templates as : "var myTemolateName = thetemplate" --%>
<g:each in="${filterTemplates}">
    var ${it.key} =  ${it.value}
</g:each>


//$('#curation_table').jqGridAfterGridComplete = loadSavedUserGrid();

// jQuery("#curation_table").jqGrid.jqGridAfterGridComplete = loadSavedUserGrid();


$( document ).ready(function() {


        //
        //we need all ids of unfiltered jqgrid rows to pass to the controller for curation checks
        //this is hard due to pagination - only elements on current page are loaded
        //we do this as described in http://stackoverflow.com/questions/9775115/get-all-rows-not-filtered-from-jqgrid
        //override jqgid.from.select
        var oldFrom = $.jgrid.from,
                lastSelected;

        $.jgrid.from = function (source, initalQuery) {
            var result = oldFrom.call(this, source, initalQuery),
                    old_select = result.select;
            result.select = function (f) {
                lastSelected = old_select.call(this, f);
                return lastSelected;
            };
            return result;
        };



       $('#clinContext').change(function(){  //enable change mut context button
                 $('#updateClinContext').prop('disabled',false);
        });




     $("#finalReviewForm").submit(function() {
       saveReviewCurateFilterPrefs()
        //alert(  $('#curation_table').jqGrid('getDataIDs'))
     });


    function disableGridButtons() {
              //disable buttons on grid
                $('#showhidecols').addClass('ui-state-disabled')
                $('#resetcols').addClass('ui-state-disabled')
                $('#saveuserprefs').addClass('ui-state-disabled')
                $('#search_curation_table').addClass('ui-state-disabled')
                $('#refresh_curation_table').addClass('ui-state-disabled')
                $('#view_curation_table').addClass('ui-state-disabled')
                jQuery("#curation_table").jqGrid('hideCol','act') //hide edit col?
    }


    function loadSavedUserGrid() {

        var colname = '';
        var curationGrid = $('#curation_table');

        var gridColOrder=[${prefsColumnRemap}]

        if(gridColOrder) {
            curationGrid.jqGrid('remapColumns',gridColOrder,true);
            curationGrid.trigger('reloadGrid');
        }
        <g:each var="colname" in="${prefsShowCols}">
        <%-- $('#curation_table_'+"${colname}").showCol()--%>
            curationGrid.jqGrid('showCol',"${colname}")
        </g:each>

        <g:each var="colname" in="${prefsHideCols}">
        <%--$('#curation_table_'+"${colname}").hideCol()--%>
            curationGrid.jqGrid('hideCol',"${colname}")
        </g:each>

        if ( (${isFinalReviewed}) ) {
            disableGridButtons()    //need to call this to re-hide edit button if we've locked our grid
        }
    }

    //end function defintions
    //



        loadSavedUserGrid(); //this loads saved hidden cols and colorder


        $("#filterShowMessage").click(function(){
            $("#filterDescription").slideToggle("fast");
            if ($("#filterShowMessage").text() == 'Show filter details') {
                $("#filterShowMessage").text('Hide filter details')
            } else {
                $("#filterShowMessage").text('Show filter details')
            }

        });

        $("#cnvfilterShowMessage").click(function(){
            $("#cnvfilterDescription").slideToggle("fast");
            if ($("#cnvfilterShowMessage").text() == 'Show filter details') {
                $("#cnvfilterShowMessage").text('Hide filter details')
            } else {
                $("#cnvfilterShowMessage").text('Show filter details')
            }

        });


         $('#curation_table').jqGrid('setGridParam', {
                gridComplete: function(data){
                    postData =  $('#curation_table').jqGrid('getGridParam', 'postData');

                    if(postData.filters && postData.filters != '{"groupOp":"AND","rules":[]}') { //if a real filter is appied
                        $('#filterNotification').show();
                        //need to translate the filter...

                        ${remoteFunction(controller: 'seqVariant', action: 'filterToReadable', params: '{filters: postData.filters}', update: "filterDescription")};
                        //alert(filter)
                        $('#postfilter_count').text($('#curation_table').jqGrid('getGridParam', 'records')); //54 or 32 (current total not incl pagination

                    } else {
                        $('#filterNotification').hide()
                    }
                }
         })


      $('#cnv_table').jqGrid('setGridParam', {
                    gridComplete: function(data){
                        postData =  $('#cnv_table').jqGrid('getGridParam', 'postData');

                        if(postData.filters && postData.filters != '{"groupOp":"AND","rules":[]}') { //if a real filter is appied
                            $('#cnvfilterNotification').show();
                            //need to translate the filter...

                             ${remoteFunction(controller: 'seqVariant', action: 'filterToReadable', params: '{filters: postData.filters}', update: "cnvfilterDescription")};
                            //alert(filter)
                            $('#cnvpostfilter_count').text($('#cnv_table').jqGrid('getGridParam', 'records')); //54 or 32 (current total not incl pagination

                        } else {
                            $('#cnvfilterNotification').hide()
                        }
                    }
             })



        //Here we set some element properties:
       //WORKAROUND: despite setting sortable: false, two of our columns are still sortable on click and give an powerassertionerror when clicked. make them unsortable.
       //todo do this programmattically
        $("#curation_table_report").unbind("click");
        $("#curation_table_curate").unbind("click");
        $("#curation_table_curated").unbind("click");
        $("#curation_table_igv").unbind("click");
        $("#curation_table_googlelink").unbind("click");
        $("#curation_table_alamut").unbind("click");
        $("#curation_table_panel").unbind("click");
        $("#curation_table_act").unbind("click");
        $("#curation_table_zygosity").unbind("click");


        //change tooltips since we dont want to hack easygrid and do it directly:
        $('#refresh_curation_table').prop('title', 'Reset Filters');
        $('#search_curation_table').prop('title', 'Apply Filters');


        //lock grid if needed
        if (${isFirstReviewed}) { //FFFFF5
            $('.ui-widget-content').css('background','none')
            $('.ui-widget-content').css('background-color','#EBEBEB') //EBEBEB
            $('.ui-widget-header').css('background','none')
            $('.ui-widget-header').css('background-color','#EBEBEB')
            $('.ui-state-default').css('background','none')
            $('.ui-state-default').css('background-color','#EBEBEB')
        }

        //lock grid if needed
        if (${isFinalReviewed}) {
            $('.ui-widget-content').css('background','none')
            $('.ui-widget-content').css('background-color','white')
            $('.ui-widget-header').css('background','none')
            $('.ui-widget-header').css('background-color','white')
            $('.ui-state-default').css('background','none')
            $('.ui-state-default').css('background-color','white')
        }

        if ( (${isFinalReviewed}) ) {
            disableGridButtons()
        }
        /* Uncomment if you want to distinguish between userfilter and reviewedsamplefilter in the user interface */
        if (  ${isFinalReviewed} ) {
            $('#appliedfiltertype').text("reviewed sample ")
        } else {
            $('#appliedfiltertype').text("user-created ")
        }
});


    var availableTags = <g:allTags/>;
    var tagModule = PathOS.tags.buildModule({
        object: 'seqvariant',
        tags: [],
        availableTags: availableTags
    });



// Author: David Ma
// Adding IGV.js
// 4th of May 2016

var seqrun = "${seqSample?.seqrun}"
var sample = "${seqSample?.sampleName}"
var panel = "${ seqSample?.panel }"

// This is the default dataUrl. Replace it with something better using ajax.
var dataUrl = "${ UrlLink.dataUrl (
        seqSample?.seqrun.toString(),
        seqSample?.sampleName,
        ''
    )
}";



var igvDiv = document.getElementById("igvDiv");

function addToIGV(sample, dataUrl, id){
    PathOS.igv.addBAM(sample, dataUrl);
    $("#relationLink-"+id).remove();
}

    $("#footer-message h1").text("This footer is used to view variants, click on one to get started.");


    var current_id = false,
        locus = "";

    var downsample = null;
    $("#curation_table").on('click', function(){
        setTimeout(function(){
            if (current_id != $(".ui-row-ltr.ui-state-highlight").attr('id')) {
                current_id = $(".ui-row-ltr.ui-state-highlight").attr('id');
                PathOS.tags.update_object(current_id);
                $("#footer-message").remove();

                var readDepth = $(".ui-row-ltr.ui-state-highlight [aria-describedby='curation_table_readDepth']").html().replace(/,/g,"").trim();

                var locus = $(".ui-row-ltr.ui-state-highlight [aria-describedby='curation_table_variant'] a").html().trim();
                var chr = locus.split("g.")[0];
                var regex = /(\d+)/;
                var pos = parseInt(regex.exec(locus.split("g.")[1]));
                var start = pos - 50;
                var stop = pos + 50;
                var location = chr+start+"-"+stop;

                var igvAutoLoad = "ask";
                if(PathOS.modules.settings[PathOS.user] && PathOS.modules.settings[PathOS.user].svlistIGV) {
                    igvAutoLoad = PathOS.modules.settings[PathOS.user].svlistIGV;
                }


                if (panel.indexOf("MRD") === 0) {
                    var message = d3.select("#pathos-footer").insert("div", "#igvDiv").attr("id", "footer-message");
                    message.append("h1").text("Warning, this is an MRD sample, you should open this using Desktop IGV because in-browser IGV will probably crash your browser.");

                    message.append("a").text("View using Desktop IGV").attr("href", "/PathOS/seqVariant/igvAction?id="+current_id);

                    message.append("span").text(" - ");

                    message.append("a").text("Downsample and open with in-browser IGV").attr("href", "#").on("click", function(){
                        $("#footer-message").remove();
                        downsample = true;
                        PathOS.igv.init(igvDiv, dataUrl, sample, panel, 2500);
                        PathOS.igv.loaded = false;
                        PathOS.igv.search(location);
                    });
                } else if ( downsample === false || igvAutoLoad == "auto" ) {
                    PathOS.igv.init(igvDiv, dataUrl, sample, panel, 50000);
                    PathOS.igv.search(location);
                } else if ( downsample === true || igvAutoLoad == "downsample" ) {
                    PathOS.igv.init(igvDiv, dataUrl, sample, panel, 2500);
                    PathOS.igv.search(location);
                } else {
                    var message = d3.select("#pathos-footer").insert("div", "#igvDiv").attr("id", "footer-message");

                    message.append("h1").attr("id", "main-button").append("a").text("Launch In-browser IGV").attr("href", "#").on("click", function(){
                        $("#footer-message").remove();
                        downsample = false;
                        PathOS.igv.init(igvDiv, dataUrl, sample, panel, 50000);
                        PathOS.igv.loaded = false;
                        PathOS.igv.search(location);
                    });

                    message.append('h1').attr("id", "igv-message").text("If your browser runs slowly, you might want to try downsampling or using Desktop IGV")

                    message.append("a").text("Launch In-browser IGV with downsampling").attr("href", "#").on("click", function(){
                        $("#footer-message").remove();
                        downsample = true;
                        PathOS.igv.init(igvDiv, dataUrl, sample, panel, 2500);
                        PathOS.igv.search(location);
                    });

                    message.append("a").text("View using Desktop IGV").attr("href", "/PathOS/seqVariant/igvAction?id="+current_id);
                }
            }
        }, 200);
    });
</r:script>
${seqSample.clinContext}
</html>








