package org.petermac.pathos.yaml

import org.petermac.yaml.*
import org.petermac.pathos.api.*

import org.yaml.snakeyaml.Yaml

/**
 * Plugin for importing from a YAML file into PathOS.
 *
 * Configuration:
 *
 * Data packing and format:
 *
 *          domain: <string>
 *          action: <string>
 *          filename: <string>
 *          expandEntitiyReferences?: <boolean> [default: false]
 *
 */
class YamlImporter implements ImporterFactory {
    class ImporterImpl implements Importer {
        private String domain
        private String action
        private String filename
        private Boolean expandEntitiyReferences

        ImporterImpl(Map config) {
            Set mustHave = []
            mustHave << 'domain'
            mustHave << 'action'
            mustHave << 'filename'

            Set mayHave = []

            YamlConfig.checkParams(config, mustHave, mayHave)

            domain = config['domain']
            action = config['action']
            filename = config['filename']
        }

        void importData(ImportReceiver receiver) {
            File file = new File(filename)
            FileInputStream inp = new FileInputStream(file)
            for (Object obj : yaml.loadAll(inp)) {
                receiver.receive(domain, action, obj)
            }
        }
    }

    Importer create(Map params) {
        return new ImporterImpl(config)
    }
}
