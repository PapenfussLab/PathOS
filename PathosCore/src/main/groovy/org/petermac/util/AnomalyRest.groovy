/*
 * Copyright (c) 2018. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: andreiseleznev
 */

package org.petermac.util

import groovy.json.JsonOutput
import groovy.util.logging.Log4j
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import net.sf.json.JSON
import org.apache.commons.codec.binary.Base64
import org.apache.log4j.Level
import org.apache.log4j.Logger
import groovy.json.JsonSlurper

import java.util.concurrent.TimeUnit

import static groovyx.net.http.Method.POST

/**
 * Created by andreiseleznev on 18/9/18.
 */
@Log4j
class AnomalyRest {
    static Locator loc = Locator.instance

    //  user-readable responses to running status
    //
    static final String completeMessage = "Complete"
    static final String runningMessage = "Running"
    static final String notFoundMessage = "Not found (not running and not complete)"

    static final String defaultSchema = "pathos_schema"
    //  how often to poll in process
    //
    static final Integer pollInterval = 5
    static final Integer pollTimeOut = 200  //  time out after these many attempts

    static void main( args ) {
        //	Collect and parse command line args
        //
        def cli = new CliBuilder(usage: "Usage: AnomalyRest [options]" +
                        '\n' +
                        'AnomalyRest -a check -t <tag> : check if tag is completed' +
                        '\n' +
                        'AnomalyRest -a process -v <vcf_file.vcf> : upload vcf, poll until complete, load results into pathos' +
                                '\n' +
                        'AnomalyRest -a upload -v <vcf_file.vcf> : upload vcf' +
                        '\n' +
                        'AnomalyRest -a upload -v <vcf_file.vcf> -t <tag>: upload vcf under specified tag' +
                        '\n' +
                        'AnomalyRest -a result -t <tag>  : get response from tag' +
                        '\n' +
                        'AnomalyRest -a running -i <tag> : get running status (only if tag is incomplete)',
                header:
                        'Available options (use -h for help):\n',
                footer: '\nPost a VCF file to Anomaly for annotation. \n')

        //	Options to PipeCleaner
        //
        cli.with
                {
                    h(longOpt: 'help', 'this help message')
                    a(longOpt: 'action', args: 1, required: true, 'Action to perform [process,upload,running,check,result,summary]')
                    t(longOpt: 'tag', args: 1, 'Tag (default if uploading: filename minus extension)')
                    v(longOpt: 'vcf', args: 1, 'VCF file to upload')
                    //o(longOpt: 'output', args: 1, required: false, 'Output file to write to (stdout if not set')
                    s(longOpt: 'schema', args: 1, required: false, 'Schema to use [pathos_schema] (upload action only)')

                    d(longOpt: 'debug', 'Turn on debug logging')
                }

        //  Process options
        //
        def opt = cli.parse(args)
        if (!opt) return
        if (opt.h) {
            cli.usage()
            return
        }

        if(!opt.a in ['process','upload','running','check','response','summary']) {

        }
        //  Debug ?
        //
        if (opt.debug) Logger.getRootLogger().setLevel(Level.DEBUG)

        //  enforce tag for some actions
        //
        if(!opt.tag && !opt.vcf && opt.action != 'list' && opt.action != 'summary') {
            log.fatal("You must supply a tag for this action")
            System.exit(1)
        }

        String resp = ''
        //  upload actions
        //
        if(opt.action == 'upload' || opt.action == 'process') {
            if(!opt.vcf) {
                log.fatal("You must provide VCF file to upload")
                System.exit(1)
            }

            def vcfFile = new File(opt.vcf as String)
            if (!vcfFile.exists()) {
                log.fatal("Provided VCF does not exist: ${opt.vcf}")
                System.exit(1)
            }

            String tag = opt.tag
            if(!opt.tag) {
                tag = formulateTagFromFile(vcfFile)
            }


            String schema = defaultSchema
            if (opt.schema) {
                schema = opt.schema
            }

            log.info("Submitting ${vcfFile.getName()} under tag: ${tag}")

            switch(opt.action) {
                case 'upload':
                    resp = postAnnotateVcf(vcfFile,tag,schema) // just upload
                    break;
                case 'process':
                    resp = uploadAndProcess(vcfFile,tag,schema)  // upload, poll until complete, load into pathos
                    break;
            }
        }

        //  quick parameter check
        //
        if (opt.action in ['check','result','running']) {
            if (! opt.tag) {
                log.error("Please specify a tag")
                System.exit(1)
            }
        }

        //  list all completed
        //
        if(opt.action == 'list') {
            resp = listVcf()
        }

        //  get summary of all uploaded
        //
        if(opt.action == 'summary') {
            resp = summaryVcf()
        }

        //  check specific tag to see if its running, completed, or not found (returns human readable string)
        //
        if(opt.action == 'check') {
            resp = checkVcf(opt.tag)
        }

        //  retrieve result of completed tag
        //
        if(opt.action == 'result') {
            resp = resultVcf(opt.tag)
        }

        //  retrieve running status of running tag
        //
        if(opt.action == 'running') {
            resp = runningVcf(opt.tag)
            if(!resp) {
                println "Not currently running"
            }
        }

        if(!resp) {
            resp = "Not found"
        }

        println resp

    }

