%{--
  - Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
  -
  - Organisation: Peter MacCallum Cancer Centre
  - Author: seleznev andrei
  --}%

<%--
  Created by IntelliJ IDEA.
  User: seleznev andrei
  Date: 30/09/2015
  Time: 2:08 PM
--%>


<%@ page import="org.petermac.pathos.curate.SeqVariant" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'seqVariant.label', default: 'SeqVariant')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>

    %{--Javascript Files--}%
    <g:javascript src="quasipartikel/jquery.min.js" />
    <g:javascript src="quasipartikel/jquery-ui.min.js" />

    <g:javascript src="quasipartikel/ui.multiselect.js" />
    <g:javascript src="tag-it.js" />
    <g:javascript src='jquery/jquery.jgrowl.js' plugin='spring-security-ui'/>

    <link rel="stylesheet" href="${resource(dir: 'css/jquery-ui-1.11.0.custom', file: 'jquery-ui.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'css/tagit', file: 'jquery.tagit.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'css/tagit', file: 'tagit.ui-zendesk.css')}" type="text/css">


    <style type='text/css' media='screen'>
    .annotable tr td { line-height: normal; vertical-align: top; }
    .annotable .fieldcontain { overflow: scroll; }
    .annotable td { word-wrap: break-word }
    .annotable  tr:hover { background: none; }
    .annoShowHideMsg { text-decoration: underline; color: blue; cursor: pointer; }

    .iconminus {
        background-position: -48px -128px;
        background-image: url("images/ui-icons_72a7cf_256x240.png");
        height: 16px;
        width: 16px;
        background-repeat: no-repeat;
       /* display: inline;
        overflow: hidden;
        text-indent: -99999px;*/

    }
    .catheading {
        color: #07447c;
        font-weight: normal;
        margin: 0.8em 1em 0.3em;
        padding: 0 0.25em;
        font-size: 1em;
        cursor: pointer;
        text-decoration: underline;
    }
    .ui-dialog-content{
        background:white !important;
    }

    .ui-dialog-buttonpane{
        background:white !important;

    }

    .ui-dialog-titlebar {
        background:white !important;
    }
    .ui-dialog { color: lightslategrey;
        border: 1px solid;
        font-size: 10px;
        padding: 0.25em;
    }

    /*
     * CSS applied dynamically when tags are shown/hidden (important is to override default):
     */
    .tagsShown {
        background-color: #dee7f8 !important;
    }
    .tagsShown:hover {
        background-color: #bbcef1 !important;
    }
    .tagsHidden {
        background-color: #d3d3d3 !important;
    }
    .tagsHidden:hover {
        background-color: darkgrey !important;
    }

    #annotags {
        border: 0px solid;
        border-color: #d3d3d3;
        float: right;
       /* height: 100px;*/
        margin-right: 50px;
        position: relative;
        width: 350px;
        z-index: 10;
    }

        #ol-container-main {
            width: 600px;
            word-wrap: break-word;
            overflow: auto;
        }

    </style>
</head>
<body>
<a href="#show-seqVariant" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="allsvlist" action="allsvlist" controller="seqVariant"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
    </ul>
</div>

    <h1 style="margin-left:8px;">Show Annotation For ${hgvsg}</h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>


<form id="tagform">
    <ul id="annotags">
        <g:each in="${allTags}" var="thisTag">
            <li class="liclass_${thisTag.replaceAll(' ','_')} tagsShown">${thisTag}</li>  <%-- need a class with no spaces so we can change BG - Have to ReplaceAll over here--%>
        </g:each>
    </ul>

</form>

