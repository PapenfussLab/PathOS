/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



//
//	Submit.groovy
//
//	Submit a set of samples for processing
//
//  Normal mode will search for a set of LIMS*.xml run configuration files, extract sample names
//  and create the necessary directories in <Sample Root>/<seqrun>/<sample>/
//
//	01	kdoig	31-Mar-2013     Initial build
//  02  kdoig   02-Oct-2013     Refactored for integration with existing PMCC pipeline
//

package org.petermac.pathos.pipeline

import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.petermac.util.RunCommand
import org.petermac.util.Locator
import groovy.util.logging.Log4j

@Log4j
class Submit
{
    //  Pipeline phases in order of execution
    //
    static private List pipelinePhases = [ 'align', 'varcall', 'annotate', 'igv' ]

    static def loc = Locator.instance      // Locator class for file locations

    static List jobids  = []               // list of submitted jobs
    static List samples = []               // list of submitted samples

    static void main( args )
	{
		def cli = new CliBuilder(   usage: 'Submit [options] --seqrun "pattern"',
                                    header: '\nAvailable options (use -h for help):\n',
                                    footer: '\nSubmit samples for processing to cluster\n')
		
		//	Options to Submit
        cli.with
        {
            h(  longOpt: 'help',		'this help message' )
            r(  longOpt: 'seqrun',   args: 1, required: true, 'seqrun file to process (may be a prefix)' )
            p(  longOpt: 'phase',    args: 1, required: true, 'phase[s] of pipeline to perform (align,varcall,annotate,igv|all)'  )
            s(  longOpt: 'sample',   args: 1, 'single sample to process' )
            c(  longOpt: 'complete', 'skip sample if expected files exist' )
            e(  longOpt: 'external', args: 1, 'load external samples using this panel' )
            src(longOpt: 'srcdir',   args: 1, 'override source directory to use (can be the root of all sequencing runs eg /pipeline/Runs/MiSeq' )
            seq(longOpt: 'platform', args: 1, 'sequencing platform source (MiSeq|HiSeq|all) [MiSeq]' )
            d(  longOpt: 'database', args: 1, 'dump a TSV file for database upload' )
            debug(  longOpt: 'debug', 'Turn on debugging' )
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

        //  Set pipeline phase[s] to perform
        //
        def phases = []
        if ( opt.phase )
        {
            if ( opt.phase == 'all' )
                phases = pipelinePhases
            else
            {
                for ( pph in pipelinePhases )
                    if ( opt.phases =~ /${pph}/  )
                        phases << pph
            }
        }

        //  Check we have valid phases (unless we are dumping to database)
        //
        if ( ! phases && ! opt.database )
        {
            log.fatal( "No valid phases supplied")
            return
        }
        log.info( "Processing phases ${phases}")

        //  Open database file and empty it if needed
        //
        File dbFile = null
        if ( opt.database )
        {
            dbFile = new File( opt.database as String )
            dbFile.delete()
        }

        //  Validate source directory
        //
        if ( opt.srcdir )
        {
            def d = new File(opt.srcdir as String)
            if ( ! d.exists())
            {
                log.fatal( "Source directory doesn't exist [${d.path}]")
                return
            }
        }

		log.info( "Starting: Submit " + args )
        Map params = [  phases:         phases,
                        seqrun:         opt.seqrun,
                        singlesample:   opt.sample   ?: null,
                        platform:       opt.platform ?: 'MiSeq',
                        srcdir:         opt.srcdir   ?: null,
                        database:       dbFile,
                        complete:       opt.complete,
                        external:       opt.external
                     ]

        int jobs = new Submit().submit( params )

        log.info( "Finished: Submitted " + jobs + " jobs." )
	}

    /**
     * Submit jobs to cluster for processing
     *
     * @param opt   Options for job
     * @return      Number of jobs submitted
     */
    static int submit( Map params )
    {
        int jobs = 0

        //  Look for any seqrun directories matching the wildcard prefix
        //
        def dirs = findSeqrunDir( params )
        log.info(  "Seqrun dirs found " + dirs.size())
        log.debug( "Seqrun dirs found " + dirs)

        //  Loop through all seqrun directories found and process it
        //
        Map  orig   = new HashMap(params)
        for( String dir in dirs )
        {
            params = new HashMap(orig)
            params.seqpath = dir
            jobs += processSeqrun( params )
        }

        //  Create sentinel job for all jobs submitted
        //
        sentinel( jobids, samples )

        return jobs
    }

    /**
     * Process a seqrun directory
     *
     * @param   params  Parameters to use for processing a sequencing run
     * @return          Number of jobs submitted
     */
    static int processSeqrun( Map params )
    {
        int jobs = 0
        def seqd = new File( params.seqpath as String )
        assert seqd.exists()

        //  Extract a list of samples and their manifests
        //
        List<Map> sams = []
        if ( ! params.external )
            sams = getLimsSamples( params, seqd )
        else
            sams = getDirSamples( params, seqd )

        log.debug( "Samples ${sams.sample}")

        //  Set the full seqrun name
        //
        params.seqrun = seqd.name

        //  configure srcdir
        //
        params.srcdir = massageSrcdir( params )

        //  Process all sequenced samples
        //
        List done   = []                  // list of processed samples to weed out duplicates eg TruSight
        for ( sam in sams )
        {
            if ( sam.sample in done )
            {
                log.warn( "Duplicate sample found [${sam.sample}]")
                continue
            }

            //  Single sample processing
            //
            if ( params.singlesample && params.singlesample != sam.sample )
                continue

            //  Process this sample
            //
            params.sample    = sam.sample
            params.reference = sam.reference
            String jobid = processSample( params )

            //  Count and save job id
            //
            if ( jobid )
            {
                ++jobs
                jobids << jobid
                samples << params.seqrun + ':' + sam.sample
            }
            done << sam.sample
        }

        return jobs
    }

    /**
     * Create sentinel job called after all others are finished
     *
     * @param ids   List of job ids to wait for
     */
    static void sentinel( List ids, List samples )
    {
        if ( ! ids ) return

        String jobs = ids.join(':')
        println( "${ids} jobs=${jobs}")
        String cmd = "qsub -W depend=afterany:${jobs} -q pathology -N mp_sentinel -v seqrun=\"${samples.join(';')}\" ${loc.etcDir}sentinel.pbs"

        def rc = new RunCommand( cmd )
        def cmd_out = rc.run()
        if ( ! (cmd_out =~ /bioinf-head/))
            log.error( "Failed to queue job cmd: [${cmd}] out: [${cmd_out}]")
        else
            log.info( "Submitted Job ${cmd_out}: ${cmd}" )
    }

    /**
     * Massage srcdir parameter
     *
     * if not supplied set to root directory of the sequencing run
     *
     * @param params
     * @return  Supplied srcdir or <Sampleroot>/<seqrun>
     */
    static String massageSrcdir( Map params )
    {
        String sd = params.srcdir
        if ( params.database ) return sd

        //  Set the default directory for initial phase processing
        //
        if ( ! sd )
        {
            if ( params.phases[0] == 'align' )
                sd = "${params.seqpath}"
            else
                sd = loc.samDir + params.seqrun
        }

        //  Append seqrun directory as suffix if not already there
        //
        if ( ! ( sd =~ /${params.seqrun}\/?$/ ))
            sd = sd + loc.fs + params.seqrun

        log.info( "Srcdir set to [${sd}]")

        return sd
    }

    /**
     * Extract sample list from LIMS_<seqrun>.xml run config file
     *
     * @param params
     * @param seqd
     * @return       List of sample Maps
     */
    static List getLimsSamples( Map params, File seqd )
    {
        def lims = new File( seqd, 'LIMS_' + seqd.name + '.xml')
        if ( ! lims.exists())
        {
            log.error( "LIMS file doesn't exist [${lims.path}]")
            return []
        }

        //  Extract sample info from LIMs file
        //
        Map srm = new SeqrunLims().parseLims( lims.path )
        if ( ! srm )
        {
            //  invalid LIMS file
            //
            log.error( "Couldn't parse LIMS XML ${lims.path}")
            return []
        }
        assert srm.seqrun   == seqd.name

        //  Dump run details to file
        //
        if ( params.database )
        {
            dumpSeqrun( params,  srm )
            return []
        }

        return srm.samples
    }

    /**
     * Infer sample list for subdirectories in the external seqrun directory
     *
     * @param params
     * @param seqd
     * @return      List of sample Maps
     */
    static List getDirSamples( Map params, File seqd )
    {
        List samples = []

        //  Assume each subdirectory is an external sample
        //  the external parameter (from CLI options) must contain a valid manifest name
        //
        seqd.eachDir()
        {
            dir ->
                def sample = [ sample: dir.name, reference: params.external ]
                samples << sample
        }

        return samples
    }

    /**
     * Submit a qsub job for a sample with given phases
     *
     * @param   params  Map of parameters to use for submission
     * @return          Job id of job submitted
     */
    static String processSample( Map params )
    {
        log.info( "Processing Seqrun ${params.seqrun} Sample ${params.sample}")

        //	Set options to qsub jobs
        //
        String qsubopt = ""
        if ( 'align'    in params.phases ) qsubopt += "--align "
        if ( 'varcall'  in params.phases ) qsubopt += "--var "
        if ( 'annotate' in params.phases ) qsubopt += "--vep "
        if ( 'igv'      in params.phases ) qsubopt += "--igv "

        //  Set manifest
        //
        Panel panel = checkPanel( params.reference )
        if ( ! panel )
        {
            log.error( "Invalid reference for ${params.seqrun} ${params.sample} ${params.reference}")
            return null
        }

        //  Validate source directory for processing
        //
        String srcdir = checkSrcDir( params )
        if ( ! srcdir )
        {
            log.error( "Invalid srcdir for ${params.seqrun} ${params.sample} ${params.srcdir}")
            return null
        }

        def qopt = qsubopt
        qopt += " --sample " + params.sample
        qopt += " --seqrun " + params.seqrun
        qopt += " --ref "    + panel.panelRef
        qopt += " --srcdir " + srcdir

        //	Create Target directory to put log files into
        //
        def std = new SampleTgtDir( params.seqrun, params.sample)
        std.createDir()

        //  Skip if all target files are already there
        //
        if ( params.complete && std.complete())
        {
            log.info( "Skipping completed sample directory: " + std.sampleTgtDir )
            return null
        }

        //	Construct Job submission list
        //
        def jobFile = loc.etcDir + "qsub_pipeline.pbs"
        List cmd =  [
                    "qsub",
                    "-d", std.sampleTgtDir,
                    "-v", "QSUB_OPTS=\"${qopt}\"",
                    "-N", "mp_${std.sample}",
                    "-q", "pathology",
                    jobFile
                    ]

        def rc = new RunCommand( cmd )
        def cmd_out = rc.run()
        if ( ! (cmd_out =~ /bioinf-head/))
        {
            log.error( "Failed to queue job cmd: [${cmd}] out: [${cmd_out}]")
            return null
        }

        //	Job Submitted OK
        //
        cmd_out = cmd_out.replaceAll( "\\s" , "")
        log.info( "Submitted Job ${cmd_out}: " + cmd.join(" "))

        return cmd_out
    }

    /**
     * Validate manifest
     *
     * @param manifest  Manifest to use
     * @return          Panel object for manifest
     */
    static Panel checkPanel( String manifest )
    {
        Panel panel = new Panel(manifest)

        if ( ! panel.valid())
            return null

        return panel
    }

    /**
     * Validate source directory for initial processing
     *
     * @param params.phase[0]     Initial phase of processing (which may source data from a non-standard dir)
     * @param params.srcdir       Source directory of all the sequencing data eg /pathology/sequencing/Pathology_Research/<seqrun>
     * @param params.sample       Sample
     * @return                    Extended path to exact source directory for the phase
     */
    static String checkSrcDir( Map params )
    {
        //  Initial phase of pipeline which may take source files from elsewhere
        //
        def phase = params.phases[0]

        //  Set up default directory to search
        //
        def d = new File( params.srcdir as String, params.sample )
        if ( phase == 'align' )
        {
            //  Find the sample directory using Ant wildcards Todo: get rid of people in project !!
            //
            def dirscan = null
            dirscan = new AntBuilder().path
            {
                dirset(dir: "${params.srcdir}/ProjectFolders", includes: "Project_*/Sample_${params.sample}")
            }

            //  Choose last if there are multiple matching dirs
            //
            log.debug( ">>> ${params.srcdir} Found [${dirscan}]" )
            try
            {
                for ( dir in dirscan )
                    d = new File(dir as String)
            } catch( Exception ex)
            {
                log.error( "Ant dirset() fail ${params.srcdir}" + ex )
                return null
            }
        }

        //  Check we have a directory
        //
        if ( ! d.isDirectory())
        {
            log.error( "Not a direcotry ${d}" )
            return null
        }

        //  Validate files in source directory
        //
        switch (phase)
        {
            case 'align':

                //  Look for fastq.gz reads in ../ProjectFolders/Project_<person>/Sample_<sample>
                //
                def ssd = new SampleSrcDir( d.path )
                if ( ssd.reads.size() > 0 ) return d.path
                break
            case 'varcall':

                //  Look for BAM file
                //
                if ( new File(d.path + '/Bam', params.sample + '_aligned.bam').exists()) return d.path + '/Bam'
                if ( new File(d.path, params.sample + '.bam' ).exists()) return d.path
                break
            case 'annotate':

                //  Look for VCF file
                //
                if ( new File(d.path + '/VCF', params.sample + '_aligned_vs.vcf').exists()) return d.path + '/VCF'
                if ( new File(d.path, params.sample + '.vcf' ).exists()) return d.path
                break
            case 'igv':

                //  Don't need a src dir for IGV
                //
                return d.path
            default:
                assert false, "Invalid phase ${phase}"
        }

        return null
    }

    /**
     * Find all match Seqrun directories prefixed by seqrun
     *
     * @param seqrun    File name prefix to search for
     * @return          List of directories to process
     */
    static List findSeqrunDir( Map params )
    {
        List dirs = []

        if ( params.seqrun.startsWith('/'))
            return checkDirs( params.seqrun )

        //  Loop through sequencing platforms
        //
        for ( pf in ['NextSeq','MiSeq','HiSeq'] )
        {
            if ( params.platform != 'all' && pf != params.platform ) continue

            //  loop through repositories
            //
            for ( repos in ['Runs','Archives'])
            {
                def ds = checkDirs( "/pipeline/${repos}/${pf}/${params.seqrun}" )
                if ( ds ) dirs += ds
            }
        }

        return dirs
    }

    /**
     * Check this is a valid directory to process
     *
     * @param dirpat    Path file name prefix to search for
     * @return          List of directories to process
     */
    static List checkDirs( String dirpat )
    {
        List dirs = []

        def d = new File( dirpat )
        def p = new File(d.parent)
        def b = d.name

        if ( p.exists())
        {
            p.eachFileMatch( ~/${b}.*/ )
            {
                if ( it.exists() && it.isDirectory()) dirs << it.path
            }
        }
        return dirs
    }

    static boolean header = false

    /**
     * Dump a seqruns details to a file
     *
     * @param params
     * @param srm       Map of LIMS.xml parameters
     */
    static void dumpSeqrun( Map params, Map srm )
    {
        Map fldmap = [:]

        //  Load in Illumina runParameters details
        //
        Map runp = new SeqrunLims().parseRunParameters( params.seqpath + '/runParameters.xml' )
        if ( ! runp ) runp = [:]

        //  Add seqrun data
        //
        fldmap << [seqrun:   srm.seqrun]
        fldmap << [platform: srm.platform]
        fldmap << [sepe:     srm.sepe]
        fldmap << [library:  srm.library]

        fldmap << [experiment: runp.experiment ]
        fldmap << [scanner:    runp.scanner ]
        fldmap << [readlen:    runp.readlen ]

        //  Output header
        //
        if ( ! header )
        {
            List headers = []
            headers.addAll( fldmap.keySet())
            headers.addAll( 'sample,reference,analysis,username,useremail,laneno'.split(','))
            params.database << "##  Created by Submit.groovy\n##\n#"
            params.database << headers.join("\t") + "\n"
            header = true
        }

        //  Process all sequenced samples
        //
        List<Map> sams = srm.samples
        for ( sam in sams )
        {
            fldmap << [sample:    sam.sample]
            fldmap << [reference: cleanPanel(sam.reference)]
            fldmap << [analysis:  sam.analysis]
            fldmap << [username:  sam.username]
            fldmap << [useremail: sam.useremail]
            fldmap << [laneno:    sam.laneno]

            //  Output TSV row to dump file
            //
            params.database << fldmap.values().join("\t") + "\n"
        }
    }

    /**
     * Remove path rubbish from manifest name and trailing .fasta extension
     *
     * @param panel Panel with path
     * @return      Panel without path and extension
     */
    static String cleanPanel( String panel )
    {
        if ( panel )
        {
            panel = panel.replace('.fasta','')
            panel = panel.replaceFirst( /^.*\//, '' )
        }
        return panel
    }
}

