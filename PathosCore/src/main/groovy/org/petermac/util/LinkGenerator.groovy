package org.petermac.util

import java.net.URI
import java.net.URLEncoder

/**
 * A class for making configurable links using Groovy's ConfigSlurper.
 *
 * The constructor reads a nominated configuration file with ConfigSlurper
 * and then domain objects are given to the object and links are generated
 * according the to configuration.
 *
 * The structure of the configuration file should be:
 *
 * links {
 *     <DomClassName1> <domClassLinkDfns1>
 *     <DomClassName2> <domClassLinkDfns2>
 *     ....
 * }
 *
 * where <domClassLinkDfnsN> has the form
 * {
 *     <linkKind1> <linkSpec1>
 *     <linkKind2> <linkSpec2>
 *     ....
 * }
 * 
 * where <linkSpecN> is a specification with the following forms:
 *
 * list: a list of specifications which are evaluated recursively in turn
 *
 * closure: a closure which will be invoked on the domain object itself,
 *      the result of which will be a specification which will be valuated
 *      recursively.
 *
 * literal:
 *     {
 *         anchor = <string or closure returning a string>
 *         base = <string or closure returning a string>
 *         path = <string or closure returning a string>
 *         params = <map or closure returning a map>
 *         fragment = <string or closure returning a string>
 *     }
 *
 * Examples:
 *
 * 1. Populating the path from the domain object:
 *
 * links {
 *     SeqSample {
 *         igv {
 *             anchor = { rec -> rec.sampleName }
 *             path = { rec -> "igv/${rec.seqrun.seqrun}/${rec.sampleName}.xml"
 *         }
 *     }
 * }
 *
 * 2. Populating the params (query string) from the domain object:
 *
 * links {
 *     SeqCnv {
 *         gaffa {
 *             base = "http://gaffa.petermac.org/"
 *             params = { rec ->
 *                 if (rec.resolution == 'gene') {
 *                     return ['gene_id': rec.gene]
 *                 } else {
 *                     return ['locus': "${rec.chr}:${rec.start}-${rec.end}"]
 *                 }
 *             }
 *         }
 *     }
 * }
 *
 * 3. Generating a list of links
 *
 * links {
 *     SeqVariant {
 *         cosmic = { rec ->
 *             return rec.cosmic.tokenize(',').collect { cos ->
 *                 def m = (cos =~ /COSM([0-9]+)/)
 *                 if (m) {
 *                     cos = m[0][1]
 *                 }
 *                 return [anchor: cos,
 *                         base: 'https://cancer.sanger.ac.uk/cosmic/mutation/overview',
 *                         params: ['id': cos]]
 *             }
 *         }
 *     }
 *
 */
class LinkGenerator {
    private ConfigObject config

    /**
     * Build a new link generator by reading the named file
     * and parsing it as a Groovy ConfigSlurper file.
     * @param configName    the name of the configuration file to read.
     */
    public LinkGenerator(String configName) {
        File f = new File(configName)
        config = new ConfigSlurper().parse(f.text)
    }

    /**
     * Return the list of link type names that may be generated for a given domain class.
     *
     * @param domObjClass   the name of the domain object class of interest
     * @return a sorted list of the names of the kinds of links that may be generated.
     */
    public List linkKindsForObject(String domObjClass) {
        if (!(config.get('links').containsKey(domObjClass))) {
            // No links for this domain class
            return []
        }
        List kinds = config.get('links').get(domObjClass).keySet()
        return kinds.sort()
    }

    /**
     * Take a domain object and generate any links that were configured
     * for this kind of domain object. See the notes to the toplevel class
     * for more information about the expected form of the configuration.
     *
     * @param domObjClass   the (short) name of the domain object class
     * @param domObj        the domain object itself.
     * @return  a map with an entry for each kind of link as described in
     *          the slurped configuration. For each kind of link, there
     *          will be a list of 0 or more links. Each link is described
     *          by a an object with two fields: 'anchor' which is the text
     *          that should be displayed for the link, and 'url' which is
     *          the URL for the link itself.
     */
    public Map linksForObject(String domObjClass, Object domObj) {
        if (!(config.get('links').containsKey(domObjClass))) {
            // No links for this domain class
            return []
        }
        ConfigObject domConfig = config.get('links').get(domObjClass)

        Map lnks = [:]
        domConfig.each { k, v ->
            lnks[k] = []
            oneKindOfLink(k, v, domObj, lnks[k])
        }
        return lnks
    }

