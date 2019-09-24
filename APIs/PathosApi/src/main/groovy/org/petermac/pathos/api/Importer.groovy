package org.petermac.pathos.api

/**
 *
 * An interface for methods for importing data into PathOS.
 *
 */
interface Importer {

    /**
     *
     * Gather data, and invoke receive() zero or more times.
     *
     * @param receiver
     *        An object to which messages should be passed.
     *
     */
    void importData(ImportReceiver receiver);
}
