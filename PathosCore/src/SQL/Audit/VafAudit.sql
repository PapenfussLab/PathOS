/*	Audit low VAF SeqVariants
*/

select	distinct
		sr.seqrun,
		sv.sample_name seq_sample,
		pa.manifest,
		sv.filter_flag,
		sv.curated_id,
		sv.var_freq,
		sv.var_depth*100/sv.read_depth as calc_vaf,
		(sv.var_depth*100/sv.read_depth - sv.var_freq) as diff_vaf,
		sv.var_depth,
		sv.read_depth,
		sv.gene,
		sv.hgvsg,
		sv.hgvsc,
		sv.hgvsp,
		sv.hgvsp_aa1,
		sv.consequence
from	seq_variant sv,
		seq_sample  ss,
		seqrun sr,
		panel pa
where	sr.id = ss.seqrun_id
and		ss.id = sv.seq_sample_id
and		pa.id = ss.panel_id
and		pa.panel_group = 'MP FLD Germline Production'
and		sr.seqrun > '1603'
and		abs(sv.var_depth*100/sv.read_depth - sv.var_freq) > 5
and		sv.var_depth > 0
and		sv.filter_flag not regexp 'con'
		

