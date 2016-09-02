%{--
  - Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
  -
  - Organisation: Peter MacCallum Cancer Centre
  - Author: doig ken
  --}%



<%@ page import="org.petermac.pathos.curate.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'testing.header.label')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>

    <title>jQuery UI Multiselect</title>

    <r:require modules="export"/>

    %{--CSS Files--}%
    <link rel="stylesheet" href="http://www.quasipartikel.at/multiselect/css/common.css" type="text/css" />
    <link type="text/css" rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.10/themes/ui-lightness/jquery-ui.css" />
    <link type="text/css" href="${resource(plugin: 'easygrid', dir: 'jquery.jqGrid-4.6.0/css', file: 'ui.jqgrid.css')}" />
    <link href="<g:resource plugin='easygrid' dir='jquery-ui-1.11.0' file='jquery-ui.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
    <link href="<g:resource plugin='easygrid' dir='jquery.jqGrid-4.6.0/css' file='ui.jqgrid.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />
    <link href="<g:resource plugin='easygrid' dir='jquery.jqGrid-4.6.0/plugins' file='ui.multiselect.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />

    %{--Javascript Files--}%
    <g:javascript src="quasipartikel/jquery.min.js" />
    <script src="/PathOS/static/bundle-bundle_easygrid-jqgrid-dev_head.js" type="text/javascript" ></script>
    <g:javascript src="quasipartikel/jquery-ui.min.js" />
    <g:javascript src="quasipartikel/ui.multiselect.js" />
    <script type="text/javascript">
        $(function(){
            $("select").multiselect();
        });
    </script>

</head>

<body>
<a href="#list-seqVariant" class="skip" tabindex="-1">
    <g:message code="default.link.skip.label" default="Skip to content&hellip;"/>
</a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>

<h1>${params} Search Test</h1>

<form action="index.html">
    <select id="countries" class="multiselect" multiple="multiple" name="countries[]">
        <option value="AFG">Afghanistan</option>
        <option value="ALB">Albania</option>
        <option value="DZA">Algeria</option>
        <option value="AND">Andorra</option>
        <option value="ARG">Argentina</option>
        <option value="ARM">Armenia</option>
        <option value="ABW">Aruba</option>
        <option value="AUS">Australia</option>
        <option value="AUT" selected="selected">Austria</option>

        <option value="AZE">Azerbaijan</option>
        <option value="BGD">Bangladesh</option>
        <option value="BLR">Belarus</option>
        <option value="BEL">Belgium</option>
        <option value="BIH">Bosnia and Herzegovina</option>
        <option value="BRA">Brazil</option>
        <option value="BRN">Brunei</option>
        <option value="BGR">Bulgaria</option>
        <option value="CAN">Canada</option>
    </select>
    <br/>
    <input type="submit" value="Submit Form"/>
</form>

</body>

<r:script type="text/javascript">
</r:script>

</html>
