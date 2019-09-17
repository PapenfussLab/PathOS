<%@ page import="org.petermac.util.Locator" %>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<title><g:layoutTitle default='User Registration'/></title>

<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon"/>

<link href="<g:context/>/dist/css/main.css" rel="stylesheet">

<s2ui:resources module='register' />
<g:layoutHead/>

<style>
<g:if test="${Locator.pathosEnv == 'pa_prod'}">
.navbar-default {
    background: #07447c;
}
div.footer {
    background: #07447C;
}
</g:if>
<g:elseif test="${Locator.pathosEnv == 'pa_uat'}">
.navbar-default {
    background: #077c44;
}
div.footer {
    background: #044c2a;
}
</g:elseif>
<g:else>
.navbar-default {
    background: #7c0744;
}
div.footer {
    background: #4c042a;
}
</g:else>
</style>

</head>

<body>
<nav class="navbar-default">
    <div class="container-fluid">
        <div class="navbar-header">

            <div id="top-left">
                <a href="<g:context/>/"><img src="<g:context/>/dist/images/pathos_logo_transparent.svg" class="navlogo" id="pathos_logo" alt="PathOS"></a>
                <a target="_blank" href="//www.petermac.org"><img src="<g:context/>/dist/images/petermac_logo.png" class="navlogo" alt="PeterMac" id="petermac_logo"></a>
            </div>

        </div>
    </div>
</nav>


<div id="wrapper" class="toggled">
<div id="sidebar-wrapper"></div>

<div id="page-content-wrapper">
<s2ui:layoutResources module='register' />
<br>
<br>
<br>
<g:layoutBody/>
</div>
</div>

<s2ui:showFlash/>

<div class="footer" role="contentinfo" style="position: fixed;color: #ffffff;bottom: 0px;width: 100%;"><a href="mailto:pathos@petermac.org" style="margin: 5px;line-height: 1.8em; color: #ffffff;"><i class="fa fa-envelope" aria-hidden="true"></i> Feedback</a> - <g:render template='/gitDate'/></div>
</body>
</html>
