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
                <th>Readiness</th>
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
                    <td class="readinessTd" data-ss="${ss.id}" data-sn="${ss.sampleName}"></td>
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


    const readinessIcons = {
        seqVariants: {
            name: "Sequenced Variants",
            letter: "S",
            icon: "tree",
            message: "Sequenced Variants are loaded",
            error: "No Sequenced Variants found"
        },
        patSample: {
            name: "Patient Sample",
            letter: "P",
            icon: "user-o",
            message: "Valid Patient in PathOS",
            error: "No Patient Sample found"
        },
        billingCode: {
            name: "Billing Code",
            letter: "B",
            icon: "dollar",
            message: "Valid Billing Code in PathOS",
            error: "No Billing Code found"
        },
        bam: {
            name: "BAM - Binary Alignment Map",
            letter: "B",
            icon: "area-chart",
            message: "BAM has been found",
            error: "No BAM on file"
        },
        vcf: {
            name: "VCF - Variant Call Format",
            letter: "V",
            icon: "file-text-o",
            message: "VCF found",
            error: "No VCF found"
        },
        alignStats: {
            name: "Alignment Statistics",
            letter: "A",
            icon: "heartbeat",
            message: "Align Stats loaded",
            error: "Align Stats could not be found"
        }
<g:if test="${seqrunInstance.panelList?.contains('Pathology_hyb')}">
        , multiQC: {
            name: "MultiQC",
            letter: "M",
            icon: "bar-chart",
            message: "MultiQC files found",
            error: "No MultiQC files found"
        },
        cnvBam: {
            name: "CNV BAM",
            letter: "C",
            icon: "server",
            message: "CNV BAM found",
            error: "No CNV BAM found"
        }
</g:if>
    };




    const seqrun = "${seqrunInstance.seqrun}";
    const seqrunId = ${seqrunInstance.id};
    $(document).ready(function() {
        d3.selectAll(".readinessTd").each(function(d, i){
            const td = d3.select(this);
            const ss = td.attr('data-ss');
            const sampleName = td.attr('data-sn');
            const dataUrl = "${UrlLink.dataUrl()}"+seqrun+"/"+sampleName;

// Regular Bam
            const bamUrl = dataUrl + "/" + sampleName+".bam";
            readinessIcons.bam.url = bamUrl;

    <g:if test="${seqrunInstance.panelList?.contains('Pathology_hyb')}">
// MultiQC
            readinessIcons.multiQC.url = dataUrl + "/QC/MultiQC/"+sampleName+".variants_stats.tsv";

// CNV bam
            readinessIcons.cnvBam.url = dataUrl+"/CNV/bam/"+sampleName+".bam";
    </g:if>


    // Fetch data from server
                $.ajax({
                    url: "<g:context/>/SeqSample/sampleBasics?id="+ss,
                success: function(d){
                    try {

                        Object.keys(readinessIcons).forEach(function(icon){
                            console.log(icon, d[icon]);
                            const params = readinessIcons[icon];
                            const link = td.append("a").classed("halt", true);
                            link.append("i").attrs({
                                class: "fa fa-"+params.icon,
                                title: params.name,
                                'aria-hidden': 'true'
                            });
                            if (typeof d[icon] !== 'undefined') {
                                link.classed("halt", false);
                                if(d[icon] === "false" || d[icon] === 0 ) {
                                    link.classed("fail", true);
                                } else {
                                    link.classed("pass", true);
                                }
                            } else if (readinessIcons[icon].url) {
                                PathOS.urlExists(readinessIcons[icon].url, function(exists) {
                                    link.classed("halt", false);
                                    if(exists) {
                                        link.classed("pass", true);
                                    } else {
                                        link.classed("fail", true);
                                    }
                                });
                            }
                        });

                        console.log("Data", d);


                        td.on("click", function(d){
                            console.log("Hey you clicked the thing lol");
                        })

                    } catch (e) {
                        console.log("Error", e);
                    }

                }
            });




        });
    });























</r:script>
</g:if>