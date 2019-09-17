/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 */

package org.petermac.pathos.loader

import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator
import org.petermac.pathos.api.ImportReceiver
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.petermac.pathos.curate.AuthUser
import org.petermac.pathos.curate.ClinContext
import org.petermac.pathos.curate.GrpVariant
import org.petermac.pathos.curate.LabAssay
import org.petermac.pathos.curate.Panel
import org.petermac.pathos.curate.PatAssay
import org.petermac.pathos.curate.PatSample
import org.petermac.pathos.curate.Patient
import org.petermac.pathos.curate.RelationService
import org.petermac.pathos.curate.SeqRelation
import org.petermac.pathos.curate.SeqSample
import org.petermac.pathos.curate.Seqrun
import org.petermac.pathos.pipeline.SampleName
import org.petermac.util.DbConnect
import org.petermac.yaml.YamlCodec
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.transaction.UnexpectedRollbackException

import java.text.MessageFormat
import java.text.SimpleDateFormat

/**
 * Created by seleznev andrei on 9/8/17.
 */
@Log4j
class PathosImportReceiver  implements ImportReceiver {

    //  default authuser username to be an  owner of imported objects where an owner is required
    static final String defaultOwner = 'importapi'

    //  map to remap properties from import property value name to GORM property name, for each domain class (e.g. seqRun in import map = seqrun in PathOS)
    static final Map propertyRemap = [       'Patient':      ['name':'fullName'],
                                             'PatSample':    ['urn':'patient'],
                                             'Seqrun':       ['seqRun':'seqrun'],
                                             'SeqSample':    ['seqRun':'seqrun']
    ]

    //  map to map import 'class' name to GORM class name (e.g. patient in import map = Patient class in PathhOS)
    //  key: domain form import map, value: PathOS domain corresponding to this
    static final Map classRemap = [ 'seqRun':'Seqrun' ]

    private DbConnect db
    private ApplicationContext context
    private Boolean verbose

    private Map domainClassMapping
    private Map domainClassKeys

    private Integer txCount

    static public class CrossReferenceException extends Exception {
        CrossReferenceException(String msg) {
            super(msg)
        }
    }

    // Constructor
    //
    PathosImportReceiver(String rdb, Boolean verbose) {
        this.verbose = verbose
        db = new DbConnect(rdb)

        //  Load stand-alone Hibernate context - Database JDBC is embedded in <schema>_loaderContext.xml
        //
        context = new ClassPathXmlApplicationContext(db.hibernateXml)

        YamlCodec yaml = new YamlCodec()
        InputStream metaInput = this.getClass().getResourceAsStream("/META-INF/import-mapping.yaml")
        domainClassMapping = yaml.load(metaInput)
        domainClassKeys = [:]
        domainClassMapping.each {dom, classKeys ->
            classKeys.each {name, key ->
                String nm = remapDomainClassName(name)
                domainClassKeys[nm] = key
            }
        }

        txCount = 0
    }


    /**
     * recieve an import from the API and save/update/remove the gorm objects specified within
     *
     * @param domain
     * @param action
     * @param data
     **/
    void receive(String importDomain, String action, Object data) {
        if (!domainClassMapping[importDomain]) {
            log.error "unsupported import domain ${importDomain}"
            return
        }
        def receiver = this
        txCount += 1
        try {
            SeqSample.withTransaction { status ->
                try {
                    log.info "TX ${txCount} begin: ${importDomain} ${action}"
                    long st = (new Date()).getTime()
                    receiver.receiveInner(importDomain, action, data)
                    long en = (new Date()).getTime()
                    log.info "TX ${txCount} end: ${importDomain} ${action} ${(en - st)/1000.0} sec"
                } catch (CrossReferenceException e) {
                    log.info "TX ${txCount} abort"
                    status.setRollbackOnly()
                }
            }
        } catch (UnexpectedRollbackException e) {
            // pass
        }
    }


