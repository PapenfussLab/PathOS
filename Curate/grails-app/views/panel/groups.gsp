<%@ page import="org.petermac.pathos.curate.SeqSample; org.petermac.pathos.curate.Panel" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'panel.label', default: 'Panel')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <r:require module="filterpane"/>
</head>

<body>
<a href="#list-panel" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                            default="Skip to content&hellip;"/></a>

%{--<div class="nav" role="navigation">--}%
    %{--<ul>--}%
        %{--<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>--}%
    %{--</ul>--}%
%{--</div>--}%

<div id="list-panel" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>



<h1>Panel Groups</h1>

    <table>
    <thead><tr>
        <th>Group</th>
        <th>Panel</th>
        <th>Latest Seqrun</th>
        <th>Date</th>
        <th>Templates</th>
    </tr></thead>
    <tbody>
    <g:each in="${panelGroups.sort({it[1].runDate}).reverse(true)}" var="group">
        <tr>
            <td>${group[0].panelGroup}</td>
            <td>${group[0]}</td>
            <td><g:seqrunLink seqrun="${group[1]}"/></td>
            <td><g:formatDate date="${group[1].runDate}" format="dd-MMM-yyyy"/></td>
            <td>Fail / Neg / Var</td>
        </tr>
    </g:each>
    </tbody>
</table>

<h1>Tests</h1>
<table>
    <thead><tr>
        <th>Group</th>
        <th>Panel</th>
        <th>Test Set</th>
        <th>Test</th>
        <th>Latest Seqrun</th>
        <th>Date</th>
        <th>Templates</th>
    </tr></thead>
    <tbody id="test_tbody">
    <g:each in="${tests}" var="test">
        <tr name="${test}">
            <td></td>
            <td></td>
            <td></td>
            <td>${test}</td>
            <td></td>
            <td></td>
            <td>Fail / Neg / Var</td>
        </tr>
    </g:each>
    </tbody>
</table>

<r:script>
    var panelTests = PathOS.data.load("panelTests", {})

    if(Object.keys(panelTests).length != 0) {
        drawPanelTests(panelTests);
    }

    $.ajax("<g:context/>/panel/latestTests")
    .complete(function(d){

        if(d.status == 200) {
            PathOS.data.save("panelTests", d);
            drawPanelTests(d);
        }
    });

    function drawPanelTests(d) {
        d3.select("#test_tbody").selectAll("tr").remove();

        var stuff = d3.select("#test_tbody").selectAll("tr")
                .data(d.responseJSON);

        stuff.enter()
                .append("tr")
                .attr("id", function(d){
            return d.id;
        }).each(function(d){
            var row = d3.select(this);
            row.append("td").text(d.group);
            row.append("td").text(d.panel);
            row.append("td").text(d.testSet);
            row.append("td").text(d.test);
            row.append("td").append("a").attr("href", d.seqrun.seqrun).text(d.seqrun.seqrun);
            row.append("td").text(d.date);
            row.append("td").text("Fail / Neg / Var")
        });

        stuff.exit().remove();
    };

</r:script>

</div>
</body>
</html>
