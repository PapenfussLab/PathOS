package org.petermac.yaml

import groovy.json.JsonSlurper
import org.yaml.snakeyaml.Yaml
import java.text.MessageFormat

class YamlConfig {

    static Object parse(String content) {
        def yaml = new Yaml()
        return yaml.load(content)
    }

    static Object load(String filename) {
        def yaml = new Yaml()
        def file = new File(filename)
        def inp = new FileInputStream(file)
        return yaml.load(inp)
    }

    /**
     *
     * Take a parameter map and check that it has all the mandatory keys,
     * and has no keys that are neither mandatory nor optional. If 
     *
     * @param params
     *        parameter map for configuration
     *
     * @param mustHave
     *        set of parameter names that are mandatory
     *
     * @param mayHave
     *        set of parameter names that are optional
     *
     */
    static void checkParams(Map params, Set<String> mustHave, Set<String> mayHave) {
        Set seen = []
        params.each { k, v ->
            def found = (mustHave.contains(k) || mayHave.contains(k))
            if (!found) {
                throw new UnknownParameterName(k)
            }
            seen << k
        }
        Set missing = mustHave - seen
        if (missing.size() > 0) {
            throw new RequiredParameterMissing("${missing}")
        }
    }

    /**
     *
     * Expand parameters by applying MessageFormat.format
     *
     */
    static Object expand(Object obj, List args) {
        if (obj == null) {
            return obj
        }
        switch (obj) {
            case {it instanceof List}:
                List res = []
                ((List)obj).each {
                    res << expand(it, args)
                }
                return res
            case {it instanceof Map}:
                Map m = obj
                Map res = [:]
                m.each { k, v ->
                    res[k] = expand(v, args)
                }
                return res
            case {it instanceof String}:
                return doExpansion(obj, args)
            default:
                // numbers, booleans, ...
                return obj
        }
    }

    /**
     *
     * Perform repleacement with some checking.
     *
     */
    private static String doExpansion(String src, List args) {
        def grp = (src =~ /\{(\d+)\}/)
        for (int i = 0; i < grp.size(); ++i) {
            def n = grp[i][1].toInteger()
            if (n >= args.size()) {
                throw new InvalidParameterValue("argument index out of range: ${n} given, ${args.size()} available.")
            }
        }
        return MessageFormat.format(src, args as Object[])
    }

    /**
     *
     * Expand external entity references in an object loaded from YAML or JSON.
     *
     * An external entity reference is a dictionary with one of the following keys:
     *      @file       - the value is a filename to retreive
     *      @url        - the value is a URL to retrieve [not implemented yet]
     *
     * Additionally, the dictionary may specify parameters for how the external entity
     * should be included in the object:
     *      @parse      - how to parse the external entity [default: string].
     *      @recursive  - for parsed entities, true iff the expansion should be applied recursively [default: false].
     *
     * The default behaviour is to include the external content as a string, however the following
     * values of the @parse value are supported:
     *      string      - include the content as a string
     *      json        - parse the content with a JSON parser
     *      yaml        - parse the content with a YAML parser
     *      base64      - base64 encode the content and return the encoded data as a string
     *
     * It is assumed that any dictionary with other keys is just a dictionary and is returned as is.
     */
    static Object expandExternalEntityReferences(Object obj) {
        if (obj == null) {
            return obj
        }
        switch (obj) {
            case {it instanceof List}:
                List res = []
                ((List)obj).each {
                    res << expandExternalEntityReferences(it)
                }
                return res
            case {it instanceof Map}:
                Map m = obj
                Set refKeys = ['@file', '@url', '@parse', '@recursive']
                Set extra = m.keySet() - refKeys()
                if (extra.size() > 0) {
                    Map res = [:]
                    m.each { k, v ->
                        m[k] = expandExternalEntityReferences(v)
                    }
                }
                if (m.containsKey('@file')) {
                    File f = new File(m['@file'])
                    String p = m['@parse'] ?: 'string'
                    switch (p) {
                        case 'string':
                            return m.text
                        case 'base64':
                            return f.bytes.encodeBase64().toString()
                        case 'json':
                            def slurper = new JsonSlurper()
                            Object res = slurper.parseFile(f)
                            def rec = m['@recursive'] ? m['@recursive'].toBoolean() : false
                            return rec ? expandExternalEntityReferences(res) : res
                        case 'yaml':
                            def yaml = new Yaml()
                            Object res = yaml.load(new FileInputStream(f))
                            def rec = m['@recursive'] ? m['@recursive'].toBoolean() : false
                            return rec ? expandExternalEntityReferences(res) : res
                    }
                }
                throw new YamlException("unsupported external entity reference: ${m}")
            default:
                return obj
        }
    }
}
