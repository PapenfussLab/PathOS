package org.petermac.pathos.api

/**
 *
 * An interface for importing messages into PathOS.
 *
 */

interface ImportReceiver {

    /**
     *
     * Import/Publish data from PathOS.
     *
     * Implementations should assume multiple threads could
     * concurrently invoke this method.
     *
     * @param domain
     *      The domain of the data object being exported.
     *      Examples: "patient", "sequence"
     *
     * @param action
     *      The action expected of the receiver.
     *      Examples: "create", "update", "delete"
     *
     * @param data
     *      The data payload associated with the export.
     *      Example: Map containing patient data.
     */
    void receive(String domain, String action, Object data);
}

