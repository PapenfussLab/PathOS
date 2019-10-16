/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.pathos.curate

import grails.converters.JSON
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.web.json.JSONElement

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Controls the Evidence curation/classification process for variants
 *
 * User: Kenneth Doig
 * Date: 29/08/13
 */

@Log4j
class EvidenceService
{
    /**
     * We're using acmgJustification to store a blob of information, the old one might just be plain text though.
     * Also note that within the blob of information, acmgJustification might be a blank text field.
     */
    static String extractAcmgJustification(String possibleJson) {
        String result = possibleJson
        try {
            JSONElement blob = JSON.parse(possibleJson)
            if (blob?.acmgJustification != null) {
                result = blob.acmgJustification
            }
        } catch (e) {}

        return result
    }


    static private Map<String, Map<String, String>> acmgCriteria = [
        pathogenic: [
            PVS1:"PVS",
            PS1:"PS",
            PS2:"PS",
            PS3:"PS",
            PS4:"PS",
            PM1:"PM",
            PM2:"PM",
            PM3:"PM",
            PM4:"PM",
            PM5:"PM",
            PM6:"PM",
            PP1:"PP",
            PP2:"PP",
            PP3:"PP",
            PP4:"PP",
            PP5:"PP"
        ],
        benign: [
            BA1:"BA",
            BS1:"BS",
            BS2:"BS",
            BS3:"BS",
            BS4:"BS",
            BP1:"BP",
            BP2:"BP",
            BP3:"BP",
            BP4:"BP",
            BP5:"BP",
            BP6:"BP",
            BP7:"BP"
        ]
    ]

    /**
     * Put in params
     * get out cleaned ACMG stuff
     */
    static Map processACMG( Map data, Map defaultCriteria ) {
//        System.out.println("dealing with ACMG data")
//        System.out.println(data)

//  Read JSON if it exists...
//  Read the new changes
//  Updating history
        Map blob = mergeBlob( data, defaultCriteria )

        data.acmgJustification = (blob as JSON).toString()


//  Calculate, applying any (valid) modifications


        data.classification = EvidenceService.calculateACMG(blob)



        return data
    }



    /**
     * Helper function to turn params into a JSON block
     *
     * TODO: Record history, by doing a diff?
     */
    static Map mergeBlob( Map data, Map defaultCriteria ) {

        Map blob

        try {
            blob = JSON.parse(data.blob) ?: [
                acmgJustification: "",
                criteria: [:],
                dropdowns: [:],
                history: [:]
            ]
        } catch (e) {
            blob = [
                acmgJustification: "",
                criteria: [:],
                dropdowns: [:],
                history: [:]
            ]
        }

        blob.acmgJustification = data.acmgJustification

        data.keySet().each { String key ->
            if ( acmgCriteria.pathogenic[key] || acmgCriteria.benign[key] ) {
                if (blob[key] != data[key]) {
                    blob[key] = data[key]

                    if(data[key] != 'unset') {
                        blob = pushHistory(blob, key, data.currentUser, data[key], "Changed Value")
                    }
                }
            } else if (key.endsWith("-text")) {
                if (blob.criteria[key.split("-text")[0]] != data[key]) {
                    blob.criteria[key.split("-text")[0]] = data[key]
                    if(data[key] != '') {
                        blob = pushHistory(blob, key.split("-text")[0], data.currentUser, data[key], "Changed Text")
                    }
                }
            } else if (key.endsWith("-dropdown")) {
                if (blob.dropdowns[key.split("-dropdown")[0]] != data[key]) {
                    blob.dropdowns[key.split("-dropdown")[0]] = data[key]

                    def type = key[0] == 'P' ? 'pathogenic' : 'benign'
                    if(data[key] != defaultCriteria[type][key.split("-dropdown")[0]]?.default) {
                        blob = pushHistory(blob, key.split("-dropdown")[0], data.currentUser, data[key], "Changed Strength")
                    }
                }
            }
        }

        return blob
    }

    private static Map pushHistory(Map blob, String key, String currentUser, String data, String message) {
        try {
            blob.history[key] = blob.history[key] ?: []
            blob.history[key].push([
                user   : currentUser,
                date   : new Date().format("yyyy-MMM-dd HH:mm"),
                message: message,
                data   : data
            ])
        } catch (e) {
            System.out.println(e)
        }

        return blob
    }


