create table mp_roi
(
  panel 	varchar(200) not null,
  gene		varchar(50)  not null,
  exon		varchar(50)  not null,
  chr		varchar(50)  not null,
  startpos	integer      not null,
  endpos	integer      not null,
  name	        varchar(200) not null
);
