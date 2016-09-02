#!/bin/bash
#
#		RunAnnovar.sh	    Annotate a variant file
#
#		01		kdoig		02-Apr-14
#		02		Aseleznev	08-Sep-15	Upgraded Annovar
#
#       Input Format: chr start_pos end_pos ref alt otherinfo
#
#vim:ts=4

ANNOVAR_HOME=/pathology/NGS/Annovar_2015Mar22
ANNOVAR_DB=/pathology/NGS/DataSource/Annovar/humandb/
PATH=$ANNOVAR_HOME:$PATH

#
#	process -options
#
HELP=0

while getopts h\? opt					# Add additional options here
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
	Usage: `basename $0` [options] in.tsv out.tsv

	Annotate a variant file in annovar format: chr start end ref alt otherinfo
	" 1>&2

	exit 1
fi

#
#	Start of script
#

$ANNOVAR_HOME/table_annovar.pl \
		$1 \
		$ANNOVAR_DB \
		-buildver hg19 \
		-out $2 \
		-remove \
		-otherinfo \
		-protocol refGene,cosmic68,esp6500si_all,snp138,1000g2012apr_all,caddgt10,cg69,nci60,clinvar_20140211,ljb26_all \
		-operation g,f,f,f,f,f,f,f,f,f \
		2> .tmp.anv.err

if [ -s $2.hg19_multianno.txt ];then
	mv -v $2.hg19_multianno.txt $2
else
	echo "ERROR: RunAnnovar.sh missing output file"
	exit 1
fi