    /**
     *
     * process a sequence of import specifications inside a transaction environment.
     *
     **/
    void receiveInner(String importDomain, String action, Object data) {

        Map modLog = [:]
        Integer createOrUpdateCount = 0
        Integer createCount = 0
        Integer removeCount = 0

        //  we expect data is a list of maps, where underlying maps keyed by classname
        //
        for(datasegment in data) {

            Set allowedActions = []
            allowedActions << 'create'
            datasegment.each { k,v ->
                if (!domainClassMapping[importDomain].containsKey(k)) {
                    log.error "invalid data for ${importDomain}/${action}: ${k}"
                }
                if (domainClassMapping[importDomain][k]) {
                    allowedActions << 'createOrUpdate'
                    allowedActions << 'remove'
                }
                if (!action in allowedActions) {
                    log.error "unsupported import action for domain ${importDomain}/${action}"
                    return
                }

                String className = remapDomainClassName(k)

                switch (action) {
                    case 'createOrUpdate':

                        //  does this record exist and is this therefore an update?
                        boolean create = false
                        Object importObject = findDomainClassInstance(v,className)

                        v = setDefaultProperties(v, className) //set any default propertiesfor our import data
                        def originalProperties = []

                        if(!importObject) {
                            importObject = Class.forName('org.petermac.pathos.curate.'+className).newInstance()
                            create = true
                        } else {
                            originalProperties = importObject.properties    // remember original .properties for logging
                        }

                        def result = saveDomainObject(importObject, v, className, create)

                        if(result) {
                            String lk = modLogKey(className, importObject)
                            if (lk) {
                                if (!modLog[lk]) {
                                    modLog[lk] = 0
                                }
                                modLog[lk] += 1
                            } else {
                                if(create) {
                                    log.info("Created new ${className} ${result.toString()} ${verbose?"with params: ${printParams(v)}":''}")
                                } else {
                                    reportChangedFieldsToLog(originalProperties, result, v, className, verbose)
                                }
                            }
                            createOrUpdateCount += 1
                        } else if (verbose) {
                            log.info("Did not save ${className} ${verbose?"with params: ${v}":''}")
                        }

                        break;
                    case 'create':
                        if(importDomain != 'sequence') {
                            log.error("create action only allowed for sequence domain")
                            return
                        }

                        //  does this record exist?
                        Object importObject = findDomainClassInstance(v,className)
                        if(importObject) {
                            log.error("Cannot create as object already exists - existing ${className} ${verbose?"with params: ${printParams(v)}":''}")
                            return
                        }

                        v = setDefaultProperties(v,className) //set any default properties for our import data

                        importObject = Class.forName('org.petermac.pathos.curate.'+className).newInstance()
                        def result = saveDomainObject(importObject,v,className)

                        if(result) {
                            String lk = modLogKey(className, importObject)
                            if (lk) {
                                if (!modLog[lk]) {
                                    modLog[lk] = 0
                                }
                                modLog[lk] += 1
                            } else {
                                log.info("Created new ${className} ${result.toString()} ${verbose?" with params: ${printParams(v)}":''}")
                            }
                            createCount += 1
                        } else {
                            log.info("Did not save ${className} ${verbose?"with params: ${printParams(v)}":''}")
                        }

                        break;
                    case 'remove':
                        Object importObject = findDomainClassInstance(v,className)
                        if(!importObject) {
                            log.warn "Cannot delete object as it does not exist - ${className} with params: ${printParams(v)} "
                        } else {
                            if(removeRecord(importObject)) {
                                createCount += 1
                                log.info("Deleted record successfully ${className}  ${verbose?"with params: ${printParams(v)}":''}")
                                removeCount += 1
                            } else {
                                log.error("Failed to delete record successfully ${className}  ${verbose?"with params: ${printParams(v)}":''}")
                            }
                        }
                        break;
                    default:
                        log.error "Unrecognised action ${action}"
                        break;
                }
            }
        }

        modLog.each {k, v ->
            log.info "${action} ${v} ${k} objects."
        }
        if (createOrUpdateCount > 0) {
            log.info "createOrUpdate item count: ${createOrUpdateCount}"
        }
        if (createCount > 0) {
            log.info "createCount item count: ${createCount}"
        }
        if (createCount > 0) {
            log.info "removeCount item count: ${removeCount}"
        }

    }

