/*	Generate PipeCleaner test variant set
*/

drop table if exists tmp_testvar;

create	table tmp_testvar
as
select	distinct
	sr.seqrun,
	sv.sample_name as sample,
	sv.hgvsg,
	sv.hgvsc,
	pa.manifest,
	sv.amps,
	sv.var_freq
from	seq_variant sv,
	seq_sample  ss,
	seqrun sr,
	panel pa
where	sr.id = ss.seqrun_id
and	ss.id = sv.seq_sample_id
and	pa.id = ss.panel_id
and	sv.reportable = 1
and	sv.amps is not null
and	sv.amps != ''
;

select	tt.*
from	tmp_testvar tt,
	(
	select	max(sample) sample,
		hgvsc
	from	tmp_testvar
	group
	by		hgvsc
	) xx
where	tt.sample = xx.sample
and	tt.hgvsc  = xx.hgvsc
;
