package org.petermac.hl7

import ca.uhn.hl7v2.Location
import ca.uhn.hl7v2.model.Composite
import ca.uhn.hl7v2.model.Group
import ca.uhn.hl7v2.model.Message
import ca.uhn.hl7v2.model.Primitive
import ca.uhn.hl7v2.model.Segment
import ca.uhn.hl7v2.model.Structure
import ca.uhn.hl7v2.model.Type
import ca.uhn.hl7v2.model.Varies
import groovy.util.logging.Log4j

@Log4j
class Hl7Traverser {

    public Hl7Traverser() {
    }

    public Map traverse(Message msg) {
        Map res = [:]
        res[msg.getName()] = visit(msg)
        return res
    }

    private Map visit(Group grp) {
        if (grp.isEmpty()) {
            return null
        }
        Map res = [:]
        for (nm in grp.getNames()) {
            List reps = grp.getAll(nm)
            if (grp.isRepeating(nm)) {
                List lst = []
                for (int i = 0; i < reps.size(); ++i) {
                    lst << visit(reps[i])
                }
                while (lst.size() > 0 && lst.last() == null) {
                    lst.pop()
                }
                if (lst.size() > 0) {
                    log.debug "group res[${nm}] = ${lst}"
                    res[nm] = lst
                }
            } else if (reps.size() > 0) {
                def v = visit(reps[0])
                if (v != null) {
                    log.debug "group res[${nm}] = ${v}"
                    res[nm] = v
                }
            }
        }
        return res
    }

    private Map visit(Segment seg) {
        if (seg.isEmpty()) {
            return null
        }
        Map res = [:]
        String[] names = seg.getNames()
        for (int i = 1; i <= seg.numFields(); ++i) {
            String nm = names[i - 1]
            List fs = seg.getField(i)
            Boolean req = seg.isRequired(i)
            if (seg.getMaxCardinality(i) != 1) {
                List lst = []
                for (int j = 0; j < fs.size(); ++j) {
                    lst << visit(fs[j], req)
                }
                while (lst.size() > 0 && lst.last() == null) {
                    lst.pop()
                }
                if (lst.size() > 0) {
                    log.debug "segment res[${nm}] = ${lst}"
                    res[nm] = lst
                }
            } else if (fs.size() > 0) {
                def v = visit(fs[0], req)
                if (v != null) {
                    log.debug "segment res[${nm}] = ${v}"
                    res[nm] = v
                }
            }
        }
        return res
    }

    private List visit(Composite cmp, Boolean req) {
        if (cmp.isEmpty() && !req) {
            return null
        }
        log.debug "composite ${cmp.getClass()} ${cmp}"
        def res = []
        for (val in cmp.getComponents()) {
            res << visit(val, req)
        }
        while (res.size() > 0 && res.last() == null) {
            res.pop()
        }
        if (res.size() > 0) {
            log.debug "composite result ${res.getClass()} ${res}"
            return res
        } else {
            return null
        }
    }

    private String visit(Primitive prm, Boolean req) {
        if (prm.isEmpty() && !req) {
            return null
        }
        log.debug "primitive ${prm.getClass()} ${prm}"
        return prm.getValue()
    }

    private Object visit(Varies var, Boolean req) {
        return visit(var.getData(), req)
    }
}

