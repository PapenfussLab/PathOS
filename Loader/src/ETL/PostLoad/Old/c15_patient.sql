-- 		c15_patient.sql
-- 		
-- 		Cleaned table of Ca2015 patient data
--
-- 		kdd		01		13-Feb-13	Added table
-- 

drop table if exists c15_tumour;

create table c15_tumour
as
select	bs.tumouridentifier as sample,
		dd.patientid,
		dd.tumourtype,	
		dd.stage,
		dd.formalstage
from	(
		select	diagtreatid, 
				tumouridentifier
		from	ca2015.c15_biospecimen
		where	length(rtrim(ltrim(tumouridentifier))) > 0
		) as bs
		inner
		join	(
				select	distinct
						pdt.diagtreatid,
						pdt.patientid,
						tst.description	as tumourtype,
						stg.description	as stage,
						fstg.description	as formalstage
				from	ca2015.c15_pts_diagnosis_treatment	as pdt
				left
				join	ca2015.c15_tumourstream				as tst
				on		pdt.tumourstreamid = tst.Id
				left
				join	ca2015.c15_stage						as stg
				on		stg.id = pdt.stageid
				left
				join	ca2015.c15_formalstage				as fstg
				on		fstg.id = pdt.formalstageid
				where	pdt.diagtreatid is not null
				) as dd
		on		bs.diagtreatid = dd.diagtreatid
;


drop table if exists c15_patient;

create	table c15_patient
as
select	distinct
		lower(pat.firstname) as firstname,
		lower(pat.surname)   as surname,
		str_to_date(pat.dateofbirth,'%Y-%m-%d') as dob,
		datediff(curdate(),str_to_date(pat.dateofbirth,'%Y-%m-%d'))/365.25 as age,
		if(pat.genderid=1,'M','F') as sex,
		ct.*
from	c15_tumour as ct,
		ca2015.c15_patient_data as pat		
where	pat.patientid = ct.patientid
;

create index c15_patient_idx1 on c15_patient(sample);
