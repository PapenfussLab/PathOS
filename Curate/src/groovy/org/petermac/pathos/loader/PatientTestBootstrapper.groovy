/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 */

package org.petermac.pathos.loader

import groovy.util.logging.Log4j
import org.petermac.pathos.curate.SeqVariant
import org.petermac.util.*
import org.apache.commons.cli.Option
import org.petermac.pathos.curate.PatAssay
import org.petermac.pathos.curate.PatSample
import org.petermac.pathos.curate.Patient
import org.petermac.pathos.curate.Seqrun
import org.petermac.pathos.curate.SeqSample
import org.petermac.pathos.curate.Tag
import org.petermac.util.RunCommand
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
/**
 * Created by seleznev andrei on 8/6/17.
 * for auslab testing june 2017
 * setup test data
 */

@Log4j
class PatientTestBootstrapper {


    /**
     * main method for CLI execution
     *
     * @param args
     */
    static void main(args) {
        //	Collect and parse command line args
        //
        def cli = new CliBuilder(usage: "PatientTestBootstrapper -u [options]",
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nLoad Re-import \n')

        //	Options to command
        //
        cli.with
                {
                    h(longOpt: 'help', 'This help message')
                    r(longOpt: 'rdb',  args:1, required: true, 'rdb')
                    d(longOpt: 'delete',  args:0, required: true, 'delete only, do not recreate')
                    f(longOpt: 'file', args: 1, required: true, 'file for patient loader')
                }

        //  Load stand-alone Hibernate context - Database JDBC is embedded in <schema>_loaderContext.xml
        //


        def opt = cli.parse( args )
        if ( ! opt ) return
        if ( opt.help )
        {
            cli.usage()
            return
        }


                        reloadPatientDataFromFile(opt.f,opt.r,opt.d)


    }

    static void deletePatSample(PatSample ps) {
        def sss = SeqSample.findAllByPatSample(ps)
        for (ss in sss) ss.patSample = null

        println "deleting patsample ${ps}"
        ps.delete()

    }

    static void reloadPatientDataFromFile(String filename, String rdb, boolean deleteOnly = false) {
        def db   = new DbConnect( rdb )
        ApplicationContext context = new ClassPathXmlApplicationContext( db.hibernateXml)
        File f = new File(filename)
        def urns = new ArrayList<String>()
        def psamples = new ArrayList<String>()
        def passays = new ArrayList<List>()
        def patassaydetails = []
        def patienttext = f.text


        Patient.withTransaction
                {
                    status ->
                        //grep file to get URN

                        patienttext.eachLine { line ->
                            if (line.contains('urn:')) {
                                def urn = line.replace('urn:', '').trim()
                                urns.add(urn)
                            }
                            if (line.contains('- sample:')) {
                                def ps = line.replace('- sample:', '').trim()
                                psamples.add(ps)
                            }
                            if (line.contains('- testSet:')) {
                                def ts = line.replace('- testSet:', '').trim()
                                patassaydetails.add(ts)
                            }
                            if (line.contains('testName:')) {
                                def tn = line.replace('- testSet:', '').trim()
                                patassaydetails.add(tn)
                                passays.add(patassaydetails)
                                patassaydetails = []
                            }
                        }

                        //delete all patient record w/ matching urn
                        for (a in passays) {
                            PatAssay pa = PatAssay.findByTestSetAndTestName(a[0], a[1])

                            if (pa) {
                                println "deleting patassay ${pa}"
                                pa.delete()

                            }
                        }

                        for (p in psamples) {
                            PatSample ps = PatSample.findBySample(p)
                            if (ps) {
                                //clear seqsample references
                                deletePatSample(ps)
                            }
                        }


                        for (u in urns) {
                            Patient p = Patient.findByUrn(u)
                            if (p) {
                                println "deleting patient ${p}"

                                //delte all related patsamples first
                                def pss = PatSample.findAllByPatient(p)
                                for (ps in pss) deletePatSample(ps)

                                p.delete()
                            }
                        }
                }

        if (deleteOnly) {
            println "Done"
            System.exit(0)
        }

        //run patient load
        String cmd = "LoaderCli -p " + filename + " -r " + rdb
        def out = new RunCommand(cmd).run()
        println "---Output of ${cmd} ---"
        println out


        //now link TESTS and SEQRUNS to these patients
        //go by tag
        SeqSample.withTransaction {
            def runcount = 0
            def sscount = 0
            for (def i = 0; i < urns.size(); i++) {
                // assign existing seqsamples our patiets: any seqsample w/ tag testauslab_X will get assigned to the Xths patient loaded in

                def testTag = Tag.findAllByLabel("testauslab_" + i)


                def sss = SeqSample.executeQuery("from org.petermac.pathos.curate.SeqSample as ss WHERE :testtag in elements (ss.tags)",[testtag:testTag])

                Patient p = Patient.findByUrn(urns[i])
                for (ss in sss) {
                    runcount++
                    //grab samples, grab patient

                    //grab any ol' patsample by them
                    PatSample ps = PatSample.findByPatient(p)
                    //chuck these seqrun's ss over

                    ss.patSample = ps

                    sscount++

                }

                println "Patient ${p} got ${runcount} runs ${sscount} samples linked by tag ${testTag}"

                runcount = 0
                sscount = 0

                // if we really want to: RENAME an existing sample to the name of a newly importated patsample (so we have a seqsample in the 80001XXXX style of name)
                // as above, but w/ tag testrename_X and we also only do this for one sample onky
                /*
                def testRename = Tag.findAllByLabel("testrename_${i}")
                def ss = SeqSample.findByTags(testRename)
                if (ss) {
                    def svs = SeqVariant.findAllBySeqSample(ss)
                    ss.sampleName = psamples[i]
                    for (sv in svs) sv.sampleName = psamples[i]

                } */
            }
        }

    }

}