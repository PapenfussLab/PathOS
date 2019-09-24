package org.petermac.util

import groovy.sql.Sql

import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.PropertyConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger

@Log4j
@Singleton
class PathosVersion {
    String version

    PathosVersion() {
        version = '1.5.2'
    }

    void checkDatabaseVersion(Sql sql, String schema) {
        // Figure out the version of PathOS the database was made for

        def dbVersion = retreiveDatabaseVersion(sql, schema)

        if (dbVersion != version) {
            log.error "PathOS version ${version} running against database for ${dbVersion}"
        }
    }

    synchronized String retreiveDatabaseVersion(Sql sql, String schema) {
        if (!checkIfVersionTableExists(sql, schema)) {
            String lastUnversionedVersion = '1.3.13'
            log.debug "creating pathos_version table (${lastUnversionedVersion}) on database ${schema}"
            sql.execute 'CREATE TABLE pathos_version (version VARCHAR(255) NOT NULL)'
            sql.execute "INSERT INTO pathos_version (version) VALUES (${lastUnversionedVersion})"
        }
        def rows = sql.rows('SELECT * FROM pathos_version')
        assert rows.size() == 1
        return rows[0].version
    }

    synchronized void updateDatabaseVersion(Sql sql, String schema, String versionToSet) {
        if (checkIfVersionTableExists(sql, schema)) {
            sql.execute "UPDATE pathos_version SET version = $versionToSet"
        } else {
            log.debug "creating pathos_version table for update ($versionToSet) on database $schema"
            sql.execute 'CREATE TABLE pathos_version (version VARCHAR(255) NOT NULL)'
            sql.execute "INSERT INTO pathos_version (version) VALUES ($versionToSet)"
        }
    }

    private synchronized Boolean checkIfVersionTableExists(Sql sql, String schema) {
        def rows = sql.rows("SELECT * FROM information_schema.tables WHERE table_schema = $schema AND table_name = 'pathos_version';")
        assert rows.size() == 0 || rows.size() == 1
        return rows.size() == 1
    }

    private static void usage() {
        println 'PathOS version utility usage:'
        println '    PathosVersion [check]          - check that the installed version of PathOS and the database are compatible'
        println '    PathosVersion update <version> - update the install database version (NB does not alter the schema in any way)'
        System.exit(1)
    }

    public static void main(args) {
        BasicConfigurator.configure()
        Logger.getRootLogger().setLevel(Level.FATAL)

        def pv = PathosVersion.instance

        String op = "check"
        String schema = Locator.instance.dbSchema
        String newVersion
        if (args.size() > 0) {
            switch (args[0]) {
                case 'update':
                    if (args.size() != 2) {
                        usage()
                    }
                    op = args[0]
                    newVersion = args[1]
                    break

                case 'check':
                    if (args.size() > 1) {
                        usage()
                    }
                    op = args[0]
                    break

                default:
                    usage()
            }
        }
        Logger.getRootLogger().setLevel(Level.INFO)
        log.info "connecting as ${Locator.instance.pathosEnv}"
        def db = new DbConnect(Locator.instance.pathosEnv)
        def sql = db.sql()
        def s = pv.version
        def d = pv.retreiveDatabaseVersion(sql, schema)
        switch (op) {
            case 'check':
                println "installed software version: ${s}"
                println "database version          : ${d}"
                sql.close()
                if (s == d) {
                    System.exit(0)
                } else {
                    System.exit(1)
                }

            case 'update':
                pv.updateDatabaseVersion(sql, schema, newVersion)
                println "installed software version: ${s}"
                println "old database version      : ${d}"
                println "new database version      : ${newVersion}"
                sql.close()
                System.exit(0)
        }
    }
}
