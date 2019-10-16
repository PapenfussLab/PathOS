package org.petermac.hl7

import groovy.util.logging.Log4j
import java.util.concurrent.atomic.AtomicInteger
import java.util.UUID

@Log4j
class Hl7MessageComposer {
    private static UUID uuid = UUID.randomUUID()
    private static AtomicInteger seqNum = new AtomicInteger()

    void maybe(Map m, String fld, Object val) {
        if (val == null) {
            return
        }
        m[fld] = val
    }

    static Map msh(String code, String trigger, Map args) {
        Date now = new Date()
        String codeAndTrigger = "${code}_${trigger}"
        Map m = [:]
        m['Field Separator'] = '|'
        m['Encoding Characters'] = '^~\\&'
        m['Sending Application'] = ['AUSLAB']
        m['Sending Facility'] = ['PM']
        m['Receiving Application'] = ['CLARITY']
        m['Receiving Facility'] = ['Clarity']
        m['Date/Time Of Message'] = [now.format('yyyyMMddHHmmss')]
        m['Message Type'] = [code, trigger, codeAndTrigger]
        if (args['messageID']) {
            m['Message Control ID'] = args['messageID']
        } else {
            m['Message Control ID'] = msgId()
        }
        m['Processing ID'] = ['P']
        m['Version ID'] = ['2.5.1']
        m['Accept Acknowledgment Type'] = 'AL'
        m['Application Acknowledgment Type'] = 'NE'
        return m
    }

    static Map msa(Map args) {
        Map m = [:]
        m['Acknowledgment Code'] = 'CA'
        m['Message Control ID'] = args['messageId']
        return m
    }

    static Map pid(Map args) {
        def m = [:]
        m['Set ID - PID'] = '1'
        m['Patient Identifier List'] = [[args['urn'], null, null, null, 'MR']]
        m['Patient Name'] = [[[args['surname']], args['givenNames']]]
        m['Date/Time of Birth'] = [args['dob']]
        m['Administrative Sex'] = args['sex']
        return m
    }

    static Map savePid(Map pid) {
        Map m = [:]
        m['surname'] = pid['Patient Name'][0][0][0]
        m['givenNames'] = pid['Patient Name'][0][1]
        m['urn'] = pid['Patient Identifier List'][0][0]
        m['dob'] = pid['Date/Time of Birth'][0]
        m['sex'] = pid['Administrative Sex']
        return m
    }

    static Map pv1(Map args) {
        def m = [:]
        m['Set ID - PV1'] = '1'
        m['Patient Class'] = args['patientClass']
        m['Assigned Patient Location'] = [args['ward'], null, null, ['PM', 'Peter MacCallum Cancer Centre', 'AUSLAB']]
        m['Attending Doctor'] = [[args['requesterID'], [args['requester']], null, null, null, null, null, null, ['AUSLAB'], null, null, null, 'DN', ['PM', 'Peter MacCallum Cancer Centre']]]
        return m
    }

    static Map savePv1(Map pv1) {
        Map m = [:]
        m['patientClass'] = pv1['Patient Class']
        m['ward'] = pv1['Assigned Patient Location'][0]
        for (List att : pv1['Attending Doctor']) {
            if (att[12] != 'DN') {
                continue
            }
            m['requesterID'] = att[0]
            m['requester'] = att[1][0]
            break
        }
        return m
    }

    static Map qrd(Map args) {
        def now = new Date()
        def m = [:]
        m['Query Date/Time'] = [args['queryTime'] ?: now.format('yyyyMMddHHmm')]
        m['Query Format Code'] = 'R'
        m['Query Priority'] = 'I'
        m['Query ID'] = args['queryId'] ?: msgId()
        m['Quantity Limited Request'] = ['1', ['RD']]
        m['Who Subject Filter'] = [[args['urn']]]
        m['What Subject Filter'] = [['DEM']]
        m['What Department Data Code'] = [[args['sample'] ?: 'NoSuchCode']]
        return m
    }

    static Map saveQrd(Map qrd) {
        Map m = [:]
        m['queryTime'] = qrd['Query Date/Time'][0]
        m['queryId'] = qrd['Query ID']
        m['urn'] = qrd['Who Subject Filter'][0][0]
        m['sample'] = qrd['What Department Data Code'][0][0]
        return m
    }

    static String msgId() {
        def a = uuid.getMostSignificantBits()
        def b = uuid.getLeastSignificantBits()
        def n = seqNum.getAndIncrement()
        return base36(a, 10) + base36(b + n, 10)
    }

    static String base36(Long x, Integer n) {
        def ds = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        List cs = []
        if (x < 0) {
            x = -x
        }
        for (Integer i = 0; i < n; ++i) {
            int v = x.mod(36)
            x = x.intdiv(36)
            cs << ds[v]
        }
        return cs.join('')
    }
}

