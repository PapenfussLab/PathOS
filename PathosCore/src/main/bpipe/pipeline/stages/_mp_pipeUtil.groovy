/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

import org.petermac.util.Tsv

//  Global variables needed for each branch
//
sampleList      = ""                //  Filename of TSV file of samples to process
sampleParams    = []                //  Global parameters List<Map> of all samples to be analysed
runSamples      = [:]               //  Map of [sample:<input dir path>]
globDir         = ""                //  Last output dir
globPanel       = [:]               //  Map of panel samples

//  Global constants
//
MP_REFERENCE = "/data/reference/indexes/human/g1k_v37/picard/human_g1k_v37.fasta"

//  This will be replaced by a class that extracts Seqrun parameters from a REST interface
//
mp_validateSamples =
{
    doc title:  "Validate paramaters",
        desc:   "Validate Seqrun and Sample paramaters from TSV file",
        author: "Ken Doig, Molecular Pathology"

    //  Load in the run paramaters from a TSV file and convert to Map
    //  sampleParams that is available globally to all stages
    //
    def runf     = new File( "$input" )
    sampleList   = runf.absolutePath
    def run      = new Tsv( runf )
    def nlines   = run.load( true )
    def params   = run.getRowMaps()

    //  Rudimentary validation
    //
    if ( nline == 0 ) fail( "No samples found in ${run.filename}" )
    if ( params.sample?.size() != nlines ) fail( "Missing sample parameter" )
    if ( params.pipein?.size() != nlines ) fail( "Missing pipein parameter" )
    if ( params.outdir?.size() != nlines ) fail( "Missing outdir parameter" )

    //  Initialise the Sample input directories as a Map
    //
    params.eachWithIndex
    {
        sm, idx ->
            if ( sm.pipeline == PIPELINE )
            {
                def branchName  = "${idx}:${sm.sample}" // "nnn:<sample name>"
                runSamples[branchName] = sm.pipein      // Global variable visible to all stages/branches
                sampleParams << sm                      // Global variable visible to all stages/branches
                globDir    = sm.outdir                  // Global out dir paramater

                if ( ! globPanel[sm.panel] ) globPanel[sm.panel] = []
                globPanel[sm.panel] << sm.sample        // Global panel parameter
            }
    }

    println "Pipeline ${PIPELINE} loaded ${sampleParams.size()}/$nlines sample(s) from ${run.filename} ..."
    println "Samples loaded ${sampleParams.sample}"

    if ( ! sampleParams.size()) fail( "No samples with pipeline=${PIPELINE} in ${run.filename}")

    //  List command versions
    //
    exec    ". /etc/profile.d/modules.sh; module list"
}

mp_branch =
{
    doc title:  "Initialise branch parameters",
        desc:   "Initialise a bpipe branch with parameters about the sample",
        author: "Jason Li"

    def (idx, sample) = branch.name.prefix.tokenize( ':' )
    if ( ! idx.isInteger() || sample == null ) fail("Unexpected branch name ${branch.name}")
    def params = sampleParams[idx as int]

    if ( DEBUG ) println "In mp_branch[${branch.name}] $sample $idx params=$params"

    // This will trick all stages under this branch to use 'branch.OUTDIR' instead of the global one
    // All stages outside this branch are NOT affected by this (global OUTDIR is still visible outside branch)
    //
    binding.variables.remove 'OUTDIR'
    binding.variables.remove 'SAMPLE'
    binding.variables.remove 'PANEL'
    binding.variables.remove 'SEQRUN'
    binding.variables.remove 'NORMRUN'
    binding.variables.remove 'NORMSAM'

    //  Set branch specific parameters available to all stages in this branch
    //
    branch.OUTDIR  = "$params.outdir"       // pipeline results root directory
    branch.SAMPLE  = sample                 // sequenced sample name
    branch.PANEL   = "$params.panel"        // panel name for sample assay
    branch.SEQRUN  = "$params.seqrun"       // Illumina seqrun
    branch.NORMRUN = "$params.normrun"      // Matching 'normal' seqrun for a tumour sample
    branch.NORMSAM = "$params.normsam"      // Matching 'normal' sample for a tumour sample

    forward ( params.pipein )               // output the input directory for a sample eg FASTQ directory
}

mp_chkBranch =
{
    doc title:  "Debug stage for a branch"

    def (idx, sample) = branch.name.prefix.tokenize( ':' )
    def params        = idx.isInteger() ? sampleParams[idx as int] : null

    if ( DEBUG ) println "In mp_chkBranch[${branch.name}] $sample $idx params=$params"

    println "### outdir=$OUTDIR ${params?.outdir}"
    println "### sample=$SAMPLE ${params?.sample}"
    println "### panel =$PANEL  ${params?.panel}"
    println "### seqrun=$SEQRUN ${params?.seqrun}"
}

mp_done =
{
    doc title:  "Email pipeline result",
        desc:   "Email a user with the successful outcome of a pipeline run"

    requires MP_PATHOS_HOME : "Specify MP_PATHOS_HOME"

    if ( DEBUG ) println "In mp_done"

    def params = sampleParams[0]

    exec    """
            logdate=`date +%y%m%d` ;
            logtime=`date +%T` ;

            echo -e "\$logdate\t\$logtime\t${params.seqrun}\t${params.outdir}\tfinished" >> ${MP_PATHOS_HOME}/log/RunPipe.log ;
            """
}
