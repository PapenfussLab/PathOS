<%@ page import="org.petermac.pathos.curate.SeqRelation" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'seqRelation.label', default: 'SeqRelation')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#edit-seqRelation" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
<sec:ifAnyGranted roles="ROLE_DEV">
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
</sec:ifAnyGranted>
			</ul>
		</div>
		<div id="edit-seqRelation" class="content scaffold-edit" role="main">
			<h1><g:message code="default.edit.label" args="[entityName]" /></h1>
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
			<g:form method="post" >
				<g:hiddenField name="id" value="${seqRelationInstance?.id}" />
				<g:hiddenField name="version" value="${seqRelationInstance?.version}" />
				<g:hiddenField name="formaction" value="edit" />
				<fieldset class="form">

					<div class="fieldcontain ${hasErrors(bean: seqRelationInstance, field: 'relation', 'error')}">
						<label for="relation" style="float:left;text-align:right;">
							<g:message code="seqRelation.relation.label" default="Relation" />

						</label>
						<span class="property-value">${seqRelationInstance?.relation}</span>
					</div>

					<%-- if else block to disallow user editing a derived sample seqrelation --%>
					<g:if test="${['Minus','Union','Intersect'].contains(seqRelationInstance.relation)}">
						<p>You cannot edit a SeqRelation made from Derived samples.</p>
						</fieldset>
					</g:if>
					<g:else>
						<g:render template="form"/>
						</fieldset>
						<fieldset class="buttons">
							<g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
							<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" formnovalidate="" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
						</fieldset>
					</g:else>
			</g:form>
		</div>
	</body>
</html>
