package org.petermac.babble.api

/**
 *
 * An interface for methods for translating message objects
 *
 */
interface BabbleTranslator {
    /**
     *
     * Convert an object into another object.
     *
     * @param obj   An object which should be translated.
     *
     * @return      The translated object
     *
     */
    Object translate(Object obj)
}

interface BabbleTranslatorFactory {
    /**
     *
     * Default name for translators created by this factory.
     *
     */
    String name()

    /**
     *
     * Create a new BabbleTranslator.
     *
     * @param params
     *        A map object containing implementation specific
     *        information for the translator.
     * @return an object which implements the BabbleTranslator interface.
     */
    BabbleTranslator create(BabbleConfigurator confator, Map config)

    /**
     *
     * Return restructured text describing how the translator behaves.
     *
     */
    String usage()
}