    /**
     * Generate the list of links of one kind for a given domain object.
     * @param linkKind      the kind of link - used as default anchor text
     * @param generator     a closure which can be applied to the domain object to yield either a meta-link or a list of meta-links.
     * @param domObj        the domain object which may be used to link generation.
     * @param res           the result list into which the links will be accumulated.
     */
    public void oneKindOfLink(String linkKind, Closure generator, Object domObj, List res) {
        oneKindOfLink(linkKind, generator(domObj), domObj, res)
    }

    /**
     * Generate the list of links of one kind for a given domain object.
     * @param linkKind      the kind of link - used as default anchor text
     * @param metaLinks     a list of meta-links each of which may yield links.
     * @param domObj        the domain object which may be used to link generation.
     * @param res           the result list into which the links will be accumulated.
     */
    public void oneKindOfLink(String linkKind, List metaLinks, Object domObj, List res) {
        metaLinks.each { metaLink -> oneKindOfLink(linkKind, metaLink, domObj, res) }
    }

    /**
     * Generate a link for a given domain object.
     *
     * The metaLink parameter is a map containing the meta-data which will
     * be used to direct link generation. It may have the following elements,
     * all of which are optional, and each of which may be value, or a closure
     * which may be applied to the domain object to produce the value:
     *  anchor:     the text to use as anchor text in the link
     *  base:       the base URL to the resource (e.g. https://cancer.sanger.ac.uk/)
     *  path:       the path component of the URL (e.g. cosmic/mutation/overview)
     *  params:     a map containing key-value pairs to be URL-encoded with appropriate escaping
     *  fragment:   the fragment identifier
     *
     * All elements are optional, and the behaviours are as follows if the element is not given:
     *  anchor:     the link kind is used
     *  base:       the current server will be used as the base URL (as given to the constructor)
     *  path:       an empty path (i.e. /)
     *  params:     no ?key1=value1&key2=value2 part is added to the URL
     *  fragment:   no #fragid part is added to the URL
     *
     * @param linkKind      the kind of link - used as default anchor text
     * @param metaLink      a map containing the components from which the link will be built
     * @param domObj        the domain object which may be used to link generation.
     * @param res           the result list into which the link will be accumulated.
     */
    public void oneKindOfLink(String linkKind, Map metaLink, Object domObj, List res) {
        String anchor = linkKind
        String base  = null
        String path = null
        Map params = [:]
        String fragment = null

        if (metaLink['anchor']) {
            anchor = evalComponent(metaLink['anchor'], domObj)
        }
        if (metaLink['base']) {
            base = evalComponent(metaLink['base'], domObj)
        }
        if (metaLink['path']) {
            path = evalComponent(metaLink['path'], domObj)
        }
        if (metaLink['params']) {
            params = evalComponent(metaLink['params'], domObj)
        }
        if (metaLink['fragment']) {
            fragment = evalComponent(metaLink['fragment'], domObj)
        }

        String url = buildUrl(base, path, params, fragment)
        res << ['anchor': anchor, 'url': url]
    }

    public Object evalComponent(Object val, Object domObj) {
        if (val instanceof Closure) {
            Closure generator = val
            return generator(domObj)
        }
        return val
    }

    public String buildUrl(String base, String path, Map params, String fragment) {
        // TODO handle the case where the base URL is null
        // i.e. figure out the base URL to this server.
        assert base != null

        String raw = base
        if (path) {
            raw = "${raw}/${path}"
        }
        if (params) {
            List parts = []
            params.each { k, v ->
                String part = "${URLEncoder.encode(k)}=${URLEncoder.encode(v)}"
                parts << part
            }
            raw = "${raw}?${parts.join('&')}"
        }
        if (fragment) {
            raw = "${raw}#${fragment}"
        }

        URI uri = new URI(raw)
        return uri.normalize().toString()
    }
}
