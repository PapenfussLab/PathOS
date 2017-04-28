%{--
  - Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
  -
  - Organisation: Peter MacCallum Cancer Centre
  - Author: seleznev andrei
  --}%

<%--
  Created by IntelliJ IDEA.
  User: seleznev andrei
  Date: 16/09/2015
  Time: 1:30 PM
--%>

<%@ page import="org.petermac.pathos.curate.VarFilterService; org.petermac.pathos.curate.SeqVariant; org.petermac.pathos.curate.PatSample" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Upload VCF</title>
    <tooltip:resources/>
    <g:javascript src="quasipartikel/jquery.min.js" />
</head>

<body>
<div class="nav" role="navigation">
    <%--<ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" controller="admin" action="filter" params="${params}">Apply Filtering</g:link></li>
        <li><g:link class="create" controller="admin" action="reclassify" params="${params}">Re-classify Variants</g:link></li>
    </ul>--%>
</div>

<div id="admin-stats" class="content scaffold-list" role="main"
     style="white-space: nowrap; overflow-x:auto">

    <h1>Upload VCF</h1>

    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>


    <g:form action="saveupload" name='vcfUploadForm' enctype="multipart/form-data" useToken="true">
        <div class="dialog">

            <br/>

            <table>

                <tbody>
                <fieldset class="form">


                    <div class="fieldcontain">
                        <label>Seqrun</label>
                        <g:textField name="seqrun" value="${userparams?.seqrun}"/>
                    </div><br/>

                    <div class="fieldcontain">
                        <label>Panel</label>
                        <g:textField name="panel" value="${userparams?.panelName}" />
                    </div><br/>

                    <div class="fieldcontain">
                        <label>Apply filters</label>
                        <g:checkBox name="filterFlag" value="${userparams?.filter}" />
                    </div>


                    <label></label>
                    <div class="fieldcontain">
                        <label>VCF Upload</label>
                        <input type="file"  id="vcfUpload" name="vcfUpload" />
                    </div>
                    <br/>

                    <div class="fieldcontain">
                        <label>Environment</label>
                        <label style="text-align: left;">${env}</label>
                    </div>
                    <div class="fieldcontain">
                        <label>Command to Execute</label>
                        <label style="text-align: left"><div id="shellcommand" style="font-family: monospace;"></div></label>
                    </div>
                </span>
                </fieldset>

                </tbody>

                <fieldset class="buttons">
                    <div class="fieldcontain">
                        <g:submitButton name="create" class="save" value="${message(code: 'default.button.Upload.label', default: 'Upload')}" />
                </fieldset>
            </table>
        </div>

    </g:form>
</div>
<r:script>

    //binds to onchange event of your input field
    $('#vcfUpload').bind('change', function() {
        //check extention
        var filename = $("#vcfUpload").val();

        var extension = filename.replace(/^.*\./, '');


        if (extension == filename) {
            extension = '';
        } else {
             extension = extension.toLowerCase();
        }
        if (extension != 'vcf') {
            alert("The uploaded file must be a VCF file and have a .vcf extension")
            $('#vcfUpload').val('');
        }


        //check of the file is over 10MB
        if (this.files[0].size > 10000000) {
            alert("The uploaded file is too big. Max file size is 10MB")
            $('#vcfUpload').val('');

        }

        showShellCommand()

    });

    function showShellCommand()
    {
      panelval =  $('#panel').val()
      if (!panelval) {
            panelval = "NoPanel"
      }

      if( $('#panel').val() )
      {
            shellcommand =  "VcfLoader --rdb ${env} --seqrun " + $('#seqrun').val() + " --panel " + $('#panel').val() + " in.vcf"
      } else {
            shellcommand =  "VcfLoader --rdb ${env} --seqrun " + $('#seqrun').val() + " in.vcf"

      }
      $('#shellcommand').text(shellcommand)
    }

    $( document ).ready(function() {
        showShellCommand()
    });

    $('#seqsamplename').bind('change', function() {
        showShellCommand()
    });

    $('#seqrun').bind('change', function() {
        showShellCommand()
    });

    $('#panel').bind('change', function() {
        showShellCommand()
    });

</r:script>
</body>
</html>
