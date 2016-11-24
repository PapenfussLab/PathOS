/*	Audit all transcripts in PathOS for mismatches
*/

/*	Summary report of genes with transcript mismatches
*/

select	count(*),
		count(distinct sv.sample_name) samples,
		count(distinct sr.seqrun) seqruns,
		sum(sv.reportable) reported,
		sum(if(sv.curated_id != 0,1,0)) curated,
		max(sr.seqrun) lastestRun,
		max(pl.panel_group) latestPanel,
		sv.gene variantgene,
		substring_index(sv.hgvsc,':',1) as pathos,
		ts.refseq as preferred
from	seq_variant sv
join	transcript ts
on		substring_index(sv.hgvsc,'.',1) != ts.accession
and		sv.gene = ts.gene
and		ts.preferred = 1
and		ts.build = 'hg19'
join	seq_sample ss
on		sv.seq_sample_id = ss.id
join	seqrun sr
on		ss.seqrun_id = sr.id
join	panel pl
on		ss.panel_id = pl.id
where	
		substring(sv.hgvsc,1,3) = 'NM_'
and		pl.panel_group not in ('MP CPP','Research')
and		sr.seqrun > '150701'
group
by		sv.gene,
		substring_index(sv.hgvsc,'.',1)
order
by		1 desc
;

/*	Check ano_variant to validate transcripts
*/

select	av.hgvsg,
		av.gene,
		rt.accession,
		av.hgvsc
from	ano_variant as av,
		transcript as rt
where	data_source = 'MUT'
and		rt.build = 'hg19'
and		rt.preferred  = 1
and		rt.gene = av.gene
and		rt.accession != substring_index(av.hgvsc,'.',1)
;

/*	Find all affected variants with a transcript mismatch
*/

select	distinct
		ts.gene,
		ts.refseq as preferred,
		substring_index(sv.hgvsc,':',1) as pathos,
		ts2.gene
from	seq_variant sv
join	transcript ts
on		substring_index(sv.hgvsc,'.',1) != ts.accession
and		sv.gene = ts.gene
and		ts.preferred = 1
and		ts.build = 'hg19'
join	seq_sample ss
on		sv.seq_sample_id = ss.id
join	seqrun sr
on		ss.seqrun_id = sr.id
join	panel pl
on		ss.panel_id = pl.id
join	transcript ts2
on		substring_index(sv.hgvsc,':',1) = ts2.refseq
and		ts2.preferred = 1
and		ts2.build = 'hg19'
where	substring(sv.hgvsc,1,3) = 'NM_'
and		pl.panel_group not in ('MP CPP','Research')
and		sr.seqrun > '150701'
;
