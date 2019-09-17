%{--
  - Copyright (c) 2018. PathOS Variant Curation System. All rights reserved.
  -
  - Organisation: Peter MacCallum Cancer Centre
  - Author: andreiseleznev
  -
  --}%

%{--
 INSTRUCTIONS

 Dropzone upload gsp. Include this template in any field to provide a file uploader.
 Submits to controller method 'fileupload' with file under param name 'fileupload'
 have your controller fileupload() method call UploadService with your CommonsMultipartFile from request.getFile(paramupload),
 a String upload destination, and a Closure for server-side validation. from it, return JSON
 'uploadedTo' the upload dest (on success) or 'errors' the list of errors

 see VcfUploadController fileupload() for an example

 Uses dropzone JS, see https://www.dropzonejs.com/ for details.

 Files are uploaded one-by-one, even when multiple selected.

 --}%
<script>
    /**
     * configure the uploader here
     **/

    //  blank if not used. ID of hidden input to append file uploads to.
    //
    var uploadsHiddenInputId = "${uploadsHiddenInputId?:''}";  //

    //  submit toggling
    //  if true, submit button on the page will have disabled class removed upon successful upload and
    //  added during pending upload (you probably want to add disabled='true' to it if you're using this)
    //
    var submitToggling = ${submitToggling?:'false'};
</script>

<script src="${resource(dir: 'js', file:'dropzone.js')}"></script>
<link rel="stylesheet" href="${resource(dir: 'css', file:'dropzone.css')}">

<g:form url="[controller:controllerName, action:'fileupload']" class="dropzone" id="filesUpload">
<div class="fallback">
    <input name="file" id="filesUpload" name="filesUpload"  type="file" multiple />
</div>
</g:form>

<script>
Dropzone.options.filesUpload = {
  paramName: "fileupload", // The name that will be used to transfer the file. check params.(paramName) to process upload.
  maxFilesize: 6,
  createImageThumbnail: false,
  acceptedFiles: '.vcf',

    init: function() {
        var hasFile = false; // track if we've uploaded a file at all getAcceptedFiles not always accurate
        this.on("success", function(file, response) {
            var args = Array.prototype.slice.call(arguments);

            try {
                var parsed = jQuery.parseJSON(response);

            } catch(e) {
                //did not return JSON, throw an error
                console.log ("Error: uploaded but could not pass ")
            }
            var uploadpath = parsed.uploadedTo; //  this is the path of the newly uploaded file

            if(uploadsHiddenInputId) {  //if we have a hidden inout field to populate with an upload list: do it
                var currentuploadList = $("#"+uploadsHiddenInputId).val();
                if (!currentuploadList) {
                    var currentuploadListArr = new Array();
                } else
                {
                    currentuploadListArr = $.parseJSON(currentuploadList)
                }
                currentuploadListArr.push(uploadpath);
                $("#"+uploadsHiddenInputId).val(JSON.stringify(currentuploadListArr));
            }

            hasFile = true

         });
        this.on("queuecomplete", function (file) {
            //  below code enables/disables a submit button (enabled only when nothing being processes and an upload
            //  is successful), remove it if uneeded
            //
            if (submitToggling == true && hasFile == true) {
                    $(':input[type="submit"]').prop('disabled', false);
                }


        });
        this.on("error", function (file, response) {
            try {
                var parsed = jQuery.parseJSON(response);
                console.log ("Upload Error: ")
                console.log (parsed.errors)
                var errorMessage = "Failed to upload: " + parsed.errors
                if(parsed.uploadPath && parsed.uploadPath != '') {
                    console.log ("Warning! Validation failed but a file exists already at the attempted upload path")
                    console.log (parsed.uploadPath)
                }
                alert(errorMessage);

                //if we already have a succcessfully uploaded file, reenable submit button
                //
                if (hasFile == true && submitToggling == true) {
                    $(':input[type="submit"]').prop('disabled', false);
                }

            } catch(e) {
                //did not return JSON, clientside error
                alert(response);
            }

            $(file.previewElement).find('.dz-error-message').text("Failed to validate");
        });

        this.on("addedfile", function (file) {
            // disable your submit button while proessing takes place
            //
            if(submitToggling == true) {
                $(':input[type="submit"]').prop('disabled', true);
            }
        });
    }
};



</script>