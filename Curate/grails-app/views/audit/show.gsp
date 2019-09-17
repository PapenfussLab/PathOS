<%@ page import="org.petermac.pathos.curate.Audit" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'audit.label', default: 'Audit')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-audit" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                            default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-audit" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list audit">

        <g:if test="${auditInstance?.category}">
            <li class="fieldcontain">
                <span id="category-label" class="property-label"><g:message code="audit.category.label"
                                                                            default="Category"/></span>

                <span class="property-value" aria-labelledby="category-label"><g:fieldValue bean="${auditInstance}"
                                                                                            field="category"/></span>

            </li>
        </g:if>

        <g:if test="${auditInstance?.seqrun}">
            <li class="fieldcontain">
                <span id="seqrun-label" class="property-label"><g:message code="audit.seqrun.label"
                                                                          default="Seqrun"/></span>

                <span class="property-value" aria-labelledby="seqrun-label"><g:fieldValue bean="${auditInstance}"
                                                                                          field="seqrun"/></span>

            </li>
        </g:if>

        <g:if test="${auditInstance?.variant}">
            <li class="fieldcontain">
                <span id="variant-label" class="property-label"><g:message code="audit.variant.label"
                                                                           default="Variant"/></span>

                <span class="property-value" aria-labelledby="variant-label"><g:fieldValue bean="${auditInstance}"
                                                                                           field="variant"/></span>

            </li>
        </g:if>

        <g:if test="${auditInstance?.sample}">
            <li class="fieldcontain">
                <span id="sample-label" class="property-label"><g:message code="audit.sample.label"
                                                                          default="Sample"/></span>

                <span class="property-value" aria-labelledby="sample-label"><g:fieldValue bean="${auditInstance}"
                                                                                          field="sample"/></span>

            </li>
        </g:if>

        <g:if test="${auditInstance?.task}">
            <li class="fieldcontain">
                <span id="task-label" class="property-label"><g:message code="audit.task.label" default="Task"/></span>

                <span class="property-value" aria-labelledby="task-label"><g:fieldValue bean="${auditInstance}"
                                                                                        field="task"/></span>

            </li>
        </g:if>

        <g:if test="${auditInstance?.complete}">
            <li class="fieldcontain">
                <span id="complete-label" class="property-label"><g:message code="audit.complete.label"
                                                                            default="Complete"/></span>

                <span class="property-value" aria-labelledby="complete-label"><g:formatDate
                        date="${auditInstance?.complete}" format="dd-MMM-yyyy"/></span>

            </li>
        </g:if>

        <g:if test="${auditInstance?.elapsed}">
            <li class="fieldcontain">
                <span id="elapsed-label" class="property-label"><g:message code="audit.elapsed.label"
                                                                           default="Elapsed"/></span>

                <span class="property-value" aria-labelledby="elapsed-label"><g:fieldValue bean="${auditInstance}"
                                                                                           field="elapsed"/></span>

            </li>
        </g:if>

        <g:if test="${auditInstance?.software}">
            <li class="fieldcontain">
                <span id="software-label" class="property-label"><g:message code="audit.software.label"
                                                                            default="Software"/></span>

                <span class="property-value" aria-labelledby="software-label"><g:fieldValue bean="${auditInstance}"
                                                                                            field="software"/></span>

            </li>
        </g:if>

        <g:if test="${auditInstance?.swVersion}">
            <li class="fieldcontain">
                <span id="swVersion-label" class="property-label"><g:message code="audit.swVersion.label"
                                                                             default="Sw Version"/></span>

                <span class="property-value" aria-labelledby="swVersion-label"><g:fieldValue bean="${auditInstance}"
                                                                                             field="swVersion"/></span>

            </li>
        </g:if>

        <g:if test="${auditInstance?.username}">
            <li class="fieldcontain">
                <span id="username-label" class="property-label"><g:message code="audit.username.label"
                                                                            default="Username"/></span>

                <span class="property-value" aria-labelledby="username-label"><g:fieldValue bean="${auditInstance}"
                                                                                            field="username"/></span>

            </li>
        </g:if>

        <g:if test="${auditInstance?.description}">
            <li class="fieldcontain">
                <span id="description-label" class="property-label"><g:message code="audit.description.label"
                                                                               default="Description"/></span>

                <span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${auditInstance}"
                                                                                               field="description"/></span>

            </li>
        </g:if>

    </ol>
    <g:form>
        <fieldset class="buttons">
            <g:hiddenField name="id" value="${auditInstance?.id}"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
