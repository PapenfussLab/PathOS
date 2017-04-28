/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.pathos.pipeline

import groovy.util.logging.Log4j
import groovy.json.JsonSlurper
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import org.apache.commons.codec.binary.Base64

import static groovyx.net.http.Method.*

/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 30/04/13
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */

@Log4j
class Mutalyzer
{
    static JsonSlurper slurper = new JsonSlurper()

    private static          def         baseURL   = 'https://mutalyzer.nl'
    private static          def         mutURL    = baseURL + '/json/'
    private static          String      proxyHost = null
    private static          Integer     proxyPort = null
    private static          Integer     waitTime  = 180000    //  3 Minute default wait time for batch
    private static          boolean     insecure  = false     //  allow insecure https connections

    /**
     * Constructor:
     * This function was designed to bypass the
     * problem of proving system properties variables from
     * the command line for unit testing.
     *
     * This function writes the proxyHost, proxyPort and waitTime variables
     * The function first looks if the environment variables exist if not it looks at
     * the System properties for the proxy configuration. If non of the exist the
     * proxy will be set as default.
     *
     * For testing Mutalyzer, 2 environment variables can be set
     * 1) PATHOS_MUTALYZER_PROXY
     * 2) PATHOS_MUTALYZER_WAITTIME
     *
     * If cntlm is configured then PATHOS_MUTALYZER_PROXY=http://127.0.0.1:3128
     * and PATHOS_MUTALYZER_WAITTIME=1000 to speed the test
     *
     */
    Mutalyzer( String mutHost = 'https://mutalyzer.nl' )
    {
        //  Allow insecure https connections if within PeterMac
        //
        if ( mutHost.endsWith('.petermac.org.au')) insecure = true

        //  Set mutalyzer host
        //
        baseURL = mutHost
        mutURL  = baseURL + '/json/'

        //  Get proxy and waittime
        //
        List proxyEnv = getProxyConfFromEnv()

        if( proxyEnv[0] )
        {
            //we have a proxy - set it for this instance
            proxyHost = proxyEnv[1]
            proxyPort = proxyEnv[2] as Integer
        }

        if ( ! proxyPort || ! proxyHost )
            log.info( "Mutalyzer: No Proxy set")
        else
            log.info( "Mutalyzer: Proxy set host=${proxyHost} port=${proxyPort}")
    }


    /**
     * Todo: this needs to be removed and waittime and proxy passed through to constructor
     *
     * This function is called by the constructor to read the content of
     *  PATHOS_MUTALYZER_PROXY and PATHOS_MUTALYZER_WAITTIME
     *
     *  It checks for the pattern http://<Host>:<Port>
     *
     * @return a list with
     * 1) boolean to indicate if the proxy is valid or not
     * 2) String that contains the host
     * 3) String that contains the port
     */
    private static List getProxyConfFromEnv()
    {
        String  Host  = ""
        String  Port  = ""
        List    fullProxy = []

        //  Set wait time for Mutalyzer batch() call to prevent early http call
        //
        String waitTimeStr = System.getenv('PATHOS_MUTALYZER_WAITTIME')
        if ( waitTimeStr )
        {
            waitTime = waitTimeStr as Integer
            log.debug( "Mutalyzer wait time changed to (${waitTime/1000.0} s.)")
        }

        //  Look for explicit environment variable PATHOS_MUTALYZER_PROXY for proxy info
        //
        String httpsProxy = System.getenv('PATHOS_MUTALYZER_PROXY')

        if( httpsProxy == null )
            return [ false, "", "" ]

        if( httpsProxy.startsWith( 'http://' ))
            httpsProxy = httpsProxy.replaceFirst( 'http://', '' )

        // This is is for a http://host:port pattern
        //
        if( httpsProxy.contains(':'))
        {
            fullProxy = httpsProxy.split(":")
            assert fullProxy.size() == 2
            Host = fullProxy[0]
            Port = fullProxy[1]
        }
        else
            log.error( "Unexpected proxy string ${httpsProxy}")

        if( Host == "" || Port == "" ) return [false,"",""]

        return [true, Host, Port]
    }

