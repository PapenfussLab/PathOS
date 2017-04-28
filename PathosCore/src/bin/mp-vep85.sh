#!/bin/bash
#
#   mp-vep.sh   Annotate variants using local Ensembl VEP with dbNSFP
#
#   01  clove   13-Dec-12	based on mp-vep.sh script created by kdoig
#
#   Usage: mp-vep85 [options] in.vcf out.vep
#
#vim:ts=4

#
#       process -options
#
HELP=0

while getopts irh\? opt                                 # Add additional options here
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

#
#   Set dbNSFP Columns to extract using plugin
#
DBCOLS=COSMIC_CNT,COSMIC_ID,MutationTaster_pred,SIFT_pred,Polyphen2_HVAR_pred,LRT_pred,MutationAssessor_pred,FATHMM_pred,MetaSVM_pred,MetaLR_pred,clinvar_trait,clinvar_clnsig

#
#   Reference genome is hg19 at present
#
REF_GENOME=/config/binaries/ensembl_api/cache/homo_sapiens/85_GRCh37/Homo_sapiens.GRCh37.75.dna.primary_assembly.fa.gz
GENOME_BUILD=GRCh37

variant_effect_predictor.pl \
        --force_overwrite    \
        --species homo_sapiens    \
        --assembly $GENOME_BUILD    \
        --offline    \
        --sift b    \
        --ccds    \
        --uniprot    \
        --hgvs    \
        --symbol    \
        --numbers    \
        --domains    \
        --gene_phenotype    \
        --canonical    \
        --protein    \
        --biotype    \
        --uniprot    \
        --tsl    \
        --pubmed    \
        --variant_class    \
        --shift_hgvs 1    \
        --check_existing    \
        --total_length    \
        --allele_number    \
        --no_escape    \
        --xref_refseq    \
        --failed 1    \
        --minimal    \
        --flag_pick_allele    \
        --pick_order canonical,tsl,biotype,rank,ccds,length    \
        --dir /config/binaries/ensembl_api/cache    \
        --fasta $REF_GENOME    \
        --input_file $1    \
        --output_file $2    \
        --polyphen b    \
        --gmaf    \
        --maf_1kg    \
        --maf_esp    \
        --regulatory    \
        --plugin ExAC,/data/databases/ExAC/ExAC_nonTCGA.r0.3.1.sites.vep.vcf.gz \
        --plugin CADD,/data/databases/CADD/InDels.tsv.gz,/data/databases/CADD/whole_genome_SNVs.tsv.gz \
        --plugin dbNSFP,/pathology/NGS/DataSource/VEP/dbNSFP/dbNSFP2.9.1/dbNSFP.gz,$DBCOLS \
        --fork 8 2> vep$$.log

