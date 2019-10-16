package org.petermac.babble.mario

import groovy.util.logging.Log4j

@Log4j
class MarioMessageTransformer {
    static Object transform(Object msg, Map vars, Boolean strict = true) {
        return applyTransform(msg, vars, strict)
    }

    Object applyTransform(List msg, Map vars, Boolean strict) {
        if (msg == null) {
            return msg
        }
        List res = []
        msg.each { itm ->
            res << applyTransform(itm, vars, strict)
        }
        return res
    }

    static Object applyTransform(Map msg, Map vars, Boolean strict) {
        if (msg == null) {
            return msg
        }
        if (msg.size() == 1 && ('$' in msg)) {
            String nm = msg['$']
            if (vars.containsKey(nm)) {
                return vars[nm]
            }
            if (strict) {
                log.error "substitution parameter ${nm} not found."
                return null
            }
            return msg
        }

        Map res = [:]
        msg.each { k,v ->
            res[k] = applyTransform(v, vars, strict)
        }
        return res
    }

    static Object applyTransform(Object msg, Map vars, Boolean strict) {
        return msg
    }
}
