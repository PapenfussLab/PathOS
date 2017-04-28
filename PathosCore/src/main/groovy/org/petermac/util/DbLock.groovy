/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.util

import groovy.sql.Sql
import groovy.util.logging.Log4j

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Utility class for locking the database for loader operations
 *
 * User: Ken Doig
 * Date: 08-Dec-2015
 */

@Log4j
class DbLock
{
    static Sql    sql       = null
    static String dbname    = null
    static double staleLock = 120        // Lock is stale if more than 2 hourso ld

    /**
     * Constructor to define DB and optional stale lock timeout
     *
     * @param dbname        DB to lock
     * @param staleMin      Minutes after which lock is considered stale (2 hours default)
     */
    DbLock( String dbname, double staleMin = 120 )
    {
        def db      = new DbConnect( dbname )
        this.dbname = dbname
        sql         = db.sql()
        staleLock   = staleMin
    }

    /**
     * Create the DB Lock table
     *
     * @return  Map of lock status
     */
    static Map createLock()
    {
        def create= """
                    create table if not exists db_lock
                    (
                        id          int unique,
                        host        varchar(50),
                        pid         varchar(50),
                        datetime    datetime
                    )
                    """

        assert ! sql.execute( create ), "DB Lock create failed"
        assert ! sql.execute( "insert into db_lock values ( 1, '', 0, '2000-01-01 12:00:00.0')" ) : "DB Lock insert failed"

        return setLock()
    }

    /**
     * Set DB Lock with current time
     *
     * @return  Map of lock status
     */
    static Map setLock()
    {
        String pid = new File( '/proc/self' )?.getCanonicalFile()?.getName()

        def upd =   """
                    update  db_lock
                    set     host = ${InetAddress.getLocalHost()?.getHostName()},
                            pid  = ${pid},
                            datetime = now()
                    where   id = 1;
                    """

        assert ! sql.execute( upd ), "DB Lock update failed"

        return hasLock()
    }

    /**
     * Clear DB Lock
     *
     * @param   Original lock settings
     * @return  Map of lock status
     */
    static Map clearLock( Map lockMap )
    {
        String pid  = new File( '/proc/self' )?.getCanonicalFile()?.getName()
        String host = InetAddress.getLocalHost()?.getHostName()

        if ( pid != lockMap?.pid || host != lockMap?.host )
        {
            log.error( "Lock may have been set by a different process: Set by [${lockMap?.host}:${lockMap?.pid}] This process [${host}:${pid}]")
        }

        def upd =   """
                    update  db_lock
                    set     host = ${host},
                            pid  = ${pid},
                            datetime = '2000-01-01 12:00:00.0'
                    where   id = 1;
                    """

        assert ! sql.execute( upd ), "DB Lock update failed"

        return hasLock()
    }

    /**
     * Get the current lock for this DB
     *
     * @return          Map of the current lock status
     */
    static Map hasLock()
    {
        //  Setup get lock select
        //
        def sel = 	"""
                    select	host,
                            pid,
                            datetime,
                            (now() - datetime)/60 as ago     -- lock set minutes ago
                    from	db_lock
                    where	id = 1
                    """

        def    rows  = sql.rows(sel)
        assert rows.size() == 1, log.fatal( "Couldn't find DB lock record for ${dbname}" )

        Map lockMap = [ host: rows[0].host, pid: rows[0].pid, datetime: rows[0].datetime, ago: rows[0].ago ]
        log.debug( "Current Lock ${lockMap}")

        //  Check for stale lock
        //
        if (( lockMap.ago as double ) > staleLock )
            return [:]

        return lockMap
    }
}