    /**
     * This function should just return the highest thing for that evidence.
     * I.e. level A, B = Tier I
     * level C, D = Tier II
     * Anything else = Tier III
     *
     * Tier IV should be for benign but we have no way of doing that..?
     */
    static String calculateAMP ( def obj ) {
//        println "Calculating AMP Tier"
//        println obj

        String result = "Unclassified"

        if ( obj?.therapeuticRating == "levelC" ||
                obj?.diagnosisRating == "levelC" ||
                obj?.prognosisRating == "levelC" ||
                obj?.therapeuticRating == "levelD" ||
                obj?.diagnosisRating == "levelD" ||
                obj?.prognosisRating == "levelD"
        ) {
            result = "Tier II"
        }

        if ( obj?.therapeuticRating == "levelA" ||
             obj?.diagnosisRating == "levelA" ||
             obj?.prognosisRating == "levelA" ||
            obj?.therapeuticRating == "levelB" ||
            obj?.diagnosisRating == "levelB" ||
            obj?.prognosisRating == "levelB"
        ) {
            result = "Tier I"
        }

//        println "Amp tier is ${result}"

        return result
    }

    static String calculateACMG( def obj ) {

        Map m = [
                PVS:        0,
                PS:         0,
                PM:         0,
                PP:         0,
                BA:         0,
                BS:         0,
                BP:         0
        ]

        Map codes = [
            pathogenic: [
                'Very Strong':  'PVS',
                'Strong':       'PS',
                'Moderate':     'PM',
                'Supporting':   'PP'
            ],
            benign: [
                'Stand Alone':  'BA',
                'Strong':       'BS',
                'Supporting':   'BP'
            ]
        ]

        try {
            acmgCriteria.each { Map.Entry criteriaGroup ->

                criteriaGroup.value.each { Map.Entry entry ->

                    String criterion = entry.key

                    if (obj[criterion] && obj[criterion] == 'yes') {

                        m[codes[criteriaGroup.key][obj.dropdowns[criterion]]]++


//  We should never pass back "Default"... 19-March-2019 DKGM
//                        if (obj.dropdowns[criterion] && obj.dropdowns[criterion] != 'Default') {
//                            m[codes[type][obj.dropdowns[criterion]]]++
//                        } else {
//                            m[codes[type][criterion]]++
//                        }

                    }
                }
            }

            int pathScore = 0
            int benignScore = 0

// C5
            if (
                m.PVS >= 1 && m.PS >= 1 ||
                m.PVS >= 1 && m.PM >= 2 ||
                m.PVS >= 1 && m.PM >= 1 && m.PP >= 1 ||
                m.PVS >= 1 && m.PP >= 2 ||
                m.PS >= 2 ||
                m.PS >= 1 && m.PM >= 3 ||
                m.PS >= 1 && m.PM >= 2 && m.PP >= 2 ||
                m.PS >= 1 && m.PM >= 1 && m.PP >= 4
            ) {
                pathScore = 2
// C4
            } else if (
                m.PVS >= 1 && m.PM >= 1 ||
                m.PS >= 1 && m.PM >=1 ||
                m.PS >= 1 && m.PP >= 2 ||
                m.PM >= 3 ||
                m.PM >= 2 && m.PP >= 2 ||
                m.PM >= 1 && m.PP >= 4
            ){
                pathScore = 1
            }

// C1
            if (
                m.BA >= 1 ||
                m.BS >= 2
            ) {
                benignScore = 2

// C2
            } else if (
                m.BS >= 1 && m.BP ||
                m.BP >= 2
            ) {
                benignScore = 1
            }


// Only assign C1, C2, C4, C5 if it is clearly benign or pathogenic. All others get C3
            if ( benignScore == 0 ) {
                if( pathScore == 2 ) {
                    return "C5: Pathogenic"
                } else if ( pathScore == 1 ) {
                    return "C4: Likely pathogenic"
                }
            }

            if ( pathScore == 0 ) {
                if ( benignScore == 2 ) {
                    return "C1: Not pathogenic"
                } else if ( benignScore == 1 ) {
                    return "C2: Unlikely pathogenic"
                }
            }

            Integer total = 0
            m.each { item ->
                total += item.value
            }

            if (total == 0) {
                return "Unclassified"
            } else {
                return "C3: Unknown pathogenicity"
            }

        } catch (e) {
            log.error(e.toString())
            log.error(e.cause)
            log.error(e.message)
            log.error(e.stackTrace)
            log.error(e)
            return e.toString()
        }
    }


// Only useful for the migration
    static String inferACMGclassification( Evidence evidence ) {
        Map m = countACMG( evidence )

        int pathScore = 0
        int benignScore = 0

// C5
        if (
            m.pathVS >= 1 && m.pathS >= 1 ||
            m.pathVS >= 1 && m.pathM >= 2 ||
            m.pathVS >= 1 && m.pathM >= 1 && m.pathP >= 1 ||
            m.pathVS >= 1 && m.pathP >= 2 ||
            m.pathS >= 2 ||
            m.pathS >= 1 && m.pathM >= 3 ||
            m.pathS >= 1 && m.pathM >= 2 && m.pathP >= 2 ||
            m.pathS >= 1 && m.pathM >= 1 && m.pathP >= 4
        ) {
            pathScore = 2
// C4
        } else if (
            m.pathVS >= 1 && m.pathM >= 1 ||
            m.pathS >= 1 && m.pathM >=1 ||
            m.pathS >= 1 && m.pathP >= 2 ||
            m.pathM >= 3 ||
            m.pathM >= 2 && m.pathP >= 2 ||
            m.pathM >= 1 && m.pathP >= 4
        ){
            pathScore = 1
        }

// C1
        if (
            m.benignSA >= 1 ||
            m.benignS >= 2
        ) {
            benignScore = 2

// C2
        } else if (
            m.benignS >= 1 && m.benignP ||
            m.benignP >= 2
        ) {
            benignScore = 1
        }


// Only assign C1, C2, C4, C5 if it is clearly benign or pathogenic. All others get C3
        if ( benignScore == 0 ) {
            if( pathScore == 2 ) {
                return "C5: Pathogenic"
            } else if ( pathScore == 1 ) {
                return "C4: Likely pathogenic"
            }
        }

        if ( pathScore == 0 ) {
            if ( benignScore == 2 ) {
                return "C1: Not pathogenic"
            } else if ( benignScore == 1 ) {
                return "C2: Unlikely pathogenic"
            }
        }

        return "C3: Unknown pathogenicity"

    }

// For migration use only
    private static Map countACMG( Evidence evidence ) {

        Map m = [
            pathVS:     0,
            pathS:      0,
            pathM:      0,
            pathP:      0,
            benignSA:   0,
            benignS:    0,
            benignP:    0
        ]

// Very Strong
        if ( evidence.pathAloneTruncating ) m.pathVS++ // pvs1


        if ( evidence.pathAloneKnown ) m.pathS++ //ps1
// ps2 is missing
        if ( evidence.pathStrongFunction ) m.pathS++ // ps3
        if ( evidence.pathStrongCase ) m.pathS++ // ps4
        if ( evidence.pathStrongCoseg ) m.pathS++  // pp1_strong

        if ( evidence.pathSupportHotspot ) m.pathM++ // pm1
        if ( evidence.pathSupportGmaf ) m.pathM++ //pm2
// pm3 is missing

        if ( evidence.pathSupportIndel ) m.pathM++ // pm4
        if ( evidence.pathSupportNovelMissense ) m.pathM++ // pm5
// pm6 is missing
// pp1_Moderate is missing

        if ( evidence.pathSupportCoseg ) m.pathP++ // pp1
//pp2 is missing
        if ( evidence.pathSupportInsilico ) m.pathP++ // pp3

        if ( evidence.pathSupportGene ) m.pathP++ // pp4

// Not used!!!
//        if ( evidence.pathSupportSpectrum ) m.pathP++  // not included???

        if ( evidence.pathSupportLsdb ) m.pathP++ // pp5



        if ( evidence.benignAloneGmaf ) m.benignSA++ // ba1

        // bs1 is missing
        if ( evidence.benignAloneHealthy ) m.benignS++ //bs2
        if ( evidence.benignStrongFunction ) m.benignS++  // bs3
        if ( evidence.benignStrongCoseg ) m.benignS++  // bs4

        if ( evidence.benignSupportSpectrum ) m.benignP++ // bp1
        if ( evidence.benignSupportPath ) m.benignP++  // bp2
// bp3 is missing
//        if ( evidence.benignSupportVariable ) m.benignP++

        if ( evidence.benignSupportInsilico ) m.benignP++ // bp4
// bp5 is missing
        if ( evidence.benignSupportLsdb ) m.benignP++ // bp6
// bp7 is missing

        return m
    }

