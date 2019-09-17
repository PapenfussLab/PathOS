/*
 * Copyright (c) 2018. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Tom Conway
 */
package org.petermac.pathos.loader

import groovy.util.logging.Log4j
import java.text.SimpleDateFormat
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator
import org.petermac.util.DbConnect
import org.petermac.util.Locator
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.petermac.yaml.YamlCodec
import org.petermac.pathos.curate.LabAssay
import org.petermac.pathos.curate.PatAssay
import org.petermac.pathos.curate.PatSample
import org.petermac.pathos.curate.SeqSample
import org.petermac.pathos.curate.SeqRelation

/**
 * Created by seleznev andrei on 9/8/17.
 *
 * A class for encoding/decoding [parsed] YAML to/from domain objects
 *
 */
@Log4j
class PathosExporter  {
    static final Map classRemap = [ 'seqRun':'Seqrun' ]

    static final Map propertyRemap = [       'Patient':      ['name':'fullName'],
                                             'PatSample':    ['urn':'patient'],
                                             'Seqrun':       ['seqRun':'seqrun'],
                                             'SeqSample':    ['seqRun':'seqrun']
    ]

    static final Set builtinFields = ['attached', 'class', 'belongsTo', 'constraints', 'embedded', 'errors',
                                      'hasMany', 'id', 'mappedBy', 'mapping', 'searchable',
                                      'serialVersionUID', 'springSecurityService', 'transients', 'version']

    static final Set specialFields = ['authorities', 'CurateService', 'defaultClinContextCode', 'defaultClinContextDescription']

    private YamlCodec yaml

    private Map domainClassKeys
    private Map domainClassHasMany
    private Map domainClassFields
    private Map domainClassEmbedded
    private Map domainClassDeps
    private Map domainClassDomains

    private Set exclusions
    private Set seen
    private List todo
    private Map currMsg
    private OutputStream out

    /**
     * Constructor
     */
    PathosExporter(OutputStream outStream, Set exclusions) {
        yaml = new YamlCodec()

        InputStream metaInput = this.getClass().getResourceAsStream("/META-INF/import-mapping.yaml")
        def domainClassMapping = yaml.load(metaInput)

        domainClassKeys = [:]
        domainClassDomains = [:]
        domainClassMapping.each {dom, classKeys ->
            classKeys.each {name, key ->
                String nm = remapDomainClassName(name)
                domainClassKeys[nm] = key
                domainClassDomains[nm] = dom
            }
        }

        domainClassHasMany = [:]
        domainClassFields = [:]
        domainClassEmbedded = [:]

        domainClassKeys.each {className, metaKey ->
            Class domClass = Class.forName('org.petermac.pathos.curate.'+className)
            domainClassFields[className] = [:]
            domClass.getDeclaredFields().each { fld ->
                if (fld.getName() == 'hasMany') {
                    domainClassHasMany[className] = domClass.newInstance().hasMany
                }
                if (fld.getName() == 'embedded') {
                    domainClassEmbedded[className] = domClass.newInstance().embedded
                }
                if (fld.synthetic) {
                    return
                }
                if (fld.name in builtinFields) {
                    return
                }
                if (fld.name in specialFields) {
                    return
                }
                domainClassFields[className][fld.name] = fld.type
            }

        }

        metaInput = this.getClass().getResourceAsStream("/META-INF/domain-dependencies.yaml")
        def depsStuff = yaml.load(metaInput)
        domainClassDeps = [:]
        depsStuff.each { k, vs ->
            String className = k.capitalize()
            domainClassDeps[className] = vs
        }

        seen = []
        todo = []

        out = outStream

        this.exclusions = exclusions
    }

    /**
     * Create a YAML serialization of a domain object
     *
     * @param obj   the domain object
     * @return the string of the YAML serialization.
     *
     */
    String encode(String fieldName, Object obj) {
        def enc = encodeInner(obj, false)
        Map res = [:]
        res[fieldName] = enc
        return yaml.dump(res)
    }

    /**
     * Create a YAML serialization of a domain object
     *
     * @param obj   the domain object
     * @return the string of the YAML serialization.
     *
     */
    String encode(Object obj) {
        def enc = encodeInner(obj, false)
        return yaml.dump(enc)
    }

    Object encodeInner(Class objCls, Object obj, Boolean keyOnly) {
        if (objCls?.getPackage()?.getName() == 'org.petermac.pathos.curate') {
            String className = objCls.getSimpleName()
            Object enc = encodeInner(className, obj, keyOnly)
            if (enc instanceof Map && enc.size() == 1) {
                Object val
                enc.each {k, v ->
                    val = v
                }
                return val
            }
            return enc
        }
        String objClassName = objCls.getName()
        if (objClassName == 'java.util.Date') {
            return new SimpleDateFormat("yyyyMMdd").format(obj);
        }
        if (objClassName == 'java.util.DateTime') {
            return new SimpleDateFormat("yyyyMMddHHmm").format(obj);
        }
        return obj
    }

