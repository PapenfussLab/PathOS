package org.petermac.babble.mario

import org.petermac.babble.api.*

import groovy.util.logging.Log4j

/**
 * A class for doing the translation of the LIMS XML
 * and sample relationship files and composing a message
 * for importing them into the Loader.
 *
 * The class name doesn't reflect the fact that it is a factory
 * because the class is named in the babble script, and calling
 * it a factory there looks weird to users.
 *
 * Author: Tom Conway
 * Date: 2018/05/08
 */
@Log4j
class MarioLimsHandler implements BabbleTranslatorFactory {

    static class Translator implements BabbleTranslator {
        private BabbleTranslator limsXpander
        private BabbleTranslator rshipXpander
        Translator(BabbleTranslator lX, BabbleTranslator rX) {
            limsXpander = lX
            rshipXpander = rX
        }

        Object translate(Object src) {
            String limsFn = src
            def m = (limsFn =~ /(.*)\/LIMS(.*).xml/)
            if (!m) {
                log.error "could not match LIMS XML filename: ${limsFn}"
                return null
            }
            String rshipFn = "${m[0][1]}/SampleRelationship.csv"
            Map msg = limsXpander.translate(limsFn)
            if (!msg) {
                return null
            }
            File file = new File(rshipFn)
            if (file.exists()) {
                Map rsh = rshipXpander.translate(rshipFn)
                if (!rsh) {
                    return null
                }
                msg['data'] += rsh['data']
            }
            return msg
        }
    }

    String name() {
        return 'lims-xml-handler'
    }

    BabbleTranslator create(BabbleConfigurator confator, Map config) {
        BabbleTranslator lx = (new MarioLimsExpander()).create(confator, config)
        BabbleTranslator rx = (new SampleRelationshipExpander()).create(confator, config)
        return new Translator(lx, rx)
    }

    String usage() {
        return \
"""
The MarioLimsHandler takes the filename for a LIMS XML file
and creates a message that creates all the sequence related records,
as well as reading the sample relationships file and adding any
relationships to the message.

Configuration parameters: none.
"""
    }

    static void main(args) {
        def fac = new MarioLimsHandler()
        println fac.usage()
    }
}
