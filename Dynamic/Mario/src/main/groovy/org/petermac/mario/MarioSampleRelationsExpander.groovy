package org.petermac.babble.mario

import org.petermac.yaml.YamlCodec
import java.nio.file.Path
import java.text.SimpleDateFormat
import au.com.bytecode.opencsv.CSVReader

import org.petermac.babble.api.*
import org.petermac.yaml.*

import groovy.util.logging.Log4j

/**
 * A class for translating the SampleRelationship.csv file into
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
class SampleRelationshipExpander implements BabbleTranslatorFactory {

    static class Translator implements BabbleTranslator {
        Translator() {
        }

        Object translate(Object src) {
            String fn = src

            CSVReader reader = new CSVReader(new FileReader(fn))

            String [] nextLine
            def first = true
            String type = ''

            Map msg = [:]
            msg['domain'] = 'sequence'
            msg['action'] = 'createOrUpdate'
            msg['data'] = []
            while ((nextLine = reader.readNext()) != null) {
                if(first) {
                    //  figure out type from header. TN: #Tumor RunName,Tumor SampleName,Normal RunName,Normal SampleName
                    //  at time of writing we only ever parse a TN file. will
                    if(nextLine[0] == '#Tumor RunName' || nextLine[0] == '#Tumour RunName') {
                        type = "TumourNormal"
                    }
                    first = false
                    continue
                }

                switch(type) {
                    case "TumourNormal":
                        if (nextLine[0].isEmpty() || nextLine[1].isEmpty() || nextLine[2].isEmpty() || nextLine[3].isEmpty()) {
                            continue
                        }
                        String tumourSeqrun = nextLine[0]
                        String tumourSeqSample = nextLine[1]
                        String normalSeqrun = nextLine[2]
                        String normalSeqSample = nextLine[3]
                        String diffSeqSample = "${tumourSeqSample}--${normalSeqSample}"

                        Map diffSample = [:]
                        diffSample['seqRun'] = tumourSeqrun
                        diffSample['sampleName'] = diffSeqSample
                        diffSample['sampleType'] = 'TumourNormal'

                        // Fill in some dummy values required by SeqSample
                        //
                        diffSample['panel'] = 'Pathology_hyb_CCP_2'
                        diffSample['laneNo'] = '1'
                        diffSample['analysis'] = 'Seqliner0.6.1_pathhyb_somatic'
                        diffSample['userName'] = 'A User'
                        diffSample['userEmail'] = 'a.user@petermac.org'

                        msg['data'] << ['seqSample': diffSample]

                        Map tumourUpdate = ['seqRun': tumourSeqrun, 'sampleName': tumourSeqSample, 'sampleType': 'Tumour']
                        msg['data'] << ['seqSample': tumourUpdate]

                        Map normalUpdate = ['seqRun': normalSeqrun, 'sampleName': normalSeqSample, 'sampleType': 'Normal']
                        msg['data'] << ['seqSample': normalUpdate]

                        Map rel = [:]
                        rel['relation'] = type
                        rel['base'] = ("${tumourSeqrun}-${diffSeqSample}") as String
                        rel['samples'] = []
                        rel['samples'] << ['seqRun': tumourSeqrun, 'sampleName': tumourSeqSample]
                        rel['samples'] << ['seqRun': normalSeqrun, 'sampleName': normalSeqSample]
                        rel['samples'] << ['seqRun': tumourSeqrun, 'sampleName': diffSeqSample]
                        msg['data'] << ['seqRelation': rel]
                        break;
                }
            }

            // Build the metadata object.
            //
            Map meta = [:]
            meta['datestamp'] = now()
            msg['_meta'] = meta

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
        return 'sample-relationship-xpander'
    }

    BabbleTranslator create(BabbleConfigurator confator, Map config) {
        return new Translator()
    }

    String usage() {
        return \
"""
The SampleRelationshipExpander class reads the given csv file and
extracts all the relevant sample information.

Configuration parameters: none.
"""
    }

    static void main(args) {
        def fac = new SampleRelationshipExpander()
        println fac.usage()
    }
}
