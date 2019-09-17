/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.annotate

import com.mysql.jdbc.MysqlDataTruncation
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import org.petermac.util.DbConnect
import org.petermac.util.Tsv

/**
 * Created for PathOS.
 *
 * Description:
 *
 * This is the base class for variant annotation data sources. A data source can be a a basic TSV
 * file with a header and data columns and optional meta data describing the columns. A more
 * complex data source may involve an extract or generate process followed by a loader phase.
 *
 * User: Ken Doig
 * Date: 22-Nov-2014
 */

@Log4j
class DataSource
{
    /**
     * Enum for supported data sources
     */
    static enum DS
    {
        Mutalyzer(  'MUT'),
        Annovar(    'ANV'),
        VEP(        'VEP'),
        IARC(       'IARC'),
        Oncotator(  'ONC'),
        Cosmic(     'COS'),
        PeterMac(   'PMC'),
        Invitae(    'INV'),
        MyVariant(  'MYV')

        DS( String code ) { this.code = code }
        private final String code
        public String code() { return code }
        public String toString() { return code }
    }

    static sql                  //  SQL object for RDB queries

    /**
     * Constructor for a Datasource
     *
     * @param rdb   Database of cache eg pa_uat, pa_local, pa_prod
     *              pa_prod is the master production cache and shouldn't be used directly
     */
    DataSource(String rdb) {
        ensureDatabaseConnection(rdb)
    }

    /**
     * Connect to the database if necessary.
     */
    static void ensureDatabaseConnection(String rdb) {
        if (sql && sql.execute('select 1 from dual')) {
            return
        }

        //  Connect to db
        //
        def db = new DbConnect(rdb)
        sql    = db.sql()

        if (!sql.execute('select 1 from dual')) {
            sql.close()
            throw new Exception("Couldn't connect to database [$rdb]")
        }
    }

    /**
     * Load a TSV file from disk
     *
     * @param   intsv   Input file name
     * @return          Tsv object of file
     */
    static Tsv loadTsv( String intsv )
    {
        File inf = new File(intsv)
        if ( ! inf.exists())
        {
            log.error( "File [${intsv}] doesn't exist")
            return null
        }

        Tsv tsv = new Tsv( )
        tsv.load( true )
        return tsv
    }

    /**
     * Load a set of TSV files from disk
     *
     * @param   wildCard    File wildcard pattern
     * @return              List of TSV objects loaded
     */
    static List<Tsv> loadTsvFiles( String wildCard )
    {
        List<Tsv> tsvs = []

        //  Find the files using Ant wildcards
        //
        def filescan = new AntBuilder().fileScanner
                            {
                                fileset(dir:".") { include( name: wildCard) }
                            }

        //  Process all files found
        //
        for ( tsvf in filescan )
        {
            assert tsvf instanceof File
            Tsv tsv = new Tsv( tsvf )
            tsv.load( true )
            tsvs << tsv
        }

        //  Choose last if there are multiple matching dirs
        //
        log.info( ">>> ${wildCard} Found [${tsvs.size()}]" )

        return tsvs
    }

    /**
     * Save a TSV array into database
     *
     * @param   tsv     TSV to save
     * @return          number of rows saved
     */
    static int saveTsv( String ds, Closure key, Tsv tsv )
    {
        int nr = 0

        List       cols = tsv.getCols()
        List<List> rows = tsv.getRows()

        for ( List<String> row in rows )
        {
            assert cols.size() == row.size(), "Row [${row.size()}] doesn't match Cols [${cols.size()}]"

            saveRow( ds, key, row, cols, )
        }

        return nr
    }

    /**
     * Save a TSV row into cache
     *
     * @param dataSource
     * @param key
     * @param row
     * @param cols
     * @return
     */
    static int saveRow( String dataSource, Closure key, List row, List cols )
    {
        int nc = 0

        cols.eachWithIndex
        {
            String col, int i ->

                def hgsvg = key(row)
                List ids = saveValue( dataSource, hgsvg, col, row[i] as String )

                log.info( "Added ${col} ${row[i]} = ${ids}")
        }

        return nc
    }

