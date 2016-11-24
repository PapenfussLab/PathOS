-- 	Preserve all classified variants from pa_prod into pa_stage
--
--      The production database is the sole repository of curated data
--      This script preserves all production curated records by copying them
--      the test DB prior to populating it fully from disk
--      This allows the test DB to replicate the production DB for curated
--      variants. This is only occurs at database rebuild
--
-- 	01	ken doig	08-Nov-13
-- 	02	ken doig	06-Mar-14
--      03      ken doig        14-Mar-14       Removed evidence object mapping
--

--      Clean out existing records
--

set foreign_key_checks = 0;

truncate table auth_user;
truncate table auth_role;
truncate table auth_user_auth_role;
truncate table patient;
truncate table sample;
truncate table sample_test;
truncate table panel;
truncate table roi;
truncate table seqrun;
truncate table seq_sample;
truncate table seq_variant;
truncate table audit;
truncate table cur_variant;

insert into auth_user           select * from dblive.auth_user;
insert into auth_role           select * from dblive.auth_role;
insert into auth_user_auth_role select * from dblive.auth_user_auth_role;
insert into dbalt.cur_variant   select * from dblive.cur_variant;

set     foreign_key_checks = 1;

drop table if exists align_stats;

rename table mp_alignstats to align_stats;
