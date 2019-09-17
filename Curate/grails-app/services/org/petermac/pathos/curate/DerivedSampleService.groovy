package org.petermac.pathos.curate

import com.google.common.collect.Sets
import groovy.util.logging.Log4j
import org.petermac.util.Locator

import java.text.MessageFormat

@Log4j
class DerivedSampleService {

    /**
     * returns a normalised name for a synthetic sample
     * A minus B == "A--B"
     * A union B union C == "A-u-B-u-C"
     * A intersect B intersect C == "A-n-B-n-C"q
     *
     * this is public - we might need to work out derived sampe name from elsewhere
     *
     * @param relation
     * @param samples
     * @return
     */
    static String derivedSampleName( String relation, List<SeqSample> samples ) {
        if (samples.size() != 2) {
            log.warn("Error: unable to generate derived sample name, need exactly two samples. Given:")
            log.warn(samples)
            return null
        }

        String sName = ""


        switch (relation.toLowerCase()) {
            case 'minus':
                return samples[0].sampleName + "---" + samples[1].sampleName
                break
            case 'union':
                return samples[0].sampleName + "-u-" + samples[1].sampleName
                break
            case 'intersect':
                return samples[0].sampleName + "-n-" + samples[1].sampleName
                break
            default:
                log.warn("Error: unable to generate derived sample name, invalid relation.")
                return null
                break
        }



    }

