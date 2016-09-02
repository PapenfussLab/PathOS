#!/bin/bash
#
#	MakeTestSuite.sh		Make suite of variant test files for a pipeline
#
#     1  seqrun    151111_M01053_0296_000000000-AK1CT
#     2  sample    14M4506
#     3  hgvsg     chr2:g.198266828_198266833del
#     4  hgvsc     NM_012433.2:c.2099_2104del
#     5  manifest  Lymphoid_Panel_v5.1
#     6  amps      SF3B1_EX15_2
#     7  vaf       4.5

testsr=ReportableAmpliconTS
outseq=/pathology/NGS/Samples/PipeCleaner/$testsr
testnum=0

#
#	Create Pipeline sample file for all runs
#
mkdir -p $outseq
echo -e "#seqrun\tsample\tpanel\tpipeline\tpipein\toutdir" > $outseq/Seqrun.tsv

while read line
do
	set $line
	seqrun=$1
	sample=$2
	hgvsg=$3
	hgvsc=$4
	panel=$5
	amps=$6
	vaf=$7

	testnum=`expr 1 \+ $testnum`
	tn="$(printf "%03d" $testnum)"
	prefix="Test$tn"

	outdir=$outseq/$prefix
	testdir=$outdir/CAN
	pipein=$outdir/FASTQ

	mkdir -p $testdir
	pushd $testdir
		#
		#	output test parameters as a TSV file
		#
		echo -e "seqrun\tsample\thgvsg\thgvsc\tpanel\tamps\tvaf" > ../variant.tsv
		echo -e "$line" >> ../variant.tsv

		#
		#	output Seqrun parameters as a TSV file
		#
		echo -e "$testsr\t$prefix\t$panel\tmp_testAmplicon\t$pipein\t$outdir" >> $outseq/Seqrun.tsv

		RunCanary -f $prefix -o $prefix -p 1 -c $panel $seqrun $sample
		gzip *.fastq
		mkdir ../FASTQ
		mv -v *.gz ../FASTQ
		mkdir ../VCF
		cp -v *.vcf ../VCF
	popd
done < ReportableTestVariants.tsv