    Object encodeInner(String className, Object obj, Boolean keyOnly) {
        Map res = [:]
        if (className == 'SeqRelation') {
            SeqRelation sr = obj
            res['relation'] = sr.relation
            if (sr.base) {
                res['base'] = sr.base
            }
            res['samples'] = []
            for (SeqSample ss in sr.samples()) {
                res['samples'] << encodeInner('SeqSample', ss, true)
            }
            return res
        }
        assert domainClassFields[className] != null
        domainClassFields[className].each { propName, fldCls ->
            if (className == 'CurVariant' && propName == 'originating') {
                // Skip originating variants.
                return
            }
            if (className in domainClassHasMany && propName in domainClassHasMany[className]) {
                return
            }
            def fldVal = obj[propName]
            if (fldVal == null) {
                return
            }
            if (fldVal instanceof String && fldVal == '') {
                return
            }
            String fldName = getFieldName(propName, className)
            if (keyOnly && !fieldInKey(className, fldName)) {
                return
            }
            if (domainClassEmbedded[className] && propName in domainClassEmbedded[className]) {
                // Embedded things want to be included as is.
                res[fldName] = encodeInner(fldCls, fldVal, false)
                return
            }
            res[fldName] = encodeInner(fldCls, fldVal, true)
        }
        return res
    }

    /**
     * Create a new domain object instance from the given [parsed] YAML representation.
     *
     * @param className         the unqualified domain class name
     * @param data              the parsed YAML data from which the object is to be populated
     * @param updateIfExists    if true, then the object should be found if it already exists
     * @return the new/updated domain class instance
     *
     */
    Object decode(String className, Map data, Boolean updateIfExists) {
        def originalProperties = []
        Object obj
        if (updateIfExists) {
            def k = extractKey(className, data)
            obj = findInner(className, k)
        }
        if (!obj) {
            obj = Class.forName('org.petermac.pathos.curate.'+className).newInstance()
        }

        return decodeInner(className, obj, data)
    }

    Object decodeInner(String className, Object domObj, Map data) {
        assert false
    }

    Object find(String className, Object keyData) {
        Map key = extractKey(className, keyData)
        return findInner(className, key)
    }

    Object findInner(String className, Map keys) {
        // Get the class object.
        //
        def domClass = Class.forName("org.petermac.pathos.curate.${className}")

        if (keys.size() == 1) {
            def fldName
            def fldVal
            keys.each { k, v ->
                fldName = k
                fldVal = v
            }
            String meth = "findBy${fldName.capitalize()}"
            def res = domClass."$meth"(fldVal)
            return res
        }
        return domClass."findWhere"(keys)
    }

    Object findAll(String className) {
        def domClass = Class.forName("org.petermac.pathos.curate.${className}")
        return domClass.findAll()
    }

    Object findAll(String className, Object keyData) {
        Map key = extractKey(className, keyData)
        assert key != null
        return findAllInner(className, key)
    }

    Object findAllInner(String className, Map keys) {
        // Get the class object.
        //
        def domClass = Class.forName("org.petermac.pathos.curate.${className}")

        if (keys.size() == 1) {
            def fldName
            def fldVal
            keys.each { k, v ->
                fldName = k
                fldVal = v
            }
            String meth = "findAllBy${fldName.capitalize()}"
            return domClass."$meth"(fldVal)
        }
        return domClass."findAllWhere"(keys)
    }

    Set dependsOn(String className, Object domObj) {
        Set res = []
        if (domainClassDeps[className]) {
            domainClassDeps[className].each { propName ->
                Object fld = domObj[propName]
                if (fld == null) {
                    return
                }
                String fldClsNm = domainClassFields[className][propName].getSimpleName()
                Tuple item = vignette(fldClsNm, fld)
                res << item
            }
        }
        if (className == 'SeqSample') {
            SeqSample ss = domObj
            if (ss.patSample) {
                res << vignette('PatSample', ss.patSample)
                for (PatAssay pa in ss.patSample.patAssays) {
                    res << vignette('PatAssay', pa)
                }
            }
        }
        if (className == 'SeqRelation') {
            SeqRelation sr = domObj
            for (SeqSample ss in sr.samples()) {
                res << vignette('SeqSample', ss)
            }
        }
        if (className == 'PatAssay') {
            PatAssay pa = domObj
            for (LabAssay la in LabAssay.findAllByTestSet(pa.testSet)) {
                res << vignette('LabAssay', la)
            }
        }
        return res
    }

    void capture(Object domObj) {
        String className = domObj.class.getSimpleName()
        todo.push([className, domObj])
        captureAll()
    }

