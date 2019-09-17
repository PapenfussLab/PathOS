/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

import org.petermac.util.Locator

hibernate
{
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}

//  Application properties
//
def loc  = Locator.instance
String context = grailsApplication.metadata['app.context']
println "DataSource initialising locator... ${context}"
loc.init(context)

dataSource
{
    pooled = true
    dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"

    dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''

    //  MySql parameters for DB
    //
    driverClassName = "com.mysql.jdbc.Driver"
    username = loc.prop.get('db.username')
    password = loc.prop.get('db.password')
    dbschema = loc.prop.get('db.schema')
    dbhost   = loc.prop.get('db.host')
    dbport   = loc.dbPort
    url = "jdbc:mysql://${dbhost}:${dbport}/${dbschema}"

    properties
    {
        dbProperties
        {
            autoReconnect               =   true
            useUnicode                  =   true
            characterEncoding           =   'utf8'
            zeroDateTimeBehavior        =   'convertToNull'
        }
        testWhileIdle                   =   true
        validationQuery                 =   "SELECT 1"
    }
}

// environment specific settings
//
environments
{
    pa_uat
    {
        dataSource
        {
            dbCreate = "nothing"
        }
    }
    pa_prod
    {
        dataSource
        {
            dbCreate = "nothing"
        }
    }
}
