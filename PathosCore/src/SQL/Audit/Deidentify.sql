/*      SQL clean up DB from incriminating data

 */

truncate ref_cosmic;
drop table aa;
drop table old_user;
drop table mp_vcf;
drop table mp_vep;
drop table mp_vcfvep;
drop table mp_detente;
drop table mp_alignstats;
set foreign_key_checks=0;
drop table user;
update auth_user set email = '' where username not in ('akumar','kdd');
update patient set full_name = concat('Patient ',id), urn = concat('12/34/',id),dob='1950-01-01 00:00:00';


/* Remove everything except for one run

delete from seqrun where id != 604;
delete from seq_sample where seqrun_id != 604;
delete from seq_variant where seq_sample_id not in (30436,30444,30445);
*/