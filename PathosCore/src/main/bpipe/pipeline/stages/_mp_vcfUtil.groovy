/*
 * Copyright (c) 2016 PathOS Variant Curation System. All rights reserved.
 *
 * Organisation:    Peter MacCallum Cancer Centre
 * Author:          Kenneth Doig
 */

/**
 * Pipeline stages for production molecular pathology pipeline
 *
 * VCF Utilities
 *
 * @Author  Ken Doig
 */

@Filter('nrm')
mp_normVcf	=
{
	doc	title:	"Normalise VCF",
		desc:	"Normalise a VCF with Mutalyzer to conform to HGVS nomenclature and annotate",
		author:	"Ken Doig, Molecular Pathology"

    requires OUTDIR : "Specify OUTDIR"
    requires DBNAME : "Specify DBNAME"

    output.dir = file(input).parent

    if (DEBUG) println "In mp_normVcf in=$input.vcf out=$output"

	exec "${MP_PATHOS_HOME}/bin/NormaliseVcf --rdb ${DBNAME} $input.vcf $output"
}

@Filter('ma')
mp_splitAllele	=
{
	doc	title:	"Split multi-allele VCF",
		desc:	"Split multi-allele lines in a VCF with vcf tools (vt) ",
		author:	"Ken Doig, Molecular Pathology"

    output.dir = file(input).parent

    if (DEBUG) println "In mp_splitAllele in=$input.vcf out=$output"

	exec "${MP_PATHOS_HOME}/bin/VcfSplitMultiAllele.sh $input.vcf $output"
}

@Filter('ls')
mp_leftShift	=
{
	doc	title:	"Left shift VCF variants",
		desc:	"Genomic left shift variants in a VCF with vcf tools (vt) ",
		author:	"Ken Doig, Molecular Pathology"

    output.dir = file(input).parent

    if (DEBUG) println "In mp_leftShift in=$input.vcf out=$output"

	exec "vt normalize -q -r ${MP_REFERENCE} -o $output $input.vcf"
}

mp_indexVcf	=
{
	doc	title:	"Index a VCF file for IGV",
		author:	"Broad IGV group, Wrapper - Ken Doig, Molecular Pathology"

    output.dir = file(input).parent

	transform( '.vcf' ) to( '.vcf.idx' )
	{
        if (DEBUG) println "In mp_indexVcf in=$input out=$output"

        exec "igvtools index $input"
	}
	forward input										// act as a filter 
}

@Transform('tsv')
mp_vcfToTsv =
{
    doc title:  "Unpack VCF to TSV",
        desc:   "Unpack a VCF file columns into a TSV file",
        author: "Ken Doig, Molecular Pathology"

    requires SAMPLE : "Specify SAMPLE"
    requires SEQRUN : "Specify SEQRUN"

    output.dir = file(input.vcf).parent

    if (DEBUG) println "In mp_vcfToTsv in=$input.vcf out=$output"

    def vcfcols = "${MP_PATHOS_HOME}/etc/vcfcols.txt"

    exec    """
            PATH="${MP_PATHOS_HOME}/bin:$PATH";
            Vcf2Tsv --columns ${vcfcols} --sample $SAMPLE --seqrun $SEQRUN $input.vcf $output
            """

    forward input.vcf										// act as a filter
}

mp_forwardVcf =
    {
        doc title:  "Forward VCF file to next stage",
            desc:   "Explicitly forward the VCF file for subsequent VCF needy stages",
            author: "Ken Doig"

        forward( OUTDIR + "/${SAMPLE}.vcf" )
    }

mp_copyVcf =
    {
        doc title:  "Copy a VCF file into repository",
            desc:   "Move VCF file into a known spot in the data repository",
            author: "Ken Doig"

        requires OUTDIR : "Specify OUTDIR"

        output.dir = OUTDIR + '/VCF'

        produce( "${SAMPLE}.vcf" )
        {
            if (DEBUG) println "In mp_copyVcf in=$input.vcf out=$output dir=${output.dir}"

            exec    """
                    mkdir -p ${output.dir} ;
                    cp -v $input $output
                    """
        }
    }

