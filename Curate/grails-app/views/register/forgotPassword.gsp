<html>

<head>
<title><g:message code='spring.security.ui.forgotPassword.title'/></title>
<meta name='layout' content='register'/>
    <style type='text/css' media='screen'>
        /*.ui-dialog-titlebar {
            color: #27447b;
            background: #FFFFFF;
            font-size: 16px;
            font-weight: bold;

        }*/
    </style>
</head>

<body>

<p/>
<%--
<s2ui:form width='400' height='220' elementId='forgotPasswordFormContainer'
           titleCode='spring.security.ui.forgotPassword.header' center='true'>
--%>
<cs2ui:form width='400' height='220' resizable='false' titleCode='spring.security.ui.forgotPassword.header' elementId='forgotPasswordFormContainer' center='true'>

	<g:form action='forgotPassword' name="forgotPasswordForm" autocomplete='off'>

	<g:if test='${emailSent}'>
	<br/>
	<g:message code='spring.security.ui.forgotPassword.sent'/> <br/>
        <br/>
       <a href="${createLink(uri:'/')}">Back to PathOS home</a>
	</g:if>

	<g:else>

	<br/>
	<h4><g:message code='spring.security.ui.forgotPassword.description'/></h4>


			<label for="username"><g:message code='spring.security.ui.forgotPassword.username'/></label>
			<g:textField name="username" size="25" />
        <br/><br/>

	<s2ui:submitButton elementId='reset' form='forgotPasswordForm' messageCode='spring.security.ui.forgotPassword.submit'/>

	</g:else>

	</g:form>
</cs2ui:form>

<script>
$(document).ready(function() {
	$('#username').focus();
});
</script>

</body>
</html>
