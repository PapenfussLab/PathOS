<%@ page import="org.petermac.pathos.curate.SeqSample; org.petermac.pathos.pipeline.UrlLink" %>
<r:style>
%{--To combat hotfix stuff--}%
#seqSampleInfo {
    margin-top: 5px;
}

#seqSampleInfo .property-value, #seqSampleInfo .property-label {
    float: inherit;
    display: table-cell;
    line-height: 1em;
}

#reviewSeqSample form {
    padding: 2px;
    margin-bottom: 3px;
}

#seqSampleInfo table.infoTable tbody tr td:first-child {
    vertical-align: top;
}

#seqSampleInfo table.infoTable tbody tr td:last-child {
    vertical-align: middle;
}

#svListInfo, div[class='nav'][role='navigation'] {
    width: 100%;
}

#seqSampleInfo .infoTable tr td:first-child {
    width: 1px;
}

</r:style>
<section id="seqSampleInfo">
    <div style="margin: 0 15px;">
        <div class="row">
            <div class="col-sm-6">
                <table class="infoTable" id="pat_sample_defaultinfo">
                    <tbody>

                    <tr class="fieldcontain">
                        <td id="sampleName-label" class="property-label"><b><g:message code="seqSample.sampleName.label" /></b></td>
                        <td class="property-value" aria-labelledby="sampleName-label">
                            ${seqSample.sampleName}
                        </td>
                    </tr>

                    <tr class="fieldcontain">
                        <td id="seqrun-label" class="property-label"><b><g:message code="seqSample.seqrun.label"/></b></td>
                        <td class="property-value" aria-labelledby="seqrun-label">
                            <g:link controller="seqrun" action="show" id="${seqSample?.seqrun?.id}">${seqSample?.seqrun?.encodeAsHTML()}</g:link>
                        </td>
                    </tr>

                    <g:if test="${seqSample?.panel}">
                        <tr class="fieldcontain">
                            <td id="panel-label" class="property-label"><g:message code="seqSample.panel.label" /></td>
                            <td class="property-value" aria-labelledby="panel-label"><g:fieldValue bean="${seqSample}" field="panel"/></td>
                        </tr>
                    </g:if>

                    <tr class="fieldcontain">
                        <td id="relation-label" class="property-label"><g:message code="seqSample.relations.label" /></td>
                        <td class="property-value" aria-labelledby="relation-label">
                            <ul id="seqrunRelations">
                                <g:render template="relationships" />
                            </ul>
                        </td>
                    </tr>

                    <g:if test="${seqSample?.dnaconc}">
                        <tr class="fieldcontain">
                            <td id="dnaconc-label" class="property-label"><g:message code="seqSample.dnaconc.label" /></td>
                            <td class="property-value" aria-labelledby="dnaconc-label"><g:fieldValue bean="${seqSample}" field="dnaconc"/></td>
                        </tr>
                    </g:if>

                    <g:if test="${seqSample?.analysis}">
                        <tr class="fieldcontain">
                            <td id="analysis-label" class="property-label"><g:message code="seqSample.analysis.label" /></td>
                            <td class="property-value" aria-labelledby="analysis-label"><g:fieldValue bean="${seqSample}" field="analysis"/></td>
                        </tr>
                    </g:if>

                    <g:if test="${seqSample?.userName}">
                        <tr class="fieldcontain">
                            <td id="userName-label" class="property-label"><g:message code="seqSample.userName.label" /></td>
                            <td class="property-value" aria-labelledby="userName-label">
                                <a href="mailto:${seqSample.userEmail}">${seqSample.userName}</a>
                            </td>
                        </tr>
                    </g:if>

                    <tr class="fieldcontain">
                        <td class="property-label">CNV</td>
                        <td class="property-value">
                            <a href='http://gaffa.unix.petermac.org.au/?file=/pathology/NGS/Samples/Gaffa1/targeted_bins_only/${seqSample?.panel?.manifest}_${seqSample?.seqrun?.seqrun}.targeted_bins_only.tsv' target="_blank">Old Gaffa1</a> - <a href='${UrlLink.gaffaUrl( seqSample?.seqrun?.seqrun, seqSample?.sampleName, seqSample.panel.manifest )}' target="_blank">GAFFA</a>
                        </td>
                    </tr>

                    </tbody>
                </table>
            </div>
            <div class="col-sm-6">
                <table class="infoTable "  id="pat_sample_hollyinfo">
                    <tbody>

                    <g:if test="${seqSample?.patSample}">
                        <tr class="fieldcontain">
                            <td id="sample-label" class="property-label"><g:message code="seqSample.patSample.label" /></td>
                            <td class="property-value" aria-labelledby="sample-label">
                                <g:link controller="patSample" action="show" id="${seqSample?.patSample?.id}">
                                    ${seqSample?.patSample?.encodeAsHTML()}
                                </g:link>
                                Pat Assays: ${seqSample.patSample?.patAssays?.collect { it.testName }}
                            </td>
                        </tr>
                    </g:if>

                    <tr class="fieldcontain">
                        <td id="experiment-label" class="property-label">Experiment</td>
                        <td class="property-value" aria-labelledby="experiment-label">${seqSample.seqrun?.experiment ?: "Not labeled"}</td>
                    </tr>


                    <g:if test="${seqSample?.patSample?.hollyLastUpdated}">
                        <g:if test="${seqSample?.patSample?.pathologist}">
                            <tr class="fieldcontain">
                                <td class="property-label">Pathologist Review</td>
                                <td class="property-value" aria-labelledby="sampleName-label">
                                    ${seqSample.patSample.pathologist}
                                </td>
                            </tr>
                        </g:if>

                        <g:if test="${seqSample?.patSample?.pathComments}">
                            <tr class="fieldcontain">
                                <td class="property-label">Pathologist Comments</td>
                                <td class="property-value" aria-labelledby="sampleName-label">
                                    ${seqSample.patSample.pathComments}
                                </td>
                            </tr>
                        </g:if>

                        <g:if test="${seqSample?.patSample?.repMorphology}">

                            <tr class="fieldcontain">
                                <td class="property-label">Report Morphology</td>
                                <td class="property-value" aria-labelledby="sampleName-label">
                                    ${seqSample.patSample.repMorphology}
                                </td>
                            </tr>
                        </g:if>

                        <g:if test="${seqSample?.patSample?.pathMorphology}">
                            <tr class="fieldcontain">
                                <td class="property-label">Notes</td>
                                <td class="property-value" aria-labelledby="sampleName-label">
                                    ${seqSample.patSample.pathMorphology}
                                </td>
                            </tr>
                        </g:if>


                        <g:if test="${seqSample?.patSample?.retSite}">
                            <tr class="fieldcontain">
                                <td class="property-label">Tissue Site</td>
                                <td class="property-value" aria-labelledby="sampleName-label">
                                    ${seqSample.patSample.retSite}
                                </td>
                            </tr>
                        </g:if>


                        <g:if test="${seqSample?.patSample?.tumourPct}">
                            <tr class="fieldcontain">
                                <td class="property-label">Tumour %</td>
                                <td class="property-value" aria-labelledby="sampleName-label">
                                    ${seqSample.patSample.tumourPct}
                                </td>
                            </tr>
                        </g:if>

                    </g:if>

                    <g:if test="${seqSample?.panel}">
                    %{-- Gene Mask should be shown to everyone, not just unmaskers --}%
                    %{-- Only devs and unmaskers should be able to edit the mask --}%
                        <tr class="fieldcontain">
                            <td id="unmasker-label" class="property-label">Gene Mask</td>
                            <td class="property-value" aria-labelledby="unmasker-label">
                                <g:if test="${seqSample.defaultGeneMask() && seqSample.usingDefaultGeneMask()}">
                                    Default Gene Mask: ${seqSample.defaultGeneMask()}
                                    <br>
                                </g:if>
                                <g:elseif test="${seqSample.sampleGeneMask && seqSample.sampleGeneMask != ""}">
                                    Custom Gene Mask: ${seqSample.geneMask().join(", ")}
                                    <br>
                                </g:elseif>
                                <g:elseif test="${seqSample?.sampleGeneMask == ""}">
                                    Custom Gene Mask: Everything is unmasked
                                    <br>
                                </g:elseif>
                                <g:else>
                                    No Gene Mask
                                    <br>
                                </g:else>
                                Assay: "${seqSample.geneMaskAssayName()}"
                                <br>
                                <sec:ifAnyGranted roles="ROLE_UNMASKER,ROLE_DEV">
                                    <g:if test="${vcfExists}">
                                    <g:link controller="seqSample" id="${seqSample.id}" action="editGeneMask">Edit Gene Mask</g:link>
                                    </g:if><g:else>
                                        <a title="VCF not stored on fileshare, cannot edit Gene Mask" style="cursor:not-allowed; color:grey;" href="#none">Edit Gene Mask</a> <span style="color:red;">VCF not stored on fileshare, cannot edit Gene Mask</span>
                                    </g:else>
                                </sec:ifAnyGranted>
                            </td>
                        </tr>
                    </g:if>

                    <tr class="fieldcontain">
                        <td id="clinContext-label" class="property-label">Clinical Context</td>
                        <td class="property-value" aria-labelledby="sampleName-label">
                            <g:form action="updateClinContext" id="${seqSample?.id}" name="updateClinContextForm">

                                <g:select id="clinContext" name="clinContext" from="${clinContextList.sort{a,b -> a.code <=> b.code}}"  noSelection="${['None':'None']}" optionKey="code" value="${seqSample.clinContext? seqSample.clinContext.code : 'None'}" />
                                <g:submitButton id="updateClinContext" name="updateClinContext" disabled="true" value="Save Change" style="margin-top:4px;"/>
                                <g:hiddenField name="seqsampleid" id="seqsampleid" value="${seqSample?.id}"/>
                            </g:form>
                            <span id="get-a-curator">To change the Clinical Context, please contact a Curator.</span>
                        </td>
                    </tr>

                    %{--Authorised QC --}%
                    <tr class="fieldcontain">
                        <td id="authorisedQcFlag-label" class="property-label"><g:message code="seqSample.authorisedQcFlag.label" /></td>

                        <td class="property-value" aria-labelledby="authorisedQcFlag-label">
                            <g:link controller="seqSample"  action="showQC" id="${seqSample?.id}">
                                <g:if test="${seqSample?.authorisedQcFlag}">
                                    <g:qcPassFail authorised="${true}" passfailFlag="${seqSample?.passfailFlag}" />
                                </g:if>
                                <g:else>
                                    Set QC
                                </g:else>
                            </g:link>
                        </td>
                    </tr>

                    <tr class="fieldcontain">
                        <td id="review-label" class="property-label">Review SeqSample</td>
                        <td id="reviewSeqSample" class="property-value" aria-labelledby="review-label">
                            <g:if test="${seqSample?.firstReviewBy && seqSample?.firstReviewedDate}">
                                <g:render template="reviewExists" model="[ review: 'First', seqSample: seqSample, user: seqSample.firstReviewBy, date: seqSample.firstReviewedDate ]"/>
                                <g:if test="${seqSample?.secondReviewBy}">
                                    <g:render template="reviewExists" model="[ review: 'Second', seqSample: seqSample, user: seqSample.secondReviewBy, date: seqSample.secondReviewedDate ]"/>
                                </g:if>
                                <g:elseif test="${!seqSample?.finalReviewBy}">
                                    <g:render template="reviewAuthorisation" model="[ review: 'Second', seqSample: seqSample ]"/>
                                </g:elseif>
                                <g:if test="${seqSample?.finalReviewBy}">
                                    <g:render template="reviewExists" model="[ review: 'Final', seqSample: seqSample, user: seqSample.finalReviewBy, date: seqSample.finalReviewedDate ]"/>
                                </g:if>
                                <g:else>
                                    <g:render template="reviewAuthorisation" model="[ review: 'Final', seqSample: seqSample ]"/>
                                </g:else>
                            </g:if>
                            <g:else>
                                <g:render template="reviewAuthorisation" model="[ review: 'First', seqSample: seqSample ]"/>
                            </g:else>
                        </td>
                    </tr>

                    <tr class="fieldcontain" id="showTags">
                        <td class="property-label">Tags of SeqSample:<br>${seqSample?.toString()}</td>
                        <td>
                            <div id="showTagBox" class="outlined-box tags_field property-value">
                                <textarea id="showTagTextArea" placeholder="Enter Tags Here" class="ui-autocomplete-input" autocomplete="off"></textarea>
                            </div>
                        </td>
                    </tr>

                    </tbody>
                </table>
            </div>
        </div>
    </div>
</section>


<r:script>
var tags = ${seqSample?.tags as grails.converters.JSON},
    tagbox = d3.select("#showTagBox").on('click', function(){
        $('#showTagTextArea').focus();
    });

tags.sort(function(a,b){return a.id - b.id}).forEach(function(tag){
    PathOS.tags.drawTag(tagbox, tag);
});
$("#showTagTextArea").autocomplete({source:Object.keys(<g:allTags/>)});

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
                        url: PathOS.application + "/tag/removeLink?" + $.param(params),
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
                        url: PathOS.application + "/tag/removeLink?" + $.param(params),
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


// PATHOS-2366 stuff for the scrollbar
// DKGM 10-July-2017
$(document).ready(function(){
    $(window).scroll(function(){
        var offset = $(window).scrollLeft();
        d3.selectAll("#seqSampleInfo, div[class='nav'][role='navigation']").style("margin-left", offset+"px");
    });
});


</r:script>









