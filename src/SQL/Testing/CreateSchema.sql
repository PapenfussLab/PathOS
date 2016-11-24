drop database if exists dbci;

create database if not exists dbci;

grant all privileges on *.* to 'bioinformatics'@'localhost' identified by 'testpwd' with grant option;
grant all privileges on *.* to 'bioinformatics'@'%' identified by 'testpwd' with grant option;

use dbci;

create  table testtbl
	(
	id bigint(20) primary key,
	beer_name varchar(50),
	brewer varchar(50),
	date_received date
	)
;

