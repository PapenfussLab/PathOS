package org.petermac.pathos.api

interface ExportReceiverFactory {

    /**
     *
     * Create a new ExportReceiver.
     *
     * @param params
     *        A map object containing implementation specific
     *        information for the receiver.
     * @return an object which implements the ExportReceiver interface.
     */
    ExportReceiver create(Map config);
}
