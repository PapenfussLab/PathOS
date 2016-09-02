create table mp_cnv
(
	seqrun 				varchar(50) NOT NULL,
	sample				varchar(50) NOT NULL,
	gene				varchar(50) NOT NULL,
	cnv_type 			varchar(50) NOT NULL,
	chr					varchar(10) NOT NULL,
	startpos 			int(11) NOT NULL,
	endpos				int(11) NOT NULL,
	lr_mean				double NOT NULL,
	lr_median			double NOT NULL,
	lr_sd				double DEFAULT NULL,
	gainloss			varchar(4) DEFAULT NULL,
	pval				double NOT NULL,
	n					int(11) NOT NULL,
	probes_pct			double DEFAULT NULL,
	pval_adj			double NOT NULL
);