    /**
     * Check server is up
     * To test use % curl 'https://mutalyzer.nl/json/ping'
     *
     * @return      true if server available
     */
    public static Boolean ping()
    {
        def http = new HTTPBuilder( baseURL )
        if ( insecure ) http.ignoreSSLIssues()

        //  Set proxy if needed
        //
        if ( proxyHost && proxyPort ) {  //  this is not called statically - these will be set in constructor
            http.setProxy(proxyHost, proxyPort.toInteger(), 'http')
        }

        def url = mutURL + "ping"
        def res
        try
        {
            StringReader rdr = http.get( path: url, contentType: ContentType.TEXT ) as StringReader
            res = rdr.readLine()
            rdr.close()
        }
        catch( Exception ex )
        {
            log.fatal( "Exception when trying to connect to ${baseURL}: " + ex )
            return false
        }

        return res == '"pong"'
    }

    /**
     * Get software version details
     * To test use % curl 'https://mutalyzer.nl/json/info'
     *
     * @return      Release date string: "26 Sep 2014"
     */
    static String info()
    {
        //  Expected Response
        //
        //{"announcement": "Server update successful",
        // "versionParts": ["2", "0", "0"],
        // "serverName": "res-muta-app01",
        // "nomenclatureVersion": "2.0",
        // "announcementUrl": "https://humgenprojects.lumc.nl/trac/mutalyzer/wiki/News/2014-09-26-server-update",
        // "contactEmail": "humgen@lumc.nl",
        // "version": "2.0.0",
        // "releaseDate": "26 Sep 2014",
        // "nomenclatureVersionParts": ["2", "0"]}
        //

        def url = mutURL + "info"
        def ret = url.toURL()

        def res = slurper.parseText(ret.text)

        log.info(res)

        return res.contactEmail
    }

    /**
     * Convert chromosome to NC transcript
     * To test use % curl 'https://mutalyzer.nl/json/chromAccession?build=hg19;name=chrX'
     *
     * @param chr
     * @return
     */
    static String chromAccession( String chr )
    {
        if ( ! chr.startsWith('chr'))
            chr = 'chr' + chr

        def url = mutURL + "chromAccession?build=hg19;name=${chr}"
        def ret = url.toURL()
        return ret.text.replaceAll('"','')
    }

    static List getTranscriptsByGeneName( String gene )
    {
        def url = mutURL + "getTranscriptsByGeneName?build=hg19;name=${gene}"
        def ret = url.toURL()
        def res = slurper.parseText(ret.text)
        return res
//        return res.getTranscriptsByGeneNameResponse.getTranscriptsByGeneNameResult.string
    }

    /**
     * Convert a transcript and c.xxx variant to protein, verified cds and transcript
     * To test use: curl 'https://mutalyzer.nl/json/runMutalyzer?variant=NM_007294.3:c.117_118del'
     *
     * @param transcript
     * @param hgvsc
     * @return
     */
    static Map runMutalyzer( String transcript, String hgvsc )
    {
        runMutalyzer( transcript + ':' + hgvsc )
    }

    static Map runMutalyzer( String variant )
    {
        def url = mutURL + "runMutalyzer?variant=${variant}"

        def ret = url.toURL()

        def res = slurper.parseText(ret.text)
        def base = res

        log.info base
        Map map = [:]

        //  Parse response
        //
        //  transcriptDescriptions=[NM_007294(BRCA1_v001):c.117_118del]
        //  proteinDescriptions=[NM_007294(BRCA1_i001):p.(Cys39*)]
        //  referenceId=NM_007294.3

        String tok = base.proteinDescriptions[0]
        def tokens = tok.split( ':' )
        assert tokens.size() == 2
        if ( tokens.size() == 2 )
            map << [ 'hgvsp' : tokens[1] ]

        //  Parse HGVSc string
        //
        tok = base.transcriptDescriptions[0]
        tokens = tok.split( ':' )
        assert tokens.size() == 2
        if ( tokens.size() == 2 )
            map << [ 'hgvsc' : tokens[1] ]

        //  Extract transcript
        //
        map << [ 'transcript' : base.sourceId ]

        return map
    }

