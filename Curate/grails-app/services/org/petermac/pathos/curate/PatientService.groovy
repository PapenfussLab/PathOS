/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.pathos.curate

import groovy.util.logging.Log4j
import org.petermac.util.DateUtil
import org.petermac.util.Locator

import java.text.MessageFormat
import java.text.SimpleDateFormat

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Controls the processing of patient data
 *
 * User: Andrei Seleznev
 * Date: 26/06/17
 */

@Log4j
class PatientService
{
    static def loc = Locator.instance


    /**
     * This function takes in a PatSample ps and sends a request to Auslab
     * for updated details. Because the update is asynchronous, the UI will
     * simply have to display last checked and last updated dates.
     * @return 0
     */
    public Integer refreshPatient(PatSample ps) {
        if (loc.pathosExport) {
            Map qryArgs = [:]
            Patient p               = ps.patient
            qryArgs['urn']   = p.urn
            qryArgs['sample']    = ps.sample
            loc.pathosExport.receive('patient', 'request', qryArgs)
        }
        return 0
    }

    /**
     * return a list of valid test codes (ie ones that exist in labassay). we only load patassays w/ valid codes when parsing from auslab
     * @param rdb
     * @return
     */
    public static List validTestCodes() {
        def las = LabAssay.findAll()
        def testCodes = []
        for (la in las) {
            testCodes.add(la.testSet)
        }
        return testCodes
    }
    /**
     * publish a PDF to auslab
     * returns a YAML dump of the response (look for the ACK)
     *
     * @param p patient
     * @param s patSample
     * @param f File obect
     * @return
     */
    public void publishToAuslab( PatSample ps, File f) {
        if (!loc.pathosExport) {
            log.warn("No export plugin loaded. Consider setting pathos.export.config in the pathos.properties file.")
            return
        }

        Patient p = ps.patient;

        def pdf = f.getBytes().encodeBase64().toString()

        //  build map of args for resultpublisher
        //
        Map res = [:]
        List pnameList =  p.fullName.split(' ')
        res['urn'] = p.urn
        res['surname'] = pnameList.last()
        res['givenNames'] =  pnameList.size() > 1 ? pnameList.take(pnameList.size() - 1).join(' ') : p.fullName
        res['sex'] = p.sex
        res['dateOfBirth'] = new SimpleDateFormat("yyyyMMdd").format(p.dob)
        res['episodeID'] = ps.sample

        res['pdfBase64'] = pdf
        res['sampleID'] = ps.sample

        if (!ps.patAssays) {
            log.warn("PatSample ${ps} has no PatAssays, refusing to publish")
            return
        }
        PatAssay pa = ps.patAssays.sort(false)[0] //first patassay alphabeticaly
        LabAssay la = LabAssay.findByTestSet(pa.testSet)

        if (!la) {
            log.warn("No labassay for testset ${pa.testSet}, refusing to publish")
            return
        }

        res['panelID'] = la.panelReportable
        res['panelName'] = la.panelReportableName
        res['returnID'] = la.returnCode
        res['returnName'] = la.returnName

        log.info "publishing ${p.urn} ${ps.sample} ${la.panelReportable} ${la.returnCode}"
        loc.pathosExport.receive('report', 'publish', res)
    }

    /**
     * Load YAML file of patients into GORM db
     *
     *
     * @param patf      YAML file of patients
     * @param dbname    Database to load
     * @return          Number of records added or changed
     */
    public static int loadAllPatients( List<Map> pats)
    {
        int npat = 0
        int recordsChanged = 0

        for (pat in pats)
        {
            try
            {
                Map updated = loadPatient( pat.content )
                ++npat

                if ( updated ) log.info("Parsed patient ${npat}, loaded: ${updated}")
                //  calc number of records mutated (either added or changed) updated is a map w/ key as descriptor, value as int of records changed
                updated.each{ k,v ->
                    recordsChanged = recordsChanged + v
                }
            }
            catch(e)
            {
                log.error( "Failed to load Patient(${pat}) Exception: ${e}")
                println org.codehaus.groovy.runtime.StackTraceUtils.sanitize(new Exception(e)).printStackTrace()
            }
        }


        return recordsChanged
    }


