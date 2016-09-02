create table ano_attr
(
  data_source		varchar(50)  not null,
  attr		        varchar(50)  not null,
  data_type		varchar(50)  not null,
  description		varchar(200) null,
  format		varchar(50)  null,
  category		varchar(50)  null,
  id                    bigint(20)   not null auto_increment,
  primary key (id)
);
