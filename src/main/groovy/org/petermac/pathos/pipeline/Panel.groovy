/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



//
//		Panel.groovy	Sample pipeline utilities
//
//		01		kdoig	25-Mar-13
//
//		Usage: new Panel( panelpath )
//
//vim:ts=4

package org.petermac.pathos.pipeline

import groovy.util.logging.Log4j
import org.petermac.util.Locator

@Log4j
class Panel
{
	String  panelPath           //  /pipeline/RunFolder/MiSeq/Indexes               master directory of amplicon manifests
    String 	panelName           //  Germline_v4-2_0603132_manifest                  prefix of files below 227 Amplicons
	String  panelRef            //  Germline_v4-2_0603132_manifest.fasta            Used by aligner (1 row/amplicon) inc. primers
	String  panelPrimers        //  Germline_v4-2_0603132_manifest.fasta.primers    Used by aligner (1 row/amplicon) chr:start-end  pr1len  pr2len  amplicon_name
	String	regionBed           //  Germline_v4-2_0603132_manifest_Filter.bed       Used for BAM file filtering (positions don't inc. primers, coalesced regions)
    String	panelBed            //  Germline_v4-2_0603132_manifest.bed              Used by IGV to show individual amplicons (exc. primers)

    static def loc = Locator.instance      // Locator class for file locations

    /**
     * Constructor sets up file paths
     *
     * @param path  Can be either manifest name or absolute path to manifest .fasta file
     */
    Panel( String path )
	{
        this.panelPath	= loc.manifestDir		    //	fixed location of manifests

        //  Absolute path or manfiest name ?
        //
        if ( path.startsWith('/'))
        {
            //  Parse file name
            //
            def match = ( path =~ /(.*)\/(.+).fasta$/ )
            if ( match.count == 1 )
            {
                this.panelRef   = path          //  path to reference file
                this.panelName  = match[0][2]	//	name of reference file
            }
            else
            {
                log.error( "Invalid reference file format: ${path}" )
                return
            }
        }
        else
        {
            //  Set path to reference file
            //
            this.panelRef  = loc.manifestDir + path + ".fasta"
            this.panelName = path
        }

		//	Check directory of references exists
		//
		if ( ! new File( this.panelPath ).exists())
		{
			log.error( "Panel path doesn't exist: ${this.panelPath}" )
			return
		}

		//
		//	set other reference files
		//
		this.panelPrimers = this.panelRef + ".primers"
		this.regionBed    = this.panelRef.replaceFirst( /\.fasta$/, "_Filter.bed" )
        this.panelBed     = this.panelRef.replaceFirst( /\.fasta$/, ".bed" )
	}
	
	//
	//	Get sample name from path
	//
	Boolean valid()
	{
		if ( ! this.panelRef ) return false
		
		//	Check reference files exist
		//
		if ( ! new File( this.panelRef ).canRead())
			log.warn( "Cant read ${this.panelRef}" )
		else if ( ! new File( this.panelPrimers ).canRead())
			log.warn( "Cant read ${this.panelPrimers}" )
		else if ( ! new File( this.panelBed ).canRead())
			log.warn( "Cant read ${this.panelBed}" )
        else if ( ! new File( this.regionBed ).canRead())
            log.warn( "Cant read ${this.regionBed}" )
		else
			return true
			
		return false
	}

    String toString()
    {
        return( "${panelPath} ${panelName} ${panelPrimers}")
    }
}
