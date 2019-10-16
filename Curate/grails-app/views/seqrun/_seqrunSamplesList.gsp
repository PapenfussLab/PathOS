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

table#seqrunSamplesTable thead th,
table#seqrunSamplesTable thead td {
  padding: 8px 5px;
  padding-right: 15px;
  border-bottom: 1px solid #111111;
}
</r:style>
<g:if test="${seqrunInstance?.seqSamples}">
<section id="seqrunSamplesList">
    <div class="outlined-box">
        <table id="seqrunSamplesTable" class="infoTable display dataTable">
            <thead>
            <tr>
                <th><p>Sample</p><br>Name</th>
                <th>Readiness</th>
                <th>QC</th>
                <th>Variants</th>
                <th>Curated</th>
                <th>Review</th>
                <th><p>Filter</p><br>Passed</th>
                <th>Reported</th>
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


    const seqrun = "${seqrunInstance.seqrun}";
    const seqrunId = ${seqrunInstance.id};
    $(document).ready(function() {
        d3.selectAll(".readinessTd").each(function(d, i){

            const readinessIcons = {
                seqVariants: {
                    name: "Variants",
                    letter: "Var",
                    icon: "tree",
                    message: "Variants loaded",
                    error: "Variants missing"
                },
                patSample: {
                    name: "Patient Sample",
                    letter: "Pat",
                    icon: "user-o",
                    message: "Patient: ", // put a link to the pat sample if successful
                    error: "Patient missing"
                },
                billingCode: {
                    name: "Billing Code",
                    letter: "Bill",
                    icon: "dollar",
                    message: "Billing: ", // link
                    error: "Billing Code missing"
                },
                bam: {
                    name: "BAM file",
                    letter: "BAM",
                    icon: "area-chart",
                    message: "BAM file found",
                    error: "BAM file missing"
                },
                vcf: {
                    name: "VCF file",
                    letter: "VCF",
                    icon: "file-text-o",
                    message: "VCF file found",
                    error: "VCF file missing"
                },
                alignStats: {
                    name: "Alignment Statistics",
                    letter: "Stats",
                    icon: "heartbeat",
                    message: "Align Stats loaded",
                    error: "Align Stats not loaded"
                }
            <g:if test="${seqrunInstance.panelList?.contains('Pathology_hyb')}">
                , multiQC: {
                    name: "MultiQC",
                    letter: "QC",
                    icon: "bar-chart",
                    message: "MultiQC files found",
                    error: "MultiQC files missing"
                },
                cnv: {
                    name: "CNV file",
                    letter: "CNV",
                    icon: "server",
                    message: "CNV file found",
                    error: "CNV file missing"
                }
            </g:if>
        };

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

// CNV
            readinessIcons.cnv.url = dataUrl+"/CNV/"+sampleName+"_gaffa_pathos.tsv";
    </g:if>


    // Fetch data from server
                $.ajax({
                    url: "<g:context/>/SeqSample/sampleBasics?id="+ss,
                success: function(d){
                    try {

                        Object.keys(readinessIcons).forEach(function(icon){
                            const params = readinessIcons[icon];
                            params.data = d;

                            const link = td.datum(params).append("span").classed("halt", true);

                            // link.append("i").attrs({
                            //     class: "fa fa-"+params.icon,
                            //     title: params.name,
                            //     'aria-hidden': 'true'
                            // });

                            link.append("letter").text(params.letter);

                            const p = link.append("span").classed("name", true).text(params.name);
                            // link.append("br");
                            // const p = link.append("p").text(params.name +" status not loaded yet");


                            if (typeof d[icon] !== 'undefined') {
                                link.classed("halt", false);
                                if(d[icon] === "false" || d[icon] === 0 || d[icon] == false) {
                                    link.classed("fail", true);
                                    p.text(params.error);
                                } else {
                                    link.classed("pass", true);
                                    p.text(params.message);
                                }
                            } else if (readinessIcons[icon].url) {
                                PathOS.urlExists(readinessIcons[icon].url, function(exists) {
                                    link.classed("halt", false);
                                    if(exists) {
                                        link.classed("pass", true);
                                        p.text(params.message);
                                    } else {
                                        link.classed("fail", true);
                                        p.text(params.error);
                                    }
                                });
                            }

                            switch(icon) {
                                case 'patSample':
                                    var patientLink = link.append("span").classed("patientLink", true);
                                    patientLink.append("a")
                                        .text(d.urn)
                                        .attrs({
                                            href: PathOS.application + "/Patient/find?urn=" + encodeURIComponent(d.urn),
                                            target: "_blank"
                                        }).on("click", function(){
                                            event.stopPropagation();
                                        });
                                    break;
                                case 'billingCode':
                                    if(d.billingCodes.length > 0) {
                                        var codes = link.append("span").classed("billingCodes", true);

                                        d.billingCodes.forEach(function(code) {
                                            codes.append("a")
                                            .text(code)
                                            .attrs({
                                                href: PathOS.application + "/LabAssay/find?billingCode=" + code,
                                                target: "_blank"
                                            }).on("click", function(){
                                                event.stopPropagation();
                                            });
                                        });
                                    }
                                    break;
                                default:
                            }



                            link.append("br");

                        });


                        td.on("click", function(d){
                            // console.log("Hey you clicked the thing lol",d);
                            $(this).toggleClass("expand");
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