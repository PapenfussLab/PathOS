/*	Create mp_webtest schema reference tables
**
**	kdd		13-aug-2013
**
*/

drop table if exists mp_webtest.ref_hgmd;

create table mp_webtest.ref_hgmd (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_test.ref_hgmd
;

drop table if exists mp_webtest.ref_hgmdimputed;

create table mp_webtest.ref_hgmdimputed (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_test.ref_hgmdimputed
;

drop table if exists mp_webtest.ref_emory;

create table mp_webtest.ref_emory (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_test.ref_emory
;

drop table if exists mp_webtest.ref_clinvar;

create table mp_webtest.ref_clinvar (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_test.ref_clinvar
;

drop table if exists mp_webtest.ref_kconfab;

create table mp_webtest.ref_kconfab (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_test.ref_kconfab
;

drop table if exists mp_webtest.ref_iarc;

create table mp_webtest.ref_iarc (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_test.ref_iarc
;

drop table if exists mp_webtest.ref_bic;

create table mp_webtest.ref_bic (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_test.ref_bic
;

drop table if exists mp_webtest.ref_cosmic;

create table mp_webtest.ref_cosmic (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_test.ref_cosmic as rc
where	rc.cosmicid != ''
and		rc.cosmicid != 'Mutation ID'
;

drop table if exists mp_webtest.ref_hgnc_genes;

create table mp_webtest.ref_hgnc_genes (id bigint(20) not null auto_increment, primary key (id))
as
select	*
from	mp_test.ref_hgnc_genes
;
