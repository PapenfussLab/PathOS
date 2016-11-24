#!/bin/bash
#
#		VcfSplitMultiAllele.sh		Normalise VCF file by left shifting variants and splitting multi-alleles
#
#		01		kdoig		Jun-15
#
#		Usage: VcfSplitMultiAllele.sh in.vcf out.vcf
#
#vim:ts=4

#set -x

#
#	process -options
#
HELP=0

while getopts h\? opt			# Add additional options here
do	case "$opt" in
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
	Usage: `basename $0` [options] in.vcf out.vcf

	Option:	-h                  Help screen

	Normalise VCF file by left shifting variants and splitting multi-alleles
	" 1>&2

	exit 1
fi

#
#   Check vt (vcf tools) exists
#
VT=`which vt`


if [ "$VT" == "" ]; then
    echo FATAL: Missing vt \(vcf tools\)
    exit 1
fi

#
#   Check for reference genome
#
HumanREF=/data/reference/indexes/human/g1k_v37/picard/human_g1k_v37.fasta
if [ ! -s "$HumanREF" ]; then
    echo FATAL: Missing reference genome $HumanREF
    exit 1
fi

#
#   Split multi-allele and left shift along the genome
#   Todo: use Vcf.class and GATK.class for this
#

#   Fix header so allele depth (AD) parameters are 'R' (Reference + alleles)
#
sed 's/|/,/g'                           < $1    |\
sed 's/ID=AD,Number=./ID=AD,Number=R/'          |\
vt decompose -s - -o -                          |\
vt normalize -n -q - -o - -r $HumanREF          |\
sed 's/ID=AD,Number=./ID=AD,Number=1/' > $2

vcfLinesIn=`grep -v '#' $1 | wc -l`
vcfLinesOut=`grep -v '#' $2 | wc -l`
if [ $vcfLinesIn -gt $vcfLinesOut ]; then
    echo "FATAL: $0: lines out [$vcfLinesOut] in $2 less than lines in [$vcfLinesIn] in $1"
    exit 1
fi

exit 0