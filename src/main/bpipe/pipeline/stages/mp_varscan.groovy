/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */


@Transform( 'pu' )
mp_pileup =
{
    doc desc:			"Pileup for VarScan",
        title:			"Samtools pileup",
        constraints:	"Input BAM file, Human reference build",
        author:			"Wrapper - Ken Doig, Molecular Pathology"

    requires OUTDIR         : "Specify OUTDIR"
    requires MP_REFERENCE   : "Specify MP_REFERENCE"

    output.dir = OUTDIR + "/VCF"

    def puerr           = "${output.prefix}.puerr"

    if (DEBUG) println "In mp_pileup in=$input out=$output"

    exec	"samtools mpileup -d 1000000 -B -f ${MP_REFERENCE} $input 2> $puerr > $output"
}

mp_varscan =
{
    doc desc:			"Variant Calling for Amplicon Pipeline using an old version of VarScan2",
        title:			"VarScan Variant Caller",
        constraints:	"Input pileup file",
        author:			"Wrapper - Ken Doig, Molecular Pathology"

    requires OUTDIR : "Specify OUTDIR"

    output.dir = OUTDIR + "/VCF"

    produce( "${SAMPLE}.Primal.vcf" )
    {
        def vs2tovcfExec	= "vs2vcf.pl"
        def vsParams	    = "--min-avg-qual 20 --vcf-sample-list .${SAMPLE}.tmp --min-var-freq 0.03 --min-freq-for-hom 0.85 --p-value 1e-04 --variants --strand-filter 0 --output-vcf 1 --min-coverage 100 --min-reads2 20"
        def vserr           = "${output.prefix}.vserr"

        if (DEBUG) println "In mp_varscan in=$input out=$output"

        exec    """
                echo ${SAMPLE} > .${SAMPLE}.tmp ;
                varscan.sh mpileup2cns $input ${vsParams} 2> $vserr | ${vs2tovcfExec} > $output
                """
    }
}

mp_varCall = segment { mp_pileup + mp_varscan }