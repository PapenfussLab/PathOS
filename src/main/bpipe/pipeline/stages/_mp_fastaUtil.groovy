/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

/**
 * Pipeline stages for production molecular pathology pipeline
 *
 * FASTA Utilities
 *
 * @Author  Ken Doig
 */

mp_fastqc =
{
	doc	desc:   "Run FASTQC to generate read QC metrics",
		title:	"FASTQC",
		author:	"Wrapper - Ken Doig, Molecular Pathology"

    requires "OUTDIR" : "Specify OUTDIR"

    output.dir = OUTDIR + '/QC'

    if (DEBUG) println "In fastqc in=$input out=$output"

    exec "fastqc --quiet --outdir ${output.dir} ${input.dir}/*fastq.gz && date > $output"
}

mp_checkFasta =
    {
        doc title:  "Terminate branch of no reads",
            desc:   "Check a fasta files existence and terminate branch with success if no reads",
            author: "Ken Doig"

        if ( DEBUG ) println "In mp_checkFasta in=$input"

        //  Need to add a unique sample name to the check as work around for non-unique check files
        //  bpipe bug email from s.s. 31aug15
        //
        //  We're expecting two gzipped FASTQ files of paired reads
        //
        check( "mp_checkFasta_${SAMPLE}" ) {
            exec    """
                    files=`ls -1 ${input}/*.fastq.gz | wc -l` ;
                    [ $files -eq 2 ]
                    """
        } otherwise { succeed "Didn't find expected 2 x *.fastq.gz files in ${input}" }

        forward( input )
    }
