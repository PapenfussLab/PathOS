-- 		mp_vcfvep.sql
--
-- 		Molecular Pathology merged table of variants
--
-- 		kdd		01		29-Apr-13		Split VCF and VEP variants
-- 

--
-- 	Remove non refseq transcripts
--
drop table if exists web_vep;

create	table web_vep
	as
select	*
from	mp_vep as vep
where	vep.refseq regexp '^NM_'
;

--
--      Create indexes
--

create index web_vep_idx1 on web_vep(variant);
create index web_vep_idx2 on web_vep(ens_variant,seqrun,sample);

--
--      Create mp_vcfvep for legacy BioMart mp_variants
--

drop table if exists mp_vcfvep;

create	table mp_vcfvep
as
select	vcf.sample,
	vcf.seqrun,
	vcf.panel,
	vep.ens_transcript as transcript,
	vep.refseq,
	vep.variant,
	vep.ens_variant,
	vcf.chr,
	vcf.pos,
	vep.exon,
	vcf.ref,
	vcf.alt,
	vep.codons,
	vep.gene,
	vep.hgvsp,
	vep.hgvsc,
	vep.consequence,
	vep.existing_variation,
	vep.cosmic,
	vep.cosmic_count,
	vep.dbsnp,
	vep.sift,
	vep.sift_class,
	vep.sift_value,
	vep.poly,
	vep.poly_class,
	vep.poly_value,
	vcf.freq as af,
	vcf.rdf,
	vcf.rdr,
	vcf.adf,
	vcf.adr,
	vep.gmaf,
	vcf.pval,
	vcf.totalreaddepth,
	vcf.varreaddepth,
	vep.cdna_position,
	vep.cds_position,
	vep.protein_position,
	vep.amino_acids,
	vep.genename,
	vep.domains,
	vep.genedesc,
	vep.ccds,
	vep.ensp,
	vep.cytoband,
	vep.refseq_peptide,
	vep.canonical,
	vep.omim_ids,
	vep.omim_count,
	vep.eur_maf,
	vep.amr_maf,
	vep.asn_maf,
	vep.afr_maf
from	mp_vcf as vcf,
	web_vep as vep
where	vcf.ens_variant = vep.ens_variant
and	vcf.seqrun = vep.seqrun
and	vcf.sample = vep.sample
;

-- 
-- 		Create indexes
--

create index mp_vcfvep_idx1 on mp_vcfvep(chr);
create index mp_vcfvep_idx2 on mp_vcfvep(pos);
create index mp_vcfvep_idx3 on mp_vcfvep(sample);
create index mp_vcfvep_idx4 on mp_vcfvep(seqrun);


