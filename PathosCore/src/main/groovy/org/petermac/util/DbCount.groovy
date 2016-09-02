/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

import groovy.util.logging.Log4j

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Count and compare database table counts
 *
 * User: doig ken
 * Date: 22/11/2013
 * Time: 2:52 PM
 */

@Log4j
class DbCount
{
    /**
     * Count the rows in all tables (or given List)
     *
     * @param dbase     Database schema to use
     * @param tables    Optional list of tables to count
     * @return          Map of [table:rows,...]
     */
    Map countTables( String dbase, List tables = [] )
    {
        //  Connect to db
        //
        def db   = new DbConnect( dbase )
        def sql  = db.sql()
        def schema = db.schema// LE Edit

        Map tbl = [:]

        def qry = 	"""
                    select  table_name,
                            table_rows
                    from    information_schema.tables
                    where   table_schema = ${schema}
                    """

        def rows = sql.rows(qry)

        for ( row in rows )
        {
            if ( ! tables || row.table_name in tables )
                tbl << [(row.table_name): row.table_rows]
        }

        return tbl
    }

    /**
     * Compare two sets of database tables and return a comparison
     *
     * @param dbaseA    Primary database to compare
     * @param dbaseB    Secondary database to compare
     * @param tables    Optional list of tables to compare
     * @return          Map of [table: [rows:<rows>, diff:<delta rows>, pct:<delta %>],...]
     */
    Map compare( String dbaseA, String dbaseB, List tables = [] )
    {
        Map dbA = countTables( dbaseA, tables )
        Map dbB = countTables( dbaseB, tables )

        Map diff = [:]
        for ( tbl in dbA.keySet() )
        {
            int rowA = dbA[tbl] as int
            int rowB = (dbB[tbl] ?: -99999) as int      // Table might not exist in B

            diff << [ (tbl): [ rows: rowA, diff : rowA-rowB, pct: ((rowA-rowB) as Double)*100.0 / (rowA as Double)] ]
        }

        return diff
    }
}
