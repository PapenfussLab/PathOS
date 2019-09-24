package org.petermac.pathos.api

interface ImporterFactory {

    /**
     *
     * Create a new ExportReceiver.
     *
     * @param params
     *        A map object containing implementation specific
     *        information for the importer.
     * @return an object which implements the Importer interface.
     */
    Importer create(Map params);
}
