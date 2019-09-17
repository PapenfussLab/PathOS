<%@ page import="org.petermac.pathos.curate.VarFilterService; org.petermac.pathos.curate.SeqVariant; org.petermac.pathos.curate.PatSample" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Upload VCF</title>
    <parameter name="hotfix" value="off" />
</head>

<body>

<r:style>
    .seqrunField {
        width: 350px;

    }
    .emailField {
        width: 350px;

    }
    .genemaskField {
        width: 350px;

    }
     #panellist {
        width: 350px;

    }

    #custompanel {
        width: 350px;
    }
    #vcf_table tbody tr td:first-child {
        width: 150px;
    }
    #vcf_table tbody tr {
        height: 35px;
    }

    .dropzoneholder {
        cursor: pointer;

    }

    .save:disabled {
        border: 1px solid #999999;
        background-color: #cccccc;
        color: #666666;
    }

    .warning {
        display:none;
        margin-left:5px;
        color:red;
    }

</r:style>

<div class="content scaffold-list" role="main" style="white-space: nowrap; overflow-x:auto">
    <h1 style="text-align: center;">Upload VCF</h1>
    <div class="outlined-box row col-xs-10 col-xs-offset-1">
        <g:if test="${errorMsg}"> <div class="message" role="status" style="color:red;">Error: ${errorMsg}</div> </g:if>

            <table id="vcf_table" style="margin: auto;">
                <tbody>

                    <tr class="fieldcontain">
                <tr class="fieldcontain">
                    <td>VCF Upload</td>
                    <td class="dropzoneholder">
                        <g:render template="/_dropzone/dropzone" model="[submitToggling:'true',uploadsHiddenInputId:'uploadedFilesJson',controllerName:'vcfUpload']"/>
                    </td>
                </tr>

        </tbody>
        </table>
    <g:form action="upload" name='vcfUploadForm' id='vcfUploadForm' enctype="multipart/form-data" useToken="true" >
    <br/><br/>
        <div class=" row col-xs-10 col-xs-offset-1">



                <table id="vcf_table" style="margin: auto;">
                <tbody>
                    <tr class="fieldcontain">
                        <td>Seqrun</td>
                        <td>
                        <g:textField name="seqrun" class="seqrunField" value="${params?.seqrun}"/>


                        </td>
                        <td width="260px;"><span id="new_seqrun_warning" class="warning">Seqrun does not exist and will be created</span></td>
                    </tr>

                    <tr class="fieldcontain">
                        <td>Email</td>
                        <td>
                        <g:textField name="email" class="emailField" value="${params?.email?:defaultEmail}" />
                        </td>
                    </tr>

                    <tr class="fieldcontain">
                        <td for="panellist" >Panel</td>
                        <td>
                        <g:select name="panellist" noSelection="['':'Enter seqrun to select panel...']" from="${panelList?:[]}" value="${params?.panellist?:''}">
                        </g:select>
                        </td>
                    </tr>

                    <tr class="fieldcontain" id="custompanelsection"  style="display:none;">
                        <td for="custompanel" >Specify Panel</td>
                        <td>
                            <g:textField name="custompanel" class="custompanel" value="${params?.custompanel?:""}" />
                        <td width="260px;"><span id="panel_warning" class="warning">&nbsp;Please enter an existing panel</span></td>
                        </td>
                    </tr>
                <tr class="fieldcontain">
                    <td>Custom Gene Mask</td>
                    <td>
                        <g:textField name="genemask" class="genemaskField" value="${params?.genemask?:""}" />
                    </td>
                </tr>
                    <tr class="fieldcontain">
                        <td>Apply filters</td>
                        <td>
                        <g:checkBox name="filterFlag" value="${params?.filter?:'checked'}" />
                        </td>
                    </tr>


                    <label></label>
                    <%--<tr class="fieldcontain">
                        <td>VCF Upload</td>
                        <td>
                        <input type="file"  id="vcfUpload" name="vcfUpload" multiple="multiple" />
                        </td>
                    </tr>--%>

                </tbody>
                </table>
                <br/>


            <input type="hidden" id="uploadedFilesJson" name="uploadedFilesJson"/>

                <div class="fieldcontain" style="text-align: center;">
                    <g:submitButton name="process" class="save" value="Process" disabled="true"  onclick="return validatePanels();"/>
                </div>
             <br/>


        </div>

    </g:form>
