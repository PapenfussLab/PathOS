-- Count variant occurrences by panel
--

drop table if exists tmp_panel_pct;

-- 35,315 rows in 8.8 sec
-- 

create	table tmp_panel_pct
as
select sv.variant,
       pnl.panel_group,
       count(distinct ss.sample_name) as cnt
from   seq_variant   as sv
join   seq_sample as ss
join   panel      as pnl
where  pnl.panel_group != 'R&D'
and    sv.seq_sample_id = ss.id
and    ss.panel_id = pnl.id
and    upper(ss.sample_name) not like 'NTC%'
and    upper(ss.sample_name) not like 'CTRL%'
and    upper(ss.sample_name) not like 'CONTROL%'
and    upper(ss.sample_name) not like 'HL60%'
and    upper(ss.sample_name) not like 'ACD1%'
and    upper(ss.sample_name) not like '1975%'
group
by     sv.variant,
       pnl.panel_group
;

-- 	Make Variant column indexable
--

alter table tmp_panel_pct change column variant variant varchar(255);

create index tmp_panel_pct_idx1 on tmp_panel_pct(variant);
create index tmp_panel_pct_idx2 on tmp_panel_pct(panel_group);

-- 	Count sample occurrences by panel
-- 

drop table if exists tmp_panel_sam;

create	table tmp_panel_sam
as
select pnl.panel_group,
       count(distinct ss.sample_name) as cnt
from   seq_sample as ss
join   panel      as pnl
where  pnl.panel_group != 'R&D'
and    ss.panel_id = pnl.id
and    upper(ss.sample_name) not like 'NTC%'
and    upper(ss.sample_name) not like 'CTRL%'
and    upper(ss.sample_name) not like 'CONTROL%'
and    upper(ss.sample_name) not like 'HL60%'
and    upper(ss.sample_name) not like 'ACD1%'
and    upper(ss.sample_name) not like '1975%'
group
by     pnl.panel_group
;

create index tmp_panel_sam_idx1 on tmp_panel_sam(panel_group);

-- 	Check differences bewteen panel percent
-- 

/******
select	sv.variant,
		ss.sample_name,
		sr.seqrun,
		pl.panel_group,
		pp.cnt * 100 / ps.cnt newpp,
		sv.var_panel_pct oldpp,
		abs(pp.cnt * 100 / ps.cnt - sv.var_panel_pct) delta
from	seq_variant sv,
		tmp_panel_pct pp,
		tmp_panel_sam ps,
		seq_sample ss,
		seqrun sr,
		panel pl
where	sv.seq_sample_id = ss.id
and		ss.panel_id = pl.id
and		ss.seqrun_id = sr.id
and		sv.variant = pp.variant
and		pl.panel_group = pp.panel_group
and		pl.panel_group = ps.panel_group
;
******/

-- 	Update all seq_variant var_panel_pct fields
--  607,263 rows u[dated in 23.03 sec
-- 

update	seq_variant sv,
		tmp_panel_pct pp,
		tmp_panel_sam ps,
		seq_sample ss,
		panel pl
set		sv.var_panel_pct = pp.cnt * 100 / ps.cnt
where	sv.seq_sample_id = ss.id
and		ss.panel_id = pl.id
and		sv.variant = pp.variant
and		pl.panel_group = pp.panel_group
and		pl.panel_group = ps.panel_group
;