    /**
     * Save a single value into cache
     *
     * @param dataSource    DataSource enum
     * @param key           Key of value
     * @param col           Name of attribute
     * @param val           Value to cache
     * @param alias         Alternative key for value
     * @return              List of inserted rows
     */
    static List saveValue( String dataSource, String key, String col, String val, String alias=null )
    {
        log.debug( "saveValue ds=${dataSource} key=${key} val=${val} alias=${alias}")

        return sql.executeInsert(
                    """
                    insert into ano_variant
                    (
                        data_source,
                        hgvsg,
                        alias,
                        attr,
                        value,
                        created
                    )
                    values
                    (
                        ${dataSource},
                        ${key},
                        ${alias ?: ''},
                        ${col},
                        ${val},
                        now()
                    )
                    """)
    }


    /**
     * Save a single value into cache
     *
     * @param params    Map of parameters
     * @param enum
     * @param key       Key of value
     * @param col       Name of attribute
     * @param val       Value to cache
     * @param alias     Alternative key for value
     * @return          List of inserted rows
     */
    static List saveValueMap( Map params )
    {

        log.debug( "saveValueMap ds=${params.dataSource} key=${params.key} val=${params.val} alias=${params.alias}")

        try
        {
            return sql.executeInsert(
                    """
                    insert into ano_variant
                    (
                        data_source,
                        hgvsg,
                        alias,
                        attr,
                        value,
                        created,
                        hgvsc,
                        hgvsp,
                        version,
                        gene,
                        organism,
                        build,
                        classification
                    )
                    values
                    (
                        ${params.data_source},
                        ${params.hgvsg},
                        ${params.alias ?: ''},
                        ${params.attr},
                        ${params.value},
                        now(),
                        ${params.hgvsc ?: ''},
                        ${params.hgvsp ?: ''},
                        ${params.version ?: ''},
                        ${params.gene ?: ''},
                        ${params.organism ?: 'human'},
                        ${params.build ?: 'hg19'},
                        ${params.classification ?: 'Unclassified'}
                    )
                    """)
        }
        catch( MySQLIntegrityConstraintViolationException e )
        {
            log.error( "Exiting: Couldn't insert into ano_variant" + e.toString())
            return []
        }
        catch( MysqlDataTruncation e )
        {
            log.error( "Exiting: property truncated in ano_variant" + e.toString())
            return []
        }
    }

    /**
     * Find keys not in cache
     *
     * @param dataSource    DataSource type
     * @param vars          List of keys to check
     * @return              List of keys not in cache
     */
    static List notInCache( def dataSource, List vars )
    {
        List nic = []
        for ( var in vars )
        {
//            def res = sql.rows("select hgvsg from ano_variant where data_source=${dataSource} and (hgvsg=${var} or alias=${var})")
            def res = sql.rows("select hgvsg from ano_variant where data_source=${dataSource} and (hgvsg=${var})")
            log.debug("notInCache: ${dataSource} ${var} ${res.size()}")
            if ( res.size() == 0 )
            {
                nic << var
            }
        }

        log.info( "Found ${vars.size() - nic.size()}/${vars.size()} variants in ${dataSource} cache" )
        return nic
    }

    /**
     * Get a List of values from cache
     *
     * @param dataSource    DataSource type
     * @param vars          List of keys to check
     * @return              List of values in cache
     */
    static List getValues( def dataSource, List<String> vars )
    {
        List ret = []
        for ( var in vars )
        {
            ret << getValue( dataSource, var )
        }

        log.info( "Found ${ret.count{it}} variants in ${dataSource} cache" )
        return ret
    }

