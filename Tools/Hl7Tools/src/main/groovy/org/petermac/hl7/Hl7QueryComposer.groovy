package org.petermac.hl7

import org.petermac.yaml.*
import groovy.util.logging.Log4j

@Log4j
class Hl7QueryComposer extends Hl7MessageComposer {

    static Map compose(Map args) {
        Set mustHave = []
        mustHave << "urn"

        Set mayHave = []
        mayHave << "sample"

        YamlConfig.checkParams(args, mustHave, mayHave)

        def qry = new YamlComposer()

        qry[['QRY_A19','MSH']] = msh('QRY', 'A19', args)
        qry[['QRY_A19','QRD']] = qrd(args)

        return qry.thing
    }

}
