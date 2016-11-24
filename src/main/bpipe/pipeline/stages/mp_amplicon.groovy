/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

/**
 * Pipeline stages for production molecular pathology pipeline
 *
 * This is the collected stages for a bpipe/seqliner pipeline for routine MiSeq amplicon sequencing
 *
 * @Author  Ken Doig
 */

import groovy.text.GStringTemplateEngine

//  VEP annotation Todo: deprecated
//
@Transform('vep')
mp_vep	=
{
	doc	title:	"Annotate VCF with VEP",
		desc:	"Annotate a VCF file usine Ensembl VEP",
		author:	"Ensembl group, Wrapper - Ken Doig, Molecular Pathology"

    requires OUTDIR : "Specify OUTDIR"

    output.dir = OUTDIR + '/VEP'

    if (DEBUG) println "In mp_vep in=$input out=$output"

	exec "${MP_PATHOS_HOME}/bin/mp-vep.sh -i $input $output"
}

//  VEP annotation Todo: deprecated
//
@Transform('tsv')
mp_vepToTsv	=
{
	doc	title:	"Unpack VEP to TSV",
		desc:	"Unpack a VEP file columns into a TSV file",
		author:	"Ken Doig, Molecular Pathology"

    requires OUTDIR : "Specify OUTDIR"
    requires SEQRUN : "Specify SEQRUN"
    requires SAMPLE : "Specify SAMPLE"

    output.dir = OUTDIR + "/VEP"

	if (DEBUG) println "In mp_vepToTsv in=$input out=$output"

    def vepcols = "${MP_PATHOS_HOME}/etc/vepcols.txt"

    exec "${MP_PATHOS_HOME}/bin/VepToTsv --columns ${vepcols} --sample $SAMPLE --seqrun $SEQRUN $input $output"
}

mp_igv	=
{
	doc	title:	"Create an IGV session file",
		desc:	"Construct an IGV session file for browsing reads and variants",
		author:	"Ken Doig, Molecular Pathology"

    requires OUTDIR : "Specify OUTDIR"
    requires SEQRUN : "Specify SEQRUN"
    requires SAMPLE : "Specify SAMPLE"
    requires PANEL  : "Specify PANEL"

    output.dir = OUTDIR

    //  Set the base directory for pipeline output - suffix of OUTDIR
    //  last three directories of OUTDIR eg [Molpath,Research,Testing]/<seqrun>/<sample>
    //  Todo: This should be encapsulated in a Class
    //

    //  This is a bit tortured, unpacking a directory because of poorly defined URLs to access the file system
    //
    String sambase = "Testing/${SEQRUN}/${SAMPLE}"     // default base
    def    parts   = OUTDIR.tokenize('/')
    if ( parts.size() > 3 )
    {
        def psample  = parts[-1]    // last directory - sample name
        def pseqrun  = parts[-2]    //  parent dir - seqrun
        def psambase = parts[-3]    //  parent normally in [Molpath,Research,Testing]
        if ( psample == SAMPLE && pseqrun == SEQRUN )
        {
            sambase = "${psambase}/${SEQRUN}/${SAMPLE}"
        }
    }

    //  Convert template file and bind to context variables
    //
	def dataServer 	= "http://bioinf-ensembl.petermac.org.au"
    def igv_template= "${MP_PATHOS_HOME}/etc/IGV_Session_Template.xml"
    def engine  	= new GStringTemplateEngine()
	def template 	= new File(igv_template)
    def binding 	= [ hostURL: dataServer, panelDir: "Panels/${PANEL}", sampleDir: "Pathology/${sambase}", sample: SAMPLE ]
    def session 	= engine.createTemplate(template).make(binding).toString()

	produce( 'IGV_Session.xml' )
	{
        if (DEBUG) println "In mp_igv in=$input out=$output"

        //  Save as IGV session file for sample
        //
        def outf = new File( output )
        outf.delete()
        outf << session
	}
}

