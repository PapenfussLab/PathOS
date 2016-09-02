/*	HGVSp audit report
**
**	01	kdd		07-Aug-15
*/


select sr.seqrun,
		sv.sample_name,
		sv.gene,
		sv.hgvsg, 
		sv.hgvsc, 
		sv.ens_transcript,
		sv.vep_hgvsc, 
		sv.hgvsp, 
		sv.vep_hgvsp,
		sv.consequence,
		sv.mut_error,
		sv.mut_status
from	seq_variant sv,
		seq_sample ss,
		seqrun sr
where	sv.hgvsp not regexp substring_index(sv.vep_hgvsp,'.',-1)
and		sv.hgvsp != ''
and		sv.seq_sample_id = ss.id
and		ss.seqrun_id = sr.id
and		sr.seqrun = '150317_SN1055_0253_AHKTJFADXX'
;

