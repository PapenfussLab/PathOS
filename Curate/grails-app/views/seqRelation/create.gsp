<%@ page import="org.petermac.pathos.curate.SeqRelation" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'seqRelation.label', default: 'SeqRelation')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#create-seqRelation" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="create-seqRelation" class="content scaffold-create" role="main">
			<h1><g:message code="default.create.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${seqRelationInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${seqRelationInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form action="save" id="form">
				<fieldset class="form">

					<div class="fieldcontain ${hasErrors(bean: seqRelationInstance, field: 'relation', 'error')}" >
						<label for="relation" style="float:left;text-align:right;">
							<g:message code="seqRelation.relation.label" default="Relation" />

						</label>
						<span class="property-value">
							<g:select name="relation" from="${seqRelationInstance.constraints.relation.inList}" value="${seqRelationInstance?.relation}" valueMessagePrefix="seqRelation.relation" noSelection="['': '']"/>
						</span>
					</div>

					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