</div>
<r:script>
    var isNewSeqrun = false


    function validatePanels() {
        if ( $("#panellist").val() == 'Specify panel' && $("#custompanel").val() == '' ) {
            alert("Please enter a panel")
            return false
        }

        if ( $("#panellist").val() ==  '') {
            alert ("You must choose a panel")
            return false
        }

        var regexp = "^[-._A-Za-z0-9]+$"
        if (isNewSeqrun && !$("#seqrun").val().match(regexp)) {
            alert ("Seqrun name can only have letters, numbers, dashes and underscores")
            return false
        }
        var customManifest = $("#custompanel").val()

        if ( $("#panellist").val() == 'Specify panel' ) {
            //  we have a custom panel: ajax call a panel validation function that will submit form on sucess
            var response = ${remoteFunction(action: 'checkPanelExists', params: '{manifest:customManifest}', onSuccess: 'submitIfValidPanel(data)')}
    } else {
        return true //  we've chosen for a dropdown
    }
    return false

}

$("#custompanel").change(function() {
//if ( $("#panellist").val() == 'Specify panel')
    var customManifest = $("#custompanel").val()
    ${remoteFunction(action: 'checkPanelExists', params: '{manifest:customManifest}', onSuccess: 'showPanelWarning(data)')}
})

function showPanelWarning(data) {
    //todo on change dropdown hide it on show call remotefunction if val is not empty
     if(data == "" || data == "null") {
        $('#panel_warning').css('display','table-row');
     } else {
        $('#panel_warning').css('display','none');
     }
}

function submitIfValidPanel(data) {
    if(data != "" && data != "null") {
        //  append a 'process' input field to mimick submit button to confirm valid form submission  before submission
        //
        var input = $("<input>").attr("type", "hidden").attr("name", "process").val("process");
        $('#vcfUploadForm').append($(input));
        $('#vcfUploadForm').submit();
    } else {
        alert("This panel does not exist, please enter an existing panel")
    }
}

/**
if user selects specifying custom panel, let them
**/
$("#panellist").change(function()  {
    if( $("#panellist").val() == 'Specify panel') {
                $('#custompanelsection').css('display','table-row');

                if ( $("#custompanel").val() != '') {
                    var customManifest = $("#custompanel").val()
                    ${remoteFunction(action: 'checkPanelExists', params: '{manifest:customManifest}', onSuccess: 'showPanelWarning(data)')}
                }
    } else {
       //hide
       $('#custompanelsection').css('display','none');
    }
});

/**
* on seqrun change, we update our panel list and autofill the most numerous panel with AJAX
**/
$("#seqrun").change(function() {
   var sr = $('#seqrun').val()

                ${remoteFunction(action:'updatePanelsForSeqrun', params: '{seqrunName: sr}', update: [success: 'panellist'] )}         // populate the select box with the seqrun's panels
                ${remoteFunction(action:'suggestPanelForSeqrun', params: '{seqrunName: sr}', onSuccess: 'suggestPanel(data)')}

	});

    /**
    * called by suggestPanelForSeqrun remote function
    * pre-fill seqrun dropdown with most populated panel
    **/
	function suggestPanel(data) {

		  if(data == 'noseqrun') {  // no seqrun exists, we are making a new one
		        data = ''
		         $('#new_seqrun_warning').css('display','inline');
                 isNewSeqrun = true
		  } else {
              $('#new_seqrun_warning').css('display','none');
              isNewSeqrun = false
		  }

		  if(data != '') { // either no seqrun exists or  seqrun has no samples/panels
		        $("#panellist").val(data)
		  }


		  //}

		  // show or hide warning
		  // disabling this for now, currently we do not create seqrun
		  /*
                 if(data.length == 0) {
                     $('#new_seqrun_warning').css('display','inline');
                     isNewSeqrun = true
                 } else {
                    //hide
                     $('#new_seqrun_warning').css('display','none');
                     isNewSeqrun = false
                 }*/
	}

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



    });

</r:script>
</body>
</html>
