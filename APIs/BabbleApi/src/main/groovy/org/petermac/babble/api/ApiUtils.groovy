package org.petermac.babble.api

import org.petermac.yaml.YamlConfig

class ApiUtils {
    /**
     *
     * Load a BabbleTranslatorFactory class, and create a BabbleTranslator.
     *
     * @param config
     *        A map containing two keys: class, and config.
     *        The class parameter is the fully qualified class name for the translator.
     *        The config parameter is a map containing the configuration for the translator.
     *
     * @return a BabbleTranslator object.
     *
     */
    static BabbleTranslator loadTranslator(Map config) {
        Set mustHave = []
        mustHave << "class"
        mustHave << "config"

        Set mayHave = []

        YamlConfig.checkParams(config, mustHave, mayHave)

        def facClass = (Class<? extends BabbleTranslatorFactory>) Class.forName(config['class'])
        BabbleTranslatorFactory fac = facClass.getConstructor().newInstance()
        return fac.create(config['config'])
    }
}

