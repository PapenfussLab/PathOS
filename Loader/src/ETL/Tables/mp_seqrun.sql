create table mp_seqrun
(
	seqrun			varchar(100)		NOT NULL,
	platform		varchar(50)		NOT NULL,
	sepe			varchar(10)		NOT NULL,
	library			varchar(255)		NOT NULL,
	experiment	        varchar(255)		NOT NULL,
	scanner			varchar(100)		NOT NULL,
	readlen			varchar(100)		NOT NULL,
	sample			varchar(100)		NOT NULL,
	panel			varchar(300)		NOT NULL,
	analysis		varchar(100)		NOT NULL,
	username		varchar(100)		NOT NULL,
	useremail		varchar(100)		NOT NULL,
	laneno		        varchar(10)		NOT NULL
);
