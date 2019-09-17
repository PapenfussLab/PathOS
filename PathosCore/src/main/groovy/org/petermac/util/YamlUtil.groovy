/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.util

import org.yaml.snakeyaml.Yaml

/**
 * Description:
 *
 * YAML processing utilities
 *
 * Author: Ken Doig
 * Date: 30-mar-17
 */
class YamlUtil
{
 
    /**
     * Load a yaml formatted file as a stream of records
     *
     * @param   yfile   File of YAML to read
     * @return          List of record Objects
     */
    public static List load( File yfile ) throws IOException
    {
        Yaml parser = new Yaml()

        if ( ! yfile.canRead()) return []

        InputStream ios = new FileInputStream( yfile )

        //  Load all records in stream
        //
        List records = []
        for ( data in parser.loadAll( ios ))
        {
            records << data
        }

        return records
    }


    /**
     * Load a yaml formatted string as a stream of records
     *
     * @param   yfile   File of YAML to read
     * @return          List of record Objects
     */
    public static List load( String yString ) throws IOException
    {
        Yaml parser = new Yaml()

        //  Load all records in the string
        //
        List records = []
        def loadAll = parser.loadAll( yString )

        for ( data in loadAll)
        {
            records << data
        }

        return records
    }

    /**
     * Dump an Object to a file in default Yaml structure
     *
     * @param   yfile   dump File
     * @param   map     java Object to dump
     * @throws IOException
     */
    public static void dump( File yfile, Object map ) throws IOException
    {
        yfile << toYaml( map )
    }

    /**
     * Dump an Object to a String in default Yaml structure
     *
     * @param   yfile   dump File
     * @param   map     java Object to dump
     * @throws IOException
     */
    public static String toYaml( Object map ) throws IOException
    {
        Yaml dumper = new Yaml()

        return dumper.dump( map )
    }
}

