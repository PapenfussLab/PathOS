/*	Sequencing volumes
*/

select	substring(sr.seqrun,1,4) yymm,
		count( distinct sr.seqrun) seqruns,
		count( distinct ss.sample_name) samples,
		count( distinct sv.hgvsg) variants
from	seqrun sr,
		seq_sample ss,
		seq_variant sv
where	ss.seqrun_id = sr.id
and		sv.seq_sample_id = ss.id
-- and		substring(sr.seqrun,-2,2)!='XX'
group
by		substring(sr.seqrun,1,4)
;