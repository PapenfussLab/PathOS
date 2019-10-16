package org.petermac.babble.auslab

import org.petermac.babble.api.*
import org.petermac.yaml.*
import groovy.util.logging.Log4j

@Log4j
class AuslabToPathos implements BabbleTranslatorFactory {

    static class Translator implements BabbleTranslator {
        private Set<String> testSets
        private Map checker

        Translator(Map config) {
            Set mustHave = []
            Set mayHave = []
            mayHave << 'testSets'
            mayHave << 'validation'
            YamlConfig.checkParams(config, mustHave, mayHave)

            if (config['testSets']) {
                testSets = YamlConfig.load(config['testSets']) as Set
            }

            if (config['validation']) {
                checker = [:]
                def doms = YamlConfig.load(config['validation'])
                doms.each {dom, acts ->
                    checker[dom] = [:]
                    acts.each {act, con ->
                        checker[dom][act] = new YamlChecker(con)
                    }
                }
            }
        }

        /**
         *
         * Translate the maps-and-lists representation of an HL7 message
         * to maps-and-lists for PathOS.
         *
         */
        Object translate(Object obj) {
            Map msg = obj
            if (msg.keySet().size() != 1) {
                throw new BadData("message must have exactly 1 key (${msg.keySet().size()} found)")
            }
            Object res
            switch (msg.keySet()[0]) {
                case 'OML_O21':
                    res = translateOML_O21(msg['OML_O21'])
                    break
                case 'ADR_A19':
                    res = translateADR_A19(msg['ADR_A19'])
                    break
                default:
                    throw new BadData("unsupported message type ${msg.keySet()[0]}")
            }
            if (checker) {
                assert res['domain']
                assert res['action']
                assert res['data']
                assert checker[res['domain']]
                assert checker[res['domain']][res['action']]
                checker[res['domain']][res['action']].check(res['data'])
            }
            return res
        }

        Map translateOML_O21(Map msg) {
            Map MSH = msg['MSH']
            String msgTime = MSH['Date/Time Of Message'][0]

            // Build the metadata object.
            //
            Map meta = [:]
            meta['datestamp'] = msgTime

            // Get the patient information
            //
            // We won't know if we need it till we've juiced the ORC
            //
            Map pid = mkPatientStuff(msg['PATIENT']['PID'])

            // Extract all the good stuff out of the PV1 segment.
            //
            Map PV1 = msg['PATIENT']['PATIENT_VISIT']['PV1']

            String requester
            for (att in PV1['Attending Doctor']) {
                if (att[12] == 'DN') {
                    List parts = []
                    if (att[5]) { // Dr
                        parts << att[5]
                    }
                    if (att[2]) { // Lisa
                        parts << att[2]
                    }
                    if (att[3]) { // J
                        parts << att[3]
                    }
                    if (att[1]) { // Weston Smith
                        parts += att[1]
                    }
                    if (att[4]) { // Jr
                        parts << att[4]
                    }
                    if (att[6]) { // MD
                        parts << att[6]
                    }
                    requester = parts.join(' ')

                    // If there is more than 1 doctor, we're going to ignore the others.
                    break
                }
            }

            List patLoc = PV1['Assigned Patient Location']
            if (patLoc.size() < 4) {
                throw new BadData("Badly formed Assigned Patient Location")
            }
            if (patLoc[3] == null || patLoc[3].size() != 3) {
                throw new BadData("Badly formed Assigned Patient Location")
            }
            String pathLab = patLoc[3][1]

            // Now start grovelling over the complicated bit: the Order
            //
            if (msg['ORDER'].size() != 1) {
                throw new BadData("Message does not contain unique ORDER block")
            }
            Map ORDER = msg['ORDER'][0]

            Map ORC = ORDER['ORC']

            String sample = ORC['Filler Order Number'][0]
            String extSample
            if (ORC['Placer Order Number'][0] != sample) {
                extSample = ORC['Placer Order Number'][0]
            }

            // Work out if we're getting something new/updated or cancelling
            //
            String oc = ORC['Order Control']
            String os = ORC['Order Status']
            Boolean cancel = ((oc && oc == 'CA') || (os && os == 'CA'))

            // Now to squeeze the TQ1 for juicy goodness
            //
            Map TQ1 = ORDER['TIMING'][0]['TQ1']
            String requestDate = TQ1['Start date/time'] ? TQ1['Start date/time'][0] : null
            // Oh. There wasn't much in that dry shrivelled husk of a segment.

            List samples = []
            List assays = []
            mkSamplesAndAssays(ORDER['OBSERVATION_REQUEST'], samples, assays, cancel)

            assays.each {
                it['patSample'] = sample
            }
            
            if (cancel) {
                Map res = [:]
                res['_meta'] = meta
                res['domain'] = 'patient'
                res['action'] = 'remove'
                res['data'] = []
                assays.each {
                    Map r = [:]
                    r['patAssay'] = it
                    res['data'] << r
                }
                return res
            }

            List itms = []
            itms << pid
            samples.each {
                it['urn'] = pid['patient']['urn']
                it['sample'] = sample
                it['pathlab'] = pathLab
                if (requestDate) {
                    it['requestDate'] = requestDate
                }
                if (extSample) {
                    it['extSample'] = extSample
                }
                if (requester) {
                    it['requester'] = requester
                }
                Map r = [:]
                r['patSample'] = it
                itms << r
            }
            assays.each {
                Map r = [:]
                r['patAssay'] = it
                itms << r
            }

            Map res = [:]
            res['_meta'] = meta
            res['domain'] = 'patient'
            res['action'] = 'createOrUpdate'
            res['data'] = itms
            return res
        }

