package org.petermac.babble.auslab

import groovyx.net.http.HTTPBuilder
import groovy.util.slurpersupport.GPathResult
import org.petermac.babble.api.*
import org.petermac.hl7.*
import org.petermac.yaml.*
import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger

@Log4j
class QueryHook implements BabbleHookFactory {
    static class Hook implements BabbleHook {
        private Map db
        private Map qry
        private Boolean ignoreHeartbeats
        
        Hook(BabbleConfigurator confator, Map config) {
            Set mustHave = []

            Set mayHave = []
            mayHave << 'file'
            mayHave << 'ignore-heartbeats'

            YamlConfig.checkParams(config, mustHave, mayHave)

            db = [:]
            if (config['file']) {
                def yaml = new YamlCodec()
                def file = new File(config['file'])
                def inp = new FileInputStream(file)
                db = yaml.load(inp)
            }

            ignoreHeartbeats = true
            if (config['ignore-heartbeats'] != null) {
                ignoreHeartbeats = config['ignore-heartbeats'].toBoolean()
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

            // ignore heartbeats
            //
            if (ignoreHeartbeats && msg['QRY_A19']['QRD']['Who Subject Filter'][0][0] == 'heartbeat') {
                return msg
            }

            qry = Hl7QueryResultComposer.saveQrd(msg['QRY_A19']['QRD'])
            qry['messageId'] = msg['QRY_A19']['MSH']['Message Control ID']

            return msg
        }

        Object after(Object rsp) {
            if (qry == null) {
                return rsp
            }
            if (db[qry['urn']]) {
                qry += db[qry['urn']]
                log.info "${qry['messageId']} ${qry['urn']} found"
            } else {
                log.info "${qry['messageId']} ${qry['urn']} not found"
            }
            Map res = Hl7QueryResultComposer.compose(qry)
            return res
        }

        void close() {
        }

    }

    String name() {
        return 'oracle'
    }

    BabbleHook create(BabbleConfigurator confator, Map config) {
        return new Hook(confator, config)
    }

    String usage() {
        return \
"""
The QueryHook plugin simulates the query lookup in Auslab.

Configuration parameters:

file
    The name of a YAML file containing a map from patient URN to
    the set of fields for populating an ADR_A19 response.

ignore-heartbeats
    If set to false, heartbeat messages will be treated as normal queries.
"""
    }

    static main(args) {
        BasicConfigurator.configure()
        Logger.getRootLogger().setLevel(Level.INFO)

        File file = new File(args[0])
        FileInputStream inp = new FileInputStream(file)
        def yaml = new YamlCodec()
        Map db = [:]
        for (Map m : yaml.loadAll(inp)) {
            if (!m['OML_O21']) {
                continue
            }
            Map rec = [:]
            rec += Hl7QueryResultComposer.savePid(m['OML_O21']['PATIENT']['PID'])
            rec += Hl7QueryResultComposer.savePv1(m['OML_O21']['PATIENT']['PATIENT_VISIT']['PV1'])
            db[rec['urn']] = rec
        }
        File out = new File(args[1])
        out.write(yaml.dump(db))
    }
}
