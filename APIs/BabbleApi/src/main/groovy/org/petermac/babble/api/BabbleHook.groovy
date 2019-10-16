package org.petermac.babble.api

/**
 *
 * A BabbleHook is an object that gets to observe a message before
 * it is passed out of a source or in to a destination, and after
 * it has been passed out of a source or to a destination.
 *
 * For a source hook, the calling sequence is:
 * <ul>
 *  <li>source obtains message</li>
 *  <li>source before hook is invoked</li>
 *  <li>destination deliverMessage is invoked</li>
 *  <li>source after hook is invoked</li>
 * </ul>
 *
 * For a destination hook, the calling sequence is:
 * <ul>
 *  <li>destination before hook is invoked</li>
 *  <li>destination deliverMessage is invoked</li>
 *  <li>destination after hook is invoked</li>
 * </ul>
 *
 */
interface BabbleHook {
    Object before(Object msg)

    Object after(Object msg)

    void close()
}

interface BabbleHookFactory {
    /**
     *
     * Default name for translators created by this factory.
     *
     */
    String name()

    /**
     *
     * Create a new BabbleHook.
     *
     * @param config A map containing the configuration information for the
     * BabbleHook object to be created.
     *
     */
    BabbleHook create(BabbleConfigurator confator, Map config)

    /**
     *
     * Return restructured text describing how the destination behaves.
     *
     */
    String usage()
}
