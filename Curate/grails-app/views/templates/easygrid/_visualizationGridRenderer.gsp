<%@ page import="org.grails.plugin.easygrid.JsUtils" defaultCodec="none" %>

<jq:jquery>
%{--// attach the gid data to the element--}%
    $.data(document.getElementById("${attrs.id}_div"), 'grid',
    {
        options:${JsUtils.convertToJs(gridConfig.visualization, "${attrs.id}_div")},
        url:'${g.createLink(action: "${gridConfig.id}Rows")}',
        loadAll: ${gridConfig.visualization.loadAllData ? 'true' : 'false'}
    });

    google.load('visualization', '1', {
        'packages' : ['table'],
        'callback' : easygrid.initTable("${attrs.id}","")
        });

</jq:jquery>

<div id="${attrs.id}_div"></div>

