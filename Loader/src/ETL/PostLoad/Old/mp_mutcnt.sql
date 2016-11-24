-- 		mp_mutcnt.sql
-- 		
-- 		Number of variants per sample for variants
--
-- 		kdd		01		08-Feb-13	Added table
-- 

drop table if exists mp_mutcnt;

create table mp_mutcnt
as
select	vcf.sample,
		vcf.seqrun,
		count(*) as mutcnt
from	mp_vcf   as vcf
group
by		vcf.sample,
		vcf.seqrun
;

create index mp_mutcnt_idx1 on mp_mutcnt(sample);
create index mp_mutcnt_idx2 on mp_mutcnt(seqrun);

