/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.clarity
//
//		ClarityContainer.groovy	Clarity container utilities
//
//		01		kdoig			12-Mar-13
//
//		Usage: import ClarityContainer
//
//vim:ts=4

import groovy.xml.StreamingMarkupBuilder

class ClarityContainer
{
	Clarity clarity
	String  container

	Node create()
	{
		String uri = clarity.limsHost
		String containersListURI = "${uri}/containers"

		def builder = new StreamingMarkupBuilder()

		builder.encoding = "UTF-8"
	
		def containerDoc = builder.bind
		{
			mkp.xmlDeclaration()
			mkp.declareNamespace(con: 'http://genologics.com/ri/container')
			mkp.declareNamespace(udf: 'http://genologics.com/ri/userdefined')
			'con:container'
			{
				'name'( container )
				'type'(uri:"${uri}/containertypes/2", name:"Tube")
			}
		}

		def createdContainerNode = GLSRestApiUtils.xmlStringToNode( containerDoc.toString())
		createdContainerNode = GLSRestApiUtils.httpPOST( createdContainerNode, containersListURI, clarity.user, clarity.pass )
		//println GLSRestApiUtils.nodeToXmlString( createdContainerNode )

		return createdContainerNode
	}
}