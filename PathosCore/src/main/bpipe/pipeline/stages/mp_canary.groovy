/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

mp_runCanary =
{
    doc title:  "Run Canary amplicon variant caller",
        desc:   "Call variants directly from FASTQ with Canary",
        author: "Ken Doig, Molecular Pathology"

    requires "SAMPLE" : "Specify SAMPLE"
    requires "PANEL"  : "Specify PANEL"
    requires "OUTDIR" : "Specify OUTDIR"

    output.dir = "$OUTDIR/CAN"

    produce( "${SAMPLE}.Canary.vcf", "${SAMPLE}.tsv", "${SAMPLE}.bam" )
    {
        if (DEBUG) println "In mp_runCanary in=$input out=$output"

        def panelPath   = "/pipeline/Runs/MiSeq/Indexes"
        def pct         = 100   // % of reads to process
        def vaf         = 3     // VAF % to call reads
        def minp        = 10    // minimum number of read pairs to call variant
        def complex     = '--complex'
        def cache       = ''
        if ( PANEL.startsWith( "MRD" ))
        {
            vaf     = 0.000001
            minp    = 1
            complex = ''
            cache   = '--nocache'
        }

        exec    """
                Canary  \
                        --amplicon ${panelPath}/${PANEL}.fasta \
                        --primers  ${panelPath}/${PANEL}.fasta.primers \
                        --vcf      $output1 \
                        --output   $output2 \
                        --bam      $output3 \
                        --vaf      $vaf \
                        --reads    $pct \
                        --minpair  $minp \
                        ${cache}     \
                        ${complex}   \
                        ${input}/${SAMPLE}*.fastq.gz
                """
    }
}
