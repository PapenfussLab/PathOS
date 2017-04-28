<%@ page import="org.petermac.pathos.curate.CurVariant" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'variant.label', default: 'CurVariant')}"/>
    <title>${variantInstance} - <g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="edit-variant" class="content scaffold-edit" role="main">
    <h1><g:message code="default.edit.label" args="[entityName]"/> ${variantInstance}</h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${variantInstance}">
        <ul class="errors" role="alert">
            <g:eachError bean="${variantInstance}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                        error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form method="post">
        <g:hiddenField name="id" value="${variantInstance?.id}"/>
        <g:hiddenField name="version" value="${variantInstance?.version}"/>
        <fieldset class="form">
            <g:render template="form"/>
        </fieldset>
        <fieldset class="buttons">
            <g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
        </fieldset>
    </g:form>
</div>


%{--
DKGM 29-November-2016

Since this page is built using the grails 2.3.7 scaffold and built-in taglibs,
it is a bit difficult to mess with the code above.

The best way to manipulate this document is with some javascript, as follows.

The purpose of this javascript is to add some user-friendly instructions for PMID formatting

PMIDs should be included in the Report Description as such:
[PMID: 1212332]
or
[PMID: 121512, 3211233]
for multiple PMIDs.

To facilitate this, we're going to highlight correctly written PMIDs.

--}%
<r:script>

    PathOS.pubmed.applyHighlight('highlightClass');

    // This is the dumbest thing ever. But I can't figure out a better way right now sorry!
    d3.select("label[for='reportDesc']")
        .html("")
        .append("p").html("Report Description<br><br>Please add Pubmed IDs in this format:<br><mark>[PMID: 123456789]</mark><br><br>For multiple PMIDs use this format:<br><mark>[PMID: 123456, 567890]</mark><br><br>Correctly formatted PMIDs will be <mark>highlighted</mark>")
        .append("span")
        .html("<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>");




</r:script>



</body>
</html>





























