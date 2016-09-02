<%@ page import="org.springframework.validation.FieldError; org.petermac.pathos.curate.Evidence" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'evidence.label', default: "Evidence for: ${variantInstance}")}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
        <tooltip:resources/>
    </head>
	<body>
		<a href="#edit-evidence" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<div id="edit-evidence" class="content scaffold-edit" role="main">
			<h1><g:message code="default.edit.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
			</g:if>

			<g:hasErrors bean="${evidenceInstance}">
                <ul class="errors" role="alert">
                    <g:eachError bean="${evidenceInstance}" var="error">
                    <li <g:if test="${error in FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                    </g:eachError>
                </ul>
			</g:hasErrors>

			<g:form method="post" >
				<g:hiddenField name="id" value="${variantInstance?.id}" />
				<g:hiddenField name="version" value="${variantInstance?.version}" />
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
