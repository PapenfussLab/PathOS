package org.petermac.pathos.yaml

import org.petermac.yaml.*
import org.petermac.pathos.api.*

class YamlExporter implements ExportReceiverFactory {
    class ExporterImpl implements ExportReceiver {
        private Map names
        private Map files
        private Codec codec
        private Boolean tolerant

        ExporterImpl(Map names, Codec codec, Boolean tolerant) {
            this.names = names
            this.files = [:]
            this.codec = codec
            this.tolerant = tolerant
        }

        void receive(String domain, String action, Object data) {
            if (!names[domain]) {
                if (tolerant) {
                    return
                }
                throw new UnsupportedDomain("${domain}")
            }
            if (!names[domain][action]) {
                if (tolerant) {
                    return
                }
                throw new UnsupportedAction("${domain}/${action}")
            }
            if (!files[domain]) {
                files[domain] = [:]
            }
            if (!files[domain][action]) {
                files[domain][action] = new File(names[domain][action])
            }
            files[domain][action] << codec.encode(data)
        }

        void close() {
        }
    }

    ExportReceiver create(Map config) {
        Set mustHave = []
        mustHave << 'filenames'
        mustHave << 'encoding'

        Set mayHave = []
        mustHave << 'tolerant'

        YamlConfig.checkParams(config, mustHave, mayHave)
       
        Codec codec = Codecs.find(config['encoding'])

        if (!config['filenames'] instanceof Map) {
            throw new BadData("filenames must be a map")
        }
        Map doms = config['filenames'] as Map
        doms.each { dom, actsVal ->
            if (!actsVal instanceof Map) {
                throw new BadData("filenames[${dom}] must be a map")
            }
            Map acts = actsVal
            acts.each { act, fnmVal ->
                if (!fnmVal instanceof String) {
                    throw new BadData("filenames[${dom}][${act}] must be a string")
                }
            }
        }

        Boolean tolerant = config['tolerant'] ? config['tolerant'].toBoolean() : false

        return new ExporterImpl(doms, codec, tolerant)
    }
}
