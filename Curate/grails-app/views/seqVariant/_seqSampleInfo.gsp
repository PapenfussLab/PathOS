<%@ page import="org.petermac.pathos.curate.SeqSample; org.petermac.pathos.pipeline.UrlLink" %>

<section id="svListInfo">
    <div class="">
        <h1 style="margin-left:50px;"><g:message code="default.list.label" args="[entityName]"/></h1>

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

                    <li class="fieldcontain">
                        <span id="relation-label" class="property-label"><g:message code="seqSample.relations.label" /></span>
                        <span class="property-value" aria-labelledby="relation-label">
                            <ul id="seqrunRelations">
                                <g:render template="relationships" />
                            </ul>
                        </span>
                    </li>

                    <li class="fieldcontain">
                        <span id="sampleType-label" class="property-label"><g:message code="seqSample.sampleType.label" /></span>

                        <span class="property-value" aria-labelledby="panel-label">
                            <g:if test="${seqSample?.sampleType}"><g:fieldValue bean="${seqSample}" field="sampleType"/>
                            </g:if><g:else>Generic (no sample type)
                            </g:else>
                        </span>
                    </li>

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

                    <g:if test="${seqSample?.panel}">
                        <li class="fieldcontain">
                            <span id="panel-label" class="property-label"><g:message code="seqSample.panel.label" /></span>
                            <span class="property-value" aria-labelledby="panel-label"><g:fieldValue bean="${seqSample}" field="panel"/></span>
                        </li>

                        %{-- Gene Mask should be shown to everyone, not just unmaskers --}%
                        %{-- Only devs and unmaskers should be able to edit the mask --}%
                        <li class="fieldcontain">
                            <span id="unmasker-label" class="property-label">Gene Mask</span>
                            <span class="property-value" aria-labelledby="unmasker-label">
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
                            </span>

                        </li>
                    </g:if>

                    <g:if test="${seqSample?.dnaconc}">
                        <li class="fieldcontain">
                            <span id="dnaconc-label" class="property-label"><g:message code="seqSample.dnaconc.label" /></span>
                            <span class="property-value" aria-labelledby="dnaconc-label"><g:fieldValue bean="${seqSample}" field="dnaconc"/></span>
                        </li>
                    </g:if>

                    <li class="fieldcontain">
                        <span id="experiment-label" class="property-label">Experiment</span>
                        <span class="property-value" aria-labelledby="experiment-label">${seqSample.seqrun?.experiment ?: "Not labeled"}</span>
                    </li>

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
                        <span class="property-value">
                            <a href='http://gaffa.unix.petermac.org.au/?file=/pathology/NGS/Samples/Gaffa1/targeted_bins_only/${seqSample?.panel?.manifest}_${seqSample?.seqrun?.seqrun}.targeted_bins_only.tsv' target="_blank">Old Gaffa1</a>
                            <br>
                            <a href='${UrlLink.gaffaUrl( seqSample?.seqrun?.seqrun, seqSample?.sampleName, seqSample.panel.manifest )}' target="_blank">GAFFA</a>
                        </span>
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

<r:style>
    #reviewSeqSample form {
        padding: 2px;
        margin-bottom: 3px;
        %{--border: 1px dashed grey;--}%
        border-radius: 2px;
    }
