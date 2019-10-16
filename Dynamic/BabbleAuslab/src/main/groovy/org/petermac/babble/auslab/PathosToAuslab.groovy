package org.petermac.babble.auslab

import org.petermac.babble.api.*
import org.petermac.hl7.*
import org.petermac.yaml.*
import groovy.util.logging.Log4j

@Log4j
class PathosToAuslab implements BabbleTranslatorFactory {
    private Map rules

    class Translator implements BabbleTranslator {

        Translator(Map config) {
            Set mustHave = []
            Set mayHave = []
            YamlConfig.checkParams(config, mustHave, mayHave)

            rules = [:]
            rules['report'] = [:]
            rules['report']['publish'] = {args -> Hl7ResultComposer.compose(args)}
            rules['patient'] = [:]
            rules['patient']['request'] = {args -> Hl7QueryComposer.compose(args)}
        }

        /**
         *
         * Translate the maps-and-lists representation of an HL7 message
         * to maps-and-lists for PathOS.
         *
         */
        Object translate(Object obj) {
            Map msg = obj

            Set mustHave = []
            mustHave << 'domain'
            mustHave << 'action'
            mustHave << 'data'

            Set mayHave = []

            YamlConfig.checkParams(msg, mustHave, mayHave)

            if (!rules[msg['domain']]) {
                log.error "unsupported domain ${msg['domain']}"
                throw new BadData("unsupported domain ${msg['domain']}")
            }
            if (!rules[msg['domain']][msg['action']]) {
                log.error "in domain ${msg['domain']}, unsupported action ${msg['action']}"
                throw new BadData("in domain ${msg['domain']}, unsupported action ${msg['action']}")
            }
            return rules[msg['domain']][msg['action']](msg['data'])
        }
    }

    String name() {
        return 'to-auslab'
    }

    Translator create(BabbleConfigurator confator, Map config) {
        return new Translator(config)
    }

    String usage() {
        return \
"""
The PathosToAuslab translator converts the messages produced by the PathOS
export API into HL7 messages.

Configuration parameters:

none.
"""
    }

    static main(args) {
        def fac = new PathosToAuslab()
        println fac.usage()
    }
}
