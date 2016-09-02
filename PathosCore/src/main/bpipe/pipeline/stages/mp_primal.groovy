/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

/**
 * Description of stage
 *
 * @input <dir>  Sample read directory containing fastq pair(s)
 * @output <bam> description of output
 * @outputdir  Description of output directory
 */

mp_align =
{
	doc		title: 		"Primal Aligner",
        	desc:		"Perform alignment (primal aligner) on a pair of fastq files",
			constraints:"Input must be a DIRECTORY where fastq.gz files are located",
        	author: 	"Primal: Jason Ellul, Bpipe wrapper: Ken Doig"

	requires MP_REFERENCE:	"Please specify REFERENCE"
    requires OUTDIR : "Specify OUTDIR"
    requires SEQRUN : "Specify SEQRUN"
    requires SAMPLE : "Specify SAMPLE"
    requires PANEL  : "Specify PANEL"

	//	Convert panel to aligner input file paths
	//
	def panelfasta  = "/pipeline/Runs/MiSeq/Indexes/${PANEL}.fasta"
	def panelprimer = "${panelfasta}.primers"
    output.dir  = OUTDIR + '/BAM'

	produce( "${SAMPLE}.bam" )
	{
        if (DEBUG) println "In primal in=$input,$PANEL out=$output"

		exec "alignCustomAmplicon.pl -l ${output}.log -o $output $panelfasta ${input.dir}/${SAMPLE}*R1_001.fastq.gz ${input.dir}/${SAMPLE}*R2_001.fastq.gz $panelprimer","primal"
	}
}

@Filter('cnv')
mp_convertBedCoords =
{
    doc     desc:      "Convert the amplicon coordinates of a BAM file to genomic coordinates",
            title:     "Convert to genomic coordinates"

    requires MP_REFERENCE:	"Please specify REFERENCE"
    requires OUTDIR:        "Specify OUTDIR"

    output.dir = OUTDIR + '/BAM'

    if (DEBUG) println "In mp_convertBedCoords in=$input out=$output"

    exec "ConvertBedCoordsToGenome.pl -l ${output}.log ${MP_REFERENCE} ${input} ${output}"
}

mp_alignStats =
{
    doc	title:  "Clean alignment stats",
        desc:	"Massage alignment stats into a TSV file",
        author:	"Wrapper - Ken Doig, Molecular Pathology"

    requires "OUTDIR" : "Specify OUTDIR"
    requires "SEQRUN" : "Specify SEQRUN"
    requires "SAMPLE" : "Specify SAMPLE"
    requires "PANEL"  : "Specify PANEL"

    output.dir = OUTDIR + '/BAM'

    transform( '.cnv.bam' ) to( '.stats.tsv' )
    {
        if (DEBUG) println "In mp_alignStats in=$input out=$output"

        exec "${MP_PATHOS_HOME}/bin/AlignStatsToTsv --sample $SAMPLE --seqrun $SEQRUN --panel $PANEL ${input.prefix.prefix}_stats.csv $output"
    }
}

mp_alignStatsRun =
{
    doc	title:  "Summarise alignment stats for run",
        desc:	"Collect alignment stats for run into a CSV file",
        author:	"Wrapper - Ken Doig, Molecular Pathology"

    //  globDir = ../<seqrun>/<sample>
    //
    output.dir  = globDir + '/../RunPipe'   // ../Samples/<rootdir>/<seqrun>/RunPipe
    List panels = globPanel.keySet() as List
    assert panels, "No panels in globPanel"

    //  Use the first panel (panels[0]) as the check for successful output
    //
    produce( "${panels[0]}_PoorMapping.csv", "${panels[0]}_ReadStatsBySample.csv", "${panels[0]}_Stats.csv", "${panels[0]}_TotalReadsUsed.csv" )
    {
        if (DEBUG) println "In mp_alignStatsRun in=$panels out=$output.dir"

        //  stats.csv files are collected from ../<seqrun>/*/BAM/*stats.csv
        //

        for ( panel in panels )
        {
            //  Construct list of samples BAM alignment stats
            //
            String samples = ""
            for ( sample in globPanel[panel] )
            {
                samples += "${output.dir}/../${sample}/BAM/*stats.csv "
            }

            //  Bug in aligncustom_runstats.pl - expectas a minimum of 2 samples
            //
            check( "mp_alignStatsRun_${SAMPLE}" ) {
                exec    "aligncustom_runstats.pl ${output.dir} $panel $samples ; exit 0"
            } otherwise { succeed "Only one sample found in mp_alignStatsRun ${globPanel[panel]}" }
        }
    }

    forward( inputs )
}

mp_forwardBam =
{
    doc title:  "Forward BAM file to next stage",
        desc:   "Explicitly forward the BAM file for subsequent BAM needy stages",
        author: "Ken Doig"

    forward( OUTDIR + "/BAM/${SAMPLE}.cnv.bam" )
}

//  Single branch to perform Primal alignment
//
mp_primal = segment { mp_align + mp_convertBedCoords + mp_alignStats + mp_forwardBam }