    /**
     * Convert a transcript and variant to genomic cooords
     * To test use: curl 'https://mutalyzer.nl/json/numberConversion?build=hg19;variant=NC_000017.10:g.41245466G>A'
     *
     * @param   variant        Note transcript MUST have a version number
     * @param   assembly       Genome build eg hg19,hg18
     *
     * @return  List of hgvsg format eg NC_000017.10:g.41245466G>A
     */
    static List numberConversion( String variant, String assembly='hg19' )
    {
        def url = mutURL + "numberConversion?build=${assembly};variant=${variant}"
        log.debug( "Before toURL() ${url}")
        def ret = url.toURL()
        log.debug( "After toURL() ${ret}")
        def res = slurper.parseText(ret.text)

        return res
    }


    /**
     * Submit a batch job to Mutalyzer server
     * To test use % curl -d 'process=SyntaxChecker' -d 'argument=hg19' -d "data=$(echo 'NM_003002.2:c.274delG' | base64)" 'https://mutalyzer.nl/json/submitBatchJob'
     * Or % curl -d 'process=NameChecker' -d 'argument=hg19' -d "data=$(echo 'NM_001093772.1:c.1656_1658del,NM_001276760.1:c.673-36G>C,NM_005228.3:c.2239_2250del' | base64)" 'https://mutalyzer.nl/json/submitBatchJob'
     *
     * @param data      Comma separated variant String
     * @param process   Type of request SyntaxChecker, NameChecker, PositionConverter, SNPConverter
     * @return          Job number string from server
     */
    static String submitBatch( String data, String process, String assembly = 'hg19' )
    {
        String job = ''

        //   Setup HTTPBuilder with proxy and destination host
        //
        def http = new HTTPBuilder( baseURL )
        if ( insecure ) http.ignoreSSLIssues()

        //  Called statically so need to get proxy settings first, if any
        //
        List proxyEnv = getProxyConfFromEnv()
        if ( proxyEnv.size() > 2 && proxyEnv[0] )
                http.setProxy( proxyEnv[1], proxyEnv[2].toInteger(), 'http')

        //  Submit a POST request as URL encoded data in body: Expect a JSON response
        //
        http.request( POST, ContentType.TEXT )
        {
            uri.path = '/json/submitBatchJob'

            send ContentType.URLENC, [process: process, argument: assembly, data: Base64.encodeBase64String(data as byte[]) ]

            response.success =
            {
                resp, json ->
                    job = json.text
                    log.debug( "Success! ${resp.statusLine} ${job}" )
                    assert resp.status == 200
            }

            response.failure =
            {
                resp, json ->
                    log.error( "Request failed with status ${resp.statusLine} ${json}" )
                    assert resp.status != 200
            }
        }

        return job.replaceAll( '"', '')
    }

    /**
     * Query batch for number of batch variants remaining
     * To test use % curl 'https://mutalyzer.nl/json/monitorBatchJob?job_id=bcf4380b-0fda-4b42-a6e0-fba06e14e6a4'
     *
     * @param job
     * @return      Number of batch variants remaining
     */
    static Integer monitorBatch( String job )
    {
        def http = new HTTPBuilder( baseURL )
        if ( insecure ) http.ignoreSSLIssues()

        //  Called statically so need to get proxy settings first, if any
        //
        List proxyEnv = getProxyConfFromEnv()
        if ( proxyEnv.size() > 2 && proxyEnv[0] )
            http.setProxy( proxyEnv[1], proxyEnv[2].toInteger(), 'http')


        String url = baseURL+'/json/monitorBatchJob?job_id='+job
        StringReader rdr = http.get( path: '/json/monitorBatchJob', contentType: ContentType.TEXT, query: [ job_id: job] ) as StringReader
        String res = rdr.readLine()
        rdr.close()
        log.debug( "URL: ${url} Ret: ${res}")

        if ( ! res.isInteger())
        {
            log.fatal( "URL: ${url} doesn't return Integer(): ${res}")
            return null
        }
        return res as Integer
    }