//  This stage maps bpipe files names in sub-directories into legacy pipeline file names for compatibility
//	Note: this is used by both Amplicon and MRD samples
//	MRD samples copy the Canary BAM and VCF files into the top directory
//
mp_mapFiles	=
{
	doc	title:	"Map files to legacy PathOS",
		desc:	"Move generated files into top level sample directory to match current PathOS structure",
		author:	"Ken Doig, Molecular Pathology"

    requires OUTDIR : "Specify OUTDIR"
    requires SAMPLE : "Specify SAMPLE"

    output.dir = OUTDIR

	produce (	"${SAMPLE}.bam",
				"${SAMPLE}.bai",
				"${SAMPLE}.stats.tsv",
				"${SAMPLE}.vcf",
				"${SAMPLE}.vcf.idx",
				"${SAMPLE}.vcf.tsv.mut"
			)
	{
		if (DEBUG) println "In mp_mapFiles in=$inputs out=$output $SAMPLE"

		String mrdcmds = ''
		String cp      = 'cp --no-preserve=timestamps'

		if ( PANEL.startsWith( "MRD" ))
		{
			mrdcmds =	"""
						if [ -s ${OUTDIR}/CAN/${SAMPLE}.bam ]; then $cp ${OUTDIR}/CAN/${SAMPLE}.bam  ${OUTDIR}  ; fi ;
						if [ -s ${OUTDIR}/CAN/${SAMPLE}.bai ]; then $cp ${OUTDIR}/CAN/${SAMPLE}.bai  ${OUTDIR}  ; fi ;
						$cp ${OUTDIR}/CAN/${SAMPLE}.*.nrm.vcf        ${OUTDIR}/${SAMPLE}.vcf		    ;
						$cp ${OUTDIR}/CAN/${SAMPLE}.*.nrm.vcf.idx    ${OUTDIR}/${SAMPLE}.vcf.idx		;
						$cp ${OUTDIR}/CAN/${SAMPLE}.*.nrm.tsv        ${OUTDIR}/${SAMPLE}.vcf.tsv.mut	;
						"""
		}

		exec    """
                $cp ${OUTDIR}/BAM/${SAMPLE}.cnv.bam                 ${OUTDIR}/${SAMPLE}.bam			;
                $cp ${OUTDIR}/BAM/${SAMPLE}.cnv.bai                 ${OUTDIR}/${SAMPLE}.bai			;
                $cp ${OUTDIR}/BAM/${SAMPLE}.stats.tsv               ${OUTDIR}					    ;
                $cp ${OUTDIR}/CAN/${SAMPLE}.tsv                     ${OUTDIR}/${SAMPLE}.canary.tsv	;
                $cp ${OUTDIR}/VCF/${SAMPLE}.merge.tsv               ${OUTDIR}/${SAMPLE}.vcf.tsv.mut ;
                $cp ${OUTDIR}/VCF/${SAMPLE}.merge.vcf               ${OUTDIR}/${SAMPLE}.vcf		    ;
                $cp ${OUTDIR}/VCF/${SAMPLE}.merge.vcf.idx           ${OUTDIR}/${SAMPLE}.vcf.idx		;
				$mrdcmds
                """
	}
}

//  This stage maps bpipe files names in sub-directories into legacy pipeline file names for compatibility
//
mp_mapVcfFiles	=
{
	doc	title:	"Map files to legacy PathOS",
		desc:	"Move generated files into top level sample directory to match current PathOS structure",
		author:	"Ken Doig, Molecular Pathology"

    requires OUTDIR : "Specify OUTDIR"
    requires SAMPLE : "Specify SAMPLE"

    output.dir = OUTDIR

	produce (	"${SAMPLE}.vcf",
				"${SAMPLE}.vcf.idx",
				"${SAMPLE}.vcf.tsv.mut"
			)
	{
		if (DEBUG) println "In mp_mapVcfFiles in=$inputs out=$output $SAMPLE"

		def cp  = "cp --no-preserve=timestamps -v"
		def src = "VCF"
		if ( PANEL.startsWith( "MRD" ))
			src = "CAN"

		exec    """
                if [ -s ${OUTDIR}/CAN/${SAMPLE}.bam ]; then $cp ${OUTDIR}/CAN/${SAMPLE}.bam  ${OUTDIR}  ; fi ;
                if [ -s ${OUTDIR}/CAN/${SAMPLE}.bai ]; then $cp ${OUTDIR}/CAN/${SAMPLE}.bai  ${OUTDIR}  ; fi ;
                $cp ${OUTDIR}/${src}/${SAMPLE}.*.nrm.vcf        ${OUTDIR}/${SAMPLE}.vcf		    ;
                $cp ${OUTDIR}/${src}/${SAMPLE}.*.nrm.vcf.idx    ${OUTDIR}/${SAMPLE}.vcf.idx		;
                $cp ${OUTDIR}/${src}/${SAMPLE}.*.nrm.tsv        ${OUTDIR}/${SAMPLE}.vcf.tsv.mut	;
                """
	}
}

//  This stage maps bpipe files names in sub-directories into legacy pipeline file names for compatibility
//
mp_mapTestFiles	=
{
	doc	title:	"Map files to legacy PathOS",
		desc:	"Move generated files into top level sample directory to match current PathOS structure",
		author:	"Ken Doig, Molecular Pathology"

    requires OUTDIR : "Specify OUTDIR"
    requires SAMPLE : "Specify SAMPLE"

    output.dir = OUTDIR

	produce (	"${SAMPLE}.vcf",
				"${SAMPLE}.vcf.idx",
				"${SAMPLE}.vcf.tsv.mut"
			)
	{
		if (DEBUG) println "In mp_mapTestFiles in=$inputs out=$output $SAMPLE"

		def cp  = "cp --no-preserve=timestamps -v"
		def src = "CAN"

		exec    """
                $cp ${OUTDIR}/${src}/${SAMPLE}.*.nrm.vcf        ${OUTDIR}/${SAMPLE}.vcf		    ;
                $cp ${OUTDIR}/${src}/${SAMPLE}.*.nrm.vcf.idx    ${OUTDIR}/${SAMPLE}.vcf.idx		;
                $cp ${OUTDIR}/${src}/${SAMPLE}.*.nrm.tsv        ${OUTDIR}/${SAMPLE}.vcf.tsv.mut	;
                """
	}
}
