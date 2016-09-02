/*	Find all seq samples that are replicates
**	Only one out of the pair
*/

create	table tmp_sin
as
select	distinct
		ss.id
from	seq_variant sv,
		seq_sample  ss,
		panel pl
where	ss.id = sv.seq_sample_id
and		pl.id = ss.panel_id
and		ss.passfail_flag = 0
and		sv.filter_flag regexp 'sin'
and		sv.sample_name regexp '-1$'
;