create table ref_hgnc_genes
(
  gene	    varchar(30)		NOT NULL,
  hgncid    varchar(30)		NOT NULL,
  accession varchar(30)     NOT NULL,
  genedesc	varchar(200)	NOT NULL,
  refseq	varchar(30)		NOT NULL,
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (id)
);
