package org.petermac.pathos.curate

import grails.test.spock.IntegrationSpec
import spock.lang.Specification


/**
 *
 */


class RelationServiceSpec extends Specification {
    def RelationService

    void "test SeqRelation dup"() {
        given:

        when:

            Seqrun testSeqrun = new Seqrun(seqrun:"REP_TEST",runDate:new Date( ),platform:"platform",sepe:"sepe",readlen:"readlen",library:"library",experiment:"experiment",scanner:"scanner").save(flush:true,failOnError:true)
            Seqrun testSeqrunOlder = new Seqrun(seqrun:"REP_TEST2",runDate:new Date(System.currentTimeMillis() - (4 * 60 * 60 * 1000)),platform:"platform",sepe:"sepe",readlen:"readlen",library:"library",experiment:"experiment",scanner:"scanner").save(flush:true,failOnError:true)
            println "NOW" + testSeqrun.runDate
            println  "OLDR" + testSeqrunOlder.runDate
            Panel testPanel = new Panel(manifest:"manifest",panelGroup:"panelGroup").save(flush:true,failOnError:true)
            def testSample1 = new SeqSample(sampleName: "10A1",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)     //these two are reps
            def testSample2 = new SeqSample(sampleName: "10A1-1",seqrun:testSeqrunOlder,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)   //


            def sr = new SeqRelation(samples:testSample1,relation:'Duplicate',base:'none')

            sr.setRelation("Duplicate")
            sr.addToSamples(testSample2)

        then:
            testSeqrun.delete()
            testSeqrunOlder.delete()
            sr.delete()
            testSample1.delete()
            testSample2.delete()
            testPanel.delete()
            assert RelationService.getPrimary(sr) == testSample2

    }

    void "test SeqRelation rep"() {
        given:

        when:



            Seqrun testSeqrun = new Seqrun(seqrun:"REP_TEST",runDate:new Date(System.currentTimeMillis()),platform:"platform",sepe:"sepe",readlen:"readlen",library:"library",experiment:"experiment",scanner:"scanner").save(flush:true,failOnError:true)

            Panel testPanel = new Panel(manifest:"manifestA",panelGroup:"panelGroup").save(flush:true,failOnError:true)
            def a = new SeqSample(sampleName: "10A1",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)     //these two are reps
            def b = new SeqSample(sampleName: "10A1-2",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)   //


            def sr = new SeqRelation(samples:b,relation:'Replicate',base:'none')
            sr.setRelation("Replicate")

            sr.addToSamples(a)

        then:
            a.delete()
            b.delete()
            testSeqrun.delete()

            testPanel.delete()
           assert RelationService.getPrimary(sr) == a

    }

    void "test SeqRelation rep 2"() {
        given:
        //
        when:

            Seqrun testSeqrun = new Seqrun(seqrun:"REP_TEST",runDate:new Date(System.currentTimeMillis()),platform:"platform",sepe:"sepe",readlen:"readlen",library:"library",experiment:"experiment",scanner:"scanner").save(flush:true,failOnError:true)

            Panel testPanel = new Panel(manifest:"manifestA",panelGroup:"panelGroup").save(flush:true,failOnError:true)
            def a = new SeqSample(sampleName: "10A1-2",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)     //these two are reps
            def b = new SeqSample(sampleName: "10A1-1",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)   //
            def c = new SeqSample(sampleName: "10A1-1-1",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)   //




            def sr = new SeqRelation(samples:b,relation:'Replicate',base:'none')
            sr.setRelation("Replicate")

            sr.addToSamples(a)
            sr.addToSamples(c)

        then:
            a.delete()
            b.delete()
            c.delete()
            testSeqrun.delete()
            sr.delete()
            testPanel.delete()
            assert RelationService.getPrimary(sr) == b

    }

    void "test SeqRelation rep 3"() {
        given:
        //
        when:

            Seqrun testSeqrun = new Seqrun(seqrun:"REP_TEST",runDate:new Date(System.currentTimeMillis()),platform:"platform",sepe:"sepe",readlen:"readlen",library:"library",experiment:"experiment",scanner:"scanner").save(flush:true,failOnError:true)

            Panel testPanel = new Panel(manifest:"manifestA",panelGroup:"panelGroup").save(flush:true,failOnError:true)
            def a = new SeqSample(sampleName: "10A1-2",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)     //these two are reps
            def b = new SeqSample(sampleName: "10A1-1",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)   //
            def c = new SeqSample(sampleName: "10A1-1-1",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)   //
            def d = new SeqSample(sampleName: "10A1",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)   //

            def sr = new SeqRelation(samples:a,relation:'Replicate',base:'none')
            sr.setRelation("Replicate")

            sr.addToSamples(b)
            sr.addToSamples(c)
            sr.addToSamples(d)

        then:
            a.delete()
            b.delete()
            c.delete()
            testSeqrun.delete()
            sr.delete()
            testPanel.delete()
            assert RelationService.getPrimary(sr) == d  //no suffix

    }

