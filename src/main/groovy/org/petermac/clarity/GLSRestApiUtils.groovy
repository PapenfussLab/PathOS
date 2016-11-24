/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.clarity
/**
 * Description:
 *
 * This class provides a number of convenience utility methods
 * for performing HTTP requests and manipulating 'Node' objects
 * in the context of the GLS REST API.
 *
 * Validated for: GLS REST API Version 'v2'
 *
 * @author Jennifer Murdoch
 * @revisions Dan Apperloo
 */
import groovy.xml.StreamingMarkupBuilder

class GLSRestApiUtils {

    private static final String PUT_REQUEST_METHOD  = 'PUT'
    private static final String POST_REQUEST_METHOD = 'POST'

    /**
     * Return a String of XML data extracted from a 'Node'.
     * containing element indentation but preserving whitespace.
     *
     * @param xmlNode the 'Node' of XML data
     * @return a 'String' representation of 'xmlNode'
     */
    static String nodeToXmlString(Node xmlNode) {

        StringWriter xmlStringWriter = new StringWriter()
        XmlNodePrinter printer = new XmlNodePrinter(new PrintWriter(xmlStringWriter, true))
        printer.setPreserveWhitespace(true)

        printer.print(xmlNode)
        return xmlStringWriter.toString()
    }

    /**
     * Return a 'Node' of XML data constructed from a String.
     *
     * @param xmlString the XML data
     * @return a 'Node' representation of 'xmlString'
     */
    static Node xmlStringToNode(String xmlString) {

        return new XmlParser().parseText(xmlString)
    }

    /**
     * Insert a 'Node' of XML data into a parent's children either at the end of the
     * list of children, or at a specified index.
     *
     * For backwards compatibility with older versions of the Api.
     *
     * @param childNode the 'Node' of XML data to insert into the parent's children
     * @param parentNode the 'Node' of XML data to add the child to
     * @param elementIndex the index at which to insert the new child 'Node', or -1 to append to the end of the children list
     * @return the 'parentNode' with the 'childNode' inserted
     */
    static Node insertChildNode(Node childNode, Node parentNode, int elementIndex=-1) {

        if(elementIndex == -1 || elementIndex > parentNode?.children()?.size() ) {
            parentNode?.children()?.add(childNode)
        } else {
            parentNode?.children()?.add(elementIndex, childNode)
        }

        return parentNode
    }

    /**
     * Insert a 'Node' of XML data into a parent's children after the specified 'Node'.
     * If the specified 'Node' to insert after does not exist in the parent's children,
     * do not add the specified child 'Node'.
     *
     * For backwards compatibility with older versions of the Api.
     *
     * @param childNode the 'Node' of XML data to insert into the parent's children
     * @param parentNode the 'Node' of XML data to add the child to
     * @param insertAfterNode the child 'Node' of XML data to add the 'childNode' directly after
     * @return the 'parentNode' with the 'childNode' inserted
     */
    static Node insertChildNodeAfterNode(Node childNode, Node parentNode, Node insertAfterNode) {

        int elementIndex = parentNode?.children().indexOf(insertAfterNode)

        if(elementIndex != -1) {
            insertChildNode(childNode, parentNode, elementIndex+1)
        }

        return parentNode
    }

    /**
     * Set the value for a specified UDF on the given 'Node' of XML data
     *
     * @param xmlNode the 'Node' of XML data to set the specified UDF on
     * @param udfName the name of the UDF to set
     * @param udfValue the value of the UDF to set
     * @return the updated XML 'Node'
     */
    static Node setUdfValue(Node xmlNode, String udfName, udfValue) {

        xmlNode.'@xmlns:udf' = 'http://genologics.com/ri/userdefined'

        Node foundUdfNode = xmlNode.'udf:field'.find{udfName.equals(it.@name)}

        foundUdfNode != null ?
            foundUdfNode.setValue(udfValue) :
            xmlNode.append(NodeBuilder.newInstance().'udf:field'(name: udfName, udfValue))

        return xmlNode
    }

    /**
     * Executes an HTTP GET against the specified URL and parses the XML response into a 'Node'.
     *
     * @param url the URL to connect to
     * @param username the username to authenticate with
     * @param password the password to authenticate with
     * @return parsed XML 'Node' from a GET to 'url'
     */
    static Node httpGET(String url, String username, String password) {

        URLConnection connection = getConnection(url, username, password)

        try {
            return new XmlParser().parse(connection.getInputStream())
        } catch (Exception e) {
            return createResponseCodeXML(connection)
        }
    }

    /**
     * Executes an HTTP PUT to the specified URL with the specified Node contents as the request body.
     *
     * @param xmlNode the request body XML content for the PUT
     * @param url the URL to connect to
     * @param username the username to authenticate with
     * @param password the password to authenticate with
     * @return parsed XML 'Node' from a PUT to 'url'
     */
    static Node httpPUT(Node xmlNode, String url, String username, String password) {

        URLConnection connection = getConnection(url, username, password)
        connection.setRequestMethod(PUT_REQUEST_METHOD)
        pushXmlContent(connection, xmlNode)

        try {
            return new XmlParser().parse(connection.getInputStream())
        } catch (Exception e) {
            return createResponseCodeXML(connection)
        }
    }

