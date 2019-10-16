package org.petermac.babble.mario

import org.petermac.babble.api.*
import org.petermac.yaml.*

import au.com.bytecode.opencsv.CSVReader
import groovy.util.logging.Log4j

/**
 *
 * A BabbleTranslator that reads CSV files and turns them into messages.
 *
 */
@Log4j
class MarioCSVTranslator implements BabbleTranslatorFactory {
    static class CSVTranslatorImpl implements BabbleTranslator {
        Map config
        Map columns

        CSVTranslatorImpl(Map cfg) {
            config = cfg
        }

        Object translate(Object src) {
            String fn = MarioMessageTransformer.transform(config['from'], src)

            Object wrapper = null
            if (config['wrapper']) {
                wrapper = MarioMessageTransformer.transform(config['wrapper'], src, false)
            }

            Object tmplt = null
            if (config['template']) {
                tmplt = MarioMessageTransformer.transform(config['template'], src, false)
            }

            char separator = ','
            if ('separator' in config) {
                separator = config['separator'][0]
                log.debug "using `${separator}` as the separator"
            }

            String empty = ''
            Boolean omitEmpty = false
            if ('empty' in config) {
                if (config['empty'] == null || config['empty'] == 'null') {
                    empty = null
                } else if (config['empty'] == 'omit') {
                    omitEmpty = true
                } else {
                    empty = config['empty']
                }
            }

            CSVReader reader = new CSVReader(new FileReader(fn), separator)

            def first = true
            if ('header' in config) {
                first = config['header'].toBoolean()
            }

            Map idx = [:]

            List rows = []

            String [] nextLine
            while ((nextLine = reader.readNext()) != null) {
                if (first) {
                    for (int i = 0; i < nextLine.length; i++) {
                        idx[nextLine[i]] = i
                    }
                    if ('fields' in config) {
                        Map flds = [:]
                        Set specKeys = ['column', 'type']
                        Set fldTypes = ['str', 'int', 'float']
                        config['fields'].each { k,v ->
                            Integer colNum = null
                            Object colName = v
                            String colType = 'str'
                            if (v instanceof Map) {
                                Map spec = v
                                spec.keySet().each { sk ->
                                    if (sk in specKeys) {
                                        return
                                    }
                                    log.error "specification for field ${k} contains unrecognized field '${sk}'"
                                }
                                if (!spec.containsKey('column')) {
                                    log.error "specification for field ${k} must contain a column"
                                    return
                                }
                                colName = spec['column']
                                if (spec.containsKey('type')) {
                                    colType = spec['type']
                                }
                            }
                            if (colName instanceof Integer) {
                                // Fields are numbered from 1, indexs from 0
                                colNum = colName - 1
                            }
                            if (colName.isInteger()) {
                                // Fields are numbered from 1, indexs from 0
                                colNum = colName.toInteger() - 1
                            }
                            if (colNum == null) {
                                if (!idx.containsKey(colName)) {
                                    log.error "field ${colName} not in headers (${idx.keySet()})"
                                    return
                                }
                                colNum = idx[colName]
                            }
                            if (!fldTypes.contains(colType)) {
                                log.error "field ${colName} specification contains unrecognized type '${colType}'"
                                return
                            }
                            flds[k] = [col: colNum, type: colType]
                        }
                        idx = flds
                        log.debug "using fields ${idx}"
                    }
                    first = false
                    continue
                }
                Map row = [:]
                idx.each { k,v ->
                    if (nextLine[v.col] != '') {
                        row[k] = coerceType(nextLine[v.col], v.type)
                        return
                    }
                    if (omitEmpty) {
                        return
                    }
                    row[k] = empty
                }
                if (tmplt) {
                    tmplt = MarioMessageTransformer.transform(tmplt, row)
                    rows << substituteStar(tmplt, row)
                } else {
                    rows << row
                }
            }

            if (!wrapper) {
                return rows
            }
            return populateWrapper(wrapper, rows)
        }

        Object coerceType(String src, String typeName) {
            if (typeName == 'str') {
                return src
            }
            if (typeName == 'int') {
                return src.toInteger()
            }
            if (typeName == 'float') {
                if (src == 'NA') {
                    return null
                }
                return src.toDouble()
            }
            assert false
        }

        Object populateWrapper(Object wrapper, List items) {
            Map vars = [:]
            vars['items'] = items
            MarioMessageTransformer.transform(wrapper, vars)
        }

        Object substituteStar(List tmpl, Map flds) {
            List res = []
            tmpl.each { itm ->
                res << substituteStar(itm, flds)
            }
        }

        Object substituteStar(Map tmpl, Map flds) {
            Map res = [:]
            Boolean addHere = false
            tmpl.each { k,v ->
                if (k == '*' && v == null) {
                    addHere = true
                } else {
                    res[k] = substituteStar(v, flds)
                }
            }
            if (!addHere) {
                return res
            }
            flds.each { k,v ->
                if (res.containsKey(k)) {
                    log.error "key ${k} present in both template and substitution."
                }
                res[k] = v
            }
            return res
        }

        Object substituteStar(Object tmpl, Map flds) {
            return tmpl
        }
    }

    String name() {
        return 'csv-translator'
    }

    /**
     *
     * Create an identity translator. Has no parameters.
     *
     */
    BabbleTranslator create(BabbleConfigurator confator, Map config) {
        Set mustHave = []
        mustHave << 'from'

        Set mayHave = []
        mayHave << 'template'
        mayHave << 'wrapper'
        mayHave << 'separator'
        mayHave << 'fields'
        mayHave << 'empty'

        YamlConfig.checkParams(config, mustHave, mayHave)
        
        return new CSVTranslatorImpl(config)
    }

    String usage() {
        return \
"""
Translate a CSV into a message
"""
    }
}

