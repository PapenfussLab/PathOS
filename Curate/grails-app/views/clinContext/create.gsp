<%@ page import="org.petermac.pathos.curate.ClinContext" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'clinContext.label', default: 'ClinContext')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>

	</head>
	<body>
		<a href="#create-clinContext" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>

<r:style>
#create-clinContext table {
	width: 500px;
	margin: auto;
}
#create-clinContext table input#create {
	background-image: url(<g:context/>/images/skin/database_save.png);
	background-position: 0.7em center;
	background-repeat: no-repeat;
	text-indent: 25px;
	background-color: transparent;
	border: 1px solid grey;
	color: #666666;
	cursor: pointer;
	display: inline-block;
	overflow: visible;
	padding: 10px 0;
	text-decoration: none;
	-moz-border-radius: 0.3em;
	-webkit-border-radius: 0.3em;
	border-radius: 0.3em;
	width: 110px;
	margin: 0 55px;
}

#create-clinContext table td h3 {
	margin-bottom: 0;
}

#create-clinContext table td p {
	margin-top: 5px;
}
td {
	width: 50%;
	line-height: 1em;
}
td input {
	width: 100%;
}
</r:style>

		<div id="create-clinContext" class="content scaffold-create" role="main">
			<h1><g:message code="default.create.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${clinContextInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${clinContextInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form action="save" >
				<table>
					<tr>
						<td><h3>Name</h3><p>A descriptive name, e.g. Colorectal Cancer</p></td>
						<td><g:textField name="description" value="${clinContextInstance?.description}"/></td>
					</tr>
					<tr>
						<td><h3>Code</h3><p>A short code, e.g. CRC</p></td>
						<td><g:textField name="code" value="${clinContextInstance?.code}"/></td>
					</tr>
					<tr>
						<td><h3>Save</h3></td>
						<td>
							<g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
						</td>
					</tr>
				</table>
			</g:form>
		</div>
	</body>
</html>
