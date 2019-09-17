/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.util

import groovy.sql.Sql
import groovy.util.logging.Log4j
import org.petermac.util.Locator

/**
 * Datbase Connect class for creating a JDBC connection to a set of PathOS databases
 *
 * User: Ken Doig
 * Date: 10 April 13
 */

@Log4j
class DbConnect
{
    String  host        // DB host
    String  port        // DB port
    String  schema      // DB schema
    String  user        // DB user
    String  pass        // DB pass
    String  driver
    String  jdbc
    String  hibernateXml = '' // Hibernate XML configuration file
    boolean ok = false

    static Locator  loc  = Locator.instance

    /**
     * Database Connector Constructor
     *
     * Supports all current PathOS databases
     *
     * @param env   Enivornment you think you're on currently
     * if executed targeting a different env, it will exit
     */
    DbConnect( String env )
    {
        ok = false

        host   = loc.dbHost
        port   = loc.dbPort
        schema = loc.dbSchema
        jdbc   = "jdbc:mysql://${host}:${port}/${schema}"
        user   = loc.dbUsername
        pass   = loc.dbPassword
        driver = 'com.mysql.jdbc.Driver'
        hibernateXml = "META-INF/pathosLoaderContext.xml"

        if (env == loc.pathosEnv) { ok = true }

        if ( ! ok )
        {
            log.fatal( "Invalid database ${env} - currrent environment is ${loc.pathosEnv}")
            System.exit(1)
        }
    }

    /**
     * Is DB host and schema valid ?
     *
     * @return  true if valid
     */
    Boolean valid( Boolean orm = false )
    {
        if ( orm ) return( ok && hibernateXml != '' )

        return( ok )
    }

    /**
     * Create an SQL instance for this database
     *
     * @return  Sql object connected to database
     */
    Sql sql()
    {
        if ( ! user )
        {
            log.fatal( "Database not set host: $host schema: $schema" )
            return null
        }

        //	Connect to Database
        //
        def sql = Sql.newInstance( this.jdbc, this.user, this.pass, this.driver )
        assert( sql )
        log.info( "Connected to DB host: $host schema: $schema" )

        // Check that the version in the database is the same
        // as what we're running.
        //
        PathosVersion.instance.checkDatabaseVersion(sql, schema)

        return sql
    }
}
