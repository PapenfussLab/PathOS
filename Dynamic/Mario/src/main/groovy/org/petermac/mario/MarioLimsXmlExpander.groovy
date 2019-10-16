package org.petermac.babble.mario

import org.petermac.yaml.YamlCodec
import java.nio.file.Path
import java.text.SimpleDateFormat

import org.petermac.babble.api.*
import org.petermac.yaml.*

import groovy.util.logging.Log4j

/**
 * A class for translating the LIMS XML file into
 * a YAML-stuff object for transmission.
 *
 * The class name doesn't reflect the fact that it is a factory
 * because the class is named in the babble script, and calling
 * it a factory there looks weird to users.
 *
 * Author: Tom Conway
 * Date: 2018/02/04
 */
@Log4j
class MarioLimsExpander implements BabbleTranslatorFactory {

    static class Translator implements BabbleTranslator {
        Set wantedAnalysisTypes

        Translator(Set wantedAnalysisTypes) {
            this.wantedAnalysisTypes = []
            if (wantedAnalysisTypes != null) {
                this.wantedAnalysisTypes = wantedAnalysisTypes
            }
        }

        Object translate(Object src) {
            String fn = src
            def lims = new XmlSlurper().parse(new File(fn))

            Map msg = [:]
            msg['domain'] = 'sequence'
            msg['action'] = 'createOrUpdate'
            msg['data'] = []

            // Build the metadata object.
            //
            Map meta = [:]
            meta['datestamp'] = now()
            msg['_meta'] = meta

            String seqrun = lims.RunInfo.@ExperimentName
            String platform = lims.RunInfo.@Platform
            String runlen = lims.RunInfo.@RunLength
            String layout = lims.RunInfo.@SEPE
            String library = lims.Lanes.@LibraryName
            if (library.size() == 0) {
                library = lims.Lanes.Lane.@LibraryName[0]
            }

            def m = (seqrun =~ /^(\d\d\d\d\d\d)_([^_]+)_(\d+)_(.*)/)
            if (!m) {
                log.error "badly-formed seqrun label ${seqrun}"
                return null
            }
            String datestamp = m[0][1]
            String machine = m[0][2]
            String runnum = m[0][3]
            String runid = m[0][4]

            Map srMsg = [:]
            srMsg['seqRun'] = [:]
            srMsg['seqRun']['seqRun'] = seqrun
            srMsg['seqRun']['runDate'] = "20${datestamp}" as String
            srMsg['seqRun']['platform'] = platform
            srMsg['seqRun']['sepe'] = layout
            srMsg['seqRun']['library'] = library
            srMsg['seqRun']['experiment'] = library
            srMsg['seqRun']['scanner'] = machine
            srMsg['seqRun']['readlen'] = runlen

            Set samples = []
            List seqsamples = []
            String laneNo = lims.Lanes.@LaneNumber
            lims.Lanes.'*'.each { l -> 
                def ll = l
                if (l.name() == 'Lane') {
                    laneNo = l.@LaneNumber
                    ll = l.Libraries
                }
                assert ll.name() == 'Libraries'
                ll.Library.each { n ->
                    String sample = n.@LibraryName

                    String panel = n.@AnalysisType
                    if (wantedAnalysisTypes && !(panel in wantedAnalysisTypes)) {
                        // If wantedAnalysisTypes is set and the panel isn't in the
                        // set, then skip this sample.
                        log.info "skipping ${sample} because the analysis type '${panel}' isn't in the wanted list"
                        return
                    }

                    if (platform == 'MiSeq') {
                        panel = n.@ReferenceGenome
                    }

                    String user = n.@UserName
                    if (!user || !user.trim()) {
                        user = "Unspecified Molpath Scientist"
                    }
                    String email = n.@UserEmail
                    if (!email || !email.trim()) {
                        email = "unspecified.molpath.scientist@petermac.org"
                    }

                    if (sample in samples) {
                       return
                    }
                    samples << sample

                    Map ssMsg = [:]
                    ssMsg['seqSample'] = [:]
                    ssMsg['seqSample']['panel'] = panel
                    ssMsg['seqSample']['seqRun'] = seqrun
                    ssMsg['seqSample']['sampleName'] = sample
                    ssMsg['seqSample']['analysis'] = library
                    ssMsg['seqSample']['userName'] = user
                    ssMsg['seqSample']['userEmail'] = email
                    ssMsg['seqSample']['laneNo'] = laneNo

                    if ( sample.startsWith("NTC")) {
                        ssMsg['seqSample']['sampleType'] = "NTC"    // include sample type for sample named as a non-template-control
                    } else if ( sample.startsWith("CTRL") || sample.startsWith("CONTROL") || sample.startsWith("NA12878") || sample.startsWith("NA19240")) {
                        ssMsg['seqSample']['sampleType'] = "Control"    // include sample type for sample named as control
                    }

                    seqsamples  << ssMsg
                }
            }
            if (seqsamples.size() > 0 ) {
                msg['data']  << srMsg
                msg['data'] += seqsamples
            }

            Map sampleKeys = [:]
            samples.each { s ->
                m = (s =~ /([^-]+)(-\d+)?$/)
                if (m) {
                    def sk = m[0][1]
                    if (sampleKeys[sk] == null) {
                        sampleKeys[sk] = []
                    }
                    sampleKeys[sk] << s
                }
            }

            List repls = []
            sampleKeys.each {k, ss ->
                if (ss.size() >= 2) {
                    String base = "${seqrun}-${k}"
                    Map rel = [:]
                    rel['relation'] = 'Replicate'
                    rel['base'] = base
                    rel['samples'] = []
                    for (s in ss) {
                        rel['samples'] << ['seqRun': seqrun, 'sampleName': s]
                    }
                    Map srlMsg = [:]
                    srlMsg['seqRelation'] = rel
                    msg['data'] << srlMsg
                }
            }

            return msg
        }

        /**
         * Get the current time and return it in an appropriate format.
         */
        String now() {
            return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        }
    }

    String name() {
        return 'lims-xml-xpander'
    }

    BabbleTranslator create(BabbleConfigurator confator, Map config) {
        Set wanted
        if ('wanted-analysis-types' in config) {
            wanted = []
            for (String s in config['wanted-analysis-types']) {
                wanted << s
            }
        }
        return new Translator(wanted)
    }

    String usage() {
        return \
"""
The MarioLimsExpander class reads the given LIMS XML file and
extracts all the relevant sample information.

Configuration parameters:
    wanted-analysis-types:   [optional] a list of analysis types. Default is to include all.
"""
    }

    static void main(args) {
        def fac = new MarioLimsExpander()
        println fac.usage()
    }
}
