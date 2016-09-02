select pnl.panel_group,
	count(distinct ss.sample_name) as cnt
from   seq_variant   as sv
	join   seq_sample as ss
	join   panel      as pnl
where  pnl.panel_group != 'R&D'
       and		sv.reportable = 1
       and    sv.seq_sample_id = ss.id
       and    ss.panel_id = pnl.id
       and    upper(ss.sample_name) not like 'NTC%'
       and    upper(ss.sample_name) not like 'CTRL%'
       and    upper(ss.sample_name) not like 'CONTROL%'
       and    upper(ss.sample_name) not like 'HL60%'
       and    upper(ss.sample_name) not like 'ACD1%'
       and    upper(ss.sample_name) not like '1975%'
group
by     pnl.panel_group
;

/*      As at 21/2/2016

MP ADS Somatic Production	4
MP CCP Somatic Development	26
MP FLD Germline Production	315
MP FLD Lymphoid Production	143
MP FLD Myeloid Production	255
MP FLD Somatic Production	3860
MP TSACP Production	513

 */