    /**
     * Executes an HTTP POST using the specified URL with the specified Node contents as the request body.
     *
     * @param xmlNode the request body XML content for the POST
     * @param url the URL to connect to
     * @param username the username to authenticate with
     * @param password the password to authenticate with
     * @return parsed XML 'Node' from a POST to 'url'
     */
    static Node httpPOST(Node xmlNode, String url, String username, String password) {

        URLConnection connection = getConnection(url, username, password)
        connection.setRequestMethod(POST_REQUEST_METHOD)
        pushXmlContent(connection, xmlNode)

        try {
            return new XmlParser().parse(connection.getInputStream())
        } catch (Exception e) {
            return createResponseCodeXML(connection)
        }
    }

    /**
     * Does a single batch Retrieve POST to get all the containers or artifacts represented by the provided URIs
     *
     * @param uris URIs of the artifacts or containers to get (set must be homogeneous)
     * @return all of the retrieved nodes
     */
    static Collection<Node> batchGET(Collection<String> uris, String username, String password) {
        if(!uris) {
            return Collections.emptyList()
        }
        uris = new ArrayList(uris)
        String rawURIString = uris[0]
        rawURIString = rawURIString.substring(0, rawURIString.lastIndexOf("/"))
        String resource = rawURIString.substring(rawURIString.lastIndexOf("/")+1, rawURIString.length()-1)
        rawURIString = rawURIString+"/batch/retrieve"
        def xml = new StreamingMarkupBuilder()
        xml.encoding = 'UTF-8'
        def doc = xml.bind {
            mkp.xmlDeclaration()
            mkp.declareNamespace(ri: 'http://genologics.com/ri')
            'ri:links'(){
                uris.each{ uri ->
                    'link'(uri: uri, rel: resource)
                }
            }
        }

        def resp = httpPOST(xmlStringToNode(doc.toString()), rawURIString, username, password)
        return resp.children()
    }

    /**
     * Does a single batchUpdate POST call to update all the provided artifacts or containers.
     * The provided set must be homogeneus (all artifacts or all containers)
     * @param toPut the artifact or container nodes to update
     * @return links to all the updated nodes
     */
    static Collection<Node> batchPUT(Collection<Node> toPut, String username, String password) {
        if(!toPut) {
            return Collections.emptyList()
        }
        toPut = new ArrayList(toPut)
        String rawURIString = toPut.iterator().next().@uri;
        String resource = toPut.iterator().next().name().prefix
        rawURIString = rawURIString.substring(0, rawURIString.lastIndexOf("/"))+"/batch/update"
        def xml = new StreamingMarkupBuilder()
        xml.encoding = 'UTF-8'
        def doc = xml.bind {
            mkp.xmlDeclaration()
            mkp.declareNamespace(ri: 'http://genologics.com/ri', udf: 'http://genologics.com/ri/userdefined',
                    file: 'http://genologics.com/ri/file', art: 'http://genologics.com/ri/artifact', con: 'http://genologics.com/ri/container')
            "${resource}:details"(){
                def nodes = new StringWriter()
                def nodesPrinter = new XmlNodePrinter(new PrintWriter(nodes))
                nodesPrinter.setPreserveWhitespace(true);
                toPut.each { nodesPrinter.print(it) }
                mkp.yieldUnescaped(nodes)
            }
        }

        def resp = httpPOST(xmlStringToNode(doc.toString()), rawURIString, username, password)
        return resp.children()
    }

    /**
     * Prepares a URL connection to the specified URL using basic authentication
     *
     * @param url the URL to connect to
     * @param username the username to authenticate with
     * @param password the password to authenticate with
     * @return a non-connected 'URLConnection' with the 'Authorization' header set
     */
    private static URLConnection getConnection(String url, String username, String password) {

        URLConnection connection = url.toURL().openConnection();

        def encoding = "${username}:${password}".getBytes().encodeBase64()
        connection.setRequestProperty('Authorization', "Basic ${encoding}")

        return connection
    }

    /**
     * Push the specified 'Node' contents as the request body to the specified 'URLConnection'.
     *
     * @param connection a 'URLConnection' with the request method set to either 'PUT' or 'POST'
     * @param xmlNode the request body XML content for the 'PUT' or 'POST' request
     */
    private static void pushXmlContent(URLConnection connection, Node xmlNode) {

        if(connection.getRequestMethod().equals(PUT_REQUEST_METHOD) ||
                connection.getRequestMethod().equals(POST_REQUEST_METHOD) ) {

            connection.setRequestProperty('Content-Type', 'application/xml')
            connection.setDoOutput(true)

            connection.getOutputStream() << nodeToXmlString(xmlNode)
            connection.getOutputStream().flush()
        }
    }

    /**
     * Create an XML 'Node' representing connection request response information
     * including the response code and response message.
     *
     * @param connection a connected 'URLConnection'
     * @return a 'Node' object representing the HTTP response code
     */
    private static Node createResponseCodeXML(URLConnection connection) {

        try {
            return new XmlParser().parse(connection.getErrorStream())
        } catch( Exception e ) {

            StreamingMarkupBuilder builder = new StreamingMarkupBuilder()
            builder.setEncoding('UTF-8')

            def error = builder.bind {
                'HttpRequestError' (uri: connection.getURL().toString()) {
                    'error-code'(connection.getResponseCode())
                    'error-message'(connection.getResponseMessage())
                }
            }
            return new XmlParser().parseText(error.toString())
        }
    }

    /**
     * Convenience method to facilitate comparing URIs without any queries (such as state information associated with artifacts).
     *
     * @param uri URI to strip a query from
     * @return the original URI, without a query
     */
    public static String stripQuery(String uri) {
        return uri?.contains('?') ? uri?.substring(0, uri.indexOf('?')) : uri
    }
}
