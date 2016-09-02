
<%@ page import="org.petermac.pathos.curate.Amplicon" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'amplicon.label', default: 'Amplicon')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-amplicon" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<%--<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>--%>
			</ul>
		</div>
		<div id="show-amplicon" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list amplicon">
			
				<g:if test="${ampliconInstance?.panel}">
				<li class="fieldcontain">
					<span id="panel-label" class="property-label"><g:message code="amplicon.panel.label" default="Panel" /></span>
					
						<span class="property-value" aria-labelledby="panel-label"><g:fieldValue bean="${ampliconInstance}" field="panel"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${ampliconInstance?.amplicon}">
				<li class="fieldcontain">
					<span id="amplicon-label" class="property-label"><g:message code="amplicon.amplicon.label" default="Amplicon" /></span>
					
						<span class="property-value" aria-labelledby="amplicon-label"><g:fieldValue bean="${ampliconInstance}" field="amplicon"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${ampliconInstance?.chr}">
				<li class="fieldcontain">
					<span id="chr-label" class="property-label"><g:message code="amplicon.chr.label" default="Chr" /></span>
					
						<span class="property-value" aria-labelledby="chr-label"><g:fieldValue bean="${ampliconInstance}" field="chr"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${ampliconInstance?.startpos}">
				<li class="fieldcontain">
					<span id="startpos-label" class="property-label"><g:message code="amplicon.startpos.label" default="Startpos" /></span>
					
						<span class="property-value" aria-labelledby="startpos-label"><g:fieldValue bean="${ampliconInstance}" field="startpos"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${ampliconInstance?.endpos}">
				<li class="fieldcontain">
					<span id="endpos-label" class="property-label"><g:message code="amplicon.endpos.label" default="Endpos" /></span>
					
						<span class="property-value" aria-labelledby="endpos-label"><g:fieldValue bean="${ampliconInstance}" field="endpos"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${ampliconInstance?.primerlen1}">
				<li class="fieldcontain">
					<span id="primerlen1-label" class="property-label"><g:message code="amplicon.primerlen1.label" default="Primerlen1" /></span>
					
						<span class="property-value" aria-labelledby="primerlen1-label"><g:fieldValue bean="${ampliconInstance}" field="primerlen1"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${ampliconInstance?.primerlen2}">
				<li class="fieldcontain">
					<span id="primerlen2-label" class="property-label"><g:message code="amplicon.primerlen2.label" default="Primerlen2" /></span>
					
						<span class="property-value" aria-labelledby="primerlen2-label"><g:fieldValue bean="${ampliconInstance}" field="primerlen2"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<%--<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${ampliconInstance?.id}" />
					<g:link class="edit" action="edit" id="${ampliconInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>--%>
		</div>
	</body>
</html>
