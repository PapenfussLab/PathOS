package org.petermac.pathos.curate

import org.petermac.pathos.loader.VcfLoader
import java.lang.Runnable
import java.lang.Thread
import org.petermac.util.Vcf
import org.petermac.util.RunCommand
import org.petermac.util.Locator

import java.text.MessageFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SeqSampleService {

    def loc = Locator.instance
    def AuditService


    /**
     * Authorise Review Function
     */
    HashMap authoriseReview( SeqSample ss, AuthUser user, String review ) {
        HashMap results = [
            errors: [],
            warnings: []
        ]

        // Block review if QC has not been set
        if ( !ss.authorisedQcFlag ) {
            results.errors.push("Cannot complete review: Sample must pass QC first")
        }

        // Current Context
        ClinContext cc = ss.clinContext

        // Block review if a reported seqvar variant doesn't have a curvariant with the current clinContext
        SeqVariant.findAllBySeqSampleAndReportable(ss, true).each { sv ->
            if(!sv.curatedInContext(cc)) {
                results.errors.push("Cannot complete review: ${sv} is Reported but has no CurVariant for the current context ${cc}")
            } else if (review == "Final" && !sv.currentCurVariant().authorisedFlag) {
                results.errors.push("Cannot complete review: The CurVariant record ${sv.currentCurVariant()} for seqVariant ${sv} is not Authorised")
            }
        }

        // List of Unreported C5 CurVariants
        List<CurVariant> unreportedCurVariants = CurVariant.executeQuery("from CurVariant cv, SeqVariant sv where cv.pmClass = 'C5: Pathogenic' and cv.clinContext = :cc and sv.seqSample = :ss and sv.reportable = false and cv.hgvsg = sv.hgvsg", [ss: ss, cc: cc])

        unreportedCurVariants.each { cv ->
            results.warnings.push("Warning: CurVariant ${cv} is C5 Pathogenic but not reportable.")
        }

        if(results.errors.size() > 0) {
            return results
        }


        // Create a warning if Patient has another PatSample with SeqSamples.
        ss?.patSample?.seqSamples.each { otherSeqSample ->
            if( ss != otherSeqSample ) {
                results.warnings.push("Warning, this PatSample has another SeqSample: ${otherSeqSample}")
            }
        }

        if ( review == "First" ) {
            if ( ss.finalReviewBy ) {
                results.errors.push("Failed to set First Review because Final Review has already been set")
                return results
            }
            if ( ss.secondReviewBy ) {
                results.errors.push("Failed to set First Review because Second Review has already been set")
                return results
            }

            if ( !ss.firstReviewBy ) {
                ss.setFirstReviewBy(user)
                ss.setFirstReviewedDate(new Date())
            } else {
                results.errors.push("Failed to set First Review because First Review has already been set")
                return results
            }
        } else if ( review == "Second" ) {
            if ( !ss.firstReviewBy ) {
                results.errors.push("Failed to set Second Review because First Review has not been set")
                return results
            }

            if ( ss.firstReviewBy == user ) {
                results.errors.push("Failed to set Second Review because First Reviewer is the same as the Second Reviewer")
                return results
            }

            if ( ss.finalReviewBy ) {
                results.errors.push("Failed to set Second Review because Final Review has already been set")
                return results
            }

            if ( !ss.secondReviewBy ) {
                ss.setSecondReviewBy(user)
                ss.setSecondReviewedDate(new Date())
            } else {
                results.errors.push("Failed to set Second Review because Second Review has already been set")
                return results
            }
        } else if ( review == "Final" ) {

            if ( !ss.firstReviewBy ) {
                results.errors.push("Failed to set Final Review because First Review has not been set")
                return results
            }

            if ( ss.firstReviewBy == user ) {
                results.errors.push("Failed to set Final Review because First Reviewer is the same as the Final Reviewer")
                return results
            }

            if ( ss.secondReviewBy && ss.secondReviewBy == user ) {
                results.errors.push("Failed to set Final Review because Final Reviewer is the same as the Second Reviewer")
                return results
            }

            if ( !ss.finalReviewBy ) {
                ss.setFinalReviewBy(user)
                ss.setFinalReviewedDate(new Date())
            } else {
                results.errors.push("Failed to set Final Review because Final Review has already been set")
                return results
            }
        } else {
            results.errors.push("Invalid review state")
            return results
        }

        results.warnings.push("Successfully set Curation Review: ${review} on ${ss.sampleName} to Final Review by: ${ss.finalReviewBy} First Review by: ${ss.firstReviewBy} Second Review by: ${ss.secondReviewBy}")
        return results
    }

    String revokeReview( SeqSample ss, String review) {
        AuthUser previousReviewer = null

        if ( review == "First" ) {
            if ( ss.finalReviewBy ) {
                return "Failed to revoke First Review because Final Review has been set"
            }
            if ( ss.secondReviewBy ) {
                return "Failed to revoke First Review because Second Review has been set"
            }

            previousReviewer = ss.getFirstReviewBy()
            ss.setFirstReviewBy( null )
            ss.setFirstReviewedDate( null )
        } else if ( review == "Second" ) {
            if ( ss.finalReviewBy ) {
                return "Failed to revoke Second Review because Final Review has been set"
            }

            previousReviewer = ss.getSecondReviewBy()
            ss.setSecondReviewBy( null )
            ss.setSecondReviewedDate( null )
        } else if ( review == "Final" ) {

            previousReviewer = ss.getFinalReviewBy()
            ss.setFinalReviewBy( null )
            ss.setFinalReviewedDate( null )
        } else {
            return "Invalid review state"
        }

        return "Successfully revoked ${review} Review by ${previousReviewer ?: 'unknown'} on ${ss.sampleName}"
    }


    /**
     * parse user input of gene list into gene list: limit on space and comma (and newline), tabs to blank space, remove null and empty elements
     * @param genelist
     * @return
     */
    ArrayList<String> parseGeneList(String geneMask) {
        ArrayList<String> geneList = geneMask?.replaceAll("\n",",")?.replaceAll("\t"," ")?.toUpperCase()?.tokenize(", ")?.findAll{it}
        println "gene list parsed from: ${geneMask} to ${geneList}"
        log.info("Gene list parsed from: ${geneMask} to ${geneList}")

        return geneList
    }

    /***
     * clean a user inputted gene mask. the result is sorted, allowing for comparisons.
     * @param geneMask
     * @return
     */
    String cleanGeneMask(String geneMask) {
        String cleanMask = parseGeneList(geneMask)?.join(",") ?: ""

        return cleanMask.tokenize().unique().sort().join(",")
    }
    /**
     * change the gene mask for a seq sample and all replicates in the same run
     * @param seqSampleInstance
     * @param geneMask
     *
     */
    void changeGeneMask(SeqSample seqSampleInstance, String geneMask) {
        def oldSampleGeneMask = seqSampleInstance.geneMask()

        seqSampleInstance.setSampleGeneMask(geneMask)
        seqSampleInstance.save(flush: true,failOnError:true)


        //  Set audit record
        //
        def audit_msg = "Changed gene mask for SeqSample ${seqSampleInstance.sampleName} ${seqSampleInstance.seqrun}, was: ${oldSampleGeneMask ?: "(default mask: ${seqSampleInstance?.defaultGeneMask()})"} and is now: ${geneMask ?: ' the default mask'}"
        AuditService.audit([category: 'masking', task: 'gene mask change qc', description: audit_msg])
        log.info(audit_msg)
        //  do replicates in this run
        //
        seqSampleInstance.relations.findAll { it.relation == "Replicate" }.each { relation ->
            relation.samples().each { repsample ->
                if (repsample.seqrun.seqrun == seqSampleInstance.seqrun.seqrun) {
                    oldSampleGeneMask = repsample.geneMask()
                    repsample.setSampleGeneMask(geneMask)
                    repsample.save(flush: true,failOnError:true)

                    //  Set audit record
                    //
                    audit_msg = "Changed gene mask for SeqSample ${repsample.sampleName} ${repsample.seqrun}, was: ${oldSampleGeneMask ?: "(default mask: ${seqSampleInstance?.defaultGeneMask()})"} and is now: ${geneMask ?: ' the default mask'}"
                    AuditService.audit([category: 'masking', task: 'gene mask change qc', description: audit_msg])
                }
            }
        }
    }





    /**
     * reload all variants for a set of samples with vcfloader, method depends on locator
     * will call directly VcfLoader
     * @param seqSamples
     * @param seqrun
     * @param runBackground run job in bg
     * @param notifyEmail email to notify user when job done, if job is ran in bg
     * @return
     */
    Integer reloadVariants(Set<SeqSample> seqSamples, Seqrun seqrun, boolean runBackground = false, String notifyEmail = "") {
        //todo pathos-4116 should PANEL be in here?

        //  assemble a list of VCFs for all samples
        //
        Set<String> vcfPaths = []    //  set of VCF URIs
        for (sample in seqSamples) {
            File vcf = retrieveSampleVcf(seqrun.seqrun,sample.sampleName)
            if(vcf.exists()) {
                vcfPaths.add(vcf.getAbsolutePath())
            }
        }
        if (vcfPaths.isEmpty()) {
            log.error("No VCF files around!")
            return
        }

        String env      = loc.pathosEnv
        String muthost  = loc.mutalyzer
        Integer loaded = 0
        switch( loc.sampleReloadMethod ) {
            case "Anomaly":     //anomaly
                log.info"Anomaly sample reload method"
                ArrayList<File> uploads = []
                VcfAnomaly anomaly = new VcfAnomaly()

                for (String filepath in vcfPaths) {
                    uploads.add(new File(filepath))
                }

                log.info("Processing " + uploads)
                loaded = anomaly.processFiles(seqrun, uploads, '')

                //todo implement this
                break;
            default:    //vcfloader
                loaded = performVcfLoaderLoad(env, muthost, seqrun.seqrun, vcfPaths, true, '', notifyEmail, runBackground)
                break;
        }
        return loaded
    }

    Integer performVcfLoaderLoad(String env, String muthost, String seqrunName, Set<String> vcfPaths, Boolean filter = false, String panel = '', String notifyEmail, Boolean background) {

        //  convert set of vcf file paths to a list for Files loadVcf call
        //
        List<File> vcfFiles = new ArrayList<File>();
        List<String> sampleNames = []
        for (vcfPath in vcfPaths) {
            File f = new File(vcfPath)
            vcfFiles.add(f)
            Vcf v = new Vcf(f)
            sampleNames.add(v.sampleName())
        }
        //  vcfFiles.addAll(vcfPaths);

        if (!vcfFiles) {
            log.fatal( "No data files to process for PerformVcfLoaderLoad")
            return
        }
        Boolean nocache = true          // nocache true by default
        List dss = ['mutalyzer','vep']  // Comma separated list of datasources to use for annotation [no annotation]
        File colsf = new File( Locator.instance.etcDir + "vcfcols.txt" )

        //  Open error file and zero it if required
        //
        def now = new Date()
        String timeStamp = now.format("yyMMdd'T'HHmmss")
        File errFile = File.createTempFile("vcferror",timeStamp)
        log.info("Error file for load is: " + errFile.getAbsolutePath())
        Boolean normalise = true

        log.info "Processing ${vcfFiles.size()} files from ${seqrunName} with VcfLoaderLoad"

        if(background) {
            ExecutorService executor = Executors.newSingleThreadExecutor()
            executor.submit(new Runnable() {
                public void run() {
                    def nrows = VcfLoader.loadVcf(vcfFiles, seqrunName, panel ?: 'NoPanel',
                            env as String, dss, colsf, errFile,
                            filter, nocache, normalise, muthost)

                    def shellcommand = "echo 'Loaded ${nrows} variants in total' | mail -s 'Variant Reload Completed for ${seqrunName} : ${sampleNames.join(", ")}' ${notifyEmail}"
                    Map responseMap = new RunCommand(shellcommand).runMap()
                    println responseMap
                }

            })
            executor.shutdown()

        } else {
            int loaded = VcfLoader.loadVcf(vcfFiles, seqrunName, panel ?: 'NoPanel',
                    env as String, dss, colsf, errFile,
                    filter, nocache, normalise, muthost)
            log.info "Done: processed ${vcfFiles.size()} files into ${seqrunName}"

            return loaded
        }

        return -1   //-1 if
    }



    //  retrieve the vcf file from the sample dir for a given sample
    //
    File retrieveSampleVcf(String seqrunName, String sampleName) {
        String sampledir = loc.samDir
        String vcfPath = ("${sampledir}/${seqrunName}/${sampleName}/${sampleName}.vcf")
        File vcf = new File(vcfPath)
        if(!vcf.exists())  {
            log.error("Could not find existing VCF file for ${seqrunName} ${sampleName}, looked in: ${vcfPath}'")
        }
        return vcf
    }

    /*
     * Assign gene mask to a sample (that is, set its sampleGeneMask property to its default mask if one exists)
     * does nothing if sampleGeneMask already set
     *
     * (takes over LoadSeqVariant's assignMask which is deprecated)
     */
    void assignGeneMask(SeqSample ss) {
        if(!ss.sampleGeneMask) {
            if (ss.patSample) {
                List genes = ss.geneMask()
                if (genes) {
                    ss.setSampleGeneMask(genes.join(","))
                }
            }
        }
    }

    /**
     * create a new seqsample, link it to any patsamples, set its gene mask
     * @return
     */
    SeqSample makeNewSeqSample(Seqrun seqrun, String sampleName, Panel panel, String ownerName, String ownerEmail, String analysis, String laneNo) {
        SeqSample ss = new SeqSample(sampleName: sampleName, seqrun: seqrun, passfailFlag: false, authorisedQcFlag: false, userName:ownerName, userEmail: ownerEmail, laneNo: laneNo, analysis: analysis, panel: panel, clinContext: ClinContext.generic())

        if(PatSample.findBySample(sampleName)) {
            ss.patSample = PatSample.findBySample(sampleName)
        }
        assignGeneMask(ss)

        return ss

    }


}














