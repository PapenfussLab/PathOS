create table mp_alignstats
(
  seqrun		varchar(50) not null,
  sample_name		varchar(50) null,
  panel_name		varchar(50) null,
  panel_group		varchar(50) null,   -- Todo: deprecated
  amplicon		varchar(200) not null,
  location		varchar(50)  not null,
  readsout	        integer not null,
  totreads		integer not null,
  unmapped		integer not null,
  goodamp		integer not null,
  sample_stats          varchar(1000),
  id                    bigint(20) not null auto_increment,
  version               bigint(20) null,
  primary key (id)
);
