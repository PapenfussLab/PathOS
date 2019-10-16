package org.petermac.babble.auslab

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.URIBuilder
import groovy.util.slurpersupport.GPathResult
import org.petermac.babble.api.*
import org.petermac.yaml.*
import org.petermac.hl7.Hl7QueryResultComposer
import groovy.util.logging.Log4j

@Log4j
class IguanaLogHook implements BabbleHookFactory {
    static class Hook implements BabbleHook {
        private BabbleCodec codec
        private HTTPBuilder http
        private URIBuilder uri
        private String username
        private String password
        private Integer maxRetries
        private Integer retryInterval
        private String source
        private Map qry
        private BabbleDestination dest
        
        Hook(BabbleConfigurator confator, Map config) {
            Set mustHave = []
            mustHave << "host"
            mustHave << "port"
            mustHave << "destination"

            Set mayHave = []
            mayHave << "source"
            mayHave << "username"
            mayHave << "password"
            mayHave << "max-retries"
            mayHave << "retry-interval"

            YamlConfig.checkParams(config, mustHave, mayHave)

            codec = confator.createCodec('hl7')

            log.info "queries to: ${config['host']}:${config['port'].toInteger()}"
            http = new HTTPBuilder("http://${config['host']}:${config['port'].toInteger()}")
            uri = new URIBuilder("http://${config['host']}:${config['port'].toInteger()}")
            username = config['username']
            password = config['password']
            maxRetries = config['max-retries'] ? config['max-retries'].toInteger() : 10
            retryInterval = config['retry-interval'] ? (config['retry-interval'].toInteger() * 1000) : 1500

            config['destination'].each { nm, cfg ->
                dest = confator.createDestination(nm, cfg)
            }
        }

        Object before(Object msg) {
            qry = null
            if (!msg instanceof Map) {
                return msg
            }
            if (!msg['QRY_A19']) {
                return msg
            }

            qry = Hl7QueryResultComposer.saveQrd(msg['QRY_A19']['QRD'])
            qry['messageId'] = msg['QRY_A19']['MSH']['Message Control ID']

            // ignore heartbeats
            //
            if (qry['urn'] == 'heartbeat') {
                qry = null
            }
            return msg
        }

        Object after(Object rsp) {
            if (!qry) {
                return rsp
            }
            if (rsp == null || !rsp instanceof Map) {
                log.error "no response for patient urn ${qry['urn']}"
                return rsp
            }
            if (!rsp['ACK']) {
                log.error "expected ACK response for patient urn ${qry['urn']}"
                return rsp
            }
            retrieveResults()
            return rsp
        }

        void retrieveResults() {
            def numRetries = 0
            while (numRetries < maxRetries) {
                sleep(retryInterval)
                numRetries += 1

                log.info "querying patient id ${qry['urn']} (attempt ${numRetries}/${maxRetries})"

                def params = [:]
                if (username) {
                    params['username'] = username
                }
                if (password) {
                    params['password'] = password
                }
                if (source) {
                    params['source'] = source
                }
                params['limit'] = 50
                params['type'] = 'ack_messages'
                params['reverse'] = 'true'

                //uri.path = "/api_query"
                //uri.query = params
                //println "uri: ${uri.toString()}"

                def stuff = http.get(path:'api_query', query:params)

                Boolean gotAnError = false
                for (GPathResult node : stuff.error) {
                    log.error "Iguana query failed: ${node.@description}"
                    gotAnError = true
                }
                if (gotAnError) {
                    return
                }

                for (GPathResult node : stuff.message) {
                    if (node.@type != "Ack") {
                        continue
                    }
                    Map rsp = codec.decode(node.@data.text())
                    if (rsp.containsKey('ADR_A19') && rsp['ADR_A19']['QRD']['Query ID'] == qry['queryId']) {
                        rsp['ADR_A19']['MSH']['Message Control ID'] = qry['messageId']
                        if (rsp['ADR_A19']['QUERY_RESPONSE']) {
                            log.info "query found demographics for ${qry['urn']}"
                            dest.deliverMessage(null, rsp)
                        } else {
                            log.info "query didn't find demographics for ${qry['urn']}"
                        }
                        return
                    }
                }
            }
            log.error "Unable to retrieve query results after ${numRetries} retries."
        }

        void close() {
        }
    }

    String name() {
        return 'viaiguana'
    }

    BabbleHook create(BabbleConfigurator confator, Map config) {
        return new Hook(confator, config)
    }

    String usage() {
        return \
"""
The IguanaHook hook intercepts queries being sent to Iguana,
and once an ACK is received, it queries the REST API for the Iguana logs
to try and find the actual query response from Auslab. If it succeeds in
getting a query response, it sends it to the configured destination.

Configuration parameters:

host
    [required] The Iguana hostname.

port
    [required] The Iguana port.

source
    [optional] The Iguana channel that should contain the response

username
    [optional] The username for authentication against the Iguana server

password
    [optional] The password for authentication against the Iguana server

max-retries
    [default: 10] The maximum number of times to attempt to find the response
    in the Iguana logs. In practice the response is usually found in the first
    couple of tries - if it take more tries than that, it usually means that
    the link to Auslab is down.

retry-interval
    [default: 1] The number of seconds to wait between retries.

destination
    [required] A destination specification to which the result will be sent,
    if found.
"""
    }
}
