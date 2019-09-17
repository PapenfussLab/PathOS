package org.petermac.pathos.curate

import grails.converters.JSON
import org.apache.commons.io.FileUtils
import org.springframework.web.multipart.commons.CommonsMultipartFile

/**
 * Service to perform file uploads
 * Expected to be called from controllers where dropzone js used for upload
 *
 * Author: Andrei Seleznev
 */
class UploaderService {

    /**
     * upload a single file
     *
     * @param f CommonsMultiPartFile (output of request.getFile(uploadparameter))
     * @param uploadPath path for new uploaded file
     * @param validateFunction closure to validate, expected to return Map with return.errors if any errors
     * @return Map, with List errors a list of errors, Stirng uploadPath destination of uploaded file
     */
    UploadResult upload(CommonsMultipartFile f, String uploadPath, Closure validateFunction) {
        File outFile = new File(uploadPath)

        f.transferTo(outFile)
        def validation = validateFunction(outFile)  //  expect a map of [errors:[List of any errors]]

        if(validation.errors) {
            FileUtils.deleteQuietly(outFile);   //  delete uploaded file if failed validation
        }

        UploadResult uploadResult = new UploadResult(errors:validation.errors,uploadFile:outFile)
        return uploadResult
    }

    /**
     * an upload response for rendering back to the dropzone uploader
     * a controller should call render on the result of this
     *
     */
    HashMap uploadResponse(UploadResult upload) {

        ArrayList<String>  errors = upload.errors


        //   check for errors and set status apporpiately
        //
        String status = "200"
        if(errors) {
            status = "400"
        }

        //  set uploadPath only if file exists
        //
        String uploadPath = ''
        if(upload.uploadFile.exists()) {
            uploadPath = upload.uploadFile.getAbsolutePath()
        }

        return new HashMap(contentType: "text/json", status: status, text: [uploadedTo: uploadPath, errors: errors ] as JSON)
    }
}

/**
 * result of an attempted upload of a single file
 */
class UploadResult {
    ArrayList errors    //  list of validation errors
    File uploadFile    //   File to attempted upload - File will !exist if file is not uploaded
}