    /**
     * Load or update a Patient record
     * @param   pat Map of Patient attributes
     * @return      Map of records updated
     */
    static Map loadPatient( Map pat )
    {
        Map updated = [:]
        updated['Patients added'] = 0
        updated['Patients changed'] = 0

        log.debug( "Adding Patient(${pat})")

        //  Map values for Patient
        //
        Map p =     [
                fullName:   pat.fullName,
                urn:        pat.urn,
                dob:        DateUtil.dateParse( "yyyy-MM-dd", pat.dob as String),
                sex:        pat.sex
        ]


        //  Find or Load patient
        //
        Patient patient = Patient.findByUrn( pat.urn )
        if ( ! patient )
        {
            patient = new Patient( p )
            if ( ! saveRecord( patient ))
            {
                println "Could not save patient"
                log.warn("Could not save patient " + p)
                return updated
            }
            updated['Patients added'] = 1
            log.info("Created new patient with URN ${p.urn}")
        }
        else
        {


            //  Update Patient if details have changed
            //
            if ( patient.fullName != p.fullName || patient.dob != p.dob || patient.sex != p.sex )
            {

                log.warn( "Patient details changed for urn: ${pat.urn}")


                if ( changeRecord( patient, p ))
                {
                    log.warn("Changed pa")
                    updated['Patients changed'] = 1
                }

            }
        }

        //  Load Patient Samples
        //
        for ( ps in pat.patSamples )
        {
             updated += loadPatSample( patient, ps, updated )
        }

        return updated
    }

    /**
     * Load a PatSample record
     *
     * @param patient   Parent Patient record
     * @param PatSample Map of PatSample
     * @param updated     Map of records updated
     * @return          Map of records updated
     */
    static Map loadPatSample( Patient patient, Map patSample, Map updated )
    {
        log.debug( "Loading PatSample(${patSample}) to ${patient}")
        updated['PatSamples added'] = 0
        updated['PatSamples changed'] = 0
        updated['PatAssays added'] = 0
        updated['PatAssays changed'] = 0
        updated['PatAssays deleted'] = 0

        //  Map values for PatSample
        //
        Map ps =    [
                patient:    patient,
                sample:     patSample.sample,
                owner:      AuthUser.findByUsername( patSample.owner ),
                extSample:  patSample.extSample,

                requester:  patSample.requester,
                pathlab:    patSample.pathlab
        ]

        if(  patSample.rcvdDate ) {
            ps['rcvdDate'] =  DateUtil.dateParse( 'yyyy-MM-dd', patSample.rcvdDate as String )
        }

        if(  patSample.requestDate ) {
            ps['requestDate'] =  DateUtil.dateParse( 'yyyy-MM-dd', patSample.requestDate as String )
        }

        if(  patSample.collectDate ) {
            ps['collectDate'] =  DateUtil.dateParse( 'yyyy-MM-dd', patSample.collectDate as String )
        }

        //  Load patSample
        //
        PatSample addps = PatSample.findBySample( patSample.sample )

        if ( ! addps )
        {
            addps = new PatSample( ps )
            if ( ! saveRecord( addps ))
            {
                log.warn("Unable to create new PatSample record, abandoning loading this record")   //AES: do we actually want to return here?
                return updated
            }
            ++updated['PatSamples added']
            log.info("Created new PatSample ${ps.sample}")
        }
        else {
            //  Update PatAssay if details have changed. the below retu
            //
            if ( changeRecord( addps, ps ) > 0 )
            {
                log.debug("Updated PatSample record")
                ++updated['PatSamples changed']
            }
        }


        //  Load Patient Assays
        //
        for ( pa in patSample.patAssays )
        {
            def loaded = loadPatAssay( addps, pa, updated )
            updated['PatAssays changed'] += loaded['PatAssays changed']
            updated['PatAssays added'] += loaded['PatAssays added']

        }


        //  Delete Patient Assays
        //
        for ( pa in patSample.patAssaysCancelled )
        {
            if(deletePatAssay( pa , addps )) {
                ++updated['PatAssays deleted']
            }
        }
        return updated
    }

