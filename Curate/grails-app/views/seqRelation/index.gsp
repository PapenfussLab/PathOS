
<%@ page import="org.petermac.pathos.curate.SeqRelation" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'seqRelation.label', default: 'SeqRelation')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-seqRelation" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-seqRelation" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="relation" title="${message(code: 'seqRelation.relation.label', default: 'Relation')}" />
					
						<g:sortableColumn property="base" title="${message(code: 'seqRelation.base.label', default: 'Base')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${seqRelationInstanceList}" status="i" var="seqRelationInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${seqRelationInstance.id}">${fieldValue(bean: seqRelationInstance, field: "relation")}</g:link></td>
					
						<td>${fieldValue(bean: seqRelationInstance, field: "base")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${seqRelationInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
