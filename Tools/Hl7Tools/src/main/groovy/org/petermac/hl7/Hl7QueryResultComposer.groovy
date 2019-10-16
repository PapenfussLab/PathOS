package org.petermac.hl7

import org.petermac.yaml.*
import groovy.util.logging.Log4j

@Log4j
class Hl7QueryResultComposer extends Hl7MessageComposer {

    static Map compose(Map args) {
        def res = new YamlComposer()

        res[['ADR_A19','MSH']] = msh('ADR', 'A19', args)
        res[['ADR_A19','MSA']] = msa(args)
        res[['ADR_A19','QRD']] = qrd(args)
        res[['ADR_A19','QUERY_RESPONSE']] = []
        if (args['dob']) {
            res[['ADR_A19','QUERY_RESPONSE',0,'PID']] = pid(args)
            if (args['requester']) {
                res[['ADR_A19','QUERY_RESPONSE',0,'PV1']] = pv1(args)
            }
        }

        return res.thing
    }

}

