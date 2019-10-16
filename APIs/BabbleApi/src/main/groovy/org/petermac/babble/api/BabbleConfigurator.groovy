package org.petermac.babble.api

import org.reflections.Reflections
import groovy.util.logging.Log4j

@Log4j
class BabbleConfigurator {
    private Map codecs
    private Map sources
    private Map hooks
    private Map translators
    private Map destinations

    public BabbleConfigurator() {
        codecs = [:]
        sources = [:]
        hooks = [:]
        translators = [:]
        destinations = [:]
    }

    /**
     *
     * Scan the class path for classes in the named package, and register
     * all Codecs, Sources, Translators and Destinations.
     *
     * @param pkg The name of the package to auto register classes from.
     */
    public void autoRegisterFromPackage(String pkg) {
        Reflections rfl = new Reflections(pkg)
        rfl.getSubTypesOf(BabbleCodecFactory.class).each {cls ->
            BabbleCodecFactory fac = cls.getConstructor().newInstance()
            String nm = fac.name()
            log.info "registering codec ${nm}"
            registerCodec(nm, fac)
        }
        rfl.getSubTypesOf(BabbleSourceFactory.class).each {cls ->
            BabbleSourceFactory fac = cls.getConstructor().newInstance()
            String nm = fac.name()
            log.info "registering source ${nm}"
            registerSource(nm, fac)
        }
        rfl.getSubTypesOf(BabbleTranslatorFactory.class).each {cls ->
            BabbleTranslatorFactory fac = cls.getConstructor().newInstance()
            String nm = fac.name()
            log.info "registering translator ${nm}"
            registerTranslator(nm, fac)
        }
        rfl.getSubTypesOf(BabbleDestinationFactory.class).each {cls ->
            BabbleDestinationFactory fac = cls.getConstructor().newInstance()
            String nm = fac.name()
            log.info "registering destination ${nm}"
            registerDestination(nm, fac)
        }
        rfl.getSubTypesOf(BabbleHookFactory.class).each {cls ->
            BabbleHookFactory fac = cls.getConstructor().newInstance()
            String nm = fac.name()
            log.info "registering hook ${nm}"
            registerHook(nm, fac)
        }
    }

    /**
     *
     * Add a new source type, by providing a factory object
     *
     * @param name The name to be used. It is up to the implementation to
     * define behaviour if the name has already been registered.
     *
     * @param fac The BabbleCodecFactory instance to associate with the given name.
     */
    public void registerCodec(String name, BabbleCodecFactory fac) {
        codecs[name] = fac
    }

    /**
     *
     * Create a new codec
     *
     * @param name The name of kind of codec - either previously defined
     * by a call to registerCodec() or an implementation defined default.
     *
     */
    public BabbleCodec createCodec(String name) {
        def fac = codecs[name]
        if (!fac) {
            throw new InvalidParameterValue("unknown source ${name}")
        }
        return fac.create()
    }

    /**
     *
     * Add a new source type, by providing a fully qualified class name.
     *
     * @param name The name to be used. It is up to the implementation to
     * define behaviour if the name has already been registered.
     *
     * @param className The fully qualified class name to lookup in the
     * loader.
     *
     */
    public void registerSource(String name, String className, String classPath) {
        def facClass = (Class<? extends BabbleSourceFactory>) loadThing(className, classPath)
        sources[name] = facClass.getConstructor().newInstance()
    }

    /**
     *
     * Add a new source type, by providing a fully qualified class name.
     *
     * @param name The name to be used. It is up to the implementation to
     * define behaviour if the name has already been registered.
     *
     * @param srcFac The BabbleSourceFactory instance to associate with the given name.
     */
    public void registerSource(String name, BabbleSourceFactory srcFac) {
        sources[name] = srcFac
    }

    /**
     *
     * Create a new instance of a BabbleSource.
     *
     * @param name The name of kind of source - either previously defined
     * by a call to registerSource() or an implementation defined default.
     *
     * @param config The configuration parameters of the source.
     */
    public BabbleSource createSource(String name, Map config) {
        def fac = sources[name]
        if (!fac) {
            throw new InvalidParameterValue("unknown source ${name}")
        }
        return fac.create(this, config)
    }

    /**
     *
     * Add a new hook type, by providing a fully qualified class name.
     *
     * @param name The name to be used. It is up to the implementation to
     * define behaviour if the name has already been registered.
     *
     * @param className The fully qualified class name to lookup in the
     * loader.
     *
     */
    public void registerHook(String name, String className, String classPath) {
        def facClass = (Class<? extends BabbleHookFactory>) loadThing(className, classPath)
        hooks[name] = facClass.getConstructor().newInstance()
    }

