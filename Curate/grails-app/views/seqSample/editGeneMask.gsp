<%@ page import="org.petermac.pathos.curate.SeqSample; grails.converters.JSON" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Edit Gene Mask for ${seqSample.sampleName} on ${seqSample.seqrun}</title>

    <r:style>
        table tr td {
            line-height: 1em;
        }

        .genemask {
            width: 70%;
        }
        .genemask input {
            width: 100%;
        }
        .save_button input {
            padding: 8px;
            border-radius: 3px;
        }
        .save_button input:hover {
            background: lightgrey;
            border: 1px solid grey;
        }

        #editGeneMaskLoading {
            position: fixed;
            z-index: 100;
            width: 50%;
            margin: 100px 25%;
            text-align: center;
            display: none;
        }
        #editGeneMaskLoading.show {
            display: inherit;
        }

td {
    vertical-align: top;
    background: white;
}
tr:hover {
    background: white;
}
#gene_mask_input, .hwt-container {
    width: 100%;
    height: 150px;
}

#resultMessage {
    display: none;
    text-align: center;
    margin: 5px;
}
#resultMessage.show {
    display: inherit;
}

.successMessage {
    color:blue !important;
}

.failMessage {
    color:red;
}

    </r:style>

</head>

<body>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="sample" action="svlist" controller="seqVariant"
                    id="${seqSample.id}">SeqSample - ${seqSample.sampleName}</g:link></li>
    </ul>
</div>

<img id="editGeneMaskLoading" src="<g:context/>/dist/images/pathos_logo_animated.svg">

<div id="resultMessage"></div>



<section>
    <div class="container">
        <div class="row">
            <div class="col-xs-10 col-xs-offset-1 outlined-box">
                <h1>Edit Gene Mask - ${seqSample.seqrun} : ${seqSample.sampleName} </h1>
                <g:if test="${vcfExists}">
                <table>
                    <tr>
                        <td>Default Mask</td>
                        <td>${seqSample.defaultGeneMask().join(", ") ?: 'No Mask, all genes allowed'}</td>
                    </tr>
                    <tr>
                        <td>Custom Mask<g:if test="${seqSample?.sampleGeneMask == ""}"><p>Note: empty gene mask means all variants will be shown.</p></g:if></td>
                        <td class="genemask"><div><textarea id="gene_mask_input">${seqSample.sampleGeneMask || seqSample?.sampleGeneMask == ""? seqSample.geneMask().join(", ") : seqSample.defaultGeneMask().join(", ") }</textarea></div></td>
                    </tr>
                   <tr><td>Run in background</td><td>
                       <g:checkBox name="runbackground" value="${userparams?.runbackground}" />

                   </td>
                    <span>
                    <tr style="display:none;" id="useremailfield" class="useremailfield">   <td>Email </td><td>

                    <g:textField name="email" class="emailField"   value="${userparams?.email?:defaultEmail}" />
                </span>
                </td>
               <br/>
                   </tr>
                    <tr>
                        <td></td>
                        <g:if test="${!seqSample.firstReviewBy}">
                        <td class="save_button"><input id="save_button_input" type="button" value="Reload Variants into PathOS">
                        <br>
                        <p>Note: Reloading may take several minutes.</p>
                        </td>
                        </g:if><g:else><td><p>This sample has been reviewed, gene mask cannot be changed.</p></td></g:else>
                    </tr>
                </table>


                <g:if test="${!seqSample.relations.findAll { it.relation == "Replicate"} }">
                    <%-- <h4>No Replicates Found</h4> --%>
                </g:if>
                <g:else>
                    <h1>Replicates of SeqSample ${seqSample.sampleName} in this run:</h1>
                <table>
                    <tr>
                        <th>Seqrun</th>
                        <th>SeqSample</th>
                        <th>Relationship</th>
                        <th>Gene Mask</th>
                    </tr>
                    <g:each in="${seqSample.relations.findAll { it.relation == "Replicate"} }" var="relation">
                        <g:each in="${relation.samples()}" var="replicate">
                            <g:if test="${seqSample != replicate && replicate.seqrun.seqrun == seqSample.seqrun.seqrun}">
                    <tr>
                        <td><a href="<g:context/>/seqrun/show?id=${replicate.seqrun.id}">${replicate.seqrun}</a></td>
                        <td><a href="<g:context/>/seqVariant/svlist/${replicate.id}">${replicate.sampleName}</a></td>
                        <td>${relation.relation}</td>
                        <td><a class="replicate_gene_mask" href="<g:context/>/seqSample/editGeneMask/${replicate.id}">${replicate?.geneMask()?.join(", ") ?: 'No Gene Mask'}</a></td>
                    </tr>
                            </g:if>
                        </g:each>
                    </g:each>
                </table>
                </g:else>
                </g:if><g:else>
                <p>No VCF on the fileshare exists for this sample, cannot alter gene mask. <br/><br/>Gene masks for samples stemming from uploaded VCF files cannot be edited since the
                        uploaded VCF files are not permanantly stored; to alter a gene mask for an uploaded VCF, delete the sample and re-upload. </p>
            </g:else>

            </div>
        </div>
    </div>
 </section>
