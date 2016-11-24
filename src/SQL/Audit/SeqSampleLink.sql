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
on		sr.id = ss.seqrun_id
left
join	sample sa
on		sa.sample = substring(ss.sample_name,1,7)
where	ss.sample_id is null
having	sa.id is not null
;

**/

select	'update seq_sample set pat_sample_id = ',
		sa.id,
		' where id = ',
		ss.id,
		';'
from	seq_sample ss
join	seqrun sr
on      sr.id = ss.seqrun_id
left
join	pat_sample sa
on	sa.sample = substring(ss.sample_name,1,7)
where	ss.pat_sample_id is null
having	sa.id is not null
;

