/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

import org.petermac.util.Locator

dataSource
{
    pooled = true
}

hibernate
{
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}

//  Application properties
//
def loc  = Locator.instance

// environment specific settings
//
environments
{
    gebtest {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        }
    }

    pa_test
    {
        dataSource
        {
            //  In Memory hibernate DB
            //
            driverClassName = "org.h2.Driver"
            dbCreate = "create"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        }
    }

    pa_uat
    {
        dataSource
        {
            dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"

            //  MySql parameters for DB
            //
            driverClassName = "com.mysql.jdbc.Driver"
            username = loc.prop.get('db.username')
            password = loc.prop.get('db.password')
            dbschema = loc.prop.get('db.schema')
            dbhost   = loc.prop.get('db.host')
            url = "jdbc:mysql://${dbhost}:3306/${dbschema}?autoreconnect=true?useUnicode=yes&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull"

            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''

            properties
                    {
                        maxActive = -1
                        minEvictableIdleTimeMillis=1800000
                        timeBetweenEvictionRunsMillis=1800000
                        numTestsPerEvictionRun=3
                        testOnBorrow=true
                        testWhileIdle=true
                        testOnReturn=true
                        validationQuery="SELECT 1"
                    }
        }
    }

    pa_dev
            {
                dataSource
                        {
                            dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"

                            //  MySql parameters for DB
                            //
                            driverClassName = "com.mysql.jdbc.Driver"
                            username = loc.prop.get('db.username')
                            password = loc.prop.get('db.password')
                            dbschema = loc.prop.get('db.schema')
                            dbhost   = loc.prop.get('db.host')
                            url = "jdbc:mysql://${dbhost}:3306/${dbschema}?autoreconnect=true?useUnicode=yes&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull"

                            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''

                            properties
                                    {
                                        maxActive = -1
                                        minEvictableIdleTimeMillis=1800000
                                        timeBetweenEvictionRunsMillis=1800000
                                        numTestsPerEvictionRun=3
                                        testOnBorrow=true
                                        testWhileIdle=true
                                        testOnReturn=true
                                        validationQuery="SELECT 1"
                                    }
                        }
            }

    pa_stage
    {
        dataSource
        {
            dialect  = "org.hibernate.dialect.MySQL5InnoDBDialect"
            dbCreate = "update"

            //  MySql parameters for DB
            //
            driverClassName = "com.mysql.jdbc.Driver"
            username = loc.prop.get('db.username')
            password = loc.prop.get('db.password')
            dbschema = loc.prop.get('db.schema')
            dbhost   = loc.prop.get('db.host')
            url = "jdbc:mysql://${dbhost}:3306/${dbschema}?autoreconnect=true?useUnicode=yes&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull"

            properties
                    {
                        testOnBorrow=true
                        testWhileIdle=true
                        testOnReturn=true
                        validationQuery="SELECT 1"
                    }
        }
    }

    pa_test
    {
        dataSource
        {
            dialect  = "org.hibernate.dialect.MySQL5InnoDBDialect"
            dbCreate = "update"

            //  MySql parameters for DB
            //
            driverClassName = "com.mysql.jdbc.Driver"
            username = loc.prop.get('db.username')
            password = loc.prop.get('db.password')
            dbschema = loc.prop.get('db.schema')
            dbhost   = loc.prop.get('db.host')
            url = "jdbc:mysql://${dbhost}:3306/${dbschema}?autoreconnect=true?useUnicode=yes&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull"


            properties
                    {
                        testOnBorrow=true
                        testWhileIdle=true
                        testOnReturn=true
                        validationQuery="SELECT 1"
                    }
        }
    }

    pa_local
    {
        dataSource
        {
            dialect  = "org.hibernate.dialect.MySQL5InnoDBDialect"
            dbCreate = "update"

            //  MySql parameters for DB
            //
            driverClassName = "com.mysql.jdbc.Driver"
            username = loc.prop.get('db.username')
            password = loc.prop.get('db.password')
            dbschema = loc.prop.get('db.schema')
            dbhost   = loc.prop.get('db.host')
            url = "jdbc:mysql://${dbhost}:3306/${dbschema}?autoreconnect=true?useUnicode=yes&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull"
        }
    }

    pa_cloud
    {
        dataSource
        {
            dialect  = "org.hibernate.dialect.MySQL5InnoDBDialect"

            //  MySql parameters for DB
            //
            driverClassName = "com.mysql.jdbc.Driver"
            username = loc.prop.get('db.username')
            password = loc.prop.get('db.password')
            dbschema = loc.prop.get('db.schema')
            dbhost   = loc.prop.get('db.host')
            url = "jdbc:mysql://${dbhost}:3306/${dbschema}?autoreconnect=true?useUnicode=yes&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull"

            properties
            {
                testOnBorrow=true
                testWhileIdle=true
                testOnReturn=true
                validationQuery="SELECT 1"
            }
        }
    }

    pa_prod
    {
        dataSource
        {
            dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"

            //  MySql parameters for DB
            //
            driverClassName = "com.mysql.jdbc.Driver"
            username = loc.prop.get('db.username')
            password = loc.prop.get('db.password')
            dbschema = loc.prop.get('db.schema')
            dbhost   = loc.prop.get('db.host')
            url = "jdbc:mysql://${dbhost}:3306/${dbschema}?autoreconnect=true?useUnicode=yes&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull"

            properties
            {
                maxActive = -1
                minEvictableIdleTimeMillis=1800000
                timeBetweenEvictionRunsMillis=1800000
                numTestsPerEvictionRun=3
                testOnBorrow=true
                testWhileIdle=true
                testOnReturn=true
                validationQuery="SELECT 1"
            }
        }
    }

    pa_research
    {
        dataSource
        {
            dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"

            //  MySql parameters for DB
            //
            driverClassName = "com.mysql.jdbc.Driver"
            username = loc.prop.get('db.username')
            password = loc.prop.get('db.password')
            dbschema = loc.prop.get('db.schema')
            dbhost   = loc.prop.get('db.host')
            url = "jdbc:mysql://${dbhost}:3306/${dbschema}?autoreconnect=true?useUnicode=yes&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull"


            properties
            {
                maxActive = -1
                minEvictableIdleTimeMillis=1800000
                timeBetweenEvictionRunsMillis=1800000
                numTestsPerEvictionRun=3
                testOnBorrow=true
                testWhileIdle=true
                testOnReturn=true
                validationQuery="SELECT 1"
            }
        }
    }
}
