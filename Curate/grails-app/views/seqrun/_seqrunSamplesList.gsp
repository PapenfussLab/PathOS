<%@ page import="org.petermac.pathos.pipeline.UrlLink; org.petermac.pathos.curate.SeqVariant" %>


<tooltip:resources/>
<r:require module="datatables"/>

<r:style>
.reviewTd span {
    color: black;
    %{--background-color: #EBEBEB;--}%
    padding: 0 10px;
    text-decoration: none;
}

#seqrunSamplesTable_wrapper #seqrunSamplesTable tbody tr td:first-child {
    text-align: left;
    padding-left: 25px;
}

</r:style>
<g:if test="${seqrunInstance?.seqSamples}">
<section id="seqrunSamplesList">
    <div class="outlined-box">
        <table id="seqrunSamplesTable" class="infoTable display dataTable">
            <thead>
            <tr>
                <th>Seq. Sample</th>
                <th>QC</th>
                <th>Raw Variants</th>
                <th>Curated</th>
                <th>Review</th>
                <th>Filter Passed</th>
                <th>Reportable</th>
                <th>IGV.js</th>
                <th>IGV</th>
                <th>Panel</th>
                <th>Analysis</th>
                <th>User</th>
            </tr>
            </thead>

            <g:each in="${seqrunInstance.seqSamples.sort{a,b -> a.sampleName <=> b.sampleName}}" var="ss">
                <tr>
                    <td>
                        <g:link controller="seqVariant" action="svlist" id="${ss?.id}">${ss?.encodeAsHTML()}</g:link>
                    </td>
                    <td>
                        <g:link controller="seqSample"  action="showQC" id="${ss?.id}">
                            <g:if test="${ss.authorisedQcFlag}">
                                <g:qcPassFail authorised="${true}" passfailFlag="${ss.passfailFlag}" />
                            </g:if>
                            <g:else>
                                Set QC
                            </g:else>
                        </g:link></td>
                    <td>${SeqVariant.countBySeqSample(ss)}</td>
                    <td>${SeqVariant.executeQuery("select count(*) from SeqVariant sv, CurVariant cv where cv.grpVariant.accession=sv.hgvsg and sv.seqSample=:ss ", [ss: ss])[0]}</td>
                    <td class="reviewTd">
                        <g:if test="${ss.finalReviewBy}">
                            <span>Final</span>
                        </g:if>
                        <g:elseif test="${ss.secondReviewBy}">
                            <span>Second</span>
                        </g:elseif>
                        <g:elseif test="${ss.firstReviewBy}">
                            <span>First</span>
                        </g:elseif>
                        <g:else>
                            <span>&nbsp;</span>
                        </g:else>
                    </td>
                    <td>${SeqVariant.countBySeqSampleAndFilterFlag(ss, "pass")}</td>
                    <td>${SeqVariant.countBySeqSampleAndReportable(ss, true)}</td>
                    <td id="igv-open-${ss.sampleName}">
                        <tooltip:tip code="Open this sample with the in-browser IGV">
                            <a href="#none" onclick='launchIGV({
                                seqrun: "${ss.seqrun.seqrun}",
                                sample: "${ss.sampleName}",
                                panel:  "${ss.panel}",
                                dataUrl: "${ UrlLink.dataUrl (ss.seqrun.seqrun, ss.sampleName, '')}"
                            })'>View Sample</a>
                        </tooltip:tip>
                    </td>
                    <td>
                        <tooltip:tip code="seqrun.merge.tip">
                            <a href="http://localhost:60151/load?file=${org.petermac.pathos.pipeline.UrlLink.dataUrl(ss.seqrun.seqrun,ss.sampleName,ss.sampleName+".vcf")},${org.petermac.pathos.pipeline.UrlLink.dataUrl(ss.seqrun.seqrun,ss.sampleName,ss.sampleName+".bam")}&merge=true">Merge</a>
                        </tooltip:tip>
                    </td>
                    <td>${ss.panel}</td>
                    <td>${ss.analysis}</td>
                    <td>${ss.userName}</td>
                </tr>
            </g:each>
        </table>
    </div>
</section>





<r:script>
    //    Datatables stuff

    var table = $('#seqrunSamplesTable').DataTable({
        paging: false
    });


</r:script>
</g:if>