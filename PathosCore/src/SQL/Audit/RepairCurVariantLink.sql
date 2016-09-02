/*	Audit cur_variant to seq_variant link
*/

update	seq_variant sv,
	cur_variant cv
set	sv.curated_id = cv.id
where	cv.variant    = sv.hgvsg
and	sv.curated_id is null
;		