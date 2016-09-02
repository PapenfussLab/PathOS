#!/usr/bin/perl -w
#
#	vs2vcf			Convert mangled VarScan VCF format to vcf4.1
#
#	01		kdoig	21-Nov-12
#
#	VarScan 2 produces an invalid VCF format file with the alt bases preceded
#	by +/- for insertions and deletion respectively. This filter puts the VCF file
#	in VCF4.1 format.
#
#vim:ts=4

use strict;
use warnings;
use Getopt::Long;
#use Data::Dumper;
#use Bio::SeqIO;

#
#	G l o b a l     v a r i a b l e s 
#
my $version = '0.1';

#
#	Parse the options
#
my $help = 0;

my $optok = GetOptions	(	"help"			=>	\$help,
						);

#
#	Print usage if invalid args
#
my $Usage = qq(
Usage:   vs2vcf.pl [options] < in.vs > out.vcf

Options: -help      BOOL   output this help       [$help]

Convert mangled VarScan VCF format to vcf4.1
);

die $Usage if (@ARGV != 0 || $optok != 1 || $help );

#
#	Read in file
#
#my $fil = $ARGV[0];

while ( <> )
{
	if ( /^#/ )
	{
		print;
		next;
	}
	chomp;
	my $line = $_;

	my @flds = split ( "\t", $line );
	my $ref = $flds[3];
	my $alt = $flds[4];
	
	#
	#	Deletion of bases
	#
	if ( $alt =~ /^\-/ )
	{
		($flds[3], $flds[4]) = ($ref.substr($alt,1), $ref);
	}

	#
	#	Insertion of bases
	#
	if ( $alt =~ /^\+/ )
	{
		$flds[4] = $ref.substr($alt,1);
	}
	
	print join( "\t", @flds),"\n";
}

1;
