/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.clarity

/*	ListProjects.groovy
 *
 *	List all LIMS projects
 *
 */

def clarity = new Clarity( 'test')

//	The base URI for this class instance to use.  Replace server and IP with accurate information.
URI baseURI = new URI( clarity.limsHost + '/projects/')

//	Get a single Project by limsid
Node projectNode = GLSRestApiUtils.httpGET(baseURI.toString(), clarity.user , clarity.pass )

//	Dump the XML returned
//println GLSRestApiUtils.nodeToXmlString(projectNode)

projectNode.project.each
{
	println "LIMSid:\t" + it.@limsid + "\tName:\t" + it.name.text()
}