    //  formulate a tag name from a File (by taking filename minus extention)
    //
    static String formulateTagFromFile(File vcfFile) {

            String tag = vcfFile.getName()
            int pos = tag.lastIndexOf(".");
            if (pos > 0) {
                tag = tag.substring(0, pos);
            }

        return tag
    }

    //  check if VCF is running, done, or not found and return a human-readable message
    //
    static String checkVcf(String tag) {
        def running = runningVcf(tag)
        if(running) {
            return runningMessage
        }

        def listComplete = resultVcf(tag)
        if (listComplete) {
            return completeMessage
        }

        return notFoundMessage
    }

    //  retrieve the result of a vcf annotation in anomaly
    //
    static String resultVcf(String tag) {
        def url = loc.anomalyUrl + "/v1/annotate/vcf/${tag}"
        return getResponse(url)
    }

    //  retrieve the result of a vcf annotation in anomaly as a Map
    //
    static Map resultVcfMap(String tag) {
        def url = loc.anomalyUrl + "/v1/annotate/vcf/${tag}"
        String resp = getResponse(url)
        Map json = [:]
        try {
            json = new JsonSlurper().parseText(resp)
        } catch (Exception e) {
            log.debug(e.getLocalizedMessage())
        }
        return json
    }


    //  retrieve list of completed vcfs in anomaly
    //  note: this takes a few seconds to populate after a vcf is complete, so don't use this to check completion
    //
    static String listVcf() {
        def url = loc.anomalyUrl + "/v1/annotate/vcf"
        return getResponse(url)
    }

    //  retrieve info of a running vcf in anomaly
    //
    static String runningVcf(String tag) {
        def url = loc.anomalyUrl + "/v1/annotate/vcf/running/${tag}"
        return getResponse(url)
    }

    //  show anomaly summary of vcfs
    //
    static String summaryVcf() {
        def url = loc.anomalyUrl + "/v1/annotate/vcf/summary"
        return getResponse(url)
    }

    /**
     * GET a response from a URL and return it
     * @param url url to get response from
     * @return
     */
    static String getResponse(url) {
        log.debug("GET " + url)
        try {
            URLConnection get = new URL(url).openConnection()
            def getRC = get.getResponseCode();
            log.debug("Response status:" + getRC);
            if(getRC.equals(200)) {
                return(get.getInputStream().getText());
            }
        } catch (Exception e) {
            log.error("Exception: ${e} ${e.getMessage()}")
            return ""
        }
    }

    /**
     * submit a file to Anomaly, poll until completed, and return result as String
     *
     * @param vcfFile File of the VCF
     * @param tag user-defined tag for anomaly
     * @return
     */
    static String uploadAndProcess(File vcfFile, String tag,String schema) {
        String uploadResponse = postAnnotateVcf(vcfFile,tag,schema)
        log.info("Uploaded.")

        pollForResultCompletion(tag)
        return resultVcf(tag)
    }

