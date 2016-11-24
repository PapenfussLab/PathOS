/*	Panel reporting
*/

drop table if exists sv_report;

create table sv_report
as
	select	pl.panel_group,
		pl.manifest,
		sr.seqrun,
		sv.sample_name as sample,
		ps.sample as patsample,
		cv.pm_class,
		sv.var_freq,
		sv.hgvsg,
		sv.gene,
		sv.consequence,
		sv.filter_flag,
		sv.hgvsc,
		sv.hgvsp
	from	seq_variant sv
		join	seq_sample ss
			on		sv.seq_sample_id = ss.id
		join	panel pl
			on		ss.panel_id      = pl.id
		join	seqrun sr
			on		ss.seqrun_id     = sr.id
		left
		join	cur_variant cv
			on		cv.hgvsg = sv.hgvsg
		left
		join	pat_sample ps
			on		ss.pat_sample_id = ps.id
	where	sv.reportable = 1
	             and		cv.pm_class is not null
	             and		cv.pm_class regexp '^C[45]:'
	             and		ss.sample_name regexp '^1[65432][KM][0-9][0-9][0-9][0-9]$'
	             and		pl.panel_group in (	'MP FLD Myeloid Production',
	                                                           'MP FLD Lymphoid Production',
	                                                           'MP FLD Germline Production',
	                                                           'MP FLD Somatic Production',
	                                                           'MP CCP Somatic Development',
	                                                           'MP TSACP Production',
	                                                           'MP ADS Somatic Production')
;

/*      Summarise variants by Panel/Gene
*/

select panel_group,
	gene,
	count(distinct seqrun) as seqruns,
	count(distinct sample) as samples,
	count(distinct patsample) as patsamples,
	count(*) as reportable,
	count(distinct hgvsc) as hgvscs,
	count(distinct hgvsp) as hgvsps,
	avg(var_freq) as mean_vaf
from	sv_report
group
by		panel_group,
	gene
order
by		1,3 desc
;

/*	Find all Detente reportable episodes
*/

select	distinct
	sr.sample,
	ps.sample,
	pa.test_set,
	pa.test_name
from	sv_report sr
	left
	join	pat_sample ps
		on		ps.sample = sr.sample
	left
	join	pat_assay pa
		on		pa.pat_sample_id = ps.id
where	ps.sample is not null
;