<div id="list-seqVariant" class="content scaffold-list" role="main" >


    <ol class="property-list" id="ol-container-main">

        <g:each in="${annoByCategory}" var="annoByCat">
            <div style="margin-top:10px;">
                <span class="catheading" id="catheading_anno_${annoByCat.key}" onclick="hideshow('anno_${annoByCat.key}')">${annoByCat.key}</span><br/>
            <div id="anno_${annoByCat.key}" style="display:inline;">
            <g:each in="${annoByCat.value}" var="item">

                <li class="fieldcontain ${item.tags}">
                    <span id="${item.colName}" class="property-label" style='cursor: pointer' onclick="showmeta('annometa_${item.colName}')">${item.displayName}</span>

                    <div id='annometa_${item.colName}'>Display Name: <b>${item.displayName}</b><br/>Column Name:<b>${item.colName}</b><br/>Description: ${item.metadata.description}<br/>Datasource: ${item.metadata.annoDataSource}<br/>Category: ${item.metadata.category}</div>

                    <span class="property-value" aria-labelledby="seqrun-label">
                        ${item.colValue}
                    </span>
                </li>

              </g:each>
            </div>

        </div>
        </g:each>

     </ol>

<r:script>
    var sampleTags = ['c++', 'java', 'php', 'coldfusion', 'javascript', 'asp', 'ruby', 'python', 'c', 'scala', 'groovy', 'haskell', 'perl', 'erlang', 'apl', 'cobol', 'go', 'lua'];

    var toggleEvent = function (text) {

        //this replaces all spaces by underscores - easier than a replaceAll regexp
        //
        var className = text.split(' ').join('_');

        if($(".liclass_" + className).hasClass('tagsShown')) {
            $("." + className).hide(100);

            $("." + className).addClass("hiddenBy"+className);   //this class keeps track of whether a user wants this class hidden

            //$(".hideBy" + className).css("display","none","important")

            $(".liclass_" + className).removeClass("tagsShown");
            $(".liclass_" + className).addClass("tagsHidden");

        } else {

            $("." + className).removeClass("hiddenBy"+className);

            //only show elements if they do not have a tag that says the yshould be hidden
            //if shouldBeHidden is true, it means the user has hidden another tag that this element has, and we should not
            //show it yet
            $("." + className).each(function(i, obj) {
                var shouldBeHidden = false;
                var classList = $(this).attr('class').split(/\s+/); //get all classes of this element
                for (i = 0; i < classList.length; i++) {
                    if(classList[i].length > 0){
                        var thisClass = classList[i];
                        if (thisClass.substring(0, 8) == "hiddenBy") {  //this class has a tag that the user wants to hide
                            shouldBeHidden = true
                        }
                    }
                }
                if(!shouldBeHidden) {
                    $(this).show(10)
                }
            });

            $(".liclass_" + className).addClass("tagsShown");
            $(".liclass_" + className).removeClass("tagsHidden");

        }

    };

    var annotags = $('#annotags');

    $('#annotags').tagit({
        readOnly: true,
        onTagClicked: function (evt, ui) {
            toggleEvent(annotags.tagit('tagLabel', ui.tag));
        }
    });


    $(document).ready(function () {

        //dynamic array to store how many times a var had been hidden


        //define all metadata div as dialog boxes
        //
        $("[id^=annometa]").dialog({  autoOpen: false, title: "Column Metadata" });
        //define tags

    });

    //  hide or show an element
    //
    function hideshow(element) {

        var which = document.getElementById(element)
        if (!document.getElementById)
            return
        if (which.style.display == "inline") {
            which.style.display = "none"
            $("#catheading_"+element).css({"color":"#A9A9A9"})

        }
        else {
            which.style.display = "inline"
            $("#catheading_"+element).css({"color":"#07447c"})

        }

    }

    //  show a metadata dialog box
    //
    function showmeta(which) {
        $('#' + which).dialog("open")
    }

</r:script>
<%--%>


    <%--  <g:form>
          <fieldset class="buttons">
              <g:hiddenField name="id" value="${seqVariantInstance?.id}" />
              <g:link class="edit" action="edit" id="${seqVariantInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
              <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
          </fieldset>
      </g:form> --%>
</div>
</body>
</html>
