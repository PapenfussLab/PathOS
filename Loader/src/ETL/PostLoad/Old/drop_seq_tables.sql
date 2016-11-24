/*	Drop_seq_tables.sql
**
**	Drop all tables sequencing tables from current schema
*/

set foreign_key_checks = 0;

truncate table mp_webtest.patient;
truncate table mp_webtest.sample;
truncate table mp_webtest.sample_test;
truncate table mp_webtest.seqrun;
truncate table mp_webtest.panel;
truncate table mp_webtest.seq_sample;
truncate table mp_webtest.seq_variant;          /* this contains the reportable flag and curated link */

set foreign_key_checks = 1;

--
--  Remove sequencing audit records
--

delete from audit where category = 'pipeline';