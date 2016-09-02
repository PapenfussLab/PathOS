/*	Create the db_lock table
*/

drop table if exists db_lock;

create table db_lock
(
	id			int,
	host		varchar(50),
	pid			varchar(50),
	datetime 	datetime
);

insert into db_lock values ( 1, 'bioinf-pathos-test', 0, '2000-01-01 12:00:00.0');