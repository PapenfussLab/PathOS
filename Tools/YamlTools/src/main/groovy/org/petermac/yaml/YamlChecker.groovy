package org.petermac.yaml

import org.yaml.snakeyaml.Yaml

/**
 *
 *
 *
 */
class YamlChecker {
    private Object theType

    YamlChecker(String typeDoc) {
        def yaml = new Yaml()
        theType = yaml.load(typeDoc)
    }

    YamlChecker(File typeFile) {
        def yaml = new Yaml()
        theType = yaml.load(typeFile.text)
    }

    YamlChecker(Map aType) {
        theType = aType
    }

    public void check(Object data) {
        traverse(theType, data)
    }

    private void traverse(String type, Object data) {
        switch (type) {
            case 'string':
                if (!(data instanceof String)) {
                    throw new TypeError("expected string, got ${data.getClass()}")
                }
                return
            case 'number':
                if (!(data instanceof Number)) {
                    throw new TypeError("expected number, got ${data.getClass()}")
                }
                return
            case 'boolean':
                if (!(data instanceof Boolean)) {
                    throw new TypeError("expected boolean, got ${data.getClass()}")
                }
                return
            case 'any':
                return
            default:
                throw new BadlyFormedType("Unrecognized atomic type: ${type}")
        }
    }

    private void traverse(Map type, Object data) {
        assert type.size() == 1
        switch (type.keySet()[0]) {
            case 'list':
                if (!(data instanceof List)) {
                    throw new TypeError("expected list")
                }
                def lt = type['list']
                def ld = (List)data
                ld.each {
                    traverse(lt, it)
                }
                return
            case 'iterator':
                if (!(data instanceof Iterator)) {
                    throw new TypeError("expected iterator")
                }
                def t = type['iterator']
                def itr = (Iterator)data
                itr.each {
                    traverse(t, it)
                }
                return
            case 'tuple':
                if (!(data instanceof List)) {
                    throw new TypeError("expected list")
                }
                def lt = type['tuple']
                def ld = (List)data
                if (lt.size() != ld.size()) {
                    throw new TypeError("tuple has wrong number of elements: ${lt.size()} / ${ld.size()}")
                }
                for (int i = 0; i < lt.size(); ++i) {
                    traverse(lt[i], ld[i])
                }
                return
            case 'oneof':
                if (!(data instanceof Map)) {
                    throw new TypeError("expected map")
                }
                def ot = type['oneof']
                def od = (Map)data
                if (od.size() != 1) {
                    throw new TypeError("data for oneof type must have exactly 1 element (${od.size()}) found")
                }
                def k = od.keySet()[0]
                if (!ot[k]) {
                    throw new TypeError("constructor for oneof type not in type (${od.keySet()[0]}) found")
                }
                traverse(ot[k], od[k])
                return
            case 'map':
                if (!(data instanceof Map)) {
                    throw new TypeError("expected map")
                }
                def dt = type['map']
                def dd = (Map)data
                Set seen = []
                Object anyType = null
                for (item in dt) {
                    if (item.key == '*') {
                        anyType = item.value
                        continue
                    }
                    String k = item.key
                    Boolean opt = k.endsWith('?')
                    if (opt) {
                        k = k.substring(0, k.size() - 1)
                    }
                    if (!opt && !dd.containsKey(k)) {
                        throw new TypeError("map did not contain mandatory key: ${k}")
                    }
                    if (dd.containsKey(k)) {
                        traverse(item.value, dd[k])
                    }
                    seen << k
                }
                Set extra = dd.keySet() - seen
                println extra
                if (extra.size() > 0 && anyType == null) {
                    throw new TypeError("map contained unexpected keys: ${extra}")
                }
                extra.each {
                    traverse(anyType, dd[it])
                }
                return
            default:
                throw new BadlyFormedType("unknown type constructor ${type.keySet()[0]}")
        }
    }
}
