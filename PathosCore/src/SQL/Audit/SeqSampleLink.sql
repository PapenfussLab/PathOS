/*	Detect and Fix SeqSample to Sample link
*/

/**

select	sa.sample,
	sa.id,
	sr.seqrun,
	ss.sample_name,
	ss.id
from	seq_sample ss
join	seqrun sr
on	sr.id = ss.seqrun_id
left
join	pat_sample ps
on	ps.sample = substring(ss.sample_name,1,7)
where	ss.pat_sample_id is null
having	ps.id is not null
;

**/

select	concat('update seq_sample set pat_sample_id = ',
	ps.id,
	' where id = ',
	ss.id,
	';')
from	seq_sample ss
join	seqrun sr
on      sr.id = ss.seqrun_id
left
join	pat_sample ps
on	ps.sample = substring(ss.sample_name,1,7)
where	ss.pat_sample_id is null
having	ps.id is not null
;