    static boolean deletePatAssay(Map pa, PatSample patSample, boolean flush = true) {
        PatAssay passay = PatAssay.findByPatSampleAndTestSet(patSample,pa.testSet)
        if(!passay) {
            log.info("Cannot delete: can't find PatAssay ${pa.testSet} ${patSample.sample} to delete")
            return false
        }

        passay.delete(flush:flush)

        log.info("Deleted PatAssay testSet ${pa.testSet} PatSample ${patSample.sample}")

        return true
    }
    /**
     * Load a PatAssay record
     *
     * @param patSample Parent PatSample
     * @param patAssay  Map of PatAssay
     * @param updated     Map of records updated
     * @return          Map of records updated
     */
    static Map loadPatAssay( PatSample patSample, Map patAssay, Map updated )
    {
        log.debug( "Adding PatAssay(${patAssay}) to ${patSample}")
        updated['PatAssays added'] = 0

        //  Set LabAssay if one exists
        //
        LabAssay labAssay = LabAssay.findByTestSet( patAssay.testSet )

        //  Map values for PatAssay
        //
        Map pa =    [
                patSample:  patSample,
                testSet:    patAssay.testSet,
                testName:   (patAssay.testName).trim(),
                labAssay:   labAssay
        ]

        //  Find or Load patAssay
        //
        PatAssay passay =  PatAssay.findByPatSampleAndTestSet( patSample, patAssay.testSet )

        if ( passay )   //exists, update
        {
            log.debug( "PatAssay exists ${passay}")
            if ( passay?.testName != pa.testName ) {
                log.debug("PatAssay changed name, updating")

                if (changeRecord(passay,pa) > 0) {
                    ++updated['PatAssays changed']
                }
            }
        } else {    //does not exist, create
            PatAssay parec = new PatAssay(pa)
            if (saveRecord(parec)) {

                ++updated['PatAssays added']
                log.info("Created new PatAssay testSet ${parec.testSet} testName ${parec.testName}")
            }
        }
        return updated
    }



    /**
     * Save a GORM record with validation
     *
     * @param rec   GORM domain object
     * @param flush flag to flush the record
     * @return      true is successfully saved
     */
    static boolean saveRecord( Object rec, boolean flush = true )
    {
        rec.withTransaction
                {

                    //  Validate record
                    //
                    if (!rec?.validate())
                    {
                        rec?.errors?.allErrors?.each {
                            log.error("GORM record failed validation: " + new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                        }
                        return false
                    }

                    //  Save the record
                    //
                    if (!rec?.save(flush: flush))
                    {
                        rec?.errors?.allErrors?.each {
                            log.error("GORM record failed to save: " + new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                        }
                        return false
                    }
                    return true
                }


    }

    /**
     * Update a changed GORM record with the provided properties map and validation
     * returns FALSE is record is not changed. we only return true if record is changed
     *
     * @param rec   GORM domain object
     * @param flush flag to flush the record
     * @return int number of record properties changed, or null upon failure
     */
    static int changeRecord( Object rec, Map props, boolean flush = true )
    {

        rec.withTransaction
                {

                    int propertiesChanged = 0
                    props.each{ k,v ->
                        //  we can't just set .properties on an Object
                        //  so we iterate instead
                        if(rec.properties.containsKey(k)) {
                            if (rec[k] != v ) {
                                rec[k] = v
                                propertiesChanged++
                            }
                        }
                    }

                    //  Return false if record not changed
                    if(propertiesChanged == 0) return propertiesChanged

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
                    if (!rec?.save(flush: flush))
                    {
                        rec?.errors?.allErrors?.each {
                            log.error("GORM record failed to save: " + new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                        }
                        return null
                    }

                    return propertiesChanged

                }

    }
}