    private String modLogKey(String className, Object obj) {
        switch (className) {
            case 'Patient':
                return "<${obj.urn}> Patient"
            case 'PatSample':
                return "<${obj.patient.urn},${obj.sample}> PatSample"
            case 'PatAssay':
                return "<${obj.patSample.patient.urn},${obj.patSample.sample},${obj.testSet}> PatAssay"
            case 'Seqrun':
                return "${obj.seqrun};${obj.experiment} Seqrun"
            case 'SeqSample':
                return "${obj.seqrun.seqrun}:${obj.sampleName} SeqSample"
            case 'SeqCnv':
                return "${obj.seqSample.seqrun.seqrun}:${obj.seqSample.sampleName} SeqCnv"
            case 'SeqVariant':
                return "${obj.seqSample.seqrun.seqrun}:${obj.seqSample.sampleName} SeqVariant"
        }
        return null
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

    //  set default properties if they are unset
    //
    static Map setDefaultProperties(Map v, String domainClassName) {
        switch (domainClassName) {
            case 'PatSample':
                if(!v.owner) {  //authuser migration is default

                    //  create this user if they do not exist
                    if (AuthUser.findByUsername(defaultOwner) == null)
                    {
                        //  no role so user should not be able to do anything.
                        def defaultuser = new AuthUser(username: defaultOwner, password: 'invalidhash', accountLocked: true, displayName: 'Default Import Owner',email:'no.such.user@petermac.org')
                        if(defaultuser.save(flush:true, failOnError:true)) {
                            log.info("Created new AuthUser as default import owner with username: ${defaultOwner}")
                        }

                    }

                    v.owner = defaultOwner
                }
                break;
        }

        return v
    }


    /**
     * convert import field into appropriate java object for import into domain
     * @param field (from a class)
     * @param classname (matching domain)
     * @return
     */
   private Map convertFieldForImport(Object fieldName, Object fieldValue, String domainClassName) {
        //  get the grails property name for the import field
        String propertyName = getPropertyName(fieldName.toString(), domainClassName)
        //if(propertyRemap[domainClassName].containsKey(fieldName)) {
        //    propertyName = propertyRemap[domainClassName][fieldName]
        //}

       //  coerce the value to be imported into the appropriate class, if need be
       //
       def propertyValue = fieldValue
       String propertyType = Class.forName("org.petermac.pathos.curate." + domainClassName)?.getDeclaredField(propertyName)?.type

       if (propertyType == 'class java.util.Date' || propertyType == 'class java.util.DateTime') {
            propertyValue = convertStringToDate(fieldValue.toString())
       } else if (propertyType == 'class org.petermac.pathos.curate.Panel') {
           propertyValue = Panel.findByManifest(fieldValue.toString())
           if (!propertyValue) {
               log.error "panel not found: ${fieldValue.toString()}"
           }
       } else if (propertyType == 'class org.petermac.pathos.curate.GrpVariant') {
           // GrpVariant objects are kind of special because they are embedded.
           GrpVariant grpVar = GrpVariant.findByAccession(fieldValue['accession'])
           if (!grpVar) {
               grpVar = new GrpVariant(fieldValue)
           }
           propertyValue = grpVar
       } else if (propertyType == 'class org.petermac.pathos.curate.LabAssay') {
           if (fieldValue instanceof Map) {
               propertyValue = LabAssay.findByTestSet(fieldValue.testSet)
           } else {
               propertyValue = LabAssay.findByTestSet(fieldValue.toString())
           }
       } else if (propertyType == 'class org.petermac.pathos.curate.PatSample') {
           if (fieldValue instanceof Map) {
               propertyValue = PatSample.findBySample(fieldValue.sample)
           } else {
               propertyValue = PatSample.findBySample(fieldValue.toString())
           }
       }  else if (propertyType == 'class org.petermac.pathos.curate.Seqrun') {
           propertyValue = Seqrun.findBySeqrun(fieldValue.toString())
           if (!propertyValue) {
               log.error "seqrun not found: ${fieldValue.toString()}"
               throw new CrossReferenceException("seqrun not found: ${fieldValue.toString()}")
           }
       }  else if (propertyType == 'class org.petermac.pathos.curate.SeqSample') {
           assert fieldValue instanceof Map
           String srName =fieldValue['seqRun'] ?: fieldValue['seqrun']
           Seqrun sr = Seqrun.findBySeqrun(srName)
           if (!sr) {
               log.error "while finding SeqSample, Seqrun ${srName} not found"
               throw new CrossReferenceException("while finding SeqSample, Seqrun ${srName} not found")
           }
           propertyValue = SeqSample.findBySeqrunAndSampleName(sr, fieldValue['sampleName'])
           if (!propertyValue) {
               log.error "seq_sample not found: ${fieldValue.toString()}"
               throw new CrossReferenceException("seq_sample not found: ${fieldValue.toString()}")
           }
       }  else if (propertyType == 'class org.petermac.pathos.curate.Patient') {
           if (fieldValue instanceof Map) {
              propertyValue = Patient.findByUrn(fieldValue.urn)
           } else {
              propertyValue = Patient.findByUrn(fieldValue.toString())
           }
       }  else if (propertyType == 'class org.petermac.pathos.curate.ClinContext') {
           if (fieldValue instanceof Map) {
              propertyValue = ClinContext.findByCode(fieldValue.code)
           } else {
              propertyValue = ClinContext.findByCode(fieldValue.toString())
           }
       }  else if (propertyType.contains('java.util.Set')) {

           //need to switch on domainclassname to find out what it is a set of, and add apropriately
           switch (domainClassName) {
               case 'SeqRelation':
                   List seqsamples = new ArrayList<SeqSample>()

                   for (sample in fieldValue) {
                       def ss = SeqSample.findBySeqrunAndSampleName(Seqrun.findBySeqrun(sample.seqRun), sample.sampleName)
                       if (ss) {
                           seqsamples.add(ss)   //AES: not sure if this is necessarey since we back-add seqsamples (ie add the sr to ss, not other way around)
                       } else {
                           log.error("When trying to make SeqRelation, could not find sample ${sample.sampleName} seqrun ${sample.seqRun}")
                           //return
                       }
                   }
                   propertyValue = seqsamples

                   break;
           }
       } else if (propertyType.contains('AuthUser')) {
           if (fieldValue instanceof Map) {
               propertyValue = AuthUser.findByUsername(fieldValue.username)
           } else {
               propertyValue = AuthUser.findByUsername(fieldValue.toString())
           }
       }

        if (propertyValue == null) {
               log.error "for ${fieldName} of ${domainClassName}, failed to find a domain object corresponding to ${fieldValue}"
               throw new CrossReferenceException("for ${fieldName} of ${domainClassName}, failed to find a domain object corresponding to ${fieldValue}")
        }

        return [propertyValue:propertyValue,propertyName:propertyName,propertyType:propertyType]
    }

    static private Date convertStringToDate(String fieldValue) {
        def propertyValue
        switch (fieldValue.size()) {
            case 8:
                propertyValue = new SimpleDateFormat("yyyyMMdd").parse(fieldValue);
                break;
            case 12:
                propertyValue = new SimpleDateFormat("yyyyMMddHHmm").parse(fieldValue);
                break;
            default:
                //unknown
                propertyValue = new SimpleDateFormat("yyyyMMdd").parse(fieldValue); //or something else?
                break;
        }
        return propertyValue
    }


    /**
     * print, to log.info, the details of any fields changed between oldObject and updatedObject that are present in newData
     * @param oldObject
     * @param updatedObject
     * @param newData
     */
    private void reportChangedFieldsToLog(Object oldObject, Object updatedObject, Map newData, String domainClassName, Boolean verbose) {
        if (domainClassName == 'SeqRelation') {
            // SeqRelation objects cannot be directly updated.
            return
        }

        List changes = []
        newData.each{k,v ->
            def propertyName = getPropertyName(k,domainClassName)

            if(oldObject[propertyName] != updatedObject[propertyName]) {
                if(updatedObject[propertyName] instanceof Date) {
                    if (oldObject[propertyName]) {
                        changes << "${propertyName} changed from '${new SimpleDateFormat("yyyyMMdd").format(oldObject[propertyName])}' to '${new SimpleDateFormat("yyyyMMdd").format(updatedObject[propertyName])}'"
                    } else {
                        changes << "${propertyName} changed from '<absent>' to '${new SimpleDateFormat("yyyyMMdd").format(updatedObject[propertyName])}'"
                    }
                } else if(domainClassName == 'Patient') {
                    changes << "${propertyName} changed"
                } else {
                    changes << "${propertyName} changed from '${oldObject[propertyName]}' to '${updatedObject[propertyName]}'"
                }
            }
        }
        if (changes.size() == 0) {
            changes << "with no modifications"
        }
        if (verbose) {
            log.info "Updated ${domainClassName} ${updatedObject.toString()} with params: ${newData} ${changes.join(', ')}"
        } else {
            log.info "Updated ${domainClassName} ${updatedObject.toString()} ${changes.join(', ')}"
        }
    }


    /**
     * given a domain className and the fields of an object ready for import,
     * return the Object if it exists in the database, or null if does not exist

     * @param className
     * @param fields
     * @return
     */
    Object findDomainClassInstance(Map fields, String className) {
        // Switch to deal with exceptional cases.
        // The default behaviour is to use reflection to
        // find the record using the key fields derived
        // from the set of definitions in META-INF/import-mapping.yaml
        //
        switch (className) {
            case 'SeqRelation':
                def seqsamples = new HashSet<SeqSample>()
                for (sample in fields.samples) {

                    def ss = SeqSample.findBySeqrunAndSampleName(Seqrun.findBySeqrun(sample.seqRun),sample.sampleName)
                    if(ss) {
                        seqsamples.add(ss)
                    } else {
                        log.error("When trying to find SeqRelation, could not find sample ${sample.sampleName} seqrun ${sample.seqRun}")
                        return null
                    }
                }

                Set<SeqRelation> srs = []
                for (ss in seqsamples) {
                    for (sr in ss.relations) {
                        if (sr.relation == fields.relation) {
                            srs << sr
                        }
                    }
                }

                for (thisSr in srs) {
                    //  comparing two sets by checking if the ids in both are equal
                    if (thisSr.samples().id.sort() == (seqsamples.id.sort()))   {
                        return thisSr
                    }
                }

                return null

            default:
                if (!domainClassKeys.containsKey(className)) {
                    log.error "'${className}' does not name a type of object that can be imported."
                    return null
                }

                def metaKey = domainClassKeys[className]
                if (metaKey == null) {
                    // No defined key - cannot be queried or
                    // must be handled above as a special case.
                    return null
                }

                // Get the class object.
                //
                def domClass = Class.forName("org.petermac.pathos.curate.${className}")

                // If a single string key is defined for the class,
                // then use the DomainClass.findByMyKey() meta-method.
                //
                if (metaKey instanceof String) {
                    String fldName = getPropertyName(metaKey, className)
                    String meth = "findBy${fldName.capitalize()}"
                    Object key = fields[metaKey]
                    if (key instanceof Map) {
                        key = key[metaKey]
                    }
                    return domClass."$meth"(key)
                }

                // Handle the case where a compound key is given.
                //
                if (metaKey instanceof List) {
                    List keyFields = metaKey
                    Map key = [:]
                    keyFields.each { fld ->
                        // "fix" property names.
                        //
                        String propName = fld
                        if (propertyRemap[className] && propertyRemap[className][fld]) {
                            propName = propertyRemap[className][fld]
                        }

                        Object fldVal = fields[fld]

                        // If the type of the field is another domain class,
                        // we need to recurively query to get the object.
                        //
                        Class fldClass = domClass.getDeclaredField(propName).type
                        if (fldClass.getPackage()?.getName() == 'org.petermac.pathos.curate') {
                            String fldClassName = fldClass.getSimpleName()

                            Map qryFlds = [:]
                            if (domainClassKeys[fldClassName] instanceof String) {
                                qryFlds[domainClassKeys[fldClassName]] = fldVal
                            } else {
                                // If it's a compound key then the fldVal had better be
                                // a map containing the appropriate key/value pairs.
                                qryFlds << (fldVal as Map)
                            }
                            def resObj = findDomainClassInstance(qryFlds, fldClassName)
                            if (!resObj) {
                                return null
                            }

                            key[propName] = resObj
                        } else {
                            // It's just an ordinary "by value" property
                            //
                            key[propName] = fldVal
                        }
                    }
                    log.debug "trying ${className}.findWhere(${key})"
                    return domClass."findWhere"(key)
                }
        }
    }


    /**
     * do our own validation: some objects we dont actually want to import e.g. patassays without am atching lab assay
     *
     * @return true if import allowed, false if not
     */
    static private boolean approveImport(Object rec, String domainName) {
        switch (domainName) {
            case 'PatAssay':
                //only valid if labassay contains testSet
                if(LabAssay.findByTestSet(rec.testSet)) {
                    return true
                } else {
                    log.info("Rejecting PatAssay testset ${rec.testSet}, could not find matching LabAssay")
                    return false
                }

                break;
            default:
                return true
        }
    }


    /**
     * create or update a GORM record
     * //todo can we replace domainName with rec.getType().toString()???
     * @param rec   GORM domain object
     * @param prop  domain object properties to set for above GORM domain object
     * @param flush flag to flush the record
     * @return the updated object
     */
    private Object saveDomainObject(Object rec, Map props, String domainName, Boolean flush = true) {
        //  we can't just set .properties on an Object
        //  so we iterate instead, converting in the meantime
        props.each { importName,importValue ->
            if (domainName == 'SeqRelation' && importName == 'samples') {
                // Skip this - it will get filled in during postProcessRecord.
                return
            }
            Map importField = convertFieldForImport(importName, importValue, domainName)
            log.debug "setting ${domainName}[${importField.propertyName}] = ${importField.propertyValue}"
            if(rec.properties.containsKey(importField.propertyName)) {
                rec[importField.propertyName] = importField.propertyValue
            } else {
                log.error("No such import field ") + importField.propertyName
            }
        }

        //  Validate against our own validator first
        //
        if(!approveImport(rec,domainName)) {
            return null
        }

        //  Validate record
        //
        if (!rec?.validate())
        {
            rec?.errors?.allErrors?.each {
                log.error("GORM record failed validation: " + new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
            }
            return null
        }

        //  Save the record
        //
        if (!rec?.save(flush: flush, failOnError:true))
        {
            rec?.errors?.allErrors?.each {
                log.error("GORM record failed to save: " + new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
            }
            return null
        }

        //  now do post-processing deal with backwards-adding (ie where we call someObject.addOur(rec) isntead of rec.addOurObject()
        //
        if (!postProcessRecord(rec,props,domainName,flush)) {
            log.error("Unable to add record to necessary objects")
        }

        return rec
    }

    /**
     * do post-processing for a new record:
     * for a newly-created record, we may have to add the record to related objects in its properties
     * (ie for ourRecord call someObject.addToSomeCollection(ourRecord) instead of rec.addSomeObject() )
     * or propagate any linking information.
     *
     * Note, this may require the record to be re-saved.
     *
     * @param rec
     * @param props
     * @param domainName
     * @param flush
     * @return true if the post-processing was successful.
     */
    static private boolean postProcessRecord(Object rec,  Map props, String domainName, Boolean flush = true) {
        switch(domainName) {
            case 'SeqRelation':
                for (ssRef in props.samples) {
                    def ss = SeqSample.findBySeqrunAndSampleName(Seqrun.findBySeqrun(ssRef.seqRun), ssRef.sampleName)
                    if (!ss) {
                        log.error "SeqSample ${ssRef.seqRun}/${ssRef.sampleName} not found"
                        return false
                    }
                    ss.addToRelations(rec)
                    //  Save the record
                    //
                    if (!ss?.save(flush: flush, failOnError:true)) {
                        ss?.errors?.allErrors?.each {
                            log.error("GORM record failed to save: " + new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                        }
                        return false
                    }


                    //  Also set Sample Type if it's set
                    //
                    if(ssRef.sampleType && org.petermac.pathos.curate.SeqSample.constraints.sampleType['inList'].contains(ssRef?.sampleType) && ss) {
                        ss.setSampleType(ssRef.sampleType)
                    }
                }
                break;
            case 'SeqSample':
                boolean changed = false
                //  Set Clin Context to generic if not specified
                //
                if(!rec.clinContext) {
                    rec.setClinContext(ClinContext.generic())
                    changed = true
                }

                // Attempt to link the patSample automagically.
                //
                if (!rec.patSample) {
                    def ps = PatSample.findBySample(SampleName.impliedPatientSampleName(rec.sampleName))
                    if (ps) {
                        rec.patSample = ps
                        changed = true
                    }
                }

                // Look for SeqSample relationships and update them if necessary.
                //
                if (rec.patSample) {
                    RelationService.inferReplicatesAndDuplicates(rec)
                    changed = true
                }

                if (changed) {
                    if (!rec.save(flush: flush, failOnError:true)) {
                        rec.errors?.allErrors?.each {
                            log.error("GORM record failed to save: " + new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                        }
                        return false
                    }
                }
                break;
            case 'PatAssay':
                // Link up the LabAssay if it exists.
                //
                LabAssay la = LabAssay.findByTestSet(rec.testSet)
                if (la) {
                    rec.labAssay = la
                    if (!rec.save(flush: flush, failOnError:true)) {
                        rec.errors?.allErrors?.each {
                            log.error("GORM record failed to save: " + new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                        }
                        return false
                    }
                }
                break;
            case 'PatSample':
                //  If we have a seqsample with a null patsample, and the seqsample name
                //  matches the patSample name, set it (as well as any replicates)
                def matchSamples = SeqSample.findAllBySampleNameAndPatSampleIsNull(rec.sample)
                HashSet<SeqSample> setSS = []   //  set of seqsamples wehre we'll set the ps
                for (ss in matchSamples) {
                    for (rel in ss.relations) {
                        if(rel.relation == 'Replicate') {
                            for (relSample in rel.samples()) {
                                setSS.add(relSample)
                            }
                        }
                    }
                    setSS.add(ss)
                }
                for (ss in setSS) {
                    log.info("Linked SeqSample ${ss} to imported PatSample ${rec}")
                    ss.setPatSample(rec)
                }
                break;
        }
        return true
    }

    String remapDomainClassName(String orig) {
        if (classRemap[orig]) {
            return classRemap[orig]
        }
        return orig.capitalize()
    }

    static boolean removeRecord(Object rec,Boolean flush = true) {
        rec.withTransaction
                {
                    //  Save the record
                    //
                    if (!rec?.delete(flush: flush))
                    {
                        rec?.errors?.allErrors?.each {
                            log.error("GORM record failed to delete: " + new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                        }
                        return false
                    }
                    return true
                }
    }

    /**
     * return parameters (.properties) of an object, minus name or fullname, to avoid patient names in logs
     * @param domaiNClassName
     * @return
     */
    String printParams(Map v) {
        if(v.containsKey('name')) {
            v.name = "NAME"
        }
        if(v.containsKey('sex')) {
            v.sex = "X"
        }
        if(v.containsKey('dob')) {
            v.dob = "DOB"
        }
        return v.toString()
    }

    /* only for test: main function */
    static void main(args) {
        BasicConfigurator.configure()
        Logger.getRootLogger().setLevel(Level.INFO)

        def cli = new CliBuilder(
                usage: 'test only')

        cli.with
                {
                    h(longOpt: 'help',              'Usage Information')
                    r(longOpt: 'rdb',     args:1,   'rdb', required:true)
                    v(longOpt: 'verbose',           'verbose')
                }

        def opt = cli.parse(args)

        if ( ! opt ) return

        List argin = opt.arguments()
        if ( opt.h )
        {
            cli.usage()
            return
        }

        def receiver = new PathosImportReceiver(opt.rdb, opt.v)

        def yaml = new YamlCodec()
        for (String fn : opt.arguments()) {
            File f = new File(fn)
            for (Object ob : yaml.loadAll(f.text)) {
                if (!ob) {
                    continue
                }
                receiver.receive(ob['domain'], ob['action'], ob['data'])
            }
        }
    }
}
