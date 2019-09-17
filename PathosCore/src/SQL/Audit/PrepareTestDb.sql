-- Data Simplify from Production DB
-- ================================
--

drop table mp_alamutxml;
drop table mp_alignstats;
drop table mp_amplicon;
drop table mp_annovar;
drop table mp_as_bu;
drop table mp_audit;
drop table mp_batch;
drop table mp_cnv;
drop table mp_curated;
drop table mp_detente;
drop table mp_detente_tests;
drop table mp_genedesc;
drop table mp_isdup;
drop table mp_mutalyser;
drop table mp_mutcnt;
drop table mp_mutdesc;
drop table mp_panelcnt;
drop table mp_roi;
drop table mp_seqrun;
drop table mp_tumournormal;
drop table mp_tumourtype;
drop table mp_varfreq;
drop table mp_vcf;

delete from align_stats where seqrun not like '17%';

set foreign_key_checks = 0;

delete from seqrun where seqrun not in (select distinct seqrun from align_stats);

delete from seq_sample where seqrun_id not in (select id from seqrun);

delete from seq_variant where seq_sample_id not in (select id from seq_sample);

update patient set full_name = concat('Patient ',id), urn = concat('12/34/',id),dob='1950-01-01 00:00:00';

update auth_user set email = 'user@host', display_name = 'NN', username= concat('NN',id) where username not regexp 'pathos';

delete from ano_variant where hgvsg not in (select distinct hgvsg from seq_variant);

delete from seq_cnv where seq_sample_id not in (select id from seq_sample);

-- Fix any orphan SeqRelations

delete  ssr.*,
	sr.*
from    seq_sample_relations ssr
	left
	join    seq_sample ss
		on      ss.id = ssr.seq_sample_id
	left
	join    seq_relation sr
		on      sr.id = ssr.seq_relation_id
where   ss.sample_name is null
        or      sr.relation is null
;