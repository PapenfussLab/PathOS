/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.clarity

import groovy.util.logging.Log4j
import groovy.xml.StreamingMarkupBuilder
//
//		ClarityProject.groovy	Clarity project utilities
//
//		01		kdoig			12-Mar-13
//
//		Usage: import ClarityProject
//
//vim:ts=4

@Log4j
class ClarityProject
{
	Clarity clarity
	String  project     // Project name
    String  URI

    ClarityProject( clarity, project )
    {
        this.clarity = clarity
        this.project = project
        this.URI  = "${clarity.limsHost}/projects"
    }

    /**
     * Create a project
     *
     * @return created Node of project
     */
    Node create()
    {
        //  Check if it exists
        //
        if ( exists())
        {
            log.debug( "Project ${project} already exists: " + limsid())
            return node()
        }

        def builder = new StreamingMarkupBuilder()
        builder.encoding = "UTF-8"
        def dt = new Date()
        def openDate = dt.format("yyyy-MM-dd")

        // Build a new project using Markup Builder
        //
        def projectDoc = builder.bind {
            mkp.xmlDeclaration()
            mkp.declareNamespace(prj: 'http://genologics.com/ri/project')
            mkp.declareNamespace(udf: 'http://genologics.com/ri/userdefined')
            'prj:project'{
                'name'(project)
                'open-date'(openDate)
                'researcher'(uri: clarity.limsHost + "/researchers/1")
            }
        }

        //'udf:field'(name:"Objective", "Detente Loader")

        // Turn the markup into a node and post it to the API
        //
        def projectNode = GLSRestApiUtils.xmlStringToNode(projectDoc.toString())
        projectNode = GLSRestApiUtils.httpPOST( projectNode, URI, clarity.user, clarity.pass )
        log.debug( "Create Project:\n" + GLSRestApiUtils.nodeToXmlString(projectNode))

        return projectNode
    }

    /**
     * Get Project node by LIMSID
     *
     * @param limsid    ID of project
     * @return          Node of project
     */
    Node getByLimsId( String limsid )
	{
		Node n = GLSRestApiUtils.httpGET( URI + "/" + limsid, clarity.user, clarity.pass )
        if ( n.message.text() =~ "Project Not Found" )
            return null

        return n
	}

    /**
     * Get Project Node by name
     *
     * @return  Node of project - assumes a single project is found
     */
    Node node()
    {
        def u = URI + "?name=${project}"
        Node n = GLSRestApiUtils.httpGET( u, clarity.user, clarity.pass )
        return n.project[0]
    }

    /**
     * Find limsid of a project
     *
     * @return limsid
     */
    String limsid()
    {
        Node n = node()
        return n.@limsid
    }

    Boolean exists()
    {
        return node() != null
    }
}