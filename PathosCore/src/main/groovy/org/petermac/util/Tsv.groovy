/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.util

import groovy.util.logging.Log4j

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Utility class for manipulating TSV files and data
 *
 * Data is held in tsvMap
 * where: tsvMap = [ rows: List<List<String>>, preamble: List<String>, cols: List<Map>]
 *          rows        List of rows, each row being a List of fields
 *          preamble    List of initial ## lines preceding header and data
 *          cols        List<Map> of columns =[ name: String ]
 *
 * User: Ken Doig
 * Date: 22/06/2014
 * Time: 6:15 PM
 */

@Log4j
class Tsv
{
    private String      filename
    private InputStream inStream
    private Map         tsvMap = null

    /**
     * Empty constructor
     */
    Tsv()
    {
        this.filename = 'empty'
        this.inStream = null
        this.tsvMap   = [rows: [], preamble: [], cols:[]]
    }

    /**
     * Constructor for URL
     *
     * @param url
     */
    Tsv( String url )
    {
        this.filename = url
        this.inStream = new URL(url).openStream()
        this.tsvMap   = [rows: [], preamble: [], cols:[]]
    }

    /**
     * Constructor for File
     *
     * @param file
     */
    Tsv( File file )
    {
        this.filename = file.name
        this.inStream = file.newInputStream()
        this.tsvMap   = [rows: [], preamble: [], cols:[]]
    }

    /**
     * Retrieve inStream
     *
     * @return
     */
    public InputStream getInStream()
    {
        return inStream
    }

    /**
     * Retrieve filename
     *
     * @return
     */
    public String getFilename()
    {
        return filename
    }

    /**
     * Set inStream
     *
     * @return
     */
    public void setInStream( InputStream is )
    {
        inStream = is
    }

    /**
     * Set filename
     *
     * @return
     */
    public void setFilename( String fn )
    {
        filename = fn
    }

    /**
     * Retrieve tsvMap (makes a deep copy)
     *
     * @return
     */
    public Map getTsvMap()
    {
        if ( ! tsvMap ) return [:]

        Map tm = [:]
        tm.preamble = new ArrayList( tsvMap.preamble as List )
        tm.cols     = new ArrayList( tsvMap.cols     as List )
        tm.rows     = []
        for ( List row in tsvMap.rows )
        {
            tm.rows << new ArrayList( row )
        }
        return tm
    }

    /**
     * Set tsvMap by creating new lists (makes a deep copy)
     *
     * @param tsv   tsvMap to copy
     */
    public void setTsvMap( Map tsv)
    {
        if ( ! tsv ) return

        tsvMap.preamble = new ArrayList( tsv.preamble as List )
        tsvMap.cols     = new ArrayList( tsv.cols     as List )
        setRows( tsv.rows )
    }

    /**
     * Returns a List of column names for TSV
     *
     * @return  List of column headers
     */
    public List<String> getCols()
    {
        if ( ! tsvMap ) return []

        List<Map> colMaps = tsvMap.cols
        colMaps.collect{ it.name }
    }

    /**
     * Set a List of column names for TSV
     */
    private void setCols( List cols )
    {
        List l = []
        cols.each {l << [name:it]}
        tsvMap.cols = l
    }

    /**
     * Get all rows of table
     *
     * @return  List of Lists (fields of row)
     */
    public List<List> getRows()
    {
        if ( ! tsvMap ) return []

        tsvMap.rows
    }

    /**
     * Get all TSV rows as a List of Maps
     *
     * @return  List<Map> for each row as a Map keyed on columns name
     */
    public List<Map> getRowMaps()
    {
        List cols = tsvMap.cols.name

        List<Map> mapRows = []
        for ( List<String> row in tsvMap.rows )
        {
            Map mapRow = [:]

            cols.eachWithIndex { String col, int i -> mapRow << [(col): row[i]] }

            mapRows << mapRow
        }

        return mapRows
    }

    /**
     * Set rows by creating a new list (makes a deep copy)
     *
     * @param rows   rows to copy
     */
    public void setRows( List<List> rows )
    {
        tsvMap.rows = []
        for ( List row in rows )
        {
            tsvMap.rows << new ArrayList( row as List<String> )
        }
    }

    /**
     * Set preamble by creating a new list (makes a deep copy)
     *
     * @param preamble   preamble to copy
     */
    public void setPreamble( List<String> preamble )
    {
        tsvMap.preamble = new ArrayList( preamble as List<String> )
    }

