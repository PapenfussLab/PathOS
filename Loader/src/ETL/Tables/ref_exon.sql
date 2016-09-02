create table ref_exon
(
	gene		varchar(255) NOT NULL,
	refseq 		varchar(255) NOT NULL,
	exon 	        varchar(20) NOT NULL,
	strand		char(1) NOT NULL,
	idx	        int(10) unsigned NOT NULL,
	exonStart	int(10) unsigned NOT NULL,
	exonEnd 	int(10) unsigned NOT NULL,
	exonFrame	int(10) NOT NULL,
	id BIGINT(20) NOT NULL AUTO_INCREMENT,
	PRIMARY KEY (id)
);