    /**
     *
     * Add a new hook type, by providing a fully qualified class name.
     *
     * @param name The name to be used. It is up to the implementation to
     * define behaviour if the name has already been registered.
     *
     * @param hookFac The BabbleHookFactory instance to associate with the given name.
     */
    public void registerHook(String name, BabbleHookFactory hookFac) {
        hooks[name] = hookFac
    }

    /**
     *
     * Create a new instance of a BabbleHook.
     *
     * @param name The name of kind of hook - either previously defined
     * by a call to registerHook() or an implementation defined default.
     *
     * @param config The configuration parameters of the hook.
     */
    public BabbleHook createHook(String name, Map config) {
        def fac = hooks[name]
        if (!fac) {
            throw new InvalidParameterValue("unknown hook ${name}")
        }
        return fac.create(this, config)
    }

    /**
     *
     * Add a new translator type, by providing a fully qualified class name.
     *
     * @param name The name to be used. It is up to the implementation to
     * define behaviour if the name has already been registered.
     *
     * @param className The fully qualified class name to lookup in the
     * loader.
     *
     */
    public void registerTranslator(String name, String className, String classPath) {
        def facClass = (Class<? extends BabbleTranslatorFactory>) loadThing(className, classPath)
        translators[name] = facClass.getConstructor().newInstance()
    }

    /**
     *
     * Add a new translator type, by providing a fully qualified class name.
     *
     * @param name The name to be used. It is up to the implementation to
     * define behaviour if the name has already been registered.
     *
     * @param txFac The BabbleTranslatorFactory instance to associate with the given name.
     */
    public void registerTranslator(String name, BabbleTranslatorFactory txFac) {
        translators[name] = txFac
    }

    /**
     *
     * Create a new instance of a BabbleTranslator.
     *
     * @param name The name of kind of translator - either previously defined
     * by a call to registerTranslator() or an implementation defined default.
     *
     * @param config The configuration parameters of the translator.
     */
    public BabbleTranslator createTranslator(String name, Map config) {
        def fac = translators[name]
        if (!fac) {
            throw new InvalidParameterValue("unknown translator ${name}")
        }
        return fac.create(this, config)
    }

    /**
     *
     * Add a new destination type, by providing a fully qualified class name.
     *
     * @param name The name to be used. It is up to the implementation to
     * define behaviour if the name has already been registered.
     *
     * @param className The fully qualified class name to lookup in the
     * loader.
     *
     */
    public void registerDestination(String name, String className, String classPath) {
        def facClass = (Class<? extends BabbleDestinationFactory>) loadThing(className, classPath)
        destinations[name] = facClass.getConstructor().newInstance()
    }

    /**
     *
     * Add a new destination type, by providing a fully qualified class name.
     *
     * @param name The name to be used. It is up to the implementation to
     * define behaviour if the name has already been registered.
     *
     * @param dstFac The BabbleDestinationFactory instance to associate with the given name.
     */
    public void registerDestination(String name, BabbleDestinationFactory dstFac) {
        destinations[name] = dstFac
    }

    /**
     *
     * Create a new instance of a BabbleDestination.
     *
     * @param name The name of kind of destination - either previously defined
     * by a call to registerDestination() or an implementation defined default.
     *
     * @param config The configuration parameters of the destination.
     */
    public BabbleDestination createDestination(String name, Map config) {
        def fac = destinations[name]
        if (!fac) {
            throw new InvalidParameterValue("unknown destination ${name}")
        }
        return fac.create(this, config)
    }

    /**
     *
     * Return usage information for all registered plugins.
     *
     */
    String usage() {
        List<String> parts = []
        parts << \
"""
====================
Babble Configuration
====================
"""

        parts << "Sources\n=======\n"
        sources.each { nm, fac ->
            parts << "${nm}\n${'-'*(nm.size())}"
            parts << fac.usage()
        }

        parts << "Destinations\n============\n"
        destinations.each { nm, fac ->
            parts << "${nm}\n${'-'*(nm.size())}"
            parts << fac.usage()
        }

        parts << "Translators\n===========\n"
        translators.each { nm, fac ->
            parts << "${nm}\n${'-'*(nm.size())}"
            parts << fac.usage()
        }

        parts << "Hooks\n=====\n"
        hooks.each { nm, fac ->
            parts << "${nm}\n${'-'*(nm.size())}"
            parts << fac.usage()
        }
        return parts.join('\n')
    }

    private Class loadThing(String className, String classPath) {
        def loader = ClassLoader.getSystemClassLoader()
        if (classPath) {
            List urls = [new URL(classPath)]
            loader = new URLClassLoader(urls as URL[], loader)
        }
        return Class.forName(className, true, loader)
    }
}