    /**
     * For migrating from the old evidence class based on the ACMG draft to the published version
     * 18-Oct-2018 DKGM
     */
    static AcmgEvidence migrateACMG( CurVariant cv ) {
        if(cv.fetchAcmgEvidence()) return null

        Evidence e = cv.evidence

// Note that some fields are missing from the draft:
//    PS2, PM3, PM6, PP1_Moderate, PS1, BP3, BP5, BP7
// and some fields from the draft are not included in the final version:
//    benignSupportVariable, pathSupportSpectrum

        String classification = "Unclassified"

        if( cv.evidence.evidenceClass != "Unclassified" ) {
            classification = inferACMGclassification(e)
        } else if(cv.pmClass == cv.evidence.evidenceClass) {
            cv.setClassOverrideReason("pmClass prior to PathOS v1.5.0 migration")
        }

        AcmgEvidence acmgEvidence = new AcmgEvidence([
            PVS1: e.pathAloneTruncating ? 'yes' : 'unset',
            PS1 : e.pathAloneKnown ? 'yes' : 'unset',
            PS3 : e.pathStrongFunction ? 'yes' : 'unset',
            PS4 : e.pathStrongCase ? 'yes' : 'unset',
            PM1 : e.pathSupportHotspot ? 'yes' : 'unset',
            PM2 : e.pathSupportGmaf ? 'yes' : 'unset',
            PM4 : e.pathSupportIndel ? 'yes' : 'unset',
            PM5 : e.pathSupportNovelMissense ? 'yes' : 'unset',
            PP1 : e.pathStrongCoseg ? 'yes' : e.pathSupportCoseg ? 'yes' : 'unset',
            PP3 : e.pathSupportInsilico ? 'yes' : 'unset',
            PP4 : e.pathSupportGene ? 'yes' : 'unset',
            PP5 : e.pathSupportLsdb ? 'yes' : 'unset',
            BA1 : e.benignAloneGmaf ? 'yes' : 'unset',
            BS2 : e.benignAloneHealthy ? 'yes' : 'unset',
            BS3 : e.benignStrongFunction ? 'yes' : 'unset',
            BS4 : e.benignStrongCoseg ? 'yes' : 'unset',
            BP1 : e.benignSupportSpectrum ? 'yes' : 'unset',
            BP2 : e.benignSupportPath ? 'yes' : 'unset',
            BP4 : e.benignSupportInsilico ? 'yes' : 'unset',
            BP6 : e.benignSupportLsdb ? 'yes' : 'unset',
            acmgJustification : e.justification,
            classification : classification,
            curVariant: cv
        ])


        acmgEvidence.save(flush:true)

        return acmgEvidence
    }






