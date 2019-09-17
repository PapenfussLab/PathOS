#!/bin/bash
#
#   mp-vep.sh   Annotate variants using local Ensembl VEP with dbNSFP
#
#   01  clove   13-Dec-12	based on mp-vep.sh script created by kdoig
#   02  kdoig   04-May-17   migrated to JSON VEP v88
#
#   Usage: mp-vep [options] in.vcf out.vep
#
#vim:ts=4

#
#       process -options
#
HELP=0

while getopts h\? opt                                 # Add additional options here
do      case "$opt" in
        h)              HELP=1;;
        [?])    HELP=1;;
        esac
done
shift `expr $OPTIND - 1`

#
#       output usage if required
#
if [ $# -ne 2 -o $HELP -eq 1 ]; then    # Set number of required arguments here
        echo "
        Usage: `basename $0` [options] in.vcf out.vep

        -h              this help

        Annotate variants using Ensembl VEP
        " 1>&2

        exit 1
fi

#   Set up Ensembl 90 for Vep
#
. /etc/profile.d/modules.sh
module load ensembl/90

#   Set dbNSFP Columns to extract using plugin
#
dbcols=MutationTaster_pred,SIFT_pred,Polyphen2_HVAR_pred,LRT_pred,MutationAssessor_pred,FATHMM_pred,MetaSVM_pred,MetaLR_pred,clinvar_trait,clinvar_clnsig
dbnsfp=/data/databases/dbNSFP/3.5a/dbNSFP.hg19.bgz

#   Reference genome is hg19 at present
#
ref_genome=/config/binaries/ensembl_api/cache/homo_sapiens/90_GRCh37/Homo_sapiens.GRCh37.dna.primary_assembly.fa.gz

#	CADD data
#
cadd_indel=/data/databases/CADD/1.3/InDels.tsv.gz
cadd_snv=/data/databases/CADD/1.3/whole_genome_SNVs.tsv.gz

vep \
	--fork 8 \
	--force_overwrite    \
	--assembly GRCh37    \
	--offline \
	--cache \
	--total_length    \
	--allele_number    \
	--no_escape    \
	--refseq    \
	--failed 1    \
	--everything \
	--flag_pick_allele    \
	--pick_order	canonical,tsl,biotype,rank,ccds,length    \
	--fasta     	$ref_genome \
	--dir 			/config/binaries/ensembl_api/cache    \
	--plugin    	dbNSFP,${dbnsfp},${dbcols} \
	--plugin		CADD,${cadd_indel},${cadd_snv} \
	--json \
	--no_stats \
	--input_file $1 \
	--output_file $2

#--exclude_predicted \
#--cache_version 88 \
#--shift_hgvs 1    \
#--check_existing    \
#--hgvs --sift b --polyphen b --ccds --uniprot --symbol --numbers --domains --regulatory --canonical --protein --biotype --uniprot --tsl --appris --gene_phenotype --pubmed --variant_class \
