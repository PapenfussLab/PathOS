package org.petermac.yaml

import java.util.regex.Pattern 
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Representer
import org.yaml.snakeyaml.resolver.Resolver

class YamlCodec extends Yaml {
    static class OurResolver extends Resolver {

        /*
         * do not resolve float and timestamp
         */
        protected void addImplicitResolvers() {
            addImplicitResolver(Tag.BOOL, BOOL, "yYnNtTfFoO");
            addImplicitResolver(Tag.MERGE, MERGE, "<");
            addImplicitResolver(Tag.NULL, NULL, "~nN\0");
            addImplicitResolver(Tag.NULL, EMPTY, null);
            addImplicitResolver(Tag.INT, INT, "-+0123456789");
            addImplicitResolver(Tag.STR, Pattern.compile('^(0[0-9]+)$'), "0");
            addImplicitResolver(Tag.FLOAT, FLOAT, "-+0123456789.");
            addImplicitResolver(Tag.TIMESTAMP, TIMESTAMP, "0123456789");
            //addImplicitResolver(Tag.VALUE, VALUE, "=");
        }
    }

    static class OurDumperOptions extends DumperOptions {
        OurDumperOptions() {
            super()
            setExplicitStart(true)
        }
    }

    YamlCodec() {
        super(new Constructor(), new Representer(), new OurDumperOptions(), new OurResolver())
    }
}
