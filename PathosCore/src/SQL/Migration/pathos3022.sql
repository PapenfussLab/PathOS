# migrate from pre-3022 to 3022: seqrelation relationship refactor
# remove old columns and indices that'll cause loader to throw errors
alter table seq_relation  drop foreign key FKEBCBE9DC5E49C77F;
alter table seq_relation  drop index FKEBCBE9DC5E49C77F;
alter table seq_relation drop column derived_sample_id;

#dont need the below if you have dbcreate update
alter table seq_relation add column `derived_sample_name` varchar(255) DEFAULT NULL;
alter table seq_relation add column `derived_sample_seqrun_name` varchar(255) DEFAULT NULL;
