package org.petermac.hl7

import ca.uhn.hl7v2.HapiContext
import ca.uhn.hl7v2.model.Composite
import ca.uhn.hl7v2.model.Field
import ca.uhn.hl7v2.model.Group
import ca.uhn.hl7v2.model.Message
import ca.uhn.hl7v2.model.Primitive
import ca.uhn.hl7v2.model.Segment
import ca.uhn.hl7v2.model.Structure
import ca.uhn.hl7v2.model.Type
import ca.uhn.hl7v2.model.Varies
import groovy.util.logging.Log4j

@Log4j
class Hl7Inflator {
    public Message inflateMessage(Map spec) {
        assert spec.size() == 1
        String nm = spec.keySet().iterator().next()
        HapiContext context = new LocalHapiContext()
        def factory = context.getModelClassFactory()
        def cls = factory.getMessageClass(nm, "2.5.1", true)
        Message msg = cls.getConstructor().newInstance()
        Map grp = spec[nm]
        inflateGroup(msg, grp)
        return msg
    }

    private void inflateGroup(Group grp, Map spec) {
        def used = [] as HashSet
        grp.getNames().each { nm ->
            if (grp.isRequired(nm) && !spec.containsKey(nm)) {
                log.warn "required structure ${nm} was missing"
                return
            }
            if (!spec.containsKey(nm)) {
                return
            }
            used << nm
            def ig = grp.isGroup(nm)
            if (grp.isRepeating(nm)) {
                List rpts = spec[nm]
                def n = 0
                rpts.each {
                    def v = grp.get(nm, n)
                    n += 1
                    if (ig) {
                        inflateGroup(v, it)
                    } else {
                        inflateSegment(v, it)
                    }
                }
            } else {
                def v = grp.get(nm)
                if (ig) {
                    inflateGroup(v, spec[nm])
                } else {
                    inflateSegment(v, spec[nm])
                }
            }
        }
        alertUnused(spec, used)
    }

    private void inflateSegment(Segment seg, Map spec) {
        def n = 0
        def segName = seg.getName()
        def used = [] as HashSet
        seg.getNames().each { nm ->
            n += 1
            if (seg.isRequired(n) && !spec.containsKey(nm)) {
                log.warn "required field ${nm} (${n}) was missing."
                return
            }
            if (!spec.containsKey(nm)) {
                return
            }

            used << nm

            if (segName == 'OBX' && n == 5) {
                def msg = seg.getMessage()
                String tp = "ca.uhn.hl7v2.model.v251.datatype.${spec['Value Type']}"
                Type t = Class.forName(tp).getConstructor(Message.class).newInstance(msg)
                seg.getField(n, 0).setData(t)
                inflateValue(t, spec[nm])
            } else {
                if (seg.getMaxCardinality(n) != 1) {
                    log.debug "expanding ${nm} ${spec[nm]}"
                    for (int i = 0; i < spec[nm].size(); ++i) {
                        def v = seg.getField(n, i)
                        inflateValue(v, spec[nm][i])
                    }
                } else {
                    def v = seg.getField(n, 0)
                    inflateValue(v, spec[nm])
                }
            }
        }
        alertUnused(spec, used)
    }

    private void inflateValue(Composite cmp, List itms) {
        def n = 0
        itms.each {
            if (it != null) {
                inflateValue(cmp.getComponent(n), it)
            }
            n += 1
        }
    }

    private void inflateValue(Primitive prm, String val) {
        prm.setValue(val)
    }

    private void alertUnused(Map spec, Set used) {
        spec.each { nm, v ->
            if (!nm in used) {
                log.error "Key ${nm} in spec was not used"
            }
        }
    }
}
