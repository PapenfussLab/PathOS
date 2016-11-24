/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.clarity
//
//		Clarity.groovy		Clarity Connection Classes
//
//		01		kdoig		21-Mar-13
//
//		Usage: import Clarity
//				new Clarity( server: "test"|"prod" )
//
//vim:ts=4

import groovy.util.logging.Log4j
import org.petermac.util.DbConnect

@Log4j
class Clarity
{
    static String uriTest = "http://172.23.8.161:8080/api/v2"   // Test host - must be IP address
    static String uriProd = "http://172.23.8.186:8080/api/v2"   // Production host
    static String user
    static String pass
    String server   = "test"
    String limsHost = uriTest
	def    sql

    //
	//	Constructor
    //  Set host to use, test the REST iface and connect to database
    //
	Clarity( server )
	{
        this.server   = server
        assert( this.server == "test" || this.server == "prod" )
        this.limsHost = (server == "test" ? uriTest : uriProd )

        //	Connect to Database
        //
        def db = new DbConnect( "lims_"+server )
        user   = db.userRest
        pass   = db.passRest
		sql    = db.sql()
		log.debug( "Connected to DB " + db.dbname )
    }
}