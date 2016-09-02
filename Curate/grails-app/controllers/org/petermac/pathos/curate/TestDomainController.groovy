/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import com.aspose.words.DataTable
import groovy.sql.Sql

import java.sql.ResultSet

class TestDomainController
{
    def scaffold = true
    def dataSource

    def test()
    {
        println "TestController: " + grailsApplication.config.dataSource.pathos_home

        def sql = Sql.newInstance(  grailsApplication.config.dataSource.url,
                                    grailsApplication.config.dataSource.username,
                                    grailsApplication.config.dataSource.password,
                                    grailsApplication.config.dataSource.driverClassName )
        def dbConnection = sql.getConnection()
        def stmt = dbConnection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
        def rs = stmt.executeQuery("select * from user")
        println "RS: ${rs.getClass()} " + rs

        DataTable dt = new DataTable( rs, "Variants")
        println "DT: ${dt.getClass()} " + dt.tableName

//        def vars = sql.firstRow(
//                '''
//                select	count(*) as cnt
//                from	patient
//                ''' )
//        println "Found rows: ${vars}"

//        sql.query("select * from user")
//        {
//            ResultSet rs ->
//                println "RS: ${rs.getClass()} " + rs
//                DataTable dt = new DataTable( rs, "Variants")
//                println "DT: ${dt.getClass()} " + dt.columnsCount
//        }

        redirect( controller: "testDomain", action: "list", params: params )
    }
}
