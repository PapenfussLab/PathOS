drop table if exists tmpreport;

create  table tmpreport
as
	select	sr.seqrun,
		substring(sr.seqrun,1,4) runmonth,
		ps.sample patient,
		pl.panel_group,
		pl.manifest panel,
		count(distinct ss.id) sample,
		count(distinct sv.gene) genes,
		count(distinct sv.variant) vars
	from	seq_variant sv,
		seq_sample ss,
		pat_sample ps,
		seqrun sr,
		panel pl
	where	sv.seq_sample_id = ss.id
	             and		ss.seqrun_id = sr.id
	             and		sr.seqrun regexp '^1[567]'
	             and		sv.hgvsp = ''
	             and		ps.id = ss.pat_sample_id
	             and		pl.id = ss.panel_id
	group
	by		sr.seqrun,
		ps.sample,
		pl.panel_group,
		pl.manifest
;

select	runmonth,
	panel,
	max(panel_group) panelgrp,
	count(distinct patient) patients,
	sum(sample) samples,
	max(genes) genes,
	sum(vars) variants
from	tmpreport
where	panel_group != 'Research'
group
by		runmonth,
	panel
order
by		runmonth desc, patients desc
;

select	runmonth,
	count(distinct patient) patients
from	tmpreport
where	panel_group != 'Research'
group
by		runmonth
;