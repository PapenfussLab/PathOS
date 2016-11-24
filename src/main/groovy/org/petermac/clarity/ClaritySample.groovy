/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.clarity
//
//		ClaritySample.groovy	Clarity utilities to manipulate samples
//
//		01		kdoig			12-Mar-13
//
//		Usage: import ClaritySample
//
//vim:ts=4

import groovy.xml.StreamingMarkupBuilder
import groovy.util.logging.Log4j

@Log4j
class ClaritySample
{
	Clarity clarity
	String  sample
    String  limsid

    /**
     * Add a given sample to a project in a container and set the workflow field
     *
     * @param projectid     Project name
     * @param container     Container for sample
     * @param workflow      Workflow for sample
     * @return              Sample limsid
     */
    String add( String project, String container, Map params )
	{
        assert sample == params.sample : "Sample object attribute doesn't match parameters"

		//	Look for sample in DB first
		//	return if it already exists
		//
        if ( exists())
            return limsid

		//	Get Project using the project name - create if necessary
		//
		def cp = new ClarityProject( clarity, project )
        if ( ! cp.exists())
        {
            cp.create()
        }

		//	Create new container
		//
		def cc = new ClarityContainer( clarity: clarity, container: container )
		Node containerNode = cc.create()
	
		return create( cp.node(), containerNode, params )
	}

    /**
     * Create a sample in a given project and container and set some UDF fields
     *
     * @param projectNode       Project in Node form
     * @param containerNode     Container in Node form
     * @param params            Sample attributes from Detente
     * @return                  Sample in Node form
     */
    Node create( Node projectNode, Node containerNode, Map params )
	{
		def builder = new StreamingMarkupBuilder()

		builder.encoding = "UTF-8"

		def sampleDoc = builder.bind
		{
			mkp.xmlDeclaration()
			mkp.declareNamespace(smp: 'http://genologics.com/ri/sample')
			'smp:samplecreation'
			{
				'name' ("${sample}")
				'project' (uri:"${projectNode.'@uri'}")
				'location'
				{
					'container' (limsid:"${containerNode.'@limsid'}", uri:"${containerNode.'@uri'}")
					'value' ("1:1")
				}
			}
		}

		Node sampleNode = GLSRestApiUtils.xmlStringToNode( sampleDoc.toString())
		log.debug( ">>> Request: \n" + GLSRestApiUtils.nodeToXmlString(sampleNode))

        //  Add sample attributes
        //
		sampleNode = GLSRestApiUtils.setUdfValue(sampleNode, "test_sets",    params.test_sets )
		sampleNode = GLSRestApiUtils.setUdfValue(sampleNode, "test_descs",   params.test_descs )
		sampleNode = GLSRestApiUtils.setUdfValue(sampleNode, "patient",      params.patient )
		sampleNode = GLSRestApiUtils.setUdfValue(sampleNode, "urn",          params.urn )
		sampleNode = GLSRestApiUtils.setUdfValue(sampleNode, "dob",          params.dob )
		sampleNode = GLSRestApiUtils.setUdfValue(sampleNode, "sex",          params.sex )
		sampleNode = GLSRestApiUtils.setUdfValue(sampleNode, "pathlab",      params.pathlab )
		sampleNode = GLSRestApiUtils.setUdfValue(sampleNode, "requester",    params.requester )
		sampleNode = GLSRestApiUtils.setUdfValue(sampleNode, "request_date", params.request_date )
		sampleNode = GLSRestApiUtils.setUdfValue(sampleNode, "collect_date", params.collect_date )
		sampleNode = GLSRestApiUtils.setUdfValue(sampleNode, "rcvd_date",    params.rcvd_date )
		sampleNode = GLSRestApiUtils.setUdfValue(sampleNode, "auth_date",    params.auth_date )

        String sampleListURI = clarity.limsHost + "/samples"
        sampleNode = GLSRestApiUtils.httpPOST( sampleNode, sampleListURI, clarity.user, clarity.pass )
        log.debug( ">>> Response:\n" + GLSRestApiUtils.nodeToXmlString( sampleNode ))

        sampleNode = GLSRestApiUtils.httpPUT( sampleNode, sampleNode.@uri, clarity.user, clarity.pass)

		return sampleNode
	}

    /**
     * Check if a sample exists in the database
     *
     * @return  Number of occurrences of sample name in DB
     */
    int findInDB()
	{
		//
		//	Find any samples matching the sample name
		//
		def dbsamples = clarity.sql.rows(
		"""
		select	sam.name as sname,
				prj.luid,
				prj.name as pname
		from	sample	as sam,
				project as prj
		where	sam.projectid = prj.projectid
		and		sam.name = ${sample}
		""" )

		log.debug( "Found ${sample} by name in ${dbsamples.size()} samples")
	
		return dbsamples.size()
	}

    /**
     * Check if a sample exists in the REST API
     *
     * @return  limsid of sample object
     */
    Node node()
    {
        //The base URI for samples
        //
        URI baseURI = new URI( clarity.limsHost + "/samples?name=${sample}" )

        def cnt = 0
        Node samples = GLSRestApiUtils.httpGET(baseURI.toString(), clarity.user, clarity.pass )
        samples.each
                {
                    ++cnt
                    limsid = it.@limsid
                    log.debug( "Sample: ${sample} LIMSid:\t" + it.@limsid)
                }

        if ( cnt > 1 )
            log.warn( "Multiple [${cnt}] samples with the same name " + sample)

        return samples.sample[0]
    }

    /**
     * Check if sample exists
     *
     * @return true if exists
     */
    Boolean exists()
    {
        def sample = node()

        return ( sample != null )
    }
}