    /**
     * Get batch results for a job
     * To test use % curl 'https://mutalyzer.nl/batch-job-result/batch-job-80faf83d-7bb0-463f-bbb7-404049e395a8.txt'
     *
     * @param   job     Job identifier String
     * @return          Entire file
     */
    static String getBatch( String job )
    {
        def url = baseURL + "/batch-job-result/batch-job-${job}.txt"
        log.debug( "About to retrieve ${url}")

        def http = new HTTPBuilder( baseURL )
        if ( insecure ) http.ignoreSSLIssues()

        //  Called statically so need to get proxy settings first, if any
        //
        List proxyEnv = getProxyConfFromEnv()
        if ( proxyEnv.size() > 2 && proxyEnv[0] )
            http.setProxy( proxyEnv[1], proxyEnv[2].toInteger(), 'http')

        StringReader rdr = http.get( path: "/batch-job-result/batch-job-${job}.txt", contentType: ContentType.TEXT ) as StringReader
        List res = rdr.readLines()
        log.debug( "Got URL: ${url} Ret: ${res.size()}")

        return res.join('\n')
    }

    /**
     * Parse a typical variant string
     *
     * @param var   Variant to parse
     * @return      Map of variant components
     */
    static Map parse( String var )
    {
        Map m = [:]

        //  Parse result <trans>(gene_xxx):x.AAAA
        //
        def match = ( var =~ /(.+)\((.+)_.+\):(.+)$/ )
        if ( match.count == 1)
        {
            log.debug( "Parse hgvs, in: " + var + " out " + match[0])
            m << ['transcript' : match[0][1]]
            m << ['gene'       : match[0][2]]
            m << ['hgvs'       : match[0][3]]
            return m
        }

        //  Parse result <trans>:x.AAAA
        //
        match = ( var =~ /(.+):(.+)$/ )
        if ( match.count == 1)
        {
            log.debug( "Parse hgvs, in: " + var + " out " + match[0])
            m << ['transcript' : match[0][1]]
            m << ['hgvs'       : match[0][2]]
            return m
        }

        return m
    }

    /**
     * Process a List of variants using one of the available batch tasks
     *
     * @param muts  List of variants to submit
     * @param task  One of SyntaxChecker, NameChecker, PositionConverter, SnpConverter
     * @return      Results as a string
     */
    static String batch( List muts, String task )
    {
        if ( ! muts ) return ''

        String job = submitBatch( muts.join('\n'), task )
        assert job
        def url = baseURL + "/batch-job-result/batch-job-${job}.txt"
        log.info( "Job running: retrieve with: ${url}")

        int remain = monitorBatch( job )

        int msgcnt = 0
        int last   = remain
        int loop   = 0
        while ( remain > 0 )
        {
            sleep 3000                 // 3 sec.

            //  chatty message
            //
            if ((++msgcnt % 10) == 0 )
                log.info( "Remaining variants to batch: ${remain}")

            if ( last == remain )
            {
                if  ( ++loop > 100 ) break   // exit if no change in status for 100 tries ~5 minutes
            }
            else
            {
                loop  = 0
                last  = remain
            }

            remain = monitorBatch( job )
        }

        //  Check we're really finished
        //
        sleep 1000                 // 1 sec.
        remain = monitorBatch( job )
        if ( remain )
        {
            log.fatal( "Exceeded wait time (${waitTime/1000.0} s.) to process variants, remaining/total: ${remain}/${muts.size()}")
            System.exit(1)
        }

        //  Download results file: format depends on batch task
        //
        sleep waitTime
        return getBatch( job )
    }

