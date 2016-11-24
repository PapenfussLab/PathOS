/*	Create mp_webprod schema reference tables
**
**	kdd		13-aug-2013
**
*/

drop table if exists mp_webprod.ref_hgmd;

create table mp_webprod.ref_hgmd (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_webtest.ref_hgmd
;

drop table if exists mp_webprod.ref_hgmdimputed;

create table mp_webprod.ref_hgmdimputed (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_webtest.ref_hgmdimputed
;

drop table if exists mp_webprod.ref_emory;

create table mp_webprod.ref_emory (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_webtest.ref_emory
;

drop table if exists mp_webprod.ref_clinvar;

create table mp_webprod.ref_clinvar (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_webtest.ref_clinvar
;

drop table if exists mp_webprod.ref_kconfab;

create table mp_webprod.ref_kconfab (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_webtest.ref_kconfab
;

drop table if exists mp_webprod.ref_iarc;

create table mp_webprod.ref_iarc (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_webtest.ref_iarc
;

drop table if exists mp_webprod.ref_bic;

create table mp_webprod.ref_bic (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_webtest.ref_bic
;

drop table if exists mp_webprod.ref_cosmic;

create table mp_webprod.ref_cosmic (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_webtest.ref_cosmic as rc
where	rc.cosmicid != ''
and	rc.cosmicid != 'Mutation ID'
;

create index ref_cosmic_idx1 on mp_webprod.ref_cosmic(hgvsc,gene);

drop table if exists mp_webprod.ref_hgnc_genes;

create table mp_webprod.ref_hgnc_genes (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_webtest.ref_hgnc_genes
;

