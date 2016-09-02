create table ref_lrg
(
	tax_id	        varchar(30)     NOT NULL,
	geneid          varchar(30)	NOT NULL,
	gene            varchar(30)	NOT NULL,
	rsg             varchar(30)     NOT NULL,
	lrg             varchar(200)	NULL,
	refseq      	varchar(30)	NOT NULL,
	lrg_t           varchar(30)	NULL,
	refseq_protein  varchar(30)	NOT NULL,
	lrg_p           varchar(30)	NULL,
	category	varchar(30)	NOT NULL
);
