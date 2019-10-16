package org.petermac.babble.api

/**
 *
 * A BabbleSource is an object which obtains messages and
 * delivers them to a BabbleDestination.
 *
 * A BabbleSource object will be created once for each channel,
 * and it's slurpMessages method will be called once.
 *
 * The logical workflow is that all source, translator and destination
 * objects are created (via the Factory pattern), and then each channel
 * is run from its own thread.
 *
 * It is up to the implementation to determine whether exceptions that
 * occur while obtaining or delivering a message should be isolated, and
 * processing should continue, or whether the exception should propagate
 * and the containing thread be terminated.
 *
 */
interface BabbleSource {
    /**
     *
     * An invocation of the stop() method should cause the
     * implementation to obtain and deliver no further messages.
     *
     */
    void stop()

    /**
     *
     * The slurpMessages method should obtain zero or more messages
     * and pass each to the given BabbleDestination object in turn.
     *
     */
    void slurpMessages(BabbleDestination dest)
}

/**
 *
 * A BabbleSourceFactory implements the Factory design pattern,
 * to create BabbleSource objects.
 *
 */
interface BabbleSourceFactory {
    /**
     *
     * Default name for sources created by this factory.
     *
     */
    String name()

    /**
     *
     * Create a new BabbleSource.
     *
     * @param config A map containing the configuration information for the
     * BabbleSource object to be created.
     *
     */
    BabbleSource create(BabbleConfigurator confator, Map config)

    /**
     *
     * Return restructured text describing how the source behaves.
     *
     */
    String usage()
}
