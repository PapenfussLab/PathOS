/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

import groovy.util.logging.Log4j

/**
 * A class for File manipulation utilities
 *
 * Author: Ken Doig
 * Date: 30/07/13
 * Time: 9:59 AM
 */

@Log4j
class FileUtil
{
    /**
     * Convert URL to File
     *
     * @param   url     URL of a text file
     * @param   fname   File name to create
     * @param   dir     optional directory for created file
     * @return          File of URL contents
     */
    static File copyUrl( String url, String fname, String dir = null )
    {
        def tf
        if ( dir )
            tf = new File(dir,fname)
        else
            tf = new File(fname)

        def tmpFile = tf.newOutputStream()
        tmpFile << new URL(url).openStream()
        tmpFile.close()

        return new File( tf.absolutePath )
    }

    /**
     * Convert URL to File
     *
     * @param   url
     * @return  File of URL contents
     */
    static File copyUrl( String url )
    {
        String fname = "tmp_${Math.abs(new Random().nextInt() % 10000 + 1)}.tmp"

        return copyUrl( url, fname )
    }

    /**
     * Create a unique temporary file which is deleted on exit
     *
     * @param prefix    Optional file name prefix [mp_]
     * @param suffix    Optional suffix [.tmp]
     * @return          File with unique name
     */
    static File tmpFile( String prefix = 'mp_', String suffix = '.tmp' )
    {
        File tmpFile = File.createTempFile( prefix, suffix)
        tmpFile.deleteOnExit()

        return tmpFile
    }

    /**
     * Create a peristent 'temporary' file in /tmp by default
     *
     * @param path      Optional path to file [/tmp]
     * @param prefix    Optional file prefix [tmp_]
     * @return          Unique File with a timestamp embedded file name
     */
    static File tmpFixedFile( String path = '/tmp', String prefix = 'tmp_')
    {
        def now       = new Date()
        def timeStamp = now.format("yyyyMMdd'T'HHmmss.SSS")

        File tmpFile = new File( path, prefix + timeStamp)
        tmpFile.deleteOnExit()

        return tmpFile
    }
}