    /**
     * Simplified headers to use for map returns by batchNameChecker
     */
    static private final List ncHeaders  =  [
                                        'in',
                                        'error',
                                        'transcript',
                                        'gene',
                                        'variant',
                                        'hgvsn',
                                        'hgvsc',
                                        'hgvsp',
                                        'hgvsc_full',
                                        'hgvsp_full',
                                        'genomic_ref',
                                        'transcript2',
                                        'protein_ref',
                                        'affected_transcripts',
                                        'affected_proteins',
                                        'restriction_sites_created',
                                        'restriction_sites_deleted']

    /**
     * Check a List of variants using the NameChecker
     *
     * @param muts  List of variants (needs versioned transcript eg NM_000033.3:c.31_46delCGGGGGAACACGCTGA)
     * @return      List of Maps - one for each variant input
     *                  Map =   [
     *                          in:, error:, transcript:, gene:, variant:, hgvsn:,
     *                          hgvsc:, hgvsp:, hgvsc_full:, hgvsp_full:, genomic_ref:,
     *                          transcript2:, protein_ref:, affected_transcripts:,
     *                          affected_proteins:, restriction_sites_created:, restriction_sites_deleted
     *                          ]
     */
    static List batchNameChecker( List muts )
    {
        def res = batch( muts, "NameChecker" )

        //  Split up the NL separated lines in the result string
        //
        def lines = res.split("\n")
        def header = lines[0].split("\t")
        assert ncHeaders.size() == header.size()

        List vars = []
        lines     = lines[1..lines.size()-1]
        for ( line in lines )
        {
            def row  = [:]
            def flds = line.split("\t")
            flds.eachWithIndex{ String fld, int i -> row << [(ncHeaders[i]) : fld ] }
            vars.add(row)
        }

        return vars
    }

    /**
     * Check a List of variants using the SyntaxChecker
     *
     * @param muts  List of variants
     * @return      List of variant maps with two elements 'Input' and 'Status'
     *              Status is OK for valid syntax and an error message otherwise
     */
    static List batchSyntaxChecker( List muts )
    {
        def res = batch( muts, "SyntaxChecker" )

        //  Split up the tab separated lines in the result string
        //
        def lines = res.split("\n")
        def header = lines[0].split("\t")
        assert 2 == header.size()

        List vars = []
        lines     = lines[1..lines.size()-1]
        for ( line in lines )
        {
            def row  = [:]
            def flds = line.split("\t")
            flds.eachWithIndex{ String fld, int i -> row << [(header[i]) : fld ] }
            vars.add(row)
        }

        return vars
    }

    /**
     * Convert variant to chromosome position variant
     *
     * @param muts  List of variants to convert
     * @return      List of maps of variant values
     */
    static List<Map> batchPositionConverter( List muts )
    {
        def res = batch( muts, "PositionConverter" )

        //  Split up the tab separated lines in the result string
        //
        def lines  = res.split("\n")
        def header = lines[0].split("\t")
        assert   4 == header.size()

        //  Parse list of variants and the returned fields
        //  Note: there are a variable number of transcripts following the HGVSg entry
        //
        List vars = []
        lines     = lines[1..lines.size()-1]
        for ( line in lines )
        {
            def flds = line.split("\t")
            Map     m = [:]
            m =  [variant:      flds[0]]
            m << [error: (flds.size() > 1 ? flds[1] : 'No transcripts found in mutation region' )]

            if ( ! m.error && flds.size() > 3 )
            {
                m << [hgvsg:        flds[2]]
                m << [transcripts:  flds[3..-1].join(',')]
            }
            else
            {
                if ( HGVS.isHgvsG(flds[0]))
                    m << [hgvsg: flds[0]]        // use input variant if hgvsg
            }
            vars << m
        }

        return vars
    }

    /**
     * Short form of gene
     *
     * @param   gene    Long form eg PTEN_v001
     * @return          Short form eg PTEN
     */
    static String shortGene( String gene )
    {
        def match = ( gene =~ /^(.*)_v\d+/ )
        if ( match.count == 1 )
        {
            return match[0][1]
        }

        return gene
    }
}