</r:style>
                    <li class="fieldcontain">
                        <span id="review-label" class="property-label">Review SeqSample</span>
                        <span id="reviewSeqSample" class="property-value" aria-labelledby="review-label">
                            <g:if test="${seqSample?.firstReviewBy && seqSample?.firstReviewedDate}">
                                <g:render template="reviewExists" model="[ review: 'First', user: seqSample.firstReviewBy, date: seqSample.firstReviewedDate ]"/>
                                <g:if test="${seqSample?.secondReviewBy}">
                                    <g:render template="reviewExists" model="[ review: 'Second', user: seqSample.secondReviewBy, date: seqSample.secondReviewedDate ]"/>
                                </g:if>
                                <g:elseif test="${!seqSample?.finalReviewBy}">
                                    <g:render template="reviewAuthorisation" model="[ review: 'Second' ]"/>
                                </g:elseif>
                                <g:if test="${seqSample?.finalReviewBy}">
                                    <g:render template="reviewExists" model="[ review: 'Final', user: seqSample.finalReviewBy, date: seqSample.finalReviewedDate ]"/>
                                </g:if>
                                <g:else>
                                    <g:render template="reviewAuthorisation" model="[ review: 'Final' ]"/>
                                </g:else>
                            </g:if>
                            <g:else>
                                <g:render template="reviewAuthorisation" model="[ review: 'First' ]"/>
                            </g:else>
                        </span>
                    </li>

                    <li class="fieldcontain" id="showTags">
                        <span class="property-label">Tags of SeqSample:<br>${seqSample?.toString()}</span>
                        <div id="showTagBox" class="outlined-box tags_field property-value">
                            <textarea id="showTagTextArea" placeholder="Enter Tags Here" class="ui-autocomplete-input" autocomplete="off"></textarea>
                        </div>
                    </li>
                </ol>
            </div>
            <div class="col-sm-6">
                <ol class="property-list seqSample"  id="pat_sample_hollyinfo">
                    <g:if test="${seqSample?.patSample?.hollyLastUpdated}">


                        <g:if test="${seqSample?.patSample?.pathologist}">
                            <li class="fieldcontain">
                                <span class="property-label">Pathologist Review</span>
                                <span class="property-value" aria-labelledby="sampleName-label">
                                    ${seqSample.patSample.pathologist}
                                </span>
                            </li>
                        </g:if>

                        <g:if test="${seqSample?.patSample?.pathComments}">
                            <li class="fieldcontain">
                                <span class="property-label">Pathologist Comments</span>
                                <span class="property-value" aria-labelledby="sampleName-label">
                                    ${seqSample.patSample.pathComments}
                                </span>
                            </li>
                        </g:if>


                        <g:if test="${seqSample?.patSample?.repMorphology}">

                            <li class="fieldcontain">
                                <span class="property-label">Report Morphology</span>
                                <span class="property-value" aria-labelledby="sampleName-label">
                                    ${seqSample.patSample.repMorphology}
                                </span>
                            </li>
                        </g:if>

                        <g:if test="${seqSample?.patSample?.pathMorphology}">
                            <li class="fieldcontain">
                                <span class="property-label">Notes</span>
                                <span class="property-value" aria-labelledby="sampleName-label">
                                    ${seqSample.patSample.pathMorphology}
                                </span>
                            </li>
                        </g:if>


                        <g:if test="${seqSample?.patSample?.retSite}">
                            <li class="fieldcontain">
                                <span class="property-label">Tissue Site</span>
                                <span class="property-value" aria-labelledby="sampleName-label">
                                    ${seqSample.patSample.retSite}
                                </span>
                            </li>
                        </g:if>


                        <g:if test="${seqSample?.patSample?.tumourPct}">
                            <li class="fieldcontain">
                                <span class="property-label">Tumour %</span>
                                <span class="property-value" aria-labelledby="sampleName-label">
                                    ${seqSample.patSample.tumourPct}
                                </span>
                            </li>
                        </g:if>


                    </g:if>

                    <li class="fieldcontain">
                        <span id="clinContext-label" class="property-label">Clinical Context</span>
                        <span style="padding-bottom: 10px" class="property-value" aria-labelledby="sampleName-label">
                            <g:form action="updateClinContext" id="${seqSample?.id}" name="updateClinContextForm">

                                <g:select id="clinContext" name="clinContext" from="${clinContextList.sort{a,b -> a.code <=> b.code}}"  noSelection="${['None':'None']}" optionKey="code" value="${seqSample.clinContext? seqSample.clinContext.code : 'None'}" />
                                <g:submitButton id="updateClinContext" name="updateClinContext" disabled="true" value="Save Change" style="margin-top:4px;"/>
                                <g:hiddenField name="seqsampleid" id="seqsampleid" value="${seqSample?.id}"/>
                            </g:form>
                            <span id="get-a-curator">To change the Clinical Context, please contact a Curator.</span>
                        </span>

                    </li>
                </ol>
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
                        url: "<g:context/>/tag/removeLink?" + $.param(params),
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
                        url: "<g:context/>/tag/removeLink?" + $.param(params),
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
            d3.selectAll("#svListInfo, div[class='nav'][role='navigation']").style("margin-left", offset+"px");
        });
    });


</r:script>

<r:style>
%{-- PATHOS-2366 stuff for the scrollbar--}%
%{-- DKGM 10-July-2017--}%
#svListInfo, div[class='nav'][role='navigation'] {
    width: 100%;
}
</r:style>








