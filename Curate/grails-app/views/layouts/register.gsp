<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<title><g:layoutTitle default='User Registration'/></title>

<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon"/>

<link href="/PathOS/dist/css/main.css" rel="stylesheet">

<s2ui:resources module='register' />
<g:layoutHead/>

</head>

<body>
<nav class="navbar-default">
    <div class="container-fluid">
        <div class="navbar-header">

            <div id="top-left">
                <a href="/PathOS/"><img src="/PathOS/dist/img/pathos_logo.svg" class="navlogo" id="pathos_logo" alt="PathOS"></a>
                <a target="_blank" href="http://www.petermac.org"><img src="/PathOS/dist/img/petermac_logo.png" class="navlogo" alt="PeterMac" id="petermac_logo"></a>
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

<div class="footer" role="contentinfo" style="background: #07447C;position: fixed;color: #ffffff;bottom: 0px;width: 100%;"><a href="mailto:ken.doig@petermac.org" style="margin: 5px;line-height: 1.8em;background: #07447C;color: #ffffff;"><i class="fa fa-envelope" aria-hidden="true"></i>
     Feedback</a> August 2016</div>
</body>
</html>
