package org.petermac.babble.api

import java.io.Reader

interface BabbleCodec {
    /**
     * Encode a data object into a serialized string form.
     */
    String encode(Object obj)

    /**
     * Decode a serialized object to produce a data object.
     */
    Object decode(String str)

    /**
     * Decode a file/stream to produce a series of data objects.
     */
    Iterator<Object> decodeAll(Reader inp)
}

interface BabbleCodecFactory {
    String name()
    BabbleCodec create()
}
