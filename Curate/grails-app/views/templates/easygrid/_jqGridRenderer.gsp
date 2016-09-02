<%@ page import="org.grails.plugin.easygrid.GridUtils; org.grails.plugin.easygrid.JsUtils" defaultCodec="none" %>

<g:set var="gridId" value="${attrs.id}_table"/>
<g:set var="pagerId" value="${attrs.id}Pager"/>
<g:set var="conf" value="${gridConfig.jqgrid}"/>

<table id="${gridId}"></table>

<div id="${pagerId}"></div>

<jq:jquery>
    function setFilterNotification() {
            postData =  $('#curation_table').jqGrid('getGridParam', 'postData')
            if(postData.filters) { //filter appied
                $('#filterNotification').show()
                $('#filterDescription').text(postData.filters)
            } else {
                $('#filterNotification').hide()
            }
        }


    jQuery("#${gridId}").jqGrid({
    url: '${g.createLink(controller: attrs.controller, action: "${gridConfig.id}Rows", params: GridUtils.externalParams(gridConfig))}',
    loadError: easygrid.loadError,
    pager: '#${pagerId}',
    ${JsUtils.convertToJs(conf - [navGrid: conf.navGrid] - [filterToolbar: conf.filterToolbar], gridId, true)},
    <g:if test="${gridConfig.subGrid}">
        subGrid: true,
        subGridRowExpanded: easygrid.subGridRowExpanded('${g.createLink(controller: attrs.controller, action: "${gridConfig.subGrid}Html")}'),
    </g:if>
    <g:if test="${gridConfig.childGrid}">
        "onSelectRow":easygrid.onSelectGridRowReloadGrid('${gridConfig.childGrid}','${gridConfig.childParamName}'),
    </g:if>
    <g:if test="${gridConfig.inlineEdit}">
        editurl: '${g.createLink(controller: attrs.controller, action: "${gridConfig.id}InlineEdit")}',
        cellurl: '${g.createLink(controller: attrs.controller, action: "${gridConfig.id}InlineEdit")}',
        onSelectRow: easygrid.onSelectRowInlineEdit('${gridId}'),
    </g:if>
    colModel: [
    <grid:eachColumn gridConfig="${gridConfig}">
        <g:if test="${col.render}">
            {${JsUtils.convertToJs(col.jqgrid + [name: col.name, search: col.enableFilter, label: g.message(code: col.label, default: col.label)], gridId, true)}
            <g:if test="${col.otherProperties}">
                ,${col.otherProperties}
            </g:if>
            },
        </g:if>
    </grid:eachColumn>
    ],
    <g:if test="${gridConfig.otherProperties}">
        ${gridConfig.otherProperties.trim()}   // render properties defined in the gsp
    </g:if>

    <g:if test="${gridConfig.userFilter}">
        postData: { filters: '${gridConfig.userFilter}' },
        search: true,
    </g:if>
    });
    <g:if test="${gridConfig.masterGrid}">%{--set the on select row of the master grid--}%
        jQuery('#${gridConfig.masterGrid}_table').jqGrid('setGridParam',{ "onSelectRow" : easygrid.onSelectGridRowReloadGrid('${gridId}', '${gridConfig.childParamName}')});
    </g:if>
    <g:if test="${gridConfig.enableFilter}">
        jQuery('#${gridId}').jqGrid('filterToolbar', ${JsUtils.convertToJs(conf.filterToolbar, gridId)});
    </g:if>

    <g:if test="${gridConfig.addNavGrid}">
        jQuery('#${gridId}').jqGrid('navGrid','#${pagerId}',
        ${JsUtils.convertToJs(conf.navGrid.generalOpts, gridId)},
        ${JsUtils.convertToJs(conf.navGrid.editOpts, gridId)},     //edit
        ${JsUtils.convertToJs(conf.navGrid.addOpts, gridId)},     //add
        ${JsUtils.convertToJs(conf.navGrid.delOpts, gridId)},     //delete
        ${JsUtils.convertToJs(conf.navGrid.searchOpts, gridId)},     //search
        ${JsUtils.convertToJs(conf.navGrid.viewOpts, gridId)}     //view
        )
        <g:if test="${gridConfig.addUrl}">
            .jqGrid('navButtonAdd','#${pagerId}',{caption:"", buttonicon:"ui-icon-plus", onClickButton:function(){
            document.location = '${gridConfig.addUrl}';
        }})
        </g:if>

        <g:if test="${gridConfig.addFunction}">
           .jqGrid('navButtonAdd','#${pagerId}',{id:"showhidecols", caption:"", buttonicon:"ui-icon-script", title:"Show/hide columns", onClickButton:${gridConfig.addFunction}})
        </g:if>

        <g:if test="${gridConfig.savePrefsFunction}">
            .jqGrid('navButtonAdd','#${pagerId}',{id:"resetcols", caption:"", buttonicon:"ui-icon-seek-first", title:"Reset columns", onClickButton:${gridConfig.resetPrefsFunction}})
            .jqGrid('navButtonAdd','#${pagerId}',{id:"saveuserprefs", caption:"", buttonicon:"ui-icon-disk", title:"Save filter and column preferences", onClickButton:${gridConfig.savePrefsFunction}})
        </g:if>


        ;

    </g:if>
</jq:jquery>

