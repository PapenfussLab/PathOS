/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.clarity

//
//	ListUsers.groovy
//
//	List all users
//
//

def clarity = new Clarity( 'test')

//The base URI for this class instance to use.  Replace server and IP with accurate information.
URI baseURI = new URI( clarity.limsHost + '/researchers/')   // <--- Replace ADM6 with the lismid value


//Get a single Project by limsid
def users = GLSRestApiUtils.httpGET(baseURI.toString(), clarity.user , clarity.pass )

//println GLSRestApiUtils.nodeToXmlString(users)

int  i=1
users.researcher.each
{
	println i++ + "\tName:\t" + it.'first-name'.text() + "\t" + it.'last-name'.text()
}
