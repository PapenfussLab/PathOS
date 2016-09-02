/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */


//
//	PipelineDispatcher.groovy
//
//	Process a Seqrun for pipeline processing
//
//  Take a legacy LIMS_<seqrun>.xml file and generate a sample list and input to a bpipe pipeline
//
//	01	kdoig	20-Apr-2016     Initial build
//

package org.petermac.pathos.pipeline

import groovy.io.FileVisitResult
import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.petermac.util.Locator
import org.petermac.util.RunCommand
import org.petermac.util.Tsv

@Log4j
class PipelineDispatcher
{
    static def loc = Locator.instance       // Locator class for file locations

    static boolean exec = false             // execute pipeline command

    static void main( args )
	{
		def cli = new CliBuilder(   usage: 'PipelineDispatcher [options]',
                                    header: '\nAvailable options (use -h for help):\n',
                                    footer: '\nProcess a seqrun and run pipeline(s)\n')
		
		//	CLI options
        //
        cli.with
        {
            h(      longOpt: 'help',     'this help message' )
            sr(     longOpt: 'seqrun',   args: 1, required: true, 'Seqrun to process' )
            sam(    longOpt: 'sample',   args: 1, 'single sample to process [ALL]' )
            l(      longOpt: 'limsxml',  args: 1, 'lims XML file to process [LIMS_<sr>.xml] ' )
            rdb(    longOpt: 'rdb',      args: 1, 'database name for loading,annotation [pa_stage]' )
            b(      longOpt: 'base',     args: 1, 'base directory for pipeline output [/pathology/NGS/Samples/Testing]' )
            d(      longOpt: 'debug',    'Turn on debugging' )
            e(      longOpt: 'execute',  'execute pipeline [echo only]' )
            o(      longOpt: 'output',   args: 1, 'output file [Seqrun.tsv]' )
            tn(     longOpt: 'tumnorm',  args: 1, 'tumour normal TSV file' )
        }

		def opt = cli.parse( args )
		if ( ! opt ) return 
		if ( opt.h )
        {
            cli.usage()
            return
        }

        //  Debug ?
        //
        if ( opt.debug ) Logger.getRootLogger().setLevel(Level.DEBUG)
        log.debug( "Debugging turned on!" )

        //  Execute pipeline ?
        //
        if ( opt.execute )
        {
            log.debug( "Execute mode on")
            exec = true
        }

        //  Check for tumour normal file
        //
        List<Map> tnmap = []
        if ( opt.tn )
        {
            File tnf = new File( opt.tn as String )
            if ( ! tnf.exists())
            {
                log.fatal( "Missing tumour normal file [${opt.tn}]")
                return
            }

            Tsv tn = new Tsv( tnf )
            tn.load( true )
            tnmap = tn.rowMaps
        }

		log.info( "Starting: PipelineDispatcher " + args )

        boolean ok = new PipelineDispatcher().processSeqrun( opt.seqrun, opt.sample ?: null, opt.limsxml ?: null, opt.rdb ?: 'pa_stage', opt.base ?: '/pathology/NGS/Samples/Testing', opt.output ?: 'Seqrun.tsv', tnmap )

        log.info( "Finished: PipelineDispatcher ${ok ? 'OK' : 'with errors'}" )
	}

