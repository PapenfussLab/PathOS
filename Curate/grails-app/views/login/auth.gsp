<html>

<head>
    <title>Log in to PathOS</title>
    <meta name='layout' content='register'/>
    <style type='text/css' media='screen'>
    .loginlabel {
        float:left;
        margin-left:8px;
    }
    .loginfield {
        float:right;
        margin-right:8px;
    }
    .login-inner .sign-in h1 {
        color: #07447C;
        font-size: 16px;
        font-weight: bold;
        margin-bottom: 15px;
    }
    .login {
        width: 350px;
    }
    </style>
</head>

<body>

<div id="ieWarningMessage" style="color:red; font-size:14px; display:none; margin:30px;">PathOS has detected that are using Internet Explorer, which is an unsupported browser. You are likely to encounter issues - please use <a href="https://www.mozilla.org/en-US/firefox/new/" target="_blank">Firefox</a>, <a href="https://www.google.com/chrome/"  target="_blank">Chrome</a> or <a href="http://www.apple.com/au/safari/" target="_blank">Safari</a> instead.</div>

<div class="login s2ui_center ui-corner-all" style='text-align:center;'>
    <div class="login-inner">
        <form action='${postUrl}' method='POST' id="loginForm" name="loginForm"> <%-- autocomplete='off' --%>
            <div class="sign-in">

                <h1>Log in to PathOS</h1>
                <table>
                    <tr>
                        <td><label class=loginlabel  for="username">Username:</label></td>
                        <td><input class=loginfield name="j_username" id="username" size="24" /></td>
                    </tr>
                    <tr>
                        <td><label class=loginlabel for="password">Password:</label></td>
                        <td><input class=loginfield type="password" name="j_password" id="password" size="24" /></td>
                    </tr>
                </table>
                <s2ui:submitButton elementId='loginButton' form='loginForm' messageCode='spring.security.ui.login.login'/>
            </div>
        </form>
    </div>


</div>

<script>
    function detectIE() {
        var ua = window.navigator.userAgent;

        var msie = ua.indexOf('MSIE ');
        if (msie > 0) {
            // IE 10 or older => return version number
            return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
        }

        var trident = ua.indexOf('Trident/');
        if (trident > 0) {
            // IE 11 => return version number
            var rv = ua.indexOf('rv:');
            return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
        }

        var edge = ua.indexOf('Edge/');
        if (edge > 0) {
            // Edge (IE 12+) => return version number
            return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
        }

        // other browser
        return false;
    }

    $(document).ready(function() {
        $('#username').focus();
        if(detectIE()) {
            //var warningString = ''

            //$('#ieWarningMessage').text(warningString).html();
            $('#ieWarningMessage').slideToggle("fast");
        }

    });

    <s2ui:initCheckboxes/>

</script>

</body>
</html>