    /**
     * Data rows in this TSV
     *
     * @return
     */
    public Integer nrows()
    {
        if ( ! tsvMap ) return null

        tsvMap?.rows?.size()
    }

    /**
     * Display method
     *
     * @return
     */
    public String toString()
    {
        return this.filename
    }

    /**
     * Load in file from input stream
     *
     * @param hasHeader   Read initial lines as header
     * @param type
     * @return            rows read
     */
    int load( boolean hasHeader, String type = 'TSV' )
    {
        if ( type != 'TSV' )
        {
            log.error( "Unknown file type [${type}]" )
            return 0
        }

        //  Read in file
        //
        List lines    = inStream.readLines()
        if ( ! lines.size())
        {
            log.error( "No lines found in ${inStream}")
            return 0
        }

        //  Process all lines and convert to a map
        //
        List preamble       = []
        List<String> header = []
        List<List>   rows   = []
        int ncol = 0
        int nlin = 0

        for ( String line in lines )
        {
            //  Save lines preceding header in preamble
            //
            if ( hasHeader && line.startsWith('##'))
            {
                preamble << line
                continue
            }

            //  First line is # style header if present
            //
            if (  hasHeader && line.startsWith('#'))
            {
                //  Split header and remove leading '#'
                //
                header = line.split('\t')
                ncol   = header.size()
                if ( ncol && header[0].startsWith('#')) header[0] = header[0].substring(1)
                hasHeader = false
                continue
            }

            //  First line is plain header if present
            //
            if (  hasHeader )
            {
                //  Split header and remove leading '#'
                //
                header = line.split('\t')
                ncol   = header.size()
                hasHeader = false
                continue
            }

            //  add a sentinel space to preserve trailing null TSV fields
            //  This is a bug in the way Groovy splits Strings
            //
            def row = (line + ' ' ).split("\t")

            //  Remove sentinel space in last field
            //
            row[row.size()-1] = row[row.size()-1].replaceAll( / $/ , '')

            if ( hasHeader && row.size() != ncol )
            {
                log.warn( "Row columns [${row.size()}] doesn't match header columns [${ncol}]")
                continue
            }

            rows << row
            ++nlin
        }

        //  Set header list removing whitespace
        //
        def cols = []
        for ( String col in header )
        {
            //  Accumulate a List of Maps (one for each column)
            //
            String name = col.replaceAll( /\s/, '_')
            cols << [ name: name]
        }

        //  Populate TsvMap
        //
        tsvMap = [ rows: rows, preamble: preamble, cols: cols ]

        return nlin
    }

    /**
     * Write out contents of TSV to file
     *
     * @param   fileName    File name to create
     * @return              true if successful
     */
    boolean write( String fileName, Boolean header = true )
    {
        if ( ! this.tsvMap )
        {
            log.error( "No data in Tsv")
            return false
        }

        //  Create empty file
        //
        File fn = new File( fileName )
        fn.delete()
        if ( ! fn.createNewFile())
        {
            log.error( "Can't create ${fn.absolutePath}")
            return false
        }
        if ( ! fn.canWrite())
        {
            log.error( "Can't write to ${fn.absolutePath}")
            return false
        }

        return write( fn, header )
    }

    /**
     * Write out contents of TSV to file
     *
     * @param   fn          File to write to
     * @return              true if successful
     */
    boolean write( File fn, Boolean header = true )
    {
        if ( ! this.tsvMap )
        {
            log.error( "No data in Tsv")
            return false
        }

        if ( header )
        {
            //  Write out meta data preamble if any
            //
            for ( pre in tsvMap.preamble )
                fn << pre + "\n"

            //  Header
            //
            fn << "#" + getCols().join("\t") + "\n"
        }

        //  Rows
        //
        List<String> rows = tsvMap.rows
        for ( row in rows )
            fn << row.join("\t") + "\n"

        return true
    }

    /**
     * Write out contents of TSV to file for the supplied columns
     * if there is no matching column an empty '' field is output
     *
     * @param   fn          File to write to
     * @param   cols        List of columns to output
     * @return              true if successful
     */
    boolean write( File fn, List cols, Boolean header = true )
    {
        //  Rebuild Rows
        //
        List newRows = []
        List<List> rows = tsvMap.rows
        List oldcols    = getCols()
        for ( List row in rows )
        {
            List newRow = []
            for ( col in cols )
            {
                int cidx = oldcols.indexOf(col)             // look for column name
                newRow << (cidx != -1 ? row[cidx] : '')     // copy across its value
            }
            newRows << newRow
        }

        tsvMap.rows = newRows

        //  Set new column headers
        //
        setCols(cols)

        return write( fn, header )
    }

