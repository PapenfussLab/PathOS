create table amplicon
(
  panel 	varchar(200) not null,
  chr		varchar(50)  not null,
  startpos	integer      not null,
  endpos	integer      not null,
  amplicon	varchar(200) not null,
  primerlen1	integer      not null,
  primerlen2	integer      not null,
  id            bigint(20)   not null auto_increment,
  primary key (id)
);
