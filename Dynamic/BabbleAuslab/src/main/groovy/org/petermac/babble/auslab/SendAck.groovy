package org.petermac.babble.auslab

import groovyx.net.http.HTTPBuilder
import groovy.util.slurpersupport.GPathResult
import org.petermac.babble.api.*
import org.petermac.hl7.*
import org.petermac.yaml.*
import groovy.util.logging.Log4j

@Log4j
class SendAck implements BabbleHookFactory {
    static class Hook implements BabbleHook {
        BabbleDestination dst
        
        Hook(BabbleConfigurator confator, Map config) {
            config.each {nm, cfg ->
                dst = confator.createDestination(nm, cfg)
            }
        }

        Object before(Object msg) {
            return msg
        }

        Object after(Object rsp) {
            dst.deliverMessage(null, rsp)
            return rsp
        }

        void close() {
            dst.close()
        }
    }

    String name() {
        return 'send-ack'
    }
    BabbleHook create(BabbleConfigurator confator, Map config) {
        return new Hook(confator, config)
    }

    String usage() {
        return \
"""
The SendAck plugin sends the Ack messages to a destination.

Configuration parameters:
    A destination configuration.

"""
    }

    static main(args) {
        def fac = new SendAck()
        println fac.usage()
    }
}
