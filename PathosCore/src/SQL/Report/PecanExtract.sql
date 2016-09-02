/*	Pecan Extractor query
**
**	01	kdd		07jan16
*/

select	sv.gene,
	substring_index(sv.hgvsc,'.',1) as refseq,
	sv.chr as chromosome,
	sv.pos as 'start',
	substring_index(sv.hgvsp_aa1,':',-1) as aachange,
	ss.sample_name as sample,
	if(sv.consequence='missense_variant','missense',
	if(sv.consequence='synonymous_variant','silent',
	if(sv.consequence='frameshift_variant','frameshift',
	if(sv.consequence='3_prime_UTR_variant','utr_3',
	if(sv.consequence='5_prime_UTR_variant','utr_5',
	'noncoding'))))) as 'class'
from	seq_variant sv,
	seq_sample ss,
	seqrun sr
where	sr.id = ss.seqrun_id
and	ss.id = sv.seq_sample_id
and	sr.seqrun regexp '160101_'
and	sv.hgvsp_aa1 != ''
and	ss.sample_name not regexp '-1$'
order  by	ss.sample_name
;

/**
'splice_region_variant,synonymous_variant'
'missense_variant'
'synonymous_variant'
'frameshift_variant'
'missense_variant,splice_region_variant'
'3_prime_UTR_variant'
'5_prime_UTR_variant'
'stop_gained'
'inframe_deletion'
'initiator_codon_variant'
**/