    void "test SeqRelation rep test"()
    {
        given:
        //
        when:
         Seqrun testSeqrun = new Seqrun(seqrun:"REP_TEST",runDate:new Date(System.currentTimeMillis()),platform:"platform",sepe:"sepe",readlen:"readlen",library:"library",experiment:"experiment",scanner:"scanner").save(flush:true,failOnError:true)

            Panel testPanel = new Panel(manifest:"manifestA",panelGroup:"panelGroup").save(flush:true,failOnError:true)
            def a = new SeqSample(sampleName: "10A1-2",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)     //these two are reps
            def b = new SeqSample(sampleName: "10A1-1",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)   //
            def c = new SeqSample(sampleName: "10A1-1-1",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)   //
            def sr = new SeqRelation(samples:c,relation:'Replicate',base:'none')
            sr.setRelation("Replicate")
            sr.addToSamples(a)
            sr.addToSamples(b)

        then:
            a.delete()
            b.delete()
            c.delete()
            testSeqrun.delete()
            sr.delete()
            testPanel.delete()
            print RelationService.getPrimary(sr)

    }


    void "test assign replicates"() {
         given:
            //cleanup first: kill all seqrelations
            RelationService.dropAllRelations()
            Seqrun testSeqrun = new Seqrun(seqrun:"REP_TEST",runDate:new Date(),platform:"platform",sepe:"sepe",readlen:"readlen",library:"library",experiment:"experiment",scanner:"scanner").save(flush:true,failOnError:true)
            Panel testPanel = new Panel(manifest:"manifestAssignRep",panelGroup:"panelGroup").save(flush:true,failOnError:true)
            def testSample = new ArrayList<SeqSample>()
            testSample[0] = new SeqSample(sampleName: "10A",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)
            testSample[1] = new SeqSample(sampleName: "10A1",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)     //these two are reps
            testSample[2] = new SeqSample(sampleName: "10A1-1",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)   //
            testSample[3] = new SeqSample(sampleName: "10B-1",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)      //these two are reps
            testSample[4] = new SeqSample(sampleName: "10B-1-1",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)   //

            testSample[5] = new SeqSample(sampleName: "10C-1",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)      //these two are reps
            testSample[6]= new SeqSample(sampleName: "10C-2",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)      //

            testSample[7] = new SeqSample(sampleName: "12D-A",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)      //none of these are reps
            testSample[8] = new SeqSample(sampleName: "11D-A-A",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)    //
            testSample[9] = new SeqSample(sampleName: "12D-1",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)    //
            testSample[10] = new SeqSample(sampleName: "11D-1-A-1",seqrun:testSeqrun,panel:testPanel,analysis:"analysis",userName:"userName",userEmail:"userEmail",laneNo:"laneNo",authorisedQcFlag:true,passfailFlag:true).save(flush:true,failOnError:true)   //

            for(def i = 0; i < 11; i++) {
                testSeqrun.addToSeqSamples(testSample[i])
            }
            //do 7 and 8 end up together? check, they shouldnt


        when:

            def rels = RelationService.assignReplicateRelation([testSeqrun],true)

        then:
            def allSeqs = SeqRelation.findAll()
            //grab one
            println "SALL SEQ RELS"
            println allSeqs
            def repset1 = false
            def repset2 = false
            def repset3 = false

            def check1 = [testSample[1],testSample[2]]
            def check2 = [testSample[3],testSample[4]]
            def check3 = [testSample[5],testSample[6]]
            for(sr in allSeqs) {
                if (sr.samples.sort() == check1.sort()) {
                    repset1 = true
                }
                else if (sr.samples.sort() == check2.sort()) {
                    repset2 = true
                }
                else if (sr.samples.sort() == check3.sort()) {
                    repset3 = true
                }

            }

            testSeqrun.delete()
            for(def i = 0; i <3; i++) {
                testSample[i].delete()
            }
            testPanel.delete()
            assert repset1 == true && repset2 == true && repset3 == true && allSeqs.size() == 3
    }
}
