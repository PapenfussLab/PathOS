package org.petermac.babble.api

/**
 *
 * A BabbleDestination is an object which is passed messages
 * and delivers them to some destination such as a File, a REST API, etc.
 *
 * A BabbleDestination object will be created once for each channel,
 * and it's deliverMessage method will be called once with each message.
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
interface BabbleDestination {
    /**
     *
     * Deliver a message.
     *
     * @param src The source of the message. Supplied so that the
     * destination can signal that no further messages should be
     * sent by invoking src.stop().
     *
     * @param msg The message to deliver.
     *
     */
    Object deliverMessage(BabbleSource src, Object msg)

    /**
     *
     * The close method will be invoked to cause an orderly shutdown of the
     * destination.
     *
     */
    void close()
}

/**
 *
 * A BabbleDestinationFactory implements the Factory design pattern,
 * to create BabbleDestination objects.
 *
 */
interface BabbleDestinationFactory {
    /**
     *
     * Default name for translators created by this factory.
     *
     */
    String name()

    /**
     *
     * Create a new BabbleDestination.
     *
     * @param config A map containing the configuration information for the
     * BabbleDestination object to be created.
     *
     */
    BabbleDestination create(BabbleConfigurator confator, Map config)

    /**
     *
     * Return restructured text describing how the destination behaves.
     *
     */
    String usage()
}
