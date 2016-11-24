<!DOCTYPE html>
<html>
	<head>
		<title><g:if env="development">Grails Runtime Exception</g:if><g:else>Error</g:else></title>
		<meta name="layout" content="main">
		<g:if env="development"><link rel="stylesheet" href="${resource(dir: 'css', file: 'errors.css')}" type="text/css"></g:if>
	</head>
	<body>
		<g:if env="development">
			<g:renderException exception="${exception}" />
		</g:if>
		<g:else>
			<ul  class="errors">
				<li>You've attempted to access a page which does not exist - PathOS cannot find this SeqSample. If you went directly to a URL, please double-check it.</li>
				<g:if test="${sid}"><li>SeqSample ID ${sid} does not exist</li></g:if>
				<g:else><li>No ID parameter specified</li></g:else>


			</ul>
		</g:else>
	</body>
</html>
