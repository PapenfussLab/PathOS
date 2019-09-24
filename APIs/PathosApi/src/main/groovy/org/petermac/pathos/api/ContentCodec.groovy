package org.petermac.pathos.api

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.yaml.snakeyaml.Yaml

interface Codec {
    /**
     * Serialize an object.
     *
     * @param obj
     *      An object that can be serialized as json/yaml/etc.
     *
     * @return the encoded string.
     */
    String encode(Object obj)

    /**
     * Parse a string to create an object.
     *
     * @param str
     *      A serialized object.
     *
     * @return an object
     */
    Object decode(String str)
}

class Codecs {
    private static Map codecs

    static Codec find(String name) {
        if (codecs == null) {
            initCodecs()
        }
        return codecs[name]
    }

    static Codec initCodecs() {
        codecs = [:]
        codecs['json'] = new JsonCodec()
        codecs['yaml'] = new YamlCodec()
    }
}

class JsonCodec implements Codec {
    private JsonSlurper slurper

    JsonCodec() {
        slurper = new JsonSlurper()
    }

    synchronized String encode(Object obj) {
        return JsonOutput.toJson(obj)
    }

    synchronized Object decode(String str) {
        return slurper.parseText(str)
    }
}

class YamlCodec implements Codec {
    private Yaml yaml

    YamlCodec() {
        yaml = new Yaml()
    }

    synchronized String encode(Object obj) {
        return yaml.dump(obj)
    }

    synchronized Object decode(String str) {
        return yaml.load(str)
    }
}

