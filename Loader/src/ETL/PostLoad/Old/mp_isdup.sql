-- 		mp_isdup.sql
-- 		
-- 		Sample classification as duplicate
--
-- 		kdd		01		11-Feb-13	Added table
-- 

drop table if exists mp_isdup;

create	table mp_isdup
as
select	sample,
		panel,
		min(seqrun) as earliest,
		max(seqrun) as latest
from	mp_vcf
group
by		sample,
		panel
having earliest != latest
;

create index mp_isdup_idx1 on mp_isdup(sample);
create index mp_isdup_idx2 on mp_isdup(panel);

