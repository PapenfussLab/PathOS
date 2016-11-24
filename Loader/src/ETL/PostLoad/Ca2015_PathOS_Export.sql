/*	Ca2015 PathOS Export SQL		cCa2015_PathOS_Export.sql
 *
 *		kdd		01		31-may-2013	Initial
 *		kdd		02		30-jan-2014	Rewrite for PathOS Gorm extract
 *		kdd		03		31-jan-2014	Added missing fields
 *		kdd		04		18-feb-2014	Added dnaconc, classification and tumour/normal
 *
 */

/*
**		Excludes samples without the TruSeq panel
**		Failed QC Seqrun or failed QC Sample
**		Excludes samples with "M" numbers
*/

drop 	table if exists pr_webtest.c15_ss;

create	table pr_webtest.c15_ss
as
select	distinct
		sr.seqrun,
		sr.id as seqrun_id,
		sr.passfail_flag as sr_pass,
		sr.authorised_flag as sr_auth,
		ss.sample_name as sample,
		ss.id as seq_sample_id,
		ss.passfail_flag as ss_pass,
		ss.authorised_qc_flag as ss_auth
from	seq_sample  as ss
join	seqrun as sr
on		ss.seqrun_id = sr.id
join	sample      as sa
on		sa.sample = ss.sample_name
join	mp_detente as md
on		md.sample = ss.sample_name
join	patient     as pa
on		sa.patient_id = pa.id
join	mp_batch as ba
on		ba.seqrun = sr.seqrun
and		ba.sample = ss.sample_name
join	panel as pl
on		pl.id = ss.panel_id
where	ba.isca2015 = 'Y'
and		pl.manifest = 'CancerGNA10212011_170_190_Viewermanifest'
and		((ss.authorised_qc_flag = 1 and ss.passfail_flag = 1) or ss.authorised_qc_flag = 0)
and		((sr.authorised_flag    = 1 and sr.passfail_flag = 1) or sr.authorised_flag    = 0)
and		ss.sample_name not regexp 'M'
;

/*
**		Count the number of passed variants for each Ca2015 sample
*/

drop table if exists pr_webtest.c15_numvar;

create table pr_webtest.c15_numvar
as
select	sa.sample,
		sum(sv.filtered) as numvar
from	pr_webtest.c15_ss as sa
left
join	seq_variant as sv
on		sa.seq_sample_id = sv.seq_sample_id
join	seq_sample  as ss
on		sa.seq_sample_id = ss.id
join	seqrun as sr
on		sa.seqrun_id = sr.id
group
by		sa.sample
;

create index c15_numvar_idx1 on pr_webtest.c15_numvar(sample);

drop 	table if exists pr_webtest.c15_var;

create	table pr_webtest.c15_var
as
select	distinct
		sv.sample_name as sample,
		sr.seqrun,
		var.evidence_evidence_class as classification,
		sv.var_freq    as varfreq,
		sv.gene,
		sv.hgvsc,
		sv.hgvsp,
		sv.consequence,
		pa.full_name,
		pa.urn as pmac_urn,
		pa.dob,
		pa.sex,
		cg.gene_type,
		ss.dnaconc,
		ba.isca2015,
		sa.tumour,
		substring_index(sv.variant,':',1) as refseq,
		cg.pathway as gene_pathway,
		md.request_date as path_request_date,
		sa.rcvd_date as detente_date,
		ba.extract_date,
		'YES' as tested,
		nv.numvar as variants
from	seq_variant as sv
join	seq_sample  as ss
on		sv.seq_sample_id = ss.id
join	seqrun as sr
on		ss.seqrun_id = sr.id
left
join	variant as var
on		sv.curated_id = var.id
left
join	ref_cancergenes as cg
on		cg.gene = sv.gene
join	sample      as sa
on		sa.sample = sv.sample_name
join	mp_detente as md
on		md.sample = sv.sample_name
join	patient     as pa
on		sa.patient_id = pa.id
join	pr_webtest.c15_numvar as nv
on		nv.sample = sv.sample_name
left
join	mp_batch as ba
on		ba.seqrun = sr.seqrun
and		ba.sample = sv.sample_name
join	pr_webtest.c15_ss as vs
on		vs.seqrun = sr.seqrun
and		vs.sample = sv.sample_name
where	sv.filtered = 1
and		ba.isca2015 = 'Y'
order
by		1, 2
;

/*	Cancer 2015 Samples without variants
*/

drop table if exists pr_webtest.c15_novar;

create table pr_webtest.c15_novar
as
select	distinct
		sa.sample,
		sa.tumour,
		sa.rcvd_date as detente_date,
		pa.*
from	pr_webtest.c15_numvar as nv
join	sample as sa
on		nv.sample = sa.sample
join	patient     as pa
on		sa.patient_id = pa.id
where	nv.numvar = 0
;


/*	Samples from Detente never sequenced
*/

drop table if exists pr_webtest.c15_detente;

create table pr_webtest.c15_detente
as
select	*
from	mp_detente
where	test_set = 'M965'
and		sample not in (select sample from pr_webtest.c15_ss)
;


/*	Samples in multiple runs
*/

drop table if exists pr_webtest.c15_dups;

create table pr_webtest.c15_dups
as
select s1.sample,
		s1.seqrun as run1,
		s2.seqrun as run2
from 	pr_webtest.c15_ss as s1,
		pr_webtest.c15_ss as s2
where	s1.sample  = s2.sample
and		s1.seqrun  < s2.seqrun
;