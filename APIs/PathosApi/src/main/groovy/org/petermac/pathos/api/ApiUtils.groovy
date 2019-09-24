package org.petermac.pathos.api

import org.petermac.yaml.YamlConfig
import java.nio.file.Path
import java.nio.file.Paths

class ApiUtils {
    /**
     *
     * Load an ExportReceiverFactory class, and create an ExportReceiver.
     *
     * @param configFile
     *        the name of a file containing the YAML configuration for the export receiver
     *
     * @return an ExportReceiver object.
     *
     */
    static ExportReceiver loadExporter(String configName, Path home) {
        Path configPath = home.resolve(Paths.get(configName))
        Map config = YamlConfig.load(configPath.toString())
        return loadExporter(config, home)
    }

    /**
     *
     * Load an ExportReceiverFactory class, and create an ExportReceiver.
     *
     * @param config
     *        A map containing two keys: class, and config.
     *        The class parameter is the fully qualified class name for the exporter.
     *        The config parameter is a map containing the configuration for the exporter.
     *
     * @return an ExportReceiver object.
     *
     */
    static ExportReceiver loadExporter(Map config, Path home) {
        Set mustHave = []
        mustHave << "class"
        mustHave << "config"

        Set mayHave = []
        mayHave << "path"

        YamlConfig.checkParams(config, mustHave, mayHave)

        ClassLoader loader = ClassLoader.getSystemClassLoader()
        if (config['path']) {
            Path path = home.resolve(Paths.get(config['path']))
            List urls = [path.toUri().toURL()]
            loader = new URLClassLoader(urls as URL[])
        }
        Class<? extends ExportReceiverFactory> facClass = (Class<? extends ExportReceiverFactory>) Class.forName(config['class'])
        ExportReceiverFactory fac = facClass.getConstructor().newInstance()
        return fac.create(config['config'])
    }

    /**
     *
     * Load an ImporterFactory class, and create an Importer.
     *
     * @param config
     *        A map containing two keys: class, and config.
     *        The class parameter is the fully qualified class name for the importer.
     *        The config parameter is a map containing the configuration for the importer.
     *
     * @return an Importer object.
     *
     */
    static Importer loadImporter(Map config) {
        Set mustHave = []
        mustHave << "class"
        mustHave << "config"

        Set mayHave = []

        YamlConfig.checkParams(config, mustHave, mayHave)

        def facClass = (Class<? extends ImporterFactory>) Class.forName(config['class'])
        ImporterFactory fac = facClass.getConstructor().newInstance()
        return fac.create(config['config'])
    }
}
