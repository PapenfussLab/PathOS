package org.petermac.hl7

import org.petermac.yaml.*
import groovy.util.logging.Log4j

@Log4j
class Hl7ResultComposer extends Hl7MessageComposer {

    static Map compose(Map args) {
        Set mustHave = []
        mustHave << 'dateOfBirth'
        mustHave << 'episodeID'
        mustHave << 'givenNames'
        mustHave << 'panelID'
        mustHave << 'panelName'
        mustHave << 'pdfBase64'
        mustHave << 'returnID'
        mustHave << 'returnName'
        mustHave << 'sampleID'
        mustHave << 'sex'
        mustHave << 'surname'
        mustHave << 'urn'

        Set mayHave = []
        mayHave << 'requesterID'
        mayHave << 'requester'
        mayHave << 'patientClass'
        mayHave << 'receivedDateTime'
        mayHave << 'resultDateTime'
        mayHave << 'sampleDateTime'
        mayHave << 'specimenType'
        mayHave << 'ward'

        YamlConfig.checkParams(args, mustHave, mayHave)

        log.debug "composing result: ${args['urn']} ${args['episodeID']} ${args['panelID']}"

        if (true) {
            def oul = new YamlComposer()

            oul[['OUL_R21','MSH']] = msh('OUL', 'R21', args)
            oul[['OUL_R21','PATIENT','PID']] = pid(args)
            oul[['OUL_R21','VISIT','PV1']] = pv1(args)
            oul[['OUL_R21','ORDER_OBSERVATION',0,'ORC']] = orc(args)
            oul[['OUL_R21','ORDER_OBSERVATION',0,'OBR']] = obr(args)
            oul[['OUL_R21','ORDER_OBSERVATION',0,'OBSERVATION',0,'OBX']] = obx(args)

            return oul.thing
        } else {
            def oru = new YamlComposer()

            oru[['ORU_R01','MSH']] = msh('ORU', 'R01', args)
            oru[['ORU_R01','PATIENT_RESULT',0,'PATIENT','PID']] = pid(args)
            oru[['ORU_R01','PATIENT_RESULT',0,'PATIENT','VISIT','PV1']] = pv1(args)
            oru[['ORU_R01','PATIENT_RESULT',0,'ORDER_OBSERVATION',0,'ORC']] = orc(args)
            oru[['ORU_R01','PATIENT_RESULT',0,'ORDER_OBSERVATION',0,'OBR']] = obr(args)
            oru[['ORU_R01','PATIENT_RESULT',0,'ORDER_OBSERVATION',0,'TIMING_QTY',0,'TQ1']] = tq1(args)
            oru[['ORU_R01','PATIENT_RESULT',0,'ORDER_OBSERVATION',0,'OBSERVATION',0,'OBX']] = obx(args)
            oru[['ORU_R01','PATIENT_RESULT',0,'ORDER_OBSERVATION',0,'SPECIMEN',0,'SPM']] = spm(args)

            return oru.thing
        }
    }

    static Map orc(Map args) {
        def m = [:]
        m['Order Control'] = 'RE'
        m['Placer Order Number'] = [args['episodeID'], 'AUSLAB']
        m['Filler Order Number'] = [args['episodeID']]
        m['Order Status'] = 'CM'
        return m
    }

    static Map obr(Map args) {
        def m = [:]
        m['Set ID - OBR'] = '1'
        m['Placer Order Number'] = [args['episodeID'], 'AUSLAB']
        m['Filler Order Number'] = [args['episodeID']]
        m['Universal Service Identifier'] = [args['panelID'], args['panelName'], 'AUSLAB', args['panelID']]
        m['Observation Date/Time'] = [args['sampleDateTime']]
        m['Result Status'] = 'R'
        return m
    }

    static Map tq1(Map args) {
        def m = [:]
        m['Set ID - TQ1'] = '1'
        m['Quantity'] = ['1']
        m['Start date/time'] = [args['sampleDateTime']]
        return m
    }

    static Map obx(Map args) {
        def m = [:]
        m['Set ID - OBX'] = '1'
        m['Value Type'] = 'ED'
        m['Observation Identifier'] = ['48014-5', args['returnName'], 'LN', args['returnID'], args['returnName'], 'AUSLAB']
        m['Observation Value'] = [null, 'APPLICATION', 'PDF', 'Base64', args['pdfBase64']]
        m['Observation Result Status'] = 'R'
        return m
    }

    static Map spm(Map args) {
        def m = [:]
        m['Set ID - SPM'] = '1'
        m['Specimen ID'] = [[args['sampleID']]]
        m['Specimen Parent IDs'] = [[[args['episodeID']]]]
        m['Specimen Type'] = [args['specimenType'],args['specimenType']]
        m['Specimen Collection Date/Time'] = [[args['sampleDateTime']]]
        m['Specimen Received Date/Time'] = [args['receivedDateTime']]
        return m
    }
}