    /**
     * submit a file to Anomaly, poll until completed, and return result as Map
     * used for debugging
     *
     * @param vcfFile File of the VCF
     * @param tag user-defined tag for anomaly
     * @return
     */
    static Map uploadAndProcessMap(File vcfFile, String tag, String schema) {
        String uploadResponse = postAnnotateVcf(vcfFile,tag,schema)
        pollForResultCompletion(tag)

        return resultVcfMap(tag)
    }

    /**
     * poll a tag for completion - check running status of a job every pollInterval seconds
     * loop until status is complete or notFound
     * @param tag
     * @return 1 if error or 0 if success
     */
    static Integer pollForResultCompletion(tag) {
        String pollAnswer = ''
        Integer attempts = 0

        while (pollAnswer != completeMessage) {
            pollAnswer = checkVcf(tag)
            log.debug("Polling in ${pollInterval} secs, tag is " + pollAnswer)
            TimeUnit.SECONDS.sleep(pollInterval);
            if(pollAnswer == notFoundMessage) {
                log.error("Upload failed, tag is neither running nor complete.")
                return 1
            }
            attempts++
            if(attempts > pollTimeOut) {
                log.error("Timed out after ${attempts} attempts of ${pollInterval} second intervals")
                return 1
            }
        }
        return 0
    }


    /**
     * poll a Set of Tags for completion
     * loop until all tagd are either complete or notFound
     * @param tag
     * @return number of failed tags or 0 if success
     */
    static Integer pollForMultipleResultCompletion(Set<String> tags) {
        Set<String> completeTags = []
        Set<String> incompleteTags = []
        String pollAnswer
        Integer fails = 0
        Integer attempts = 0
        while (!completeTags.equals(tags)) {
            incompleteTags = tags - completeTags
            for(tag in incompleteTags) {
                pollAnswer = checkVcf(tag)
                if (pollAnswer != runningMessage) {
                    completeTags.add(tag)

                    if (pollAnswer != completeMessage) {
                        log.warn("Tag ${tag} not in Anomaly (not running and not complete), check POST response status")
                        fails++
                    }
                }
            }
            incompleteTags = tags - completeTags
            log.debug("Polling in ${pollInterval} secs, processed tags: ${completeTags} running tags: ${incompleteTags}")
            TimeUnit.SECONDS.sleep(pollInterval);
            attempts++
            if(attempts > pollTimeOut) {
                log.error("Timed out after ${attempts} attempts of ${pollInterval} second intervals. At time of timeout, have hailed tags: ${fails} processed tags: ${completeTags} running tags: ${incompleteTags}")
                return fails
            }
        }

        return fails
    }


    /**
     * post a VCF to anomaly annotation
     * @param vcfFile vf file
     * @param tag user-defined tag
     * @return true if submitted approriately , false if error
     */
    static String postAnnotateVcf(File vcfFile, String tag, String schema) {
        //  construct url
        log.info("Upload file with schema: ${schema}")
        
        def url = loc.anomalyUrl + "/v1/annotate/vcf/file?tag=${tag}&schema=${schema}&sources=Vep%2B,MyVariant%2B,Mutalyzer%2B"

        log.debug("posting to URL " + url)
        try {
            def post = setupPOSTConnection(url)
            post.setRequestProperty("Content-Length", "" + vcfFile.text.getBytes().length)
            post.getOutputStream().write(vcfFile.text.getBytes());
            def postRC = post.getResponseCode();
            log.info("POST response status:" + postRC);
            if(postRC.equals(200)) {
                return(post.getInputStream().getText());
            }
        } catch (Exception e) {
            log.error("Exception: ${e} ${e.getMessage()}")
            return ""
        }
    }


    /**
     * set up a URLConnection to POST text to
     * @param url url to post to
     *
     * */
    static URLConnection setupPOSTConnection(String url) {
        def post = new URL(url).openConnection();

        post.setRequestMethod("POST")
        post.setDoOutput(true)
        post.setRequestProperty("Content-Type", "text/plain")
        post.setRequestProperty("Accept", "*/*")
        post.setUseCaches(false);
        return post
    }


}
