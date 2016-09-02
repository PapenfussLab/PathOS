<html>

<head>
<title><g:message code='spring.security.ui.resetPassword.title'/></title>
<meta name='layout' content='register'/>
    <style type='text/css' media='screen'>
        .sign-in label {
            float: right;
        }
        .sign-in input {
            width: 150px;
        }
    </style>
</head>

<body>

<p/>

<cs2ui:form width='975' height='250' elementId='resetPasswordFormContainer'
           titleCode='spring.security.ui.resetPassword.header' center='true'>

	<g:form action='resetPassword' name='resetPasswordForm' autocomplete='off'>
	<g:hiddenField name='t' value='${token}'/>
	<div class="sign-in">

	<br/>
	<h4><g:message code='spring.security.ui.resetPassword.description'/></h4>

	<table>
		<s2ui:passwordFieldRow name='password' labelCode='resetPasswordCommand.password.label' bean="${command}"
                             labelCodeDefault='Password' value="${command?.password}"/>

		<s2ui:passwordFieldRow name='password2' labelCode='resetPasswordCommand.password2.label' bean="${command}"
                             labelCodeDefault='Password (again)' value="${command?.password2}"/>
	</table>

	<s2ui:submitButton elementId='reset' form='resetPasswordForm' messageCode='spring.security.ui.resetPassword.submit'/>

	</div>
	</g:form>

</cs2ui:form>

<script>
$(document).ready(function() {
	$('#password').focus();
});
</script>

</body>
</html>