    /**
     * Get a value from cache  Todo: add organism = human, build = GRCh37
     * Todo: remove use index() which is needed for legacy bioinf-ensembl:mysql DB
     * This DB ignores all indexes except for index on data_source so we need to force the use of
     * hgvsg and data_source indexes
     *
     * @param dataSource    DataSource type
     * @param vars          key to get
     * @return              value in cache
     */
    static String getValue( def dataSource, String key )
    {
//        def res = sql.rows("select value from ano_variant use index(ano_variant_idx1,ano_variant_idx3,ano_variant_idx8) where data_source=${dataSource} and (hgvsg=${key} or alias=${key})")
        def res = sql.rows("select value from ano_variant where data_source=${dataSource} and hgvsg=${key}")
        log.debug("getValue: ${dataSource} ${key} ${res.size()}")
        if ( res.size() == 0 )
            return null

        return res[0].value
    }

    /**
     * Get a List of values from cache
     *
     * @param dataSource    DataSource type
     * @param vars          List of keys to check
     * @return              List of values in cache
     */
    static List<Map> getValueMaps( def dataSource, List<String> vars )
    {
        List ret = []
        for ( var in vars )
        {
            ret << getValueMap( dataSource, var )
        }
        log.info( "Found ${ret.count{it}} variants in ${dataSource} cache" )
        return ret
    }

    /**
     * Get a value from cache
     *
     * @param dataSource    DataSource type
     * @param vars          key to get
     * @return              value in cache
     */
    static Map getValueMap( String dataSource, String key )
    {
        String val = getValue( dataSource, key )
        if ( ! val ) return null

        def js = new JsonSlurper()

        return js.parseText(val) as Map
    }

    /**
     * Get a value from cache
     *
     * @param vars          key to get
     * @return              value in cache
     */
    static List<Map> getValueMaps(  String dataSource, List<String> vars )
    {
        def js = new JsonSlurper()

        List<Map> vms = []
        for ( var in vars )
        {
            String v = getValue( dataSource, var )
            if ( v )
            {
                vms << ( js.parseText( v ) as Map )
            }
        }

        return vms
    }

    /**
     * Delete keys from cache
     *
     * @param dataSource    DataSource type
     * @param vars          List of keys to delete
     * @return
     */
    static void removeFromCache( def dataSource, List vars )
    {
        for ( var in vars )
        {
            sql.execute("delete from ano_variant where data_source=${dataSource} and (hgvsg=${var} or alias=${var})")
        }
    }

    /**
     * Delete gene from cache
     *
     * @param dataSource    DataSource type
     * @param gene          Gene to delete
     * @return
     */
    static void removeGeneFromCache( def dataSource, String gene )
    {
        sql.execute("delete from ano_variant where data_source = ${dataSource} and gene = ${gene}")
    }

    /**
     * Delete a genomic region from cache
     *
     * @param dataSource    DataSource type
     * @param chr           Region chromosome "chrNN"
     * @param ts_start      Region transcript start
     * @param ts_stop       Region transcript stop
     */
    static void removeRegionFromCache( def dataSource, String chr, int ts_start, int ts_stop )
    {
        String del = "delete from ano_variant where '${chr+':g.'+Integer.toString(ts_start)}' < hgvsg and hgvsg < '${chr+':g.'+Integer.toString(ts_stop)}' and data_source = '${dataSource}'"
        log.info( "Running : ${del}")
        sql.execute( del )
    }

    /**
     * Return a List of dataSources for this key
     *
     * @param key   Key to lookup in cache
     * @return      List of distinct dataSources
     */
    static List getDataSources( String key )
    {
        List dss = []

        def rows = sql.rows("select distinct data_source from ano_variant where (hgvsg=${key} or alias=${key})")
        for ( Map row in rows )
        {
            dss << row.data_source
        }

        return dss
    }

    /**
     * Delete entire data source from cache
     *
     * @param dataSource    DataSource type
     * @return
     */
    static boolean deleteAll( String dataSource )
    {
        sql.execute("delete from ano_variant where data_source like ${dataSource + '%'}")
    }
}
