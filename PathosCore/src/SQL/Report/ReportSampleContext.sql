/*	ReportSampleContext.sql
*/

drop table if exists pathos_cnt;

create	table pathos_cnt
as
select	rw.sample_id,
		count(*) as variants,
		sum(if(sv.curated_id is not null,1,0)) curated,
		sum(sv.reportable) reported		
from	review rw,
		dblive.seq_variant sv
where	sv.sample_name = rw.sample_id
group
by		rw.sample_id
;

select	distinct
		rw.sample_id as sample,
		rw.date_added,
		rw.proportion_of_tumour_cells as pct_tumour,
		an.description as site,
		tf.description as histology,
		pu.username as pathologist,
		inf.description finding,
		rw.comments,
		pc.variants,
		pc.curated,
		pc.reported
from	review rw,
		pathologist_user pu,
		internal_finding inf,
		tissue_finding tf,
		anatomical_site an,
		pathos_cnt pc
where	pu.id = rw.pathologist_user_id
and		inf.id = rw.internal_finding
and		tf.id = rw.histological_typing
and		an.id = rw.external_diagnosis
and		pc.sample_id = rw.sample_id
;