    /**
     * Algorithmic classification based on Evidence object
     *
     * @param   evidence  Evidence object to classify
     * @return            IARC 5 class classification string
     */
    static String inferClass( Evidence evidence)
    {
        Map m = countCriteria( evidence )
        log.debug( "after count: ${m}")

        if ( m.pathAlone >= 1 || m.pathStrong >= 2 || m.pathStrong == 1 && m.pathSupport >= 3)
            return "C5: Pathogenic"

        if ( m.pathStrong == 1 && m.pathSupport >= 2 || m.pathSupport >= 4 )
            return "C4: Likely pathogenic"

        if ( m.benignAlone >= 1 || m.benignStrong >= 2 || m.benignStrong == 1 && m.benignSupport >= 3)
            return "C1: Not pathogenic"

        if ( m.benignStrong == 1 && m.benignSupport >= 2 || m.benignSupport >= 4 )
            return "C2: Unlikely pathogenic"

        return "C3: Unknown pathogenicity"
    }

    /**
     * Count the types of evidence for a CurVariant
     *
     * @param   evidence    Evidence object to count
     * @return              Map of evidence type counts
     */
    private static Map countCriteria( Evidence evidence )
    {
        Map m = [:]
        m.pathAlone     = 0
        m.pathStrong    = 0
        m.pathSupport   = 0
        m.benignAlone   = 0
        m.benignStrong  = 0
        m.benignSupport = 0

        if ( evidence.pathAloneTruncating )     m.pathAlone++
        if ( evidence.pathAloneKnown )          m.pathAlone++

        if ( evidence.pathStrongFunction )      m.pathStrong++
        if ( evidence.pathStrongCase )          m.pathStrong++
        if ( evidence.pathStrongCoseg )         m.pathStrong++

        if ( evidence.pathSupportHotspot )      m.pathSupport++
        if ( evidence.pathSupportGene )         m.pathSupport++
        if ( evidence.pathSupportInsilico )     m.pathSupport++
        if ( evidence.pathSupportSpectrum )     m.pathSupport++
        if ( evidence.pathSupportGmaf )         m.pathSupport++
        if ( evidence.pathSupportIndel )        m.pathSupport++
        if ( evidence.pathSupportNovelMissense )m.pathSupport++
        if ( evidence.pathSupportLsdb )         m.pathSupport++
        if ( evidence.pathSupportCoseg )        m.pathSupport++

        if ( evidence.benignAloneGmaf )         m.benignAlone++
        if ( evidence.benignAloneHealthy )      m.benignAlone++

        if ( evidence.benignStrongFunction )    m.benignStrong++
        if ( evidence.benignStrongCase )        m.benignStrong++
        if ( evidence.benignStrongCoseg )       m.benignStrong++

        if ( evidence.benignSupportVariable )   m.benignSupport++
        if ( evidence.benignSupportInsilico )   m.benignSupport++
        if ( evidence.benignSupportSpectrum )   m.benignSupport++
        if ( evidence.benignSupportLsdb )       m.benignSupport++
        if ( evidence.benignSupportPath )       m.benignSupport++

        return m
    }
}
