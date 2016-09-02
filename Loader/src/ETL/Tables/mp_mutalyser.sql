create table mp_mutalyser
(
  variant	varchar(500)	NOT NULL,
  status	varchar(20)	NOT NULL,
  errmsg        varchar(200)	NULL,
  corrected     varchar(500)	NULL,
  refseq        varchar(100)	NULL,
  gene		varchar(20)     NULL,
  hgvsc		varchar(500)	NOT NULL,
  hgvsp		varchar(500)	NOT NULL
);
