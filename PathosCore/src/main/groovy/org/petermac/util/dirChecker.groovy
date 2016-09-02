/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 */

package org.petermac.util

/**
 * Housekeeping: check data store
 * groovy rewrite of /pathology/NGS/Samples/Admin/CheckDir.sh
 * check a directory tree and its samples for missing BAMs, IGVs etc
 *
 * Created by seleznev andrei on 19/12/2014.
 */
class DirChecker {

    /**
     * validate the contents of a single dir
     * @param dir - path to dir
     * @param samplename - name of sample
     * @param mut - do we also check for Mut files
     * @return
     */
    static List<String> validateDirContents( dir, samplename, mut )
    {
        List<String> errorList = new ArrayList()

        //check sample.bam exists
        //
        def bamFile = new File("${dir}/${samplename}.bam")
        if ( ! bamFile.exists()) errorList.add("BAM")

        //check IGV_Session.xml exists
        //
        def igvFile = new File("${dir}/IGV_Session.xml")
        if ( ! igvFile.exists()) errorList.add("IGV")


        //check ${sample}.vcf.tsv
        //
        def vcfFile = new File("${dir}/${samplename}.vcf.tsv")
        if (!vcfFile.exists()) errorList.add("VCF")
        else
        {
            def readVcf = vcfFile.text.tokenize('\n')
            int numvar = 0
            for (line in readVcf)
                if (line[0] != '#')
                    numvar = numvar + 1

            if (numvar > 0)
            {
                if (mut)
                {
                    def vcfmutFile = new File("${dir}/${samplename}.vcf.tsv.mut")
                    if ( ! vcfmutFile.exists()) errorList.add("VCFMUT")

                    def vepmutFile = new File("${dir}/${samplename}.vep.tsv.mut")
                    if ( ! vepmutFile.exists()) errorList.add("VEPMUT")
                }
            }
        }

        return errorList
    }

    /**
     * validate contests of all dirs.
     * currently unused - this function validates SamplesInDir for each dir in basePath
     * @param basePath
     * @return
     */
    HashMap validateAll(String basePath) {
        //go UP A LEVEL
        //println ("Validating from base directory: ${basePath}")
        HashMap allErrors = new HashMap()
        def base = new File(basePath)


        if (!base.isDirectory()) {
            println "ERROR: Not a directory: ${base.getPath()}"
            return allErrors
        }

        def alldirs = base.listFiles()

        for (thisdir in alldirs) {

            if (thisdir.isDirectory()) {
                def thisErrorList = validateSamplesInDir(thisdir.getPath())

                if (!thisErrorList.isEmpty()) {
                    allErrors[thisdir] = thisErrorList

                }
            }
        }

        return allErrors

    }
    /**
     * validate contests of a sample directory - wrapper for validateOne
     * @param basePath
     * @return
     */
    HashMap validateSampleDir(String dirpath ) {
        //go UP A LEVEL
        //println ("Validating from base directory: ${basePath}")
        HashMap allErrors = new HashMap()
        def thisdir = new File(dirpath)
        if (thisdir.isDirectory()) {
                def thisErrorList = validateOne(thisdir.getPath())

                if (!thisErrorList.isEmpty()) {
                    allErrors[thisdir] = thisErrorList
                    //println thisErrorList
                }
         } else {   //we shouldn't get here from HouseKeeping - this wont be called unless dirpath is a dir
            System.err.println "ERROR: Not a directory. ${dirpath}"
        }


        return allErrors

    }


    /**
     * validate all samples in a directory
     * @param basePath
     * @return
     */
    HashMap validateSamplesInDir(basePath) {
        def base = new File(basePath)
        def alldirs = base.listFiles()


        HashMap allErrorList = new HashMap()
        List<String> errorList = new ArrayList()

        for (thisdir in alldirs) {
            if(thisdir.isDirectory()) {
                def arrpath = thisdir.getPath().split('/')
                def samplename = arrpath[arrpath.size() - 1]



                errorList = validateOne(thisdir.getPath())
                if (!errorList.isEmpty()) {
                    allErrorList[samplename] = errorList
                }
            }
        }

        return allErrorList

    }

    /**
     * validate a single sample dir
     * @param dirpath
     * @return
     */
    List<String> validateOne(String dirpath) {
        def dirFile = new File(dirpath)

        if(!dirFile.isDirectory()) {
            System.err.println "ERROR: Validate called on non-directory ${dirpath}"
            return List()
        }


        //parse samplename out
        def arrpath = dirpath.split('/')
        def samplename = arrpath[arrpath.size() - 1]
        def mut = true //we decide to always validate for VCFMUT and VEPMUT

        List<String> errorList = validateDirContents(dirpath,samplename,mut)
        return errorList
    }

}
