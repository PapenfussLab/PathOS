#!/bin/bash
#
#		mp-vep.sh		Annotate variants unsing Ensembl VEP
#
#		01		kdoig		13-Dec-12
#		02		kdoig		21-Oct-13   Moved to Ensembl v73
#		03		kdoig		21-Oct-14   Added -c and -e flags
#		04		kdoig		01-Jun-15   Moved to Ensembl v78 added CADD and ExAC
#
#		Usage: mp-vep [options] in.vcf out.vep
#
#vim:ts=4

#
#	process -options
#
HELP=0
INTG='--no_intergenic --coding_only'
REFSEQ=''
CONSERVE=''
EVS=''
REFSEQ=''

while getopts irh\? opt					# Add additional options here
do	case "$opt" in
	i)		INTG='';;
	r)		REFSEQ='--refseq';;
	e)		EVS='--plugin EVS';;
	c)		CONSERVE='--plugin CONSERVE';;
	h)		HELP=1;;
	[?])	HELP=1;;
	esac
done
shift `expr $OPTIND - 1`

#
#	output usage if required
#
if [ $# -ne 2 -o $HELP -eq 1 ]; then	# Set number of required arguments here
	echo "
	Usage: `basename $0` [options] in.vcf out.vep

	-h 		this help
	-c 		Apply conservation plugin
	-e 		Apply EVS plugin
	-i 		Show intergenic and non coding
	-r 		Output refseq transcripts (doesn't output HGNC gene though)

	Annotate variants using Ensembl VEP
	" 1>&2 

	exit 1
fi

#
#   Set paths for Ensembl 78 legacy VEP
#
###export Ensembl=/usr/local/cluster/all_arch/ensembl_api/api-ver-78
###export PATH=$Ensembl/ensembl-tools/scripts/variant_effect_predictor:$PATH

export HOST='bioinf-ensembl.petermac.org.au'

variant_effect_predictor.pl							\
		--fork 8    								\
		--cache       								\
		--html        								\
		--everything								\
		-i $1										\
		-o $2										\
		--species human								\
		$INTG										\
		$REFSEQ										\
		$CONSERVE									\
		$EVS										\
		--check_alleles		 						\
		--check_existing	 						\
		--check_ref			 						\
		--host $HOST								\
		--dir /config/binaries/ensembl_api/cache	\
		-user vep							        \
		-password vep_pass							\
		-port 3306									\
		--plugin Additional_Annotation				\
		--plugin ExAC,/data/databases/ExAC/ExAC.r0.3_noTCGA.vcf.gz \
		--plugin CADD,/data/databases/CADD/InDels.tsv.gz,/data/databases/CADD/whole_genome_SNVs.tsv.gz \
		--force_overwrite                           2> vep$$.log