    void captureAll() {
        while (todo.size() > 0) {
            def v = todo.pop()
            capture(v[0], v[1])
        }
    }

    void capture(String className, Object domObj) {

        if (className in exclusions) {
            return
        }

        // Capture the object with dependencies.
        //
        dumpWithDependencies(className, domObj)

        // Capture the "downward" depenendent objects.
        //
        Class domClass = domObj.class
        String domClassName = domClass.getSimpleName()

        if (className == 'SeqSample') {
            SeqSample ss = domObj
            for (SeqRelation sr in ss.relations) {
                def v = ['SeqRelation', sr]
                if (v in seen) {
                    continue
                }
                todo.push << v
            }
        }

        domainClassDeps.each { clsNm, deps ->
            if (clsNm in exclusions) {
                return
            }
            Class cls = Class.forName('org.petermac.pathos.curate.'+clsNm)
            deps.each { prop ->
                Class fldCls = cls.getDeclaredField(prop).getType()
                String fldClsNm = fldCls.getSimpleName()
                if (fldClsNm != domClassName) {
                    return
                }
                String meth = "findAllBy${prop.capitalize()}"
                //println "finding: ${clsNm}.${meth}(${domObj})"
                cls."$meth"(domObj).each { nxt ->
                    capture(clsNm, nxt)
                }
            }
        }
    }

    void dumpWithDependencies(String className, Object obj) {

        def v = vignette(className, obj)
        if (v in seen) {
            return
        }
        seen << v

        List deps = (dependsOn(className, obj) - seen).toList()
        log.info "dumping ${v}"
        //log.info "dumping ${v} with dependencies ${deps}"
        deps.each { qry ->
            //log.info "dumping dependency of ${v}:  ${qry}"
            findAll(qry[0], qry[1]).each { dep ->
                dumpWithDependencies(qry[0], dep)
            }
        }

        String exportClassName = uncapitalize(className)
        if (exportClassName == 'seqrun') {
            exportClassName = 'seqRun'
        }

        Map enc = [:]
        enc[exportClassName] = encodeInner(className, obj, false)

        addToMessage(domainClassDomains[className], enc)
    }

    Tuple vignette(String className, Object obj) {
        assert obj.class.getPackage().getName() == 'org.petermac.pathos.curate'
        Map key = extractKeyFromDomObj(className, obj)
        Tuple item = new Tuple(className, key)
        return item
    }

    Map extractKeyFromDomObj(String className, Object obj) {
        assert obj.class.getPackage().getName() == 'org.petermac.pathos.curate'

        return encodeInner(className, obj, true)
    }

    Map extractKey(String className, String data) {
        if (!domainClassKeys.containsKey(className)) {
            log.error "'${className}' does not name a type of object that can be searched for."
            return null
        }

        def metaKey = domainClassKeys[className]
        if (metaKey == null) {
            log.error "'${className}' cannot be searched for"
            return null
        }

        if (!(metaKey instanceof String)) {
            log.error "'${className}' cannot be searched for with a simple key"
            return null
        }

        def domClass = Class.forName("org.petermac.pathos.curate.${className}")

        Map key = [:]
        addKeyValue(className, metaKey, data, domClass, key)
        return key
    }
    Map extractKey(String className, List data) {
        if (!domainClassKeys.containsKey(className)) {
            log.error "'${className}' does not name a type of object that can be searched for."
            return null
        }

        def metaKey = domainClassKeys[className]
        if (metaKey == null) {
            log.error "'${className}' cannot be searched for"
            return null
        }

        def domClass = Class.forName("org.petermac.pathos.curate.${className}")

        assert false
    }
    Map extractKey(String className, Map data) {
        if (!domainClassKeys.containsKey(className)) {
            log.error "'${className}' does not name a type of object that can be searched for."
            return null
        }

        def metaKey = domainClassKeys[className]
        if (metaKey == null) {
            log.error "'${className}' cannot be searched for"
            return null
        }

        def domClass = Class.forName("org.petermac.pathos.curate.${className}")


        if (metaKey instanceof String) {
            Map key = [:]
            String fldName = metaKey
            Object fldVal = data[fldName]
            addKeyValue(className, fldName, fldVal, domClass, key)
            return key
        }

        if (metaKey instanceof List) {
            Map key = [:]
            List keyFields = metaKey
            keyFields.each { fldName ->
                Object fldVal = data[fldName]
                addKeyValue(className, fldName, fldVal, domClass, key)
            }
            return key
        }
        assert false
    }

