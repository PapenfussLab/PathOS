#!/usr/local/cluster/all_arch/groovy/users/bin/groovy
/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

//
//		SampleTgtDir.groovy		Class for target directory of sample
//
//		01		kdoig		03-Apr-13
//
//
//vim:ts=4

package org.petermac.pathos.pipeline

import groovy.util.logging.Log4j
import org.petermac.util.Locator

@Log4j
class SampleTgtDir
{
	String	sampleTgtDir
	String	sample
	String	seqrun
	String	bamFile
	String	vcfFile
	String	vcfTsvFile
	String	vepFile
	String	vepTsvFile
	String	igvFile

    static def loc = Locator.instance      // Locator class for file locations

    /**
     * Constructor - needs components of path: seqrun and sample
     *
     * @param seqrun    Illumina seqrun string
     * @param sample    Sample name
     */
    SampleTgtDir( String seqrun, String sample )
	{
		this.seqrun = seqrun
		this.sample = sample
		this.sampleTgtDir = loc.samDir + seqrun + loc.fs + sample
		
		//
		//	Output files from analysis
		//
		def base	= this.sampleTgtDir + loc.fs + sample
		bamFile		= base + ".bam"
		vcfFile		= base + ".vcf"
		vcfTsvFile	= base + ".vcf.tsv"
		vepFile		= base + ".vep"
		vepTsvFile	= base + ".vep.tsv"
		igvFile		= this.sampleTgtDir + loc.fs + "IGV_Session.xml"
	}
	
    /**
     * Create the directory if it doesn't exist
     */
    File createDir()
	{
		def f = new File(sampleTgtDir)
		
		log.debug( "Creating directory for sample: ${sampleTgtDir}" )
		if ( ! f.exists())
			f.mkdirs()

        return f
	}

    /**
     * Check if sample directory is fully populated
     *
     * Allows for empty VEP files if there are no variants found
     *
     * @return  true if fully populated
     */
    Boolean complete()
    {
        def exists = checkFiles()

        //  All files are there ?
        //
        if ( ! exists.containsValue(false))
            return true

        //  Missing 'must-have' files
        //
        if ( ! exists['bam'] || ! exists['igv'] || ! exists['vcf'])
            return false

        //  check if there are no variants
        //
        if ( numVariants() == 0 )
            return true             // no need for vep or tsv files

        return false
    }

    /**
     * Find number of variants of the VCF file in the directory
     *
     * @return  number of variants (non-header lines)
     */
    int numVariants()
    {
        def vcf = new File( vcfFile )

        //  Collect all variant lines (non comment '#' lines)
        //
        def vars = []
        vcf.eachLine
        {
            if ( ! (it =~ /^#/))
                vars << it
        }

        return vars.size()
    }

    /**
     * Check that the right files have been created
     *
     * @return  Map of file existence by suffix
     */
    Map checkFiles()
	{
		Map files = [:]
		files['bam']	= new File( bamFile ).exists()
		files['vcf']	= new File( vcfFile ).exists()
		files['vep']	= new File( vepFile ).exists()
		files['igv']	= new File( igvFile ).exists()
		files['vcftsv']	= new File( vcfTsvFile ).exists()
		files['veptsv'] = new File( vepTsvFile ).exists()
		
		return files
	}
}