    /**
     * create a new seqsample from a set operation on two existing seqsamples
     * @param relation
     * @param samples
     * @return
     */
    SeqSample createDerivedSample( String relation, List<SeqSample> samples ) {


        if (samples.size() != 2) {
            log.warn("Error: unable to create synthetic sample, need exactly two samples.")
            return null
        }
        //  need a valid relation
        //
        if (! ['minus','union','intersect'].contains(relation) )    {
            log.warn("Error: unable to create derived, invalid relation ${relation}")
            return null
        }

        //  two samples must be from same seqrun
        if (samples[0].seqrun != samples[1].seqrun) {
            log.warn("Error: unable to create derived sample, two given samples must be from same run")
            return null
        }

        //  generate name for sample
        String sName = derivedSampleName(relation,samples)
        Seqrun sr = samples[0].seqrun
        Panel pl = samples[0].panel

        //  check if a sample with this name already exists in the seqrun
        if(SeqSample.findBySampleNameAndSeqrun(sName,samples[0].seqrun)) {
            log.warn("Error: unable to create derived sample, sample with this name already exists")
            return null
        }

        //  sensible defaults for username and email (grails will cast empties to null and we'll fail validation check)
        //
        def username = samples[0].userName
        if (!username || !username.trim()) {
            username = "unknown"
        }
        def useremail = samples[0].userEmail
        if (!useremail || !useremail.trim()) {
            useremail = "unknown"
        }

        def ss = new SeqSample(sampleName:sName,sampleType:'Derived',seqrun:sr,panel:pl,analysis:samples[0].analysis,userName:username,userEmail:useremail,laneNo:samples[0].laneNo)

        if (! ss.validate()) {
            log.error("Failed to validate newly created derived sample")
            ss?.errors?.allErrors?.each {
                log.error(new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                println (new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
            }
        }
        //  BUG WORKAROUND
        //  the below merge(flush:true) is a workaround for a Searchable bug
        //  see: https://web.archive.org/web/20091224110529/http://jira.codehaus.org/browse/GRAILSPLUGINS-601
        //
        //ss.merge(flush: true).save()
        //println "made " + ss.sampleName + " with id " + ss.id + "in seqrun " + sr.seqrun
        //  manually index since the above would not
        //
        //SeqSample.index(ss)

        //  combine variants with operation
        //
        Integer combined = combineVariants(relation,samples,ss)
        if (combined)  log.warn("Created new derived sample ${ss.sampleName} in ${ss.seqrun} with ${combined} (${ss.seqVariants.size()}) new seqvariants")
        else           log.warn("Error: Created new derived sample ${ss.sampleName} but could not create new seqvariants")


        return ss
    }



    /**
     * creates combined vars and adds them to a given seqsample
     * combine vairants for a derived sample: relation is Minus, Union or Intersect
     * @param relation
     * @param samples  a list of samples (ORDERED if minus relation - list[0]-list[1]
     * @param syntheticSamples
     * @return
     */
    private Integer combineVariants( String relation, List<SeqSample> samples, SeqSample ss ) {
        if (samples.size() != 2) {
            log.warn("Error: unable to combine variants, need exactly two samples.")
            return null
        }


        def combinedVars

        //  perform the appropriate combination of variants. the below methods will create new seqvariants
        //  from the samples, based on the appropriate relation
        //
        switch (relation) {
            case 'minus':
                combinedVars = minusSeqVars(samples[0].seqVariants, samples[1].seqVariants)
                break
            case 'union':
                combinedVars = unionSeqVars(samples[0].seqVariants, samples[1].seqVariants)
                break
            case 'intersect':
                combinedVars = intersectSeqVars(samples[0].seqVariants, samples[1].seqVariants)
                break
            default:
                log.warn("Error: unable to combine variants")
                return null
                break
        }

        for (var in combinedVars)  {
            ss.addToSeqVariants(var)
        }

        return combinedVars.size()

    }

    /**
     * given two lists of SeqVariants: return all seqVariants in the first list that have a matching
     * hgvsg in the second list
     * @param l1
     * @param l2
     */
    private static ArrayList<SeqVariant> intersectSeqVars(l1, l2 ) {
        ArrayList<SeqVariant> intersecting = new ArrayList<SeqVariant>()

        //  build hashSet of l2 hgvsgs
        HashSet l2_hgvsg = new HashSet<String>()
        for (sv in l2) {
            l2_hgvsg.add(sv.hgvsg)
        }

        //  for each sv1 in l1, add it to our return list if we see its hgvsg in l2
        for (sv in l1) {
            if (l2_hgvsg.contains(sv.hgvsg)) {
                intersecting.add(cloneSeqVariant(sv))
            }

        }

        return intersecting
    }


    /**
     * given two lists of SeqVariants: return all seqVariants in the first list and all variants in the
     * second list that do not have an hgvsg in the first list
     * @param l1
     * @param l2
     */
    private static ArrayList<SeqVariant> unionSeqVars(l1, l2 ) {
        ArrayList<SeqVariant> union = new ArrayList<SeqVariant>()

        //  build hashSet of l1 hgvsgs
        HashSet l1_hgvsg = new HashSet<String>()
        for (sv in l1) {
            l1_hgvsg.add(sv.hgvsg)
        }

        //  first, make new SeqVariants from all elements from the first list
        for (sv in l1) {
             union.add(cloneSeqVariant(sv))
        }

        //  now, make new SeqVariants from all elements in second list, unless they appear in first
        for (sv in l2) {
            if (! l1_hgvsg.contains(sv.hgvsg)) {
                //def svproperties = sv.properties
                //svproperties.tags = null     //dont copy tags. if we ever want to copy them too, need to clone them.
                union.add(cloneSeqVariant(sv))
            }
        }

        return union
    }


    /**
     * given two lists of SeqVariants: return all seqVariants in the first list that do NOT have a matching seqvariant
     * (by hgvsg) in the second list
     * @param l1
     * @param l2
     */
    private static ArrayList<SeqVariant> minusSeqVars(l1, l2 ) {
        ArrayList<SeqVariant> minus = new ArrayList<SeqVariant>()

        //  build hashSet of l2 hgvsgs
        HashSet l2_hgvsg = new HashSet<String>()
        for (sv in l2) {
            l2_hgvsg.add(sv.hgvsg)
        }

        //  now,  make new SeqVariants from all elements in first list unless they appear in second
        for (sv in l1) {
            if (! l2_hgvsg.contains(sv.hgvsg)) {
                minus.add(cloneSeqVariant(sv))
            }
        }

        return minus
    }

    /**
     * given a seqvar, clone it. we strip tags because we dont want to have shared references to a collection later on.
     * we also strip sin flags since we don't have singletons in a non-replicate
     * @param sv
     * @return
     */
    private static SeqVariant cloneSeqVariant(SeqVariant sv) {
        def svproperties = sv.properties
        svproperties.tags = null                                                    //dont copy tags. if we ever want to copy them too, need to clone them.
        svproperties.filterFlag = svproperties.filterFlag.replaceAll('sin,','').replaceAll(',sin','')         //strip out sin flags if they are part of the string
        if (svproperties.filterFlag == 'sin')   svproperties.filterFlag = 'nof'    //set as nofilter if sin is only flag

        return new SeqVariant(svproperties)
    }


}