    void addKeyValue(String className, String fldName, Object fldVal, Class domClass, Map key) {
        if (fldVal == null) {
            return
        }
        String propName = getPropertyName(fldName, className)
        Class fldClass = domClass.getDeclaredField(propName).type
        String fldPkg = fldClass.getPackage().getName()
        if (fldPkg == 'org.petermac.pathos.curate') {
            String fldClassName = fldClass.getSimpleName()
            def innerKey =  extractKey(fldClassName, fldVal)
            fldVal = findInner(fldClassName, innerKey)
        }
        if (fldClass.getName() == 'java.util.Set') {
            String fldClassName = fldClass.getSimpleName()
            def innerKey =  extractKey(fldClassName, fldVal)
            fldVal = findInner(fldClassName, innerKey)
        }
        key[propName] = fldVal
    }

    private Boolean fieldInKey(String className, String fieldName) {
        Object metaKey = domainClassKeys[className]
        if (!metaKey) {
            return false
        }
        if (metaKey instanceof String) {
            return metaKey == fieldName
        }
        if (metaKey instanceof List) {
            return fieldName in metaKey
        }
        return false
    }

    /**
     * get grails class property name for import field name, for given domain class
     * @param fieldName
     * @param domainClassName
     * @return
     */
    private String getPropertyName(String fieldName, String domainClassName) {
        if(propertyRemap[domainClassName]?.containsKey(fieldName)) {
            return propertyRemap[domainClassName][fieldName]
        }
        return fieldName
    }

    private String getFieldName(String propertyName, String domainClassName) {
        if (propertyRemap[domainClassName]) {
            String mappedName
            propertyRemap[domainClassName].each {k, v ->
                if (v == propertyName) {
                    mappedName = k
                }
            }
            if (mappedName) {
                return mappedName
            }
        }
        return propertyName
    }

    private String remapDomainClassName(String orig) {
        if (classRemap[orig]) {
            return classRemap[orig]
        }
        return orig.capitalize()
    }

    private static String uncapitalize(String s) {
        def bs = s.getBytes()
        if (bs.size() > 0) {
            bs[0] = Character.toLowerCase(bs[0])
        }
        return new String(bs)
    }

    private void addToMessage(String domain, Map obj) {
        if (currMsg && currMsg['domain'] != domain) {
            flush()
        }
        if (!currMsg) {
            currMsg = [:]
            currMsg['domain'] = domain
            currMsg['action'] = 'createOrUpdate'
            currMsg['data'] = []
        }
        currMsg['data'] << obj
    }

    void flush() {
        assert currMsg != null
        out << yaml.dump(currMsg)
        currMsg = null
    }

    void close() {
        if (currMsg) {
            flush()
        }
    }

    static void main(args) {
        BasicConfigurator.configure()
        Logger.getRootLogger().setLevel(Level.INFO)


        //  Collect and parse command line args
        //
        def cli = new CliBuilder(
                usage:  'PathosDump [options]',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nDump/Load PathOS data.\n'
        )

        //  Options to command
        //
        cli.with {
            h(longOpt: 'help',                  'this help message')
            v(longOpt: 'verbose',               'generate verbose output')

            l(longOpt: 'log-config',    args:1, 'log4j properties file')
            L(longOpt: 'log-level',     args:1, 'set the logging level')

            f(longOpt: 'input-file',    args:1, 'filename for queries (defaults to stdin)')
            o(longOpt: 'output-file',   args:1, 'filename for output (defaults to stdout)')

            e(longOpt: 'exclude',       args:1, 'a comma separated list of domain classes to exclude from dumping')
            a(longOpt: 'all',           args:1, 'dump all of the objects in the named domain class (with dependencies)')
        }

        def opt = cli.parse(args)
        if (!opt) {
            return
        }

        if (opt.help) {
            cli.usage()
            return
        }

        if (opt.l) {
            PropertyConfigurator.configure(opt.l)
        }

        if (opt.L) {
            Logger.getRootLogger().setLevel(Level.toLevel(opt.L))
        }

        Set exclusions = []
        if (opt.e) {
            exclusions = opt.e.tokenize(',') as Set
        }

        OutputStream out = System.out
        if (opt.o) {
            out = new FileOutputStream(opt.o)
        }

        Locator loc = Locator.instance

        DbConnect db   = new DbConnect(loc.pathosEnv)
        ApplicationContext context = new ClassPathXmlApplicationContext(db.hibernateXml)

        PathosExporter codec = new PathosExporter(out, exclusions)

        if (opt.a) {
            SeqSample.withTransaction {
                codec.findAll(opt.a).each { obj ->
                    codec.capture(obj)
                }
            }
        } else {
            File inf
            InputStream ins = System.in
            if (opt.f) {
                ins = new FileInputStream(opt.f)
            }

            def yaml = new YamlCodec()
            for (Map spec : yaml.loadAll(ins)) {
                assert spec.size() == 1
                SeqSample.withTransaction {
                    spec.each { k,v ->
                        def obj = codec.find(k, v)
                        if (obj) {
                            codec.capture(obj)
                        } else {
                            log.warn "${k} ${v} not found"
                        }
                    }
                }
            }
        }
        codec.close()
        context.close()
    }

}