<r:script>
$(function(){
    <g:if test="${!seqSample.firstReviewBy}">
    var confirmMessage = "This will re-run the pipeline on this sample ${seqSample.relations.findAll { it.relation == "Replicate"} ?"and its replicates, ":"- "} proceed?"
    d3.select("#save_button_input").on('click', function(){
        if(confirm(confirmMessage)) {
            save();
        }
    });
    d3.select("#gene_mask_input").on('keydown', function(){
        if(d3.event && d3.event.key == "Enter") {
            if(confirm(confirmMessage)) {
                save();
            }
        }
    });

    $("#gene_mask_input").highlightWithinTextarea(function() {
        return /\b(${genes.join("|")})\b/ig;
    });
    </g:if>
});



$('#runbackground').change(function() {
        if($(this).is(":checked")) {
               $('#useremailfield').css('display','');
        } else {
               //hide
               $('#useremailfield').css('display','none');
        }
});

function save() {
    $("#editGeneMaskLoading").addClass("show");


    var package = {
        id: ${seqSample.id},
        version: ${seqSample.version},
        geneMask: $("#gene_mask_input").val(),
        runbackground: $("#runbackground").is(':checked'),
        email: $("#email").val()
    };
    console.log(package);

	$.ajax({
		type: "POST",
        url: "<g:context/>/SeqSample/updateGeneMask?id=${seqSample.id}",
		complete: function(d){
            $("#editGeneMaskLoading").removeClass("show");
            console.log("ajax returned with:", d);
            d3.select("#resultMessage")
                .html("")
                .attr("class", "");

            var geneMask = package.geneMask;
            try {
                geneMask = geneMask.split(",").map((d) => d.trim().toUpperCase()).join(", ");
            } catch (e) {
                console.error(e);
            }

            if(d.responseJSON.success) {
                PathOS.notes.add(d.responseJSON.success);
                $("#gene_mask_input").text(geneMask);
                $(".replicate_gene_mask").text(geneMask);
            }

            if(d.responseJSON.messages) {
                d.responseJSON.messages.forEach(function(message){
                    PathOS.notes.add(message);
                    $("#resultMessage").addClass("show");
                    d3.select("#resultMessage").append("p").text(message);
                    if(message.indexOf("rerun failed") !== -1) {
                        $("#resultMessage").addClass("failMessage")
                    }
                    if(message.indexOf("rerun success") !== -1) {
                        $("#resultMessage").addClass("successMessage")
                    }
                });
            }

            if(d.responseJSON.error) {
                PathOS.notes.addError(d.responseJSON.error);
                if(confirm("Error, please refresh page")) {
                    window.location.reload();
                }
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










