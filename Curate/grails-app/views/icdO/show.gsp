
<%@ page import="org.petermac.pathos.curate.IcdO" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'icdO.label', default: 'IcdO')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-icdO" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-icdO" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list icdO">
			
				<g:if test="${icdOInstance?.histCode}">
				<li class="fieldcontain">
					<span id="histCode-label" class="property-label"><g:message code="icdO.histCode.label" default="Hist Code" /></span>
					
						<span class="property-value" aria-labelledby="histCode-label"><g:fieldValue bean="${icdOInstance}" field="histCode"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${icdOInstance?.histDetail}">
				<li class="fieldcontain">
					<span id="histDetail-label" class="property-label"><g:message code="icdO.histDetail.label" default="Hist Detail" /></span>
					
						<span class="property-value" aria-labelledby="histDetail-label"><g:fieldValue bean="${icdOInstance}" field="histDetail"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${icdOInstance?.histDetailCode}">
				<li class="fieldcontain">
					<span id="histDetailCode-label" class="property-label"><g:message code="icdO.histDetailCode.label" default="Hist Detail Code" /></span>
					
						<span class="property-value" aria-labelledby="histDetailCode-label"><g:fieldValue bean="${icdOInstance}" field="histDetailCode"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${icdOInstance?.histology}">
				<li class="fieldcontain">
					<span id="histology-label" class="property-label"><g:message code="icdO.histology.label" default="Histology" /></span>
					
						<span class="property-value" aria-labelledby="histology-label"><g:fieldValue bean="${icdOInstance}" field="histology"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${icdOInstance?.site}">
				<li class="fieldcontain">
					<span id="site-label" class="property-label"><g:message code="icdO.site.label" default="Site" /></span>
					
						<span class="property-value" aria-labelledby="site-label"><g:fieldValue bean="${icdOInstance}" field="site"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${icdOInstance?.siteCode}">
				<li class="fieldcontain">
					<span id="siteCode-label" class="property-label"><g:message code="icdO.siteCode.label" default="Site Code" /></span>
					
						<span class="property-value" aria-labelledby="siteCode-label"><g:fieldValue bean="${icdOInstance}" field="siteCode"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${icdOInstance?.id}" />
					<g:link class="edit" action="edit" id="${icdOInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
