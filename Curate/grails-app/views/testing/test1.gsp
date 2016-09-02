<%@ page import="org.petermac.pathos.curate.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'testing.header.label')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>

    %{--CSS Files--}%
    <link type="text/css" href="http://mind2soft.com/labs/jquery/multiselect/css/common.css" rel="stylesheet" />
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css" />

    %{--<link href="<g:resource plugin='easygrid'  dir='jquery-ui-1.11.0' file='jquery-ui.min.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />--}%
    %{--<link href="<g:resource plugin='easygrid'  dir='jquery.jqGrid-4.6.0/css' file='ui.jqgrid.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />--}%

    %{--<script src="<g:resource plugin="jquery" dir="js/jquery" file="jquery-1.11.1.min.js"/>" type="test/javascript"></script>--}%
    %{--<script src="<g:resource plugin="easygrid" dir="jquery-ui-1.11.0" file="jquery-ui.min.js"/>" type="test/javascript"></script>--}%
    <script src="http://code.jquery.com/jquery-1.9.0.min.js"></script>
    <script src="http://code.jquery.com/ui/1.9.2/jquery-ui.min.js"></script>
    <script src="http://mind2soft.com/labs/jquery/multiselect/js/jquery.uix.multiselect.js"></script>
    <link href="http://mind2soft.com/labs/jquery/multiselect/css/jquery.uix.multiselect.css" type="text/css" rel="stylesheet" media="screen, projection" />

    %{--<script src="/PathOS/static/bundle-bundle_easygrid-jqgrid-dev_head.js" type="text/javascript" ></script>--}%

    %{--<script src="<g:resource dir='js'  file='jquery.uix.multiselect.js'/>"  type="test/javascript"></script>--}%
    %{--<link  href="<g:resource dir='css' file='jquery.uix.multiselect.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />--}%
    %{--<g:javascript src='jquery.multiselect.js'/>--}%
    %{--<link  href="<g:resource dir='css' file='jquery.multiselect.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />--}%
    %{--<script src="<g:resource plugin="easygrid" dir='jquery.jqGrid-4.6.0/plugins' file='ui.multiselect.js'/>"  type="test/javascript"></script>--}%
    %{--<link  href="<g:resource plugin="easygrid" dir='jquery.jqGrid-4.6.0/plugins' file='ui.multiselect.css'/>" type="text/css" rel="stylesheet" media="screen, projection" />--}%

    %{--<r:require modules="easygrid-jqgrid-dev,export"/>--}%

    <style type="text/css">
    .ui-jqgrid .ui-jqgrid-htable th     { vertical-align: top; }
    .ui-jqgrid .ui-jqgrid-htable th div { height: 30px; }
    </style>

    <style type="text/css">
    .multiselect {
        width: 450px;
        height: 200px;
    }
    #locales { padding-bottom:16px; }
    div.debug_uiControls {
        margin-top: 16px;
    }
    div.debug_log {
        height: 100px;
        overflow: auto;
        border: 1px solid black;
        padding: 8px;
        margin-top: 16px;
    }
    .example-container {
        overflow:visible;
        width:100%;
    }
    </style>

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

<div style="margin: 2em; overflow: auto;">

    <h1>${params}</h1>


    <div id="usage_groups">
        <form action="" method="get">
        <h2>Group related options</h2>
        <div class="example-container">
            <select id="multiselect_groups" name="multiselect" class="multiselect" multiple="multiple">
                <optgroup label="Australia">
                    <option value="Holden">Holden</option>
                </optgroup><optgroup label="Belgium">
                    <option value="Gillet">Gillet</option>
                </optgroup><optgroup label="Brazil">
                    <option value="Agrale">Agrale</option>
                    <option value="Lobini">Lobini</option>
                    <option value="TAC Motors">TAC Motors</option>
                    <option value="Troller">Troller</option>
                </optgroup><optgroup label="Canada">
                    <option value="Bricklin">Bricklin</option>
                    <option value="Studebaker">Studebaker</option>
                </optgroup>
            </select>
        </div>
        </form>
    </div>
</div>
</body>

<r:script type="text/javascript">
    $(function() {

        var defaultOptions = {
            //availableListPosition: 'bottom',
            moveEffect: 'blind',
            moveEffectOptions: {direction:'vertical'},
            moveEffectSpeed: 'fast'
        };

        var widgets = {
            'simple': $.extend($.extend({}, defaultOptions), {
                sortMethod: 'standard',
                sortable: true
            }),
            'disabled': $.extend({}, defaultOptions),
            'groups': $.extend($.extend({}, defaultOptions), {
                sortMethod: 'standard',
                showEmptyGroups: true,
                sortable: true
            }),
            'dynamic': $.extend({}, defaultOptions)
        };

        $.each(widgets, function(k, i) {
            $('#multiselect_'+k).multiselect(i).on('multiselectChange', function(evt, ui) {
                var values = $.map(ui.optionElements, function(opt) { return $(opt).attr('value'); }).join(', ');
                $('#debug_'+k).prepend( $('<div></div>').text('Multiselect change event! ' + (ui.optionElements.length == $('#multiselect_'+k).find('option').size() ? 'all ' : '') + (ui.optionElements.length + ' value' + (ui.optionElements.length > 1 ? 's were' : ' was')) + ' ' + (ui.selected ? 'selected' : 'deselected') + ' (' + values + ')') );
            }).on('multiselectSearch', function(evt, ui) {
                        $('#debug_'+k).prepend( $('<div></div>').text('Multiselect beforesearch event! searching for "' + ui.term + '"') );
                    }).closest('form').submit(function(evt) {
                        evt.preventDefault(); evt.stopPropagation();

                        $('#debug_'+k).prepend( $('<div></div>').text("Submit query = " + $(this).serialize() ) );

                        return false;
                    });

            $('#btnToggleOriginal_'+k).click(function() {
                var _m = $('#multiselect_'+k);
                if (_m.is(':visible')) {
                    _m.next().toggle().end().toggleClass('uix-multiselect-original').multiselect('refresh');
                } else {
                    _m.toggleClass('uix-multiselect-original').next().toggle();
                }
                return false;
            });
            $('#btnSearch_'+k).click(function() {
                $('#multiselect_'+k).multiselect('search', $('#txtSearch_'+k).val());
            });

        });

        $('#btnGenerate_dynamic').click(function() {
            var start = new Date().getTime();
            var temp = $('<select></select>');
            var count = parseInt($('#txtGenerate_dynamic').val());
            for (var i=0; i<count; i++) {
                temp.append($('<option></option>').val('item'+(i+1)).text("Item " + (i+1)));
            }
            $('#multiselect_dynamic').empty().html(temp.html()).multiselect('refresh', function() {
                var diff = new Date().getTime() - start;
                if (diff > 1000) {
                    diff /= 1000;
                    if (diff > 60) {
                        diff = (diff / 60) + " min";
                    } else {
                        diff += " sec";
                    }
                } else {
                    diff += " ms";
                }
                $('#debug_dynamic').prepend($('<div></div>').text("Generated " + count + " options in " + diff));
            });
        });

    });
</r:script>

</html>
