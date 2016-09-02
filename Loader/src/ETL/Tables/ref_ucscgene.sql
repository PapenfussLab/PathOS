create table ref_ucscgene
(
	bin 	        smallint(5) unsigned NOT NULL,
	refseq 		varchar(255) NOT NULL,
	chr 		varchar(255) NOT NULL,
	strand		char(1) NOT NULL,
	txStart		int(10) unsigned NOT NULL,
	txEnd		int(10) unsigned NOT NULL,
	cdsStart	int(10) unsigned NOT NULL,
	cdsEnd		int(10) unsigned NOT NULL,
	exonCount	int(10) unsigned NOT NULL,
	exonStarts	text NOT NULL,
	exonEnds	text NOT NULL,
	score		int(11) default NULL,
	gene		varchar(255) NOT NULL,
	cdsStartStat 	enum('none','unk','incmpl','cmpl') NOT NULL,
	cdsEndStat	enum('none','unk','incmpl','cmpl') NOT NULL,
	exonFrames	text NOT NULL
);