        Map translateADR_A19(Map msg) {
            Map MSH = msg['MSH']
            List itms = []
            for (Map rsp : msg['QUERY_RESPONSE']) {
                Map pat = mkPatientStuff(rsp['PID'])
                itms << pat
                if (rsp['PV1']) {

                    String requester
                    for (att in rsp['PV1']['Attending Doctor']) {
                        if (att[12] == 'DN') {
                            if (requester != null) {
                                throw BadData("multiple possible requesting doctors")
                            }
                            requester = att[1][0]
                        }
                    }

                    Map sam = [:]
                    sam['patSample'] = [:]
                    sam['patSample']['urn'] = pat['patient']['urn']
                    sam['patSample']['sample'] = msg['QRD']['What Department Data Code'][0][0]
                    sam['patSample']['pathlab'] = rsp['PV1']['Assigned Patient Location'][3][1]
                    if (requester) {
                        sam['patSample']['requester'] = requester
                    }
                    itms << sam
                }
            }

            Map res = [:]
            res['domain'] = 'patient'
            res['action'] = 'createOrUpdate'
            res['data'] = itms
            return res
        }

        Map mkPatientStuff(Map pid) {
            Map m = [:]
            m['patient'] = [:]
            m['patient']['name'] = mkPatientName(pid['Patient Name'])
            m['patient']['urn'] = pid['Patient Identifier List'][0][0]
            m['patient']['dob'] = pid['Date/Time of Birth'][0]
            m['patient']['sex'] = pid['Administrative Sex']
            return m
        }

        String mkPatientName(List nameList) {
            if (nameList.size() < 1) {
                throw new BadData("No Patient Names")
            }
            List nameParts = nameList[0]
            if (nameParts.size() < 1) {
                throw new BadData("Patient Name has no parts!")
            }
            List components = []
            if (nameParts.size() > 1) {
                components << nameParts[1]
            }
            components += nameParts[0]
            return components.join(' ')
        }

        /**
         *
         * Recursively traverse the gunge and find OBR/SPM pairs.
         *
         */
        void mkSamplesAndAssays(Map stuff, List samples, List assays, Boolean cancel) {
            if (!stuff.containsKey('OBR')) {
                throw new BadData("ORDER structure even more weird than normal")
            }
            Map OBR = stuff['OBR']

            if (stuff.containsKey('SPM')) {
                // Ok, we're in one of the strangely formed segment soup bits.

                Map SPM = stuff['SPM'][0]
                mkOneSampleAndAssay(OBR, SPM, samples, assays, cancel)
                return
            }

            if (stuff.containsKey('SPECIMEN')) {
                Map SPM = stuff['SPECIMEN'][0]['SPM']
                mkOneSampleAndAssay(OBR, SPM, samples, assays, cancel)
            }

            if (stuff.containsKey('PRIOR_RESULT')) {
                if (stuff['PRIOR_RESULT'].size() != 1) {
                    throw new BadData("PRIOR_RESULT structure even more weird than normal")
                }
                if (!stuff['PRIOR_RESULT'][0].containsKey('ORDER_PRIOR')) {
                    throw new BadData("PRIOR_RESULT structure didn't have an ORDER_PRIOR")
                }
                for (Map more in stuff['PRIOR_RESULT'][0]['ORDER_PRIOR']) {
                    mkSamplesAndAssays(more, samples, assays, cancel)
                }
            }
        }

        void mkOneSampleAndAssay(Map OBR, Map SPM, List samples, List assays, Boolean cancel) {
            if (!cancel) {
                Map s = [:]
                if (SPM['Specimen Collection Date/Time']) {
                    s['collectDate'] = SPM['Specimen Collection Date/Time'][0][0]
                }
                if (SPM['Specimen Received Date/Time']) {
                    s['rcvdDate'] = SPM['Specimen Received Date/Time'][0]
                }
                samples << s
            }

            Map a = [:]
            a['testSet'] = OBR['Universal Service Identifier'][0]
            a['testName'] = OBR['Universal Service Identifier'][1]

            if (!testSets || testSets.contains(a['testSet'])) {
                assays << a
            }
        }
    }

    String name() {
        return 'to-pathos'
    }

    Translator create(BabbleConfigurator confator, Map config) {
        return new Translator(config)
    }

    String usage() {
        return \
"""
The AuslabToPathos translator converts HL7 messages (expanded into
Maps/Lists), into distilled packets for transport to the Pathos API.

Configuration parameters:

testSets:
    [optional] If specified, this parameter is interpreted as a filename
    for a list of billable codes for assays that should be imported in to PathOS.
    If not specified, all assays are sent through to the PathOS API.

validation:
    [optional] If specified, this parameter names a file containing a YAML
    type specification for valid messages. If specified, this file is loaded,
    and each message is validated before it is returned.
"""
    }

    static main(args) {
        def fac = new AuslabToPathos()
        println fac.usage()
    }
}
