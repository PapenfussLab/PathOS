-- 		mp_variants.sql
--
-- 		Molecular Pathology table of variants for Cancer 2015
--
-- 		kdd		02		03-Dec-12
-- 						04-Dec-12		Added Cosmic count
-- 						12-Dec-12		Added join bw mp_vcf and mp_vep to accomodate indels
-- 						19-Dec-12		Integrated cleaned mp_batch dat
-- 						02-Jan-13		Added Ca2015 flag and fth QC field
-- 						04-Jan-13		Added GMAF and exon fields
-- 						07-Jan-13		Rewrite for integrated VCF/VCF file 
-- 						04-Feb-13		Added panel type
-- 						08-Feb-13		Added age and mut count
-- 						11-Feb-13		Added mp_batchrun table, removed mp_tumournormal table, removed fth1, added tumourtype/tumourgrade from ca2015
-- 						25-Mar-13     Made mp_batchrun left join, split sift/polyphen
-- 						27-Mar-13		Added indexes after table creation
-- 						08-Apr-13		Removed batch field and consolidated pre-LIMS data into mp_batch
-- 						23-Apr-13		Added oncogene/TSG, pathway, process
-- 						29-Apr-13		Split VCF and VEP variants
-- 						09-May-13		Removed mp_cosmiccnt table
-- 						14-May-13		Matched Alamut on HGVSp, Matched mp_detente on all test_sets (not just M965)
-- 						16-May-13		Added forward/reverse read/variants counts from varscan
-- 						21-May-13		Switched to Alamut XML files
-- 						29-May-13		Added additional fields: tumoursite,drugaction,pathlab,varpct,af->varfreq
-- 

drop table if exists mp_variants;

create table mp_variants (filterflag varchar(255), drugaction varchar(255))
as
select	distinct
		mba.isca2015 as Ca2015,
		if(dup.sample is not null, if( dup.latest=vep.seqrun,'L','D'), 'L') as isdup,
		det.patient,
		det.urn,
		det.dob,
		datediff(curdate(),str_to_date(det.dob,'%d/%m/%Y'))/365.25 as age,
		det.sex,
		det.location as pathlab,
 		if ( mba.isca2015 = 'Y', if(c15.tumourtype is null,'## Missing in Ca2015 ##',c15.tumourtype), null) as tumourtype,
		c15.stage as tumourstage,
		c15.formalstage as tumoursite,
		mba.tumour,
		vep.sample,
		vep.seqrun,
		vep.panel,
		vep.transcript,
		vep.refseq,
		vep.variant,
		vep.ens_variant,
		vep.chr,
		vep.pos,
		vep.exon,
		vep.ref,
		vep.alt,
		vep.codons,
		vep.gene,
		vep.genedesc,
		ifnull(cag.description,vep.genedesc) as gene_description,
		cag.gene_type,
		cag.pathway as gene_pathway,
		cag.process as gene_process,
		replace(vep.hgvsp,'X','*') as hgvsp,
		vep.hgvsc,
		vep.consequence,
		vep.cosmic,
		vep.dbsnp,
		mal.classlevel as alamut_class,
		'N' as filtered,
		if( mal.classlevel is not null, 'Y', 'N') as curated,
		'N' as reportable,
		vep.sift,
		vep.sift_class,
		vep.sift_value,
		vep.poly,
		vep.poly_class,
		vep.poly_value,
		frq.varcnt,
		frq.varpct,
		mut.mutcnt,
		vep.cosmic_count as cosmiccnt,
		vep.af as varfreq,
		vep.rdf,
		vep.rdr,
		vep.adf,
		vep.adr,
		vep.gmaf,
		vep.pval,
  		mba.dnaconc,
		vep.totalreaddepth,
		vep.varreaddepth,
		vep.rdr/(vep.rdr+vep.rdf) as readbias,
		vep.adr/(vep.adr+vep.adf) as varbias,
		if((vep.rdr>10 and vep.adr<10) or (vep.rdf>10 and vep.adf<10), 1, 0) as ampliconbias,
		concat(vep.chr,':',convert(vep.pos,char(15))) as Alamut_URL,
		concat(vep.chr,':',convert(vep.pos,char(15))) as IGV_URL,
		concat(vep.chr,':',convert(vep.pos,char(15))) as UCSC_URL,
		vep.cdna_position,
		vep.cds_position,
		vep.protein_position,
		vep.amino_acids,
		vep.domains,
		vep.ccds,
		vep.ensp,
		vep.cytoband,
		'' as refseq_peptide, -- vep.refseq_peptide,
		vep.canonical,
		vep.omim_ids,
		vep.omim_count,
		vep.eur_maf,
		vep.amr_maf,
		vep.asn_maf,
		vep.afr_maf
from	mp_vcfvep as vep
left
join	mp_detente as det
on		det.sample = vep.sample
left
join	mp_batch as mba					-- Legacy pre-LIMS data: isca2015, tumour, DNA concentration
on		vep.sample = mba.sample
and		vep.seqrun = mba.seqrun
left
join	mp_varfreq as frq
on		vep.ens_variant = frq.ens_variant
and		vep.panel = frq.panel
left
join	mp_mutcnt as mut
on		vep.sample = mut.sample
and		vep.seqrun = mut.seqrun
left
join	mp_isdup as dup
on		vep.sample = dup.sample
and		vep.panel  = dup.panel
left
join	ref_cancergenes as cag
on		cag.gene = vep.gene
left
join	mp_alamutxml as mal
on		vep.variant = mal.variant
left
join	c15_patient as c15
on		vep.sample = c15.sample
where	vep.ens_variant is not null
;

-- 
-- 		Create indexes
--

create index mp_variants_idx1 on mp_variants(sample);
create index mp_variants_idx2 on mp_variants(seqrun);
create index mp_variants_idx3 on mp_variants(hgvsp);
create index mp_variants_idx4 on mp_variants(hgvsc);
create index mp_variants_idx5 on mp_variants(pos);
create index mp_variants_idx6 on mp_variants(gene);
create index mp_variants_idx7 on mp_variants(variant);
