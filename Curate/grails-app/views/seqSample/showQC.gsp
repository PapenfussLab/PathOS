<%@ page import="org.petermac.pathos.pipeline.UrlLink; org.petermac.pathos.curate.StatsService; org.petermac.pathos.curate.AlignStats; org.petermac.pathos.curate.SeqSample" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'seqSample.label', default: 'SeqSample QC')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-seqSample" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="list">List SeqSample</g:link></li>
    </ul>
</div>

<div id="show-seqSample" class="content scaffold-show" role="main">
    <h1>${entityName}</h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list seqSample">

        <g:if test="${seqSampleInstance?.seqrun}">
            <li class="fieldcontain">
                <span id="seqrun-label" class="property-label"><g:message code="seqSample.seqrun.label"
                                                                          default="Seqrun"/></span>

                <span class="property-value" aria-labelledby="seqrun-label"><g:link controller="seqrun" action="show"
                                                                                    id="${seqSampleInstance?.seqrun?.id}">${seqSampleInstance?.seqrun?.encodeAsHTML()}</g:link></span>

            </li>
        </g:if>

        <g:if test="${seqSampleInstance?.sampleName}">
            <li class="fieldcontain">
                <span id="sampleName-label" class="property-label">
                    <g:message code="seqSample.sampleName.label" default="Sequenced Sample"/>
                </span>
                <span class="property-value" aria-labelledby="sampleName-label">
                    <g:link controller="seqVariant" action="svlist" id="${seqSampleInstance?.id}">
                        ${seqSampleInstance?.sampleName?.encodeAsHTML()}
                    </g:link>
                </span>
            </li>
        </g:if>

        <g:if test="${seqSampleInstance?.patSample}">
            <li class="fieldcontain">
                <span id="sample-label" class="property-label"><g:message code="seqSample.patSample.label"
                                                                          default="Patient Sample"/></span>

                <span class="property-value" aria-labelledby="sample-label"><g:link controller="patSample" action="show"
                                                                                    id="${seqSampleInstance?.patSample?.id}">${seqSampleInstance?.patSample?.encodeAsHTML()}</g:link> PatAssays: ${seqSampleInstance.patSample?.patAssays?.collect { it.testName }}</span>

            </li>
        </g:if>

        <g:if test="${seqSampleInstance?.panel}">
            <li class="fieldcontain">
                <span id="panel-label" class="property-label"><g:message code="seqSample.panel.label"
                                                                         default="Panel"/></span>

                <span class="property-value" aria-labelledby="panel-label"><g:link controller="panel" action="show"
                                                                                   id="${seqSampleInstance?.panel?.id}">${seqSampleInstance?.panel?.encodeAsHTML()}</g:link></span>

            </li>
        </g:if>

        <g:if test="${seqSampleInstance?.dnaconc}">
            <li class="fieldcontain">
                <span id="dnaconc-label" class="property-label"><g:message code="seqSample.dnaconc.label"
                                                                           default="Dnaconc"/></span>

                <span class="property-value" aria-labelledby="dnaconc-label"><g:fieldValue bean="${seqSampleInstance}"
                                                                                           field="dnaconc"/></span>

            </li>
        </g:if>

        <g:if test="${seqSampleInstance?.analysis}">
            <li class="fieldcontain">
                <span id="analysis-label" class="property-label"><g:message code="seqSample.analysis.label"
                                                                            default="Analysis"/></span>

                <span class="property-value" aria-labelledby="analysis-label"><g:fieldValue bean="${seqSampleInstance}"
                                                                                            field="analysis"/></span>

            </li>
        </g:if>

        <g:if test="${seqSampleInstance?.userName}">
            <li class="fieldcontain">
                <span id="userName-label" class="property-label"><g:message code="seqSample.userName.label"
                                                                            default="User Name"/></span>

                <span class="property-value" aria-labelledby="userName-label"><g:fieldValue bean="${seqSampleInstance}"
                                                                                            field="userName"/></span>

            </li>
        </g:if>

        <g:if test="${seqSampleInstance?.userEmail}">
            <li class="fieldcontain">
                <span id="userEmail-label" class="property-label"><g:message code="seqSample.userEmail.label"
                                                                             default="User Email"/></span>

                <span class="property-value" aria-labelledby="userEmail-label"><g:fieldValue bean="${seqSampleInstance}"
                                                                                             field="userEmail"/></span>

            </li>
        </g:if>

        <g:if test="${seqSampleInstance?.laneNo}">
            <li class="fieldcontain">
                <span id="laneno-label" class="property-label"><g:message code="seqSample.laneno.label"
                                                                          default="Lane No"/></span>

                <span class="property-value" aria-labelledby="laneno-label"><g:fieldValue bean="${seqSampleInstance}"
                                                                                          field="laneNo"/></span>

            </li>
        </g:if>

        <li class="fieldcontain">
            <span id="passfail-label" class="property-label"><g:message code="seqSample.passfail.label" default="Pass/Fail"/></span>
            <span class="property-value" aria-labelledby="passfail-label">
                <g:if test="${ ! seqSampleInstance?.authorisedQcFlag}">
                    <g:form action="authoriseSampleQc" id="${seqSampleInstance?.id}">
                        <g:select name="passfail" from="${['Pass','Fail']}" value="${params.passfail}" noSelection="['': '-Select QC-']"/>
                        <g:textField name="qcComment" />
                        <g:submitButton name="authorise" value="Authorise"/>
                    </g:form>
                </g:if>
                <g:else>
                    <g:qcPassFail authorised="${seqSampleInstance.authorisedQcFlag}" passfailFlag="${seqSampleInstance.passfailFlag}" />
                </g:else>
            </span>
        </li>

        <g:if test="${seqSampleInstance?.qcComment}">
            <li class="fieldcontain">
                <span id="comment-label" class="property-label"><g:message code="seqSample.comment.label" default="QC Comments"/></span>
                <span class="property-value" aria-labelledby="comment-label">
                    ${seqSampleInstance.qcComment}
                </span>
            </li>
        </g:if>

        <g:if test="${seqSampleInstance?.authorisedQcFlag}">
            <li class="fieldcontain">
                <span id="authorised-label" class="property-label"><g:message code="seqSample.authorisedQc.label" default="Authorised"/></span>
                <span class="property-value" aria-labelledby="authorised-label">
                    <g:form action="authoriseSampleQc" id="${seqSampleInstance?.id}">
                        ${seqSampleInstance.authorisedQc}
                        <g:submitButton name="authorise" value="Revoke"/>
                    </g:form>
                </span>
            </li>
        </g:if>


        <g:if test="${UrlLink.fastqcUrl( seqSampleInstance.seqrun.seqrun, seqSampleInstance.sampleName )}">
            <li class="fieldcontain">
                <span id="fastqc-label" class="property-label">
                    Sequencing base quality
                </span>
                <span class="property-value" aria-labelledby="fastqc-label">
                    <g:each in="${UrlLink.fastqcUrl(seqSampleInstance.seqrun.seqrun, seqSampleInstance.sampleName )}" status="i" var="read">
                        <g:link url="${read}" target="_blank">Read ${i+1}</g:link>
                    </g:each>
                </span>

            </li>
        </g:if>

        <li class="fieldcontain">
            <span id="seqSampleAmps-label" class="property-label">Sample Alignment Stats</span>
                <span class="property-value" aria-labelledby="stats-label" style="width: 400pt">
                    <table border="1" >
                        <tr>
                            <th>Parameter</th>
                            <th>Value</th>
                        </tr>
                        <g:each in="${StatsService.sampleSummary(seqSampleInstance)}" var="row">
                            <tr>
                                <td>${row.key}</td>
                                <td>${row.value}</td>
                            </tr>
                        </g:each>
                    </table>
                </span>
        </li>

        <li class="fieldcontain">

            <g:each in="${seqSampleInstance.patSample?.patAssays}" var="thisPatAssay">
                <span id="stest-label" class="property-label">
                    ROI for Pat Assay Test ${thisPatAssay.testName}
                </span>

                <span class="property-value" aria-labelledby="stats-label" style="width: 400pt">
                    <roi:roiList sample="${seqSampleInstance}" template="${thisPatAssay.testName}"/>
                </span>

            </g:each>
        </li>

        <li class="fieldcontain">
            <span id="seqSamples-label" class="property-label">Low Read Amplicons (<100 reads, count=${StatsService.lowAmplicons(seqSampleInstance,100).size()})</span>

            <g:each in="${StatsService.lowAmplicons(seqSampleInstance,100)}" var="amp">
                <span class="property-value" aria-labelledby="seqSamples-label">
                    ${amp.amplicon}
                    (reads ${amp.readsout})
                </span>
            </g:each>

        </li>

        <g:showPageTags/>

    </ol>
    <g:form>
        <fieldset class="buttons">
            <g:hiddenField name="id" value="${seqSampleInstance?.id}"/>
        </fieldset>
    </g:form>
</div>
<script>
    <g:showPageTagsScript tags="${seqSampleInstance?.tags as grails.converters.JSON}" id="${seqSampleInstance?.id}" controller="seqrun"/>
</script>
</body>
</html>
