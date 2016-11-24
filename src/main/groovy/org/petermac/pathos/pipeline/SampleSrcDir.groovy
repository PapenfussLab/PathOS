/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



//
//		SampleSrcDir.groovy	Sample pipeline utilities
//
//		01		kdoig	25-Mar-13     kdd
//
//		Usage: new SampleSrcDir( sampath )
//
//      This class relies on a brittle absolute path to extract sample and seqrun out of.
//
//vim:ts=4

package org.petermac.pathos.pipeline

import groovy.util.logging.Log4j

@Log4j
class SampleSrcDir
{
	String		sampleSrcDir
	String		sample
	String		seqrun
	Panel		panel
	Boolean		valid

    //
    //	Constructor - sets path and directory
    //
    SampleSrcDir( path )
    {
        this.sampleSrcDir = path
        def f = new File( this.sampleSrcDir )
        if ( ! f.exists())
            log.error( "Sample path doesn't exist: ${path}" )

        this.valid  = false
        getSample()
        getSeqRun()
    }

    //
    //	Get sample name from path
    //
    String getSample()
    {
        this.sample = "NoSample"
        def m = ( this.sampleSrcDir =~ /Sample_([\w\-]+)/ )

        if ( m.count == 1)
            this.sample = m[0][1]
        else
            log.warn( "No sample in path: ${this.sampleSrcDir}" )

        return this.sample
    }

    //
    //	Get seqrun name from path
    //
    String getSeqRun()
    {
        this.seqrun = "NoSeqRun"
        def m = ( this.sampleSrcDir =~ /\/(1\d{5}_[^\/]+)\// )

        if ( m.count == 1)
            this.seqrun = m[0][1]
        else
            log.warn( "No seqrun in path: ${this.sampleSrcDir}" )

        return this.seqrun
    }

    //
    //	Get sequenced reads
    //
    List getReads()
    {
        def f = new File( this.sampleSrcDir )
        def fastq = f.listFiles( {d, file-> file ==~ /.*\.fastq\.gz/ } as FilenameFilter ).toList()*.absolutePath

        return fastq.sort()   // Note must sort so read1 is before read2, otherwise aligner gets different results
    }

    //
    //	Check the sample source is valid
    //
    boolean valid()
    {
        this.valid = false
        def ssd = new File( sampleSrcDir )

        //	Check directory exists
        if ( ! ssd.isDirectory())
        {
            log.error( "Directory doesn't exist: ${sampleSrcDir}" )
            return false;
        }

        //
        //	get reference file from SampleConfig.csv
        //
        def refFilePath = getRefFile( ssd )
        if ( ! refFilePath )
        {
            log.error( "Missing reference file in ${sampleSrcDir}" )
            return false
        }

        panel = new Panel( refFilePath )

        //
        //	Check all reference files are valid
        //
        if ( ! panel.valid()) return false

        //
        //	Count the number of *.fastq.gz files in directory
        //
        def fastq = ssd.listFiles( {d, file -> file ==~ /.*\.fastq\.gz/ } as FilenameFilter ).toList()*.name
        log.debug( "Fastq files found ${fastq} ${fastq.size()}" )

        if ( fastq.size() != 2 )
        {
            log.error( "Directory doesn't have only 2 fastq files (${fastq.size()}) in ${sampleSrcDir}" )
            return false
        }

        this.valid = true
        return true
    }

    //
    //	Extract path to reference file from SampleSheet.csv
    //
    private String getRefFile( File ssd )
    {
        //
        //	Check for sample sheet in directory
        //
        def sscsv = ssd.listFiles( {d, file-> file ==~ /SampleSheet\.csv/ } as FilenameFilter ).toList()
        if ( sscsv.size != 1 ) return null

        //	Read in header and data for sample sheet
        //
        def list = []
        sscsv[0].splitEachLine(','){ list << it }
        def header = list[0]
        def data   = list[1]

        //	Extract SampleRef field from data

        def idx = header.indexOf('SampleRef')
        if ( idx < 0 )
            return null		// header not found

        return ( data[idx] )
    }
}

