package org.petermac.pathos.api

/**
 *
 * An interface for the export of messages from PathOS.
 *
 */

interface ExportReceiver {

    /**
     *
     * Export/Publish data from PathOS.
     *
     * Implementations should assume multiple threads could
     * concurrently invoke this method.
     *
     * @param domain
     *      The domain of the data object being exported.
     *      Examples: "report", "patient"
     *
     * @param action
     *      The action expected of the receiver.
     *      Examples: "publish", "requestUpdate"
     *
     * @param data
     *      The data payload associated with the export.
     *      Example: Map containing metadata and PDF for a report.
     */
    void receive(String domain, String action, Object data);

    /**
     * Close the ExportReceiver.
     * After calling this method no more calls to receive() will be made.
     */
    void close();
}
