/**
 *	mp_test.groovy	Example modules to demonstrate Bpipe
 *
 */

@Transform('tst')
test_init =
{
	doc	title:	"Test Initialisation",
		desc:	"Create a dummy .tst file in the current dir to experiment with",
		author:	"Ken Doig, Molecular Pathology"

	println "In test_ini{} in=$input out=$output $output.dir" 
	exec "date > $output.tst"
}

@Transform('bam')
test_tx_bam =
{
	doc	title:	"Test Transform to BAM",
		desc:	"Transform file into a BAM file in a subdirectory",
		author:	"Ken Doig, Molecular Pathology"

	output.dir = "BAM"
	println "in test_tx_bam() in=$input out=$output $output.dir" 
	exec "cp -v $input $output"
}

@Filter('cnv')
test_filt_cnv =
{
	doc	title:	"Test Filter",
		desc:	"Filter a file with a 'cnv' filter in the BAM subdirectory",
		author:	"Ken Doig, Molecular Pathology"

	output.dir = "BAM"
	println "in test_filt_cnv{} in=$input out=$output $output.dir" 
	exec "cp -v $input $output"
}

@Transform('vcf')
test_tx_vcf =
{
	doc	title:	"Test Transform to VCF",
		desc:	"Transform a file into a VCF file in the VCF subdirectory",
		author:	"Ken Doig, Molecular Pathology"

	output.dir = "VCF"
	println "in test_tx_vcf{} in=$input out=$output $output.dir" 
	exec "cp -v $input $output"
}

test_compress =
{
	doc	title:	"Test Compression",
		desc:	"Compress a file in the VCF subdirectory",
		author:	"Ken Doig, Molecular Pathology"

	output.dir = "VCF"
	produce( input + ".gz" )
	{
		println "in test_compress{} tin=$input out=$output $output.dir" 
		exec "gzip $input"
	}
}
