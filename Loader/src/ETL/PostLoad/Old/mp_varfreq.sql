-- 		mp_varfreq.sql
-- 		
-- 		Frequency of variants (proportion of samples that the variant appears in) in a panel
--
-- 		kdd		01		29-Nov-12
-- 				02		03-Jan-13	restricted counts to Ca 2015 samples only
-- 				03		07-Jan-13	modified for combined VCF/vcf file
-- 				04		12-Feb-13	made has_off_target yes/no field
-- 				05		28-May-13	varcnt now counts distinct samples and excludes controls
-- 									and outputs a percentage figure for variant
-- 

drop table if exists mp_varfreq;
drop table if exists mp_panelcnt;

create	table mp_panelcnt
as
select	vcf.panel,
	count(distinct vcf.sample) as totcnt
from	mp_vcf   as vcf
where	not
		(	vcf.sample regexp 'ACD1'
or			vcf.sample regexp 'Control'
or			vcf.sample regexp 'Ctrl'
or			vcf.sample regexp 'HL60'
or			vcf.sample regexp '1975-.*[AB]'
		)
group
by		vcf.panel
;

create table mp_varfreq
as
select	vcf.ens_variant,
	vcf.panel,
	count(distinct vcf.sample) as varcnt,
	count(distinct vcf.sample) * 100.0 / pc.totcnt as varpct
from	mp_vcf      as vcf
join	mp_panelcnt as pc
on		vcf.panel = pc.panel
where	not
		(	vcf.sample regexp 'ACD1'
or			vcf.sample regexp 'Control'
or			vcf.sample regexp 'Ctrl'
or			vcf.sample regexp 'HL60'
or			vcf.sample regexp '1975-.*[AB]'
		)
group
by		vcf.ens_variant,
		vcf.panel
;

create index mp_varfreq_idx1 on mp_varfreq(ens_variant);
create index mp_varfreq_idx5 on mp_varfreq(panel);

