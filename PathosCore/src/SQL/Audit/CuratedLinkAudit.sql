/*	Audit cur_variant to seq_variant link
*/

select	sr.seqrun,
	sv.sample_name,
	sv.gene,
	sv.hgvsg
from	seq_variant sv,
	cur_variant cv,
	seq_sample  ss,
	seqrun sr
where	cv.variant = sv.hgvsg
and	sv.curated_id is null
and	sr.id = ss.seqrun_id
and	ss.id = sv.seq_sample_id;