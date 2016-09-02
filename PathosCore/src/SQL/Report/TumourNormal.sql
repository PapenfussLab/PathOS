/*	Find Tumour/Normal pairs by matching patient names and DOB
**
**	Cant use Patient.id because the URNs don't match
**	One or both are "T-nnnn" URNs
*/


select	distinct
		sr.seqrun,
		ps.sample,
		ss.sample_name,
		upper(pp.full_name),
		upper(p2.full_name),
		pp.dob,
		p2.dob
from	seq_sample ss,
		pat_sample ps,
		patient pp,
		patient p2,
		seqrun sr
where	ss.seqrun_id = sr.id
and		substring(ss.sample_name,1,7) = ps.sample
and		ss.sample_name regexp '-[TN]$'
and		pp.id = ps.patient_id
and		upper(pp.full_name) = upper(p2.full_name)
and		pp.dob = p2.dob
and		ss.seqrun_id in (123,
128,
137,
575,
143,
4,
583,
689,
695,
710,
802,
822,
875,
929,
957)
;