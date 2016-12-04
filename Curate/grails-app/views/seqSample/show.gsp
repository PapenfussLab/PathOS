<%@ page import="org.petermac.pathos.curate.SeqSample" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'seqSample.label', default: 'SeqSample')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-seqSample" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-seqSample" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
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

        <g:if test="${seqSampleInstance?.patSample}">
            <li class="fieldcontain">
                <span id="sample-label" class="property-label"><g:message code="seqSample.patSample.label"
                                                                          default="Sample"/></span>

                <span class="property-value" aria-labelledby="sample-label"><g:link controller="patSample" action="show"
                                                                                    id="${seqSampleInstance?.patSample?.id}">${seqSampleInstance?.patSample?.encodeAsHTML()}</g:link></span>

            </li>
        </g:if>

        <g:if test="${seqSampleInstance?.sampleName}">
            <li class="fieldcontain">
                <span id="sampleName-label" class="property-label"><g:message code="seqSample.sampleName.label"
                                                                              default="Sample Name"/></span>

                <span class="property-value" aria-labelledby="sampleName-label"><g:fieldValue
                        bean="${seqSampleInstance}" field="sampleName"/></span>

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

        <g:if test="${seqSampleInstance?.sampleType}">
            <li class="fieldcontain">
                <span id="sampleName-label" class="property-label"><g:message code="seqSample.sampleType.label"
                                                                              default="Sample Type"/></span>

                <span class="property-value" aria-labelledby="sampleName-label"><g:fieldValue
                        bean="${seqSampleInstance}" field="sampleType"/></span>

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
                <span id="laneNo-label" class="property-label"><g:message code="seqSample.laneNo.label"
                                                                          default="Lane No"/></span>

                <span class="property-value" aria-labelledby="laneNo-label"><g:fieldValue bean="${seqSampleInstance}"
                                                                                          field="laneNo"/></span>

            </li>
        </g:if>

        <g:if test="${seqSampleInstance?.authorisedQcFlag}">
            <li class="fieldcontain">
                <span id="authorisedQcFlag-label" class="property-label"><g:message code="seqSample.authorisedQcFlag.label"
                                                                                  default="Authorised Flag"/></span>

                <span class="property-value" aria-labelledby="authorisedQcFlag-label"><g:formatBoolean
                        boolean="${seqSampleInstance?.authorisedQcFlag}"/></span>

            </li>
        </g:if>

        <g:if test="${seqSampleInstance?.passfailFlag}">
            <li class="fieldcontain">
                <span id="passfailFlag-label" class="property-label"><g:message code="seqSample.passfailFlag.label"
                                                                                default="Passfail Flag"/></span>

                <span class="property-value" aria-labelledby="passfailFlag-label"><g:formatBoolean
                        boolean="${seqSampleInstance?.passfailFlag}"/></span>

            </li>
        </g:if>

        <g:if test="${seqSampleInstance?.finalReviewBy}">
            <li class="fieldcontain">
                <span id="finalReviewBy-label" class="property-label"><g:message code="seqSample.finalReviewBy.label"
                                                                              default="Authorised"/></span>

                <span class="property-value" aria-labelledby="finalReviewBy-label">
                    <g:link controller="User" action="show" id="${seqSampleInstance?.finalReviewBy?.id}">
                        ${seqSampleInstance?.finalReviewBy?.encodeAsHTML()}
                    </g:link>
                </span>
            </li>
        </g:if>

        <g:if test="${seqSampleInstance?.qcComment}">
            <li class="fieldcontain">
                <span id="qcComment-label" class="property-label"><g:message code="seqSample.qcComment.label"
                                                                             default="Qc Comment"/></span>

                <span class="property-value" aria-labelledby="qcComment-label"><g:fieldValue bean="${seqSampleInstance}"
                                                                                             field="qcComment"/></span>

            </li>
        </g:if>

        <g:showPageTags/>
        <g:if test="${seqSampleInstance?.seqVariants}">
            <li class="fieldcontain">
                <span id="seqVariants-label" class="property-label"><g:message code="seqSample.seqVariants.label"
                                                                               default="Seq Variants"/></span>

                <g:each in="${seqSampleInstance.seqVariants}" var="s">
                    <span class="property-value" aria-labelledby="seqVariants-label"><g:link controller="seqVariant"
                                                                                             action="show"
                                                                                             id="${s.id}">${s?.encodeAsHTML()}</g:link></span>
                </g:each>

            </li>
        </g:if>

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
