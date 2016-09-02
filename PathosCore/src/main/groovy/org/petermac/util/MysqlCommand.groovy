/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.util

import groovy.util.logging.Log4j

import java.text.SimpleDateFormat

/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 15/04/13
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */

@Log4j
class MysqlCommand
{
    DbConnect db

    /**
     * Constructor with database name
     *
     * @param dbname    mysql database to connect to
     */
    MysqlCommand( String dbname )
    {
        this.db = new DbConnect( dbname )
    }

    /**
     * Run a single command through mysql
     *
     * @param cmd       Command to execute (usually a DML)
     * @return          String returned on stdout
     */
    String run( String cmd )
    {
        def mscmd = "mysql -h ${db.host} --password=${db.pass} -u ${db.user} -e \"${cmd};\" ${db.schema}"
        def sout = new RunCommand( mscmd ).run()

        if ( sout ) log.warn( "MySQL command output: " + sout )
        sout
    }

    /**
     * Take a snapshot of the database and save in standard directory
     *
     * @param dir       Directory for backup file (must have trailing file separator)
     * @param tables    Optional list of tables to dump
     */
    String snapshot( String dir, List tables = [] )
    {
        def sdf = new SimpleDateFormat("yyMMddHHmm")
        def now = new Date()

        def buname = dir + 'snapshot_' + sdf.format(now)
        backup( buname, tables )

        return buname
    }

    /**
     * Backup an entire schema or selected tables
     *
     * @param   backupFile  Filename to dump to
     * @param   tables      Optional list of tables to dump
     * @return              stdout CLI response
     */
    String backup( String backupFile, List tables = [] )
    {
        //  Optional table list
        //
        def tableList = ''
        if ( tables )
            tableList = tables.join(',')

        //  Dump the database schema
        //
        def mscmd = "mysqldump --host ${db.host} --password=${db.pass} --user ${db.user} --result-file=${backupFile} ${db.schema} ${tableList}"
        def sout = new RunCommand( mscmd ).run()

        if ( sout ) log.warn( "DB backup output: " + sout )
        sout
    }
}
