-- 		Collapse evidence fields
--
-- 		DB Migration script to be run before deployment of PathOS v0.95
--
--      kdd     01      07-feb-14
--

update	variant
set		evidence_benign_alone_comment = null
where	evidence_benign_alone_comment regexp '^Insert comments about'
;

update	variant
set		evidence_benign_strong_comment = null
where	evidence_benign_strong_comment regexp '^Insert comments about'
;

update	variant
set		evidence_benign_support_comment = null
where	evidence_benign_support_comment regexp '^Insert comments about'
;

update	variant
set		evidence_path_alone_comment = null
where	evidence_path_alone_comment regexp '^Insert comments about'
;

update	variant
set		evidence_path_strong_comment = null
where	evidence_path_strong_comment regexp '^Insert comments about'
;

update	variant
set		evidence_path_support_comment = null
where	evidence_path_support_comment regexp '^Insert comments about'
;

alter	table variant add evidence_justification longtext null;
alter	table variant add hgvsp_aa1 varchar(255) null;

alter   table sample  add tumour_type  varchar(255) null;
alter   table sample  add stage       varchar(255) null;
alter   table sample  add formal_stage varchar(255) null;
alter   table sample  drop column histology;

update		variant
set			evidence_justification = 
			trim(
			concat(	ifnull(evidence_path_alone_comment,''),    ' ',
						ifnull(evidence_path_strong_comment,''),   ' ',
						ifnull(evidence_path_support_comment,''),  ' ',
						ifnull(evidence_benign_alone_comment,''),  ' ',
						ifnull(evidence_benign_strong_comment,''), ' ',
						ifnull(evidence_benign_support_comment,'')))
where	evidence_path_alone_comment is not null
or		evidence_path_strong_comment is not null
or		evidence_path_support_comment is not null
or		evidence_benign_alone_comment is not null
or		evidence_benign_strong_comment is not null
or		evidence_benign_support_comment is not null
;

/*
alter	table variant drop column evidence_path_alone_comment;
alter	table variant drop column evidence_path_strong_comment;
alter	table variant drop column evidence_path_support_comment;
alter	table variant drop column evidence_benign_alone_comment;
alter	table variant drop column evidence_benign_strong_comment;
alter	table variant drop column evidence_benign_support_comment;
*/