mp_checkVcf =
    {
        doc title:  "Terminate branch of no variants",
            desc:   "Check a VCF and terminate branch with success if no variants",
            author: "Ken Doig"

        if ( DEBUG ) println "In mp_checkVcf in=$inputs.vcf"

        //  Need to add a unique sample name to the check as work around for non-unique check files
        //  bpipe bug email from s.s. 31aug15
        //
        check( "mp_checkVcf_${SAMPLE}" ) {
            exec    """
                    numvar=`grep -v '#' $input1.vcf | wc -l`;
                    [ -s $input1.vcf -a $numvar -gt 0 ]
                    """
        } otherwise { succeed "No variants found in $input1.vcf" }

        forward( input.vcf )
    }

//  Todo: Deprecated - replaced by mp_mergeVcfPM

mp_mergeVcf =
    {
        doc title:  "Merge a pair of VCFs",
            desc:   "Use GATK to create a union of a pair of VCFs",
            author: "Ken Doig"

        requires MP_REFERENCE   : "Specify MP_REFERENCE"
        requires OUTDIR         : "Specify OUTDIR"

        output.dir = OUTDIR + '/VCF'

        //  Extract variant call from the basename of the VCF file eg VCF=/path/sample.varcall.vcf
        //
        def vcfs = inputs.vcf
        if ( vcfs.size() != 2  ) succeed( "Need 2 VCFs for mp_mergeVcf" )

        //  Loop through VCF paths and extract variant caller
        //
        def varcalls = []
        vcfs.each
        {
            vcf ->
            List dots = vcf.tokenize('.')
            if ( dots[-1] == 'vcf' )
            {
                println dots
                varcalls << dots[1]    // expecting a filename sample.varcall.nrm.vcf
            }
        }
        if ( varcalls.size() != 2 ) succeed( "Need 2 Variant callers for mp_mergeVcf" )

        //  Marge VCFs using GATK CombineVariants
        //
        produce( "${SAMPLE}.merge.vcf" )
        {
            if (DEBUG) println "In mp_mergeVcf in=$inputs.vcf out=$output"

            exec    """
                    GenomeAnalysisTK \
                       -T CombineVariants \
                       -R $MP_REFERENCE \
                       --variant:${varcalls[0]} $input1.vcf \
                       --variant:${varcalls[1]} $input2.vcf \
                       -o $output \
                       -genotypeMergeOptions PRIORITIZE \
                       -priority Primal,Canary \
                       --setKey Identified	\
                       --downsampling_type none
                    """
        }
    }

mp_mergeVcfPM =
    {
        doc title:  "Merge a pair of VCFs (PM local)",
            desc:   "Use VcfMerge to create a union of a pair of VCFs. If this is an MRD sample we don't need to merge Primal variants, only use the Canary VCF",
            author: "Ken Doig"

        requires OUTDIR         : "Specify OUTDIR"

        output.dir = OUTDIR + '/VCF'

        //  Extract variant call from the basename of the VCF file eg VCF=/path/sample.varcall.vcf
        //
        def vcfs = inputs.vcf
        if ( vcfs.size() != 2 && ! PANEL.startsWith( "MRD" )) succeed( "Need 2 VCFs for mp_mergeVcfPM" )
        if ( vcfs.size() == 0 &&   PANEL.startsWith( "MRD" )) succeed( "No VCFs for mp_mergeVcfPM" )

        //  Loop through VCF paths and extract variant caller
        //
        def vcfp = vcfs.find{ it =~ /Primal/ }
        def vcfc = vcfs.find{ it =~ /Canary/ }

        //  Merge VCFs using VcfMerge
        //
        produce( "${SAMPLE}.merge.vcf" )
        {
            if (DEBUG) println "In mp_mergeVcfPm in=$inputs.vcf out=$output"

            //  For an MRD sample copy over the Canary VCF, otherwise merge Primal and Canary
            //
            if ( PANEL.startsWith( "MRD" ))
            {
                exec "cp -v ${vcfc} ${output}"
            }
            else
            {
                exec    """
                        canLabel=`Canary --version`;
                        prmLabel='Primal 1.0';
                        ${MP_PATHOS_HOME}/bin/VcfMerge --output $output --labels '\${prmLabel},\${canLabel}' ${vcfp} ${vcfc}
                        """
            }
        }
    }


//  Shorthand closure for all VCF manipulation steps
//
mp_normalise  = segment { mp_checkVcf + mp_splitAllele + mp_normVcf }
mp_prepareVcf = segment { mp_checkVcf + [ mp_indexVcf , mp_vcfToTsv ] }