    /**
     * Add a new column to a Tsv
     *
     * @param colName   Name of column for header
     * @param colData   List of data to add (must match number of rows)
     * @return          True if successful
     */
    boolean addColumn( String colName, List colData )
    {
        //  Check the number of rows of data to be added
        //
        if ( colData?.size() != tsvMap?.rows?.size())
        {
            log.error( "Incorrect number of columns to add ${colData?.size()} should be ${tsvMap?.rows?.size()}")
            return false
        }

        //  Add column header
        //
        tsvMap.cols << [ name: colName ]

        //  Add Rows
        //
        List newRows = []
        List<List> rows = tsvMap.rows
        int i = 0
        for ( List row in rows )
        {
            row << colData[i++] as String
            newRows << row
        }

        tsvMap.rows = newRows

        return true
    }

    /**
     * Add a new column to a Tsv (repeating the value for all rows)
     *
     * @param colName   Name of column for header
     * @param colData   value to add
     * @return          True if successful
     */
    boolean addColumn( String colName, String colData )
    {
        //  Add column header
        //
        tsvMap.cols << [ name: colName ]

        //  Add Rows
        //
        List newRows = []
        List<List> rows = tsvMap.rows
        for ( List row in rows )
        {
            row << colData
            newRows << row
        }

        tsvMap.rows = newRows

        return true
    }

    /**
     * Subtract one Tsv from another
     *
     * @param remove    Tsv to subtract from this Tsv
     * @param keys      List of keys to match - Keys must exist in column names
     * @return          New Tsv with set removed
     */
    Tsv minus( Tsv remove, List keys )
    {
        //  Create empty Tsv for output and set tsvMap
        //
        Tsv min = new Tsv()
        min.setTsvMap( tsvMap )

        //  Convert keys to column positions
        //
        List<Integer> keypos = []
        for ( key in keys )
        {
            List cols = getCols()
            int idx = cols.indexOf(key)
            if ( idx == -1 )
            {
                log.error( "Can't find key [${key}] in column names ${cols}")
                return null
            }
            keypos << idx
        }

        //  Collect keys and rows
        //
        Map minuend    = [:]
        for ( List row in tsvMap.rows )
        {
            List key = keypos.collect { row[it] }
            minuend += [(key.join('\t')): row]
        }

        //  Collect keys to remove from set
        //
        List subtrahend = []
        for ( List row in remove.tsvMap.rows )
        {
            List key = keypos.collect { row[it] }
            subtrahend << key.join('\t')
        }

        //  Remove all keys in remove TSV from minuend
        //
        minuend.keySet().removeAll( subtrahend as Set )

        //  Save all the remaining rows
        //
        min.setRows( minuend.values() as List<List> )

        return min
    }

    /**
     * Intersect one Tsv with another
     *
     * @param join      Tsv to intersect with this Tsv
     * @param keys      List of keys to match - Keys must exist in column names
     * @return          New Tsv with the intersection of rows
     */
    Tsv intersect( Tsv join, List keys )
    {
        //  Create new Tsv for output
        //
        Tsv ins = new Tsv()
        ins.setTsvMap( tsvMap )

        //  Convert keys to column positions
        //
        List<Integer> keypos = []
        for ( key in keys )
        {
            List cols = getCols()
            int idx = cols.indexOf(key)
            if ( idx == -1 )
            {
                log.error( "Can't find key [${key}] in column names ${cols}")
                return null
            }
            keypos << idx
        }

        //  Collect keys and rows
        //
        Map primary = [:]
        for ( List row in tsvMap.rows )
        {
            List key = keypos.collect { row[it] }
            primary += [(key.join('\t')): row]
        }

        //  Collect keys to intersect with set
        //
        List secondary = []
        for ( List row in join.tsvMap.rows )
        {
            List key = keypos.collect { row[it] }
            secondary << key.join('\t')
        }

        //  Remove all keys in remove TSV from minuend
        //
        primary.keySet().retainAll( secondary as Set )

        //  Save all the remaining rows
        //
        ins.setRows( primary.values() as List<List>)

        return ins
    }
}