    /**
     * Generate a pipeline file from the supplied paramaters
     * @param seqrun
     * @param sample
     * @param limsxml
     * @param pipeline
     * @return
     */
    static boolean processSeqrun( String seqrun, String sample, String limsxml, String rdb, String baseDir, String outfile, List<Map> tnmap)
    {
        File lf = findLimsXml( seqrun, limsxml )
        if ( ! lf ) return false

        File of = new File( outfile )
        of.delete()

        List headers = []
        headers.addAll( 'seqrun sample panel pipeline pipein outdir normfastq analysis'.split(' '))
        of << "##  Created by PipelineDispatcher.groovy\n##\n#"
        of << headers.join("\t") + "\n"

        Map srm = new SeqrunLims().parseLims( lf.absolutePath )

        //  Process all sequenced samples
        //
        List pipelines = []
        List<Map> sams = srm.samples
        for ( sam in sams )
        {
            String normfastq = ''

            if ( sample && sam.sample != sample ) continue
            String pipeline = 'none'

            //  Amplicon sample
            //
            if ( sam.analysis.startsWith( 'Pathology Amplicon' ))
                pipeline = 'mp_dualAmplicon'

            //  MRD sample
            //
            if ( sam.reference.startsWith( 'MRD' ))
                pipeline = 'mp_mrdAmplicon'

            //  Tumour Normal sample
            //
            if ( sam.sample in tnmap.tumoursam )
            {
                //  Extract MAP with TN details
                //
                Map nmap = tnmap.find { it.tumoursam == sam.sample }

                //  Do we have a normal to process ?
                //
                if ( nmap.normalseqrun )
                {
                    //  Is normal in same seqrun as tumour ?
                    //
                    File srrootdir = lf.parentFile
                    if ( seqrun != nmap.normalseqrun )
                    {
                        File nlf = findLimsXml( nmap.normalseqrun )
                        if ( ! nlf )
                        {
                            log.error( "${seqrun} ${sam.sample} Couldn't find pipeline directory for ${nmap.normalseqrun}:${nmap.normalsam}")
                            continue
                        }
                        srrootdir = nlf.parentFile
                    }

                    //  Construct a path to the normal fastq files
                    //
                    normfastq = samplePath( srrootdir, nmap.normalsam )
                }

                //  Set pipeline for TN pairs
                //
                pipeline = 'TumourNormal'
            }

            if ( pipeline == 'none' )
            {
                log.warn( "${seqrun} ${sam.sample} Unknown analysis type: ${sam.analysis}")
                continue
            }

            //  Construct fields in order
            //
            List flds = []

            String fastqDir = samplePath(lf.parentFile, sam.sample)

            flds << seqrun                                  //  Seqrun
            flds << sam.sample                              //  Sample
            flds << sam.reference                           //  Panel
            flds << pipeline                                //  Pipeline name eg mp_dualAmplicon
            flds << fastqDir                                //  FASTQ file
            flds << "${baseDir}/${seqrun}/${sam.sample}"    //  Output data directory
            flds << normfastq                               //  Input FASTQ for TN pairs
            flds << sam.analysis                            //  Analysis type

            log.debug( "${seqrun} ${sam.sample} Analysis=\"${sam.analysis}\" Panel=${sam.reference} Pipeline=${pipeline}")

            pipelines << pipeline

            //  Output TSV row to dump file
            //
            of << flds.join("\t") + "\n"
        }

        //  Run all pipelines
        //
        pipelines = pipelines.unique()
        for ( pipeline in pipelines )
        {
            String cmd = "RunPipe -b -d ${baseDir} -s ${seqrun} -r ${rdb} ${sample ? '-a '+sample : ''} -p ${pipeline} ${of.name}"
            if ( pipeline == 'TumourNormal' )
            {
                cmd = "RunTumourNormalPipeline -d ${baseDir} -r ${rdb} ${of.name}";
            }

            if ( ! exec )
                cmd = 'echo ' + cmd

            //  Run command
            //
            log.debug( "% $cmd" )
            String stdout = new RunCommand( cmd ).run()
            if ( stdout ) return false
        }

        return true
    }

    /**
     * Find the LIMS XML file for a seqrun
     *
     * @param seqrun    Seqrun to search for
     * @param limsxml   Optional LIMS file to use if supplied
     * @return          List of directories to process
     */
    static File findLimsXml( String seqrun, String limsxml = null )
    {
        File limsfile

        if ( limsxml )
        {
            limsfile = new File( limsxml )
            if ( limsfile.exists()) return limsfile

            log.fatal( "LIMS XML file doesn't exist: ${limsxml}")
            return null
        }

        //  Loop through sequencing platforms
        //
        for ( pf in ['NextSeq','MiSeq','HiSeq'] )
        {
            //  loop through repositories
            //
            for ( repos in ['Runs','Archives'])
            {
                limsfile = new File( "/pipeline/${repos}/${pf}/${seqrun}/LIMS_${seqrun}.xml" )
                log.debug( "Checking for LIMS.xml in ${limsfile.absolutePath} ${limsfile.exists()}")
                if ( limsfile.exists()) return limsfile
            }
        }

        log.fatal( "LIMS XML file wasn't found for ${seqrun}")
        return null
    }

    /**
     * Find the full path to a sample's FASTQ files
     *
     * @param root     File of base directory for seqrun
     * @param sample   Sample dir to find
     * @return         Path to sample FASTQ files
     */
    static String samplePath( File root, String sample )
    {
        File base = new File( root, "ProjectFolders")
        def ret = ""
        if ( base.exists())
        {
            base.traverse
            {
                file ->
                if ( file.directory && (file.name == 'Sample_'+sample ))
                {
                    ret = file.absolutePath
                    FileVisitResult.TERMINATE
                }
                else
                    FileVisitResult.CONTINUE
            }
        }

        if ( ! ret ) log.error( "Sample ${sample} not found under ${root}")

        return ret
    }
}

