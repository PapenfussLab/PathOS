create table mp_mutdesc
(
  gene		varchar(20)		NOT NULL,
  hgvsc		varchar(200)	NOT NULL,
  hgvsp		varchar(200)	NOT NULL,
  mutdesc	text			NOT NULL,
  var	        varchar(200)	NOT NULL,
  hgvsg	        varchar(200)	NOT NULL
);
