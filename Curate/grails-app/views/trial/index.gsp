
<%@ page import="org.petermac.pathos.curate.Trial" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'trial.label', default: 'Trial')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-trial" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-trial" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="study" title="${message(code: 'trial.study.label', default: 'Study Id')}" />
					
						<g:sortableColumn property="briefTitle" title="${message(code: 'trial.briefTitle.label', default: 'Brief Title')}" />
					
						<g:sortableColumn property="molecularAlterations" title="${message(code: 'trial.molecularAlterations.label', default: 'Molecular Alterations')}" />
					
						<g:sortableColumn property="status" title="${message(code: 'trial.status.label', default: 'Status')}" />
					
						<g:sortableColumn property="startDate" title="${message(code: 'trial.startDate.label', default: 'Start Date')}" />
					
						<g:sortableColumn property="interventions" title="${message(code: 'trial.interventions.label', default: 'Interventions')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${trialInstanceList}" status="i" var="trialInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${trialInstance.id}">${fieldValue(bean: trialInstance, field: "study")}</g:link></td>
					
						<td>${fieldValue(bean: trialInstance, field: "briefTitle")}</td>
					
						<td>${fieldValue(bean: trialInstance, field: "molecularAlterations")}</td>
					
						<td>${fieldValue(bean: trialInstance, field: "status")}</td>
					
						<td>${fieldValue(bean: trialInstance, field: "startDate")}</td>
					
						<td>${fieldValue(bean: trialInstance, field: "interventions")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${trialInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
