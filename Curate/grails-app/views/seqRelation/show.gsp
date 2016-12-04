
<%@ page import="org.petermac.pathos.curate.SeqRelation" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'seqRelation.label', default: 'SeqRelation')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-seqRelation" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-seqRelation" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list seqRelation">
			
				<g:if test="${seqRelationInstance?.relation}">
				<li class="fieldcontain">
					<span id="relation-label" class="property-label"><g:message code="seqRelation.relation.label" default="Relation" /></span>
					
						<span class="property-value" aria-labelledby="relation-label"><g:fieldValue bean="${seqRelationInstance}" field="relation"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${seqRelationInstance?.base}">
				<li class="fieldcontain">
					<span id="base-label" class="property-label"><g:message code="seqRelation.base.label" default="Base" /></span>
					
						<span class="property-value" aria-labelledby="base-label"><g:fieldValue bean="${seqRelationInstance}" field="base"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${seqRelationInstance?.samples}">
				<li class="fieldcontain">
					<span id="samples-label" class="property-label"><g:message code="seqRelation.samples.label" default="Samples" /></span>
					
						<g:each in="${seqRelationInstance.samples}" var="s">
						<span class="property-value" aria-labelledby="samples-label"><g:link controller="seqSample" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${seqRelationInstance?.id}" />
					<g:link class="edit" action="edit" id="${seqRelationInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
